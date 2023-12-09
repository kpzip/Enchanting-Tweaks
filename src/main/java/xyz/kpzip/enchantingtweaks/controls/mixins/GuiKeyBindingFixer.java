package xyz.kpzip.enchantingtweaks.controls.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;

import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import xyz.kpzip.enchantingtweaks.controls.GuiKeyBinding;
import xyz.kpzip.enchantingtweaks.util.MixinPriority;

public final class GuiKeyBindingFixer {

	private GuiKeyBindingFixer() {}
	
	@Mixin(value = KeyBinding.class, priority = MixinPriority.HIGHEST)
	private static abstract class KeyBindingMixin {
		
		@Shadow private static final Map<String, KeyBinding> KEYS_BY_ID = Maps.newHashMap();
		
		@Shadow private static final Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS = Maps.newHashMap();
		
		/**
		 * @Author kpzip
		 * @Reason Stop non-conflicting bindings from being added to the map
		 * TODO Overwrite: Maintain this for every update in case the original changes
		 * */
		@Overwrite
		public static void updateKeysByCode() {
	        KEY_TO_BINDINGS.clear();
	        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
	            if (!(keyBinding instanceof GuiKeyBinding)) KEY_TO_BINDINGS.put(((KeyBindingAccessor) keyBinding).fabric_getBoundKey(), keyBinding);
	        }
	    }
		
		@SuppressWarnings("unlikely-arg-type")
		@Inject(method = "equals", at = @At("HEAD"), cancellable = true)
		private void checkEquals(KeyBinding o, CallbackInfoReturnable<Boolean> cir) {
			if (o instanceof GuiKeyBinding g) cir.setReturnValue(g.equals(this));
		}
		
		
	}

}
