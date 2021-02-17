package com.troblecodings.launcher.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.List;
import javax.crypto.Cipher;
import com.troblecodings.launcher.ErrorDialog;
import com.troblecodings.launcher.HomePage;
import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.SettingsPage;

import net.cydhra.nidhogg.data.Session;

public class FileUtil {

	public static String BASE_DIR = null;
	public static String ASSET_DIR = null;
	public static String LIB_DIR = null;
	
	public static Session DEFAULT = null;

	public static String TRANSFORM = "AES";
	public static Path REMEMBERFILE;
	
	public static final Path SETTINGSPATH = Paths.get(System.getProperty("user.home") + "/.launcher/Settings.txt");
	
	private static String setCreateIfNotExists(String pathstr) throws Throwable {
		Path path = Paths.get(pathstr);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}
		return pathstr;
	}
	
	// Initiates all folders and reads the remember file
	public static void init() throws Throwable {
		try {
			if (Files.exists(SETTINGSPATH)) {
				List<String> settings = Files.readAllLines(SETTINGSPATH);
				StartupUtil.LWIDTH = settings.size() < 1 ?  "1280":settings.get(0);
				StartupUtil.LHEIGHT = settings.size() < 2 ?  "720":settings.get(1);
				if(settings.size() >= 3)
					StartupUtil.RAM = Integer.valueOf(settings.get(2));
				BASE_DIR = settings.size() < 4 ?  (System.getenv("APPDATA") + "/gir"):settings.get(3);
			} else {
				Files.createDirectories(SETTINGSPATH.getParent());
				Files.createFile(SETTINGSPATH);
				BASE_DIR = System.getenv("APPDATA") + "/gir";
			}
		} catch (IOException e) {
			ErrorDialog.createDialog(e);
		}

		SettingsPage.NEWBASEDIR = BASE_DIR = setCreateIfNotExists(BASE_DIR.replace("\\", "/"));
		ASSET_DIR = setCreateIfNotExists(BASE_DIR + "/assets");
		LIB_DIR = setCreateIfNotExists(BASE_DIR + "/libraries");
		
		REMEMBERFILE = Paths.get(BASE_DIR + "/ac.ce");
		if (Files.exists(REMEMBERFILE)) {
			byte[] content = Files.readAllBytes(REMEMBERFILE);
			if (content != null && content.length > 0) {
				Key key = CryptoUtil.getKey(TRANSFORM);

				Cipher cipher = Cipher.getInstance(TRANSFORM);
				cipher.init(Cipher.DECRYPT_MODE, key);

				try {
					byte[] encrypted = cipher.doFinal(content);
					String[] session = new String(encrypted).split(System.lineSeparator());
					if (session.length == 4)
						DEFAULT = new Session(session[0], session[1], session[2], session[3]);
				} catch (Exception e) {
					Launcher.LOGGER.trace(e.getMessage(), e);
				}
			}
		}
		
	}

	// Encrypts and saves a session
	public static void saveSession(Session session) throws Throwable {
		Key key = CryptoUtil.getKey(TRANSFORM);

		String sessionstring = session.getId() + System.lineSeparator() + session.getAlias() + System.lineSeparator()
				+ session.getAccessToken() + System.lineSeparator() + session.getClientToken();

		Cipher cipher = Cipher.getInstance(TRANSFORM);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] encrypted = cipher.doFinal(sessionstring.getBytes());
		Files.write(REMEMBERFILE, encrypted);
	}
	
	// Delete option files and mod, assets and libraries folder
	public static void resetFiles() {
		deleteFile(Paths.get(FileUtil.BASE_DIR + "/options.txt").toFile());
		deleteFile(Paths.get(FileUtil.BASE_DIR + "/optionsof.txt").toFile());
		deleteFile(Paths.get(FileUtil.BASE_DIR + "/GIR.json").toFile());
		deleteDirectory(Paths.get(FileUtil.BASE_DIR + "/mods").toFile());
		deleteDirectory(Paths.get(FileUtil.BASE_DIR + "/assets").toFile());
		deleteDirectory(Paths.get(FileUtil.BASE_DIR + "/libraries").toFile());
		deleteDirectory(Paths.get(FileUtil.BASE_DIR + "/config").toFile());
		try {
			FileUtil.init();
		} catch (Throwable e) {
			Launcher.LOGGER.trace(e.getMessage(), e);
		}
		Launcher.INSTANCEL.setPart(new HomePage());
	}
	
	private static void deleteDirectory(File directory) {
		if(directory != null && directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i = 0; i < files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	        directory.delete();
	    }
	}
	
	private static void deleteFile(File file) {
		if(file != null && file.exists() && !file.isDirectory()) {
			file.delete();
		}
	}

}
