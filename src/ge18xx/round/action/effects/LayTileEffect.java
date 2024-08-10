package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.toplevel.MapFrame;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class LayTileEffect extends ChangeTileContentEffect {
	public final static String NAME = "Lay Tile";
	final static AttributeName AN_NEW_TILE_TOKENS = new AttributeName ("newTileTokens");
	String newTileTokens;
	
	public LayTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation) {
		this (NAME);
	}

	public LayTileEffect (String aName) {
		super (aName);
	}

	public LayTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation, String aTokens,
			String aBases, String aNewTileTokens) {
		super (aActor, aMapCell, aTile, aOrientation, aTokens, aBases);
		setNewTileTokens (aNewTileTokens);
		setName (NAME);
	}

	public LayTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tNewTileTokens;

		tNewTileTokens = aEffectNode.getThisAttribute (AN_NEW_TILE_TOKENS);
		setNewTileTokens (tNewTileTokens);

		setName (NAME);
	}

	public void setNewTileTokens (String aNewTileTokens) {
		newTileTokens = aNewTileTokens;
	}
	
	public String getNewTileTokens () {
		return newTileTokens;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_NEW_TILE_TOKENS, getNewTileTokens ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tBenefitReport = getBenefitEffectReport ();

		return (REPORT_PREFIX + name + " #" + tileNumber + " with orientation " + orientation + " by "
				+ actor.getName () + " on MapCell " + mapCellID + " Tokens [ " + getTokens () + " ] New Bases [ "
				+ getBases () + " ]" + " New Tile Tokens [" + getNewTileTokens () + "]." + tBenefitReport);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameTile tTile;
		MapCell tMapCell;
		HexMap tGameMap;
		TileSet tTileSet;

		tEffectApplied = false;
		tTileSet = aRoundManager.getTileSet ();
		tGameMap = aRoundManager.getGameMap ();
		tMapCell = getMapCell (tGameMap);
		tTile = tTileSet.getGameTile (tileNumber);
		// Apply if the Tile Number on the Cell matches
		if (tTile.getTileNumber () == tileNumber) {
			tMapCell.putThisTileDown (tTileSet, tTile, orientation);
			applyTokens (aRoundManager, tMapCell, newTileTokens);
			applyBases (aRoundManager, tMapCell);
			tGameMap.redrawMap ();
			tEffectApplied = true;
			tTileSet.clearAllSelected ();
		} else {
			System.err
					.println ("Applying " + name + " by " + actor.getName () + " Fails since Tile Numbers don't match");
			System.err.println ("Effect Tile # " + tileNumber + " Map Cell Tile # " + tTile.getTileNumber ());
		}

		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Tile tTile;
		MapCell tMapCell;
		MapFrame tMapFrame;
		HexMap tGameMap;
		TileSet tTileSet;

		tEffectUndone = false;
		tTileSet = getTileSet (aRoundManager);
		tGameMap = getMap (aRoundManager);
		tMapFrame = getMapFrame (aRoundManager);
		tMapCell = getMapCell (tGameMap);
		tTile = tMapCell.getTile ();
		// Undo if the Tile Number on the Cell matches
		// OR if the tileNumber was 0, which means the tile was Upgraded,
		// and the RemoveTileEffect will restore that previous tile
		if (tTile != Tile.NO_TILE) {
			if ((tTile.getNumber () == tileNumber) || (tileNumber == 0)) {
				tTile.returnTokens ();
				tMapCell.removeTile ();
				tMapCell.restoreTile (tTileSet, tTile);
				tGameMap.setPlayableTiles (tMapCell);
				tMapFrame.resetAllModes ();
				tGameMap.redrawMap ();
				tEffectUndone = true;
			} else {
				System.err
						.println ("Undo " + name + " by " + actor.getName () + " Fails since Tile Numbers don't match");
				System.err.println ("Effect Tile # " + tileNumber + " Map Cell Tile # " + tTile.getNumber ());
			}
		}

		return tEffectUndone;
	}
}
