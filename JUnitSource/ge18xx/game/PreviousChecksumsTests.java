package ge18xx.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Previous Checksums Tests")
class PreviousChecksumsTests {
	GameTestFactory gameTestFactory;
	GameManager gameManager;
	
	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
	}

	@Test
	@DisplayName ("Basic Previous Checksums Tests")
	void previousChecksumsTests () {
		String tAlphaChecksum;
		String tBetaChecksum;
		
		assertEquals (1, gameManager.getPreviousChecksumCount ());
		assertEquals ("", gameManager.getLastPreviousChecksum ());
		
		assertEquals (0, gameManager.getLastPreviousChecksumIndex ());
		
		tAlphaChecksum = "ALPHA Checksum";
		gameManager.addPreviousChecksum (tAlphaChecksum);
		assertEquals (2, gameManager.getPreviousChecksumCount ());
		assertEquals (1, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("ALPHA Checksum", gameManager.getLastPreviousChecksum ());
		
		tBetaChecksum = "BETA Checksum";
		gameManager.setPreviousChecksumValue (tBetaChecksum);
		assertEquals (2, gameManager.getPreviousChecksumCount ());
		assertEquals (1, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("BETA Checksum", gameManager.getLastPreviousChecksum ());
		
		gameManager.addPreviousChecksum (tAlphaChecksum);
		assertEquals (3, gameManager.getPreviousChecksumCount ());
		assertEquals (2, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("ALPHA Checksum", gameManager.getLastPreviousChecksum ());

		gameManager.removeLastPreviousChecksum ();
		assertEquals (2, gameManager.getPreviousChecksumCount ());
		assertEquals (1, gameManager.getLastPreviousChecksumIndex ());
		assertEquals ("BETA Checksum", gameManager.getLastPreviousChecksum ());
	}

}
