package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.AuctionBidChangeEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.EscrowChangeEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class AuctionRaiseAction extends AuctionStateChangeAction {
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
		String tCompanyAbbrev = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tCompanyAbbrev.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof AuctionBidChangeEffect) {
					tCompanyAbbrev = ((AuctionBidChangeEffect) tEffect).getCertificate ().getCompanyAbbrev ();
				}
			}
		}

		return tCompanyAbbrev;
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		int tNewBid;

		tNewBid = 0;
		tSimpleActionReport = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tNewBid == 0) {
				if (tEffect instanceof AuctionBidChangeEffect) {
					tNewBid = ((AuctionBidChangeEffect) tEffect).getNewBid ();
				}
			}
		}

		tSimpleActionReport = actor.getName () + " raised the bid by " + 
				Bank.formatCash (getCashAmount ()) + " to "
				+ Bank.formatCash (tNewBid) + " for " + getCompanyAbbrev () + ".";

		return tSimpleActionReport;
	}

	@Override
	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied = super.applyAction (aRoundManager);

		Certificate tCertificate = Certificate.NO_CERTIFICATE;

		for (Effect tEffect : effects) {
			if (tCertificate == Certificate.NO_CERTIFICATE) {
				if (tEffect instanceof AuctionBidChangeEffect) {
					tCertificate = ((AuctionBidChangeEffect) tEffect).getCertificate ();
				}
			}
		}

		return tActionApplied;
	}
}
