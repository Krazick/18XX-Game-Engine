package ge18xx.network;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Test Network Players Class")
class NetworkPlayerTests {

	NetworkPlayer basicNetworkPlayer;
	
	@BeforeEach
	void setUp () throws Exception {
		basicNetworkPlayer = new NetworkPlayer ("BasicPlayer1");
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Network Player Construction Test")
	void testBasicConstructors () {
		NetworkPlayer badNameForNetworkPlayer;
		
		assertEquals ("BasicPlayer1", basicNetworkPlayer.getName ());
		
		badNameForNetworkPlayer = new NetworkPlayer ("First Last");
		assertEquals (NetworkPlayer.INVALID_NAME, badNameForNetworkPlayer.getName ());
		
		badNameForNetworkPlayer = new NetworkPlayer ("1FirstLast");
		assertEquals (NetworkPlayer.INVALID_NAME, badNameForNetworkPlayer.getName ());
		
		badNameForNetworkPlayer = new NetworkPlayer ("First###Last");
		assertEquals (NetworkPlayer.INVALID_NAME, badNameForNetworkPlayer.getName ());
		
		badNameForNetworkPlayer = new NetworkPlayer (null);
		assertEquals (NetworkPlayer.INVALID_NAME, badNameForNetworkPlayer.getName ());
		
		badNameForNetworkPlayer = new NetworkPlayer ("");
		assertEquals (NetworkPlayer.INVALID_NAME, badNameForNetworkPlayer.getName ());
		
		badNameForNetworkPlayer = new NetworkPlayer("First_Last");
		assertEquals ("First_Last", badNameForNetworkPlayer.getName ());
	}

	@Test
	@DisplayName ("Network Player AFK and READY Tests")
	void NetworkPlayerAFK_READY_Tests () {
		assertEquals ("BasicPlayer1", basicNetworkPlayer.toString ());
		
		basicNetworkPlayer.setAFK (true);
		assertEquals ("BasicPlayer1 [AFK]", basicNetworkPlayer.toString ());
		assertTrue (basicNetworkPlayer.isAFK ());
		
		basicNetworkPlayer.setReady (true);
		assertEquals ("BasicPlayer1 [READY] [AFK]", basicNetworkPlayer.toString ());
		
		assertTrue (basicNetworkPlayer.isAFK ());
		assertTrue (basicNetworkPlayer.isReady ());
		
		basicNetworkPlayer.setAFK (false);
		assertEquals ("BasicPlayer1 [READY]", basicNetworkPlayer.toString ());
		
		assertFalse (basicNetworkPlayer.isAFK ());
		assertTrue (basicNetworkPlayer.isReady ());
		
		basicNetworkPlayer.setReady (false);
		assertFalse (basicNetworkPlayer.isAFK ());
		assertFalse (basicNetworkPlayer.isReady ());
	
	}
}