package ge18xx.center;

import java.awt.Graphics;

import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameTestFactory;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class CenterTestFactory {
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	private CompanyTestFactory companyTestFactory;
	private MapTestFactory mapTestFactory;
	
	/**
	 * Builds the Center Test Factory by creating the gameTestFactory and get the Utilities Test Factory
	 *
	 */
	public CenterTestFactory () {
		mapTestFactory = new MapTestFactory ();
		utilitiesTestFactory = mapTestFactory.getUtilitiesTestFactory ();
		gameTestFactory = mapTestFactory.getGameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
	}

	public CompanyTestFactory getCompanyTestFactory () {
		return companyTestFactory;
	}
	
	public MapTestFactory getMapTestFactory () {
		return mapTestFactory;
	}
	
	public PrivateRailwayCenter buildPrivateRailwayCenter () {
		PrivateRailwayCenter tPrivateRailwayCenter;
		String tPrivateRailwayCenterXMLText = "<RevenueCenter id=\"0\" location=\"12\" name=\"CTF Private\" type=\"Private Railway\" />\n";
		XMLNode tPrivateRailwayCenterNode;

		tPrivateRailwayCenterNode = utilitiesTestFactory.buildXMLNode (tPrivateRailwayCenterXMLText);
		tPrivateRailwayCenter = (PrivateRailwayCenter) RevenueCenter.NO_CENTER;
		if (tPrivateRailwayCenterNode != XMLNode.NO_NODE) {
			tPrivateRailwayCenter = new PrivateRailwayCenter (tPrivateRailwayCenterNode);
			tPrivateRailwayCenter.setupCityInfo ();
		}

		return tPrivateRailwayCenter;
	}

	public City buildCity (int aIndex) {
		City tCity;
		boolean tIsDeltaTerrain;
		
		tIsDeltaTerrain = false;
		tCity = buildCity (aIndex, tIsDeltaTerrain);
		
		return tCity;
	}
	
	public City buildCity (int aIndex, boolean aIsDeltaTerrain) {
		String tCityXMLTexts [] = {
				"<RevenueCenter id=\"7\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"3\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"50\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"50\" location=\"50\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"50\" location=\"9\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"18\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"19\" location=\"50\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"20\" location=\"9\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"20\" location=\"9\" name=\"\" number=\"2\" type=\"Single City\" />",
				"<RevenueCenter id=\"20\" location=\"9\" name=\"\" number=\"2\" type=\"Single City\" />"
		};
		City tCity;
		CityInfo tCityInfo;
		XMLNode tCityNode;
		
		tCityInfo = CityInfo.NO_CITY_INFO;
		if ((aIndex > 0) && (aIndex < 11)) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLTexts [aIndex - 1]);
		} else {
			tCityNode = XMLNode.NO_NODE;
		}

		tCityInfo = buildCityInfo (aIndex, aIsDeltaTerrain);
		tCity = (City) City.NO_CITY;
		if (tCityNode != XMLNode.NO_NODE) {
			tCity = new City (tCityNode);
			tCity.setCityInfo (tCityInfo);
		}
		
		return tCity;
	}
	
	public CityInfo buildCityInfo (int aIndex) {
		boolean aIsDeltaTerrain;
		CityInfo tCityInfo;
		
		aIsDeltaTerrain = false;
		tCityInfo = buildCityInfo (aIndex, aIsDeltaTerrain);
		
		return tCityInfo;
	}

	public CityInfo buildCityInfo (int aIndex, boolean aIsDeltaTerrain) {
		String tCityInfoXMLTexts [] = {
				"<CityInfo id=\"7\" location=\"14\" name=\"Baltimore\" type=\"3\" />",
				"<CityInfo id=\"3\" location=\"14\" name=\"Chicago\" type=\"3\" />",
				"<CityInfo id=\"22\" name=\"Calcutta\" type=\"3\" bond=\"50\" shareCompanies=\"1901,1904,1908\" />",
				"<CityInfo id=\"8\" name=\"Delhi\" type=\"3\" bond=\"40\" shareCompanies=\"1903\" />",
				"<CityInfo id=\"5\" location=\"14\" name=\"Peshawar\" type=\"3\" bond=\"20\" shareCompanies=\"1903\" />",
				"<CityInfo id=\"18\" name=\"Mysore\" type=\"3\" bond=\"20\" shareCompanies=\"1907\" />",
				"<CityInfo id=\"19\" location=\"17\" name=\"Bangalore\" type=\"3\" bond=\"20\" shareCompanies=\"1906\" />",
				"<CityInfo id=\"20\" name=\"Madras\" type=\"3\" bond=\"30\" shareCompanies=\"1906,1907\" />",
				"<CityInfo id=\"11\" name=\"Cawnpore\" type=\"3\" bond=\"20\" shareCompanies=\"1902\" />",
				"<CityInfo id=\"12\" name=\"Lucknow\" type=\"3\" bond=\"20\" shareCompanies=\"1901\" />"
		};
		CityInfo tCityInfo;
		XMLNode tCityInfoNode;
		MapCell mMapCell;
		
		if ((aIndex > 0) && (aIndex < 11)) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLTexts [aIndex - 1]);
		} else {
			tCityInfoNode = XMLNode.NO_NODE;
		}
		tCityInfo = (CityInfo) CityInfo.NO_CITY_INFO;
		if (tCityInfoNode != XMLNode.NO_NODE) {
			tCityInfo = new CityInfo (tCityInfoNode);
			
			mMapCell = mapTestFactory.buildMapCellMock ("D1");
			Mockito.when (mMapCell.isDeltaTerrain ()).thenReturn (aIsDeltaTerrain);
			tCityInfo.setMapCell (mMapCell);
		}
		
		return tCityInfo;
	}
	
	public CityInfo buildCityInfoMock () {
		CityInfo mCityInfo = Mockito.mock (CityInfo.class);

		Mockito.when (mCityInfo.getName ()).thenReturn ("MCityInfo");

		return mCityInfo;
	}

	public Graphics buildGraphicsMock () {
		Graphics mGraphics = Mockito.mock (Graphics.class);

		return mGraphics;
	}
}
