package xyz.kpzip.enchantingtweaks.networking;

import java.util.HashMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.config.JsonHandler;
import xyz.kpzip.enchantingtweaks.config.SyncedConfig;

public class EnchantingTweaksConfig implements SyncedConfig {
	
	public static final String FILE_NAME = "enchanting-tweaks-config";
	public static final String FILE_EXTENSION = "json";
	
	private boolean bypassAnvilMaxLevel = true;
	private boolean allowBowEnchantmentsTogether = true;
	private boolean allowDamageEnchantmentsTogether = false;
	private boolean allowProtectionEnchantmentsTogether = false;
	private boolean allowCrossbowEnchantmentsTogether = false;
	
	private HashMap<String, Byte> maxLevels = addAllEnchantments(new HashMap<String, Byte>());
	
	public EnchantingTweaksConfig() {
		updateConfig();
	}
	
	//encode boolean values into a single byte for syncing
	public byte toByte() {
		byte b = 0;
		if (bypassAnvilMaxLevel) b |= 128;
		if (allowBowEnchantmentsTogether) b |= 64;
		if (allowDamageEnchantmentsTogether) b |= 32;
		if (allowProtectionEnchantmentsTogether) b |= 16;
		if (allowCrossbowEnchantmentsTogether) b |= 8;
		return b;
	}
	
	public void fromByte(byte b) {
		if (b % 256 > 127) bypassAnvilMaxLevel = true; else bypassAnvilMaxLevel = false; //is bit 1 set?
		if (b % 128 > 63) allowBowEnchantmentsTogether = true; else allowBowEnchantmentsTogether = false; //is bit 2 set?
		if (b % 64 > 31) allowDamageEnchantmentsTogether = true; else allowDamageEnchantmentsTogether = false; //is bit 3 set?
		if (b % 32 > 15) allowProtectionEnchantmentsTogether = true; else allowProtectionEnchantmentsTogether = false; //is bit 4 set?
		if (b % 16 > 7) allowCrossbowEnchantmentsTogether = true; else allowCrossbowEnchantmentsTogether = false; //is bit 5 set?
	}
	
	public String getFileName() {
		return FILE_NAME;
	}
	
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	@Override
	public Identifier getSyncPacketIdentifier() {
		return NetworkingConstants.ETCFG_PACKET_ID;
	}

	@Override
	public void loadFromPacket(PacketByteBuf buf) {
		this.fromByte(buf.readByte());
		buf.release();
	}

	@Override
	public void writeToPacket(PacketByteBuf buf) {
		buf.writeByte(this.toByte());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EnchantingTweaksConfig reloadFromFile() {
		EnchantingTweaksConfig readcfg = JsonHandler.readConfig(EnchantingTweaksConfig.class, EnchantingTweaksConfig::new, FILE_NAME, FILE_EXTENSION, EnchantingTweaks.MOD_ID);
		this.bypassAnvilMaxLevel = readcfg.allowBypassAnvilMaxLevel();
		this.allowBowEnchantmentsTogether = readcfg.allowBowEnchantmentsTogether();
		this.allowDamageEnchantmentsTogether = readcfg.allowDamageEnchantmentsTogether();
		this.allowProtectionEnchantmentsTogether = readcfg.allowProtectionEnchantmentsTogether();
		this.allowCrossbowEnchantmentsTogether = readcfg.allowCrossbowEnchantmentsTogether();
		
		maxLevels.clear();
		this.maxLevels = readcfg.maxLevels;
		addAllEnchantments(this.maxLevels);
		return this;
	}
	
	public void updateConfig() {
		fromByte(toByte());
		addAllEnchantments(this.maxLevels);
	}
	
	public boolean allowBypassAnvilMaxLevel() {
		return bypassAnvilMaxLevel;
	}

	public boolean allowBowEnchantmentsTogether() {
		return allowBowEnchantmentsTogether;
	}

	public boolean allowDamageEnchantmentsTogether() {
		return allowDamageEnchantmentsTogether;
	}

	public boolean allowProtectionEnchantmentsTogether() {
		return allowProtectionEnchantmentsTogether;
	}
	
	public boolean allowCrossbowEnchantmentsTogether() {
		return allowCrossbowEnchantmentsTogether;
	}
	
	public boolean isBypassAnvilMaxLevel() {
		return bypassAnvilMaxLevel;
	}

	public boolean isAllowBowEnchantmentsTogether() {
		return allowBowEnchantmentsTogether;
	}

	public boolean isAllowDamageEnchantmentsTogether() {
		return allowDamageEnchantmentsTogether;
	}

	public boolean isAllowProtectionEnchantmentsTogether() {
		return allowProtectionEnchantmentsTogether;
	}

	public boolean isAllowCrossbowEnchantmentsTogether() {
		return allowCrossbowEnchantmentsTogether;
	}
	
	public HashMap<String, Byte> getMaxLevels() {
		return maxLevels;
	}
	
	private static HashMap<String, Byte> addAllEnchantments(HashMap<String, Byte> enchants) {
		for (Enchantment e : Registries.ENCHANTMENT) {
			if (!enchants.containsKey(EnchantmentHelper.getEnchantmentId(e).toString())) {
				enchants.put(EnchantmentHelper.getEnchantmentId(e).toString(), (byte) e.getMaxLevel());
			}
		}
		return enchants;
	}

	

}
