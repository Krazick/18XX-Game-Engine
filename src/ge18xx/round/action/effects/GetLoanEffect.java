package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class GetLoanEffect extends Effect {
	final static AttributeName AN_OLD_LOAN_TAKEN = new AttributeName ("oldLoanTaken");
	final static AttributeName AN_NEW_LOAN_TAKEN = new AttributeName ("newLoanTaken");
	public final static String NAME = "Get Loan";
	boolean oldLoanTaken;
	boolean newLoanTaken;

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
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;
		ActorI tActor;

		tEffectApplied = false;
		tActor = getActor ();
		if (tActor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tActor;
			tShareCompany.setLoanTaken (newLoanTaken);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;
		ActorI tActor;

		tEffectUndone = false;
		tActor = getActor ();
		if (tActor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tActor;
			tShareCompany.setLoanTaken (oldLoanTaken);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
