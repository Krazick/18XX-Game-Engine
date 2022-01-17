package ge18xx.game;

import org.mockito.Mockito;

import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.benefit.MapBenefit;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLNode;

public class GameTestFactory {
	XMLDocument theXMLDocument;

	public GameTestFactory () {
		theXMLDocument = new XMLDocument ();
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
	
	public GameManager buildGameManager () {
		return buildGameManager ("GTF Client");
	}
	
	public GameManager buildGameManager (String aClientName) {
		GameManager tGameManager;
		Game_18XX tGame_18XX;
		
		tGame_18XX = new Game_18XX (false);
		tGame_18XX.setupLogger (aClientName);
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
		tGameInfoNode = constructXMLNode (t1830TestXML);
		if (tGameInfoNode != XMLNode.NO_NODE) {
			tGameInfo = new GameInfo (tGameInfoNode);
			tGameInfo.setTestingFlag (true);			
		}
		
		return tGameInfo;
	}
	
	public PrivateCompany buildAPrivateCompany (int tCompanyIndex) {
		String tPrivateCompany1TestXML =
				"	<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n"
				+ "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n"
				+ "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n"
				+ "	</Private>\n"
				+ "";
		PrivateCompany tPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;
		CorporationList mCorporationList = Mockito.mock (CorporationList.class);
		GameManager mGameManager = Mockito.mock (GameManager.class);
		Mockito.when (mGameManager.getClientUserName ()).thenReturn ("MockedUserName");
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (tCompanyIndex == 1) {
			tPrivateCompany = constructPrivateCompany (tPrivateCompany1TestXML, tPrivateCompany, mCorporationList);
		}
		
		return tPrivateCompany;
	}
	
	public ShareCompany buildAShareCompany (int tCompanyIndex) {
		String tShareCompany1TestXML =
				"<Share id=\"901\" name=\"TestPennsylvania\" abbrev=\"TPRR\" homeCell1=\"H12\" \n" +
				"	homeLocation1=\"14\" bgColor=\"Dark Green\" fgColor=\"White\" tokens=\"4\"> \n" +
				"	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n" +
				"	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n" +
				"		allowedOwners=\"IPO,Player,BankPool\" /> \n" +
				"</Share>";
		String tShareCompany2TestXML =
				"<Share id=\"902\" name=\"Test Baltimore and Ohio\" abbrev=\"TBNO\" homeCell1=\"I15\" \n" +
				"	homeLocation1=\"21\" bgColor=\"Deep Blue\" fgColor=\"White\" tokens=\"3\"> \n" +
				"	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n" +
				"	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n" +
				"		allowedOwners=\"IPO,Player,BankPool\" /> \n" +
				"</Share>";

		ShareCompany tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		CorporationList mCorporationList = Mockito.mock (CorporationList.class);
		GameManager mGameManager = Mockito.mock (GameManager.class);
		Mockito.when (mGameManager.getClientUserName ()).thenReturn ("MockedUserName");
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (tCompanyIndex == 1) {
			tShareCompany = constructShareCompany (tShareCompany1TestXML, tShareCompany, mCorporationList);
		} else if (tCompanyIndex == 2) {
			tShareCompany = constructShareCompany (tShareCompany2TestXML, tShareCompany, mCorporationList);
		} else {
			tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		}
		
		return tShareCompany;
	}

	public XMLNode constructXMLNode (String aXMLText) {
		XMLNode tXMLNode;
		
		theXMLDocument = theXMLDocument.ParseXMLString (aXMLText);
		
		if (theXMLDocument.ValidDocument ()) {
			tXMLNode = theXMLDocument.getDocumentElement ();
		} else {
			tXMLNode = null;
		}
		
		return tXMLNode;
	}
	
	private PrivateCompany constructPrivateCompany (String aPrivateCompanyTextXML, PrivateCompany aPrivateCompany, 
			CorporationList mCorporationList) {
		XMLNode tPrivateCompanyNode;
		
		tPrivateCompanyNode = constructXMLNode (aPrivateCompanyTextXML);
		if (tPrivateCompanyNode != XMLNode.NO_NODE) {
			aPrivateCompany = new PrivateCompany (tPrivateCompanyNode, mCorporationList);
			aPrivateCompany.setTestingFlag (true);			
		}
		
		return aPrivateCompany;
	}
	
	private ShareCompany constructShareCompany (String aShareCompanyTestXML, ShareCompany aShareCompany,
			CorporationList mCorporationList) {
		XMLNode tShareCompanyNode;
		
		tShareCompanyNode = constructXMLNode (aShareCompanyTestXML);
		if (tShareCompanyNode != XMLNode.NO_NODE) {
			aShareCompany = new ShareCompany (tShareCompanyNode, mCorporationList);
			aShareCompany.setTestingFlag (true);			
		}
		
		return aShareCompany;
	}

	public MapBenefit buildMapBenefit (PrivateCompany aPrivateCompany) {
		MapBenefit tMapBenefit;
		String tXMLBenefitTest = "<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>";
		XMLNode tBenefitXMLNode;
		
		tBenefitXMLNode = constructXMLNode (tXMLBenefitTest);

		tMapBenefit = new MapBenefit (tBenefitXMLNode);
		tMapBenefit.setPrivateCompany (aPrivateCompany);
		
		return tMapBenefit;
	}
}
