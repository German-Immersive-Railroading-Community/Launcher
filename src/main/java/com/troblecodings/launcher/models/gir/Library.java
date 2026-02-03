package com.troblecodings.launcher.models.gir;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Library {
    @SerializedName("name")
    private String name;
    
    @SerializedName("downloads")
    private LibraryDownloads downloads;
    
    @SerializedName("rules")
    private List<Rule> rules;
    
    @SerializedName("natives")
    private Map<String, String> natives;
    
    @SerializedName("extract")
    private ExtractionInfo extract;

    public Library() {
    }

    public Library(String name, LibraryDownloads downloads, List<Rule> rules, 
                   Map<String, String> natives, ExtractionInfo extract) {
        this.name = name;
        this.downloads = downloads;
        this.rules = rules;
        this.natives = natives;
        this.extract = extract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LibraryDownloads getDownloads() {
        return downloads;
    }

    public void setDownloads(LibraryDownloads downloads) {
        this.downloads = downloads;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Map<String, String> getNatives() {
        return natives;
    }

    public void setNatives(Map<String, String> natives) {
        this.natives = natives;
    }

    public ExtractionInfo getExtract() {
        return extract;
    }

    public void setExtract(ExtractionInfo extract) {
        this.extract = extract;
    }
}
