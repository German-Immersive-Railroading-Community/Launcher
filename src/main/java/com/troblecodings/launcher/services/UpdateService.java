package com.troblecodings.launcher.services;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.models.github.GitHubAsset;
import com.troblecodings.launcher.models.github.GitHubRelease;
import com.troblecodings.launcher.util.ConnectionUtil;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.Version;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UpdateService {
    private static final Logger log = LogManager.getLogger(UpdateService.class);

    private static final String releases_uri = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases";
    private static final String latest_release_uri = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases/latest";

    private static final Pattern version_regex = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+)");

    private Version latestVersion;
    private GitHubRelease cachedRelease;

    public UpdateService() {
    }

    /**
     * Checks for available updates against GitHub's api.
     *
     * @return true if an update is available; false otherwise.
     */
    public boolean checkForUpdates() {
        log.info("Checking for updates...");

        if (isDevelopmentBuild()) {
            log.info("Detected development build, skipping update check.");
            return false;
        }

        String releaseContentJson = ConnectionUtil.getStringFromURL(latest_release_uri);
        cachedRelease = FileUtil.GSON.fromJson(releaseContentJson, GitHubRelease.class);

        Matcher matcher = version_regex.matcher(cachedRelease.getTagName());
        if (!matcher.find()) {
            log.warn("Failed to retrieve version information from GitHub Api request.");
            return false;
        }

        latestVersion = new Version(matcher.group());
        if (!latestVersion.isNewerThan(Launcher.VERSION)) {
            log.info("No new updates available.");
            return false;
        }

        log.info("New updates are available!");
        return true;
    }

    public void doUpdate() throws IOException, URISyntaxException, InterruptedException {
        if (isDevelopmentBuild()) {
            log.info("Detected development build, skipping update process.");
            return;
        }

        if (cachedRelease == null || latestVersion == null) {
            checkForUpdates();
        }

        if (latestVersion.equals(Launcher.VERSION)) {
            log.info("Already on latest version, nothing to do.");
            return;
        }

        log.info("Commencing update to v{}.", cachedRelease.getTagName());
        Optional<GitHubAsset> assetOpt = cachedRelease.getAssets().stream().filter(a -> a.getName().equals("Launcher-" + latestVersion.toString() + "-jar-with-dependencies.jar")).findFirst();

        if (!assetOpt.isPresent()) {
            log.warn("Couldn't find Launcher asset in release! Aborting.");
            return;
        }

        GitHubAsset asset = assetOpt.get();

        ProgressMonitor progress = new ProgressMonitor(new JButton(), "Downloading update!", "", 0, (int) asset.getSize());
        Path launcherPath = Paths.get(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path tempLauncherPath = Paths.get(launcherPath + ".tmp");
        try (OutputStream stream = Files.newOutputStream(tempLauncherPath)) {
            if (!ConnectionUtil.openConnection(asset.getBrowserDownloadUrl(), stream, downloadedSize -> progress.setProgress(downloadedSize.intValue()))) {
                log.error("Error when downloading new Launcher version!");
                return;
            }
        }

        // TODO: Maybe ask the user if they want to restart
        Files.move(tempLauncherPath, launcherPath, StandardCopyOption.REPLACE_EXISTING);
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", launcherPath.toString());
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Platform.exit();
        System.exit(builder.start().waitFor());
    }

    // When running from IDE, the class is loaded from file system, not JAR
    // The protocol equals "jar" when packaged, and "file" when running from the file system.
    private boolean isDevelopmentBuild() {
        String protocol = Objects.requireNonNull(Launcher.class.getResource("Launcher.class")).getProtocol();
        return "file".equals(protocol);
    }
}
