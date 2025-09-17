package ge18xx.train;

import org.junit.jupiter.api.BeforeAll;

import ge18xx.bank.BankTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
//import ge18xx.player.PlayerTestFactory;
//import ge18xx.round.RoundTestFactory;

public class TrainTester {
	GameTestFactory gameTestFactory;
//	PlayerTestFactory playerTestFactory;
	CompanyTestFactory companyTestFactory;
//	RoundTestFactory roundTestFactory;
	TrainTestFactory trainTestFactory;
	BankTestFactory bankTestFactory;
	GameManager mGameManager;

	@BeforeAll
	void factorySetup () {
		gameTestFactory = new GameTestFactory ();
//		roundTestFactory = new RoundTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		trainTestFactory = new TrainTestFactory ();
		bankTestFactory = new BankTestFactory ();
//		mGameManager = gameTestFactory.buildGameManagerMock ();
	}
}
