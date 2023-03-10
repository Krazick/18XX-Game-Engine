package ge18xx.company.benefit;

import ge18xx.bank.Bank;
import ge18xx.company.License;
import ge18xx.company.ShareCompany;
import ge18xx.round.action.Action;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.utilities.XMLNode;

public class PassiveEffectBenefit extends Benefit {

	Effect effect;

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
		
		aOwningCompany.addLicense (aLicense);
		tBank = aOwningCompany.getBank ();
		effect = new AddLicenseEffect (tBank, aOwningCompany, 0, aLicense);
	}

	/**
	 *  Add Any additional Effects to the provided Action generated in the process of applying this Benefit.
	 *  
	 * @param aAction The Action to which the Effect needs to be added.
	 * 
	 */
	public void addAdditionalEffects (Action aAction) {
		aAction.addEffect (effect);
	}

	public String buildLicenseName () {
		String tLicenseName;
		
		tLicenseName = privateCompany.getAbbrev () + " License";
	
		return tLicenseName;
	}

}
