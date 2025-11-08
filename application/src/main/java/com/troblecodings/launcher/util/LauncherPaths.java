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

    private static Path logsDir;

    private static Path gameDataDir;

    private static Path assetsDir;

    private static Path librariesDir;

    private static Path jreDir;

    private static Path settingsFile;

    private static Path sessionFile;

    private static Path girJsonFile;

    private static Path windowStateFile;

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
        logsDir = dataDir.resolve("logs");
        gameDataDir = dataDir.resolve("game");
        assetsDir = gameDataDir.resolve("assets");
        librariesDir = gameDataDir.resolve("libraries");
        jreDir = dataDir.resolve("jre");

        settingsFile = configDir.resolve("settings.json");
        sessionFile = dataDir.resolve("session.json");
        girJsonFile = dataDir.resolve("GIR.json");
        windowStateFile = cacheDir.resolve("state.json");

        Files.createDirectories(configDir);
        Files.createDirectories(cacheDir);
        Files.createDirectories(logsDir);
        Files.createDirectories(gameDataDir);
        Files.createDirectories(assetsDir);
        Files.createDirectories(librariesDir);
        Files.createDirectories(jreDir);
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
     * Points to the directory where launcher logs are stored.
     *
     * @return The path to the launcher logs inside the project data dir.
     */
    public static Path getLogsDir() {
        return logsDir;
    }

    public static Path getGameDataDir() {
        return gameDataDir;
    }

    public static Path getAssetsDir() {
        return assetsDir;
    }

    public static Path getLibrariesDir() {
        return librariesDir;
    }

    public static Path getJreDir() {
        return jreDir;
    }

    /**
     * Points to where the application settings in JSON format are stored.
     *
     * @return The path to the settings.json file.
     */
    public static Path getSettingsFile() {
        return settingsFile;
    }

    public static Path getSessionFile() {
        return sessionFile;
    }

    public static Path getGirJsonFile() {
        return girJsonFile;
    }

    public static Path getWindowStateFile() {
        return windowStateFile;
    }
}
