package ge18xx.company;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.phase.PhaseInfo;
import ge18xx.utilities.GUI;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class CompanyTestFactory {
	public final TokenCompanyConcrete NO_TOKEN_COMPANY = null;
	public final TrainCompanyConcrete NO_TRAIN_COMPANY = null;
	public final int NO_COMPANY_INDEX = 99;
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	private GameManager mGameManager;
	private CorporationList mCorporationList;

	/**
	 * Builds the Company Test Factory by creating the gameTestFactory and get the Utilities Test Factory
	 *
	 */
	
	public CompanyTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mCorporationList = Mockito.mock (CorporationList.class);
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);
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
		mGameManager = gameTestFactory.buildGameManagerMock ();
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
		String tPrivateCompany1TestXML = "<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n" + "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n" + "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n" + "	</Private>\n";
		String tPrivateCompany2TestXML = "<Private id=\"805\" name=\"TEST-Camden &amp; Amboy\" abbrev=\"TEST-C&amp;A\"\n"
				+ "		cost=\"160\" revenue=\"25\" homeCell1=\"H18\" homeLocation1=\"15\" homeLocation2=\"45\" \n"
				+ "		note=\"The initial purchaser of the C&amp;A immediately receives a 10% share of PRR stock without further payment. This action does NOT close the C&amp;A. The PRR Corporation will not be running at this point, but the stock may be retained or sold subject to the ordinary rules of the game (see  8.1, last paragraph).\"\n"
				+ "		special=\"Free 10% TPRR\">\n"
				+ "		<Benefits>\n"
				+ "			<Benefit actorType=\"Player\" class=\"ge18xx.company.benefit.FreeCertificateBenefit\" corporationID=\"901\" certificatePercentage=\"10\" passive=\"true\"/>\n"
				+ "		</Benefits>\n"
				+ "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n"
				+ "	</Private>\n";
		PrivateCompany tPrivateCompany;
		
		tPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;

		if (tCompanyIndex == 1) {
			tPrivateCompany = buildPrivateCompany (tPrivateCompany1TestXML, tPrivateCompany, mCorporationList);
		} else if (tCompanyIndex == 2) {
			tPrivateCompany = buildPrivateCompany (tPrivateCompany2TestXML, tPrivateCompany, mCorporationList);
		}

		tPrivateCompany.fillCertificateInfo (mGameManager);
		
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

	public TrainCompany buildATrainCompany (int aCompanyIndex) {
		TrainCompanyConcrete tTrainCompany;
		String tTrainCompany1TestXML = "<Share id=\"991\" name=\"Test Train Pennsylvania\" abbrev=\"TTPRR\" homeCell1=\"H12\" \n"
				+ "	homeLocation1=\"14\" bgColor=\"Dark Green\" fgColor=\"White\" tokens=\"4\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
		String tTrainCompany2TestXML = "<Share id=\"992\" name=\"Test Train Baltimore and Ohio\" abbrev=\"TTBNO\" homeCell1=\"I15\" \n"
				+ "	homeLocation1=\"21\" bgColor=\"Deep Blue\" fgColor=\"White\" tokens=\"3\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
	
		CorporationList mCorporationList;
		GameManager mGameManager;
		PhaseInfo mPhaseInfo;
		CorporationFrame mCorporationFrame;
		
		tTrainCompany = NO_TRAIN_COMPANY;
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mPhaseInfo = gameTestFactory.buildPhaseInfoMock ();
		mCorporationList = buildCorporationListMock (mGameManager, mPhaseInfo);

		if (aCompanyIndex == 1) {
			tTrainCompany = buildTrainCompany (tTrainCompany1TestXML, tTrainCompany, mCorporationList);
		} else if (aCompanyIndex == 2) {
			tTrainCompany = buildTrainCompany (tTrainCompany2TestXML, tTrainCompany, mCorporationList);			
		}
		
		mCorporationFrame = buildCorporationFrameMock ();
		tTrainCompany.setCorporationFrame (mCorporationFrame);
		
		return tTrainCompany;
	}

	public GameManager getGameManagerMock () {
		return mGameManager;
	}
	
	public CorporationList getCorporationListMock () {
		return mCorporationList;
	}
	
	public TokenCompany buildATokenCompany (int aCompanyIndex) {
		TokenCompanyConcrete tTokenCompany;
		String tTokenCompany1TestXML = "<Share id=\"991\" name=\"Test Token Pennsylvania\" abbrev=\"TTPRR\" homeCell1=\"H12\" \n"
				+ "	homeLocation1=\"14\" bgColor=\"Dark Green\" fgColor=\"White\" tokens=\"4\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
		String tTokenCompany2TestXML = "<Share id=\"992\" name=\"Test Token Baltimore and Ohio\" abbrev=\"TTBNO\" homeCell1=\"I15\" \n"
				+ "	homeLocation1=\"21\" bgColor=\"Deep Blue\" fgColor=\"White\" tokens=\"3\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
	
		CorporationList mCorporationList;
		GameManager mGameManager;
		PhaseInfo mPhaseInfo;
		CorporationFrame mCorporationFrame;
		
		tTokenCompany = NO_TOKEN_COMPANY;
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mPhaseInfo = gameTestFactory.buildPhaseInfoMock ();
		mCorporationList = buildCorporationListMock (mGameManager, mPhaseInfo);

		if (aCompanyIndex == 1) {
			tTokenCompany = buildTokenCompany (tTokenCompany1TestXML, tTokenCompany, mCorporationList);
		} else if (aCompanyIndex == 2) {
			tTokenCompany = buildTokenCompany (tTokenCompany2TestXML, tTokenCompany, mCorporationList);			
		}
		
		mCorporationFrame = buildCorporationFrameMock ();
		tTokenCompany.setCorporationFrame (mCorporationFrame);
		
		return tTokenCompany;
	}

	private CorporationFrame buildCorporationFrameMock () {
		CorporationFrame mCorporationFrame;
		
		mCorporationFrame = Mockito.mock (CorporationFrame.class);
		
		return mCorporationFrame;
	}
	
	private CorporationList buildCorporationListMock (GameManager mGameManager, PhaseInfo mPhaseInfo) {
		CorporationList mCorporationList;
		JPanel mJPanel;
		
		mCorporationList = Mockito.mock (CorporationList.class);
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);
		Mockito.when (mCorporationList.getCurrentPhaseInfo ()).thenReturn (mPhaseInfo);
		Mockito.when (mCorporationList.getCurrentRoundOf ()).thenReturn ("2");
		mJPanel = Mockito.mock (JPanel.class);
		Mockito.when (mCorporationList.buildFullCorpsJPanel (null, NO_TOKEN_COMPANY, mGameManager, false, false, null)).thenReturn (mJPanel);
//		buildFullCorpsJPanel (this, corporation,
//				tGameManager, TrainPortfolio.FULL_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);	
		
//		buildFullCorpsJPanel (CorporationFrame aCorporationFrame, Corporation aBuyingCorporation,
//				GameManager aGameManager, boolean aFullTrainPortfolio, boolean aCanBuyTrain, String aDisableToolTipReason)
		
		return mCorporationList;
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
		String tShareCompany1TestXML = "<Share id=\"901\" name=\"Test Pennsylvania\" abbrev=\"TPRR\" homeCell1=\"H12\" \n"
				+ "	homeLocation1=\"14\" bgColor=\"Dark Green\" fgColor=\"White\" tokens=\"4\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
		String tShareCompany2TestXML = "<Share id=\"902\" name=\"Test Baltimore and Ohio\" abbrev=\"TBNO\" homeCell1=\"I15\" \n"
				+ "	homeLocation1=\"21\" bgColor=\"Deep Blue\" fgColor=\"White\" tokens=\"3\"> \n"
				+ "	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n"
				+ "	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n"
				+ "		allowedOwners=\"IPO,Player,BankPool\" /> \n" + "</Share>";
		String tShareCompany3TestXML = "	<Share id=\"1501\" name=\"Buffalo, Brantford &amp; Goderich Railway\"\n"
				+ "		abbrev=\"BBG\" homeCell1=\"J15\" homeLocation1=\"50\" destination=\"N17\"\n"
				+ "		destinationLocation=\"12\" bgColor=\"255,102,255\" fgColor=\"Black\" tokens=\"3\" tokenType=\"FixedCost\">\n"
				+ "		<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" />\n"
				+ "		<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\"\n"
				+ "			allowedOwners=\"IPO,Player,BankPool\" />\n"
				+ "	</Share>\n";

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
		} else if (aCompanyIndex == 3) {
			tShareCompany = buildShareCompany (tShareCompany3TestXML, tShareCompany, mCorporationList);
		} else {
			tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		}

		return tShareCompany;
	}

	private TrainCompanyConcrete buildTrainCompany (String aTrainCompanyTestXML, TrainCompanyConcrete aTrainCompany,
			CorporationList mCorporationList) {
		XMLNode tTrainCompanyNode;

		tTrainCompanyNode = utilitiesTestFactory.buildXMLNode (aTrainCompanyTestXML);
		if (tTrainCompanyNode != XMLNode.NO_NODE) {
			aTrainCompany = new TrainCompanyConcrete (tTrainCompanyNode, mCorporationList);
			aTrainCompany.setTestingFlag (true);
		}

		return aTrainCompany;
	}

	private TokenCompanyConcrete buildTokenCompany (String aTokenCompanyTestXML, TokenCompanyConcrete aTokenCompany,
			CorporationList mCorporationList) {
		XMLNode tTokenCompanyNode;

		tTokenCompanyNode = utilitiesTestFactory.buildXMLNode (aTokenCompanyTestXML);
		if (tTokenCompanyNode != XMLNode.NO_NODE) {
			aTokenCompany = new TokenCompanyConcrete (tTokenCompanyNode, mCorporationList);
			aTokenCompany.setTestingFlag (true);
		}

		return aTokenCompany;
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
	
	// Class to create Concrete Train Company rather than a specific extended class for Testing
	
	class TrainCompanyConcrete extends TrainCompany {
		
		public TrainCompanyConcrete (int aID, String aName) {
			super (aID, aName);
		}

		public TrainCompanyConcrete (XMLNode aChildNode, CorporationList aCorporationList) {
			super (aChildNode, aCorporationList);
		}

		@Override
		public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
			return GUI.NO_PANEL;
		}

		@Override
		public int calculateStartingTreasury () {
			return 0;
		}

		@Override
		protected boolean choiceForBaseToken () {
			return false;
		}
	}

	public TrainCompany buildTrainCompanyConcrete () {
		TrainCompany cTrainCompany;
		
		cTrainCompany = buildTrainCompanyConcrete (NO_COMPANY_INDEX, "TEST TRAIN COMPANY");
		
		return cTrainCompany;
	}

	public TrainCompany buildTrainCompanyConcrete (int aCompanyID, String aCompanyName) {
		TrainCompany cTrainCompany;
		
		cTrainCompany = new TrainCompanyConcrete (aCompanyID, aCompanyName);
		
		return cTrainCompany;
	}

	// Class to create Concrete Token Company rather than a specific extended class for Testing
	
	class TokenCompanyConcrete extends TokenCompany {
		
		public TokenCompanyConcrete (int aID, String aName) {
			super (aID, aName);
		}

		public TokenCompanyConcrete (XMLNode aChildNode, CorporationList aCorporationList) {
			super (aChildNode, aCorporationList);
		}

		@Override
		public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
			return null;
		}

		@Override
		public int calculateStartingTreasury () {
			return 0;
		}
	}

	public TokenCompany buildTokenCompanyConcrete () {
		TokenCompany cTokenCompany;
		
		cTokenCompany = buildTokenCompanyConcrete (NO_COMPANY_INDEX, "TEST TOKEN COMPANY");
		
		return cTokenCompany;
	}
	
	public TokenCompany buildTokenCompanyConcrete (int aCompanyID, String aCompanyName) {
		TokenCompany cTokenCompany;
		
		cTokenCompany = new TokenCompanyConcrete (aCompanyID, aCompanyName);
		
		return cTokenCompany;
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
	
	public Token buildToken (TokenCompany aTokenCompany, TokenInfo.TokenType aType) {
		Token tToken;
		
		tToken = new Token (aTokenCompany, aType);
		
		return tToken;
	}
	
	public Token buildToken () {
		TokenCompany mTokenCompany;
		int tMockCoID;
		Token tToken;

		tMockCoID = 5001;
		mTokenCompany = buildTokenCompanyMock (tMockCoID, "MC1");
		tToken = buildToken (mTokenCompany, TokenInfo.TokenType.MAP);

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
	
	public MapToken buildMapToken () {
		TokenCompany mTokenCompany;
		int tMockCoID;
		MapToken tMapToken;

		tMockCoID = 5001;
		mTokenCompany = buildTokenCompanyMock (tMockCoID, "MC1");
		tMapToken = buildMapToken (mTokenCompany);
		
		return tMapToken;
	}
	
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
