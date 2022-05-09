package ge18xx.company.benefit;

import ge18xx.company.PrivateCompany;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class BenefitTestFactory {
	UtilitiesTestFactory utilitiesTestFactory;

	public BenefitTestFactory () {
		utilitiesTestFactory = new UtilitiesTestFactory ();
	}

	public BenefitTestFactory (UtilitiesTestFactory aUtilitiesTestFactory) {
		utilitiesTestFactory = aUtilitiesTestFactory;
	}

	public MapBenefit buildMapBenefit (PrivateCompany aPrivateCompany) {
		MapBenefit tMapBenefit;
		String tXMLBenefitTest = "<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>";
		XMLNode tBenefitXMLNode;

		tBenefitXMLNode = utilitiesTestFactory.constructXMLNode (tXMLBenefitTest);

		tMapBenefit = new MapBenefit (tBenefitXMLNode);
		tMapBenefit.setPrivateCompany (aPrivateCompany);

		return tMapBenefit;
	}

}
