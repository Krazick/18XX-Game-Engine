package ge18xx.map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameTestFactory;
import ge18xx.round.RoundTestFactory;
import ge18xx.tiles.TilesTestFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;

@TestInstance (Lifecycle.PER_CLASS)
public class MapTester {
	protected CompanyTestFactory companyTestFactory;
	protected GameTestFactory gameTestFactory;
	protected MapTestFactory mapTestFactory;
	protected RoundTestFactory roundTestFactory;
	protected TilesTestFactory tilesTestFactory;
	protected UtilitiesTestFactory utilitiesTestFactory;

	@BeforeAll
	void factorySetup () {
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		mapTestFactory = new MapTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		tilesTestFactory = new TilesTestFactory (mapTestFactory);
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}
}
