package ge18xx.train;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.center.City;
import ge18xx.center.RevenueCenter;
import ge18xx.center.RevenueCenterType;
import ge18xx.map.Location;
import ge18xx.tiles.TileType;

@DisplayName ("Node Information Constructor Tests")

class NodeInformationTestConstuctors {
	Location locationSide;
	Location locationCenter;
	NodeInformation nodeInformationSide;
	NodeInformation nodeInformationCenter;
	City centerCity;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
		TileType tYellow = new TileType (TileType.YELLOW, false);
		locationSide = new Location (1);
		locationCenter = new Location (50);
		centerCity = new City (RevenueCenterType.SINGLE_CITY, 1, 1, Location.CENTER_CITY_LOC, "Home Town", 20, tYellow);
		nodeInformationSide = new NodeInformation (locationSide, false, true, false, 0, 0, RevenueCenter.NO_CENTER);
		nodeInformationCenter = new NodeInformation (locationCenter, false, true, true, 0, 0, centerCity);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}
	
	@Test
	@DisplayName ("Node Information Constructor Test No Args")
	public void NodeInformationTestConstructor () {
		Location tFoundLocation;
		
		tFoundLocation = nodeInformationSide.getLocation ();
		
		assertEquals (locationSide, tFoundLocation, "Node Information Constructor - Valid Side Location");
		assertEquals (1, nodeInformationSide.getLocationInt (), "Node Information - getLocationInt");
		assertEquals (0, nodeInformationSide.getRevenue (), "Node Information -- Zero Revenue ");
		assertEquals (RevenueCenter.NO_CENTER, nodeInformationSide.getRevenueCenter (), "Node Information -- Get RevenueCenter.NO_CENTER");
		assertTrue (nodeInformationCenter.hasRevenueCenter (), "Node Information - Center Has Revenue Center");
		assertFalse (nodeInformationSide.hasRevenueCenter (), "Node Information - Side has no RevenueCenter");
	}

	@Test
	@DisplayName ("Node Information Test for isValid Method")
	public void NodeInformationIsValidMethodTests () {
		NodeInformation tNodeInformationBadLocation1;
		NodeInformation tNodeInformationBadLocation2;
		
		tNodeInformationBadLocation1 = new NodeInformation (new Location (), false, true, false, 0, 0, RevenueCenter.NO_CENTER);
		tNodeInformationBadLocation2 = new NodeInformation (null, false, true, false, 0, 0, RevenueCenter.NO_CENTER);

		assertTrue (nodeInformationSide.isValid (), "Valid Node Information for Side");
		assertTrue (nodeInformationCenter.isValid (), "Valid Node Information for Center");
		
		assertTrue (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
		assertFalse (nodeInformationCenter.isSide (), "Node Information for Center is a Side");

		assertFalse (tNodeInformationBadLocation1.isValid (), "Valid Node Information for NO_LOCATION");
		assertFalse (tNodeInformationBadLocation2.isValid (), "Valid Node Information for NULL Location");
	}
	
	@Test
	@DisplayName ("Node Information Test for setting Location")
	public void NodeInformationSetLocationTests () {
		Location tFoundLocation, tNewLocation;
		
		tFoundLocation = nodeInformationSide.getLocation ();
		assertEquals (locationSide, tFoundLocation, "Node Information Constructor - Valid Side Location");
		assertEquals (1, nodeInformationSide.getLocationInt (), "Node Information - getLocationInt");

		tNewLocation = new Location (3);
		nodeInformationSide.setLocation (tNewLocation);
		assertNotEquals (tNewLocation, tFoundLocation, "Node Information Set Location - did not Change Location Object");
		tFoundLocation = nodeInformationSide.getLocation ();
		assertEquals (tNewLocation, tFoundLocation, "Node Information reset Side - did not get New Location");
		assertNotEquals (1, nodeInformationSide.getLocationInt (), "Node Information - Location is not 3");
		assertEquals (3, nodeInformationSide.getLocationInt (), "Node Information - getLocationInt");

		assertTrue (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
		tNewLocation = new Location (10);
		nodeInformationSide.setLocation (tNewLocation);
		
		assertFalse (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
	}
}
