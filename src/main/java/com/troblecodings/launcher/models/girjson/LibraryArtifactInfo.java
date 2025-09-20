package com.troblecodings.launcher.models.girjson;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record LibraryArtifactInfo(String name, @SerializedName("downloads") LibraryArtifactDownloadInfo downloadInfo,
                                  @SerializedName("extract") LibraryArtifactExcludes excludes,
                                  Map<String, String> natives) {
}
