package com.troblecodings.launcher;

import java.util.Arrays;
import java.util.Optional;

import com.troblecodings.launcher.util.StartupUtil;

public class LaunchSystem {

	public static String USERVERSION = "-useversion";

	public static void main(String[] args) {
		if(Arrays.stream(args).anyMatch(USERVERSION::equals)) {
			Launcher.mainStartup(args);
			return;
		}
		Optional<String> str = StartupUtil.findJavaVersion();
		if(str.isPresent()) {
			
		}

	}

}
