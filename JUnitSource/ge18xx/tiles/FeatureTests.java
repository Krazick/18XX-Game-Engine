/**
 *
 */
package ge18xx.tiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.awt.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapTestFactory;

/**
 * @author marksmith
 *
 */
@DisplayName ("Feature Tests")
class FeatureTests {
	TilesTestFactory tilesTestFactory;
	MapTestFactory mapTestFactory;
	Feature featureAlpha;
	Feature featureBeta;
	Feature featureDelta;
	Feature featureGamma;
	Location locationDeadEnd;
//	Location locationSide;
	Location locationCenter;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
		mapTestFactory = new MapTestFactory ();
		tilesTestFactory = new TilesTestFactory ();
		featureAlpha = tilesTestFactory.buildFeature ();
		locationDeadEnd = mapTestFactory.buildLocation (99);
		featureBeta = tilesTestFactory.buildFeature (locationDeadEnd);
		locationCenter = mapTestFactory.buildLocation (50); // Center City Location
		featureDelta = tilesTestFactory.buildFeature (0); // Hex Side # 0 Location
	}

	@Test
	@DisplayName ("Test CalcCenter access")
	public void testCallingCalcCenter () {
		Point tPoint;
		Point tCalculatedCenter;
		int tXValue;
		int tYValue;
		Hex tHex;
		Feature tFeature;
		Location mLocation;

		tPoint = new Point (100, 100);
		tHex = mapTestFactory.buildHex (100, 50, true);
		mLocation = mapTestFactory.buildLocationMock ();
		Mockito.when (mLocation.calcCenter (any (Hex.class))).thenReturn (tPoint);
		tFeature = tilesTestFactory.buildFeature (mLocation);
		tCalculatedCenter = tFeature.calcCenter (tHex);

		tXValue = (int) tCalculatedCenter.getX ();
		tYValue = (int) tCalculatedCenter.getY ();
		verify (mLocation).calcCenter (tHex);
		assertEquals (100, tXValue);
		assertEquals (100, tYValue);
	}

	@Test
	@DisplayName ("Test Feature Constructors")
	public void FeatureConstructorTest () {

		assertTrue (featureAlpha.isNoLocation (), "Feature Alpha with no Location");
		assertTrue (featureBeta.isDeadEnd (), "Feature Beta with a Dead End Location");
		assertEquals (0, featureDelta.getLocationToInt (), "Feature is Hex 0");

		featureGamma = featureDelta.clone ();
		assertEquals (0, featureGamma.getLocationToInt (), "Feature is Hex Side 0 -- Cloned");
		assertNotEquals (featureDelta, featureGamma, "Feature Delta is Not Feature Gamma");
		
		assertFalse (featureAlpha.isOpen ());
	}

	@Test
	@DisplayName ("Test Feature Setting Locations")
	public void FeatureSetLocationsTest () {
		Location tFeatureBetaLocation;

		featureAlpha.setLocation (null);
		assertTrue (featureAlpha.isNoLocation (), "Feature Alpha with no Location (null) - TRUE");
		assertFalse (featureBeta.isNoLocation (), "Feature Beta with no Location (null) - FALSE");

		featureAlpha.setLocation (50); // Center Location
		assertTrue (featureAlpha.isCenterLocation (), "Feature Alpha with Center Location - After SetLocation");
		assertEquals (50, featureAlpha.getLocationToInt (), "Feature Alpha Location for Center as Int");
		assertEquals ("99", featureBeta.getLocationToString (), "Feature Beta Location for Center as String");

		assertFalse (featureAlpha.isAtLocation (locationDeadEnd),
				"Feature Alpha not at Dead-End - With 'isAtLocation' Method");
		assertTrue (featureAlpha.isAtLocation (locationCenter),
				"Feature Center at Center - With 'isAtLocation' Method");

		tFeatureBetaLocation = featureBeta.getLocation ();
		assertSame (locationDeadEnd, tFeatureBetaLocation,
				"Dead End Location (null) and Feature Beta Location Objects are the same");
		assertFalse (featureDelta.bleedThroughAll (), "Feature Will not Bleed Through");
		assertTrue (featureBeta.bleedThroughJustStarting (), "Feature Will Bleed Through First");
	}
}
