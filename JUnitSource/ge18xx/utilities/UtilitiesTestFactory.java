package ge18xx.utilities;

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

	public XMLDocument getTheXMLDocument () {
		return theXMLDocument;
	}
}
