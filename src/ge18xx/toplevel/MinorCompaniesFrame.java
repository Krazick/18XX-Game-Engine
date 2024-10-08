package ge18xx.toplevel;

import ge18xx.company.Corporation;

//
//  MinorCompaniesFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 1/29/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.CorporationList;
import ge18xx.company.MinorCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import geUtilities.xml.ElementName;

public class MinorCompaniesFrame extends CorporationTableFrame {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TYPE = CorporationList.TYPE_NAMES [1].toString ();
	public static final MinorCompaniesFrame NO_MINORS_FRAME = null;
	public static final String BASE_TITLE = BASE_TYPE +  " " + Corporation.COMPANIES;
	public static final ElementName EN_MINORS = new ElementName (BASE_TYPE + "s");

	public MinorCompaniesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [1], aRoundManager);
	}

//	@Override	
//	public XMLElement addElements (XMLDocument aXMLDocument) {
//		XMLElement tXMLElement;
//		
//		tXMLElement = super.addElements (aXMLDocument, EN_MINORS);
//		
//		return tXMLElement;
//	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		super.fixLoadedRoutes (aMapFrame, "Minor");
	}
	

	public MinorCompany getMinorCompany (String aCompanyAbbrev) {
		MinorCompany tMinorCompany;

		tMinorCompany = (MinorCompany) companies.getCorporation (aCompanyAbbrev);

		return tMinorCompany;
	}
	
	public void fillCertificateInfo (GameManager aGameManager) {
		MinorCompany tMinorCompany;
		int tIndex;
		int tCorpCount;

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
					tMinorCompany = (MinorCompany) companies.getCorporation (tIndex);
					tMinorCompany.fillCertificateInfo (aGameManager);
				}
			}
		}
	}
}
