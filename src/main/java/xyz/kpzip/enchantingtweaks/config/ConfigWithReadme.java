package xyz.kpzip.enchantingtweaks.config;

import java.nio.file.Path;

import net.minecraft.client.resource.ResourceIndex;

public interface ConfigWithReadme {
	
	public ResourceIndex getInternalReadmeLocation();
	
	public Path getExternalReadmeLocation();

}
