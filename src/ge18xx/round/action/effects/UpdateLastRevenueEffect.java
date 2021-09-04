package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class UpdateLastRevenueEffect extends Effect {
	public final static String NAME = "Update Last Revenue";
	final static AttributeName AN_OLD_LAST_REVENUE = new AttributeName ("oldLastRevenue");
	final static AttributeName AN_NEW_LAST_REVENUE = new AttributeName ("newLastRevenue");
	int oldLastRevenue;
	int newLastRevenue;
	
	public UpdateLastRevenueEffect () {
		this (NAME);
	}

	public UpdateLastRevenueEffect (String aName) {
		super (aName);
	}

	public UpdateLastRevenueEffect (ActorI aActor, int aNewLastRevenue, int aOldLastRevenue) {
		super (NAME, aActor);
		setNewLastRevenue (aNewLastRevenue);
		setOldLastRevenue (aOldLastRevenue);
	}

	public UpdateLastRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int aOldLastRevenue, aNewLastRevenue;
		
		aOldLastRevenue = aEffectNode.getThisIntAttribute (AN_OLD_LAST_REVENUE);
		aNewLastRevenue = aEffectNode.getThisIntAttribute (AN_NEW_LAST_REVENUE);
		setNewLastRevenue (aNewLastRevenue);
		setOldLastRevenue (aOldLastRevenue);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OLD_LAST_REVENUE, getOldLastRevenue ());
		tEffectElement.setAttribute (AN_NEW_LAST_REVENUE, getNewLastRevenue ());
	
		return tEffectElement;
	}

	public void setOldLastRevenue (int aRevenue) {
		oldLastRevenue = aRevenue;
	}
	
	public void setNewLastRevenue (int aRevenue) {
		newLastRevenue = aRevenue;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (oldLastRevenue) +
				" with " + newLastRevenue + " for " + getActorName () + ".");
	}
	
	public int getOldLastRevenue () {
		return oldLastRevenue;
	}
	
	public int getNewLastRevenue () {
		return newLastRevenue;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tOperatingCompany;
		
		tEffectApplied = false;
		tOperatingCompany = (TrainCompany) aRoundManager.getOperatingCompany ();
		tOperatingCompany.setLastRevenue (newLastRevenue);
		aRoundManager.updateAllCorporationsBox ();
		
		return tEffectApplied;
	}
}
