package ge18xx.company;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class CompanyTestFactory {
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;

	/**
	 * Builds the Company Test Factory by creating the gameTestFactory and get the Utilities Test Factory
	 *
	 */
	
	public CompanyTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}

	/**
	 * Builds the Company Test Factory using the provided GameTest Factory, and getting the Utilities Test Factory
	 *
	 * @param aGameTestFactory A Game Test Factory to be attacheck to this Company Test Factory
	 *
	 */
	
	public CompanyTestFactory (GameTestFactory aGameTestFactory) {
		gameTestFactory = aGameTestFactory;
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}

	/**
	 * Retrieves the Utilities Test Factory attached to this Company Test Factory
	 *
	 * @return the Utilities Test Factory attached to this Company Test Factory;
	 *
	 */
	
	public UtilitiesTestFactory getUtilitiesTestFactory () {
		return utilitiesTestFactory;
	}

	/**
	 * Build a Private Company from XML Data for Testing Purposes. The
	 * CorporationList attached to Private Company will be Mocked, and the
	 * GameManager attached to the Corporation List will be mocked, and returned
	 * when requesting to 'getGameManager'
	 *
	 * @param aCompanyIndex Use 1 for TestC&SL any other will return
	 *                      NO_PRIVATE_COMPANY
	 * @return a PrivateCompany for the one requested
	 */
	
	public PrivateCompany buildAPrivateCompany (int tCompanyIndex) {
		String tPrivateCompany1TestXML = "	<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n" + "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n" + "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n" + "	</Private>\n" + "";
		PrivateCompany tPrivateCompany;
		CorporationList mCorporationList;
		GameManager mGameManager;
		
		tPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;
		mCorporationList = Mockito.mock (CorporationList.class);
		mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (tCompanyIndex == 1) {
			tPrivateCompany = buildPrivateCompany (tPrivateCompany1TestXML, tPrivateCompany, mCorporationList);
		}

		return tPrivateCompany;
	}

	private PrivateCompany buildPrivateCompany (String aPrivateCompanyTextXML, PrivateCompany aPrivateCompany,
			CorporationList mCorporationList) {
		XMLNode tPrivateCompanyNode;

		tPrivateCompanyNode = utilitiesTestFactory.buildXMLNode (aPrivateCompanyTextXML);
		if (tPrivateCompanyNode != XMLNode.NO_NODE) {
			aPrivateCompany = new PrivateCompany (tPrivateCompanyNode, mCorporationList);
			aPrivateCompany.setTestingFlag (true);
		}

		return aPrivateCompany;
	}
	
	public PrivateCompany buildPrivateCompanyMock (String aClientName) {
		PrivateCompany mPrivateCompany;

		mPrivateCompany = Mockito.mock (PrivateCompany.class);
		Mockito.when (mPrivateCompany.getAbbrev ()).thenReturn ("MPC");

		return mPrivateCompany;
	}

	/**
	 * Build a Share Company from XML Data for Testing Purposes. The CorporationList
	 * attached to Share Company will be Mocked, and the GameManager attached to the
	 * Corporation List will be mocked, and returned when requesting to
	 * 'getGameManager'
	 *
	 * @param aCompanyIndex Use 1 for TestPennsylvania and 2 for Test BnO, any other
	 *                      will return NO_SHARE_COMPANY
	 * @return a ShareCompany for the one requested
	 */
	
	public ShareCompany buildAShareCompany (int aCompanyIndex) {
		String tShareCompany1TestXML = "<Share id=\"901\" name=\"TestPennsylvania\" abbrev=\"TPRR\" homeCell1=\"H12\" \n"
				+ "	homeLocation1=\"14\" bgColor=\"Dark Green\" fgColor=\"White\" tokens=\"4\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
		String tShareCompany2TestXML = "<Share id=\"902\" name=\"Test Baltimore and Ohio\" abbrev=\"TBNO\" homeCell1=\"I15\" \n"
				+ "	homeLocation1=\"21\" bgColor=\"Deep Blue\" fgColor=\"White\" tokens=\"3\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";

		ShareCompany tShareCompany;
		CorporationList mCorporationList;
		GameManager mGameManager;
		
		tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		mCorporationList = Mockito.mock (CorporationList.class);
		mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (aCompanyIndex == 1) {
			tShareCompany = buildShareCompany (tShareCompany1TestXML, tShareCompany, mCorporationList);
		} else if (aCompanyIndex == 2) {
			tShareCompany = buildShareCompany (tShareCompany2TestXML, tShareCompany, mCorporationList);
		} else {
			tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		}

		return tShareCompany;
	}

	private ShareCompany buildShareCompany (String aShareCompanyTestXML, ShareCompany aShareCompany,
			CorporationList mCorporationList) {
		XMLNode tShareCompanyNode;

		tShareCompanyNode = utilitiesTestFactory.buildXMLNode (aShareCompanyTestXML);
		if (tShareCompanyNode != XMLNode.NO_NODE) {
			aShareCompany = new ShareCompany (tShareCompanyNode, mCorporationList);
			aShareCompany.setTestingFlag (true);
		}

		return aShareCompany;
	}
	
	public ShareCompany buildShareCompanyMock () {
		return buildShareCompanyMock ("Test Client Share");
	}

	public ShareCompany buildShareCompanyMock (String aClientName) {
		ShareCompany mShareCompany;

		mShareCompany = Mockito.mock (ShareCompany.class);
		Mockito.when (mShareCompany.getAbbrev ()).thenReturn ("MSC");

		return mShareCompany;
	}

	/**
	 * Build a Minor Company from XML Data for Testing Purposes. The CorporationList
	 * attached to Minor Company will be Mocked, and the GameManager attached to the
	 * Corporation List will be mocked, and returned when requesting to
	 * 'getGameManager'
	 *
	 * @param aCompanyIndex Use 1 for TestBergisch-Markische Bahn and 2 for
	 *                      TestBerline-Potsdamer Bahn, any other will return
	 *                      NO_MINOR_COMPANY
	 * @return a MinorCompany for the one requested
	 */
	
	public MinorCompany buildAMinorCompany (int aCompanyIndex) {
		String tMinorCompany1TestXML = "<Minor id=\"1701\" name=\"TestBergisch-Markische Bahn\" abbrev=\"1\" cost=\"80\"\n"
				+ "		homeCell1=\"H2\" homeLocation1=\"15\" upgradeID=\"1810\" upgradePercentage=\"5\"\n"
				+ "		bgColor=\"Black\" fgColor=\"White\" tokens=\"1\">\n"
				+ "		<Certificate director=\"YES\" percentage=\"50\" allowedOwners=\"IPO,Player\" />\n"
				+ "		<Certificate director=\"NO\" percentage=\"50\" allowedOwners=\"Minor\" />\n" + "	</Minor>";
		String tMinorCompany2TestXML = "<Minor id=\"1702\" name=\"TestBerline-Potsdamer Bahn\" abbrev=\"2\" cost=\"170\"\n"
				+ "		homeCell1=\"E19\" homeLocation1=\"6\" upgradeID=\"1810\" upgradePercentage=\"10\"\n"
				+ "		bgColor=\"Black\" fgColor=\"White\" tokens=\"1\">\n"
				+ "		<Certificate director=\"YES\" percentage=\"50\" allowedOwners=\"IPO,Player\" />\n"
				+ "		<Certificate director=\"NO\" percentage=\"50\" allowedOwners=\"Minor\" />\n" + "	</Minor>";

		MinorCompany tMinorCompany;
		CorporationList mCorporationList;
		GameManager mGameManager;
		
		tMinorCompany = MinorCompany.NO_MINOR_COMPANY;
		mCorporationList = Mockito.mock (CorporationList.class);
		mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (aCompanyIndex == 1) {
			tMinorCompany = buildMinorCompany (tMinorCompany1TestXML, tMinorCompany, mCorporationList);
		} else if (aCompanyIndex == 2) {
			tMinorCompany = buildMinorCompany (tMinorCompany2TestXML, tMinorCompany, mCorporationList);
		} else {
			tMinorCompany = MinorCompany.NO_MINOR_COMPANY;
		}

		return tMinorCompany;
	}

	private MinorCompany buildMinorCompany (String aMinorCompanyTestXML, MinorCompany aMinorCompany,
			CorporationList mCorporationList) {
		XMLNode tMinorCompanyNode;

		tMinorCompanyNode = utilitiesTestFactory.buildXMLNode (aMinorCompanyTestXML);
		if (tMinorCompanyNode != XMLNode.NO_NODE) {
			aMinorCompany = new MinorCompany (tMinorCompanyNode, mCorporationList);
			aMinorCompany.setTestingFlag (true);
		}

		return aMinorCompany;
	}
	
	public MinorCompany buildMinorCompanyMock (String aClientName) {
		MinorCompany mMinorCompany;

		mMinorCompany = Mockito.mock (MinorCompany.class);
		Mockito.when (mMinorCompany.getAbbrev ()).thenReturn ("MMC");

		return mMinorCompany;
	}

	// Build Mocked Token Company Methods
	
	public TokenCompany buildTokenCompanyMock () {
		TokenCompany mTokenCompany;
		
		mTokenCompany = Mockito.mock (TokenCompany.class);
		
		return mTokenCompany;
	}
	
	public TokenCompany buildTokenCompanyMock (int aCompanyID) {
		TokenCompany mTokenCompany;
		
		mTokenCompany = buildTokenCompanyMock ();
		Mockito.when (mTokenCompany.getID ()).thenReturn (aCompanyID);
		
		return mTokenCompany;
	}
	
	public TokenCompany buildTokenCompanyMock (int aCompanyID, String aCompanyAbbrev) {
		TokenCompany mTokenCompany;
		
		mTokenCompany = buildTokenCompanyMock (aCompanyID);
		Mockito.when (mTokenCompany.getAbbrev ()).thenReturn (aCompanyAbbrev);
		
		return mTokenCompany;
	}

	// Build Token Methods
	
	public Token buildToken (TokenCompany aTokenCompany) {
		Token tToken;
		
		tToken = new Token (aTokenCompany);
		
		return tToken;
	}
	
	public Token buildToken () {
		TokenCompany mTokenCompany;
		int tMockCoID;
		Token tToken;

		tMockCoID = 5001;
		mTokenCompany = buildTokenCompanyMock (tMockCoID, "MC1");
		tToken = buildToken (mTokenCompany);

		return tToken;
	}
	
	public Token buildTokenMock () {
		Token mToken;
		
		mToken = Mockito.mock (Token.class);
		Mockito.when (mToken.getCorporationAbbrev ()).thenReturn ("MTC_MT");
		Mockito.when (mToken.isAMapToken ()).thenReturn (false);
		
		return mToken;
	}
	
	// Build Map Token Methods
	
	public MapToken buildMapToken (TokenCompany aTokenCompany) {
		MapToken tMapToken;
		
		tMapToken = new MapToken ();
		tMapToken.setCompany (aTokenCompany);
		tMapToken.setCost (40);
		
		return tMapToken;
	}

	public MapToken buildMapTokenMock () {
		MapToken mMapToken;
		
		mMapToken = Mockito.mock (MapToken.class);
		Mockito.when (mMapToken.getCorporationAbbrev ()).thenReturn ("MTC_MT");
		Mockito.when (mMapToken.isAMapToken ()).thenReturn (true);

		return mMapToken;
	}
}
