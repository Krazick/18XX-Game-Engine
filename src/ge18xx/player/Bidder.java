package ge18xx.player;

import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class Bidder implements ActorI {
	public static final ElementName EN_BIDDER = new ElementName ("Bidder");
	public static final AttributeName AN_CASH = new AttributeName ("cash");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	CashHolderI cashHolder;
	int amount;
	ActionStates auctionActionState;
	
	public Bidder (CashHolderI aBidder, int aAmount) {
		cashHolder = aBidder;
		setAmount (aAmount);
		auctionActionState = ActorI.ActionStates.AuctionRaise;
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
		return auctionActionState.toString ();
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (EN_BIDDER);
		tXMLElement.setAttribute (AN_CASH, amount);
		tXMLElement.setAttribute (AN_NAME, cashHolder.getName ());

		return tXMLElement;
	}

	public boolean hasPassed () {
		return (auctionActionState == ActorI.ActionStates.AuctionPass);
	}
	
	public void passBid () {
		setAuctionActionState (ActorI.ActionStates.AuctionPass);
	}
	
	public void raiseBid (Certificate aCertificate, int aRaise) {
		Player tPlayer = (Player) cashHolder;
		
		setAmount (amount + aRaise);
		tPlayer.raiseBid (aCertificate, aRaise);
		setAuctionActionState (ActorI.ActionStates.AuctionRaise);
	}

	public void setAuctionActionState (ActionStates aActionState) {
		auctionActionState = aActionState;
	}

	@Override
	public boolean isAPrivateCompany () {
		return false;
	}
	
	@Override
	public boolean isAPlayer () {
		return false;
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for the Bidder State
	}
	
	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	@Override
	public boolean isABank () {
		return false;
	}

	@Override
	public boolean isACorporation () {
		return false;
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
}
