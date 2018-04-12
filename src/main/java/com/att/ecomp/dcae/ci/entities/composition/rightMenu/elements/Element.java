package com.att.ecomp.dcae.ci.entities.composition.rightMenu.elements;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Element {

@SerializedName("itemId")
@Expose
private String itemId;
@SerializedName("catalogId")
@Expose
private Integer catalogId;
@SerializedName("catalog")
@Expose
private String catalog;
@SerializedName("name")
@Expose
private String name;
@SerializedName("id")
@Expose
private Integer id;
@SerializedName("labels")
@Expose
private List<String> labels = null;

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

public String getCatalog() {
return catalog;
}

public void setCatalog(String catalog) {
this.catalog = catalog;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public List<String> getLabels() {
return labels;
}

public void setLabels(List<String> labels) {
this.labels = labels;
}

}