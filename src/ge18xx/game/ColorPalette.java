package ge18xx.game;

import java.awt.Color;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.io.IOException;

import org.w3c.dom.NodeList;

import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLNode;

public class ColorPalette implements LoadableXMLI {
	public static final AttributeName AN_COLOR = new AttributeName ("color");
	public static final AttributeName AN_HIGHLIGHT = new AttributeName ("highlight");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final ColorPalette NO_PALETTE = null;
	public ElementName EN_Tier1;
	public ElementName EN_Tier2;
	static Paint [] [] paints = null;
	int maxID;
	
	public ColorPalette (ElementName aEN_Tier1, ElementName aEN_Tier2) {
		setValues (aEN_Tier1, aEN_Tier2);
	}
	
	public ColorPalette (String aTier1Name, String aTier2Name) {
		ElementName tEN_Tier1;
		ElementName tEN_Tier2;
		
		tEN_Tier1 = new ElementName (aTier1Name);
		tEN_Tier2 = new ElementName (aTier2Name);
		
		setValues (tEN_Tier1, tEN_Tier2);
	}

	private void setValues (ElementName aEN_Tier1, ElementName aEN_Tier2) {
		EN_Tier1 = aEN_Tier1;
		EN_Tier2 = aEN_Tier2;
		maxID = 0;
	}
	
	public String getName () {
		return EN_Tier1.toString ();
	}
	
	@Override
	public String getTypeName () {
		return "PaintColor";
	}

	public Paint getPaint (int tIndex) {
		if ((tIndex >= 0) && (tIndex < paints.length)) {
			return paints [tIndex] [0];
		} else {
			return Color.BLACK;
		}
	}
	
	public Paint getHighlightPaint (int tIndex) {
		if ((tIndex >= 0) && (tIndex < paints.length)) {
			return paints [tIndex] [1];
		} else {
			return Color.BLACK;
		}
	}
	
	public boolean loadXML (XMLDocument aXMLDocument, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;

		try {
			aLoadableObject.loadXML (aXMLDocument);
			tXMLFileWasLoaded = true;
		} catch (Exception tException) {
			System.err.println ("Exception Message [" + tException.getMessage () + "].");
			tException.printStackTrace (System.err);
			tXMLFileWasLoaded = false;
		}

		return tXMLFileWasLoaded;
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNode XMLMapRoot;
		XMLNode tChildNode1;
		XMLNode tChildNode2;
		NodeList tChildren1;
		NodeList tChildren2;
		String tChildName1;
		String tChildName2;
		int tChildrenCount1;
		int tChildrenCount2;
		int tIndex1;
		int tIndex2;
		int tPaintCount;

		XMLMapRoot = aXMLDocument.getDocumentNode ();
		tChildren1 = XMLMapRoot.getChildNodes ();
		tChildrenCount1 = tChildren1.getLength ();
		for (tIndex1 = 0; tIndex1 < tChildrenCount1; tIndex1++) {
			tChildNode1 = new XMLNode (tChildren1.item (tIndex1));
			tChildName1 = tChildNode1.getNodeName ();
			if (EN_Tier1.equals (tChildName1)) {
				tChildren2 = tChildNode1.getChildNodes ();
				tChildrenCount2 = tChildren2.getLength ();
				tPaintCount = (tChildrenCount2 - 1)/2;
				paints = new Paint [tPaintCount] [2];
				
				for (tIndex2 = 0; tIndex2 < tChildrenCount2; tIndex2++) {
					tChildNode2 = new XMLNode (tChildren2.item (tIndex2));
					tChildName2 = tChildNode2.getNodeName ();
					if (EN_Tier2.equals (tChildName2)) {
						parseColor (tChildNode2);
					}
				}
			}
		}
	}

	public void parseColor (XMLNode aChildNode) {
		int tID;
		Color tColor;
		Color tHighlightColor;
		TexturePaint tTexturePaint;

		tID = aChildNode.getThisIntAttribute (AN_ID);
		tColor = aChildNode.getThisColorAttribute (AN_COLOR);
		tHighlightColor = aChildNode.getThisColorAttribute (AN_HIGHLIGHT);
		if (tID > maxID) {
			maxID = tID;
		}
		paints [tID] [0] = tColor;
		if (tHighlightColor != XMLNode.NO_COLOR) {
			tTexturePaint = XMLNode.createTexture (tColor, tHighlightColor);
			paints [tID] [1]  = tTexturePaint;
		} else {
			paints [tID] [1]  = tColor;
		}
	}
}
