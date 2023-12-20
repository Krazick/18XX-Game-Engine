package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ShowFrameEffect;
import geUtilities.xml.XMLFrame;
import geUtilities.XMLNode;

public class ShowFrameAction extends Action {
	public final static String NAME = "Show Frame";

	public ShowFrameAction () {
		super ();
		setName (NAME);
	}

	public ShowFrameAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ShowFrameAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addShowFrameEffect (ActorI aActor, XMLFrame aXMLFrame) {
		ShowFrameEffect tShowFrameEffect;

		tShowFrameEffect = new ShowFrameEffect (aActor, aXMLFrame);
		addEffect (tShowFrameEffect);
	}
}
