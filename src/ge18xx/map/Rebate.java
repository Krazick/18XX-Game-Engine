package ge18xx.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import ge18xx.bank.Bank;

//
//  Rebate.java
//  Game_18XX
//
//  Created by Mark Smith on 1/13/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.tiles.Feature;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.xml.XMLNode;

public class Rebate extends Feature {
	public static final AttributeName AN_VALUE = new AttributeName ("value");
	public static final ElementName EN_REBATE = new ElementName ("Rebate");
	public static final Rebate NO_REBATE = null;
	public static final int NO_REBATE_VALUE = 0;
	int amount;

	public Rebate () {
		this (NO_REBATE_VALUE);
	}

	public Rebate (int aAmount) {
		this (aAmount, Location.CENTER_CITY_LOC);
	}

	public Rebate (int aAmount, int aLocation) {
		super (aLocation);
		amount = aAmount;
	}

	public Rebate (XMLNode aNode) {
		int tValue, tLocation;

		tValue = aNode.getThisIntAttribute (AN_VALUE);
		tLocation = aNode.getThisIntAttribute (Location.AN_LOCATION, Location.CENTER_CITY_LOC);
		amount = tValue;
		setLocation (tLocation);
	}

	public void draw (Graphics g, int Xc, int Yc, Hex aHex) {
		String tRebate;
		Point tDisplace;
		int tXc, tYc, tXUL, tYUL;
		int tWidth, tHeight;
		Font tNewFont;
		Font tCurrentFont;

		tRebate = getAmountToString ();
		if (tRebate != null) {
			tRebate = "(" + tRebate + ")";
			tDisplace = location.calcCenter (aHex);
			tCurrentFont = g.getFont ();
			tNewFont = new Font ("Dialog", Font.PLAIN, 10);
			g.setFont (tNewFont);
			tXc = Xc + tDisplace.x;
			tYc = Yc + tDisplace.y;
			tWidth = g.getFontMetrics ().stringWidth (tRebate);
			tHeight = g.getFontMetrics ().getHeight ();
			tXUL = tXc - tWidth / 2;
			tYUL = tYc - tHeight / 2;
			g.setColor (Color.black);
			g.drawString (tRebate, tXUL, tYUL);
			g.setFont (tCurrentFont);
		}
	}

	public int getAmount () {
		return amount;
	}

	public String getFormattedAmount () {
		return Bank.formatCash (amount);
	}
	
	public String getAmountToString () {
		String tRebate;

		if (amount > 0) {
			tRebate = Integer.valueOf (amount).toString ();
		} else {
			tRebate = null;
		}

		return (tRebate);
	}

	public void setValues (int aAmount, int aLocation) {
		setLocation (aLocation);
		amount = aAmount;
	}
}
