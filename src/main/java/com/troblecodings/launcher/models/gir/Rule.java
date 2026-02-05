package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

public class Rule {
    @SerializedName("action")
    private String action;
    
    @SerializedName("os")
    private OsInfo os;

    public Rule() {
    }

    public Rule(String action, OsInfo os) {
        this.action = action;
        this.os = os;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public OsInfo getOs() {
        return os;
    }

    public void setOs(OsInfo os) {
        this.os = os;
    }
}
