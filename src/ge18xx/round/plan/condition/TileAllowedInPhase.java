package ge18xx.round.plan.condition;

import ge18xx.tiles.GameTile;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;

public class TileAllowedInPhase extends Condition {
	public static final String NAME = "Tile allowed in Phase";
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
		String tFailsReason;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (liveMapFrame == MapFrame.NO_XML_FRAME) {
			tMeets = FAILS;
			tFailsReason = "Live Map Frame is not set yet";
		} else if (newGameTile == GameTile.NO_GAME_TILE){
			tMeets = FAILS;
			tFailsReason = "No GameTile has been set in the Plan";
		} else if (currentGameTile == GameTile.NO_GAME_TILE) {
			tMeets = FAILS;
			tFailsReason = "Current GameTile has not been set in the Plan";
		} else {
			if (liveMapFrame.isUpgradeAllowed (newGameTile, currentGameTile)) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
				tFailsReason = "Upgrade to a " + newGameTile.getTileColor () + 
							" not allowing to be used in current Phase";
			}
		}
		setFailsReason (tFailsReason);
		
		return tMeets;
	}
}
