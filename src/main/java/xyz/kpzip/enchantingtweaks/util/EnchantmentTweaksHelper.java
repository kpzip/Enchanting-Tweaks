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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

public final class EnchantmentTweaksHelper {
	
	private EnchantmentTweaksHelper() {}
	
	private static final String ENCHANTMENT_DESCRIPTION_KEY = "enchantment.descriptions.";
	
	private static final Text ENCHANTMENT_DESCRIPTION_HIDDEN_TEXT = Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + "hidden").formatted(Formatting.DARK_GRAY);
	private static final Text ENCHANTMENT_DESCRIPTION_HIDDEN_ADVANCED_TEXT = Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + "hidden_advanced").formatted(Formatting.DARK_GRAY);
	private static final Text ENCHANTMENT_EXCLUSIVITY_HIDDEN_TEXT = Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + "hidden_exclusive").formatted(Formatting.DARK_GRAY);
	
	private static final MutableText ENCHANTMENT_DESCRIPTION_PREFIX = Text.literal("  ").formatted(Formatting.DARK_GRAY);
	
	public static final String DESCRIPTION_FALLBACK = "§8No Description";
	public static final String APPLICABILITY_FALLBACK = "§8-None";
	
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
				return exclusivity.get(s);
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
	
	private static List<Enchantment> getExclusiveEnchantments(Enchantment e) {
		List<Enchantment> exclusives = new ArrayList<Enchantment>();
		for (Set<String> enchantments : EnchantingTweaks.getConfig().getExclusivity().keySet()) {
			if (enchantments.contains(EnchantmentHelper.getEnchantmentId(e).toString()) && !EnchantingTweaks.getConfig().getExclusivity().get(enchantments)) {
				for (String s : enchantments) {
					if (s.equals(EnchantmentHelper.getEnchantmentId(e).toString())) {
						continue;
					}
					Enchantment exclusive = null;
					for (Enchantment en : Registries.ENCHANTMENT) {
						if (EnchantmentHelper.getEnchantmentId(en).toString().equals(s)) {
							exclusive = en;
							break;
						}
					}
					exclusives.add(exclusive);
				}
			}
		}
		return exclusives;
	}
	
	public static List<Text> getDescription(Enchantment e) {
		String enchantmentId = EnchantmentHelper.getEnchantmentId(e).toString();
		List<Text> description = new ArrayList<Text>();
		MutableText line;
		description.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + enchantmentId + ".line1", DESCRIPTION_FALLBACK)));
		for (int i = 2; (line = Text.translatableWithFallback(ENCHANTMENT_DESCRIPTION_KEY + enchantmentId + ".line" + String.valueOf(i), "")).asTruncatedString(1) != "" && i < 10; i++) description.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(line));
		return description;
	}
	
	public static List<Text> getApplicableItemsText(Enchantment e) {
		List<Text> lines = new ArrayList<Text>();
		MutableText line;
		lines.add(Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + "applicable_to").formatted(Formatting.DARK_GRAY));
		if (e instanceof CustomApplicabilityTooltipProvider) {
			lines.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(CustomApplicabilityTooltipProvider.getFirstApplicabilityTooltipLine((CustomApplicabilityTooltipProvider)e)));
			for (int i = 2; (line = CustomApplicabilityTooltipProvider.getApplicabilityTooltipLine((CustomApplicabilityTooltipProvider)e, i)).asTruncatedString(1) != "" && i < 10; i++) lines.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(line));
		}
		else {
			lines.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + "applicable." + e.target.toString().toLowerCase() + ".line1", APPLICABILITY_FALLBACK)));
			for (int i = 2; (line = Text.translatableWithFallback(ENCHANTMENT_DESCRIPTION_KEY + "applicable." + e.target.toString().toLowerCase() + ".line" + String.valueOf(i), "")).asTruncatedString(1) != "" && i < 10; i++) lines.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(line));
		}
		return lines;
	}
	
	public static List<Text> getExclusivityText(Enchantment e) {
		List<Text> lines = new ArrayList<Text>();
		List<Enchantment> exclusives = getExclusiveEnchantments(e);
		if (!exclusives.isEmpty()) {
			lines.add(Text.translatable(ENCHANTMENT_DESCRIPTION_KEY + "exclusive_to").formatted(Formatting.DARK_GRAY));
			for (Enchantment en : exclusives) {
				lines.add(ENCHANTMENT_DESCRIPTION_PREFIX.copy().append(Text.translatable(en.getTranslationKey())));
			}
		}
		return lines;
	}
	
	public static Text getHiddenDescriptionText() {
		return ENCHANTMENT_DESCRIPTION_HIDDEN_TEXT;
	}
	
	public static Text getHiddenAdvancedDescriptionText() {
		return ENCHANTMENT_DESCRIPTION_HIDDEN_ADVANCED_TEXT;
	}
	
	public static Text getHiddenExclusivityText() {
		return ENCHANTMENT_EXCLUSIVITY_HIDDEN_TEXT;
	}
	

}
