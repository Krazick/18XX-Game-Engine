package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Bidder;
import ge18xx.player.Escrow;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.AuctionRound;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.Action;
import ge18xx.round.action.AuctionPassAction;
import ge18xx.round.action.AuctionRaiseAction;

public class AuctionFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static int NO_BIDDER_INDEX = -1;
	private final String HIGHEST_NO_RAISE = "Highest Bidder does not need to Raise the Bid";
	private final String HIGHEST_NO_PASS = "Highest Bidder does not need to Pass the Bid";
	private final String ONLY_ONE_BIDDER_NOT_YOU = "Only One Bidder, which is not You";
	private final String WON_NOT_YOU = "Auction has been won, but not by you";
	private final String WON_BY_YOU = "You have won the Auction";
	private final String MULTIPLE_BIDDERS_IN_AUCTION = "Multiple Bidders still in the Auction";
	private final String AUCTION_WON = "Auction has been won";
	private final String HIGHEST = "Highest Bidder";
	private final String ONLY_BIDDER = "Only Bidder";
	private final String NOT_HIGHEST = "";
	private final String RAISE = "Raise";
	private final String PASS = "Pass";
	private final String RAISED = "Raised";
	private final String PASSED = "Passed";
	private final String BIDDING = "Bidding";
	private final String WAITING = "Waiting";
	private final String DONE = "Done Auction";
	private final String UNDO = "Undo";

	JLabel privateCompanyLabel;
	JButton doneButton;
	JButton undoButton;
	
	JPanel oneBidderBox;
	JPanel topBox;
	JPanel biddersBox;
	JPanel bottomBox;
	JButton [] bidderRaiseButtons;
	JButton [] bidderPassButtons;
	JLabel [] bidderLabels;
	JLabel [] bidderSuffixLabel;
	Certificate certificateToAuction;
	AuctionRound auctionRound;
	String clientUserName;
	String doneToolTipText = MULTIPLE_BIDDERS_IN_AUCTION;
	boolean isNetworkGame;
	boolean onlyBidder = false;
	Color defaultColor;
	
	public AuctionFrame (String aFrameName, String aClientUser, boolean aIsNetworkGame) {
		super (aFrameName);
		
		JLabel tLabel;
		
		clientUserName = aClientUser;
		isNetworkGame = aIsNetworkGame;
		defaultColor = this.getBackground ();
		
		tLabel = new JLabel ("Auction Round for Private Company");
		tLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		privateCompanyLabel = new JLabel ("DUMMY PRIVATE");
		topBox = new JPanel ();
		topBox.setLayout (new BoxLayout (topBox, BoxLayout.Y_AXIS));
		topBox.add (Box.createVerticalStrut (15));
		topBox.add (tLabel);
		topBox.add (Box.createVerticalStrut (10));
		topBox.add (privateCompanyLabel);
		topBox.add (Box.createVerticalStrut (15));
		
		biddersBox = new JPanel ();
		biddersBox.setLayout (new BoxLayout (biddersBox, BoxLayout.Y_AXIS));
		bottomBox = new JPanel ();
		bottomBox.setLayout (new BoxLayout (bottomBox, BoxLayout.Y_AXIS));

		biddersBox.add (Box.createVerticalStrut (15));
		oneBidderBox = new JPanel ();
		oneBidderBox.setLayout (new BoxLayout (oneBidderBox, BoxLayout.X_AXIS));
		oneBidderBox.add (new JLabel ("Bidder Box"));
		biddersBox.add (Box.createVerticalStrut (15));
		
		bottomBox.add (Box.createVerticalStrut (5));
		doneButton = setupButton (DONE, DONE);
		bottomBox.add (Box.createVerticalStrut (5));
		undoButton = setupButton (UNDO, UNDO);
		bottomBox.add (Box.createVerticalStrut (5));
		
		add (topBox, BorderLayout.NORTH);
		add (biddersBox, BorderLayout.CENTER);
		add (bottomBox, BorderLayout.SOUTH);
	}
	
	public JButton setupButton (String aButtonText, String aButtonCommand) {
		JButton tJButton;
		
		tJButton = new JButton (aButtonText);
		tJButton.setActionCommand(aButtonCommand);
		tJButton.addActionListener (this);
		tJButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		bottomBox.add (tJButton);
		if (isNetworkGame) {
			if (aButtonText.equals (UNDO)) {
				tJButton.setEnabled (false);
			}
		}
		
		return tJButton;
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tTheAction = aEvent.getActionCommand ();
		int tActingBidderIndex = getActingBidderIndex (aEvent);
		
		if (DONE.equals (tTheAction)) {
			completeAuction ();
		}
		if (RAISE.equals (tTheAction)) {
			raiseBid (tActingBidderIndex);
		}
		if (PASS.equals (tTheAction)) {
			passBid (tActingBidderIndex);
		}
		if (UNDO.equals (tTheAction)) {
			undoLastAction ();
		}
	}
	
	private void completeAuction () {
		boolean tNextShareHasBids;
		
		// Transfer Certificate to Highest Bidder
		// Transfer Escrow from Highest Bidder to Bank
		// Remove Escrow element for the Certificate from the Highest Bidder
		// For all Losing Bidders:
		//    1. transfer Escrow back to Treasury for the Bidder
		//    2. Remove Escrow element for the Certificate from the Bidder
		// Remove all Bids from the Certificate Purchased
		// Add the Complete Auction Action with the Effects above to the auctionRound
		//    1. Complete Auction extends Buy Stock Action
		//    2. Effect to Remove Escrow Element from Player
		//    3. Effect to Remove Bid Element from Certificate
		int tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();
		Player tPlayer = (Player) certificateToAuction.getCashHolderAt (tHighestBidderIndex);
		
		this.setBidderBoxColor (tPlayer.getName (), false);
		tNextShareHasBids = tPlayer.finishAuction (certificateToAuction, true);
		if (! tNextShareHasBids) {
			hideAuctionFrame ();			
		}
	}
	
	private void undoLastAction () {
		boolean tLastActionUndone;
		boolean tWasStartAuction;

		tWasStartAuction = auctionRound.wasLastActionStartAuction ();
		tLastActionUndone = auctionRound.undoLastAction ();
		updateBidderBoxes ();
		auctionRound.updateAllFrames ();
		
		// If the Last Action being undone was to Start the Auction, the Auction Frame should be hidden.
		if (tWasStartAuction && tLastActionUndone) {
			hideAuctionFrame ();
		}
	}
	
	private void raiseBid (int aActingBidderIndex) {
		int tBidderCount = certificateToAuction.getNumberOfBidders ();
		int tNextBidderIndex, tRaiseAmount;
		int tOldBidAmount, tNewBidAmount;
		Player tPlayer;
		Player tNextPlayer;
		Bidder tBidder;
		Escrow tEscrow;
		ActorI.ActionStates tOldBidderState, tNewBidderState, tOldNextBidderState, tNewNextBidderState;
		AuctionRaiseAction tAuctionRaiseAction;
		
		tPlayer = (Player) certificateToAuction.getCashHolderAt (aActingBidderIndex);
		tEscrow = tPlayer.getEscrowFor (certificateToAuction);
		tNextBidderIndex = (aActingBidderIndex + 1) % tBidderCount;
		tNextPlayer = (Player) certificateToAuction.getCashHolderAt (tNextBidderIndex);

		tOldBidderState = tPlayer.getAuctionActionState ();
		tAuctionRaiseAction = new AuctionRaiseAction (ActorI.ActionStates.AuctionRound, "0", tPlayer);

		tOldBidAmount = certificateToAuction.getBidAt (aActingBidderIndex);
		tRaiseAmount = certificateToAuction.getRaiseAmount (aActingBidderIndex);
		certificateToAuction.raiseBidFor (aActingBidderIndex);
		tNewBidAmount = certificateToAuction.getBidAt (aActingBidderIndex);
		tPlayer.setAuctionActionState (ActorI.ActionStates.AuctionRaise);
		
		tEscrow.setCash (tNewBidAmount);
		tNewBidderState = tPlayer.getAuctionActionState ();
		
		tAuctionRaiseAction.addAuctionStateChangeEffect (tPlayer, tOldBidderState, tNewBidderState);
		for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
			tNextPlayer = (Player) certificateToAuction.getCashHolderAt (tBidderIndex);
			tBidder = certificateToAuction.getBidderAt (tBidderIndex);
			if (! (tNextPlayer.equals (tPlayer))) {
				tOldNextBidderState = tNextPlayer.getAuctionActionState ();
				if (tNextBidderIndex == tBidderIndex) {
					tNextPlayer.setAuctionActionState (ActorI.ActionStates.Bidder);
				} else {
					tNextPlayer.setAuctionActionState (ActorI.ActionStates.NoAction);
					tBidder.setAuctionActionState (ActorI.ActionStates.NoAction);
				}
				tNewNextBidderState = tNextPlayer.getAuctionActionState ();
				if (tBidder.getName ().equals (tNextPlayer.getName ())) {
					if (tOldNextBidderState != tNewNextBidderState) {
						tAuctionRaiseAction.addAuctionStateChangeEffect (tNextPlayer, tOldNextBidderState, tNewNextBidderState);
					}
				} else {
					tAuctionRaiseAction.addAuctionStateChangeEffect (tNextPlayer, tOldNextBidderState, tNewNextBidderState);	
				}
			}
		}
		tAuctionRaiseAction.addCashTransferEffect (tPlayer, tEscrow, tRaiseAmount);
		tAuctionRaiseAction.addBidChangeEffect (tPlayer, tOldBidAmount, tNewBidAmount, certificateToAuction);
		tAuctionRaiseAction.addNewCurrentBidderEffect (auctionRound, aActingBidderIndex, tNextBidderIndex);
		setBidderBoxColor (tPlayer.getName (), false);
		completeAuctionAction (tAuctionRaiseAction, false);
	}
	
	public void setNewBidderBoxColor (int aNewBidderIndex) {
		Player tPlayer;
		String tBidderName;
		
		tPlayer = (Player) certificateToAuction.getCashHolderAt (aNewBidderIndex);
		tBidderName = tPlayer.getName ();
		setBidderBoxColor (tBidderName, true);
	}
	
	public void setPrevBidderBoxColor (int aPrevBidderIndex) {
		Player tPlayer;
		String tBidderName;
		
		tPlayer = (Player) certificateToAuction.getCashHolderAt (aPrevBidderIndex);
		tBidderName = tPlayer.getName ();
		setBidderBoxColor (tBidderName, false);
	}
	
	private void setBidderBoxColor (String aBidderName, boolean aBidderActing) {
		Color tBackgroundColor = defaultColor;
		
		if (aBidderActing) {
			if (isNetworkGame) {
				if (aBidderName.equals (clientUserName)) {
					tBackgroundColor = Color.ORANGE;
					toFront ();
				}
			}
		}
		topBox.setBackground (tBackgroundColor);
		biddersBox.setBackground (tBackgroundColor);
		bottomBox.setBackground (tBackgroundColor);
//		getContentPane ().setBackground (tBackgroundColor);
	}
	
	private int getNextBidderIndex (int aActingBidderIndex) {
		int tNextBidderIndex = 0;
		int tBidderCount;
		
		tBidderCount = certificateToAuction.getNumberOfBidders ();
		tNextBidderIndex = (aActingBidderIndex + 1) % tBidderCount;
		
		return tNextBidderIndex;
	}
	
	private void passBid (int aActingBidderIndex) {
		int tNextBidderIndex;
		boolean tDone = true;
		boolean tHaveOnlyOneBidderLeft;
		Player tPlayer;
		Player tNextPlayer;
		Bidder tBidder;
		AuctionPassAction tAuctionPassAction;
		ActorI.ActionStates tOldBidderState, tNewBidderState, tOldNextBidderState, tNewNextBidderState;
		
		certificateToAuction.passBidFor (aActingBidderIndex);
		tPlayer = (Player) certificateToAuction.getCashHolderAt (aActingBidderIndex);
		
		tNextBidderIndex = getNextBidderIndex (aActingBidderIndex);
		tNextPlayer = (Player) certificateToAuction.getCashHolderAt (tNextBidderIndex);
		tBidder = certificateToAuction.getBidderAt (tNextBidderIndex);
		
		tOldBidderState = tPlayer.getAuctionActionState ();
		tOldNextBidderState = tNextPlayer.getAuctionActionState ();
		
		tAuctionPassAction = new AuctionPassAction (ActorI.ActionStates.AuctionRound, "0", tPlayer);
		tPlayer.setAuctionActionState (ActorI.ActionStates.AuctionPass);
		
		tNewBidderState = tPlayer.getAuctionActionState ();
		tAuctionPassAction.addAuctionPassEffect (tPlayer, tOldBidderState, tNewBidderState);
		tAuctionPassAction.addAuctionStateChangeEffect (tPlayer, tOldBidderState, tNewBidderState);
		
		tHaveOnlyOneBidderLeft = certificateToAuction.haveOnlyOneBidderLeft ();
		if (tHaveOnlyOneBidderLeft) {
			tDone = clientIsWinner ();
		} else {
			tNextPlayer.setAuctionActionState (ActorI.ActionStates.Bidder);
			tNewNextBidderState = tNextPlayer.getAuctionActionState ();
			tAuctionPassAction.addAuctionStateChangeEffect (tBidder, tOldNextBidderState, tNewNextBidderState);
			tAuctionPassAction.addNewCurrentBidderEffect (auctionRound, aActingBidderIndex, tNextBidderIndex);
			tDone = false;
		}
		setBidderBoxColor (tPlayer.getName (), false);
		completeAuctionAction (tAuctionPassAction, tDone);
	}

	private boolean clientIsWinner () {
		boolean tClientIsWinner;
		Player tWinningPlayer;
		int tHighestBidderIndex;
		
		if (isNetworkGame) {
			tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();
			tWinningPlayer = (Player) certificateToAuction.getCashHolderAt (tHighestBidderIndex);
			if (tWinningPlayer != Player.NO_PLAYER) {
				if (clientUserName.equals (tWinningPlayer.getName ())) {
					tClientIsWinner = true;
					doneToolTipText = WON_BY_YOU;
				} else {
					tClientIsWinner = false;
					doneToolTipText = WON_NOT_YOU;
				}
			} else {
				tClientIsWinner = false;
			}
		} else {
			tClientIsWinner = true;
			doneToolTipText = AUCTION_WON;
		}
		
		return tClientIsWinner;
	}
		
	private void completeAuctionAction (Action aAuctionAction, boolean aDone) {
		auctionRound.addAction (aAuctionAction);
		// If all Bidders have passed, and one Raise -- We are done, and can enable Done Button.
		updateAuctionFrame (aDone);
	}
	
	public void updateAuctionFrame () {
		boolean tDone;
		
		if (certificateToAuction != null) {
			tDone = certificateToAuction.haveOnlyOneBidderLeft ();
			updateAuctionFrame (tDone);
		}
	}
	
	public void updateAuctionFrame (boolean aDone) {
		boolean tClientIsActing = false;
		
		if (aDone) {
			tClientIsActing = clientIsWinner ();
			doneButton.setEnabled (tClientIsActing);
		}

		doneButton.setToolTipText (doneToolTipText);
		updateBidderBoxes ();
		setBidderBoxColor (clientUserName, tClientIsActing);
	}
	
	private boolean thisIsTheClient () {
		boolean thisIsTheClient;
		
		if (isNetworkGame) {
			Player tPlayer = (Player) certificateToAuction.getCashHolderAt (0);
			if (clientUserName.equals (tPlayer.getName ())) {
				thisIsTheClient = true;
			} else {
				thisIsTheClient = false;
			}
		} else {
			thisIsTheClient = true;
		}
		
		return thisIsTheClient;
	}
	
	private void clearAllAuctionStates () {
		auctionRound.clearAllAuctionStates ();
	}
		
	public int getActingBidderIndex (ActionEvent aEvent) {
		int tActingBidderIndex = NO_BIDDER_INDEX;
		int tBidderCount = certificateToAuction.getNumberOfBidders();
		JButton tThisButton;
		
		tThisButton = (JButton) aEvent.getSource ();
		if (tBidderCount > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				if (tThisButton.equals (bidderRaiseButtons [tBidderIndex])) {
					tActingBidderIndex = tBidderIndex;
				} else if (tThisButton.equals (bidderPassButtons [tBidderIndex])) {
					tActingBidderIndex = tBidderIndex;
				}
			}
		}
	
		return tActingBidderIndex;
	}
	
	public void hideAuctionFrame () {
		setVisible (false);
	}
	
	public void addPrivateToAuction (Certificate aCertificateToAuction) {
		Player tPlayer;
		int tCash;
		int tBidderCount;
		int tHighestBidderIndex, tLowestBidderIndex;
		String tRaiseLabel, tBidderName;
		boolean tBidderIsActing;
		
		privateCompanyLabel.setText (aCertificateToAuction.getCompanyAbbrev () + 
				" Value " + Bank.formatCash (aCertificateToAuction.getValue ()));
		privateCompanyLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		certificateToAuction = aCertificateToAuction;
		
		tBidderCount = certificateToAuction.getNumberOfBidders ();
		if (tBidderCount > 0) {
			bidderRaiseButtons = new JButton [tBidderCount];
			bidderPassButtons = new JButton [tBidderCount];
			bidderSuffixLabel = new JLabel [tBidderCount];
			bidderLabels = new JLabel [tBidderCount];
			
			// Clear the Auction States of All Players
			clearAllAuctionStates ();
			// Then set for the Bidders for this Certificate to Raise Bid -- For Multiple Auctions, need to clear and reset to allow it to detect all proper bidders are done
			certificateToAuction.setBiddersAsRaiseBid ();
			tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();
			
			// Find the Lowest Bidder and set the state to NoAction
			tLowestBidderIndex = certificateToAuction.getLowestBidderIndex ();
			tPlayer = (Player) certificateToAuction.getCashHolderAt (tLowestBidderIndex);
			tPlayer.setAuctionActionState (ActorI.ActionStates.Bidder);
			
			// Empty out the Bidders Box of all "one Bidder Box"s in case of undo/redo.. or followup auctions.
			biddersBox.removeAll ();
			for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				setBidderSuffixLabel (tBidderCount, tBidderIndex, tHighestBidderIndex);
				tPlayer = (Player) certificateToAuction.getCashHolderAt (tBidderIndex);
				tBidderName = tPlayer.getName ();
				tBidderIsActing = isBidderActing (tBidderIndex, tBidderName);
				
				tCash = certificateToAuction.getBidAt (tBidderIndex);
				bidderLabels [tBidderIndex] = new JLabel (getBidderLabel (tPlayer, tCash));
				oneBidderBox = new JPanel ();
				oneBidderBox.setLayout (new BoxLayout (oneBidderBox, BoxLayout.X_AXIS));
				oneBidderBox.add (Box.createHorizontalStrut (15));
				oneBidderBox.add (bidderLabels [tBidderIndex]);
				oneBidderBox.add (Box.createHorizontalStrut (5));
				tRaiseLabel = RAISE + " " + Bank.formatCash (PlayerManager.BID_INCREMENT);
				bidderRaiseButtons [tBidderIndex] = new JButton (tRaiseLabel);
				bidderRaiseButtons [tBidderIndex].addActionListener (this);
				bidderRaiseButtons [tBidderIndex].setActionCommand (RAISE);

				oneBidderBox.add (bidderRaiseButtons [tBidderIndex]);
				oneBidderBox.add (Box.createHorizontalStrut (5));
				bidderPassButtons [tBidderIndex] = new JButton (PASS);
				bidderPassButtons [tBidderIndex].addActionListener (this);
				bidderPassButtons [tBidderIndex].setActionCommand (PASS);
				oneBidderBox.add (bidderPassButtons [tBidderIndex]);
				
				if (tBidderIndex == tHighestBidderIndex) {
					if (tBidderCount == 1) {
						tBidderIsActing = thisIsTheClient ();
						doneButton.setEnabled (tBidderIsActing);
						doneToolTipText = ONLY_ONE_BIDDER_NOT_YOU;
						if (!tBidderIsActing) {
							doneButton.setToolTipText (doneToolTipText);
						}
					} else {
						tBidderIsActing = false;
						doneButton.setEnabled (false);
						doneButton.setToolTipText (doneToolTipText);
					}
					setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, false, tBidderIsActing, HIGHEST_NO_RAISE);
					setButton (bidderPassButtons [tBidderIndex], PASS, false, tBidderIsActing, HIGHEST_NO_PASS);
				} else {
					setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, true, tBidderIsActing, NOT_HIGHEST);
					setButton (bidderPassButtons [tBidderIndex], PASS, true, tBidderIsActing, NOT_HIGHEST);
					doneButton.setEnabled (false);
					doneButton.setToolTipText (doneToolTipText);
				}
				oneBidderBox.add (bidderSuffixLabel [tBidderIndex]);
				oneBidderBox.add (Box.createHorizontalStrut (15));
				biddersBox.add (oneBidderBox);
				biddersBox.add (Box.createVerticalStrut (15));
				
				if (tBidderIsActing) {
					setBidderBoxColor (tBidderName, tBidderIsActing);
				}
				configAuctionUndoButton ();
			}			
		} else {
			System.err.println ("ERROR -- Adding Certificate for " + certificateToAuction.getCompanyAbbrev () + " with NO Bidders!!!");
		}
	}
	
	public void configAuctionUndoButton () {
		String tClientName;
		GameManager tGameManager;
		boolean tAmIBidder;
		
		tGameManager = auctionRound.getGameManager ();
		tClientName = tGameManager.getClientUserName ();
		tAmIBidder = certificateToAuction.amIABidder (tClientName);
		if (tAmIBidder && ! isNetworkGame) {
			undoButton.setEnabled (true);
			undoButton.setToolTipText ("");
		} else {
			disableAuctionUndoButton ();
		}
	}
	
	public void disableAuctionUndoButton () {
		undoButton.setEnabled (false);
		undoButton.setToolTipText ("You are not a Bidder, cannot Undo");
	}
	
	public void updateBidderBoxes () {
		Player tPlayer;
		int tCash, tBidderCount, tHighestBidderIndex;
		String tRaiseLabel;
		String tBidderName;
		boolean tBidderIsActing;

		tBidderCount = certificateToAuction.getNumberOfBidders ();
		tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();

		for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
			setBidderSuffixLabel (tBidderCount, tBidderIndex, tHighestBidderIndex);
			tPlayer = (Player) certificateToAuction.getCashHolderAt (tBidderIndex);
			tBidderName = tPlayer.getName ();
			tBidderIsActing = isBidderActing (tBidderIndex, tBidderName);
			tCash = certificateToAuction.getBidAt (tBidderIndex);
			bidderLabels [tBidderIndex].setText (getBidderLabel (tPlayer, tCash));
			tRaiseLabel = RAISE + " " + Bank.formatCash (PlayerManager.BID_INCREMENT);
			configAuctionUndoButton ();
//			setBidderBoxColor (tBidderName, tBidderIsActing);
			
			if (tBidderIndex == tHighestBidderIndex) {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, false, tBidderIsActing, HIGHEST_NO_RAISE);
				setButton (bidderPassButtons [tBidderIndex], PASS, false, tBidderIsActing, HIGHEST_NO_PASS);
			} else {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, true, tBidderIsActing, NOT_HIGHEST);
				setButton (bidderPassButtons [tBidderIndex], PASS, true, tBidderIsActing, NOT_HIGHEST);
			}
		}
	}
	
	private boolean isBidderActing (int aBidderIndex, String aBidderName) {
		boolean tIsBidderActing = false;
		
		if (BIDDING.equals (bidderSuffixLabel [aBidderIndex].getText ())) {
			tIsBidderActing = true;
		}
		
		if (isNetworkGame) {
			if (! aBidderName.equals (clientUserName)) {
				tIsBidderActing = false;
			}
		}
		
		return tIsBidderActing;
	}
	
	private void setBidderSuffixLabel (int aBidderCount, int aBidderIndex, int aHighestBidderIndex) {
		String tSuffixLabel = HIGHEST;
		Player tPlayer;
		Bidder tBidder;
		ActorI.ActionStates tLastAction;
		
		if (aBidderCount > 0) {
			if (aBidderCount == 1) {
				tSuffixLabel = ONLY_BIDDER;
				onlyBidder = true;
			} else if (aBidderIndex == aHighestBidderIndex) {
				tSuffixLabel = HIGHEST;
			} else {
				tPlayer = (Player) certificateToAuction.getCashHolderAt (aBidderIndex);
				tLastAction = tPlayer.getAuctionActionState ();
				tBidder = certificateToAuction.getBidderAt (aBidderIndex);
				System.out.println ("Player " + tPlayer.getName () + " State " + 
						tPlayer.getAuctionActionState () + 
						" Bidder " + tBidder.getName () + " State " + tBidder.getStateName ());
				if (ActorI.ActionStates.AuctionPass.equals (tLastAction)) {
					tSuffixLabel = PASSED;
				} else if (ActorI.ActionStates.AuctionRaise.equals (tLastAction)) {
					tSuffixLabel = RAISED;
				} else if (ActorI.ActionStates.Bidder.equals (tLastAction)) {
					tSuffixLabel = BIDDING;
				} else if (ActorI.ActionStates.NoAction.equals (tLastAction)) {
					tSuffixLabel = WAITING;
				} else {
					tSuffixLabel = "BAD Auction State";
				}
			}
		} else {
			tSuffixLabel = "BAD BIDDER COUNT";
		}
		
		if (bidderSuffixLabel [aBidderIndex] == null) {
			bidderSuffixLabel [aBidderIndex] = new JLabel (tSuffixLabel);
		} else {
			bidderSuffixLabel [aBidderIndex].setText (tSuffixLabel);
		}
	}
	
	private String getBidderLabel (Player aPlayer, int aCash) {
		String tBidderLabel;
		int tPlayerCash = aPlayer.getCash ();
		
		tBidderLabel = "Bidder: " + aPlayer.getName () + " Treasury " + Bank.formatCash (tPlayerCash) + " Bid Amount " + Bank.formatCash (aCash);
		
		return tBidderLabel;
	}
	
	public void setAuctionRound (AuctionRound aAuctionRound) {
		auctionRound = aAuctionRound;
		if (auctionRound == AuctionRound.NO_AUCTION_ROUND) {
			System.err.println ("OOPs, Auction Round Not set yet");
		}
	}
	
	private void setButton (JButton aButton, String aLabel, boolean aVisible, boolean aEnabled, String aToolTip) {
		aButton.setText (aLabel);
		aButton.setVisible (aVisible);
		aButton.setEnabled (aEnabled);
		aButton.setToolTipText (aToolTip);
	}
}