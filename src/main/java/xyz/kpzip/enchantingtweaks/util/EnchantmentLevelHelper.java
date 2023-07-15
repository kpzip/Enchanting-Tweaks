package xyz.kpzip.enchantingtweaks.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.Vanishable;
import net.minecraft.registry.Registries;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

public abstract class EnchantmentLevelHelper {
	
	private static List<ItemStack> testItems = getEnchantableItems();
	
	public static int getEnchantmentMaxLevel(Enchantment e) {
		for (String s : EnchantingTweaks.getConfig().getMaxLevels().keySet()) {
			if (s.equals(EnchantmentHelper.getEnchantmentId(e).toString())) {
				return EnchantingTweaks.getConfig().getMaxLevels().get(s) < 1 ? Integer.MAX_VALUE : EnchantingTweaks.getConfig().getMaxLevels().get(s);
			}
		}
		return e.getMaxLevel();
	}
	
	public static boolean canCombine(Enchantment e1, Enchantment e2) {
		if (e1 == e2) return false;
		Set<String> test = new HashSet<String>();
		test.add(EnchantmentHelper.getEnchantmentId(e1).toString());
		test.add(EnchantmentHelper.getEnchantmentId(e2).toString());
		Map<Set<String>, Boolean> exclusivity = EnchantingTweaks.getConfig().getExclusivity();
		for(Set<String> s : exclusivity.keySet()) {
			if (s.equals(test)) {
				return !exclusivity.get(s);
			}
		}
		return e1.canAccept(e2) && e2.canAccept(e1);
	}
	
	public static boolean canBeOnSameItem(Enchantment e1, Enchantment e2) {
		for (ItemStack i : testItems) {
			if (e1.isAcceptableItem(i) && e2.isAcceptableItem(i)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isPossiblyEnchantable(Item i) {
		return i.isDamageable() || i instanceof Vanishable || Block.getBlockFromItem(i) instanceof Vanishable || i instanceof Equipment || Block.getBlockFromItem(i) instanceof Equipment
				|| i instanceof CrossbowItem || i instanceof BowItem || i instanceof TridentItem || i instanceof MiningToolItem || i instanceof SwordItem || i instanceof ArmorItem || i instanceof FishingRodItem;
	}
	
	private static ArrayList<ItemStack> getEnchantableItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Item i : Registries.ITEM) {
			if (isPossiblyEnchantable(i)) {
				items.add(new ItemStack(i));
			}
		}
		return items;
	}

}
