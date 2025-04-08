package com.nttdata.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.nttdata.utils.reporter.Attachment;
import com.nttdata.utils.reporter.Step;
import com.nttdata.utils.reporter.TestCase;

import io.cucumber.java.Scenario;

public class ScenarioContext {

	private Map<String, Object> scenarioContext;
	private Scenario scenario;
	private TestCase testCase;
	private Step currentStep;

	public ScenarioContext() {
		scenarioContext = new HashMap<>();
	}

	public void setScenarioContext(String key, Object value) {
		scenarioContext.put(key, value);
	}

	public Object getScenarioContext(String key) {
		return scenarioContext.get(key);
	}

	public boolean isConstains(String key) {
		return scenarioContext.containsKey(key);
	}

	@Override
	public String toString() {
		return Utilities.GSOn_PRETTY.toJson(this);
	}

	public void attach(File file, String mediaType, String name) throws Exception {
		byte[] fileContent = Files.readAllBytes(file.toPath());
		file.deleteOnExit();
		getScenario().attach(fileContent, mediaType, name);
		if (!Objects.isNull(currentStep)) {
			currentStep.getAttachments().add(new Attachment(Utilities.encodeBase64(fileContent), mediaType, name));
		} else {
			testCase.getAttachments().add(new Attachment(Utilities.encodeBase64(fileContent), mediaType, name));
		}
	}

	public void attach(String text, String mediaType, String name) {
		getScenario().attach(text, mediaType, name);
		if (!Objects.isNull(currentStep)) {
			currentStep.getAttachments().add(new Attachment(Utilities.encodeBase64(text), mediaType, name));
		} else {
			testCase.getAttachments().add(new Attachment(Utilities.encodeBase64(text), mediaType, name));
		}
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Step getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(Step currentStep) {
		this.currentStep = currentStep;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

}
