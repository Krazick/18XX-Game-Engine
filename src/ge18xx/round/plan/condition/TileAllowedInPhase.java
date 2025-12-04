package ge18xx.round.plan.condition;

import ge18xx.tiles.GameTile;
import ge18xx.toplevel.MapFrame;

public class TileAllowedInPhase extends Condition {
	public static final String NAME = "Tile allowed during Applying Phase";
	MapFrame liveMapFrame;
	GameTile currentGameTile;
	GameTile newGameTile;
	
	public TileAllowedInPhase (GameTile aNewGameTile, GameTile aCurrentGameTile, MapFrame aLiveMapFrame) {
		super (NAME);
		setNewGameTile (aNewGameTile);
		setCurrentGameTile (aCurrentGameTile);
		setLiveMapFrame (aLiveMapFrame);
	}

	public void setNewGameTile (GameTile aNewGameTile) {
		newGameTile = aNewGameTile;
	}

	public GameTile getNewGameTile () {
		return newGameTile;
	}

	public void setCurrentGameTile (GameTile aCurrentGameTile) {
		currentGameTile = aCurrentGameTile;
	}

	public GameTile getCurrentGameTile () {
		return currentGameTile;
	}
	
	public void setLiveMapFrame (MapFrame aLiveMapFrame) {
		liveMapFrame = aLiveMapFrame;
	}
	
	public MapFrame getLiveMapFrame () {
		return liveMapFrame;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		
		if (liveMapFrame == MapFrame.NO_XML_FRAME) {
			tMeets = FAILS;
		} else if (newGameTile == GameTile.NO_GAME_TILE){
			tMeets = FAILS;
		} else if (currentGameTile == GameTile.NO_GAME_TILE){
			tMeets = FAILS;
		} else {
			if (liveMapFrame.isUpgradeAllowed (newGameTile, currentGameTile)) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
			}
		}
		
		return tMeets;
	}
}
