package ge18xx.round.action.effects;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class LayBenefitTokenEffect extends ChangeMapEffect {
	public static final AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	public final static String NAME = "Lay Benefit Token";
	String tokenType;
	
	public LayBenefitTokenEffect () {
		this (NAME);
	}

	public LayBenefitTokenEffect (String aName) {
		super (aName);
	}

	public LayBenefitTokenEffect (ActorI aActor, MapCell aMapCell, String aTokenType) {
		super (aActor, aMapCell);
		setTokenType (aTokenType);
		setName (NAME);
	}

	public LayBenefitTokenEffect (ActorI aActor, MapCell aMapCell, String aTokenType, Benefit aBenefitInUse) {
		super (aActor, aMapCell, aBenefitInUse);
		setTokenType (aTokenType);
		setName (NAME);
	}

	public LayBenefitTokenEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tTokenType;

		tTokenType = aEffectNode.getThisAttribute (AN_TOKEN_TYPE);
		setTokenType (tTokenType);
	}

	public String getTokenType () {
		return tokenType;
	}
	
	public void setTokenType (String aTokenType) {
		tokenType = aTokenType;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_TOKEN_TYPE, tokenType);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " by " + actor.getName () + "lay " + tokenType + " on MapCell " + mapCellID + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		MapCell tMapCell;
		HexMap tGameMap;

		tGameMap = aRoundManager.getGameMap ();
		tEffectApplied = false;
		tMapCell = getMapCell (tGameMap);
		tMapCell.layBenefitToken (tokenType);
		setBenefitUsed (aRoundManager);
		tEffectApplied = true;

		return tEffectApplied;
	}

	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		MapCell tMapCell;
		HexMap tGameMap;

		tGameMap = aRoundManager.getGameMap ();
		tEffectApplied = false;
		tMapCell = getMapCell (tGameMap);
		tMapCell.removeBenefitToken (tokenType);
		setBenefitUnUsed (aRoundManager);
		tEffectApplied = true;
		tGameMap.redrawMap ();

		return tEffectApplied;
	}

}
