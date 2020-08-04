package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.XMLNode;

public class QueryActorAction extends Action {
	public final static String NAME = "Query Actor";

	public QueryActorAction () {
		this (NAME);
	}

	public QueryActorAction (String aName) {
		super (aName);
	}

	public QueryActorAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public QueryActorAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " has generated a Query Action.";
		
		return tSimpleActionReport;
	}
}
