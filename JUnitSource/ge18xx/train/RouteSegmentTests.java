package ge18xx.train;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;

@DisplayName ("Route Segment Tests")
class RouteSegmentTests {

	@Test
	@DisplayName ("Route Segment Constructor Tests with Mocks")
	void RouteSegmentConstructorTests () {
		Location tLocation1, tLocation2, tLocation3;
//		tMockCoID = 5001;
//		TokenCompany mCompany = Mockito.mock (TokenCompany.class);
//		Mockito.when (mCompany.getID ()).thenReturn (tMockCoID);
		RouteSegment tRouteSegment;
		
		MapCell mMapCell = Mockito.mock (MapCell.class);
		Tile mTile = Mockito.mock (Tile.class);
		Mockito.when (mMapCell.getTile ()).thenReturn (mTile);
		
		tRouteSegment = new RouteSegment (mMapCell);
		assertEquals (tRouteSegment.getCost (), 0, "Route Segment Cost is not Zero");
		
		assertEquals (tRouteSegment.getStartLocationInt (), Location.NO_LOCATION, "Route Segment Start is not NO_LOCATTION");
		assertEquals (tRouteSegment.getEndLocationInt (), Location.NO_LOCATION, "Route Segment End is not NO_LOCATTION");
		
		assertFalse (tRouteSegment.validSegment (), "Route Segment before Locations set does have two good Locations");
		tLocation1 = new Location (1);
		tLocation2 = new Location (50);
		tRouteSegment.setStartNode (tLocation1);
		assertFalse (tRouteSegment.validSegment (), "Route Segment after location1 set does have two good Locations");
		tRouteSegment.setEndNode (tLocation2);
		assertTrue (tRouteSegment.validSegment (), "Route Segment after location2 set does NOT have two good Locations");
		
		tLocation3 = new Location (1);
		tRouteSegment.setEndNode (tLocation3);
		assertFalse (tRouteSegment.validSegment (), "Route Segment after location1 set does have two good Different Int Locations");
		tRouteSegment.setEndNode (tLocation1);
		assertFalse (tRouteSegment.validSegment (), "Route Segment after location1 set does have two good Different Locations");
	}

}
