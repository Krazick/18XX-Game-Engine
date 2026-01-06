package ge18xx.map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.center.RevenueCenter;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.LayTileEffect;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TileType;
import ge18xx.tiles.Upgrade;

import geUtilities.GUI;
import geUtilities.xml.XMLNode;

@DisplayName ("Map Cell Integration Tests")
class MapCellIntegrationTests extends MapTester {
	MapCell mapCell;
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	GameManager mGameManager;
	TileSet tileSet;
	RoundManager mRoundManager;
	Tile tile9995;
	Tile tile120;
	LayTileEffect layTileEffect;
	
	@BeforeEach
	void setUp () throws Exception {
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		mapCell = mapTestFactory.buildMapCell ("N11");
		
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
		setupTileSet (mGameManager);
	}
	
	void setupTileSet (GameManager mGameManager) {
		Mockito.when (mGameManager.getActiveGameName ()).thenReturn ("Mock GameManager MapCellTests");
		tileSet = tilesTestFactory.buildTileSet (mGameManager);
		
		tile9995 = addTileAndUpgrade (0);
		tile120 = addTileAndUpgrade (2);
		
		setupMockNeighbors ();
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

	@Test
	@DisplayName ("Test Toronto Tile Upgrade on MapCell")
	void torontoTileUpgradeTest () {	
		
		prepareAndVerifyMapCell ();
		
		// ================= Upgrade Tile to 120 ==============
		
		mapCell.upgradeTile (tileSet, tile120);
		verifyMapCellAfterUpgrade ();
	}
	
	@Test
	@DisplayName ("Test Toronto TileUpgrade via LayTileEffect")
	void torntoLayTileEffectUpgradeTest () {
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
		Mockito.when (mRoundManager.getTokenCompany ("TPRR")).thenReturn (alphaShareCompany);
		Mockito.when (mGameManager.getTokenCompany ("TPRR")).thenReturn (alphaShareCompany);
		Mockito.when (mRoundManager.getGameManager ()).thenReturn (mGameManager);
		
		layTileEffect.applyEffect (mRoundManager);
		verifyMapCellAfterUpgrade ();
	}
	
	void buildLayTileEffect () {
		String tTokens;
		String tNewTokens;
		String tBases;
		
		tTokens = GUI.EMPTY_STRING;
		tNewTokens = GUI.EMPTY_STRING;
		tBases = "TPRR,1";
		layTileEffect = new LayTileEffect (alphaShareCompany, mapCell, tile120, 0, tTokens, tBases, tNewTokens);
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
		mapCell.setFixedTile (true);

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
	
	String getXMLMapText (int aXMLIndex) {
		String tXMLMapCell;
		String [] tXMLMapCells = {
				"<MapCell>\n"
				+ "   <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "	  <Terrain category=\"optional\" location=\"16\" type=\"River\" />\n"
				+ "	  <TileName name=\"NY\" location=\"13\" />\n"
				+ "	  <Tile number=\"9940\" orientation=\"0\" starting=\"TRUE\" />\n"
				+ "	  <RevenueCenter id=\"8\" location=\"8\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "	  <RevenueCenter id=\"8\" location=\"11\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "</MapCell>",
				"<MapCell>\n"
				+ "   <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "   <Terrain category=\"optional\" location=\"50\" type=\"Hill\" />\n"
				+ "</MapCell>",
				"<MapCell>\n"
				+ "   <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "	  <Terrain category=\"optional\" location=\"8\" type=\"River\" />\n"
				+ "	  <TileName name=\"OO\" location=\"7\" />\n"
				+ "	  <RevenueCenter id=\"0\" location=\"6\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "	  <RevenueCenter id=\"0\" location=\"10\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "</MapCell>",
				"<MapCell>\n"
				+ "	  <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "   <Terrain category=\"optional\" location=\"16\" type=\"River\" />\n"
				+ "   <TileName name=\"XX\" />\n"
				+ "   <RevenueCenter id=\"19\" location=\"15\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "   <RevenueCenter id=\"19\" location=\"19\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "	  <Tile number=\"9966\" starting=\"TRUE\" />\n"
				+ "</MapCell>\n",
				"<MapCell>\n"
				+ "   <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "   <Terrain category=\"optional\" location=\"41\" type=\"River\" />\n"
				+ "   <TileName location=\"15\" name=\"Y\" />\n"
				+ "   <RevenueCenter id=\"18\" location=\"50\" name=\"\" number=\"2\" type=\"Double City\" />\n"
				+ "   <Tile number=\"9968\" starting=\"TRUE\" />\n"
				+ "</MapCell>",
				"<MapCell>\n"
				+ "	  <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "	  <Terrain category=\"optional\" location=\"50\" type=\"River\" />\n"
				+ "	  <TileName name=\"HH\" />\n"
				+ "	  <RevenueCenter id=\"3\" location=\"7\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "	  <RevenueCenter id=\"3\" location=\"14\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "	  <RevenueCenter id=\"3\" location=\"11\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "   <Tile number=\"9972\" starting=\"TRUE\" />\n"
				+ "</MapCell>",
				"<MapCell>\n"
				+ "	  <Terrain category=\"base\" type=\"Clear\" />\n"
				+ "   <Terrain category=\"optional\" location=\"16\" type=\"River\" />\n"
				+ "   <RevenueCenter id=\"19\" location=\"50\" name=\"\" number=\"1\" type=\"Single City\" />\n"
				+ "	  <Tile number=\"201\" starting=\"FALSE\" />\n"
				+ "</MapCell>\n",

		};
		tXMLMapCell = tXMLMapCells [aXMLIndex];
		
		return tXMLMapCell;
	}
	
	int [] getIntAnswers (int aIntIndex) {
		int [] tIntAnswers;
		int [] [] tAllIntAnswers = {
				{ 3,  0,  80,  80,  80,  80,  80 },
				{ 4,  0,   0, 120, 120, 120, 120 },
				{ 5,  0,  80,  80,  80,  80,  80 },
				{ 6,  0,  50,  50,  50,  50,  50 },
				{ 7,  0,  50,  50,  50,  50,  50 },
				{ 8,  0,  50,  50,  50,  50,  50 },
				{ 9,  0,  50,  50,  50,  50,  50 }
		};
		
		tIntAnswers = tAllIntAnswers [aIntIndex];
		
		return tIntAnswers;
	}
	
	int [] getTerrainCosts (int aTerrainIndex) {
		int [] tTerrainCosts;
		int [] [] tAllTerrainCosts = {
				{ 80, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 80, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 80, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 50,  70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 50,  70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 50,  70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 50,  70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
		};
		
		tTerrainCosts = tAllTerrainCosts [aTerrainIndex];
		
		return tTerrainCosts;
	}
	
	int [] getTerrainTypes (int aTerrainIndex) {
		int [] tTerrainType;
		int [] [] tAllTerrainTypes = {
				{ 10,  13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 10,  13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 10,  13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 10,  14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 10,  14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 10,  14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 10,  14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
		};
		
		tTerrainType = tAllTerrainTypes [aTerrainIndex];
		
		return tTerrainType;
	}

	@Test
	@Disabled
	@DisplayName ("Test getCostToLayTile")
	void costToLayTileTest () {
		String tXMLMapCell;
		int [] tTerrainCost;
		int [] tTerrainType;
		MapCell tMapCell;
		Terrain tBaseTerrain;
		Terrain tTerrain1;
		Tile tTile;
		Tile mTile;
		TileType mTileType;
		int [] tIntAnswers;
		int tTestSetIndex;
		int tTestCount;
		int tAnswerIndex;
		
		tTestCount = 6;
		for (tTestSetIndex = 0; tTestSetIndex < tTestCount; tTestSetIndex++ ) {
			mTile = tilesTestFactory.buildTileMock (9940);
			mTileType = tilesTestFactory.buildTileTypeMock (TileType.YELLOW);
			Mockito.when (mTile.isFixedTile ()).thenReturn (true);
			Mockito.when (mTile.getType ()).thenReturn (mTileType);
			
			tAnswerIndex = 0;
			tXMLMapCell = getXMLMapText (tTestSetIndex);
			tIntAnswers = getIntAnswers (tTestSetIndex);
			tTerrainCost = getTerrainCosts (tTestSetIndex);
			tTerrainType = getTerrainTypes (tTestSetIndex);
			
			tMapCell = buildMapCell (tXMLMapCell, tTerrainCost, tTerrainType, mTile);
			
			tBaseTerrain = tMapCell.getBaseTerrain ();
			tTerrain1 = tMapCell.getTerrain1 ();
			
			tTile = tilesTestFactory.buildTile (tIntAnswers [tAnswerIndex++]);
			assertEquals (tIntAnswers [tAnswerIndex++], tBaseTerrain.getCost (), "Set " + tTestSetIndex + " Answer Index " + tAnswerIndex);
			assertEquals (tIntAnswers [tAnswerIndex++], tTerrain1.getCost (), "Set " + tTestSetIndex + " Answer Index " + tAnswerIndex);
			assertEquals (tIntAnswers [tAnswerIndex++], tMapCell.getTotalTerrainCost (), "Set " + tTestSetIndex + " Answer Index " + tAnswerIndex);
			assertEquals (tIntAnswers [tAnswerIndex++], tMapCell.getCostToLayTile (), "Set " + tTestSetIndex + " Answer Index " + tAnswerIndex);
			assertEquals (tIntAnswers [tAnswerIndex++], tMapCell.getCostToLayTile (tTile), "Set " + tTestSetIndex + " Answer Index " + tAnswerIndex);
			tTile = Tile.NO_TILE;
			assertEquals (tIntAnswers [tAnswerIndex++], tMapCell.getCostToLayTile (tTile), "Set " + tTestSetIndex + " Answer Index " + tAnswerIndex);
		}
	}

	protected MapCell buildMapCell (String aXMLMapCell, int [] aTerrainCost, int [] aTerrainType, Tile mTile) {
		XMLNode tXMLNode;
		MapCell tMapCell;
		
		tXMLNode =  utilitiesTestFactory.buildXMLNode (aXMLMapCell);
		tMapCell = mapTestFactory.buildMapCell ("A9");
		tMapCell.loadXMLCell (tXMLNode, aTerrainCost, aTerrainType, "G19");
		tMapCell.setTile (mTile);
		
		return tMapCell;
	}
}
