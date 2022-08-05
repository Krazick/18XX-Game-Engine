package ge18xx.company;

import org.w3c.dom.NodeList;

import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.train.Train;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PurchaseOffer extends QueryOffer {
	public static final AttributeName AN_AMOUNT = new AttributeName ("amount");
	public static final AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	public static final ElementName EN_PURCHASE_OFFER = new ElementName ("PurchaseOffer");

	int amount;
	Train train;
	String trainName;

	public PurchaseOffer (String aItemName, String aItemType, Train aTrain, PrivateCompany aPrivateCompany,
			String aFromActorName, String aToName, int aAmount, ActionStates aOldState) {
		super (aItemName, aItemType, aPrivateCompany, aFromActorName, aToName, aOldState);
		if (TRAIN_TYPE.equals (aItemType)) {
			setTrain (aTrain);
		}
		setAmount (aAmount);
	}

	public PurchaseOffer (XMLNode aChildNode) {
		super (aChildNode);
		
		XMLNode tPONode;
		NodeList tPurchaseOfferList;
		int tPOCount, tPOIndex;
		int tAmount;
		String tTrainName;
		
		tPurchaseOfferList = aChildNode.getChildNodes ();
		tPOCount = tPurchaseOfferList.getLength ();
		for (tPOIndex = 0; tPOIndex < tPOCount; tPOIndex++) {
			tPONode = new XMLNode (tPurchaseOfferList.item (tPOIndex));
			tAmount = tPONode.getThisIntAttribute (AN_AMOUNT);
			tTrainName = tPONode.getThisAttribute (AN_TRAIN_NAME);
			setAmount (tAmount);
			setTrainName (tTrainName);
			System.out.println ("Train Name [" + tTrainName + "] ");
		}
	}
	
	@Override
	public XMLElement getElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;

		tXMLElement = super.getElements (aXMLDocument, aElementName);
		tXMLElement.setAttribute (AN_AMOUNT, amount);
		if (train != Train.NO_TRAIN) {
			tXMLElement.setAttribute (AN_TRAIN_NAME, train.getName ());
		}

		return tXMLElement;
	}
	
	public String getTrainName () {
		return trainName;
	}
	
	private void setTrainName (String aTrainName) {
		trainName = aTrainName;
	}
	
	private void setTrain (Train aTrain) {
		train = aTrain;
	}

	private void setAmount (int aAmount) {
		amount = aAmount;
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

	public Train getTrain () {
		return train;
	}

	public int getAmount () {
		return amount;
	}
}
