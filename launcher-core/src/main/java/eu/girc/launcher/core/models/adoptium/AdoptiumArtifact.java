package eu.girc.launcher.core.models.adoptium;

import com.google.gson.annotations.SerializedName;

public record AdoptiumArtifact(String checksum,
                               @SerializedName("checksum_link") String checksumLink,
                               @SerializedName("download_count") long downloadCount,
                               String link,
                               @SerializedName("metadata_link") String metadataLink,
                               String name,
                               @SerializedName("signature_link") String signatureLink,
                               long size) { }
