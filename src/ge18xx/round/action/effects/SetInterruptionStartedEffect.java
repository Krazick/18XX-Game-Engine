package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.InterruptionRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetInterruptionStartedEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Interruption Started";
	final static AttributeName AN_SET_INTERRUPTION_STARTED = new AttributeName ("setInterruptionStarted");

	public SetInterruptionStartedEffect (String aName) {
		super (aName);
	}

	public SetInterruptionStartedEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetInterruptionStartedEffect (ActorI aActor, boolean aSetInterruptionStarted) {
		super (NAME, aActor, aSetInterruptionStarted);
	}

	public SetInterruptionStartedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_SET_INTERRUPTION_STARTED);
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_SET_INTERRUPTION_STARTED);

		return tEffectElement;
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
		InterruptionRound tInterruptionRound;

		tEffectApplied = false;
		if (actor.isAInterruptionRound ()) {
			tInterruptionRound = (InterruptionRound) actor;
			tInterruptionRound.setInterruptionStarted (tEffectApplied);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The provided Actor " + actor.getName () + 
					" is not an Interruption Round");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		InterruptionRound tInterruptionRound;

		tEffectUndone = false;
		if (actor.isAInterruptionRound ()) {
			tInterruptionRound = (InterruptionRound) actor;
			tInterruptionRound.setInterruptionStarted (! getBooleanFlag ());
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("The provided Actor " + actor.getName () + 
					" is not an Interruption Round");
		}

		return tEffectUndone;
	}
}
