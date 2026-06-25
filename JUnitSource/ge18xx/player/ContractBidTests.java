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

class ContractBidTests {
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	CenterTestFactory centerTestFactory;
	CompanyTestFactory companyTestFactory;
	ShareCompany shareCompany;
	GameManager mGameManager;
	PlayerManager mPlayerManager;
	Bank bank;
	Player player;

	int playerCount;
	int certificateLimit;
	String playerName;

	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();

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
		playerName = "BusterPlayer";
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (playerCount);
		player = playerTestFactory.buildPlayer (playerName, mPlayerManager, certificateLimit);
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
		City tCity3;
		City tCity4;
		City tCity5;
		City tCity6;
		ContractLine tContractLine1;
		ContractLine tContractLine2;
		ContractLine tContractLine4;
		ContractBid tContractBid;
		int tBond;
		
		tContractBid = player.getContractBid ();
		tCity3 = (City) centerTestFactory.buildCity (3);
		tBond = tCity3.getCityInfoBond ();
		tContractLine1 = playerTestFactory.buildContractLine (tCity3, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine1);
		tContractBid.setExtraForBond (20);
		assertEquals (1, tContractBid.getCount ());
		assertEquals (70, tContractBid.getTotalValue ());
		
		tCity2 = (City) centerTestFactory.buildCity (8);
		tBond = tCity2.getCityInfoBond ();
		tContractLine2 = playerTestFactory.buildContractLine (tCity2, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine2);
		assertEquals (2, tContractBid.getCount ());
		assertEquals (100, tContractBid.getTotalValue ());
		
		tCity4 = (City) centerTestFactory.buildCity (4);
		tBond = tCity4.getCityInfoBond ();
		tContractLine4 = playerTestFactory.buildContractLine (tCity4, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine4);
		assertEquals (3, tContractBid.getCount ());
		assertEquals (140, tContractBid.getTotalValue ());
		
		tCity2 = (City) centerTestFactory.buildCity (4);
		tBond = tCity2.getCityInfoBond ();
		tContractLine2 = playerTestFactory.buildContractLine (tCity2, shareCompany, tBond);
		tContractBid.addContractLine (tContractLine2);
		assertEquals (3, tContractBid.getCount ());
		assertEquals (140, tContractBid.getTotalValue ());
		
		assertEquals (20, tContractBid.getExtraForBond ());
		
		// Test to delete a Contract Line based on CityName
		
		tCity5 = City.NO_CITY;
		tContractBid.deleteContractLine (tCity5);
		assertEquals (3, tContractBid.getCount ());

		tCity6 = (City) centerTestFactory.buildCity (6);
		tContractBid.deleteContractLine (tCity6);
		assertEquals (3, tContractBid.getCount ());
		
		tContractBid.deleteContractLine (tCity3);
		assertEquals (2, tContractBid.getCount ());
		assertEquals (90, tContractBid.getTotalValue ());
	}
}
