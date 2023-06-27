package xyz.kpzip.enchantingtweaks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import xyz.kpzip.enchantingtweaks.config.EnchantingTweaksConfig;
import xyz.kpzip.enchantingtweaks.config.JsonHandler;

public class EnchantingTweaks implements ModInitializer {
	
	
	public static final Logger LOGGER = LoggerFactory.getLogger("enchantingtweaks");

	public static final String MOD_ID = "enchantingtweaks";
	public static final String MOD_VERSION = "1.0.0";
	public static final EnchantingTweaksConfig CONFIG = JsonHandler.readConfig(EnchantingTweaksConfig.class, EnchantingTweaksConfig::new, EnchantingTweaksConfig.FILE_NAME, EnchantingTweaksConfig.FILE_EXTENSION, MOD_ID);
	
	
	
	@Override
	public void onInitialize() {
		
	}
}
