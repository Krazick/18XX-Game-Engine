package ge18xx.center;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

import java.awt.Graphics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.map.Hex;
import ge18xx.tiles.Feature2;

@DisplayName ("Testing PrivateRailwayCenter Class")
class PrivateRailwayCenterTests {
	PrivateRailwayCenter privateRailywayCenter;
	CenterTestFactory centerTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		centerTestFactory = new CenterTestFactory ();
		privateRailywayCenter = centerTestFactory.buildPrivateRailwayCenter ();
	}

	@Test
	@DisplayName ("Private Railway Center Tests")
	void privateRailwayCenterTests () {
		assertFalse (privateRailywayCenter.cityOrTown ());
		assertFalse (privateRailywayCenter.isCity ());
		assertFalse (privateRailywayCenter.cityHasOpenStation ());
		assertTrue (privateRailywayCenter.validCityInfo ());
	}

	@Test
	@DisplayName ("Private Railway Center CityInfo Test")
	void privateRailwayCenterCityInfoTest () {
		CityInfo mCityInfo;
		CityInfo tNoCityInfo;
		Graphics mGraphics;
		int tX;
		int tY;
		int tOrient;
		Hex tHex;
		boolean tOnTile;
		Feature2 tFeature;


		mGraphics = centerTestFactory.buildGraphicsMock ();
		tX = 10;
		tY = 10;
		tOrient = 0;
		tHex = new Hex ();
		tOnTile = false;
		tFeature = new Feature2 ();
		tNoCityInfo = CityInfo.NO_CITY_INFO;
		privateRailywayCenter.setNoCloneCityInfo (tNoCityInfo);

		privateRailywayCenter.draw (mGraphics, tX, tY, tOrient, tHex, tOnTile, tFeature);

		mCityInfo = centerTestFactory.buildCityInfoMock ();
		Mockito.doNothing ().when (mCityInfo).drawPrivateRailway (any (Graphics.class), 
				anyInt (), anyInt (), any (ge18xx.map.Hex.class));
		privateRailywayCenter.setNoCloneCityInfo (mCityInfo);

		privateRailywayCenter.draw (mGraphics, tX, tY, tOrient, tHex, tOnTile, tFeature);
		Mockito.verify (mCityInfo, times (1)).drawPrivateRailway (any (Graphics.class), anyInt (), anyInt (),
				any (ge18xx.map.Hex.class));

	}
}
