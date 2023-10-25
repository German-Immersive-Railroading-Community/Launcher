package eu.girc.launcher;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LauncherPaths {
    private static Path userHome;

    private static Path localAppData;

    private static Path appData;

    private static Path configDir;

    private static Path configFile;

    private static Path assetsDir;

    private static Path objectsDir;

    private static Path indexesDir;

    private static Path librariesDir;

    private static Path modsDir;

    private static Path authFile;

    // TODO: to be replaced by config.json
    private static Path settings;

    private LauncherPaths() { }

    public static void build() {
        userHome = SystemUtils.getUserHome().toPath();

        if (SystemUtils.IS_OS_WINDOWS) {
            localAppData = userHome.resolve("AppData").resolve("Local");
            appData = userHome.resolve("AppData").resolve("Roaming");
            configDir = appData.resolve("GIR-Launcher");
        } else if (SystemUtils.IS_OS_MAC) {
            // i believe local_app_data and app_data are identical on mac
            localAppData = appData = userHome.resolve("Library").resolve("Application Support");
            // tld.organization.application
            configDir = appData.resolve("eu.girc.Launcher");
        } else if (SystemUtils.IS_OS_LINUX) {
            localAppData = userHome.resolve(".local");
            appData = userHome.resolve(".config");
            configDir = appData.resolve("GIR-Launcher");
        } else {
            throw new RuntimeException("Unsupported OS: " + SystemUtils.OS_NAME);
        }

        configFile = configDir.resolve("config.json");
        assetsDir = configDir.resolve("assets");
        objectsDir = assetsDir.resolve("objects");
        indexesDir = assetsDir.resolve("indexes");
        librariesDir = configDir.resolve("libraries");
        modsDir = configDir.resolve("mods");
        authFile = configDir.resolve("ac.ce");
        settings = userHome.resolve(".launcher").resolve("settings.json");

        try {
            Files.createDirectories(configDir);
            Files.createDirectories(assetsDir);
            Files.createDirectories(librariesDir);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static Path getUserHome() {
        return userHome;
    }

    public static Path getLocalAppData() {
        return localAppData;
    }

    public static Path getAppData() {
        return appData;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static Path getConfigFile() {
        return configFile;
    }

    public static Path getAssetsDir() {
        return assetsDir;
    }

    public static Path getObjectsDir() {
        return objectsDir;
    }

    public static Path getIndexesDir() {
        return indexesDir;
    }

    public static Path getLibrariesDir() {
        return librariesDir;
    }

    public static Path getModsDir() {
        return modsDir;
    }

    public static Path getAuthFile() {
        return authFile;
    }

    public static Path getSettings() {
        return settings;
    }
}
