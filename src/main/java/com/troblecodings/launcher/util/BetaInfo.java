package com.troblecodings.launcher.util;

public class BetaInfo {
    private final String modName;
    private final int prNum;
    private final String prName;
    private final String prDownload;
    private final int prPort;

    public BetaInfo(String modName, int prNum, String prName, String prDownload, int prPort) {
        this.modName = modName;
        this.prNum = prNum;
        this.prName = prName;
        this.prDownload = prDownload;
        this.prPort = prPort;
    }

    public String getModName() {
        return this.modName;
    }

    public int getPrNum() {
        return this.prNum;
    }

    public String getPrName() {
        return this.prName;
    }

    public String getPrDownload() {
        return this.prDownload;
    }

    public int getPrPort() {
        return this.prPort;
    }

    @Override
    public String toString() {
        return modName + " - #" + prNum + ": " + prName;
    }
}
