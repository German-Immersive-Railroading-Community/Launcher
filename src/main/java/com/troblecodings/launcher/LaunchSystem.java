package com.troblecodings.launcher;

import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.StartupUtil;

import java.util.Arrays;

public class LaunchSystem {

	public static final String DO_NOT_UPDATE = "--no-update";

	public static void main(String[] args) {
		FileUtil.readSettings();
		Launcher.initializeLogger();

		if(Arrays.stream(args).noneMatch(s -> s.equals(DO_NOT_UPDATE))) {
			StartupUtil.update();
		} else {
			Launcher.getLogger().info("Skipping updates!");
		}

		Runtime.getRuntime().addShutdownHook(Launcher.SHUTDOWNHOOK);
		FileUtil.init();
		Launcher.launch(Launcher.class, args);
	}

}
