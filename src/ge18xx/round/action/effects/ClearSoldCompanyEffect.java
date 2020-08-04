package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class ClearSoldCompanyEffect extends Effect {
	public final static String NAME = "Add Clear Sold Company";
	public final static AttributeName AN_SOLD_COMPANIES = new AttributeName ("soldCompanies");
	String soldCompanies;

	public ClearSoldCompanyEffect () {
		super ();
		setName (NAME);
		setCompanies (Corporation.NO_NAME_STRING);
	}

	public ClearSoldCompanyEffect (ActorI aActor, String aSoldCompanies, String aRoundID) {
		super (NAME, aActor);
		setCompanies (aSoldCompanies);
	}

	public ClearSoldCompanyEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tSoldCompanies;
		
		tSoldCompanies = aEffectNode.getThisAttribute (AN_SOLD_COMPANIES);
		setCompanies (tSoldCompanies);
	}

	private void setCompanies (String aSoldCompanies) {
		soldCompanies = aSoldCompanies;
	}
	
	public String getCompanyAbbrev () {
		return soldCompanies;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Player tPlayer;
		
		tPlayer = (Player) getActor ();

		return (REPORT_PREFIX + name + " for " + tPlayer.getName () + ".");
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
		tPlayer.undoSoldCompany (soldCompanies);
		tEffectApplied = true;
	
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		
		tPlayer = (Player) getActor ();
		tPlayer.undoClearSoldCompany (soldCompanies);
		tEffectUndone = true;
	
		return tEffectUndone;
	}
}