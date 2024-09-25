package ge18xx.round;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
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
		auctionFrame.setAuctionRound (this);
		setID (0, 0);
		setName (NAME);
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

//	public void startAuctionRound () {
////		roundManager.addPrivateToAuction ();
////		roundManager.setAuctionFrameLocation ();
////		auctionFrame.showFrame ();
//	}
	
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
		ActorI.ActionStates tRoundType;
		String tRoundID;
	
		super.start ();
		tRoundType = interruptedRound.getRoundType ();
		tRoundID = interruptedRound.getID ();

		tStartAuctionAction = new StartAuctionAction (tRoundType, tRoundID, interruptedRound);
		tStartAuctionAction.addSetTriggeredAuctionEffect (this, true);	
		tStartAuctionAction.setChainToPrevious (true);
		addAction (tStartAuctionAction);
		
		setRoundToThis (true);
		
		gameManager.addPrivateToAuction ();
		gameManager.setAuctionFrameLocation ();

		roundManager.updatePassButton ();
		
		auctionFrame.showFrame ();
	}
	
	public void setRoundToThis (boolean aCreateNewAuctionAction) {
		String tOldRoundID;
		String tNewRoundID;
		String tGameName;
		int tRoundID;
		RoundFrame tRoundFrame;

		tOldRoundID = getID ();
		tRoundID = roundManager.incrementRoundIDPart1 (this);
		tNewRoundID = tRoundID + "";
//		setID (tNewRoundID);
		roundManager.changeRound (interruptedRound, ActorI.ActionStates.AuctionRound, this, tOldRoundID, tNewRoundID,
				aCreateNewAuctionAction);
		tGameName = roundManager.getGameName ();
		tRoundFrame = roundManager.getRoundFrame ();
		tRoundFrame.setAuctionRound (tGameName, tRoundID);
	}
	
	@Override
	public void finish () {
		super.finish ();
		
		ActorI.ActionStates tRoundType;
		ActorI.ActionStates tInterruptedRoundType;
		ChangeRoundAction tChangeRoundAction;
		String tOldRoundID;
		String tNewRoundID;
		String tCurrentRoundID;
		
		tRoundType = getRoundType ();
		tInterruptedRoundType = interruptedRound.getRoundType ();
		tOldRoundID = interruptedRound.getID ();
		tNewRoundID = tOldRoundID;
		tCurrentRoundID = getID ();

		tChangeRoundAction = new ChangeRoundAction (tRoundType, tCurrentRoundID, this);
		tChangeRoundAction.addStateChangeEffect (this, tRoundType, tInterruptedRoundType);
		tChangeRoundAction.addHideFrameEffect (this, auctionFrame);
		tChangeRoundAction.setChainToPrevious (true);
		roundManager.changeRound (this, tInterruptedRoundType, interruptedRound, tOldRoundID, tNewRoundID, true);
		auctionFrame.hideFrame ();
	}

	@Override
	public boolean interruptRound () {
		boolean tInterruptRound;

		tInterruptRound = roundManager.firstCertificateHasBidders ();
		
		return tInterruptRound;
	}
}