package com.troblecodings.launcher;

import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.StartupUtil;

public class LaunchSystem {

	public static final String USERVERSION = "-useversion";

	public static void main(String[] args) {
		FileUtil.readSettings();
		System.out.println("read settings");
		Launcher.initializeLogger();
		StartupUtil.update();
		System.out.println("updatet check finished");
		Runtime.getRuntime().addShutdownHook(Launcher.SHUTDOWNHOOK);
		FileUtil.init();
		Launcher.launch(Launcher.class, args);
	}

}
