package com.nttdata.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import com.google.gson.Gson;
import com.nttdata.utils.database.BasicReader;
import com.nttdata.utils.driver.DriverFactory;
import com.nttdata.utils.driver.LocalBrowserFactory;
import com.nttdata.utils.driver.MobileFactory;
import com.nttdata.utils.reporter.Attachment;
import com.nttdata.utils.reporter.HtmlReporter;
import com.nttdata.utils.reporter.JiraReporter;
import com.nttdata.utils.reporter.ScenarioReport;
import com.nttdata.utils.reporter.TestCase;

import io.cucumber.core.runner.TestCaseStateReflex;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;

public class CommonsHooks {
	protected static Logger log = LogManager.getLogger(CommonsHooks.class);
	protected String fechaInicioTest;
	protected String fechaFinTest;
	protected String testCaseKey;
	protected String testPlan;
	protected ScenarioContext scenarioContext;
	protected JSONObject environment;
	protected long millisInicioTest;
	protected long millisFinTest;
	protected String sessionID;
	public String localIdentifier;
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss");
	protected Configuration configuration;
	protected BasicReader basicReader;
	List<String> contenidoScenario;
	protected String labExecution = System.getProperty("labExecution", Constants.EXECUTION_LOCAL);
	protected String platform = System.getProperty("platform", null);
	int stepNumber = 0;
	TestCase testCase;
	TestCaseStateReflex testCaseStateReflex;
	ScenarioReport scenarioReport;

	public void prepareTest(Scenario scenario) throws Throwable {
		Utilities.setTimeZone();
		this.scenarioContext.setScenario(scenario);
		testCase = new TestCase();

		testCase.setName(scenario.getName());
		testCase.setStart(new Timestamp(System.currentTimeMillis()));
		testCaseStateReflex = new TestCaseStateReflex(scenario);
		scenarioReport = ScenarioReport.instance(testCaseStateReflex.getScenarioName(), testCase);
		testCase.getSteps().addAll(testCaseStateReflex.getStepFormat());
		testCase.getTags().addAll(scenario.getSourceTagNames());
		testCase.setTestKey(generarTestCaseKey());
		testCaseKey = testCase.getTestKey();
		fechaInicioTest = Utilities.getFechaHora();
		millisInicioTest = testCase.getStart().getTime();
		scenarioContext.setTestCase(testCase);

		testPlan = getTestPlan();
		setProjectName(scenario);

		String ambiente = System.getenv("AMBIENTE");

		log.info("Ambiente :" + ambiente);
		basicReader = new BasicReader(ambiente);
		environment = basicReader.getEnvironmentByName(ambiente);

		if (environment == null)
			throw new Exception(
					"Se debe indicar ambiente de ejecucion con la variable AMBIENTE existente en environment.json");

		String configurationId = System.getProperty("configuration", "defaultAndroid");
		if (basicReader.getConfigurationFileName().exists()) {
			configuration = Configuration.configureDeviceCapabilities(configurationId,
					basicReader.getConfigurationFileName());
		}

	}

	protected void attachCapabilitiesInfo(Scenario scenario) {
		HashMap<String, String> capabilitiesInfo = new HashMap<String, String>();
		Capabilities capabilities = ((RemoteWebDriver) DriverFactory.getDriver()).getCapabilities();
		capabilitiesInfo.put("labExecution", System.getProperty("labExecution"));
		capabilitiesInfo.put("platform", System.getProperty("platform"));

		capabilitiesInfo.put("os", String.valueOf(capabilities.getCapability("platformName")));
		capabilitiesInfo.put("os_version", String.valueOf(capabilities.getCapability("platformVersion")));
		capabilitiesInfo.put("deviceName", String.valueOf(capabilities.getCapability("deviceName")));
		capabilitiesInfo.put("deviceManufacturer", String.valueOf(capabilities.getCapability("deviceManufacturer")));

		setBrowser(capabilitiesInfo);
		setMobileAplication(capabilitiesInfo);

		System.out.println(new Gson().toJson(capabilitiesInfo));
		scenarioContext.attach(Utilities.GSON.toJson(capabilitiesInfo), "text/plain", "capabilities");
	}

	private void setMobileAplication(HashMap<String, String> capabilitiesInfo) {
		String app_version = null;
		try {
			Capabilities capabilities = ((RemoteWebDriver) DriverFactory.getDriver()).getCapabilities();
			app_version = (String) Optional.ofNullable(capabilities.getCapability("appPackage")).orElse(null);
			if (app_version == null) {
				app_version = (String) Optional.ofNullable(capabilities.getCapability("bundleId")).orElse(null);
			}
		} catch (Exception e) {
		}
		if (app_version != null) {
			capabilitiesInfo.put("app", System.getProperty("app", null));
			capabilitiesInfo.put("app_version", app_version);
		}
	}

	protected String setBrowser(HashMap<String, String> capabilitiesInfo) {
		try {
			String javaScript = "return (function(){\r\n" + "    var ua= navigator.userAgent;\r\n" + "    var tem; \r\n"
					+ "    var M= ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\\/))\\/?\\s*(\\d+)/i) || [];\r\n"
					+ "    if(/trident/i.test(M[1])){\r\n" + "        tem=  /\\brv[ :]+(\\d+)/g.exec(ua) || [];\r\n"
					+ "        return 'IE '+(tem[1] || '');\r\n" + "    }\r\n" + "    if(M[1]=== 'Chrome'){\r\n"
					+ "        tem= ua.match(/\\b(OPR|Edge)\\/(\\d+)/);\r\n"
					+ "        if(tem!= null) return tem.slice(1).join(' ').replace('OPR', 'Opera');\r\n" + "    }\r\n"
					+ "    M= M[2]? [M[1], M[2]]: [navigator.appName, navigator.appVersion, '-?'];\r\n"
					+ "    if((tem= ua.match(/version\\/(\\d+)/i))!= null) M.splice(1, 1, tem[1]);\r\n"
					+ "    return M.join(' ');\r\n" + "})();";
			String result = (String) ((RemoteWebDriver) DriverFactory.getDriver()).executeScript(javaScript);

			capabilitiesInfo.put("browser", result.split(" ")[0].toLowerCase());
			capabilitiesInfo.put("browser_version", result.split(" ")[1].toLowerCase());

			return result;
		} catch (Exception e) {
			return null;
		}
	}

	protected File takeScreenShoot(String nombreSS) throws IOException {
		try {
			return DriverFactory.getScreenshot(DriverFactory.getDriver(), nombreSS.replace("\"", ""),
					sdf.format(new Date()));
		} catch (Exception e) {
			log.error("Error en la captura de screenshoot:" + e.getMessage(), e);
		}
		return null;
	}

	protected void sessionId() {
		SessionId sessionIdObject = getSessionId();
		sessionID = (Objects.isNull(sessionIdObject)) ? "" : sessionIdObject.toString();
		scenarioContext.setScenarioContext("SESSIONID", sessionID);
		scenarioContext.getScenarioContext("SESSIONID");
		log.info("SESSIONID = " + sessionID);
	}

	public String getTestPlan() {
		return System.getenv("ID_TEST_PLAN");
	}

	public SessionId getSessionId() {
		if (Objects.isNull(DriverFactory.getDriver()))
			return null;
		return ((RemoteWebDriver) DriverFactory.getDriver()).getSessionId();
	}

	public void setProjectName(Scenario scenario) {
		String name = "";
		if (testCaseKey != null) {
			name = testCaseKey.toUpperCase();
		}
		name = name + " " + scenario.getName();
		System.setProperty("project.session", name);
	}

	public void shutdownDriver() {
		try {
			if (DriverFactory.getDriver() != null) {
				log.info("Cerrando Driver");
				DriverFactory.getDriver().quit();
				DriverFactory.removeDriver();
			}
		} catch (Exception ex) {
			log.error(ex);
		}
	}

	public void buildDriver() throws Throwable {
		String browser = System.getProperty("browser", "Chrome");
		if (Constants.EXECUTION_LOCAL.equals(labExecution)) {
			DriverFactory.addDriver(LocalBrowserFactory.buildBrowser(browser));
		} else if (Constants.EXECUTION_MOBILE.equals(labExecution)) {
			DriverFactory.addDriver(MobileFactory.instanceMobile(configuration));
		} else {
			throw new Exception("Configuracion de ejecucion no encontrada.");
		}

	}

	private String generarTestCaseKey() {
		String platform = System.getProperty("platform", null);
		if (Objects.isNull(platform)) {
			String test = testCase.getTags().stream().filter(e -> e.startsWith("@TestCaseKey")).findFirst()
					.orElse(" = ").split("=")[1];
			if (!test.equals(" "))
				return test;
		}
		String test = testCase.getTags().stream().filter(e -> e.startsWith("@" + platform + "TestCaseKey")).findFirst()
				.orElse(" = ").split("=")[1];
		if (!test.equals(" ")) {
			return test;
		} else {
			test = testCase.getTags().stream().filter(e -> e.startsWith("@TestCaseKey")).findFirst().orElse(" = ")
					.split("=")[1];
		}

		return test;
	}

	public void stepsScenarioTest(Scenario scenario) throws Exception {
		setCurrentStep();
		scenarioContext.getCurrentStep().setNumber(stepNumber + 1);
		scenarioContext.setCurrentStep(scenarioContext.getCurrentStep());
		if (scenario.isFailed()) {
			scenarioContext.getCurrentStep().setStatus(Status.FAILED.toString());
			scenarioContext.getCurrentStep().setError(testCaseStateReflex.getError().toString());
			testCase.setError(testCaseStateReflex.getError().toString());
			testCase.setStatus(Status.FAILED.toString());
		} else {
			scenarioContext.getCurrentStep().setStatus(Status.PASSED.toString());
			testCase.setStatus(Status.PASSED.toString());
		}
		scenarioContext.attach(takeScreenShoot("" + new Date().getTime()), "image/png", Utilities.getFechaReporte());
		if ((stepNumber + 1) < testCase.getSteps().size()) {
			stepNumber++;
			setCurrentStep();
		}
	}

	public void setCurrentStep() {
		scenarioContext.setCurrentStep(testCase.getSteps().get(stepNumber));
	}

	public void finalizarTest(Scenario scenario) throws Exception {
		try {
			testCase.setFinish(new Timestamp(System.currentTimeMillis()));

			/* Generar Reporte HTML */
			File reporter = HtmlReporter.generate(testCase);
			log.info("REPORTE: " + reporter.getAbsolutePath());

			/* Inicio Reporte a JIRA solo si JIRA_UPDATE=SI */
			if (System.getenv().getOrDefault("JIRA_UPDATE", "NO").toUpperCase().equals("SI")) {

				String testPlanKey = System.getenv().getOrDefault("ID_TEST_PLAN", null);
				if (Objects.isNull(testPlanKey)) {
					throw new Exception(
							"Se debe definir variable de entorno ID_TEST_PLAN, para actualizar en XRAY. Ejemplo SANAUT-33");
				}

				if (Objects.isNull(testCase.getTestKey())) {
					throw new Exception("TestCaseKey no encontrada, debe definice en los TAGS del caso de prueba");
				}

				HashMap<String, String> capabilitesInfo = getCapabilitiFromTestCase(testCase);
				String featureName = scenarioReport.getFeatureName();

				if (Objects.isNull(scenarioReport.getTestExecutionKey())) {
					log.info("Variable ID_TEST_EXECUTION no definida, se creara un nuevo Test Execution");
					JSONObject testExecution = new JSONObject(
							JiraReporter.createTestExecution(capabilitesInfo, featureName));
					log.info(testExecution.toString());
					scenarioReport.setTestExecutionKey(testExecution.getString("key"));
					System.setProperty("testExecutionKey", scenarioReport.getTestExecutionKey());
				}

				testCase.getAttachments().add(0,
						new Attachment(Utilities.encodeBase64(Files.readAllBytes(reporter.toPath())), "text/html",
								reporter.getName()));

				String resultService = JiraReporter.postTestCaseExecution(testCase, testPlanKey,
						scenarioReport.getTestExecutionKey());

				testCase.getAttachments().remove(0);

				log.info("UPLOAD JIRA: " + resultService);

				/* FIN Reporte a JIRA */
			} else {
				log.info("No se actualizara en JIRA, debes definir variable de entorno JIRA_UPDATE=SI para actualizar");
			}
		} catch (Exception e) {
			log.error("Error en finalizarTest: " + e.getMessage(), e);
		}

	}

	public static void afterAll() {
		log.info("Features: " + ScenarioReport.getScenarioReports().size());
		try {

			for (ScenarioReport scenarioReport : ScenarioReport.getScenarioReports()) {
				log.info("Feature: " + scenarioReport.getFeatureName() + " - TestCases: "
						+ scenarioReport.getTestCases().size());
				/* Generar Reporte HTML */
				File reporter = HtmlReporter.generate(scenarioReport.getTestCases(), scenarioReport.getFeatureName());

				/* Inicio Reporte a JIRA solo si JIRA_UPDATE=SI */
				if (System.getenv().getOrDefault("JIRA_UPDATE", "NO").toUpperCase().equals("SI")) {

					String testPlanKey = System.getenv().getOrDefault("ID_TEST_PLAN", null);
					if (Objects.isNull(testPlanKey)) {
						throw new Exception(
								"Se debe definir variable de entorno ID_TEST_PLAN, para actualizar en XRAY. Ejemplo SANAUT-33");
					}

					if (Objects.isNull(scenarioReport.getTestExecutionKey())) {
						throw new Exception(
								"No se adjuntara reporte a un Test Execution, ya que no se creo ninguno anteriormente");
					}

					JiraReporter.attachmentsToIssue(reporter, scenarioReport.getTestExecutionKey());

					/* FIN Reporte a JIRA */
				} else {
					log.info(
							"No se actualizara en JIRA, debes definir variable de entorno JIRA_UPDATE=SI para actualizar");
				}
			}

		} catch (Exception e) {
			log.error("Error en finalizarTest: " + e.getMessage(), e);
		}
	}

	private static HashMap<String, String> getCapabilitiFromTestCase(TestCase testCase) {
		Attachment capabilities = testCase.getAttachments().stream().filter(x -> "capabilities".equals(x.getName()))
				.findFirst().orElse(null);
		if (capabilities != null) {
			return Utilities.GSON.fromJson(Utilities.decodeBase64((String) capabilities.getData()),
					new HashMap<String, String>().getClass());
		}
		return null;
	}

}
