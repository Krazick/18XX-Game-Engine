package ge18xx.company;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.train.Train;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class PurchaseTrainOffer extends QueryOffer {
	public static final ElementName EN_PURCHASE_OFFER = new ElementName ("PurchaseOffer");
	public static final AttributeName AN_AMOUNT = new AttributeName ("amount");
	public static final AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	public static final String TRAIN_TYPE = Train.TYPE_NAME;

	int amount;
	Train train;
	String trainName;

	public PurchaseTrainOffer (String aItemName, Train aTrain, String aFromActorName, String aToActorName,
					int aAmount, ActionStates aOldState) {
		super (aItemName, aFromActorName, aToActorName, aOldState);
		setTrain (aTrain);
		setAmount (aAmount);
	}

	public PurchaseTrainOffer (XMLNode aChildNode, GameManager aGameManager) {
		super (aChildNode, aGameManager);

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
		tXMLElement.setAttribute (AN_CLASS_NAME, this.getClass ().getName ());

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

	public Coupon getTrain () {
		return train;
	}

	public int getAmount () {
		return amount;
	}

	@Override
	public String getItemType () {
		return train.getType ();
	}
}
