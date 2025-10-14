package ge18xx.round.action.effects;

import ge18xx.company.formation.FormCompany;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetAllPlayersHandledEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set All Players Handled";
	final static AttributeName AN_ALL_PLAYERS_HANDLED = new AttributeName ("allPlayersHandled");

	public SetAllPlayersHandledEffect (ActorI aActor, boolean aHasLaidTile) {
		super (NAME, aActor, aHasLaidTile);
	}

	public SetAllPlayersHandledEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_ALL_PLAYERS_HANDLED);
		
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_ALL_PLAYERS_HANDLED);

		return tEffectElement;
	}
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " to " + getBooleanFlag () +".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		FormCompany tFormCompany;
		TriggerClass tTriggerFormationClass;
		GameManager tGameManager;
		
		tEffectApplied = false;
		tGameManager = aRoundManager.getGameManager ();
		tTriggerFormationClass = tGameManager.getTriggerFormation ();
		if (tTriggerFormationClass instanceof FormCompany) {
			tFormCompany = (FormCompany) tTriggerFormationClass;
			tFormCompany.setAllPlayerHandled (getBooleanFlag ());
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The game does not have a Formation Round.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		FormCompany tFormCompany;
		TriggerClass tTriggerFormationClass;
		GameManager tGameManager;
		
		tEffectUndone = false;
		tGameManager = aRoundManager.getGameManager ();
		tTriggerFormationClass = tGameManager.getTriggerFormation ();
		if (tTriggerFormationClass instanceof FormCompany) {
			tFormCompany = (FormCompany) tTriggerFormationClass;
			tFormCompany.setAllPlayerHandled (! getBooleanFlag ());
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("The game does not have a Formation Round.");
		}

		return tEffectUndone;
	}

}
