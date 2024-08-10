package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.xml.XMLNode;

public class CloseCorporationEffect extends ChangeCorporationStatusEffect {
	public final static String NAME = "Close Corporation";

	public CloseCorporationEffect () {
		super ();
		setName (NAME);
	}

	public CloseCorporationEffect (ActorI aActor, ActionStates aPreviousState, ActionStates aNewState) {
		super (aActor, aPreviousState, aNewState);
		setName (NAME);
	}

	public CloseCorporationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tActorFullName;

		tActorFullName = actor.getName ();
		if (actor.isAPrivateCompany ()) {
			tActorFullName = tActorFullName + " (Private)";
		}
		return (REPORT_PREFIX + "Close the " + tActorFullName);
	}
}