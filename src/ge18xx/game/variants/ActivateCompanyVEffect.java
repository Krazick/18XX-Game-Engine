package ge18xx.game.variants;

import javax.swing.JComponent;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class ActivateCompanyVEffect extends VariantEffect {
	static final String NAME = "Activate Company";
	static final AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	String companyAbbrev;
	
	public ActivateCompanyVEffect () {
		setName (NAME);
	}

	public ActivateCompanyVEffect (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tCompanyAbbrev;
		
		tCompanyAbbrev = aXMLNode.getThisAttribute (AN_QUANTITY);
		setCompanyAbbrev (tCompanyAbbrev);
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}
	
	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
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
		System.out.println ("Activate Company " + companyAbbrev + " in the " + tBank.getName ());
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

		tEffectComponent = buildEffectCheckBox ();
		
		return tEffectComponent;
	}
}
