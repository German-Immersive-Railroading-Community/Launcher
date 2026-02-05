package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class ModInfo {
    @SerializedName("name")
    private String name;
    
    @SerializedName("url")
    private String url;
    
    @SerializedName("sha1")
    private String sha1;
    
    @SerializedName("sha256")
    private String sha256;
    
    @SerializedName("size")
    private long size;

    public ModInfo() {
    }

    public ModInfo(String name, String url, String sha1, String sha256, long size) {
        this.name = name;
        this.url = url;
        this.sha1 = sha1;
        this.sha256 = sha256;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
