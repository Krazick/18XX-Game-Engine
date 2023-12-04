package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import ge18xx.round.action.effects.SetNotificationEffect;
import ge18xx.round.action.effects.SetRepaymentHandledEffect;
import ge18xx.round.action.effects.ShareFoldCountEffect;
import geUtilities.XMLNode;

public class RepaymentHandledAction extends ChangeStateAction {
	public final static String NAME = "Repayement Handled";

	public RepaymentHandledAction () {
		this (NAME);
	}

	public RepaymentHandledAction (String aName) {
		super (aName);
	}

	public RepaymentHandledAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RepaymentHandledAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addSetRepaymentHandledEffect (ActorI aActor, boolean aRepaymentHandled) {
		SetRepaymentHandledEffect tSetRepaymentHandledEffect;
		
		if (actor.isACorporation ()) {
			tSetRepaymentHandledEffect = new SetRepaymentHandledEffect (aActor, aRepaymentHandled);
			addEffect (tSetRepaymentHandledEffect);
		}
	}

	public void addShareFoldCountEffect (ActorI aActor, int aOldShareFoldCount, int aNewShareFoldCount) {
		ShareFoldCountEffect tShareFoldCountEffect;
		
		if (actor.isACorporation ()) {
			tShareFoldCountEffect = new ShareFoldCountEffect (aActor, aOldShareFoldCount, aNewShareFoldCount);
			addEffect (tShareFoldCountEffect);
		}
	}

	public void addSetNotificationEffect (ActorI aActor, String aNotificationText) {
		SetNotificationEffect tSetNotificationEffect;
		
		if (actor.isACorporation ()) {
			tSetNotificationEffect = new SetNotificationEffect (aActor, aNotificationText);
			addEffect (tSetNotificationEffect);
		}
	}

	public void addRebuildFormationPanelEffect (ActorI aFromActor) {
		RebuildFormationPanelEffect tRebuildFormationPanelEffect;
		
		tRebuildFormationPanelEffect = new RebuildFormationPanelEffect (aFromActor);
		addEffect (tRebuildFormationPanelEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		Corporation tCorporation;
		Player tPlayer;
		
		if (actor.isAShareCompany ()) {
			tCorporation = (Corporation) actor;
			tPlayer = (Player) tCorporation.getPresident ();
			tSimpleActionReport = tPlayer.getName () + " has handled Loan Repayment for " + 
					tCorporation.getAbbrev () + ".";
		} else {
			tSimpleActionReport = "The actor is not a Corporation";
		}

		return tSimpleActionReport;
	}
}
