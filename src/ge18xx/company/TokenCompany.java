package ge18xx.company;

//
//  TokenCompany.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.center.RevenueCenter;
import ge18xx.game.GameManager;
import ge18xx.map.Hex;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.LayTokenAction;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.util.List;
import java.util.LinkedList;
import java.awt.*;

import javax.swing.JLabel;

public abstract class TokenCompany extends TrainCompany {
	public static final ElementName EN_TOKEN_COMPANY = new ElementName ("TokenCompany");
	static final AttributeName AN_TOKENS = new AttributeName ("tokens");
	static final AttributeName AN_AVAILABLE_TOKEN_COUNT = new AttributeName ("availableTokenCount");
	public static final Token NO_TOKEN = null;
	public static final TokenCompany NO_TOKEN_COMPANY = null;
	public static final MapToken NO_MAP_TOKEN = null;
	public static String FONT_CNAME = "Courier";
	public static String FONT_DNAME = "Dialog";
	public static String FONT_SNAME = "Serif";
	public static String FONT_SSNAME = "SansSerif";
	int NO_COST_CALCULATED = 1000;

	List<MapToken> mapTokens;
	int totalTokenCount;
	
	public TokenCompany () {
		super ();
		mapTokens = new LinkedList<MapToken> ();
		totalTokenCount = 0;
	}
	
	public TokenCompany (int aID, String aName) {
		super (aID, aName);
		mapTokens = new LinkedList<MapToken> ();
		totalTokenCount = 0;
	}

	public TokenCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);

		MapToken tMapToken;
		
		mapTokens = new LinkedList<MapToken> ();
		totalTokenCount = aChildNode.getThisIntAttribute (AN_TOKENS);
		if (totalTokenCount > 0) {
			tMapToken = new MapToken ();
			tMapToken.setCompany (this);
			addNTokens (totalTokenCount, tMapToken);
		}
	}
	
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		int tTokenCount;
		
		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		tTokenCount = getTokenCount ();
		aCorporationList.addDataElement (tTokenCount, aRowIndex, tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Token Count", tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	public void addNTokens (int aCount, MapToken aMapToken) {
		int tIndex;
		MapToken tMapToken;
		int tCost;
		
		tCost = 0;
		for (tIndex = 0; tIndex < aCount; tIndex++) {
			tMapToken = new MapToken (aMapToken, tCost);
			if (tIndex == 0) {
				tCost = 40;
			} else if (tIndex > 0) {
				tCost = 100;
			}
			tMapToken.setCompany (this);
			addMapToken (tMapToken);
		}
	}

	public void addAsFirstMapToken (MapToken aMapToken) {
		mapTokens.add (0, aMapToken);
	}
	
	public void addMapToken (MapToken aMapToken) {
		mapTokens.add (aMapToken);
	}

	@Override
	public void placeBaseTokens () {
		MapFrame tMapFrame;
		MapCell tBaseMapCell;
		Tile tTile;
		RevenueCenter tBaseRevenueCenter;
		Location tHomeLocation;
		int tBaseCount;
		
		if (homeMapCell1HasTile ()) {
			tBaseMapCell = this.getHomeCity1 ();
			if (tBaseMapCell != MapCell.NO_MAP_CELL) {
				tTile = tBaseMapCell.getTile ();
				if (tTile != Tile.NO_TILE) {
					tHomeLocation = this.getHomeLocation1 ();
					tBaseRevenueCenter = tTile.getCenterAtLocation (tHomeLocation);
					if (tBaseRevenueCenter != RevenueCenter.NO_CENTER) {
						tMapFrame = corporationList.getMapFrame ();
						tMapFrame.putTokenDownHere (this, tBaseMapCell, tBaseRevenueCenter);
					} else {
						tBaseCount = tTile.getCorporationBaseCount ();
						if (tBaseCount > 1) {
							enterPlaceTokenMode ();
						} else {
							System.err.println ("No RevenueCenter found for " + getAbbrev () + " at " + tHomeLocation);
							System.err.println ("Corp Bases [" + tBaseCount + "]");
						}
					}
				} else {
					System.err.println ("No Tile found on Base MapCell found for " + getAbbrev ());
				}
			} else {
				System.err.println ("No Base MapCell found for " + getAbbrev ());
			}
		}
	}
	
	public JLabel buildTokenLabel () {
		return new JLabel ("");
	}

	public String getTokenLabel () {
		return "Token Count: " + getTokenCount ();
	}
	
	@Override
	public boolean isATokenCompany () {
		return true;
	}

	public boolean canLayToken () {
		boolean tCanLayToken;
		
		tCanLayToken = false;
		if ((status == ActorI.ActionStates.StartedOperations) ||
			(status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.StationLaid) ||
			(status == ActorI.ActionStates.TileAndStationLaid) ||
			(status == ActorI.ActionStates.TileUpgraded)) {
			tCanLayToken = true;
		}
		if (getTokenCount () == 0) {
			tCanLayToken = false;
		}
		
		return tCanLayToken;
	}

	public String reasonForNoTokenLay () {
		String tReason;
		
		tReason = NO_REASON;
		if (status == ActorI.ActionStates.OperatedTrain) {
			tReason = "Already Operated Train";
		}
		if (getTokenCount () == 0) {
			tReason = "No Available Tokens to Lay";
		} else if (! haveMoneyForToken ()) {
			tReason = "Don't have enough cash for the Token";
		}
		if (NO_REASON.equals (tReason)) {
			tReason = commonReason ();
		}
		
		return tReason;
	}
	
	public void drawBase (Graphics g, int X1, int Y1, int aWidth, int aHeight, boolean aHome) {
		Font tCurrentFont;
		Font tNewFont;
		Color tCurrentColor;
		int tX, tY;
		int tAbbrevWidth;
		int tAbbrevHeight;
		int tFontSize;
		int tScale = Hex.getScale ();
		
		tFontSize = tScale + 1;
		
		tCurrentFont = g.getFont ();
		tNewFont = new Font (FONT_SSNAME, Font.BOLD, tFontSize);
		g.setFont (tNewFont);
		tCurrentColor = g.getColor ();
		tX = X1 + aWidth/2;
		tY = Y1 + aHeight/2;
		tAbbrevWidth = g.getFontMetrics ().stringWidth (abbrev);
		tAbbrevHeight = g.getFontMetrics ().getHeight ();
		tX = tX - tAbbrevWidth/2;
		tY = tY + tAbbrevHeight/2;
		
		g.setColor (fgColor);
		g.drawString (abbrev, tX, tY);		
		g.setColor (bgColor);
		g.drawString (abbrev, tX, tY);
		g.setFont (tCurrentFont);
		g.setColor (tCurrentColor);
	}
	
	public void drawToken (Graphics g, int X1, int Y1, int width, int height) {
		Font tCurrentFont;
		Font tNewFont;
		Color tCurrentColor;
		int tX, tY;
		int tAbbrevWidth;
		int tAbbrevHeight;
		int tFontSize;
		int tScale = Hex.getScale ();
		
		tFontSize = tScale + 1;
		tCurrentFont = g.getFont ();
		tNewFont = new Font (FONT_SSNAME, Font.BOLD, tFontSize);
		g.setFont (tNewFont);
		tCurrentColor = g.getColor ();
		tX = X1 + width/2;
		tY = Y1 + height/2;
		tAbbrevWidth = g.getFontMetrics ().stringWidth (abbrev);
		tAbbrevHeight = g.getFontMetrics ().getHeight ();
		tX = tX - tAbbrevWidth/2;
		tY = tY + tAbbrevHeight/2;
		
		g.setColor (bgColor);
		g.fillOval (X1, Y1, width, height);
		g.setColor (fgColor);
		g.drawString (abbrev, tX, tY);
		g.setColor (Color.BLACK);
		g.drawOval (X1, Y1, width, height);
		g.setFont (tCurrentFont);
		g.setColor (tCurrentColor);
	}
	
	public Color getBgColor () {
		return bgColor;
	}
	
	@Override
	public void enterPlaceTokenMode () {
		corporationList.enterPlaceTokenMode ();
	}

	@Override
	public int fieldCount () {
		return super.fieldCount () + 1;
	}

	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;
		
		tXMLCorporationState = aXMLDocument.createElement (EN_TOKEN_COMPANY);
		getCorporationStateElement (tXMLCorporationState);

		return tXMLCorporationState;
	}
	
	public void appendOtherElements (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		super.appendOtherElements (aXMLCorporationState, aXMLDocument);	
	}

	public void getCorporationStateElement (XMLElement aXMLCorporationState) {
		aXMLCorporationState.setAttribute (AN_AVAILABLE_TOKEN_COUNT, getTokenCount ());
		super.getCorporationStateElement (aXMLCorporationState);
	}

	@Override
	public MapToken getMapToken () {
		MapToken tMapToken;
		
		tMapToken = mapTokens.get (0);
		
		return tMapToken;
	}
	
	public Token getToken () {
		Token tToken;
		
		tToken = new Token ();
		tToken.setCompany (this);
		
		return tToken;
	}
	
	public int getTokenCount () {
		int tTokenCount;
		
		if (mapTokens == null) {
			tTokenCount = 0;
		} else {
			tTokenCount = mapTokens.size ();
		}
		
		return tTokenCount;
	}
	
	public int getTotalTokenCount () {
		return totalTokenCount;
	}
	
	public boolean haveMoneyForToken () {
		boolean tHaveMoneyForToken = true;
		
		if (getNonBaseTokenCost () > treasury) {
			tHaveMoneyForToken = false;
		}
		
		return tHaveMoneyForToken;
	}
	

	public boolean haveLaidThisBaseToken (MapCell aMapCell) {
		boolean tHaveLaidThisBaseToken;
		
		if (aMapCell != HexMap.NO_MAP_CELL) {
			tHaveLaidThisBaseToken = aMapCell.haveLaidBaseTokenFor (this);
		} else { // Note, if MapCell is NO_MAP_CELL, treat as Token Laid
			tHaveLaidThisBaseToken = true;
		}
		
		return tHaveLaidThisBaseToken;
	}
	
	@Override
	public boolean haveLaidAllBaseTokens () {
		boolean tHaveLaidAllBaseTokens, tLaidBaseToken1, tLaidBaseToken2;
		
		tLaidBaseToken1 = haveLaidThisBaseToken (homeCity1);
		tLaidBaseToken2 = haveLaidThisBaseToken (homeCity2);
		tHaveLaidAllBaseTokens = tLaidBaseToken1 && tLaidBaseToken2;
		
		return tHaveLaidAllBaseTokens;
	}
	
	public void loadStatus (XMLNode aXMLNode) {
		super.loadStatus (aXMLNode);
	}

	public MapToken popToken () {
		MapToken tMapToken;
		
		if (getTokenCount () == 0) {
			tMapToken = NO_MAP_TOKEN;
		} else {
			tMapToken = mapTokens.remove (0);
		}
		
		return tMapToken;
	}
	
	@Override
	public void tokenWasPlaced (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex) {
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		LayTokenAction tLayTokenAction;
		String tOperatingRoundID;
		int tCostToLayTokenOnMapCell;
		Bank tBank;
		
		tCurrentStatus = status;
		if ((status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded)) {
			tStatusUpdated = updateStatus (ActorI.ActionStates.TileAndStationLaid);
		} else {
			tStatusUpdated = updateStatus (ActorI.ActionStates.StationLaid);
		}
		if (tStatusUpdated) {
			tNewStatus = status;
			tCostToLayTokenOnMapCell = getCostToLayToken (aMapCell);
			popToken ();  // Pop off the Token from the list of Map Tokens, don't want infinite supply
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tLayTokenAction = new LayTokenAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			tLayTokenAction.addLayTokenEffect (this, aMapCell, aTile, aRevenueCenterIndex);
			tLayTokenAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			if (tCostToLayTokenOnMapCell > 0) {
				tBank = corporationList.getBank ();
				this.transferCashTo (tBank, tCostToLayTokenOnMapCell);
				tLayTokenAction.addCashTransferEffect (this, tBank, tCostToLayTokenOnMapCell);
			}
			addAction (tLayTokenAction);

			corporationFrame.updateInfo ();
		}
	}
	
	public int getCostToLayToken (MapCell aMapCell) {
		int tCostToLayToken;
		String tMapCellID;
		
		// Token Cost Rules
		//  -- On Any Base Location -- $0
		//  -- If Private gives Free Token Lay -- $0
		//  -- If Calculated on Token Laid Count:
		//      -- First Token after Home Bases -- $40
		//      -- Second and Later Tokens after Home Bases -- $100
		//  -- If Calculated on Distance from Base -- $X * # of Hexes
		
		tCostToLayToken = NO_COST_CALCULATED;
		tMapCellID = aMapCell.getID ();
		if (tCostToLayToken == NO_COST_CALCULATED) {
			// Home City 1 for this Corporation -- This Token is Free
			tCostToLayToken = getHomeBaseCost (homeCity1, tMapCellID);
		} 
		if (tCostToLayToken == NO_COST_CALCULATED) {
			// Home City 2 for this Corporation -- This Token is Free
			tCostToLayToken = getHomeBaseCost (homeCity2, tMapCellID);
		} 
		// First Token is used on the Market 
		
		/* If Laying Base Token -- Cost is Zero */
		// Test by comparing the Available Count to the Total Starting Count,
		// If Available Count plus 1 equals Total Starting Count -- this is First Token
		if (tCostToLayToken == NO_COST_CALCULATED) {
			tCostToLayToken = getNonBaseTokenCost ();
		}
		
		// TODO: If laying the D&H Tile and Token, as Special Effect of Private
		// Cost to Lay this token is $0
		
		// Also note, some games may vary token cost on Distance from Home Station
		
		return tCostToLayToken;
	}
	
	public int getNonBaseTokenCost () {
		int tCostToLayToken = 0;
		MapToken tFirstToken;
		
		tFirstToken = mapTokens.get (0);
		tCostToLayToken = tFirstToken.getCost ();
		
		return tCostToLayToken;
	}
	
	private int getHomeBaseCost (MapCell aBaseMapCell, String aMapCellID) {
		int tCostToLayHome;
		
		tCostToLayHome = NO_COST_CALCULATED;
		if (aBaseMapCell != NO_HOME_MAPCELL) {
			if (aBaseMapCell.getID () == aMapCellID) {
				tCostToLayHome = 0;
			}
		}
		
		return tCostToLayHome;
	}
	
	@Override
	public String buildCorpInfoLabel () {
		String tCorpLabel = "";

		tCorpLabel = super.buildCorpInfoLabel ();
		tCorpLabel += "<br>Tokens: " + getTokenCount ();
		tCorpLabel = "<html>" + tCorpLabel + "</html>";

		return tCorpLabel;
	}
	
	@Override
	public Container buildPortfolioTrainsJPanel (CorporationFrame aItemListener, 
			GameManager aGameManager, boolean aFullTrainPortfolio, 
			boolean aCanBuyTrain, String aDisableToolTipReason, 
			Corporation aBuyingCorporation) {
		Container tTrainPortfolioInfoContainer;
		int tTokenCount;
		
		tTokenCount = getTokenCount ();
		tTrainPortfolioInfoContainer = super.buildPortfolioTrainsJPanel (aItemListener, aGameManager, aFullTrainPortfolio, aCanBuyTrain,
				aDisableToolTipReason, aBuyingCorporation, tTokenCount);
		
		return tTrainPortfolioInfoContainer;
	}
}
