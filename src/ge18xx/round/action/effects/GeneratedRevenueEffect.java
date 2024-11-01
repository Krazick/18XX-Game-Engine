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
	final static AttributeName AN_THIS_REVENUE = new AttributeName ("thisRevenue");
	final static AttributeName AN_OLD_THIS_REVENUE = new AttributeName ("oldThisRevenue");
	final static AttributeName AN_TRAIN_COUNT = new AttributeName ("trainCount");
	int trainCount;
	int thisRevenue;
	int oldThisRevenue;
	int corporationID;

	public GeneratedRevenueEffect () {
		this (NAME);
	}

	public GeneratedRevenueEffect (String aName) {
		super (aName);
	}

	public GeneratedRevenueEffect (ActorI aActor, int aOldThisRevenue, int aThisRevenue, int aTrainCount) {
		super (NAME, aActor);
		
		int tCorporationID;
		Corporation tCorporation;
		
		if (aActor.isACorporation ()) {
			tCorporation = (Corporation) aActor;
			tCorporationID = tCorporation.getID ();
			setCorporationID (tCorporationID);
		}
		setThisRevenue (aThisRevenue);
		setOldThisRevenue (aOldThisRevenue);
		setTrainCount (aTrainCount);
	}

	public GeneratedRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tThisRevenue;
		int tOldThisRevenue;
		int tTrainCount;
		int tCorporationID;

		tThisRevenue = aEffectNode.getThisIntAttribute (AN_THIS_REVENUE);
		tOldThisRevenue = aEffectNode.getThisIntAttribute (AN_OLD_THIS_REVENUE);
		tTrainCount = aEffectNode.getThisIntAttribute (AN_TRAIN_COUNT);
		tCorporationID = aEffectNode.getThisIntAttribute (Corporation.AN_ID);
		setThisRevenue (tThisRevenue);
		setOldThisRevenue (tOldThisRevenue);
		setTrainCount (tTrainCount);
		setCorporationID (tCorporationID);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_THIS_REVENUE, getThisRevenue ());
		tEffectElement.setAttribute (AN_OLD_THIS_REVENUE, getOldThisRevenue ());
		tEffectElement.setAttribute (AN_TRAIN_COUNT, getTrainCount ());
		tEffectElement.setAttribute (Corporation.AN_ID, getCorporationID ());

		return tEffectElement;
	}
	
	public void setCorporationID (int aCorporationID) {
		corporationID = aCorporationID;
	}

	public void setThisRevenue (int aThisRevenue) {
		thisRevenue = aThisRevenue;
	}

	public void setOldThisRevenue (int aOldThisRevenue) {
		oldThisRevenue = aOldThisRevenue;
	}

	public void setTrainCount (int aTrainCount) {
		trainCount = aTrainCount;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tTrainsUsed;
		String tFormattedThisRevenue;
		String tFormattedOldThisRevenue;
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

		if (oldThisRevenue > 0) {
			tFormattedOldThisRevenue = Bank.formatCash (oldThisRevenue);
		} else {
			tFormattedOldThisRevenue = "NO REVENUE";
		}
		if (thisRevenue > 0) {
			tFormattedThisRevenue = Bank.formatCash (thisRevenue);
		} else {
			tFormattedThisRevenue = "NO REVENUE";
		}
		
		return (REPORT_PREFIX + name + " of " + tFormattedThisRevenue + " with " + tTrainsUsed + " for "
				+ tActorNameAbbrev + " (Corp ID: " + getCorporationID () + "). Previous Revenue "
				+ tFormattedOldThisRevenue);
	}

	public int getCorporationID () {
		return corporationID;
	}
	
	public int getThisRevenue () {
		return thisRevenue;
	}

	public int getOldThisRevenue () {
		return oldThisRevenue;
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
		tOperatingCompany.setThisRevenue (thisRevenue);
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
		tOperatingCompany.setThisRevenue (oldThisRevenue);
		
		return tEffectUndone;
	}
}
