package ge18xx.round.action.effects;

import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class SpecialPanelEffect extends ToEffect {
	public final static String NAME = "Repayment Finished";

	public SpecialPanelEffect () {
		this (NAME);
	}

	public SpecialPanelEffect (String aName) {
		super (aName);
	}

	public SpecialPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}
	
	public SpecialPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
		super (aName, aToActor, aFromActor);
	}

	public SpecialPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public void rebuildSpecialPanel (RoundManager aRoundManager, Player aPresident) {
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerClass.rebuildSpecialPanel (aPresident);
		}
	}
}
