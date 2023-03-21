package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PayCashDividendEffect extends CashTransferEffect {
	public final static String NAME = "Pay Cash Dividend";
	public final static AttributeName AN_OPERATING_ROUND_PART2 = new AttributeName ("operatingRoundPart2");
	int operatingRoundPart2;
	
	public PayCashDividendEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount, int aOperatingRoundPart2) {
		super (NAME, aFromActor, aToActor, aCashAmount);
		setOperatingRoundPart2 (aOperatingRoundPart2);
	}

	public PayCashDividendEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (NAME, aEffectNode, aGameManager);
		
		int tOperatingRoundPart2;
		
		tOperatingRoundPart2 = aEffectNode.getThisIntAttribute (AN_OPERATING_ROUND_PART2);
		setOperatingRoundPart2 (tOperatingRoundPart2);
	}

	public void setOperatingRoundPart2 (int aOperatingRoundPart2) {
		operatingRoundPart2 = aOperatingRoundPart2;
	}
	
	public int getOperatingRoundPart2 () {
		return operatingRoundPart2;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OPERATING_ROUND_PART2, getOperatingRoundPart2 ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (cash) + " from " + getActorName () + " to "
				+ getToActorName () + " in Operating Round " + operatingRoundPart2 + ".");
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		CashHolderI tFromCashHolder;

		tEffectApplied = false;
		tFromCashHolder = (CashHolderI) getActor ();
		tFromCashHolder.addCashToDividends (cash, operatingRoundPart2);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		CashHolderI tFromCashHolder;

		tEffectUndone = false;
		tFromCashHolder = (CashHolderI) getActor ();
		tFromCashHolder.addCashToDividends (-cash, operatingRoundPart2);
		tEffectUndone = true;

		return tEffectUndone;
	}

}
