package ge18xx.game;

import org.mockito.Mockito;

import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLNode;

public class TestFactory {

	public TestFactory () {
		
	}

	public PlayerInputFrame buildPIFMock () {
		String tClientName, tPlayer2Name;
		
		tClientName = "TFBuster";
		tPlayer2Name = "TFPlayer2";
		
		PlayerInputFrame mPlayerInputFrame = Mockito.mock (PlayerInputFrame.class);
		Mockito.when (mPlayerInputFrame.getPlayerCount ()).thenReturn (2);
		Mockito.when (mPlayerInputFrame.getPlayerName (0)).thenReturn (tClientName);
		Mockito.when (mPlayerInputFrame.getPlayerName (1)).thenReturn (tPlayer2Name);
		
		return mPlayerInputFrame;
	}
	
	public GameManager buildGameManager (String aClientName) {
		GameManager tGameManager;
		Game_18XX tGame_18XX;
		
		tGame_18XX = new Game_18XX (false);
		tGameManager = new GameManager (tGame_18XX, aClientName);
		tGame_18XX.setGameManager (tGameManager);
		
		return tGameManager;
	}
	
	public GameInfo buildGameInfo () {
		String t1830TestXML =
				"<GameInfo id=\"1\" name=\"1830TEST\" minPlayers=\"2\" maxPlayers=\"6\" bankTotal=\"1500\"  \n" + 
				"   currencyFormat=\"$ ###,###\" subtitle=\"1830 Test\" location=\"JUNIT TEST DATA\"\n" + 
				"   privates=\"true\" shares=\"true\" bankPoolShareLimit=\"5\" playerShareLimit=\"6\">\n" + 
				"   <PlayerInfo><Player numPlayers=\"2\" startingCash=\"600\" certificateLimit=\"28\" /></PlayerInfo>\n" + 
				"   <Phases><Phase name=\"1\" subName=\"1\" rounds=\"1\" tiles=\"Yellow\" trainLimit=\"4\"/></Phases>\n" + 
				"   <Trains><Train name=\"2\" order=\"1\" revenueCenters=\"2\" quantity=\"6\" price=\"80\" /></Trains>\n" + 
				"   <Files>\n" + 
				"      <File type=\"map\" name=\"1830 XML Data/1830 Map.xml\" /> \n" + 
				"      <File type=\"companies\" name=\"1830 XML Data/1830 Companies.xml\" /> \n" + 
				"      <File type=\"cities\" name=\"1830 XML Data/1830 Cities.xml\" /> \n" + 
				"      <File type=\"market\" name=\"1830 XML Data/1830 Market.xml\" /> \n" + 
				"      <File type=\"tileSet\" name=\"1830 XML Data/1830 TileSet.xml\" /> \n" + 
				"   </Files>\n" + 
				"</GameInfo>";
		GameInfo tGameInfo = null;
		XMLNode tGameInfoNode;
		XMLDocument tXMLDocument = new XMLDocument ();
		tXMLDocument = tXMLDocument.ParseXMLString (t1830TestXML);
		
		if (tXMLDocument.ValidDocument()) {
			tGameInfoNode = tXMLDocument.getDocumentElement ();
			tGameInfo = new GameInfo (tGameInfoNode);
			tGameInfo.setTestingFlag (true);
		}
		
		return tGameInfo;
	}

}
