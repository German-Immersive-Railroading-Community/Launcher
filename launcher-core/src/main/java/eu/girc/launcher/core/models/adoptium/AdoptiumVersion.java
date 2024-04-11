package eu.girc.launcher.core.models.adoptium;

import com.google.gson.annotations.SerializedName;

public record AdoptiumVersion(int build, int major, int minor, @SerializedName("openjdk_version") String openjdkVersion, int security, String semver) { }
