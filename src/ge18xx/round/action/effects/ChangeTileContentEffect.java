package ge18xx.round.action.effects;

import ge18xx.center.RevenueCenter;
import ge18xx.company.ShareCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class ChangeTileContentEffect extends ChangeTileEffect {
	public static final String NAME = "Change Tile Contents";
	final static AttributeName AN_TOKENS = new AttributeName ("tokens");
	final static AttributeName AN_BASES = new AttributeName ("bases");

	String tokens;
	String bases;

	public ChangeTileContentEffect () {
		this (NAME);
	}

	public ChangeTileContentEffect (String aName) {
		super (aName);
	}

	public ChangeTileContentEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation, String aTokens,
			String aBases) {
		this (aActor, aMapCell, aTile, aOrientation, aTokens, aBases, NO_BENEFIT_IN_USE);
	}

	public ChangeTileContentEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation, String aTokens,
			String aBases, Benefit aBenefitInUse) {
		super (aActor, aMapCell, aTile, aOrientation, aBenefitInUse);

		setName (NAME);
		setTokens (aTokens);
		setBases (aBases);
	}

	public ChangeTileContentEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		String tTokens;
		String tBases;

		tTokens = aEffectNode.getThisAttribute (AN_TOKENS);
		tBases = aEffectNode.getThisAttribute (AN_BASES);
		setTokens (tTokens);
		setBases (tBases);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_TOKENS, getTokens ());
		tEffectElement.setAttribute (AN_BASES, getBases ());

		return tEffectElement;
	}

	public String getBases () {
		return bases;
	}

	public String getTokens () {
		return tokens;
	}

	public void setBases (String aBases) {
		bases = aBases;
	}

	public void setTokens (String aTokens) {
		tokens = aTokens;
	}
	
	public TileSet getTileSet (RoundManager aRoundManager) {
		return aRoundManager.getTileSet ();
	}

	public void applyTokens (RoundManager aRoundManager, MapCell aMapCell) {
		String [] tTokens;
		String [] tTokenInfo;
		String tAbbrev;
		int tStationIndex;
		int tCityIndex;
		int tTokenIndex;
		ShareCompany tShareCompany;
		String tTheTokens;

		tTheTokens = getTokens ();
		if (!(Tile.NO_TOKENS.equals (tTheTokens))) {
			tTokens = tTheTokens.split (";");
			// Format for Tokens are "CompanyAbbrev,StationIndex,CityIndex"
			for (String tAToken : tTokens) {
				tTokenInfo = tAToken.split (",");
				tAbbrev = tTokenInfo [0];
				tStationIndex = Integer.parseInt (tTokenInfo [1]);
				tCityIndex = Integer.parseInt (tTokenInfo [2]);
				tTokenIndex = Integer.parseInt (tTokenInfo [3]);
				tShareCompany = aRoundManager.getShareCompany (tAbbrev);
				aMapCell.returnStation (tShareCompany);
				aMapCell.setStationAt (tShareCompany, tStationIndex, tCityIndex, tTokenIndex);
			}
		}
	}

	public void applyBases (RoundManager aRoundManager, MapCell aMapCell) {
		String [] tBases;
		String [] tBaseInfo;
		String tAbbrev;
		int tIndex;
		ShareCompany tShareCompany;
		RevenueCenter tRevenueCenter;
		Location tLocation;
		String tTheBases;
		Tile tTile;

		tTile = aMapCell.getTile ();
		tTheBases = getBases ();
		if (!(Tile.NO_BASES.equals (tTheBases))) {
			tBases = tTheBases.split (";");
			// Format for Bases are "CompanyAbbrev,CityIndex"
			for (String tAPreviousBase : tBases) {
				tBaseInfo = tAPreviousBase.split (",");
				tAbbrev = tBaseInfo [0];
				tIndex = Integer.parseInt (tBaseInfo [1]);
				tShareCompany = aRoundManager.getShareCompany (tAbbrev);

				tRevenueCenter = tTile.getRevenueCenter (tIndex);
				tLocation = tRevenueCenter.getLocation ();
				tLocation = tLocation.rotateLocation (aMapCell.getTileOrient ());
				aMapCell.setCorporationHome (tShareCompany, tLocation);
			}
		}
	}
}
