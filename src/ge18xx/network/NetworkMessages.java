package ge18xx.network;

import org.w3c.dom.NodeList;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class NetworkMessages {
	XMLDocument xmlDocument;
	
	public NetworkMessages () {
		xmlDocument = new XMLDocument ();
	}

	public String constructGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN, AttributeName aAttributeName1,
			String aAttributeValue1) {
		String tGameSupport;
		XMLElement tXMLGameMessage, tXMLElement;

		xmlDocument.clearDocumentChildren ();
		tXMLGameMessage = xmlDocument.createElement (aPrimaryEN);
		tXMLElement = xmlDocument.createElement (aSecondaryEN);

		tXMLElement.setAttribute (aAttributeName1, aAttributeValue1);
		tXMLGameMessage.appendChild (tXMLElement);
		xmlDocument.appendChild (tXMLGameMessage);

		tGameSupport = toString ();

		return tGameSupport;
	}

	public String constructGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN, AttributeName aAttributeName1,
			String aAttributeValue1, AttributeName aAttributeName2, String aAttributeValue2) {
		String tGameSupport;
		XMLElement tXMLGameMessage, tXMLElement;

		xmlDocument.clearDocumentChildren ();
		tXMLGameMessage = xmlDocument.createElement (aPrimaryEN);
		tXMLElement = xmlDocument.createElement (aSecondaryEN);

		tXMLElement.setAttribute (aAttributeName1, aAttributeValue1);
		tXMLElement.setAttribute (aAttributeName2, aAttributeValue2);
		tXMLGameMessage.appendChild (tXMLElement);
		xmlDocument.appendChild (tXMLGameMessage);

		tGameSupport = toString ();

		return tGameSupport;
	}

	public XMLElement getXMLGameMessage (ElementName aElementName) {
		XMLNode tXMLGameNode;
		XMLNode tXMLChildNode;
		XMLElement tXMLGameMessage;
		
		tXMLGameMessage = XMLElement.NO_XML_ELEMENT;
		if (xmlDocument.hasChildNodes ()) {
			tXMLGameNode = xmlDocument.getDocumentNode ();
			tXMLChildNode = tXMLGameNode.getNode (aElementName);
			tXMLGameMessage = tXMLChildNode.getXMLElement ();
		}
		
		return tXMLGameMessage;
	}
	
	@Override
	public String toString () {
		String tMessage;
		
		tMessage = xmlDocument.toString ();
		tMessage = tMessage.replace ("\n", "");

		return tMessage;
	}
}
