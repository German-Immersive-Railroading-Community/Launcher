package eu.girc.launcher.models.adoptium;

import com.google.gson.annotations.SerializedName;

public record AdoptiumAsset(AdoptiumBinary binary, @SerializedName("release_link") String releaseLink, @SerializedName("release_name") String releaseName, String vendor, AdoptiumVersion version) { }
