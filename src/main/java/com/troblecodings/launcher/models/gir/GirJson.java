package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class GirJson {
    @SerializedName("assetIndex")
    private AssetIndex assetIndex;
    
    @SerializedName("assets")
    private String assets;
    
    @SerializedName("downloads")
    private Downloads downloads;
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("time")
    private String time;
    
    @SerializedName("releaseTime")
    private String releaseTime;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("mainClass")
    private String mainClass;
    
    @SerializedName("inheritsFrom")
    private String inheritsFrom;
    
    @SerializedName("logging")
    private Map<String, Object> logging;
    
    @SerializedName("minecraftArguments")
    private String minecraftArguments;
    
    @SerializedName("additional")
    private AdditionalMods additional;
    
    @SerializedName("libraries")
    private List<Library> libraries;
    
    @SerializedName("wholeSize")
    private long wholeSize;
    
    @SerializedName("optionalMods")
    private List<ModInfo> optionalMods;

    public GirJson() {
    }

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

    public Downloads getDownloads() {
        return downloads;
    }

    public void setDownloads(Downloads downloads) {
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

    public Map<String, Object> getLogging() {
        return logging;
    }

    public void setLogging(Map<String, Object> logging) {
        this.logging = logging;
    }

    public String getMinecraftArguments() {
        return minecraftArguments;
    }

    public void setMinecraftArguments(String minecraftArguments) {
        this.minecraftArguments = minecraftArguments;
    }

    public AdditionalMods getAdditional() {
        return additional;
    }

    public void setAdditional(AdditionalMods additional) {
        this.additional = additional;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

    public long getWholeSize() {
        return wholeSize;
    }

    public void setWholeSize(long wholeSize) {
        this.wholeSize = wholeSize;
    }

    public List<ModInfo> getOptionalMods() {
        return optionalMods;
    }

    public void setOptionalMods(List<ModInfo> optionalMods) {
        this.optionalMods = optionalMods;
    }
}
