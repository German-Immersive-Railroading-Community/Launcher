package eu.girc.launcher.models;

import java.util.Map;

public record LibraryDownload(LibraryArtifact artifact, Map<String, LibraryArtifact> classifiers) { }
