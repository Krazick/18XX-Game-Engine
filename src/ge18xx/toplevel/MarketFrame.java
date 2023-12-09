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
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.StockRound;
import ge18xx.utilities.xml.XMLFrame;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class MarketFrame extends XMLFrame {
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

	public XMLElement getMarketStateElements (XMLDocument aXMLDocument) {
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
		Token tToken;

		tMarketCell = market.findStartCell (aParPrice);
		if (tMarketCell != MarketCell.NO_MARKET_CELL) {
			tToken = aShareCompany.getMarketToken ();
			if (tToken != Token.NO_TOKEN) {
				aShareCompany.setParPrice (aParPrice);
				aShareCompany.setSharePrice (tMarketCell);
				tMarketCell.addTokenToBottom (tToken);
			}
			tMarketCell.redrawMarket ();
		}
	}

	public void fullOwnershipAdjustment (StockRound aStockRound) {
		market.fullOwnershipAdjustment (aStockRound, market);
	}
}
