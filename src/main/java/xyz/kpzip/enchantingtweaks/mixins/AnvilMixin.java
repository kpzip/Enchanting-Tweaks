package xyz.kpzip.enchantingtweaks.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentTweaksHelper;

public final class AnvilMixin {
	
	private AnvilMixin() {}
	
	@Mixin(AnvilScreenHandler.class)
	private static abstract class AnvilScreenHandlerMixin {
		
		@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
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
	
	@Environment(value=EnvType.CLIENT)
	@Mixin(AnvilScreen.class)
	private static abstract class AnvilScreenMixin {
		
		@Redirect(method="drawForeground", at = @At(value = "FIELD", ordinal = 0,  target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z", opcode = Opcodes.GETFIELD))
		private boolean shouldBeTooExpensive(PlayerAbilities playerAbilities) {
			return !(!playerAbilities.creativeMode && !EnchantingTweaks.getConfig().allowBypassAnvilMaxLevel());
		}

	}



}
