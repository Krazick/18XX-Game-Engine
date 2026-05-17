package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.ChangeCurrentPlayerEffect;
import geUtilities.xml.XMLNode;

public class ChangePlayerAction extends SetWaitStateAction {
	public static final String NAME = "Change Player";

	public ChangePlayerAction () {
		this (NAME);
	}

	public ChangePlayerAction (String aName) {
		super (aName);
	}

	public ChangePlayerAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangePlayerAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addChangeCurrentPlayerEffect (ActorI aPlayer, int aCurrentPlayerIndex, int aNextPlayerIndex) {
		ChangeCurrentPlayerEffect tChangeCurrentPlayerEffect;

		tChangeCurrentPlayerEffect = new ChangeCurrentPlayerEffect (aPlayer, aCurrentPlayerIndex, aNextPlayerIndex);
		addEffect (tChangeCurrentPlayerEffect);
	}

	public String getNewPlayerName () {
		String tNewPlayerName = Player.NO_PLAYER_NAME;
		int tNewPlayerIndex;
		ChangeCurrentPlayerEffect tNewCurrentPlayerEffect;

		for (Effect tEffect : effects) {
			if (tNewPlayerName.equals (Player.NO_PLAYER_NAME)) {
				if (tEffect instanceof ChangeCurrentPlayerEffect) {
					tNewCurrentPlayerEffect = (ChangeCurrentPlayerEffect) tEffect;
					tNewPlayerIndex = tNewCurrentPlayerEffect.getNewPlayer ();
					tNewPlayerName = "Player # " + tNewPlayerIndex;
				}
			}
		}

		return tNewPlayerName;
	}
}