package ge18xx.round.plan.condition;

import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;

public class TileAvailableInTileSet extends Condition {
	public static final String NAME = "Specified Tile on TileSet";
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
		GameTile tGameTile;
		
		if (liveTileSet == TileSet.NO_TILE_SET) {
			tMeets = FAILS;
		} else if (tileNumber == Tile.NOT_A_TILE){
			tMeets = FAILS;
		} else {
			tGameTile = liveTileSet.getGameTile (tileNumber);
			tAvailableCount = tGameTile.getAvailableCount ();
			if (tAvailableCount > 0) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
			}
		}
		
		return tMeets;
	}
}
