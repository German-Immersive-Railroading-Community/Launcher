package com.troblecodings.launcher.models.minecraft;

public record MinecraftObject(String hash, long size) {
    /**
     * @return The first two letters of the hash
     */
    public String folder() {
        return hash.substring(0, 2);
    }
}
