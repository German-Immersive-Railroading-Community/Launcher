package com.troblecodings.launcher.util;

import dev.dirs.ProjectDirectories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class LauncherPaths {
    private static final ProjectDirectories directories = ProjectDirectories.from("", "", "girc-launcher");

    private LauncherPaths() {
    }

    /**
     * Initialise directories if they don't exist.
     *
     * @throws IOException if an I/O error occurs.
     */
    public static void init() throws IOException {
        Files.createDirectories(getConfigDir());
        Files.createDirectories(getDataDir());
        Files.createDirectories(getLogsDir());
    }

    public static Path getConfigDir() {
        return Paths.get(directories.configDir);
    }

    public static Path getDataDir() {
        return Paths.get(directories.dataDir);
    }

    public static Path getDataLocalDir() {
        return Paths.get(directories.dataLocalDir);
    }

    public static Path getCacheDir() {
        return Paths.get(directories.cacheDir);
    }

    public static Path getLogsDir() {
        return getDataDir().resolve("logs");
    }

    public static Path getGameDir() {
        return getDataDir().resolve("minecraft");
    }

    public static Path getModsDir() {
        return getGameDir().resolve("mods");
    }

    public static Path getSettingsFilePath() {
        return getConfigDir().resolve("settings.json");
    }

    public static Path getSessionFilePath() {
        return getDataDir().resolve("session.json");
    }
}
