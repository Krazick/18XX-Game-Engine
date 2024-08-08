package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TilesTestFactory;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;

class MapTokenTests {
	CompanyTestFactory companyTestFactory;
	MapToken mapToken;
	MapToken mapToken1;
	MapToken mapToken2;
	MapToken mapToken3;
	TokenCompany mCompany2;
	TokenCompany mCompany3;

	@BeforeEach
	void setUp () throws Exception {
		int tMockCoID2;
		int tMockCoID3;

		companyTestFactory = new CompanyTestFactory ();
		tMockCoID2 = 5002;
		tMockCoID3 = 5003;
		mapToken = new MapToken ();
		mapToken1 = new MapToken (mapToken, 20, TokenInfo.TokenType.FIXED_COST);
		mapToken1.setConnectedSide (0, true);
		mapToken1.setConnectedSide (4, true);
		
		mCompany2 = companyTestFactory.buildTokenCompanyMock (tMockCoID2, "MC2");
		mapToken2 = companyTestFactory.buildMapToken (mCompany2);
		
		mCompany3 = companyTestFactory.buildTokenCompanyMock (tMockCoID3, "MC3");
		mapToken3 = companyTestFactory.buildMapToken (mCompany3);
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
			assertNull (mapToken.getSides ());
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
		

		@Test
		@DisplayName ("That Token is a Map Token")
		public void testIsAMapTokenA () {
			assertTrue (mapToken.isAMapToken ());
		}

		@Test
		@DisplayName ("That Duplciated Token is NOT a Map Token")
		public void testIsAMapTokenB () {
			assertTrue (mapToken1.isAMapToken ());
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
		tTile = tTilesTestFactory.buildTile (1);
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
		assertFalse (mapToken.tokenPlaced ());

		assertEquals ("T1", mapToken1.getMapCellID ());
	}
	
	
	@Test
	@DisplayName ("Test Creating XML Element for MapToken")
	public void testXMLElementCreation () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement;
		MapTestFactory tMapTestFactory;
		MapCell tMapCell2;
		Location tLocation2;
		
		tXMLDocument = new XMLDocument ();

		tMapTestFactory = new MapTestFactory ();
		tMapCell2 = tMapTestFactory.buildMapCell ();
		tLocation2 = tMapTestFactory.buildLocation ();
		mapToken2.placeToken (tMapCell2, tLocation2);

		tXMLElement = mapToken2.getTokenElement (tXMLDocument);
		tXMLDocument.appendChild (tXMLElement);
		assertEquals ("<Token abbrev=\"MC2\" location=\"50\" mapCellID=\"T1\"/>\n", tXMLDocument.toXMLString ());
	}
	
	@Test
	@DisplayName ("Test Creating XML Element for MapToken")
	public void tesdtXMLElement3Creation () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement;
		MapCell tMapCell3;
		Location tLocation3;
		MapTestFactory tMapTestFactory;
		TilesTestFactory tTilesTestFactory;
		Tile tTile;

		tXMLDocument = new XMLDocument ();

		tMapTestFactory = new MapTestFactory ();
		tTilesTestFactory = new TilesTestFactory ();
		tTile = tTilesTestFactory.buildTile (1);
		tMapCell3 = tMapTestFactory.buildMapCell ("T3");
		tLocation3 = tMapTestFactory.buildLocation ();
		tMapCell3.putTile (tTile, 0);
		mapToken3.placeToken (tMapCell3, tLocation3);
		mapToken3.setConnectedSide (0, true);
		mapToken3.setConnectedSide (4, true);
		
		tXMLDocument = new XMLDocument ();
		tXMLElement = mapToken3.getTokenElement (tXMLDocument);
		tXMLDocument.appendChild (tXMLElement);
		assertEquals ("<Token abbrev=\"MC3\" connectedSides=\"|0|1|3|4|\" location=\"50\" mapCellID=\"T3\"/>\n", tXMLDocument.toXMLString ());
	}

}
