package ge18xx.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;

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
			document = null;
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
			document = null;
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
			document = null;
			tException.printStackTrace ();
		}
	}
	
	public XMLDocument ParseXMLString (String aXMLString) {
		DocumentBuilderFactory tDocBuilderFac;
		DocumentBuilder tDocBuilder;
		
		try {
			tDocBuilderFac = DocumentBuilderFactory.newInstance ();
			tDocBuilder = tDocBuilderFac.newDocumentBuilder ();
			document = tDocBuilder.parse (new InputSource (new StringReader (aXMLString)));
		} catch (Exception tException) {
			System.err.println ("Caught Exception " + tException);
			document = null;
			tException.printStackTrace ();
		}
		
		return this;
	}
	
	public boolean ValidDocument () {
		boolean tValidDocument;
		
		if (document == null) {
			tValidDocument = false;
		} else {
			tValidDocument = true;
		}
		
		return tValidDocument;
	}
	
	public void appendChild (XMLElement aXMLElement) {
		document.appendChild (aXMLElement.getElement ());
	}
	
	public XMLElement createElement (ElementName aEntityName) {
		XMLElement tXMLElement;
		
		tXMLElement = new XMLElement (document.createElement (aEntityName.getString ()));
		
		return tXMLElement;
	}
	
	public Document getDocument () {
		return document;
	}

	public XMLNode getDocumentElement () {
		return new XMLNode (document.getDocumentElement ());
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

	public String toString () {
		String xmlString = "";
		
		try {
			//set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance ();
			Transformer trans = transfac.newTransformer ();
			trans.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty (OutputKeys.INDENT, "yes");
			
			//create string from xml tree
			StringWriter sw = new StringWriter ();
			StreamResult result = new StreamResult (sw);
			DOMSource source = this.getDOMSource ();
			trans.transform (source, result);
			xmlString = sw.toString ();
//			xmlString = sw.toString ().replaceAll ("\r", "").replaceAll ("\n", "");
		} catch (Exception tException) {
			System.err.println (tException);
			tException.printStackTrace ();
		}

		return xmlString;
	}
}
