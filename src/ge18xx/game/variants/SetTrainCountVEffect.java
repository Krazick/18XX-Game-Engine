package ge18xx.game.variants;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.train.Train;
import ge18xx.train.TrainInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class SetTrainCountVEffect extends VariantEffect {
	static final AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	static final String NAME = "Set Train Count";
	String trainName;
	int quantity;
	
	public SetTrainCountVEffect () {
		setName (NAME);
	}

	public SetTrainCountVEffect (XMLNode aCellNode) {
		super (aCellNode);
		
		String tTrainName;
		int tQuantity;
		
		tTrainName = aCellNode.getThisAttribute (AN_TRAIN_NAME);
		tQuantity = aCellNode.getThisIntAttribute (AN_QUANTITY);
		setTrainName (tTrainName);
		setQuantity (tQuantity);
	}

	public int getQuantity () {
		return quantity;
	}
	
	public String getTrainName () {
		return trainName;
	}
	
	public void setQuantity (int aQuantity) {
		quantity = aQuantity;
	}
	
	public void setTrainName (String aTrainName) {
		trainName = aTrainName;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tTrainElement;

		tXMLElement = aXMLDocument.createElement (EN_VARIANT_EFFECT);
		tXMLElement.setAttribute (AN_NAME, name);
		if (trainName != null) {
			tXMLElement.setAttribute (AN_TRAIN_NAME, actorName);
		}
		if (quantity != NO_QUANTITY) {
			tXMLElement.setAttribute (AN_QUANTITY, quantity);
		}
		if (trainInfo != TrainInfo.NO_TRAIN_INFO) {
			tTrainElement = trainInfo.getTrainInfoElement (aXMLDocument);
			tXMLElement.appendChild (tTrainElement);
		}

		return tXMLElement;
	}

	/**
	 * Apply the Variant Effect using the Game Manager as needed.
	 * 
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 * 
	 */
	@Override
	public void applyVariantEffect (GameManager aGameManager) {
		Train tTrain;
		Train tNewTrain;
		Bank tBank;
		int tFoundQuantity;
		int tAddThisMany;
		int tTrainIndex;
		int tRemoveThisMany;
		
		System.out.println ("Setting Train Quanity in Subclass");
		tBank = aGameManager.getBank ();
		tTrain = tBank.getTrain (trainName);
		if (quantity == TrainInfo.UNLIMITED_TRAINS) {
			tTrain.setUnlimitedQuantity ();
		} else {
			tFoundQuantity = tBank.getTrainQuantity (trainName);
			// TODO If Found Quantity is Zero, must create a NEW Train using the info in the child of this Effect
			if (quantity > tFoundQuantity) {
				tAddThisMany = quantity - tFoundQuantity;
				for (tTrainIndex = 0; tTrainIndex < tAddThisMany; tTrainIndex++) {
					System.out.println ("Train " + tTrain.getName () + " adding " + tAddThisMany);
					tNewTrain = new Train (tTrain);
					tBank.addTrain (tNewTrain);
				}
			} else if (quantity < tFoundQuantity) {
				tRemoveThisMany = tFoundQuantity - quantity;
				for (tTrainIndex = 0; tTrainIndex < tRemoveThisMany; tTrainIndex++) {
					tBank.removeTrain (trainName);
				}
			}
		}
	}

}
