package ge18xx.company;

import java.awt.Graphics;

//
//  Token.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class Token {
	public static final ElementName EN_TOKEN = new ElementName ("Token");
	public static final Token NO_TOKEN = null;
	public static final int NO_COST = 0;
	public static final int RANGE_COST = -1;
	TokenCompany whichCompany;

	public Token () {
		setCompany (TokenCompany.NO_TOKEN_COMPANY);
	}

	public Token (TokenCompany aTokenCompany) {
		setCompany (aTokenCompany);
	}

	public Token (Token aToken) {
		TokenCompany tCompany;

		tCompany = aToken.getWhichCompany ();
		setCompany (tCompany);
	}

	public void drawToken (Graphics g, int X1, int Y1, int width, int height) {
		whichCompany.drawToken (g, X1, Y1, width, height);
	}

	public String getCorporationAbbrev () {
		return whichCompany.getAbbrev ();
	}

	public int getCorporationID () {
		return whichCompany.getID ();
	}

	public String getCorporationStatus () {
		return whichCompany.getStatusName ();
	}

	public XMLElement getTokenElement (XMLDocument aXMLDocument) {
		XMLElement tTokenElement;

		tTokenElement = aXMLDocument.createElement (EN_TOKEN);
		fillTokenElement (tTokenElement);
		
		return tTokenElement;
	}

	protected void fillTokenElement (XMLElement aTokenElement) {
		aTokenElement.setAttribute (Corporation.AN_ABBREV, getCorporationAbbrev ());
	}

	public TokenCompany getWhichCompany () {
		return whichCompany;
	}

	public boolean isCorporationAbbrev (String aCorporationAbbrev) {
		return (aCorporationAbbrev.equals (getCorporationAbbrev ()));
	}

	public boolean isSameCompany (Token aToken) {
		return (whichCompany == aToken.getWhichCompany ());
	}

	public void printlog () {
		System.out.println ("Token for " + whichCompany.getAbbrev ());
	}

	public void setCompany (TokenCompany aCompany) {
		whichCompany = aCompany;
	}
	
	public boolean isAMapToken () {
		return false;
	}
}
