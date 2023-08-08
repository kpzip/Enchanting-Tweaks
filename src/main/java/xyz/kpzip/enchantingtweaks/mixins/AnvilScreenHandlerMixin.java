package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
	
	@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private int getRealMaxLevel(Enchantment e) {
		return EnchantmentLevelHelper.getEnchantmentMaxLevel(e);
	}
	
	//This allows us to lie to the part of the code that checks if something should be too expensive.
	//If the config is set to remove the "TOO EXPENSIVE" text, and the repair would normally be too expensive, we tell the anvil that the level cost is 39,
	//which allows you to still use the anvil
	//This works because the code that actually deals with displaying and deducting the levels is in a seperate function and is not altered
	//TODO this might not work in the future
	@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
	private int getHack(Property p) {
		if (EnchantingTweaks.getConfig().allowBypassAnvilMaxLevel() && p.get() >= 40) {
			return 39;
		}
		else return p.get();
	}

}
