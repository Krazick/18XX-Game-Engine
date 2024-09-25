package ge18xx.round;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.StartAuctionAction;
import ge18xx.toplevel.AuctionFrame;
import geUtilities.GUI;

public class AuctionRound extends InterruptionRound {
	public final static String NAME = "Auction Round";
	public final static AuctionRound NO_AUCTION_ROUND = null;
	AuctionFrame auctionFrame;
	GameManager gameManager;

	public AuctionRound (RoundManager aRoundManager) {
		super (aRoundManager);

		gameManager = aRoundManager.getGameManager ();
		setAuctionFrame (new AuctionFrame (gameManager.createFrameTitle (NAME), 
							gameManager.getClientUserName (), gameManager.isNetworkGame (), gameManager));
		gameManager.setAuctionFrame (auctionFrame);
		gameManager.addNewFrame (auctionFrame);
		setID (0, 0);
		setName (NAME);
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
	public ActorI.ActionStates getRoundType () {
		return ActorI.ActionStates.AuctionRound;
	}

	@Override
	public String getID () {
		return getIDPart1 () + GUI.EMPTY_STRING;
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
		auctionFrame.showFrame ();
	}

	public void startAuctionRound () {
//		roundManager.addPrivateToAuction ();
//		roundManager.setAuctionFrameLocation ();
//		auctionFrame.showFrame ();
	}
	
	@Override
	public boolean isAAuctionRound () {
		return true;
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
	
	// New methods to Check and Handle this Auction Round to Interrupt another Round
	
	@Override
	public void start () {
		StartAuctionAction tStartAuctionAction;
		Round tCurrentRound;
		ActorI.ActionStates tRoundType;
		String tRoundID;
	
		setInterruptionStarted (true);
		tCurrentRound = roundManager.getStockRound ();
		tRoundType = tCurrentRound.getRoundType ();
		tRoundID = tCurrentRound.getID ();

		tStartAuctionAction = new StartAuctionAction (tRoundType, tRoundID, tCurrentRound);
		tStartAuctionAction.addSetTriggeredAuctionEffect (this, true);	
		tStartAuctionAction.setChainToPrevious (true);
//		addAction (tStartAuctionAction);
		
		roundManager.setRoundToAuctionRound (true);

		roundManager.addPrivateToAuction ();
		roundManager.setAuctionFrameLocation ();
		auctionFrame.showFrame ();
		
		roundManager.updatePassButton ();
	}
	
	@Override
	public void finish () {
		setInterruptionStarted (false);
		auctionFrame.hideFrame ();
	}

	@Override
	public boolean interruptRound () {
		boolean tInterruptRound;

		tInterruptRound = roundManager.firstCertificateHasBidders ();
		
		return tInterruptRound;
	}
}