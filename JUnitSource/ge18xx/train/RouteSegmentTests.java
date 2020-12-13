package ge18xx.train;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;
import ge18xx.tiles.Track;

@DisplayName ("Route Segment Tests")
class RouteSegmentTests {
	MapCell mMapCellWithRCCityTile;
	MapCell mMapCellWithRCTownTile;
	MapCell mMapCellWithNoRCTile;
	Tile mTileWithRCCity;
	Tile mTileWithRCTown;
	Tile mTileWithNoRC;
	RouteSegment routeSegmentWithRCCity;
	RouteSegment routeSegmentWithRCTown;
	RouteSegment routeSegmentWithNoRC;
	NodeInformation mNodeRCCity, mNodeRCTown, mNodeSide1, mNodeSide3;
	Location mLocationRC, mLocationSide1, mLocationSide3;
	RevenueCenter mRCCity;
	RevenueCenter mRCTown;
	RevenueCenter mNoRevenueCenter;
	Track mTrackSideToSide, mTrackSideToRC;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception { 
		mMapCellWithRCCityTile = Mockito.mock (MapCell.class);
		mMapCellWithRCTownTile = Mockito.mock (MapCell.class);
		mMapCellWithNoRCTile = Mockito.mock (MapCell.class);
		
		mRCCity = Mockito.mock (RevenueCenter.class);
		Mockito.when (mRCCity.getRevenue (1)).thenReturn (30);
		Mockito.when (mRCCity.getRevenue (2)).thenReturn (50);
		mRCTown = Mockito.mock (RevenueCenter.class);
		Mockito.when (mRCTown.getRevenue (1)).thenReturn (10);
		mNoRevenueCenter = RevenueCenter.NO_CENTER;
		
		mTileWithRCCity = Mockito.mock (Tile.class);
		Mockito.when (mMapCellWithRCCityTile.getTile ()).thenReturn (mTileWithRCCity);
		Mockito.when (mTileWithRCCity.hasTown ()).thenReturn (false);
		
		mTileWithRCTown = Mockito.mock (Tile.class);
		Mockito.when (mMapCellWithRCTownTile.getTile ()).thenReturn (mTileWithRCTown);
		Mockito.when (mTileWithRCTown.hasTown ()).thenReturn (true);
		
		mTileWithNoRC = Mockito.mock (Tile.class);
		Mockito.when (mMapCellWithNoRCTile.getTile ()).thenReturn (mTileWithNoRC);
		Mockito.when (mTileWithNoRC.hasTown ()).thenReturn (false);
		
		routeSegmentWithRCCity = new RouteSegment (mMapCellWithRCCityTile);
		routeSegmentWithRCTown = new RouteSegment (mMapCellWithRCTownTile);
		routeSegmentWithNoRC = new RouteSegment (mMapCellWithNoRCTile);
		
		mLocationRC = Mockito.mock (Location.class);
		Mockito.when (mLocationRC.getLocation ()).thenReturn (50);
		Mockito.when (mLocationRC.isSide ()).thenReturn (false);
		mLocationSide1 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide1.getLocation ()).thenReturn (1);
		Mockito.when (mLocationSide1.isSide ()).thenReturn (true);
		mLocationSide3 = Mockito.mock (Location.class);
		Mockito.when (mLocationSide3.getLocation ()).thenReturn (3);
		Mockito.when (mLocationSide3.isSide ()).thenReturn (true);
		
		mNodeRCCity = Mockito.mock (NodeInformation.class);
		Mockito.when (mNodeRCCity.hasRevenueCenter ()).thenReturn (true);
		Mockito.when (mNodeRCCity.getLocation ()).thenReturn (mLocationRC);
		Mockito.when (mNodeRCCity.getLocationInt ()).thenReturn (50);
		Mockito.when (mNodeRCCity.isSide ()).thenReturn (false);
		Mockito.when (mNodeRCCity.getRevenueCenter ()).thenReturn (mRCCity);
		
		mNodeRCTown = Mockito.mock (NodeInformation.class);
		Mockito.when (mNodeRCTown.hasRevenueCenter ()).thenReturn (true);
		Mockito.when (mNodeRCTown.getLocation ()).thenReturn (mLocationRC);
		Mockito.when (mNodeRCTown.getLocationInt ()).thenReturn (26);
		Mockito.when (mNodeRCTown.isSide ()).thenReturn (false);
		Mockito.when (mNodeRCTown.getRevenueCenter ()).thenReturn (mRCTown);
		
		mNodeSide1 = Mockito.mock (NodeInformation.class);
		Mockito.when (mNodeSide1.hasRevenueCenter ()).thenReturn (false);
		Mockito.when (mNodeSide1.getLocation ()).thenReturn (mLocationSide1);
		Mockito.when (mNodeSide1.getLocationInt ()).thenReturn (1);
		Mockito.when (mNodeSide1.isSide ()).thenReturn (true);
		Mockito.when (mNodeSide1.getRevenueCenter ()).thenReturn (mNoRevenueCenter);
		
		mNodeSide3 = Mockito.mock (NodeInformation.class);
		Mockito.when (mNodeSide3.hasRevenueCenter ()).thenReturn (false);
		Mockito.when (mNodeSide3.getLocation ()).thenReturn (mLocationSide3);
		Mockito.when (mNodeSide3.getLocationInt ()).thenReturn (3);
		Mockito.when (mNodeSide3.isSide ()).thenReturn (true);
		Mockito.when (mNodeSide3.getRevenueCenter ()).thenReturn (mNoRevenueCenter);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Route Segment Constructor Tests with Mocks")
	void RouteSegmentConstructorTests () {
		Location tLocation1, tLocation2, tLocation3;
		
		assertEquals (routeSegmentWithRCCity.getCost (), 0, "Route Segment Cost is not Zero");
		
		assertEquals (Location.NO_LOCATION, routeSegmentWithRCCity.getStartLocationInt (), "Route Segment Start is not NO_LOCATTION");
		assertEquals (Location.NO_LOCATION, routeSegmentWithRCCity.getEndLocationInt (),  "Route Segment End is not NO_LOCATTION");
		
		assertFalse (routeSegmentWithRCCity.validSegment (), "Route Segment before Locations set does have two good Locations");
		tLocation1 = new Location (1);
		tLocation2 = new Location (50);
		routeSegmentWithRCCity.setStartNode (tLocation1);
		assertFalse (routeSegmentWithRCCity.validSegment (), "Route Segment after location1 set does have two good Locations");
		routeSegmentWithRCCity.setEndNode (tLocation2);
		assertTrue (routeSegmentWithRCCity.validSegment (), "Route Segment after location2 set does NOT have two good Locations");
		
		tLocation3 = new Location (1);
		routeSegmentWithRCCity.setEndNode (tLocation3);
		assertFalse (routeSegmentWithRCCity.validSegment (), "Route Segment after location1 set does have two good Different Int Locations");
		routeSegmentWithRCCity.setEndNode (tLocation1);
		assertFalse (routeSegmentWithRCCity.validSegment (), "Route Segment after location1 set does have two good Different Locations");
	}

	@Test
	@DisplayName ("Route Segment Test RevenueCenter presence")
	void RouteSegmentTestRevenueCenters () {
		
		routeSegmentWithNoRC.setStartNode (mNodeSide3);
		routeSegmentWithNoRC.setEndNode (mNodeSide1);
		assertFalse (routeSegmentWithNoRC.hasRevenueCenterAtStart (), "Route Segment has RevenueCenter at Start");
		assertFalse (routeSegmentWithNoRC.hasRevenueCenterAtEnd (), "Route Segment has RevenueCenter at End");
		assertFalse (routeSegmentWithNoRC.hasRevenueCenter (), "Route Segment has Revenue Centers");

		routeSegmentWithRCCity.setStartNode (mNodeSide1);
		routeSegmentWithRCCity.setEndNode (mNodeRCCity);
		assertFalse (routeSegmentWithRCCity.hasRevenueCenterAtStart (), "Route Segment has RevenueCenter at Start");
		assertTrue (routeSegmentWithRCCity.hasRevenueCenterAtEnd (), "Route Segment has no RevenueCenter at End");
		assertTrue (routeSegmentWithRCCity.hasRevenueCenter (), "Route Segment has No Revenue Centers");
		
		routeSegmentWithRCCity.setStartNode (mNodeRCCity);
		routeSegmentWithRCCity.setEndNode (mNodeSide1);
		assertTrue (routeSegmentWithRCCity.hasRevenueCenterAtStart (), "Route Segment has no RevenueCenter at Start");
		assertFalse (routeSegmentWithRCCity.hasRevenueCenterAtEnd (), "Route Segment has RevenueCenter at End");
		assertTrue (routeSegmentWithRCCity.hasRevenueCenter (), "Route Segment has No Revenue Centers");
		assertFalse (routeSegmentWithRCCity.hasTownOnTile (), "Route Segment has Town on Tile - at Start");
		
		routeSegmentWithRCCity.swapStartEndLocations ();
		assertFalse (routeSegmentWithRCCity.hasRevenueCenterAtStart (), "Route Segment has RevenueCenter at Start");
		assertTrue (routeSegmentWithRCCity.hasRevenueCenterAtEnd (), "Route Segment has no RevenueCenter at End");
		assertTrue (routeSegmentWithRCCity.hasRevenueCenter (), "Route Segment has No Revenue Centers");
		assertFalse (routeSegmentWithRCCity.hasTownOnTile (), "Route Segment has Town On Tile - at End");

		routeSegmentWithRCTown.setStartNode (mNodeRCCity);
		routeSegmentWithRCTown.setEndNode (mNodeSide1);
		assertTrue (routeSegmentWithRCTown.hasTownOnTile (), "Route Segment has NO Town On Tile - at End");
		routeSegmentWithRCTown.swapStartEndLocations ();
		assertTrue (routeSegmentWithRCTown.hasTownOnTile (), "Route Segment has NO Town On Tile - at Start");

	}
	
	@Test
	@DisplayName ("Route Segment Test getting Locations")
	void RouteSegmentTestGetLocations () {
		Location tLocation1, tLocation3;
		Location tStartLocationSide, tEndLocationSide;
		
		routeSegmentWithNoRC.setStartNode (mNodeSide1);
		routeSegmentWithNoRC.setEndNode (mNodeSide3);
		tLocation1 = routeSegmentWithNoRC.getStartLocation ();
		tLocation3 = routeSegmentWithNoRC.getEndLocation ();
		assertEquals (1, tLocation1.getLocation (), "Route Segment Start Location is not 1");
		assertEquals (3, tLocation3.getLocation (), "Route Segment End Location is not 3");
		
		assertEquals (1, routeSegmentWithNoRC.getStartLocationInt (), "Route Segment Start Location Int is not 1");
		assertEquals (3, routeSegmentWithNoRC.getEndLocationInt (), "Route Segment End Location Int is not 3");

		routeSegmentWithNoRC.setEndNode (mNodeRCCity);
		assertTrue (routeSegmentWithNoRC.isStartASide (), "Route Segment Is Start NOT A Side");
		assertFalse (routeSegmentWithNoRC.isEndASide (), "Route Segment Is End A Side ");
		
		routeSegmentWithNoRC.swapStartEndLocations ();
		assertFalse (routeSegmentWithNoRC.isStartASide (), "Route Segment Is Start A Side");
		assertTrue (routeSegmentWithNoRC.isEndASide (), "Route Segment Is End NOT A Side ");
		
		routeSegmentWithNoRC.setStartNode (mNodeSide1);
		routeSegmentWithNoRC.setEndNode (mNodeSide3);
		tStartLocationSide = routeSegmentWithNoRC.getStartLocationIsSide ();
		assertEquals (tLocation1, tStartLocationSide, "Route Segment Start is NOT tLocation1");
		tEndLocationSide = routeSegmentWithNoRC.getEndLocationIsSide ();
		assertEquals (tLocation3, tEndLocationSide, "Route Segment End is NOT tLocation3");
		
		routeSegmentWithNoRC.setEndNode (mNodeRCCity);
		tEndLocationSide = routeSegmentWithNoRC.getEndLocationIsSide ();
		assertEquals (Location.NO_LOC, tEndLocationSide, "Route Segment End is NOT Location.NO_LOC");
		routeSegmentWithNoRC.swapStartEndLocations ();
		tStartLocationSide = routeSegmentWithNoRC.getStartLocationIsSide ();
		assertEquals (Location.NO_LOC, tStartLocationSide, "Route Segment Start is NOT tLocation1");
	}
	
	@Test
	@DisplayName ("Route Segment Test getting Revenue stuff")
	void RouteSegmentTestGetRevenueStuff () {
		RevenueCenter tFoundRevenueCenter;
		
		routeSegmentWithRCCity.setStartNode (mNodeSide1);
		routeSegmentWithRCCity.setEndNode (mNodeRCCity);
		tFoundRevenueCenter = routeSegmentWithRCCity.getRevenueCenter ();
		assertEquals (mRCCity, tFoundRevenueCenter, "Expected mRCCity, did not find it");
		assertEquals (30, tFoundRevenueCenter.getRevenue (1), "Asking mRCCity for Revenue - Expected 30");
		assertEquals (50, tFoundRevenueCenter.getRevenue (2), "Asking mRCCity for Revenue - Expected 50");
		
		assertEquals (30, routeSegmentWithRCCity.getRevenue (1), "Asking RouteSegment for Revenue - Expected 30");
		assertEquals (50, routeSegmentWithRCCity.getRevenue (2), "Asking RouteSegment for Revenue - Expected 50");
		
		routeSegmentWithRCTown.setStartNode (mNodeRCTown);
		routeSegmentWithRCTown.setEndNode (mNodeSide3);
		tFoundRevenueCenter = routeSegmentWithRCTown.getRevenueCenter ();
		assertEquals (mRCTown, tFoundRevenueCenter, "Expected mNodeRCTown, did not find it");
		assertEquals (10, tFoundRevenueCenter.getRevenue (1), "Asking mRCCity for Revenue - Expected 10");
		assertEquals (10, routeSegmentWithRCTown.getRevenue (1), "Asking RouteSegment for Revenue - Expected 10");
		
		routeSegmentWithRCCity.setStartNode (mNodeSide3);
		routeSegmentWithRCCity.setEndNode (mNodeSide1);
		tFoundRevenueCenter = routeSegmentWithRCCity.getRevenueCenter ();
		assertEquals (RevenueCenter.NO_CENTER, tFoundRevenueCenter, "Expected No RevenueCenter, did not find one");
		assertEquals (0, routeSegmentWithRCCity.getRevenue (1), "Asking RouteSegment for Revenue - Expected 0");
	}
}
