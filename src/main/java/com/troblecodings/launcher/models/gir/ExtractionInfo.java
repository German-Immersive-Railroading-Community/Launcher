package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExtractionInfo {
    @SerializedName("exclude")
    private List<String> exclude;

    public ExtractionInfo() {
    }

    public ExtractionInfo(List<String> exclude) {
        this.exclude = exclude;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }
}
