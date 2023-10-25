package eu.girc.launcher.models;

import com.google.gson.annotations.SerializedName;

public record LibraryAsset(@SerializedName("downloads") LibraryDownload libraryDownload, @SerializedName("extract") LibraryExtract libraryExtract, String name) { }
