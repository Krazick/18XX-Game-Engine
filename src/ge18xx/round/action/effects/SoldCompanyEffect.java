package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class SoldCompanyEffect extends Effect {
	public static final AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	public static final String NAME = "Sold Company";
	String companyAbbrev;

	public SoldCompanyEffect () {
		super ();
		setName (NAME);
		setCompanyAbbrev (Corporation.NO_NAME_STRING);
	}

	public SoldCompanyEffect (ActorI aActor, String aCompanyAbbrev) {
		super (NAME, aActor);
		setCompanyAbbrev (aCompanyAbbrev);
	}

	public SoldCompanyEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tCompanyAbbrev;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		setCompanyAbbrev (tCompanyAbbrev);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, companyAbbrev);

		return tEffectElement;
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Player tPlayer;

		tPlayer = (Player) getActor ();

		return (REPORT_PREFIX + name + " for " + tPlayer.getName () + " Company Abbrev " + companyAbbrev + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tPlayer = (Player) getActor ();
		tPlayer.addSoldCompany (companyAbbrev);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tPlayer = (Player) getActor ();
		tPlayer.undoSoldCompany (companyAbbrev);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
