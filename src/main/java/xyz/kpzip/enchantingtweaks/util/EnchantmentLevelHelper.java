package xyz.kpzip.enchantingtweaks.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

public abstract class EnchantmentLevelHelper {
	
	public static int getEnchantmentMaxLevel(Enchantment e) {
		for (String s : EnchantingTweaks.getConfig().getMaxLevels().keySet()) {
			if (s.equals(EnchantmentHelper.getEnchantmentId(e).toString())) {
				return EnchantingTweaks.getConfig().getMaxLevels().get(s) < 1 ? Integer.MAX_VALUE : EnchantingTweaks.getConfig().getMaxLevels().get(s);
			}
		}
		return e.getMaxLevel();
	}
	
	public static boolean canCombine(Enchantment e1, Enchantment e2) {
		Set<String> test = new HashSet<String>();
		test.add(EnchantmentHelper.getEnchantmentId(e1).toString());
		test.add(EnchantmentHelper.getEnchantmentId(e2).toString());
		Map<Set<String>, Boolean> exclusivity = EnchantingTweaks.getConfig().getExclusivity();
		for(Set<String> s : exclusivity.keySet()) {
			if (s.equals(test)) {
				return !exclusivity.get(s) && e1 != e2;
			}
		}
		return e1.canAccept(e2) && e2.canAccept(e1);
	}

}
