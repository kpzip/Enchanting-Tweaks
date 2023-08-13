package xyz.kpzip.enchantingtweaks.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface CustomApplicabilityTooltipProvider {
	
	public static final String TRANSLATION_KEY_PREFIX = "enchantment.descriptions.applicable";
	
	// ".line#" will be appended to the translation key. # will be replaced with the line number.
	public default String getApplicabilityTooltipTranslationKey() {
		return TRANSLATION_KEY_PREFIX + ".armor";
	}
	
	public static MutableText getFirstApplicabilityTooltipLine(CustomApplicabilityTooltipProvider enchantment) {
		return Text.translatableWithFallback(enchantment.getApplicabilityTooltipTranslationKey() + ".line1", EnchantmentTweaksHelper.APPLICABILITY_FALLBACK);
	}
	
	public static MutableText getApplicabilityTooltipLine(CustomApplicabilityTooltipProvider enchantment, int linenum) {
		if (linenum == 1) return getFirstApplicabilityTooltipLine(enchantment);
		return Text.translatableWithFallback(enchantment.getApplicabilityTooltipTranslationKey() + ".line" + String.valueOf(linenum), "");
	}

}
