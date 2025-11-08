package com.troblecodings.launcher.models.girjson;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Contains the modpack information tailored for German-Immersive-Railroading-Community.
 */
public record GirJson(AssetIndex assetIndex, String assets, Map<String, DownloadInfo> downloads, String id, String time,
                      String releaseTime, String type, String mainClass, String inheritsFrom,
                      String minecraftArguments,
                      @SerializedName("additional") Map<String, List<AdditionalArtifact>> additionalArtifacts,
                      List<LibraryArtifactInfo> libraries, @SerializedName("wholeSize") long totalSize,
                      List<OptionalMod> optionalMods) {
}
