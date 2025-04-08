package com.nttdata.runner;

import org.junit.BeforeClass;

import com.nttdata.utils.Constants;
import com.nttdata.utils.runner.MainRun;

import io.cucumber.junit.CucumberOptions;

@CucumberOptions(tags = "@login", plugin = {
		"json:" + MainRun.REPORT_FOLDER + "/LocalEdgeRunner.json",
		"html:" + MainRun.REPORT_FOLDER + "/LocalEdgeRunner.html",
		"junit:" + MainRun.REPORT_FOLDER + "/LocalEdgeRunner.xml" })
public class LocalEdgeRunner extends DesktopRunner {
	@BeforeClass
	public static void config() {
		DesktopRunner.config();
		System.setProperty("browser", "Edge");
		System.setProperty("labExecution", Constants.EXECUTION_LOCAL);
		System.setProperty("closeDriver", "false");
	}
}
