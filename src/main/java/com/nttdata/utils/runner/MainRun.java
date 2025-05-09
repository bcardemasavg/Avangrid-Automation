package com.nttdata.utils.runner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(glue = {
		"com.nttdata.StepDefinition" }, features = "src/test/resources/TestCases", plugin = { "pretty" })
public class MainRun {
	public static final String REPORT_FOLDER = "target/report/";
	public static List<String> tags = new ArrayList<String>();

	@BeforeClass
	public static void configPrincipal() {
		System.setProperty("project.build", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()));
		System.setProperty("project.name", System.getenv().getOrDefault("PROJECT_NAME", "Automation"));
	}
}
