package com.nttdata.StepDefinition;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.nttdata.page.CommonsPage;
import com.nttdata.page.HomePage;
import com.nttdata.page.LoginPage;
import com.nttdata.utils.Constants;
import com.nttdata.utils.ScenarioContext;
import com.nttdata.utils.driver.DriverFactory;

public class TestBase {
	protected Logger log = LogManager.getLogger(this.getClass());
	protected WebDriver driver = DriverFactory.getDriver();
	protected ScenarioContext scenarioContext;
	protected JSONObject environment;
	SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss");
	String labExecution = System.getProperty("labExecution", Constants.EXECUTION_LOCAL);
	String platform = System.getProperty("platform", null);

	public HomePage homePage;
	public LoginPage loginPage;
	public CommonsPage commonsPage;
	// public LogOutPage logOutPage;
	private static String currentDataFile;

	public TestBase(ScenarioContext scenarioContext) {
		this.scenarioContext = scenarioContext;
		environment = (JSONObject) scenarioContext.getScenarioContext("ENVIRONMENT");

		homePage = new HomePage(driver);
		loginPage = new LoginPage(driver);
		commonsPage = new CommonsPage(driver);
		// logOutPage = new LogOutPage(driver);

	}

	public void loadData(String dataFile, String id) {
		currentDataFile = dataFile;
		JSONArray datos = (JSONArray) scenarioContext.getScenarioContext(dataFile);
		JSONObject dato = filtrarJSONArrayByCampo(datos, "id", id);
		assertNotNull(dataFile + " no encontrada '" + id + "'", dato);
		scenarioContext.setScenarioContext("datos_" + dataFile, dato);
	}

	public Object getData(String nameDato) {
		return getDato(currentDataFile, nameDato);
	}

	public Object getDato(String dataFile, String nameDato) {
		JSONObject dato = (JSONObject) scenarioContext.getScenarioContext("datos_" + dataFile);
		return dato.opt(nameDato);
	}

	public void takeScreenshotToReport(String name) throws Exception {
		scenarioContext.attach(takeScreenShoot(name), "image/png", name);
	}

	private File takeScreenShoot(String nombreSS) throws IOException {
		return DriverFactory.getScreenshot(DriverFactory.getDriver(), nombreSS.replace("\"", ""),
				sdf.format(new Date()));
	}

	private JSONObject filtrarJSONArrayByCampo(JSONArray array, String campo, String comparacion) {
		for (int i = 0; i < array.length(); i++) {
			JSONObject objeto = array.getJSONObject(i);
			if (comparacion.equals(objeto.optString(campo, null))) {
				return objeto;
			}
		}
		return null;
	}
}
