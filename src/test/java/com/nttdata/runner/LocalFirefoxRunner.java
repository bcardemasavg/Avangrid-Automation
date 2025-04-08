package com.nttdata.runner;

import org.junit.BeforeClass;

import com.nttdata.utils.Constants;
import com.nttdata.utils.runner.MainRun;

import io.cucumber.junit.CucumberOptions;

@CucumberOptions(tags = "@login", plugin = {
		"json:" + MainRun.REPORT_FOLDER + "/LocalFirefoxRunner.json",
		"html:" + MainRun.REPORT_FOLDER + "/LocalFirefoxRunner.html",
		"junit:" + MainRun.REPORT_FOLDER + "/LocalFirefoxRunner.xml" })
public class LocalFirefoxRunner extends DesktopRunner {
	@BeforeClass
	public static void config() {
		DesktopRunner.config();
		System.setProperty("browser", "Firefox");
		System.setProperty("labExecution", Constants.EXECUTION_LOCAL);
		System.setProperty("closeDriver", "false");
	}
}
