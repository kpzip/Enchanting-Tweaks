package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	
	@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir, float f) {
		cir.setReturnValue(f * (1.0f + (0.2f * (EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, ((PlayerEntity)(Object)this)) - 1))));
		cir.cancel();
	}

}
