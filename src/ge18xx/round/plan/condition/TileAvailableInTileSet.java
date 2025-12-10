package ge18xx.round.plan.condition;

import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import geUtilities.GUI;

public class TileAvailableInTileSet extends Condition {
	public static final String NAME = "Specified Tile in TileSet";
	int tileNumber;
	TileSet liveTileSet;
	
	public TileAvailableInTileSet (int aTileNumber, TileSet aLiveTileSet) {
		super (NAME);
		setTileNumber (aTileNumber);
		setLiveTileSet (aLiveTileSet);
	}

	public void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}

	public int getTileNumber () {
		return tileNumber;
	}
	
	public void setLiveTileSet (TileSet aLiveTileSet) {
		liveTileSet = aLiveTileSet;
	}

	public TileSet getLiveTileSet () {
		return liveTileSet;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		int tAvailableCount;
		String tFailsReason;
		GameTile tGameTile;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (liveTileSet == TileSet.NO_TILE_SET) {
			tMeets = FAILS;
			tFailsReason = "TileSet is NOT set";
		} else if (tileNumber == Tile.NOT_A_TILE){
			tMeets = FAILS;
			tFailsReason = "Tile Number is not set";
		} else {
			tGameTile = liveTileSet.getGameTile (tileNumber);
			tAvailableCount = tGameTile.getAvailableCount ();
			if (tAvailableCount > 0) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
				tFailsReason = "There is no Tile with number " + tileNumber + " Left in the TileSet";
			}
		}
		setFailsReason (tFailsReason);
		
		return tMeets;
	}
	

	@Override
	public String getReport () {
		String tReport;
		
		tReport = super.getReport () + " (" + tileNumber + ")";
		tReport = appendStatus (tReport);
		
		return tReport;
	}
}
