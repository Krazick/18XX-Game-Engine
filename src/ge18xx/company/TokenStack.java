package ge18xx.company;

//
//  TokenStack.java
//  Game_18XX
//
//  Created by Mark Smith on 11/28/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.market.MarketCell;
import ge18xx.round.StockRound;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.round.action.SoldOutAdjustmentAction;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.Graphics;
import java.util.LinkedList;

public class TokenStack {
	public static final ElementName EN_TOKENS = new ElementName ("Tokens");
	public static final int NO_STACK_LOCATION = -1;
	LinkedList<Token> tokens;
	MarketCell marketCell;
	
	public TokenStack (MarketCell aMarketCell) {
		tokens = new LinkedList<Token> ();
		setMarketCell (aMarketCell);
	}
	
	public void addTokenToBottom (Token aToken) {
		tokens.addFirst (aToken);
	}
	
	public void addTokenToLocation (int aStackLocation, Token aToken) {
		if (aStackLocation != NO_STACK_LOCATION) {
			tokens.add (aStackLocation, aToken);
		}
	}
	
	public void addTokenToTop (Token aToken) {
		tokens.addLast (aToken);
	}
	
	public int compareLocation (Corporation aCorporation1, Corporation aCorporation2) {
		int tCompareLocation;
		int tTokenIndex, tTokenCount, tLocation1, tLocation2;
		Token tToken;
		Corporation tCorporation;
		
		tTokenCount = getTokenCount ();
		tLocation1 = NO_STACK_LOCATION;
		tLocation2 = NO_STACK_LOCATION;
		for (tTokenIndex = tTokenCount - 1; tTokenIndex >= 0; tTokenIndex--) {
			tToken = getTokenAtIndex (tTokenIndex);
			tCorporation = (Corporation) tToken.getWhichCompany ();
			if (tCorporation == aCorporation1) {
				tLocation1 = tTokenIndex;
			}
			if (tCorporation == aCorporation2) {
				tLocation2 = tTokenIndex;
			}
		}

		if ((tLocation1 == NO_STACK_LOCATION) || (tLocation2 == NO_STACK_LOCATION)) {
			tCompareLocation = 0;
		} else {
			tCompareLocation = tLocation2 - tLocation1;
		}
		
		return tCompareLocation;
	}
	
	public void fullOwnershipAdjustment (StockRound aStockRound) {
		int tTokenIndex, tTokenCount;
		Token tToken;
		ShareCompany tShareCompany;
		
		tTokenCount = getTokenCount ();
		for (tTokenIndex = tTokenCount - 1; tTokenIndex >= 0; tTokenIndex--) {
			tToken = getTokenAtIndex (tTokenIndex);
			tShareCompany = (ShareCompany) tToken.getWhichCompany ();
			if (tShareCompany.isSoldOut ()) {
				doAllSoldOutAdjustment (tShareCompany, aStockRound);
			}
		}
	}

	/* Do the All Sold Out Adjustment for a Single Share Company */
	public void doAllSoldOutAdjustment (ShareCompany aShareCompany, StockRound aStockRound) {
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		String tCompanyAbbrev;
		SoldOutAdjustmentAction tSoldOutAdjustmentAction;
		int tStartLocation, tNewLocation;
		
		tCompanyAbbrev = aShareCompany.getAbbrev ();
		tMarketCell = aShareCompany.getSharePriceMarketCell ();
		if (tMarketCell != MarketCell.NO_MARKET_CELL) {
			tNewMarketCell = tMarketCell.getSoldOutMarketCell ();
			tStartLocation = tMarketCell.getTokenLocation (tCompanyAbbrev);
			if (tMarketCell != tNewMarketCell) {
				moveTokenToNewMarketCell (aShareCompany, tMarketCell, tNewMarketCell);
				tNewLocation = tNewMarketCell.getTokenLocation (tCompanyAbbrev);
				tSoldOutAdjustmentAction = new SoldOutAdjustmentAction (aStockRound.getRoundType (), aStockRound.getID (), aStockRound);
				tSoldOutAdjustmentAction.addChangeMarketCellEffect (aShareCompany, tMarketCell, tStartLocation, tNewMarketCell, tNewLocation);
				aStockRound.addAction (tSoldOutAdjustmentAction);
			}
		}
	}

	public void doPayNoDividendAdjustment (ShareCompany aShareCompany, PayNoDividendAction aPayNoDividendAction) {
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		String tCompanyAbbrev;
		int tStartStackLocation, tNewStackLocation;
		
		tCompanyAbbrev = aShareCompany.getAbbrev ();
		tMarketCell = aShareCompany.getSharePriceMarketCell ();
		if (tMarketCell != MarketCell.NO_MARKET_CELL) {
			tNewMarketCell = tMarketCell.getDividendHoldMarketCell ();
			tStartStackLocation = tMarketCell.getTokenLocation (tCompanyAbbrev);
			if (tMarketCell != tNewMarketCell) {
				moveTokenToNewMarketCell (aShareCompany, tMarketCell, tNewMarketCell);
				tNewStackLocation = tNewMarketCell.getTokenLocation (tCompanyAbbrev);
				aPayNoDividendAction.addChangeMarketCellEffect (aShareCompany, tMarketCell, tStartStackLocation, 
								tNewMarketCell, tNewStackLocation);
			}
		}
	}
	
	public void doPayFullDividendAdjustment (ShareCompany aShareCompany, PayFullDividendAction aPayFullDividendAction) {
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		String tCompanyAbbrev;
		int tStartStackLocation, tNewStackLocation;
		
		tCompanyAbbrev = aShareCompany.getAbbrev ();
		tMarketCell = aShareCompany.getSharePriceMarketCell ();
		if (tMarketCell != MarketCell.NO_MARKET_CELL) {
			tNewMarketCell = tMarketCell.getDividendPayMarketCell ();
			tStartStackLocation = tMarketCell.getTokenLocation (tCompanyAbbrev);
			if (tMarketCell != tNewMarketCell) {
				moveTokenToNewMarketCell (aShareCompany, tMarketCell, tNewMarketCell);
				tNewStackLocation = tNewMarketCell.getTokenLocation (tCompanyAbbrev);
				aPayFullDividendAction.addChangeMarketCellEffect (aShareCompany, tMarketCell, 
							tStartStackLocation, tNewMarketCell, tNewStackLocation);
			}
		}
	}

	public void moveTokenToNewMarketCell (ShareCompany aShareCompany, MarketCell aMarketCell, 
			MarketCell aNewMarketCell) {
		String tCompanyAbbrev;
		Token tToken;
		
		tCompanyAbbrev = aShareCompany.getAbbrev ();
		aShareCompany.setSharePrice (aNewMarketCell);
		tToken = aMarketCell.getToken (tCompanyAbbrev);
		if (tToken != Token.NO_TOKEN) {
			aNewMarketCell.addTokenToBottom (tToken);
		}
		aNewMarketCell.redrawMarket ();	
	}
	
	public void drawStack (Graphics g, int x1, int y1, int width, int height) {
		for (Token tToken : tokens) {
			tToken.drawToken (g, x1, y1, width, height);
			x1 += 4;
			y1 -= 4;
		}
	}
	
	public Token findTokenFor (String aCompanyAbbrev) {
		Token tThisToken;
		
		tThisToken = Token.NO_TOKEN;
		for (Token tToken : tokens) {
			if (tToken.isCorporationAbbrev (aCompanyAbbrev)) {
				tThisToken = tToken;
			}
		}
		
		return tThisToken;
	}
	
	public int getLocation (String aCompanyAbbrev) {
		int tLocation, tIndex;
		
		tLocation = NO_STACK_LOCATION;
		tIndex = 0;
		for (Token tToken : tokens) {
			if (tToken.isCorporationAbbrev (aCompanyAbbrev)) {
				tLocation = tIndex;
			}
			tIndex++;
		}
		
		return tLocation;
	}
	
	public int getTokenCount () {
		return tokens.size ();
	}
	
	public Token getTokenAtIndex (int aIndex) {
		return tokens.get (aIndex);
	}
	
	public XMLElement getTokenStackElements (XMLDocument aXMLDocument) {
		XMLElement tTokenStackElements;
		XMLElement tTokenElement;
		
		tTokenStackElements = aXMLDocument.createElement(EN_TOKENS);
		for (Token tToken : tokens) {
			tTokenElement = tToken.getTokenElement (aXMLDocument);
			tTokenStackElements.appendChild (tTokenElement);
		}
		
		return tTokenStackElements;
	}
	
	public String getToolTip () {
		String tTip;
		
		tTip = "";
		for (Token tToken : tokens) {
			tTip = tToken.getCorporationAbbrev () + " [" + tToken.getCorporationStatus () + "]<br>" + tTip;
		}
		
		return tTip;
	}
	
	public Token getTopToken () {
		return tokens.getFirst ();
	}
	
	public void loadTokenStack (XMLNode aXMLMarketNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (tokenParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLMarketNode, Token.EN_TOKEN);
	}
	
	ParsingRoutineI tokenParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aMarketCellNode) {
			String tCompanyAbbrev;
			Token tToken;
			ShareCompany tShareCompany;
			
			tCompanyAbbrev = aMarketCellNode.getThisAttribute (Corporation.AN_ABBREV);
			tToken = marketCell.getTokenGM (tCompanyAbbrev);
			tShareCompany = (ShareCompany) tToken.getWhichCompany ();
			tShareCompany.setSharePrice (marketCell);
			addTokenToTop (tToken);
		}
	};

	public Token removeToken (String aCompanyAbbrev) {
		Token tToken;
		
		tToken = Token.NO_TOKEN;
		if (getTokenCount () > 0) {
			tToken = findTokenFor (aCompanyAbbrev);
			tokens.remove (tToken);
		}
		
		return tToken;
	}
	
	public void setMarketCell (MarketCell aMarketCell) {
		marketCell = aMarketCell;
	}
}
