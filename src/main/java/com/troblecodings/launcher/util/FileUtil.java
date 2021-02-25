package com.troblecodings.launcher.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

import net.cydhra.nidhogg.data.Session;

public class FileUtil {

	public static SettingsData SETTINGS = null;
	public static String ASSET_DIR = null;
	public static String LIB_DIR = null;

	public static Session DEFAULT = null;

	public static Path REMEMBERFILE;

	public static final Path SETTINGSPATH = Paths.get(System.getProperty("user.home") + "/.launcher/settings.json");

	private static String setCreateIfNotExists(String pathstr) {
		Path path = Paths.get(pathstr);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				// TODO LOG AND PRINT
				e.printStackTrace();
			}
		}
		return pathstr;
	}

	public static class SettingsData {

		public String baseDir = System.getenv("APPDATA") + "/gir";
		public int width = 1280;
		public int height = 720;
		public int ram = 2048;

	}

	public static final Gson GSON = new Gson();

	public static void readSettings() {
		try {
			if (Files.exists(SETTINGSPATH)) {
				SETTINGS = GSON.fromJson(Files.newBufferedReader(SETTINGSPATH), SettingsData.class);
			} else {
				Files.createDirectories(SETTINGSPATH.getParent());
				Files.createFile(SETTINGSPATH);
			}
		} catch (IOException e) {
			try {
				Files.delete(SETTINGSPATH);
			} catch (IOException e1) {
				e1.printStackTrace();// Do not log
			}
		} finally {
			if (SETTINGS == null)
				SETTINGS = new SettingsData();
		}
	}

	public static void init() {
		ASSET_DIR = setCreateIfNotExists(SETTINGS.baseDir + "/assets");
		LIB_DIR = setCreateIfNotExists(SETTINGS.baseDir + "/libraries");

		REMEMBERFILE = Paths.get(SETTINGS.baseDir + "/ac.ce");
		DEFAULT = CryptoUtil.readEncrypted(REMEMBERFILE, Session.class);
	}


	// Delete option files and mod, assets and libraries folder
	public static void resetFiles() {
		deleteFile(Paths.get(SETTINGS.baseDir + "/options.txt").toFile());
		deleteFile(Paths.get(SETTINGS.baseDir + "/optionsof.txt").toFile());
		deleteFile(Paths.get(SETTINGS.baseDir + "/GIR.json").toFile());
		deleteDirectory(Paths.get(SETTINGS.baseDir + "/mods").toFile());
		deleteDirectory(Paths.get(SETTINGS.baseDir + "/assets").toFile());
		deleteDirectory(Paths.get(SETTINGS.baseDir + "/libraries").toFile());
		deleteDirectory(Paths.get(SETTINGS.baseDir + "/config").toFile());
		try {
			FileUtil.init();
		} catch (Throwable e) {
			// Launcher.LOGGER.trace(e.getMessage(), e);
		}
		// Launcher.INSTANCEL.setPart(new HomePage());
	}

	private static void deleteDirectory(File directory) {
		if (directory != null && directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
			directory.delete();
		}
	}

	private static void deleteFile(File file) {
		if (file != null && file.exists() && !file.isDirectory()) {
			file.delete();
		}
	}

}
