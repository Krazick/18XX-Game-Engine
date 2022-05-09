package ge18xx.utilities;

import org.w3c.dom.Element;

public class XMLElement {
	Element element;
	public static XMLElement NO_XML_ELEMENT = null;

	public XMLElement (Element aElement) {
		element = aElement;
	}

	public void appendChild (XMLElement aXMLElement) {
		element.appendChild (aXMLElement.getElement ());
	}

	public Element getElement () {
		return element;
	}

	public void setAttribute (AttributeName aAttributeName, String aValue) {
		element.setAttribute (aAttributeName.getString (), aValue);
	}

	public void setAttribute (AttributeName aAttributeName, int aValue) {
		setAttribute (aAttributeName, new Integer (aValue).toString ());
	}

	public void setAttribute (AttributeName aAttributeName, boolean aValue) {
		setAttribute (aAttributeName, new Boolean (aValue).toString ());
	}
}
