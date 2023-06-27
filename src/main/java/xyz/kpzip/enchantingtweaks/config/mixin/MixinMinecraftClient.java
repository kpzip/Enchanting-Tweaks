package xyz.kpzip.enchantingtweaks.config.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import xyz.kpzip.enchantingtweaks.config.Cfgstorrer;

@Environment(value=EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements Cfgstorrer {

}
