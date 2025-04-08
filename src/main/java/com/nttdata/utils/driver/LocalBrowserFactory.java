package com.nttdata.utils.driver;

import java.io.File;
import java.net.URL;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LocalBrowserFactory {
	protected static Logger log = LogManager.getLogger(LocalBrowserFactory.class);
	static String remote = System.getenv().getOrDefault("remote", System.getProperty("remote", null));
	public static Capabilities capabilitiesToMerge;
	public static String proxyParam = System.getenv().getOrDefault("PROXY_SERVER", null);

	public static WebDriver buildBrowser(String browser) throws Exception {
		WebDriver result;
		switch (browser.toLowerCase()) {
		case "chrome":
			result = buildChromeBrowser();
			break;
		case "firefox":
			result = instanceFirefoxDriver();
			break;
		case "iexplorer":
			result = instanceIExplorerDriver();
			break;
		case "safari":
			result = instanceSafariDriver();
			break;
		case "edge":
			result = instanceEdgeDriver();
			break;
		default:
			throw new Exception("Driver no Implementado '" + browser + "'");
		}
		return result;
	}

	public static WebDriver buildChromeBrowser() throws Exception {
		ChromeOptions options = CapabilitiesFactory.generateDefaultChrome();
		if (!Objects.isNull(capabilitiesToMerge)) {
			options = options.merge(capabilitiesToMerge);
		}
		if (!Objects.isNull(remote)) {
			log.info("REMOTE=" + remote);
			return new RemoteWebDriver(new URL(remote), options);
		}

		File driverFile = new File(System.getenv().getOrDefault("chromedriver", "/usr/bin/chromedriver"));
		if (!driverFile.exists()) {
			log.info("Download driver from WebDriverManager");
			String browserVersion = System.getenv().getOrDefault("browserVersion", "latest");
			if (proxyParam != null) {
				WebDriverManager.chromedriver().browserVersion(browserVersion).proxy(proxyParam).setup();
			} else {
				WebDriverManager.chromedriver().browserVersion(browserVersion).setup();
			}
		} else {
			log.info("LOCAL_DRIVER: " + driverFile.getAbsolutePath());
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverFile.getAbsolutePath());
		}

		if (proxyParam != null) {
			options.addArguments("--proxy-server=" + proxyParam);
		}

		CapabilitiesFactory.setDownloadPath();
		ChromeDriverService driverService = getChromeDriverService();
		ChromeDriver driver = new ChromeDriver(driverService, options);
		CapabilitiesFactory.setdownloadPathOnChrome(CapabilitiesFactory.getDownloadsPath(), driverService, driver);
		return driver;
	}

	public static WebDriver instanceFirefoxDriver() throws Exception {
		FirefoxOptions options = CapabilitiesFactory.generateDefaultFirefox();
		if (!Objects.isNull(capabilitiesToMerge)) {
			options.merge(capabilitiesToMerge);
		}
		if (!Objects.isNull(remote)) {
			log.info("REMOTE=" + remote);
			return new RemoteWebDriver(new URL(remote), options);
		}

		File driverFile = new File(System.getenv().getOrDefault("firefoxdriver", "/usr/bin/geckodriver"));
		if (!driverFile.exists()) {
			log.info("Download driver from WebDriverManager");
			if (proxyParam != null) {
				WebDriverManager.firefoxdriver().proxy(proxyParam).setup();
			} else {
				WebDriverManager.firefoxdriver().setup();
			}

		} else {
			log.info("LOCAL_DRIVER: " + driverFile.getAbsolutePath());
			System.setProperty("webdriver.gecko.driver", driverFile.getAbsolutePath());
		}

		if (proxyParam != null) {
			Proxy proxy = new Proxy();
			proxy.setHttpProxy(proxyParam);
			proxy.setSslProxy(proxyParam);
			proxy.setSslProxy(proxyParam);
			options.setProxy(proxy);
		}

		return new FirefoxDriver(options);
	}

	public static WebDriver instanceIExplorerDriver() throws Exception {
		InternetExplorerOptions options = CapabilitiesFactory.generateDefaultIExplorer();
		if (!Objects.isNull(capabilitiesToMerge)) {
			options.merge(capabilitiesToMerge);
		}

		if (!Objects.isNull(remote)) {
			log.info("REMOTE=" + remote);
			return new RemoteWebDriver(new URL(remote), options);
		}

		File driverFile = new File(System.getenv().getOrDefault("iexplorerdriver", "/usr/bin/iexplorerdriver"));
		if (!driverFile.exists()) {
			log.info("Download driver from WebDriverManager");
			WebDriverManager.iedriver().arch32().setup();
		} else {
			log.info("LOCAL_DRIVER: " + driverFile.getAbsolutePath());
			System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, driverFile.getAbsolutePath());
		}
		return new InternetExplorerDriver(options);
	}

	public static WebDriver instanceSafariDriver() throws Exception {
		SafariOptions options = CapabilitiesFactory.generateDefaultSafari();
		if (!Objects.isNull(capabilitiesToMerge)) {
			options.merge(capabilitiesToMerge);
		}
		if (!Objects.isNull(remote)) {
			log.info("REMOTE=" + remote);
			return new RemoteWebDriver(new URL(remote), options);
		}

		File driverFile = new File(System.getenv().getOrDefault("safaridriver", "/usr/bin/safaridriver"));
		if (!driverFile.exists()) {
			log.info("Download driver from WebDriverManager");
			String browserVersion = System.getenv().getOrDefault("browserVersion", "latest");
			WebDriverManager.safaridriver().browserVersion(browserVersion).setup();
		} else {
			log.info("LOCAL_DRIVER: " + driverFile.getAbsolutePath());
			System.setProperty(SafariDriverService.SAFARI_DRIVER_EXE_PROPERTY, driverFile.getAbsolutePath());
		}
		return new SafariDriver(options);
	}

	public static WebDriver instanceEdgeDriver() throws Exception {
		EdgeOptions options = CapabilitiesFactory.generateDefaultEdge();
		if (!Objects.isNull(capabilitiesToMerge)) {
			options.merge(capabilitiesToMerge);
		}
		if (!Objects.isNull(remote)) {
			log.info("REMOTE=" + remote);
			return new RemoteWebDriver(new URL(remote), options);
		}

		File driverFile = new File(System.getenv().getOrDefault("edgedriver", "/usr/bin/edgedriver"));
		if (!driverFile.exists()) {
			log.info("Download driver from WebDriverManager");
			String browserVersion = System.getenv().getOrDefault("browserVersion", "latest");
			WebDriverManager.edgedriver().browserVersion(browserVersion).setup();
		} else {
			log.info("LOCAL_DRIVER: " + driverFile.getAbsolutePath());
			System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY, driverFile.getAbsolutePath());
		}

		if (proxyParam != null) {
			options.addArguments("--proxy-server=" + proxyParam);
		}

		return new EdgeDriver(options);
	}

	public static ChromeDriverService getChromeDriverService() {
		return ChromeDriverService.createDefaultService();
	}

	public static String getDownloadsPath() {
		return CapabilitiesFactory.getDownloadsPath();
	}

}
