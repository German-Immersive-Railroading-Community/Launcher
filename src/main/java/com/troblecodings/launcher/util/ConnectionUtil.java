package com.troblecodings.launcher.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import com.troblecodings.launcher.ErrorDialog;

public class ConnectionUtil {

	public static final String URL = "";

	private static void addHeader(HttpURLConnection connection) {
		connection.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:64.0) Gecko/20100101 Firefox/64.0");
	}
	
	// Downloads a given file from the given URL onto the machine
	public static void download(String url, String name) throws Throwable {
		URL urlcon = new URL(url);

		Path pathtofile = Paths.get(name);
		if (!Files.exists(pathtofile)) {
			Path parent = pathtofile.getParent();
			if (parent != null)
				Files.createDirectories(parent);
			Files.createFile(pathtofile);
		}

		HttpURLConnection connection = (HttpURLConnection) urlcon.openConnection();
		addHeader(connection);
		if(connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
			String newUrl = connection.getHeaderField("Location");
			connection = (HttpURLConnection) new URL(newUrl).openConnection();
			addHeader(connection);
		}
		InputStream stream = connection.getInputStream();
		ReadableByteChannel rbc = Channels.newChannel(stream);
		FileOutputStream fos = new FileOutputStream(name);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

		stream.close();
		fos.close();
	}

	// Checks if the file exist and that its sha1 hash equals the given
	// returns true if all the checks pass
	public static boolean validate(String name, String sha1) throws Throwable {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		Path pathtofile = Paths.get(name);
		if (!Files.exists(pathtofile))
			return false;
		try (DigestInputStream stream = new DigestInputStream(Files.newInputStream(pathtofile), digest)) {
			while (true) {
				byte[] buffer = new byte[1028];
				if(stream.read(buffer, 0, 1028) <= 0) break;
			}
			byte[] digestreturn = digest.digest();
			BigInteger sha1bigintegers = new BigInteger(1, digestreturn);
			String sha1result = sha1bigintegers.toString(16);
			while (sha1result.length() < 40) {
				sha1result = "0" + sha1result;
			}
			return sha1result.equalsIgnoreCase(sha1);
		}
	}

	// This attempts to download a file if it isn't valid
	public static void validateDownloadRetry(String url, String name, String sha1) throws Throwable {
		byte times = 0;
		if(url.isEmpty()) {
			if(!ConnectionUtil.validate(name, sha1))
				throw new VerifyError("Couldn't verify " + name);
			return;
		}
		while (!ConnectionUtil.validate(name, sha1)) {
			ConnectionUtil.download(url, name);
			if (times == 5) {
				ErrorDialog.createDialog(new VerifyError("Couldn't verify " + name));
				break;
			}
			times++;
		}
	}

}
