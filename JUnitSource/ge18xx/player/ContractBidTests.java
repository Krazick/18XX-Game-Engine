package ge18xx.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.JLabel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.center.CenterTestFactory;
import ge18xx.center.City;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import geUtilities.GUI;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

class ContractBidTests {
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	CenterTestFactory centerTestFactory;
	CompanyTestFactory companyTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	ShareCompany shareCompany;
	GameManager mGameManager;
	PlayerManager mPlayerManager;
	Bank bank;
	Player player;

	int playerCount;
	int certificateLimit;
	int minBidCities;
	int maxBidCities;
	String playerName;
	City city3;
	City city4;
	City city5;

	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
		
		// Game Info Index passed in of 3 means 1853 Game Info
		// And this 1853 Game Info will have ContractBid Round so it adds a Contract Bid for Player
		mGameManager = setupGameInfoAndManager (3);
		
		playerTestFactory = new PlayerTestFactory (mGameManager);
		BankTestFactory bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		bank.setFormat ("£ ###,###");

		centerTestFactory = new CenterTestFactory ();
		companyTestFactory = centerTestFactory.getCompanyTestFactory ();
		shareCompany = companyTestFactory.buildAShareCompany (3);

		playerCount = 4;
		certificateLimit = 16;
		minBidCities = 3;
		maxBidCities = 6;
		playerName = "BusterPlayer";
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (playerCount);
		player = playerTestFactory.buildPlayer (playerName, mPlayerManager, certificateLimit, 
				minBidCities, maxBidCities);
		city3 = (City) centerTestFactory.buildCity (3);		
		city4 = (City) centerTestFactory.buildCity (4);		
		city5 = (City) centerTestFactory.buildCity (5);		

	}
	
	protected GameManager setupGameInfoAndManager (int aGameInfoIndex) {
		GameInfo tGameInfo;
		GameManager tmGameManager;
		
		tGameInfo = gameTestFactory.buildGameInfo (aGameInfoIndex);
		tmGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (tmGameManager.getMaxRounds ()).thenReturn (1);
		Mockito.when (tmGameManager.getActiveGame ()).thenReturn (tGameInfo);
		Mockito.when (tmGameManager.gameHasRoundType ("Contract Bid Round")).thenReturn (true);
		
		return tmGameManager;
	}

	@Test
	@DisplayName ("Player Contract Bid Tests")
	void playerContractBidTests () {
		assertTrue (player.hasContractBid ());
		assertFalse (player.hasSignedContractBid ());
		assertFalse (player.hasFulfilledContractBid ());
			
		player.setHasSignedContractBid (true);
		assertTrue (player.hasSignedContractBid ());
		player.setHasSignedContractBid (false);
		assertFalse (player.hasSignedContractBid ());
		
		player.setHasFullfilledContractBid (true);
		assertFalse (player.hasFulfilledContractBid ());
		player.setHasSignedContractBid (true);

		player.setHasFullfilledContractBid (true);
		assertTrue (player.hasFulfilledContractBid ());
		player.setHasFullfilledContractBid (false);
		assertFalse (player.hasFulfilledContractBid ());
	}
	
	@Test
	@DisplayName ("Player Contract Bid Label Tests")
	void playerContractBidLabelTests () {
		JLabel tContractJLabel;
		ContractBid tContractBid;
		
		tContractBid = player.getContractBid ();
		assertNotNull (tContractBid);
		tContractJLabel = tContractBid.buildLabel ();
		assertEquals ("Contract Bid: Unsigned", tContractJLabel.getText ());
		
		player.setHasSignedContractBid (true);
		tContractJLabel = tContractBid.buildLabel ();
		assertEquals ("Contract Bid: Signed 0/£ 0", tContractJLabel.getText ());
		
		player.setHasFullfilledContractBid (true);
		tContractJLabel = tContractBid.buildLabel ();
		assertEquals ("Contract Bid: Fulfilled", tContractJLabel.getText ());
	}
	
	@Test
	@DisplayName ("Adding ContractLine Tests")
	void addContractLineTests () {
		City tCity2;
//		City tCity3;
		City tCity4;
		City tCity5;
		City tCity6;
		ContractLine tContractLine1;
		ContractLine tContractLine2;
		ContractLine tContractLine4;
		ContractBid tContractBid;
		int tBond;
		
		tContractBid = player.getContractBid ();
//		tCity3 = (City) centerTestFactory.buildCity (3);		
		tBond = city3.getCityInfoBond ();
		tContractLine1 = playerTestFactory.buildContractLine (city3, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine1);
		tContractBid.setExtraForBond (20);
		assertEquals (1, tContractBid.getCityCount ());
		assertEquals (70, tContractBid.getTotalValue ());
		
		tCity2 = (City) centerTestFactory.buildCity (8);
		tBond = tCity2.getCityInfoBond ();
		tContractLine2 = playerTestFactory.buildContractLine (tCity2, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine2);
		assertEquals (2, tContractBid.getCityCount ());
		assertEquals (100, tContractBid.getTotalValue ());
		
		tCity4 = (City) centerTestFactory.buildCity (4);
		tBond = tCity4.getCityInfoBond ();
		tContractLine4 = playerTestFactory.buildContractLine (tCity4, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine4);
		assertEquals (3, tContractBid.getCityCount ());
		assertEquals (140, tContractBid.getTotalValue ());
		
		tCity2 = (City) centerTestFactory.buildCity (4);
		tBond = tCity2.getCityInfoBond ();
		tContractLine2 = playerTestFactory.buildContractLine (tCity2, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine2);
		assertEquals (3, tContractBid.getCityCount ());
		assertEquals (140, tContractBid.getTotalValue ());
		
		assertEquals (20, tContractBid.getExtraForBond ());
		
		// Test to delete a Contract Line based on CityName
		
		tCity5 = City.NO_CITY;
		tContractBid.deleteContractLine (tCity5);
		assertEquals (3, tContractBid.getCityCount ());

		tCity6 = (City) centerTestFactory.buildCity (6);
		tContractBid.deleteContractLine (tCity6);
		assertEquals (3, tContractBid.getCityCount ());
		
		tContractBid.deleteContractLine (city3);
		assertEquals (2, tContractBid.getCityCount ());
		assertEquals (90, tContractBid.getTotalValue ());
	}
	
	@Test
	@DisplayName ("Player Contract Line Validation Tests")
	void playerContractLineValidationTests () {
		City tCity1;
//		City tCity3;
		City tCity4;
		City tCity5;
		ShareCompany tShareCompany1;
		int tBond1;
		int tBond3;
		int tBond4;
		int tBond5;
		ContractBid tContractBid1;
		ContractLine tContractLine1;
		ContractLine tContractLine3;
		ContractLine tContractLine4;
		ContractLine tContractLine5;
		
		tContractBid1 = player.getContractBid ();
		
		player.addCash (500);
		
		tCity1 = City.NO_CITY;
		tBond1 = 0;
		tShareCompany1 = ShareCompany.NO_SHARE_COMPANY;
		tContractLine1 = playerTestFactory.buildContractLine (tCity1, tShareCompany1, tBond1);
		tContractBid1.addContractLine (tContractLine1);
		
		assertFalse (tContractBid1.isValid ());
		assertEquals ("No City is specified\n"
				+ "No Share Company is specified\n"
				+ "Bond Value is <= zero (0)\n"
				+ "Not enough Cities (minimum is 3) are in the Contract Bid\n", tContractBid1.getAllReasonsInvalid ());
		tContractBid1.deleteContractLine (tCity1);
		assertEquals (0, tContractBid1.getCityCount ());

//		tCity3 = (City) centerTestFactory.buildCity (3);
		tBond3 = city3.getCityInfoBond ();
		tContractLine3 = playerTestFactory.buildContractLine (city3, shareCompany, tBond3);
		tContractBid1.addContractLine (tContractLine3);
		
		tCity4 = (City) centerTestFactory.buildCity (4);
		tBond4 = tCity4.getCityInfoBond ();
		tContractLine4 = playerTestFactory.buildContractLine (tCity4, shareCompany, tBond4);
		tContractBid1.addContractLine (tContractLine4);
		
		assertEquals ("Not enough Cities (minimum is 3) are in the Contract Bid\n", tContractBid1.getAllReasonsInvalid ());
		
		assertFalse (tContractBid1.isValid ());

		tCity5 = (City) centerTestFactory.buildCity (5);
		tBond5 = tCity5.getCityInfoBond ();
		tContractLine5 = playerTestFactory.buildContractLine (tCity5, shareCompany, tBond5);
		tContractBid1.addContractLine (tContractLine5);

		assertEquals (3, tContractBid1.getCityCount ());
		assertEquals (GUI.EMPTY_STRING, tContractBid1.getAllReasonsInvalid ());
		assertTrue (tContractBid1.isValid ());
	}
	
	@Test
	@DisplayName ("Player Contract Bid Validation Tests")
	void playerContractBidValidationTests () {
		City tCity2;
//		City tCity3;
		City tCity4;
		City tCity5;
		City tCity6;
		City tCity7;
		City tCity8;
		City tCity9;
		ContractLine tContractLine1;
		ContractLine tContractLine2;
		ContractLine tContractLine4;
		ContractLine tContractLine5;
		ContractLine tContractLine6;
		ContractLine tContractLine7;
		ContractLine tContractLine8;
		ContractLine tContractLine9;
		ContractLine tContractLine0;
		ContractBid tContractBid;
		int tBond;
		boolean tIsDeltaTerrain;
		
		tContractBid = player.getContractBid ();
		player.addCash (500);
		
		assertFalse (tContractBid.isValid ());
		
//		tCity3 = (City) centerTestFactory.buildCity (3);
		assertFalse (city3.isDeltaTerrain ());
		tBond = city3.getCityInfoBond ();
		tContractLine1 = playerTestFactory.buildContractLine (city3, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine1);
		
		assertFalse (tContractBid.isValid ());
		
		tIsDeltaTerrain = true;
		tCity2 = (City) centerTestFactory.buildCity (8, tIsDeltaTerrain);
		assertTrue (tCity2.isDeltaTerrain ());
		assertEquals (0, tContractBid.getDeltaCityCount ());
		
		tBond = tCity2.getCityInfoBond ();
		tContractLine2 = playerTestFactory.buildContractLine (tCity2, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine2);
		assertEquals (1, tContractBid.getDeltaCityCount ());

		assertFalse (tContractBid.isValid ());

		tCity4 = (City) centerTestFactory.buildCity (4, tIsDeltaTerrain);
		tBond = tCity4.getCityInfoBond ();
		tContractLine4 = playerTestFactory.buildContractLine (tCity4, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine4);
		assertEquals (2, tContractBid.getDeltaCityCount ());
		
		assertTrue (tContractBid.isValid ());

		tCity5 = (City) centerTestFactory.buildCity (5);
		tBond = tCity5.getCityInfoBond ();
		tContractLine5 = playerTestFactory.buildContractLine (tCity5, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine5);
		
		assertTrue (tContractBid.isValid ());

		tCity6 = (City) centerTestFactory.buildCity (6);
		tBond = tCity6.getCityInfoBond ();
		tContractLine6 = playerTestFactory.buildContractLine (tCity6, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine6);
		
		assertTrue (tContractBid.isValid ());
		
		tCity7 = (City) centerTestFactory.buildCity (7);
		tBond = tCity7.getCityInfoBond ();
		tContractLine7 = playerTestFactory.buildContractLine (tCity7, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine7);
		
		assertTrue (tContractBid.isValid ());
		
		tCity9 = (City) centerTestFactory.buildCity (9);
		tBond = tCity9.getCityInfoBond ();
		tContractLine9 = playerTestFactory.buildContractLine (tCity9, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine9);
		
		assertFalse (tContractBid.isValid ());
		assertEquals ("Too many Cities (maximum is 6) are in the Contract Bid\n", 
						tContractBid.getAllReasonsInvalid ());

		tContractBid.deleteContractLine (tCity7);
		assertTrue (tContractBid.isValid ());
		assertEquals (180, tContractBid.getTotalValue ());
		player.addCash (-330);
		assertEquals (170, player.getCash ());
		assertFalse (tContractBid.isValid ());

		tContractBid.deleteContractLine (tCity6);
		assertEquals (5, tContractBid.getCityCount ());
		assertEquals (2, tContractBid.getDeltaCityCount ());
		
		tCity8 = (City) centerTestFactory.buildCity (10, tIsDeltaTerrain);
		tBond = tCity8.getCityInfoBond ();
		tContractLine8 = playerTestFactory.buildContractLine (tCity8, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine8);
		
		assertEquals (6, tContractBid.getCityCount ());
		assertEquals (3, tContractBid.getDeltaCityCount ());
		assertFalse (tContractBid.isValid ());
		assertEquals ("Too many Cities in the Delta (maximum of 2) are in the Contract Bid\n"
				+ "Player does not have enough cash to post bond.", 
				tContractBid.getAllReasonsInvalid ());
		
		player.addCash (330);

		tContractBid.deleteContractLine (tCity5);
		assertEquals (5, tContractBid.getCityCount ());
		tContractLine0 = playerTestFactory.buildContractLine (tCity5, ShareCompany.NO_SHARE_COMPANY, 20);
		tContractBid.addContractLine (tContractLine0);
		assertFalse (tContractBid.isValid ());
		assertEquals ("No Share Company is specified\n"
				+ "Too many Cities in the Delta (maximum of 2) are in the Contract Bid\n", 
				tContractBid.getAllReasonsInvalid ());
	}
	
	@Test
	@DisplayName ("Verify ContractBid XML Tests")
	void verifyContractBidXMLTests () {
		XMLDocument tXMLDocument;
		XMLElement tContractBidXML;
		String tContractBidXMLText;
//		City tCity4;
//		City tCity5;
		ContractLine tContractLine1;
		ContractLine tContractLine4;
		ContractLine tContractLine5;
		ContractLine tContractLine;
		ContractBid tContractBid;
		int tBond;
		int tContractLineCount;
		boolean tIsDeltaTerrain;

		tXMLDocument = new XMLDocument ();

		tContractBid = player.getContractBid ();
		tContractBidXML = tContractBid.getElements (tXMLDocument);
		tContractBidXMLText = tContractBidXML.toXMLString ();
		assertEquals ("<ContractBid extraForBond=\"0\" fullfilled=\"false\" signed=\"false\"/>\n", tContractBidXMLText);
		
		tBond = city3.getCityInfoBond ();
		tContractLine1 = playerTestFactory.buildContractLine (city3, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine1);
		tContractBid.setExtraForBond (20);
		tContractBidXML = tContractBid.getElements (tXMLDocument);
		tContractBidXMLText = tContractBidXML.toXMLString ();
		assertEquals ("<ContractBid extraForBond=\"20\" fullfilled=\"false\" signed=\"false\">\n"
				+ "<ContractLines>\n"
				+ "<ContractLine bond=\"50\" cityName=\"Calcutta\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"c8d4bde7c07bb1fca59000a26ef6caf14e175dbf31eef24294819d9ee7d948b8\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "</ContractLines>\n"
				+ "<ChecksumXMLElement checksum=\"040a2d46060e464d2426515b9db3588ce6c83a8d35fedf2cce2b02b742ab7244\" label=\"\" nodeName=\"ContractLines\"/>\n"
				+ "</ContractBid>\n", tContractBidXMLText);

		tIsDeltaTerrain = true;
		city4 = (City) centerTestFactory.buildCity (4, tIsDeltaTerrain);
		tBond = city4.getCityInfoBond ();
		tContractLine4 = playerTestFactory.buildContractLine (city4, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine4);
		assertEquals (1, tContractBid.getDeltaCityCount ());
		
		tContractBidXML = tContractBid.getElements (tXMLDocument);
		tContractBidXMLText = tContractBidXML.toXMLString ();

		assertEquals ("<ContractBid extraForBond=\"20\" fullfilled=\"false\" signed=\"false\">\n"
				+ "<ContractLines>\n"
				+ "<ContractLine bond=\"50\" cityName=\"Calcutta\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"c8d4bde7c07bb1fca59000a26ef6caf14e175dbf31eef24294819d9ee7d948b8\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "<ContractLine bond=\"40\" cityName=\"Delhi\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"6d39f629c4436441458dd9f171dee843908e432efc95a0beebe73f7acf37bd95\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "</ContractLines>\n"
				+ "<ChecksumXMLElement checksum=\"1e644bc80ebaefe7da086945803451a48cf90008992fbe16c8d8f97e240cef72\" label=\"\" nodeName=\"ContractLines\"/>\n"
				+ "</ContractBid>\n", tContractBidXMLText);

//		tCity5 = (City) centerTestFactory.buildCity (5);
		tBond = city5.getCityInfoBond ();
		tContractLine5 = playerTestFactory.buildContractLine (city5, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine5);
		
		tContractBidXML = tContractBid.getElements (tXMLDocument);
		tContractBidXMLText = tContractBidXML.toXMLString ();

		assertEquals ("<ContractBid extraForBond=\"20\" fullfilled=\"false\" signed=\"false\">\n"
				+ "<ContractLines>\n"
				+ "<ContractLine bond=\"50\" cityName=\"Calcutta\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"c8d4bde7c07bb1fca59000a26ef6caf14e175dbf31eef24294819d9ee7d948b8\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "<ContractLine bond=\"40\" cityName=\"Delhi\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"6d39f629c4436441458dd9f171dee843908e432efc95a0beebe73f7acf37bd95\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "<ContractLine bond=\"20\" cityName=\"Peshawar\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"f2176658c4cd58f058ebb0c388f74494c29f660c6f276545367d551ea458f509\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "</ContractLines>\n"
				+ "<ChecksumXMLElement checksum=\"e7c5697eec3fe13934dbb018fce9b66b64d4eb4b92d5bdffa67930b3e63df7ef\" label=\"\" nodeName=\"ContractLines\"/>\n"
				+ "</ContractBid>\n", tContractBidXMLText);
		
		tContractLineCount = tContractBid.getCityCount ();
		assertEquals (3, tContractLineCount);
		for (int tIndex = 0;  tIndex < tContractLineCount; tIndex++) {
			tContractLine = tContractBid.getContractLineAt (tIndex);
			if (tIndex == 0) {
				assertEquals ("Calcutta", tContractLine.getCityName ());
			} else if (tIndex == 1) {
				assertEquals ("Delhi", tContractLine.getCityName ());
			} else if (tIndex == 2) {
				assertEquals ("Peshawar", tContractLine.getCityName ());
			}
		}
	}
	
	@Test
	@DisplayName ("Parse  ContractBid XML Tests")
	void parseContractBidXMLTests () {
		String tContractBidText = "<ContractBid extraForBond=\"20\" fullfilled=\"false\" signed=\"false\">\n"
				+ "<ContractLines>\n"
				+ "<ContractLine bond=\"50\" cityName=\"Calcutta\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"c8d4bde7c07bb1fca59000a26ef6caf14e175dbf31eef24294819d9ee7d948b8\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "<ContractLine bond=\"40\" cityName=\"Delhi\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"6d39f629c4436441458dd9f171dee843908e432efc95a0beebe73f7acf37bd95\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "<ContractLine bond=\"20\" cityName=\"Peshawar\" connected=\"false\" shareCompanyID=\"1501\"/>\n"
				+ "<ChecksumXMLElement checksum=\"f2176658c4cd58f058ebb0c388f74494c29f660c6f276545367d551ea458f509\" label=\"\" nodeName=\"ContractLine\"/>\n"
				+ "</ContractLines>\n"
				+ "<ChecksumXMLElement checksum=\"e7c5697eec3fe13934dbb018fce9b66b64d4eb4b92d5bdffa67930b3e63df7ef\" label=\"\" nodeName=\"ContractLines\"/>\n"
				+ "</ContractBid>\n";
		ContractBid tContractBid;
		XMLNode tContractBidNode;
		ContractLine tContractLine;

		Mockito.when (mGameManager.getCityWithName ("Calcutta")).thenReturn (city3);
		Mockito.when (mGameManager.getCityWithName ("Delhi")).thenReturn (city4);
		Mockito.when (mGameManager.getCityWithName ("Peshawar")).thenReturn (city5);

		tContractBidNode = utilitiesTestFactory.buildXMLNode (tContractBidText);
		tContractBid = player.getContractBid ();
		tContractBid.loadXMLNode (tContractBidNode);
		assertEquals (3, tContractBid.getCityCount ());
		tContractLine = tContractBid.getContractLineAt (0);
		System.out.println ("Contract Line 0, Name is " + tContractLine.getCityName ());
		tContractLine = tContractBid.getContractLineAt (1);
		System.out.println ("Contract Line 1, Name is " + tContractLine.getCityName ());
		tContractLine = tContractBid.getContractLineAt (2);
		System.out.println ("Contract Line 2, Name is " + tContractLine.getCityName ());
	}
}
