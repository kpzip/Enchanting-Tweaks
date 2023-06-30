package xyz.kpzip.enchantingtweaks.networking;

import java.util.HashMap;
import java.util.Map;

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
	private boolean showAllLevelEnchantedBooksInCreativeInventory = true;
	private boolean enchantmentCommandAbidesByMaxLevel = false;
	
	private Map<String, Integer> maxLevels = addAllEnchantments(new HashMap<String, Integer>());
	
	public EnchantingTweaksConfig() {
		updateConfig();
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
		this.maxLevels = maxLevelsFromPacket(buf);
		bypassAnvilMaxLevel = buf.readBoolean();
		allowBowEnchantmentsTogether = buf.readBoolean();
		allowDamageEnchantmentsTogether = buf.readBoolean();
		allowProtectionEnchantmentsTogether = buf.readBoolean();
		allowCrossbowEnchantmentsTogether = buf.readBoolean();
		showAllLevelEnchantedBooksInCreativeInventory = buf.readBoolean();
		enchantmentCommandAbidesByMaxLevel = buf.readBoolean();
		buf.release();
	}
	
	public static Map<String, Integer> maxLevelsFromPacket(PacketByteBuf buf) {
        return buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt);
    }

	@Override
	public void writeToPacket(PacketByteBuf buf) {
		maxLevelsToPacket(buf, maxLevels);
		buf.writeBoolean(bypassAnvilMaxLevel);
		buf.writeBoolean(allowBowEnchantmentsTogether);
		buf.writeBoolean(allowDamageEnchantmentsTogether);
		buf.writeBoolean(allowProtectionEnchantmentsTogether);
		buf.writeBoolean(allowCrossbowEnchantmentsTogether);
		buf.writeBoolean(showAllLevelEnchantedBooksInCreativeInventory);
		buf.writeBoolean(enchantmentCommandAbidesByMaxLevel);
		
	}
	
	public static void maxLevelsToPacket(PacketByteBuf buf, Map<String, Integer> m) {
		buf.writeMap(m, PacketByteBuf::writeString, (buffer, i) -> {buffer.writeInt(i);});
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
		this.showAllLevelEnchantedBooksInCreativeInventory = readcfg.showAllLevelEnchantedBooksInCreativeInventory();
		this.enchantmentCommandAbidesByMaxLevel = readcfg.enchantmentCommandAbidesByMaxLevel();
		
		maxLevels.clear();
		this.maxLevels = readcfg.maxLevels;
		addAllEnchantments(this.maxLevels);
		return this;
	}
	
	public void updateConfig() {
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
	
	public boolean showAllLevelEnchantedBooksInCreativeInventory() {
		return showAllLevelEnchantedBooksInCreativeInventory;
	}
	
	public boolean enchantmentCommandAbidesByMaxLevel() {
		return enchantmentCommandAbidesByMaxLevel;
	}
	
	public Map<String, Integer> getMaxLevels() {
		return maxLevels;
	}
	
	private static Map<String, Integer> addAllEnchantments(Map<String, Integer> maxLevels2) {
		for (Enchantment e : Registries.ENCHANTMENT) {
			if (!maxLevels2.containsKey(EnchantmentHelper.getEnchantmentId(e).toString())) {
				maxLevels2.put(EnchantmentHelper.getEnchantmentId(e).toString(), e.getMaxLevel());
			}
		}
		return maxLevels2;
	}
	

}
