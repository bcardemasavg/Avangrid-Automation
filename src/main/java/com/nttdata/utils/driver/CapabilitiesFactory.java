package com.nttdata.utils.driver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.safari.SafariOptions;

import com.google.gson.Gson;

public class CapabilitiesFactory {
	public static boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
	public static String downloadPath = System.getProperty("user.dir") + File.separator + "target" + File.separator
			+ "downloads";

	public static String windowSize = System.getProperty("window-size", "1440,808");
	public static int WEB_WIDTH = Integer.parseInt(windowSize.split(",")[0]);
	public static int WEB_HEIGHT = Integer.parseInt(windowSize.split(",")[1]);

	public static ChromeOptions generateDefaultChrome() {
		ChromeOptions options = new ChromeOptions();
		HashMap<String, Object> chromePrefs = prepareChromePrefs();
		options.setExperimentalOption("prefs", chromePrefs);

		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("--disable-notifications");
		options.addArguments("disable-infobars");
		options.addArguments("--allow-running-insecure-content");
		options.addArguments("--disable-extensions");
		options.addArguments("--ignore-certificate-errors");
		// options.addArguments("--disable-web-security");
		options.addArguments("--disable-gpu");
		options.addArguments("--remote-allow-origins=*");

		if (headless) {
			options.addArguments("--headless=new");
		}
		if (System.getProperty("sandbox", "false").equals("true")) {
			options.addArguments("--no-sandbox");
		}
		if (System.getProperty("webMobile", "false").equals("true")) {
			Map<String, Object> mobileEmulation = new HashMap<>();
			Map<String, Object> deviceMetrics = new HashMap<>();

			deviceMetrics.put("width", WEB_WIDTH);
			deviceMetrics.put("height", (int) (WEB_HEIGHT));

			Map<String, Object> clientHints = new HashMap<>();
			clientHints.put("platform", "Android");
			clientHints.put("mobile", true);

			// mobileEmulation.put("deviceName", "Nexus 5");
			mobileEmulation.put("userAgent",
					"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
			mobileEmulation.put("deviceMetrics", deviceMetrics);
			mobileEmulation.put("clientHints", clientHints);
			options.setExperimentalOption("mobileEmulation", mobileEmulation);
			options.addArguments(
					String.format("--window-size=%s", WEB_WIDTH + "," + (int) (WEB_HEIGHT + WEB_HEIGHT * 0.2)));
		} else {
			options.addArguments(String.format("--window-size=%s", windowSize));
		}
		return options;
	}

	public static HashMap<String, Object> prepareChromePrefs() {
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("cmd", "Page.setDownloadBehavior");
		chromePrefs.put("behavior", "allow");
		chromePrefs.put("download.prompt_for_download", "false");
		chromePrefs.put("download.directory_upgrade", "true");
		chromePrefs.put("safebrowsing.enabled", "false");
		chromePrefs.put("safebrowsing.disable_download_protection", "true");
		chromePrefs.put("download.default_directory", getDownloadsPath());
		return chromePrefs;
	}

	public static void setdownloadPathOnChrome(String downloadPath, ChromeDriverService driverService,
			ChromeDriver driver) {
		try {
			Map<String, Object> commandParams = new HashMap<>();
			commandParams.put("cmd", "Page.setDownloadBehavior");
			Map<String, String> params = new HashMap<>();
			params.put("behavior", "allow");
			params.put("downloadPath", downloadPath);
			commandParams.put("params", params);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			Gson gson = new Gson();
			String command = gson.toJson(commandParams);
			HttpPost request = new HttpPost(
					prepareUrlToChrome(driverService.getUrl().toString(), driver.getSessionId().toString()));
			request.addHeader("content-type", "application/json");
			request.setEntity(new StringEntity(command));
			httpClient.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String prepareUrlToChrome(String url, String sessionId) {
		String u = url + "/session/" + sessionId + "/chromium/send_command";
		System.setProperty("webdriverfactory.chrome.chromeServiceUrl" + Thread.currentThread(), u);
		return u;
	}

	protected static FirefoxOptions generateDefaultFirefox() {
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments("--disable-extensions", "-private", "safebrowsing.enabled=false");
		firefoxOptions.setAcceptInsecureCerts(true);
		String lang = System.getProperty("lang");
		if (lang == null || lang.isEmpty()) {
			lang = "es";
		}
		firefoxOptions.addArguments("--lang=" + lang);
		if (headless) {
			firefoxOptions.addArguments("-headless");
		}
		return firefoxOptions;
	}

	protected static InternetExplorerOptions generateDefaultIExplorer() {
		InternetExplorerOptions ieOptions = new InternetExplorerOptions();
		ieOptions.disableNativeEvents();
		ieOptions.destructivelyEnsureCleanSession();
		ieOptions.addCommandSwitches("-private");
		return ieOptions;
	}

	protected static SafariOptions generateDefaultSafari() {
		SafariOptions safariOptions = new SafariOptions();
		safariOptions.setCapability("safari.cleanSession", true);
		return safariOptions;
	}

	protected static EdgeOptions generateDefaultEdge() {
		EdgeOptions edgeOptions = new EdgeOptions();
		if (headless) {
			edgeOptions.addArguments("--headless=new");
		}
		edgeOptions.setAcceptInsecureCerts(true);
		return edgeOptions;
	}

	public static Dimension setWebDimension() {
		WEB_WIDTH = Integer.parseInt(windowSize.split(",")[0]);
		WEB_HEIGHT = Integer.parseInt(windowSize.split(",")[1]);
		return new Dimension(WEB_WIDTH, WEB_HEIGHT);
	}

	public static String getDownloadsPath() {
		return downloadPath;
	}

	public static void setDownloadPath() {
		System.setProperty("webdriverfactory.chrome.downloadpath", getDownloadsPath());
	}
}
