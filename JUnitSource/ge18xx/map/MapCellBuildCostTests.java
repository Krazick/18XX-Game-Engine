package ge18xx.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameManager;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;

@DisplayName ("Map Cell Build Costs Tests")
class MapCellBuildCostTests extends MapTester {
	GameManager mGameManager;
	TileSet tileSet;

	@BeforeEach
	void setUp () throws Exception {
		mGameManager = gameTestFactory.buildGameManagerMock ();
		tileSet = tilesTestFactory.buildTileSet (mGameManager);
	}

	@Test
	@DisplayName ("Empty Map Cell Build Cost Test")
	void emptyMapCellBuildCostTest () {
		Terrain tBaseTerrain;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		MapCell tMapCell;
		
		tMapCell = mapTestFactory.buildAMapCellFromXML (2, "N11");
		
		// Verify the Map Cell has Clear Base Terrain, and no other Terrain Features,
		// and No Tile on the MapCell before testing build cost
		assertEquals ("N11", tMapCell.getID ());
		tBaseTerrain = tMapCell.getBaseTerrain ();
		assertEquals (1, tBaseTerrain.getTerrain ());
		assertNull (tMapCell.getTerrain1 ());
		assertNull (tMapCell.getTerrain2 ());
		assertFalse (tMapCell.isTileOnCell ());

		assertFalse (tMapCell.isTileLayCostFree ());
		
		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (0, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (0, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (0, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (0, tTileBuildCost);
	}

	@Test
	@DisplayName ("Map Cell with Hill Terrain1 Build Cost Test")
	void mapCellHillBuildCostTest () {
		Terrain tBaseTerrain;
		Terrain tTerrain1;
		Terrain tTerrain2;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		MapCell mapCell;
		
		mapCell = mapTestFactory.buildAMapCellFromXML (3, "M9");
	
		// Verify the Map Cell has Clear Base Terrain, and no other Terrain Features,
		// and No Tile on the MapCell before testing build cost
		assertEquals ("M9", mapCell.getID ());
		tBaseTerrain = mapCell.getBaseTerrain ();
		assertEquals (1, tBaseTerrain.getTerrain ());
		tTerrain1 = mapCell.getTerrain1 ();
		tTerrain1.setCost (120);
		tTerrain2 = mapCell.getTerrain2 ();
		
		assertEquals (13, tTerrain1.getTerrain ());
		assertNull (tTerrain2);
		assertFalse (mapCell.isTileOnCell ());

		assertFalse (mapCell.isTileLayCostFree ());
		
		tTotalTerrainCost = mapCell.getTotalTerrainCost ();
		assertEquals (120, tTotalTerrainCost);
		
		tTileBuildCost = mapCell.getCostToLayTile ();
		assertEquals (120, tTileBuildCost);

		tTileBuildCost = mapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (120, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = mapCell.getCostToLayTile (tTile);
		assertEquals (120, tTileBuildCost);
	}

	@Test
	@DisplayName ("Map Cell with River Terrain2 Build Cost Test")
	void mapCellRiver2BuildCostTest () {
		Terrain tBaseTerrain;
		Terrain tTerrain1;
		Terrain tTerrain2;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		MapCell mapCell;
		
		mapCell = mapTestFactory.buildAMapCellFromXML (4, "L8");
	
		// Verify the Map Cell has Clear Base Terrain, and no other Terrain Features,
		// and No Tile on the MapCell before testing build cost
		assertEquals ("L8", mapCell.getID ());
		tBaseTerrain = mapCell.getBaseTerrain ();
		assertEquals (1, tBaseTerrain.getTerrain ());
		tTerrain1 = mapCell.getTerrain1 ();
		tTerrain2 = mapCell.getTerrain2 ();
		tTerrain2.setCost (80);
		
		assertEquals (1, tTerrain1.getTerrain ());
		assertEquals (10, tTerrain2.getTerrain ());
		assertFalse (mapCell.isTileOnCell ());

		assertFalse (mapCell.isTileLayCostFree ());
		
		tTotalTerrainCost = mapCell.getTotalTerrainCost ();
		assertEquals (80, tTotalTerrainCost);
		
		tTileBuildCost = mapCell.getCostToLayTile ();
		assertEquals (80, tTileBuildCost);

		tTileBuildCost = mapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (80, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = mapCell.getCostToLayTile (tTile);
		assertEquals (80, tTileBuildCost);
	}
	@Test
	@DisplayName ("Map Cell with Hill and River Build Cost Test")
	void mapCellHillRiverBuildCostTest () {
		Terrain tBaseTerrain;
		Terrain tTerrain1;
		Terrain tTerrain2;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		MapCell mapCell;
		
		mapCell = mapTestFactory.buildAMapCellFromXML (5, "K7");
	
		// Verify the Map Cell has Clear Base Terrain, and no other Terrain Features,
		// and No Tile on the MapCell before testing build cost
		assertEquals ("K7", mapCell.getID ());
		tBaseTerrain = mapCell.getBaseTerrain ();
		assertEquals (1, tBaseTerrain.getTerrain ());
		tTerrain1 = mapCell.getTerrain1 ();
		tTerrain1.setCost (120);
		tTerrain2 = mapCell.getTerrain2 ();
		tTerrain2.setCost (80);
		
		assertEquals (13, tTerrain1.getTerrain ());
		assertEquals (10, tTerrain2.getTerrain ());
		assertFalse (mapCell.isTileOnCell ());

		assertFalse (mapCell.isTileLayCostFree ());
		
		tTotalTerrainCost = mapCell.getTotalTerrainCost ();
		assertEquals (200, tTotalTerrainCost);
		
		tTileBuildCost = mapCell.getCostToLayTile ();
		assertEquals (200, tTileBuildCost);

		tTileBuildCost = mapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (200, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = mapCell.getCostToLayTile (tTile);
		assertEquals (200, tTileBuildCost);
	}

}
