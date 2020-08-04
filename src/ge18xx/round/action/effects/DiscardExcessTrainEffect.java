package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class DiscardExcessTrainEffect extends TransferTrainEffect {
	public final static String NAME = "Discard Excess Train";

	public DiscardExcessTrainEffect() {
		super ();
		setName (NAME);
	}

	public DiscardExcessTrainEffect(ActorI aFromActor, Train aTrain,
			ActorI aToActor) {
		super (aFromActor, aTrain, aToActor);
		setName (NAME);
	}

	public DiscardExcessTrainEffect(XMLNode aEffectNode,
			GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " named " + train.getName () + " from " + actor.getName () + 
				" into the Bank Pool Train Portfolio due to reduction on Train Limit.");
	}
}
