package com.nttdata.utils.driver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.nttdata.utils.Configuration;

public class BrowserStackFactory {

	public static HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();

	public static final String AUTOMATE_USERNAME = System.getenv("BROWSERSTACK_USERNAME") != null
			? System.getenv("BROWSERSTACK_USERNAME")
			: "BROWSERSTACK_USERNAME";
	public static final String AUTOMATE_ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY") != null
			? System.getenv("BROWSERSTACK_ACCESS_KEY")
			: "BROWSERSTACK_ACCESS_KEY";
	public static final String URL_BROWSERSTACK = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY
			+ "@hub-cloud.browserstack.com/wd/hub";

	public static WebDriver buildBrowserStackWeb(Configuration configuration) throws Throwable {

		if ("BROWSERSTACK_USERNAME".equals(AUTOMATE_USERNAME)
				|| "BROWSERSTACK_ACCESS_KEY".equals(AUTOMATE_ACCESS_KEY)) {
			throw new Exception(
					"Para usar BROWSERSTACK se deben definir las variables de entorno: BROWSERSTACK_USERNAME y BROWSERSTACK_ACCESS_KEY");
		}

		if (configuration.getCapability().getCapability("bstack:options") != null) {
			browserstackOptions.putAll((Map<? extends String, ? extends Object>) configuration.getCapability().getCapability("bstack:options"));
		}

		browserstackOptions.put("projectName", System.getProperty("project.name", "Project Name no definido").trim());
		browserstackOptions.put("buildName", System.getProperty("project.build", "Build Name no definido").trim());
		browserstackOptions.put("sessionName",
				System.getProperty("project.session", "Session Name no definido").trim());

		configuration.getCapability().setCapability("bstack:options", browserstackOptions);
		MobileFactory.remote = URL_BROWSERSTACK;
		return new RemoteWebDriver(new URL(URL_BROWSERSTACK), configuration.getCapability());
	}

}
