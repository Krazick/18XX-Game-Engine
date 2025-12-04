package ge18xx.round.plan.condition;

import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;

public class SpecifiedTileOnMapCell extends NoTileOnMapCell {
	public static final String NAME = "Specified Tile on MapCell";
	int tileNumber;
	int tileOrientation;
	
	public SpecifiedTileOnMapCell (MapCell aMapCell, int aTileNumber,int aTileOrientation) {
		super (NAME, aMapCell);
		setTileNumber (aTileNumber);
		setTileOrientation (aTileOrientation);
	}

	public void setTileOrientation (int aTileOrientation) {
		tileOrientation = aTileOrientation;
	}
	
	public int getTileOrientation () {
		return tileOrientation;
	}

	public void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}

	public int getTileNumber () {
		return tileNumber;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		Tile tTile;
		
		if (super.meets ()) {
			tMeets = FAILS;
		} else {
			tTile = mapCell.getTile ();
			if (tTile.getNumber () == tileNumber) {
				if (mapCell.getTileOrient () == tileOrientation) {
					tMeets = MEETS;
				} else {
					tMeets = FAILS;
				}
			} else {
				tMeets = FAILS;
			}
		}
		
		return tMeets;
	}
}
