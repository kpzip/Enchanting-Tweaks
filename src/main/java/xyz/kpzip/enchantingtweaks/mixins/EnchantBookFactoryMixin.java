package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.village.TradeOffers;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;

@Mixin(TradeOffers.EnchantBookFactory.class)
public abstract class EnchantBookFactoryMixin {
	
	@Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	public int getRealMaxLevel(Enchantment e) {
		return EnchantmentLevelHelper.getEnchantmentMaxLevel(e);
	}

}
