package ge18xx.game.variants;

import javax.swing.JComponent;

import org.w3c.dom.NodeList;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.train.TrainInfo;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class AddTrainVEffect extends VariantEffect {
	public static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	public static final String NAME = "Add Train";
	int quantity;
	TrainInfo trainInfo;

	public AddTrainVEffect () {
		setName (NAME);
	}

	public AddTrainVEffect (XMLNode aXMLNode) {
		super (aXMLNode);

		int tQuantity;

		tQuantity = aXMLNode.getThisIntAttribute (AN_QUANTITY);
		setQuantity (tQuantity);
		loadTrainInfo (aXMLNode);
	}

	public int getQuantity () {
		return quantity;
	}

	public TrainInfo getTrainInfo () {
		return trainInfo;
	}

	public void setQuantity (int aQuantity) {
		quantity = aQuantity;
	}

	public void setTrainInfo (TrainInfo aTrainInfo) {
		trainInfo = aTrainInfo;
	}

	public void loadTrainInfo (XMLNode aAddTrainVEffectNode) {
		String tChildName;
		XMLNode tChildNode;
		NodeList tChildren;
		int tIndex;
		int tChildrenCount;
		TrainInfo tTrainInfo;

		tChildren = aAddTrainVEffectNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (TrainInfo.EN_TRAIN_INFO.equals (tChildName)) {
				tTrainInfo = new TrainInfo (tChildNode);
				if (tTrainInfo != TrainInfo.NO_TRAIN_INFO) {
					setTrainInfo (tTrainInfo);
				}
			}
		}
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the TrainInfo, and the Quantity
	 *
	 * @param aXMLDocument The XMLDocument to use to create the XMLElement
	 *
	 * @return the filled out XMLElement
	 *
	 */
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tTrainInfoXMLElement;

		tXMLElement = super.getEffectElement (aXMLDocument);
		if (quantity != NO_QUANTITY) {
			tXMLElement.setAttribute (AN_QUANTITY, getQuantity ());
		}
		if (trainInfo != TrainInfo.NO_TRAIN_INFO) {
			tTrainInfoXMLElement = trainInfo.getTrainInfoElement (aXMLDocument);
			tXMLElement.appendChild (tTrainInfoXMLElement);
		}
		tXMLElement.setAttribute (AN_CLASS, getClass ().getName ());

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
		Bank tBank;

		tBank = aGameManager.getBank ();
		tBank.loadTrains (quantity, trainInfo);
	}

	/**
	 * Variant Effect Component Builder -- this should be overridden by the subclasses
	 *
	 * @param aComponentType Placeholder for the Item Listener class that will handle the request
	 * @return the Component that was built
	 *
	 */
	@Override
	public JComponent buildEffectComponent (VariantEffect.ComponentType aComponentType) {
		JComponent tEffectComponent;

		tEffectComponent = buildEffectJLabel () ;

		return tEffectComponent;
	}
}
