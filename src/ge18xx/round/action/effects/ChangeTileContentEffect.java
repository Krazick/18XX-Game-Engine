package ge18xx.round.action.effects;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

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

	public boolean applyTokens (RoundManager aRoundManager, MapCell aMapCell, String aTokens) {
		GameManager tGameManager;
		boolean tTokensApplied;
		
		tGameManager = aRoundManager.getGameManager ();
		tTokensApplied = aMapCell.applyTokens (aTokens, tGameManager);
			 
		return tTokensApplied;
	}

	public boolean applyBases (RoundManager aRoundManager, MapCell aMapCell) {
		boolean tBasesApplied;
		GameManager tGameManager;
		
		tGameManager = aRoundManager.getGameManager ();
		tBasesApplied = aMapCell.applyBases (bases, tGameManager);
		
		return tBasesApplied;
	}
} 
