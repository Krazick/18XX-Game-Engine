package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class SetRepaymentHandledEffect extends Effect {
	public final static String NAME = "Set Repayment Handled";
	final static AttributeName AN_REPAYMENT_HANDLED = new AttributeName ("repaymentHandled");
	boolean replaymentHandled;

	public SetRepaymentHandledEffect () {
		super ();
		setName (NAME);
	}

	public SetRepaymentHandledEffect (String aName) {
		super (aName);
	}

	public SetRepaymentHandledEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetRepaymentHandledEffect (ActorI aActor, boolean aRepaymentHandled) {
		super (NAME, aActor);
		setRepaymentHandled (aRepaymentHandled);
	}

	public SetRepaymentHandledEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_REPAYMENT_HANDLED, replaymentHandled);

		return tEffectElement;
	}

	public void setRepaymentHandled (boolean aRepaymentHandled) {
		replaymentHandled = aRepaymentHandled;
	}
	
	public boolean getRepaymentHandled () {
		return replaymentHandled;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;

		tEffectApplied = false;
		if (actor.isATrainCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setRepaymentHandled (replaymentHandled);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;
		
		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setRepaymentHandled (false);
		}
		tEffectUndone = true;

		return tEffectUndone;
	}
}
