package eu.girc.launcher.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record LibraryAsset(@SerializedName("downloads") LibraryDownload libraryDownload, @SerializedName("extract") LibraryExtract libraryExtract, String name, Map<String, String> natives) { }
