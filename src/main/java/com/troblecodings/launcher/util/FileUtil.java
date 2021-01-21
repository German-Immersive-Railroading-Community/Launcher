package com.troblecodings.launcher.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.List;

import javax.crypto.Cipher;

import com.troblecodings.launcher.ErrorDialog;
import com.troblecodings.launcher.SettingsPage;

import net.cydhra.nidhogg.data.Session;

public class FileUtil {

	public static String BASE_DIR = null;
	public static String ASSET_DIR = null;
	public static String LIB_DIR = null;
	
	public static Session DEFAULT = null;

	public static String TRANSFORM = "AES";
	public static Path REMEMBERFILE;

	private static String setCreateIfNotExists(String pathstr) throws Throwable {
		Path path = Paths.get(pathstr);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}
		return pathstr;
	}
	
	// Initiates all folders and reads the remember file
	public static void init() throws Throwable {
		Path settingpath = Paths.get(System.getenv("APPDATA") + "/gir/Settings.txt");
		try {
			if (Files.exists(settingpath)) {
				List<String> settings = Files.readAllLines(settingpath);
				StartupUtil.LWIDTH = settings.size() < 1 ?  "1280":settings.get(0);
				StartupUtil.LHEIGHT = settings.size() < 2 ?  "720":settings.get(1);
				StartupUtil.RAM = Integer.valueOf(settings.size() < 3 ?  "1024":settings.get(2));
				BASE_DIR = settings.size() < 4 ?  (System.getenv("APPDATA") + "/gir"):settings.get(3);
			} else {
				Files.createDirectories(settingpath.getParent());
				Files.createFile(settingpath);
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
				} catch (Exception e) {}
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

}
