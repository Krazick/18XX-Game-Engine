package ge18xx.center;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//
//  CityList.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.toplevel.InformationTable;
import geUtilities.xml.ElementName;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.ParsingRoutineI;

public class CityList extends InformationTable implements LoadableXMLI {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final CityList NO_CITY_LIST = null;
	public static final ElementName EN_CITIES = new ElementName ("Cities");
	List<CityInfo> cities;

	public CityList () {
		super ();
		cities = new LinkedList<> ();
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tCityElement;

		tXMLElement = aXMLDocument.createElement (EN_CITIES);
		for (CityInfo city : cities) {
			tCityElement = city.createElement (aXMLDocument);
			tXMLElement.appendChild (tCityElement);
		}
		aXMLDocument.appendChild (tXMLElement);

		return tXMLElement;
	}

	public CityInfo getCityInfo (int aCityID) {
		CityInfo tCityInfo = CityInfo.NO_CITY_INFO;
		Iterator<CityInfo> tCityInfoIter = cities.iterator ();
		boolean tNotFoundYet = true;
		int tCityID;

		while (tCityInfoIter.hasNext () && tNotFoundYet) {
			tCityInfo = tCityInfoIter.next ();
			tCityID = tCityInfo.getID ();
			if (tCityID == aCityID) {
				tNotFoundYet = false;
			}
		}

		if (tNotFoundYet) {
			tCityInfo = null;
		}

		return tCityInfo;
	}

	public String getCityName (int aCityID) {
		String tName;
		CityInfo tCityInfo;

		tCityInfo = getCityInfo (aCityID);
		if (tCityInfo != CityInfo.NO_CITY_INFO) {
			tName = tCityInfo.getName ();
		} else {
			tName = null;
		}

		return tName;
	}

	@Override
	public int getColCount () {
		CityInfo tCityInfo;
		Iterator<CityInfo> tCityInfoIter = cities.iterator ();
		int tColCount;

		if (tCityInfoIter.hasNext ()) {
			tCityInfo = tCityInfoIter.next ();
			tColCount = tCityInfo.fieldCount ();
		} else {
			tColCount = 0;
		}

		return tColCount;
	}

	@Override
	public int getRowCount () {
		return cities.size ();
	}

	public void loadJTable () {
		int tColCount;
		int tRowCount;
		int tRowIndex;

		tColCount = getColCount ();
		tRowCount = getRowCount ();
		initiateArrays (tRowCount, tColCount);
		addHeader ("City Name", 0);
		addHeader ("ID", 1);
		addHeader ("Map Cell ID", 2);
		addHeader ("Type", 3);

		tRowIndex = 0;
		for (CityInfo tCityInfo : cities) {
			addDataElement (tCityInfo.getName (), tRowIndex, 0);
			addDataElement (tCityInfo.getID (), tRowIndex, 1);
			addDataElement (tCityInfo.getMapCellID (), tRowIndex, 2);
			addDataElement (tCityInfo.getType (), tRowIndex, 3);
			tRowIndex++;
		}
		setModel ();
	}

	@Override
	public String getTypeName () {
		return "City List";
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNodeList tXMLNodeList;
		XMLNode XMLCityListRoot;

		XMLCityListRoot = aXMLDocument.getDocumentNode ();
		tXMLNodeList = new XMLNodeList (CityInfoParsingRoutine);
		tXMLNodeList.parseXMLNodeList (XMLCityListRoot, CityInfo.EN_CITY_INFO);
		loadJTable ();
	}

	ParsingRoutineI CityInfoParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			CityInfo tCityInfo;
			boolean tFoundDuplicate;

			tCityInfo = new CityInfo (aChildNode);
			tFoundDuplicate = cities.add (tCityInfo);
			if (!tFoundDuplicate) {
				System.err.println ("Oops, two cities have ID = " + tCityInfo.getID ());
			}
		}
	};
}
