package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class GeneratedRevenueEffect extends Effect {
	public final static String NAME = "Set Generated Revenue";
	final static AttributeName AN_REVENUE = new AttributeName ("revenue");
	final static AttributeName AN_PREVIOUS_REVENUE = new AttributeName ("previousRevenue");
	final static AttributeName AN_TRAIN_COUNT = new AttributeName ("trainCount");
	int trainCount;
	int revenue;
	int previousRevenue;
	int corporationID;

	public GeneratedRevenueEffect () {
		this (NAME);
	}

	public GeneratedRevenueEffect (String aName) {
		super (aName);
	}

	public GeneratedRevenueEffect (ActorI aActor, int aRevenue, int aTrainCount, int aPreviousRevenue) {
		super (NAME, aActor);
		
		int tCorporationID;
		Corporation tCorporation;
		
		if (aActor.isACorporation ()) {
			tCorporation = (Corporation) aActor;
			tCorporationID = tCorporation.getID ();
			setCorporationID (tCorporationID);
		}
		setRevenue (aRevenue);
		setPreviousRevenue (aPreviousRevenue);
		setTrainCount (aTrainCount);
	}

	public GeneratedRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tRevenue;
		int tPreviousRevenue;
		int tTrainCount;
		int tCorporationID;

		tRevenue = aEffectNode.getThisIntAttribute (AN_REVENUE);
		tPreviousRevenue = aEffectNode.getThisIntAttribute (AN_PREVIOUS_REVENUE);
		tTrainCount = aEffectNode.getThisIntAttribute (AN_TRAIN_COUNT);
		tCorporationID = aEffectNode.getThisIntAttribute (Corporation.AN_ID);
		setRevenue (tRevenue);
		setPreviousRevenue (tPreviousRevenue);
		setTrainCount (tTrainCount);
		setCorporationID (tCorporationID);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_REVENUE, getRevenue ());
		tEffectElement.setAttribute (AN_PREVIOUS_REVENUE, getPreviousRevenue ());
		tEffectElement.setAttribute (AN_TRAIN_COUNT, getTrainCount ());
		tEffectElement.setAttribute (Corporation.AN_ID, getCorporationID ());

		return tEffectElement;
	}
	
	public void setCorporationID (int aCorporationID) {
		corporationID = aCorporationID;
	}

	public void setRevenue (int aRevenue) {
		revenue = aRevenue;
	}

	public void setPreviousRevenue (int aPriorRevenue) {
		previousRevenue = aPriorRevenue;
	}

	public void setTrainCount (int aTrainCount) {
		trainCount = aTrainCount;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tTrainsUsed;
		String tFormattedRevenue;
		String tActorNameAbbrev;
		Corporation tCorporation;

		tTrainsUsed = trainCount + " train";
		if ((trainCount > 1) || (trainCount == 0)) {
			tTrainsUsed += "s";
		}
			
		if (actor.isACorporation ()) {
			tCorporation = (Corporation) getActor ();
			tActorNameAbbrev = tCorporation.getAbbrev ();
		} else {
			tActorNameAbbrev = getActorName ();
		}

		if (revenue > 0) {
			tFormattedRevenue = Bank.formatCash (revenue);
		} else {
			tFormattedRevenue = "NO REVENUE";
		}
		
		return (REPORT_PREFIX + name + " of " + tFormattedRevenue + " with " + tTrainsUsed + " for "
				+ tActorNameAbbrev + " (Corp ID: " + getCorporationID () + ").");
	}

	public int getCorporationID () {
		return corporationID;
	}
	
	public int getRevenue () {
		return revenue;
	}

	public int getPreviousRevenue () {
		return previousRevenue;
	}

	public int getTrainCount () {
		return trainCount;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tOperatingCompany;

		tEffectApplied = false;
		tOperatingCompany = (TrainCompany) aRoundManager.getCompanyByID (corporationID);
		tOperatingCompany.setThisRevenue (revenue);
		tOperatingCompany.setPreviousRevenue (previousRevenue);
		tOperatingCompany.closeTrainRevenueFrame ();
		aRoundManager.updateAllCorporationsBox ();
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainCompany tOperatingCompany;
		
		tEffectUndone = true;
		tOperatingCompany = (TrainCompany) aRoundManager.getCompanyByID (corporationID);
		if (tOperatingCompany == Corporation.NO_CORPORATION) {
			tOperatingCompany = (TrainCompany) aRoundManager.getOperatingCompany ();
		}
		tOperatingCompany.setThisRevenue (previousRevenue);
		
		return tEffectUndone;
	}
}
