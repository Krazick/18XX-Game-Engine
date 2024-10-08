package ge18xx.game.variants;

import javax.swing.JComponent;

import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class PhaseInfoVEffect extends VariantEffect {
	public static final AttributeName AN_PHASE_NAME = new AttributeName ("phaseName");
	public static final String NAME = "Phase Info";
	String phaseName;

	public PhaseInfoVEffect () {
		setName (NAME);
	}

	public PhaseInfoVEffect (String aPhaseName) {
		setName (aPhaseName);
	}

	public PhaseInfoVEffect (XMLNode aXMLNode) {
		super (aXMLNode);

		String tPhaseName;

		tPhaseName = aXMLNode.getThisAttribute (AN_PHASE_NAME);
		setPhaseName (tPhaseName);
	}

	public String getPhaseName () {
		return phaseName;
	}

	public void setPhaseName (String aPhaseName) {
		phaseName = aPhaseName;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the PhaseName
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
		tXMLElement.setAttribute (AN_PHASE_NAME, phaseName);
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
		System.out.println (NAME + " being Applied");
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
