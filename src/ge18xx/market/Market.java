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
import ge18xx.toplevel.LoadableXMLI;
import ge18xx.toplevel.MarketFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class Market extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	final static AttributeName AN_COLS = new AttributeName ("cols");
	final static AttributeName AN_ROWS = new AttributeName ("rows");
	final static AttributeName AN_COL = new AttributeName ("col");
	final static AttributeName AN_ROW = new AttributeName ("row");
	final static AttributeName AN_INDEX = new AttributeName ("index");
	final static AttributeName AN_NEIGHBORS = new AttributeName ("neighbors");
	public final static ElementName EN_MARKET = new ElementName ("Market");
	public final static MarketCell [] [] NO_MARKET_CELLS = null;
	public final static Market NO_MARKET = null;

	MarketCell market[][];
	GameManager gameManager;
	int cellHeight, cellWidth;
	int parseRowIndex, parseColIndex;

	public Market (int CH, int CW, GameManager aGameManager) {
		addMouseListener (this);
		addMouseMotionListener (this);
		cellHeight = CH;
		cellWidth = CW;
		gameManager = aGameManager;
	}

	public void CalcCellCenters () {
		int rowIndex, colIndex, Xc, Yc;
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
		int tRowCount, tMaxColCount, tColCount, tRowIndex, tColIndex;
		final ElementName EN_MARKET = new ElementName ("Market");
		final ElementName EN_ROW = new ElementName ("Row");

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
		Integer [] tAllCells;
		Integer [] tAllStartCells;
		int tCountofStartCells;
		int tRowCount, tColCount, tRowIndex, tColIndex, tCellIndex;

		tAllCells = new Integer [10];
		tCountofStartCells = 0;
		tRowCount = getMaxRowCount ();
		if (tRowCount > 0) {
			for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
				tColCount = getColCount (tRowIndex);
				for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
					if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
						if (market [tRowIndex] [tColIndex].isStart ()) {
							tAllCells [tCountofStartCells++] = market [tRowIndex] [tColIndex].getValue ();
						}
					}
				}
			}
		}
		tAllStartCells = new Integer [tCountofStartCells];
		for (tCellIndex = 0; tCellIndex < tCountofStartCells; tCellIndex++) {
			tAllStartCells [tCellIndex] = tAllCells [tCellIndex];
		}

		return tAllStartCells;
	}

	public MarketCell findStartCell (int aParPrice) {
		MarketCell tMarketCell;
		int tRowCount, tColCount, tRowIndex, tColIndex;

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
		MarketCell tMarketCell, tPreviousMarketCell;
		int tRowCount, tColCount, tRowIndex, tColIndex;
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
		if (market == NO_MARKET_CELLS) {
			return 0;
		}

		return market [aRow].length;
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

	public MarketCell getMarketCellAtCoordinates (String aCoordinates) {
		int tRowIndex, tColIndex, tRowCount, tColCount;
		MarketCell tFoundMarketCell = MarketCell.NO_MARKET_CELL;

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
		int tRowIndex, tColIndex, tRowCount, tColCount;
		MarketCell tFoundMarketCell = MarketCell.NO_MARKET_CELL;

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
		int tRowIndex, tColIndex, tRowCount, tColCount;
		MarketCell tFoundMarketCell = MarketCell.NO_MARKET_CELL;

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
		int tRowIndex, tColIndex, tRowCount, tColCount;
		MarketCell tMarketCell = MarketCell.NO_MARKET_CELL;
		Token tToken;

		tRowCount = getMaxRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tMarketCell == MarketCell.NO_MARKET_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount) && (tMarketCell == MarketCell.NO_MARKET_CELL); tColIndex++) {
				if (market [tRowIndex] [tColIndex] != MarketCell.NO_MARKET_CELL) {
					tToken = market [tRowIndex] [tColIndex].findTokenFor (aCompanyAbbrev);
					if (tToken != Token.NO_TOKEN) {
						tMarketCell = market [tRowIndex] [tColIndex];
					}
				}
			}
		}

		return tMarketCell;
	}

	public XMLElement getMarketStateElements (XMLDocument aXMLDocument) {
		XMLElement tMarketElements;
		XMLElement tMarketCellElementTokens;
		int tRowIndex, tColIndex, tRowCount, tColCount;
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
		int retValue, index;

		retValue = getColCount (0);
		if (retValue > 0) {
			for (index = 1; index < market.length; index++) {
				if (market [index].length > retValue) {
					retValue = market [index].length;
				}
			}
		}

		return retValue;
	}

	public int getMaxRowCount () {
		if (market == NO_MARKET_CELLS) {
			return 0;
		}

		return market.length;
	}

	public int getMaxX () {
		int tMaxCol = getMaxColCount ();
		int tMaxX;

		tMaxX = market [0] [tMaxCol - 1].getX () + Double.valueOf (cellWidth / 2).intValue ();

		return tMaxX;
	}

	public int getMaxY () {
		int tMaxRow = getMaxRowCount ();
		int tMaxY;

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
		int tMaxRow, tColCount;

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
			int tRow, tCol;

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
		int tRowIndex = 0;
		int tIndex;
		int tChildIndex;
		boolean tGoodLoad = true;

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
	public void paintComponent (Graphics g) {
		int rowIndex = 0;
		int colIndex = 0;

		if (market == NO_MARKET_CELLS) {
			g.setColor (Color.black);
			g.drawString ("Empty Market Table", 0, 0);
			g.setColor (Color.blue);
			g.drawRect (1, 1, cellWidth, cellHeight);

		} else {
			try {
				for (rowIndex = 0; rowIndex < market.length; rowIndex++) {
					for (colIndex = 0; colIndex < market [rowIndex].length; colIndex++) {
						if (market [rowIndex] [colIndex] != MarketCell.NO_MARKET_CELL) {
							market [rowIndex] [colIndex].draw (g, cellWidth, cellHeight);
						}
					}
				}
			} catch (Exception exc) {
				System.err.println ("Oops... Exception trying to draw the Market, " + rowIndex + ", " + colIndex);
			}
		}
	}

	public void redrawMarket () {
		revalidate ();
		repaint ();
	}

	public void setMarketSize () {
		int maxX, maxY;

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
