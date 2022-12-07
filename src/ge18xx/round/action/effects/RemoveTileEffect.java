package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class RemoveTileEffect extends ChangeTileContentEffect {
	public static final String NAME = "Remove Tile";
	final static AttributeName AN_PREVIOUS_TOKENS = new AttributeName ("previousTokens");
	final static AttributeName AN_PREVIOUS_BASES = new AttributeName ("previousBases");

	public RemoveTileEffect () {
		super ();
		setName (NAME);
	}

	public RemoveTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation, String aPreviousTokens,
			String aPreviousBases) {
		super (aActor, aMapCell, aTile, aOrientation, aPreviousTokens, aPreviousBases);
		setName (NAME);
	}

	public RemoveTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " #" + tileNumber + " with orientation " + orientation + " by "
				+ actor.getName () + " on MapCell " + mapCellID + " Old Tokens [ " + getTokens () + " ] Old Bases [ "
				+ getBases () + " ].");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Tile tTile;
		MapCell tMapCell;
		HexMap tGameMap;
		TileSet tTileSet;

		tEffectApplied = false;
		tGameMap = getMap (aRoundManager);
		tTileSet = getTileSet (aRoundManager);
		tMapCell = getMapCell (tGameMap);
		tTile = tMapCell.getTile ();
		tTile.returnTokens ();

		tMapCell.removeTile ();
		tMapCell.restoreTile (tTileSet, tTile);
		tGameMap.redrawMap ();
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Tile tTile;
		MapCell tMapCell;
		HexMap tGameMap;
		TileSet tTileSet;
		GameTile tGameTile;
		int tTileNumber;

		tEffectUndone = false;
		tGameMap = getMap (aRoundManager);
		tTileSet = getTileSet (aRoundManager);
		tTileNumber = getTileNumber ();
		tTile = tTileSet.popTile (tTileNumber);
		tGameTile = tTileSet.getGameTile (tTileNumber);
		tGameTile.clearPlayable ();
		tMapCell = getMapCell (tGameMap);
		tMapCell.putTile (tTile, orientation);
		tMapCell.lockTileOrientation ();
		applyBases (aRoundManager, tMapCell);
		applyTokens (aRoundManager, tMapCell);
		tGameMap.redrawMap ();
		tEffectUndone = true;

		return tEffectUndone;
	}
}