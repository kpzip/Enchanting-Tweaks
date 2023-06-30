package xyz.kpzip.enchantingtweaks.mixins.enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;
import xyz.kpzip.enchantingtweaks.util.RomanNumerals;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
	
	@Shadow public abstract String getTranslationKey();
	
	@Shadow public abstract boolean isCursed();
	
	//TODO Overwrite: Maintain this for every update in case the original changes
	@Overwrite
	public Text getName(int level) {
        MutableText mutableText = Text.translatable(this.getTranslationKey());
        if (this.isCursed()) {
            mutableText.formatted(Formatting.RED);
        } else {
            mutableText.formatted(Formatting.GRAY);
        }
        if (level != 1 || EnchantmentLevelHelper.getEnchantmentMaxLevel((Enchantment) (Object)this) != 1) {
            mutableText.append(ScreenTexts.SPACE).append(Text.of(RomanNumerals.getNumeral(level)));
        }
        return mutableText;
    }

}
