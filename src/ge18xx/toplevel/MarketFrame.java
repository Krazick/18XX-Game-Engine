package ge18xx.toplevel;

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
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.*;

import javax.swing.*;

public class MarketFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	Market market;
	
	public MarketFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());
		JScrollPane scrollPane;
		
		market = new Market (40, 40, aGameManager);
        scrollPane = new JScrollPane ();
		scrollPane.setViewportView (market);
        scrollPane.setPreferredSize (new Dimension (300, 300));
		add (scrollPane, BorderLayout.CENTER);
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
	
	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		MarketCell tMarketCell;
		Token tToken;
		
		tMarketCell = market.findStartCell (aParPrice);
		if (tMarketCell != Market.NO_MARKET_CELL) {
			tToken = aShareCompany.getToken ();
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
