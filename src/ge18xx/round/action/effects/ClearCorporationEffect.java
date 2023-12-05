package ge18xx.round.action.effects;

import ge18xx.center.RevenueCenter;
import ge18xx.company.TokenCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import geUtilities.XMLNode;

public class ClearCorporationEffect extends ChangeTileEffect {
	public final static String NAME = "Clear Corporation";
	int revenueCenterIndex;

	public ClearCorporationEffect () {
		super ();
		setName (NAME);
	}

	public ClearCorporationEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex) {
		super (aActor, aMapCell, aTile);
		
		setRevenueCenterIndex (aRevenueCenterIndex);
		setName (NAME);
	}

	public ClearCorporationEffect  (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		int tRevenueCenterIndex;

		tRevenueCenterIndex = aEffectNode.getThisIntAttribute (MapCell.AN_REVENUE_CENTER_INDEX);
		setRevenueCenterIndex (tRevenueCenterIndex);
	}

	public void setRevenueCenterIndex (int aRevenuCenterIndex) {
		revenueCenterIndex = aRevenuCenterIndex;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TokenCompany tTokenCompany;
		int tTileNumber;
		Tile tTile;
		MapCell tMapCell;
		HexMap tHexMap;
		GameManager tGameManager;
		
		tEffectApplied = false;
		if (actor.isATokenCompany ()) {
			tTokenCompany = (TokenCompany) actor;
			tGameManager = aRoundManager.getGameManager ();
			tHexMap = tGameManager.getGameMap ();
			tMapCell = getMapCell (tHexMap);
			tTileNumber = tMapCell.getTileNumber ();
			tTile = tMapCell.getTile ();
			if (tTileNumber == tTile.getNumber ()) {
				tEffectApplied = tTile.clearCorporation (tTokenCompany);
			} else {
				setApplyFailureReason ("Specified Tile Number " + tTileNumber + " Does not match the Tile on the MapCell");
			}
		} else {
			setApplyFailureReason ("The Actor " + actor.getName () + " is not a TokenCompany.");
		}
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TokenCompany tTokenCompany;
		int tTileNumber;
		Tile tTile;
		MapCell tMapCell;
		HexMap tHexMap;
		GameManager tGameManager;
		RevenueCenter tRevenueCenter;
		
		tEffectUndone = false;
		if (actor.isATokenCompany ()) {
			tTokenCompany = (TokenCompany) actor;
			tGameManager = aRoundManager.getGameManager ();
			tHexMap = tGameManager.getGameMap ();
			tMapCell = getMapCell (tHexMap);
			tTileNumber = tMapCell.getTileNumber ();
			tTile = tMapCell.getTile ();
			if (tTileNumber == tTile.getNumber ()) {
				tRevenueCenter = tTile.getRevenueCenter (revenueCenterIndex);
				tEffectUndone = tRevenueCenter.setCorporationHome (tTokenCompany);
			} else {
				setUndoFailureReason ("Specified Tile Number " + tTileNumber + " Does not match the Tile on the MapCell");
			}

		} else {
			setUndoFailureReason ("The Actor " + actor.getName () + " is not a TokenCompany.");
		}

		return tEffectUndone;
	}
}
