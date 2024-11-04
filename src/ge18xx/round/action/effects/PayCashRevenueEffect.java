package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;

public class PayCashRevenueEffect extends PayCashDividendEffect {
	public final static String NAME = "Pay Cash Revenue";
	public final static AttributeName AN_OPERATING_ROUND_PART2 = new AttributeName ("operatingRoundPart2");
	int operatingRoundPart2;

	public PayCashRevenueEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount, String aOperatingRoundPart2) {
		super (aFromActor, aToActor, aCashAmount, aOperatingRoundPart2);
		setName (NAME);
	}
	
	public PayCashRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
//		int tOperatingRoundPart2;
//		
//		tOperatingRoundPart2 = aEffectNode.getThisIntAttribute (AN_OPERATING_ROUND_PART2);
//		setOperatingRoundPart2 (tOperatingRoundPart2);
	}

}
