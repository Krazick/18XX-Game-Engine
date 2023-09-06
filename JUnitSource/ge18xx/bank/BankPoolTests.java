package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

@DisplayName ("BankPool Tests")
class BankPoolTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private GameManager mGameManager;
	private GameManager mGameManager2;
	private GameInfo mGameInfo;
	private GameInfo gameInfo;
	BankPool bankPool;
	BankPool bankPool2;

	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.getBankPoolName ()).thenReturn ("Open Market");
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (mGameInfo);

		bankPool = bankTestFactory.buildBankPool (mGameManager);
		
		mGameManager2 = gameTestFactory.buildGameManagerMock ();
		gameInfo = gameTestFactory.buildGameInfo ();
		Mockito.when (mGameManager2.getActiveGame ()).thenReturn (gameInfo);
		bankPool2 = bankTestFactory.buildBankPool (mGameManager2);
	}

	@Test
	@DisplayName ("Constructor Tests")
	void ConstructorTests () {
		assertTrue (bankPool.isABankPool ());
		assertTrue (bankPool.isABank ());
	}

	@Test
	@DisplayName ("Bank Pool Name and Abbrev Test")
	void NameAndAbbrevTest () {
		assertEquals ("Open Market", bankPool.getName ());
		assertEquals ("Bank Pool", bankPool.getAbbrev ());

		assertEquals ("Bank Pool", bankPool2.getName ());
		assertEquals ("Bank Pool", bankPool2.getAbbrev ());
	}
}
