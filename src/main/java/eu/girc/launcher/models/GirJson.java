package eu.girc.launcher.models;

import org.json.JSONPropertyName;

import java.util.List;

public record GirJson(AssetIndex assetIndex, String assets, Downloads downloads, String id, String time,
                      String releaseTime, String type, String mainClass, String inheritsFrom,
                      String minecraftArguments, @JSONPropertyName("additional") AdditionalAssets additionalAssets,
                      @JSONPropertyName("libraries") List<LibraryAssets> libraryAssets, long wholeSize,
                      List<OptionalMod> optionalMods) {
}
