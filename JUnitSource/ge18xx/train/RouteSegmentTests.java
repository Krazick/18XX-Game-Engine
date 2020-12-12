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
//		fail("Not yet implemented");
	}

}
