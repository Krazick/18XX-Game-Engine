package ge18xx.map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.center.RevenueCenter;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TilesTestFactory;

class MapCellTests {
	MapCell mapCell;
	MapTestFactory mapTestFactory;
	TilesTestFactory tilesTestFactory;
	CompanyTestFactory companyTestFactory;
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	Tile mTile;
	
	@BeforeEach
	void setUp () throws Exception {
		mapTestFactory = new MapTestFactory ();
		mapCell = mapTestFactory.buildMapCell ();
		tilesTestFactory = new TilesTestFactory (mapTestFactory);
		mTile = tilesTestFactory.buildTileMock (7);
		companyTestFactory = new CompanyTestFactory ();
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
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
		
		tTile9995 = tilesTestFactory.buildTile (0);
		tTile120 = tilesTestFactory.buildTile (2);
		
		assertEquals (tTile9995.getNumber (), 9995);
		assertEquals (tTile120.getNumber (), 120);
		assertFalse (mapCell.isTileOnCell ());
		
		mapCell.putTile (tTile9995, 0);
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
