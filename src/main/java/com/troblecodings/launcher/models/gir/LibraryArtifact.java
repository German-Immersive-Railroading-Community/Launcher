package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class LibraryArtifact {
    @SerializedName("path")
    private String path;
    
    @SerializedName("url")
    private String url;
    
    @SerializedName("sha1")
    private String sha1;
    
    @SerializedName("size")
    private long size;

    public LibraryArtifact() {
    }

    public LibraryArtifact(String path, String url, String sha1, long size) {
        this.path = path;
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
