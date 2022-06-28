package ge18xx.game.variants;

import javax.swing.JComponent;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class RemovePhaseVEffect extends VariantEffect {
	static final AttributeName AN_PHASE_NAME = new AttributeName ("phaseName");
	static final String NAME = "Remove Phase";
	String phaseName;
	
	public RemovePhaseVEffect () {
		setName (NAME);
	}

	public RemovePhaseVEffect (XMLNode aXMLNode) {
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
	 * Apply the Variant Effect using the Game Manager as needed.
	 * 
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 * 
	 */
	@Override
	public void applyVariantEffect (GameManager aGameManager) {
		System.out.println ("Remove Phase being Applied");
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
