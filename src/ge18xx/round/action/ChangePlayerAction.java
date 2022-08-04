package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.NewCurrentPlayerEffect;
import ge18xx.utilities.XMLNode;

public class ChangePlayerAction extends SetWaitStateAction {
	public final static String NAME = "Change Player";

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

	public void addNewCurrentPlayerEffect (ActorI aPlayer, int aCurrentPlayerIndex, int aNextPlayerIndex) {
		NewCurrentPlayerEffect tNewCurrentPlayerEffect;

		tNewCurrentPlayerEffect = new NewCurrentPlayerEffect (aPlayer, aCurrentPlayerIndex, aNextPlayerIndex);
		addEffect (tNewCurrentPlayerEffect);
	}

	public String getNewPlayerName () {
		String tNewPlayerName = Player.NO_PLAYER_NAME;
		int tNewPlayerIndex;
		NewCurrentPlayerEffect tNewCurrentPlayerEffect;

		for (Effect tEffect : effects) {
			if (tNewPlayerName.equals (Player.NO_PLAYER_NAME)) {
				if (tEffect instanceof NewCurrentPlayerEffect) {
					tNewCurrentPlayerEffect = (NewCurrentPlayerEffect) tEffect;
					tNewPlayerIndex = tNewCurrentPlayerEffect.getNewPlayer ();
					tNewPlayerName = "Player # " + tNewPlayerIndex;
				}
			}
		}

		return tNewPlayerName;
	}
}