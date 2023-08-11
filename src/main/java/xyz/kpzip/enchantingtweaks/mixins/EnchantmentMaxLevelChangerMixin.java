package xyz.kpzip.enchantingtweaks.mixins;

import java.util.Set;
import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
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
		
		/**
		 * @Author kpzip
		 * @Reason allow the max level to be fetched from the enchanting tweaks config
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		private static void addMaxLevelEnchantedBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, Set<EnchantmentTarget> enchantmentTargets, ItemGroup.StackVisibility visibility) {
	        registryWrapper.streamEntries().map(RegistryEntry::value).filter(enchantment -> enchantmentTargets.contains((Object)enchantment.target))
	        .map(enchantment -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry((Enchantment)enchantment, EnchantmentTweaksHelper.getEnchantmentMaxLevel(enchantment))))
	        .forEach(stack -> entries.add((ItemStack)stack, visibility));
	    }

		/**
		 * @Author kpzip
		 * @Reason allow the max level to be fetched from the enchanting tweaks config and dont add all level enchanted books if the level is greater than 15
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
	    private static void addAllLevelEnchantedBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, Set<EnchantmentTarget> enchantmentTargets, ItemGroup.StackVisibility visibility) {
	        if (EnchantingTweaks.getConfig().showAllLevelEnchantedBooksInCreativeInventory()) registryWrapper.streamEntries()
	        .map(RegistryEntry::value).filter(enchantment -> enchantmentTargets.contains((Object)enchantment.target))
	        .flatMap(enchantment -> getEnchantRange(enchantment)
	        .mapToObj(level -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry((Enchantment)enchantment, level))))
	        .forEach(stack -> entries.add((ItemStack)stack, visibility));
	        else addMaxLevelEnchantedBooks(entries, registryWrapper, enchantmentTargets, visibility);
	    }
		
		@Unique
		private static IntStream getEnchantRange(Enchantment e) {
			int maxLevel = EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
			int minLevel = e.getMinLevel();
			if (maxLevel > 15) return IntStream.rangeClosed(maxLevel, maxLevel);
			return IntStream.rangeClosed(minLevel, maxLevel);
		}
	}
	
	@Mixin(value = Enchantment.class, priority = MixinPriority.LOWEST)
	private static abstract class EnchantmentMixin {
		
		@Unique
		private static int currentLevel = -1;
		
		@Redirect(method = "getName", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
		private MutableText appendNumeral(MutableText enchantmentName, Text useless) {
			return enchantmentName.append(Text.of(RomanNumerals.getNumeral(Math.max(1, currentLevel))));
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
