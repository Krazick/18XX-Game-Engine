package ge18xx.map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.tiles.Tile;

class MapCellTests extends MapTester {
	MapCell mapCell;
	Tile mTile;
	
	@BeforeEach
	void setUp () throws Exception {
		mapCell = mapTestFactory.buildMapCell ("N11");
		mTile = tilesTestFactory.buildTileMock (7);
	}
	
	@Test
	@DisplayName ("Test if IDs are the same")
	void mapCellsHaveSameID () {
		MapCell tSecondMapCell;
		
		tSecondMapCell = MapCell.NO_MAP_CELL;
		assertFalse (mapCell.sameID (tSecondMapCell));
		
		tSecondMapCell = mapTestFactory.buildMapCell ("A3");
		assertFalse (mapCell.sameID (tSecondMapCell));

		tSecondMapCell = mapTestFactory.buildMapCell ("N11");
		assertTrue (mapCell.sameID (tSecondMapCell));

		assertTrue (mapCell.sameID (mapCell));
	}
	
	@Test
	@DisplayName ("Test if Tile is on Cell")
	void mapCellIsTileOnCellTest () {		
		assertFalse (mapCell.isTileOnCell ());
		
		mapCell.setTile (mTile);
		assertTrue (mapCell.isTileOnCell ());
		mapCell.setTileNumber (7);
		assertTrue (mapCell.isTileOnCell ());
		mapCell.setTile (Tile.NO_TILE);
		assertTrue (mapCell.isTileOnCell ());
	}
	
	@Test
	@DisplayName ("Test getTile on MapCell")
	void mapCellGetTileTest () {	
		Tile tFoundTile;
		
		tFoundTile = mapCell.getTile ();
		assertNull (mapCell.getTile ());
		
		mapCell.setTile (mTile);
		tFoundTile = mapCell.getTile ();
		assertNotNull (mapCell.getTile ());
		assertEquals (tFoundTile, mTile);
	}
	
	@Test
	@DisplayName ("Test getTileNumber on MapCell")
	void mapCellgetTileNumberTest () {		
		assertEquals (0, mapCell.getTileNumber ());
		
		Mockito.when (mTile.getNumber ()).thenReturn (7);
		mapCell.setTile (mTile);
		assertEquals (7, mapCell.getTileNumber ());
		
//		mapCell.setTileNumber (7);
//		assertTrue (mapCell.isTileOnCell ());
//		mapCell.setTile (Tile.NO_TILE);
//		assertTrue (mapCell.isTileOnCell ());
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
	}
	

	@Test
	@DisplayName ("Test Cloning of a MapCell") 
	void mapCellCloningTest () {
		MapCell tCloneOfMapCell;
		
		try {
			tCloneOfMapCell = (MapCell) mapCell.clone ();
			assertEquals ("N11", tCloneOfMapCell.getID ());
			
		} catch (CloneNotSupportedException eException) {
			eException.printStackTrace ();
		}
	}

	@Test
	@DisplayName ("Test loading a MapCell from XML String")
	void mapCellFromXMLTest () {
		MapCell tMapCellFromXML2;
		MapCell tMapCellFromXML3;
		Terrain tBaseTerrain;
		Terrain tOptionalTerrain;
		MapCell tCloneOfMapCell;

		tMapCellFromXML2 = mapTestFactory.buildAMapCellFromXML (2, "A2");
		tBaseTerrain = tMapCellFromXML2.getBaseTerrain ();
		assertEquals (Terrain.CLEAR, tBaseTerrain.getTerrain ());
		tMapCellFromXML3 = mapTestFactory.buildAMapCellFromXML (3, "B3");
		tOptionalTerrain = tMapCellFromXML3.getTerrain1 ();
		assertEquals (Terrain.HILL, tOptionalTerrain.getTerrain (), "Optional Terrain does not Match");
		assertEquals ("B3", tMapCellFromXML3.getID (), "Original ID Does not Match");
		
		try {
			tCloneOfMapCell = (MapCell) tMapCellFromXML3.clone ();
			assertEquals ("B3", tCloneOfMapCell.getID (), "Cloned ID Does not Match");
			tOptionalTerrain = tCloneOfMapCell.getTerrain1 ();
			assertEquals (Terrain.HILL, tOptionalTerrain.getTerrain (), "Cloned Terrain does not Match");
			
		} catch (CloneNotSupportedException eException) {
			eException.printStackTrace ();
		}

	}
}
