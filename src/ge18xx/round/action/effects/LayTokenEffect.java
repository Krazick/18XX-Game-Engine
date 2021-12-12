package ge18xx.round.action.effects;

import ge18xx.center.City;
import ge18xx.company.Corporation;
import ge18xx.company.TokenCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class LayTokenEffect extends ChangeMapEffect {
	public final static String NAME = "Lay Token";
	int tileNumber;
	int revenueCenterIndex;
	
	public LayTokenEffect () {
		super ();
		setName (NAME);
	}

	public LayTokenEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, Benefit aBenefitInUse) {
		super (aActor, aMapCell, aBenefitInUse);
		setName (NAME);
		setTileNumber (aTile);
		setRevenueCenterIndex (aRevenueCenterIndex);
	}

	public LayTokenEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tTileNumber, tRevenueCenterIndex;
		Tile tTile;
		
		tRevenueCenterIndex = aEffectNode.getThisIntAttribute (MapCell.AN_REVENUE_CENTER_INDEX);
		tTileNumber = aEffectNode.getThisIntAttribute (Tile.AN_TILE_NUMBER);
		tTile = aGameManager.getTile (tTileNumber);
		
		setTileNumber (tTile);
		setRevenueCenterIndex (tRevenueCenterIndex);
	}

	public int getTileNumber () {
		return tileNumber;
	}
	
	public int getRevenueCenterInex () {
		return revenueCenterIndex;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (Tile.AN_TILE_NUMBER, tileNumber);
		tEffectElement.setAttribute (MapCell.AN_REVENUE_CENTER_INDEX, getRevenueCenterInex ());
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tBenefitReport = getBenefitEffectReport ();
		
		return (REPORT_PREFIX + getName () + " on Tile " + tileNumber + " at Center Index " + revenueCenterIndex +
				" by " + getActor ().getName () + " on MapCell " + getMapCellID () + "." + tBenefitReport);
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	public void setTileNumber (Tile aTile) {
		tileNumber = aTile.getNumber ();
	}
	
	public void setRevenueCenterIndex (int aRevenuCenterIndex) {
		revenueCenterIndex = aRevenuCenterIndex;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Tile tTile;
		MapCell tMapCell;
		Corporation tCorporation;
		City tCity;
		HexMap tGameMap;
		
		tGameMap = aRoundManager.getGameMap ();
		tEffectApplied = false;
		tMapCell = super.getMapCell (tGameMap);
		tTile = tMapCell.getTile ();
		if (tTile.getNumber () == tileNumber) {
			tCorporation = (Corporation) getActor ();
			if (tCorporation instanceof TokenCompany) {
				tCity = (City) tTile.getRevenueCenter (revenueCenterIndex);
				// Apply effect on Remote Client, don't want to add a new LayTokenAction (or spend money)
				tGameMap.putMapTokenDown (tCorporation, tCity, tMapCell, false);
				tGameMap.redrawMap ();
				tEffectApplied = true;
			} else {
				System.err.println ("Apply " + getName () + " by " + getActor ().getName () +
						" Fails since this is not a Token Company");
			}
		} else {
			System.err.println ("Apply " + getName () + " by " + getActor().getName () + 
					" Fails since Tile Numbers don't match");
		}

		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Tile tTile;
		MapCell tMapCell, tCorpHomeCell1, tCorpHomeCell2;
//		MapToken tMapToken;
		Corporation tCorporation;
		TokenCompany tTokenCompany;
		int tCorporationID;
		int tTokenAtID;
		HexMap tGameMap;
		
		tGameMap = aRoundManager.getGameMap ();
		tEffectUndone = false;
		tMapCell = super.getMapCell (tGameMap);
		tTile = tMapCell.getTile ();
		if (tTile.getNumber () == tileNumber) {
			tCorporation = (Corporation) getActor ();
			if (tCorporation instanceof TokenCompany) {
				tTokenCompany = (TokenCompany) tCorporation;
				tCorporationID = tTokenCompany.getID ();
				tTokenAtID = tTile.getStationIndex (tCorporationID);
				if (tTokenAtID == revenueCenterIndex) {
					tCorpHomeCell1 = tTokenCompany.getHomeCity1 ();
					tCorpHomeCell2 = tTokenCompany.getHomeCity2 ();
					if (tCorpHomeCell1 != MapCell.NO_MAP_CELL) {
						if (tMapCell.getID ().equals (tCorpHomeCell1.getID ())) {
							tMapCell.setCorporation (tTokenCompany, tTokenCompany.getHomeLocation1 ());
						}
					}
					if (tCorpHomeCell2 != MapCell.NO_MAP_CELL) {
						if (tMapCell.getID ().equals (tCorpHomeCell2.getID ())) {
							tMapCell.setCorporation (tTokenCompany, tTokenCompany.getHomeLocation2 ());
						}
					}
					tTile.returnStation (tTokenCompany);
					tGameMap.redrawMap ();
					tTokenCompany.updateFrameInfo ();
					tEffectUndone = true;
				} else {
					System.err.println ("Undo " + getName () + " by " + getActor ().getName () + 
							" Fails since TokenAtID " + tTokenAtID + " doesn't match the RCIndex " + 
							revenueCenterIndex);
				}
			} else {
				System.err.println ("Undo " + getName () + " by " + getActor ().getName () +
						" Fails since this is not a Token Company");
			}
		} else {
			System.err.println ("Undo " + getName () + " by " + getActor().getName () + 
					" Fails since Tile Numbers don't match");
		}
		
		return tEffectUndone;
	}

}
