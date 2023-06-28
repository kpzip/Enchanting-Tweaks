package xyz.kpzip.enchantingtweaks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import xyz.kpzip.enchantingtweaks.config.ConfigHandler;
import xyz.kpzip.enchantingtweaks.config.JsonHandler;
import xyz.kpzip.enchantingtweaks.networking.EnchantingTweaksConfig;

public class EnchantingTweaks implements ModInitializer {
	
	public static final Logger LOGGER = LoggerFactory.getLogger("enchantingtweaks");

	public static final String MOD_ID = "enchantingtweaks";
	public static final String MOD_VERSION = "1.0.0";
	public static final EnchantingTweaksConfig CONFIG = JsonHandler.readConfig(EnchantingTweaksConfig.class, EnchantingTweaksConfig::new, EnchantingTweaksConfig.FILE_NAME, EnchantingTweaksConfig.FILE_EXTENSION, MOD_ID);
	
	@Override
	public void onInitialize() {
		ConfigHandler.registerConfig(CONFIG);
	}
}
