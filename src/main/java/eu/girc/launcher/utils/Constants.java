package eu.girc.launcher.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Constants {
    private Constants() {}

    public static final Gson GSON = new GsonBuilder().create();
}
