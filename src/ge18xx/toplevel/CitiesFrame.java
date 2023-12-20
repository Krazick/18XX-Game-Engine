package ge18xx.toplevel;

import javax.swing.JTable;

//
//  CitiesFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 9/1/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.center.CityList;
import ge18xx.game.GameManager;

import geUtilities.xml.XMLFrame;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;

public class CitiesFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	CityList cities;

	public CitiesFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
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