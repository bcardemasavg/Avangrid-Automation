package com.nttdata.utils.reporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nttdata.utils.Utilities;

public class ScenarioReport {

	private static List<ScenarioReport> scenarioReports = new ArrayList<ScenarioReport>();

	private List<TestCase> testCases = new ArrayList<TestCase>();
	private String featureName;
	private File fileReport;
	private String testExecutionKey = System.getenv().getOrDefault("ID_TEST_EXECUTION", null);

	public ScenarioReport() {
		setTestCases(new ArrayList<TestCase>());
	}

	public static ScenarioReport instance(String scenarioName, TestCase testCase) {
		ScenarioReport scenarioReport = new ScenarioReport();
		if (scenarioReports.isEmpty() || findScenarioReport(scenarioReports, scenarioName) == null) {
			scenarioReport.setFeatureName(scenarioName);
			scenarioReport.getTestCases().add(testCase);
			scenarioReports.add(scenarioReport);
		} else {
			scenarioReport = findScenarioReport(scenarioReports, scenarioName);
			scenarioReport.getTestCases().add(testCase);
		}
		return scenarioReport;
	}

	public static ScenarioReport findScenarioReport(List<ScenarioReport> scenarioReports, String scenarioName) {
		for (ScenarioReport scenarioReport : scenarioReports) {
			if (scenarioName.equals(scenarioReport.getFeatureName())) {
				return scenarioReport;
			}

		}
		return null;
	}

	@Override
	public String toString() {
		return Utilities.GSON.toJson(this);
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScenarioReport other = (ScenarioReport) obj;
		return Objects.equals(featureName, other.featureName);
	}

	public File getFileReport() {
		return fileReport;
	}

	public void setFileReport(File fileReport) {
		this.fileReport = fileReport;
	}

	public static List<ScenarioReport> getScenarioReports() {
		return scenarioReports;
	}

	public String getTestExecutionKey() {
		return testExecutionKey;
	}

	public void setTestExecutionKey(String testExecutionKey) {
		this.testExecutionKey = testExecutionKey;
	}

}
