/**
 * 
 */
package ge18xx.map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author marksmith
 *
 */
@DisplayName ("Location Tests")
class LocationTests {

	Location noLocation;
	Location locations [];
	Location deadEndLocation;
	Location centerLocation;
	
	@BeforeEach
	public void setUp () throws Exception {
		int index;
		int num_locations = 46;
		
		noLocation = new Location ();
		deadEndLocation = new Location (Location.DEAD_END_LOC);
		centerLocation = new Location (Location.CENTER_CITY_LOC);
		locations = new Location [num_locations];
		for (index = 0; index < num_locations; index++) {
			locations [index] = new Location(index);
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	@DisplayName ("Test Constructors")
	public void TestConstructors () {
		assertTrue (noLocation.isNoLocation ());
		assertFalse (locations [0].isNoLocation ());
		Location tLocation = centerLocation.clone ();
		assertNotEquals (tLocation.isSameLocationValue (centerLocation), "Clone value test Equals - Same, Cloning Failed");
		assertEquals (tLocation.getLocation (), centerLocation.getLocation (), "Clone value same");
		tLocation.setValue (-2);
		assertEquals (tLocation.getLocation (), centerLocation.getLocation (), "Clone value still Same after Invalid (-2) Location set attempt");
		tLocation.setValue (100);
		assertEquals (tLocation.getLocation (), centerLocation.getLocation (), "Clone value still Same after Invalid (100) Location set attempt");
		tLocation.setValue (locations [10].getLocation ());
		assertNotEquals (tLocation.getLocation (), centerLocation.getLocation (), "Clone value now Different");
	}

	@Test
	@DisplayName ("Test Location Hex Sides")
	public void TestLocationHexSides () {
		int index;
		int min_side = 0;
		int max_side = 5;
		int min_city_ns = 6;
		int max_city_ns = 11;
		
		assertFalse (noLocation.isSide());
		for (index = min_side; index <= max_side; index++) {
			assertTrue (locations [index].isSide ());
		}
		for (index = min_city_ns; index <= max_city_ns; index++) {
			assertFalse (locations [index].isSide ());
		}
		for (index = min_side; index <= max_side; index++) {
			assertTrue (locations [index].isSide (index));
			assertFalse (locations [index].isSide (index + 1));
		}
		for (index = min_city_ns; index <= max_city_ns; index++) {
			assertFalse (locations [index].isSide (index));
			assertFalse (locations [index].isSide (index - 6));
		}
	}
	
	@Test
	@DisplayName ("Test Location Fixed Locations")
	public void TestLocationIsFixedLocations () {
		assertTrue (deadEndLocation.isDeadEnd ());
		assertFalse (deadEndLocation.isCenterLocation ());
		assertTrue (centerLocation.isCenterLocation ());
		assertFalse (centerLocation.isDeadEnd ());
	}
	
	@Test
	@DisplayName ("Test Comparing Locations")
	public void TestCompareLocations () {
		assertFalse (locations[0].GreaterThan (locations [1]));
		assertTrue (locations[7].GreaterThan (locations [1]));
	}
	
	@Test
	@DisplayName ("Test Locations are Cities")
	public void TestIsLocationCity () {
		int min_side = 0;
		int max_side = 5;
		int min_city = 6;
		int max_city = 45;
		int index;
		
		assertTrue (centerLocation.isCity ());
		assertFalse (deadEndLocation.isCity ());
		for (index = min_side; index <= max_side; index++) {
			assertFalse (locations [index].isCity ());
		}
		for (index = min_city; index <= max_city; index++) {
			assertTrue (locations [index].isCity ());
		}
	}
	
	private void testCityLocations (Location aLocation, int aIndex, boolean cityHC, boolean cityHS, 
					boolean cityFHCR, boolean cityFHCL, boolean cityFHS, boolean cityNC, boolean cityAS) {
		Integer tIndex = new Integer(aIndex);
		String tLabel = "Location is " + tIndex.toString () + " City ";
		String tNotLabel = "Location is " + tIndex.toString () + " Not City ";
		
		if (cityHC) {
			assertTrue (aLocation.isCityHexCorner(), tLabel + "Hex Corner");
		} else {
			assertFalse (aLocation.isCityHexCorner(), tNotLabel + "Hex Corner");
		}
		if (cityHS) {
			assertTrue (aLocation.isCityHexSide(), tLabel + "Hex Side");
		} else {
			assertFalse (aLocation.isCityHexSide(), tNotLabel + "Hex Side");
		}
		if (cityFHCR) {
			assertTrue (aLocation.isCityFarHexCornerRight(), tLabel + "Far Hex Corner Right");
		} else {
			assertFalse (aLocation.isCityFarHexCornerRight(), tNotLabel + "Far Hex Corner Right");
		}
		if (cityFHCL) {
			assertTrue (aLocation.isCityFarHexCornerLeft(), tLabel + "Far Hex Corner Left");
		} else {
			assertFalse (aLocation.isCityFarHexCornerLeft(), tNotLabel + "Far Hex Corner Left");
		}
		if (cityFHS) {
			assertTrue (aLocation.isCityFarHexSide(), tLabel + "Far Hex Side");
		} else {
			assertFalse (aLocation.isCityFarHexSide(), tNotLabel + "Far Hex Side");
		}
		if (cityNC) {
			assertTrue (aLocation.isCityNearCenter(), tLabel + "Near Center");
		} else {
			assertFalse (aLocation.isCityNearCenter(), tNotLabel + "Near Center");
		}
		if (cityAS) {
			assertTrue (aLocation.isCityAdjacentSide(), tLabel + "Adjcent Side");
		} else {
			assertFalse (aLocation.isCityAdjacentSide(), tNotLabel + "Adjecent Side");
		}
	}
	
	@Test
	@DisplayName ("Test Various City Locations")
	public void TestIsCityLocationsVarious () {
		int min_side = 0;
		int max_side = 5;
		int min_city_loc_ns= 6;
		int max_city_loc_ns = 11;
		int min_city_loc_nc = 12;
		int max_city_loc_nc = 17;
		int min_city_loc_fs = 18;
		int max_city_loc_fs = 23;
		int min_city_loc_fcl = 24;
		int max_city_loc_fcl = 29;
		int min_city_loc_fcr = 30;
		int max_city_loc_fcr = 35;
		int min_city_loc_ncntr = 36;
		int max_city_loc_ncntr = 39;
		int min_city_loc_adjside = 40;
		int max_city_loc_adjside = 45;
		int index;
		
		for (index = min_side; index <= max_side; index++) {
			testCityLocations (locations[index], index, false, false, false, false, false, false, false);
		}
		for (index = min_city_loc_ns; index <= max_city_loc_ns; index++) {
			testCityLocations (locations[index], index, false, true, false, false, false, false, false);
		}
		for (index = min_city_loc_nc; index <= max_city_loc_nc; index++) {
			testCityLocations (locations[index], index, true, false, false, false, false, false, false);
		}
		for (index = min_city_loc_fs; index <= max_city_loc_fs; index++) {
			testCityLocations (locations[index], index, false, false, false, false, true, false, false);
		}
		for (index = min_city_loc_fcl; index <= max_city_loc_fcl; index++) {
			testCityLocations (locations[index], index, false, false, false, true, false, false, false);
		}
		for (index = min_city_loc_fcr; index <= max_city_loc_fcr; index++) {
			testCityLocations (locations[index], index, false, false, true, false, false, false, false);
		}
		for (index = min_city_loc_ncntr; index <= max_city_loc_ncntr; index++) {
			testCityLocations (locations[index], index, false, false, false, false, false, true, false);
		}
		for (index = min_city_loc_adjside; index <= max_city_loc_adjside; index++) {
			testCityLocations (locations[index], index, false, false, false, false, false, false, true);
		}
		assertFalse(centerLocation.isCityAdjacentSide(), "City Center is NOT City Adjcent");
	}
	
	@Test
	@DisplayName ("Test Rotating Locations Set 1")
	public void LocationRotationTestsSet1 () {
		Location tLocation1;
		Location tLocation2;
		
		tLocation1 = locations [0].clone ();
		tLocation2 = tLocation1.rotateLocation(2);
		assertEquals (0, tLocation1.getLocation (), "Side Rotation Start");
		assertEquals (0, tLocation1.getLocation (), "Side Rotation After");
		assertEquals (2, tLocation2.getLocation (), "Side Rotation After");
		tLocation2 = tLocation1.rotateLocation(7);
		assertEquals (1, tLocation2.getLocation (), "Side Rotation After");
		
		tLocation1 = locations [7].clone ();
		tLocation2 = tLocation1.rotateLocation(1);
		assertEquals (7, tLocation1.getLocation (), "City Hex Side Rotation Start");
		assertEquals (7, tLocation1.getLocation (), "City Hex Side Rotation After");
		assertEquals (8, tLocation2.getLocation (), "City Hex Side Rotation After");
		tLocation2 = tLocation1.rotateLocation(8);
		assertEquals (9, tLocation2.getLocation (), "City Hex Side Rotation After");
		
		tLocation1 = locations [14].clone ();
		tLocation2 = tLocation1.rotateLocation(3);
		assertEquals (14, tLocation1.getLocation (), "City Hex Corner Rotation Start");
		assertEquals (14, tLocation1.getLocation (), "City Hex Corner Rotation After");
		assertEquals (17, tLocation2.getLocation (), "City Hex Corner Rotation After");
		tLocation2 = tLocation1.rotateLocation(9);
		assertEquals (17, tLocation2.getLocation (), "City Hex Corner Rotation After");
		
		tLocation1 = locations [21].clone ();
		tLocation2 = tLocation1.rotateLocation(4);
		assertEquals (21, tLocation1.getLocation (), "City Far Hex Side Rotation Start");
		assertEquals (21, tLocation1.getLocation (), "City Far Hex Side Rotation After");
		assertEquals (19, tLocation2.getLocation (), "City Far Hex Side Rotation After");
		tLocation2 = tLocation1.rotateLocation(11);
		assertEquals (20, tLocation2.getLocation (), "City Far Hex Side Rotation After");

		tLocation1 = locations [24].clone ();
		tLocation2 = tLocation1.rotateLocation (5);
		assertEquals (24, tLocation1.getLocation (), "City Far Hex Corner Left Rotation Start");
		assertEquals (24, tLocation1.getLocation (), "City Far Hex Corner Left Rotation After");
		assertEquals (29, tLocation2.getLocation (), "City Far Hex Corner Left Rotation After");
		tLocation2 = tLocation1.rotateLocation(7);
		assertEquals (25, tLocation2.getLocation (), "City Far Hex Corner Left Rotation After");

		tLocation1 = locations [30].clone ();
		tLocation2 = tLocation1.rotateLocation (1);
		assertEquals (30, tLocation1.getLocation (), "City Far Hex Corner Right Rotation Start");
		assertEquals (30, tLocation1.getLocation (), "City Far Hex Corner Right Rotation After");
		assertEquals (31, tLocation2.getLocation (), "City Far Hex Corner Right Rotation After");
		tLocation2 = tLocation1.rotateLocation(8);
		assertEquals (32, tLocation2.getLocation (), "City Far Hex Corner Right Rotation After");

		tLocation1 = locations [36].clone ();
		tLocation2 = tLocation1.rotateLocation (3);
		assertEquals (36, tLocation1.getLocation (), "City Near Center Rotation Start");
		assertEquals (36, tLocation1.getLocation (), "City Near Center Rotation After");
		assertEquals (39, tLocation2.getLocation (), "City Near Center Rotation After");
		tLocation2 = tLocation1.rotateLocation (6);
		assertTrue (tLocation2.isCityNearCenter(), "City Near Center");
		assertEquals (36, tLocation2.getLocation (), "City Near Center Rotation After");

		tLocation1 = locations [40].clone ();
		tLocation2 = tLocation1.rotateLocation (1);
		assertEquals (40, tLocation1.getLocation (), "City Adjacent Side Rotation Start");
		assertEquals (40, tLocation1.getLocation (), "City Adjacent Side Rotation After");
		assertEquals (41, tLocation2.getLocation (), "City Adjacent Side Rotation After");
		tLocation2 = tLocation1.rotateLocation (8);
		assertEquals (42, tLocation2.getLocation (), "City Adjacent Side Rotation After");

		tLocation1 = deadEndLocation.clone ();
		tLocation2 = tLocation1.rotateLocation (1);
		assertEquals (99, tLocation1.getLocation (), "Dead End Rotation Start");
		assertEquals (99, tLocation1.getLocation (), "Dead End Side Rotation After");
		assertEquals (99, tLocation2.getLocation (), "Dead End Side Rotation After");
	}
	
	@Test
	@DisplayName ("Test Unrotating (Reverse Rotation) of Locations")
	public void LocationUnRotationTestsSet1 () {
		Location tLocation1;
		Location tLocation2;
		
		tLocation1 = locations [0].clone ();
		tLocation2 = tLocation1.unrotateLocation (2);
		assertEquals (0, tLocation1.getLocation (), "Side UnRotation Start");
		assertEquals (4, tLocation2.getLocation (), "Side UnRotation After");
		
		tLocation1 = locations [6].clone ();
		tLocation2 = tLocation1.unrotateLocation (1);
		assertEquals (6, tLocation1.getLocation (), "City Hex Side UnRotation Start");
		assertEquals (6, tLocation1.getLocation (), "City Hex Side UnRotation After");
		assertEquals (11, tLocation2.getLocation (), "City Hex Side UnRotation After");
		
		tLocation1 = locations [14].clone ();
		tLocation2 = tLocation1.unrotateLocation (3);
		assertEquals (14, tLocation1.getLocation (), "City Hex Corner UnRotation Start");
		assertEquals (14, tLocation1.getLocation (), "City Hex Corner UnRotation After");
		assertEquals (17, tLocation2.getLocation (), "City Hex Corner UnRotation After");
		
		tLocation1 = locations [21].clone ();
		tLocation2 = tLocation1.unrotateLocation (4);
		assertEquals (21, tLocation1.getLocation (), "City Far Hex Side UnRotation Start");
		assertEquals (21, tLocation1.getLocation (), "City Far Hex Side UnRotation After");
		assertEquals (23, tLocation2.getLocation (), "City Far Hex Side UnRotation After");

		tLocation1 = locations [24].clone ();
		tLocation2 = tLocation1.unrotateLocation (5);
		assertEquals (24, tLocation1.getLocation (), "City Far Hex Corner Left UnRotation Start");
		assertEquals (24, tLocation1.getLocation (), "City Far Hex Corner Left UnRotation After");
		assertEquals (25, tLocation2.getLocation (), "City Far Hex Corner Left UnRotation After");

		tLocation1 = locations [30].clone ();
		tLocation2 = tLocation1.unrotateLocation (1);
		assertEquals (30, tLocation1.getLocation (), "City Far Hex Corner Right UnRotation Start");
		assertEquals (30, tLocation1.getLocation (), "City Far Hex Corner Right UnRotation After");
		assertEquals (35, tLocation2.getLocation (), "City Far Hex Corner Right UnRotation After");

		tLocation1 = locations [36].clone ();
		tLocation2 = tLocation1.unrotateLocation (3);
		assertEquals (36, tLocation1.getLocation (), "City Near Center UnRotation Start");
		assertEquals (36, tLocation1.getLocation (), "City Near Center UnRotation After");
		assertEquals (37, tLocation2.getLocation (), "City Near Center UnRotation After");
		tLocation2 = tLocation1.unrotateLocation (5);
		assertEquals (36, tLocation2.getLocation (), "City Near Center UnRotation After");

		tLocation1 = locations [40].clone ();
		tLocation2 = tLocation1.unrotateLocation (1);
		assertEquals (40, tLocation1.getLocation (), "City Adjacent Side UnRotation Start");
		assertEquals (40, tLocation1.getLocation (), "City Adjacent Side UnRotation After");
		assertEquals (45, tLocation2.getLocation (), "City Adjacent Side UnRotation After");

		tLocation1 = deadEndLocation.clone ();
		tLocation2 = tLocation1.unrotateLocation (1);
		assertEquals (99, tLocation1.getLocation (), "Dead End UnRotation Start");
		assertEquals (99, tLocation1.getLocation (), "Dead End Side UnRotation After");
		assertEquals (99, tLocation2.getLocation (), "Dead End Side UnRotation After");
	}

	@Test
	public void LocationRotationTestsSet2 () {
		Location tLocation1;
		Location tLocation2;
		
		tLocation1 = locations [0].clone ();
		assertEquals (0, tLocation1.getLocation (), "Side Rotation Start");
		tLocation1.rotateLocation1Tick ();
		assertEquals (1, tLocation1.getLocation (), "Side Rotation 1 Tick After");
		tLocation1.rotateLocation1Tick ();
		assertEquals (2, tLocation1.getLocation (), "Side Rotation 1 Tick After");
		tLocation1.rotateLocation1Tick ();
		assertEquals (3, tLocation1.getLocation (), "Side Rotation 1 Tick After");
		tLocation1.rotateLocation1Tick ();
		assertEquals (4, tLocation1.getLocation (), "Side Rotation 1 Tick After");
		tLocation1.rotateLocation1Tick ();
		assertEquals (5, tLocation1.getLocation (), "Side Rotation 1 Tick After");
		tLocation1.rotateLocation2Tick ();
		assertEquals (1, tLocation1.getLocation (), "Side Rotation 2 Ticks After");
		tLocation1.rotateLocation2Tick ();
		assertEquals (3, tLocation1.getLocation (), "Side Rotation 2 Ticks After");
		tLocation1.rotateLocation2Tick ();
		assertEquals (5, tLocation1.getLocation (), "Side Rotation 2 Ticks After");
		tLocation1.rotateLocation1Tick ();
		assertEquals (0, tLocation1.getLocation (), "Side Rotation 1 Tick After");
		tLocation1.rotateLocation2Tick ();
		assertEquals (2, tLocation1.getLocation (), "Side Rotation 2 Ticks After");
		tLocation1.rotateLocation2Tick ();
		assertEquals (4, tLocation1.getLocation (), "Side Rotation 2 Ticks After");		

		tLocation1 = locations [7].clone ();
		tLocation1.rotateLocation180 ();
		assertEquals (10, tLocation1.getLocation (), "City Near Side Rotation After 180");		
		tLocation1.rotateLocation180 ();
		assertEquals (7, tLocation1.getLocation (), "City Near Side Rotation After 180");		
		tLocation2 = centerLocation.clone ();
		assertEquals (50, tLocation2.getLocation (), "City Center Rotation After 180");		
		tLocation2 = noLocation.clone ();
		assertEquals (-1, tLocation2.getLocation (), "City Center Rotation After 180");		
		
		tLocation1 = locations [40].clone ();
		tLocation1.rotateLocation180 ();
		assertEquals (43, tLocation1.getLocation (), "City Near Side Rotation After 180");		
		tLocation1.rotateLocation180 ();
		assertEquals (40, tLocation1.getLocation (), "City Near Side Rotation After 180");		
		
		tLocation2 = locations [36].clone ();
		tLocation2.rotateLocation180 ();
		assertEquals (38, tLocation2.getLocation (), "City Near Center Rotation After 180");		
		tLocation2.rotateLocation180 ();
		assertEquals (36, tLocation2.getLocation (), "City Near Center Rotation After 180");		
		
		tLocation2 = deadEndLocation.clone ();
		tLocation2.rotateLocation180 ();
		assertEquals (99, tLocation2.getLocation (), "Dead End Location Rotation After 180");		

		tLocation2 = noLocation.clone ();
		tLocation2.rotateLocation180 ();
		assertEquals (-1, tLocation2.getLocation (), "No Location Rotation After 180");		
	}
	
	@Test
	public void LocationMiscTests () {
		assertEquals ("50", centerLocation.toString (), "Test String Conversion");
	}
	
	@Test
	@DisplayName ("Test Two Opposite Sides are indeed Opposite Sides")
	public void LocationSidetoSideTests () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "Side-to-Side Opposite %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index];
			tLocation2 = locations [(index + 3) % 6];
			tLabelFinal = String.format (tLabel, index, (index + 3) % 6);
			assertTrue (tLocation1.isOppositeSide (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index, (index + 1) % 6);
			assertFalse (tLocation1.isOppositeSide (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations[2].clone();
		tLocation2 = locations[6].clone();

		assertFalse (tLocation1.isOppositeSide (tLocation2), "Side-to-Non-Side 2-6");
		assertFalse (tLocation2.isOppositeSide (tLocation1), "NonSide-to-Side 6-2");
	}
	
	@Test
	public void LocationIsCitySideClose () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Close to Hex Side %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 6];
			tLocation2 = locations [index];
			tLabelFinal = String.format (tLabel, index + 6, (index + 3) % 6);
			assertTrue (tLocation1.isClose(tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index + 6, (index + 1) % 6);
			assertFalse (tLocation1.isClose (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [11].clone ();
		tLocation2 = locations [5].clone ();
		
		assertFalse (tLocation2.isClose (tLocation1), "Hex Side not Close to City 0-11");
	}
	
	@Test
	public void LocationIsCitySideFarOpposite () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Adjacent to Far Opposite Hex Side %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 6];
			tLocation2 = locations [(index + 3) % 6];
			tLabelFinal = String.format (tLabel, index + 6, (index + 3) % 6);
			assertTrue (tLocation1.isFarOpposite (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index + 6, (index + 1) % 6);
			assertFalse (tLocation1.isFarOpposite (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [11].clone ();
		tLocation2 = locations [0].clone ();
				
		assertFalse (tLocation2.isFarOpposite (tLocation1), "Hex Side not Close to City 0-11");
	}
	
	@Test
	public void LocationIsCityAdjacentSide () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Adjacent to Hex Side %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 40];
			tLocation2 = locations [index];
			tLabelFinal = String.format (tLabel, index + 40, index);
			assertTrue (tLocation1.isAdjacent (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index + 40, (index + 1) % 6);
			assertFalse (tLocation1.isAdjacent (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [40].clone ();
		tLocation2 = locations [0].clone ();
		
		
		assertFalse (tLocation2.isAdjacent (tLocation1), "City is Not a City Adjacent to Side");
	}
	
	@Test
	public void LocationIsCityAdjacentBackward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Adjacent Backward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 12];
			tLocation2 = locations [index];
			tLabelFinal = String.format (tLabel, index + 12, index);
			assertTrue (tLocation1.isAdjacentBackward (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index + 12, (index + 1) % 6);
			assertFalse (tLocation1.isAdjacentBackward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [12].clone ();
		tLocation2 = locations [0].clone ();
				
		assertFalse (tLocation2.isAdjacentBackward (tLocation1), "City is Not a City Adjacent Backward");
	}
	
	@Test
	public void LocationIsCityAdjacentFarBackward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Adjacent Far Backward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 30];
			tLocation2 = locations [index];
			tLabelFinal = String.format (tLabel, index + 30, index);
			assertTrue (tLocation1.isAdjacentFarBackward (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index + 30, (index + 1) % 6);
			assertFalse (tLocation1.isAdjacentFarBackward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [35].clone ();
		tLocation2 = locations [0].clone ();
		
		assertFalse (tLocation2.isAdjacentFarBackward (tLocation1), "City is Not a City Adjacent Far Backward");
	}
	
	@Test
	public void LocationIsCityForward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Forward %d-%d ";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 18];
			tLocation2 = locations [(index + 5) % 6];
			tLabelFinal = String.format (tLabel, index + 18, (index + 5) % 6);
			assertTrue (tLocation1.isForward (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 2) % 6];
			tLabelFinal = String.format (tLabel, index + 18, (index + 2) % 6);
			assertFalse (tLocation1.isForward (tLocation2), tLabelFinal);		
		}

		tLocation1 = locations [23].clone ();
		tLocation2 = locations [0].clone ();
		assertFalse (tLocation2.isForward (tLocation1), "City is Not a City Forward");
	}
	
	@Test
	public void LocationIsCityBackward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Backward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 18];
			tLocation2 = locations [(index + 1) % 6];
			tLabelFinal = String.format (tLabel, index + 18, (index + 1) % 6);
			assertTrue (tLocation1.isBackward (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 2) % 6];
			tLabelFinal = String.format (tLabel, index + 18, (index + 2) % 6);
			assertFalse (tLocation1.isBackward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [18].clone ();
		tLocation2 = locations [3].clone ();
		assertFalse (tLocation2.isBackward (tLocation1), "City is Not a City Backward");
	}
	
	@Test
	public void LocationIsCityFarAdjacentForward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Far Adjacent Forward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 30];
			tLocation2 = locations [(index + 4) % 6];
			tLabelFinal = String.format (tLabel, index + 30, (index + 4) % 6);
			assertTrue (tLocation1.isFarAdjacentForward(tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 2) % 6];
			tLabelFinal = String.format (tLabel, index + 30, (index + 2) % 6);
			assertFalse (tLocation1.isFarAdjacentForward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [30].clone ();
		tLocation2 = locations [3].clone ();
		assertFalse (tLocation2.isFarAdjacentForward (tLocation1), "City is Not Far Adjacent Forward");
	}
	
	@Test
	public void LocationIsCityFarAdjacentBackward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Far Adjacent Backward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [(index + 5) % 6 + 24];
			tLocation2 = locations [index];
			tLabelFinal = String.format (tLabel, (index + 5) % 6 + 24, index);
			assertTrue (tLocation1.isFarAdjacentBackward (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 2) % 6];
			tLabelFinal = String.format (tLabel, (index + 5) % 6 + 24, (index + 2) % 6);
			assertFalse (tLocation1.isFarAdjacentBackward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [24];
		tLocation2 = locations [3];
		assertFalse (tLocation2.isFarAdjacentBackward (tLocation1), "City is Not Far Adjacent Backward");
	}
	
	@Test
	public void LocationIsCityAdjacentFarForward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Adjacent Far Forward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 24];
			tLocation2 = locations [(index + 5) % 6];
			tLabelFinal = String.format (tLabel, index + 24, (index + 5) % 6);
			assertTrue (tLocation1.isAdjacentFarForward (tLocation2), tLabelFinal);
			tLocation2 = locations [(index + 2) % 6];
			tLabelFinal = String.format(tLabel, index + 24, (index + 2) % 6);
			assertFalse (tLocation1.isAdjacentFarForward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [24];
		tLocation2 = locations [3];
		assertFalse (tLocation2.isAdjacentFarForward (tLocation1), "City is Not Far Adjacent Forward");
	}
	
	@Test
	public void LocationIsCityAdjacentForward () {
		Location tLocation1;
		Location tLocation2;
		String tLabel = "City is Adjacent Far Forward %d-%d";
		String tLabelFinal;
		
		for (int index = 0; index < 6; index++) {
			tLocation1 = locations [index + 12];
			tLocation2 = locations [(index + 5) % 6];
			tLabelFinal = String.format (tLabel, index + 12, (index + 5) % 6);
			assertTrue (tLocation1.isAdjacentForward(tLocation2), tLabelFinal);
			tLocation2 = locations[(index + 2) % 6];
			tLabelFinal = String.format (tLabel, index + 24, (index + 2) % 6);
			assertFalse (tLocation1.isAdjacentForward (tLocation2), tLabelFinal);		
		}
		
		tLocation1 = locations [12];
		tLocation2 = locations [3];
		assertFalse (tLocation2.isAdjacentForward (tLocation1), "City is Not  Adjacent Forward");
	}
}
