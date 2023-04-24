package ge18xx.company.benefit;

import ge18xx.bank.Bank;
import ge18xx.company.License;
import ge18xx.company.ShareCompany;
import ge18xx.round.action.Action;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.utilities.XMLNode;

public class PassiveEffectBenefit extends Benefit {

	public PassiveEffectBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
	}

	@Override
	public int getCost () {
		return 0;
	}

	@Override
	public String getNewButtonLabel () {
		String tButtonLabel;
		
		tButtonLabel = "Passive Effect";
		
		return tButtonLabel;
	}

	public void handlePassive (ShareCompany aShareCompany, Action aAction) {
	}

	public void addLicense (ShareCompany aOwningCompany, License aLicense) {
		Bank tBank;
		AddLicenseEffect tAddLicenseEffect;
		
		aOwningCompany.addLicense (aLicense);
		tBank = aOwningCompany.getBank ();
		tAddLicenseEffect = new AddLicenseEffect (tBank, aOwningCompany, aLicense);
		addAdditionalEffect (tAddLicenseEffect);
	}
}
