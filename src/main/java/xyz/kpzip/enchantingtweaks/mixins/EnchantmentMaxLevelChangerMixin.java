package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemGroups;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffers;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentTweaksHelper;
import xyz.kpzip.enchantingtweaks.util.MixinPriority;
import xyz.kpzip.enchantingtweaks.util.RomanNumerals;

public final class EnchantmentMaxLevelChangerMixin {
	
	private EnchantmentMaxLevelChangerMixin() {}
	
	@Mixin(value = TradeOffers.EnchantBookFactory.class, priority = MixinPriority.HIGHEST)
	private static abstract class EnchantBookFactoryMixin {
		
		@Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		public int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Mixin(value = EnchantCommand.class, priority = MixinPriority.HIGHEST)
	private static abstract class EnchantCommandMixin {

		@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			if (EnchantingTweaks.getConfig().enchantmentCommandAbidesByMaxLevel()) return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
			return Integer.MAX_VALUE;
		}
	}
	
	@Mixin(value = EnchantmentHelper.class, priority = MixinPriority.HIGHEST)
	private static abstract class EnchantmentHelperMixin {
		
		@Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Mixin(value = EnchantRandomlyLootFunction.class, priority = MixinPriority.HIGHEST)
	private static abstract class EnchantRandomlyLootFunctionMixin {
		
		@Redirect(method = "addEnchantmentToStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Mixin(value = ItemGroups.class, priority = MixinPriority.HIGH)
	private static abstract class ItemGroupsMixin {
		
		//Top Teir Bytecode BS right here:
		@Redirect(method = "method_48942", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
		
		@Redirect(method = "method_48942", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinLevel()I"))
		private static int getMinLevelHack(Enchantment e) {
			int m = EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
			return m > 15 || m == 0 ? EnchantmentTweaksHelper.getEnchantmentMaxLevel(e) : e.getMinLevel();
		}
		
		@Redirect(method = "method_48949", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel2(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
		
	}
	
	@Mixin(value = Enchantment.class, priority = MixinPriority.LOWEST)
	private static abstract class EnchantmentMixin {
		
		@Unique
		private static int currentLevel = -1;
		
		@Redirect(method = "getName", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
		private MutableText appendNumeral(MutableText enchantmentName, Text useless) {
			return enchantmentName.append(RomanNumerals.getNumeral(Math.max(1, currentLevel)));
		}
		
		@Inject(method = "getName", at = @At("HEAD"))
		public void getNameHead(int level, CallbackInfoReturnable<Text> cir) {
			currentLevel = level;
		}
		
		@Inject(method = "getName", at = @At("RETURN"))
		public void getNameReturn(int level, CallbackInfoReturnable<Text> cir) {
			currentLevel = -1;
		}
	}
}
