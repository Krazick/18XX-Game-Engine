package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class UpdateLoanCountEffect extends Effect {
	public static final AttributeName AN_OLD_LOAN_COUNT = new AttributeName ("oldLoanCount");
	public static final AttributeName AN_NEW_LOAN_COUNT = new AttributeName ("newLoanCount");
	public static final String NAME = "Update Loan Count";
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
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;
		ActorI tActor;

		tEffectApplied = false;
		tActor = getActor ();
		if (tActor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tActor;
			tShareCompany.setLoanCount (newLoanCount);
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
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tActor;
			tShareCompany.setLoanCount (oldLoanCount);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
