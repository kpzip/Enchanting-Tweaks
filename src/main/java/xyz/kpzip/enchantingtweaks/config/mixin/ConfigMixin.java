package xyz.kpzip.enchantingtweaks.config.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.kpzip.enchantingtweaks.config.ConfigHandler;
import xyz.kpzip.enchantingtweaks.config.SyncedConfig;

public final class ConfigMixin {
	
	private ConfigMixin() {}
	
	@Environment(value=EnvType.CLIENT)
	@Mixin(MinecraftClient.class)
	private static abstract class MinecraftClientMixin {

		@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
		public void onDisconnect(Screen screen, CallbackInfo ci) {
			for (SyncedConfig c : ConfigHandler.configs) {
				c.reloadFromFile();
			}
		}
	}
	
	@Mixin(PlayerManager.class)
	private static abstract class PlayerManagerMixin {

		@Shadow @Final private MinecraftServer server;
		
		@Inject(method = "onPlayerConnect", at = @At("RETURN"))
		public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData data, CallbackInfo ci) {
			PacketByteBuf buf;
			for (SyncedConfig cfg : ConfigHandler.configs) {
				buf = PacketByteBufs.create();
				cfg.writeToPacket(buf);
				ServerPlayNetworking.send(player, cfg.getSyncPacketIdentifier(), buf);
			}
		}
		
	}


}
