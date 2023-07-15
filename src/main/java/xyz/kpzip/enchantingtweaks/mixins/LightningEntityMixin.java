package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.LightningEntity;
import xyz.kpzip.enchantingtweaks.util.Damager;

@Mixin(LightningEntity.class)
public class LightningEntityMixin implements Damager {
	
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
