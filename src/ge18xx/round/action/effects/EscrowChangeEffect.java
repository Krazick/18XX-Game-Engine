package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.Escrow;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class EscrowChangeEffect extends Effect {
	public final static String NAME = "Auction Escrow Change";
	final static AttributeName AN_OLD_ESCROW = new AttributeName ("oldEscrow");
	final static AttributeName AN_NEW_ESCROW = new AttributeName ("newEscrow");
	public final static int NO_ESCROW = 0;
	int oldEscrow;
	int newEscrow;

	public EscrowChangeEffect () {
		super ();
		setName (NAME);
		setOldEscrow (NO_ESCROW);
		setNewEscrow (NO_ESCROW);
	}

	public EscrowChangeEffect (ActorI aFromActor, int aOldEscrow, int aNewEscrow) {
		super (NAME, aFromActor);
		setOldEscrow (aOldEscrow);
		setNewEscrow (aNewEscrow);
	}

	public EscrowChangeEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		int tOldEscrow, tNewEscrow;

		tOldEscrow = aEffectNode.getThisIntAttribute (AN_OLD_ESCROW);
		tNewEscrow = aEffectNode.getThisIntAttribute (AN_NEW_ESCROW);
		setOldEscrow (tOldEscrow);
		setNewEscrow (tNewEscrow);
//		System.out.println ("Parsing Escrow Old " + oldEscrow + " New " + newEscrow);
	}

	public int getOldEscrowAmount () {
		return oldEscrow;
	}

	public int getNewEscrowAmount () {
		return newEscrow;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OLD_ESCROW, oldEscrow);
		tEffectElement.setAttribute (AN_NEW_ESCROW, newEscrow);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " from " + Bank.formatCash (oldEscrow) + " to "
				+ Bank.formatCash (newEscrow) + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setOldEscrow (int aOldEscrow) {
		oldEscrow = aOldEscrow;
	}

	public void setNewEscrow (int aNewEscrow) {
		newEscrow = aNewEscrow;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Escrow tEscrow;
		int tBefore = 0;

		tEffectApplied = false;
		tEscrow = aRoundManager.getEscrowMatching (actor.getName ());
		if (tEscrow != Escrow.NO_ESCROW) {
			tBefore = tEscrow.getCash ();
			tEscrow.setCash (newEscrow);
			tEffectApplied = true;
			System.out.println (
					"=== Effect " + name + " Current Escrow Value Before " + tBefore + " After " + tEscrow.getCash ());
		} else {
			System.err.println ("No Escrow found for " + actor.getName ());
		}
		aRoundManager.revalidateAuctionFrame ();

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Escrow tEscrow;

		tEffectUndone = false;
		tEscrow = aRoundManager.getEscrowMatching (actor.getName ());
		if (tEscrow != Escrow.NO_ESCROW) {
			tEscrow.setCash (oldEscrow);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
