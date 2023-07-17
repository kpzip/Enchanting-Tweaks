package xyz.kpzip.enchantingtweaks.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
		//Why do I have to call a function to get it to work properly? shouldn't it work properly by default? see: https://github.com/google/gson/issues/210
		Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
		Path configPath = getConfigPath(fileName, fileType, modid);
		T config = newconfig.get();
		try {
			
			//If the config doesn't exist, write defaults
			if (!Files.exists(configPath)) {
				EnchantingTweaks.LOGGER.info("Config File does not exist, writing defaults...");
				Files.createDirectories(getConfigDirectory(modid));
				Files.createFile(configPath);
				writeConfig(gson, configPath, config);
				if (config instanceof ConfigWithReadme) {
					ConfigWithReadme readmeconf = (ConfigWithReadme) config;
					Path readmePath = getConfigPath(readmeconf.getReadmeName(), readmeconf.getReadmeExtension(), modid);
					handleReadme(readmeconf, readmePath, config, modid);
				}
				return config;
			}
			
			//Read the config file
			file = Files.readAllLines(configPath);
			String json = String.join("", file);
			config = gson.fromJson(json, type);
			
			//Update and write the config back in case it is out of date
			config.updateConfig();
			writeConfig(gson, configPath, config);
			
			if (config instanceof ConfigWithReadme) {
				ConfigWithReadme readmeconf = (ConfigWithReadme) config;
				Path readmePath = getConfigPath(readmeconf.getReadmeName(), readmeconf.getReadmeExtension(), modid);
				handleReadme(readmeconf, readmePath, config, modid);
			}
			
			return config;
			
			
		} catch (IOException e) {
			EnchantingTweaks.LOGGER.error("Error opening configuration file");
			EnchantingTweaks.LOGGER.error(e.getMessage());
			e.printStackTrace();
			return config;
		} catch (JsonSyntaxException e) {
			EnchantingTweaks.LOGGER.error("Error reading configuration file");
			EnchantingTweaks.LOGGER.error(e.getMessage());
			e.printStackTrace();
			return config;
		}
	}
	
	public static <T extends SyncedConfig> void writeConfig(Gson g, Path p, T c) throws IOException {
		Files.writeString(p, g.toJson(c));
	}
	
	private static <T extends SyncedConfig> void handleReadme(ConfigWithReadme readmeconf, Path readmePath, T config, String modid) throws IOException {
		if (!Files.exists(readmePath)) {
			InputStream in = config.getClass().getResourceAsStream("/data/" + modid + "/" + readmeconf.getReadmeName() + "." + readmeconf.getReadmeExtension());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String readmeline;
			List<String> readmelines = new ArrayList<String>();
			while ((readmeline = reader.readLine()) != null) {
				readmelines.add(readmeline);
			}
			reader.close();
			String readme = String.join("\n", readmelines);
			Files.writeString(readmePath, readme);
		}
	}

}
