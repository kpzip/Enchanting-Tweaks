package xyz.kpzip.enchantingtweaks.controls;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

@Environment(value = EnvType.CLIENT)
public final class Keybindings {

	private Keybindings() {}
	
	private static final String CATEGORY = "category.enchantingtweaks.controls";
	
	private static GuiKeyBinding DESCRIPTIONS;
	private static GuiKeyBinding APPLICABILITY;
	private static GuiKeyBinding EXCLUSIVITY;
	
	public static void init() {
		DESCRIPTIONS = new GuiKeyBinding("key.enchantingtweaks.descriptions", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, CATEGORY);
		APPLICABILITY = new GuiKeyBinding("key.enchantingtweaks.applicability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);
		EXCLUSIVITY = new GuiKeyBinding("key.enchantingtweaks.exclusivity", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
		
		KeyBindingHelper.registerKeyBinding(DESCRIPTIONS);
		KeyBindingHelper.registerKeyBinding(APPLICABILITY);
		KeyBindingHelper.registerKeyBinding(EXCLUSIVITY);
	}
	
	public static boolean isDescriptionsPressed() {
		return DESCRIPTIONS.isPressed();
	}
	
	public static boolean isApplicabilityPressed() {
		return APPLICABILITY.isPressed();
	}
	
	public static boolean isExclusivityPressed() {
		return EXCLUSIVITY.isPressed();
	}
	
	public static Text getDesciptionsKeyName() {
		return DESCRIPTIONS.getBoundKeyLocalizedText();
	}
	
	public static Text getApplicabilityKeyName() {
		return APPLICABILITY.getBoundKeyLocalizedText();
	}
	
	public static Text getExclusivityKeyName() {
		return EXCLUSIVITY.getBoundKeyLocalizedText();
	}

}
