package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class PayCashDividendEffect extends CashTransferEffect {
	public final static String NAME = "Pay Cash Dividend";
	public final static AttributeName AN_OPERATING_ROUND_ID = new AttributeName ("operatingRoundID");
	String operatingRoundID;
	
	public PayCashDividendEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount, String tOperatingRoundID) {
		super (NAME, aFromActor, aToActor, aCashAmount);
		setOperatingRoundID (tOperatingRoundID);
	}

	public PayCashDividendEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (NAME, aEffectNode, aGameManager);
		
		String tOperatingRoundID;
		
		tOperatingRoundID = aEffectNode.getThisAttribute (AN_OPERATING_ROUND_ID);
		setOperatingRoundID (tOperatingRoundID);
	}

	public void setOperatingRoundID (String tOperatingRoundID) {
		operatingRoundID = tOperatingRoundID;
	}
	
	public String getOperatingRoundID () {
		return operatingRoundID;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OPERATING_ROUND_ID, getOperatingRoundID ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (cash) + " from " + getActorName () + " to "
				+ getToActorName () + " in Operating Round " + operatingRoundID + ".");
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		CashHolderI tToCashHolder;

		tEffectApplied = false;
		tToCashHolder = (CashHolderI) getToActor ();
		tToCashHolder.addCashToDividends (cash, operatingRoundID);
		super.applyEffect (aRoundManager);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		CashHolderI tToCashHolder;

		tEffectUndone = false;
		tToCashHolder = (CashHolderI) getToActor ();
		tToCashHolder.addCashToDividends (-cash, operatingRoundID);
		super.undoEffect (aRoundManager);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
