package ge18xx.game;

import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public interface XMLSaveGameI {
	public static final XMLSaveGameI NO_XML_SAVE_GAME = null;
	
	default public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		return XMLElement.NO_XML_ELEMENT;
	}
}
