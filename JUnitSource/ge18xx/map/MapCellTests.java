package ge18xx.map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameTestFactory;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TilesTestFactory;

class MapCellTests {
	MapCell mapCell;
	MapTestFactory mapTestFactory;
	TilesTestFactory tilesTestFactory;
	GameTestFactory gameTestFactory;
	Tile mTile;
	
	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		mapTestFactory = new MapTestFactory ();
		mapCell = mapTestFactory.buildMapCell ("N11");
		tilesTestFactory = new TilesTestFactory (mapTestFactory);
		mTile = tilesTestFactory.buildTileMock (7);
	}

	@Test
	@DisplayName ("Test Old Sides Connected with New Sides Test")
	void oldVSnewSidesTests () {
		String tOldSides;
		String tNewSides;
		
		tOldSides = "|0|4";
		tNewSides = "|0|1|4|5";
		
		assertTrue (mapCell.allOldSidesConnected (tOldSides, tNewSides));
		
		tNewSides = "|0|1|5";
		assertFalse (mapCell.allOldSidesConnected (tOldSides, tNewSides));
	}

	@Test
	@DisplayName ("Test Allowed Rotations set Properly") 
	void allowedRotationsTests () {
		mapCell.setAllowedRotation (0, false);
		mapCell.setAllowedRotation (1, true);
		mapCell.setAllowedRotation (2, true);
		mapCell.setAllowedRotation (3, false);
		mapCell.setAllowedRotation (4, false);
		mapCell.setAllowedRotation (5, true);

		mapCell.setAllowedRotation (-2, true);
		mapCell.setAllowedRotation (6, true);

		assertFalse (mapCell.getAllowedRotation (0));
		assertTrue (mapCell.getAllowedRotation (1));
		assertTrue (mapCell.getAllowedRotation (2));
		assertFalse (mapCell.getAllowedRotation (3));
		assertFalse (mapCell.getAllowedRotation (4));
		assertTrue (mapCell.getAllowedRotation (5));
		
		
		assertFalse (mapCell.getAllowedRotation (-2));
		assertFalse (mapCell.getAllowedRotation (6));

		assertEquals (3, mapCell.getCountofAllowedRotations ());
		
		mapCell.setAllRotations (true);
		assertEquals (6, mapCell.getCountofAllowedRotations ());
	}
	
	@Test
	@DisplayName ("Test Calculate Steps for Rotation of Tile on MapCell")
	void calculateStepsTests () {
		mapCell.setAllRotations (true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 0, true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 1, true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 2, true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 3, true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 4, true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 5, true);
		
		mapCell.setAllRotations (true);
		tilesTestFactory.setMockCanAllTracksExit (mTile, mapCell, 5, false);
	}

	@Test 
	@DisplayName ("Test Distance Calculations between Map Cells")
	void distanceCalculationsBetweenMapCells () {
		MapCell mapCell1;
		MapCell mapCell2;
		
		mapCell1 = mapTestFactory.buildMapCell ("O15");
		mapCell1.setOffsetCoordinates (7, 1);
		mapCell2 = mapTestFactory.buildMapCell ("L14");
		mapCell2.setOffsetCoordinates (7, 4);
		assertEquals (3, mapCell1.getDistanceTo (mapCell2));		
		assertEquals (3, mapCell2.getDistanceTo (mapCell1));
		
		mapCell1 = mapTestFactory.buildMapCell ("H16");
		mapCell1.setOffsetCoordinates (4, 6);
		mapCell2 = mapTestFactory.buildMapCell ("H20");
		mapCell2.setOffsetCoordinates (6, 10);
		assertEquals (4, mapCell1.getDistanceTo (mapCell2));
		assertEquals (4, mapCell2.getDistanceTo (mapCell1));
	
		mapCell1 = mapTestFactory.buildMapCell ("J8");
		mapCell1.setOffsetCoordinates (8, 8);
		mapCell2 = mapTestFactory.buildMapCell ("F12");
		mapCell2.setOffsetCoordinates (10, 8);
		assertEquals (2, mapCell1.getDistanceTo (mapCell2));
		assertEquals (2, mapCell2.getDistanceTo (mapCell1));

//		testDistance (evenOdd, 7, 1, 6, 2, 2);
//		testDistance (evenOdd, 7, 1, 6, 1, 1);
//		testDistance (evenOdd, 7, 1, 7, 4, 3);
//		testDistance (evenOdd, 7, 1, 4, 6, 6);
//		testDistance (evenOdd, 7, 1, 9, 11, 10);
//		testDistance (evenOdd, 7, 1, 5, 15, 14);
//		testDistance (evenOdd, 6, 10, 7, 1, 9);

	}
}
