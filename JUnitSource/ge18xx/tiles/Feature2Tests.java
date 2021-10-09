package ge18xx.tiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ge18xx.map.Location;

@DisplayName ("Feature2 Object, subclass of Feature Tests")
class Feature2Tests {

	@BeforeEach
	void setUp () throws Exception {
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test Constructor with No Arguments")
	void testConstructorNoArgs () {
		Feature2 tFeatureA;
		
		tFeatureA = new Feature2 ();
		assertTrue (tFeatureA.isNoLocation ());
		assertTrue (tFeatureA.isNoLocation2 ());
	}
	
	@Test
	@DisplayName ("Test SetLocations and bothLocationSet Methods")
	void testSetLocationAndBothLocationsSet () {
		Feature2 tFeatureA;

		tFeatureA = new Feature2 ();
		assertFalse (tFeatureA.bothLocationsSet ());
		tFeatureA.setLocation (31);
		assertFalse (tFeatureA.bothLocationsSet ());
		tFeatureA.setLocation2 (42);
		assertTrue (tFeatureA.bothLocationsSet ());
		tFeatureA.setLocation (Location.NO_LOCATION);
		assertFalse (tFeatureA.bothLocationsSet ());
	}
	
	@Test
	@DisplayName ("Test creation with Locations")
	void testCreationWithLocations () {
		Location tLocation1;
		Location tLocation2;
		Feature2 tFeatureB;
		
		tLocation1 = new Location (99); // Dead End Location
		tLocation2 = new Location (50); // CenterCity Location
		tFeatureB = new Feature2 (tLocation2, tLocation1);
		assertTrue (tFeatureB.isCenterLocation (), "Feature2 with a Center at Location");
		assertTrue (tFeatureB.isDeadEnd2 (), "Feature2 with a Dead End at Location2");
	}
	
	@Test
	@DisplayName ("Test getLocationToInt Methods from Feature")
	void testGetLocationToInt () {
		Feature2 tFeatureC;
		
		tFeatureC = new Feature2 (23, 33);
		assertEquals (tFeatureC.getLocationToInt (), 23);
		assertEquals (tFeatureC.getLocation2ToInt (), 33);
	}
	
	@Test
	@DisplayName ("Test isLocation Methods")
	void testLocation2isNull () {
		Location tLocation1 = new Location (10);
		Location tLocation2 = null;
		Feature2 tFeatureA;
		
		tFeatureA = new Feature2 (tLocation1, tLocation2);
		assertTrue (tFeatureA.isNoLocation2 ());
		assertFalse (tFeatureA.isNoLocation ());
	}
	
	@Test
	@DisplayName ("Test isCenterLocation Methods")
	void testCenterLocationMethods () {
		Feature2 tFeatureA;
		
		tFeatureA = new Feature2 (39, 50);
		assertFalse (tFeatureA.isCenterLocation ());
		assertTrue (tFeatureA.isCenterLocation2 ());
	}

	@Test
	@DisplayName ("Test isAtLocation Methods")
	void testIsAtLocationMethods () {
		Location tLocation1 = new Location (50);
		Location tLocation2 = new Location (42);
		Feature2 tFeatureA;
		
		tFeatureA = new Feature2 (39, 50);
		assertFalse (tFeatureA.isAtLocation (tLocation1));
		assertTrue (tFeatureA.isAtLocation2 (tLocation1));
		assertFalse (tFeatureA.isAtLocation2 (tLocation2));
	}
	
	@Test
	@DisplayName ("Test getLocation2 Method")
	void testGetLocation2Methods () {
		Location tLocation1, tLocation2;
		Feature2 tFeatureA;
		
		tFeatureA = new Feature2 (35, 99);
		tLocation1 = tFeatureA.getLocation ();
		tLocation2 = tFeatureA.getLocation2 ();
		
		assertEquals (35, tLocation1.getLocation ());
		assertEquals (99, tLocation2.getLocation ());
	}
	
	@Test
	@DisplayName ("Test Feature2 Will Bleed Through (Default responses)")
	void testDefaultBleedThroughResponses () {
		Feature2 tFeatureA = new Feature2 (17, 20);
		
		assertFalse (tFeatureA.bleedThroughAll ());
		assertTrue (tFeatureA.bleedThroughJustStarting ());
	}
}
