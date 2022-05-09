/**
 * 
 */
package ge18xx.tiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

//import org.mockito.Mockito;
//import static org.mockito.Mockito.verify;  
//import static org.mockito.Mockito.any;  

import ge18xx.map.Location;

/**
 * @author marksmith
 *
 */
@DisplayName ("Feature Tests")
class FeatureTests {
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
		featureAlpha = new Feature ();
		locationDeadEnd = new Location (99); // Dead End Location
		featureBeta = new Feature (locationDeadEnd);
		locationCenter = new Location (50); // Center City Location
		featureDelta = new Feature (0); // Hex Side # 0 Location
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}

//	@Test
//	@DisplayName ("Test CalcCenter access")
//	public void testCallingCalcCenter () {
//		Point tPoint = new Point (100, 100);
//		Point tCalculatedCenter;
//		int tXValue, tYValue;
//		Hex tHex = new Hex (100, 50, true);

//		Location mLocation = Mockito.mock (Location.class);
//		Mockito.when (mLocation.calcCenter (any (Hex.class))).thenReturn (tPoint);
//		Feature tFeature = new Feature (10);
//		tCalculatedCenter = tFeature.calcCenter (tHex);
//		
//		tXValue = (int) tCalculatedCenter.getX ();
//		tYValue = (int) tCalculatedCenter.getY ();
//		verify (mLocation).calcCenter (tHex);
//		System.out.println ("Calculated Center " + tCalculatedCenter.getX () + ", " + tCalculatedCenter.getY ());
//		System.out.println ("Point " + tPoint.getX () + ", " + tPoint.getY ());
//		assertEquals (8, tXValue);
//		assertEquals (15, tYValue);
//	}

	@Test
	@DisplayName ("Test Feature Constructors")
	public void FeatureConstructorTest () {

		assertTrue (featureAlpha.isNoLocation (), "Feature Alpha with no Location");
		assertTrue (featureBeta.isDeadEnd (), "Feature Beta with a Dead End Location");
		assertEquals (0, featureDelta.getLocationToInt (), "Feature is Hex 0");

		featureGamma = featureDelta.clone ();
		assertEquals (0, featureGamma.getLocationToInt (), "Feature is Hex Side 0 -- Cloned");
		assertNotEquals (featureDelta, featureGamma, "Feature Delta is Not Feature Gamma");
	}

	@Test
	@DisplayName ("Test Feature Setting Locations")
	public void FeatureSetLocationsTest () {
//		Location tFeatureAlphaLocation;
		Location tFeatureBetaLocation;

//		locationSide = null;
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

//		tFeatureAlphaLocation = featureAlpha.getLocation ();
//		assertNotSame (Location.NO_LOC, tFeatureAlphaLocation, "Side Location (null) and Feature Alpha Location Objects are not the same");
		tFeatureBetaLocation = featureBeta.getLocation ();
		assertSame (locationDeadEnd, tFeatureBetaLocation,
				"Dead End Location (null) and Feature Beta Location Objects are the same");
		assertFalse (featureDelta.bleedThroughAll (), "Feature Will not Bleed Through");
		assertTrue (featureBeta.bleedThroughJustStarting (), "Feature Will Bleed Through First");
	}
}
