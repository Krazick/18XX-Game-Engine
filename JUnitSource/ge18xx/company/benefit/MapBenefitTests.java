package ge18xx.company.benefit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import ge18xx.round.RoundTestFactory;
import ge18xx.toplevel.MapFrame;
import geUtilities.utilites.xml.UtilitiesTestFactory;

class MapBenefitTests {
	GameTestFactory gameTestFactory;
	CompanyTestFactory companyTestFactory;
	MapTestFactory mapTestFactory;
	RoundTestFactory roundTestFactory;
	BenefitTestFactory benefitTestFactory;
	GameManager mGameManager;
	PrivateCompany privateSV;
	PrivateCompany privateCSL;
	MapBenefit mapBenefit1;
	MapBenefit mapBenefit2;
	MapFrame mapFrame;
	MapFrame mMapFrame;
	HexMap hexMap;
	HexMap mHexMap;
	MapCell mapCell1;
	MapCell mapCell2;
	MapCell mMapCell1;

	@BeforeEach
	void setUp () throws Exception {
		UtilitiesTestFactory tUtilitiesTestFactory;
		
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		mapTestFactory = new MapTestFactory ();
		tUtilitiesTestFactory = companyTestFactory.getUtilitiesTestFactory ();
		benefitTestFactory = new BenefitTestFactory (tUtilitiesTestFactory);
		privateSV = companyTestFactory.buildAPrivateCompany (1);
		privateCSL = companyTestFactory.buildAPrivateCompany (2);
		mGameManager = companyTestFactory.getGameManagerMock ();
		
		mapCell1 = mapTestFactory.buildMapCell ("C7");
		mapCell2 = mapTestFactory.buildMapCell ("T4");
		mMapCell1 = mapTestFactory.buildMapCellMock ("M8");
		mapFrame = mapTestFactory.buildMapFrame ("Map Benefit Test Frame", mGameManager);
		hexMap = mapTestFactory.buildHexMap (mapFrame);
		hexMap.setMapFrame (mapFrame);
		mapFrame.setHexMap (hexMap);
		
		mHexMap = mapTestFactory.buildHexMapMock ();
		mMapFrame = mapTestFactory.buildMapFrameMock ();
		
		mapBenefit1 = benefitTestFactory.buildMapBenefit (privateSV, 1);
		mapBenefit2 = benefitTestFactory.buildMapBenefit (privateCSL, 2);
		mapBenefit2.setMapCellID (mapCell1);
	}

	@Test
	@DisplayName ("Map Benefit Tests")
	void mapBenefitTests () {
		Mockito.when (mGameManager.getMapFrame ()).thenReturn (mapFrame);

		assertEquals ("B20", mapBenefit1.getMapCellID ());
		assertEquals (mapFrame, mapBenefit1.getMapFrame ());
		assertEquals (hexMap, mapBenefit1.getMap ());
		mapBenefit1.setMapCellID (mapCell2);
		assertEquals ("T4", mapBenefit1.getMapCellID ());
		assertEquals (0, mapBenefit1.getCost ());
		assertFalse (mapBenefit1.getSameTurn ());

		assertNull (mapBenefit1.getMapCell ());
		assertNull (mapBenefit1.getNewButtonLabel ());
		assertNull (mapBenefit1.getTokenType ());
	}
	
	@Test
	@DisplayName ("Map Benefit 2 Tests")
	void mapBenefitTests2 () {
		Mockito.when (mGameManager.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mHexMap.getMapCellForID (anyString ())).thenReturn (mapCell1);
		Mockito.when (mHexMap.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mMapFrame.getMap ()).thenReturn (mHexMap);

		assertEquals (mHexMap, mapBenefit2.getMap ());
		assertEquals (mMapFrame, mapBenefit2.getMapFrame ());
		assertEquals (mapCell1, mapBenefit2.getMapCell ());
		assertEquals ("Port", mapBenefit2.getTokenType ());
		Mockito.when (mHexMap.isTileAvailableForMapCell (mapCell1)).thenReturn (true);
		
		assertTrue (mapBenefit2.isTileAvailable ());
	}
	
	@Test
	@DisplayName ("Map Benefit Tile Tests")
	void mapBenefitTileTests () {
		Mockito.when (mGameManager.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mHexMap.getMapCellForID (anyString ())).thenReturn (mMapCell1);
		Mockito.when (mHexMap.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mMapFrame.getMap ()).thenReturn (mHexMap);
		Mockito.when (mMapCell1.isTileOnCell ()).thenReturn (false);
		
		assertFalse (mapBenefit2.hasTile ());
		
		Mockito.when (mMapCell1.isTileOnCell ()).thenReturn (true);
		Mockito.when (mHexMap.getMapCellForID (anyString ())).thenReturn (mMapCell1);
		assertTrue (mapBenefit2.hasTile ());		
	}
}
