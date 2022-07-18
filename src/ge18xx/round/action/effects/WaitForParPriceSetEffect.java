package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class WaitForParPriceSetEffect extends StateChangeEffect {
	public final static String NAME = "Wait For Par Price Set";

	public WaitForParPriceSetEffect (ActorI aActor, ActorI.ActionStates aPreviousState, 
									ActorI.ActionStates aNewState) {
		super (aActor, aPreviousState, aNewState);
		setName (NAME);
	}

	public WaitForParPriceSetEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
}
