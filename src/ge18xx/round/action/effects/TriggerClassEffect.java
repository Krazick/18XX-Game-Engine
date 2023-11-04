package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class TriggerClassEffect extends SpecialPanelEffect {
	public final static String NAME = "Trigger Class";

	public TriggerClassEffect () {
		this (NAME);
	}

	public TriggerClassEffect (String aName) {
		super (aName);
	}

	public TriggerClassEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public TriggerClassEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public TriggerClassEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = true;

		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			hideFormationPanel (aRoundManager);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
