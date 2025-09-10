package ge18xx.bank;

import org.junit.jupiter.api.BeforeAll;

import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameTestFactory;

class BankTester {
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
