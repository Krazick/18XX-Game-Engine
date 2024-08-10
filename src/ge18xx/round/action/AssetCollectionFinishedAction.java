package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.xml.XMLNode;

public class AssetCollectionFinishedAction extends FormationPhaseAction {
	public final static String NAME = "Asset Collection Finished";

	public AssetCollectionFinishedAction () {
		this (NAME);
	}

	public AssetCollectionFinishedAction (String aName) {
		super (aName);
	}

	public AssetCollectionFinishedAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public AssetCollectionFinishedAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " has finished all Asset Collections";

		return tSimpleActionReport;
	}
}
