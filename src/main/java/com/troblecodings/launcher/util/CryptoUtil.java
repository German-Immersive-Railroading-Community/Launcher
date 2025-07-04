package com.troblecodings.launcher.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonParseException;
import com.troblecodings.launcher.Launcher;

public class CryptoUtil {
	
	public static String TRANSFORM = "AES";
	
	// Encrypts and saves a session
	public static void saveEncrypted(Path file, Object session) {
		Key key = CryptoUtil.getKey(TRANSFORM);
		
		String sessionstring = Launcher.GSON.toJson(session);
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORM);
			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] encrypted = cipher.doFinal(sessionstring.getBytes());
			Files.write(file, encrypted);
		} catch (Throwable e) {
			Launcher.onError(e);
		}
	}
	
	public static <T> T readEncrypted(Path file, Class<T> t) {
		try {
			if (Files.exists(file)) {
				byte[] content = Files.readAllBytes(file);
				if (content != null && content.length > 0) {
					Key key = CryptoUtil.getKey(TRANSFORM);

					Cipher cipher = Cipher.getInstance(TRANSFORM);
					cipher.init(Cipher.DECRYPT_MODE, key);

					byte[] encrypted = cipher.doFinal(content);
					String session = new String(encrypted);
					return Launcher.GSON.fromJson(session, t);
				}
			}
		} catch(JsonParseException ex) {
			Launcher.getLogger().warn("Could not parse encrypted file");
		} catch (Throwable e) {
			Launcher.onError(e);
		}
		return null;
	}
	
	// Generates a 256 bit AES key
	public static Key getKey(String transform) {
		String user = System.getProperty("user.name");
		long seed = user.length();
		for (char x : user.toCharArray()) {
			seed += x;
		}
		for (int i = 0; i < user.length(); i += 2) {
			seed |= user.charAt(i);
		}
		Random rand = new Random(seed);
		byte[] key = new byte[32];
		rand.nextBytes(key);
		return new SecretKeySpec(key, transform);
	}

}
