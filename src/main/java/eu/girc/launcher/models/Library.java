package eu.girc.launcher.models;

import org.json.JSONPropertyName;

public record Library(@JSONPropertyName("downloads") LibraryDownload libraryDownload, String name) {
}
