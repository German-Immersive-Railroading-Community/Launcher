package eu.girc.launcher.utils;

import java.nio.file.Path;

public final class Directories {
    private static final String os = System.getProperty("os.name").toLowerCase();

    private Directories() { }

    private static final Path homeDir = switch (os) {
        case "windows" -> Path.of(System.getProperty("USERPROFILE"));
        case "linux" -> Path.of(System.getenv("HOME"));
        default -> null;
    };

    private static final Path configDir = switch (os) {
        case "windows" -> homeDir.resolve("AppData").resolve("Roaming");
        case "linux" -> homeDir.resolve(".config");
        default -> null;
    };

    private static final Path dataDir = switch (os) {
        case "windows" -> homeDir.resolve("AppData").resolve("Roaming");
        case "linux" -> homeDir.resolve(".local").resolve("share");
        default -> null;
    };

    /**
     * @return a path representing the current users' home directory; or null.
     */
    public static Path getHomeDir() {
        return homeDir;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static Path getDataDir() {
        return dataDir;
    }
}
