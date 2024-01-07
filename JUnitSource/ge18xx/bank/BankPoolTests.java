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
	private GameManager mGameManager1830;
	private GameManager mGameManager1856;
	private GameInfo mGameInfo;
	private GameInfo gameInfo1830;
	private GameInfo gameInfo1856;
	BankPool bankPool;
	BankPool bankPool1830;
	BankPool bankPool1856;

	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.getBankPoolName ()).thenReturn ("Open Market");
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (mGameInfo);

		bankPool = bankTestFactory.buildBankPool (mGameManager);
		
		mGameManager1830 = gameTestFactory.buildGameManagerMock ();
		gameInfo1830 = gameTestFactory.buildGameInfo (1);
		Mockito.when (mGameManager1830.getActiveGame ()).thenReturn (gameInfo1830);
		bankPool1830 = bankTestFactory.buildBankPool (mGameManager1830);

		mGameManager1856 = gameTestFactory.buildGameManagerMock ();
		gameInfo1856 = gameTestFactory.buildGameInfo (2);
		Mockito.when (mGameManager1856.getActiveGame ()).thenReturn (gameInfo1856);
		bankPool1856 = bankTestFactory.buildBankPool (mGameManager1856);
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

		assertEquals ("Bank Pool", bankPool1830.getName ());
		assertEquals ("Bank Pool", bankPool1830.getAbbrev ());
		
		assertEquals ("Open Market", bankPool1856.getName ());
		assertEquals ("Bank Pool", bankPool1856.getAbbrev ());

	}
}
