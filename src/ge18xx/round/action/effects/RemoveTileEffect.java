package ge18xx.round.action.effects;

import ge18xx.center.RevenueCenter;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class RemoveTileEffect extends ChangeTileContentEffect {
	public static final String NAME = "Remove Tile";
	final static AttributeName AN_PREVIOUS_TOKENS = new AttributeName ("previousTokens");
	final static AttributeName AN_PREVIOUS_BASES = new AttributeName ("previousBases");
//	String previousTokens;
//	String previousBases;

	public RemoveTileEffect () {
		super ();
		setName (NAME);
	}

	public RemoveTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, 
			int aOrientation, String aPreviousTokens, String aPreviousBases) {
		super (aActor, aMapCell, aTile, aOrientation, aPreviousTokens, aPreviousBases);
		setName (NAME);
//		setPreviousTokens (aPreviousTokens);
//		setPreviousBases (aPreviousBases);
	}

	public RemoveTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
//		String tPreviousTokens, tPreviousBases;
//		
//		tPreviousTokens = aEffectNode.getThisAttribute (AN_PREVIOUS_TOKENS);
//		tPreviousBases = aEffectNode.getThisAttribute (AN_PREVIOUS_BASES);
//		setPreviousTokens (tPreviousTokens);
//		setPreviousBases (tPreviousBases);
	}

	public String getPreviousBases () {
		return getBases ();
	}
	
	public String getPreviousTokens () {
		return getTokens ();
	}
//	
//	public void setPreviousBases (String aPreviousBases) {
//		previousBases = aPreviousBases;
//	}
//
//	public void setPreviousTokens (String aPreviousTokens) {
//		previousTokens = aPreviousTokens;
//	}
	
	public void restorePreviousBases (RoundManager aRoundManager, Tile aTile, MapCell aMapCell) {
		String [] tPreviousBases;
		String [] tBaseInfo;
		String tAbbrev;
		int tIndex;
		ShareCompany tShareCompany;
		GameManager tGameManager;
		RevenueCenter tRevenueCenter;
		Location tLocation;
		String tOldBases;
		
		tOldBases = getBases ();
		if (! (Tile.NO_BASES.equals (tOldBases))) {
			tPreviousBases = tOldBases.split (";");
			// Previous Bases are "CompanyAbbrev,CityIndex"
			for (String tAPreviousBase : tPreviousBases) {
				tBaseInfo = tAPreviousBase.split (",");
				tAbbrev = tBaseInfo [0];
				tIndex = Integer.parseInt (tBaseInfo [1]);
				tGameManager = aRoundManager.getGameManager ();
				tShareCompany = tGameManager.getShareCompany (tAbbrev);

				tRevenueCenter = aTile.getRevenueCenter (tIndex);
				tLocation = tRevenueCenter.getLocation ();
				aMapCell.setCorporation (tShareCompany, tLocation);
			}
		}
	}
	
	public void restorePreviousTokens (RoundManager aRoundManager, MapCell aMapCell) {
		String [] tPreviousTokens;
		String [] tTokenInfo;
		String tAbbrev;
		int tStationIndex, tCityIndex;
		ShareCompany tShareCompany;
		GameManager tGameManager;
		String tOldTokens;
		
		tOldTokens = getTokens ();
		if (! (Tile.NO_TOKENS.equals (tOldTokens))) {
			tPreviousTokens = tOldTokens.split (";");
			// Previous Tokens are "CompanyAbbrev,StationIndex,CityIndex"
			for (String tAPreviousToken : tPreviousTokens) {
				tTokenInfo = tAPreviousToken.split (",");
				tAbbrev = tTokenInfo [0];
				tStationIndex = Integer.parseInt (tTokenInfo [1]);
				tCityIndex = Integer.parseInt (tTokenInfo [2]);
				tGameManager = aRoundManager.getGameManager ();
				tShareCompany = tGameManager.getShareCompany (tAbbrev);
				aMapCell.setStationAt (tShareCompany, tStationIndex, tCityIndex);
			}
		}
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " #" + tileNumber + " with orientation " + orientation +
				" by " + actor.getName () + " on MapCell " + mapCellID +
				" Old Tokens [ " + getTokens () + " ] Old Bases [ " + getBases () + " ].");
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
		tTileSet = aRoundManager.getTileSet ();
		tGameMap = aRoundManager.getGameMap ();
		tMapCell = this.getMapCell (tGameMap);
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
		
		tEffectUndone = false;
		tTileSet = aRoundManager.getTileSet ();
		tTile = tTileSet.popTile (getTileNumber ());
		tGameMap = aRoundManager.getGameMap ();
		tMapCell = this.getMapCell (tGameMap);
		tMapCell.putTile (tTile, orientation);
		restorePreviousBases (aRoundManager, tTile, tMapCell);
		restorePreviousTokens (aRoundManager, tMapCell);
		tGameMap.redrawMap ();
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}