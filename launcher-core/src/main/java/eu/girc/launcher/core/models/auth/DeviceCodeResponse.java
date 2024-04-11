package eu.girc.launcher.core.models.auth;

import com.google.gson.annotations.SerializedName;

import java.net.URI;

public record DeviceCodeResponse(@SerializedName("device_code") String deviceCode,
                                 @SerializedName("user_code") String userCode,
                                 @SerializedName("verification_uri") URI verificationUri,
                                 @SerializedName("expires_in") int expiresIn,
                                 @SerializedName("interval") int interval,
                                 @SerializedName("message") String message) { }
