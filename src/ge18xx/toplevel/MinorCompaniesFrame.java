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

public class MinorCompaniesFrame extends CorporationTableFrame {
	/**
	 *
	 */
	public static final MinorCompaniesFrame NO_MINORS_FRAME = null;
	public static final String BASE_TITLE = "Minor Companies";
	public static final ElementName EN_MINORS = new ElementName (CorporationList.TYPE_NAMES [1] + "s");
	private static final long serialVersionUID = 1L;

	public MinorCompaniesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [1], aRoundManager);
	}

	@Override
	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		return (super.getCorporationStateElements (aXMLDocument, EN_MINORS));
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		super.fixLoadedRoutes (aMapFrame, "Minor");
	}
}
