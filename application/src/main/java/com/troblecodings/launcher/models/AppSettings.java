package com.troblecodings.launcher.models;

import java.util.ArrayList;
import java.util.List;

public final class AppSettings {
    private int width = 1280;
    private int height = 720;
    // in MB (Megabyte) 1 MB = 1000 KB; 1 GB = 1000 MB
    private int memory = 4000;

    private boolean appUpdatesEnabled = true;

    private String customJrePath = "";

    private final List<String> optionalMods = new ArrayList<>();

    public AppSettings() {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Memory in MB (Megabyte) 1 MB = 1000 KB; 1 GB = 1000 MB
     * @return Memory in MB (Megabyte)
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Memory in MB (Megabyte) 1 MB = 1000 KB; 1 GB = 1000 MB
     * @param memory Memory in MB (Megabyte)
     */
    public void setMemory(int memory) {
        this.memory = memory;
    }

    public boolean isAppUpdatesEnabled() {
        return appUpdatesEnabled;
    }

    public void setAppUpdatesEnabled(boolean appUpdatesEnabled) {
        this.appUpdatesEnabled = appUpdatesEnabled;
    }

    public String getCustomJrePath() {
        return customJrePath;
    }

    public void setCustomJrePath(String customJrePath) {
        this.customJrePath = customJrePath;
    }

    public List<String> getOptionalMods() {
        return optionalMods;
    }

    public void addOptionalMod(String mod) {
        optionalMods.add(mod);
    }
}
