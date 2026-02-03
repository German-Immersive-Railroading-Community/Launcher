package com.troblecodings.launcher.models.gir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GirJson {
    private AssetIndex assetIndex;

    private String assets;

    private Map<String, MojangDl> downloads = new HashMap<>();

    private String id;

    private String time;

    private String releaseTime;

    private String type;

    private String mainClass;

    private String inheritsFrom;

    // private <type> logging TODO: logging is empty in gir.json, what is this used for?

    private String minecraftArguments;

    private Map<String, List<ModDl>> additional = new HashMap<>();

    private List<LibraryInfo> libraries = new ArrayList<>();

    private long wholeSize;

    private List<ModDl> optionalMods = new ArrayList<>();


    public AssetIndex getAssetIndex() {
        return assetIndex;
    }

    public void setAssetIndex(AssetIndex assetIndex) {
        this.assetIndex = assetIndex;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public Map<String, MojangDl> getDownloads() {
        return downloads;
    }

    public void setDownloads(Map<String, MojangDl> downloads) {
        this.downloads = downloads;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getInheritsFrom() {
        return inheritsFrom;
    }

    public void setInheritsFrom(String inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public String getMinecraftArguments() {
        return minecraftArguments;
    }

    public void setMinecraftArguments(String minecraftArguments) {
        this.minecraftArguments = minecraftArguments;
    }

    public Map<String, List<ModDl>> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, List<ModDl>> additional) {
        this.additional = additional;
    }

    public List<LibraryInfo> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<LibraryInfo> libraries) {
        this.libraries = libraries;
    }

    public long getWholeSize() {
        return wholeSize;
    }

    public void setWholeSize(long wholeSize) {
        this.wholeSize = wholeSize;
    }

    public List<ModDl> getOptionalMods() {
        return optionalMods;
    }

    public void setOptionalMods(List<ModDl> optionalMods) {
        this.optionalMods = optionalMods;
    }
}
