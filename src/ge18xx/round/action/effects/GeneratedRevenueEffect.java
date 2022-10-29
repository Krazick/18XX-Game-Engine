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

public class GeneratedRevenueEffect extends Effect {
	public final static String NAME = "Generated Revenue";
	final static AttributeName AN_REVENUE = new AttributeName ("revenue");
	final static AttributeName AN_TRAIN_COUNT = new AttributeName ("trainCount");
	int trainCount;
	int revenue;

	public GeneratedRevenueEffect () {
		this (NAME);
	}

	public GeneratedRevenueEffect (String aName) {
		super (aName);
	}

	public GeneratedRevenueEffect (ActorI aActor, int aRevenue, int aTrainCount) {
		super (NAME, aActor);
		setRevenue (aRevenue);
		setTrainCount (aTrainCount);
	}

	public GeneratedRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tRevenue, tTrainCount;

		tRevenue = aEffectNode.getThisIntAttribute (AN_REVENUE);
		tTrainCount = aEffectNode.getThisIntAttribute (AN_TRAIN_COUNT);
		setRevenue (tRevenue);
		setTrainCount (tTrainCount);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_REVENUE, getRevenue ());
		tEffectElement.setAttribute (AN_TRAIN_COUNT, getTrainCount ());

		return tEffectElement;
	}

	public void setRevenue (int aRevenue) {
		revenue = aRevenue;
	}

	public void setTrainCount (int aTrainCount) {
		trainCount = aTrainCount;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tTrainsUsed;

		tTrainsUsed = trainCount + " train";
		if (trainCount > 1) {
			tTrainsUsed += "s";
		}

		return (REPORT_PREFIX + name + " of " + Bank.formatCash (revenue) + " with " + tTrainsUsed + " for "
				+ getActorName () + ".");
	}

	public int getRevenue () {
		return revenue;
	}

	public int getTrainCount () {
		return trainCount;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tOperatingCompany;

		tEffectApplied = false;
		tOperatingCompany = (TrainCompany) aRoundManager.getOperatingCompany ();
		tOperatingCompany.setThisRevenue (revenue);
		tOperatingCompany.closeTrainRevenueFrame ();
		aRoundManager.updateAllCorporationsBox ();
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
