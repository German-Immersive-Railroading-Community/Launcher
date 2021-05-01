package com.troblecodings.launcher;

import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.StartupUtil;

public class LaunchSystem {

	public static final String USERVERSION = "-useversion";

	public static void main(String[] args) {
		FileUtil.readSettings();
		Launcher.initializeLogger();
		StartupUtil.update();
		Runtime.getRuntime().addShutdownHook(Launcher.SHUTDOWNHOOK);
		FileUtil.init();
		Launcher.launch(Launcher.class, args);
	}

}
