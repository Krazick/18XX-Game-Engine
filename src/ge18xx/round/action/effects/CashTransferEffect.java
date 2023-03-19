package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.AuditFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class CashTransferEffect extends ToEffect {
	public final static String NAME = "Cash Transfer";
	final static AttributeName AN_CASH = new AttributeName ("cash");
	public final static int NO_CASH = 0;
	int cash;

	public CashTransferEffect () {
		this (NAME);
	}

	public CashTransferEffect (String aName) {
		super (aName);
		setCash (NO_CASH);
		setToActor (ActorI.NO_ACTOR);
	}

	public CashTransferEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount) {
		this (NAME, aFromActor, aToActor, aCashAmount);
	}
	
	public CashTransferEffect (String aName, ActorI aFromActor, ActorI aToActor, int aCashAmount) {
		super (aName, aFromActor, aToActor);
		setCash (aCashAmount);
	}

	public CashTransferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		this (NAME, aEffectNode, aGameManager);
	}
	
	public CashTransferEffect (String aName, XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (aName);

		int tCashAmount;

		tCashAmount = aEffectNode.getThisIntAttribute (AN_CASH);
		setCash (tCashAmount);
	}

	public int getCash () {
		return cash;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_CASH, getCash ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (cash) + " from " + getActorName () + " to "
				+ getToActorName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCash (int aCash) {
		cash = aCash;
	}

	public int getEffectDebit (String aActorName) {
		int tDebit = AuditFrame.NO_DEBIT;

		if (isActor (aActorName)) {
			tDebit = cash;
		}

		return tDebit;
	}

	public int getEffectCredit (String aActorName) {
		int tCredit = AuditFrame.NO_CREDIT;

		if (isToActor (aActorName)) {
			tCredit = cash;
		}

		return tCredit;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		CashHolderI tToCashHolder;
		CashHolderI tFromCashHolder;

		tEffectApplied = false;
		tToCashHolder = (CashHolderI) getToActor ();
		tFromCashHolder = (CashHolderI) getActor ();
		tFromCashHolder.transferCashTo (tToCashHolder, cash);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		CashHolderI tToCashHolder;
		CashHolderI tFromCashHolder;

		tEffectUndone = false;
		tToCashHolder = (CashHolderI) getToActor ();
		tFromCashHolder = (CashHolderI) getActor ();
		tFromCashHolder.transferCashTo (tToCashHolder, -cash);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
