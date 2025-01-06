package ge18xx.company.benefit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
	}

}
