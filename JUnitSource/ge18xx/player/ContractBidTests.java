package ge18xx.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.JLabel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

class ContractBidTests {
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	GameManager mGameManager;
	PlayerManager mPlayerManager;
	Bank bank;
	Player player;

	int playerCount;
	int certificateLimit;
	String playerName;

	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();

		mGameManager = setupGameInfoAndManager (4);
		
		playerTestFactory = new PlayerTestFactory (mGameManager);
		BankTestFactory bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		bank.setFormat ("£ ###,###");

		playerCount = 4;
		certificateLimit = 16;
		playerName = "BusterPlayer";
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (playerCount);
		player = playerTestFactory.buildPlayer (playerName, mPlayerManager, certificateLimit);
	}
	
	protected GameManager setupGameInfoAndManager (int aGameInfoIndex) {
		GameInfo tGameInfo;
		GameManager tmGameManager;
		
		tGameInfo = gameTestFactory.buildGameInfo (aGameInfoIndex);
		tmGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (tmGameManager.getMaxRounds ()).thenReturn (1);
		Mockito.when (tmGameManager.getActiveGame ()).thenReturn (tGameInfo);
		Mockito.when (tmGameManager.gameHasRoundType ("Contract Bid Round")).thenReturn (true);
		
		return tmGameManager;
	}

	@Test
	@DisplayName ("Player Contract Bid Tests")
	void playerContractBidTests () {
		assertTrue (player.hasContractBid ());
		assertFalse (player.hasSignedContractBid ());
		assertFalse (player.hasFulfilledContractBid ());
			
		player.setHasSignedContractBid (true);
		assertTrue (player.hasSignedContractBid ());
		player.setHasSignedContractBid (false);
		assertFalse (player.hasSignedContractBid ());
		
		player.setHasFullfilledContractBid (true);
		assertFalse (player.hasFulfilledContractBid ());
		player.setHasSignedContractBid (true);

		player.setHasFullfilledContractBid (true);
		assertTrue (player.hasFulfilledContractBid ());
		player.setHasFullfilledContractBid (false);
		assertFalse (player.hasFulfilledContractBid ());
	}
	
	@Test
	@DisplayName ("Player Contract Bid Label Tests")
	void playerContractBidLabelTests () {
		JLabel tContractJLabel;
		ContractBid tContractBid;
		
		tContractBid = player.getContractBid ();
		assertNotNull (tContractBid);
		tContractJLabel = tContractBid.buildLabel ();
		assertEquals ("Contract Bid: Unsigned", tContractJLabel.getText ());
		
		player.setHasSignedContractBid (true);
		tContractJLabel = tContractBid.buildLabel ();
		assertEquals ("Contract Bid: Signed 0/£ 0", tContractJLabel.getText ());
		
		player.setHasFullfilledContractBid (true);
		tContractJLabel = tContractBid.buildLabel ();
		assertEquals ("Contract Bid: Fulfilled", tContractJLabel.getText ());
	}
}
