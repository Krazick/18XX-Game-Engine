package ge18xx.round.action.effects;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class StartStockEffect extends Effect {
	public static final String NAME = "Start Stock";

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
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;
	
		tEffectReport = " " + REPORT_PREFIX + name + " activity for " + getActorName () + ".";
		
		return tEffectReport;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		aRoundManager.setPlayerDoingAction (true);
		
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		aRoundManager.setPlayerDoingAction (false);
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
