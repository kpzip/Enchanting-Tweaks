package xyz.kpzip.enchantingtweaks.mixins;

import java.util.Set;
import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffers;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentTweaksHelper;
import xyz.kpzip.enchantingtweaks.util.RomanNumerals;

public final class EnchantmentMaxLevelChangerMixin {
	
	private EnchantmentMaxLevelChangerMixin() {}
	
	@Mixin(TradeOffers.EnchantBookFactory.class)
	private static abstract class EnchantBookFactoryMixin {
		
		@Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		public int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Mixin(EnchantCommand.class)
	private static abstract class EnchantCommandMixin {

		@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			if (EnchantingTweaks.getConfig().enchantmentCommandAbidesByMaxLevel()) return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
			return Integer.MAX_VALUE;
		}
	}
	
	@Mixin(EnchantmentHelper.class)
	private static abstract class EnchantmentHelperMixin {
		
		@Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Mixin(EnchantRandomlyLootFunction.class)
	private static abstract class EnchantRandomlyLootFunctionMixin {
		
		@Redirect(method = "addEnchantmentToStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private static int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
	}
	
	@Mixin(ItemGroups.class)
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
	
	@Mixin(Enchantment.class)
	private static abstract class EnchantmentMixin {

		@Shadow public abstract String getTranslationKey();
		
		@Shadow public abstract boolean isCursed();
		
		/**
		 * @Author kpzip
		 * @Reason Stop using the lang file for roman numerals
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		public Text getName(int level) {
	        MutableText mutableText = Text.translatable(this.getTranslationKey());
	        if (this.isCursed()) {
	            mutableText.formatted(Formatting.RED);
	        } else {
	            mutableText.formatted(Formatting.GRAY);
	        }
	        if (level != 1 || EnchantmentTweaksHelper.getEnchantmentMaxLevel((Enchantment) (Object)this) != 1) {
	            mutableText.append(ScreenTexts.SPACE).append(Text.of(RomanNumerals.getNumeral(level)));
	        }
	        return mutableText;
	    }
		
		@Redirect(method = "canCombine", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canAccept(Lnet/minecraft/enchantment/Enchantment;)Z"))
		private boolean acautallyCanAccept(Enchantment e1, Enchantment e2) {
			return EnchantmentTweaksHelper.canCombine(e1, e2);
		}

	}
}
