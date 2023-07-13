package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity{
	
	protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
		super(type, x, y, z, world);
	}
	
	@Shadow
	boolean dealtDamage;
	
	@Shadow
	private ItemStack tridentStack;

	//TODO Overwrite: Maintain this for every update in case the original changes
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
            	for (int i = 0; i < channeling; ++i) {
            		lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            		lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
            		this.getWorld().spawnEntity(lightningEntity);
            		soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
            		if (i < channeling - 1) {
            			lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld());
            		}
            	}
            	g = 4.5f + 0.5f * channeling;
            	g = g > 7.0f ? 7.0f : g;
            }
        }
        this.playSound(soundEvent, g, 1.0f);
    }

}
