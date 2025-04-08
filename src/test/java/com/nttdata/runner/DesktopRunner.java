package com.nttdata.runner;

import com.nttdata.utils.Constants;
import com.nttdata.utils.runner.MainRun;

public class DesktopRunner extends MainRun {

	public static void config() {
		System.setProperty("platform", Constants.EXECUTION_DESKTOP);
	}
}
