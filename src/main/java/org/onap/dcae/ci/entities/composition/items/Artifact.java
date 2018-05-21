
package org.onap.dcae.ci.entities.composition.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Artifact {

    @SerializedName("artifactChecksum")
    @Expose
    private String artifactChecksum;
    @SerializedName("artifactType")
    @Expose
    private String artifactType;
    @SerializedName("artifactUUID")
    @Expose
    private String artifactUUID;
    @SerializedName("artifactVersion")
    @Expose
    private String artifactVersion;
    @SerializedName("artifactName")
    @Expose
    private String artifactName;
    @SerializedName("artifactGroupType")
    @Expose
    private String artifactGroupType;
    @SerializedName("artifactURL")
    @Expose
    private String artifactURL;
    @SerializedName("artifactDescription")
    @Expose
    private String artifactDescription;
    @SerializedName("artifactLabel")
    @Expose
    private String artifactLabel;

    public String getArtifactChecksum() {
        return artifactChecksum;
    }

    public void setArtifactChecksum(String artifactChecksum) {
        this.artifactChecksum = artifactChecksum;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    public String getArtifactUUID() {
        return artifactUUID;
    }

    public void setArtifactUUID(String artifactUUID) {
        this.artifactUUID = artifactUUID;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactGroupType() {
        return artifactGroupType;
    }

    public void setArtifactGroupType(String artifactGroupType) {
        this.artifactGroupType = artifactGroupType;
    }

    public String getArtifactURL() {
        return artifactURL;
    }

    public void setArtifactURL(String artifactURL) {
        this.artifactURL = artifactURL;
    }

    public String getArtifactDescription() {
        return artifactDescription;
    }

    public void setArtifactDescription(String artifactDescription) {
        this.artifactDescription = artifactDescription;
    }

    public String getArtifactLabel() {
        return artifactLabel;
    }

    public void setArtifactLabel(String artifactLabel) {
        this.artifactLabel = artifactLabel;
    }

}
