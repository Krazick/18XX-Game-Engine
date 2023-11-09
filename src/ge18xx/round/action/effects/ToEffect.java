package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ToEffect extends Effect {
	public final static String NO_NAME = ">>NO TO EFFECT NAME<<";
	public final static ActorI NO_TO_ACTOR = null;
	static final AttributeName AN_TO_NICK_NAME = new AttributeName ("toNickName");
	ActorI toActor;
	String toNickName;

	public ToEffect () {
		this (NO_NAME);
	}

	public ToEffect (String aName) {
		this (aName, NO_ACTOR, NO_TO_ACTOR);
	}

	public ToEffect (String aName, ActorI aFromActor) {
		this (aName, aFromActor, NO_TO_ACTOR);
	}
	
	public ToEffect (String aName, ActorI aFromActor, ActorI aToActor) {
		super (aName, aFromActor);
		
		setToActor (aToActor);
	}
	
	public ToEffect (String aName, ActorI aFromActor, String aFromNickName, ActorI aToActor, String aToNickName) {
		super (aName, aFromActor, aFromNickName);
		setToActor (aToActor);
		setToNickName (aToNickName);
	}

	public ToEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tToActorName;
		String tToNickName;
		ActorI tToActor;

		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tToNickName = aEffectNode.getThisAttribute (AN_TO_NICK_NAME);
		tToActor = aGameManager.getActor (tToActorName);
		setToActor (tToActor);
		setToNickName (tToNickName);
	}

	public String getToNickName () {
		String tToNickName;
		
		tToNickName = GUI.NULL_STRING;
		if (toNickName == GUI.NULL_STRING) {
			tToNickName = GUI.NULL_STRING;
		} else if (toNickName == GUI.EMPTY_STRING) {
			tToNickName = GUI.NULL_STRING;		
		} else {
			tToNickName = toNickName;
		}
		
		return tToNickName;
	}

	public void setToNickName (String aToNickName) {
		toNickName = aToNickName;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		if (toActor != ActorI.NO_ACTOR) {
			if (toActor.isACorporation ()) {
				tActorName = ((Corporation) toActor).getAbbrev ();
			} else {
				tActorName = getToActorName ();
			}
			tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, tActorName);
		}
		
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
	
	public String getToDisplayName () {
		String tToDisplayName;
		
		tToDisplayName = getToNickName ();
		
		if (tToDisplayName == GUI.NULL_STRING) {
			tToDisplayName = toActor.getName ();
		}

		return tToDisplayName;
	}

	@Override
	public String getToActorName () {
		String tToName;
		
		if (toActor == ActorI.NO_ACTOR) {
			tToName = GUI.NULL_STRING;
		} else {
			tToName = toActor.getName ();
		}

		return tToName;
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

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
