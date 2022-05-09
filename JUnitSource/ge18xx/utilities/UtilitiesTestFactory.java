package ge18xx.utilities;

public class UtilitiesTestFactory {
	XMLDocument theXMLDocument;

	public UtilitiesTestFactory () {
		theXMLDocument = new XMLDocument ();
	}

	public XMLNode constructXMLNode (String aXMLText) {
		XMLNode tXMLNode;

		theXMLDocument = theXMLDocument.ParseXMLString (aXMLText);

		if (theXMLDocument.ValidDocument ()) {
			tXMLNode = theXMLDocument.getDocumentElement ();
		} else {
			tXMLNode = XMLNode.NO_NODE;
		}

		return tXMLNode;
	}

	public XMLDocument getTheXMLDocument () {
		return theXMLDocument;
	}
}
