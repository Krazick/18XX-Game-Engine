package ge18xx.toplevel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Bidder;
import ge18xx.player.Escrow;
import ge18xx.player.ParPriceFrame;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.AuctionRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.AuctionPassAction;
import ge18xx.round.action.AuctionRaiseAction;
import ge18xx.round.action.AuctionStateChangeAction;
import ge18xx.utilities.xml.XMLFrame;
import geUtilities.GUI;
import swingDelays.KButton;

public class AuctionFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static int NO_BIDDER_INDEX = -1;
	private final String HIGHEST_NO_RAISE = "Highest Bidder does not need to Raise the Bid";
	private final String HIGHEST_NO_PASS = "Highest Bidder does not need to Pass the Bid";
	private final String BIDDER_HAS_PASSED = "You have passed previously, and are out of the Auction";
	private final String WON_NOT_YOU = "Auction has been won, but not by you";
	private final String WON_BY_YOU = "You have won the Auction";
	private final String ACTIVE_BIDDER_CAN_UNDO = "As the active bidder, you can Undo";
	private final String NOT_ACTIVE_CANNOT_UNDO = "You are not the active Bidder, you cannot Undo";
	private final String MUST_SET_PAR_PRICE = "Auction must completed before setting Par Price";
	private final String MULTIPLE_BIDDERS_IN_AUCTION = "Multiple Bidders still in the Auction";
	private final String AUCTION_WON = "Auction has been won";
	private final String HIGHEST = "Highest Bidder";
	private final String ONLY_BIDDER = "Only Bidder";
	private final String NOT_HIGHEST = "";
	private final String RAISE = "Raise";
	private final String RAISE_BY = "Raise by:";
	private final String PASS = "Pass";
	private final String RAISED = "Raised";
	private final String PASSED = "Passed";
	private final String BIDDING = "Bidding";
	private final String WAITING = "Waiting";
	private final String DONE = "Done Auction";
	private final String UNDO = "Undo";

	JLabel privateCompanyLabel;
	JLabel freeCertificateLabel;
	KButton doneButton;
	KButton undoButton;

	JPanel oneBidderJPanel;
	JPanel auctionItemInfoJPanel;
	JPanel biddersJPanel;
	JPanel buttonJPanel;

	JPanel fullPanel;

	JComboBox<String> parValuesCombo;
	KButton setParPrice;

	KButton [] bidderRaiseButtons;
	KButton [] bidderRaiseByButtons;
	JTextField [] bidderRaiseAmountField;
	KButton [] bidderPassButtons;
	JLabel [] bidderLabels;
	JLabel [] bidderSuffixLabel;
	Certificate certificateToAuction;
	Certificate freeCertificate;
	AuctionRound auctionRound;
	String clientUserName;
	String doneToolTipText = MULTIPLE_BIDDERS_IN_AUCTION;
	boolean isNetworkGame;
	boolean onlyBidder = false;
	Color defaultColor;

	public AuctionFrame (String aFrameName, String aClientUser, boolean aIsNetworkGame, GameManager aGameManager) {
		super (aFrameName, aGameManager);

		fullPanel = new JPanel ();
		fullPanel.setLayout (new BoxLayout (fullPanel, BoxLayout.Y_AXIS));
		clientUserName = aClientUser;
		isNetworkGame = aIsNetworkGame;
		defaultColor = getBackground ();

		buildAuctionItemInfoJPanel ();
		buildBiddersJPanel ();
		buildButtonJPanel ();

		fullPanel.add (auctionItemInfoJPanel);
		fullPanel.add (biddersJPanel);
		fullPanel.add (buttonJPanel);
		add (fullPanel);
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		setSize (450, 210);
	}

	public void buildButtonJPanel () {
		buttonJPanel = new JPanel ();
		buttonJPanel.setLayout (new BoxLayout (buttonJPanel, BoxLayout.Y_AXIS));
		buttonJPanel.add (Box.createVerticalStrut (5));
		doneButton = setupButton (DONE, DONE);
		buttonJPanel.add (Box.createVerticalStrut (5));
		undoButton = setupButton (UNDO, UNDO);
		buttonJPanel.add (Box.createVerticalStrut (5));
	}

	public void buildBiddersJPanel () {
		biddersJPanel = new JPanel ();
		biddersJPanel.setLayout (new BoxLayout (biddersJPanel, BoxLayout.Y_AXIS));

		biddersJPanel.add (Box.createVerticalStrut (15));
		buildOneBidderJPanel ();
		biddersJPanel.add (Box.createVerticalStrut (15));
	}

	public void buildOneBidderJPanel () {
		oneBidderJPanel = new JPanel ();
		oneBidderJPanel.setLayout (new BoxLayout (oneBidderJPanel, BoxLayout.X_AXIS));
		oneBidderJPanel.add (new JLabel ("Bidder JPanel"));
	}

	public void buildAuctionItemInfoJPanel () {
		auctionItemInfoJPanel = new JPanel ();
		privateCompanyLabel = new JLabel ("DUMMY PRIVATE");
		freeCertificateLabel = new JLabel ("");
		fillAuctionItemInfo ();
	}

	public KButton setupButton (String aButtonText, String aButtonCommand) {
		KButton tKButton;

		tKButton = new KButton (aButtonText);
		tKButton.setActionCommand (aButtonCommand);
		tKButton.addActionListener (this);
		tKButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		buttonJPanel.add (tKButton);
		if (isNetworkGame) {
			if (aButtonText.equals (UNDO)) {
				tKButton.setEnabled (false);
			}
		}

		return tKButton;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tTheAction;
		int tActingBidderIndex;

		tTheAction = aEvent.getActionCommand ();
		tActingBidderIndex = getActingBidderIndex (aEvent);
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
		Player tPlayer;

		// Transfer Certificate to Highest Bidder
		// Transfer Escrow from Highest Bidder to Bank
		// Remove Escrow element for the Certificate from the Highest Bidder
		// For all Losing Bidders:
		// 1. transfer Escrow back to Treasury for the Bidder
		// 2. Remove Escrow element for the Certificate from the Bidder
		// Remove all Bids from the Certificate Purchased
		// Add the Complete Auction Action with the Effects above to the auctionRound
		// 1. Complete Auction extends Buy Stock Action
		// 2. Effect to Remove Escrow Element from Player
		// 3. Effect to Remove Bid Element from Certificate
		tPlayer = getHighestBidder ();

		setBidderJPanelColor (tPlayer.getName (), false);
		tNextShareHasBids = tPlayer.finishAuction (certificateToAuction, true);
		if (!tNextShareHasBids) {
			hideAuctionFrame ();
		}
	}

	private Player getHighestBidder () {
		int tHighestBidderIndex;
		Player tPlayer;

		tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();
		tPlayer = (Player) certificateToAuction.getCashHolderAt (tHighestBidderIndex);

		return tPlayer;
	}

	private void undoLastAction () {
		boolean tLastActionUndone;

		auctionRound.wasLastActionStartAuction ();
		tLastActionUndone = auctionRound.undoLastAction ();
		updateBidderJPanels ();
		auctionRound.updateAllFrames ();

		if (tLastActionUndone) {
			hideAuctionFrame ();
		}
	}

	private void raiseBid (int aActingBidderIndex) {
		int tRaiseAmount;
		int tOldBidAmount;
		int tNewBidAmount;
		Player tPlayer;
		String tPlayerName;
		Escrow tEscrow;
		ActorI.ActionStates tOldBidderState;
		ActorI.ActionStates tNewBidderState;
		AuctionRaiseAction tAuctionRaiseAction;

		tPlayer = (Player) certificateToAuction.getCashHolderAt (aActingBidderIndex);
		tEscrow = tPlayer.getEscrowFor (certificateToAuction);

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
		
		tAuctionRaiseAction.addCashTransferEffect (tPlayer, tEscrow, tRaiseAmount);
		tAuctionRaiseAction.addBidChangeEffect (tPlayer, tOldBidAmount, tNewBidAmount, certificateToAuction);
		
		moveToNextBidder (aActingBidderIndex, tPlayer, tAuctionRaiseAction);
		
		tPlayerName = tPlayer.getName ();
		setBidderJPanelColor (tPlayerName, false);
		completeAuctionAction (tAuctionRaiseAction, false);
	}

	public void moveToNextBidder (int aActingBidderIndex, Player aPlayer,
			AuctionStateChangeAction aAuctionStateChangeAction) {
		int tBidderCount;
		int tNextBidderIndex;
		Player tNextPlayer;
		Bidder tBidder;
		ActorI.ActionStates tOldNextBidderState;
		ActorI.ActionStates tNewNextBidderState;
		
		tBidderCount = certificateToAuction.getNumberOfBidders ();
		tNextBidderIndex = getNextBidderIndex (aActingBidderIndex, tBidderCount);
		tNextPlayer = (Player) certificateToAuction.getCashHolderAt (tNextBidderIndex);
		for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
			tNextPlayer = (Player) certificateToAuction.getCashHolderAt (tBidderIndex);
			tBidder = certificateToAuction.getBidderAt (tBidderIndex);
			if (!(tNextPlayer.equals (aPlayer))) {
				tOldNextBidderState = tNextPlayer.getAuctionActionState ();
				if (tNextBidderIndex == tBidderIndex) {
					tNextPlayer.setAuctionActionState (ActorI.ActionStates.Bidder);
				} else if (! tNextPlayer.hasPassedInAuction ()) {
					tNextPlayer.setAuctionActionState (ActorI.ActionStates.NoAction);
				}
				tNewNextBidderState = tNextPlayer.getAuctionActionState ();
				if (tBidder.getName ().equals (tNextPlayer.getName ())) {
					if (tOldNextBidderState != tNewNextBidderState) {
						aAuctionStateChangeAction.addAuctionStateChangeEffect (tNextPlayer, tOldNextBidderState,
								tNewNextBidderState);
					}
				} else {
					aAuctionStateChangeAction.addAuctionStateChangeEffect (tNextPlayer, tOldNextBidderState,
							tNewNextBidderState);
				}
			}
		}
		aAuctionStateChangeAction.addNewCurrentBidderEffect (auctionRound, aActingBidderIndex, tNextBidderIndex);
	}

	public int getNextBidderIndex (int aActingBidderIndex, int aBidderCount) {
		int tNextBidderIndex;
		boolean tNextNotFound;
		
		tNextNotFound = true;
		tNextBidderIndex = aActingBidderIndex;
		while (tNextNotFound) {
			tNextBidderIndex++;
			tNextBidderIndex = tNextBidderIndex % aBidderCount;
			if (! PASSED.equals (bidderSuffixLabel [tNextBidderIndex].getText ())) {
				tNextNotFound = false;
			}
				
		}
		
		return tNextBidderIndex;
	}

	public void setNewBidderJPanelColor (int aNewBidderIndex) {
		Player tPlayer;
		String tBidderName;

		tPlayer = (Player) certificateToAuction.getCashHolderAt (aNewBidderIndex);
		tBidderName = tPlayer.getName ();
		setBidderJPanelColor (tBidderName, true);
	}

	public void setPrevBidderJPanelColor (int aPrevBidderIndex) {
		Player tPlayer;
		String tBidderName;

		tPlayer = (Player) certificateToAuction.getCashHolderAt (aPrevBidderIndex);
		tBidderName = tPlayer.getName ();
		setBidderJPanelColor (tBidderName, false);
	}

	private void setBidderJPanelColor (String aBidderName, boolean aBidderActing) {
		Color tBackgroundColor;

		tBackgroundColor = defaultColor;
		if (aBidderActing) {
			if (isNetworkGame) {
				if (aBidderName.equals (clientUserName)) {
					tBackgroundColor = Color.ORANGE;
					toTheFront ();
				}
			}
		}
		auctionItemInfoJPanel.setBackground (tBackgroundColor);
		biddersJPanel.setBackground (tBackgroundColor);
		buttonJPanel.setBackground (tBackgroundColor);
	}

	private void passBid (int aActingBidderIndex) {
		boolean tDone;
		boolean tAuctionIsOver;
		Player tPlayer;
		String tPlayerName;
		AuctionPassAction tAuctionPassAction;
		ActorI.ActionStates tOldBidderState;
		ActorI.ActionStates tNewBidderState;

		tPlayer = (Player) certificateToAuction.getCashHolderAt (aActingBidderIndex);
		tAuctionPassAction = new AuctionPassAction (ActorI.ActionStates.AuctionRound, "0", tPlayer);
		tOldBidderState = tPlayer.getAuctionActionState ();
		certificateToAuction.passBidFor (aActingBidderIndex);

		tPlayer.setAuctionActionState (ActorI.ActionStates.AuctionPass);

		tNewBidderState = tPlayer.getAuctionActionState ();
		tAuctionPassAction.addAuctionPassEffect (tPlayer, tOldBidderState, tNewBidderState);
		tAuctionPassAction.addAuctionStateChangeEffect (tPlayer, tOldBidderState, tNewBidderState);

		tAuctionIsOver = certificateToAuction.auctionIsOver ();
		if (tAuctionIsOver) {
			tDone = clientIsWinner ();
		} else {		
			moveToNextBidder (aActingBidderIndex, tPlayer, tAuctionPassAction);
			tDone = false;
		}
		tPlayerName = tPlayer.getName ();
		setBidderJPanelColor (tPlayerName, false);
		completeAuctionAction (tAuctionPassAction, tDone);
	}

	private boolean clientIsBidding () {
		boolean tClientIsBidding;

		tClientIsBidding = true;
		if (isNetworkGame) {
			tClientIsBidding = certificateToAuction.amIABidder (clientUserName);
		}

		return tClientIsBidding;
	}
	
	private boolean clientIsWinner () {
		boolean tClientIsWinner;
		Player tWinningPlayer;

		if (isNetworkGame) {
			tWinningPlayer = getHighestBidder ();
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
		// If all Bidders have passed, and one Raise -- We are done, and can enable Done
		// Button.
		updateAuctionFrame (aDone);
		auctionRound.addAction (aAuctionAction);
	}

	public void updateAuctionFrame () {
		boolean tDone;

		if (certificateToAuction != Certificate.NO_CERTIFICATE) {
			tDone = certificateToAuction.auctionIsOver ();
			updateAuctionFrame (tDone);
		}
	}

	public void updateAuctionFrame (boolean aDone) {
		boolean tClientIsActing;

		if (aDone) {
			tClientIsActing = clientIsWinner ();
		} else {
			tClientIsActing = isBidderActing ();
		}
		updateDoneButton (aDone);
		updateBidderJPanels ();
		updateParValueComponents ();
		setBidderJPanelColor (clientUserName, tClientIsActing);
	}

	private boolean updateDoneButton (boolean aDone) {
		boolean tClientIsActing;

		tClientIsActing = true;
		if (isNetworkGame) {
			if (aDone) {
				if (clientIsWinner ()) {
					if (freeCertificate == Certificate.NO_CERTIFICATE) {
						doneButton.setEnabled (true);
						doneButton.setToolTipText ("You have won the Auction.");
					} else if (freeCertificate.isPresidentShare ()) {
						if (freeCertificate.hasParPrice ()) {
							doneButton.setEnabled (true);
							doneButton.setToolTipText ("You have won the Auction and will receive a Free Pre Certificate.");
						} else {
							doneButton.setEnabled (false);
							doneButton.setToolTipText ("You have won the Auction, and need to set the Par Price.");
						}
					} else {
						doneButton.setEnabled (true);
						doneButton.setToolTipText ("You have won and the Auction and will receive a Free Certificate.");
					}
				} else {
					doneButton.setEnabled (false);
					doneButton.setToolTipText ("The Auction is over, and you have lost.");		
				}
			} else {
				if (clientIsBidding ()) {
					doneButton.setEnabled (false);
					doneButton.setToolTipText ("The Auction is not over.");		
				} else {
					if (clientIsBidding ()) {
						doneButton.setEnabled (false);
						doneButton.setToolTipText ("You are bidding in the Auction, but Auction is not over.");
					} else {
						doneButton.setEnabled (false);
						doneButton.setToolTipText ("You are not bidding in the Auction.");
					}
					tClientIsActing = false;
				}
			}

		} else {
			doneButton.setEnabled (aDone);
			if (aDone) {
				if (freeCertificate == Certificate.NO_CERTIFICATE) {
					doneButton.setToolTipText ("Auction is done.");
				} else {
					if (freeCertificate.isPresidentShare ()) {
						if (freeCertificate.hasParPrice ()) {
							doneButton.setToolTipText ("Auction is done, winner will received a Free Prez Share.");
						} else {
							doneButton.setEnabled (false);
							doneButton.setToolTipText ("Auction is done, winner will set Par Price for Free Prez Share.");
						}
					} else {
						doneButton.setToolTipText ("Auction is done, winner will receive a Free Certificate");
					}
				}
			} else {
				doneButton.setToolTipText ("Auction is not over.");
			}
		}

		return tClientIsActing;
	}

	private void clearAllAuctionStates () {
		auctionRound.clearAllAuctionStates ();
	}

	public int getActingBidderIndex (ActionEvent aEvent) {
		int tActingBidderIndex;
		int tBidderCount;
		KButton tThisButton;

		tActingBidderIndex = NO_BIDDER_INDEX;
		tBidderCount = certificateToAuction.getNumberOfBidders ();

		tThisButton = (KButton) aEvent.getSource ();
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

	public void addPrivateToAuction (Certificate aCertificateToAuction, Certificate aFreeCertificate) {
		Player tPlayer;
		int tBidderCount;
		int tHighestBidderIndex;
		int tLowestBidderIndex;

		updateAuctionItemInfo (aCertificateToAuction, aFreeCertificate);

		tBidderCount = certificateToAuction.getNumberOfBidders ();
		if (tBidderCount > 0) {
			bidderRaiseButtons = new KButton [tBidderCount];
			bidderRaiseByButtons = new KButton [tBidderCount];
			bidderRaiseAmountField = new JTextField [tBidderCount];
			bidderPassButtons = new KButton [tBidderCount];
			bidderSuffixLabel = new JLabel [tBidderCount];
			bidderLabels = new JLabel [tBidderCount];

			// Clear the Auction States of All Players
			clearAllAuctionStates ();
			// Then set for the Bidders for this Certificate to Raise Bid -- For Multiple
			// Auctions, need to clear and reset to allow it to detect all proper bidders
			// are done
			certificateToAuction.setBiddersAsRaiseBid ();
			tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();

			// Find the Lowest Bidder and set the state to NoAction
			tLowestBidderIndex = certificateToAuction.getLowestBidderIndex ();
			tPlayer = (Player) certificateToAuction.getCashHolderAt (tLowestBidderIndex);
			tPlayer.setAuctionActionState (ActorI.ActionStates.Bidder);

			// Empty out the Bidders Box of all "one Bidder Box"s in case of undo/redo.. or
			// followup auctions.
			fillBiddersPanel (tBidderCount, tHighestBidderIndex);

			updateAuctionFrame ();
		} else {
			System.err.println ("ERROR -- Adding Certificate for " + certificateToAuction.getCompanyAbbrev ()
					+ " with NO Bidders!!!");
		}
	}

	private void fillBiddersPanel (int aBidderCount, int aHighestBidderIndex) {
		Player tPlayer;
		int tCash;
		int tFrameHeight;
		String tRaiseLabel;
		String tRaiseByLabel;
		String tBidderName;
		boolean tBidderIsActing;

		biddersJPanel.removeAll ();
		for (int tBidderIndex = 0; tBidderIndex < aBidderCount; tBidderIndex++) {
			setBidderSuffixLabel (aBidderCount, tBidderIndex, aHighestBidderIndex);
			tPlayer = (Player) certificateToAuction.getCashHolderAt (tBidderIndex);
			tBidderName = tPlayer.getName ();
			tBidderIsActing = isBidderActing (tBidderIndex, tBidderName);

			tCash = certificateToAuction.getBidAt (tBidderIndex);
			bidderLabels [tBidderIndex] = new JLabel (getBidderLabel (tPlayer, tCash));

			tRaiseLabel = RAISE + " " + Bank.formatCash (PlayerManager.BID_INCREMENT);
			tRaiseByLabel = RAISE_BY;
			updateOneBidderJPanel (tBidderIndex, tRaiseLabel);

			if (tBidderIndex == aHighestBidderIndex) {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, false, tBidderIsActing,
							HIGHEST_NO_RAISE);
				setButton (bidderRaiseByButtons [tBidderIndex], tRaiseByLabel, false, tBidderIsActing,
							HIGHEST_NO_RAISE);
				setButton (bidderPassButtons [tBidderIndex], PASS, false, tBidderIsActing, HIGHEST_NO_PASS);
			} else {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, true, tBidderIsActing, NOT_HIGHEST);
				setButton (bidderRaiseByButtons [tBidderIndex], tRaiseByLabel, true, tBidderIsActing, 
							NOT_HIGHEST);
				setButton (bidderPassButtons [tBidderIndex], PASS, true, tBidderIsActing, NOT_HIGHEST);
			}
			oneBidderJPanel.add (bidderSuffixLabel [tBidderIndex]);
			oneBidderJPanel.add (Box.createHorizontalStrut (15));
			biddersJPanel.add (oneBidderJPanel);
			biddersJPanel.add (Box.createVerticalStrut (15));

			if (tBidderIsActing) {
				setBidderJPanelColor (tBidderName, tBidderIsActing);
			}
		}
		tFrameHeight = 190 + aBidderCount * 40;
		if (freeCertificate != Certificate.NO_CERTIFICATE) {
			if (freeCertificate.isPresidentShare ()) {
				if (! freeCertificate.hasParPrice ()) {
					tFrameHeight += 50;
				}
			}
		}
		setSize (600, tFrameHeight);
	}

	public void updateAuctionItemInfo (Certificate aCertificateToAuction, Certificate aFreeCertificate) {
		String tCertText;

		setCertificateToAuction (aCertificateToAuction);
		setFreeCertificate (aFreeCertificate);

		tCertText = buildCertText (certificateToAuction, false);
		privateCompanyLabel.setText (tCertText);
		privateCompanyLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		if (freeCertificate != Certificate.NO_CERTIFICATE) {
			tCertText = buildCertText (freeCertificate, true);
			freeCertificateLabel.setText (tCertText);
			freeCertificateLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		} else {
			freeCertificateLabel.setText ("");
		}
		fillAuctionItemInfo ();
	}

	private String buildCertText (Certificate aCertificate, boolean aFree) {
		String tCertText;

		if (aFree) {
			tCertText = "Free " + freeCertificate.getPercentage () + "% ";
			if (aCertificate.isPresidentShare ()) {
				tCertText += "President ";
			}
			tCertText += "Certficate of " + freeCertificate.getCompanyAbbrev ();
		} else {
			tCertText = aCertificate.getCompanyAbbrev () + " Value "
					+ Bank.formatCash (aCertificate.getValue ());
		}

		return tCertText;
	}

	public void fillAuctionItemInfo () {
		JLabel tLabel;
		JPanel tParPricePanel;

		auctionItemInfoJPanel.removeAll ();
		tLabel = new JLabel ("Auction Round for Private Company");
		tLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		auctionItemInfoJPanel.setLayout (new BoxLayout (auctionItemInfoJPanel, BoxLayout.Y_AXIS));
		auctionItemInfoJPanel.add (Box.createVerticalStrut (15));
		auctionItemInfoJPanel.add (tLabel);
		auctionItemInfoJPanel.add (Box.createVerticalStrut (10));
		auctionItemInfoJPanel.add (privateCompanyLabel);
		auctionItemInfoJPanel.add (Box.createVerticalStrut (15));
		if (freeCertificate != Certificate.NO_CERTIFICATE) {
			auctionItemInfoJPanel.add (freeCertificateLabel);
			auctionItemInfoJPanel.add (Box.createVerticalStrut (15));
			if (freeCertificate.isPresidentShare ()) {
				tParPricePanel = buildSetParPricePanel ();
				auctionItemInfoJPanel.add (tParPricePanel);
				auctionItemInfoJPanel.add (Box.createVerticalStrut (15));
			}
		}
	}

	private JPanel buildSetParPricePanel () {
		GameManager tGameManager;
		Integer [] tParValues;
		Dimension tParValueSize;
		JPanel tParPricePanel;

		tParPricePanel = new JPanel ();
		tParPricePanel.setLayout (new BoxLayout (tParPricePanel, BoxLayout.X_AXIS));

		tGameManager = auctionRound.getGameManager ();

		tParValues = tGameManager.getAllStartCells ();
		parValuesCombo = new JComboBox<> ();
		tParValueSize = new Dimension (75, 20);
		parValuesCombo.setPreferredSize (tParValueSize);
		parValuesCombo.setMaximumSize (tParValueSize);
		freeCertificate.fillParValueComboBox (parValuesCombo, tParValues);
		parValuesCombo.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				updateParValueComponents ();
			}
		});
		setParPrice = new KButton ("Set Par Price");
		setParPrice.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				handleSetParPrice ();
			}
		});
		tParPricePanel.add (parValuesCombo);
		tParPricePanel.add (Box.createHorizontalStrut (15));
		tParPricePanel.add (setParPrice);
		updateParValueComponents ();

		return tParPricePanel;
	}

	private void handleSetParPrice () {
		GameManager tGameManager;
		ParPriceFrame tParPriceFrame;
		int tSelectedParPrice;

		tGameManager = auctionRound.getGameManager ();
		tParPriceFrame = tGameManager.buildParPriceFrame (freeCertificate);
		tSelectedParPrice = getParPrice ();
		if (tSelectedParPrice > 0) {
			tGameManager.handleSetParPrice (freeCertificate, tSelectedParPrice, tParPriceFrame);
		}
		updateDoneButton (true);
	}

	public void updateParValueComponents () {
		GameManager tGameManager;
		boolean tAuctionOver;
		Player tWinningPlayer;
		String tWinningName;

		tGameManager = auctionRound.getGameManager ();
		if (freeCertificate != Certificate.NO_CERTIFICATE) {
			if (freeCertificate.isPresidentShare ()) {
				tAuctionOver = certificateToAuction.auctionIsOver ();
				if (tAuctionOver) {
					tWinningPlayer = getHighestBidder ();
					tWinningName = tWinningPlayer.getName ();
					if (tGameManager.isNetworkGame ()) {
						if (tGameManager.isNetworkAndIsThisClient (tWinningName)) {
							updateParValueForWinner ();
						} else {
							parValuesCombo.setEnabled (false);
							parValuesCombo.setToolTipText (WON_NOT_YOU);
							setParPrice.setEnabled (false);
							setParPrice.setToolTipText (WON_NOT_YOU);
						}
					} else {
						updateParValueForWinner ();
					}
				} else {
					parValuesCombo.setEnabled (false);
					parValuesCombo.setToolTipText (MUST_SET_PAR_PRICE);
					setParPrice.setEnabled (false);
					setParPrice.setToolTipText (MUST_SET_PAR_PRICE);
				}
			}
		}
	}
	
	private void updateParValueForWinner () {
		parValuesCombo.setEnabled (true);
		if (getParPrice () > 0) {
			setParPrice.setEnabled (true);
			setParPrice.setToolTipText (GUI.EMPTY_STRING);
		} else {
			setParPrice.setEnabled (false);
			setParPrice.setToolTipText ("Par Price has not been selected yet");
		}
	}

	public int getParPrice () {
		int tParPrice = 0;
		String tParPriceString;

		tParPriceString = (String) parValuesCombo.getSelectedItem ();
		if (!Certificate.NO_PAR_PRICE.equals (tParPriceString)) {
			tParPrice = Integer.parseInt (tParPriceString);
		}

		return tParPrice;
	}

	public void setCertificateToAuction (Certificate aCertificateToAuction) {
		certificateToAuction = aCertificateToAuction;
	}

	public void setFreeCertificate (Certificate aFreeCertificate) {
		freeCertificate = aFreeCertificate;
	}

	public void updateOneBidderJPanel (int aBidderIndex, String aRaiseLabel) {
		oneBidderJPanel = new JPanel ();
		oneBidderJPanel.setLayout (new BoxLayout (oneBidderJPanel, BoxLayout.X_AXIS));
		oneBidderJPanel.add (Box.createHorizontalStrut (15));
		oneBidderJPanel.add (bidderLabels [aBidderIndex]);
		oneBidderJPanel.add (Box.createHorizontalStrut (5));
		bidderRaiseButtons [aBidderIndex] = new KButton (aRaiseLabel);
		bidderRaiseButtons [aBidderIndex].addActionListener (this);
		bidderRaiseButtons [aBidderIndex].setActionCommand (RAISE);

		bidderRaiseByButtons [aBidderIndex] = new KButton (aRaiseLabel);
		bidderRaiseByButtons [aBidderIndex].addActionListener (this);
		bidderRaiseByButtons [aBidderIndex].setActionCommand (RAISE_BY);

		oneBidderJPanel.add (bidderRaiseButtons [aBidderIndex]);
		oneBidderJPanel.add (Box.createHorizontalStrut (5));
		bidderPassButtons [aBidderIndex] = new KButton (PASS);
		bidderPassButtons [aBidderIndex].addActionListener (this);
		bidderPassButtons [aBidderIndex].setActionCommand (PASS);
		oneBidderJPanel.add (bidderPassButtons [aBidderIndex]);
	}

	public void updateAuctionUndoButton () {
		String tClientName;
		GameManager tGameManager;
		boolean tAmIBidder;

		tGameManager = auctionRound.getGameManager ();
		tClientName = tGameManager.getClientUserName ();
		tAmIBidder = certificateToAuction.amIABidder (tClientName);
		if (isNetworkGame) {
			if (tAmIBidder) {
				if (isBidderActing ()) {
						undoButton.setEnabled (true);
						undoButton.setToolTipText (ACTIVE_BIDDER_CAN_UNDO);
				} else {
					undoButton.setEnabled (false);
					undoButton.setToolTipText (NOT_ACTIVE_CANNOT_UNDO);				
				}
			} else {
				undoButton.setEnabled (false);
				undoButton.setToolTipText (NOT_ACTIVE_CANNOT_UNDO);
			}
		} else {
			undoButton.setEnabled (true);
			undoButton.setToolTipText (GUI.EMPTY_STRING);
		}
	}

	public void updateBidderJPanels () {
		Player tPlayer;
		int tBidAmount;
		int tBidderCount;
		int tHighestBidderIndex;
		String tRaiseLabel;
		String tBidderName;
		boolean tBidderIsActing;
		boolean tBidderHasPassed;

		tBidderCount = certificateToAuction.getNumberOfBidders ();
		tHighestBidderIndex = certificateToAuction.getHighestBidderIndex ();

		for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
			setBidderSuffixLabel (tBidderCount, tBidderIndex, tHighestBidderIndex);
			tPlayer = (Player) certificateToAuction.getCashHolderAt (tBidderIndex);
			tBidderName = tPlayer.getName ();
			tBidderIsActing = isBidderActing (tBidderIndex, tBidderName);
			tBidderHasPassed = hasBidderPassed (tBidderIndex, tBidderName);
			tBidAmount = certificateToAuction.getBidAt (tBidderIndex);
			bidderLabels [tBidderIndex].setText (getBidderLabel (tPlayer, tBidAmount));
			tRaiseLabel = RAISE + " " + Bank.formatCash (PlayerManager.BID_INCREMENT);
			updateAuctionUndoButton ();

			if (tBidderIndex == tHighestBidderIndex) {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, false, tBidderIsActing, HIGHEST_NO_RAISE);
				setButton (bidderPassButtons [tBidderIndex], PASS, false, tBidderIsActing, HIGHEST_NO_PASS);
			} else if (tBidderHasPassed) {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, false, tBidderIsActing, BIDDER_HAS_PASSED);
				setButton (bidderPassButtons [tBidderIndex], PASS, false, tBidderIsActing, BIDDER_HAS_PASSED);
			} else {
				setButton (bidderRaiseButtons [tBidderIndex], tRaiseLabel, true, tBidderIsActing, NOT_HIGHEST);
				setButton (bidderPassButtons [tBidderIndex], PASS, true, tBidderIsActing, NOT_HIGHEST);
			}
		}
	}

	private boolean isBidderActing () {
		boolean tBidderIsActing;
		int tBidderCount;
		int tBidderIndex;
		Bidder tBidder;
		String tBidderName;

		tBidderIsActing = true;
		tBidderCount = certificateToAuction.getNumberOfBidders ();
		if (isNetworkGame) {
			for (tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				tBidder = certificateToAuction.getBidderAt (tBidderIndex);
				tBidderName = tBidder.getName ();
				if (tBidderName.equals (clientUserName)) {
					tBidderIsActing = ! tBidder.hasActed ();
				}
			}
		}

		return tBidderIsActing;
	}

	private boolean isBidderActing (int aBidderIndex, String aBidderName) {
		boolean tIsBidderActing;

		tIsBidderActing = false;
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

	private boolean hasBidderPassed (int aBidderIndex, String aBidderName) {
		boolean tHasBidderPassed;

		tHasBidderPassed = false;
		if (PASSED.equals (bidderSuffixLabel [aBidderIndex].getText ())) {
			tHasBidderPassed = true;
		}

		if (isNetworkGame) {
			if (!aBidderName.equals (clientUserName)) {
				tHasBidderPassed = false;
			}
		}

		return tHasBidderPassed;
	}

	private void setBidderSuffixLabel (int aBidderCount, int aBidderIndex, int aHighestBidderIndex) {
		String tSuffixLabel;
		Player tPlayer;
		ActorI.ActionStates tLastAction;

		tSuffixLabel = HIGHEST;
		if (aBidderCount > 0) {
			if (aBidderCount == 1) {
				tSuffixLabel = ONLY_BIDDER;
				onlyBidder = true;
			} else if (aBidderIndex == aHighestBidderIndex) {
				tSuffixLabel = HIGHEST;
			} else {
				tPlayer = (Player) certificateToAuction.getCashHolderAt (aBidderIndex);
				tLastAction = tPlayer.getAuctionActionState ();
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
		int tPlayerCash;

		tPlayerCash = aPlayer.getCash ();
		tBidderLabel = "Bidder: " + aPlayer.getName () + " Treasury " + Bank.formatCash (tPlayerCash) + " Bid Amount "
				+ Bank.formatCash (aCash);

		return tBidderLabel;
	}

	public void setAuctionRound (AuctionRound aAuctionRound) {
		auctionRound = aAuctionRound;
		if (auctionRound == AuctionRound.NO_AUCTION_ROUND) {
			System.err.println ("OOPs, Auction Round Not set yet");
		}
	}

	private void setButton (KButton aButton, String aLabel, boolean aVisible, boolean aEnabled, String aToolTip) {
		aButton.setText (aLabel);
		aButton.setVisible (aVisible);
		aButton.setEnabled (aEnabled);
		aButton.setToolTipText (aToolTip);
	}
}