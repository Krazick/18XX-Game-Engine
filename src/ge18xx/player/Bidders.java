package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.WinAuctionAction;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

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

	public XMLElement getOnlyBiddersElement (XMLDocument aXMLDocument) {
		XMLElement tXMLBidders;
		XMLElement tXMLBidderElement;

		tXMLBidders = NO_XML_BIDDERS;
		if (bidders.size () > 0) {
			tXMLBidders = aXMLDocument.createElement (EN_BIDDERS);
			for (Bidder tBidder : bidders) {
				tXMLBidderElement = tBidder.getElements (aXMLDocument);
				tXMLBidders.appendChild (tXMLBidderElement);
			}
		}

		return tXMLBidders;
	}
	
	public void addBidderInfo (XMLNode aBiddersNode) {
		XMLNodeList tXMLBiddersNodeList;

		tXMLBiddersNodeList = new XMLNodeList (bidderParsingRoutine);
		tXMLBiddersNodeList.parseXMLNodeList (aBiddersNode, Bidder.EN_BIDDER);
	}

	ParsingRoutineI bidderParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aBidderNode) {
			CashHolderI tCashHolder;
			String tBidderName;
			int tCash;

			tBidderName = aBidderNode.getThisAttribute (Player.AN_NAME);
			tCash = aBidderNode.getThisIntAttribute (Player.AN_CASH);
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

	public void addBidderInfo (CashHolderI aCashHolder, int aAmount) {
		Bidder tBidder;

		tBidder = new Bidder (aCashHolder, aAmount);
		bidders.add (tBidder);
	}

	public boolean hasBidOnThisCert (Player aPlayer) {
		String tPlayerName;
		boolean tHasBidOnThisCert;
		
		tPlayerName = aPlayer.getName ();
		tHasBidOnThisCert = hasBidOnThisCert (tPlayerName);
		
		return tHasBidOnThisCert;
	}

	public boolean hasBidOnThisCert (String aPlayerName) {
		boolean tPlayerAlreadyBid;
		int tBidderCount;
		CashHolderI tThisBidder;

		tPlayerAlreadyBid = false;
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
		String tBidderNames;

		tBidderNames = GUI.EMPTY_STRING;
		for (Bidder tBidder : bidders) {
			if (tBidderNames.length () > 0) {
				tBidderNames += GUI.COMMA_SPACE;
			}
			tBidderNames += tBidder.getName ();
		}

		return tBidderNames;
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
		int tBidAt;
		int tHighestBid;
		int tNumberOfBidders;

		tHighestBid = certificate.getValue ();
		tNumberOfBidders = getNumberOfBidders ();
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
		int tBidAt;
		int tLowestBid;
		int tNumberOfBidders;
		int tLowestBidderIndex;
		
		tLowestBid = certificate.getValue ();
		tNumberOfBidders = getNumberOfBidders ();
		tLowestBidderIndex = NO_BIDDER;

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
		int tBidAt;
		int tHighestBid;
		int tNumberOfBidders;
		int tHighestBidderIndex;

		tHighestBid = certificate.getValue ();
		tNumberOfBidders = getNumberOfBidders ();
		tHighestBidderIndex = NO_BIDDER;
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
		int tCurrentBid;
		int tHighestBid ;

		tCurrentBid = getBidAt (aBidderIndex);
		tHighestBid = getHighestBid ();
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
		int tNumberOfBidders;
		CashHolderI tBidder;
		Player tPlayer;

		tNumberOfBidders = getNumberOfBidders ();
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
		Bidder tBidder;
		
		tBidder = bidders.get (aBidderIndex);
		tBidder.passBid ();
	}

	public void raiseBidFor (int aBidderIndex) {
		int tRaiseAmount;
		Bidder tBidder;
		
		tBidder = bidders.get (aBidderIndex);
		tRaiseAmount = getRaiseAmount (aBidderIndex);

		tBidder.raiseBid (certificate, tRaiseAmount);
	}

	public void refundBids (WinAuctionAction aWinAuctionAction) {
		int tNumberOfBidders;
		int tBid;
		Player tBidder;

		tNumberOfBidders = getNumberOfBidders ();
		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (0);
				tBid = getBidAt (0);
				tBidder.refundEscrow (certificate, tBid, aWinAuctionAction);
//				bidders.remove (0);
				tBidder.updatePlayerJPanel ();
			}
		}
	}

	public void removeAllBids () {
		int tNumberOfBidders;
		
		tNumberOfBidders = getNumberOfBidders ();
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
		int tNumberOfBidders;
		Player tBidder;

		tNumberOfBidders = getNumberOfBidders ();
		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (tBidderIndex);
				tBidder.setAuctionActionState (ActorI.ActionStates.AuctionRaise);
			}
		}
	}

	public void setAsPassForBidder (Player aPlayer) {
		int tNumberOfBidders;
		Player tBidder;
		Bidder tAsBidder;
		int tBidderIndex;
		
		tNumberOfBidders = getNumberOfBidders ();
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
		int tNumberOfBidders;
		Player tBidder;
		String tName;
		String tCash;
		
		tNumberOfBidders = getNumberOfBidders ();
		if (tNumberOfBidders > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tNumberOfBidders; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (tBidderIndex);
				tBidder.printAllEscrows ();
				tName = tBidder.getName ();
				tCash = Bank.formatCash (tBidder.getCash ());
				System.out.println ("Player " + tName + " cash " + tCash);	// PRINTLOG
			}
		}
	}

	public int getTotalEscrows () {
		int tTotalEscrows;
		int tBidderCount;

		tTotalEscrows = 0;
		tBidderCount = bidders.size ();
		if (tBidderCount > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				tTotalEscrows += getBidAt (tBidderIndex);
			}
		}

		return tTotalEscrows;
	}
}
