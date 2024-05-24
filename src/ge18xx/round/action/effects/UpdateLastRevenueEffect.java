package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class UpdateLastRevenueEffect extends Effect {
	public static final AttributeName AN_OLD_LAST_REVENUE = new AttributeName ("oldLastRevenue");
	public static final AttributeName AN_NEW_LAST_REVENUE = new AttributeName ("newLastRevenue");
	public static final String NAME = "Update Last Revenue";
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
		int tOldLastRevenue;
		int tNewLastRevenue;

		tOldLastRevenue = aEffectNode.getThisIntAttribute (AN_OLD_LAST_REVENUE);
		tNewLastRevenue = aEffectNode.getThisIntAttribute (AN_NEW_LAST_REVENUE);
		setOldLastRevenue (tOldLastRevenue);
		setNewLastRevenue (tNewLastRevenue);
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
		String tReport;
		String tOldValue;
		String tNewValue;

		if (oldLastRevenue >= 0) {
			tOldValue = Bank.formatCash (oldLastRevenue);
		} else {
			tOldValue = " NO Previous Last Revenue Value";
		}

		if (newLastRevenue >= 0) {
			tNewValue = Bank.formatCash (newLastRevenue);
		} else {
			tNewValue = "NO Last Revenue Value";
		}
		tReport = REPORT_PREFIX + name + " of " + tOldValue + " with " + tNewValue + " for " + getActorName () + ".";

		return tReport;
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
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainCompany tTrainCompany;

		tEffectUndone = false;
		tTrainCompany = (TrainCompany) getActor ();
		tTrainCompany.setLastRevenue (oldLastRevenue);
		aRoundManager.updateAllCorporationsBox ();

		tEffectUndone = true;

		return tEffectUndone;
	}
}
