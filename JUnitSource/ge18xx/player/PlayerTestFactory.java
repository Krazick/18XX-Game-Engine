package ge18xx.player;

import org.mockito.Mockito;

import ge18xx.center.City;
import ge18xx.company.Certificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.toplevel.ContractBidFrame;

public class PlayerTestFactory {
	GameManager gameManager;

	/**
	 * Basic Constructor that saves the provided Game Manager
	 *
	 * @param aGameManager The GameManager to save in this Test Factory
	 *
	 */
	public PlayerTestFactory (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	/**
	 * Build a basic Player Manager, given the stored Game Manager
	 *
	 * @return a PlayerManager
	 *
	 */
	public PlayerManager buildPlayerManager () {
		PlayerManager tPlayerManager;

		tPlayerManager = new PlayerManager (gameManager);

		return tPlayerManager;
	}

	/**
	 * Guild a basic PlayerManager Mock that will mock the getPlayerCount and return 3
	 *
	 * @return the Mocked PlayerManager
	 */
	public PlayerManager buildPlayerManagerMock (int aPlayerCount) {
		PlayerManager mPlayerManager = Mockito.mock (PlayerManager.class);

		Mockito.when (mPlayerManager.getPlayerCount ()).thenReturn (aPlayerCount);
		Mockito.when (mPlayerManager.getGameManager ()).thenReturn (gameManager);

		return mPlayerManager;
	}

	/**
	 * Build a basic Player Mock with the Provided Name mocked when 'getName' method called
	 *
	 * @param aName Name to return when 'getName' method is called by mocked object
	 *
	 * @return the constructed Player Mock
	 *
	 */
	public Player buildPlayerMock (String aName) {
		Player mPlayer;

		mPlayer = Mockito.mock (Player.class);
		Mockito.when (mPlayer.getName ()).thenReturn (aName);
		Mockito.when (mPlayer.isAPlayer ()).thenReturn (true);

		return mPlayer;
	}
	
	/**
	 * Build a basic Player with the Provided Name, PlayerManager and Cash.
	 * The player will be added to the PlayerManager.
	 *
	 * @param aName Name to return when 'getName' method is called by mocked object
	 * @param aPlayerManager the Player Manager to add this player to
	 * @param aCertificateLimit the certificateLimit for the player
	 *
	 * @return the constructed Player
	 *
	 */
	public Player buildPlayer (String aName, PlayerManager aPlayerManager, int aCertificateLimit,
			int aMinBidCities, int aMaxBidCities) {
		Player tPlayer;
		
		tPlayer = new Player (aName, aPlayerManager, aCertificateLimit, aMinBidCities, aMaxBidCities);
		aPlayerManager.addPlayer (tPlayer);
		
		return tPlayer;
	}
	
	public Escrow buildEscrow (Certificate aCertificate, int aEscrowValue) {
		Escrow tEscrow;
		
		tEscrow = new Escrow (aCertificate, aEscrowValue);
		
		return tEscrow;
	}
	
	public ContractBidFrame buildContractBidFrameMock (ContractBid aContractBid) {
		ContractBidFrame mContractBidFrame;
		Player tPlayer;
		
		tPlayer = aContractBid.getPlayer ();
		mContractBidFrame = Mockito.mock (ContractBidFrame.class);
		Mockito.doNothing ().when (mContractBidFrame).setContractBid (aContractBid);
		Mockito.doNothing ().when (mContractBidFrame).fillContractBidJPanel (tPlayer);
		
		return mContractBidFrame;
	}
	
	public ContractBid buildContractBidMock () {
		ContractBid mContractBid;
		
		mContractBid = Mockito.mock (ContractBid.class);
		
		return mContractBid;
	}

	public Escrow buildEscrowMock (Certificate aCertificate, int aEscrowValue) {
		Escrow mEscrow;
		
		mEscrow = Mockito.mock (Escrow.class);
		Mockito.when (mEscrow.getCertificate ()).thenReturn (aCertificate);
		Mockito.when (mEscrow.getCash ()).thenReturn (aEscrowValue);

		return mEscrow;
	}
	
	public ContractBid buildContractBid (Player aPlayer) {
		ContractBid tContractBid;
		
		tContractBid = new ContractBid (aPlayer);
		
		return tContractBid;
	}

	public ContractLine buildContractLine (City aCity, ShareCompany aShareCompany, int aBond) {
		ContractLine tContractLine;
		
		tContractLine = new ContractLine (aCity, aShareCompany, aBond);
		
		return tContractLine;
	}
}
