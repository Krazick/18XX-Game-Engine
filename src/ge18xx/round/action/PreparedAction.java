package ge18xx.round.action;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLNode;

public class PreparedAction {
	public static final ElementName EN_PREPARED_ACTION = new ElementName ("PreparedAction");
	public static final ElementName EN_ACTOR_TYPE = new ElementName ("ActorType");
	public static final ElementName EN_TARGET_STATE = new ElementName ("TargetState");
	public static final ElementName EN_TRIGGERING_ACTOR = new ElementName ("TriggeringActor");
	public static final AttributeName AN_STATE = new AttributeName ("state");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final PreparedAction NO_PREPARED_ACTION = null;
	ActorI.ActionStates targetState;
	ActorI.ActorTypes actorType;
	ActorI triggeringActor;
	Action action;
	
	public PreparedAction (ActorI.ActorTypes aActorType, ActorI.ActionStates aTargetState, 
					ActorI aTriggeringActor, Action aAction) {
		setActorType (aActorType);
		setTargetState (aTargetState);
		setAction (aAction);
		setTriggeringActor (aTriggeringActor);
	}
	
	public PreparedAction (XMLNode aPreparedActionNode, GameManager aGameManager) {
		String tState;
		String tType;
		ActorI.ActionStates tTargetState;
		ActorI.ActorTypes tActorType;
		ActorI tTriggeringActor;
		XMLNode tChildNode;
		String tChildName;
		String tTriggeringActorName;
		NodeList tChildren;
		Action tAction;
		int tIndex;
		int tChildrenCount;

		tChildren = aPreparedActionNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (EN_ACTOR_TYPE.equals (tChildName)) {
				tType = tChildNode.getThisAttribute (AN_TYPE);
				tActorType = ActorI.ActorTypes.fromString (tType);
				setActorType (tActorType);
			} else if (EN_TARGET_STATE.equals (tChildName)) {
				tState = tChildNode.getThisAttribute (AN_STATE);
				tTargetState = ActorI.ActionStates.fromString (tState);
				setTargetState (tTargetState);
			} else if (EN_TRIGGERING_ACTOR.equals (tChildName)) {
				tTriggeringActorName = tChildNode.getThisAttribute (AN_NAME);
				tTriggeringActor =  aGameManager.getActor (tTriggeringActorName);
				setTriggeringActor (tTriggeringActor);
			} else if (Action.EN_ACTION.equals (tChildName)) {
				tAction = new Action (tChildNode, aGameManager);
				setAction (tAction);
			}
		}
		
	}
	
	public void setTriggeringActor (ActorI aTriggeringActor) {
		triggeringActor = aTriggeringActor;
	}
	
	private void setAction (Action aAction) {
		action = aAction;	
	}

	private void setTargetState (ActionStates aTargetState) {
		targetState = aTargetState;
	}

	private void setActorType (ActorI.ActorTypes aActorType) {
		actorType = aActorType;
	}
	
	public Action getAction () {
		return action;
	}
	
	public ActorI getTriggeringActor () {
		return triggeringActor;
	}
	
	public ActorI.ActorTypes getActorType () {
		return actorType;
	}
	
	public ActorI.ActionStates getTargetState () {
		return targetState;
	}
	
	public String getActionReport (RoundManager aRoundManager) {
		return action.getActionReport (aRoundManager);
	}
}
