package ge18xx.game.variants;

import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class AddBankCashVEffect extends VariantEffect {
	static final AttributeName AN_ACTOR_NAME = new AttributeName ("actorName");
	static final String NAME = "Add to Bank Cash";
	static final String ACTOR_NAME = "Bank";
	int quantity;
	String actorName;
	
	public AddBankCashVEffect () {
		setName (NAME);
	}

	public AddBankCashVEffect (XMLNode aCellNode) {
		super (aCellNode);
		
		int tQuantity;
		
		tQuantity = aCellNode.getThisIntAttribute (AN_QUANTITY);
		
		setActorName (ACTOR_NAME);
		setQuantity (tQuantity);
	}

	public String getActorName () {
		return actorName;
	}
	
	public int getQuantity () {
		return quantity;
	}
	
	public void setActorName (String aActorName) {
		actorName = aActorName;
	}
	
	public void setQuantity (int aQuantity) {
		quantity = aQuantity;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_VARIANT_EFFECT);
		tXMLElement.setAttribute (AN_NAME, name);
		if (quantity != NO_QUANTITY) {
			tXMLElement.setAttribute (AN_QUANTITY, getQuantity ());
		}
		if (actorName != NO_NAME) {
			tXMLElement.setAttribute (AN_ACTOR_NAME, getActorName ());
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
		Bank tBank;
		
		tBank = aGameManager.getBank ();
		tBank.addCash (getQuantity ());
	}
	
	/**
	 * Variant Effect Component Builder -- this should be overriden by the subclasses
	 * 
	 * @param aItemListener Placeholder for the Item Listener class that will handle the request
	 * @return from this case NO_VARIANT_COMPONENT
	 * 
	 */
	@Override
	public JComponent buildEffectComponent (VariantEffect.ComponentType aComponentType, ItemListener aItemListener) {
		JLabel tComponentLabel;
		
		tComponentLabel = buildEffectJLabel (aItemListener);

		return tComponentLabel;	
	}
}
