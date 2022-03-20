package ge18xx.train;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.mockito.Mockito;

import ge18xx.center.City;
import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;

@DisplayName ("Node Information Constructor Tests")
class NodeInformationTestConstuctors {
 	Location mLocationSide;
	Location mLocationSide1;
	Location mLocationSide3;
	Location mLocationCenter;
	NodeInformation nodeInformationSide;
	NodeInformation nodeInformationCenter;
	NodeInformation nodeInformationSide1, nodeInformationSide3;
	City centerCity;
	City mCenterCity;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
		
		mLocationSide = Mockito.mock (Location.class);
		Mockito.when (mLocationSide.getLocation ()).thenReturn (1);
		Mockito.when (mLocationSide.isSide ()).thenReturn (true);

		mLocationSide1 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide1.getLocation ()).thenReturn (1);
		Mockito.when (mLocationSide1.isSide ()).thenReturn (true);
		mLocationSide3 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide3.getLocation ()).thenReturn (3);
		Mockito.when (mLocationSide3.isSide ()).thenReturn (true);

		mLocationCenter = Mockito.mock (Location.class);
		Mockito.when (mLocationCenter.getLocation ()).thenReturn (50);
		Mockito.when (mLocationCenter.isSide ()).thenReturn (false);
		
		mCenterCity = Mockito.mock (City.class);
		Mockito.when (mCenterCity.isTown ()).thenReturn (false);
		Mockito.when (mCenterCity.isDotTown ()).thenReturn (false);
		Mockito.when (mCenterCity.isCity ()).thenReturn (true);
		Mockito.when (mCenterCity.cityHasOpenStation ()).thenReturn (true);
		
		// NodeInformation (Location aLocation, 
		//					boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
		//					int aRevenue, int aBonus, RevenueCenter aRevenueCenter)
		nodeInformationSide = new NodeInformation (mLocationSide, 
										false, true, false, 
										0, 0, RevenueCenter.NO_CENTER);
		nodeInformationCenter = new NodeInformation (mLocationCenter, 
										false, true, true, 
										20, 0, mCenterCity);
		nodeInformationSide1 = new NodeInformation (mLocationSide1, 
										false, true, false, 
										0, 0, RevenueCenter.NO_CENTER);
		nodeInformationSide3 = new NodeInformation (mLocationSide3, 
										false, true, false, 
										0, 0, RevenueCenter.NO_CENTER);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}
	
	@Test
	@DisplayName ("Constructor Test No Args")
	public void NodeInformationTestConstructor () {
		Location tFoundLocation;
		
		tFoundLocation = nodeInformationSide.getLocation ();
		
		assertEquals (mLocationSide, tFoundLocation, "Node Information Constructor - Valid Side Location");
		assertEquals (1, nodeInformationSide.getLocationInt (), "Node Information - getLocationInt");
		assertEquals (0, nodeInformationSide.getRevenue (), "Node Information -- Zero Revenue ");
		assertEquals (RevenueCenter.NO_CENTER, nodeInformationSide.getRevenueCenter (), "Node Information -- Get RevenueCenter.NO_CENTER");
		assertTrue (nodeInformationCenter.hasRevenueCenter (), "Node Information - Center Has Revenue Center");
		assertFalse (nodeInformationSide.hasRevenueCenter (), "Node Information - Side has no RevenueCenter");
		assertEquals (0, nodeInformationCenter.getBonus (), "Node Information - Bonus is not Zero");
		nodeInformationCenter.setBonus (20);
		assertEquals (20, nodeInformationCenter.getBonus (), "Node Information - Bonus is not 20");
	}

	@Test
	@DisplayName ("Test for isValid Method")
	public void NodeInformationIsValidMethodTests () {
		NodeInformation tNodeInformationBadLocation1;
		NodeInformation tNodeInformationBadLocation2;
		Location mNoLocation1;
		
		mNoLocation1 = Mockito.mock (Location.class);
		Mockito.when (mNoLocation1.getLocation ()).thenReturn (Location.NO_LOCATION);
		Mockito.when (mNoLocation1.isSide ()).thenReturn (false);

		tNodeInformationBadLocation1 = new NodeInformation (mNoLocation1, false, true, false, 0, 0, RevenueCenter.NO_CENTER);
		tNodeInformationBadLocation2 = new NodeInformation (Location.NO_LOC, false, true, false, 0, 0, RevenueCenter.NO_CENTER);

		assertTrue (nodeInformationSide.isValid (), "Valid Node Information for Side");
		assertTrue (nodeInformationCenter.isValid (), "Valid Node Information for Center");
		
		assertTrue (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
		assertFalse (nodeInformationCenter.isSide (), "Node Information for Center is a Side");

		assertFalse (tNodeInformationBadLocation1.isValid (), "Valid Node Information for NO_LOCATION");
		assertFalse (tNodeInformationBadLocation2.isValid (), "Valid Node Information for NULL Location");
	}
	
	@Test
	@DisplayName ("Test for setting Location")
	public void NodeInformationSetLocationTests () {
		Location tFoundLocation;
		Location mLocationSide2, mLocationSide10;
		
		mLocationSide2 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide2.getLocation ()).thenReturn (2);
		Mockito.when (mLocationSide2.isSide ()).thenReturn (true);
		
		mLocationSide10 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide10.getLocation ()).thenReturn (10);
		Mockito.when (mLocationSide10.isSide ()).thenReturn (false);
		
		tFoundLocation = nodeInformationSide.getLocation ();
		assertEquals (mLocationSide, tFoundLocation, "Node Information Constructor - Valid Side Location");
		assertEquals (1, nodeInformationSide.getLocationInt (), "Node Information - getLocationInt");

		nodeInformationSide.setLocation (mLocationSide2);
		assertNotEquals (mLocationSide2, tFoundLocation, "Node Information Set Location - did not Change Location Object");
		tFoundLocation = nodeInformationSide.getLocation ();
		assertEquals (mLocationSide2, tFoundLocation, "Node Information reset Side - did not get New Location");
		assertNotEquals (1, nodeInformationSide.getLocationInt (), "Node Information - Location is not 3");
		assertEquals (2, nodeInformationSide.getLocationInt (), "Node Information - getLocationInt");

		assertTrue (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
		nodeInformationSide.setLocation (mLocationSide10);
		
		assertFalse (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
	}
	
	@Test
	@DisplayName ("Node Information for testing OpenFlow")
	public void NodeInformationTestOpenFlow () {
		Location mLocationSide30;
		
		mLocationSide30 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide30.getLocation ()).thenReturn (30);
		Mockito.when (mLocationSide30.isSide ()).thenReturn (false);
		
		assertTrue (nodeInformationSide.isSide (), "Node Information for Side is NOT a Side");
		assertTrue (nodeInformationSide.getOpenFlow (), "Node Information for Side is NOT a Open Flow");

		assertTrue (nodeInformationCenter.getOpenFlow (), "Node Information for Center is an Open Flow");
		nodeInformationCenter.setCorpStation (false);
		nodeInformationCenter.setOpenFlow (false);
		assertFalse (nodeInformationCenter.getOpenFlow (), "Node Information for Center is an NOT Open Flow with FALSE Corp Station");
		
		nodeInformationCenter.setCorpStation (true);
		assertTrue (nodeInformationCenter.getOpenFlow (), "Node Information for Center is an Open Flow");
		
		nodeInformationCenter.setCorpStation (false);
		nodeInformationCenter.setOpenFlow (false);
		assertFalse (nodeInformationCenter.getOpenFlow (), "Node Information for Center is an NOT Open Flow with FALSE Corp Station");
		assertFalse (nodeInformationCenter.getCorpStation (), "Node Information for Center is an NOT Corp Station with FALSE Corp Station");

		nodeInformationCenter.setCorpStation (true);
		nodeInformationCenter.setLocation (mLocationSide30);
		assertTrue (nodeInformationCenter.getOpenFlow (), "Node Information for Center is an Open Flow");
		assertTrue (nodeInformationCenter.getCorpStation (), "Node Information for Center is an NOT Open Flow with FALSE Corp Station");

	}
	
	@Test
	@DisplayName ("Node Information for testing getDetail")
	public void NodeInformationTestGetDetail () {
		String tSideDetail = "[1 OF true]";
		String tCenterDetail = "[50: $20 CS false OF true]";
		
		assertEquals (tSideDetail, nodeInformationSide.getDetail (), "Side Detail does not match the expected");
		assertEquals (tCenterDetail, nodeInformationCenter.getDetail (), "Center Detail does not match the expected");
	}

	@Test
	@DisplayName ("Test if Sides have Same Location")
	public void NodeInformationSameSideTest () {
		assertTrue (nodeInformationSide.isSame (nodeInformationSide1));
		assertTrue (nodeInformationSide.isSame (nodeInformationSide));
		assertFalse (nodeInformationSide.isSame (nodeInformationSide3));
	}
}
