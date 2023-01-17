package ge18xx.utilities;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLElement {
	Element element;
	public static XMLElement NO_XML_ELEMENT = null;
	public static Element NO_ELEMENT = null;

	public XMLElement (Element aElement) {
		setElement (aElement);
	}

	public XMLElement (Node aNode) {
		Element tElement;

		tElement = (Element) aNode;
		setElement (tElement);
	}

	public void appendChild (XMLElement aXMLElement) {
		if (validElement ()) {
			element.appendChild (aXMLElement.getElement ());
		}
	}

	public void setElement (Element aElement) {
		element = aElement;
	}

	public Element getElement () {
		return element;
	}

	public boolean validElement () {
		return element != NO_ELEMENT;
	}

	public boolean hasChildNodes () {
		Node tNode;
		boolean tHasChildNodes;

		tHasChildNodes = false;
		if (validElement ()) {
			tNode = element;
			tHasChildNodes = tNode.hasChildNodes ();
		}

		return tHasChildNodes;
	}

	public NodeList getChildNodes () {
		return element.getChildNodes ();
	}

	public void setAttribute (AttributeName aAttributeName, String aValue) {
		if (validElement ()) {
			element.setAttribute (aAttributeName.getString (), aValue);
		}
	}

	public void setAttribute (AttributeName aAttributeName, int aValue) {
		setAttribute (aAttributeName, "" + aValue);
	}

	public void setAttribute (AttributeName aAttributeName, boolean aValue) {
		setAttribute (aAttributeName, "" + aValue);
	}

	public XMLElement getElement (ElementName aElementName) {
		XMLElement tFoundElement;
		NodeList tNodeList;
		int tElementIndex;
		int tElementCount;
		Element tElement;
		String tNodeName;
		String tElementName;

		tFoundElement = NO_XML_ELEMENT;
		if (element.hasChildNodes ()) {
			tNodeList = element.getChildNodes ();
			tElementCount = tNodeList.getLength ();
			tElementName = aElementName.getString ();
			for (tElementIndex = 0; tElementIndex < tElementCount; tElementIndex++) {
				tElement = (Element) tNodeList.item (tElementIndex);
				tNodeName = tElement.getNodeName ();
				if (tElementName.equals (tNodeName)) {
					tFoundElement = new XMLElement (tElement);
				}
			}
		}

		return tFoundElement;
	}

}
