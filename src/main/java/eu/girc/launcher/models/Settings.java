package eu.girc.launcher.models;

import java.util.List;

@SuppressWarnings("unused")
public class Settings {
    private int width;
    private int height;
    private int ram;
    private List<String> optionalMods;
    private List<String> activatedMods;

    public Settings(int width, int height, int ram, List<String> optionalMods, List<String> activatedMods) {
        this.width = width;
        this.height = height;
        this.ram = ram;
        this.optionalMods = optionalMods;
        this.activatedMods = activatedMods;
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

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public List<String> getOptionalMods() {
        return optionalMods;
    }

    public void setOptionalMods(List<String> optionalMods) {
        this.optionalMods = optionalMods;
    }

    public List<String> getActivatedMods() {
        return activatedMods;
    }

    public void setActivatedMods(List<String> activatedMods) {
        this.activatedMods = activatedMods;
    }
}
