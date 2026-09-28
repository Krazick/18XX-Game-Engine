package ge18xx.round.action.effects.time;

import java.time.LocalDateTime;

import ge18xx.game.GameManager;
//import ge18xx.player.Player;
//import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.effects.Effect;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class GameTimeEffect extends Effect {
	public static final String NAME = "Game Time";
	public static final AttributeName AN_PREVIOUS_TIME = new AttributeName ("previousTime");
	public static final AttributeName AN_NEW_TIME = new AttributeName ("newTime");
	LocalDateTime previousDateTime;
	LocalDateTime newDateTime;
	
	public GameTimeEffect (ActorI aActor, LocalDateTime aPreviousDateTime, LocalDateTime aNewDateTime) {
		super (NAME, aActor);
		setPreviousGameTime (aPreviousDateTime);
		setNewGameTime (aNewDateTime);
	}

	public GameTimeEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tStringDateTime;
		LocalDateTime tGameDateTime;
		
		tStringDateTime = aEffectNode.getThisAttribute (AN_PREVIOUS_TIME);
		tGameDateTime = LocalDateTime.parse (tStringDateTime);
		setPreviousGameTime (tGameDateTime);
		
		tStringDateTime = aEffectNode.getThisAttribute (AN_NEW_TIME);
		tGameDateTime = LocalDateTime.parse (tStringDateTime);
		setNewGameTime (tGameDateTime);
	}

	public void setPreviousGameTime (LocalDateTime aPreviousTime) {
		previousDateTime = aPreviousTime;
	}

	public void setNewGameTime (LocalDateTime aNewTime) {
		newDateTime = aNewTime;
	}
	
	public LocalDateTime getPreviousGameTime () {
		return previousDateTime;
	}
	
	public LocalDateTime getNewGameTime () {
		return newDateTime;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PREVIOUS_TIME, previousDateTime.toString ());
		tEffectElement.setAttribute (AN_NEW_TIME, newDateTime.toString ());

		return tEffectElement;
	}

//	@Override
//	public boolean applyEffect (RoundManager aRoundManager) {
//		boolean tEffectApplied;
//		Player tPlayer;
//		
//		tEffectApplied = false;
//		if (actor.isAPlayer ()) {
//			tPlayer = (Player) actor;
//			
//			tEffectApplied = true;
//		}
//
//		return tEffectApplied;
//	}

}
