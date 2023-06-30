package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.command.EnchantCommand;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;

@Mixin(EnchantCommand.class)
public abstract class EnchantCommandMixin {

	@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private static int getRealMaxLevel(Enchantment e) {
		if (EnchantingTweaks.getConfig().enchantmentCommandAbidesByMaxLevel()) return EnchantmentLevelHelper.getEnchantmentMaxLevel(e);
		return Integer.MAX_VALUE;
	}

}
