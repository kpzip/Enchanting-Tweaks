package xyz.kpzip.enchantingtweaks.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

public abstract class EnchantmentLevelHelper {
	
	public static Integer getEnchantmentMaxLevel(Enchantment e) {
		for (String s : EnchantingTweaks.getConfig().getMaxLevels().keySet()) {
			if (s.equals(EnchantmentHelper.getEnchantmentId(e).toString())) {
				return EnchantingTweaks.getConfig().getMaxLevels().get(s) < 1 ? Integer.MAX_VALUE : EnchantingTweaks.getConfig().getMaxLevels().get(s);
			}
		}
		return e.getMaxLevel();
	}

}
