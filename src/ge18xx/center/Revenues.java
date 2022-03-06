package ge18xx.center;

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
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.List;
import java.util.LinkedList;

public class Revenues extends Feature {
	public static final ElementName EN_REVENUE = new ElementName ("Revenue");
	public static final AttributeName AN_PHASE = new AttributeName ("phase");
	public static final AttributeName AN_LOCATION = new AttributeName ("location");
	public static final AttributeName AN_VALUE = new AttributeName ("value");
	public static final AttributeName AN_LAYOUT = new AttributeName ("layout");
	public static final Revenues NO_REVENUES = null;
	static final int LIRA_SYMBOL = -1;
	static final int LAYOUT_CIRCLE = 0;
	static final int LAYOUT_OVAL = 1;
	static final int LAYOUT_HORIZONTAL = 2;
	static final int LAYOUT_VERTICAL = 3;
	static final int LAYOUT_SPLIT = 4;
	static final int MIN_LAYOUT_STYLE = LAYOUT_CIRCLE;
	static final int MAX_LAYOUT_STYLE = LAYOUT_SPLIT;
	static final String LAYOUT_NAMES [] = {"circle", "oval", "horizontal", "vertical", "split"};
	int layoutStyle;
	List<Revenue> revenues = new LinkedList<Revenue> ();
	
	public Revenues () {
		revenues = new LinkedList<Revenue> ();
		setValues (Revenue.NO_REVENUE_VALUE, Location.CENTER_CITY_LOC, Revenue.ALL_PHASES, LAYOUT_CIRCLE);
	}
	
	public Revenues (Revenues aRevenues) {
		int tValue, tPhase;
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
	
	public void draw (Graphics g, int Xc, int Yc, Hex aHex, int aTileOrientation, TileType aTileType) {
		int tWidth, tHeight;
		int tBoxWidth, tBoxHeight;
		String tValueLabel;
		String tHorizontalLabel;
		int tValue;
		int XUL, YUL;
		int tXc, tYc;
		int tCircleRadius, tCircleDiameter;
		int tOvalHeight, tOvalWidth;
		int tRevenueCount, tRevenueIndex, tRevenueShownCount;
		Point tDisplace;
		Font tNewFont;	
		Font tCurrentFont;
		Location tNewLocation;
		
		if (! location.isNoLocation ()) {
			tNewLocation = location.rotateLocation (aTileOrientation);
			tCurrentFont = g.getFont ();
			tNewFont = new Font ("Dialog", Font.PLAIN, 10);
			g.setFont (tNewFont);
			tDisplace = tNewLocation.calcCenter (aHex);
			tXc = Xc + tDisplace.x;
			tYc = Yc + tDisplace.y;
			tRevenueCount = getRevenueCount ();
			if (tRevenueCount > 0) {
				tHorizontalLabel = "";
				tRevenueShownCount = 0;
				for (tRevenueIndex = 0; tRevenueIndex < tRevenueCount; tRevenueIndex++) {
					tValue = getValueIndex (tRevenueIndex);
					if (tValue > 0) {
						tValueLabel = getValueIndexToString (tRevenueIndex);
						tWidth = g.getFontMetrics ().stringWidth (tValueLabel);
						tHeight = g.getFontMetrics ().getHeight ();
						switch (layoutStyle) {
							case (LAYOUT_CIRCLE):
								if (tWidth > tHeight) {
									tCircleRadius = tWidth/2 + 2;
								} else {
									tCircleRadius = tHeight/2 + 2;
								}
								tCircleDiameter = tCircleRadius * 2;
								XUL = tXc - tCircleRadius;
								YUL = tYc - tCircleRadius;
								tOvalWidth = tCircleDiameter;
								if (tValue > 99) {
									tOvalHeight = tHeight + 3;
								} else {
									tOvalHeight = tCircleRadius * 2;
								}
								g.setColor (aTileType.getRevenueColor ());
								g.drawOval (XUL, YUL, tOvalWidth, tOvalHeight);
								g.drawString (tValueLabel, XUL + 2, YUL + tHeight - 1);
								break;
							
							case (LAYOUT_HORIZONTAL):
								tHorizontalLabel = tHorizontalLabel + tValueLabel + "/";
								break;
								
							case (LAYOUT_VERTICAL):
								tBoxWidth = tWidth + 2;
								tBoxHeight = tHeight * (tRevenueCount - 1) + 1;
								XUL = tXc - tBoxWidth/2;
								YUL = tYc - tBoxHeight/2;
								if (tRevenueShownCount == 0) {
									g.setColor (Color.white);
									g.fillRect (XUL, YUL, tBoxWidth, tBoxHeight);
									g.setColor (Color.black);
									g.drawRect (XUL, YUL, tBoxWidth, tBoxHeight);
								}
								g.drawString (tValueLabel, XUL + 1, YUL + (tHeight * (tRevenueShownCount + 1)) - 1);
								tRevenueShownCount++;
								break;
								
							case (LAYOUT_SPLIT):
								tBoxWidth = tWidth * 2;
								XUL = tXc - tWidth;
								YUL = tYc - tWidth;
								if (tRevenueShownCount == 0) {
									// To highlight the Current Phase Value either:
									// 1) Draw a Triangle over the area and choose different background, drawing the triangle region, over top of the "White" Rectangle
									// --- Hex class (passed in) has a drawTriangle Method, just provide the points, and a fill color
									// 2) Change the Color of the Text to something different, like "RED"
									g.setColor (Color.white);
									g.fillRect (XUL, YUL, tBoxWidth, tBoxWidth);
									// Draw Triangle Here
									// Problem is finding the current Phase to see which color to use -- 
									g.setColor (Color.black);
									g.drawRect (XUL, YUL, tBoxWidth, tBoxWidth);
									g.drawLine (XUL + tBoxWidth, YUL, XUL, YUL + tBoxWidth);
									g.drawString (tValueLabel, XUL + 1, YUL + tHeight - 1);
								}
								if (tRevenueShownCount == 1) {
									g.drawString (tValueLabel, XUL + tWidth - 2, YUL + tWidth + tHeight - 3);
								}
								tRevenueShownCount++;
								break;
						}
					}
				}
				if ((layoutStyle == LAYOUT_HORIZONTAL) && (tHorizontalLabel.length () > 0)) {
					tHorizontalLabel = tHorizontalLabel.substring (0, tHorizontalLabel.length () - 1);
					tHorizontalLabel = "(" + tHorizontalLabel + ")";
					tWidth = g.getFontMetrics ().stringWidth (tHorizontalLabel);
					tHeight = g.getFontMetrics ().getHeight ();
					g.drawString (tHorizontalLabel, tXc - tWidth/2, tYc - tHeight/2 - 1);
				}
			}
			g.setFont (tCurrentFont);
		}
	}
	
	public int getLayoutFromName (String aName) {
		int index;
		int thisLayout = LAYOUT_CIRCLE;
		
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
		int tValue = 0;
		int tPossibleValue;
		boolean tValueFound = false;
		
		for (Revenue tRevenue : revenues) {
			tPossibleValue = tRevenue.getValue ();
			if (tPossibleValue > 0) {
				if (! tValueFound) {
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
		int tCurrentIndex = 0;
		
		tValue = 0;
		for (Revenue tRevenue : revenues) {
			if (tCurrentIndex == aIndex) {
				tValue = tRevenue.getValue ();
			}
			tCurrentIndex++;
		}
		
		return tValue;
	}
	
	public String getValueToString () {
		String tValue = "";
		
		for (Revenue tRevenue : revenues) {
			tValue = tRevenue.getValueToString ();
		}
		
		return tValue;
	}
	
	public int getPhase () {
		int tPhase = 0;
		
		for (Revenue tRevenue : revenues) {
			tPhase = tRevenue.getPhase ();
		}
		
		return tPhase;
	}
	
	public int getPhaseIndex (int aIndex) {
		int tPhase = 0;
		int tCurrentIndex = 0;
		
		for (Revenue tRevenue : revenues) {
			if (tCurrentIndex == aIndex) {
				tPhase = tRevenue.getPhase ();
			}
			tCurrentIndex++;
		}
		
		return tPhase;
	}
	
	public String getPhaseIndexToString (int aIndex) {
		String tPhase = "";
		int tCurrentIndex = 0;
		
		for (Revenue tRevenue : revenues) {
			if (tCurrentIndex == aIndex) {
				tPhase = tRevenue.getPhaseToString ();
			}
			tCurrentIndex++;
		}
		
		return tPhase;
	}
	
	public String getPhaseToString () {
		String tPhase = "";
		
		for (Revenue tRevenue : revenues) {
			tPhase = tRevenue.getPhaseToString ();
		}
		
		return tPhase;
	}
	
	public int getRevenueCount () {
		return revenues.size ();
	}
	
	public String getValueIndexToString (int aIndex) {
		String tValue = "";
		int tCurrentIndex = 0;
		
		for (Revenue tRevenue : revenues) {
			if (tCurrentIndex == aIndex) {
				tValue = tRevenue.getValueToString ();
			}
			tCurrentIndex++;
		}
		
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
		System.out.println ("Revenue Count is " + tRevenueCount);
		for (tIndex = 0; tIndex < tRevenueCount; tIndex++) {
			System.out.println ("Revenue " + tIndex + " is " + getValueIndexToString (tIndex) + " Phase " + getPhaseIndexToString (tIndex));
		}
	}
	
	public void setValues (int aValue, int aLocation, int aPhase, int aLayoutStyle) {
		setLocation (aLocation);
		addRevenue (aValue, aPhase);
		layoutStyle = aLayoutStyle;
	}
}
