package ge18xx.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;
		
		tMapCellID = "N11";
		tMapCellXMLIndex = 2;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		
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

	public MapCell setupBasicMapCell (int aMapCellXMLIndex, String aMapCellID) {
		Terrain tBaseTerrain;
		MapCell tMapCell;
		
		tMapCell = mapTestFactory.buildAMapCellFromXML (aMapCellXMLIndex, aMapCellID);
		
		// Verify the Map Cell has Clear Base Terrain, and no other Terrain Features,
		// and No Tile on the MapCell before testing build cost
		assertEquals (aMapCellID, tMapCell.getID ());
		tBaseTerrain = tMapCell.getBaseTerrain ();
		assertEquals (1, tBaseTerrain.getTerrain ());
		assertFalse (tMapCell.isTileOnCell ());

		assertFalse (tMapCell.isTileLayCostFree ());
		
		return tMapCell;
	}

	@Test
	@DisplayName ("Map Cell with Hill Terrain1 Build Cost Test")
	void mapCellHillBuildCostTest () {
		Terrain tTerrain1;
		Terrain tTerrain2;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "M9";
		tMapCellXMLIndex = 3;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		
		tTerrain1 = tMapCell.getTerrain1 ();
		tTerrain1.setCost (120);
		tTerrain2 = tMapCell.getTerrain2 ();
		
		assertEquals (13, tTerrain1.getTerrain ());
		assertNull (tTerrain2);
		assertFalse (tMapCell.isTileOnCell ());
		
		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (120, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (120, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (120, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (120, tTileBuildCost);
	}

	@Test
	@DisplayName ("Map Cell with River Terrain2 Build Cost Test")
	void mapCellRiver2BuildCostTest () {
		Terrain tTerrain1;
		Terrain tTerrain2;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "L8";
		tMapCellXMLIndex = 4;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);

		tTerrain1 = tMapCell.getTerrain1 ();
		tTerrain2 = tMapCell.getTerrain2 ();
		tTerrain2.setCost (80);
		
		assertEquals (1, tTerrain1.getTerrain ());
		assertEquals (10, tTerrain2.getTerrain ());
		assertFalse (tMapCell.isTileOnCell ());
		
		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (80, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (80, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (80, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (80, tTileBuildCost);
	}
	
	@Test
	@DisplayName ("Map Cell with Hill and River Build Cost Test")
	void mapCellHillRiverBuildCostTest () {
		Terrain tTerrain1;
		Terrain tTerrain2;
		Tile tTile;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "K7";
		tMapCellXMLIndex = 5;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		
		tTerrain1 = tMapCell.getTerrain1 ();
		tTerrain1.setCost (120);
		tTerrain2 = tMapCell.getTerrain2 ();
		tTerrain2.setCost (80);
		
		assertEquals (13, tTerrain1.getTerrain ());
		assertEquals (10, tTerrain2.getTerrain ());
		assertFalse (tMapCell.isTileOnCell ());

		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (200, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (200, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (200, tTileBuildCost);
		
		tTileIndex = 5; // Yellow Tile # 55, two Small Towns, curves overpass
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (200, tTileBuildCost);
	}
	
	@Test
	@DisplayName ("Map Cell with Tile Build Cost Test")
	void mapCellTileBuildCostTest () {
		Tile tTile;
		Tile tTileInPlace;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "J6";
		tMapCellXMLIndex = 2;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		tTileIndex = 9; // Yellow Tile # 57, one Single City, straight
		
		tTileInPlace = tilesTestFactory.buildTile (tTileIndex);
		tMapCell.setTile (tTileInPlace);

		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (0, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (0, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (0, tTileBuildCost);
		
		tTileIndex = 1; // Green Tile # 14, one Double City, Track in X Formation
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (0, tTileBuildCost);
	}
	
	
	@Test
	@DisplayName ("Map Cell with Tile and Terrain Build Cost Test")
	void mapCellTileTerrainBuildCostTest () {
		Terrain tTerrain2;
		Tile tTile;
		Tile tTileInPlace;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "J6";
		tMapCellXMLIndex = 4;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		tTileIndex = 9; // Yellow Tile # 57, one Single City, straight
		
		tTileInPlace = tilesTestFactory.buildTile (tTileIndex);
		tMapCell.setTile (tTileInPlace);
		
		tTerrain2 = tMapCell.getTerrain2 ();
		tTerrain2.setCost (80);
	
		assertEquals (10, tTerrain2.getTerrain ());
		assertTrue (tMapCell.isTileOnCell ());

		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (80, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (0, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (80, tTileBuildCost);
		
		tTileIndex = 1; // Green Tile # 14, one Double City, Track in X Formation
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (0, tTileBuildCost);
	}
	
	@Test
	@DisplayName ("Map Cell with Fixed Tile and Terrain Build Cost Test")
	void mapCellFixedTileTerrainBuildCostTest () {
		Terrain tTerrain1;
		Tile tTile;
		Tile tTileInPlace;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "I5";
		tMapCellXMLIndex = 6;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		tTileIndex = 9; // Yellow Tile # 57, one Single City, straight
		
		tTileInPlace = tilesTestFactory.buildTile (tTileIndex);
		tMapCell.setTile (tTileInPlace);
		tMapCell.setFixedTileFlag (true);
		
		tTerrain1 = tMapCell.getTerrain1 ();
		tTerrain1.setCost (80);
	
		assertEquals (10, tTerrain1.getTerrain ());
		assertTrue (tMapCell.isTileOnCell ());

		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (80, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (0, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (80, tTileBuildCost);
		
		tTileIndex = 1; // Green Tile # 14, one Double City, Track in X Formation
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (80, tTileBuildCost);
	}
	
	@Test
	@DisplayName ("Map Cell BA Home with Fixed Tile and Terrain Build Cost Test")
	void mapCellBAFixedTileTerrainBuildCostTest () {
		Terrain tTerrain1;
		Tile tTile;
		Tile tTileInPlace;
		int tTileIndex;
		int tTileBuildCost;
		int tTotalTerrainCost;
		int tMapCellXMLIndex;
		MapCell tMapCell;
		String tMapCellID;

		tMapCellID = "L6";
		tMapCellXMLIndex = 6;
		tMapCell = setupBasicMapCell (tMapCellXMLIndex, tMapCellID);
		tTileIndex = 10;
		
		tTileInPlace = tilesTestFactory.buildTile (tTileIndex);
		tMapCell.setTile (tTileInPlace);
		tMapCell.setFixedTileFlag (true);
		
		tTerrain1 = tMapCell.getTerrain1 ();
		tTerrain1.setCost (50);
	
		assertEquals (10, tTerrain1.getTerrain ());
		assertTrue (tMapCell.isTileOnCell ());

		tTotalTerrainCost = tMapCell.getTotalTerrainCost ();
		assertEquals (50, tTotalTerrainCost);
		
		tTileBuildCost = tMapCell.getCostToLayTile ();
		assertEquals (50, tTileBuildCost);

		tTileBuildCost = tMapCell.getCostToLayTile (Tile.NO_TILE);
		assertEquals (50, tTileBuildCost);
		
		tTileIndex = 1; // Green Tile # 14, one Double City, Track in X Formation
		
		tTile = tilesTestFactory.buildTile (tTileIndex);
		tTileBuildCost = tMapCell.getCostToLayTile (tTile);
		assertEquals (50, tTileBuildCost);
	}

}
