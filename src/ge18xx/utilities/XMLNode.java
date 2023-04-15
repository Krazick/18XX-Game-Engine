package ge18xx.utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

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
	public static final Color NO_COLOR = null;
	public static final TexturePaint NO_TEXTURE_PAINT = null;
	public static final String NO_VALUE = GUI.NULL_STRING;
	public static final int GRID_SIZE = 5;
	Node node;

	public XMLNode (Node aNode) {
		node = aNode;
	}

	public XMLElement getXMLElement () {
		XMLElement tXMLElement;

		tXMLElement = new XMLElement (node);

		return tXMLElement;
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
		String tAttributeValue;
		String tAttributeName;

		tAttributeValue = NO_VALUE;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisAttribute (tAttributeName);
		}

		return tAttributeValue;
	}

	public String getThisAttribute (AttributeName aAttributeName, String aDefaultValue) {
		String tAttributeValue;
		String tAttributeName;

		tAttributeValue = aDefaultValue;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisAttribute (tAttributeName, aDefaultValue);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private String getThisAttribute (String aAttributeName) {
		String tAttributeValue;
		NamedNodeMap tAttributesNNM;
		Attr tAttribute;
		int tAttributeCount;
		int tAttributeIndex;
		String tThisAttributeName;

		tAttributeValue = NO_VALUE;
		tAttributesNNM = node.getAttributes ();
		tAttributeCount = tAttributesNNM.getLength ();
		for (tAttributeIndex = 0; tAttributeIndex < tAttributeCount; tAttributeIndex++) {
			tAttribute = (Attr) tAttributesNNM.item (tAttributeIndex);
			tThisAttributeName = tAttribute.getNodeName ();
			if (aAttributeName.equals (tThisAttributeName)) {
				tAttributeValue = tAttribute.getNodeValue ();
			}
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private String getThisAttribute (String aAttributeName, String aDefaultValue) {
		String tValue;

		tValue = getThisAttribute (aAttributeName);
		if (tValue == NO_VALUE) {
			tValue = aDefaultValue;
		} else if (tValue.equals (GUI.EMPTY_STRING)) {
			tValue = aDefaultValue;
		}

		return tValue;
	}

	/**
	 * Parse out a Boolean Attribute, return -false- if attribute is not found
	 *
	 * @param aAttributeName The AttributeName to be found
	 * @return TRUE if the attribute Exists, and has value of TRUE, otherwise FALSE
	 */
	public boolean getThisBooleanAttribute (AttributeName aAttributeName) {
		boolean tAttributeValue;
		String tAttributeName;

		tAttributeValue = false;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisBooleanAttribute (tAttributeName);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private boolean getThisBooleanAttribute (String aAttributeName) {
		String tValue;
		boolean retValue;

		tValue = getThisAttribute (aAttributeName);
		retValue = false;
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
		int tAttributeValue;
		String tAttributeName;

		tAttributeValue = 0;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisIntAttribute (tAttributeName, tAttributeValue);
		}

		return tAttributeValue;
	}

	public int getThisIntAttribute (AttributeName aAttributeName, int aDefaultValue) {
		int tAttributeValue;
		String tAttributeName;

		tAttributeValue = aDefaultValue;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisIntAttribute (tAttributeName, aDefaultValue);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private int getThisIntAttribute (String aAttributeName, int aDefaultValue) {
		String tValue;

		tValue = getThisAttribute (aAttributeName);
		if (tValue == NO_VALUE) {
			return aDefaultValue;
		} else {
			return Integer.parseInt (tValue);
		}
	}

	public long getThisLongAttribute (AttributeName aAttributeName) {
		long tAttributeValue;
		String tAttributeName;

		tAttributeValue = 0;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisLongAttribute (tAttributeName, tAttributeValue);
		}

		return tAttributeValue;
	}

	public long getThisLongAttribute (AttributeName aAttributeName, long aDefaultValue) {
		long tAttributeValue;
		String tAttributeName;

		tAttributeValue = aDefaultValue;
		if (aAttributeName.hasValue ()) {
			tAttributeName = aAttributeName.getString ();
			tAttributeValue = getThisLongAttribute (tAttributeName, aDefaultValue);
		}

		return tAttributeValue;
	}

	/* PRIVATE */
	private long getThisLongAttribute (String aAttributeName, long aDefaultValue) {
		String tValue;
		long tLongValue;
		
		tValue = getThisAttribute (aAttributeName);
		if (tValue == NO_VALUE) {
			tLongValue = aDefaultValue;

		} else {
			tLongValue = Long.parseLong (tValue);
		}
		
		return tLongValue;
	}

	public Color getThisColorAttribute (AttributeName aAttributeName) {
		String tColorValues;
		Color tColor;

		tColorValues = getThisAttribute (aAttributeName);
		if (tColorValues != NO_VALUE) {
			tColor = parseAColor (tColorValues);
		} else {
			tColor = NO_COLOR;
		}

		return tColor;
	}

	public Color parseAColor (String tColorValues) {
		int tRed;
		int tGreen;
		int tBlue;
		String [] tSplit;
		Color tColor;

		tSplit = tColorValues.split (",");
		if (tSplit.length == 3) {
			tRed = Integer.parseInt (tSplit [0]);
			tGreen = Integer.parseInt (tSplit [1]);
			tBlue = Integer.parseInt (tSplit [2]);
			tColor = new Color (tRed, tGreen, tBlue);
		} else {
			tColor = NO_COLOR;
		}

		return tColor;
	}

	public static TexturePaint createTexture (Color aBaseColor, Color aHighlightColor) {
		return createTexture (aBaseColor, aHighlightColor, GRID_SIZE);
	}

	public static TexturePaint createTexture (Color aBaseColor, Color aHighlightColor, int aSize) {
		TexturePaint tTexturePaint;
		BufferedImage tBufferenedImage;
		Graphics2D tBufferedGraphic;
		Rectangle tRectangle;

		if ((aBaseColor != NO_COLOR) && (aHighlightColor != NO_COLOR)) {
			tBufferenedImage = new BufferedImage (aSize, aSize, BufferedImage.TYPE_INT_RGB);
			tBufferedGraphic = tBufferenedImage.createGraphics ();
			tBufferedGraphic.setColor (aHighlightColor);
			tBufferedGraphic.fillRect (0, 0, aSize, aSize);
			tBufferedGraphic.setColor (aBaseColor);
			tBufferedGraphic.fillOval (0, 0, aSize, aSize);
			tRectangle = new Rectangle (0, 0, aSize, aSize);
			tTexturePaint = new TexturePaint (tBufferenedImage, tRectangle);
		} else {
			tTexturePaint = NO_TEXTURE_PAINT;
		}

		return tTexturePaint;
	}

	@Override
	public String toString () {
		String tFormattedOutput;
		StringBuffer buff;

		buff = new StringBuffer (1024);
		XMLTransformer.getXMLString (node, false, buff, true);

		tFormattedOutput = buff.toString ();

		return tFormattedOutput;
	}

	public XMLNode getNode (ElementName aElementName) {
		XMLNode tFoundNode;
		NodeList tNodeList;
		int tNodeIndex;
		int tNodeCount;
		Node tNode;
		String tNodeName;
		String tElementName;

		tFoundNode = NO_NODE;
		if (node.hasChildNodes ()) {
			tNodeList = node.getChildNodes ();
			tNodeCount = tNodeList.getLength ();
			tElementName = aElementName.getString ();
			for (tNodeIndex = 0; tNodeIndex < tNodeCount; tNodeIndex++) {
				tNode = tNodeList.item (tNodeIndex);
				tNodeName = tNode.getNodeName ();
				if (tElementName.equals (tNodeName)) {
					tFoundNode = new XMLNode (tNode);
				}
			}
		}

		return tFoundNode;
	}
}
