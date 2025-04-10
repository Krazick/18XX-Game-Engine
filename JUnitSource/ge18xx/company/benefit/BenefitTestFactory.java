package ge18xx.company.benefit;

import ge18xx.company.PrivateCompany;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class BenefitTestFactory {
	UtilitiesTestFactory utilitiesTestFactory;

	public BenefitTestFactory () {
		utilitiesTestFactory = new UtilitiesTestFactory ();
	}

	public BenefitTestFactory (UtilitiesTestFactory aUtilitiesTestFactory) {
		utilitiesTestFactory = aUtilitiesTestFactory;
	}

	public MapBenefit buildMapBenefit (PrivateCompany aPrivateCompany, int aIndex) {
		MapBenefit tMapBenefit;
		String tXMLBenefitTest1;
		String tXMLBenefitTest2;
		XMLNode tBenefitXMLNode;

		tXMLBenefitTest1 = "<Benefit actorType=\"Share Company\" "
				+ "class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" "
				+ "mapCell=\"B20\" cost=\"0\" passive=\"false\"/>";
		tXMLBenefitTest2 = "<Benefit actorType=\"Share Company\" ownerType=\"Player\" "
				+ "				class=\"ge18xx.company.benefit.TilePlacementBenefit\" "
				+ "				extra=\"false\" mapCell=\"M15\" tokenType=\"Port\" "
				+ "				cost=\"0\" passive=\"false\" closeOnUse=\"true\" />";
		if (aIndex == 1) {
			tBenefitXMLNode = utilitiesTestFactory.buildXMLNode (tXMLBenefitTest1);
		} else if (aIndex == 2) {
			tBenefitXMLNode = utilitiesTestFactory.buildXMLNode (tXMLBenefitTest2);
			
		} else {
			tBenefitXMLNode = XMLNode.NO_NODE;
		}
		if (tBenefitXMLNode == XMLNode.NO_NODE) {
			tMapBenefit = MapBenefit.NO_MAP_BENEFIT;
		} else {
			tMapBenefit = new MapBenefit (tBenefitXMLNode);
			tMapBenefit.setPrivateCompany (aPrivateCompany);
		}
		
		return tMapBenefit;
	}
	
	public TilePlacementBenefit buildTilePlacementBenefit (PrivateCompany aPrivateCompany, int aIndex) {
		TilePlacementBenefit tTilePlacementBenefit;
		String tXMLBenefitTest1;
		String tXMLBenefitTest2;
		String tXMLBenefitTest3;
		XMLNode tBenefitXMLNode;

		tXMLBenefitTest1 = "<Benefit actorType=\"Share Company\" "
				+ "class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" "
				+ "mapCell=\"B20\" cost=\"0\" passive=\"false\"/>";
		tXMLBenefitTest2 = "<Benefit actorType=\"Share Company\" ownerType=\"Player\" "
				+ "				class=\"ge18xx.company.benefit.TilePlacementBenefit\" "
				+ "				extra=\"false\" mapCell=\"M15\" tokenType=\"Port\" "
				+ "				cost=\"0\" passive=\"false\" closeOnUse=\"true\" />";
		tXMLBenefitTest3 = "<Benefit actorType=\"Share Company\" ownerType=\"Player\" "
				+ "				class=\"ge18xx.company.benefit.TilePlacementBenefit\" "
				+ "				extra=\"false\" mapCell=\"C15\" "
				+ "				cost=\"50\" passive=\"false\" closeOnUse=\"false\" />";
		if (aIndex == 1) {
			tBenefitXMLNode = utilitiesTestFactory.buildXMLNode (tXMLBenefitTest1);
		} else if (aIndex == 2) {
			tBenefitXMLNode = utilitiesTestFactory.buildXMLNode (tXMLBenefitTest2);
		} else if (aIndex == 3) {
			tBenefitXMLNode = utilitiesTestFactory.buildXMLNode (tXMLBenefitTest3);
		} else {
			tBenefitXMLNode = XMLNode.NO_NODE;
		}
		if (tBenefitXMLNode == XMLNode.NO_NODE) {
			tTilePlacementBenefit = TilePlacementBenefit.NO_TILE_PLACEMENT_BENEFIT;
		} else {
			tTilePlacementBenefit = new TilePlacementBenefit (tBenefitXMLNode);
			tTilePlacementBenefit.setPrivateCompany (aPrivateCompany);
		}
		
		return tTilePlacementBenefit;
	}

}
