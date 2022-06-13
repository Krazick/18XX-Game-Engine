package ge18xx.toplevel;

//
//  CitiesFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 9/1/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.center.CityList;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

import javax.swing.JTable;

public class CitiesFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	CityList cities;

	public CitiesFrame (String aFrameName, String aGameName) {
		super (aFrameName, aGameName);
		JTable tTable;

		cities = new CityList ();
		tTable = cities.getJTable ();
		buildScrollPane (tTable);
	}

	public XMLElement createCitiesListDefinitions (XMLDocument aXMLDocument) {
		return (cities.createElement (aXMLDocument));
	}

	public CityList getCities () {
		return cities;
	}
}