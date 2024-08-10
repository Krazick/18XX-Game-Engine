package ge18xx.round.action.effects;

import ge18xx.company.License;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class RemoveLicenseEffect extends AddLicenseEffect {
	public final static String SHORT_NAME = "Remove ";
	public final static String NAME = SHORT_NAME + "License";

	public RemoveLicenseEffect () {
		this (NAME);
	}

	public RemoveLicenseEffect (String aName) {
		super (aName);
		setToActor (ActorI.NO_ACTOR);
	}

	public RemoveLicenseEffect (ActorI aFromActor, ActorI aToActor, License aLicense) {
		super (aFromActor, aToActor, aLicense);
		setName (NAME);
	}

	public RemoveLicenseEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		setName (NAME);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		
		tReport = REPORT_PREFIX + SHORT_NAME + " " + license.getLicenseName () + " to " + getFromActorName () + ".";
		
		return (tReport);
	}
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tTrainCompany;
		
		tTrainCompany = getTrainCompany ();
		if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
			tTrainCompany.removeLicense (license);
			tEffectApplied = true;
		} else {
			tEffectApplied = false;
		}
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainCompany tTrainCompany;

		tTrainCompany = getTrainCompany ();
		if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
			tTrainCompany.addLicense (license);
			tEffectUndone = true;
		} else {
			tEffectUndone = false;
		}

		return tEffectUndone;
	}

}
