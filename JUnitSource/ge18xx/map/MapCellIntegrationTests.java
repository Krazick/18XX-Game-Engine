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
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.action.effects.LayTileEffect;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TilesTestFactory;
import ge18xx.tiles.Upgrade;

import geUtilities.GUI;

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
	RoundTestFactory roundTestFactory;
	RoundManager mRoundManager;
	Tile tile9995;
	Tile tile120;
	LayTileEffect layTileEffect;
	
	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		roundTestFactory = new RoundTestFactory ();
		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		mapTestFactory = new MapTestFactory ();
		mapCell = mapTestFactory.buildMapCell ("N11");
		tilesTestFactory = new TilesTestFactory (mapTestFactory);
		companyTestFactory = new CompanyTestFactory ();
		
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
		setupTileSet (mGameManager);
		tile9995 = addTileAndUpgrade (0);
		tile120 = addTileAndUpgrade (2);
		
		setupMockNeighbors ();
	}
	
	@Test
	@DisplayName ("Test Toronto Tile Upgrade on MapCell")
	void toronotTileUpgradeTest () {	
		
		prepareAndVerifyMapCell ();
		
		// ================= Upgrade Tile to 120 ==============
		
		mapCell.upgradeTile (tileSet, tile120);
		verifyMapCellAfterUpgrade ();
	}
	
	@Test
	@DisplayName ("Test Toronto TileUpgrade via LayTileEffect")
	void tornotoLayTileEffectUpgradeTest () {
		HexMap mHexMap;
		RevenueCenter tDestinationCity;
		Tile tTile;

		prepareAndVerifyMapCell ();
		tTile = mapCell.getTile ();
		tDestinationCity = tTile.getCenterAt (0);
		mapCell.removeTile ();
		mapCell.addRevenueCenter (0, tDestinationCity);
		buildLayTileEffect ();
		mHexMap = mapTestFactory.buildHexMapMock ();
		Mockito.when (mHexMap.getMapCellForID ("N11")).thenReturn (mapCell);
		Mockito.when (mRoundManager.getTileSet ()).thenReturn (tileSet);
		Mockito.when (mRoundManager.getGameMap ()).thenReturn (mHexMap);
		Mockito.when (mRoundManager.getShareCompany ("TPRR")).thenReturn (alphaShareCompany);
		Mockito.when (mGameManager.getShareCompany ("TPRR")).thenReturn (alphaShareCompany);
		Mockito.when (mRoundManager.getGameManager ()).thenReturn (mGameManager);
		
		layTileEffect.applyEffect (mRoundManager);
		verifyMapCellAfterUpgrade ();
	}
	
	void buildLayTileEffect () {
		String tTokens;
		String tBases;
		
		tTokens = GUI.EMPTY_STRING;
		tBases = "TPRR,1";
		layTileEffect = new LayTileEffect (alphaShareCompany, mapCell, tile120, 0, tTokens, tBases);
	}
	
	void verifyMapCellAfterUpgrade () {
		Location tCorpLocation;
		Location tOtherLocation;
		RevenueCenter tCenterFound;
		RevenueCenter tOtherCenterFound;
		int tDestinationCorpID;
		int tAlpahCorpID;
		int tBetaCorpID;
		int tExpectedCount;
		int tExpectedBaseCorpIDs [];
		int tExpectedCenterTypes [];
		
		tAlpahCorpID = alphaShareCompany.getID ();
		tBetaCorpID = betaShareCompany.getID ();	
		tExpectedCount = 3;
		tExpectedBaseCorpIDs = new int [tExpectedCount];
		tExpectedCenterTypes = new int [tExpectedCount];
		tExpectedBaseCorpIDs [0] = Corporation.NO_ID;
		tExpectedBaseCorpIDs [1] = tAlpahCorpID;
		tExpectedBaseCorpIDs [2] = Corporation.NO_ID;
		
		tExpectedCenterTypes [0] = 16;
		tExpectedCenterTypes [1] = 3;
		tExpectedCenterTypes [2] = 3;
		
		verifyInformationOnMapCell (tExpectedCount, tExpectedBaseCorpIDs, tExpectedCenterTypes);
		
		tDestinationCorpID = mapCell.getDestinationCorpID ();
		assertEquals (tBetaCorpID, tDestinationCorpID);
		
		tCorpLocation = new Location (13);
		tOtherLocation = new Location (17);
		
		tCenterFound = mapCell.getCenterAtLocation (tCorpLocation);
		assertNotNull (tCenterFound);
		assertEquals (alphaShareCompany, tCenterFound.getCorporation ());
		
		tOtherCenterFound = mapCell.getCenterAtLocation (tOtherLocation);
		assertNotNull (tOtherCenterFound);
		assertNull (tOtherCenterFound.getCorporation ());
	}

	void prepareAndVerifyMapCell () {
		Location tCorpLocation;
		Location tOtherLocation;
		RevenueCenter tCenterFound;
		RevenueCenter tOtherCenterFound;
		int tDestinationCorpID;
		int tAlpahCorpID;
		int tBetaCorpID;
		int tExpectedCount;
		int tExpectedBaseCorpIDs [];
		int tExpectedCenterTypes [];

		assertEquals (tile9995.getNumber (), 9995);
		assertEquals (tile120.getNumber (), 120);
		assertFalse (mapCell.isTileOnCell ());
		
		mapCell.putTile (tile9995, 0);
		mapCell.setStartingTile (true);

		assertTrue (mapCell.isTileOnCell ());

		tCorpLocation = new Location (7);
		tOtherLocation = new Location (10);
		mapCell.setCorporationHome (alphaShareCompany, tCorpLocation);
		tAlpahCorpID = alphaShareCompany.getID ();
		tCenterFound = mapCell.getCenterAtLocation (tCorpLocation);
		assertNotNull (tCenterFound);
		assertEquals (alphaShareCompany, tCenterFound.getCorporation ());
		
		tOtherCenterFound = mapCell.getCenterAtLocation (tOtherLocation);
		assertNotNull (tOtherCenterFound);
		assertNull (tOtherCenterFound.getCorporation ());
		
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
		
		tExpectedCenterTypes = new int [tExpectedCount];
		tExpectedCenterTypes [0] = 16;
		tExpectedCenterTypes [1] = 3;
		tExpectedCenterTypes [2] = 3;
		verifyInformationOnMapCell (tExpectedCount, tExpectedBaseCorpIDs, tExpectedCenterTypes);
	}
	
	void setupMockNeighbors () {
		Terrain tClearTerrain;
		Terrain tOceanTerrain;
		
		tClearTerrain = new Terrain (Terrain.CLEAR);
		tOceanTerrain = new Terrain (Terrain.OCEAN);
		setupMockNeighbor (0, tClearTerrain, "N9");
		setupMockNeighbor (1, tClearTerrain, "O10");
		setupMockNeighbor (2, tOceanTerrain, "O12");
		setupMockNeighbor (3, tOceanTerrain, "N13");
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
		int tQuantity;

		tQuantity = 1;
		tTile = tilesTestFactory.buildTile (aTileIndex);
		tileSet.addTile (tTile, tQuantity);
		tTileNumber = tTile.getNumber ();
		
		tGameTile = tileSet.getGameTile (tTileNumber);
		tGameTile.setOverride (true);
		tileSet.addNTileClones (tGameTile, tTile, tQuantity);
		
		tUpgrade = tilesTestFactory.buildUpgrade (aTileIndex);
		tGameTile.addUpgrade (tUpgrade);
		
		return tTile;
	}
	
	void verifyInformationOnMapCell (int aExpectedCount, int aCorpIDBases [], int aExpectedCenterTypes []) {
		RevenueCenter tCenterFound;
		int tCenterCount;
		int tCenterIndex;
		int tCorpID;
		int tCenterType;
		
		tCenterCount = mapCell.getRevenueCenterCount ();
		assertEquals (aExpectedCount, tCenterCount);
		for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
			tCenterFound = mapCell.getRevenueCenter (tCenterIndex);
			tCorpID = tCenterFound.getHomeCompanyID ();
			tCenterType = tCenterFound.getRevenueCenterType ().getType ();
			assertEquals (aCorpIDBases [tCenterIndex], tCorpID);
			assertEquals (aExpectedCenterTypes [tCenterIndex], tCenterType);
		}
	}
}
