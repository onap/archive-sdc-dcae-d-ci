package org.onap.dcae.ci.entities.composition.rightMenu.elements;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Elements {
	@SerializedName("elements")
	@Expose
	private List<Element> elements = null;

	public List<Element> getElements() {
	return elements;
	}

	public void setElements(List<Element> elements) {
	this.elements = elements;
	}
}
