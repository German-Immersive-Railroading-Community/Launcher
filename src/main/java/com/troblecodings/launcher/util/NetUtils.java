package com.troblecodings.launcher.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import com.troblecodings.launcher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

public final class NetUtils {
    private static final Logger logger = LogManager.getLogger();

    private static final HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).followRedirects(Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(5)).build();

    private NetUtils() { }

    /**
     * Downloads a String resources from the provided {@link java.net.URI URI}.
     *
     * @param uri The {@link java.net.URI URI} to make the request to.
     * @return String body if successful; empty otherwise.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the web request is interrupted.
     */
    public static Optional<String> downloadString(final URI uri) throws IOException, InterruptedException {
        logger.debug("Downloading String resource from {}", uri);
        HttpRequest req = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            return Optional.empty();
        }

        return Optional.of(res.body());
    }

    public static <T> Optional<T> downloadJson(final URI uri, Class<T> clazz) throws IOException, InterruptedException {
        logger.debug("Downloading JSON resource from {}", uri);
        HttpRequest req = HttpRequest.newBuilder(uri).GET().header("Content-Type", "application/json").build();

        HttpResponse<String> res = client.send(req, BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            return Optional.empty();
        }

        return Optional.of(Launcher.GSON.fromJson(res.body(), clazz));
    }

    public static <T> Optional<T> downloadJson(final URI uri, TypeToken<T> clazz) throws IOException, InterruptedException {
        logger.debug("Downloading JSON resource from {}", uri);
        HttpRequest req = HttpRequest.newBuilder(uri).GET().header("Content-Type", "application/json").build();

        HttpResponse<String> res = client.send(req, BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            return Optional.empty();
        }

        return Optional.of(Launcher.GSON.fromJson(res.body(), clazz));
    }

    public static Optional<Path> downloadFile(final URI uri, final Path path) throws IOException, InterruptedException {
        logger.debug("Downloading file resource from {}", uri);
        HttpRequest req = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<Path> res = client.send(req, BodyHandlers.ofFile(path));

        if (res.statusCode() != 200) {
            return Optional.empty();
        }

        return Optional.of(res.body());
    }

    public static Optional<Path> downloadFileIfNotExist(final URI uri, final Path path) throws IOException, InterruptedException {
        logger.debug("Checking if {} exists", path.getFileName());
        if (path.toFile().exists()) {
            logger.debug("File exists, returning");
            return Optional.of(path);
        }

        logger.debug("File does not exist, downloading");
        return downloadFile(uri, path);
    }

    public static Optional<Path> validateOrDownloadSha1(final URI uri, final Path path, final String sha1) throws IOException, InterruptedException {

        logger.debug("Validating if {} exists and SHA-1 matches", path);
        if (!validateSha1(sha1, path)) {
            logger.debug("Failed validation, deleting old file");
            path.toFile().delete();
            return downloadFile(uri, path);
        }

        logger.debug("Succeeded validation");
        return Optional.of(path);
    }

    public static Optional<Path> validateOrDownloadSha256(final URI uri, final Path path, final String sha256) throws IOException, InterruptedException {
        logger.debug("Validating if {} exists and SHA-256 matches", path);
        if (!validateSha256(sha256, path)) {
            logger.debug("Failed validation, deleting old file");
            path.toFile().delete();
            return downloadFile(uri, path);
        }

        logger.debug("Succeeded validation");
        return Optional.of(path);
    }

    @SuppressWarnings("deprecation")
    public static boolean validateSha1(String expectedSha1, Path filePath) throws IOException {
        final File file = filePath.toFile();
        if (!file.exists()) {
            return false;
        }

        final HashCode sha1Hc = Hashing.sha1().hashBytes(Files.toByteArray(filePath.toFile()));

        final String providedSha1 = sha1Hc.toString();
        logger.debug("Expected SHA-1: {}; Provided SHA-1: {}", expectedSha1, providedSha1);
        return expectedSha1.equals(providedSha1);
    }

    public static boolean validateSha256(String expectedSha256, Path filePath) throws IOException {
        final File file = filePath.toFile();
        if (!file.exists()) {
            return false;
        }

        final HashCode sha256Hc = Hashing.sha256().hashBytes(Files.toByteArray(filePath.toFile()));

        final String providedSha256 = sha256Hc.toString();
        logger.debug("Expected SHA-256: {}; Provided SHA-256: {}", providedSha256, providedSha256);
        return expectedSha256.equals(providedSha256);
    }
}