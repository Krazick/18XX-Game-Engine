package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MapTokenTests {
	MapToken mapToken;
	MapToken mapToken1;
	
	@BeforeEach
	void setUp () throws Exception {
		mapToken = new MapToken ();
		mapToken1 = new MapToken (mapToken, 20);
		mapToken1.setConnectedSide(0, true);
		mapToken1.setConnectedSide(4, true);
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
}
