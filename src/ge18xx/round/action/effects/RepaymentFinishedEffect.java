package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class RepaymentFinishedEffect extends SpecialPanelEffect {
	final static AttributeName AN_REPAYMENT_FINISHED = new AttributeName ("repaymentFinished");
	public final static String NAME = "Repayment Finished";
	boolean replaymentFinished;
	
	public RepaymentFinishedEffect () {
		this (NAME);
	}

	public RepaymentFinishedEffect (String aName) {
		super (aName);
	}

	public RepaymentFinishedEffect (ActorI aActor, boolean aRepaymentFinished) {
		super (NAME, aActor);
		setRepaymentFinished (aRepaymentFinished);
	}

	public RepaymentFinishedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public void setRepaymentFinished (boolean aRepaymentFinished) {
		replaymentFinished = aRepaymentFinished;
	}
	
	public boolean getRepaymentFinished () {
		return replaymentFinished;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;
		int tPlayerIndex;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setRepaymentFinished (replaymentFinished);
			tPlayerIndex = getPlayerIndex (aRoundManager, tPlayer);
			rebuildSpecialPanel (aRoundManager, tPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		int tPlayerIndex;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setRepaymentFinished (false);
			tPlayerIndex = getPlayerIndex (aRoundManager, tPlayer);
			rebuildSpecialPanel (aRoundManager, tPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
