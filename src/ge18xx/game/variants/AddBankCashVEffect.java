package ge18xx.game.variants;

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

	public int getQuantity () {
		return quantity;
	}

	public void setQuantity (int aQuantity) {
		quantity = aQuantity;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the Actor Name, and the Quantity
	 *
	 * @param aXMLDocument The XMLDocumdnt to use to create the XMLElement
	 *
	 * @return the filled out XMLElement
	 *
	 */
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = super.getEffectElement (aXMLDocument);
		if (quantity != NO_QUANTITY) {
			tXMLElement.setAttribute (AN_QUANTITY, getQuantity ());
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
	public JComponent buildEffectComponent (VariantEffect.ComponentType aComponentType) {
		JLabel tComponentLabel;

		tComponentLabel = buildEffectJLabel ();

		return tComponentLabel;
	}
}
