package eu.girc.launcher.core.models.adoptium;

import com.google.gson.annotations.SerializedName;

public record AdoptiumBinary(String architecture,
                             @SerializedName("download_count") long downloadCount,
                             String heap_size,
                             String image_type,
                             AdoptiumArtifact installer,
                             String jvm_impl,
                             String os,
                             @SerializedName("package") AdoptiumArtifact rawPackage,
                             String project,
                             @SerializedName("scm_ref") String scmRef,
                             @SerializedName("updated_at") String updatedAt) { }
