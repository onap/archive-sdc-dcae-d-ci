package org.onap.dcae.ci.utilities;

import org.onap.dcae.ci.config.Configuration;

import java.io.File;

public class ConfigurationReader {
	
	private static Configuration config;

	public static Configuration getConfiguration() {
		if (config == null){
			File file = getConfigFile();
			config = Configuration.loadConfigFile(file, Configuration.class);
		}
		return config;
	}
	
	public static File getConfigFile() {
		String configFile = System.getProperty("config.resource");
		if (configFile == null){
			configFile = configurationFile();
		}
		File file = new File(configFile);
		if (false == file.exists()) {
			throw new RuntimeException("The config file " + configFile + " cannot be found.");
		}
		return file;
	}

	private static String configurationFile() {
		return confPath() + File.separator + "conf.yaml";
	}

	public static String confPath() {
		return System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources"
				+ File.separator + "conf" + File.separator ;
	}
}
