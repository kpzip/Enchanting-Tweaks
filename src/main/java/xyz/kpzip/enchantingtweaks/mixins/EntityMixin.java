package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.server.world.ServerWorld;
import xyz.kpzip.enchantingtweaks.util.Damager;

@Mixin(Entity.class)
public abstract class EntityMixin {
	
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
	
	//TODO Overwrite: Maintain this for every update in case the original changes
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
