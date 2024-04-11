package eu.girc.launcher.core.models;

import java.util.Map;

public record LibraryDownload(LibraryArtifact artifact, Map<String, LibraryArtifact> classifiers) { }
