package ge18xx.map;

import java.awt.Graphics;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.NodeList;

import ge18xx.company.Corporation;
import ge18xx.company.TokenCompany;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;
import genericMap.Cell;
import genericMap.Map;

//public class GameMap extends JLabel implements Cloneable, LoadableXMLI {
public class GameMap extends Map {
	private static final long serialVersionUID = 1L;
	public static final AttributeName AN_INDEX = new AttributeName ("index");
	public static final AttributeName AN_START_COL = new AttributeName ("startCol");
	public Hex18XX hex;

	public GameMap () {
	}

	public void setMapCell (int aRow, int aCol, String aDirection) {
		MapCell tMapCell;
		
		tMapCell = new MapCell (this, aDirection);
		tMapCell.setOffsetCoordinates (aCol, aRow);
		setCell (aRow, aCol, tMapCell);
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		MapCell tMapCell;
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		
		tRowCount = getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tMapCell = (MapCell) cells [tRowIndex] [tColIndex];
				tMapCell.paintComponent (aGraphics, hex);
			}
		}
	}
	
	public int getHexWidth () {
		return (hex.getHexWidth ());
	}

	public int getHexHeight () {
		return (hex.getYd () * 2);
	}

	public int getHexYd () {
		return (hex.getYd ());
	}

	public void CalcGridCenters () {
		int tRowIndex;
		int tColIndex;
		int Xc;
		int Yc;
		int toggle;
		int temp_2DLR;
		int temp_DUP_dwidth;
		int rowCount;
		int colCount;
	
		if (hex.getHexDirection ()) {
			temp_2DLR = hex.getDisplaceLeftRight () + hex.getDisplaceLeftRight ();
			temp_DUP_dwidth = hex.getDisplaceUpDown () + hex.getHexWidth ();
			rowCount = getRowCount ();
	
			Yc = 0 - temp_DUP_dwidth + hex.getIntDWidth ();
			if (Double.valueOf (rowCount / 2).intValue () * 2 == rowCount) {
				toggle = 0;
			} else {
				toggle = 1;
			}
			for (tRowIndex = rowCount - 1; tRowIndex >= 0; tRowIndex--) {
				if (toggle == 1) {
					Xc = hex.getDisplaceLeftRight () - temp_2DLR;
				} else {
					Xc = 0;
				}
				Yc += temp_DUP_dwidth;
				colCount = getColCount (tRowIndex);
				for (tColIndex = 0; tColIndex < colCount; tColIndex++) {
					Xc += temp_2DLR;
					if (cells [tRowIndex] [tColIndex] == Cell.NO_CELL) {
						cells [tRowIndex] [tColIndex] = new MapCell (Xc, Yc, this);
					} else {
						cells [tRowIndex] [tColIndex].setXY (Xc, Yc);
					}
				}
				toggle = 1 - toggle;
			}
		} else {
			Xc = 0 - hex.getDisplaceUpDown ();
			toggle = 1;
			temp_2DLR = hex.getDisplaceLeftRight () + hex.getDisplaceLeftRight ();
			temp_DUP_dwidth = hex.getDisplaceUpDown () + hex.getHexWidth ();
			rowCount = getRowCount ();
	
			for (tRowIndex = 0; tRowIndex < rowCount; tRowIndex++) {
				Yc = 0 - hex.getDisplaceLeftRight () * toggle;
				Xc += temp_DUP_dwidth;
				colCount = getColCount (tRowIndex);
				for (tColIndex = 0; tColIndex < colCount; tColIndex++) {
					Yc += temp_2DLR;
					if (cells [tRowIndex] [tColIndex] == Cell.NO_CELL) {
						cells [tRowIndex] [tColIndex] = new MapCell (Xc, Yc, this);
					} else {
						cells [tRowIndex] [tColIndex].setXY (Xc, Yc);
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
		GameMap tGameMapClone;
		
		tGameMapClone = (GameMap) super.clone ();
		tRowCount = getRowCount ();
		tMaxColCount = getMaxColCount ();
		tGameMapClone.buildMapArray (tMaxColCount, tRowCount);
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				if (cells [tRowIndex] [tColIndex].isSelected ()) {
					tGameMapClone.cells [tRowIndex] [tColIndex] = cells [tRowIndex] [tColIndex].clone ();
				} else {
					tGameMapClone.cells [tRowIndex] [tColIndex] = cells [tRowIndex] [tColIndex];
				}
			}
		}
		
		return tGameMapClone;
	}
	
	// Methods to be Overridden by HexMap

	public void toggleSelectedMapCell (MapCell aSelectedMapCell) {

	}
	
	public int getCurrentPhase () {
		return 0;
	}

	public TokenCompany getTokenCompany (String aAbbrev) {
		return TokenCompany.NO_TOKEN_COMPANY;
	}

	public Corporation getOperatingCompany () {
		return Corporation.NO_CORPORATION;
	}
	
	public Corporation getCorporationByID (int aCorporationID) {
		return Corporation.NO_CORPORATION;
	}

	public Corporation getCorporation (String aCorporationAbbrev) {
		return Corporation.NO_CORPORATION;
	}
	
	public boolean isTileAvailableForMapCell (MapCell aMapCell) {
		return false;
	}

	public boolean mapCellIsInSelectableSMC (MapCell mapCell) {
		return false;
	}

	// Methods pulled up from HexMap
	
	public boolean isTileOnCell (int aRow, int aCol) {
		boolean tTileFound;
		MapCell tMapCell;
	
		tTileFound = false;
		if (inRowColRanges (aRow, aCol)) {
			tMapCell = (MapCell) cells [aRow] [aCol];
			tTileFound = tMapCell.isTileOnCell ();
		}
	
		return tTileFound;
	}

	protected boolean loadXMLRow (XMLNode aRowNode, int aTerrainCost[], int aTerrainType[], 
								int aCols, int aDefaultTerrainType, String [] theRowIDs,
								String [] theColIDs) throws IOException {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tID;
		int tChildrenCount;
		int tRowIndex;
		int tColIndex;
		int tIndex;
		int tOddRow;
		boolean tEvenRow;
		boolean tGoodLoad;
		MapCell tMapCell;
		Cell tCell;
	
		tGoodLoad = true;
		tRowIndex = aRowNode.getThisIntAttribute (AN_INDEX, 0);
		tChildren = aRowNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tColIndex = aRowNode.getThisIntAttribute (AN_START_COL, 0);
		if (tColIndex != 0) {
			for (tIndex = 0; tIndex < tColIndex; tIndex++) {
				tMapCell = (MapCell) cells [tRowIndex] [tIndex];
				tMapCell.setEmptyMapCell (aDefaultTerrainType);
			}
		}
		if ((tRowIndex / 2) * 2 == tRowIndex) {
			tEvenRow = true;
			tOddRow = 0;
		} else {
			tEvenRow = false;
			tOddRow = 1;
		}
	
		for (tIndex = 0; (tIndex < tChildrenCount) && tGoodLoad; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (Cell.EN_MAP_CELL.equals (tChildName)) {
				if (tColIndex < aCols) {
					if (cells [tRowIndex] [tColIndex].getMapDirection ()) {
						tID = theRowIDs [tRowIndex] + theColIDs [tColIndex * 2 + tOddRow];
					} else {
						tID = theRowIDs [tRowIndex] + theColIDs [tColIndex * 2 + tOddRow];
					}
					tMapCell = (MapCell) cells [tRowIndex] [tColIndex];
					tMapCell.loadXMLCell (tChildNode, aTerrainCost, aTerrainType, tID);
					tMapCell.setOffsetCoordinates (tColIndex, tRowIndex);
					tColIndex++;
				} else {
					tGoodLoad = false;
				}
			}
		}
		if (aCols > tColIndex) {
			for (tIndex = tColIndex; tIndex < aCols; tIndex++) {
				tMapCell = (MapCell) cells [tRowIndex] [tIndex];
				tMapCell.setEmptyMapCell (aDefaultTerrainType);
			}
		}
	
		for (tIndex = 0; tIndex < aCols; tIndex++) {
			tCell = cells [tRowIndex] [tIndex];
			if (tIndex > 0) {
				tCell.setNeighbor (0, cells [tRowIndex] [tIndex - 1], tCell);
			}
			if (tRowIndex > 0) {
				if (tEvenRow) {
					tCell.setNeighbor (4, cells [tRowIndex - 1] [tIndex], tCell);
					if (tIndex > 0) {
						tCell.setNeighbor (5, cells [tRowIndex - 1] [tIndex - 1], tCell);
					}
				} else {
					tCell.setNeighbor (5, cells [tRowIndex - 1] [tIndex], tCell);
					if ((tIndex + 1) < aCols) {
						tCell.setNeighbor (4, cells [tRowIndex - 1] [tIndex + 1], tCell);
					}
				}
			}
		}
	
		if (!tGoodLoad) {
			System.err.println ("Bad Load on Row [" + tRowIndex + "].");
		}
	
		return tGoodLoad;
	}

	public void setPlayableTiles (MapCell mapCell) {
		
	}
	
	public void setPlayableTiles (MapCell mapCell, boolean aEnforcePhase) {
		
	}
	
	public void clearPlayableTiles () {
		
	}

	public List<GameTile> getPlayableGameTiles () {
		return null;
	}

	public int getMaxX () {
		int tHexRightEdgeDisplacement;
		int tMaxX;
		
		tHexRightEdgeDisplacement = hex.rightEdgeDisplacement ();
		tMaxX = getMaxX (tHexRightEdgeDisplacement);
		
		return tMaxX;
	}
	
	public int getMaxY () {
		int tHexBottomEdgeDisplacement;
		int tMaxY;
		
		tHexBottomEdgeDisplacement = hex.bottomEdgeDisplacement ();
		tMaxY = getMaxY (tHexBottomEdgeDisplacement);
		
		return tMaxY;
	}
	
	public MapCell getSelectedMapCell () {
		int rowIndex;
		int colIndex;
		int rowCount;
		int colCount;
		MapCell tFoundMapCell;
	
		rowCount = getRowCount ();
		tFoundMapCell = (MapCell) Cell.NO_MAP_CELL;
		for (rowIndex = 0; (rowIndex < rowCount) && (tFoundMapCell == Cell.NO_CELL); rowIndex++) {
			colCount = getColCount (rowIndex);
			for (colIndex = 0; (colIndex < colCount) && (tFoundMapCell == Cell.NO_CELL); colIndex++) {
				if (cells [rowIndex] [colIndex].isSelected ()) {
					tFoundMapCell = (MapCell) cells [rowIndex] [colIndex];
				}
			}
		}
	
		return tFoundMapCell;
	}

	public void rotateTileInPlace (MapCell aThisMapCell, int aPossible, boolean aShiftDown, Tile aTile) {
		
	}
}
