package ge18xx.center;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

//
//  Revenue.java
//  Game_18XX
//
//  Created by Mark Smith on 9/28/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.phase.PhaseInfo;
import ge18xx.tiles.Feature;
import ge18xx.tiles.TileType;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class Revenues extends Feature {
	public static final ElementName EN_REVENUE = new ElementName ("Revenue");
	public static final AttributeName AN_PHASE = new AttributeName ("phase");
	public static final AttributeName AN_LOCATION = new AttributeName ("location");
	public static final AttributeName AN_VALUE = new AttributeName ("value");
	public static final AttributeName AN_LAYOUT = new AttributeName ("layout");
	public static final Revenues NO_REVENUES = null;
	public static final int LIRA_SYMBOL = -1;
	public static final int LAYOUT_CIRCLE = 0;
	public static final int LAYOUT_OVAL = 1;
	public static final int LAYOUT_HORIZONTAL = 2;
	public static final int LAYOUT_VERTICAL = 3;
	public static final int LAYOUT_SPLIT = 4;
	public static final int MIN_LAYOUT_STYLE = LAYOUT_CIRCLE;
	public static final int MAX_LAYOUT_STYLE = LAYOUT_SPLIT;
	public static final String LAYOUT_NAMES[] = { "circle", "oval", "horizontal", "vertical", "split" };
	int layoutStyle;
	List<Revenue> revenues;

	public Revenues () {
		revenues = new LinkedList<Revenue> ();
		setValues (Revenue.NO_REVENUE_VALUE, Location.CENTER_CITY_LOC, Revenue.ALL_PHASES, LAYOUT_CIRCLE);
	}

	public Revenues (Revenues aRevenues) {
		int tValue;
		int tPhase;
		int tRevenueCount;
		int tRevenueIndex;

		revenues = new LinkedList<Revenue> ();
		if (aRevenues != NO_REVENUES) {
			tRevenueCount = aRevenues.getRevenueCount ();
			for (tRevenueIndex = 0; tRevenueIndex < tRevenueCount; tRevenueIndex++) {
				tValue = aRevenues.getValueIndex (tRevenueIndex);
				tPhase = aRevenues.getPhaseIndex (tRevenueIndex);
				addRevenue (tValue, tPhase);
			}
			layoutStyle = aRevenues.layoutStyle;
			setLocation (aRevenues.getLocation ());
		} else {
			setValues (Revenue.NO_REVENUE_VALUE, Location.CENTER_CITY_LOC, Revenue.ALL_PHASES, LAYOUT_CIRCLE);
		}
	}

	public Revenues (int aValue, int aLocation, int aPhase) {
		revenues = new LinkedList<Revenue> ();
		setValues (aValue, aLocation, aPhase, LAYOUT_CIRCLE);
	}

	public Revenues (int aValue, int aLocation, int aPhase, int aLayoutStyle) {
		revenues = new LinkedList<Revenue> ();
		setValues (aValue, aLocation, aPhase, aLayoutStyle);
	}
	
	public void addRevenue (Revenue aRevenue) {
		revenues.add (aRevenue);
	}

	@Override
	public Revenues clone () {
		Revenues tRevenues = (Revenues) super.clone ();
		int tRevenueCount;
		int tRevenueIndex;
		int tValue;
		int tPhase;
		
		tRevenues.revenues = new LinkedList<> ();
		tRevenueCount = getRevenueCount ();
		for (tRevenueIndex = 0; tRevenueIndex < tRevenueCount; tRevenueIndex++) {
			tValue = getValueIndex (tRevenueIndex);
			tPhase = getPhaseIndex (tRevenueIndex);
			tRevenues.addRevenue (tValue, tPhase);
		}
		tRevenues.layoutStyle = layoutStyle;
		tRevenues.setLocation (getLocation ());
		
		return tRevenues;
	}
	
	public void addRevenue (int aValue, int aPhase) {
		Revenue tRevenue;

		tRevenue = new Revenue (aValue, aPhase);
		addRevenue (tRevenue);
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement = XMLElement.NO_XML_ELEMENT;
		int tRevenueValue;

		for (Revenue tRevenue : revenues) {
			tRevenueValue = tRevenue.getValue ();
			if (tRevenueValue > Revenue.NO_REVENUE_VALUE) {
				tXMLElement = aXMLDocument.createElement (EN_REVENUE);
				tXMLElement.setAttribute (AN_PHASE, tRevenue.getPhaseToString ());
				tXMLElement.setAttribute (AN_LOCATION, getLocationToString ());
				tXMLElement.setAttribute (AN_VALUE, tRevenueValue);
				tXMLElement.setAttribute (AN_LAYOUT, LAYOUT_NAMES [layoutStyle]);
			}
		}

		return tXMLElement;
	}

	/**
	 * Test if the Location for the Revenues is a Valid Location
	 * 
	 * @return False of the location is a Not a valid Location otherwise True
	 */
	public boolean isValidLocation () {
		boolean tIsValidLocation;

		if (location.isNoLocation ()) {
			tIsValidLocation = false;
		} else {
			tIsValidLocation = true;
		}
		
		return tIsValidLocation;
	}
	
	public void draw (Graphics aGraphics, int aXc, int aYc, Hex aHex, int aTileOrientation, TileType aTileType) {
		int tWidth;
		int tHeight;
		int tValue;
		int tXc;
		int tYc;
		int tRevenueCount;
		int tRevenueIndex;
		int tRevenueShownCount;
		String tValueLabel;
		String tHorizontalLabel;
		Point tDisplace;
		Font tCurrentFont;
		Location tNewLocation;
		Color tRevenueColor;
		Paint tTilePaint;
		Graphics2D tGraphics2D;

		tGraphics2D = (Graphics2D) aGraphics;
		tNewLocation = location.rotateLocation (aTileOrientation);
		tCurrentFont = setRevenueFont (tGraphics2D);
		tDisplace = tNewLocation.calcCenter (aHex);
		tXc = aXc + tDisplace.x;
		tYc = aYc + tDisplace.y;
		tRevenueCount = getRevenueCount ();
		if (tRevenueCount > 0) {
			tHorizontalLabel = "";
			tRevenueShownCount = 0;
			if (aTileType != TileType.NO_TILE_TYPE) {
				tTilePaint = aTileType.getPaint ();
				tRevenueColor = aTileType.getRevenueColor ();
			} else {
				tTilePaint = Color.CYAN;
				tRevenueColor = Color.BLACK;
			}
			for (tRevenueIndex = 1; tRevenueIndex < tRevenueCount; tRevenueIndex++) {
				tValue = getValueIndex (tRevenueIndex);
				tValueLabel = getValueIndexToString (tRevenueIndex);
				tWidth = tGraphics2D.getFontMetrics ().stringWidth (tValueLabel);
				tHeight = tGraphics2D.getFontMetrics ().getHeight ();
				switch (layoutStyle) {
					case (LAYOUT_CIRCLE):
						drawRevenueCircle (tWidth, tHeight, tValueLabel, tValue, tXc, tYc, tRevenueColor,
								tTilePaint, tGraphics2D);
						break;

					case (LAYOUT_HORIZONTAL):
						tHorizontalLabel = tHorizontalLabel + tValueLabel + "/";
						break;

					case (LAYOUT_VERTICAL):
						tRevenueShownCount = drawRevenueVertical (tWidth, tHeight, tValueLabel, tValue, tXc, tYc,
								tRevenueCount, tRevenueShownCount, tGraphics2D);
						break;

					case (LAYOUT_SPLIT):
						tRevenueShownCount = drawRevenueSplit (tWidth, tHeight, tValueLabel, tValue, tXc, tYc,
								tRevenueShownCount, tGraphics2D);
						break;
				}
			}
			drawRevenueHorizontal (tHorizontalLabel, tXc, tYc, tRevenueColor, tGraphics2D);
		}
		tGraphics2D.setFont (tCurrentFont);
	}

	private Font setRevenueFont (Graphics2D aGraphics2D) {
		Font tNewFont;
		Font tCurrentFont;
		
		tCurrentFont = aGraphics2D.getFont ();
		tNewFont = new Font ("Dialog", Font.PLAIN, 10);
		aGraphics2D.setFont (tNewFont);
		
		return tCurrentFont;
	}

	private void drawRevenueHorizontal (String aHorizontalLabel, int aXc, int aYc, Color aRevenueColor, Graphics2D aGraphics2D) {
		int tWidth;
		int tHeight;
		
		if ((layoutStyle == LAYOUT_HORIZONTAL) && (aHorizontalLabel.length () > 0)) {
			aHorizontalLabel = aHorizontalLabel.substring (0, aHorizontalLabel.length () - 1);
			aHorizontalLabel = "(" + aHorizontalLabel + ")";
			tWidth = aGraphics2D.getFontMetrics ().stringWidth (aHorizontalLabel);
			tHeight = aGraphics2D.getFontMetrics ().getHeight ();
			aGraphics2D.setColor (aRevenueColor);
			aGraphics2D.drawString (aHorizontalLabel, aXc - tWidth / 2, aYc - tHeight / 2 - 1);
		}
	}

	private int drawRevenueSplit (int aWidth, int aHeight, String aValueLabel, int aValue, int aXc, int aYc, 
			int aRevenueShownCount, Graphics2D aGraphics2D) {
		int tBoxWidth;
		int tXUpperLeft;
		int tYUpperLeft;
		
		if (aValue > 0) {
			tBoxWidth = aWidth * 2;
			tXUpperLeft = aXc - aWidth;
			tYUpperLeft = aYc - aWidth;
			if (aRevenueShownCount == 0) {
				// To highlight the Current Phase Value either:
				// 1) Draw a Triangle over the area and choose different background, drawing the
				// triangle region, over top of the "White" Rectangle
				// --- Hex class (passed in) has a drawTriangle Method, just provide the points,
				// and a fill color
				// 2) Change the Color of the Text to something different, like "RED"
				aGraphics2D.setColor (Color.white);
				aGraphics2D.fillRect (tXUpperLeft, tYUpperLeft, tBoxWidth, tBoxWidth);
				// Draw Triangle Here
				// Problem is finding the current Phase to see which color to use --
				aGraphics2D.setColor (Color.black);
				aGraphics2D.drawRect (tXUpperLeft, tYUpperLeft, tBoxWidth, tBoxWidth);
				aGraphics2D.drawLine (tXUpperLeft + tBoxWidth, tYUpperLeft, tXUpperLeft, tYUpperLeft + tBoxWidth);
				aGraphics2D.drawString (aValueLabel, tXUpperLeft + 1, tYUpperLeft + aHeight - 1);
			} else if (aRevenueShownCount == 1) {
				aGraphics2D.drawString (aValueLabel, tXUpperLeft + aWidth - 2, tYUpperLeft + aWidth + aHeight - 3);
			}
			aRevenueShownCount++;
		}
		
		return aRevenueShownCount;
	}

	private int drawRevenueVertical (int tWidth, int tHeight, String aValueLabel, int aValue, int aXc, int aYc, int aRevenueCount,
			int aRevenueShownCount, Graphics2D aGraphics2D) {
		int tBoxWidth;
		int tBoxHeight;
		int tXUpperLeft;
		int tYUpperLeft;
		
		if (aValue > 0) {
			tBoxWidth = tWidth + 2;
			tBoxHeight = tHeight * (aRevenueCount - 1) + 1;
			tXUpperLeft = aXc - tBoxWidth / 2;
			tYUpperLeft = aYc - tBoxHeight / 2;
			if (aRevenueShownCount == 0) {
				aGraphics2D.setColor (Color.white);
				aGraphics2D.fillRect (tXUpperLeft, tYUpperLeft, tBoxWidth, tBoxHeight);
				aGraphics2D.setColor (Color.black);
				aGraphics2D.drawRect (tXUpperLeft, tYUpperLeft, tBoxWidth, tBoxHeight);
			}
			aGraphics2D.drawString (aValueLabel, tXUpperLeft + 1, tYUpperLeft + (tHeight * (aRevenueShownCount + 1)) - 1);
			aRevenueShownCount++;
		}
		
		return aRevenueShownCount;
	}

	private void drawRevenueCircle (int aWidth, int aHeight, String aValueLabel, int aValue, int aXc, int aYc,
			Color aRevenueColor, Paint aTilePaint, Graphics2D aGraphics2D) {
		int tXUpperLeft;
		int tYUpperLeft;
		int tCircleRadius;
		int tCircleDiameter;
		int tOvalHeight;
		int tOvalWidth;
		
		if (aValue > 0) {
			if (aWidth > aHeight) {
				tCircleRadius = aWidth / 2 + 2;
			} else {
				tCircleRadius = aHeight / 2 + 2;
			}
			tCircleDiameter = tCircleRadius * 2;
			tXUpperLeft = aXc - tCircleRadius;
			tYUpperLeft = aYc - tCircleRadius;
			tOvalWidth = tCircleDiameter;
			if (aValue > 99) {
				tOvalHeight = aHeight + 3;
			} else {
				tOvalHeight = tCircleRadius * 2;
			}
			aGraphics2D.setPaint (aTilePaint);
			aGraphics2D.fillOval (tXUpperLeft, tYUpperLeft, tOvalWidth, tOvalHeight);
			aGraphics2D.setColor (aRevenueColor);
			aGraphics2D.drawOval (tXUpperLeft, tYUpperLeft, tOvalWidth, tOvalHeight);
			aGraphics2D.drawString (aValueLabel, tXUpperLeft + 2, tYUpperLeft + aHeight - 1);
		}
	}

	public int getLayoutFromName (String aName) {
		int index;
		int thisLayout;

		thisLayout = LAYOUT_CIRCLE;
		if (aName != null) {
			for (index = MIN_LAYOUT_STYLE; index <= MAX_LAYOUT_STYLE; index++) {
				if (aName.equals (LAYOUT_NAMES [index])) {
					thisLayout = index;
				}
			}
		}

		return thisLayout;
	}

	public int getValue () {
		int tValue;
		int tPossibleValue;
		boolean tValueFound;

		tValue = 0;
		tValueFound = false;
		for (Revenue tRevenue : revenues) {
			tPossibleValue = tRevenue.getValue ();
			if (tPossibleValue > 0) {
				if (!tValueFound) {
					tValue = tPossibleValue;
					tValueFound = true;
				}
			}
		}

		return tValue;
	}

	public int getValue (int aPhase) {
		int tValue;
		int tPhase;

		if (aPhase == PhaseInfo.NO_NAME) {
			tValue = getValue ();
		} else {
			tValue = 0;
			for (Revenue tRevenue : revenues) {
				tPhase = tRevenue.getPhase ();
				if (tPhase <= aPhase) {
					tValue = tRevenue.getValue ();
				}
			}
		}

		return tValue;
	}

	public int getValueIndex (int aIndex) {
		int tValue;

		tValue = revenues.get (aIndex).getValue ();

		return tValue;
	}

	public int getPhaseIndex (int aIndex) {
		int tPhase;

		tPhase = revenues.get (aIndex).getPhase ();

		return tPhase;
	}

	public String getPhaseIndexToString (int aIndex) {
		String tPhase;

		tPhase = revenues.get (aIndex).getPhaseToString ();

		return tPhase;
	}

	public int getRevenueCount () {
		return revenues.size ();
	}

	public String getValueIndexToString (int aIndex) {
		String tValue;

		tValue = revenues.get (aIndex).getValueToString ();

		return tValue;
	}

	public void load (XMLNode aNode) {
		int tValue, tLocation, tPhase, tLayoutStyle;
		String tLayoutString;

		tValue = aNode.getThisIntAttribute (AN_VALUE);
		tLocation = aNode.getThisIntAttribute (AN_LOCATION);
		tPhase = aNode.getThisIntAttribute (AN_PHASE);
		tLayoutString = aNode.getThisAttribute (AN_LAYOUT);
		tLayoutStyle = getLayoutFromName (tLayoutString);
		setValues (tValue, tLocation, tPhase, tLayoutStyle);
	}

	@Override
	public void printlog () {
		int tIndex;
		int tRevenueCount;

		tRevenueCount = getRevenueCount ();
		System.out.println ("Revenue Count is " + tRevenueCount);	// PRINTLOG method
		for (tIndex = 0; tIndex < tRevenueCount; tIndex++) {
			System.out.println ("Revenue " + tIndex + " is " + getValueIndexToString (tIndex) + " Phase "
					+ getPhaseIndexToString (tIndex));
		}
	}

	public void setValues (int aValue, int aLocation, int aPhase, int aLayoutStyle) {
		setLocation (aLocation);
		addRevenue (aValue, aPhase);
		layoutStyle = aLayoutStyle;
	}
}
