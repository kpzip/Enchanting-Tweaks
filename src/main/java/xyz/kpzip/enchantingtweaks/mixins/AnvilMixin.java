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
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentTweaksHelper;
import xyz.kpzip.enchantingtweaks.util.MixinPriority;

public final class AnvilMixin {
	
	private AnvilMixin() {}
	
	@Mixin(value = AnvilScreenHandler.class, priority = MixinPriority.HIGHEST)
	private static abstract class AnvilScreenHandlerMixin {
		
		@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Environment(value=EnvType.CLIENT)
	@Mixin(value = AnvilScreen.class, priority = MixinPriority.LOWEST)
	private static abstract class AnvilScreenMixin {
		
		@Redirect(method="drawForeground", at = @At(value = "FIELD", ordinal = 0,  target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z", opcode = Opcodes.GETFIELD))
		private boolean shouldBeTooExpensive(PlayerAbilities playerAbilities) {
			return !(!playerAbilities.creativeMode && !EnchantingTweaks.getConfig().allowBypassAnvilMaxLevel());
		}

	}



}
