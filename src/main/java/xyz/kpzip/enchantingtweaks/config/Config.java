package xyz.kpzip.enchantingtweaks.config;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface Config {
	
	public Identifier getIdentifier();
	
	public void loadFromPacket(PacketByteBuf buf);
	
	public void writeToPacket(PacketByteBuf buf);

}
