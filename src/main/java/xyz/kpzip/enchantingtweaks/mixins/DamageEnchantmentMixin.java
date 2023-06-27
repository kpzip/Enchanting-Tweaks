package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

@Mixin(DamageEnchantment.class)
public abstract class DamageEnchantmentMixin extends Enchantment {

	protected DamageEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
		super(weight, type, slotTypes);
	}
	
	@Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
	public void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> info) {
		if (EnchantingTweaks.CONFIG.allowDamageEnchantmentsTogether()) info.setReturnValue(super.canAccept(other));
	}

}
