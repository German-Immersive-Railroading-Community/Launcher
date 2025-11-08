package com.troblecodings.launcher.models.girjson;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record LibraryArtifactDownloadInfo(@SerializedName("artifact") LibraryArtifactDownload artifactDownload,
                                          Map<String, LibraryArtifactDownload> classifiers) {
}
