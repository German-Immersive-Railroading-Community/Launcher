package com.troblecodings.launcher.models.minecraft;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record MinecraftInfo(@SerializedName("objects") Map<String, MinecraftObject> mcObjects) {
}
