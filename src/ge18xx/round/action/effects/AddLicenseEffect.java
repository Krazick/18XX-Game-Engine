package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.License;
import ge18xx.company.PortLicense;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class AddLicenseEffect extends ToEffect {
	public final static String SHORT_NAME = "Add ";
	public final static String NAME = SHORT_NAME + "License";
	final static AttributeName AN_LICENSE = new AttributeName ("License");
	License license;
	
	public AddLicenseEffect () {
		this (NAME);
	}

	public AddLicenseEffect (String aName) {
		super (aName);
		setToActor (ActorI.NO_ACTOR);
	}

	public AddLicenseEffect (ActorI aFromActor, ActorI aToActor, License aLicense) {
		super (NAME, aFromActor, aToActor);
		setLicense (aLicense);
		setName (NAME);
	}

	public AddLicenseEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		setName (NAME);
		int tBenefitValue;
		int tLicensePrice;
		String tLicenseName;
		boolean tIsPortLicense;
		PortLicense tPortLicense;
		License tOtherLicense;
		
		tBenefitValue = aEffectNode.getThisIntAttribute (License.AN_BENEFIT_VALUE);
		tLicenseName = aEffectNode.getThisAttribute (License.AN_LICENSE_NAME);
		tIsPortLicense = aEffectNode.getThisBooleanAttribute (PortLicense.AN_PORT_LICENSE);
		tLicensePrice = aEffectNode.getThisIntAttribute (License.AN_LICENSE_PRICE);
		if (tIsPortLicense) {
			tPortLicense = new PortLicense (tLicenseName, tBenefitValue);
			setLicense (tPortLicense);
		} else {
			tOtherLicense = new License (tLicenseName, tLicensePrice, tBenefitValue);
			setLicense (tOtherLicense);
		}
	}

	public void setLicense (License aLicense) {
		license = aLicense;
	}
	
	public License getLicense () {
		return license;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (License.AN_BENEFIT_VALUE, license.getBenefitValue ());
		tEffectElement.setAttribute (License.AN_LICENSE_NAME, license.getName ());
		tEffectElement.setAttribute (PortLicense.AN_PORT_LICENSE, license.isPortLicense ());
		tEffectElement.setAttribute (License.AN_LICENSE_PRICE, license.getPrice ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		int tPrice;
		
		tReport = REPORT_PREFIX + SHORT_NAME + " " + license.getLicenseName () + " to " + getToActorName ();
		tPrice = license.getPrice ();
		if (tPrice > 0) {
			tReport += " for " +  Bank.formatCash (license.getPrice ());
		}
		tReport += ".";
		
		return (tReport);
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tTrainCompany;
		
		tEffectApplied = false;
		tTrainCompany = getTrainCompany ();
		if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
			tTrainCompany.addLicense (license);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The Actor to apply the effect to is NOT a Train Company");
		}
		
		return tEffectApplied;
	}

	public TrainCompany getTrainCompany () {
		ActorI tToActor;
		TrainCompany tTrainCompany;
		
		tToActor = getToActor ();
		if (tToActor.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) tToActor;
		} else {
			tTrainCompany = TrainCompany.NO_TRAIN_COMPANY;
		}
		
		return tTrainCompany;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainCompany tTrainCompany;

		tEffectUndone = false;
		tTrainCompany = getTrainCompany ();
		if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
			tTrainCompany.removeLicense (license);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("The Actor to undo the effect from is NOT a Train Company");
		}

		return tEffectUndone;
	}
}
