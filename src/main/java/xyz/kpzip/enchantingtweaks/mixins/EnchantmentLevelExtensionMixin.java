package xyz.kpzip.enchantingtweaks.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public final class EnchantmentLevelExtensionMixin {
	
	private EnchantmentLevelExtensionMixin() {}

	@Mixin(CrossbowItem.class)
	private static abstract class CrossbowItemMixin {
		
		@Shadow
		private static final String CHARGED_PROJECTILES_KEY = "ChargedProjectiles";
		
		@Shadow
		private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) { return true; }
		
		/**
		 * @Author kpzip
		 * @Reason add compatibility for loading more than 3 projectiles
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
	        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
	        int j = 1 + 2*i;
	        boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
	        ItemStack itemStack = shooter.getProjectileType(crossbow);
	        ItemStack itemStack2 = itemStack.copy();
	        for (int k = 0; k < j; ++k) {
	            if (k > 0) {
	                itemStack = itemStack2.copy();
	            }
	            if (itemStack.isEmpty() && bl) {
	                itemStack = new ItemStack(Items.ARROW);
	                itemStack2 = itemStack.copy();
	            }
	            if (loadProjectile(shooter, crossbow, itemStack, k > 0, bl)) continue;
	            return false;
	        }
	        return true;
	    }
		
		@Shadow
		private static List<ItemStack> getProjectiles(ItemStack stack) { return new ArrayList<ItemStack>(); }
		
		@Shadow
		private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {}
		
		@Shadow
		private static void postShoot(World world, LivingEntity entity, ItemStack stack) {}
		
		/**
		 * @Author kpzip
		 * @Reason add compatibility for loading more than 3 projectiles
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
	        List<ItemStack> list = getProjectiles(stack);
	        float[] fs = getSounds(entity.getRandom(), list.size());
	        for (int i = 0; i < list.size()/* && i <= 19*/; ++i) {
	        	
	            boolean creative = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
	            
	            ItemStack itemStack = list.get(i);
	            if (itemStack.isEmpty()) continue;
	            
	            shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, getArrowSpread(i, list.size() > 19 ? 19 : list.size()));
	            /*
	            if (i == 0) {
	                shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, 0.0f);
	                continue;
	            }
	            if (i == 1) {
	                shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, -10.0f);
	                continue;
	            }
	            if (i != 2) continue;
	            shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, 10.0f);
	            */
	            
	            
	        }
	        postShoot(world, entity, stack);
	    }
		
		@Unique
		private static float getArrowSpread(int arrowNum, int maxArrows) {
			
			if (arrowNum == 0) return 0.0f;
			
			float maxSpread = (float) (-25.998795268f * Math.exp(-0.55d * maxArrows) + 15.0f);
			float unitSpread = maxSpread/((maxArrows - 1)/2);
			float spread = ((arrowNum)/2) * unitSpread;
			
			if (arrowNum%2 == 1) return -spread; else return spread;
		}
		
		//TODO This is kind of an overwrite: Maintain this for every update in case the original changes (marked as @unique since the parameters needed to be changed)
		@Unique
		private static float[] getSounds(Random random, int len) {
	        boolean bl = random.nextBoolean();
	        float[] pitches = new float[len];
	        pitches[0] = 1.0f;
	        for (int i = 1; i < pitches.length; ++i) {
	        	getSoundPitch(bl, random);
	        	bl = !bl;
	        }
	        return pitches;
	    }
		
		@Shadow
	    private static float getSoundPitch(boolean flag, Random random) {return 0.0f;}

	}
	
	@Mixin(Entity.class)
	private static abstract class EntityMixin {
		
		@Shadow
		private int fireTicks;
		
		@Shadow
		public abstract void setFireTicks(int fireTicks);
		
		@Shadow
		public abstract void setOnFireFor(int seconds);
		
		@Shadow
		public abstract DamageSources getDamageSources();
		
		@Shadow
		public abstract boolean damage(DamageSource source, float amount);
		
		/**
		 * @Author kpzip
		 * @Reason allow lightning damage to be increased
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
	        this.setFireTicks(this.fireTicks + 1);
	        if (this.fireTicks == 0) {
	            this.setOnFireFor(8);
	        }
	        if (lightning instanceof Damager) this.damage(this.getDamageSources().lightningBolt(), ((Damager) lightning).getDamageAmount());
	        else this.damage(this.getDamageSources().lightningBolt(), 5.0f);
	        
	    }
	}
	
	@Mixin(LightningEntity.class)
	private static abstract class LightningEntityMixin implements Damager {
		
		@Unique
		private float damageAmount = 5.0f;

		@Override
		public float getDamageAmount() {
			return damageAmount;
		}

		@Override
		public void setDamageAmount(float damage) {
			this.damageAmount = damage;
		}
	}
	
	@Mixin(TridentEntity.class)
	private static abstract class TridentEntityMixin extends PersistentProjectileEntity{
		
		protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
			super(type, x, y, z, world);
		}
		
		@Shadow
		boolean dealtDamage;
		
		@Shadow
		private ItemStack tridentStack;

		/**
		 * @Author kpzip
		 * @Reason allow channeling tridents to strike multiple times and deal more damage with increasing levels.
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		@Override
	    public void onEntityHit(EntityHitResult entityHitResult) {
	        Entity entity = entityHitResult.getEntity();
	        float f = 8.0f;
	        if (entity instanceof LivingEntity) {
	            LivingEntity livingEntity = (LivingEntity)entity;
	            f += EnchantmentHelper.getAttackDamage(this.tridentStack, livingEntity.getGroup());
	        }
	        Entity entity2 = this.getOwner();
	        DamageSource damageSource = this.getDamageSources().trident(this, entity2 == null ? this : entity2);
	        this.dealtDamage = true;
	        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
	        if (entity.damage(damageSource, f)) {
	            if (entity.getType() == EntityType.ENDERMAN) {
	                return;
	            }
	            if (entity instanceof LivingEntity) {
	                LivingEntity livingEntity2 = (LivingEntity)entity;
	                if (entity2 instanceof LivingEntity) {
	                    EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
	                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
	                }
	                this.onHit(livingEntity2);
	            }
	        }
	        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
	        float g = 1.0f;
	        if (this.getWorld() instanceof ServerWorld && this.getWorld().isThundering() && ((TridentEntity)(Object)this).hasChanneling()) {
	        	int channeling = EnchantmentHelper.getLevel(Enchantments.CHANNELING, tridentStack);
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
	    }
	}
	
	@Mixin(PlayerEntity.class)
	private static abstract class PlayerEntityMixin {
		
		@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
		public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir, float f) {
			cir.setReturnValue(f * (1.0f + (0.2f * (EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, ((PlayerEntity)(Object)this)) - 1))));
			cir.cancel();
		}
	}
}
