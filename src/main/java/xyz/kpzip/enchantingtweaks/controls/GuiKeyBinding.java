package xyz.kpzip.enchantingtweaks.controls;

import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;

public class GuiKeyBinding extends KeyBinding {

	public GuiKeyBinding(String translationKey, Type type, int code, String category) {
		super(translationKey, type, code, category);
	}
	
	public GuiKeyBinding(String translationKey, int code, String category) {
		this(translationKey, InputUtil.Type.KEYSYM, code, category);
	}
	
	//Terrible, Awful Hack
	@Override
	public boolean isPressed() {
		Keybindings.debug();
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), ((KeyBindingAccessor) this).fabric_getBoundKey().getCode());
	}
	
	@Override
	public boolean equals(KeyBinding other) {
		return ((KeyBindingAccessor) this).fabric_getBoundKey().equals(((KeyBindingAccessor) other).fabric_getBoundKey()) && other instanceof GuiKeyBinding;
	}

}
