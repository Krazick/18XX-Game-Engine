package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Effect {
	public final static String NO_NAME = ">>NO EFFECT NAME<<";
	public final static ActorI NO_ACTOR = null;
	public static final ElementName EN_EFFECTS = new ElementName ("Effects");
	public static final ElementName EN_EFFECT = new ElementName ("Effect");
	public final static String REPORT_PREFIX = "--" + EN_EFFECT + ": ";
	public final static AttributeName AN_CLASS = new AttributeName ("class");
	public final static AttributeName AN_IS_A_PRIVATE = new AttributeName ("isAPrivate");
	static final AttributeName AN_NAME = new AttributeName ("name");

	String name;
	ActorI actor;
	boolean isAPrivate;

	Effect () {
		this (NO_NAME);
	}

	Effect (String aName) {
		this (aName, NO_ACTOR);
	}
	
	Effect (String aName, ActorI aActor) {
		setName (aName);
		setActor (aActor);
	}
	
	Effect (XMLNode aEffectNode, GameManager aGameManager) {
		String tEffectName, tActorName;
		ActorI tActor;
		boolean tIsAPrivate;
		
		tEffectName = aEffectNode.getThisAttribute (AN_NAME);
		tActorName = aEffectNode.getThisAttribute (ActorI.AN_ACTOR_NAME);
		tIsAPrivate = aEffectNode.getThisBooleanAttribute (AN_IS_A_PRIVATE);
		if (tActorName == null) {
			tActorName = aEffectNode.getThisAttribute (ActorI.AN_FROM_ACTOR_NAME);
		}
		
		tActor = aGameManager.getActor (tActorName, tIsAPrivate);
		if (tActor == null) {
			System.err.println ("No Actor Found -- Looking for [" + tActorName + "]");
		}
		setName (tEffectName);
		setActor (tActor);
	}
	
	public boolean actorIsSet () {
		boolean tActorSet;
		
		tActorSet = false;
		if (actor != NO_ACTOR) {
			tActorSet = true;
		}
		
		return tActorSet;
	}
	
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		
		tEffectElement = aXMLDocument.createElement (EN_EFFECT);
		tEffectElement.setAttribute (AN_CLASS, this.getClass ().getName ());
		if (actor.isACorporation ()) {
			tActorName = ((Corporation) actor).getAbbrev ();
		} else {
			tActorName = actor.getName ();
		}
		tEffectElement.setAttribute (AN_NAME, getName ());
		tEffectElement.setAttribute (aActorAN, tActorName);
		tEffectElement.setAttribute (AN_IS_A_PRIVATE, isAPrivate);
	
		return tEffectElement;
	}
	
	public String getName () {
		return name;
	}
	
	public ActorI getActor () {
		return actor;
	}
	
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + getActorName () + ".");
	}
	
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	public void setActor (ActorI aActor) {
		actor = aActor;
		if (aActor != NO_ACTOR) {
			isAPrivate = aActor.isAPrivateCompany ();
		}
	}

	public String getToActorName () {
		return ActorI.NO_NAME;
	}

	public String getActorName () {
		return actor.getName ();
	}
	
	public boolean undoEffect (RoundManager aRoundManager) {
		return false;
	}
	
	public boolean wasNewStateAuction () {
		return false;
	}

	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = false;
		
		return tEffectApplied;
	}
	
	public boolean nullEffect () {
		return false;
	}
}
