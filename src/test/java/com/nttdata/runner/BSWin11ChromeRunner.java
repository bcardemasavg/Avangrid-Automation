package com.nttdata.runner;

import org.junit.BeforeClass;

import com.nttdata.utils.Constants;
import com.nttdata.utils.runner.MainRun;

import io.cucumber.junit.CucumberOptions;


@CucumberOptions(tags = "@login", plugin = {
		"json:" + MainRun.REPORT_FOLDER + "/BSWin11ChromeRunner.json",
		"html:" + MainRun.REPORT_FOLDER + "/BSWin11ChromeRunner.html",
		"junit:" + MainRun.REPORT_FOLDER + "/BSWin11ChromeRunner.xml" })
public class BSWin11ChromeRunner extends DesktopRunner {
	@BeforeClass
	public static void config() {
		System.setProperty("labExecution", Constants.EXECUTION_BS);
		System.setProperty("ExecutionType", Constants.EXECUTION_WEB);
		System.setProperty("configuration", System.getenv().getOrDefault("configuration", "defaultBSWeb"));
	}
}
