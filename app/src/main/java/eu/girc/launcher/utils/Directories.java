package eu.girc.launcher.utils;

import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.util.Objects;

public final class Directories {
    private static final String os = Objects.requireNonNullElse(SystemUtils.OS_NAME, "").toLowerCase().substring(0, 3);

    private Directories() { }

    private static final Path homeDir = Path.of(SystemProperties.getUserHome());

    private static final Path configDir = switch (os) {
        case "win" -> homeDir.resolve("AppData").resolve("Roaming");
        case "lin" -> homeDir.resolve(".config");
        default -> null;
    };

    private static final Path dataDir = switch (os) {
        case "win" -> homeDir.resolve("AppData").resolve("Roaming");
        case "lin" -> homeDir.resolve(".local").resolve("share");
        default -> null;
    };

    /**
     * @return a path representing the current users' home directory; or null.
     */
    public static Path getUserHomeDir() {
        return homeDir;
    }

    public static Path getUserCfgDir() {
        return configDir;
    }

    public static Path getUserDataDir() {
        return dataDir;
    }
}
