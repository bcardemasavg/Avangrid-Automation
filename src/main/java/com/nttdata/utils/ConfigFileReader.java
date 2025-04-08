package com.nttdata.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {
	private final Properties properties;
	private final String propertyFilePath = System.getProperty("jsonreader.configurationpath",
			"configuration.properties");

	public ConfigFileReader() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.propertyFilePath));
			this.properties = new Properties();

			try {
				this.properties.load(reader);
				reader.close();
			} catch (IOException var3) {
				var3.printStackTrace();
			}

		} catch (FileNotFoundException var4) {
			var4.printStackTrace();
			throw new RuntimeException("Configuration.properties not found at " + this.propertyFilePath);
		}
	}

	public String getTestDataResourcesPath() {
		String a = this.properties.getProperty("testDataResourcePath");
		if (a != null) {
			return a;
		} else {
			throw new RuntimeException(
					"testDataResourcePath not defined on configuration.properties\n File: " + this.propertyFilePath);
		}
	}

	public String getEnvironmentMobileFileName() {
		String a = this.properties.getProperty("environmentMobileFileName");
		return a != null ? a : "environmentMobileFileName.json";
	}

	public String getEnvironmentFileName() {
		String a = this.properties.getProperty("environmentFileName");
		return a != null ? a : "environments.json";
	}

	public String getDatabaseConnectionFileName() {
		String a = this.properties.getProperty("databaseConnectionFileName");
		return a != null ? a : "db.json";
	}

	public String getConfigurationFileName() {
		String a = this.properties.getProperty("configurationFileName");
		return a != null ? a : "configurationFileName.json";
	}
}
