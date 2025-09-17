package ge18xx.company;

import org.junit.jupiter.api.BeforeAll;

import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameTestFactory;

class CorporationTester {

	protected BankTestFactory bankTestFactory;
	protected CompanyTestFactory companyTestFactory;
	protected GameTestFactory gameTestFactory;

	@BeforeAll
	void factorySetup () {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
	}
}
