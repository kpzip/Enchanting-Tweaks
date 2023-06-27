package xyz.kpzip.enchantingtweaks.networking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;
import xyz.kpzip.enchantingtweaks.config.Cfgstorrer;

@Environment(value=EnvType.CLIENT)
public class EnchantingTweaksClient implements ClientModInitializer {
	
	
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ETCFG_PACKET_ID, (client, handler, buf, responseSender) -> {
			buf.retain();
			client.execute(() -> {
				EnchantingTweaks.LOGGER.info("loading config from packet...");
				((Cfgstorrer)client).getEtcfg().loadFromPacket(buf);
				EnchantingTweaks.LOGGER.info("config synced to client.");
			});
		});
	}


}