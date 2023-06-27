package xyz.kpzip.enchantingtweaks.config;

import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

public interface Cfgstorrer {
	
	public EnchantingTweaksConfig etcfg = JsonHandler.readConfig(EnchantingTweaksConfig.class, EnchantingTweaksConfig::new, EnchantingTweaksConfig.FILE_NAME, EnchantingTweaksConfig.FILE_EXTENSION, EnchantingTweaks.MOD_ID);
	
	public default EnchantingTweaksConfig getEtcfg() {
		return etcfg;
	}

}
