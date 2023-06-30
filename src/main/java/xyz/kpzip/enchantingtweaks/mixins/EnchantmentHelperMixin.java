package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	
	@Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private static int getRealMaxLevel(Enchantment e) {
		return EnchantmentLevelHelper.getEnchantmentMaxLevel(e);
	}


}
