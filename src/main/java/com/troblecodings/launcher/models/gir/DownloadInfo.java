package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class DownloadInfo {
    @SerializedName("sha1")
    private String sha1;
    
    @SerializedName("size")
    private long size;
    
    @SerializedName("url")
    private String url;

    public DownloadInfo() {
    }

    public DownloadInfo(String sha1, long size, String url) {
        this.sha1 = sha1;
        this.size = size;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
