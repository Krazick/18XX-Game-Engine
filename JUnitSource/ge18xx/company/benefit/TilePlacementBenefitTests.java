package ge18xx.company.benefit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import javax.swing.JPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TilePlacementBenefitTests extends MapBenefitTests {
	TilePlacementBenefit tilePlacementBenefit1;
	TilePlacementBenefit tilePlacementBenefit2;
	TilePlacementBenefit tilePlacementBenefit3;
	
	@Override
	@BeforeEach
	void setUp () throws Exception {
		super.setUp ();
		tilePlacementBenefit1 = benefitTestFactory.buildTilePlacementBenefit (privateSV, 1);
		tilePlacementBenefit2 = benefitTestFactory.buildTilePlacementBenefit (privateCSL, 2);
		tilePlacementBenefit3 = benefitTestFactory.buildTilePlacementBenefit (privateCSL, 3);
	}

	@Test
	@DisplayName ("Tile Placement Benefit Tests")
	void tilePlacementBenefitTests () {
		assertEquals ("Free Extra Tile Placement", tilePlacementBenefit1.getName ());
		assertEquals ("Free Tile Placement", tilePlacementBenefit2.getName ());
		assertEquals ("Tile Placement", tilePlacementBenefit3.getName ());
		assertEquals ("Put Tile on TEST-C&A Home (M15)", tilePlacementBenefit2.getNewButtonLabel ());
		assertFalse (tilePlacementBenefit2.hasButton ());
		assertTrue (tilePlacementBenefit2.shouldConfigure ());
	}

	@Test
	@DisplayName ("Tile Placement testing Map beneath")
	void tilePlacementMapTests () {
		JPanel tButtonRow;
		
		Mockito.when (mGameManager.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mHexMap.getMapCellForID (anyString ())).thenReturn (mapCell1);
		Mockito.when (mHexMap.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mMapFrame.getMap ()).thenReturn (mHexMap);
	
		tButtonRow = new JPanel ();
		tilePlacementBenefit3.configure (privateCSL, tButtonRow);
		Mockito.when (mHexMap.isTileAvailableForMapCell (mapCell1)).thenReturn (true);

		tilePlacementBenefit3.updateButton ();
	}
	
	@Test
	@DisplayName ("Tile Placement testing Tile beneath")
	void tilePlacementTileTests () {
		JPanel tButtonRow;

		Mockito.when (mGameManager.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mHexMap.getMapCellForID (anyString ())).thenReturn (mMapCell1);
		Mockito.when (mHexMap.getMapFrame ()).thenReturn (mMapFrame);
		Mockito.when (mMapFrame.getMap ()).thenReturn (mHexMap);
		Mockito.when (mMapCell1.isTileOnCell ()).thenReturn (false);
		
		tButtonRow = new JPanel ();
		tilePlacementBenefit2.configure (privateCSL, tButtonRow);
		assertFalse (tilePlacementBenefit2.hasTile ());
		
		Mockito.when (mMapCell1.isTileOnCell ()).thenReturn (true);
		Mockito.when (mHexMap.getMapCellForID (anyString ())).thenReturn (mMapCell1);
		assertTrue (tilePlacementBenefit2.hasTile ());		
		tilePlacementBenefit2.updateButton ();
	}

}
