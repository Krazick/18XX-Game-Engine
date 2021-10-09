package ge18xx.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marksmith
 *
 */
@DisplayName ("Terrain Tests")
class TerrainTests {

	@Test
	@DisplayName ("Test Terrain Set1")
	void basicTests () {
		Terrain tTerrain1, tTerrain2, tTerrain3;
		Terrain tTerrainOBB, tTerrainOBGREEN, tTerrainOBGray, tTerrainOCEAN;
		Terrain tBadTerrain1, tBadTerrain2;
		
		tTerrain1 = new Terrain (Terrain.CLEAR);
		tTerrain2 = new Terrain (Terrain.RIVER, 80, 50);
		tTerrain3 = new Terrain (Terrain.HILL, 120, 25);
		tBadTerrain1 = new Terrain (-1);
		tBadTerrain2 = new Terrain (50);
		
		// Test if Terrains is Mountainous 
		
		assertFalse (tTerrain1.isMountainous (), "Clear Terrain should NOT be Mountainous");
		assertFalse (tTerrain2.isMountainous (), "River Terrain should NOT be Mountainous");
		assertTrue (tTerrain3.isMountainous (), "Hill Terrain should be Mountainous");
		tTerrain3 = new Terrain (Terrain.MOUNTAIN, 120, 25);
		assertTrue (tTerrain3.isMountainous (), "MOUNTAIN Terrain should be Mountainous");
		tTerrain3 = new Terrain (Terrain.HIMALAYA, 150, 25);
		assertTrue (tTerrain3.isMountainous (), "HIMALAYA Terrain should be Mountainous");
		
		assertFalse (tBadTerrain1.isMountainous (), "Bad Terrain should NOT be Mountainous");
		assertFalse (tBadTerrain2.isMountainous (), "Bad Terrain should NOT be Mountainous");
		
		// Test if Terrains is Selectable 

		assertFalse (tBadTerrain1.isSelectable (), "Bad Terrain should NOT be Selectable");
		assertFalse (tBadTerrain2.isSelectable (), "Bad Terrain should NOT be Selectable");
		assertTrue (tTerrain1.isSelectable (), "Clear Terrain should be Selectable");
		assertTrue (tTerrain2.isSelectable (), "River Terrain should be Selectable");
		assertTrue (tTerrain3.isSelectable (), "Hill Terrain should be Selectable");
		
		tTerrainOBB = new Terrain (Terrain.OFF_BOARD_BLACK, 120, 25);
		tTerrainOBGREEN = new Terrain (Terrain.OFF_BOARD_GREEN, 120, 25);
		tTerrainOBGray = new Terrain (Terrain.OFF_BOARD_GRAY, 120, 25);
		tTerrainOCEAN = new Terrain (Terrain.OCEAN, 120, 25);
		assertFalse (tTerrainOBB.isSelectable (), tTerrainOBB.getName () + " Terrain should NOT be Selectable");
		assertFalse (tTerrainOBGREEN.isSelectable (), tTerrainOBGREEN.getName () + "Bad Terrain should NOT be Selectable");
		assertFalse (tTerrainOBGray.isSelectable (), tTerrainOBGray.getName () +"Bad Terrain should NOT be Selectable");
		assertFalse (tTerrainOCEAN.isSelectable (), tTerrainOCEAN.getName () +"Bad Terrain should NOT be Selectable");

	}

}
