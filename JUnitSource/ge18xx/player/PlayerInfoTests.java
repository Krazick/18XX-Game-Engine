package ge18xx.player;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import geUtilities.utilites.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

class PlayerInfoTests {
	UtilitiesTestFactory utilitiesTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		utilitiesTestFactory = new UtilitiesTestFactory ();
	}
	
	XMLNode buildPlayerInfoNode (int tPlayerInfoIndex) {
	
		String tPlayerInfo1XML="<Player numPlayers=\"4\" startingCash=\"375\" certificateLimit=\"16\" />";
		String tPlayerInfo2XML="<Player numPlayers=\"4\" startingCash=\"375\" phases=\"5.1,6.1\"\n"
				+ "				companies=\"11\" certificateLimit=\"22\" />";
		XMLNode tPlayerInfoNode;

		tPlayerInfoNode = PlayerInfo.NO_PLAYER_INFO_NODE;
		if (tPlayerInfoIndex == 1) {
			tPlayerInfoNode = utilitiesTestFactory.buildXMLNode (tPlayerInfo1XML);
		} if (tPlayerInfoIndex == 2) {
			tPlayerInfoNode = utilitiesTestFactory.buildXMLNode (tPlayerInfo2XML);
		}
		
		return tPlayerInfoNode;
	}

	@Test
	@DisplayName ("PlayerInfo Construction Test")
	void playerInfoConstructionTest () {
		XMLNode tPlayerInfoNode1;
		XMLNode tPlayerInfoNode2;
		PlayerInfo tPlayerInfo1;
		PlayerInfo tPlayerInfo2;
		
		tPlayerInfoNode1 = buildPlayerInfoNode (1);
		tPlayerInfo1 = new PlayerInfo (tPlayerInfoNode1);
		tPlayerInfoNode2 = buildPlayerInfoNode (2);
		tPlayerInfo2 = new PlayerInfo (tPlayerInfoNode2);
		
		assertEquals (4, tPlayerInfo1.getNumPlayers ());
		assertEquals (375, tPlayerInfo1.getStartingCash ());
		assertEquals (16, tPlayerInfo1.getCertificateLimit ());

		assertEquals (4, tPlayerInfo2.getNumPlayers ());
		assertEquals (375, tPlayerInfo2.getStartingCash ());
		assertEquals (22, tPlayerInfo2.getCertificateLimit ());
	}

}
