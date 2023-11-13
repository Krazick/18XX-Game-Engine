package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.utilities.XMLNode;

public class ReplaceTokenAction extends LayTokenAction {
	public final static String NAME = "Replace Token";

	public ReplaceTokenAction () {
		super ();
		setName (NAME);
	}

	public ReplaceTokenAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ReplaceTokenAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " replaced Token on Map Cell " + getMapCellID ();

		return tSimpleActionReport;
	}
}
