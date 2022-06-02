package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class UpdateLoanCountEffect extends Effect {
	final static AttributeName AN_OLD_LOAN_COUNT = new AttributeName ("oldLoanCount");
	final static AttributeName AN_NEW_LOAN_COUNT = new AttributeName ("newLoanCount");
	public final static String NAME = "Update Loan Count";
	int oldLoanCount;
	int newLoanCount;
	
	public UpdateLoanCountEffect () {
		super ();
		setName (NAME);
	}

	public UpdateLoanCountEffect (String aName) {
		super (aName);
	}

	public UpdateLoanCountEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public UpdateLoanCountEffect (ActorI aActor, int aOldLoanCount, int aNewLoanCount) {
		super (NAME, aActor);
		setOldLoanCount (aOldLoanCount);
		setNewLoanCount (aNewLoanCount);
	}

	public UpdateLoanCountEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		int tOldLoanCount;
		int tNewLoanCount;
		
		tOldLoanCount = aEffectNode.getThisIntAttribute (AN_OLD_LOAN_COUNT);
		tNewLoanCount = aEffectNode.getThisIntAttribute (AN_NEW_LOAN_COUNT);
		setOldLoanCount (tOldLoanCount);
		setNewLoanCount (tNewLoanCount);
	}
	
	private void setOldLoanCount (int aOldLoanCount) {
		oldLoanCount = aOldLoanCount;
	}
	
	private void setNewLoanCount (int aNewLoanCount) {
		newLoanCount = aNewLoanCount;
	}
	
	public int getOldLoanCount () {
		return oldLoanCount;
	}
	
	public int getNewLoanCount () {
		return newLoanCount;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_NEW_LOAN_COUNT, newLoanCount);
		tEffectElement.setAttribute (AN_OLD_LOAN_COUNT, oldLoanCount);
		
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for "  + actor.getName () + " from " + oldLoanCount +
				" to " + newLoanCount + ".");
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {

		System.out.println ("Undo the " + name + " for " + actor.getName ());
		
		return false;
	}

}
