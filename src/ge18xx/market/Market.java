package ge18xx.market;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;

import org.w3c.dom.NodeList;

//
//  Market.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.Token;
import ge18xx.game.GameManager;
import ge18xx.round.StockRound;
import ge18xx.toplevel.MarketFrame;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.ParsingRoutineI;

public class Market extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	public static final ElementName EN_MARKET = new ElementName ("Market");
	public static final ElementName EN_ROW = new ElementName ("Row");
	public static final AttributeName AN_COLS = new AttributeName ("cols");
	public static final AttributeName AN_ROWS = new AttributeName ("rows");
	public static final AttributeName AN_COL = new AttributeName ("col");
	public static final AttributeName AN_ROW = new AttributeName ("row");
	public static final AttributeName AN_INDEX = new AttributeName ("index");
	public static final AttributeName AN_NEIGHBORS = new AttributeName ("neighbors");
	public static final MarketCell [] [] NO_MARKET_CELLS = null;
	public static final Market NO_MARKET = null;
	public static final String MARKET_CELL_ADJUSTMENT = "Market Cell Adjustment";
	MarketCell market[][];
	GameManager gameManager;
	int cellHeight;
	int cellWidth;
	int parseRowIndex;
	int parseColIndex;

	public Market (int CH, int CW, GameManager aGameManager) {
		addMouseListener (this);
		addMouseMotionListener (this);
		cellHeight = CH;
		cellWidth = CW;
		gameManager = aGameManager;
	}

	public void CalcCellCenters () {
		int rowIndex;
		int colIndex;
		int Xc;
		int Yc;
		int halfHeight = cellHeight / 2;
		int halfWidth = cellWidth / 2;

		Yc = halfHeight;
		for (rowIndex = 0; rowIndex < market.length; rowIndex++) {
			Xc = halfWidth;
			for (colIndex = 0; colIndex < market [rowIndex].length; colIndex++) {
				if (market [rowIndex] [colIndex] != MarketCell.NO_MARKET_CELL) {
					market [rowIndex] [colIndex].setXYCoord (Xc, Yc);
					Xc += cellWidth;
				}
			}
			Yc += cellHeight;
		}
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tRowElement;
		XMLElement tCellElement;
		int tRowCount;
		int tMaxColCount;
		int tColCount;
		int tRowIndex;
		int tColIndex;

		tXMLElement = aXMLDocument.createElement (EN_MARKET);

		tRowCount = getMaxRowCount ();
		tMaxColCount = getMaxColCount ();
		tXMLElement.setAttribute (AN_ROWS, tRowCount);
		tXMLElement.setAttribute (AN_COLS, tMaxColCount);
		tXMLElement.setAttribute (AN_NEIGHBORS, MarketCell.getNeighborCount ());
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tRowElement = aXMLDocument.createElement (EN_ROW);
			tRowElement.setAttribute (AN_INDEX, tRowIndex);
			tRowElement.setAttribute (AN_COLS, getColCount (tRowIndex));
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tCellElement = market [tRowIndex] [tColIndex].createElement (aXMLDocument);
				if (tCellElement != XMLElement.NO_XML_ELEMENT) {
					tRowElement.appendChild (tCellElement);
				}
			}
			tXMLElement.appendChild (tRowElement);
		}

		return tXMLElement;
	}

	public Integer [] getAllStartCells () {
		Integer [] tAllStartCells;
		List<Integer> tAllCells;
		int tCountofStartCells;
		int tRowCount;
		int tColCount;
		int tRowIndex;
		int tColIndex;
		int tCellIndex;
		int tMarketValue;

		tAllCells = new ArrayList <Integer> ();
		tCountofStartCells = 0;
		tRowCount = getMaxRowCount ();
		if (tRowCount > 0) {
			for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
				tColCount = getColCount (tRowIndex);
				for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
					if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
						if (market [tRowIndex] [tColIndex].isStart ()) {
						
							tMarketValue = market [tRowIndex] [tColIndex].getValue ();
							tAllCells.add (tMarketValue);
							tCountofStartCells++;
						}
					}
				}
			}
		}
		Collections.sort (tAllCells, Collections.reverseOrder ());
		tAllStartCells = new Integer [tCountofStartCells];
		for (tCellIndex = 0; tCellIndex < tCountofStartCells; tCellIndex++) {
			tAllStartCells [tCellIndex] = tAllCells.get (tCellIndex);
		}

		return tAllStartCells;
	}

	public MarketCell findStartCell (int aParPrice) {
		MarketCell tMarketCell;
		int tRowCount;
		int tColCount;
		int tRowIndex;
		int tColIndex;

		tMarketCell = MarketCell.NO_MARKET_CELL;
		tRowCount = getMaxRowCount ();
		if (tRowCount > 0) {
			for (tRowIndex = 0; (tRowIndex < tRowCount) && (tMarketCell == MarketCell.NO_MARKET_CELL); tRowIndex++) {
				tColCount = getColCount (tRowIndex);
				for (tColIndex = 0; (tColIndex < tColCount)
						&& (tMarketCell == MarketCell.NO_MARKET_CELL); tColIndex++) {
					if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
						if (market [tRowIndex] [tColIndex].isStart ()) {
							if (market [tRowIndex] [tColIndex].getValue () == aParPrice) {
								tMarketCell = market [tRowIndex] [tColIndex];
							}
						}
					}
				}
			}
		}

		return tMarketCell;
	}

	public void fullOwnershipAdjustment (StockRound aStockRound, Market aMarket) {
		MarketCell tMarketCell;
		MarketCell tPreviousMarketCell;
		int tRowCount;
		int tColCount;
		int tRowIndex;
		int tColIndex;
		boolean tNeighborDown;

		tMarketCell = MarketCell.NO_MARKET_CELL;
		tRowCount = getMaxRowCount ();
		if (tRowCount > 0) {
			tRowIndex = 0;
			tColCount = getRightmostColIndex (tRowIndex);
			for (tColIndex = tColCount; (tColIndex >= 0) && (tMarketCell == MarketCell.NO_MARKET_CELL); tColIndex--) {
				tNeighborDown = true;
				tMarketCell = market [tRowIndex] [tColIndex];
				while (tMarketCell == MarketCell.NO_MARKET_CELL) { // If Top of Row has no Market Cell
					// (e.g. 1835) move down column until we find one
					tMarketCell = market [++tRowIndex] [tColIndex];
				}
				while (tNeighborDown) {
					if (tMarketCell == MarketCell.NO_MARKET_CELL) {
						tNeighborDown = false;
					} else {
						tMarketCell.fullOwnershipAdjustment (aStockRound);
						tPreviousMarketCell = tMarketCell;
						tMarketCell = tMarketCell.getNeighborDown ();
						if (tPreviousMarketCell == tMarketCell) { // If Market Cell is unchanged after moving down, we
																	// are at bottom
							tNeighborDown = false;
						}
					}
				}
			}
		}
	}

	public int getColCount (int aRow) {
		int tColCount;
		
		if (market == NO_MARKET_CELLS) {
			tColCount = 0;
		} else {
			tColCount = market [aRow].length;
		}
		
		return tColCount;
	}

	public int getRightmostColIndex (int aRow) {
		int tColCount;

		tColCount = getColCount (aRow) - 1;
		if (tColCount > 0) {
			while (market [aRow] [tColCount] == MarketCell.NO_MARKET_CELL) {
				tColCount--;
			}
		}

		return tColCount;
	}

	public MarketCell getClosestMarketCell (int aPriceToFind, int aRowToExamine) {
		int tColCount;
		int tColIndex;
		int tCostDiff;
		int tMinDiff;
		int tCellValue;
		MarketCell tClosestMarketCell;
		MarketCell tMarketCell;
		
		tClosestMarketCell = MarketCell.NO_MARKET_CELL;
		tColCount = getColCount (aRowToExamine);
		tMinDiff = aPriceToFind;
		for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
			tMarketCell = getMarketCellAtRowCol (aRowToExamine, tColIndex);
			tCellValue = tMarketCell.getValue ();
			if (tCellValue == aPriceToFind) {
				tCostDiff = 1;
				tMinDiff = 0;
				tClosestMarketCell = tMarketCell;
			} else if (tCellValue < aPriceToFind) {
				tCostDiff = aPriceToFind - tCellValue;
			} else {
				tCostDiff = tCellValue - aPriceToFind;
			}
			if (tCostDiff < tMinDiff) {
				tClosestMarketCell = tMarketCell;
				tMinDiff = tCostDiff;
			}
		}
		
		return tClosestMarketCell;
	}
	
	public MarketCell getMarketCellAtCoordinates (String aCoordinates) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MarketCell tFoundMarketCell;

		tFoundMarketCell = MarketCell.NO_MARKET_CELL;
		tRowCount = getMaxRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount)
					&& (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tColIndex++) {
				if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
					if (market [tRowIndex] [tColIndex].getCoordinates ().equals (aCoordinates)) {
						tFoundMarketCell = market [tRowIndex] [tColIndex];
					}
				}
			}
		}

		return tFoundMarketCell;
	}

	public MarketCell getMarketCellAtRowCol (int aRow, int aCol) {
		MarketCell tFoundCell;

		tFoundCell = MarketCell.NO_MARKET_CELL;
		if ((aRow >= 0) && (aRow < getMaxRowCount ())) {
			if ((aCol >= 0) && (aCol < getColCount (aRow))) {
				tFoundCell = market [aRow] [aCol];
			}
		}

		return tFoundCell;
	}

	public MarketCell getMarketCellContainingPoint (Point2D.Double aPoint, int aCW, int aCH) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MarketCell tFoundMarketCell;

		tFoundMarketCell = MarketCell.NO_MARKET_CELL;
		tRowCount = getMaxRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount)
					&& (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tColIndex++) {
				if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
					if (market [tRowIndex] [tColIndex].containingPoint (aPoint, aCW, aCH)) {
						tFoundMarketCell = market [tRowIndex] [tColIndex];
					}
				}
			}
		}

		return tFoundMarketCell;
	}

	public MarketCell getMarketCellContainingPoint (Point aPoint, int aCW, int aCH) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MarketCell tFoundMarketCell;

		tFoundMarketCell = MarketCell.NO_MARKET_CELL;
		tRowCount = getMaxRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount)
					&& (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tColIndex++) {
				if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
					if (market [tRowIndex] [tColIndex].containingPoint (aPoint, aCW, aCH)) {
						tFoundMarketCell = market [tRowIndex] [tColIndex];
					}
				}
			}
		}

		return tFoundMarketCell;
	}

	public MarketCell getMarketCellContainingToken (String aCompanyAbbrev) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MarketCell tFoundMarketCell;
		Token tToken;

		tFoundMarketCell = MarketCell.NO_MARKET_CELL;
		tRowCount = getMaxRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount) && (tFoundMarketCell == MarketCell.NO_MARKET_CELL); tColIndex++) {
				if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
					tToken = market [tRowIndex] [tColIndex].findTokenFor (aCompanyAbbrev);
					if (tToken != Token.NO_TOKEN) {
						tFoundMarketCell = market [tRowIndex] [tColIndex];
					}
				}
			}
		}

		return tFoundMarketCell;
	}

	public XMLElement getMarketStateElements (XMLDocument aXMLDocument) {
		XMLElement tMarketElements;
		XMLElement tMarketCellElementTokens;
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MarketCell tMarketCell;

		tMarketElements = aXMLDocument.createElement (EN_MARKET);
		tRowCount = getMaxRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tMarketCell = market [tRowIndex] [tColIndex];
				if (tMarketCell != MarketCell.NO_MARKET_CELL) {
					tMarketCellElementTokens = tMarketCell.getCellTokenElements (aXMLDocument);
					if (tMarketCellElementTokens != XMLElement.NO_XML_ELEMENT) {
						tMarketCellElementTokens.setAttribute (AN_ROW, tRowIndex);
						tMarketCellElementTokens.setAttribute (AN_COL, tColIndex);
						tMarketElements.appendChild (tMarketCellElementTokens);
					}
				}
			}
		}

		return tMarketElements;
	}

	public int getMaxColCount () {
		int tMaxColCount;
		int tIndex;

		tMaxColCount = getColCount (0);
		if (tMaxColCount > 0) {
			for (tIndex = 1; tIndex < market.length; tIndex++) {
				if (market [tIndex].length > tMaxColCount) {
					tMaxColCount = market [tIndex].length;
				}
			}
		}

		return tMaxColCount;
	}

	public int getMaxRowCount () {
		int tMaxRowCount;
		
		if (market == NO_MARKET_CELLS) {
			tMaxRowCount = 0;
		} else {
			tMaxRowCount = market.length;
		}

		return tMaxRowCount;
	}

	public int getMaxX () {
		int tMaxCol;
		int tMaxX;

		tMaxCol = getMaxColCount ();
		tMaxX = market [0] [tMaxCol - 1].getX () + Double.valueOf (cellWidth / 2).intValue ();

		return tMaxX;
	}

	public int getMaxY () {
		int tMaxRow;
		int tMaxY;

		tMaxRow = getMaxRowCount ();
		tMaxY = market [tMaxRow - 1] [0].getY () + Double.valueOf (cellHeight / 2).intValue ();
		
		return tMaxY;
	}

	public Token getTokenGM (String aCompanyAbbrev) {
		Token tToken;

		tToken = gameManager.getToken (aCompanyAbbrev);

		return tToken;
	}

	@Override
	public String getTypeName () {
		return "Market";
	}

	public boolean goodCell (int aRow, int aCol) {
		boolean tGoodCell;
		int tMaxRow;
		int tColCount;

		tGoodCell = false;
		tMaxRow = getMaxRowCount ();
		if ((aRow >= 0) && (aRow < tMaxRow)) {
			tColCount = getColCount (aRow);
			if ((aCol >= 0) && (aCol < tColCount)) {
				tGoodCell = true;
			}
		}

		return tGoodCell;
	}

	public void loadMarketTokens (XMLNode aXMLMarketNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (marketCellParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLMarketNode, MarketCell.EN_MARKET_CELL);
	}

	ParsingRoutineI marketCellParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aMarketCellNode) {
			int tRow;
			int tCol;

			tRow = aMarketCellNode.getThisIntAttribute (AN_ROW);
			tCol = aMarketCellNode.getThisIntAttribute (AN_COL);
			if (goodCell (tRow, tCol)) {
				market [tRow] [tCol].loadMarketTokens (aMarketCellNode);
			}
		}
	};

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNode tXMLMarketRoot;
		XMLNode tChildNode;
		NodeList tChildren;
		String tChildName;
		int tMaxCols;
		int tRows;
		int tNeighborCount;
		int tChildrenCount;
		int index;
		boolean tLoadedRow = false;
		String [] tRowIds;
		String [] tColIds;
		String tUpperAlpha = "A:B:C:D:E:F:G:H:I:J:K:L:M:N:O:P:Q:R:S:T:U:V:W:X:Y:Z";
		String tNumerics = "1:2:3:4:5:6:7:8:9:10:11:12:13:14:15:16:17:18:19:20:21:22:23:24:25:26:27:28:29:30:31:32:33:34:35:36:37:38:39:40:41:42:43:44:45:46:47:48:49:50";
		MarketFrame tMarketFrame;

		tRowIds = tUpperAlpha.split (":");
		tColIds = tNumerics.split (":");
		tXMLMarketRoot = aXMLDocument.getDocumentNode ();
		tMaxCols = tXMLMarketRoot.getThisIntAttribute (AN_COLS);
		tRows = tXMLMarketRoot.getThisIntAttribute (AN_ROWS);
		tNeighborCount = tXMLMarketRoot.getThisIntAttribute (AN_NEIGHBORS);
		market = new MarketCell [tRows] [tMaxCols];
		tChildren = tXMLMarketRoot.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (index = 0; index < tChildrenCount; index++) {
			tChildNode = new XMLNode (tChildren.item (index));
			tChildName = tChildNode.getNodeName ();
			if (AN_ROW.equals (tChildName)) {
				tLoadedRow = loadXMLRow (tChildNode, tNeighborCount, tMaxCols, tRowIds, tColIds);
				if (!tLoadedRow) {
					System.err.println ("Found too many columns to Load.");
				}
			}
		}
		try {
			if (tLoadedRow) {
				CalcCellCenters ();
				setMarketSize ();
				market [0] [0].setMarket (this);
			}
		} catch (Exception exc) {
			System.err.println ("Error trying to Calculate Cell Centers, Setting Market Size");
		}

		tMarketFrame = gameManager.getMarketFrame ();
		tMarketFrame.setDefaults (tXMLMarketRoot);
		tMarketFrame.setDefaultFrameInfo ();
	}

	public boolean loadXMLRow (XMLNode aRowNode, int aNeighborCount, int aMaxCols, String [] aRowIds,
			String [] aColIds) {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		int tChildrenCount;
		int tRowIndex;
		int tIndex;
		int tChildIndex;
		boolean tGoodLoad;

		tGoodLoad = true;
		tRowIndex = aRowNode.getThisIntAttribute (AN_INDEX);

		tChildren = aRowNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tChildIndex = 0;
		for (tIndex = 0; (tIndex < tChildrenCount) && tGoodLoad; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (MarketCell.EN_MARKET_CELL.equals (tChildName)) {
				if (tChildIndex < aMaxCols) {
					market [tRowIndex] [tChildIndex] = new MarketCell (tChildNode, aNeighborCount, this);
					market [tRowIndex] [tChildIndex].setCoordinates (aRowIds [tRowIndex] + aColIds [tChildIndex]);
					if (tRowIndex > 0) {
						if (market [tRowIndex - 1] [tChildIndex] != MarketCell.NO_MARKET_CELL) {
							if (aNeighborCount == 4) {
								market [tRowIndex] [tChildIndex].setNeighbor (0, market [tRowIndex - 1] [tChildIndex]);
							} else {
								market [tRowIndex] [tChildIndex].setNeighbor (0, market [tRowIndex - 1] [tChildIndex]);
							}
						}
					}
					if (tChildIndex > 0) {
						if (market [tRowIndex] [tChildIndex - 1] != MarketCell.NO_MARKET_CELL) {
							if (aNeighborCount == 4) {
								market [tRowIndex] [tChildIndex].setNeighbor (3, market [tRowIndex] [tChildIndex - 1]);
							} else {
								market [tRowIndex] [tChildIndex].setNeighbor (5, market [tRowIndex] [tChildIndex - 1]);
							}
						}
					}
					tChildIndex++;
				} else {
					tGoodLoad = false;
				}
			}
		}

		return tGoodLoad;
	}

	@Override
	public void mouseClicked (MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged (MouseEvent arg0) {
	}

	@Override
	public void mouseEntered (MouseEvent e) {
	}

	@Override
	public void mouseExited (MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved (MouseEvent arg0) {
		MarketCell tMarketCell;

		Point tPoint = arg0.getPoint ();
		tMarketCell = getMarketCellContainingPoint (tPoint, cellWidth, cellHeight);
		if (tMarketCell == MarketCell.NO_MARKET_CELL) {
			setToolTipText ("***");
		} else {
			setToolTipText (tMarketCell.getToolTip ());
		}
	}

	@Override
	public void mousePressed (MouseEvent e) {
	}

	@Override
	public void mouseReleased (MouseEvent e) {
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		int tRowIndex;
		int tColIndex;

		tRowIndex = 0;
		tColIndex = 0;
		if (market == NO_MARKET_CELLS) {
			aGraphics.setColor (Color.black);
			aGraphics.drawString ("Empty Market Table", 0, 0);
			aGraphics.setColor (Color.blue);
			aGraphics.drawRect (1, 1, cellWidth, cellHeight);
		} else {
			try {
				for (tRowIndex = 0; tRowIndex < market.length; tRowIndex++) {
					for (tColIndex = 0; tColIndex < market [tRowIndex].length; tColIndex++) {
						if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
							market [tRowIndex] [tColIndex].draw (aGraphics, cellWidth, cellHeight);
						}
					}
				}
			} catch (Exception exc) {
				System.err.println ("Oops... Exception trying to draw the Market, " + tRowIndex + ", " + tColIndex);
			}
		}
	}

	public void redrawMarket () {
		revalidate ();
		repaint ();
	}

	public void setMarketSize () {
		int maxX;
		int maxY;

		maxX = getMaxX ();
		maxY = getMaxY ();
		setPreferredSize (new Dimension (maxX, maxY));
	}

	public void updateAllFrames () {
		gameManager.updateAllFrames ();
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {

	}
}
