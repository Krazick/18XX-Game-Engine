package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ResponseToOfferEffect;
import ge18xx.round.action.effects.StateChangeEffect;
import geUtilities.xml.XMLNode;

public class ResponseToOfferAction extends QueryActorAction {
	public static final String NAME = "Response To Offer";

	public ResponseToOfferAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ResponseToOfferAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has responded to a Query Action.";

		return tSimpleActionReport;
	}

	public void addResponseToOfferEffect (ActorI aFromActor, ActorI aToActor, boolean aResponse, String aItemType,
			String aItemName) {
		ResponseToOfferEffect tResponseToOfferEffect;

		tResponseToOfferEffect = new ResponseToOfferEffect (aFromActor, aToActor, aResponse, aItemType, aItemName);
		addEffect (tResponseToOfferEffect);
	}
	
	public void addStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, ActorI.ActionStates aNewState) {
		StateChangeEffect tStateChangeEffect;

		tStateChangeEffect = new StateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tStateChangeEffect);
	}
}
