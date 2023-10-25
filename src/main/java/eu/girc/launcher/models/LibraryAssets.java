package eu.girc.launcher.models;

import com.google.gson.annotations.SerializedName;

public record LibraryAssets(@SerializedName("downloads") LibraryDownload libraryDownload, String name) { }
