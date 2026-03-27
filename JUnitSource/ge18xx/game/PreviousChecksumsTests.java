package ge18xx.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import geUtilities.GUI;

@DisplayName ("Previous Checksums Tests")
class PreviousChecksumsTests {
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	GameManager gameManager;
	RoundManager mRoundManager;
	
	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		roundTestFactory = new RoundTestFactory ();
		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		Mockito.when (mRoundManager.getActionNumberAt (0)).thenReturn (0);
		Mockito.when (mRoundManager.getActionNumberAt (1)).thenReturn (1);
		Mockito.when (mRoundManager.getActionNumberAt (2)).thenReturn (2);
		gameManager.setRoundManager (mRoundManager);
	}

	@Test
	@DisplayName ("Basic Previous Checksums Tests")
	void previousChecksumsTests () {
		String tAlphaChecksum;
		String tBetaChecksum;
		
		assertEquals (1, gameManager.getPreviousChecksumCount ());
		assertEquals (GUI.EMPTY_STRING, gameManager.getLastPreviousChecksum ());
		
		assertEquals (0, gameManager.getLastPreviousChecksumIndex ());
		
		tAlphaChecksum = "ALPHA Checksum";
		gameManager.addPreviousChecksum ("1", tAlphaChecksum);
		assertEquals (2, gameManager.getPreviousChecksumCount ());
		assertEquals (1, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("ALPHA Checksum", gameManager.getLastPreviousChecksum ());
		
		tBetaChecksum = "BETA Checksum";
		gameManager.setPreviousChecksumValue (tBetaChecksum);
		assertEquals (2, gameManager.getPreviousChecksumCount ());
		assertEquals (1, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("BETA Checksum", gameManager.getLastPreviousChecksum ());
		
		gameManager.addPreviousChecksum ("2", tAlphaChecksum);
		assertEquals (3, gameManager.getPreviousChecksumCount ());
		assertEquals (2, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("ALPHA Checksum", gameManager.getLastPreviousChecksum ());

		gameManager.removeLastPreviousChecksum ();
		assertEquals (2, gameManager.getPreviousChecksumCount ());
		assertEquals (1, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("BETA Checksum", gameManager.getLastPreviousChecksum ());
		
		tAlphaChecksum = "ALPHA 2nd Checksum";
		gameManager.addPreviousChecksum (2, tAlphaChecksum);
		assertEquals (3, gameManager.getPreviousChecksumCount ());
		assertEquals (2, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("ALPHA 2nd Checksum", gameManager.getLastPreviousChecksum ());
		
		Mockito.when (mRoundManager.getActionNumberAt (3)).thenReturn (3);
		tBetaChecksum = "BETA 2nd Checksum";
		gameManager.addPreviousChecksum (3, tBetaChecksum);
		assertEquals (4, gameManager.getPreviousChecksumCount ());
		assertEquals (3, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("BETA 2nd Checksum", gameManager.getLastPreviousChecksum ());

	}

}
