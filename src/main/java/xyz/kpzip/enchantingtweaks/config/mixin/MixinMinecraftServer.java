package xyz.kpzip.enchantingtweaks.config.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.MinecraftServer;
import xyz.kpzip.enchantingtweaks.config.Cfgstorrer;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements Cfgstorrer {

}
