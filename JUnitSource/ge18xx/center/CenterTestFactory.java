package ge18xx.center;

import java.awt.Graphics;

import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameTestFactory;
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

	public City buildCity (int tIndex) {
		String tCityXMLText1 = "<RevenueCenter id=\"7\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText2 = "<RevenueCenter id=\"3\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText3 = "<RevenueCenter id=\"50\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText4 = "<RevenueCenter id=\"50\" location=\"50\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText5 = "<RevenueCenter id=\"50\" location=\"9\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText6 = "<RevenueCenter id=\"18\" location=\"7\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText7 = "<RevenueCenter id=\"19\" location=\"50\" name=\"\" number=\"2\" type=\"Single City\" />";
		String tCityXMLText8 = "<RevenueCenter id=\"20\" location=\"9\" name=\"\" number=\"2\" type=\"Single City\" />";
		City tCity;
		CityInfo tCityInfo;
		XMLNode tCityNode;
		
		tCityInfo = CityInfo.NO_CITY_INFO;
		if (tIndex == 1) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText1);
		} else if (tIndex == 2) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText2);
		} else if (tIndex == 3) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText3);
		} else if (tIndex == 4) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText4);
		} else if (tIndex == 5) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText5);
		} else if (tIndex == 6) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText6);
		} else if (tIndex == 7) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText7);
		} else if (tIndex == 8) {
			tCityNode = utilitiesTestFactory.buildXMLNode (tCityXMLText8);
		} else {
			tCityNode = XMLNode.NO_NODE;
		}
		tCityInfo = buildCityInfo (tIndex);
		tCity = (City) City.NO_CITY;
		if (tCityNode != XMLNode.NO_NODE) {
			tCity = new City (tCityNode);
			tCity.setCityInfo (tCityInfo);
		}
		
		return tCity;
	}
	
	public CityInfo buildCityInfo (int tIndex) {
		String tCityInfoXMLText1 = "<CityInfo id=\"7\" location=\"14\" name=\"Baltimore\" type=\"3\" />";
		String tCityInfoXMLText2 = "<CityInfo id=\"3\" location=\"14\" name=\"Chicago\" type=\"3\" />";
		String tCityInfoXMLText3 = "<CityInfo id=\"22\" name=\"Calcutta\" type=\"3\" bond=\"50\" />";
		String tCityInfoXMLText4 = "<CityInfo id=\"8\" name=\"Delhi\" type=\"3\" bond=\"40\" />";
		String tCityInfoXMLText5 = "<CityInfo id=\"5\" location=\"14\" name=\"Peshawar\" type=\"3\" bond=\"20\" />";
		String tCityInfoXMLText6 = "<CityInfo id=\"18\" name=\"Mysore\" type=\"3\" bond=\"20\" />";
		String tCityInfoXMLText7 = "<CityInfo id=\"19\" location=\"17\" name=\"Bangalore\" type=\"3\" bond=\"20\" />";
		String tCityInfoXMLText8 = "<CityInfo id=\"20\" name=\"Madras\" type=\"3\" bond=\"30\" />";
		CityInfo tCityInfo;
		XMLNode tCityInfoNode;
		
		if (tIndex == 1) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText1);
		} else if (tIndex == 2) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText2);
		} else if (tIndex == 3) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText3);
		} else if (tIndex == 4) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText4);
		} else if (tIndex == 5) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText5);
		} else if (tIndex == 6) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText6);
		} else if (tIndex == 7) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText7);
		} else if (tIndex == 8) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText8);
		} else {
			tCityInfoNode = XMLNode.NO_NODE;
		}
		tCityInfo = (CityInfo) CityInfo.NO_CITY_INFO;
		if (tCityInfoNode != XMLNode.NO_NODE) {
			tCityInfo = new CityInfo (tCityInfoNode);
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
