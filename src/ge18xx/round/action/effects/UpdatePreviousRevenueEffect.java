package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class UpdatePreviousRevenueEffect extends Effect {
	public static final AttributeName AN_OLD_PREVIOUS_REVENUE = new AttributeName ("oldPreviousRevenue");
	public static final AttributeName AN_NEW_PREVIOUS_REVENUE = new AttributeName ("newPreviousRevenue");
	public static final String NAME = "Update Previous Revenue";
	int oldPreviousRevenue;
	int newPreviousRevenue;

	public UpdatePreviousRevenueEffect () {
		this (NAME);
	}

	public UpdatePreviousRevenueEffect (String aName) {
		super (aName);
	}

	public UpdatePreviousRevenueEffect (ActorI aActor, int aOldPreviousRevenue, int aNewPreviousRevenue) {
		super (NAME, aActor);
		setNewPreviousRevenue (aNewPreviousRevenue);
		setOldPreviousRevenue (aOldPreviousRevenue);
	}

	public UpdatePreviousRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tOldPreviousRevenue;
		int tNewPreviousRevenue;

		tOldPreviousRevenue = aEffectNode.getThisIntAttribute (AN_OLD_PREVIOUS_REVENUE);
		tNewPreviousRevenue = aEffectNode.getThisIntAttribute (AN_NEW_PREVIOUS_REVENUE);
		setOldPreviousRevenue (tOldPreviousRevenue);
		setNewPreviousRevenue (tNewPreviousRevenue);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OLD_PREVIOUS_REVENUE, getOldPreviousRevenue ());
		tEffectElement.setAttribute (AN_NEW_PREVIOUS_REVENUE, getNewPreviousRevenue ());

		return tEffectElement;
	}

	public void setOldPreviousRevenue (int aRevenue) {
		oldPreviousRevenue = aRevenue;
	}

	public void setNewPreviousRevenue (int aRevenue) {
		newPreviousRevenue = aRevenue;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		String tOldValue;
		String tNewValue;

		if (oldPreviousRevenue >= 0) {
			tOldValue = Bank.formatCash (oldPreviousRevenue);
		} else {
			tOldValue = " NO Previous Revenue Value";
		}

		if (newPreviousRevenue >= 0) {
			tNewValue = Bank.formatCash (newPreviousRevenue);
		} else {
			tNewValue = "NO Previous Revenue Value";
		}
		tReport = REPORT_PREFIX + name + " of " + tOldValue + " with " + tNewValue + " for " + getActorName () + ".";

		return tReport;
	}

	public int getOldPreviousRevenue () {
		return oldPreviousRevenue;
	}

	public int getNewPreviousRevenue () {
		return newPreviousRevenue;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tOperatingCompany;

		tEffectApplied = false;
		tOperatingCompany = (TrainCompany) aRoundManager.getOperatingCompany ();
		tOperatingCompany.setPreviousRevenue (newPreviousRevenue);
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
		tTrainCompany.setPreviousRevenue (oldPreviousRevenue);
		aRoundManager.updateAllCorporationsBox ();

		tEffectUndone = true;

		return tEffectUndone;
	}
}
