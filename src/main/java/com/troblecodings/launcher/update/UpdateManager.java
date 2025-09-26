package com.troblecodings.launcher.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.troblecodings.launcher.GirBuildConfig;
import com.troblecodings.launcher.Version;
import com.troblecodings.launcher.util.ConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class UpdateManager {
    private static final String GH_RELEASE_API_URL = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases";

    private static final Logger LOGGER = LogManager.getLogger(UpdateManager.class);

    private UpdateManager() {
    }

    public static boolean tryUpdate() {
        //noinspection ConstantValue
        if (GirBuildConfig.VERSION.endsWith("-dev")) {
            LOGGER.info("Development build, skipping update.");
            return true;
        }

        LOGGER.info("Searching for updates...");

        String releases = ConnectionUtil.getStringFromURL(GH_RELEASE_API_URL);
        if (releases == null || releases.isEmpty()) {
            LOGGER.warn("No releases were returned from API.");
            return false;
        }

        // All json code matches https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28
        JsonElement jsonElement = JsonParser.parseString(releases);
        JsonArray allReleases = jsonElement.getAsJsonArray();
        JsonObject latestRelease = allReleases.get(0).getAsJsonObject();

        String lastTag = latestRelease.get("tag_name").getAsString();
        boolean isDraft = latestRelease.get("draft").getAsBoolean();
        boolean isPreRelease = latestRelease.get("prerelease").getAsBoolean();

        Version releaseVersion = Version.parse(lastTag);

        LOGGER.info("Latest release: {} (Draft: {}, Pre-Release: {})", releaseVersion, isDraft, isPreRelease);

        if (releaseVersion.isLessThan(Version.parse(GirBuildConfig.VERSION))) {
            LOGGER.info("Already on latest version.");
            return true;
        }

        if (isDraft) {
            LOGGER.info("Last release is a draft release, skipping update.");
            return false;
        }

        // FIXME: Handle beta updates via opt-in pre-releases here, for now return
        if (isPreRelease) {
            LOGGER.info("Last release is a pre-release, skipping update.");
            return false;
        }

        Path parentDir;

        try {
            parentDir = Paths.get(UpdateManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (final URISyntaxException e) {
            LOGGER.error("Could not locate the current launcher location.", e);
            return false;
        }

        switch (releaseVersion.getMajor()) {
            case 1:
                return updateV1(latestRelease, parentDir);
            case 2:
                return updateV2(latestRelease, parentDir);
            default:
                LOGGER.warn("No update method for new major version: {}", releaseVersion.getMajor());
                return false;
        }
    }

    private static boolean updateV1(JsonObject latestRelease, Path parentDir) {
        JsonArray assets = latestRelease.getAsJsonArray("assets");

        for (JsonElement asset : assets.asList()) {
            JsonObject assetObject = asset.getAsJsonObject();
            String downloadUrl = assetObject.get("browser_download_url").getAsString();
            String name = assetObject.get("name").getAsString();
            long size = assetObject.get("size").getAsLong();
            Path path = Paths.get(parentDir.toString(), name);

            if (path.toFile().exists()) {
                LOGGER.info("Asset {} is already downloaded, skipping download.", name);
            } else {
                try (OutputStream stream = Files.newOutputStream(path)) {
                    LOGGER.info("Downloading {} to {}...", name, path);
                    ProgressMonitor progress = new ProgressMonitor(new JButton(), String.format("Downloading new Version %s!", name), "", 0, (int) size);

                    if (!ConnectionUtil.openConnection(downloadUrl, stream, bytesize -> progress.setProgress(bytesize.intValue()))) {
                        return false;
                    }

                    LOGGER.info("Downloaded {} to {}.", name, path);
                } catch (final IOException ioe) {
                    LOGGER.error("Failed downloading a new version.", ioe);
                    return false;
                }
            }

            LOGGER.info("Starting new version...");
            LogManager.shutdown(false, true);

            try {
                ProcessBuilder builder = new ProcessBuilder("java", "-jar", path.toString());
                builder.redirectError(ProcessBuilder.Redirect.INHERIT);
                builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                System.exit(builder.start().waitFor());
            } catch (final IOException | InterruptedException ie) {
                LOGGER.error("Error during start of new Launcher.", ie);
                return false;
            }
        }

        return true;
    }

    private static boolean updateV2(JsonObject latestRelease, Path parentDir) {
        LOGGER.warn("Update V2 currently unimplemented.");
        return true;
    }
}