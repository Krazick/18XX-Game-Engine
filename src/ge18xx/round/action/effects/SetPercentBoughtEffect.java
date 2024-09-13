package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetPercentBoughtEffect extends Effect {
	public final static String NAME = "Set Percent Bought";
	public final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	public final static AttributeName AN_PREVIOUS_PERCENT_BOUGHT = new AttributeName ("previousPercentBought");
	public final static AttributeName AN_NEW_PERCENT_BOUGHT = new AttributeName ("newPercentBought");
	String companyAbbrev;
	int previousPercent;
	int newPercent;

	public SetPercentBoughtEffect (ActorI aActor, String aCompanyAbbrev, int aPreviousPercent, int aNewPercent) {
		super (NAME, aActor);
		setCompanyAbbrev (aCompanyAbbrev);
		setPreviousPercent (aPreviousPercent);
		setNewPercent (aNewPercent);
	}

	public SetPercentBoughtEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tCompanyAbbrev;
		int tPreviousPercent;
		int tNewPercent;
		
		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		setCompanyAbbrev (tCompanyAbbrev);
		tPreviousPercent = aEffectNode.getThisIntAttribute (AN_PREVIOUS_PERCENT_BOUGHT);
		setPreviousPercent (tPreviousPercent);
		tNewPercent = aEffectNode.getThisIntAttribute (AN_NEW_PERCENT_BOUGHT);
		setNewPercent (tNewPercent);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, companyAbbrev);
		tEffectElement.setAttribute (AN_PREVIOUS_PERCENT_BOUGHT, previousPercent);
		tEffectElement.setAttribute (AN_NEW_PERCENT_BOUGHT, newPercent);
		
		return tEffectElement;
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}
	
	public void setPreviousPercent (int aPreviousPercent) {
		previousPercent = aPreviousPercent;
	}
	
	public void setNewPercent (int aNewPercent) {
		newPercent = aNewPercent;
	}
	
	public int getPreviousPercent () {
		return previousPercent;
	}
	
	public int getNewPercent () {
		return newPercent;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Player tPlayer;

		tPlayer = (Player) getActor ();

		return (REPORT_PREFIX + name + " for " + tPlayer.getName () + " Company Abbrev " + companyAbbrev + 
				" from " + previousPercent + "% to " + newPercent + "%.");
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
		tPlayer.setPercentBought (companyAbbrev, newPercent);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tPlayer = (Player) getActor ();
		tPlayer.setPercentBought (companyAbbrev, previousPercent);
		tEffectUndone = true;

		return tEffectUndone;
	}

}
