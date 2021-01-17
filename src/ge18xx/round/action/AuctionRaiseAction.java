package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.AuctionBidChangeEffect;
import ge18xx.round.action.effects.AuctionStateChangeEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.EscrowChangeEffect;
import ge18xx.round.action.effects.NewCurrentPlayerEffect;
import ge18xx.utilities.XMLNode;

public class AuctionRaiseAction extends CashTransferAction {
	public final static String NAME = "Auction Raise";	

	public AuctionRaiseAction () {
		super ();
		setName (NAME);
	}
	
	public AuctionRaiseAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public AuctionRaiseAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addNewCurrentBidderEffect (ActorI aPlayer, int aCurrentBidderIndex, int aNextBidderIndex) {
		NewCurrentPlayerEffect tNewCurrentBidderEffect;

		tNewCurrentBidderEffect = new NewCurrentPlayerEffect (aPlayer, aCurrentBidderIndex, aNextBidderIndex);
		addEffect (tNewCurrentBidderEffect);
	}
	
	public void addAuctionStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, 
			ActorI.ActionStates aNewState) {
		AuctionStateChangeEffect tAuctionStateChangeEffect;

		tAuctionStateChangeEffect = new AuctionStateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionStateChangeEffect);
	}

	public void addBidChangeEffect (ActorI aActor, int aOldBid, int aNewBid, Certificate aCertificate) {
		AuctionBidChangeEffect tAuctionBidChangeEffect;
		
		tAuctionBidChangeEffect = new AuctionBidChangeEffect (aActor, aOldBid, aNewBid, aCertificate);
		addEffect (tAuctionBidChangeEffect);
	}
	
	public void addEscrowChangeEffect (ActorI aActor, int aOldEscrow, int aNewEscrow) {
		EscrowChangeEffect tEscrowChangeEffect;
		
		tEscrowChangeEffect = new EscrowChangeEffect (aActor, aOldEscrow, aNewEscrow);
		addEffect (tEscrowChangeEffect);
	}
	
	public String getCompanyAbbrev () {
		String tCompanyAbbrev = "";
		
		for (Effect tEffect : effects) {
			if (tCompanyAbbrev.equals ("")) {
				if (tEffect instanceof AuctionBidChangeEffect) {
					tCompanyAbbrev = ((AuctionBidChangeEffect) tEffect).getCertificate ().getCompanyAbbrev ();
				}
			}
		}
		
		return tCompanyAbbrev;
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		int tNewEscrow = 0;
		
		for (Effect tEffect : effects) {
			if (tNewEscrow == 0) {
				if (tEffect instanceof EscrowChangeEffect) {
					tNewEscrow = ((EscrowChangeEffect) tEffect).getNewEscrowAmount ();
				}
			}
		}
	
		tSimpleActionReport = actor.getName () + " raised the bid by " + Bank.formatCash (getCashAmount ()) +
				" to " + Bank.formatCash(tNewEscrow) + " for " + getCompanyAbbrev () + ".";
		
		return tSimpleActionReport;
	}
}
