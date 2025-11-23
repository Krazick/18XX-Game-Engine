package ge18xx.map;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JLabel;

import org.w3c.dom.NodeList;

import ge18xx.company.Corporation;
import ge18xx.company.TokenCompany;
import geUtilities.xml.AttributeName;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLNode;

public class GameMap extends JLabel implements Cloneable,LoadableXMLI, MouseListener, 
								MouseMotionListener {

	private static final long serialVersionUID = 1L;

	public static final AttributeName AN_INDEX = new AttributeName ("index");
	public static final AttributeName AN_START_COL = new AttributeName ("startCol");
	
	protected MapCell mapCells[][];
	public Hex18XX hex;

	public GameMap () {
	}

	@Override
	public void mouseDragged (MouseEvent e) {
	}

	@Override
	public void mouseMoved (MouseEvent e) {
	}

	@Override
	public void mouseClicked (MouseEvent e) {
	}

	@Override
	public void mousePressed (MouseEvent e) {
	}

	@Override
	public void mouseReleased (MouseEvent e) {
	}

	@Override
	public void mouseEntered (MouseEvent e) {
	}

	@Override
	public void mouseExited (MouseEvent e) {
	}

	@Override
	public String getTypeName () {
		return null;
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {

	}

	public void buildMapArray (int aCols, int aRows) {
		mapCells = new MapCell [aRows] [aCols];
	}

	public void setMapCell (int aRow, int aCol, String aDirection, GameMap hexMap) {
		MapCell tMapCell;
		
		tMapCell = new MapCell (this, aDirection);
		tMapCell.setOffsetCoordinates (aCol, aRow);
		setMapCell (aRow, aCol, tMapCell);
	}
	
	public void setMapCell (int aRow, int aCol, String aDirection) {
		MapCell tMapCell;
		
		tMapCell = new MapCell (this, aDirection);
		tMapCell.setOffsetCoordinates (aCol, aRow);
		setMapCell (aRow, aCol, tMapCell);
	}

	public void setMapCell (int aRow, int aCol, MapCell aMapCell) {
		mapCells [aRow] [aCol] = aMapCell;
	}

	public int getColCount (int thisRow) {
		int tColCount;
		
		if (mapCells == MapCell.NO_MAP_CELLS) {
			tColCount = 0;
		} else {
			tColCount = mapCells [thisRow].length;
		}
		
		return tColCount;
	}

	public int getRowCount () {
		int tRowCount;
		
		if (mapCells == MapCell.NO_MAP_CELLS) {
			tRowCount = 0;
		} else {
			tRowCount = mapCells.length;
		}
	
		return tRowCount;
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		
		tRowCount = getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				mapCells [tRowIndex] [tColIndex].paintComponent (aGraphics, hex);
			}
		}
	}
	
	public void redrawMap () {
		revalidate ();
		repaint ();
	}

	public int getHexWidth () {
		return (Hex.getWidth ());
	}

	public int getHexHeight () {
		return (hex.getYd () * 2);
	}

	public int getHexYd () {
		return (hex.getYd ());
	}

	public void CalcGridCenters (GameMap hexMap) {
		int rowIndex;
		int colIndex;
		int Xc;
		int Yc;
		int toggle;
		int temp_2DLR;
		int temp_DUP_dwidth;
		int rowCount;
		int colCount;
	
		if (Hex.getDirection ()) {
			temp_2DLR = hex.getDisplaceLeftRight () + hex.getDisplaceLeftRight ();
			temp_DUP_dwidth = hex.getDisplaceUpDown () + Hex.getWidth ();
			rowCount = getRowCount ();
	
			Yc = 0 - temp_DUP_dwidth + hex.getIntDWidth ();
			if (Double.valueOf (rowCount / 2).intValue () * 2 == rowCount) {
				toggle = 0;
			} else {
				toggle = 1;
			}
			for (rowIndex = rowCount - 1; rowIndex >= 0; rowIndex--) {
				if (toggle == 1) {
					Xc = hex.getDisplaceLeftRight () - temp_2DLR;
				} else {
					Xc = 0;
				}
				Yc += temp_DUP_dwidth;
				colCount = getColCount (rowIndex);
				for (colIndex = 0; colIndex < colCount; colIndex++) {
					Xc += temp_2DLR;
					if (mapCells [rowIndex] [colIndex] == MapCell.NO_MAP_CELL) {
						mapCells [rowIndex] [colIndex] = new MapCell (Xc, Yc, this);
					} else {
						mapCells [rowIndex] [colIndex].setXY (Xc, Yc);
					}
				}
				toggle = 1 - toggle;
			}
		} else {
			Xc = 0 - hex.getDisplaceUpDown ();
			toggle = 1;
			temp_2DLR = hex.getDisplaceLeftRight () + hex.getDisplaceLeftRight ();
			temp_DUP_dwidth = hex.getDisplaceUpDown () + Hex.getWidth ();
			rowCount = getRowCount ();
	
			for (rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Yc = 0 - hex.getDisplaceLeftRight () * toggle;
				Xc += temp_DUP_dwidth;
				colCount = getColCount (rowIndex);
				for (colIndex = 0; colIndex < colCount; colIndex++) {
					Yc += temp_2DLR;
					if (mapCells [rowIndex] [colIndex] == MapCell.NO_MAP_CELL) {
						mapCells [rowIndex] [colIndex] = new MapCell (Xc, Yc, this);
					} else {
						mapCells [rowIndex] [colIndex].setXY (Xc, Yc);
					}
				}
				toggle = 1 - toggle;
			}
	
		}
	}
	
	@Override
	public GameMap clone () throws CloneNotSupportedException {
		int tColCount;
		int tRowCount;
		int tRowIndex;
		int tColIndex;
		int tMaxColCount;
		GameMap tGameMapClone = (GameMap) super.clone ();
		
		tRowCount = getRowCount ();
		tMaxColCount = this.getMaxColCount ();
		tGameMapClone.buildMapArray (tMaxColCount, tRowCount);
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tGameMapClone.mapCells [tRowIndex] [tColIndex] = mapCells [tRowIndex] [tColIndex];
			}
		}
		
		return tGameMapClone;
	}
	
	// Methods to be Overridden by HexMap

	public void toggleSelectedMapCell (MapCell aSelectedMapCell) {

	}
	
	public TokenCompany getTokenCompany (String aAbbrev) {
		return TokenCompany.NO_TOKEN_COMPANY;
	}

	public int getCurrentPhase () {
		return 0;
	}

	public Corporation getOperatingCompany () {
		return Corporation.NO_CORPORATION;
	}
	
	public boolean isTileAvailableForMapCell (MapCell aMapCell) {
		return false;
	}
	
	public Corporation getCorporationByID (int aCorporationID) {
		return Corporation.NO_CORPORATION;
	}

	public Corporation getCorporation (String aCorporationAbbrev) {
		return Corporation.NO_CORPORATION;
	}

	public boolean mapCellIsInSelectableSMC (MapCell mapCell) {
		return false;
	}

	// Methods pulled up from HexMap
	
	public boolean inColRange (int aRow, int aCol) {
		boolean tInColRange;
		
		tInColRange = false;
		if (inRowRange (aRow)) {
			tInColRange = ((aCol >= 0) && (aCol < getColCount (aRow)));
		}
		
		return tInColRange;
	}

	public boolean inRowRange (int aRow) {
		boolean tInRowRange;
		
		tInRowRange = ((aRow >= 0) && (aRow < getRowCount ()));
		
		return tInRowRange;
	}

	public boolean inRowColRanges (int aRow, int aCol) {
		boolean tInRowColRanges;
	
		tInRowColRanges = false;
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				tInRowColRanges = true;
			}
		}
	
		return tInRowColRanges;
	}

	public boolean isTileOnCell (int aRow, int aCol) {
		boolean tileFound;
	
		tileFound = false;
		if (inRowColRanges (aRow, aCol)) {
			tileFound = mapCells [aRow] [aCol].isTileOnCell ();
		}
	
		return tileFound;
	}

	protected boolean loadXMLRow (XMLNode aRowNode, int aTerrainCost[], int aTerrainType[], int aCols, int aDefaultTerrainType, String [] theRowIDs,
								String [] theColIDs) throws IOException {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tID;
		int tChildrenCount;
		int tRowIndex;
		int tColIndex;
		int index;
		int tOddRow;
		boolean evenRow;
		boolean tGoodLoad;
		MapCell tMapCell;
	
		tGoodLoad = true;
		tRowIndex = aRowNode.getThisIntAttribute (AN_INDEX, 0);
		tChildren = aRowNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tColIndex = aRowNode.getThisIntAttribute (AN_START_COL, 0);
		if (tColIndex != 0) {
			for (index = 0; index < tColIndex; index++) {
				mapCells [tRowIndex] [index].setEmptyMapCell (aDefaultTerrainType);
			}
		}
		if ((tRowIndex / 2) * 2 == tRowIndex) {
			evenRow = true;
			tOddRow = 0;
		} else {
			evenRow = false;
			tOddRow = 1;
		}
	
		for (index = 0; (index < tChildrenCount) && tGoodLoad; index++) {
			tChildNode = new XMLNode (tChildren.item (index));
			tChildName = tChildNode.getNodeName ();
			if (MapCell.EN_MAP_CELL.equals (tChildName)) {
				if (tColIndex < aCols) {
					if (mapCells [tRowIndex] [tColIndex].getMapDirection ()) {
						tID = theRowIDs [tRowIndex] + theColIDs [tColIndex * 2 + tOddRow];
					} else {
						tID = theRowIDs [tRowIndex] + theColIDs [tColIndex * 2 + tOddRow];
					}
					mapCells [tRowIndex] [tColIndex].loadXMLCell (tChildNode, aTerrainCost, aTerrainType, tID);
					tMapCell = mapCells [tRowIndex] [tColIndex];
					tMapCell.setOffsetCoordinates (tColIndex, tRowIndex);
					tColIndex++;
				} else {
					tGoodLoad = false;
				}
			}
		}
		if (aCols > tColIndex) {
			for (index = tColIndex; index < aCols; index++) {
				mapCells [tRowIndex] [index].setEmptyMapCell (aDefaultTerrainType);
			}
		}
	
		for (index = 0; index < aCols; index++) {
			if (index > 0) {
				mapCells [tRowIndex] [index].setNeighbor (0, mapCells [tRowIndex] [index - 1]);
			}
			if (tRowIndex > 0) {
				if (evenRow) {
					mapCells [tRowIndex] [index].setNeighbor (4, mapCells [tRowIndex - 1] [index]);
					if (index > 0) {
						mapCells [tRowIndex] [index].setNeighbor (5, mapCells [tRowIndex - 1] [index - 1]);
					}
				} else {
					mapCells [tRowIndex] [index].setNeighbor (5, mapCells [tRowIndex - 1] [index]);
					if ((index + 1) < aCols) {
						mapCells [tRowIndex] [index].setNeighbor (4, mapCells [tRowIndex - 1] [index + 1]);
					}
				}
			}
		}
	
		if (!tGoodLoad) {
			System.err.println ("Bad Load on Row [" + tRowIndex + "].");
		}
	
		return tGoodLoad;
	}

	public int getMaxColCount () {
		int tIndex;
		int tMaxColCount;
		int tColCount;
		int tRowCount;
	
		tMaxColCount = getColCount (0);
		if (tMaxColCount > 0) {
			tRowCount = getRowCount ();
			for (tIndex = 1; tIndex < tRowCount; tIndex++) {
				tColCount = getColCount (tIndex);
				if (tColCount > tMaxColCount) {
					tMaxColCount = tColCount;
				}
			}
		}
	
		return tMaxColCount;
	}

	public int getMaxRowCount () {
		return (getRowCount ());
	}
}
