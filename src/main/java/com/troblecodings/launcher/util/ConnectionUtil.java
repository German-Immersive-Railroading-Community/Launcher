package com.troblecodings.launcher.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.troblecodings.launcher.ErrorPart;
import com.troblecodings.launcher.Launcher;

public class ConnectionUtil {

	public static final String URL = "";

	private static void addHeader(HttpURLConnection connection) {
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:64.0) Gecko/20100101 Firefox/64.0");
	}

	public static boolean openConnection(final String url, final OutputStream channel) {
		return openConnection(url, channel, null);
	}

	public static boolean openConnection(final String url, final OutputStream channel, final Consumer<Long> update) {
		try {
			URL urlcon = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) urlcon.openConnection();
			addHeader(connection);
			int resp = connection.getResponseCode();
			if (resp == HttpURLConnection.HTTP_MOVED_PERM || resp == HttpURLConnection.HTTP_MOVED_TEMP
					|| resp == HttpURLConnection.HTTP_SEE_OTHER) {
				String newUrl = connection.getHeaderField("Location");
				connection = (HttpURLConnection) new URL(newUrl).openConnection();
				addHeader(connection);
			}
			InputStream stream = connection.getInputStream();
			byte[] buf = new byte[8192];
			int length = 0;
			long bytesread = 0;
			while ((length = stream.read(buf)) > 0) {
				channel.write(buf, 0, length);
				if (update != null) {
					bytesread += length;
					update.accept(bytesread);
				}
			}
			stream.close();
			return true;
		} catch (Exception e) {
			if(e instanceof ConnectException || e instanceof SocketTimeoutException)
				Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "Connection error!", "No connection could be established!"));
			else if(e instanceof MalformedURLException)
				Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "URL error!", "The URL was mallformed!"));
			else if(e instanceof UnknownHostException)
				Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "Couldn't resolve host " + e.getMessage(), "Are you connected? No connection could be established!"));
			e.printStackTrace();
			return false;
		}
	}

	public static String getStringFromURL(String url) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		if (!openConnection(url, output))
			return null;
		return new String(output.toByteArray());
	}

	// Downloads a given file from the given URL onto the machine
	public static boolean download(String url, String name) {
		return download(url, name, null);
	}

	public static boolean download(String url, String name, final Consumer<Long> update) {
		Path pathtofile = Paths.get(name + ".tmp");
		Path parent = pathtofile.getParent();
		if (parent != null) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try (OutputStream fos = Files.newOutputStream(pathtofile)) {
			if(!openConnection(url, fos, update))
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Path normalFile = Paths.get(name);
		try {
			Files.move(pathtofile, normalFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static ExecutorService executors = Executors.newCachedThreadPool();

	// Checks if the file exist and that its sha1 hash equals the given
	// returns true if all the checks pass
	public static boolean validate(String name, String sha1) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			Path pathtofile = Paths.get(name);
			if (!Files.exists(pathtofile))
				return false;
			try (DigestInputStream stream = new DigestInputStream(Files.newInputStream(pathtofile), digest)) {
				while (true) {
					byte[] buffer = new byte[8192];
					if (stream.read(buffer, 0, 8192) <= 0)
						break;
				}
				byte[] digestreturn = digest.digest();
				BigInteger sha1bigintegers = new BigInteger(1, digestreturn);
				String sha1result = sha1bigintegers.toString(16);
				while (sha1result.length() < 40) {
					sha1result = "0" + sha1result;
				}
				return sha1result.equalsIgnoreCase(sha1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	// This attempts to download a file if it isn't valid
	public static void validateDownloadRetry(String url, String name, String sha1) {
		executors.submit(() -> {
			byte times = 0;
			if (url.isEmpty()) {
				if (!ConnectionUtil.validate(name, sha1))
					throw new VerifyError("Couldn't verify " + name);
				return;
			}
			while (!ConnectionUtil.validate(name, sha1)) {
				if (times == 3) {
					Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "Error verifying " + Paths.get(name).getFileName().toString(), "The file failed to download correctly after 3 tries!"));
					break;
				}
				ConnectionUtil.download(url, name);
				times++;
			}
		});
	}

}
