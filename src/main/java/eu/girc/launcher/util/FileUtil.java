package eu.girc.launcher.util;

import com.google.gson.Gson;
import eu.girc.launcher.Launcher;
import eu.girc.launcher.LauncherPaths;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    // This way, the configuration files are stored identically across Windows and
    // Unix distros
    public static final Gson GSON = new Gson();

    public static SettingsData SETTINGS = new SettingsData();

    public static String ASSET_DIR = null;

    public static String LIB_DIR = null;

    public static boolean moveBaseDir(String file) {
        Path ptof = Paths.get(file);
        if (Files.notExists(ptof) || !Files.isDirectory(ptof)) return false;

        Path old = Paths.get(SETTINGS.baseDir);
        if (ptof.equals(old)) return false;

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
        return true;
    }

    public static void readSettings() {
        try {
            if (Files.exists(LauncherPaths.getSettings())) {
                Reader reader = Files.newBufferedReader(LauncherPaths.getSettings());
                SETTINGS = GSON.fromJson(reader, SettingsData.class);
                reader.close();
            } else {
                Files.createDirectories(LauncherPaths.getSettings().getParent());
                Files.createFile(LauncherPaths.getSettings());

                Writer writer = Files.newBufferedWriter(LauncherPaths.getSettings());
                GSON.toJson(SETTINGS, writer);
                writer.close();
            }
        } catch (final Exception e) {
            // TODO Error dialog
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        Launcher.getLogger().info("Save Settings!");
        try {
            Writer writer = Files.newBufferedWriter(LauncherPaths.getSettings());
            GSON.toJson(SETTINGS, writer);
            writer.close();
        } catch (Throwable e) {
            Launcher.getLogger().trace(e.getMessage(), e);
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // NOOP
    }

    // Delete option files and mod, assets and libraries folder
    public static void resetFiles() {
        Launcher.getLogger().info("Started launcher reset!");
        deleteFile(Paths.get(SETTINGS.baseDir + "/options.txt").toFile());
        deleteFile(Paths.get(SETTINGS.baseDir + "/optionsof.txt").toFile());
        deleteFile(Paths.get(SETTINGS.baseDir + "/GIR.json").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/mods").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/assets").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/libraries").toFile());
        deleteDirectory(Paths.get(SETTINGS.baseDir + "/config").toFile());
        LauncherPaths.build();
        Launcher.getLogger().info("Finished launcher reset!");
    }

    private static void deleteDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
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

    public static class SettingsData {
        public String baseDir = LauncherPaths.getConfigDir().toString();

        public int width = 1280;

        public int height = 720;

        public int ram = 4096;

        public ArrayList<String> optionalMods = new ArrayList<>();

        public String javaPath = "";

        public List<String> activatedMods = new ArrayList<>();
    }
}
