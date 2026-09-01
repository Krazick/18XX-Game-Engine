package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class PayCashRevenueEffect extends PayCashDividendEffect {
	public static final String NAME = "Pay Cash Revenue";
	public static final AttributeName AN_OPERATING_ROUND_PART2 = new AttributeName ("operatingRoundPart2");
	public static final AttributeName AN_PRIVATE_ABBREV = new AttributeName ("privateAbbrev");
	int operatingRoundPart2;
	String companyAbbrev;

	public PayCashRevenueEffect (ActorI aFromActor, ActorI aToActor, String aCompanyAbbrev,
			int aCashAmount, String aOperatingRoundPart2) {
		super (aFromActor, aToActor, aCashAmount, aOperatingRoundPart2);
		setName (NAME);
		setCompanyAbbrev (aCompanyAbbrev);
	}
	
	public PayCashRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tPrivateAbbrev;
		int tOperatingRoundPart2;
		
		tOperatingRoundPart2 = aEffectNode.getThisIntAttribute (AN_OPERATING_ROUND_PART2);
		setOperatingRoundID2 (tOperatingRoundPart2);
		tPrivateAbbrev = aEffectNode.getThisAttribute (AN_PRIVATE_ABBREV);
		setCompanyAbbrev (tPrivateAbbrev);
		setName (NAME);	
	}

	public void setOperatingRoundID2 (int aOperatingRoundPart2) {
		operatingRoundPart2 = aOperatingRoundPart2;
	}
	
	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OPERATING_ROUND_PART2, operatingRoundPart2);
		tEffectElement.setAttribute (AN_PRIVATE_ABBREV, companyAbbrev);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tFromActorName;
		String tToActorName;
		
		tFromActorName = getActorName ();
		tToActorName = getToActorName ();
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (cash) 
				+ " for " + companyAbbrev + " Private"
				+ " from " + tFromActorName + " to "
				+ tToActorName + " in Operating Round " + operatingRoundID + ".");
	}
}
