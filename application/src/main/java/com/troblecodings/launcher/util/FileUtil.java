package com.troblecodings.launcher.util;//package com.troblecodings.launcher.util;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.Reader;
//import java.io.Writer;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
//
//import com.google.gson.Gson;
//import com.troblecodings.launcher.Launcher;
//
//public class FileUtil {
//
////	public static SettingsData SETTINGS = new SettingsData();
////	public static String ASSET_DIR = null;
////	public static String LIB_DIR = null;
//
//	public static Path REMEMBERFILE;
//
//	public static final Path SETTINGSPATH = Paths.get(System.getProperty("user.home") + "/.launcher/settings.json");
//
//	private static String setCreateIfNotExists(String pathstr) {
//		Path path = Paths.get(pathstr);
//		if (!Files.exists(path)) {
//			try {
//				Files.createDirectories(path);
//			} catch (IOException e) {
//				Launcher.onError(e);
//			}
//		}
//		return pathstr;
//	}
//
//	public static boolean moveBaseDir(String file) {
//		Path ptof = Paths.get(file);
//		if (Files.notExists(ptof) || !Files.isDirectory(ptof))
//			return false;
//
//		Path old = Paths.get(SETTINGS.baseDir);
//		if (ptof.equals(old))
//			return false;
//
//		SETTINGS.baseDir = file;
//
//		try { // Why? WHY? Let me disable Exceptions pls
//			Files.walk(old).forEach(pt -> {
//				try { // I really hate this language ... I mean ... really
//					Path newpth = Paths.get(pt.toString().replace(old.toString(), file));
//					if (Files.isDirectory(pt)) {
//						Files.createDirectories(newpth);
//						return;
//					}
//					Files.move(pt, newpth, StandardCopyOption.REPLACE_EXISTING);
//				} catch (IOException e) {
//					Launcher.onError(e);
//					// I fucking don't care if this fails
//				}
//			});
//			Files.walk(old).sorted((c1, c2) -> {
//				int c1l = c1.toString().length();
//				int c2l = c2.toString().length();
//				return c1l < c2l ? 1 : (c1l == c2l ? 0 : -1);
//			}).forEach(p -> {
//				try {
//					Files.deleteIfExists(p);
//				} catch (IOException e) {
//					Launcher.onError(e);
//					// I fucking don't care if this fails
//				}
//			});
//		} catch (IOException e) {
//			Launcher.onError(e);
//			return false;
//		}
//		init();
//		return true;
//	}
//
//	public static class SettingsData {
//
//		public String baseDir = System.getenv("APPDATA") + "/gir";
//		public int width = 1280;
//		public int height = 720;
//		public int ram = 4096;
//		public ArrayList<String> optionalMods = new ArrayList<>();
//		public String javaPath = "";
//	}
//
//	// Delete option files and mod, assets and libraries folder
////	public static void resetFiles() {
////		Launcher.getLogger().info("Started launcher reset!");
////		deleteFile(Paths.get(SETTINGS.baseDir + "/options.txt").toFile());
////		deleteFile(Paths.get(SETTINGS.baseDir + "/optionsof.txt").toFile());
////		deleteFile(Paths.get(SETTINGS.baseDir + "/GIR.json").toFile());
////		deleteDirectory(Paths.get(SETTINGS.baseDir + "/mods").toFile());
////		deleteDirectory(Paths.get(SETTINGS.baseDir + "/assets").toFile());
////		deleteDirectory(Paths.get(SETTINGS.baseDir + "/libraries").toFile());
////		deleteDirectory(Paths.get(SETTINGS.baseDir + "/config").toFile());
////		FileUtil.init();
////		Launcher.getLogger().info("Finished launcher reset!");
////	}
//
////	private static void deleteDirectory(File directory) {
////		if (directory != null && directory.exists()) {
////			File[] files = directory.listFiles();
////			if (null != files) {
////				for (int i = 0; i < files.length; i++) {
////					if (files[i].isDirectory()) {
////						deleteDirectory(files[i]);
////					} else {
////						files[i].delete();
////					}
////				}
////			}
////			directory.delete();
////		}
////	}
////
////	private static void deleteFile(File file) {
////		if (file != null && file.exists() && !file.isDirectory()) {
////			file.delete();
////		}
////	}
//
//}
