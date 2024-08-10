package ge18xx.toplevel;

import java.awt.BorderLayout;

//
//  MarketFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 9/1/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.game.GameManager;
import ge18xx.game.XMLSaveGameI;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.StockRound;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class MarketFrame extends XMLFrame implements XMLSaveGameI {
	public static final String BASE_TITLE = "Market";
	private static final long serialVersionUID = 1L;
	Market market;

	public MarketFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);

		market = new Market (40, 40, aGameManager);
		buildScrollPane (market, BorderLayout.CENTER);
	}

	/**
	 * Update the Frame, and specifically updateFrameTitle (from super class XMLFrame) with the static BASE_TITLE provided
	 */
	public void updateFrame () {
		updateFrameTitle (BASE_TITLE);
	}

	public XMLElement createMarketDefinitions (XMLDocument aXMLDocument) {
		return (market.createElement (aXMLDocument));
	}

	public Market getMarket () {
		return market;
	}

//	public XMLElement getMarketStateElements (XMLDocument aXMLDocument) {
	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		return (market.getMarketStateElements (aXMLDocument));
	}

	public void loadMarketTokens (XMLNode aXMLNode) {
		market.loadMarketTokens (aXMLNode);
	}

	public boolean marketHasTokenFor (ShareCompany aShareCompany) {
		boolean tMarketHasTokenFor;
		MarketCell tMarketCell;
		Token tFoundToken;

		tMarketCell = aShareCompany.getSharePriceMarketCell ();
		if (tMarketCell ==  MarketCell.NO_MARKET_CELL) {
			tMarketHasTokenFor = false;
		} else {
			tFoundToken = tMarketCell.findTokenFor (aShareCompany.getAbbrev ());
			if (tFoundToken == Token.NO_TOKEN) {
				tMarketHasTokenFor = false;
			} else {
				tMarketHasTokenFor = true;
			}
		}

		return tMarketHasTokenFor;
	}

	public void setSharePriceToken (ShareCompany aShareCompany) {
		MarketCell tMarketCell;
		Token tToken;

		tMarketCell = aShareCompany.getMarketCellAt (market);
		if (tMarketCell != MarketCell.NO_MARKET_CELL) {
			tToken = aShareCompany.getMarketToken ();
			if (tToken != Token.NO_TOKEN) {
				tMarketCell.addTokenToBottom (tToken);
			}
			tMarketCell.redrawMarket ();
		}
	}

	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		MarketCell tMarketCell;

		tMarketCell = market.findStartCell (aParPrice);
		setParPriceToMarketCell (aShareCompany, aParPrice, tMarketCell);
	}

	public void setParPriceToMarketCell (ShareCompany aShareCompany, int aParPrice, MarketCell aMarketCell) {
		Token tToken;
		
		if (aMarketCell != MarketCell.NO_MARKET_CELL) {
			tToken = aShareCompany.getMarketToken ();
			if (tToken != Token.NO_TOKEN) {
				aShareCompany.setParPrice (aParPrice);
				aShareCompany.setSharePrice (aMarketCell);
				aMarketCell.addTokenToBottom (tToken);
			} else {
				System.err.println ("No Market Token Found to set into Market Frame");
			}
			aMarketCell.redrawMarket ();
		} else {
			aShareCompany.setParPrice (aParPrice);
		}
	}

	public void fullOwnershipAdjustment (StockRound aStockRound) {
		market.fullOwnershipAdjustment (aStockRound, market);
	}
}
