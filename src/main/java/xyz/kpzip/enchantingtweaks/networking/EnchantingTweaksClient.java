package xyz.kpzip.enchantingtweaks.networking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.config.ConfigHandler;

@Environment(value=EnvType.CLIENT)
public class EnchantingTweaksClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		ConfigHandler.registerConfigClient(EnchantingTweaks.getConfig());
	}
}
