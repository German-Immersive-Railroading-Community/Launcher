package com.troblecodings.launcher;

/**
 * Version wrapper class representing a Version in the MAJOR.MINOR.PATH format, with an optional appendix.
 */
public final class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;
    private final String appendix;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        appendix = "";
    }

    public Version(int major, int minor, int patch, String appendix) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;

        if (appendix != null && !appendix.isEmpty())
            this.appendix = appendix;
        else
            this.appendix = "";
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

    public String getAppendix() {
        return appendix;
    }

    public boolean isGreaterThan(Version other) {
        return this.compareTo(other) > 0;
    }

    public boolean isLessThan(Version other) {
        return this.compareTo(other) < 0;
    }

    @Override
    public int compareTo(Version other) {
        if (major > other.major) return 1;
        else if (major < other.major) return -1;
        else if (minor > other.minor) return 1;
        else if (minor < other.minor) return -1;
        else if (patch > other.patch) return 1;
        else if (patch < other.patch) return -1;

        return 0;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d%s", major, minor, patch, appendix);
    }
}
