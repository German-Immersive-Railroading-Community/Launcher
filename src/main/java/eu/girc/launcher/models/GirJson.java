package eu.girc.launcher.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public record GirJson(AssetIndex assetIndex, String assets, Downloads downloads, String id, String time,
        String releaseTime, String type, String mainClass, String inheritsFrom,
        String minecraftArguments, @SerializedName("additional") AdditionalAssets additionalAssets,
        @SerializedName("libraries") List<LibraryAssets> libraryAssets, long wholeSize,
        List<OptionalMod> optionalMods) {
}
