package ge18xx.round.action.effects.time;

import java.time.Duration;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class CaptureTimeUsedEffect extends GameDurationEffect {
	public static final String NAME = "Capture Time Used Duration";

	public CaptureTimeUsedEffect (ActorI aActor, Duration aPreviousDuration, Duration aNewDuration) {
		super (aActor, aPreviousDuration, aNewDuration);
		setName (NAME);
	}

	public CaptureTimeUsedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setTotalTimeUsed (newDuration);
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
			tPlayer.setTotalTimeUsed (newDuration);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;

		tEffectReport = REPORT_PREFIX + name + " from " + previousDuration.toString () +
				" to " + newDuration.toString () + ".";

		return tEffectReport;
	}
}
