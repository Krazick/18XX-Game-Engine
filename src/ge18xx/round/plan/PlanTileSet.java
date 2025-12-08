package ge18xx.round.plan;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import ge18xx.map.GameMap;
import ge18xx.map.Hex;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.TileSet;

public class PlanTileSet extends TileSet {
	private static final long serialVersionUID = 1L;
	public static final int PLAN_TILES_PER_ROW = 2;
	PlanFrame planFrame;

	public PlanTileSet (String aSetName, PlanFrame aPlanFrame) {
		super (aSetName);
		
		boolean tHexDirection;
		
		setShowAllTiles (true);
		tHexDirection = Hex.getDirection ();
		setHex (tHexDirection);
		setPlanFrame (aPlanFrame);
	}

	public void setPlanFrame (PlanFrame aPlanFrame) {
		planFrame = aPlanFrame;
	}
	
	@Override
	public int getTilesPerRow () {
		return PLAN_TILES_PER_ROW;
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		super.paintComponent (aGraphics);
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension (300, 500); // Example size
    }
	
	@Override
	public void setTraySize () {
		Dimension tNewDimension;
		int tMaxX;
		int tMaxY;
//		int tRowCount;

//		if (hex == Hex18XX.NO_HEX18XX) {
//			setHex (Hex18XX.getDirection ());
//		}
//		tRowCount = calcRowCount ();
//		tMaxX = Double.valueOf (Hex18XX.getWidth () * 2.25 * getTilesPerRow () + 10).intValue ();
//		tMaxY = (hex.getYd () * 2 + 25) * tRowCount + 20;
		
		tMaxX = 200;
		tMaxY = 700;
		tNewDimension = new Dimension (tMaxX, tMaxY);
		setPreferredSize (tNewDimension);
	}

	public void setTraySize (GameMap planningMap, PlaceMapTilePlan tPlaceMapTilePlan) {
		int tMaxX;
		int tMaxY;
		int tCount;
		Dimension tNewDimension;

		tCount = tPlaceMapTilePlan.playableTilesCount ();
//		tMaxX = Double.valueOf (Hex18XX.getWidth () * 2.25 + 10).intValue ();
//		tMaxY = (hex.getYd () * 2 + 25) * tCount + 20;
		
		tMaxX = planningMap.getMaxX ();
		tMaxY = (planningMap.getHexHeight () + 20) * tCount;
		tNewDimension = new Dimension (tMaxX, tMaxY);
		setSize (tNewDimension);

		setPreferredSize (tNewDimension);
	}

	@Override
	public void handleClick (MouseEvent aMouseEvent) {
		Point tPoint;
		GameTile tGameTile;

		if (! planFrame.tileIsPlaced ()) {
			tPoint = aMouseEvent.getPoint ();
			tGameTile = getTileContainingPoint (tPoint);
	
			if (tGameTile != GameTile.NO_GAME_TILE) {
				System.out.println ("Clicked Game Tile " + tGameTile.getTileNumber ());
				switchSelectedTile (tGameTile);
				planFrame.updatePutdownTileButton ();
				planFrame.updatePickupTileButton ();
				planFrame.repaint ();
			} else {
				super.handleClick (aMouseEvent);
			}
		}
	}
}
