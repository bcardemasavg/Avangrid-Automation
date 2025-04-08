package io.cucumber.core.runner;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.nttdata.utils.reporter.Step;

import io.cucumber.core.stepexpression.Argument;
import io.cucumber.core.stepexpression.DataTableArgument;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;

public class TestCaseStateReflex {

	public TestCaseState testCaseState;
	public TestCase testCase;
	public Scenario scenario;

	public TestCaseStateReflex(Scenario scenario) throws Exception {
		this.scenario = scenario;

		Field fieldTestCaseState = Scenario.class.getDeclaredField("delegate");
		fieldTestCaseState.setAccessible(true);
		// Obtener el valor del atributo privado
		this.testCaseState = (TestCaseState) fieldTestCaseState.get(scenario);

		Field fieldTestCase = TestCaseState.class.getDeclaredField("testCase");
		fieldTestCase.setAccessible(true);
		this.testCase = (TestCase) fieldTestCase.get(testCaseState);
	}

	public Throwable getError() {
		return testCaseState.getError();
	}

	public List<Step> getStepFormat() throws Exception {
		List<Step> result = new ArrayList<Step>();
		Step newStep;

		Field field = TestCase.class.getDeclaredField("testSteps");
		field.setAccessible(true);
		// Obtener el valor del atributo privado
		List<PickleStepTestStep> testSteps = (List<PickleStepTestStep>) field.get(testCase);

		for (PickleStepTestStep testStep : testSteps) {
			newStep = new Step();
			newStep.setText(testStep.getStepText());
			newStep.setFileLine(testStep.getStepLine());
			newStep.setStatus(Status.PENDING.toString());
			newStep.setKeyWord(testStep.getStep().getKeyword());

			for (Argument argument : testStep.getDefinitionMatch().getArguments()) {
				if (argument instanceof DataTableArgument) {
					newStep.setDataTable(((DataTableArgument) argument).toString());
				}
			}

			result.add(newStep);
		}

		return result;
	}

	public String getScenarioName() throws Exception {
		File scenarioFile = new File(scenario.getUri());
		List<String> lines = Files.readAllLines(scenarioFile.toPath());
		String featrueLine = "";
		for (String line : lines) {
			if (line.startsWith("Feature:")) {
				featrueLine = line;
				break;
			}
		}
		return featrueLine.replace("Feature", "").replace(":", "");
	}

}
