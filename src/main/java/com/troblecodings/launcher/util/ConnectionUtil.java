package com.troblecodings.launcher.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import com.troblecodings.launcher.Launcher;

public class ConnectionUtil {

    public static final String URL = "";

    private static void addHeader(HttpURLConnection connection) {
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
            if (resp == HttpURLConnection.HTTP_FORBIDDEN) {
                Launcher.onError(new Exception("Forbidden"));
                return false;
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
            Launcher.onError(e);
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
                Launcher.onError(e1);
            }
        }

        try (OutputStream fos = Files.newOutputStream(pathtofile, StandardOpenOption.CREATE)) {
            if (!openConnection(url, fos, update))
                return false;
        } catch (Exception e) {
            Launcher.onError(e);
        }
        Path normalFile = Paths.get(name);
        try {
            Files.move(pathtofile, normalFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Launcher.onError(e);
        }
        return true;
    }

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
                Launcher.onError(e);
            }
        } catch (NoSuchAlgorithmException e) {
            Launcher.onError(e);
        }
        return false;
    }

    public static void validateDownloadRetry(final String url, final String name, final String sha1) {
        validateDownloadRetry(url, name, sha1, null);
    }

    // This attempts to download a file if it isn't valid
    public static void validateDownloadRetry(final String url, final String name, final String sha1,
                                             final Consumer<Long> update) {
        byte times = 0;
        if (url.isEmpty()) {
            if (!ConnectionUtil.validate(name, sha1))
                throw new VerifyError("Couldn't verify " + name);
            return;
        }
        while (!ConnectionUtil.validate(name, sha1)) {
            if (times == 3) {
                Launcher.onError(new VerifyError("Couldn't verfiy file against sha1!"));
                break;
            }
            ConnectionUtil.download(url, name, update);
            times++;
        }
    }

}
