package com.nttdata.StepDefinition;

import com.nttdata.utils.ScenarioContext;

import io.cucumber.java.en.Given;

public class HomeDefinition extends TestBase {

	public HomeDefinition(ScenarioContext scenarioContext) {
		super(scenarioContext);
	}

	@Given("ingreso a la aplicación web y navego a la url")
	public void ingreso_a_la_aplicación_web_y_navego_a_la_url() {
		ingresarWebPorHost("host");
	}

	private void ingresarWebPorHost(String hostName) {
		homePage.maximizarVentana();
		homePage.navigateTo(environment.getString(hostName));
		homePage.waitUnitlPageLoadedWithBussyIcon();
	}

	@Given("cargo la data {string} con id {string}")
	public void cargoLaDataConId(String dataFile, String id) throws Throwable {
		super.loadData(dataFile, id);
	}

}
