package ge18xx.game;

import java.util.ResourceBundle;

import org.mockito.Mockito;

import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.toplevel.TileTrayFrame;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class GameTestFactory {
	UtilitiesTestFactory utilitiesTestFactory;
	private String CLIENT_NAME = "GTF Client";
	GameManager mGameManager;

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
		GameInfo tGameInfo;
		
		mGameManager = Mockito.mock (GameManager.class);
		tGameInfo = buildGameInfo (1);
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (tGameInfo);
		
		return mGameManager;
	}

	public GameInfo buildGameInfoMock () {
		GameInfo mGameInfo = Mockito.mock (GameInfo.class);
		
		Mockito.when (mGameInfo.getName ()).thenReturn ("1830Test");

		return mGameInfo;
	}

	public GameInfo buildGameInfo (int aGameInfoIndex) {
		String t1830TestXML = "<GameInfo id=\"1\" name=\"1830TEST\" minPlayers=\"2\" " 
				+ "   maxPlayers=\"6\" bankTotal=\"1500\" currencyFormat=\"$ ###,###\" \n"
				+ "   subtitle=\"1830 Test\" location=\"JUNIT TEST DATA\"\n"
				+ "   privates=\"true\" shares=\"true\" bankPoolShareLimit=\"5\""
				+ "   playerShareLimit=\"6\" ipoDividends=\"Bank\" "
				+ "   maxRounds=\"3\" initialRoundName=\"Stock Round\" "
				+ "   bankPoolDividends=\"corporation\" >\n"
				+ "   <Capitalizations>\n"
				+ "     <Capitalization availableTrain=\"2\" level=\"FULL\" />\n"
				+ "     <Capitalization availableTrain=\"3\" level=\"FULL\" />\n"
				+ "     <Capitalization availableTrain=\"4\" level=\"FULL\" />\n"
				+ "     <Capitalization availableTrain=\"5\" level=\"FULL\" />\n"
				+ "     <Capitalization availableTrain=\"6\" level=\"FULL\" />\n"
				+ "     <Capitalization availableTrain=\"Diesel\" level=\"FULL\" />\n"
				+ "   </Capitalizations>\n"
				+ "   <RoundTypes>\n"
				+ "     <RoundType name=\"Stock Round\" initialRound=\"true\" "
				+ "                interruptionRound=\"Auction Round\" nextRound=\"Operating Round\""
				+ "					endsAfterActions=\"Pass Action\" />\n"
				+ "     <RoundType name=\"Auction Round\" interruptsAfterActions=\"Done Player Action, Win Auction Action\" />\n"
				+ "     <RoundType name=\"Operating Round\" nextRound=\"Stock Round\" maxRounds=\"3\""
				+ "					endsAfterActions=\"Done Corp Action\" />\n"
				+ "  </RoundTypes>\n"
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
		String t1835TestXML = "	<GameInfo id=\"2\" name=\"1835\" minPlayers=\"3\" maxPlayers=\"7\" status=\"Mostly Playable\"\n"
				+ "		bankTotal=\"12000\" currencyFormat=\"###,### M\" designers=\"Michael Meier-Bachl (Germany), Francis Tresham\"\n"
				+ "		producers=\"Hans im Gluck (Germany)\" location=\"Germany\" \n"
				+ "		maxRounds=\"3\" initialRoundName=\"Stock Round\"\n"
				+ "		releaseDate=\"1990\" bankPoolDividends=\"corporation\" ipoDividends=\"bank\" \n"
				+ "		tokenType=\"RangeCost\" allTokensCost=\"20\"\n"
				+ "		privates=\"true\" minors=\"true\" shares=\"true\" bankPoolShareLimit=\"5\" playerShareLimit=\"10\">\n"
				+ "		<Capitalizations>\n"
				+ "			<Capitalization availableTrain=\"2\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"2+2\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"3\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"3+3\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"4\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"4+4\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"5\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"5+5\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"6\" level=\"Always_Incremental\" />\n"
				+ "			<Capitalization availableTrain=\"6+6\" level=\"Always_Incremental\" />\n"
				+ "		</Capitalizations>\n"
				+ "		<RoundTypes>\n"
				+ "			<RoundType name=\"Stock Round\" initialRound=\"true\" interruptionRound=\"Formation Round\"\n"
				+ "					nextRound=\"Operating Round\" endsAfterActions=\"Pass Action\" />\n"
				+ "			<RoundType name=\"Operating Round\" interruptionRound=\"Formation Round\" \n"
				+ "					nextRound=\"Stock Round\" maxRounds=\"3\" \n"
				+ "					endsAfterActions=\"Done Action, Pay Revenue Action, Change Round Action\" />\n"
				+ "			<RoundType name=\"Formation Round\"\n"
				+ "					endsAfterActions=\"Change Formation Round State Action, Final Formation Action\" \n"
				+ "					interruptsAfterActions=\"Done Action, Pass Action, Sold Out Adjustment Action\" \n"
				+ "					interruptedName=\"Operating Round\"\n"
				+ "					interruptsCondition=\"All Passed\"\n"
				+ "					phases=\"2.3, 2.4, 3.1\" />\n"
				+ "		</RoundTypes>\n"
				+ "		<Variants>\n"
				+ "			<Variant class=\"ge18xx.game.variants.VariantAll\" id=\"100\" \n"
				+ "				title=\"Increase Leipzig Dresdener Private Railway Revenue\">\n"
				+ "			</Variant>\n"
				+ "			<Variant class=\"ge18xx.game.variants.VariantAll\" id=\"200\" title=\"Unsold Privates decrease value\">\n"
				+ "			</Variant>\n"
				+ "			<Variant class=\"ge18xx.game.variants.VariantAll\" id=\"300\" title=\"Minor Companies don't Operate\">\n"
				+ "			</Variant>\n"
				+ "		</Variants>\n"
				+ "		<PlayerInfo>\n"
				+ "			<Player numPlayers=\"3\" startingCash=\"600\" certificateLimit=\"19\" />\n"
				+ "			<Player numPlayers=\"4\" startingCash=\"475\" certificateLimit=\"15\" />\n"
				+ "			<Player numPlayers=\"5\" startingCash=\"390\" certificateLimit=\"12\" />\n"
				+ "			<Player numPlayers=\"6\" startingCash=\"340\" certificateLimit=\"11\" />\n"
				+ "			<Player numPlayers=\"7\" startingCash=\"310\" certificateLimit=\"9\" />\n"
				+ "		</PlayerInfo>\n"
				+ "		<Phases>\n"
				+ "			<Phase name=\"1\" subName=\"1\" rounds=\"1\" tiles=\"Yellow\" tileLaysAllowed=\"2\"\n"
				+ "				majorTrainLimit=\"4\" minorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"Low\" />\n"
				+ "			<Phase name=\"1\" subName=\"2\" rounds=\"1\" tiles=\"Yellow\" tileLaysAllowed=\"2\"\n"
				+ "				majorTrainLimit=\"4\" minorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"Low\" />\n"
				+ "			<Phase name=\"2\" subName=\"1\" rounds=\"2\" tiles=\"Yellow,Green\"\n"
				+ "				canBuyPrivate=\"false\" canBuyTrain=\"true\" majorTrainLimit=\"4\" \n"
				+ "				minorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"Mid\" />\n"
				+ "			<Phase name=\"2\" subName=\"2\" rounds=\"2\" tiles=\"Yellow,Green\"\n"
				+ "				canBuyPrivate=\"false\" canBuyTrain=\"true\" majorTrainLimit=\"3\" \n"
				+ "				minorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"Mid\" />\n"
				+ "			<Phase name=\"2\" subName=\"3\" rounds=\"2\" tiles=\"Yellow,Green\"\n"
				+ "				canBuyPrivate=\"false\" canBuyTrain=\"true\" govtTrainLimit=\"4\" majorTrainLimit=\"3\" \n"
				+ "				minorTrainLimit=\"1\" willFloat=\"5\" offBoard=\"Mid\" \n"
				+ "				triggerClass=\"ge18xx.company.formation.FormPrussian\" formCompanyID=\"1810\"/>\n"
				+ "			<Phase name=\"2\" subName=\"4\" rounds=\"2\" tiles=\"Yellow,Green\"\n"
				+ "				canBuyTrain=\"true\" govtTrainLimit=\"4\" majorTrainLimit=\"3\" minorTrainLimit=\"1\" \n"
				+ "				willFloat=\"5\" offBoard=\"Mid\" mustStart=\"true\"\n"
				+ "				triggerClass=\"ge18xx.company.formation.FormPrussian\" formCompanyID=\"1810\" />\n"
				+ "			<Phase name=\"3\" subName=\"1\" rounds=\"3\" tiles=\"Yellow,Green,Brown\"\n"
				+ "				canBuyTrain=\"true\" closePrivate=\"true\" govtTrainLimit=\"3\" majorTrainLimit=\"2\" \n"
				+ "				minorTrainLimit=\"1\" willFloat=\"5\" offBoard=\"High\" mustConvert=\"true\"\n"
				+ "				triggerClass=\"ge18xx.company.formation.FormPrussian\" formCompanyID=\"1810\" />\n"
				+ "			<Phase name=\"3\" subName=\"2\" rounds=\"3\" tiles=\"Yellow,Green,Brown\"\n"
				+ "				canBuyTrain=\"true\" govtTrainLimit=\"3\" majorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"High\" />\n"
				+ "			<Phase name=\"3\" subName=\"3\" rounds=\"3\" tiles=\"Yellow,Green,Brown\"\n"
				+ "				canBuyTrain=\"true\" govtTrainLimit=\"3\" majorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"High\" />\n"
				+ "			<Phase name=\"3\" subName=\"4\" rounds=\"3\" tiles=\"Yellow,Green,Brown\"\n"
				+ "				canBuyTrain=\"true\" govtTrainLimit=\"3\" majorTrainLimit=\"2\" willFloat=\"5\" offBoard=\"High\" />\n"
				+ "		</Phases>\n"
				+ "		<Trains>\n"
				+ "			<Train name=\"2\" order=\"1\" revenueCenters=\"2\" quantity=\"9\" price=\"80\" onLast=\"2\" />\n"
				+ "			<Train name=\"2+2\" order=\"2\" revenueCenters=\"2\" townRevenueCenters=\"2\"\n"
				+ "				quantity=\"4\" price=\"120\" onLast=\"3\" triggerPhase=\"1.2\" />\n"
				+ "			<Train name=\"3\" order=\"3\" revenueCenters=\"3\" quantity=\"4\" price=\"180\"\n"
				+ "				onLast=\"4\" triggerPhase=\"2.1\" tileInfo=\"Green\" />\n"
				+ "			<Train name=\"3+3\" order=\"4\" revenueCenters=\"3\" townRevenueCenters=\"3\"\n"
				+ "				quantity=\"3\" price=\"270\" onLast=\"5\" triggerPhase=\"2.2\" />\n"
				+ "			<Train name=\"4\" order=\"5\" revenueCenters=\"4\" quantity=\"3\" price=\"360\"\n"
				+ "				onLast=\"6\" triggerPhase=\"2.3\" rust=\"2\" isPermanent=\"true\" />\n"
				+ "			<Train name=\"4+4\" order=\"6\" revenueCenters=\"4\" townRevenueCenters=\"4\"\n"
				+ "				quantity=\"1\" price=\"440\" onLast=\"7\" triggerPhase=\"2.4\" rust=\"2+2\" isPermanent=\"true\" />\n"
				+ "			<Train name=\"5\" order=\"7\" revenueCenters=\"5\" quantity=\"2\" price=\"500\"\n"
				+ "				onLast=\"8\" triggerPhase=\"3.1\" tileInfo=\"Brown\" isPermanent=\"true\" />\n"
				+ "			<Train name=\"5+5\" order=\"8\" revenueCenters=\"5\" townRevenueCenters=\"5\"\n"
				+ "				quantity=\"1\" price=\"600\" onLast=\"9\" triggerPhase=\"3.2\" isPermanent=\"true\" />\n"
				+ "			<Train name=\"6\" order=\"9\" revenueCenters=\"6\" quantity=\"2\" price=\"600\"\n"
				+ "				onLast=\"10\" triggerPhase=\"3.3\" rust=\"3\" isPermanent=\"true\" />\n"
				+ "			<Train name=\"6+6\" order=\"10\" revenueCenters=\"6\" townRevenueCenters=\"6\"\n"
				+ "				quantity=\"4\" price=\"720\" triggerPhase=\"3.4\" rust=\"3+3\" isPermanent=\"true\" />\n"
				+ "		</Trains>\n"
				+ "		<Files>\n"
				+ "			<File type=\"map\" name=\"1835/1835-Map.xml\" />\n"
				+ "			<File type=\"companies\" name=\"1835/1835-Companies.xml\" />\n"
				+ "			<File type=\"cities\" name=\"1835/1835-Cities.xml\" />\n"
				+ "			<File type=\"market\" name=\"1835/1835-Market.xml\" />\n"
				+ "			<File type=\"tileSet\" name=\"1835/1835-TileSet.xml\" />\n"
				+ "		</Files>\n"
				+ "	</GameInfo>\n";
		String t1853TestXML = "	<GameInfo id=\"3\" name=\"1853\" minPlayers=\"3\" maxPlayers=\"6\" status=\"INCOMPLETE\"\n"
				+ "		bankTotal=\"12500\" currencyFormat=\"£ ###,###\" designers=\"Frances Tresham (UK)\"\n"
				+ "		producers=\"Hartland Trefoil Ltd (UK)\" location=\"India\" \n"
				+ "		maxRounds=\"4\" optionalOR=\"true\" initialRoundName=\"Contract Bid Round\"\n"
				+ "		releaseDate=\"1989\"  bankPoolDividends=\"corporation\" ipoDividends=\"bank\"\n"
				+ "		privates=\"false\" minors=\"false\" shares=\"true\" bankPoolShareLimit=\"10\" playerShareLimit=\"6\">\n"
				+ "		<Capitalizations>\n"
				+ "			<Capitalization availableTrain=\"2\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"3\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"2M\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"4\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"3M\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"5\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"4M\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"6\" level=\"FULL\" />\n"
				+ "		</Capitalizations>\n"
				+ "		<RoundTypes>\n"
				+ "			<RoundType name=\"Contract Bid Round\" initialRound=\"true\" nextRound=\"Stock Round\" \n"
				+ "						endsAfterActions=\"Done Player Action\" />\n"
				+ "			<RoundType name=\"Stock Round\" nextRound=\"Operating Round\" endsAfterActions=\"Pass Action\" />\n"
				+ "			<RoundType name=\"Operating Round\" nextRound=\"Stock Round\" maxRounds=\"3\" \n"
				+ "						optionalExtra=\"true\" endsAfterActions=\"Done Action\" />\n"
				+ "		</RoundTypes>\n"
				+ "		<PlayerInfo>\n"
				+ "			<Player numPlayers=\"3\" startingCash=\"730\" certificateLimit=\"21\" />\n"
				+ "			<Player numPlayers=\"4\" startingCash=\"570\" certificateLimit=\"16\" />\n"
				+ "			<Player numPlayers=\"5\" startingCash=\"570\" certificateLimit=\"13\" />\n"
				+ "			<Player numPlayers=\"6\" startingCash=\"510\" certificateLimit=\"11\" />\n"
				+ "		</PlayerInfo>\n"
				+ "		<Phases>\n"
				+ "			<Phase name=\"1\" subName=\"1\" rounds=\"1\" tiles=\"Yellow\" majorTileLays=\"2\" trainLimit=\"4\" offBoard=\"Low\" />\n"
				+ "			<Phase name=\"2\" subName=\"1\" rounds=\"2\" tiles=\"Yellow,Green\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"4\" offBoard=\"Mid\" />\n"
				+ "			<Phase name=\"2\" subName=\"2\" rounds=\"2\" tiles=\"Yellow,Green\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"4\" offBoard=\"Mid\" />\n"
				+ "			<Phase name=\"3\" subName=\"1\" rounds=\"3\" tiles=\"Yellow,Green,Brown\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"3\" offBoard=\"High\" />\n"
				+ "			<Phase name=\"3\" subName=\"2\" rounds=\"3\" tiles=\"Yellow,Green,Brown\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"3\" offBoard=\"High\" />\n"
				+ "			<Phase name=\"4\" subName=\"1\" rounds=\"3\" tiles=\"Yellow,Green,Brown,Grey\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"2\" offBoard=\"High\" />\n"
				+ "			<Phase name=\"4\" subName=\"2\" rounds=\"3\" tiles=\"Yellow,Green,Brown,Grey\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"2\" offBoard=\"High\" />\n"
				+ "			<Phase name=\"4\" subName=\"3\" rounds=\"3\" tiles=\"Yellow,Green,Brown,Grey\" tileLaysAllowed=\"2\"\n"
				+ "				canBuyTrain=\"true\" trainLimit=\"2\" offBoard=\"High\" />\n"
				+ "		</Phases>\n"
				+ "		<Trains>\n"
				+ "			<Train name=\"2\" order=\"1\" revenueCenters=\"2\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"2\" quantity=\"6\" price=\"300\" />\n"
				+ "			<Train name=\"3\" order=\"2\" revenueCenters=\"3\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"3\" quantity=\"5\" price=\"440\" triggerPhase=\"2.1\" tileInfo=\"Green\" />\n"
				+ "			<Train name=\"2M\" order=\"3\" revenueCenters=\"2\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"4\" gauge=\"Meter\" quantity=\"2\" price=\"250\" triggerPhase=\"2.2\" />\n"
				+ "			<Train name=\"4\" order=\"4\" revenueCenters=\"4\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"5\" quantity=\"4\" price=\"620\" rust=\"2\" triggerPhase=\"3.1\" \n"
				+ "				tileInfo=\"Brown\" isPermanent=\"true\" />\n"
				+ "			<Train name=\"3M\" order=\"5\" revenueCenters=\"3\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"6\" gauge=\"Meter\" quantity=\"3\" price=\"430\" rust=\"2\" triggerPhase=\"3.2\" \n"
				+ "				isPermanent=\"true\" />\n"
				+ "			<Train name=\"5\" order=\"6\" revenueCenters=\"5\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"7\" quantity=\"3\" price=\"830\" rust=\"3,2M\" triggerPhase=\"4.1\" tileInfo=\"Grey\" \n"
				+ "				isPermanent=\"true\" />\n"
				+ "			<Train name=\"4M\" order=\"7\" revenueCenters=\"4\" TownRevenueCenters=\"unlimited\"\n"
				+ "				onLast=\"8\" gauge=\"Meter\" quantity=\"2\" price=\"590\" rust=\"3,2M\" triggerPhase=\"4.2\" \n"
				+ "				isPermanent=\"true\" />\n"
				+ "			<Train name=\"6\" order=\"8\" revenueCenters=\"6\" TownRevenueCenters=\"unlimited\"\n"
				+ "				quantity=\"2\" price=\"1050\" rust=\"3,2M\" triggerPhase=\"4.3\" isPermanent=\"true\" />\n"
				+ "		</Trains>\n"
				+ "		<Files>\n"
				+ "			<File type=\"map\" name=\"1853 XML Data/1853 Map.xml\" />\n"
				+ "			<File type=\"companies\" name=\"1853 XML Data/1853 Companies.xml\" />\n"
				+ "			<File type=\"cities\" name=\"1853 XML Data/1853 Cities.xml\" />\n"
				+ "			<File type=\"market\" name=\"1853 XML Data/1853 Market.xml\" />\n"
				+ "			<File type=\"tileSet\" name=\"1853 XML Data/1853 TileSet.xml\" />\n"
				+ "		</Files>\n"
				+ "	</GameInfo>\n";
		String t1856TestXML = "	<GameInfo id=\"4\" name=\"1856\" "
				+ "     Subtitle=\"Railroading in Upper Canada from 1856\" "
				+ "     minPlayers=\"3\" maxPlayers=\"6\"\n"
				+ "		status=\"IN PROGRESS\" bankTotal=\"12000\" "
				+ "     currencyFormat=\"$ ###,###\" designers=\"Bill Dixon (Canada)\"\n"
				+ "		location=\"Southern Ontario, Canada\" "
				+ "     producers=\"Mayfair Games (US)\" releaseDate=\"1995\"\n"
				+ "		firstTokenCost=\"40\" laterTokenCost=\"100\" "
				+ "     maxRounds=\"3\" initialRoundName=\"Stock Round\" \n"
				+ "		privates=\"true\" minors=\"false\" shares=\"true\" "
				+ "     bankPoolDividends=\"corporation\" ipoDividends=\"bank\"\n"
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
				+ "   <RoundTypes>\n"
				+ "     <RoundType name=\"Stock Round\" initialRound=\"true\" "
				+ "                interruptionRound=\"Auction Round\" nextRound=\"Operating Round\"/>\n"
				+ "     <RoundType name=\"Auction Round\" interruptsAfterActions=\"Done Player Action, Win Auction Action\" />\n"
				+ "     <RoundType name=\"Operating Round\" interruptionRound=\"Formation Round\" "
				+ "					nextRound=\"Stock Round\" maxRounds=\"3\"/>\n"
				+ "     <RoundType name=\"Formation Round\" interruptsAfterActions=\"Done Action\" />\n"
				+ "   </RoundTypes>\n"
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

		GameInfo tGameInfo;
		XMLNode tGameInfoNode;

		tGameInfo = GameInfo.NO_GAME_INFO;
		if (aGameInfoIndex == 1) {
			tGameInfoNode = utilitiesTestFactory.buildXMLNode (t1830TestXML);
		} else if (aGameInfoIndex == 2) {
			tGameInfoNode = utilitiesTestFactory.buildXMLNode (t1835TestXML);
		} else if (aGameInfoIndex == 3) {
			tGameInfoNode = utilitiesTestFactory.buildXMLNode (t1853TestXML);
		} else if (aGameInfoIndex == 4) {
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
	
	public PhaseManager buildPhaseManagerMock () {
		PhaseManager mPhaseManager;
		
		mPhaseManager  = Mockito.mock (PhaseManager.class);
		
		return mPhaseManager;
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
