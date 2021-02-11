package ge18xx.round;

import ge18xx.game.GameManager;
import ge18xx.player.PlayerManager;
import ge18xx.toplevel.AuctionFrame;

public class AuctionRound extends Round {
	public final static String NAME = "Auction Round";
	public static final PlayerManager NO_PLAYER_MANAGER = null;
	PlayerManager playerManager;
	AuctionFrame auctionFrame;

	public AuctionRound (PlayerManager aPlayerManager, RoundManager aRoundManager) {
		super (aRoundManager);
		GameManager tGameManager;
		
		setPlayerManager (aPlayerManager);
		tGameManager = aRoundManager.getGameManager ();
		setAuctionFrame (new AuctionFrame (tGameManager.createFrameTitle (NAME), 
				tGameManager.getClientUserName (),
				tGameManager.isNetworkGame ()));
		tGameManager.setAuctionFrame (auctionFrame);
	}

	public void setAuctionRoundInAuctionFrame () {
		auctionFrame.setAuctionRound (this);
	}
	
	public void setAuctionFrame (AuctionFrame aAuctionFrame) {
		auctionFrame = aAuctionFrame;
	}
	
	public AuctionFrame getAuctionFrame () {
		return auctionFrame;
	}
	
	public String getName () {
		return NAME;
	}
	
	@Override
	public String getType () {
		return NAME;
	}
	
	public String getID () {
		return "1.0";
	}
	
	public void setPrevBidderBoxColor (int aNewBidderIndex) {
		auctionFrame.setPrevBidderBoxColor (aNewBidderIndex);
	}
	
	public void setNewBidderBoxColor (int aNewBidderIndex) {
		auctionFrame.setNewBidderBoxColor (aNewBidderIndex);
	}
	
	public void updateBidderBoxes () {
		auctionFrame.updateBidderBoxes ();	
	}
	
	public void showAuctionFrame () {
		playerManager.showAuctionRound ();
	}
	
	public void setPlayerManager (PlayerManager aPlayerManager) {
		playerManager = aPlayerManager;
		if (playerManager != NO_PLAYER_MANAGER) {
			playerManager.setAuctionRound (this);
		}
	}
	
	public void startAuctionRound () {
		roundManager.addPrivateToAuction ();
		playerManager.showAuctionRound ();
	}
	
	@Override
	public String getAbbrev () {
		return getName ();
	}
}