package ge18xx.toplevel;

import ge18xx.company.CorporationList;
import ge18xx.round.RoundManager;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class CoalCompaniesFrame extends CorporationTableFrame {
	/**
	 * 
	 */
	public static final ElementName EN_COALS = new ElementName (CorporationList.TYPE_NAMES [1] + "s");
	private static final long serialVersionUID = 1L;
	
	public CoalCompaniesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [1], aRoundManager);
	}
	
	public XMLElement createCoalCompaniesListDefinitions (XMLDocument aXMLDocument) {
		return (super.createCompaniesListDefinitions (aXMLDocument));
	}

	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		return (super.getCorporationStateElements (aXMLDocument, EN_COALS));
	}

	public int getCountOfCoals () {
		return (super.getCountOfCompanies ());
	}
	
	public CorporationList getCoalCompanies () {
		return (super.getCompanies ());
	}
	
	public void loadCoalsStates (XMLNode aXMLNode) {
		super.loadStates (aXMLNode);
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		super.fixLoadedRoutes (aMapFrame, "Coal");
	}

}
