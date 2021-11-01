package ge18xx.toplevel;

//
//  MinorCompaniesFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 1/29/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.CorporationList;
import ge18xx.round.RoundManager;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class MinorCompaniesFrame extends CorporationTableFrame {
	/**
	 * 
	 */
	public static final ElementName EN_MINORS = new ElementName (CorporationList.TYPE_NAMES [2] + "s");
	private static final long serialVersionUID = 1L;
	
	public MinorCompaniesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [2], aRoundManager);
	}
	
	public XMLElement createMinorCompaniesListDefinitions (XMLDocument aXMLDocument) {
		return (super.createCompaniesListDefinitions (aXMLDocument));
	}

	@Override
	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		return (super.getCorporationStateElements (aXMLDocument, EN_MINORS));
	}

	public int getCountOfMinors () {
		return (super.getCountOfCompanies ());
	}
	
	public CorporationList getMinorCompanies () {
		return (super.getCompanies ());
	}
	
	public void loadMinorsStates (XMLNode aXMLNode) {
		super.loadStates (aXMLNode);
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		super.fixLoadedRoutes (aMapFrame, "Minor");
	}
}
