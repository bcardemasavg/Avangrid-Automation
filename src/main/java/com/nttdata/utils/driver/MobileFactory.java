package com.nttdata.utils.driver;

import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import com.nttdata.utils.Configuration;
import com.nttdata.utils.Constants;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class MobileFactory {
	protected static Logger log = LogManager.getLogger(MobileFactory.class);
	public static String remote = System.getenv().getOrDefault("remote", System.getProperty("remote", null));
	public static Capabilities capabilitiesToMerge;

	public static WebDriver instanceMobile(Configuration configuration) throws Exception {
		log.info("REMOTE CONFIGURADO: [" + remote + "]");
		log.info("MOBILE CONFIG: [" + configuration.toString() + "]");
		URL URL = new URL(remote);

		String platfromName = (String) configuration.getCapability().getCapability("platformName");

		if (Constants.ANDROID.toLowerCase().contentEquals(platfromName.toLowerCase())) {
			return new AndroidDriver(URL, configuration.getCapability());
		} else if (Constants.IOS.toLowerCase().contentEquals(platfromName.toLowerCase())) {
			return new IOSDriver(URL, configuration.getCapability());
		}
		throw new Exception("Platform no permitida en frame " + platfromName);

	}

}
