package xyz.kpzip.enchantingtweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.util.EnchantmentTweaksHelper;

public final class AnvilMixin {
	
	private AnvilMixin() {}
	
	@Mixin(AnvilScreenHandler.class)
	private static abstract class AnvilScreenHandlerMixin {
		
		@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
		private int getRealMaxLevel(Enchantment e) {
			return EnchantmentTweaksHelper.getEnchantmentMaxLevel(e);
		}
		
		//This allows us to lie to the part of the code that checks if something should be too expensive.
		//If the config is set to remove the "TOO EXPENSIVE" text, and the repair would normally be too expensive, we tell the anvil that the level cost is 39,
		//which allows you to still use the anvil
		//This works because the code that actually deals with displaying and deducting the levels is in a seperate function and is not altered
		//TODO this might not work in the future
		@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
		private int getHack(Property p) {
			if (EnchantingTweaks.getConfig().allowBypassAnvilMaxLevel() && p.get() >= 40) {
				return 39;
			}
			else return p.get();
		}
	}
	
	@Environment(value=EnvType.CLIENT)
	@Mixin(AnvilScreen.class)
	private static abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler>{
		
		public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
			super(handler, playerInventory, title, texture);
			this.player = playerInventory.player;
		}
		
		@Shadow private final PlayerEntity player;

		/**
		 * @Author kpzip
		 * @Reason add overrides for settings in EnchantingTweaks
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		public void drawForeground(DrawContext context, int mouseX, int mouseY) {
	        RenderSystem.disableBlend();
	        super.drawForeground(context, mouseX, mouseY);
	        int i = ((AnvilScreenHandler)this.handler).getLevelCost();
	        if (i > 0) {
	            Text text;
	            int j = 8453920;
	            if (i >= 40 && !this.client.player.getAbilities().creativeMode && !EnchantingTweaks.getConfig().allowBypassAnvilMaxLevel()) {
	                text = Text.translatable("container.repair.expensive");
	                j = 0xFF6060;
	            } else 
	            if (!((AnvilScreenHandler)this.handler).getSlot(2).hasStack()) {
	                text = null;
	            } else {
	                text = Text.translatable("container.repair.cost", i);
	                if (!((AnvilScreenHandler)this.handler).getSlot(2).canTakeItems(this.player)) {
	                    j = 0xFF6060;
	                }
	            }
	            if (text != null) {
	                int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
	                context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 0x4F000000);
	                context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
	            }
	        }
	    }

	}



}
