package com.troblecodings.launcher.util;

import com.google.gson.Gson;
import com.troblecodings.launcher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

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

    public static boolean moveBaseDir(String file) {
        Path ptof = Paths.get(file);
        if (Files.notExists(ptof) || !Files.isDirectory(ptof))
            return false;

        Path old = Paths.get(SETTINGS.baseDir);
        if (ptof.equals(old))
            return false;

        SETTINGS.baseDir = file;

        try { // Why? WHY? Let me disable Exceptions pls
            Files.walk(old).forEach(pt -> {
                try { // I really hate this language ... I mean ... really
                    Path newpth = Paths.get(pt.toString().replace(old.toString(), file));
                    if (Files.isDirectory(pt)) {
                        Files.createDirectories(newpth);
                        return;
                    }
                    Files.move(pt, newpth, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    Launcher.onError(e);
                    // I fucking don't care if this fails
                }
            });
            Files.walk(old).sorted((c1, c2) -> {
                int c1l = c1.toString().length();
                int c2l = c2.toString().length();
                return Integer.compare(c2l, c1l);
            }).forEach(p -> {
                File pFile = p.toFile();
                if (!pFile.exists()) return;
                if (!pFile.delete()) {
                    log.warn("Could not delete file {}", p);
                }

                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    Launcher.onError(e);
                    // I fucking don't care if this fails
                }
            });
        } catch (IOException e) {
            Launcher.onError(e);
            return false;
        }
        init();
        return true;
    }

    public static class SettingsData {

        public String baseDir = LauncherPaths.getDataDir().toString();
        public int width = 1280;
        public int height = 720;
        public int ram = 4096;
        public ArrayList<String> optionalMods = new ArrayList<>();
        public String javaPath = "";
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
        log.info("Started launcher reset!");
        deleteFile(Paths.get(SETTINGS.baseDir + "/options.txt").toFile());
        deleteFile(Paths.get(SETTINGS.baseDir + "/optionsof.txt").toFile());
        deleteFile(Paths.get(SETTINGS.baseDir + "/GIR.json").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/mods").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/assets").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/libraries").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/config").toFile());
        FileUtil.init();
        log.info("Finished launcher reset!");
    }

    private static void deleteDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            directory.delete();
        }
    }

    private static void deleteFile(File file) {
        if (file != null && file.exists() && !file.isDirectory()) {
            file.delete();
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

        Files.move(oldPath, LauncherPaths.getDataDir(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.COPY_ATTRIBUTES);
        log.info("Migrated old directory to new location.");
    }
}
