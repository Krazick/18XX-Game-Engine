package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.ChangeMapEffect;
import geUtilities.XMLNode;

public class ChangeMapAction extends CashTransferAction {
	public final static String NAME = "Change Map";

	public ChangeMapAction () {
		this (NAME);
	}

	public ChangeMapAction (String aName) {
		super (aName);
	}

	public ChangeMapAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangeMapAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addChangeMapEffect (ActorI aActor, MapCell aMapCell) {
		ChangeMapEffect tChangeMapEffect;

		tChangeMapEffect = new ChangeMapEffect (aActor, aMapCell);
		addEffect (tChangeMapEffect);
	}

	@Override
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		if (aPreviousState != aNewState) {
			tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
			addEffect (tChangeCorporationStatusEffect);
		}
	}
}
