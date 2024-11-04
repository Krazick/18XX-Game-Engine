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

public class ClearRoundDividendEffect extends Effect {
	public final static String NAME = "Clear Round Dividend";
	final static AttributeName AN_PREVIOUS_AMOUNT = new AttributeName ("previousAmount");
	final static AttributeName AN_OPERATING_ROUND_ID = new AttributeName ("operatingRoundID");
	int previousAmount;
	String operatingRoundID;

	public ClearRoundDividendEffect (ActorI aActor, int aPreviousAmount, String aOperatingRoundID) {
		super (NAME, aActor);
		setPreviousAmount (aPreviousAmount);
		setOperatingRoundID (aOperatingRoundID);
	}

	public ClearRoundDividendEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		int tPreviousAmount;
		String tOperatingRoundID;
		
		tPreviousAmount = aEffectNode.getThisIntAttribute (AN_PREVIOUS_AMOUNT);
		tOperatingRoundID = aEffectNode.getThisAttribute (AN_OPERATING_ROUND_ID);
		setPreviousAmount (tPreviousAmount);
		setOperatingRoundID (tOperatingRoundID);
	}

	public void setPreviousAmount (int aPreviousAmount) {
		previousAmount = aPreviousAmount;
	}
	
	public int getPreviousAmount () {
		return previousAmount;
	}
	
	public void setOperatingRoundID (String aOperatingRoundID) {
		operatingRoundID = aOperatingRoundID;
	}
	
	public String getOperatingRoundID () {
		return operatingRoundID;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_PREVIOUS_AMOUNT, previousAmount);
		tEffectElement.setAttribute (AN_OPERATING_ROUND_ID, operatingRoundID);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + Bank.formatCash (previousAmount) + " from " + getActorName () + 
				 " earned during Operating Round " + operatingRoundID + ".");
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		CashHolderI tFromCashHolder;

		tEffectApplied = false;
		tFromCashHolder = (CashHolderI) getActor ();
		tFromCashHolder.clearRoundDividends (operatingRoundID);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		CashHolderI tFromCashHolder;

		tEffectUndone = false;
		tFromCashHolder = (CashHolderI) getActor ();
		tFromCashHolder.clearRoundDividends (operatingRoundID);
		tFromCashHolder.addCashToDividends (previousAmount, operatingRoundID);
		
		return tEffectUndone;
	}

}
