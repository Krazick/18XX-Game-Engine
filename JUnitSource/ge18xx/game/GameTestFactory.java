package ge18xx.game;

import java.io.File;
import java.util.ResourceBundle;

import org.mockito.Mockito;

import ge18xx.phase.PhaseInfo;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.toplevel.TileTrayFrame;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class GameTestFactory {
	UtilitiesTestFactory utilitiesTestFactory;
	private String CLIENT_NAME = "GTF Client";

	/**
	 * Builds the Game Test Factory by building the Utilities TestFactory
	 *
	 */
	public GameTestFactory () {
		utilitiesTestFactory = new UtilitiesTestFactory ();
	}

	public UtilitiesTestFactory getUtilitiesTestFactory () {
		return utilitiesTestFactory;
	}

	public PlayerInputFrame buildPIFMock () {
		String tClientName;
		String tPlayer2Name;

		tClientName = CLIENT_NAME;
		tPlayer2Name = "TFPlayer2";
		
		PlayerInputFrame mPlayerInputFrame = Mockito.mock (PlayerInputFrame.class);
		Mockito.when (mPlayerInputFrame.getPlayerCount ()).thenReturn (2);
		Mockito.when (mPlayerInputFrame.getPlayerName (0)).thenReturn (tClientName);
		Mockito.when (mPlayerInputFrame.getPlayerName (1)).thenReturn (tPlayer2Name);
				
		return mPlayerInputFrame;
	}
	
	public PlayerInputFrame buildPIFMockWithGameSet () {
		GameSet tGameSet;

		PlayerInputFrame mPlayerInputFrame = Mockito.mock (PlayerInputFrame.class);
		
		tGameSet = new GameSet (mPlayerInputFrame);

		Mockito.when (mPlayerInputFrame.getGameSet ()).thenReturn (tGameSet);
		
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
		ResourceBundle tResourceBundle;
		String tResourceName;
		
		tResourceName = Game_18XX.RESOURCE_NAME;
		tResourceBundle = Game_18XX.readResourceBundle (tResourceName);

		tGame_18XX = new Game_18XX (tResourceBundle, false);
		tGame_18XX.setupLogger (aClientName, Game_18XX.GAME_NAME + "_JUNIT");

		return tGame_18XX;
	}

	public GameManager buildGameManagerMock () {
		return buildGameManagerMock (CLIENT_NAME);
	}

	public GameManager buildGameManagerMock (String aClientName) {
		GameManager mGameManager = Mockito.mock (GameManager.class);

		Mockito.when (mGameManager.getClientUserName ()).thenReturn (aClientName);
		Mockito.when (mGameManager.getXMLBaseDirectory ()).thenReturn ("18XX XML Data" + File.separator);

		return mGameManager;
	}

	public GameInfo buildGameInfoMock () {
		GameInfo mGameInfo = Mockito.mock (GameInfo.class);
		
		Mockito.when (mGameInfo.getName ()).thenReturn ("1830Test");

		return mGameInfo;
	}

	public GameInfo buildGameInfo (int aGameInfoIndex) {
		String t1830TestXML = "<GameInfo id=\"1\" name=\"1830TEST\" minPlayers=\"2\" " 
				+ "   maxPlayers=\"6\" bankTotal=\"1500\"  currencyFormat=\"$ ###,###\"\n"
				+ "   subtitle=\"1830 Test\" location=\"JUNIT TEST DATA\"\n"
				+ "   privates=\"true\" shares=\"true\" bankPoolShareLimit=\"5\""
				+ "   playerShareLimit=\"6\">\n ipoDividends=\"Bank\" "
				+ "   bankPoolDividends=\"corporation\"\n"
				+ "   <PlayerInfo><Player numPlayers=\"2\" startingCash=\"600\" "
				+ "      certificateLimit=\"28\" /></PlayerInfo>\n"
				+ "   <Phases><Phase name=\"1\" subName=\"1\" rounds=\"1\" tiles=\"Yellow\"  "
				+ "      trainLimit=\"4\"/></Phases>\n"
				+ "   <Trains><Train name=\"2\" order=\"1\" revenueCenters=\"2\" "
				+ "      quantity=\"6\" price=\"80\" /></Trains>\n"
				+ "   <Files>\n" + "      <File type=\"map\" "
				+ "      name=\"1830TEST XML Data/1830TEST Map.xml\" /> \n"
				+ "    <File type=\"companies\" name=\"1830TEST XML Data/1830TEST Companies.xml\" /> \n"
				+ "    <File type=\"cities\" name=\"1830TEST XML Data/1830TEST Cities.xml\" /> \n"
				+ "    <File type=\"market\" name=\"1830TEST XML Data/1830TEST Market.xml\" /> \n"
				+ "    <File type=\"tileSet\" name=\"1830TEST XML Data/1830TEST TileSet.xml\" /> \n"
				+ "   </Files>\n"
				+ "</GameInfo>";
		String t1856TestXML = "	<GameInfo id=\"4\" name=\"1856\" "
				+ "     Subtitle=\"Railroading in Upper Canada from 1856\" "
				+ "     minPlayers=\"3\" maxPlayers=\"6\"\n"
				+ "		status=\"IN PROGRESS\" bankTotal=\"12000\" "
				+ "     currencyFormat=\"$ ###,###\" designers=\"Bill Dixon (Canada)\"\n"
				+ "		location=\"Southern Ontario, Canada\" "
				+ "     producers=\"Mayfair Games (US)\" releaseDate=\"1995\"\n"
				+ "		firstTokenCost=\"40\" laterTokenCost=\"100\" maxRounds=\"3\" \n"
				+ "		privates=\"true\" minors=\"false\" shares=\"true\" "
				+ "     bankPoolDividends=\"corporation\" ipoDivedends=\"bank\"\n"
				+ "		bankPoolShareLimit=\"5\" playerShareLimit=\"6\" \n"
				+ "		licenses=\"true\" noTouchPass=\"true\" bankPoolName=\"Open Market\"\n"
				+ "		loans=\"true\" loanAmount=\"100\" loanInterest=\"10\">\n"
				+ "		<Capitalizations>\n"
				+ "			<Capitalization availableTrain=\"2\" level=\"Incremental_5\" />\n"
				+ "			<Capitalization availableTrain=\"3\" level=\"Incremental_5\" />\n"
				+ "			<Capitalization availableTrain=\"4\" level=\"Incremental_5\" />\n"
				+ "			<Capitalization availableTrain=\"5\" level=\"Incremental_10\" />\n"
				+ "			<Capitalization availableTrain=\"6\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"Diesel\" level=\"FULL\" />\n"
				+ "		</Capitalizations>\n"
				+ "		<PlayerInfo>\n"
				+ "			<Player numPlayers=\"3\" startingCash=\"500\" certificateLimit=\"20\" />\n"
				+ "     </PlayerInfo>\n"
				+ "		<Phases>\n"
				+ "			<Phase name=\"1\" subName=\"1\" rounds=\"1\" tiles=\"Yellow\"\n"
				+ "				trainLimit=\"4\" offBoard=\"Low\" capitalization=\"Incremental_5\"\n"
				+ "				loansAllowed=\"true\" minToFloat=\"2\" minToFloatLast=\"3\" />\n"
				+ "		</Phases>\n"
				+ "		<Trains>\n"
				+ "			<Train name=\"2\" order=\"1\" revenueCenters=\"2\" quantity=\"6\" "
				+ "				price=\"100\" onLast=\"2\" />\n"
				+ "		</Trains>\n"
				+ "		<Files>\n"
				+ "			<File type=\"map\" name=\"1856/1856-Map.xml\" />\n"
				+ "			<File type=\"companies\" name=\"1856/1856-Companies.xml\" />\n"
				+ "			<File type=\"cities\" name=\"1856/1856-Cities.xml\" />\n"
				+ "			<File type=\"market\" name=\"1856/1856-Market.xml\" />\n"
				+ "			<File type=\"tileSet\" name=\"1856/1856-TileSet.xml\" />\n"
				+ "		</Files>\n"
				+ "	</GameInfo>\n";

		GameInfo tGameInfo = GameInfo.NO_GAME_INFO;
		XMLNode tGameInfoNode;

		if (aGameInfoIndex == 1) {
			tGameInfoNode = utilitiesTestFactory.buildXMLNode (t1830TestXML);
		} else if (aGameInfoIndex == 2) {
			tGameInfoNode = utilitiesTestFactory.buildXMLNode (t1856TestXML);
		} else {
			tGameInfoNode = XMLNode.NO_NODE;
		}
		if (tGameInfoNode != XMLNode.NO_NODE) {
			tGameInfo = new GameInfo (tGameInfoNode);
			tGameInfo.setTestingFlag (true);
		}

		return tGameInfo;
	}
	
	public PhaseInfo buildPhaseInfoMock () {
		PhaseInfo mPhaseInfo;
		
		mPhaseInfo  = Mockito.mock (PhaseInfo.class);
		Mockito.when (mPhaseInfo.getFullName ()).thenReturn ("TEST1");
		Mockito.when (mPhaseInfo.getTrainLimit ()).thenReturn (1);
		Mockito.when (mPhaseInfo.getTiles ()).thenReturn ("TEST_TILES");
		
		return mPhaseInfo;
	}
	
	public TileTrayFrame buildTileTrayFrame (GameManager aGameManager) {
		TileTrayFrame tTileTrayFrame;
		String tFrameName;
		
		tFrameName = TileTrayFrame.BASE_TITLE;
		
		tTileTrayFrame = new TileTrayFrame (tFrameName, aGameManager);
		
		return tTileTrayFrame;
	}
}
