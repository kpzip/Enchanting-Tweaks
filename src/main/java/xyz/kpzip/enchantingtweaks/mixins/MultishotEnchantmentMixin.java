package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.entity.EquipmentSlot;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

@Mixin(MultishotEnchantment.class)
public abstract class MultishotEnchantmentMixin extends Enchantment {
	
	protected MultishotEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
		super(weight, type, slotTypes);
	}
	
	@Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
	public void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> info) {
		if (EnchantingTweaks.CONFIG.allowCrossbowEnchantmentsTogether())info.setReturnValue(super.canAccept(other));
	}


}
