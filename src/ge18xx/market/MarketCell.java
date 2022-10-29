package ge18xx.market;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.w3c.dom.NodeList;

//
//  MarketCell.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

/*
 *		MarketCell Java Source
 *
 */

import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenStack;
import ge18xx.round.StockRound;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class MarketCell {
	final static AttributeName AN_VALUE = new AttributeName ("value");
	final static AttributeName AN_REGION = new AttributeName ("region");
	public final static ElementName EN_MARKET_CELL = new ElementName ("MarketCell");
	public final static String NO_COORDINATES = null;
	public final static MarketCell NO_MARKET_CELL = null;
	public final static MarketCell NO_SHARE_PRICE = null;
	Market market;
	int value;
	int xCenter, yCenter;
	MarketRegion marketRegion;
	boolean isSelected;
	MarketCell neighbors[];
	Movement SoldOut, NoDividend, HalfDividend, FullDividend, ShareSale;
	String coordinates;
	TokenStack tokens;

	static final MarketRegion REGIONS[] = { MarketRegion.Normal, MarketRegion.Yellow, MarketRegion.Brown,
			MarketRegion.Green, MarketRegion.Ledge, MarketRegion.Orange, MarketRegion.Closed, MarketRegion.Start,
			MarketRegion.Unused };
	static Movement OUT, NONE, HALF, FULL, SHARE, DOWN, UP;
	static int neighborCount = 0;
	static final int NEIGHBOR_UP = 0;
	static final int NEIGHBOR_RIGHT = 1;
	static final int NEIGHBOR_DOWN = 2;
	static final int NEIGHBOR_LEFT = 3;
	static final int NEIGHBOR_DOWN_RIGHT = 4;
	static final int NEIGHBOR_NONE = -1;
	static final String NO_COORDS = "<NONE>";
	public static final int NO_STOCK_PRICE = 0;

	public MarketCell () {
		Movement tNoMovement = new Movement ();
		setOtherValues (NO_STOCK_PRICE, tNoMovement, tNoMovement, tNoMovement, tNoMovement, tNoMovement, 0);
		setMarketRegion (MarketRegion.Unused);
		setXYCoord (0, 0);
		setCoordinates (NO_COORDS);
	}

	public MarketCell (XMLNode aCellNode, int aNeighborCount, Market aMarket) {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tRegion;
		int tValue;
		int tChildrenIndex;
		int tChildrenCount;
		MarketRegion tMarketRegion;
		Movement tOut = new Movement ();
		Movement tNone = new Movement ();
		Movement tHalf = new Movement ();
		Movement tFull = new Movement ();
		Movement tShare = new Movement ();

		tRegion = aCellNode.getThisAttribute (AN_REGION);
		tMarketRegion = getMarketRegionFromName (tRegion);
		tValue = aCellNode.getThisIntAttribute (AN_VALUE);
		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tChildrenIndex = 0; tChildrenIndex < tChildrenCount; tChildrenIndex++) {
			tChildNode = new XMLNode (tChildren.item (tChildrenIndex));
			tChildName = tChildNode.getNodeName ();
			if ("SoldOut".equals (tChildName)) {
				tOut = new Movement (tChildNode);
			} else if ("NoDividend".equals (tChildName)) {
				tNone = new Movement (tChildNode);
			} else if ("HalfDividend".equals (tChildName)) {
				tHalf = new Movement (tChildNode);
			} else if ("FullDividend".equals (tChildName)) {
				tFull = new Movement (tChildNode);
			} else if ("ShareSale".equals (tChildName)) {
				tShare = new Movement (tChildNode);
			}
		}
		setOtherValues (tValue, tOut, tNone, tHalf, tFull, tShare, aNeighborCount);
		setMarketRegion (tMarketRegion);
		setMarket (aMarket);
		setCoordinates (NO_COORDS);
	}

	public void addTokenToBottom (Token aToken) {
		tokens.addTokenToBottom (aToken);
	}

	public void addTokenToLocation (int aLocation, Token aToken) {
		tokens.addTokenToLocation (aLocation, aToken);
	}

	public void addTokenToTop (Token aToken) {
		tokens.addTokenToTop (aToken);
	}

	public int compareStackLocation (Corporation aCorporation1, Corporation aCorporation2) {
		int tCompareStackLocation;

		tCompareStackLocation = tokens.compareLocation (aCorporation1, aCorporation2);

		return tCompareStackLocation;
	}

	public boolean containingPoint (Point2D.Double aPoint, int aWidth, int aHeight) {
		int tXTopLeft, tYTopLeft;
		int tXBottomRight, tYBottomRight;
		int tX, tY;
		boolean tContains = false;

		tX = (int) aPoint.getX ();
		tY = (int) aPoint.getY ();
		tXTopLeft = xCenter - (int) (aWidth / 2);
		tYTopLeft = yCenter - (int) (aHeight / 2);
		tXBottomRight = tXTopLeft + aWidth;
		tYBottomRight = tYTopLeft + aHeight;
		if ((tX >= tXTopLeft) && (tX < tXBottomRight)) {
			if ((tY >= tYTopLeft) && (tY < tYBottomRight)) {
				tContains = true;
			}
		}

		return tContains;
	}

	public boolean containingPoint (Point aPoint, int aWidth, int aHeight) {
		int tXTopLeft, tYTopLeft;
		int tXBottomRight, tYBottomRight;
		int tX, tY;
		boolean tContains = false;

		tX = (int) aPoint.getX ();
		tY = (int) aPoint.getY ();
		tXTopLeft = xCenter - (int) (aWidth / 2);
		tYTopLeft = yCenter - (int) (aHeight / 2);
		tXBottomRight = tXTopLeft + aWidth;
		tYBottomRight = tYTopLeft + aHeight;
		if ((tX >= tXTopLeft) && (tX < tXBottomRight)) {
			if ((tY >= tYTopLeft) && (tY < tYBottomRight)) {
				tContains = true;
			}
		}

		return tContains;
	}

	public boolean canBuyMultiple () {
		return marketRegion.notCountAsBuy ();
	}

	public boolean countsAgainstCertificateLimit () {
		return marketRegion.getCountAgainstCertificateLimit ();
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tElement;
		final ElementName EN_SOLD_OUT = new ElementName ("SoldOut");
		final ElementName EN_NO_DIVIDEND = new ElementName ("NoDividend");
		final ElementName EN_HALF_DIVIDEND = new ElementName ("HalfDividend");
		final ElementName EN_FULL_DIVIDEND = new ElementName ("FullDividend");
		final ElementName EN_SHARE_SELL = new ElementName ("ShareSale");
		final ElementName EN_MARKET_CELL = new ElementName ("MarketCell");
		final AttributeName AN_REGION = new AttributeName ("region");
		final AttributeName AN_VALUE = new AttributeName ("value");

		tElement = aXMLDocument.createElement (EN_MARKET_CELL);
		tElement.setAttribute (AN_REGION, getName ());
		if (!isClosed () && !isUnused ()) {
			tElement.setAttribute (AN_VALUE, value);
			createMoveElement (aXMLDocument, tElement, SoldOut, EN_SOLD_OUT);
			createMoveElement (aXMLDocument, tElement, NoDividend, EN_NO_DIVIDEND);
			createMoveElement (aXMLDocument, tElement, HalfDividend, EN_HALF_DIVIDEND);
			createMoveElement (aXMLDocument, tElement, FullDividend, EN_FULL_DIVIDEND);
			createMoveElement (aXMLDocument, tElement, ShareSale, EN_SHARE_SELL);
		}

		return tElement;
	}

	public void createMoveElement (XMLDocument aXMLDocument, XMLElement tXMLElement2, Movement aMove,
			ElementName aElementName) {
		XMLElement tXMLElement;

		if (aMove != Movement.NO_MOVEMENT) {
			tXMLElement = aMove.createElement (aXMLDocument, aElementName);
			tXMLElement2.appendChild (tXMLElement);
		}
	}

	public void fullOwnershipAdjustment (StockRound aStockRound) {
		if (tokens.getTokenCount () > 0) {
			tokens.fullOwnershipAdjustment (aStockRound);
		}
	}

	public void doPayNoDividendAdjustment (ShareCompany aShareCompany, PayNoDividendAction aPayNoDividendAction) {
		tokens.doPayNoDividendAdjustment (aShareCompany, aPayNoDividendAction);
	}

	public void doPayFullDividendAdjustment (ShareCompany aShareCompany, PayFullDividendAction aPayFullDividendAction) {
		tokens.doPayFullDividendAdjustment (aShareCompany, aPayFullDividendAction);
	}

	public void draw (Graphics g, int width, int height) {
		int x1, y1, valueWidth;
		int arrowHeadHeight, arrowHeadWidth, arrowDownOffset, arrowSideOffset;
		String valueLabel;
		MarketCell tLeftCell = getNeighborLeft ();
		MarketCell tRightCell = getNeighborRight ();
		MarketCell tUpCell = getNeighborUp ();
		MarketCell tDownCell = getNeighborDown ();
		Color tCellColor, tTextColor;

		valueLabel = new Integer (value).toString ();
		arrowDownOffset = 5;
		arrowSideOffset = 5;
		arrowHeadHeight = 5;
		arrowHeadWidth = 3;
		x1 = xCenter - width / 2;
		y1 = yCenter - height / 2;
		tTextColor = marketRegion.getTextColor ();
		tCellColor = marketRegion.getColor ();
		g.setColor (tCellColor);
		g.fillRect (x1, y1, width, height);
		g.setColor (Color.black);
		if (isStart ()) {
			if (tUpCell == NO_MARKET_CELL) {
				drawTopRed (g, x1, y1, width, height);
			} else {
				if (tUpCell.isStart ()) {
					g.setColor (tTextColor);
					g.drawLine (x1, y1, x1 + width, y1);
				} else {
					drawTopRed (g, x1, y1, width, height);
				}
			}
			if (tDownCell == NO_MARKET_CELL) {
				drawBottomRed (g, x1, y1, width, height);
			} else {
				if (tDownCell.isStart ()) {
					g.setColor (tTextColor);
					g.drawLine (x1, y1 + height, x1 + width, y1 + height);
				} else {
					drawBottomRed (g, x1, y1, width, height);
				}
			}
			if (tRightCell == NO_MARKET_CELL) {
				drawRightRed (g, x1, y1, width, height);
			} else {
				if (tRightCell.isStart ()) {
					g.setColor (tTextColor);
					g.drawLine (x1 + width, y1, x1 + width, y1 + height);
				} else {
					drawRightRed (g, x1, y1, width, height);
				}
			}
			if (tLeftCell == NO_MARKET_CELL) {
				drawLeftRed (g, x1, y1, width, height);
			} else {
				if (tLeftCell.isStart ()) {
					g.setColor (tTextColor);
					g.drawLine (x1, y1, x1, y1 + height);
				} else {
					drawLeftRed (g, x1, y1, width, height);
				}
			}
		} else {
			if (isOpen ()) {
				g.setColor (tTextColor);
				if (tLeftCell == NO_MARKET_CELL) {
					g.drawLine (x1, y1, x1, y1 + height);
				} else {
					if (tLeftCell.isStart ()) {
						drawLeftRed (g, x1 - 1, y1, width, height);
					} else {
						if (tLeftCell.isFullDividendUp ()) {
							drawLeftRed (g, x1 - 1, y1, width, height);
						} else {
							g.drawLine (x1, y1, x1, y1 + height);
						}
					}
				}
				if (tUpCell == NO_MARKET_CELL) {
					g.drawLine (x1, y1, x1 + width, y1);
				} else if ((!tUpCell.isStart ()) && (!tUpCell.isLedge ())) {
					g.drawLine (x1, y1, x1 + width, y1);
				} else {
					drawTopRed (g, x1, y1 - 1, width, height);
				}
				g.setColor (tTextColor);
				g.drawLine (x1 + width, y1, x1 + width, y1 + height);
				g.drawLine (x1, y1 + height, x1 + width, y1 + height);
			} else {
				if (tUpCell != NO_MARKET_CELL) {
					if (tUpCell.isOpen ()) {
						g.setColor (tTextColor);
						g.drawLine (x1, y1, x1 + width, y1);
					}
				}
			}
		}
		if (isLedge ()) {
			drawBottomRed (g, x1, y1, width, height);
		}
		if (isNoDividendDown ()) {
			/* If no dividend send down, draw a down arrow */
			g.setColor (tTextColor);
			g.drawLine (x1 + arrowSideOffset, y1 + arrowDownOffset, x1 + arrowSideOffset,
					y1 + height - arrowDownOffset);
			g.drawLine (x1 + arrowSideOffset, y1 + height - arrowDownOffset, x1 + arrowSideOffset - arrowHeadWidth,
					y1 + height - arrowDownOffset - arrowHeadHeight);
			g.drawLine (x1 + arrowSideOffset, y1 + height - arrowDownOffset, x1 + arrowSideOffset + arrowHeadWidth,
					y1 + height - arrowDownOffset - arrowHeadHeight);
		}
		if (isFullDividendUp ()) {
			/* if full dividend send up, draw a up arrow */
			g.setColor (tTextColor);
			g.drawLine (x1 + width - arrowSideOffset, y1 + arrowDownOffset, x1 + width - arrowSideOffset,
					y1 + height - arrowDownOffset);
			g.drawLine (x1 + width - arrowSideOffset, y1 + arrowDownOffset,
					x1 + width - arrowSideOffset - arrowHeadWidth, y1 + arrowDownOffset + arrowHeadHeight);
			g.drawLine (x1 + width - arrowSideOffset, y1 + arrowDownOffset,
					x1 + width - arrowSideOffset + arrowHeadWidth, y1 + arrowDownOffset + arrowHeadHeight);

			if (tRightCell != NO_MARKET_CELL) {
				drawRightRed (g, x1, y1, width, height);
			}
		}
		if (isOpen () && isUsed ()) {
			valueWidth = g.getFontMetrics ().stringWidth (valueLabel);
			x1 = xCenter - valueWidth / 2;
			y1 = yCenter;
			g.setColor (tTextColor);
			g.drawString (valueLabel, x1, y1);
		}
		g.setColor (Color.black);
		drawTokenStack (g, width, height);
	}

	private void drawBottomRed (Graphics g, int x1, int y1, int width, int height) {
		drawThickRed (g, x1, y1 + height - 1, x1 + width, y1 + height - 1, true);
	}

	private void drawLeftRed (Graphics g, int x1, int y1, int width, int height) {
		drawThickRed (g, x1, y1, x1, y1 + height, false);
	}

	private void drawRightRed (Graphics g, int x1, int y1, int width, int height) {
		drawThickRed (g, x1 + width - 1, y1, x1 + width - 1, y1 + height, false);
	}

	private void drawThickRed (Graphics g, int x1, int y1, int x2, int y2, boolean aHorizontal) {
		g.setColor (Color.red);
		g.drawLine (x1, y1, x2, y2);
		if (aHorizontal) {
			g.drawLine (x1, y1 + 1, x2, y2 + 1);
		} else {
			g.drawLine (x1 + 1, y1, x2 + 1, y2);
		}
		g.setColor (Color.black);
	}

	private void drawTokenStack (Graphics g, int width, int height) {
		int tWidth, tHeight, xTL, yTL;

		if (tokens.getTokenCount () > 0) {
			tWidth = (int) (width / 3);
			tHeight = (int) (height / 3);
			xTL = xCenter - tWidth - (int) (tWidth / 3);
			yTL = yCenter;
			tWidth = tWidth + tWidth;
			tHeight = tHeight + tHeight;
			tokens.drawStack (g, xTL, yTL, tWidth, tHeight);
		}
	}

	private void drawTopRed (Graphics g, int x1, int y1, int width, int height) {
		drawThickRed (g, x1, y1, x1 + width, y1, true);
	}

	public Token findTokenFor (String aCompanyAbbrev) {
		Token tToken;

		tToken = null;
		if (tokens.getTokenCount () > 0) {
			tToken = tokens.findTokenFor (aCompanyAbbrev);
		}

		return tToken;
	}

	public int getCellCountToBottom () {
		int tCellsToBottom, tNeighbor;
		MarketCell tMarketCell, tPreviousMarketCell;

		tCellsToBottom = 0;
		tPreviousMarketCell = this;
		tNeighbor = tPreviousMarketCell.ShareSale.getMoveNeighbor ();
		tMarketCell = tPreviousMarketCell.getProperNeighbor (tNeighbor);
		while (tPreviousMarketCell != tMarketCell) {
			tPreviousMarketCell = tMarketCell;
			tNeighbor = tPreviousMarketCell.ShareSale.getMoveNeighbor ();
			tMarketCell = tPreviousMarketCell.getProperNeighbor (tNeighbor);
			tCellsToBottom++;
		}

		return tCellsToBottom;
	}

	public String getCoordinates () {
		return coordinates;
	}

	public MarketCell getDividendHoldMarketCell () {
		MarketCell tMarketCell;
		int tNeighbor;

		tNeighbor = NoDividend.getMoveNeighbor ();
		tMarketCell = getProperNeighbor (tNeighbor);

		return tMarketCell;
	}

	public MarketCell getDividendHalfMarketCell () {
		MarketCell tMarketCell;
		int tNeighbor;

		tNeighbor = HalfDividend.getMoveNeighbor ();
		tMarketCell = getProperNeighbor (tNeighbor);

		return tMarketCell;
	}

	public MarketCell getDividendPayMarketCell () {
		MarketCell tMarketCell;
		int tNeighbor;

		tNeighbor = FullDividend.getMoveNeighbor ();
		tMarketCell = getProperNeighbor (tNeighbor);

		return tMarketCell;
	}

	public boolean getExceedPlayerCorpShareLimit () {
		return marketRegion.getExceedPlayerCorpShareLimit ();
	}

	public XMLElement getCellTokenElements (XMLDocument aXMLDocument) {
		XMLElement tMarketCellElements;
		XMLElement tTokenStackElements;

		tMarketCellElements = null;
		if (tokens.getTokenCount () > 0) {
			tMarketCellElements = aXMLDocument.createElement (EN_MARKET_CELL);
			tTokenStackElements = tokens.getTokenStackElements (aXMLDocument);
			tMarketCellElements.appendChild (tTokenStackElements);
		}

		return tMarketCellElements;
	}

	public MarketRegion getMarketRegion () {
		return marketRegion;
	}

	public MarketRegion getMarketRegionFromName (String aName) {
		MarketRegion tMarketRegion;
		int index;

		tMarketRegion = MarketRegion.Unused;
		for (index = 0; index < REGIONS.length; index++) {
			if (aName.equals (REGIONS [index].toString ().toUpperCase ())) {
				tMarketRegion = REGIONS [index];
			}
		}

		return tMarketRegion;
	}

	public String getName () {
		return marketRegion.toString ();
	}

	public static int getNeighborCount () {
		return neighborCount;
	}

	public MarketCell getNeighborDown () {
		return neighbors [NEIGHBOR_DOWN];
	}

	public MarketCell getNeighborLeft () {
		return neighbors [NEIGHBOR_LEFT];
	}

	public MarketCell getNeighborRight () {
		return neighbors [NEIGHBOR_RIGHT];
	}

	public MarketCell getNeighborUp () {
		return neighbors [NEIGHBOR_UP];
	}

	public MarketCell getProperNeighbor (int aNeighbor) {
		MarketCell tMarketCell;

		if (aNeighbor != NEIGHBOR_NONE) {
			if (aNeighbor == NEIGHBOR_DOWN_RIGHT) {
				tMarketCell = neighbors [NEIGHBOR_DOWN];
				tMarketCell = tMarketCell.getProperNeighbor (NEIGHBOR_RIGHT);
			} else {
				tMarketCell = neighbors [aNeighbor];
			}
		} else {
			tMarketCell = this;
		}

		return tMarketCell;
	}

	public Color getRegionColor () {
		return marketRegion.getColor ();
	}

	public MarketCell getSellShareMarketCell (int aSharesBeingSoldCount) {
		MarketCell tMarketCell;
		int tNeighbor;
		boolean tMoveDown;

		tMoveDown = true;
		if (isLedge ()) {
			if (aSharesBeingSoldCount < getCellCountToBottom ()) {
				tMoveDown = false;
			}
		}
		if (tMoveDown) {
			tNeighbor = ShareSale.getMoveNeighbor ();
			tMarketCell = getProperNeighbor (tNeighbor);
		} else {
			tMarketCell = this;
		}

		return tMarketCell;
	}

	public MarketCell getSoldOutMarketCell () {
		MarketCell tMarketCell;
		int tNeighbor;

		tNeighbor = SoldOut.getMoveNeighbor ();
		tMarketCell = getProperNeighbor (tNeighbor);

		return tMarketCell;
	}

	public Token getTokenGM (String aCompanyAbbrev) {
		Token tToken;

		tToken = market.getTokenGM (aCompanyAbbrev);

		return tToken;
	}

	public Token getToken (String aCompanyAbbrev) {
		Token tToken;

		tToken = tokens.removeToken (aCompanyAbbrev);

		return tToken;
	}

	public int getTokenLocation (String aCompanyAbbrev) {
		int tLocation;

		tLocation = tokens.getLocation (aCompanyAbbrev);

		return tLocation;
	}

	public String getToolTip () {
		String tTip;

		tTip = "";
		if (getValue () > 0) {
			tTip = "<html>";
			if (!marketRegion.isNormal ()) {
				tTip += marketRegion.getName () + " ";
			}
			tTip += "Cell <b>" + getCoordinates () + "</b><br>";
			tTip += "Value <b>" + getValue () + "</b><br>";
			if (tokens.getTokenCount () > 0) {
				tTip += "Company Tokens in Top Down Order:<br>";
				tTip += tokens.getToolTip ();
			}
			tTip += marketRegion.getToolTip ();
			tTip += "</html>";
		} else {
			tTip = "<html>" + marketRegion.getToolTip () + "</html>";
		}

		return tTip;
	}

	public int getValue () {
		return value;
	}

	public int getX () {
		return xCenter;
	}

	public int getY () {
		return yCenter;
	}

	public boolean isClosed () {
		return (marketRegion == MarketRegion.Closed);
	}

	public boolean isFullDividendUp () {
		return (FullDividend.equals (UP));
	}

	public boolean isLedge () {
		return (marketRegion.isLedge ());
	}

	public boolean isNoDividendDown () {
		return (NoDividend.equals (DOWN));
	}

	public boolean isOpen () {
		return marketRegion.isOpen ();
	}

	public boolean isRightOf (MarketCell aMarketCell2) {
		boolean tIsRightOf;
		int tColID1, tColID2;

		tColID1 = Integer.parseInt (coordinates.substring (1));
		tColID2 = Integer.parseInt (aMarketCell2.getCoordinates ().substring (1));
		if (tColID1 < tColID2) {
			tIsRightOf = true;
		} else {
			tIsRightOf = false;
		}

		return tIsRightOf;
	}

	public boolean isAbove (MarketCell aMarketCell2) {
		boolean tIsAbove;
		String tCoordinates1, tCoordinates2;

		tCoordinates1 = coordinates;
		tCoordinates2 = aMarketCell2.getCoordinates ();
		if (tCoordinates1.compareTo (tCoordinates2) > 0) {
			tIsAbove = true;
		} else {
			tIsAbove = false;
		}

		return tIsAbove;
	}

	public boolean isSelected () {
		return isSelected;
	}

	public boolean isStart () {
		return (marketRegion.isStart ());
	}

	public boolean isUnused () {
		return (marketRegion.isUnused ());
	}

	public boolean isUsed () {
		return (marketRegion.isUsed ());
	}

	public void loadMarketTokens (XMLNode aXMLMarketNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (tokenStackParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLMarketNode, TokenStack.EN_TOKENS);
	}

	ParsingRoutineI tokenStackParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aMarketCellNode) {
			tokens.loadTokenStack (aMarketCellNode);
		}
	};

	public void printMarketCellInfo () {
		System.out.println ("Market Cell at " + coordinates + " value of " + value);
	}

	public void redrawMarket () {
		market.redrawMarket ();
	}

	public void select () {
		isSelected = true;
	}

	public void setCoordinates (String aCoordinates) {
		coordinates = aCoordinates;
	}

	public void setMarket (Market aMarket) {
		market = aMarket;
	}

	public void setMarketRegion (MarketRegion aMarketRegion) {
		marketRegion = aMarketRegion;
	}

	public void setNeighbor (int aDirection, MarketCell aMarketCell) {
		if (neighbors [aDirection] == NO_MARKET_CELL) {
			neighbors [aDirection] = aMarketCell;
			if (neighborCount == 4) {
				aMarketCell.setNeighbor ((aDirection + 2) % 4, this);
			} else if (neighborCount == 6) {
				aMarketCell.setNeighbor ((aDirection + 3) % 6, this);
			}
		}
	}

	public void setOtherValues (int aValue, Movement aOut, Movement aNone, Movement aHalf, Movement aFull,
			Movement aShare, int aNeighborCount) {
		if (OUT == Movement.NO_MOVEMENT) {
			OUT = new Movement (-1, 0);
			NONE = new Movement (0, -1);
			HALF = new Movement (0, 0);
			FULL = new Movement (0, 1);
			SHARE = new Movement (1, 0);
			DOWN = new Movement (1, 0);
			UP = new Movement (-1, 0);
		}
		value = aValue;
		SoldOut = aOut;
		NoDividend = aNone;
		HalfDividend = aHalf;
		FullDividend = aFull;
		ShareSale = aShare;
		if (neighborCount == 0) {
			neighborCount = aNeighborCount;
		}
		neighbors = new MarketCell [aNeighborCount];
		unSelect ();
		tokens = new TokenStack (this);
	}

	public void setXYCoord (int Xc, int Yc) {
		xCenter = (int) Xc;
		yCenter = (int) Yc;
	}

	public void unSelect () {
		isSelected = false;
	}
}
