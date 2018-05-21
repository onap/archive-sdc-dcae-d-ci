
package com.onap.dcae.ci.entities.composition.items;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("lifecycleState")
    @Expose
    private String lifecycleState;
    @SerializedName("models")
    @Expose
    private List<Model> models = null;
    @SerializedName("subCategory")
    @Expose
    private String subCategory;
    @SerializedName("catalog")
    @Expose
    private String catalog;
    @SerializedName("lastUpdaterUserId")
    @Expose
    private String lastUpdaterUserId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("itemId")
    @Expose
    private String itemId;
    @SerializedName("catalogId")
    @Expose
    private Integer catalogId;
    @SerializedName("toscaModelURL")
    @Expose
    private String toscaModelURL;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("invariantUUID")
    @Expose
    private String invariantUUID;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("lastUpdaterFullName")
    @Expose
    private String lastUpdaterFullName;
    @SerializedName("toscaResourceName")
    @Expose
    private String toscaResourceName;
    @SerializedName("resourceType")
    @Expose
    private String resourceType;
    @SerializedName("artifacts")
    @Expose
    private List<Artifact> artifacts = null;

    public String getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(String lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getLastUpdaterUserId() {
        return lastUpdaterUserId;
    }

    public void setLastUpdaterUserId(String lastUpdaterUserId) {
        this.lastUpdaterUserId = lastUpdaterUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }

    public String getToscaModelURL() {
        return toscaModelURL;
    }

    public void setToscaModelURL(String toscaModelURL) {
        this.toscaModelURL = toscaModelURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInvariantUUID() {
        return invariantUUID;
    }

    public void setInvariantUUID(String invariantUUID) {
        this.invariantUUID = invariantUUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLastUpdaterFullName() {
        return lastUpdaterFullName;
    }

    public void setLastUpdaterFullName(String lastUpdaterFullName) {
        this.lastUpdaterFullName = lastUpdaterFullName;
    }

    public String getToscaResourceName() {
        return toscaResourceName;
    }

    public void setToscaResourceName(String toscaResourceName) {
        this.toscaResourceName = toscaResourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

}
