package ge18xx.round;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
import ge18xx.round.action.StartAuctionAction;
import ge18xx.toplevel.AuctionFrame;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

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
		setName (NAME);
		setRoundType ();
	}

	public void setAuctionFrame (AuctionFrame aAuctionFrame) {
		auctionFrame = aAuctionFrame;
	}

	public AuctionFrame getAuctionFrame () {
		return auctionFrame;
	}

	@Override
	public ActorI.ActionStates getRoundState () {
		return ActorI.ActionStates.AuctionRound;
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

	@Override
	public boolean isAAuctionRound () {
		return true;
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
	
	public void clearAuctionStates (StartAuctionAction aStartAuctionAction) {
		PlayerManager tPlayerManager;
		ActorI.ActionStates tOldAuctionState;
		ActorI.ActionStates tNewAuctionState;
		Player tPlayer;
		int tPlayerCount;
		int tPlayerIndex;
		
		tPlayerManager = roundManager.getPlayerManager ();
		tPlayerCount = tPlayerManager.getPlayerCount ();
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
			tOldAuctionState = tPlayer.getAuctionActionState ();
			tPlayer.setAuctionActionState (ActorI.ActionStates.NoAction);
			tNewAuctionState = tPlayer.getAuctionActionState ();
			aStartAuctionAction.addAuctionStateChangeEffect (tPlayer, tOldAuctionState, tNewAuctionState);
		}
	}
	
	public void updateLowestBidderState (StartAuctionAction aStartAuctionAction) {
		ActorI.ActionStates tOldAuctionState;
		ActorI.ActionStates tNewAuctionState;
		Player tPlayer;
		int tLowestBidderIndex;
		Certificate tCertificate;
		Bank tBank;
		
		tBank = gameManager.getBank ();
		tCertificate = tBank.getPrivateForAuction ();
		tLowestBidderIndex = tCertificate.getLowestBidderIndex ();
		tPlayer = (Player) tCertificate.getCashHolderAt (tLowestBidderIndex);
		tOldAuctionState = tPlayer.getAuctionActionState ();
		tPlayer.setAuctionActionState (ActorI.ActionStates.Bidder);
		tNewAuctionState = tPlayer.getAuctionActionState ();
		aStartAuctionAction.addAuctionStateChangeEffect (tPlayer, tOldAuctionState, tNewAuctionState);
	}
	
	// New methods to Check and Handle this Auction Round to Interrupt another Round
	
	@Override
	public void start () {
		StartAuctionAction tStartAuctionAction;
		ActorI.ActionStates tRoundType;
		String tRoundID;
	
		super.start ();
		tRoundType = interruptedRound.getRoundState ();
		tRoundID = interruptedRound.getID ();

		tStartAuctionAction = new StartAuctionAction (tRoundType, tRoundID, interruptedRound);
		tStartAuctionAction.setChainToPrevious (true);
		
		setRoundToThis (tStartAuctionAction, true);
		clearAuctionStates (tStartAuctionAction);
		updateLowestBidderState (tStartAuctionAction);
		
		gameManager.addPrivateToAuction (tStartAuctionAction);
		gameManager.setAuctionFrameLocation ();

		roundManager.updatePassButton ();
		
		tStartAuctionAction.addShowFrameEffect (interruptedRound, auctionFrame);
		showAuctionFrame ();
		
		if (! roundManager.applyingAction ()) {
			addAction (tStartAuctionAction);
		}
	}
	
	@Override
	public void returnTo (Round aInterruptedRound) {
		StartAuctionAction tStartAuctionAction;
		ActorI.ActionStates tRoundType;
		String tRoundID;

		tRoundType = interruptedRound.getRoundState ();
		tRoundID = interruptedRound.getID ();

		super.returnTo (aInterruptedRound);
		tStartAuctionAction = new StartAuctionAction (tRoundType, tRoundID, interruptedRound);
		setRoundToThis (tStartAuctionAction, false);
		showAuctionFrame ();
	}
	
	public void setRoundToThis (ChangeRoundAction aChangeRoundAction, boolean aIncrementRoundID) {
		String tOldRoundID;
		String tNewRoundID;
		int tRoundID;

		tOldRoundID = getID ();
		if (aIncrementRoundID) {
			tRoundID = incrementRoundIDPart1 ();
			setIDPart1 (tRoundID);
			setIDPart2 (START_ID2);
			tNewRoundID = getID ();
		} else {
			tRoundID = getIDPart1 ();
			tNewRoundID = tOldRoundID;
		}
		setRoundTo (this, tRoundID, tOldRoundID, tNewRoundID, aChangeRoundAction);
	}

	@Override
	public void finish () {
		super.finish (auctionFrame);
	}

	@Override
	public boolean isInterrupting () {
		boolean tIsInterrupting;

		tIsInterrupting = roundManager.firstCertificateHasBidders ();
		
		return tIsInterrupting;
	}
	
	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_AUCTION_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}