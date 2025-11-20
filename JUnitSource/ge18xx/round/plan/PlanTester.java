package ge18xx.round.plan;

import org.junit.jupiter.api.BeforeAll;

import ge18xx.bank.BankTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameTestFactory;
import ge18xx.map.MapTestFactory;
import ge18xx.tiles.TilesTestFactory;

public class PlanTester {
	protected BankTestFactory bankTestFactory;
	protected CompanyTestFactory companyTestFactory;
	protected GameTestFactory gameTestFactory;
	protected MapTestFactory mapTestFactory;
	protected TilesTestFactory tilesTestFactory;

	@BeforeAll
	void factorySetup () {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		mapTestFactory = new MapTestFactory ();
		tilesTestFactory = new TilesTestFactory (mapTestFactory);
	}
}
