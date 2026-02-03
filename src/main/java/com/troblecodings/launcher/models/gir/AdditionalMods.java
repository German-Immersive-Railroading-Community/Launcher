package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class AdditionalMods {
    @SerializedName("mods")
    private List<ModInfo> mods;

    @SerializedName("resourcepacks")
    private List<ModInfo> resourcepacks;

    // This is transient to catch all cases not handled above, i.e. config/... or contentpack/...
    private transient Map<String, List<ModInfo>> other;

    public AdditionalMods() {
    }

    public List<ModInfo> getMods() {
        return mods;
    }

    public void setMods(List<ModInfo> mods) {
        this.mods = mods;
    }

    public List<ModInfo> getResourcepacks() {
        return resourcepacks;
    }

    public void setResourcepacks(List<ModInfo> resourcepacks) {
        this.resourcepacks = resourcepacks;
    }

    public Map<String, List<ModInfo>> getOther() {
        return other;
    }

    public void setOther(Map<String, List<ModInfo>> other) {
        this.other = other;
    }

    public List<ModInfo> getContentForOther(String path) {
        return other != null ? other.get(path) : null;
    }
}
