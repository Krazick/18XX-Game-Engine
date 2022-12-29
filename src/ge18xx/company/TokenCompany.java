package ge18xx.company;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

//
//  TokenCompany.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.center.RevenueCenter;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.game.GameManager;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.LayTokenAction;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public abstract class TokenCompany extends TrainCompany {
	final static AttributeName AN_TOKENS = new AttributeName ("tokens");
	final static AttributeName AN_AVAILABLE_TOKEN_COUNT = new AttributeName ("availableTokenCount");
	public final static ElementName EN_TOKEN_COMPANY = new ElementName ("TokenCompany");
	public final static TokenCompany NO_TOKEN_COMPANY = null;
	private final static List<MapToken> NO_MAP_TOKENS = null;
	public static String FONT_CNAME = "Courier";
	public static String FONT_DNAME = "Dialog";
	public static String FONT_SNAME = "Serif";
	public static String FONT_SSNAME = "SansSerif";
	int NO_COST_CALCULATED = 1000;

	// Create new Class 'TokenInfo' that has:
	// 1) Token (can be sub-Class MapToken)
	// 2) Type [an ENUM] (Market, Home, FixedCost, RangeCost
	// 3) Cost (if RangeCost Type, value -1, otherwise the cost)
	// 4) Used (Boolean to mark if used or not)
	// Store in an ArrayList, with a fixed number of entries, based on how many tokens the Company has
	// Methods:
	// 1) GetToken (TokenType) -- Market, Home
	// 2) GetToken () -- No Arg, gets the first FixedCost or RangeCost that is not used
	// 3) GetLastToken () -- No Arg, gets the last FixedCost or RangeCost that is not used
	// 4) GetTokenCount () -- No Arg, gets the count of available MapTokens (never counts the MarketToken Type)
	
	List<MapToken> mapTokens;
	Tokens tokens;
	int totalTokenCount;

	public TokenCompany (int aID, String aName) {
		super (aID, aName);
		totalTokenCount = 1;		// A Minimum number of Tokens for a Token Company (like 1835 Minor)
		setupAllTokens ();
	}

	public TokenCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);

		totalTokenCount = aChildNode.getThisIntAttribute (AN_TOKENS);
		setupAllTokens ();
	}

	private void setupAllTokens () {
		Token tMarketToken;
		
		tokens = new Tokens (totalTokenCount);
		tMarketToken = new Token (this);
		tokens.addNewToken (tMarketToken, TokenType.MARKET, Token.NO_COST);
		setupNewMapTokens ();
	}
	
	private void setupNewMapTokens () {
		MapToken tMapToken;
		
		mapTokens = new LinkedList<> ();
		if (totalTokenCount > 0) {
			tMapToken = new MapToken ();
			tMapToken.setCompany (this);
			addNTokens (totalTokenCount, tMapToken);
		}
	}

	public void addNTokens (int aCount, MapToken aMapToken) {
		MapToken tMapToken;
		int tStartIndex;
		int tIndex;
		int tCost;

		tCost = Token.NO_COST;
		tStartIndex = 1;
		if (homeCityGrid1 != XMLNode.NO_VALUE) {
			tStartIndex++;
			tMapToken = new MapToken (aMapToken, tCost);
			addMapToken (tMapToken);
			tokens.addNewToken (tMapToken, TokenType.HOME1, tCost);
		}
		if (homeCityGrid2 != XMLNode.NO_VALUE) {
			tStartIndex++;
			tMapToken = new MapToken (aMapToken, tCost);
			addMapToken (tMapToken);
			tokens.addNewToken (tMapToken, TokenType.HOME2, tCost);
		}
		for (tIndex = tStartIndex; tIndex <= aCount; tIndex++) {
			if (tIndex == tStartIndex) {
				tCost = 40;
			} else if (tIndex > tStartIndex) {
				tCost = 100;
			}
			tMapToken = new MapToken (aMapToken, tCost);
			tMapToken.setCompany (this);
			addMapToken (tMapToken);
			tokens.addNewToken (tMapToken, TokenType.FIXED_COST, tCost);
		}
	}

	public void addMapToken (MapToken aMapToken) {
		mapTokens.add (aMapToken);
	}
	
	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		int tTokenCount;

		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		tTokenCount = getTokenCount ();
		aCorporationList.addDataElement (tTokenCount, aRowIndex, tCurrentColumn++);

		return tCurrentColumn;
	}

	@Override
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Token Count", tCurrentColumn++);

		return tCurrentColumn;
	}

	public void removeOtherHome (MapCell aMapCell, Location aHomeLocation) {
		if (aMapCell == homeCity1) {
			if (homeCity2 != MapCell.NO_MAP_CELL) {
				if (homeCity2.removeHome (this, homeLocation2)) {
					setHome2 (MapCell.NO_MAP_CELL, Location.NO_LOC);
				}
			}
		} else if (aMapCell == homeCity2) {
			if (homeCity1 != MapCell.NO_MAP_CELL) {
				if (homeCity1.removeHome (this, homeLocation1)) {
					setHome1 (MapCell.NO_MAP_CELL, Location.NO_LOC);
				}
			}
		}
	}
	
	@Override
	public void placeBaseToken (MapCell aMapCell, Location aHomeLocation) {
		MapFrame tMapFrame;
		Tile tTile;
		RevenueCenter tBaseRevenueCenter;
		int tBaseCount;

		if (aMapCell.isTileOnCell ()) {
			tTile = aMapCell.getTile ();
			tBaseRevenueCenter = tTile.getRCWithBaseForCorp (this);
			if (tBaseRevenueCenter != RevenueCenter.NO_CENTER) {
				tMapFrame = corporationList.getMapFrame ();
				tMapFrame.putTokenDownHere (this, aMapCell, tBaseRevenueCenter);
				if (isHomeTypeChoice ()) {
					removeOtherHome (aMapCell, aHomeLocation);
				}
			} else { // Given multiple choice for base location on tile
				tBaseCount = tTile.getCorporationBaseCount ();
				if (tBaseCount > 1) {
					corporationFrame.handlePlaceBaseToken ();
				} else {
					System.err.println ("No RevenueCenter found for " + getAbbrev () + " at " + aHomeLocation);
					System.err.println ("Corp Bases [" + tBaseCount + "]");
				}
			}
		}
	}

	@Override
	public JLabel buildTokenLabel () {
		return new JLabel ("");
	}

	@Override
	public String getTokenLabel () {
		return "Token Count: " + getTokenCount ();
	}

	@Override
	public boolean isATokenCompany () {
		return true;
	}

	@Override
	public boolean canLayToken () {
		boolean tCanLayToken;

		tCanLayToken = false;
		if ((status == ActorI.ActionStates.StartedOperations) || (status == ActorI.ActionStates.TileLaid)
				|| (status == ActorI.ActionStates.Tile2Laid) || (status == ActorI.ActionStates.StationLaid)
				|| (status == ActorI.ActionStates.TileAndStationLaid) || (status == ActorI.ActionStates.TileUpgraded)) {
			tCanLayToken = true;
		}
		if (getTokenCount () == 0) {
			tCanLayToken = false;
		}

		return tCanLayToken;
	}

	@Override
	public String reasonForNoTokenLay () {
		String tReason;

		tReason = NO_REASON;
		if (status == ActorI.ActionStates.OperatedTrain) {
			tReason = "Already Operated Train";
		}
		if (getTokenCount () == 0) {
			tReason = "No Available Tokens to Lay";
		} else if (!haveMoneyForToken ()) {
			tReason = "Don't have enough cash for the Token";
		}
		if (NO_REASON.equals (tReason)) {
			tReason = commonReason ();
		}

		return tReason;
	}

	public void drawBase (Graphics g, int X1, int Y1, int aWidth, int aHeight, boolean aHomeX) {
		Font tCurrentFont;
		Font tNewFont;
		Color tCurrentColor;
		int tX;
		int tY;
		int tAbbrevWidth;
		int tAbbrevHeight;
		int tFontSize;
		int tScale = Hex.getScale ();

		tCurrentFont = g.getFont ();
		if (aHomeX) {
			tFontSize = tScale + 1;
			tNewFont = new Font (FONT_SSNAME, Font.BOLD, tFontSize);
		} else {
			tFontSize = tScale - 1;
			tNewFont = new Font (FONT_SSNAME, Font.ITALIC, tFontSize);
		}
		g.setFont (tNewFont);
		tCurrentColor = g.getColor ();
		tX = X1 + aWidth / 2;
		tY = Y1 + aHeight / 2;
		tAbbrevWidth = g.getFontMetrics ().stringWidth (abbrev);
		tAbbrevHeight = g.getFontMetrics ().getHeight ();
		tX = tX - tAbbrevWidth / 2;
		tY = tY + tAbbrevHeight / 2 - 1;

		g.setColor (fgColor);
		g.drawString (abbrev, tX, tY);
		g.setColor (homeColor);
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
		tX = X1 + width / 2;
		tY = Y1 + height / 2;
		tAbbrevWidth = g.getFontMetrics ().stringWidth (abbrev);
		tAbbrevHeight = g.getFontMetrics ().getHeight ();
		tX = tX - tAbbrevWidth / 2;
		tY = tY + tAbbrevHeight / 2;

		g.setColor (bgColor);
		g.fillOval (X1, Y1, width, height);
		g.setColor (fgColor);
		g.drawString (abbrev, tX, tY);
		g.setColor (Color.BLACK);
		g.drawOval (X1, Y1, width, height);
		g.setFont (tCurrentFont);
		g.setColor (tCurrentColor);
	}

	@Override
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

	@Override
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;

		tXMLCorporationState = aXMLDocument.createElement (EN_TOKEN_COMPANY);
		getCorporationStateElement (tXMLCorporationState);

		return tXMLCorporationState;
	}

	@Override
	public void appendOtherElements (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		super.appendOtherElements (aXMLCorporationState, aXMLDocument);
	}

	@Override
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

		if (mapTokens == NO_MAP_TOKENS) {
			tTokenCount = 0;
		} else {
			tTokenCount = mapTokens.size ();
		}

		return tTokenCount;
	}

	public int getTotalTokenCount () {
		return totalTokenCount;
	}

	@Override
	public boolean haveMoneyForToken () {
		boolean tHaveMoneyForToken = true;

		if (getNonBaseTokenCost () > treasury) {
			tHaveMoneyForToken = false;
		}

		return tHaveMoneyForToken;
	}

	@Override
	public boolean haveLaidThisBaseToken (MapCell aMapCell) {
		boolean tHaveLaidThisBaseToken;

		if (aMapCell != MapCell.NO_MAP_CELL) {
			tHaveLaidThisBaseToken = aMapCell.haveLaidBaseTokenFor (this);
		} else { // Note, if MapCell is NO_MAP_CELL, treat as Token Laid
			tHaveLaidThisBaseToken = true;
		}

		return tHaveLaidThisBaseToken;
	}

	@Override
	public boolean choiceForBaseToken () {
		boolean tChoiceForBaseToken;

		if (homeCity1 == MapCell.NO_MAP_CELL) {
			tChoiceForBaseToken = false;
		} else if (homeCity1.sameID (homeCity2)) {
			tChoiceForBaseToken = true;
		} else {
			tChoiceForBaseToken = false;
		}

		return tChoiceForBaseToken;
	}

	@Override
	public boolean haveLaidAllBaseTokens () {
		boolean tHaveLaidAllBaseTokens;
		boolean tLaidBaseToken1;
		boolean tLaidBaseToken2;

		tLaidBaseToken1 = haveLaidThisBaseToken (homeCity1);
		tLaidBaseToken2 = haveLaidThisBaseToken (homeCity2);
		if (isHomeTypeBoth ()) {
			tHaveLaidAllBaseTokens = tLaidBaseToken1 && tLaidBaseToken2;
		} else if (isHomeTypeChoice ()) {
			tHaveLaidAllBaseTokens = tLaidBaseToken1 || tLaidBaseToken2;
		} else {
			tHaveLaidAllBaseTokens = tLaidBaseToken1 && tLaidBaseToken2;
		}
		
		return tHaveLaidAllBaseTokens;
	}

	@Override
	public boolean canLayBaseToken () {
		boolean tCanLayBaseToken = false;

		if (! haveLaidAllBaseTokens ()) {
			if (homeMapCell1HasTile ()) {
				tCanLayBaseToken = true;
			} else if (homeMapCell2HasTile ()) {
				tCanLayBaseToken = true;
			}
		}

		return tCanLayBaseToken;
	}

	@Override
	public void loadStatus (XMLNode aXMLNode) {
		super.loadStatus (aXMLNode);
	}

	@Override
	public MapToken popMapToken () {
		MapToken tMapToken;

		if (getTokenCount () == 0) {
			tMapToken = MapToken.NO_MAP_TOKEN;
		} else {
			tMapToken = mapTokens.remove (0);
		}

		return tMapToken;
	}

	@Override
	public void tokenWasPlaced (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, boolean aAddLayTokenAction) {
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus, tNewStatus;

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
			if (aAddLayTokenAction) {
				addLayTokenAction (aMapCell, aTile, aRevenueCenterIndex, tCurrentStatus, tNewStatus);
			}
			popMapToken (); // Pop off the Token from the list of Map Tokens,
							// don't want infinite supply

			updateInfo ();
		}
	}

	public void addLayTokenAction (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex,
			ActorI.ActionStates aCurrentStatus, ActorI.ActionStates aNewStatus) {
		LayTokenAction tLayTokenAction;
		String tOperatingRoundID;
		Bank tBank;
		int tCostToLayTokenOnMapCell;

		tCostToLayTokenOnMapCell = getCostToLayToken (aMapCell);
		tOperatingRoundID = corporationList.getOperatingRoundID ();
		tLayTokenAction = new LayTokenAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
		tLayTokenAction.addLayTokenEffect (this, aMapCell, aTile, aRevenueCenterIndex, benefitInUse);
		addRemoveHomeEffect (aMapCell, tLayTokenAction);
		tLayTokenAction.addChangeCorporationStatusEffect (this, aCurrentStatus, aNewStatus);
		if (tCostToLayTokenOnMapCell > 0) {
			tBank = corporationList.getBank ();
			transferCashTo (tBank, tCostToLayTokenOnMapCell);
			tLayTokenAction.addCashTransferEffect (this, tBank, tCostToLayTokenOnMapCell);
		}
		if (benefitInUse.realBenefit ()) {
			tLayTokenAction.addBenefitUsedEffect (this, benefitInUse);
		}
		addAction (tLayTokenAction);
	}

	public void addRemoveHomeEffect (MapCell aMapCell, LayTokenAction aLayTokenAction) {
		if (isHomeTypeChoice ()) {
			if (aMapCell == homeCity1) {
				aLayTokenAction.addRemoveHomeEffect (this, getAbbrev (), MapCell.NO_MAP_CELL, getHomeCity2 (), 
													Location.NO_LOC, getHomeLocation2 ());
			} else if (aMapCell == homeCity2) {
				aLayTokenAction.addRemoveHomeEffect (this, getAbbrev (), getHomeCity1 (), MapCell.NO_MAP_CELL, 
						 							getHomeLocation1 (), Location.NO_LOC);
			}
		}
	}
	
	@Override
	public int getCostToLayToken (MapCell aMapCell) {
		int tCostToLayToken;
		String tMapCellID;

		// Token Cost Rules
		// -- On Any Base Location -- $0
		// -- If Private gives Free Token Lay -- $0
		// -- If Calculated on Token Laid Count:
		// -- First Token after Home Bases -- $40
		// -- Second and Later Tokens after Home Bases -- $100
		// -- If Calculated on Distance from Base -- $X * # of Hexes

		tCostToLayToken = NO_COST_CALCULATED;
		if (aMapCell != MapCell.NO_MAP_CELL) {
			tMapCellID = aMapCell.getID ();
			if (tCostToLayToken == NO_COST_CALCULATED) {
				// Home City 1 for this Corporation -- This Token is Free
				tCostToLayToken = getHomeBaseCost (homeCity1, tMapCellID);
			}
			if (tCostToLayToken == NO_COST_CALCULATED) {
				// Home City 2 for this Corporation -- This Token is Free
				tCostToLayToken = getHomeBaseCost (homeCity2, tMapCellID);
			}
		}
		// First Token is used on the Market

		/* If Laying Base Token -- Cost is Zero */
		// Test by comparing the Available Count to the Total Starting Count,
		// If Available Count plus 1 equals Total Starting Count -- this is First Token
		if (tCostToLayToken == NO_COST_CALCULATED) {
			tCostToLayToken = getNonBaseTokenCost ();
		}

		// Also note, some games may vary token cost on Distance from Home Station

		return tCostToLayToken;
	}

	public int getNonBaseTokenCost () {
		int tCostToLayToken;
		MapToken tFirstToken;

		tCostToLayToken = NO_COST;
		if (benefitInUse.realBenefit ()) {
			tCostToLayToken = benefitInUse.getCost ();
		} else {
			if (mapTokens.size () > 0) {
				tFirstToken = mapTokens.get (0);
				tCostToLayToken = tFirstToken.getCost ();
			}
		}

		return tCostToLayToken;
	}

	private int getHomeBaseCost (MapCell aBaseMapCell, String aMapCellID) {
		int tCostToLayHome;

		// TODO: non-1830 Games, need to determine if cost is based on Distance
		tCostToLayHome = NO_COST_CALCULATED;
		if (aBaseMapCell != MapCell.NO_MAP_CELL) {
			if (aBaseMapCell.getID () == aMapCellID) {
				tCostToLayHome = 0;
			}
		}

		return tCostToLayHome;
	}

	@Override
	public String buildCorpInfoLabel () {
		return buildCorpInfoLabel (GUI.NULL_STRING);
	}
	
	@Override
	public String buildCorpInfoLabel (String aLoanInfo) {
		String tCorpLabel = "";

		tCorpLabel = super.buildCorpInfoLabel (aLoanInfo);
		tCorpLabel += "<br>Tokens: " + getTokenCount ();
		tCorpLabel = "<html>" + tCorpLabel + "</html>";

		return tCorpLabel;
	}

	@Override
	public JPanel buildPortfolioTrainsJPanel (CorporationFrame aItemListener, GameManager aGameManager,
			boolean aFullTrainPortfolio, boolean aCanBuyTrain, String aDisableToolTipReason,
			Corporation aBuyingCorporation) {
		JPanel tTrainPortfolioInfoJPanel;
		int tTokenCount;

		tTokenCount = getTokenCount ();
		tTrainPortfolioInfoJPanel = super.buildPortfolioTrainsJPanel (aItemListener, aGameManager, aFullTrainPortfolio,
				aCanBuyTrain, aDisableToolTipReason, aBuyingCorporation, tTokenCount);

		return tTrainPortfolioInfoJPanel;
	}
}
