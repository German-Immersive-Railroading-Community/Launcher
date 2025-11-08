package com.troblecodings.launcher.models.minecraft;

import com.google.gson.annotations.SerializedName;
import com.troblecodings.launcher.models.minecraft.MinecraftObject;

import java.util.Map;

public record MinecraftInfo(@SerializedName("objects") Map<String, MinecraftObject> mcObjects) {
}
