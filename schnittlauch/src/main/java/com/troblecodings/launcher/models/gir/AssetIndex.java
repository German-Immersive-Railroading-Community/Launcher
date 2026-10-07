package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class AssetIndex {
    @SerializedName("id")
    private String id;
    
    @SerializedName("sha1")
    private String sha1;
    
    @SerializedName("size")
    private long size;
    
    @SerializedName("totalSize")
    private long totalSize;
    
    @SerializedName("url")
    private String url;

    public AssetIndex() {
    }

    public AssetIndex(String id, String sha1, long size, long totalSize, String url) {
        this.id = id;
        this.sha1 = sha1;
        this.size = size;
        this.totalSize = totalSize;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
