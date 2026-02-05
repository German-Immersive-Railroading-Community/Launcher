package com.troblecodings.launcher.util;

import java.util.Arrays;

public class Version {
    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(String version) {
        Integer[] parts = Arrays.stream(version.split("\\.")).map(Integer::parseInt).toArray(Integer[]::new);
        if (parts.length != 3) throw new IllegalArgumentException("Invalid version passed to constructor!");

        this.major = parts[0];
        this.minor = parts[1];
        this.patch = parts[2];
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean isNewerThan(Version other) {
        return this.major > other.major || (this.major == other.major && this.minor > other.minor) || (this.major == other.major && this.minor == other.minor && this.patch > other.patch);
    }

    public boolean isOlderThan(Version other) {
        return this.major < other.major || (this.major == other.major && this.minor < other.minor) || (this.major == other.major && this.minor == other.minor && this.patch < other.patch);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Version)) {
            return false;
        }

        Version ver = (Version) obj;
        return this.major == ver.major && this.minor == ver.minor && this.patch == ver.patch;
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }
}
