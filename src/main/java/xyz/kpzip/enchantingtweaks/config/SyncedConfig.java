package xyz.kpzip.enchantingtweaks.config;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface SyncedConfig {
	
	public void loadFromPacket(PacketByteBuf buf);
	
	public void writeToPacket(PacketByteBuf buf);
	
	public Identifier getSyncPacketIdentifier();

}
