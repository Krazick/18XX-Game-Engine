package ge18xx.game.variants;

import javax.swing.JComponent;

import org.w3c.dom.NodeList;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.train.TrainInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

public class AddTrainVEffect extends VariantEffect {
	static final ElementName EN_TRAIN = new ElementName ("Train");
	static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	static final String NAME = "Add Train";
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
			if (EN_TRAIN.equals (tChildName)) {
				tTrainInfo = new TrainInfo (tChildNode);
				if (tTrainInfo != TrainInfo.NO_TRAIN_INFO) {
					setTrainInfo (tTrainInfo);
				}
			}
		}
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
	 * Variant Effect Component Builder -- this should be overriden by the subclasses
	 * 
	 * @param aItemListener Placeholder for the Item Listener class that will handle the request
	 * @return 
	 * 
	 */
	@Override
	public JComponent buildEffectComponent (VariantEffect.ComponentType aComponentType) {
		JComponent tEffectComponent;

		tEffectComponent = buildEffectJLabel () ;
		
		return tEffectComponent;
	}
}
