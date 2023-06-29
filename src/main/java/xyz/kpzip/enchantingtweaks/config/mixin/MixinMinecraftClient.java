package xyz.kpzip.enchantingtweaks.config.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import xyz.kpzip.enchantingtweaks.config.ConfigHandler;
import xyz.kpzip.enchantingtweaks.config.SyncedConfig;

@Environment(value=EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

	@Inject(method = "disconnect", at = @At("TAIL"))
	public void onDisconnect(Screen screen, CallbackInfo ci) {
		for (SyncedConfig c : ConfigHandler.configs) {
			c.reloadFromFile();
		}
	}
	
}
