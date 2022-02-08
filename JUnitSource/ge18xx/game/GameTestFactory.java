package ge18xx.game;

import org.mockito.Mockito;

//import ge18xx.company.PrivateCompany;
//import ge18xx.company.benefit.MapBenefit;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class GameTestFactory {
	UtilitiesTestFactory utilitiesTestFactory;
	private String CLIENT_NAME = "GTF Client";

	public GameTestFactory () {
		utilitiesTestFactory = new UtilitiesTestFactory ();
	}

	public PlayerInputFrame buildPIFMock () {
		String tClientName, tPlayer2Name;
		
		tClientName = CLIENT_NAME;
		tPlayer2Name = "TFPlayer2";
		
		PlayerInputFrame mPlayerInputFrame = Mockito.mock (PlayerInputFrame.class);
		Mockito.when (mPlayerInputFrame.getPlayerCount ()).thenReturn (2);
		Mockito.when (mPlayerInputFrame.getPlayerName (0)).thenReturn (tClientName);
		Mockito.when (mPlayerInputFrame.getPlayerName (1)).thenReturn (tPlayer2Name);
		
		return mPlayerInputFrame;
	}
	
	public GameManager buildGameManager () {
		return buildGameManager (CLIENT_NAME);
	}
	
	public GameManager buildGameManager (String aClientName) {
		GameManager tGameManager;
		Game_18XX tGame_18XX;
		
		tGame_18XX = buildGame18XX (aClientName);
		tGameManager = new GameManager (tGame_18XX, aClientName);
		tGame_18XX.setGameManager (tGameManager);
		
		return tGameManager;
	}

	public Game_18XX buildGame18XX (String aClientName) {
		Game_18XX tGame_18XX;
		
		tGame_18XX = new Game_18XX (false);
		tGame_18XX.setupLogger (aClientName);
		
		return tGame_18XX;
	}
	
	public GameManager buildGameManagerMock () {
		return buildGameManagerMock (CLIENT_NAME);
	}

	public GameManager buildGameManagerMock (String aClientName) {
		GameManager mGameManager = Mockito.mock (GameManager.class);
		
		Mockito.when (mGameManager.getClientUserName ()).thenReturn (aClientName);

		return mGameManager;
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
		GameInfo tGameInfo = GameInfo.NO_GAME_INFO;
		XMLNode tGameInfoNode;
		
		tGameInfoNode = utilitiesTestFactory.constructXMLNode (t1830TestXML);
		if (tGameInfoNode != XMLNode.NO_NODE) {
			tGameInfo = new GameInfo (tGameInfoNode);
			tGameInfo.setTestingFlag (true);			
		}
		
		return tGameInfo;
	}
	
	// TODO Move to a 'BenefitTestFactory'
	
//	public MapBenefit buildMapBenefit (PrivateCompany aPrivateCompany) {
//		MapBenefit tMapBenefit;
//		String tXMLBenefitTest = "<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>";
//		XMLNode tBenefitXMLNode;
//		
//		tBenefitXMLNode = utilitiesTestFactory.constructXMLNode (tXMLBenefitTest);
//
//		tMapBenefit = new MapBenefit (tBenefitXMLNode);
//		tMapBenefit.setPrivateCompany (aPrivateCompany);
//		
//		return tMapBenefit;
//	}
}
