package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.WinAuctionAction;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class Bidders {
	public static ElementName EN_BIDDERS = new ElementName ("Bidders");
	public static final XMLElement NO_XML_BIDDERS = null;
	public static final Bidders NO_BIDDERS = null;
	public static final int NO_BIDDER = 0;
	List<Bidder> bidders;
	Certificate certificate;

	public Bidders (Certificate aCertificate) {
		bidders = new LinkedList<> ();
		certificate = aCertificate;
	}

	public void addBidderInfo (CashHolderI aCashHolder, int aAmount) {
		Bidder tBidder;

		tBidder = new Bidder (aCashHolder, aAmount);
		bidders.add (tBidder);
	}

	public boolean hasBidOnThisCert (Player aPlayer) {
		String tPlayerName;

		tPlayerName = aPlayer.getName ();
		return hasBidOnThisCert (tPlayerName);
	}

	public boolean hasBidOnThisCert (String aPlayerName) {
		boolean tPlayerAlreadyBid = false;
		int tBidderCount;
		CashHolderI tThisBidder;

		tBidderCount = bidders.size ();
		if (tBidderCount > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				tThisBidder = getCashHolderAt (tBidderIndex);

				if (aPlayerName.equals (tThisBidder.getName ())) {
					tPlayerAlreadyBid = true;
				}
			}
		}

		return tPlayerAlreadyBid;
	}

	public int getNumberOfBidders () {
		return bidders.size ();
	}

	public String getBidderNames () {
		String tBidderNames = "";

		for (Bidder tBidder : bidders) {
			if (tBidderNames.length () > 0) {
				tBidderNames += ", ";
			}
			tBidderNames += tBidder.getName ();
		}

		return tBidderNames;
	}

	public XMLElement getOnlyBiddersElement (XMLDocument aXMLDocument) {
		XMLElement tXMLBidders = NO_XML_BIDDERS;
		XMLElement tXMLBidderElement;

		if (bidders.size () > 0) {
			tXMLBidders = aXMLDocument.createElement (EN_BIDDERS);
			for (Bidder tBidder : bidders) {
				tXMLBidderElement = tBidder.getElements (aXMLDocument);
				tXMLBidders.appendChild (tXMLBidderElement);
			}
		}

		return tXMLBidders;
	}

	public Bidder getBidderAt (int aIndex) {
		return bidders.get (aIndex);
	}

	public boolean hasBidderAtActed (int aIndex) {
		Bidder tBidder;
		boolean tHasActed;

		tBidder = getBidderAt (aIndex);
		tHasActed = tBidder.hasActed ();

		return tHasActed;
	}

	public CashHolderI getCashHolderAt (int aIndex) {
		Bidder tBidder;
		CashHolderI tCashHolder;

		if (bidders.size () > 0) {
			tBidder = getBidderAt (aIndex);
			tCashHolder = tBidder.getCashHolder ();
		} else {
			tCashHolder = (CashHolderI) ActorI.NO_ACTOR;
		}

		return tCashHolder;
	}

	public int getBidAt (int aIndex) {
		Bidder tBidder;

		tBidder = bidders.get (aIndex);

		return tBidder.getAmount ();
	}

	public int getHighestBid () {
		int tHighestBid = certificate.getValue ();
		int tNumberOfBidders = getNumberOfBidders ();
		int tBidAt;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidAt = getBidAt (tBidderIndex);
				if (tBidAt > tHighestBid) {
					tHighestBid = tBidAt;
				}
			}
		}

		return tHighestBid;
	}

	public int getLowestBidderIndex () {
		int tLowestBid = certificate.getValue ();
		int tNumberOfBidders = getNumberOfBidders ();
		int tBidAt;
		int tLowestBidderIndex = NO_BIDDER;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidAt = getBidAt (tBidderIndex);
				if (tBidAt < tLowestBid) {
					tLowestBid = tBidAt;
					tLowestBidderIndex = tBidderIndex;
				}
			}
		}

		return tLowestBidderIndex;
	}

	public int getHighestBidderIndex () {
		int tHighestBid = certificate.getValue ();
		int tNumberOfBidders = getNumberOfBidders ();
		int tBidAt;
		int tHighestBidderIndex = NO_BIDDER;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidAt = getBidAt (tBidderIndex);
				if (tBidAt > tHighestBid) {
					tHighestBid = tBidAt;
					tHighestBidderIndex = tBidderIndex;
				}
			}
		}

		return tHighestBidderIndex;
	}

	public int getRaiseAmount (int aBidderIndex) {
		int tRaiseAmount;
		int tCurrentBid = getBidAt (aBidderIndex);
		int tHighestBid = getHighestBid ();

		tRaiseAmount = (tHighestBid + PlayerManager.BID_INCREMENT) - tCurrentBid;

		return tRaiseAmount;
	}

	public boolean hasBidders () {
		return (bidders.size () > 0);
	}

	public boolean auctionIsOver () {
		boolean tAuctionIsOver;
		int tBidderPassCount;

		tAuctionIsOver = false;
		tBidderPassCount = 0;
		if (getNumberOfBidders () > 1) {
			for (Bidder tBidder : bidders) {
				if (tBidder.hasPassed ()) {
					tBidderPassCount++;
				}
			}
			if ((tBidderPassCount + 1) == getNumberOfBidders ()) {
				tAuctionIsOver = true;
			}

		} else {
			tAuctionIsOver = true;
		}

		return tAuctionIsOver;
	}

	public void removeBidder (CashHolderI aCashHolder) {
		int tNumberOfBidders = getNumberOfBidders ();
		CashHolderI tBidder;
		Player tPlayer;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = getCashHolderAt (tBidderIndex);
				if (tBidder.equals (aCashHolder)) {
					bidders.remove (tBidderIndex);
					tPlayer = (Player) tBidder;
					tPlayer.updatePlayerJPanel ();
				}
			}
		}
	}

	public void passBidFor (int aBidderIndex) {
		Bidder tBidder = bidders.get (aBidderIndex);
		tBidder.passBid ();
	}

	public void raiseBidFor (int aBidderIndex) {
		int tRaiseAmount = getRaiseAmount (aBidderIndex);
		Bidder tBidder = bidders.get (aBidderIndex);

		tBidder.raiseBid (certificate, tRaiseAmount);
	}

	public void refundBids (WinAuctionAction aWinAuctionAction) {
		int tNumberOfBidders = getNumberOfBidders ();
		int tBid;
		Player tBidder;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (0);
				tBid = getBidAt (0);
				tBidder.refundEscrow (certificate, tBid, aWinAuctionAction);
				bidders.remove (0);
				tBidder.updatePlayerJPanel ();
			}
		}
	}

	public void removeAllBids () {
		int tNumberOfBidders = getNumberOfBidders ();

		if (tNumberOfBidders > 0) {
			bidders.clear ();
		}
	}

	public void setBidAt (int aIndex, int aAmount) {
		Bidder tBidder;

		tBidder = bidders.get (aIndex);
		tBidder.setAmount (aAmount);
	}

	public void setBiddersAsRaiseBid () {
		int tNumberOfBidders = getNumberOfBidders ();
		Player tBidder;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (tBidderIndex);
				tBidder.setAuctionActionState (ActorI.ActionStates.AuctionRaise);
			}
		}
	}

	public void setAsPassForBidder (Player aPlayer) {
		int tNumberOfBidders = getNumberOfBidders ();
		Player tBidder;
		Bidder tAsBidder;
		int tBidderIndex;

		if (tNumberOfBidders > 0) {
			for (tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tAsBidder = bidders.get (tBidderIndex);
				tBidder = (Player) getCashHolderAt (tBidderIndex);
				if (tBidder.equals (aPlayer)) {
					tBidder.setAuctionActionState (ActorI.ActionStates.AuctionPass);
					tAsBidder.passBid ();
				}
			}
		}
	}

	public int getCount () {
		return bidders.size ();
	}

	public void printAllBidderEscrows () {
		int tNumberOfBidders = getNumberOfBidders ();
		Player tBidder;

		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (tBidderIndex);
				tBidder.printAllEscrows ();
				System.out.println ("Player " + tBidder.getName () + " cash " + Bank.formatCash (tBidder.getCash ()));
			}
		}
	}

	public int getTotalEscrows () {
		int tTotalEscrows = 0;
		int tBidderCount;

		tBidderCount = bidders.size ();
		if (tBidderCount > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				tTotalEscrows += getBidAt (tBidderIndex);
			}
		}

		return tTotalEscrows;
	}

	public void addBidderInfo (XMLNode aBiddersNode) {
		XMLNodeList tXMLBiddersNodeList;

		tXMLBiddersNodeList = new XMLNodeList (bidderParsingRoutine);
		tXMLBiddersNodeList.parseXMLNodeList (aBiddersNode, Bidder.EN_BIDDER);
	}

	ParsingRoutineI bidderParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aBidderNode) {
			String tBidderName;
			int tCash;
			CashHolderI tCashHolder;

			tBidderName = aBidderNode.getThisAttribute (Bidder.AN_NAME);
			tCash = aBidderNode.getThisIntAttribute (Bidder.AN_CASH);
			if (!hasBidOnThisCert (tBidderName)) {
				tCashHolder = certificate.getCashHolderByName (tBidderName);
				if (tCashHolder != ActorI.NO_ACTOR) {
					addBidderInfo (tCashHolder, tCash);
				} else {
					System.err.println ("Failed to Find Bidder named " + tBidderName);
				}
			}
		}
	};
}
