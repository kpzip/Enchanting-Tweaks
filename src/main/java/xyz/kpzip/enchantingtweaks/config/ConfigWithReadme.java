package xyz.kpzip.enchantingtweaks.config;

public interface ConfigWithReadme {
	
	public String getReadmeName();
	
	public default String getReadmeExtension() {
		return "md";
	}

}
