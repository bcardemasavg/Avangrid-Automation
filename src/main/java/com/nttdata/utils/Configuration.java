package com.nttdata.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.Gson;

public class Configuration {

	private String id;
	private DesiredCapabilities capability;

	public static Configuration configureDeviceCapabilities(String configId, File fileConfigCaps) {
		Configuration deviceConfiguration = null;
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(fileConfigCaps));
			List<Configuration> listConfig = Arrays.asList(new Gson().fromJson(bufferReader, Configuration[].class));
			deviceConfiguration = listConfig.stream().filter(x -> x.getId().equals(configId)).findFirst().orElse(null);

		} catch (Exception e) {
			throw new RuntimeException("Error en la lectura de la configuracion desde el archivo: "
					+ fileConfigCaps.getAbsolutePath() + ", " + e.getMessage());
		}
		return deviceConfiguration;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DesiredCapabilities getCapability() {
		return capability;
	}

	public void setCapability(DesiredCapabilities capability) {
		this.capability = capability;
	}
}
