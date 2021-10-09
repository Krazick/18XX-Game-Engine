package ge18xx.network;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;

@DisplayName ("JGameClient XML Tests")
public class JGameClientXMLTests {
	TestFactory testFactory;
	JGameClient jGameClient;
	
	@BeforeEach 
	public void setUp () {
		GameManager tGameManager;
		String tClientName;
		
		testFactory = new TestFactory ();
		tClientName = "GMTestBuster";
		tGameManager = testFactory.buildGameManager (tClientName);
		jGameClient = new JGameClient ("JGameClient Testing Frame", tGameManager);
	}
	
	@Test
	@DisplayName ("Test Game Activity Generation")
	public void JGameClient_XMLTests1 () {
		int tSelectedGameIndex = 0;
		String tBroadcastMessage = "This is a Broadcast Message";		
		String tGameActivity2, tGameActivity3;
		String tExpected1 = "<GA><GameSelection gameIndex=\"0\" z_broadcast=\"This is a Broadcast Message\"/></GA>";
				
		tGameActivity2 = jGameClient.constructGameActivityXML (JGameClient.EN_GAME_SELECTION, 
				JGameClient.AN_GAME_INDEX, tSelectedGameIndex + "",
				JGameClient.AN_BROADCAST_MESSAGE, tBroadcastMessage);
		assertEquals (tExpected1, tGameActivity2);
		
		tGameActivity3 = jGameClient.constructGameActivityXML (JGameClient.EN_GAME_SELECTION, 
				JGameClient.AN_GAME_INDEX, tSelectedGameIndex,
				JGameClient.AN_BROADCAST_MESSAGE, tBroadcastMessage);
		assertEquals (tExpected1, tGameActivity3);
	}
}