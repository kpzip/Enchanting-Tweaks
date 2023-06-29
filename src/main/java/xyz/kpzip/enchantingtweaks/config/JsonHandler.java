package xyz.kpzip.enchantingtweaks.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;
import xyz.kpzip.enchantingtweaks.EnchantingTweaks;

public abstract class JsonHandler {
	
	private static Path getConfigPath(String name, String ext, String modid) {
		return Paths.get(getConfigDirectory(modid).toString(), name + "." + ext);
	}
	
	private static Path getConfigDirectory(String modid) {
		return Paths.get(FabricLoader.getInstance().getConfigDir().toString(), modid);
	}
	
	public static <T extends SyncedConfig> T readConfig(Class<T> type, Supplier<T> newconfig, String fileName, String fileType, String modid) {
		EnchantingTweaks.LOGGER.info("Readig Config...");
		List<String> file;
		Gson gson = new Gson();
		Path configPath = getConfigPath(fileName, fileType, modid);
		T config = newconfig.get();
		try {
			
			//If the config doesn't exist, write defaults
			if (!Files.exists(configPath)) {
				EnchantingTweaks.LOGGER.info("Config File does not exist, writing defaults...");
				writeConfig(gson, configPath, config);
				return config;
			}
			
			//Read the config file
			file = Files.readAllLines(configPath);
			String json = String.join("", file);
			config = gson.fromJson(json, type);
			
			//Update and write the config back in case it is out of date
			config.updateConfig();
			writeConfig(gson, configPath, config);
			
			return config;
			
			
		} catch (IOException e) {
			EnchantingTweaks.LOGGER.error("Error opening configuration file");
			EnchantingTweaks.LOGGER.error(e.getMessage());
			e.printStackTrace();
			return config;
		} catch (JsonSyntaxException e) {
			EnchantingTweaks.LOGGER.error("Error reading configuration file");
			EnchantingTweaks.LOGGER.error(e.getMessage());
			return config;
		}
	}
	
	public static <T extends SyncedConfig> void writeConfig(Gson g, Path p, T c) throws IOException {
		Files.writeString(p, g.toJson(c));
	}

}
