package ge18xx.company;

import java.awt.Graphics;

import geUtilities.GUI;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public class Token {
	public static final ElementName EN_TOKEN = new ElementName ("Token");
	public static final Token NO_TOKEN = null;
	public static final int NO_COST = 0;
	public static final int RANGE_COST = -1;
	TokenCompany tokenCompany;
	TokenInfo.TokenType type;

	public Token () {
		setCompany (TokenCompany.NO_TOKEN_COMPANY);
	}

	public Token (TokenCompany aTokenCompany, TokenInfo.TokenType aType) {
		setCompany (aTokenCompany);
		setTokenType (aType);
	}

	public Token (Token aToken, TokenInfo.TokenType aType) {
		TokenCompany tCompany;

		tCompany = aToken.getWhichCompany ();
		setCompany (tCompany);
		setTokenType (aType);
	}
	
	public String getInfo () {
		String tInfo;
		
		tInfo = GUI.EMPTY_STRING;
		if (tokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
			tInfo = "No Company";
		} else {
			tInfo = "Abbrev: " + tokenCompany.getAbbrev ();
		}
		if (type == TokenInfo.NO_TOKEN_TYPE) {
			tInfo += " No Info";
		} else {
			tInfo += " " + type;
		}
		
		return tInfo;
	}
	
	public void setTokenType (TokenInfo.TokenType aTokenType) {
		type = aTokenType;
	}
	
	public TokenInfo.TokenType getTokenType () {
		return type;
	}
	
	public void drawToken (Graphics g, int X1, int Y1, int width, int height) {
		tokenCompany.drawToken (g, X1, Y1, width, height);
	}

	public String getCorporationAbbrev () {
		return tokenCompany.getAbbrev ();
	}

	public int getTokenIndex () {
		int tTokenIndex;
		
		tTokenIndex = tokenCompany.getTokenIndex (this);
		
		return tTokenIndex;
	}
	
	public int getCorporationID () {
		return tokenCompany.getID ();
	}

	public String getCorporationStatus () {
		return tokenCompany.getStatusName ();
	}

	public TokenCompany getTokenCompany () {
		return tokenCompany;
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
		return tokenCompany;
	}

	public boolean isCorporationAbbrev (String aCorporationAbbrev) {
		return (aCorporationAbbrev.equals (getCorporationAbbrev ()));
	}

	public boolean isSameCompany (Token aToken) {
		return (tokenCompany == aToken.getWhichCompany ());
	}

	public void printlog () {
		System.out.println ("Token for " + tokenCompany.getAbbrev ());		// PRINTLOG
	}

	public void setCompany (TokenCompany aCompany) {
		tokenCompany = aCompany;
	}
	
	public boolean isAMapToken () {
		return false;
	}
}
