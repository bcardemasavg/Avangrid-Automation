package com.nttdata.utils.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class DriverFactory {
	protected static Logger log = LogManager.getLogger(DriverFactory.class);
	private static ThreadLocal<WebDriver> drivers = new ThreadLocal<WebDriver>();
	private static List<WebDriver> storedDrivers = new ArrayList<WebDriver>();

	private DriverFactory() {
		
	}

	public static WebDriver getDriver() {
		return (WebDriver) drivers.get();
	}

	public static void addDriver(WebDriver driver) {		
		storedDrivers.add(driver);
		drivers.set(driver);
	}

	public static File getScreenshot(WebDriver driver, String scenarioName, String ms) {
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(source, new File("./target/ScreenShots/" + scenarioName + "_" + ms + ".png"));
			return source;
		} catch (Exception e) {
			log.error("Exception while taking ScreenShot " + e.getMessage(), e);
		}
		return null;

	}

	public static void removeDriver() {
		storedDrivers.remove(drivers.get());
		drivers.remove();
	}

}
