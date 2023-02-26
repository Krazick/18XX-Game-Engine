package ge18xx.center;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Testing Revenue Center Types")
class RevenueCenterTypeTests {

	RevenueCenterType noRevenueCenter;
	RevenueCenterType smallTown;
	RevenueCenterType twoSmallTowns;
	RevenueCenterType singleCity;
	RevenueCenterType twoCities;
	RevenueCenterType threeCities;
	RevenueCenterType fourCities;
	RevenueCenterType fiveCities;
	RevenueCenterType sixCities;
	RevenueCenterType doubleCity;
	RevenueCenterType tripleCity;
	RevenueCenterType quadCity;
	RevenueCenterType twoDoubleCities;
	RevenueCenterType deadEndCity;
	RevenueCenterType deadEndOnlyCity;
	RevenueCenterType destinationCity;
	RevenueCenterType bypassCity;
	RevenueCenterType dotTown;
	RevenueCenterType privateRailwayPoint;
	RevenueCenterType runThroughCity;

	@BeforeEach
	public void setUp () throws Exception {
		noRevenueCenter = new RevenueCenterType ();
		smallTown = new RevenueCenterType (RevenueCenterType.SMALL_TOWN);
		twoSmallTowns = new RevenueCenterType (RevenueCenterType.TWO_SMALL_TOWNS);
		singleCity = new RevenueCenterType (RevenueCenterType.SINGLE_CITY);
		twoCities = new RevenueCenterType (RevenueCenterType.TWO_CITIES);
		threeCities = new RevenueCenterType (RevenueCenterType.THREE_CITIES);
		fourCities = new RevenueCenterType (RevenueCenterType.FOUR_CITIES);
		fiveCities = new RevenueCenterType (RevenueCenterType.FIVE_CITIES);
		sixCities = new RevenueCenterType (RevenueCenterType.SIX_CITIES);
		doubleCity = new RevenueCenterType (RevenueCenterType.DOUBLE_CITY);
		tripleCity = new RevenueCenterType (RevenueCenterType.TRIPLE_CITY);
		quadCity = new RevenueCenterType (RevenueCenterType.QUAD_CITY);
		twoDoubleCities = new RevenueCenterType (RevenueCenterType.TWO_DOUBLE_CITIES);
		deadEndCity = new RevenueCenterType (RevenueCenterType.DEAD_END_CITY);
		deadEndOnlyCity = new RevenueCenterType (RevenueCenterType.DEAD_END_ONLY_CITY);
		destinationCity = new RevenueCenterType (RevenueCenterType.DESTINATION_CITY);
		bypassCity = new RevenueCenterType (RevenueCenterType.BYPASS_CITY);
		dotTown = new RevenueCenterType (RevenueCenterType.DOT_TOWN);
		runThroughCity = new RevenueCenterType (RevenueCenterType.RUN_THROUGH_CITY);
		privateRailwayPoint = new RevenueCenterType (RevenueCenterType.PRIVATE_RAILWAY_POINT);
	}

	@AfterEach
	public void tearDown () throws Exception {
	}

	private void testRCEqualsMaxStations (int aExpectedValue, RevenueCenterType aRevenueCenterType) {
		assertEquals (aExpectedValue, aRevenueCenterType.getMaxStations (),
				"Max Station Result for [" + aRevenueCenterType.getName () + "]");
	}

	@Test
	@DisplayName ("Verify Maximum Stations per Revenue Center Type")
	public void testMaxStationCounts () {
		testRCEqualsMaxStations (0, noRevenueCenter);
		testRCEqualsMaxStations (0, privateRailwayPoint);
		testRCEqualsMaxStations (0, runThroughCity);
		testRCEqualsMaxStations (0, smallTown);
		testRCEqualsMaxStations (0, twoSmallTowns);
		testRCEqualsMaxStations (0, dotTown);
		testRCEqualsMaxStations (1, destinationCity);
		testRCEqualsMaxStations (1, singleCity);
		testRCEqualsMaxStations (0, deadEndCity);
		testRCEqualsMaxStations (0, deadEndOnlyCity);
		testRCEqualsMaxStations (0, bypassCity);
		testRCEqualsMaxStations (2, twoCities);
		testRCEqualsMaxStations (2, doubleCity);
		testRCEqualsMaxStations (3, threeCities);
		testRCEqualsMaxStations (3, tripleCity);
		testRCEqualsMaxStations (4, fourCities);
		testRCEqualsMaxStations (4, quadCity);
		testRCEqualsMaxStations (4, twoDoubleCities);
		testRCEqualsMaxStations (5, fiveCities);
		testRCEqualsMaxStations (6, sixCities);
	}

	private void testRCEqualsStationCounts (int aExpectedValue, RevenueCenterType aRevenueCenterType) {
		assertEquals (aExpectedValue, aRevenueCenterType.getStationCount (),
				"Station Counts Result for [" + aRevenueCenterType.getName () + "]");
	}

	@Test
	@DisplayName ("Verify Station Counts per Revenue Center Types")
	public void testStationCounts () {
		testRCEqualsStationCounts (0, noRevenueCenter);
		testRCEqualsStationCounts (0, privateRailwayPoint);
		testRCEqualsStationCounts (0, smallTown);
		testRCEqualsStationCounts (0, twoSmallTowns);
		testRCEqualsStationCounts (0, dotTown);
		testRCEqualsStationCounts (0, destinationCity);
		testRCEqualsStationCounts (0, runThroughCity);
		testRCEqualsStationCounts (1, singleCity);
		testRCEqualsStationCounts (0, deadEndCity);
		testRCEqualsStationCounts (0, deadEndOnlyCity);
		testRCEqualsStationCounts (0, bypassCity);
		testRCEqualsStationCounts (1, twoCities);
		testRCEqualsStationCounts (2, doubleCity);
		testRCEqualsStationCounts (1, threeCities);
		testRCEqualsStationCounts (3, tripleCity);
		testRCEqualsStationCounts (1, fourCities);
		testRCEqualsStationCounts (4, quadCity);
		testRCEqualsStationCounts (2, twoDoubleCities);
		testRCEqualsStationCounts (1, fiveCities);
		testRCEqualsStationCounts (1, sixCities);
	}

	private void testRCEqualsCenterCounts (int aExpectedValue, RevenueCenterType aRevenueCenterType) {
		assertEquals (aExpectedValue, aRevenueCenterType.getCenterCount (),
				"Center Counts Result for [" + aRevenueCenterType.getName () + "]");
	}

	@Test
	@DisplayName ("Verify Center Counts per Revenue Center Types")
	public void testCenterCounts () {
		testRCEqualsCenterCounts (0, noRevenueCenter);
		testRCEqualsCenterCounts (0, privateRailwayPoint);
		testRCEqualsCenterCounts (1, smallTown);
		testRCEqualsCenterCounts (2, twoSmallTowns);
		testRCEqualsCenterCounts (1, dotTown);
		testRCEqualsCenterCounts (1, destinationCity);
		testRCEqualsCenterCounts (1, singleCity);
		testRCEqualsCenterCounts (0, runThroughCity);
		testRCEqualsCenterCounts (1, deadEndCity);
		testRCEqualsCenterCounts (1, deadEndOnlyCity);
		testRCEqualsCenterCounts (1, bypassCity);
		testRCEqualsCenterCounts (2, twoCities);
		testRCEqualsCenterCounts (2, doubleCity);
		testRCEqualsCenterCounts (3, threeCities);
		testRCEqualsCenterCounts (4, fourCities);
		testRCEqualsCenterCounts (4, quadCity);
		testRCEqualsCenterCounts (4, twoDoubleCities);
		testRCEqualsCenterCounts (5, fiveCities);
		testRCEqualsCenterCounts (6, sixCities);
	}

	private void testRCBooleanCanPlaceStation (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.canPlaceStation (),
					"Can Place Station Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.canPlaceStation (),
					"Cannot Place Station Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify can place Station (or not) per Revenue Center Types")
	public void testCanPlaceStation () {
		testRCBooleanCanPlaceStation (false, noRevenueCenter);
		testRCBooleanCanPlaceStation (false, privateRailwayPoint);
		testRCBooleanCanPlaceStation (false, smallTown);
		testRCBooleanCanPlaceStation (false, twoSmallTowns);
		testRCBooleanCanPlaceStation (false, dotTown);
		testRCBooleanCanPlaceStation (false, destinationCity);
		testRCBooleanCanPlaceStation (false, runThroughCity);
		testRCBooleanCanPlaceStation (true, singleCity);
		testRCBooleanCanPlaceStation (true, deadEndCity);
		testRCBooleanCanPlaceStation (true, deadEndOnlyCity);
		testRCBooleanCanPlaceStation (true, bypassCity);
		testRCBooleanCanPlaceStation (true, twoCities);
		testRCBooleanCanPlaceStation (true, doubleCity);
		testRCBooleanCanPlaceStation (true, threeCities);
		testRCBooleanCanPlaceStation (true, tripleCity);
		testRCBooleanCanPlaceStation (true, fourCities);
		testRCBooleanCanPlaceStation (true, quadCity);
		testRCBooleanCanPlaceStation (true, twoDoubleCities);
		testRCBooleanCanPlaceStation (true, fiveCities);
		testRCBooleanCanPlaceStation (true, sixCities);
	}

	private void testRCBooleanIsCity (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isCity (), "Is A City Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isCity (), "Is A City Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify if this is a City (or not) per Revenue Center Types")
	public void testIsCity () {
		testRCBooleanIsCity (false, noRevenueCenter);
		testRCBooleanIsCity (false, privateRailwayPoint);
		testRCBooleanIsCity (false, smallTown);
		testRCBooleanIsCity (false, twoSmallTowns);
		testRCBooleanIsCity (false, dotTown);
		testRCBooleanIsCity (true, destinationCity);
		testRCBooleanIsCity (true, runThroughCity);
		testRCBooleanIsCity (true, singleCity);
		testRCBooleanIsCity (true, deadEndCity);
		testRCBooleanIsCity (true, deadEndOnlyCity);
		testRCBooleanIsCity (true, bypassCity);
		testRCBooleanIsCity (true, twoCities);
		testRCBooleanIsCity (true, doubleCity);
		testRCBooleanIsCity (true, threeCities);
		testRCBooleanIsCity (true, tripleCity);
		testRCBooleanIsCity (true, fourCities);
		testRCBooleanIsCity (true, quadCity);
		testRCBooleanIsCity (true, twoDoubleCities);
		testRCBooleanIsCity (true, fiveCities);
		testRCBooleanIsCity (true, sixCities);
	}

	private void testRCBooleanIsDotTown (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isDotTown (),
					"Is Dot Town Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isDotTown (),
					"Is Not Dot Town Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a Dot Town (or not) per Revenue Center Types")
	public void testIsDotTown () {
		testRCBooleanIsDotTown (false, noRevenueCenter);
		testRCBooleanIsDotTown (false, privateRailwayPoint);
		testRCBooleanIsDotTown (false, smallTown);
		testRCBooleanIsDotTown (false, twoSmallTowns);
		testRCBooleanIsDotTown (true, dotTown);
		testRCBooleanIsDotTown (false, destinationCity);
		testRCBooleanIsDotTown (false, runThroughCity);
		testRCBooleanIsDotTown (false, singleCity);
		testRCBooleanIsDotTown (false, deadEndCity);
		testRCBooleanIsDotTown (false, deadEndOnlyCity);
		testRCBooleanIsDotTown (false, bypassCity);
		testRCBooleanIsDotTown (false, twoCities);
		testRCBooleanIsDotTown (false, doubleCity);
		testRCBooleanIsDotTown (false, threeCities);
		testRCBooleanIsDotTown (false, tripleCity);
		testRCBooleanIsDotTown (false, fourCities);
		testRCBooleanIsDotTown (false, quadCity);
		testRCBooleanIsDotTown (false, twoDoubleCities);
		testRCBooleanIsDotTown (false, fiveCities);
		testRCBooleanIsDotTown (false, sixCities);
	}

	private void testRCBooleanIsCityOrTown (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.cityOrTown (),
					"Is a City or Town Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.cityOrTown (),
					"Is not a City or Town Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a City or a Town per Revenue Center Types")
	public void testIsCityOrTown () {
		testRCBooleanIsCityOrTown (false, noRevenueCenter);
		testRCBooleanIsCityOrTown (true, privateRailwayPoint);
		testRCBooleanIsCityOrTown (true, smallTown);
		testRCBooleanIsCityOrTown (true, twoSmallTowns);
		testRCBooleanIsCityOrTown (true, dotTown);
		testRCBooleanIsCityOrTown (true, destinationCity);
		testRCBooleanIsCityOrTown (true, runThroughCity);
		testRCBooleanIsCityOrTown (true, singleCity);
		testRCBooleanIsCityOrTown (true, deadEndCity);
		testRCBooleanIsCityOrTown (true, deadEndOnlyCity);
		testRCBooleanIsCityOrTown (true, bypassCity);
		testRCBooleanIsCityOrTown (true, twoCities);
		testRCBooleanIsCityOrTown (true, doubleCity);
		testRCBooleanIsCityOrTown (true, threeCities);
		testRCBooleanIsCityOrTown (true, tripleCity);
		testRCBooleanIsCityOrTown (true, fourCities);
		testRCBooleanIsCityOrTown (true, quadCity);
		testRCBooleanIsCityOrTown (true, twoDoubleCities);
		testRCBooleanIsCityOrTown (true, fiveCities);
		testRCBooleanIsCityOrTown (true, sixCities);
	}

	private void testRCBooleanIsDestination (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isDestination (),
					"Is a Destination Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isDestination (),
					"Is not a Destination Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a Destination per Revenue Center Types")
	public void testIsDestination () {
		testRCBooleanIsDestination (false, noRevenueCenter);
		testRCBooleanIsDestination (false, privateRailwayPoint);
		testRCBooleanIsDestination (false, smallTown);
		testRCBooleanIsDestination (false, twoSmallTowns);
		testRCBooleanIsDestination (false, dotTown);
		testRCBooleanIsDestination (true, destinationCity);
		testRCBooleanIsDestination (false, runThroughCity);
		testRCBooleanIsDestination (false, singleCity);
		testRCBooleanIsDestination (false, deadEndCity);
		testRCBooleanIsDestination (false, deadEndOnlyCity);
		testRCBooleanIsDestination (false, bypassCity);
		testRCBooleanIsDestination (false, twoCities);
		testRCBooleanIsDestination (false, doubleCity);
		testRCBooleanIsDestination (false, threeCities);
		testRCBooleanIsDestination (false, tripleCity);
		testRCBooleanIsDestination (false, fourCities);
		testRCBooleanIsDestination (false, quadCity);
		testRCBooleanIsDestination (false, twoDoubleCities);
		testRCBooleanIsDestination (false, fiveCities);
		testRCBooleanIsDestination (false, sixCities);
	}

	private void testRCBooleanIsPrivateRailway (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isPrivateRailway (),
					"Is a Private Railway Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isPrivateRailway (),
					"Is not a Private Railway Result for [" + aRevenueCenterType.getName () + "]");
		}
	}
	
	@Test
	@DisplayName ("Verify Center is a Run Through City per Revenue Center Types")
	public void testIsRunThroughCity () {
		testRCBooleanIsRunThrough (false, noRevenueCenter);
		testRCBooleanIsRunThrough (false, privateRailwayPoint);
		testRCBooleanIsRunThrough (false, smallTown);
		testRCBooleanIsRunThrough (false, twoSmallTowns);
		testRCBooleanIsRunThrough (false, dotTown);
		testRCBooleanIsRunThrough (false, destinationCity);
		testRCBooleanIsRunThrough (true, runThroughCity);
		testRCBooleanIsRunThrough (false, singleCity);
		testRCBooleanIsRunThrough (false, deadEndCity);
		testRCBooleanIsRunThrough (false, deadEndOnlyCity);
		testRCBooleanIsRunThrough (false, bypassCity);
		testRCBooleanIsRunThrough (false, twoCities);
		testRCBooleanIsRunThrough (false, doubleCity);
		testRCBooleanIsRunThrough (false, threeCities);
		testRCBooleanIsRunThrough (false, tripleCity);
		testRCBooleanIsRunThrough (false, fourCities);
		testRCBooleanIsRunThrough (false, quadCity);
		testRCBooleanIsRunThrough (false, twoDoubleCities);
		testRCBooleanIsRunThrough (false, fiveCities);
		testRCBooleanIsRunThrough (false, sixCities);
	}

	private void testRCBooleanIsRunThrough (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isARunThroughCity (), "Is a Run Through City Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isARunThroughCity (),
					"Is not a Run Through City Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a Private Railway per Revenue Center Types")
	public void testIsPrivateRailway () {
		testRCBooleanIsPrivateRailway (false, noRevenueCenter);
		testRCBooleanIsPrivateRailway (true, privateRailwayPoint);
		testRCBooleanIsPrivateRailway (false, smallTown);
		testRCBooleanIsPrivateRailway (false, twoSmallTowns);
		testRCBooleanIsPrivateRailway (false, dotTown);
		testRCBooleanIsPrivateRailway (false, destinationCity);
		testRCBooleanIsPrivateRailway (false, runThroughCity);
		testRCBooleanIsPrivateRailway (false, singleCity);
		testRCBooleanIsPrivateRailway (false, deadEndCity);
		testRCBooleanIsPrivateRailway (false, deadEndOnlyCity);
		testRCBooleanIsPrivateRailway (false, bypassCity);
		testRCBooleanIsPrivateRailway (false, twoCities);
		testRCBooleanIsPrivateRailway (false, doubleCity);
		testRCBooleanIsPrivateRailway (false, threeCities);
		testRCBooleanIsPrivateRailway (false, tripleCity);
		testRCBooleanIsPrivateRailway (false, fourCities);
		testRCBooleanIsPrivateRailway (false, quadCity);
		testRCBooleanIsPrivateRailway (false, twoDoubleCities);
		testRCBooleanIsPrivateRailway (false, fiveCities);
		testRCBooleanIsPrivateRailway (false, sixCities);
	}

	private void testRCBooleanIsTown (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isTown (), "Is a Town Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isTown (),
					"Is not a Town Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a Town (Single, Two Small, or Dot) per Revenue Center Types")
	public void testIsTown () {
		testRCBooleanIsTown (false, noRevenueCenter);
		testRCBooleanIsTown (false, privateRailwayPoint);
		testRCBooleanIsTown (true, smallTown);
		testRCBooleanIsTown (true, twoSmallTowns);
		testRCBooleanIsTown (true, dotTown);
		testRCBooleanIsTown (false, destinationCity);
		testRCBooleanIsTown (false, runThroughCity);
		testRCBooleanIsTown (false, singleCity);
		testRCBooleanIsTown (false, deadEndCity);
		testRCBooleanIsTown (false, deadEndOnlyCity);
		testRCBooleanIsTown (false, bypassCity);
		testRCBooleanIsTown (false, twoCities);
		testRCBooleanIsTown (false, doubleCity);
		testRCBooleanIsTown (false, threeCities);
		testRCBooleanIsTown (false, tripleCity);
		testRCBooleanIsTown (false, fourCities);
		testRCBooleanIsTown (false, quadCity);
		testRCBooleanIsTown (false, twoDoubleCities);
		testRCBooleanIsTown (false, fiveCities);
		testRCBooleanIsTown (false, sixCities);
	}

	private void testRCBooleanIsTwoTowns (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (aRevenueCenterType.isTwoTowns (),
					"Is Two Towns Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (aRevenueCenterType.isTwoTowns (),
					"Is not Two Towns Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a Double Small Town per Revenue Center Types")
	public void testIsTwoTowns () {
		testRCBooleanIsTwoTowns (false, noRevenueCenter);
		testRCBooleanIsTwoTowns (false, privateRailwayPoint);
		testRCBooleanIsTwoTowns (false, smallTown);
		testRCBooleanIsTwoTowns (true, twoSmallTowns);
		testRCBooleanIsTwoTowns (false, dotTown);
		testRCBooleanIsTwoTowns (false, destinationCity);
		testRCBooleanIsTwoTowns (false, runThroughCity);
		testRCBooleanIsTwoTowns (false, singleCity);
		testRCBooleanIsTwoTowns (false, deadEndCity);
		testRCBooleanIsTwoTowns (false, deadEndOnlyCity);
		testRCBooleanIsTwoTowns (false, bypassCity);
		testRCBooleanIsTwoTowns (false, twoCities);
		testRCBooleanIsTwoTowns (false, doubleCity);
		testRCBooleanIsTwoTowns (false, threeCities);
		testRCBooleanIsTwoTowns (false, tripleCity);
		testRCBooleanIsTwoTowns (false, fourCities);
		testRCBooleanIsTwoTowns (false, quadCity);
		testRCBooleanIsTwoTowns (false, twoDoubleCities);
		testRCBooleanIsTwoTowns (false, fiveCities);
		testRCBooleanIsTwoTowns (false, sixCities);
	}

	private void testRCBooleanIsCityFromName (boolean aExpectedValue, RevenueCenterType aRevenueCenterType) {
		if (aExpectedValue) {
			assertTrue (RevenueCenterType.isCity (aRevenueCenterType.getName ()),
					"Is is City from Name Result for [" + aRevenueCenterType.getName () + "]");
		} else {
			assertFalse (RevenueCenterType.isCity (aRevenueCenterType.getName ()),
					"Is not City from Name  Result for [" + aRevenueCenterType.getName () + "]");
		}
	}

	@Test
	@DisplayName ("Verify Center is a City from the Name per Revenue Center Types")
	public void testIsCityFromName () {
		testRCBooleanIsCityFromName (false, noRevenueCenter);
		testRCBooleanIsCityFromName (false, privateRailwayPoint);
		testRCBooleanIsCityFromName (false, smallTown);
		testRCBooleanIsCityFromName (false, twoSmallTowns);
		testRCBooleanIsCityFromName (false, dotTown);
		testRCBooleanIsCityFromName (true, destinationCity);
		testRCBooleanIsCityFromName (true, runThroughCity);
		testRCBooleanIsCityFromName (true, singleCity);
		testRCBooleanIsCityFromName (true, deadEndCity);
		testRCBooleanIsCityFromName (true, deadEndOnlyCity);
		testRCBooleanIsCityFromName (true, bypassCity);
		testRCBooleanIsCityFromName (true, twoCities);
		testRCBooleanIsCityFromName (true, doubleCity);
		testRCBooleanIsCityFromName (true, threeCities);
		testRCBooleanIsCityFromName (true, tripleCity);
		testRCBooleanIsCityFromName (true, fourCities);
		testRCBooleanIsCityFromName (true, quadCity);
		testRCBooleanIsCityFromName (true, twoDoubleCities);
		testRCBooleanIsCityFromName (true, fiveCities);
		testRCBooleanIsCityFromName (true, sixCities);
	}

	@Test
	@DisplayName ("Verify  Static Methods of Is Dot Towm Is Town")
	public void testRCTwithStrings () {
		assertTrue (RevenueCenterType.isDotTown ("Dot Town"), "Is Dot Town String Result ");
		assertFalse (RevenueCenterType.isDotTown ("Two Towns"), "Is Not Dot Town String Result ");

		assertTrue (RevenueCenterType.isTown ("Small Town"), "Is Town String Result ");
		assertTrue (RevenueCenterType.isTown ("Two Small Towns"), "Is Two Town String Result ");
		assertTrue (RevenueCenterType.isTown ("Dot Town"), "Is Dot Town String Result ");
		assertFalse (RevenueCenterType.isTown ("City"), "Is Not Town String Result ");
	}

	@Test
	@DisplayName ("Verify 'Set Type' Method is properly updated")
	public void testSetType () {
		RevenueCenterType revenueCenterType1;
		RevenueCenterType revenueCenterType2;
		RevenueCenterType revenueCenterType3;

		revenueCenterType1 = new RevenueCenterType (RevenueCenterType.SINGLE_CITY);
		revenueCenterType2 = new RevenueCenterType (RevenueCenterType.SINGLE_CITY);
		revenueCenterType3 = new RevenueCenterType (RevenueCenterType.SINGLE_CITY);

		revenueCenterType1.setType (RevenueCenterType.DOUBLE_CITY);
		assertEquals ("Double City", revenueCenterType1.getName (), "Set Type to Double City");

		revenueCenterType2.setType (-1);
		assertEquals ("No Revenue Center", revenueCenterType2.getName (), "Set Type to -1, reset to No Revenue Center");

		revenueCenterType3.setType (200);
		assertEquals ("No Revenue Center", revenueCenterType3.getName (),
				"Set Type to 200, reset to No Revenue Center ");
	}

	private void testRCIntTypeFromName (int aExpectedValue, RevenueCenterType aRevenueCenterType) {
		assertEquals (aExpectedValue, aRevenueCenterType.getTypeFromName (aRevenueCenterType.getName ()),
				"Is it Type From Name Result for [" + aRevenueCenterType.getName () + "]");
	}

	@Test
	@DisplayName ("Verify Types by Name per Revenue Center Types")
	public void testGetTypeFromName () {
		testRCIntTypeFromName (0, noRevenueCenter);
		testRCIntTypeFromName (1, smallTown);
		testRCIntTypeFromName (2, twoSmallTowns);
		testRCIntTypeFromName (3, singleCity);
		testRCIntTypeFromName (4, twoCities);
		testRCIntTypeFromName (5, threeCities);
		testRCIntTypeFromName (6, fourCities);
		testRCIntTypeFromName (7, fiveCities);
		testRCIntTypeFromName (8, sixCities);
		testRCIntTypeFromName (9, doubleCity);
		testRCIntTypeFromName (10, tripleCity);
		testRCIntTypeFromName (11, quadCity);
		testRCIntTypeFromName (12, twoDoubleCities);
		testRCIntTypeFromName (13, deadEndCity);
		testRCIntTypeFromName (14, deadEndOnlyCity);
		testRCIntTypeFromName (15, bypassCity);
		testRCIntTypeFromName (16, destinationCity);
		testRCIntTypeFromName (17, dotTown);
		testRCIntTypeFromName (18, privateRailwayPoint);
		testRCIntTypeFromName (19, runThroughCity);
	}

	@Test
	@DisplayName ("Verify Creation of Revenue Centers by Name")
	public void testConstructorWithName () {
		RevenueCenterType aRevenueCenter1 = new RevenueCenterType ("Single City");
		assertEquals (3, aRevenueCenter1.getType (), "Created Revenue Center Type with String [Single City]");
		RevenueCenterType aRevenueCenter2 = new RevenueCenterType ("BAD Revenue Center Type Name");
		assertNotEquals (3, aRevenueCenter2.getType (),
				"Created Revenue Center Type with String [BAD RevenueCenterType Name]");
		assertEquals (0, aRevenueCenter2.getType (),
				"Created Revenue Center Type with String [BAD RevenueCenterType Name]");
	}

	@Test
	@DisplayName ("Verify Closing Revenue Center Types")
	public void testCloningRevenueCenterType () {
		RevenueCenterType aRevenueCenterType = singleCity.clone ();
		String tName = aRevenueCenterType.getName ();
		assertEquals ("Single City", tName, "Created Revenue Center Type by Cloning ");
	}
}
