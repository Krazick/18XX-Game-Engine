package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ToEffect extends Effect {
	public final static String NO_NAME = ">>NO TO EFFECT NAME<<";
	public final static ActorI NO_TO_ACTOR = null;
	ActorI toActor;

	public ToEffect () {
		this (NO_NAME);
	}

	public ToEffect (String aName) {
		this (aName, NO_ACTOR, NO_TO_ACTOR);
	}

	public ToEffect (String aName, ActorI aFromActor, ActorI aToActor) {
		super (aName, aFromActor);
		setToActor (aToActor);
	}

	public ToEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tToActorName;
		ActorI tToActor;
		
		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tToActor = aGameManager.getActor (tToActorName);
		setToActor (tToActor);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		if (toActor.isACorporation ()) {
			tActorName = ((Corporation) toActor).getAbbrev ();
		} else {
			tActorName = toActor.getName ();
		}
		tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, tActorName);
	
		return tEffectElement;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + getActorName () + " to " + getToActorName () + ".");
	}
	
	public ActorI getToActor () {
		return toActor;
	}
	
	public void setToActor (ActorI aToActor) {
		toActor = aToActor;
	}
	
	@Override
	public String getToActorName () {
		return toActor.getName ();
	}
	
	public boolean isToActor (String aActorName) {
		boolean tIsToActor = false;
		
		if (toActor != NO_TO_ACTOR) {
			if (toActor.getName ().equals (aActorName)) {
				tIsToActor = true;
			}
		}
		
		return tIsToActor;
	}
}
