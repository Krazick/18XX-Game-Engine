package ge18xx.map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.center.RevenueCenter;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TilesTestFactory;
import ge18xx.tiles.Upgrade;

class MapCellIntegrationTests {
	MapCell mapCell;
	MapTestFactory mapTestFactory;
	TilesTestFactory tilesTestFactory;
	CompanyTestFactory companyTestFactory;
	GameTestFactory gameTestFactory;
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	GameManager mGameManager;
	TileSet tileSet;
	
	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mapTestFactory = new MapTestFactory ();
		mapCell = mapTestFactory.buildMapCell ("N11");
		tilesTestFactory = new TilesTestFactory (mapTestFactory);
		companyTestFactory = new CompanyTestFactory ();
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
	}
	
	@Test
	@DisplayName ("Test Toronto Tile Upgrade on MapCell")
	void toronotTileUpgradeTest () {
		Tile tTile9995;
		Tile tTile120;
		Location tCorpLocation;
		Location tOtherLocation;
		RevenueCenter tCenterFound;
		RevenueCenter tOtherCenterFound;
		int tDestinationCorpID;
		int tAlpahCorpID;
		int tBetaCorpID;
		int tExpectedCount;
		int tExpectedBaseCorpIDs [];
		
		setupTileSet (mGameManager);
		tTile9995 = addTileAndUpgrade (0);
		tTile120 = addTileAndUpgrade (2);
		
		assertEquals (tTile9995.getNumber (), 9995);
		assertEquals (tTile120.getNumber (), 120);
		assertFalse (mapCell.isTileOnCell ());
		
		mapCell.putTile (tTile9995, 0);
		
		setupMockNeighbors ();
		
		tCorpLocation = new Location (7);
		tOtherLocation = new Location (10);
		mapCell.setCorporationHome (alphaShareCompany, tCorpLocation);
		tAlpahCorpID = alphaShareCompany.getID ();
		assertTrue (mapCell.isTileOnCell ());
		tCenterFound = mapCell.getCenterAtLocation (tCorpLocation);
		assertNotNull (tCenterFound);
		tOtherCenterFound = mapCell.getCenterAtLocation (tOtherLocation);
		assertNotNull (tOtherCenterFound);
		
		tBetaCorpID = betaShareCompany.getID ();	
		tDestinationCorpID = mapCell.getDestinationCorpID ();
		assertEquals (Corporation.NO_ID, tDestinationCorpID);
		
		mapCell.setDestinationCorpID (tBetaCorpID);
		tDestinationCorpID = mapCell.getDestinationCorpID ();
		assertEquals (tBetaCorpID, tDestinationCorpID);
		
		tExpectedCount = 3;
		tExpectedBaseCorpIDs = new int [tExpectedCount];
		tExpectedBaseCorpIDs [0] = Corporation.NO_ID;
		tExpectedBaseCorpIDs [1] = tAlpahCorpID;
		tExpectedBaseCorpIDs [2] = Corporation.NO_ID;
		verifyCorporationBasesOnMapCell (tExpectedCount, tExpectedBaseCorpIDs);
		
		mapCell.upgradeTile (tileSet, tTile120);
		verifyCorporationBasesOnMapCell (tExpectedCount, tExpectedBaseCorpIDs);
	}

	void setupMockNeighbors () {
		Terrain tClearTerrain;
		Terrain tOceanTerrain;
		
		tClearTerrain = new Terrain (Terrain.CLEAR);
		tOceanTerrain = new Terrain (Terrain.OCEAN);
		setupMockNeighbor (0, tClearTerrain, "N9");
		setupMockNeighbor (1, tClearTerrain, "O10");
		setupMockNeighbor (2, tClearTerrain, "O12");
		setupMockNeighbor (3, tClearTerrain, "N13");
		setupMockNeighbor (4, tClearTerrain, "M12");
		setupMockNeighbor (5, tClearTerrain, "M10");
	}
	
	void setupMockNeighbor (int aSide, Terrain aTerrain, String aID) {
		MapCell mNeighborMapCell;
		
		mNeighborMapCell = mapTestFactory.buildMapCellMock (aID);
		Mockito.when (mNeighborMapCell.canTrackToSide (anyInt ())).thenReturn (true);
		if (aTerrain.getTerrain () == Terrain.CLEAR) { 
			Mockito.when (mNeighborMapCell.isSelectable ()).thenReturn (true);
		} else {
			Mockito.when (mNeighborMapCell.isSelectable ()).thenReturn (false);
		}
		mNeighborMapCell.setBaseTerrain (aTerrain);
		mapCell.setNeighbor (aSide, mNeighborMapCell);
	}
	
	void setupTileSet (GameManager mGameManager) {
		Mockito.when (mGameManager.getActiveGameName ()).thenReturn ("Mock GameManager MapCellTests");
		tileSet = tilesTestFactory.buildTileSet (mGameManager);
	}
	
	Tile addTileAndUpgrade (int aTileIndex) {
		Tile tTile;
		GameTile tGameTile;
		Upgrade tUpgrade;
		int tTileNumber;

		tTile = tilesTestFactory.buildTile (aTileIndex);
		tileSet.addTile (tTile, 1);
		tUpgrade = tilesTestFactory.buildUpgrade (aTileIndex);
		tTileNumber = tTile.getNumber ();
		tGameTile = tileSet.getGameTile (tTileNumber);
		tGameTile.addUpgrade (tUpgrade);
		
		return tTile;
	}
	
	void verifyCorporationBasesOnMapCell (int aExpectedCount, int aCorpIDBases []) {
		RevenueCenter tCenterFound;
		int tCenterCount;
		int tCenterIndex;
		int tCorpID;
		
		tCenterCount = mapCell.getRevenueCenterCount ();
		assertEquals (aExpectedCount, tCenterCount);
		for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
			tCenterFound = mapCell.getRevenueCenter (tCenterIndex);
			tCorpID = tCenterFound.getHomeCompanyID ();
			assertEquals (aCorpIDBases [tCenterIndex], tCorpID);
		}
	}

}
