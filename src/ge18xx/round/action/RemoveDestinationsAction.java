package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.RemoveDestinationEffect;
import geUtilities.XMLNode;

public class RemoveDestinationsAction extends ChangeMapAction {
	public final static String NAME = "Remove Destinations";

	public RemoveDestinationsAction () {
		super ();
		setName (NAME);
	}

	public RemoveDestinationsAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RemoveDestinationsAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	public void addRemoveDestinationEffect (ActorI aActor, MapCell aMapCell, Location aLocation) {
		RemoveDestinationEffect tRemoveDestinationEffect;

		tRemoveDestinationEffect = new RemoveDestinationEffect (aActor, aMapCell, aLocation);
		addEffect (tRemoveDestinationEffect);
	}
}
