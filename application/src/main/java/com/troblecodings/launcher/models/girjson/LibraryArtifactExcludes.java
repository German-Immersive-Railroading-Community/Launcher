package com.troblecodings.launcher.models.girjson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record LibraryArtifactExcludes(@SerializedName("exclude") List<String> excludeList) {
}
