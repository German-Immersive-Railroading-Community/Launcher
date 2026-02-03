package com.troblecodings.launcher.models.gir;

public final class LibraryInfo {
    private String name;

    private LibraryDownloadInfo downloads;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LibraryDownloadInfo getDownloads() {
        return downloads;
    }

    public void setDownloads(LibraryDownloadInfo downloads) {
        this.downloads = downloads;
    }
}
