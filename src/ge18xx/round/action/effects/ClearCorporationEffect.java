package ge18xx.round.action.effects;

import ge18xx.company.TokenCompany;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.utilities.XMLNode;

public class ClearCorporationEffect extends ChangeTileEffect {
	public final static String NAME = "Clear Corporation";
//	int tileNumber;
	int revenueCenterIndex;

	public ClearCorporationEffect () {
		super ();
		setName (NAME);
	}

	public ClearCorporationEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex) {
		super (aActor, aMapCell, aTile);
		
//		setTileNumber (aTile);
		setRevenueCenterIndex (aRevenueCenterIndex);
		setName (NAME);
	}

	public ClearCorporationEffect  (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
//		int tTileNumber;
		int tRevenueCenterIndex;
//		Tile tTile;

		tRevenueCenterIndex = aEffectNode.getThisIntAttribute (MapCell.AN_REVENUE_CENTER_INDEX);
//		tTileNumber = aEffectNode.getThisIntAttribute (Tile.AN_TILE_NUMBER);
//		tTile = aGameManager.getTile (tTileNumber);
//		setTileNumber (tTile);
		setRevenueCenterIndex (tRevenueCenterIndex);
	}
//
//	public void setTileNumber (Tile aTile) {
//		tileNumber = aTile.getNumber ();
//	}

	public void setRevenueCenterIndex (int aRevenuCenterIndex) {
		revenueCenterIndex = aRevenuCenterIndex;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TokenCompany tTokenCompany;
		Tile tTile;
		GameManager tGameManager;
		
		tEffectApplied = false;
		System.out.println ("Ready to Apply " + name);
		if (actor.isATokenCompany ()) {
			tTokenCompany = (TokenCompany) actor;
			tGameManager = aRoundManager.getGameManager ();
			tTile = tGameManager.getTile (tileNumber);
			tEffectApplied = tTile.clearCorporation (tTokenCompany);
		}
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = false;
//		tEffectUndone = layToken (aRoundManager, tActionVerb);
		
		return tEffectUndone;
	}
}
