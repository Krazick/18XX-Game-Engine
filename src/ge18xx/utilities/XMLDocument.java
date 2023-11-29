package ge18xx.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLDocument {
	public static final Document NO_DOCUMENT = null;
	public static final XMLDocument NO_XML_DOCUMENT = null;
	Document document;

	public XMLDocument (Document aDocument) {
		document = aDocument;
	}

	public XMLDocument () {
		DocumentBuilderFactory tDocBuilderFac;
		DocumentBuilder tDocBuilder;

		try {
			tDocBuilderFac = DocumentBuilderFactory.newInstance ();
			tDocBuilder = tDocBuilderFac.newDocumentBuilder ();
			document = tDocBuilder.newDocument ();
		} catch (ParserConfigurationException tException) {
			System.err.println ("Caught Exception " + tException);
			document = NO_DOCUMENT;
			tException.printStackTrace ();
		}
	}

	public XMLDocument (File aFile) {
		DocumentBuilderFactory tDocBuilderFac;
		DocumentBuilder tDocBuilder;

		try {
			tDocBuilderFac = DocumentBuilderFactory.newInstance ();
			tDocBuilder = tDocBuilderFac.newDocumentBuilder ();
			document = tDocBuilder.parse (aFile);
		} catch (Exception tException) {
			System.err.println ("Caught Exception " + tException);
			document = NO_DOCUMENT;
			tException.printStackTrace ();
		}
	}

	public XMLDocument (URL aURL) {
		DocumentBuilderFactory tDocBuilderFac;
		DocumentBuilder tDocBuilder;

		try {
			tDocBuilderFac = DocumentBuilderFactory.newInstance ();
			tDocBuilder = tDocBuilderFac.newDocumentBuilder ();
			document = tDocBuilder.parse (aURL.openStream ());
		} catch (Exception tException) {
			System.err.println ("Caught Exception " + tException);
			document = NO_DOCUMENT;
			tException.printStackTrace ();
		}
	}

	public XMLDocument (String aFileName) {
		DocumentBuilderFactory tDocBuilderFac;
		DocumentBuilder tDocBuilder;

		try {
			tDocBuilderFac = DocumentBuilderFactory.newInstance ();
			tDocBuilder = tDocBuilderFac.newDocumentBuilder ();
			document = tDocBuilder.parse (aFileName);
		} catch (Exception tException) {
			System.err.println ("Caught Exception " + tException);
			document = NO_DOCUMENT;
			tException.printStackTrace ();
		}
	}

	/**
	 * Clear the document of any Children from the Document.
	 *
	 */
	public void clearDocumentChildren () {
		if (validDocument ()) {
			while (document.hasChildNodes ()) {
				document.removeChild (document.getFirstChild ());
			}
		}
	}

	public XMLDocument parseXMLString (String aXMLString) {
		DocumentBuilderFactory tDocBuilderFac;
		DocumentBuilder tDocBuilder;

		try {
			tDocBuilderFac = DocumentBuilderFactory.newInstance ();
			tDocBuilder = tDocBuilderFac.newDocumentBuilder ();
			document = tDocBuilder.parse (new InputSource (new StringReader (aXMLString)));
		} catch (Exception tException) {
			System.err.println ("Caught Exception " + tException);
			document = NO_DOCUMENT;
			tException.printStackTrace ();
		}

		return this;
	}

	/**
	 * If this XML Document has an Action non-NULL Document Object, then it is Valid
	 *
	 * @return FALSE if the DOCUMENT Object is NO_DOCUMENT, otherwise True
	 *
	 */
	public boolean validDocument () {
		boolean tValidDocument;

		if (document == NO_DOCUMENT) {
			tValidDocument = false;
		} else {
			tValidDocument = true;
		}

		return tValidDocument;
	}

	/**
	 * Append the Element from the provided XMLElement to this Document.
	 *
	 * @param aXMLElement This XMLElement contains an Element
	 */
	public void appendChild (XMLElement aXMLElement) {
		if (validDocument ()) {
			if (aXMLElement != XMLElement.NO_XML_ELEMENT) {
				document.appendChild (aXMLElement.getElement ());
			}
		}
	}

	/**
	 * If this XMLDocument has a Valid Document Element (ie non-NULL), find if the Document has
	 * any children, then return TRUE. If the Document is NULL, or the Document has NO Children,
	 * then return FALSE.
	 *
	 * @return True if there are one (or more) Children of the document.
	 *
	 */
	public boolean hasChildNodes () {
		boolean tHasChildNodes;

		if (validDocument ()) {
			tHasChildNodes = document.hasChildNodes ();
		} else {
			tHasChildNodes = false;
		}

		return tHasChildNodes;
	}

	/**
	 * Create an XMLElement with the provided Element Name.
	 * If the EntityName is NULL, or the String in the Entity Name is NULL this
	 * will return a NO_XML_ELEMENT
	 *
	 * @param aElementName The Element Name to be created. No Attributes or children will be attached
	 *
	 * @return If a Valid Entity
	 */
	public XMLElement createElement (ElementName aElementName) {
		XMLElement tXMLElement;

		if (aElementName != ElementName.NO_ELEMENT_NAME) {
			if (aElementName.validElementName ()) {
				tXMLElement = new XMLElement (document.createElement (aElementName.getString ()));
			} else {
				tXMLElement = XMLElement.NO_XML_ELEMENT;
			}
		} else {
			tXMLElement = XMLElement.NO_XML_ELEMENT;
		}

		return tXMLElement;
	}

	public Document getDocument () {
		return document;
	}

	public XMLNode getDocumentNode () {
		XMLNode tXMLNode;

		if (validDocument ()) {
			tXMLNode = new XMLNode (document.getDocumentElement ());
		} else {
			tXMLNode = XMLNode.NO_NODE;
		}

		return tXMLNode;
	}

	public XMLElement getDocumentElement () {
		XMLElement tXMLElement;

		if (validDocument ()) {
			tXMLElement = new XMLElement (document.getDocumentElement ());
		} else {
			tXMLElement = XMLElement.NO_XML_ELEMENT;
		}

		return tXMLElement;
	}

	public DOMSource getDOMSource () {
		return new DOMSource (document);
	}

	public void outputXML (File aFile) {
		String xmlString = "";

		try {
			FileWriter tFWout = new FileWriter (aFile);
			xmlString = toString ();

			tFWout.write ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			tFWout.write (xmlString);
			tFWout.close ();
		} catch (Exception tException) {
			System.err.println (tException);
			tException.printStackTrace ();
		}
	}

	@Override
	public String toString () {
		String xmlString = "";

		try {
			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance ();
			Transformer trans = transfac.newTransformer ();
			trans.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty (OutputKeys.INDENT, "yes");

			// create string from XML tree
			StringWriter sw = new StringWriter ();
			StreamResult result = new StreamResult (sw);
			DOMSource source = this.getDOMSource ();
			trans.transform (source, result);
			xmlString = sw.toString ();
//			xmlString = xmlString.replaceAll ("\n *\n", xmlString);
		} catch (Exception tException) {
			System.err.println (tException);
			tException.printStackTrace ();
		}

		return xmlString;
	}
}
