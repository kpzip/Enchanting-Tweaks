package xyz.kpzip.enchantingtweaks.config;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import xyz.kpzip.enchantingtweaks.networking.NetworkingConstants;

public class EnchantingTweaksConfig implements Config {
	
	public static final String FILE_NAME = "enchanting-tweaks-config";
	public static final String FILE_EXTENSION = "json";
	
	private boolean bypassAnvilMaxLevel = true;
	private boolean allowBowEnchantmentsTogether = true;
	private boolean allowDamageEnchantmentsTogether = false;
	private boolean allowProtectionEnchantmentsTogether = false;
	
	//encode boolean values into a single byte for syncing
	public byte toByte() {
		byte b = 0;
		if (bypassAnvilMaxLevel) b |= 128;
		if (allowBowEnchantmentsTogether) b |= 64;
		if (allowDamageEnchantmentsTogether) b |= 32;
		if (allowProtectionEnchantmentsTogether) b |= 16;
		return b;
	}
	
	public void fromByte(byte b) {
		if (b % 256 > 127) bypassAnvilMaxLevel = true; else bypassAnvilMaxLevel = false; //is bit 1 set?
		if (b % 128 > 63) allowBowEnchantmentsTogether = true; else allowBowEnchantmentsTogether = false; //is bit 2 set?
		if (b % 64 > 31) allowDamageEnchantmentsTogether = true; else allowDamageEnchantmentsTogether = false; //is bit 3 set?
		if (b % 32 > 15) allowProtectionEnchantmentsTogether = true; else allowProtectionEnchantmentsTogether = false; //is bit 4 set?
	}
	
	public String getFileName() {
		return FILE_NAME;
	}
	
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	@Override
	public Identifier getIdentifier() {
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


}
