package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ChangeTrainStatusEffect extends Effect {
	public final static String NAME = "Train Status Change";
	final static AttributeName AN_TRAIN_ORDER = new AttributeName ("trainOrder");
	final static AttributeName AN_OLD_STATUS = new AttributeName ("oldStatus");
	final static AttributeName AN_NEW_STATUS = new AttributeName ("newStatus");
	final static AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	String trainName;
	int trainOrder;
	int oldTrainStatus;
	int newTrainStatus;

	public ChangeTrainStatusEffect () {
		setName (NAME);
		setTrainName (Train.NO_TRAIN_NAME);
		setTrainOrder (Train.NO_ORDER);
		setOldStatus (Train.NO_TRAIN_STATUS);
		setNewStatus (Train.NO_TRAIN_STATUS);
	}

	public ChangeTrainStatusEffect (ActorI aActor, String aTrainName, int aTrainOrder, int aOldTrainStatus,
			int aNewTrainStatus) {
		super (NAME, aActor);
		setTrainName (aTrainName);
		setTrainOrder (aTrainOrder);
		setOldStatus (aOldTrainStatus);
		setNewStatus (aNewTrainStatus);
	}

	public ChangeTrainStatusEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		int tTrainOrder, tOldTrainStatus, tNewTrainStatus;
		String tTrainName;

		tTrainName = aEffectNode.getThisAttribute (AN_TRAIN_NAME);
		tTrainOrder = aEffectNode.getThisIntAttribute (AN_TRAIN_ORDER);
		tOldTrainStatus = aEffectNode.getThisIntAttribute (AN_OLD_STATUS);
		tNewTrainStatus = aEffectNode.getThisIntAttribute (AN_NEW_STATUS);

		setTrainName (tTrainName);
		setTrainOrder (tTrainOrder);
		setOldStatus (tOldTrainStatus);
		setNewStatus (tNewTrainStatus);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_TRAIN_NAME, trainName);
		tEffectElement.setAttribute (AN_TRAIN_ORDER, trainOrder);
		tEffectElement.setAttribute (AN_OLD_STATUS, oldTrainStatus);
		tEffectElement.setAttribute (AN_NEW_STATUS, newTrainStatus);

		return tEffectElement;
	}

	public String getTrainName () {
		return trainName;
	}

	public int getTrainOrder () {
		return trainOrder;
	}

	public int getNewTrainStatus () {
		return newTrainStatus;
	}

	public int getOldTrainStatus () {
		return oldTrainStatus;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " by " + actor.getName () + " for Trains Named " + trainName + " of Order "
				+ trainOrder + " from [" + Train.getNameOfStatus (oldTrainStatus) + "] to ["
				+ Train.getNameOfStatus (newTrainStatus) + "].");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setTrainName (String aTrainName) {
		trainName = aTrainName;
	}

	public void setTrainOrder (int aTrainOrder) {
		trainOrder = aTrainOrder;
	}

	public void setOldStatus (int aOldStatus) {
		oldTrainStatus = aOldStatus;
	}

	public void setNewStatus (int aNewStatus) {
		newTrainStatus = aNewStatus;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainPortfolio tTrainPortfolio;
		Bank tBank;

		tEffectUndone = false;

		tBank = aRoundManager.getBank ();
		tTrainPortfolio = tBank.getTrainPortfolio ();
		tTrainPortfolio.setTrainsStatus (trainOrder, newTrainStatus);
		tEffectUndone = true;

		return tEffectUndone;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainPortfolio tTrainPortfolio;
		Bank tBank;

		tEffectUndone = false;

		tBank = aRoundManager.getBank ();
		tTrainPortfolio = tBank.getTrainPortfolio ();
		tTrainPortfolio.setTrainsStatus (trainOrder, oldTrainStatus);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
