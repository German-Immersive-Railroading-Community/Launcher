package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class Downloads {
    @SerializedName("client")
    private DownloadInfo client;
    
    @SerializedName("server")
    private DownloadInfo server;

    public Downloads() {
    }

    public Downloads(DownloadInfo client, DownloadInfo server) {
        this.client = client;
        this.server = server;
    }

    public DownloadInfo getClient() {
        return client;
    }

    public void setClient(DownloadInfo client) {
        this.client = client;
    }

    public DownloadInfo getServer() {
        return server;
    }

    public void setServer(DownloadInfo server) {
        this.server = server;
    }
}
