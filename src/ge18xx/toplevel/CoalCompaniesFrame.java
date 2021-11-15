package ge18xx.toplevel;

import ge18xx.company.CorporationList;
import ge18xx.round.RoundManager;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class CoalCompaniesFrame extends CorporationTableFrame {
	/**
	 * 
	 */
	public static final String BASE_TYPE = CorporationList.TYPE_NAMES [1].toString ();
	public static final String BASE_TITLE = BASE_TYPE + " Companies";
	public static final CoalCompaniesFrame NO_COAL_COMPANIES_FRAME = null;
	public static final ElementName EN_COALS = new ElementName (BASE_TYPE + "s");
	private static final long serialVersionUID = 1L;
	
	public CoalCompaniesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [1], aRoundManager);
	}

	@Override
	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		return (super.getCorporationStateElements (aXMLDocument, EN_COALS));
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		super.fixLoadedRoutes (aMapFrame, BASE_TYPE);
	}

}
