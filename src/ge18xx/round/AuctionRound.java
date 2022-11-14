package ge18xx.round;

import ge18xx.game.GameManager;
import ge18xx.toplevel.AuctionFrame;

public class AuctionRound extends Round {
	public final static String NAME = "Auction Round";
	public final static AuctionRound NO_AUCTION_ROUND = null;
	AuctionFrame auctionFrame;
	GameManager gameManager;

	public AuctionRound (RoundManager aRoundManager) {
		super (aRoundManager);

		gameManager = aRoundManager.getGameManager ();
		setAuctionFrame (new AuctionFrame (gameManager.createFrameTitle (NAME), gameManager.getClientUserName (),
				gameManager.isNetworkGame ()));
		gameManager.setAuctionFrame (auctionFrame);
		setID (0, 0);
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

	@Override
	public String getName () {
		return NAME;
	}

	@Override
	public String getType () {
		return NAME;
	}

	@Override
	public String getID () {
		return getIDPart1 () + "";
	}

	public void setPrevBidderJPanelColor (int aNewBidderIndex) {
		auctionFrame.setPrevBidderJPanelColor (aNewBidderIndex);
	}

	public void setNewBidderJPanelColor (int aNewBidderIndex) {
		auctionFrame.setNewBidderJPanelColor (aNewBidderIndex);
	}

	public void updateBidderJPanels () {
		auctionFrame.updateBidderJPanels ();
	}

	public void showAuctionFrame () {
		gameManager.showAuctionFrame ();
	}

	public void startAuctionRound () {
		roundManager.addPrivateToAuction ();
		roundManager.setAuctionFrameLocation ();
		showAuctionFrame ();
	}
	
	@Override
	public boolean isAAuctionRound () {
		return true;
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
}