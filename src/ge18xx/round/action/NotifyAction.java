package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ShowNotifyEffect;
import geUtilities.xml.XMLNode;

public class NotifyAction extends Action {
	public static final String NAME = "Notify";

	public NotifyAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public NotifyAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	public void addShowNotifyEffect (ActorI aFromActor, ActorI aToActor, String aMessage) {
		ShowNotifyEffect tShowNotifyEffect;

		tShowNotifyEffect = new ShowNotifyEffect (aFromActor, aToActor, aMessage);
		addEffect (tShowNotifyEffect);
	}
}
