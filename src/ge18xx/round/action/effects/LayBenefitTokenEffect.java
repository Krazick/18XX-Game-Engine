package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class LayBenefitTokenEffect extends ChangeMapEffect {
	public static final AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	public static final AttributeName AN_TOKEN_BONUS = new AttributeName ("tokenBonus");
	public final static String NAME = "Lay Benefit Token";
	String tokenType;
	int tokenBonus;
	
	public LayBenefitTokenEffect () {
		this (NAME);
	}

	public LayBenefitTokenEffect (String aName) {
		super (aName);
	}

	public LayBenefitTokenEffect (ActorI aActor, MapCell aMapCell, String aTokenType, int aTokenBonus) {
		super (aActor, aMapCell);
		setTokenType (aTokenType);
		setTokenBonus (aTokenBonus);
		setName (NAME);
	}

	public LayBenefitTokenEffect (ActorI aActor, MapCell aMapCell, String aTokenType, Benefit aBenefitInUse, int aTokenBonus) {
		super (aActor, aMapCell, aBenefitInUse);
		setTokenType (aTokenType);
		setTokenBonus (aTokenBonus);
		setName (NAME);
	}

	public LayBenefitTokenEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tTokenType;
		int tTokenBonus;

		tTokenType = aEffectNode.getThisAttribute (AN_TOKEN_TYPE);
		setTokenType (tTokenType);
		tTokenBonus = aEffectNode.getThisIntAttribute (AN_TOKEN_BONUS);
		setTokenBonus (tTokenBonus);
	}

	public int getTokenBonus () {
		return tokenBonus;
	}
	
	public void setTokenBonus (int aTokenBonus) {
		tokenBonus = aTokenBonus;
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
		tEffectElement.setAttribute (AN_TOKEN_BONUS, tokenBonus);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " by " + actor.getName () + " to lay " + 
				tokenType + " token on MapCell " + mapCellID + " with value " + Bank.formatCash (tokenBonus) + ".");
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
		tMapCell.layBenefitToken (tokenType, tokenBonus);
		setBenefitUsed (aRoundManager);
		tEffectApplied = true;
		tGameMap.redrawMap ();
		
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
