package com.nttdata.StepDefinition;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nttdata.utils.CommonsHooks;
import com.nttdata.utils.Constants;
import com.nttdata.utils.ScenarioContext;
import com.nttdata.utils.Utilities;
import com.nttdata.utils.reporter.VideoReord;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks extends CommonsHooks {
	protected Logger log = LogManager.getLogger(this.getClass());
	VideoReord videoReord;

	public Hooks(ScenarioContext scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	@Before
	public void prepareTest(Scenario scenario) throws Throwable {
		super.prepareTest(scenario);
		scenarioContext.setScenarioContext("usuarios", basicReader.readJsonFile("usuarios.json"));
		scenarioContext.setScenarioContext("ENVIRONMENT", environment);
		scenarioContext.setScenarioContext(Constants.TEST_CASE_KEY, testCaseKey);
		scenarioContext.setScenario(scenario);

		buildDriver();
		sessionId();
		attachCapabilitiesInfo(scenario);
		super.setCurrentStep();

		if (System.getenv().getOrDefault("REC_VIDEO", "false").equals("true")) {
			videoReord = new VideoReord(testCaseKey);
			videoReord.startRecording();
		}

	}

	@AfterStep
	public void stepsScenarioTest(Scenario scenario) throws Exception {
		super.stepsScenarioTest(scenario);
	}

	@After
	public void finalizarTest(Scenario scenario) throws Exception {
		try {
			sessionId();
			fechaFinTest = Utilities.getFechaHora();
			millisFinTest = (new Timestamp(System.currentTimeMillis())).getTime();
			this.shutdownDriver();
			super.finalizarTest(scenario);
			if (System.getenv().getOrDefault("REC_VIDEO", "false").equals("true")) {
				videoReord.stopRecording();
			}
		} catch (Exception e) {
			log.error("Error en finalizarTest: " + e.getMessage(), e);
		}

	}

	@AfterAll
	public static void afterAll() {
		CommonsHooks.afterAll();
	}

	@Override
	public void shutdownDriver() {
		super.shutdownDriver();
	}

	@Override
	public void buildDriver() throws Throwable {
		super.buildDriver();
	}

}
