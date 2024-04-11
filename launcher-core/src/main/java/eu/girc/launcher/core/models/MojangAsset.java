package eu.girc.launcher.core.models;

public record MojangAsset(String hash, long size) {
    /**
     * @return The first two letters of the hash
     */
    public String folder() {
        return hash.substring(0, 2);
    }
}
