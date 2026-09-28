package ge18xx.round.action.effects.time;

import java.time.LocalDateTime;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class CaptureStartTimeEffect extends GameTimeEffect {
	public static final String NAME = "Capture Start Time";
	
	public CaptureStartTimeEffect (ActorI aActor, LocalDateTime aPreviousDateTime, LocalDateTime aNewDateTime) {
		super (aActor, aPreviousDateTime, aNewDateTime);
		setName (NAME);
	}
	
	public CaptureStartTimeEffect (XMLNode aEffectNode, GameManager aGameManager){
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setActionStartTime (newDateTime);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setActionStartTime (previousDateTime);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;

		tEffectReport = REPORT_PREFIX + name + " from " + previousDateTime.toString () +
				" to " + newDateTime.toString () + ".";

		return tEffectReport;
	}
}
