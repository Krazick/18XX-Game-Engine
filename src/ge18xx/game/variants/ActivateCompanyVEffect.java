package ge18xx.game.variants;

import javax.swing.JComponent;

import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ActivateCompanyVEffect extends VariantEffect {
	static final String NAME = "Activate Company";
	static final AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	static final AttributeName AN_COMPANY_ID = new AttributeName ("companyID");
	String companyAbbrev;
	int companyID;

	public ActivateCompanyVEffect () {
		setName (NAME);
	}

	public ActivateCompanyVEffect (XMLNode aXMLNode) {
		super (aXMLNode);

		String tCompanyAbbrev;
		int tCompanyID;

		tCompanyAbbrev = aXMLNode.getThisAttribute (AN_COMPANY_ABBREV);
		tCompanyID = aXMLNode.getThisIntAttribute (AN_COMPANY_ID);
		setCompanyAbbrev (tCompanyAbbrev);
		setCompanyID (tCompanyID);
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	public int getCompanyID () {
		return companyID;
	}

	public void setCompanyID (int aCompanyID) {
		companyID = aCompanyID;
	}

	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the CompanyID, the CompanyAbbrev and the VariantEffect Class
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
		tXMLElement.setAttribute (AN_COMPANY_ID, companyID);
		tXMLElement.setAttribute (AN_COMPANY_ABBREV, companyAbbrev);
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
		CorporationList tCorporationList;
		boolean tActivated;

		tCorporationList = aGameManager.getPrivates ();
		tActivated = tCorporationList.activateCorporation (companyID);

		if (! tActivated) {
			tCorporationList = aGameManager.getMinorCompanies ();
			tActivated = tCorporationList.activateCorporation (companyID);
		}

		if (! tActivated) {
			tCorporationList = aGameManager.getShareCompanies ();
			tActivated = tCorporationList.activateCorporation (companyID);
		}
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
