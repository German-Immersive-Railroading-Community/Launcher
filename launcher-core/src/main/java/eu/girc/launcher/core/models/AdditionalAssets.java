package eu.girc.launcher.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record AdditionalAssets(List<AdditionalObject> mods,
                               List<AdditionalObject> config,
                               @SerializedName("config/immersiverailroading") List<AdditionalObject> irConfig,
                               @SerializedName("resourcepacks") List<AdditionalObject> resourcePacks,
                               @SerializedName("contentpacks/opensignals") List<AdditionalObject> contentPacks) { }
