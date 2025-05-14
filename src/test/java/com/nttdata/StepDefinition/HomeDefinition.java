package com.nttdata.StepDefinition;

import com.nttdata.utils.ScenarioContext;

import com.nttdata.utils.database.BasicReader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.nttdata.utils.database.ExcelReader;
import org.json.JSONObject;

public class HomeDefinition extends TestBase {

	public HomeDefinition(ScenarioContext scenarioContext) {
		super(scenarioContext);
	}

	@Given("Enter the web application and navigate to the URL")
	public void ingreso_a_la_aplicación_web_y_navego_a_la_url() {
		ingresarWebPorHost("host");

		System.out.println("Dato generado: " + super.getData("Account Number"));
	}

	@Given("a user with incorrect username and password")
	public void user_logs_in_with_incorrect_credentials() {
		ingresarWebPorHost("host");
		loginPage.clickButtonSingsInRegister();
		loginPage.sendKeysInputUser("user");
		loginPage.sendKeysInputPassword("password");

		System.out.println("Dato generado: " + super.getData("Account Number"));
	}

	@Given("a user with correct username and password")
	public void a_user_with_correct_username_and_password() throws Exception {

		String ambiente = System.getenv().getOrDefault("AMBIENTE", null);
		String filePath = new BasicReader(ambiente).getFileFromResources("Execution Control.xlsx").getAbsolutePath();

		ExcelReader excelReader = new ExcelReader(filePath);
		JSONObject credentials = excelReader.getCredentialsForTcId("TestCasesRunner", "SC003");
		excelReader.close();

		String username = credentials.getString("User Name");
		String password = credentials.getString("Password");

		ingresarWebPorHost("host");

		loginPage.clickButtonSingsInRegister();
		loginPage.sendKeysInputUser(username);
		loginPage.sendKeysInputPassword(password);

		System.out.println("Dato generado: " + super.getData("Account Number"));
	}

	@When("the user tries to log in on the app")
	public void the_user_tries_to_log_in_on_the_app() {
		loginPage.clickButtonbuttonSingsIn();
	}

	@When("the user logs in on the app")
	public void the_user_logs_in_on_the_app() {
		loginPage.clickButtonbuttonSingsIn();
	}

	@Then("the system displays an incorrect user message")
	public void the_system_displays_an_incorrect_user_message() throws Exception {
		loginPage.ValidateLoginErrorText();
	}

	@Then("the system allows the user to log in")
	public void the_system_allows_the_user_to_log_in() {
		loginPage.isVisibleButtonAccount();
	}

	private void ingresarWebPorHost(String hostName) {
		homePage.maximizarVentana();
		homePage.navigateTo(environment.getString(hostName));
		homePage.waitUnitlPageLoadedWithBussyIcon();
	}


	@Then("the system displays the following tabs:")
	public void theSystemDisplaysTheFollowingTabs(DataTable table) {
		loginPage.validateMenu(table);
	}
}
