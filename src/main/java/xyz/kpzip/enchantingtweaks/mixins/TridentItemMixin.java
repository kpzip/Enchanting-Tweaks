package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

@Mixin(TridentItem.class)
public final class TridentItemMixin {
	
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
