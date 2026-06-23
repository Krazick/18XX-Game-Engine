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
			tCityInfo = buildCityInfo (tIndex);
		} else {
			tCityNode = XMLNode.NO_NODE;
		}
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
		CityInfo tCityInfo;
		XMLNode tCityInfoNode;
		
		if (tIndex == 1) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText1);
		} else if (tIndex == 2) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText2);
		} else if (tIndex == 3) {
			tCityInfoNode = utilitiesTestFactory.buildXMLNode (tCityInfoXMLText3);
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
