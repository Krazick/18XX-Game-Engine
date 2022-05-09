package ge18xx.utilities;

//
//  XMLNode.java
//  Game_18XX
//
//  Created by Mark Smith on 10/13/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLNode {
	public static final String XML_TEXT_TAG = "#text";
	public static final XMLNode NO_NODE = null;
	public static final String NO_VALUE = GUI.NULL_STRING;
	Node node;

	public XMLNode (Node aNode) {
		node = aNode;
	}

	public Node getNode () {
		return node;
	}

	public String getNodeName () {
		return node.getNodeName ();
	}

	public NamedNodeMap getAttributes () {
		return node.getAttributes ();
	}

	public NodeList getChildNodes () {
		return node.getChildNodes ();
	}

	public String getThisAttribute (AttributeName aAttributeName) {
		String tAttributeValue = NO_VALUE;
		String tAttributeName;

		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisAttribute (tAttributeName);
		}

		return tAttributeValue;
	}

	public String getThisAttribute (AttributeName aAttributeName, String aDefaultValue) {
		String tAttributeValue = aDefaultValue;
		String tAttributeName;

		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisAttribute (tAttributeName, aDefaultValue);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private String getThisAttribute (String aAttributeName) {
		String tAttributeValue = NO_VALUE;
		NamedNodeMap tAttributesNNM;
		Attr tAttribute;
		int tAttributeCount;
		int tAttributeIndex;

		tAttributesNNM = node.getAttributes ();
		tAttributeCount = tAttributesNNM.getLength ();
		for (tAttributeIndex = 0; tAttributeIndex < tAttributeCount; tAttributeIndex++) {
			tAttribute = (Attr) tAttributesNNM.item (tAttributeIndex);
			if (aAttributeName.equals (tAttribute.getNodeName ())) {
				tAttributeValue = tAttribute.getNodeValue ();
			}
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private String getThisAttribute (String aAttributeName, String aDefaultValue) {
		String tValue = getThisAttribute (aAttributeName);

		if (tValue == NO_VALUE) {
			return aDefaultValue;
		} else {
			return tValue;
		}
	}

	/* Parse out a Boolean Attribute, return -false- if attribute is not found */
	public boolean getThisBooleanAttribute (AttributeName aAttributeName) {
		boolean tAttributeValue = false;
		String tAttributeName;

		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisBooleanAttribute (tAttributeName);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private boolean getThisBooleanAttribute (String aAttributeName) {
		String tValue = getThisAttribute (aAttributeName);
		boolean retValue = false;

		if (tValue == NO_VALUE) {
			retValue = false;
		} else if ((tValue.equals ("TRUE")) || (tValue.equals ("true")) || (tValue.equals ("True"))
				|| (tValue.equals ("T")) || (tValue.equals ("t")) || (tValue.equals ("YES")) || (tValue.equals ("yes"))
				|| (tValue.equals ("Yes")) || (tValue.equals ("Y")) || (tValue.equals ("y"))) {
			retValue = true;
		} else if ((tValue.equals ("FALSE")) || (tValue.equals ("false")) || (tValue.equals ("False"))
				|| (tValue.equals ("F")) || (tValue.equals ("f")) || (tValue.equals ("NO")) || (tValue.equals ("no"))
				|| (tValue.equals ("No")) || (tValue.equals ("N")) || (tValue.equals ("n"))) {

			retValue = false;
		}

		return retValue;
	}

	public int getThisIntAttribute (AttributeName aAttributeName) {
		int tAttributeValue = 0;
		String tAttributeName;

		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisIntAttribute (tAttributeName, 0);
		}

		return tAttributeValue;
	}

	public int getThisIntAttribute (AttributeName aAttributeName, int aDefaultValue) {
		int tAttributeValue = aDefaultValue;
		String tAttributeName;

		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisIntAttribute (tAttributeName, aDefaultValue);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private int getThisIntAttribute (String aAttributeName, int aDefaultValue) {
		String tValue = getThisAttribute (aAttributeName);

		if (tValue == NO_VALUE) {
			return aDefaultValue;
		} else {
			return Integer.parseInt (tValue);
		}
	}

	@Override
	public String toString () {
		String tFormattedOutput;
		StringBuffer buff = new StringBuffer (1024);

		XMLTransformer.getXMLString (node, false, buff, true);

		tFormattedOutput = buff.toString ();

		return tFormattedOutput;
	}
}
