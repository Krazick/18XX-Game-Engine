package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class RustTrainEffect extends TransferTrainEffect {
	final static AttributeName AN_TRAIN_STATUS = new AttributeName ("trainStatus");
	public final static String NAME = "Rust Train";
	int oldTrainStatus;

	public RustTrainEffect () {
		super ();
		setName (NAME);
	}

	public RustTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor, int aTrainStatus) {
		super (aFromActor, aTrain, aToActor);
		setName (NAME);
		setOldTrainStatus (aTrainStatus);
	}

	public RustTrainEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tTrainStatus;

		tTrainStatus = aEffectNode.getThisIntAttribute (AN_TRAIN_STATUS);
		setName (NAME);
		setOldTrainStatus (tTrainStatus);
	}

	/**
	 * This method will verify that the Train Object to be rusted has been found after Parsing the Action
	 * That includes rusting Trains. If the Train Object has not been found seek it from the provided Actor.
	 * This Actor should be a Train Company. It will be used during a Train Upgrade, that is rusted by the Upgrade.
	 *
	 */

	@Override
	public void postParse (ActorI aActor) {
		TrainCompany tTrainCompany;
		Train tTrain;

		if (train == Train.NO_TRAIN) {
			if (aActor.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) aActor;
				tTrain = tTrainCompany.getTrain (trainName);
				if (tTrain != Train.NO_TRAIN) {
					setTrain (tTrain);
				} else {
					System.err.println ("Could not find the " + trainName +
							" Train in the Train Portfolio within the " + tTrainCompany.getName () + " Train Company.");
				}
			} else {
				System.err.println ("The Actor " + aActor.getName () + " is not A Train Company, so doesn't have a Train Portfolio.");
			}
		}

	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_TRAIN_STATUS, oldTrainStatus);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tTrainName, tActorName, tToActorName;

		if (train != Train.NO_TRAIN) {
			tTrainName = train.getName ();
		} else {
			tTrainName = "???";
		}
		tActorName = actor.getName ();
		tToActorName = toActor.getName ();

		return (REPORT_PREFIX + name + " named " + tTrainName + " from " + tActorName
				+ " placing it into the Rusted Portfolio held by the " + tToActorName + ".");
	}

	public int getOldTrainStatus () {
		return oldTrainStatus;
	}

	public void setOldTrainStatus (int aOldTrainStatus) {
		oldTrainStatus = aOldTrainStatus;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		Bank tBank;
		boolean tEffectApplied = false;
		TrainPortfolio tToTrainPortfolio;
		TrainHolderI tFromHolder;

		tBank = (Bank) toActor;
		tToTrainPortfolio = tBank.getRustedTrainPortfolio ();
		train.setStatus (Train.RUSTED);
		tToTrainPortfolio.addTrain (train);
		tFromHolder = (TrainHolderI) actor;
		tFromHolder.removeTrain (getTrainName ());
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainHolderI tToHolder, tFromHolder;
		TrainPortfolio tToTrainPortfolio;

		tEffectUndone = false;
		tToHolder = (TrainHolderI) toActor;
		tToTrainPortfolio = tToHolder.getTrainPortfolio ();

		tToTrainPortfolio.removeTrain (train.getName ());
		train.setStatus (oldTrainStatus);
		tFromHolder = (TrainHolderI) actor;
		tFromHolder.addTrain (train);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
