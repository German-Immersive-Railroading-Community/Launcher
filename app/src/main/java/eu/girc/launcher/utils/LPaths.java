package eu.girc.launcher.utils;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LPaths {
    private static Path userHome;

    private static Path basePath;

    private static Path logsPath;
    
    private static Path settingsPath;
    
    // The path where minecraft and all the mods are downloaded to.
    // This directory will also contain the resourcepacks, shaderpacks, etc. folders.
    private static Path minecraftPath;

    public static void initialise() {
        userHome = SystemUtils.getUserHome().toPath();

        if (SystemUtils.IS_OS_WINDOWS) {
            basePath = userHome.resolve("AppData").resolve("Roaming").resolve("eu.girc.launcher");
        } else {
            throw new UnsupportedOperationException("The current operating system is not supported.");
        }
        
        logsPath = basePath.resolve("logs");
        settingsPath = basePath.resolve("settings.json");
        minecraftPath = basePath.resolve("minecraft");
    }
    
    public static void ensureDirsCreated() throws IOException {
        Files.createDirectories(basePath);
        Files.createDirectories(logsPath);
        Files.createDirectories(minecraftPath);
    }

    /**
     * Gets the user home directory.
     * This will be <code>C:\Users\[USERNAME]\</code> on Windows, and
     * <code>/home/[USERNAME]/</code> on Linux/Unix.
     * @return the user home directory.
     */
    public static Path getUserHome() { return userHome; }

    public static Path getBasePath() { return basePath; }
    
    public static Path getLogsPath() { return logsPath; }

    public static Path getSettingsPath() { return settingsPath; }
    
    public static Path getMinecraftPath() { return minecraftPath; }
}
