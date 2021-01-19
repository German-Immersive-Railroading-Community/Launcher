package com.troblecodings.launcher.util;

import java.security.Key;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
	
	// Generates a 256 bit AES key
	public static Key getKey(String transform) {
		Runtime run =  Runtime.getRuntime();
		long seed = run.availableProcessors();
		String user = System.getProperty("user.name");
		seed *= user.length();
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
