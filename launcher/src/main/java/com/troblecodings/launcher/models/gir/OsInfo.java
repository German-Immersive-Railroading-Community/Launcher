package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class OsInfo {
    @SerializedName("name")
    private String name;
    
    @SerializedName("version")
    private String version;
    
    @SerializedName("arch")
    private String arch;

    public OsInfo() {
    }

    public OsInfo(String name, String version, String arch) {
        this.name = name;
        this.version = version;
        this.arch = arch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }
}
