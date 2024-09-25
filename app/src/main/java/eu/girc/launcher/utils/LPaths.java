package eu.girc.launcher.utils;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LPaths {
    private static Path userHome;

    private static Path configDir;

    private static Path dataDir;

    private static Path logsPath;

    private static Path settingsPath;

    // The path where minecraft and all the mods are downloaded to.
    // This directory will also contain the resourcepacks, shaderpacks, etc. folders.
    private static Path minecraftPath;

    public static void initialise() {
        userHome = SystemUtils.getUserHome().toPath();

        if (SystemUtils.IS_OS_WINDOWS) {
            configDir = userHome.resolve("AppData").resolve("Roaming").resolve("eu.girc.launcher");
            dataDir = configDir;
        } else if (SystemUtils.IS_OS_LINUX) {
            configDir = userHome.resolve(".config").resolve("eu.girc.launcher");
            dataDir = userHome.resolve(".local").resolve("share").resolve("eu.girc.launcher");
        } else {
            throw new UnsupportedOperationException("The current operating system is not supported.");
        }

        logsPath = configDir.resolve("logs");
        settingsPath = configDir.resolve("settings.json");
        minecraftPath = dataDir.resolve("minecraft");
    }

    public static void ensureDirsCreated() throws IOException {
        Files.createDirectories(configDir);
        Files.createDirectories(dataDir);
        Files.createDirectories(logsPath);
        Files.createDirectories(minecraftPath);
    }

    /**
     * Gets the user home directory.
     * This will be <code>C:\Users\[USERNAME]\</code> on Windows, and
     * <code>/home/[USERNAME]/</code> on Linux/Unix.
     *
     * @return the user home directory.
     */
    public static Path getUserHome() { return userHome; }

    public static Path getConfigDir() { return configDir; }

    public static Path getDataDir() { return dataDir; }

    public static Path getLogsPath() { return logsPath; }

    public static Path getSettingsPath() { return settingsPath; }

    public static Path getMinecraftPath() { return minecraftPath; }
}
