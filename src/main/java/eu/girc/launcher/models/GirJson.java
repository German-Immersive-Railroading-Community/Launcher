package eu.girc.launcher.models;

import org.json.JSONPropertyName;

import java.util.List;

public record GirJson(AssetIndex assetIndex, String assets, Downloads downloads, String id, String time,
                      String releaseTime, String type, String mainClass, String inheritsFrom,
                      String minecraftArguments, @JSONPropertyName("additional") AdditionalMods additionalMods,
                      List<Library> libraries, long wholeSize, List<OptionalMod> optionalMods) {
}
