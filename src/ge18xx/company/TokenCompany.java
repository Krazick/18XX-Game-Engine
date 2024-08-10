package ge18xx.company;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

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
import ge18xx.company.benefit.MapBenefit;
import ge18xx.game.GameManager;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.LayTokenAction;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public abstract class TokenCompany extends TrainCompany {
	public final static ElementName EN_TOKEN_COMPANY = new ElementName ("TokenCompany");
	public final static AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	public final static AttributeName AN_ALL_TOKENS_COST = new AttributeName ("allTokensCost");
	public final static AttributeName AN_TOKENS = new AttributeName ("tokens");
	public final static AttributeName AN_AVAILABLE_TOKEN_COUNT = new AttributeName ("availableTokenCount");
	public final static TokenCompany NO_TOKEN_COMPANY = null;
	public final static int MIN_TOKEN_COUNT = 1;
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
	
	Tokens tokens;
	int allTokensCost;
	int totalTokenCount;
	String tokenType;

	public TokenCompany (int aID, String aName) {
		super (aID, aName);
		setupAllTokens (MIN_TOKEN_COUNT);
	}

	public TokenCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);

		int tTotalTokenCount;
		int tAllTokensCost;
		String tTokenType;
		
		tTotalTokenCount = aChildNode.getThisIntAttribute (AN_TOKENS);
		tAllTokensCost = aChildNode.getThisIntAttribute (AN_ALL_TOKENS_COST);
		tTokenType = aChildNode.getThisAttribute (AN_TOKEN_TYPE);
		setTotalTokenCount (tTotalTokenCount);
		setAllTokensCost (tAllTokensCost);
		setupAllTokens (tTotalTokenCount);
		setTokenType (tTokenType);
	}
	
	private void setupAllTokens (int aTotalTokenCount) {
		Token tMarketToken;
		int tAddForFirstHome;
		
		setTotalTokenCount (aTotalTokenCount);
		tAddForFirstHome = 0;
		if (hasHomeCell ()) {
			tAddForFirstHome = 1;
		}
		tokens = new Tokens (totalTokenCount + tAddForFirstHome);
		tMarketToken = new Token (this, TokenType.MARKET);
		tokens.addNewToken (tMarketToken, TokenType.MARKET, Token.NO_COST);
		setupNewMapTokens ();
	}
	
	private void setupNewMapTokens () {
		MapToken tMapToken;
		
		tMapToken = new MapToken ();
		tMapToken.setCompany (this);
		addNTokens (totalTokenCount, tMapToken);
	}

	public void setTokenType (String aTokenType) {
		tokenType = aTokenType;
	}
	
	public void setTotalTokenCount (int aTotalTokenCount) {
		totalTokenCount = aTotalTokenCount;
	}

	public void setAllTokensCost (int aAllTokensCost) {
		allTokensCost = aAllTokensCost;
	}

	public void addNTokens (int aCount, MapToken aMapToken) {
		MapToken tMapToken;
		int tStartIndex;
		int tIndex;
		int tCost;
		TokenType tTokenTypeToAdd;

		tCost = Token.NO_COST;
		tStartIndex = 1;
		// TODO: For the Token Type (Fixed Cost vs Distance) MUST get from XML Game Info File, not static 1835 for Example
		tTokenTypeToAdd = TokenType.FIXED_COST;
		if (tokenType != GUI.NULL_STRING) {
			if (tokenType.equals (TokenType.FIXED_COST.toString ())) {
				System.out.println ("Fixed Cost Token Type");
			} else 	if (tokenType.equals (TokenType.RANGE_COST.toString ())) {
				System.out.println ("Range Cost Token Type");
			}
		}
		if (homeCityGrid1 != XMLNode.NO_VALUE) {
			tStartIndex++;
			tMapToken = new MapToken (aMapToken, tCost, TokenType.HOME1);
			tokens.addNewToken (tMapToken, TokenType.HOME1, tCost);
		}
		if (homeCityGrid2 != XMLNode.NO_VALUE) {
			tStartIndex++;
			tMapToken = new MapToken (aMapToken, tCost, TokenType.HOME2);
			tokens.addNewToken (tMapToken, TokenType.HOME2, tCost);
		}
		if (tStartIndex == 1) {
			tStartIndex++;
		}
		
		// TODO: Must get the FIXED Cost from the XML Game Info Data File
		for (tIndex = tStartIndex; tIndex <= aCount; tIndex++) {
			if (allTokensCost > Token.NO_COST) {
				tCost = allTokensCost;
			} else if (tIndex == tStartIndex) {
				tCost = 40;
			} else if (tIndex > tStartIndex) {
				tCost = 100;
			}
			tMapToken = new MapToken (aMapToken, tCost, tTokenTypeToAdd);
			tMapToken.setCompany (this);
			tokens.addNewToken (tMapToken, tTokenTypeToAdd, tCost);
		}
	}

	public void setTokenUsed (Token aToken, boolean aUsed) {
		tokens.setTokenUsed (aToken, aUsed);
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
	public void placeHomeToken (MapCell aMapCell, Location aHomeLocation) {
		MapFrame tMapFrame;
		Tile tTile;
		RevenueCenter tHomeRevenueCenter;
		int tHomeCount;

		MapToken tMapToken;

		if (aMapCell.isTileOnCell ()) {
			tTile = aMapCell.getTile ();
			tHomeRevenueCenter = tTile.getRCWithBaseForCorp (this);
			if (tHomeRevenueCenter != RevenueCenter.NO_CENTER) {
				tMapFrame = corporationList.getMapFrame ();
				tMapToken = getHome1Token ();
				tMapFrame.putTokenDownHere (this, tMapToken, TokenType.HOME1, aMapCell, tHomeRevenueCenter);
				if (isHomeTypeChoice ()) {
					removeOtherHome (aMapCell, aHomeLocation);
				}
			} else { // Given multiple choice for base location on tile
				tHomeCount = tTile.getCorporationHomeCount ();
				if (tHomeCount > 1) {
					corporationFrame.handlePlaceHomeToken ();
				} else {
					System.err.println ("No RevenueCenter found for " + getAbbrev () + " at " + aHomeLocation);
					System.err.println ("Corp Bases [" + tHomeCount + "]");
				}
			}
		}
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

	public int getTokenCost () {
		MapToken tMapToken;
		MapCell tMapCell;
		int tTokenCost;
		
		tMapToken = getMapTokenOnly ();
		tMapCell = MapCell.NO_MAP_CELL;
		tTokenCost = getTokenCost (tMapToken, TokenType.MAP, tMapCell); 
		
		return tTokenCost;
	}
	
	@Override
	public String reasonForNoTokenLay () {
		String tReason;

		tReason = NO_REASON;
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
		int tScale;

		tScale = Hex.getScale ();
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
		getCorporationStateElement (tXMLCorporationState, aXMLDocument);

		return tXMLCorporationState;
	}

	@Override
	public void appendOtherElements (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		tokens.getTokensElement (aXMLCorporationState, aXMLDocument);
		super.appendOtherElements (aXMLCorporationState, aXMLDocument);
	}

	@Override
	public void getCorporationStateElement (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		aXMLCorporationState.setAttribute (AN_AVAILABLE_TOKEN_COUNT, getTokenCount ());
		super.getCorporationStateElement (aXMLCorporationState, aXMLDocument);
	}

	/**
	 * Retrieve the Last available Token that is a MapToken
	 * 
	 * @return The Last available Map Token
	 * 
	 */
	@Override
	public MapToken getLastMapToken () {
		MapToken tMapToken;

		tMapToken = tokens.getLastMapToken ();
		tokens.setTokenUsed (tMapToken, true);
		
		return tMapToken;
	}

	/**
	 * Retrieve the next available Token that is a MapToken and set as Used
	 * 
	 * @return The first available Map Token
	 * 
	 */
	@Override
	public MapToken getMapToken () {
		MapToken tMapToken;

		tMapToken = getMapTokenOnly ();
		tokens.setTokenUsed (tMapToken, true);
		
		return tMapToken;
	}
	
	/**
	 * Retrieve the next available Token that is a MapToken
	 * 
	 * @return The first available Map Token
	 * 
	 */
	public MapToken getMapTokenOnly () {
		MapToken tMapToken;

		tMapToken = tokens.getMapToken ();
		
		return tMapToken;
	}
	
	/**
	 * Retrieve the next available Token that is a MapToken
	 * 
	 * @return The first available Map Token
	 * 
	 */
	@Override
	public MapToken getMapToken (TokenType aTokenType) {
		MapToken tMapToken;

		tMapToken = tokens.getMapToken (aTokenType);
		tokens.setTokenUsed (tMapToken, true);
		
		return tMapToken;
	}

	/**
	 * Retrieve the Home1 Token that is a MapToken
	 * 
	 * @return The Home1 Map Token
	 * 
	 */

	public MapToken getHome1Token () {
		MapToken tMapToken;

		tMapToken = tokens.getHome1Token ();
		tokens.setTokenUsed (tMapToken, true);
		
		return tMapToken;
	}

	/**
	 * Retrieve the Home2 Token that is a MapToken
	 * 
	 * @return The Home2 Map Token
	 * 
	 */

	public MapToken getHome2Token () {
		MapToken tMapToken;

		tMapToken = tokens.getHome2Token ();
		
		return tMapToken;
	}
	
	/**
	 * This method will return a MarketToken, never a MapToken
	 * 
	 * @return the Market Token for the Token Company, to be used on the Market
	 * 
	 */
	
	public Token getMarketToken () {
		Token tMarketToken;

		tMarketToken = tokens.getToken (TokenType.MARKET);
		tokens.setTokenUsed (tMarketToken, true);

		return tMarketToken;
	}
	
	/**
	 * This method will return a Token of the specified Type
	 * 
	 * @return the Market Token for the Token Company, to be used on the Market
	 * 
	 */
	
	public Token getToken (TokenType aTokenType) {
		Token tToken;

		tToken = tokens.getToken (aTokenType);
		tokens.setTokenUsed (tToken, true);

		return tToken;
	}
	
	/**
	 * This method will return a Token of the specified Index in the Tokens Array
	 * 
	 * @return the Token for the Token Company, at the specified Index location
	 * 
	 */
	
	public Token getTokenAt (int aIndex) {
		Token tToken;

		tToken = tokens.getTokenAt (aIndex);
		tokens.setTokenUsed (tToken, true);

		return tToken;
	}

	public TokenType getTokenType (Token aToken) {
		TokenType tTokenType;
		
		tTokenType = tokens.getTokenType (aToken);
		
		return tTokenType;
	}
	
	public int getTokenIndex (Token aToken) {
		int tTokenIndex;
		
		tTokenIndex = tokens.getTokenIndex (aToken);
		
		return tTokenIndex;
	}
	
	@Override
	public int getTokenCount () {
		int tTokenCount;

		if (tokens == Tokens.NO_TOKENS) {
			tTokenCount = 0;
		} else {
			tTokenCount = tokens.getAvailableTokenCount ();
		}
		
		return tTokenCount;
	}

	public int getTotalTokenCount () {
		int tTotalTokenCount;
		
		tTotalTokenCount = tokens.getTokenCount ();
		
		return tTotalTokenCount;
	}

	@Override
	public boolean haveMoneyForToken () {
		boolean tHaveMoneyForToken = true;
		MapToken tMapToken;
		int tTokenCost;
		
		tMapToken = getMapTokenOnly ();
		tTokenCost = getNonHomeTokenCost (tMapToken);
		if (tTokenCost > treasury) {
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
		
		tokens.loadStatus (aXMLNode);
	}

	@Override
	public void tokenWasPlaced (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, MapToken aMapToken,
								int aTokenIndex, boolean aAddLayTokenAction) {
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;

		tCurrentStatus = status;
		if ((status == ActorI.ActionStates.TileLaid) || 
			(status == ActorI.ActionStates.Tile2Laid) || 
			(status == ActorI.ActionStates.TileUpgraded)) {
			tStatusUpdated = updateStatus (ActorI.ActionStates.TileAndStationLaid);
		} else {
			tStatusUpdated = updateStatus (ActorI.ActionStates.StationLaid);
		}
		if (benefitInUse.isRealBenefit ()) {
			tStatusUpdated = true;
			benefitInUse.setUsed (true);
		}
		if (tStatusUpdated) {
			tNewStatus = status;
			if (aAddLayTokenAction) {
				addLayTokenAction (aMapCell, aTile, aRevenueCenterIndex, aMapToken, aTokenIndex, tCurrentStatus, tNewStatus);
			}
			tokens.setTokenUsed (aMapToken, true);

			updateInfo ();
		}
	}

	public void addLayTokenAction (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, 
									MapToken aMapToken, int aTokenIndex, 
									ActorI.ActionStates aCurrentStatus, ActorI.ActionStates aNewStatus) {
		LayTokenAction tLayTokenAction;
		TokenType tTokenType;
		MapBenefit tMapBenefit;
		Bank tBank;
		String tOperatingRoundID;
		int tTokenCost;

		tTokenType = tokens.getTokenType (aMapToken);
		tTokenCost = getTokenCost (aMapToken, tTokenType, aMapCell);
		if (tTokenCost == Token.RANGE_COST) {
			System.out.println ("Need to Calculate Cost for Token based on RANGE from Home");
			// TODO -- Calculate the Cost based on the Range from the Home Station
		}
		tOperatingRoundID = corporationList.getOperatingRoundID ();
		tLayTokenAction = new LayTokenAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
		tLayTokenAction.addLayTokenEffect (this, aMapCell, aTile, aRevenueCenterIndex, tTokenType, aTokenIndex, benefitInUse);
		addRemoveHomeEffect (aMapCell, tLayTokenAction);
		tLayTokenAction.addChangeCorporationStatusEffect (this, aCurrentStatus, aNewStatus);
		if (tTokenCost > 0) {
			tBank = corporationList.getBank ();
			transferCashTo (tBank, tTokenCost);
			tLayTokenAction.addCashTransferEffect (this, tBank, tTokenCost);
		}
		if (benefitInUse.isRealBenefit ()) {
			tLayTokenAction.addBenefitUsedEffect (this, benefitInUse);
			if (benefitInUse instanceof MapBenefit) {
				tMapBenefit = (MapBenefit) benefitInUse;
				tMapBenefit.completeBenefitInUse (this, tLayTokenAction);
			}
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
	public int getTokenCost (MapToken aMapToken, TokenType aTokenType, MapCell aMapCell) {
		int tTokenCost;
		String tMapCellID;

		// Token Cost Rules
		// -- On Any Base Location -- $0
		// -- If Private gives Free Token Lay -- $0
		// -- If Calculated on Token Laid Count:
		// -- First Token after Home Bases -- $40
		// -- Second and Later Tokens after Home Bases -- $100
		// -- If Calculated on Distance from Base -- $X * # of Hexes

		tTokenCost = NO_COST_CALCULATED;
		if (aMapCell != MapCell.NO_MAP_CELL) {
			tMapCellID = aMapCell.getID ();
			if (aTokenType == TokenType.HOME1) {
				tTokenCost = getHomeBaseCost (homeCity1, tMapCellID);
			} else if (aTokenType == TokenType.HOME2) {
				tTokenCost = getHomeBaseCost (homeCity2, tMapCellID);
			}
//
//			if (tTokenCost == NO_COST_CALCULATED) {
//				// Home City 1 for this Corporation -- This Token is Free
//				tTokenCost = getHomeBaseCost (homeCity1, tMapCellID);
//			}
//			if (tTokenCost == NO_COST_CALCULATED) {
//				// Home City 2 for this Corporation -- This Token is Free
//				tTokenCost = getHomeBaseCost (homeCity2, tMapCellID);
//			}
		}
		// First Token is used on the Market

		/* If Laying Base Token -- Cost is Zero */
		// Test by comparing the Available Count to the Total Starting Count,
		// If Available Count plus 1 equals Total Starting Count -- this is First Token
		if (tTokenCost == NO_COST_CALCULATED) {
			tTokenCost = getNonHomeTokenCost (aMapToken);
		}

		// Also note, some games may vary token cost on Distance from Home Station like 1835.
		// This is if the a RANGED COST

		return tTokenCost;
	}

	public int getNonHomeTokenCost (MapToken aMapToken) {
		int tTokenCost;

		tTokenCost = NO_COST;
		if (benefitInUse.isRealBenefit ()) {
			tTokenCost = benefitInUse.getCost ();
		} else {
			tTokenCost = tokens.getTokenCost (aMapToken);
		}

		return tTokenCost;
	}

	private int getHomeBaseCost (MapCell aHomeMapCell, String aMapCellID) {
		int tHomeTokenCost;
		
		// TODO: non-1830 Games, need to determine if cost is based on Distance
		tHomeTokenCost = NO_COST_CALCULATED;
		if (aHomeMapCell != MapCell.NO_MAP_CELL) {
			if (aHomeMapCell.getID () == aMapCellID) {
				tHomeTokenCost = 0;
			}
		}

		return tHomeTokenCost;
	}

	@Override
	public JPanel buildCorpInfoJPanel () {
		JPanel tCorpInfoPanel;
		JLabel tTokens;

		tCorpInfoPanel = super.buildCorpInfoJPanel ();
		if (! isClosed ()) {
			tTokens = new JLabel ("Tokens: " + getTokenCount ());
			tCorpInfoPanel.add (tTokens);
		}

		return tCorpInfoPanel;
	}

	@Override
	public JPanel buildPortfolioTrainsJPanel (CorporationFrame aItemListener, GameManager aGameManager,
			boolean aFullTrainPortfolio, Corporation aBuyingCorporation) {
		JPanel tTrainPortfolioInfoJPanel;
		int tTokenCount;

		tTokenCount = getTokenCount ();
		tTrainPortfolioInfoJPanel = super.buildPortfolioTrainsJPanel (aItemListener, aGameManager, aFullTrainPortfolio, aBuyingCorporation, tTokenCount);

		return tTrainPortfolioInfoJPanel;
	}
}
