package ge18xx.utilities;

import org.mockito.Mockito;

public class UtilitiesTestFactory {
	XMLDocument theXMLDocument;

	public UtilitiesTestFactory () {
		theXMLDocument = new XMLDocument ();
	}

	public XMLDocument buildXMLDocument (String aXMLText) {
		XMLDocument tXMLDocument;

		tXMLDocument = theXMLDocument.ParseXMLString (aXMLText);

		return tXMLDocument;
	}

	public XMLDocument buildXMLDocumentMock () {
		XMLDocument mXMLDocument;

		mXMLDocument = Mockito.mock (XMLDocument.class);

		return mXMLDocument;
	}

	public XMLNode buildXMLNode (String aXMLText) {
		XMLNode tXMLNode;
		XMLDocument tXMLDocument;

		tXMLDocument = buildXMLDocument (aXMLText);

		if (tXMLDocument.validDocument ()) {
			tXMLNode = tXMLDocument.getDocumentNode ();
		} else {
			tXMLNode = XMLNode.NO_NODE;
		}

		return tXMLNode;
	}

	public XMLNode buildXMLNodeMock () {
		XMLNode mXMLNode;

		mXMLNode = Mockito.mock (XMLNode.class);

		return mXMLNode;
	}

	public XMLDocument getTheXMLDocument () {
		return theXMLDocument;
	}

	public XMLElement buildXMLElement (String aXMLText) {
		XMLElement tXMLElement;
		XMLDocument tXMLDocument;

		tXMLDocument = buildXMLDocument (aXMLText);

		if (tXMLDocument.validDocument ()) {
			tXMLElement = tXMLDocument.getDocumentElement ();
		} else {
			tXMLElement = XMLElement.NO_XML_ELEMENT;
		}

		return tXMLElement;
	}

	public XMLElement buildXMLElementMock () {
		XMLElement mXMLElement;

		mXMLElement = Mockito.mock (XMLElement.class);

		return mXMLElement;
	}
}
