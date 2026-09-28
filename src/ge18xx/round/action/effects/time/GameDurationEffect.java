package ge18xx.round.action.effects.time;

import java.time.Duration;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.effects.Effect;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class GameDurationEffect extends Effect {
	public static final String NAME = "Game Duration";
	public static final AttributeName AN_PREVIOUS_DURATION = new AttributeName ("previousDuration");
	public static final AttributeName AN_NEW_DURATION = new AttributeName ("newDuration");
	Duration previousDuration;
	Duration newDuration;

	public GameDurationEffect (ActorI aActor, Duration aPreviousDuration, Duration aNewDuration) {
		super (NAME, aActor);
		setPreviousDuration (aPreviousDuration);
		setNewDuration (aNewDuration);
	}

	public GameDurationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tStringDuration;
		Duration tGameDuration;
		
		tStringDuration = aEffectNode.getThisAttribute (AN_PREVIOUS_DURATION);
		tGameDuration = Duration.parse (tStringDuration);
		setPreviousDuration (tGameDuration);
		
		tStringDuration = aEffectNode.getThisAttribute (AN_NEW_DURATION);
		tGameDuration = Duration.parse (tStringDuration);
		setNewDuration (tGameDuration);
	}

	public void setPreviousDuration (Duration aPreviousDuration) {
		previousDuration = aPreviousDuration;
	}

	public void setNewDuration (Duration aNewDuration) {
		newDuration = aNewDuration;
	}
	
	public Duration getPreviousDuration () {
		return previousDuration;
	}
	
	public Duration getNewDuration () {
		return newDuration;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PREVIOUS_DURATION, previousDuration.toString ());
		tEffectElement.setAttribute (AN_NEW_DURATION, newDuration.toString ());

		return tEffectElement;
	}
}
