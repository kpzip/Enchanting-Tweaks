package xyz.kpzip.enchantingtweaks.networking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.config.ConfigWithReadme;
import xyz.kpzip.enchantingtweaks.config.JsonHandler;
import xyz.kpzip.enchantingtweaks.config.SyncedConfig;
import xyz.kpzip.enchantingtweaks.util.EnchantmentLevelHelper;

public class EnchantingTweaksConfig implements SyncedConfig, ConfigWithReadme {
	
	public static final String FILE_NAME = "enchanting-tweaks-config";
	public static final String FILE_EXTENSION = "json";
	
	//Defaults
	private boolean bypassAnvilMaxLevel = true;
	private boolean showAllLevelEnchantedBooksInCreativeInventory = true;
	private boolean enchantmentCommandAbidesByMaxLevel = false;
	private boolean allowRiptideAlways = false;
	
	private Map<String, Integer> maxLevels = addAllEnchantments(new HashMap<String, Integer>());
	private Map<Set<String>, Boolean> exclusivity = getExclusivity(new HashMap<Set<String>, Boolean>());
	
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
		this.exclusivity = exclusivityFromPacket(buf);
		bypassAnvilMaxLevel = buf.readBoolean();
		showAllLevelEnchantedBooksInCreativeInventory = buf.readBoolean();
		enchantmentCommandAbidesByMaxLevel = buf.readBoolean();
		allowRiptideAlways = buf.readBoolean();
		buf.release();
	}
	
	public static Map<String, Integer> maxLevelsFromPacket(PacketByteBuf buf) {
        return buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt);
    }
	
	public static Map<Set<String>, Boolean> exclusivityFromPacket(PacketByteBuf buf) {
		return buf.readMap(bufx -> {return bufx.readCollection(HashSet::new, bufy -> {return bufy.readString();});}, PacketByteBuf::readBoolean);
	}

	@Override
	public void writeToPacket(PacketByteBuf buf) {
		maxLevelsToPacket(buf, maxLevels);
		exclusivityToPacket(buf, exclusivity);
		buf.writeBoolean(bypassAnvilMaxLevel);
		buf.writeBoolean(showAllLevelEnchantedBooksInCreativeInventory);
		buf.writeBoolean(enchantmentCommandAbidesByMaxLevel);
		buf.writeBoolean(allowRiptideAlways);
		
	}
	
	public static void maxLevelsToPacket(PacketByteBuf buf, Map<String, Integer> m) {
		buf.writeMap(m, PacketByteBuf::writeString, (buffer, i) -> {buffer.writeInt(i);});
    }
	
	//This is why I don't like lambdas.
	public static void exclusivityToPacket(PacketByteBuf buf, Map<Set<String>, Boolean> m) {
		buf.writeMap(m, (bufx, pairs) -> {bufx.writeCollection(pairs, (bufy, str) -> {bufy.writeString(str);});}, (bufz, i) -> {bufz.writeBoolean(i);});
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public EnchantingTweaksConfig reloadFromFile() {
		EnchantingTweaksConfig readcfg = JsonHandler.readConfig(EnchantingTweaksConfig.class, EnchantingTweaksConfig::new, FILE_NAME, FILE_EXTENSION, EnchantingTweaks.MOD_ID);
		this.bypassAnvilMaxLevel = readcfg.allowBypassAnvilMaxLevel();
		this.showAllLevelEnchantedBooksInCreativeInventory = readcfg.showAllLevelEnchantedBooksInCreativeInventory();
		this.enchantmentCommandAbidesByMaxLevel = readcfg.enchantmentCommandAbidesByMaxLevel();
		this.allowRiptideAlways = readcfg.allowRiptideAlways();
		
		this.maxLevels = readcfg.maxLevels;
		//addAllEnchantments(this.maxLevels);
		
		this.exclusivity = readcfg.exclusivity;
		//getExclusivity(this.exclusivity);
		
		return this;
	}
	
	public void updateConfig() {
		addAllEnchantments(this.maxLevels);
		getExclusivity(this.exclusivity);
	}
	
	public boolean allowBypassAnvilMaxLevel() {
		return bypassAnvilMaxLevel;
	}
	
	public boolean showAllLevelEnchantedBooksInCreativeInventory() {
		return showAllLevelEnchantedBooksInCreativeInventory;
	}
	
	public boolean enchantmentCommandAbidesByMaxLevel() {
		return enchantmentCommandAbidesByMaxLevel;
	}
	
	public boolean allowRiptideAlways() {
		return allowRiptideAlways;
	}
	
	public Map<String, Integer> getMaxLevels() {
		return maxLevels;
	}
	
	public Map<Set<String>, Boolean> getExclusivity() {
		return exclusivity;
	}
	
	private static Map<String, Integer> addAllEnchantments(Map<String, Integer> maxLevels) {
		for (Enchantment e : Registries.ENCHANTMENT) {
			if (!maxLevels.containsKey(EnchantmentHelper.getEnchantmentId(e).toString())) {
				maxLevels.put(EnchantmentHelper.getEnchantmentId(e).toString(), e.getMaxLevel());
			}
		}
		return maxLevels;
	}
	
	
	
	private static Map<Set<String>, Boolean> getExclusivity(Map<Set<String>, Boolean> exclusivity) {
		Set<String> mapping;
		Enchantment e1, e2;
		for (int i = 0; i < Registries.ENCHANTMENT.size(); i++) {
			for (int j = i+1; j < Registries.ENCHANTMENT.size(); j++) {
				e1 = Registries.ENCHANTMENT.get(i);
				e2 = Registries.ENCHANTMENT.get(j);
				if (EnchantmentLevelHelper.canBeOnSameItem(e1, e2)) {
					mapping = new HashSet<String>();
					mapping.add(EnchantmentHelper.getEnchantmentId(e1).toString());
					mapping.add(EnchantmentHelper.getEnchantmentId(e2).toString());
					if (!exclusivity.containsKey(mapping)) {
						//Need to check the combination both ways, since sometimes this function will return different values depending on the order (Thank you Mojang, very cool)
						exclusivity.put(mapping, e1.canAccept(e2) && e2.canAccept(e1));
					}
				}
			}
		}
		return exclusivity;
	}

	@Override
	public String getReadmeName() {
		return "config_readme";
	}
	

}
