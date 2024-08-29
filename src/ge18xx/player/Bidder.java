package ge18xx.player;

import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public class Bidder implements ActorI {
	public static final ElementName EN_BIDDER = new ElementName ("Bidder");
	CashHolderI cashHolder;
	int amount;

	public Bidder (CashHolderI aBidder, int aAmount) {
		Player tPlayer;
		
		if (aBidder.isAPlayer ()) {
			setCashHolder (aBidder);
			setAmount (aAmount);
			tPlayer = (Player) cashHolder;
			tPlayer.setAuctionActionState (ActorI.ActionStates.AuctionRaise);
		} else {
			System.err.println ("Bidder is not a Player, can't set Auction State.");
		}
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_BIDDER);
		tXMLElement.setAttribute (Player.AN_CASH, amount);
		tXMLElement.setAttribute (Player.AN_NAME, cashHolder.getName ());

		return tXMLElement;
	}

	private void setCashHolder (CashHolderI aBidder) {
		cashHolder = aBidder;
	}

	public CashHolderI getCashHolder () {
		return cashHolder;
	}

	public int getAmount () {
		return amount;
	}

	public void setAmount (int aAmount) {
		amount = aAmount;
	}

	@Override
	public String getName () {
		return cashHolder.getName ();
	}

	@Override
	public String getStateName () {
		Player tPlayer;
		String tStateName;
		
		tPlayer = (Player) cashHolder;
		tStateName = tPlayer.auctionActionState.toString ();
		
		return tStateName;
	}

	public boolean hasPassed () {
		Player tPlayer;
		boolean tHasPassed;
		
		tPlayer = (Player) cashHolder;
		
		tHasPassed = (tPlayer.getAuctionActionState () == ActorI.ActionStates.AuctionPass);
		
		return tHasPassed;
	}

	public void passBid () {
		Player tPlayer;
		
		tPlayer = (Player) cashHolder;
		tPlayer.setAuctionActionState (ActorI.ActionStates.AuctionPass);
	}

	public void raiseBid (Certificate aCertificate, int aRaise) {
		Player tPlayer;

		tPlayer = (Player) cashHolder;
		setAmount (amount + aRaise);
		tPlayer.raiseBid (aCertificate, aRaise);
		tPlayer.setAuctionActionState (ActorI.ActionStates.AuctionRaise);
	}

	public boolean hasActed () {
		boolean tHasActed;
		Player tPlayer;
		ActorI.ActionStates tAuctionState;
		
		tPlayer = (Player) getCashHolder ();
		tAuctionState = tPlayer.getAuctionActionState ();
		if ((tAuctionState == ActorI.ActionStates.NoAction) ||
			(tAuctionState == ActorI.ActionStates.Bidder)) {
			tHasActed = false;
		} else {
			tHasActed = true;
		}

		return tHasActed;
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
}
