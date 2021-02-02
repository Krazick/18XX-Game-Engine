package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ResponseOfferEffect;
import ge18xx.utilities.XMLNode;

public class ResponseOfferAction extends QueryActorAction {
	public final static String NAME = "Response To Offer";

	public ResponseOfferAction () {
		this (NAME);
	}

	public ResponseOfferAction (String aName) {
		super (aName);
	}

	public ResponseOfferAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ResponseOfferAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " has responded to a Query Action.";
		
		return tSimpleActionReport;
	}

	public void addResponseOfferEffect (ActorI aFromActor, ActorI aToActor, 
			boolean aResponse, String aItemType, String aItemName) {
		ResponseOfferEffect tOfferResponseEffect;
		
		tOfferResponseEffect = new ResponseOfferEffect (aFromActor, aToActor, 
				aResponse, aItemType, aItemName);
		addEffect (tOfferResponseEffect);
	}

}
