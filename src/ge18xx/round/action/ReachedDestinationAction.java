package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ReachedDestinationEffect;
import geUtilities.XMLNode;

public class ReachedDestinationAction extends CashTransferAction {
	public final static String NAME = "Reached Destionation";
	
	public ReachedDestinationAction () {
		this (NAME);
	}

	public ReachedDestinationAction (String aName) {
		super (aName);
	}

	public ReachedDestinationAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ReachedDestinationAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	public void addReachedDestinationEffect (ActorI aActor, boolean aReached, int aOldCapitalizationLevel, 
										int aNewCapitalizationLevel) {
		ReachedDestinationEffect tReachedDestinationEffect;

		if (actor.isACorporation ()) {
			tReachedDestinationEffect = new ReachedDestinationEffect (aActor, aReached, 
										aOldCapitalizationLevel, aNewCapitalizationLevel);
			addEffect (tReachedDestinationEffect);
		}
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has " + NAME + ".";

		return tSimpleActionReport;
	}

}
