package eu.girc.launcher.models;

import org.json.JSONPropertyName;

public record LibraryAssets(@JSONPropertyName("downloads") LibraryDownload libraryDownload, String name) {
}
