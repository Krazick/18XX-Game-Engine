package ge18xx.company;

import org.w3c.dom.NodeList;

import ge18xx.round.action.ActorI;
import ge18xx.round.action.GenericActor;
import ge18xx.train.Train;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PurchaseOffer {
	public static final PurchaseOffer NO_PURCHASE_OFFER = null;
	public static final ElementName EN_PURCHASE_OFFER = new ElementName ("PurchaseOffer");
	public static final AttributeName AN_ITEM_NAME = new AttributeName ("itemName");
	public static final AttributeName AN_ITEM_TYPE = new AttributeName ("itemType");
	public static final AttributeName AN_FROM_ACTOR_NAME = new AttributeName ("fromActorName");
	public static final AttributeName AN_TO_ACTOR_NAME = new AttributeName ("toActorName");
	public static final AttributeName AN_AMOUNT = new AttributeName ("amount");
	public static final AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	public static final AttributeName AN_PRIVATE_ABBREV = new AttributeName ("privateAbbrev");
	public static final AttributeName AN_OLD_STATUS = new AttributeName ("oldStatus");
	public static final AttributeName AN_STATUS = new AttributeName ("status");
	public static final String TRAIN_TYPE = Train.TYPE_NAME;
	public static final String PRIVATE_TYPE = Corporation.PRIVATE_COMPANY;
	public static final String NONE = "None";
	public static final String ACCEPTED = "Accepted";
	public static final String PENDING = "Pending";
	public static final String REJECTED = "Rejected";
	public static final String PROCESSED = "Processed";
	String itemName;
	String itemType;
	String fromActorName;
	String toActorName;
	int amount;
	Train train;
	PrivateCompany privateCompany;
	ActorI.ActionStates oldStatus;
	// TODO Convert to an ENUM with options on Status.
	String status;

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
		setStatus (PENDING);
	}

	public PurchaseOffer (XMLNode aChildNode) {
		XMLNode tPONode;
		NodeList tPurchaseOfferList;
		int tPOCount, tPOIndex;
		String tItemName;
		String tItemType;
		String tFromActorName;
		String tToActorName;
		int tAmount;
		String tOldStateName;
		ActorI.ActionStates tOldState;	
		GenericActor tGenericActor;
		String tStatus;
		String tTrainName;
		String tPrivateAbbrev;

		tPurchaseOfferList = aChildNode.getChildNodes ();
		tPOCount = tPurchaseOfferList.getLength ();
		for (tPOIndex = 0; tPOIndex < tPOCount; tPOIndex++) {
			tPONode = new XMLNode (tPurchaseOfferList.item (tPOIndex));
			tItemName = tPONode.getThisAttribute (AN_ITEM_NAME);
			tItemType = tPONode.getThisAttribute (AN_ITEM_TYPE);
			tFromActorName = tPONode.getThisAttribute (AN_FROM_ACTOR_NAME);
			tToActorName = tPONode.getThisAttribute (AN_TO_ACTOR_NAME);
			tAmount = tPONode.getThisIntAttribute (AN_AMOUNT);
			tOldStateName = tPONode.getThisAttribute (AN_OLD_STATUS);
			tStatus = tPONode.getThisAttribute (AN_STATUS);
			tTrainName = tPONode.getThisAttribute (AN_TRAIN_NAME);
			tPrivateAbbrev = tPONode.getThisAttribute (AN_PRIVATE_ABBREV);
			setItemName (tItemName);
			setItemType (tItemType);
			setFromActorName (tFromActorName);
			setToName (tToActorName);
			setAmount (tAmount);
			
			tGenericActor = new GenericActor ();
			tOldState = tGenericActor.getCorporationActionState (tOldStateName);
			setOldState (tOldState);
			setStatus (tStatus);
			System.out.println ("Train Name [" + tTrainName + "] Private Abbrev [" + tPrivateAbbrev + "]");
		}
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		return getElements (aXMLDocument, EN_PURCHASE_OFFER);
	}

	public XMLElement getElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (aElementName);
		tXMLElement.setAttribute (AN_ITEM_NAME, itemName);
		tXMLElement.setAttribute (AN_ITEM_TYPE, itemType);
		tXMLElement.setAttribute (AN_ITEM_NAME, fromActorName);
		tXMLElement.setAttribute (AN_FROM_ACTOR_NAME, fromActorName);
		tXMLElement.setAttribute (AN_TO_ACTOR_NAME, toActorName);
		tXMLElement.setAttribute (AN_AMOUNT, amount);
		if (train != Train.NO_TRAIN) {
			tXMLElement.setAttribute (AN_TRAIN_NAME, train.getName ());
		}
		if (privateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
			tXMLElement.setAttribute (AN_PRIVATE_ABBREV, privateCompany.getAbbrev ());
			// TODO -- When loading need to get the Private Company for the given Name, and attach to the Purchase Offer
		}
		tXMLElement.setAttribute (AN_OLD_STATUS, oldStatus.toString ());
		tXMLElement.setAttribute (AN_STATUS, status);

		return tXMLElement;
	}

	public void setStatus (String aStatus) {
		status = aStatus;
	}
	
	public boolean wasAccepted () {
		boolean tWasAccepted;
	
		tWasAccepted = false;
		if (status.equals (ACCEPTED)) {
			tWasAccepted = true;
		}
		
		return tWasAccepted;
	}
	
	public boolean wasRejected () {
		boolean tWasRejected;
	
		tWasRejected = false;
		if (status.equals (REJECTED)) {
			tWasRejected = true;
		}
		
		return tWasRejected;
	}
	
	public boolean isPending () {
		boolean tIsPending;
		
		tIsPending = false;
		if (status.equals (PENDING)) {
			tIsPending = true;
		}
		
		return tIsPending;
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
		toActorName = aToName;
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
		return toActorName;
	}

	public int getAmount () {
		return amount;
	}

	public ActorI.ActionStates getOldStatus () {
		return oldStatus;
	}
}
