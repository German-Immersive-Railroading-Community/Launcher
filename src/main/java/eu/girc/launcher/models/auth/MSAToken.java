package eu.girc.launcher.models.auth;

import com.google.gson.annotations.SerializedName;

public record MSAToken(@SerializedName("token_type") String tokenType,
                       @SerializedName("scope") String scope,
                       @SerializedName("expires_in") int expires_in,
                       @SerializedName("ext_expires_in") int ext_expires_in,
                       @SerializedName("access_token") String accessToken,
                       @SerializedName("refresh_token") String refreshToken,
                       @SerializedName("id_token") String idToken) { }