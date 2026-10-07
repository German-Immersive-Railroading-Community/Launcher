package com.troblecodings.launcher.util;

import com.google.gson.Gson;
import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.models.CustomJavaSettings;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    private static Logger log;
    public static SettingsData SETTINGS = new SettingsData();
    public static String ASSET_DIR = null;
    public static String LIB_DIR = null;

    public static final Path SETTINGSPATH = LauncherPaths.getSettingsFilePath();

    private static String setCreateIfNotExists(String pathstr) {
        Path path = Paths.get(pathstr);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                Launcher.onError(e);
            }
        }
        return pathstr;
    }

    public static class SettingsData {

        public String baseDir = LauncherPaths.getDataDir().toString();
        public int width = 1280;
        public int height = 720;
        public int ram = 4096;
        public ArrayList<String> optionalMods = new ArrayList<>();
        public CustomJavaSettings javaSettings = new CustomJavaSettings("", "");
    }

    public static final Gson GSON = new Gson();

    public static void readSettings() {
        try {
            if (Files.exists(SETTINGSPATH)) {
                Reader reader = Files.newBufferedReader(SETTINGSPATH);
                SETTINGS = GSON.fromJson(reader, SettingsData.class);
                reader.close();
            } else {
                Files.createDirectories(SETTINGSPATH.getParent());
                Files.createFile(SETTINGSPATH);

                Writer writer = Files.newBufferedWriter(SETTINGSPATH);
                GSON.toJson(SETTINGS, writer);
                writer.close();
            }
        } catch (Exception e) {
            // TODO Error dialog
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        log.info("Writing settings to disk!");
        try {
            Writer writer = Files.newBufferedWriter(SETTINGSPATH);
            GSON.toJson(SETTINGS, writer);
            writer.close();
        } catch (Throwable e) {
            log.trace(e.getMessage(), e);
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // NOOP
    }

    public static void init() {
        ASSET_DIR = setCreateIfNotExists(SETTINGS.baseDir + "/assets");
        LIB_DIR = setCreateIfNotExists(SETTINGS.baseDir + "/libraries");
        log = LogManager.getLogger(FileUtil.class);
    }

    // Delete option files and mod, assets and libraries folder
    public static void resetFiles() {
        log.info("Reset requested.");

        try {
            FileUtils.delete(Paths.get(SETTINGS.baseDir + "/options.txt").toFile());
            FileUtils.delete(Paths.get(SETTINGS.baseDir + "/optionsof.txt").toFile());
            FileUtils.delete(Paths.get(SETTINGS.baseDir + "/GIR.json").toFile());
            FileUtils.deleteDirectory(Paths.get(SETTINGS.baseDir + "/mods").toFile());
            FileUtils.deleteDirectory(Paths.get(SETTINGS.baseDir + "/assets").toFile());
            FileUtils.deleteDirectory(Paths.get(SETTINGS.baseDir + "/libraries").toFile());
            FileUtils.deleteDirectory(Paths.get(SETTINGS.baseDir + "/config").toFile());
            FileUtil.init();

            log.info("Reset complete.");
        } catch (final IOException e) {
            log.error("Reset operation failed: ", e);
        }
    }

    /**
     * This method is to only be used for migration purposes, and to be removed in the next update.
     */
    @Deprecated
    public static void migrateOldDirectory() throws IOException {
        String appData = System.getenv("APPDATA");
        if (appData == null) {
            log.info("Not on Windows, skipping migration.");
            return;
        }

        Path oldPath = Paths.get(appData, "gir");
        if (!Files.exists(oldPath)) {
            log.info("%APPDATA%/gir does not exist, nothing to migrate.");
            return;
        }

        Path migrationStateFile = LauncherPaths.getDataDir().resolve(".did-migrate");
        if(Files.exists(migrationStateFile)) {
            log.info("Migration already occurred, nothing to migrate.");
            return;
        }

        // Skip 1, index 0 is the Roaming/gir/ directory file entry. We don't want to move that.
        try (Stream<Path> walk = Files.walk(oldPath).skip(1)) {
            Path dataDir = LauncherPaths.getDataDir();
            List<Path> files = walk.collect(Collectors.toList());

            for (Path p : files) {
                Path relPath = oldPath.relativize(p);
                Path newPath = dataDir.resolve(relPath);

                if (Files.isDirectory(p)) {
                    log.debug("Creating directory {} if it doesn't exist", relPath);
                    Files.createDirectories(newPath);
                } else {
                    log.debug("Migrating {} to new folder", relPath);
                    Files.copy(p, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        Files.createFile(migrationStateFile);
        log.info("Migration complete. You may now delete the old directory at your convenience.");
    }
}
