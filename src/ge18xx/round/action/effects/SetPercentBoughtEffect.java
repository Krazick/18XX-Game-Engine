package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class SetPercentBoughtEffect extends Effect {
	public final static String NAME = "Set Percent Bought";
	public final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	public final static AttributeName AN_PERCENT_BOUGHT = new AttributeName ("percentBought");
	String companyAbbrev;
	int percent;

	public SetPercentBoughtEffect () {
		super ();
		setName (NAME);
		setCompanyAbbrev (Corporation.NO_NAME_STRING);
		setPercent (0);
	}

	public SetPercentBoughtEffect (ActorI aActor, String aCompanyAbbrev, int aPercent) {
		super (NAME, aActor);
		setCompanyAbbrev (aCompanyAbbrev);
		setPercent (aPercent);
	}

	public SetPercentBoughtEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tCompanyAbbrev;
		int tPercent;
		
		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		setCompanyAbbrev (tCompanyAbbrev);
		tPercent = aEffectNode.getThisIntAttribute (AN_PERCENT_BOUGHT);
		setPercent (tPercent);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, companyAbbrev);
		tEffectElement.setAttribute (AN_PERCENT_BOUGHT, percent);

		return tEffectElement;
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}
	
	public void setPercent (int aPercent) {
		percent = aPercent;
	}
	
	public int getPercent () {
		return percent;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Player tPlayer;

		tPlayer = (Player) getActor ();

		return (REPORT_PREFIX + name + " for " + tPlayer.getName () + " Company Abbrev " + companyAbbrev + 
				" " + percent + "%.");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tPlayer = (Player) getActor ();
		tPlayer.addPercentBought (companyAbbrev, percent);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tPlayer = (Player) getActor ();
		tPlayer.addPercentBought (companyAbbrev, -percent);
		tEffectUndone = true;

		return tEffectUndone;
	}

}
