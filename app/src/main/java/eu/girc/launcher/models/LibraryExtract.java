package eu.girc.launcher.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record LibraryExtract(@SerializedName("exclude") List<String> excludeList) { }
