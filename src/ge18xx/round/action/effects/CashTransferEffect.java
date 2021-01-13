package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLDocument;

public class CashTransferEffect extends Effect {
	public final static String NAME = "Cash Transfer";
	final static AttributeName AN_CASH = new AttributeName ("cash");
	public final static int NO_CASH = 0;
	int cash;
	ActorI toActor;
	
	public CashTransferEffect () {
		this (NAME);
	}

	public CashTransferEffect (String aName) {
		super (aName);
		setCash (NO_CASH);
		setToActor (ActorI.NO_ACTOR);
	}
	
	public CashTransferEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount) {
		super (NAME, aFromActor);
		setCash (aCashAmount);
		setToActor (aToActor);
	}

	public CashTransferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		int tCashAmount;
		String tToActorName;
		ActorI tToActor;
		
		tCashAmount = aEffectNode.getThisIntAttribute (AN_CASH);
		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tToActor = aGameManager.getActor (tToActorName);
		setCash (tCashAmount);
		setToActor (tToActor);
	}

	public int getCash () {
		return cash;
	}
	
	public ActorI getToActor () {
		return toActor;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_CASH, getCash ());
		if (toActor.isACorporation ()) {
			tActorName = ((Corporation) toActor).getAbbrev ();
		} else {
			tActorName = toActor.getName ();
		}
		tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, tActorName);
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (cash) + 
				" from " +  actor.getName () + " to " +  toActor.getName () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCash (int aCash) {
		cash = aCash;
	}
	
	public void setToActor (ActorI aToActor) {
		toActor = aToActor;
	}
	
	public int getEffectDebit (String aActorName) {
		int tDebit = 0;
		
		if (aActorName.equals (actor.getName ())) {
			tDebit = cash;
		}
		
		return tDebit;
	}
	
	public int getEffectCredit (String aActorName) {
		int tCredit = 0;
		
		if (aActorName.equals (toActor.getName ())) {
			tCredit = cash;
		}
		
		return tCredit;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		CashHolderI tToCashHolder, tFromCashHolder;
		
		tEffectApplied = false;
		tToCashHolder = (CashHolderI) toActor;
		tFromCashHolder = (CashHolderI) actor;
		tFromCashHolder.transferCashTo (tToCashHolder, cash);
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		CashHolderI tToCashHolder, tFromCashHolder;
		
		tEffectUndone = false;
		tToCashHolder = (CashHolderI) toActor;
		tFromCashHolder = (CashHolderI) actor;
		tFromCashHolder.transferCashTo (tToCashHolder, -cash);
		tEffectUndone = true;
		
		return tEffectUndone;
	}
	

	public String getToActorName () {
		return toActor.getName ();
	}

}
