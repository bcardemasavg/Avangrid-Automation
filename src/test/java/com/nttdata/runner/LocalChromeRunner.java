package com.nttdata.runner;

import org.junit.BeforeClass;

import com.nttdata.utils.Constants;
import com.nttdata.utils.runner.MainRun;

import io.cucumber.junit.CucumberOptions;


@CucumberOptions(plugin = {
		"json:" + MainRun.REPORT_FOLDER + "/LocalChromeRunner.json",
		"html:" + MainRun.REPORT_FOLDER + "/LocalChromeRunner.html",
		"junit:" + MainRun.REPORT_FOLDER + "/LocalChromeRunner.xml" })
public class LocalChromeRunner extends DesktopRunner {
	@BeforeClass
	public static void config() {
		DesktopRunner.config();
		System.setProperty("browser", "Chrome");
		System.setProperty("labExecution", Constants.EXECUTION_LOCAL);
		System.setProperty("closeDriver", "true");
	}
}
