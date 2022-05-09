package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TilesTestFactory;

class MapTokenTests {
	MapToken mapToken;
	MapToken mapToken1;

	@BeforeEach
	void setUp () throws Exception {
		mapToken = new MapToken ();
		mapToken1 = new MapToken (mapToken, 20);
		mapToken1.setConnectedSide (0, true);
		mapToken1.setConnectedSide (4, true);
	}

	@Nested
	@DisplayName ("Map Token Constructor Tests")
	class MapTokenConstructorTests {
		@Test
		@DisplayName ("Basic NoArg Constructor Tests")
		void testMapTokenNoArgs () {
			assertNull (mapToken.getLocation ());
			assertNull (mapToken.getMapCell ());
			assertEquals (0, mapToken.getCost ());
			assertEquals ("", mapToken.getMapCellID ());
			assertEquals ("|", mapToken.getSides ());
		}

		@Test
		@DisplayName ("Basic Two Arg Constructor Tests")
		void testMapToken2Args () {
			assertNull (mapToken1.getLocation ());
			assertNull (mapToken1.getMapCell ());
			assertEquals (20, mapToken1.getCost ());
			assertEquals ("", mapToken1.getMapCellID ());
			assertEquals ("|0|4|", mapToken1.getSides ());
		}
	}

	@Test
	@DisplayName ("Connected to Side Tests")
	void testMapTokenSides () {
		mapToken1.setConnectedSide (10, true);
		assertEquals ("|0|4|", mapToken1.getSides ());
		assertTrue (mapToken1.getConnectedSide (0));
		assertFalse (mapToken1.getConnectedSide (3));
		assertFalse (mapToken1.getConnectedSide (13));
		assertTrue (mapToken1.isConnectedToSide (4));
	}

	@Test
	@DisplayName ("Set MapCell and Location for MapToken Tests")
	void testMapTokenMapCellAndLocationTests () {
		MapCell tMapCell;
		Location tLocation;
		MapTestFactory tMapTestFactory;
		TilesTestFactory tTilesTestFactory;
		Tile tTile;

		tMapTestFactory = new MapTestFactory ();
		tMapCell = tMapTestFactory.buildMapCell ();
		tLocation = tMapTestFactory.buildLocation ();
		tTilesTestFactory = new TilesTestFactory ();
		tTile = tTilesTestFactory.buildTile ();
		tMapCell.putTile (tTile, 0);

		assertFalse (mapToken.tokenPlaced ());
		mapToken.setConnectedSides (tMapCell, tLocation);
		assertEquals ("|0|1|3|4|", mapToken.getSides ());
		mapToken.setMapCell (tMapCell);
		assertFalse (mapToken.tokenPlaced ());

		assertFalse (mapToken1.tokenPlaced ());

		mapToken1.setConnectedSide (0, false);
		mapToken1.setConnectedSide (4, false);
		mapToken1.placeToken (tMapCell, tLocation);

		assertEquals (tLocation, mapToken1.getLocation ());
		assertEquals (tMapCell, mapToken1.getMapCell ());
		assertTrue (mapToken1.tokenPlaced ());

		assertEquals ("T1", mapToken1.getMapCellID ());
	}
}
