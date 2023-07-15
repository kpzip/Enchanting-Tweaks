package xyz.kpzip.enchantingtweaks.mixins;

import java.util.Set;
import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;

@Mixin(ItemGroups.class)
public abstract class ItemGroupsMixin {
	
	//TODO Overwrite: Maintain this for every update in case the original changes
	@Overwrite
	private static void addMaxLevelEnchantedBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, Set<EnchantmentTarget> enchantmentTargets, ItemGroup.StackVisibility visibility) {
        registryWrapper.streamEntries().map(RegistryEntry::value).filter(enchantment -> enchantmentTargets.contains((Object)enchantment.target))
        .map(enchantment -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry((Enchantment)enchantment, EnchantmentLevelHelper.getEnchantmentMaxLevel(enchantment))))
        .forEach(stack -> entries.add((ItemStack)stack, visibility));
    }

	//TODO Overwrite: Maintain this for every update in case the original changes
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
		int maxLevel = EnchantmentLevelHelper.getEnchantmentMaxLevel(e);
		int minLevel = e.getMinLevel();
		if (maxLevel > 15) return IntStream.rangeClosed(maxLevel, maxLevel);
		return IntStream.rangeClosed(minLevel, maxLevel);
	}
	

}
