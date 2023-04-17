package ge18xx.round.action.effects;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class StartStockEffect extends Effect {
	public final static String NAME = "Start Stock";

	public StartStockEffect () {
		super ();
		setName (NAME);
	}

	public StartStockEffect (String aName) {
		super (aName);
		setName (NAME);
	}

	public StartStockEffect (String aName, ActorI aActor) {
		super (aName, aActor);
		setName (NAME);
	}

	public StartStockEffect (String aName, ActorI aActor, Benefit aBenefitInUse) {
		super (aName, aActor, aBenefitInUse);
		setName (NAME);
	}

	public StartStockEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

}