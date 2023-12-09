package xyz.kpzip.enchantingtweaks.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.controls.Keybindings;
import xyz.kpzip.enchantingtweaks.util.CustomApplicabilityTooltipProvider;
import xyz.kpzip.enchantingtweaks.util.EnchantmentTweaksHelper;
import xyz.kpzip.enchantingtweaks.util.MixinPriority;

public final class EnchantmentTooltipMixin {

	private EnchantmentTooltipMixin() {}
	
	@Environment(value=EnvType.CLIENT)
	@Mixin(value = ItemStack.class, priority = MixinPriority.LOW)
	public static abstract class ItemStackMixin {
		
		//Only used in one method, but must be declared here since it is used in a lambda
		@Unique
		private static boolean hasEnchantments;
		
		/**
		 * @Author kpzip
		 * @Reason add tooltip code for enchantment descriptions
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		public static void appendEnchantments(List<Text> tooltip, NbtList enchantments) {
			hasEnchantments = false;
	        for (int i = 0; i < enchantments.size(); ++i) {
	            NbtCompound nbtCompound = enchantments.getCompound(i);
	            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e -> {
	            	int level = EnchantmentHelper.getLevelFromNbt(nbtCompound);
	            	tooltip.add(e.getName(level));
	            	hasEnchantments = true;
	        		if (Keybindings.isDescriptionsPressed()) {
	        			tooltip.addAll(EnchantmentTweaksHelper.getDescription(e));
	        		}
	        		if (Keybindings.isApplicabilityPressed()) {
	        			tooltip.addAll(EnchantmentTweaksHelper.getApplicableItemsText(e));
	        		}
	        		if (Keybindings.isExclusivityPressed()) {
	        			tooltip.addAll(EnchantmentTweaksHelper.getExclusivityText(e));
	        		}
	        		
	            });
	        }
	        if (EnchantingTweaks.getConfig().showDescriptionHints() && !Keybindings.isDescriptionsPressed() && hasEnchantments) {
	        	tooltip.add(Text.translatable(EnchantmentTweaksHelper.getHiddenDescriptionText(), Keybindings.getDesciptionsKeyName()).formatted(Formatting.DARK_GRAY));
	        }
	        if (EnchantingTweaks.getConfig().showDescriptionHints() && !Keybindings.isApplicabilityPressed() && hasEnchantments && Keybindings.isDescriptionsPressed()) {
	        	tooltip.add(Text.translatable(EnchantmentTweaksHelper.getHiddenAdvancedDescriptionText(), Keybindings.getApplicabilityKeyName()).formatted(Formatting.DARK_GRAY));
	        }
	        if (EnchantingTweaks.getConfig().showDescriptionHints() && !Keybindings.isExclusivityPressed() && hasEnchantments && Keybindings.isDescriptionsPressed()) {
	        	tooltip.add(Text.translatable(EnchantmentTweaksHelper.getHiddenExclusivityText(), Keybindings.getExclusivityKeyName()).formatted(Formatting.DARK_GRAY));
	        }
	    }
	}
	
	@Mixin(value = DamageEnchantment.class, priority = MixinPriority.DEFAULT)
	private static abstract class DamageEnchantmentMixin implements CustomApplicabilityTooltipProvider {
		
		@Override
		public String getApplicabilityTooltipTranslationKey() {
			return CustomApplicabilityTooltipProvider.TRANSLATION_KEY_PREFIX + ".sword_or_axe";
		}
	}
}
