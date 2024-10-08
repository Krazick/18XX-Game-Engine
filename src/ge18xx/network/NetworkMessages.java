package ge18xx.network;

import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class NetworkMessages {
	XMLDocument xmlDocument;
	String gameSupport;
	XMLElement xmlGameMessage;
	XMLElement xmlElement;

	public NetworkMessages () {
		xmlDocument = new XMLDocument ();
	}

	public XMLDocument getXMLDocument () {
		return xmlDocument;
	}

	public void buildGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN) {
		xmlDocument.clearDocumentChildren ();
		xmlGameMessage = xmlDocument.createElement (aPrimaryEN);
		xmlElement = xmlDocument.createElement (aSecondaryEN);
		xmlGameMessage.appendChild (xmlElement);
		xmlDocument.appendChild (xmlGameMessage);
	}

	public String buildGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN, AttributeName aAttributeName1,
			String aAttributeValue1) {
		buildGameXML (aPrimaryEN, aSecondaryEN);
		addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName1, aAttributeValue1);

		gameSupport = toString ();

		return gameSupport;
	}

	public String constructGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN, AttributeName aAttributeName1,
			String aAttributeValue1, AttributeName aAttributeName2, String aAttributeValue2) {
		buildGameXML (aPrimaryEN, aSecondaryEN);
		addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName1, aAttributeValue1);
		addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName2, aAttributeValue2);

		gameSupport = toString ();

		return gameSupport;
	}

	public void addAttribute (ElementName aPrimaryEN, ElementName aSecondaryEN,
							AttributeName aAttributeName, String aAttributeValue) {
		XMLElement tXMLPrimaryElement;
		XMLElement tXMLSecondaryElement;

		tXMLPrimaryElement = getXMLGameMessage (aPrimaryEN);
		tXMLSecondaryElement = getXMLChildElement (tXMLPrimaryElement, aSecondaryEN);
		tXMLSecondaryElement.setAttribute (aAttributeName, aAttributeValue);
	}


	public void addAttribute (ElementName aPrimaryEN, ElementName aSecondaryEN,
							AttributeName aAttributeName, int aAttributeValue) {
		XMLElement tXMLPrimaryElement;
		XMLElement tXMLSecondaryElement;

		tXMLPrimaryElement = getXMLGameMessage (aPrimaryEN);
		tXMLSecondaryElement = getXMLChildElement (tXMLPrimaryElement, aSecondaryEN);
		tXMLSecondaryElement.setAttribute (aAttributeName, aAttributeValue);
	}

	public void appendChild (ElementName aPrimaryEN, ElementName aSecondaryEN, XMLElement aXMLElement) {
		XMLElement tXMLPrimaryElement;
		XMLElement tXMLSecondaryElement;

		tXMLPrimaryElement = getXMLGameMessage (aPrimaryEN);
		tXMLSecondaryElement = getXMLChildElement (tXMLPrimaryElement, aSecondaryEN);
		tXMLSecondaryElement.appendChild (aXMLElement);
	}

	public XMLElement getXMLGameMessage (ElementName aElementName) {
		XMLNode tXMLGameNode;
		XMLElement tXMLGameMessage;
		String tGameNodeName;

		tXMLGameMessage = XMLElement.NO_XML_ELEMENT;
		if (xmlDocument.hasChildNodes ()) {
			tXMLGameNode = xmlDocument.getDocumentNode ();
			tGameNodeName = tXMLGameNode.getNodeName ();
			if (tGameNodeName.equals (aElementName.toString ())) {
				tXMLGameMessage = new XMLElement (tXMLGameNode.getNode ());
			}
		}

		return tXMLGameMessage;
	}

	public XMLElement getXMLChildElement (XMLElement aXMLElement, ElementName aElementName) {
		XMLElement tXMLChildElement;

		tXMLChildElement = XMLElement.NO_XML_ELEMENT;
		if (aXMLElement.hasChildNodes ()) {
			tXMLChildElement = aXMLElement.getElement (aElementName);
		}

		return tXMLChildElement;
	}

	@Override
	public String toString () {
		String tMessage;

		tMessage = xmlDocument.toXMLString ();
		tMessage = tMessage.replace (GUI.NEWLINE, "");
		tMessage = tMessage.replaceAll ("> *<", "><");

		return tMessage;
	}
}
