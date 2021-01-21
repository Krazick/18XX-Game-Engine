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

public class RotateTileEffect extends LayTileEffect {
	public final static String NAME = "Rotate Tile";
	protected static final AttributeName AN_PREVIOUS_ORIENTATION = new AttributeName ("tilePreviousOrientation");
	int previousOrientation;

	public RotateTileEffect () {
		this (NAME);
	}

	public RotateTileEffect (String aName) {
		super (aName);
	}

	public RotateTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, 
			int aNewOrientation, int aPreviousOrientation, String aTokens, String aBases) {
		super (aActor, aMapCell, aTile, aNewOrientation, aTokens, aBases);
		setName (NAME);
		setPreviousOrientation (aPreviousOrientation);
	}

	public RotateTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		int tPreviousOrientation;

		tPreviousOrientation = aEffectNode.getThisIntAttribute (AN_PREVIOUS_ORIENTATION);
		setPreviousOrientation (tPreviousOrientation);
	}

	private void setPreviousOrientation (int aPreviousOrientation) {
		previousOrientation = aPreviousOrientation;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		int tPossibleRotation;
		GameTile tGameTile;
		Tile tTile;
		MapCell tMapCell;
		HexMap tGameMap;
		TileSet tTileSet;
		int tNewOrientation;
		int tCurrentOrientation;
		
		tEffectApplied = false;
		tTileSet = aRoundManager.getTileSet ();
		tGameMap = aRoundManager.getGameMap ();
		tMapCell = super.getMapCell (tGameMap);
		tGameTile = tTileSet.getGameTile (tileNumber);
		tTile = tGameTile.getTile ();
		// Apply if the Tile Number on the Cell matches
		if (tGameTile.getTileNumber () == tileNumber) {
			tPossibleRotation = tMapCell.getAllAllowedRotations (tTile);
			if (tPossibleRotation != MapCell.NO_ROTATION) {
				tCurrentOrientation = tMapCell.getTileOrient ();
				tMapCell.setTileOrientationLocked (false);
				if (tCurrentOrientation == orientation) {
					System.out.println ("Tile in Correct Orientation of " + orientation);
					tEffectApplied = true;
				} else {
					tMapCell.setTileOrient (orientation);
	//				tGameMap.rotateTileInPlace (tMapCell, HexMap.DONT_ADD_ACTION);
					tNewOrientation = tMapCell.getTileOrient ();
					if (tNewOrientation != orientation) {
						System.err.println ("Tile was supposed to be Rotated to " + orientation + " which is NOT the same as current " + tNewOrientation );
					} else {
						tEffectApplied = true;
					}
				}
				tMapCell.setTileOrientationLocked (true);
				tGameMap.redrawMap ();
			} else {
				System.err.println ("Tile Allowed Rotation is " + tPossibleRotation);
			}
		}
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameTile tGameTile;
		MapCell tMapCell;
		HexMap tGameMap;
		TileSet tTileSet;

		tEffectUndone = false;
		tTileSet = aRoundManager.getTileSet ();
		tGameMap = aRoundManager.getGameMap ();
		tMapCell = super.getMapCell (tGameMap);
		tGameTile = tTileSet.getGameTile (tileNumber);
		// Apply if the Tile Number on the Cell matches
		if (tGameTile.getTileNumber () == tileNumber) {
			tMapCell.setTileOrientation (previousOrientation);
			tGameMap.redrawMap ();
			tEffectUndone = true;
		}
		
		return tEffectUndone;
	}
}
