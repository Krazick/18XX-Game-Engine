package ge18xx.company;

import ge18xx.round.action.ActorI;
import ge18xx.train.Train;

public class PurchaseOffer {
	public static final String TRAIN_TYPE = Train.TYPE_NAME;
	public static final String PRIVATE_TYPE = Corporation.PRIVATE_COMPANY;
	String itemName;
	String itemType;
	String fromActorName;
	String toName;
	int amount;
	Train train;
	PrivateCompany privateCompany;
	ActorI.ActionStates oldStatus;

	public PurchaseOffer (String aItemName, String aItemType, Train aTrain, PrivateCompany aPrivateCompany,
			String aFromActorName, String aToName, int aAmount, ActorI.ActionStates aOldState) {
		if (TRAIN_TYPE.equals (aItemType)) {
			setTrain (aTrain);
		} else if (PRIVATE_TYPE.equals (aItemType)) {
			setPrivateCompany (aPrivateCompany);
		} else {
			System.err.println ("The Type " + aItemType + " is not a " + TRAIN_TYPE + " or a " + PRIVATE_TYPE);
		}
		setItemName (aItemName);
		setItemType (aItemType);
		setFromActorName (aFromActorName);
		setToName (aToName);
		setAmount (aAmount);
		setOldState (aOldState);
	}

	private void setTrain (Train aTrain) {
		train = aTrain;
	}

	private void setPrivateCompany (PrivateCompany aPrivateCompany) {
		privateCompany = aPrivateCompany;
	}

	private void setItemName (String aItemName) {
		itemName = aItemName;
	}

	private void setItemType (String aItemType) {
		itemType = aItemType;
	}

	private void setFromActorName (String aFromActorName) {
		fromActorName = aFromActorName;
	}

	private void setToName (String aToName) {
		toName = aToName;
	}

	private void setAmount (int aAmount) {
		amount = aAmount;
	}

	private void setOldState (ActorI.ActionStates aOldState) {
		oldStatus = aOldState;
	}

	public boolean isTrain () {
		boolean tIsTrain = false;

		if (TRAIN_TYPE.equals (itemType)) {
			tIsTrain = true;
		} else {
			tIsTrain = false;
		}

		return tIsTrain;
	}

	public boolean isPrivateCompany () {
		boolean tIsPrivateCompany = false;

		if (PRIVATE_TYPE.equals (itemType)) {
			tIsPrivateCompany = true;
		} else {
			tIsPrivateCompany = false;
		}

		return tIsPrivateCompany;
	}

	public Train getTrain () {
		return train;
	}

	public PrivateCompany getPrivateCompany () {
		return privateCompany;
	}

	public String getItemName () {
		return itemName;
	}

	public String getItemType () {
		return itemType;
	}

	public String getFromActorName () {
		return fromActorName;
	}

	public String getToName () {
		return toName;
	}

	public int getAmount () {
		return amount;
	}

	public ActorI.ActionStates getOldStatus () {
		return oldStatus;
	}
}
