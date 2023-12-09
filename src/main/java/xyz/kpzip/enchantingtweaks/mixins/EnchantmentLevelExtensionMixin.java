package xyz.kpzip.enchantingtweaks.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import xyz.kpzip.enchantingtweaks.util.Damager;
import xyz.kpzip.enchantingtweaks.util.MixinPriority;

public final class EnchantmentLevelExtensionMixin {
	
	private EnchantmentLevelExtensionMixin() {}

	@Mixin(value = CrossbowItem.class, priority = MixinPriority.LOW)
	private static abstract class CrossbowItemMixin {
		
		@ModifyVariable(method = "loadProjectiles(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Z", at = @At("STORE"), ordinal = 0)
		private static int getArrowAmount(int vanillaArrowAmount, LivingEntity shooter, ItemStack crossbow) {
			return 1 + 2 * EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
		}
		
		@Unique
		private static float[] getSoundPitchesUnlimited(Random random, int size) {
	        boolean bl = random.nextBoolean();
	        float[] pitches = new float[size];
	        pitches[0] = 1.0f;
	        for (int i = 1; i < pitches.length; ++i) {
	        	getSoundPitch(bl, random);
	        	bl = !bl;
	        }
	        return pitches;
	    }
		
		@Inject(method = "getProjectiles(Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
		private static void makeGetProjectilesNullSafe(ItemStack i, CallbackInfoReturnable<List<ItemStack>> cir) {
			if (i == null || i == ItemStack.EMPTY) cir.setReturnValue(new ArrayList<ItemStack>());
		}
		
		@Inject(method = "shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/CrossbowItem;getProjectiles(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
		private static void shootEachProjectile(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, CallbackInfo ci, List<ItemStack> list) {
			float[] fs = getSoundPitchesUnlimited(entity.getRandom(), list.size());
			for (int i = 0; i < list.size(); ++i) {
	            boolean creative = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
	            ItemStack itemStack = list.get(i);
	            
	            if (itemStack.isEmpty()) continue;
	            
	            shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, getArrowSpread(i, list.size()));
	            
	        }
	        postShoot(world, entity, stack);
	        ci.cancel();
		}
		
		@Shadow
		private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {}
		
		@Shadow
		private static void postShoot(World world, LivingEntity entity, ItemStack stack) {}
		
		@Unique
		private static float getArrowSpread(int arrowNum, int maxArrows) {
			
			if (arrowNum == 0) return 0.0f;
			
			float maxSpread = (float) (-25.998795268f * Math.exp(-0.55d * maxArrows) + 15.0f);
			float unitSpread = maxSpread/((maxArrows - 1)/2);
			float spread = ((arrowNum)/2) * unitSpread;
			
			if (arrowNum%2 == 1) return -spread; else return spread;
		}
		
		@Shadow
	    private static float getSoundPitch(boolean flag, Random random) {return 0.0f;}

	}
	
	@Mixin(value = Entity.class, priority = MixinPriority.HIGH)
	private static abstract class EntityMixin {
		
		@Unique
		private static LightningEntity currentLightningEntity = null;
		
		@Redirect(method = "onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
		private boolean damageWithDamager(Entity self, DamageSource source, float amount) {
			return self.damage(source, source.equals(self.getDamageSources().lightningBolt()) ? currentLightningEntity != null && currentLightningEntity instanceof Damager ? ((Damager)currentLightningEntity).getDamageAmount() : 5.0f : amount);
		}
		
		@Inject(method = "onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V", at = @At("HEAD"))
		public void onStruckByLightningHead(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
			currentLightningEntity = lightning;
	    }
		
		@Inject(method = "onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V", at = @At("RETURN"))
		public void onStruckByLightningTail(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
			currentLightningEntity = null;
	    }
	}
	
	@Mixin(value = LightningEntity.class, priority = MixinPriority.DEFAULT)
	private static abstract class LightningEntityMixin implements Damager {
		
		@Unique
		private float damageAmount = 5.0f;

		@Unique
		@Override
		public float getDamageAmount() {
			return damageAmount;
		}

		@Unique
		@Override
		public void setDamageAmount(float damage) {
			this.damageAmount = damage;
		}
	}
	
	@Mixin(value = TridentEntity.class, priority = MixinPriority.LOWEST)
	private static abstract class TridentEntityMixin extends PersistentProjectileEntity{
		
		protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world, ItemStack stack) {
			super(type, x, y, z, world, stack);
		}
		
		@Inject(method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = Shift.AFTER), cancellable = true)
		private void onEntityHit(EntityHitResult result, CallbackInfo ci) {
			Entity entity = result.getEntity();
			Entity entity2 = this.getOwner();
			SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
			float g = 1.0f;
	        if (this.getWorld() instanceof ServerWorld && this.getWorld().isThundering() && ((TridentEntity)(Object)this).hasChanneling()) {
	        	int channeling = EnchantmentHelper.getLevel(Enchantments.CHANNELING, this.getItemStack());
	            LightningEntity lightningEntity;
	            BlockPos blockPos = entity.getBlockPos();
	            if (this.getWorld().isSkyVisible(blockPos) && (lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld())) != null) {
	            	for (int i = 0; i < channeling && i < 25; ++i) {
	            		lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
	            		lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
	            		((Damager) lightningEntity).setDamageAmount(5.0f * channeling);
	            		this.getWorld().spawnEntity(lightningEntity);
	            		if (i < channeling - 1) {
	            			lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld());
	            		}
	            	}
	            	soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
	            	g = 4.5f + 0.5f * channeling;
	            	g = g > 7.0f ? 7.0f : g;
	            }
	        }
	        this.playSound(soundEvent, g, 1.0f);
	        ci.cancel();
		}
	}
	
	@Mixin(value = PlayerEntity.class, priority = MixinPriority.HIGHEST)
	private static abstract class PlayerEntityMixin {
		
		@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
		public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
			if (((PlayerEntity)(Object)this).isSubmergedIn(FluidTags.WATER)) cir.setReturnValue(cir.getReturnValue() * (1.0f + (0.2f * (EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, ((PlayerEntity)(Object)this)) - 1))));
			else cir.cancel();
		}
	}
}
