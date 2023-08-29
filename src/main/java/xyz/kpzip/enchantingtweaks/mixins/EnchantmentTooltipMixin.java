package xyz.kpzip.enchantingtweaks.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
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
	        		if (Screen.hasShiftDown()) {
	        			tooltip.addAll(EnchantmentTweaksHelper.getDescription(e));
	        		}
	        		if (Screen.hasControlDown()) {
	        			tooltip.addAll(EnchantmentTweaksHelper.getApplicableItemsText(e));
	        		}
	        		if (Screen.hasAltDown()) {
	        			tooltip.addAll(EnchantmentTweaksHelper.getExclusivityText(e));
	        		}
	        		
	            });
	        }
	        if (!Screen.hasShiftDown() && hasEnchantments) {
	        	tooltip.add(EnchantmentTweaksHelper.getHiddenDescriptionText());
	        }
	        if (!Screen.hasControlDown() && hasEnchantments) {
	        	tooltip.add(EnchantmentTweaksHelper.getHiddenAdvancedDescriptionText());
	        }
	        if (!Screen.hasAltDown() && hasEnchantments) {
	        	tooltip.add(EnchantmentTweaksHelper.getHiddenExclusivityText());
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
