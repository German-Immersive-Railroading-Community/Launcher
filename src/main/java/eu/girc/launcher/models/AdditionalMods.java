package eu.girc.launcher.models;

import org.json.JSONPropertyName;

import java.util.List;

public record AdditionalMods(List<AdditionalObject> mods, List<AdditionalObject> config,
                             @JSONPropertyName("config/immersiverailroading") List<AdditionalObject> irConfig,
                             @JSONPropertyName("resourcepacks") List<AdditionalObject> resourcePacks,
                             @JSONPropertyName("contentpacks/opensignals/") List<AdditionalObject> contentPacks) {
}
