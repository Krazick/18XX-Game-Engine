package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.benefit.BenefitTestFactory;
import ge18xx.company.benefit.MapBenefit;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.UtilitiesTestFactory;

class ChangeMapEffectTests {
	ChangeMapEffect effectAlpha;
	ChangeMapEffect effectBeta;
	ChangeMapEffect effectGamma;
	ShareCompany sharePenn;
	ShareCompany shareBnO;
	PrivateCompany privateSV;
	GameManager gameManager;
	PlayerManager playerManager;
	GameTestFactory gameTestFactory;
	CompanyTestFactory companyTestFactory;
	MapTestFactory mapTestFactory;
	RoundTestFactory roundTestFactory;
	BenefitTestFactory benefitTestFactory;
	MapFrame mapFrame;
	HexMap mHexMap;
	MapCell mapCell1;
	MapCell mapCell2;
	MapBenefit mapBenefit;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		UtilitiesTestFactory tUtilitiesTestFactory;

		tClientName = "TFBuster";
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		tUtilitiesTestFactory = companyTestFactory.getUtilitiesTestFactory ();
		benefitTestFactory = new BenefitTestFactory (tUtilitiesTestFactory);
		gameManager = gameTestFactory.buildGameManager (tClientName);
		sharePenn = companyTestFactory.buildAShareCompany (1);
		shareBnO = companyTestFactory.buildAShareCompany (2);
		privateSV = companyTestFactory.buildAPrivateCompany (1);
		mapTestFactory = new MapTestFactory ();
		mapCell1 = mapTestFactory.buildMapCell ();
		mapCell2 = mapTestFactory.buildMapCell ("T4");
		mHexMap = mapTestFactory.buildMockHexMap ();
		mapBenefit = benefitTestFactory.buildMapBenefit (privateSV);
		effectAlpha = new ChangeMapEffect (sharePenn, mapCell1);
		effectBeta = new ChangeMapEffect (sharePenn, mapCell2);
		effectGamma = new ChangeMapEffect (shareBnO, mapCell2, mapBenefit);
	}

	@Test
	@DisplayName ("Simple ChangeMapEffect Tests")
	void simpleConstructorTests () {
		assertEquals ("T1", effectAlpha.getMapCellID ());
		assertEquals ("T4", effectBeta.getMapCellID ());
		effectBeta.setMapCellID ("B2");
		assertEquals ("B2", effectBeta.getMapCellID ());
		assertEquals (mapCell2, effectAlpha.getMapCell (mHexMap));
	}

	@Test
	@DisplayName ("Benefit used by ChangeMapEffect Tests")
	void benefitMapEffectTests () {
		assertFalse (effectAlpha.getBenefitUsed ());
		assertEquals ("", effectAlpha.getBenefitName ());
		assertEquals ("", effectAlpha.getBenefitPrivateAbbrev ());
		assertFalse (effectGamma.getBenefitUsed ());
		assertEquals ("Map", effectGamma.getBenefitName ());
		assertEquals ("TEST-C&SL", effectGamma.getBenefitPrivateAbbrev ());
	}

	@Test
	@DisplayName ("Using RoundManager Test")
	void useOfRoundManagerTests () {
		RoundManager tRoundManager;
		RoundTestFactory tRoundTestFactory;
		String tEffectReport;

		tRoundTestFactory = new RoundTestFactory ();
		tRoundManager = tRoundTestFactory.buildRoundManager ();
		tEffectReport = effectAlpha.getEffectReport (tRoundManager);
		assertEquals ("--Effect: Change Map by TestPennsylvania on MapCell T1.", tEffectReport);
		assertTrue (effectAlpha.undoEffect (tRoundManager));
	}
}
