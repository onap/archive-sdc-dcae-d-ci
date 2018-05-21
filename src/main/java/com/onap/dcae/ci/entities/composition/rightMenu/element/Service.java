
package com.onap.dcae.ci.entities.composition.rightMenu.element;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("element")
    @Expose
    private ItemsElement element;

    public ItemsElement getElement() {
        return element;
    }

    public void setElement(ItemsElement element) {
        this.element = element;
    }

}
