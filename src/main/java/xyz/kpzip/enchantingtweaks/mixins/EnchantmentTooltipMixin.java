package xyz.kpzip.enchantingtweaks.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
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
	        		
	            });
	        }
	        if (!Screen.hasShiftDown() && hasEnchantments) {
	        	tooltip.add(EnchantmentTweaksHelper.getHiddenDescriptionText());
	        }
	        if (!Screen.hasControlDown() && hasEnchantments) {
	        	tooltip.add(EnchantmentTweaksHelper.getHiddenAdvancedDescriptionText());
	        }
	    }
	}
	
	@Mixin(value = Enchantment.class, priority = MixinPriority.HIGHEST)
	private static abstract class EnchantmentMixin {
		
		@Redirect(method = "canCombine", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canAccept(Lnet/minecraft/enchantment/Enchantment;)Z"))
		private boolean acautallyCanAccept(Enchantment e1, Enchantment e2) {
			return EnchantmentTweaksHelper.canCombine(e1, e2);
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
