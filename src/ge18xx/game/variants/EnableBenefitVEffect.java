package ge18xx.game.variants;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;


public class EnableBenefitVEffect extends VariantEffect {
	public static final String NAME = "Enable Benefit";
	public static final AttributeName AN_COMPANY_ID = new AttributeName ("companyID");
	public static final AttributeName AN_BENEFIT_NAME = new AttributeName ("benefitName");
	String benefitName;
	int companyID;

	public EnableBenefitVEffect (XMLNode aXMLNode) {
		super (aXMLNode);

		int tCompanyID;
		String tBenefitName;

		tCompanyID = aXMLNode.getThisIntAttribute (AN_COMPANY_ID);
		setCompanyID (tCompanyID);
		tBenefitName = aXMLNode.getThisAttribute (AN_BENEFIT_NAME);
		setBenefitName (tBenefitName);
	}

	public int getCompanyID () {
		return companyID;
	}

	public void setCompanyID (int aCompanyID) {
		companyID = aCompanyID;
	}

	public String getBenefitName () {
		return benefitName;
	}

	public void setBenefitName (String aBenefitName) {
		benefitName = aBenefitName;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the CompanyID and the VariantEffect Class
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
		tXMLElement.setAttribute (AN_BENEFIT_NAME, benefitName);
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
		PrivateCompany tPrivate;
		Corporation tCorporation;
		boolean tState;

		tState = getState ();
		tCorporationList = aGameManager.getPrivates ();
		tCorporation = tCorporationList.getCorporationByID (companyID);
		if (tCorporation != Corporation.NO_CORPORATION) {
			tPrivate = (PrivateCompany) tCorporation;
			if (tState) {
				tPrivate.enableBenefit (benefitName);
			} else {
				tPrivate.disableBenefit (benefitName);
			}
		}
	}
}
