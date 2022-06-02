package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class GetLoanEffect extends Effect {
	final static AttributeName AN_OLD_LOAN_TAKEN = new AttributeName ("oldLoanTaken");
	final static AttributeName AN_NEW_LOAN_TAKEN = new AttributeName ("newLoanTaken");
	public final static String NAME = "Get Loan";
	boolean oldLoanTaken;
	boolean newLoanTaken;

	public GetLoanEffect () {
		super (NAME);
	}

	public GetLoanEffect (String aName) {
		super (aName);
	}

	public GetLoanEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public GetLoanEffect (ActorI aActor, boolean aOldLoanTaken, boolean aNewLoanTaken) {
		super (NAME, aActor);
		setOldLoanTaken (aOldLoanTaken);
		setNewLoanTaken (aNewLoanTaken);
	}

	public GetLoanEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		boolean tOldLoanTaken;
		boolean tNewLoanTaken;
		
		tOldLoanTaken = aEffectNode.getThisBooleanAttribute (AN_OLD_LOAN_TAKEN);
		tNewLoanTaken = aEffectNode.getThisBooleanAttribute (AN_NEW_LOAN_TAKEN);
		setOldLoanTaken (tOldLoanTaken);
		setNewLoanTaken (tNewLoanTaken);
	}

	private void setOldLoanTaken (boolean aOldLoanTaken) {
		oldLoanTaken = aOldLoanTaken;
	}
	
	private void setNewLoanTaken (boolean aNewLoanTaken) {
		newLoanTaken = aNewLoanTaken;
	}
	
	public boolean getOldLoanTaken () {
		return oldLoanTaken;
	}
	
	public boolean getNewLoanTaken () {
		return newLoanTaken;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_NEW_LOAN_TAKEN, newLoanTaken);
		tEffectElement.setAttribute (AN_OLD_LOAN_TAKEN, oldLoanTaken);
		
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for "  + actor.getName () + " changed from " + oldLoanTaken + " to " + newLoanTaken + ".");
	}


	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		System.out.println ("Undo the " + name + " for " + actor.getName ());
		
		return false;
	}

}
