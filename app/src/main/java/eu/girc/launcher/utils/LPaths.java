package eu.girc.launcher.utils;

import eu.girc.launcher.utils.Directories;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LPaths {
    private static final String packageName = "eu.girc.launcher";

    private static final Path configDir = Directories.getConfigDir().resolve(packageName);

    private static final Path dataDir = Directories.getDataDir().resolve(packageName);

    private static final Path logsPath = configDir.resolve("logs");

    private static final Path settingsPath = configDir.resolve("settings.json");

    // The path where minecraft and all the mods are downloaded to.
    // This directory will also contain the resourcepacks, shaderpacks, etc. folders.
    private static final Path minecraftPath = dataDir.resolve("minecraft");

    public static void ensureDirsCreated() throws IOException {
        Files.createDirectories(configDir);
        Files.createDirectories(dataDir);
        Files.createDirectories(logsPath);
        Files.createDirectories(minecraftPath);
    }

    public static Path getConfigDir() { return configDir; }

    public static Path getDataDir() { return dataDir; }

    public static Path getLogsPath() { return logsPath; }

    public static Path getSettingsPath() { return settingsPath; }

    public static Path getMinecraftPath() { return minecraftPath; }
}
