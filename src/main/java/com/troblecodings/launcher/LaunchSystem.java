package com.troblecodings.launcher;

import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.StartupUtil;

import java.util.Arrays;

public class LaunchSystem {

	public static final String DO_NOT_UPDATE = "--no-update";
	public static final String[] ENABLE_BETA = new String[] { "-eb", "--enable-beta" };

	public static void main(String[] args) {
		FileUtil.readSettings();
		Launcher.initializeLogger();

		if(Arrays.stream(args).noneMatch(s -> s.equals(DO_NOT_UPDATE))) {
			Launcher.getLogger().info("Checking for updates...");
			StartupUtil.update();
		} else {
			Launcher.getLogger().info("Skipping updates!");
		}

		if(Arrays.stream(ENABLE_BETA).anyMatch(s -> Arrays.asList(args).contains(s))) {
			Launcher.getLogger().info("Enabling beta features.");
			Launcher.setBetaMode(true);
			StartupUtil.refreshBetaJson();
		}

		Runtime.getRuntime().addShutdownHook(Launcher.SHUTDOWNHOOK);
		FileUtil.init();
		Launcher.launch(Launcher.class, args);
	}

}
