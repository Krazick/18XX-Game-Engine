package ge18xx.round.action.effects;

import ge18xx.center.City;
import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.TokenCompany;
import ge18xx.company.TokenInfo;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.company.Tokens;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class LayTokenEffect extends ChangeMapEffect {
	public final static String NAME = "Lay Token";
	int tileNumber;
	int revenueCenterIndex;
	int tokenIndex;
	TokenType tokenType;

	public LayTokenEffect () {
		super ();
		setName (NAME);
	}

	public LayTokenEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex,
			TokenType aTokenType, int aTokenIndex, Benefit aBenefitInUse) {
		super (aActor, aMapCell, aBenefitInUse);
		setName (NAME);
		setTileNumber (aTile);
		setRevenueCenterIndex (aRevenueCenterIndex);
		setTokenIndex (aTokenIndex);
		setTokenType (aTokenType);
	}

	public LayTokenEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tTileNumber;
		int tRevenueCenterIndex;
		int tTokenIndex;
		String tTokenType;
		Tile tTile;

		tRevenueCenterIndex = aEffectNode.getThisIntAttribute (MapCell.AN_REVENUE_CENTER_INDEX);
		tTileNumber = aEffectNode.getThisIntAttribute (Tile.AN_TILE_NUMBER);
		tTokenType = aEffectNode.getThisAttribute (TokenInfo.AN_AVAILABLE_TOKEN_TYPE);
		tTokenIndex = aEffectNode.getThisIntAttribute (Tokens.AN_TOKEN_INDEX);
		tTile = aGameManager.getTile (tTileNumber);
		setTileNumber (tTile);
		setRevenueCenterIndex (tRevenueCenterIndex);
		setTokenIndex (tTokenIndex);
		setTokenType (tTokenType);
	}
	
	public void setTokenType (String aTokenType) {
		for (TokenInfo.TokenType eTokenType : TokenInfo.TokenType.values ()) { 
			if (eTokenType.toString ().equals (aTokenType)) {
				setTokenType (eTokenType);
			}
		}
	}

	public void setTokenIndex (int aTokenIndex) {
		tokenIndex = aTokenIndex;
	}
	
	public void setTokenType (TokenType aTokenType) {
		tokenType = aTokenType;
	}
	
	public int getTileNumber () {
		return tileNumber;
	}

	public int getRevenueCenterIndex () {
		return revenueCenterIndex;
	}

	public int getTokenIndex () {
		return tokenIndex;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (Tile.AN_TILE_NUMBER, tileNumber);
		tEffectElement.setAttribute (MapCell.AN_REVENUE_CENTER_INDEX, getRevenueCenterIndex ());
		tEffectElement.setAttribute (Tokens.AN_TOKEN_INDEX, getTokenIndex ());
		tEffectElement.setAttribute (TokenInfo.AN_AVAILABLE_TOKEN_TYPE, tokenType.toString ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tBenefitReport;
		String tTokenType;
		String tActorName;
		String tMapCellID;
		
		tBenefitReport = getBenefitEffectReport ();
		tTokenType = tokenType.toString ();
		tActorName = getActor ().getName ();
		tMapCellID = getMapCellID ();
		
		return (REPORT_PREFIX + getName () + " Token (Index " + tokenIndex + " Type " + tTokenType +
				") on Tile " + tileNumber + " at Center Index " + revenueCenterIndex
				+ " by " + tActorName + " on MapCell " + tMapCellID + "." + tBenefitReport);
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
		String tActionVerb;

		tActionVerb = "Apply";
		tEffectApplied = layToken (aRoundManager, tActionVerb);

		return tEffectApplied;
	}

	public boolean layToken (RoundManager aRoundManager, String aActionVerb) {
		boolean tEffectApplied;
		Tile tTile;
		MapCell tMapCell;
		MapToken tMapToken;
		Corporation tCorporation;
		TokenCompany tTokenCompany;
		City tCity;
		HexMap tGameMap;
		
		tGameMap = aRoundManager.getGameMap ();
		tEffectApplied = false;
		tMapCell = getMapCell (tGameMap);
		tTile = tMapCell.getTile ();
		if (tTile.getNumber () == tileNumber) {
			tCorporation = (Corporation) getActor ();
			if (tCorporation.isATokenCompany ()) {
				tTokenCompany = (TokenCompany) tCorporation;
				tCity = (City) tTile.getRevenueCenter (revenueCenterIndex);
				tMapToken = (MapToken) tTokenCompany.getTokenAt (tokenIndex);
				// Apply effect on Remote Client, don't want to add a new LayTokenAction (or
				// spend money)

				tGameMap.putMapTokenDown (tTokenCompany, tMapToken, tokenType, tCity, tMapCell, false);
				tGameMap.redrawMap ();
				tEffectApplied = true;
			} else {
				System.err.println (aActionVerb + " " + getName () + " by " + getActor ().getName () +
						" Fails since this is not a Token Company");
			}
		} else {
			System.err.println (aActionVerb + " " + getName () + " by " + getActor ().getName () + 
						" Fails since Tile Numbers don't match");
		}
		return tEffectApplied;
	}
	
	public TileSet getTileSet (RoundManager aRoundManager) {
		return aRoundManager.getTileSet ();
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		String tActionVerb;
		
		tActionVerb = "Undone";
		tEffectUndone = removeToken (aRoundManager, tActionVerb);

		return tEffectUndone;
	}

	public boolean removeToken (RoundManager aRoundManager, String aActionVerb) {
		Tile tTile;
		MapCell tMapCell;
		MapCell tCorpHomeCell1;
		MapCell tCorpHomeCell2;
		Corporation tCorporation;
		TokenCompany tTokenCompany;
		int tCorporationID;
		int tTokenAtID;
		HexMap tGameMap;
		boolean tEffectUndone;

		tEffectUndone = false;
		tGameMap = getMap (aRoundManager);
		tMapCell = getMapCell (tGameMap);
		tTile = tMapCell.getTile ();
		if (tTile.getNumber () == tileNumber) {
			tCorporation = (Corporation) getActor ();
			if (tCorporation.isATokenCompany ()) {
				tTokenCompany = (TokenCompany) tCorporation;
				tCorporationID = tTokenCompany.getID ();
				tTokenAtID = tTile.getStationIndex (tCorporationID);
				if (tTokenAtID == revenueCenterIndex) {
					tCorpHomeCell1 = tTokenCompany.getHomeCity1 ();
					tCorpHomeCell2 = tTokenCompany.getHomeCity2 ();
					if (tCorpHomeCell1 != MapCell.NO_MAP_CELL) {
						if (tMapCell.getID ().equals (tCorpHomeCell1.getID ())) {
							tMapCell.setCorporationHome (tTokenCompany, tTokenCompany.getHomeLocation1 ());
						}
					}
					if (tCorpHomeCell2 != MapCell.NO_MAP_CELL) {
						if (tMapCell.getID ().equals (tCorpHomeCell2.getID ())) {
							tMapCell.setCorporationHome (tTokenCompany, tTokenCompany.getHomeLocation2 ());
						}
					}
					tTile.returnStation (tTokenCompany);
					tGameMap.redrawMap ();
					tTokenCompany.updateFrameInfo ();
					tEffectUndone = true;
				} else {
					System.err.println (aActionVerb + " " + getName () + " by " + getActor ().getName () + 
										" Fails since TokenAtID " + tTokenAtID + " doesn't match the RCIndex " + 
										revenueCenterIndex);
				}
			} else {
				System.err.println (aActionVerb + " " + getName () + " by " + getActor ().getName () +
						" Fails since this is not a Token Company");
			}
		} else {
			System.err.println (aActionVerb +" " + getName () + " by " + getActor ().getName () + 
								" Fails since Tile Numbers don't match");
		}
		
		return tEffectUndone;
	}
}
