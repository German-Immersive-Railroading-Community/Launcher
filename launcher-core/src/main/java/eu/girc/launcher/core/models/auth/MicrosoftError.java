package eu.girc.launcher.core.models.auth;

import com.google.gson.annotations.SerializedName;

public record MicrosoftError(@SerializedName("error") String error, @SerializedName("error_description") String errorDescription, @SerializedName("error_codes") long[] errorCodes) { }
