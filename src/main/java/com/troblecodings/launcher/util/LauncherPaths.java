package com.troblecodings.launcher.util;

import dev.dirs.ProjectDirectories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LauncherPaths {
    private static ProjectDirectories projectDirs;

    private static Path configDir;

    private static Path dataDir;

    private static Path cacheDir;

    private static Path settingsFile;

    private static Path logsDir;

    private LauncherPaths() {

    }

    public static void init() throws IOException {
        // Instantiate project dir information, details on the directories here:
        // https://codeberg.org/dirs/directories-jvm
        projectDirs = ProjectDirectories.from("eu", "girc", "GIRC-Launcher");

        // Set up base directories
        configDir = Path.of(projectDirs.configDir);
        dataDir = Path.of(projectDirs.dataDir);
        cacheDir = Path.of(projectDirs.cacheDir);

        settingsFile = configDir.resolve("settings.json");
        logsDir = dataDir.resolve("logs");

        Files.createDirectories(configDir);
        Files.createDirectories(logsDir);
    }

    public static ProjectDirectories getProjectDirs() {
        return projectDirs;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static Path getDataDir() {
        return dataDir;
    }

    public static Path getCacheDir() {
        return cacheDir;
    }

    /**
     * Points to where the application settings in JSON format are stored.
     * @return The path to the settings.json file.
     */
    public static Path getSettingsFile() {
        return settingsFile;
    }

    /**
     * Points to the directory where launcher logs are stored.
     * @return The path to the launcher logs inside the project data dir.
     */
    public static Path getLogsDir() {
        return logsDir;
    }
}
