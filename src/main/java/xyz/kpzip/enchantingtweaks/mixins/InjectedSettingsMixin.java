package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.MixinPriority;

public final class InjectedSettingsMixin {

	private InjectedSettingsMixin() {}
	
	@Mixin(value = AnvilScreenHandler.class, priority = MixinPriority.LOWEST)
	private static abstract class AnvilScreenHandlerMixin {
		
		//This allows us to lie to the part of the code that checks if something should be too expensive.
		//If the config is set to remove the "TOO EXPENSIVE" text, and the repair would normally be too expensive, we tell the anvil that the level cost is 39,
		//which allows you to still use the anvil
		//This works because the code that actually deals with displaying and deducting the levels is in a seperate function and is not altered
		@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
		private int getHack(Property p) {
			if (EnchantingTweaks.getConfig().allowBypassAnvilMaxLevel() && p.get() >= 40) {
				return 39;
			}
			else return p.get();
		}
	}
	
	@Mixin(value = TridentItem.class, priority = MixinPriority.HIGHEST)
	public static abstract class TridentItemMixin {
		
		private TridentItemMixin() {}
		
		@Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
		private boolean shouldRiptideWork(PlayerEntity e) {
			return e.isTouchingWaterOrRain() || EnchantingTweaks.getConfig().allowRiptideAlways();
		}
		
		@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
		private boolean shouldRiptideWork2(PlayerEntity e) {
			return e.isTouchingWaterOrRain() || EnchantingTweaks.getConfig().allowRiptideAlways();
		}
	}

}
