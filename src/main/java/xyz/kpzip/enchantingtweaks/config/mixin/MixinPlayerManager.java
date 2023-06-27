package xyz.kpzip.enchantingtweaks.config.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.kpzip.enchantingtweaks.config.Cfgstorrer;
import xyz.kpzip.enchantingtweaks.networking.NetworkingConstants;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

	@Shadow @Final private MinecraftServer server;
	
	@Inject(method = "onPlayerConnect", at = @At("RETURN"))
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		PacketByteBuf buf = PacketByteBufs.create();
		((Cfgstorrer)player.getServer()).getEtcfg().writeToPacket(buf);
		ServerPlayNetworking.send(player, NetworkingConstants.ETCFG_PACKET_ID, buf);
	}
	
}