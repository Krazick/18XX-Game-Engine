package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.round.action.effects.RemoveDestinationEffect;
import geUtilities.xml.XMLNode;
import genericMap.Cell;

public class RemoveDestinationsAction extends ChangeMapAction {
	public static final String NAME = "Remove Destinations";

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
	
	public void addRemoveDestinationEffect (ActorI aActor, Cell aMapCell, Location aLocation) {
		RemoveDestinationEffect tRemoveDestinationEffect;

		tRemoveDestinationEffect = new RemoveDestinationEffect (aActor, aMapCell, aLocation);
		addEffect (tRemoveDestinationEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = NAME + " for Closed Companies";

		return tSimpleActionReport;
	}
}
