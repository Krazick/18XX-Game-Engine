package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.InterruptionRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetAtStartOfRoundEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set at Start of Round";
	final static AttributeName AN_AT_START_OF_ROUND = new AttributeName ("atStartOfRound");

	public SetAtStartOfRoundEffect (ActorI aActor, boolean aHasLaidTile) {
		super (NAME, aActor, aHasLaidTile);
	}

	public SetAtStartOfRoundEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_AT_START_OF_ROUND);
		
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_AT_START_OF_ROUND);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " to " + getBooleanFlag () + ".");
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
			tInterruptionRound.setAtStartOfRound (getBooleanFlag ());
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
			tInterruptionRound.setAtStartOfRound (! getBooleanFlag ());
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("The provided Actor " + actor.getName () + 
					" is not an Interruption Round");
		}

		return tEffectUndone;
	}

}
