package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class SetRepaymentFinishedEffect extends Effect {
	public final static String NAME = "Set Repayment Finished";
	final static AttributeName AN_REPAYMENT_FINISHED = new AttributeName ("repaymentFinished");
	boolean replaymentFinished;

	public SetRepaymentFinishedEffect () {
		super ();
		setName (NAME);
	}

	public SetRepaymentFinishedEffect (String aName) {
		super (aName);
	}

	public SetRepaymentFinishedEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetRepaymentFinishedEffect (ActorI aActor, boolean aRepaymentFinished) {
		super (NAME, aActor);
		setRepaymentFinished (aRepaymentFinished);
	}

	public SetRepaymentFinishedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_REPAYMENT_FINISHED, replaymentFinished);

		return tEffectElement;
	}

	public void setRepaymentFinished (boolean aRepaymentFinished) {
		replaymentFinished = aRepaymentFinished;
	}
	
	public boolean getRepaymentFinished () {
		return replaymentFinished;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " to TRUE.");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;
		Player tPresident;
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			tShareCompany.setRepaymentHandled (replaymentFinished);
			tEffectApplied = true;
			rebuildSpecialPanel (aRoundManager, tPresident);
		}

		return tEffectApplied;
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

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;
		Player tPresident;
		
		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setRepaymentHandled (false);
			tPresident = (Player) tShareCompany.getPresident ();
			rebuildSpecialPanel (aRoundManager, tPresident);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
