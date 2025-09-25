package com.troblecodings.launcher;

// Based on https://stackoverflow.com/questions/2469922/generate-a-version-java-file-in-maven
public final class GirBuildConfig {
    public static final String VERSION = "${project.version}";

    private GirBuildConfig() {}
}