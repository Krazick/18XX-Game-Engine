package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

@DisplayName ("BankPool Tests")
class BankPoolTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private GameManager mGameManager;
	BankPool bankPool;
	
	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		bankPool = bankTestFactory.buildBankPool (mGameManager);
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
		assertEquals ("Bank Pool", bankPool.getName ());
		assertEquals ("Bank Pool", bankPool.getAbbrev ());
	}
}
