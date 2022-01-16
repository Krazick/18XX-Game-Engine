package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.player.PlayerManager;
import ge18xx.toplevel.MapFrame;

class ChangeMapEffectTests {
	ChangeMapEffect effectAlpha;
	ChangeMapEffect effectBeta;
	ShareCompany sharePenn;
	ShareCompany shareBnO;
	GameManager gameManager;
	PlayerManager playerManager;
	TestFactory testFactory;
	MapFrame mapFrame;
	HexMap mHexMap;
	MapCell mapCell1;
	MapCell mapCell2;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		
		tClientName = "TFBuster";
		testFactory = new TestFactory ();
		gameManager =  testFactory.buildGameManager (tClientName);
		sharePenn = testFactory.buildAShareCompany (1);
		shareBnO = testFactory.buildAShareCompany (2);
		mapCell1 = testFactory.buildMapCell ();
		mapCell2 = testFactory.buildMapCell ("T4");
		mHexMap = testFactory.buildMockHexMap ();
		effectAlpha = new ChangeMapEffect (sharePenn, mapCell1);
		effectBeta = new ChangeMapEffect (sharePenn, mapCell2);
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
}
