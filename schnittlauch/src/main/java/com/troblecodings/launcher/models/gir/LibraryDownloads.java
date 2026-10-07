package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class LibraryDownloads {
    @SerializedName("artifact")
    private LibraryArtifact artifact;
    
    @SerializedName("classifiers")
    private Map<String, LibraryArtifact> classifiers;

    public LibraryDownloads() {
    }

    public LibraryDownloads(LibraryArtifact artifact, Map<String, LibraryArtifact> classifiers) {
        this.artifact = artifact;
        this.classifiers = classifiers;
    }

    public LibraryArtifact getArtifact() {
        return artifact;
    }

    public void setArtifact(LibraryArtifact artifact) {
        this.artifact = artifact;
    }

    public Map<String, LibraryArtifact> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(Map<String, LibraryArtifact> classifiers) {
        this.classifiers = classifiers;
    }
}
