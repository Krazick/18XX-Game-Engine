package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.NewCurrentPlayerEffect;
import ge18xx.utilities.XMLNode;

public class ChangePlayerAction extends Action {
	public final static String NAME = "Change Player";
	
	public ChangePlayerAction () {
		this (NAME);
	}

	public ChangePlayerAction (String aName) {
		super (NAME);
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
	
	public String getPlayerName () {
		String tPlayerName = "";
		
		for (Effect tEffect : effects) {
			if (tPlayerName.equals ("")) {
				if (tEffect instanceof NewCurrentPlayerEffect) {
					tPlayerName = ((NewCurrentPlayerEffect) tEffect).getName ();
				}
			}
		}
		
		return tPlayerName;
	}
}