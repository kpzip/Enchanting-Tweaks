package xyz.kpzip.enchantingtweaks.omegaconfig.src.main.java.draylar.omegaconfiggui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terraformersmc.modmenu.ModMenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.kpzip.enchantingtweaks.omegaconfig.src.main.java.draylar.omegaconfiggui.OmegaConfigGui;
import xyz.kpzip.enchantingtweaks.omegaconfig.src.main.java.draylar.omegaconfiggui.api.screen.OmegaModMenu;

@Environment(EnvType.CLIENT)
@Mixin(ModMenu.class)
public class ModMenuMixin {

    @Inject(method = "onInitializeClient", at = @At("RETURN"), remap = false)
    private void addOmegaConfigurationScreens(CallbackInfo ci) {
        OmegaConfigGui.modMenuInitialized = true;

        // Add loaded configuration screens to mod menu.
        OmegaConfigGui.getConfigScreenFactories().forEach(OmegaModMenu::injectScreen);
    }
}
