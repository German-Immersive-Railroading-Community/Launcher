package eu.girc.launcher.models.auth;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record MSAError(String error,
                       @SerializedName("error_description") String errorDescription,
                       @SerializedName("error_codes") List<Integer> errorCodes,
                       String timestamp,
                       @SerializedName("trace_id") String traceId,
                       @SerializedName("correlation_id") String correlationId,
                       @SerializedName("error_id") String errorId) { }
