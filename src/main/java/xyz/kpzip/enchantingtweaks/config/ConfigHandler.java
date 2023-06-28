package xyz.kpzip.enchantingtweaks.config;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public abstract class ConfigHandler {
	
	public static List<SyncedConfig> configs = new ArrayList<SyncedConfig>();
	
	public static void registerConfigClient(SyncedConfig cfg) {
		ClientPlayNetworking.registerGlobalReceiver(cfg.getSyncPacketIdentifier(), (client, handler, buf, responseSender) -> {
			buf.retain();
			client.execute(() -> {
				cfg.loadFromPacket(buf);
			});
		});
	}
	
	public static void registerConfig(SyncedConfig cfg) {
		configs.add(cfg);
	}
}
