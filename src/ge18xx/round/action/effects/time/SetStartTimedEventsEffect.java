package ge18xx.round.action.effects.time;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.effects.ChangeBooleanFlagEffect;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetStartTimedEventsEffect extends ChangeBooleanFlagEffect {
	public static final String NAME = "Start Timed Events";
	final static AttributeName AN_START_TIMED_EVENTS = new AttributeName ("startTimedEvents");

	public SetStartTimedEventsEffect (ActorI aActor, boolean aBooleanFlag) {
		super (NAME, aActor, aBooleanFlag);
	}

	public SetStartTimedEventsEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_START_TIMED_EVENTS);
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_START_TIMED_EVENTS);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		
		if (booleanFlag) {
			tReport = REPORT_PREFIX + NAME + " Flag for " + actor.getName () + " set to TRUE.";
		} else {
			tReport = REPORT_PREFIX + NAME + " Flag for " + actor.getName () + " set to FALSE.";
		}
		
		return tReport;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setStartTimedEvents (booleanFlag);
		} else {
			setApplyFailureReason ("This is not a Player");
		}

		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setStartTimedEvents (! booleanFlag);
		} else {
			setUndoFailureReason ("This is not a Player");
		}

		tEffectUndone = true;

		return tEffectUndone;
	}
}
