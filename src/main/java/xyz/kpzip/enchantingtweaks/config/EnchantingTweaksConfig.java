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
	private boolean allowCrossbowEnchantmentsTogether = false;
	
	private byte protectionMaxLevel = 4;
	private byte fire_protectionMaxLevel = 4;
	private byte feather_fallingMaxLevel = 4;
	private byte blast_protectionMaxLevel = 4;
	private byte projectile_protectionMaxLevel = 4;
	private byte respirationMaxLevel = 3;
	private byte thornsMaxLevel = 3;
	private byte depth_striderMaxLevel = 3;
	private byte frost_walkerMaxLevel = 2;
	private byte soul_speedMaxLevel = 3;
	private byte swift_sneakMaxLevel = 3;
	private byte sharpnessMaxLevel = 5;
	private byte smiteMaxLevel = 5;
	private byte bane_of_arthropodsMaxLevel = 5;
	private byte knockbackMaxLevel = 2;
	private byte fire_aspectMaxLevel = 2;
	private byte lootingMaxLevel = 3;
	private byte sweepingMaxLevel = 3;
	private byte efficiencyMaxLevel = 5;
	private byte unbreakingMaxLevel = 3;
	private byte fortuneMaxLevel = 3;
	private byte powerMaxLevel = 5;
	private byte punchMaxLevel = 2;
	private byte flameMaxLevel = 1;
	private byte luch_of_the_seaMaxLevel = 3;
	private byte lureMaxLevel = 3;
	private byte loyaltyMaxLevel = 3;
	private byte impalingMaxLevel = 5;
	private byte riptideMaxLevel = 3;
	private byte multishotMaxLevel = 1;
	private byte quick_chargeMaxLevel = 3;
	private byte piercingMaxLevel = 4;
	
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
		if (b % 16 > 7) allowCrossbowEnchantmentsTogether = true; else allowCrossbowEnchantmentsTogether = false; //is bit 4 set?
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
	
	public boolean allowCrossbowEnchantmentsTogether() {
		return allowCrossbowEnchantmentsTogether;
	}


}
