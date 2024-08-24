package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.BidShareEffect;
import ge18xx.round.action.effects.BoughtShareEffect;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.ShowFrameEffect;
import ge18xx.round.action.effects.StateChangeEffect;
import ge18xx.round.action.effects.UpdateToNextPlayerEffect;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.GUI;

public class ChangeStateAction extends ChangePlayerAction {
	public final static String NAME = "Change State";
	public final static ChangeStateAction NO_CHANGE_STATE_ACTION = null;

	public ChangeStateAction () {
		this (NAME);
	}

	public ChangeStateAction (String aName) {
		super (aName);
	}

	public ChangeStateAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangeStateAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, ActorI.ActionStates aNewState) {
		StateChangeEffect tStateChangeEffect;
		
		if (aActor.isAPlayer () || aActor.isAStockRound () || aActor.isAOperatingRound ()) {
			tStateChangeEffect = new StateChangeEffect (aActor, aOldState, aNewState);
			addEffect (tStateChangeEffect);
		} else if (aActor.isACorporation ()) {
			addChangeCorporationStatusEffect (aActor, aOldState, aNewState);
		}
	}
	
	public void addUpdateToNextPlayerEffect (ActorI aActor, ActorI aOldPlayer, ActorI aNewPlayer) {
		UpdateToNextPlayerEffect tUpdateToNextPlayerEffect;;

		if (actor.isAPlayer ()) {
			tUpdateToNextPlayerEffect = new UpdateToNextPlayerEffect (aOldPlayer, aNewPlayer);
			addEffect (tUpdateToNextPlayerEffect);
		}
	}

	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}

	public void addBidShareEffect (ActorI aPlayer, boolean aBidOnShare) {
		BidShareEffect tBidShareEffect;
		
		tBidShareEffect = new BidShareEffect (aPlayer, aBidOnShare);
		addEffect (tBidShareEffect);
	}

	public void addBoughtShareEffect (ActorI aActor, String aBoughtShare, String aPriorBoughtShare) {
		BoughtShareEffect tBoughtShareEffect;

		tBoughtShareEffect = new BoughtShareEffect (aActor, aBoughtShare, aPriorBoughtShare);
		addEffect (tBoughtShareEffect);
	}

	public void addShowFrameEffect (ActorI aActor, XMLFrame aXMLFrame) {
		ShowFrameEffect tShowFrameEffect;
		
		tShowFrameEffect = new ShowFrameEffect (aActor, aXMLFrame);
		addEffect (tShowFrameEffect);
	}

	@Override
	public boolean wasLastActionStartAuction () {
		boolean tWasNewStateAuction = false;

		for (Effect tEffect : effects) {
			if (tEffect.wasNewStateAuction ()) {
				tWasNewStateAuction = true;
			}
		}

		return tWasNewStateAuction;
	}

	public String getOldState () {
		String tOldState = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tOldState.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof StateChangeEffect) {
					tOldState = ((StateChangeEffect) tEffect).getPreviousState ().toString ();
				}
			}
		}

		return tOldState;
	}

	public String getNewState () {
		String tOldState = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tOldState.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof StateChangeEffect) {
					tOldState = ((StateChangeEffect) tEffect).getNewState ().toString ();
				}
			}
		}

		return tOldState;
	}

	public String getOldCorpState () {
		String tOldState = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tOldState.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof ChangeCorporationStatusEffect) {
					tOldState = ((ChangeCorporationStatusEffect) tEffect).getPreviousState ().toString ();
				}
			}
		}

		return tOldState;
	}

	public String getNewCorpState () {
		String tOldState = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tOldState.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof ChangeCorporationStatusEffect) {
					tOldState = ((ChangeCorporationStatusEffect) tEffect).getNewState ().toString ();
				}
			}
		}

		return tOldState;
	}

	public String getActorNames () {
		String tActorNames = GUI.EMPTY_STRING;
		String tActorName;

		for (Effect tEffect : effects) {
			if (tEffect instanceof StateChangeEffect) {
				tActorName = ((StateChangeEffect) tEffect).getActorName ();
				if (!tActorNames.contains (tActorName)) {
					tActorNames += tActorName + ", ";
				}
			}
		}
		if (tActorNames.equals (GUI.EMPTY_STRING)) {
			tActorNames = "NONE";
		} else {
			tActorNames = tActorNames.substring (0, tActorNames.length () - 2);
		}

		return tActorNames;
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = GUI.EMPTY_STRING;
		String tOldState, tNewState;

		tOldState = getOldState ();
		tNewState = getNewState ();
		if (!tNewState.equals (tOldState)) {
			tSimpleActionReport = actor.getName () + " changed state of " + getActorNames () + " from  "
					+ getOldState () + " to " + getNewState () + ".";
		} else {
			tSimpleActionReport = actor.getName () + " state remains [" + tOldState + "]";
		}

		return tSimpleActionReport;
	}

	@Override
	public boolean allNullEffects () {
		boolean tAllNullEffects = true;

		for (Effect tEffect : effects) {
			if (!tEffect.nullEffect ()) {
				tAllNullEffects = false;
			}
		}

		return tAllNullEffects;
	}

	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		ActorI.ActionStates aCurrentRoundType;
		ActorI.ActionStates aPreviousRoundType;
		XMLFrame tOperatingCompanyFrame;
		Corporation tOperatingCompany;
		
		aCurrentRoundType = aRoundManager.getCurrentRoundType (); 

		tActionUndone = super.undoAction (aRoundManager);

		aPreviousRoundType = aRoundManager.getCurrentRoundType ();
		if (aPreviousRoundType.equals (ActorI.ActionStates.StockRound)) {

		} else if (aCurrentRoundType.equals (ActorI.ActionStates.OperatingRound)) {
			tOperatingCompany = aRoundManager.getOperatingCompany ();
			if (tOperatingCompany != Corporation.NO_CORPORATION) {
				tOperatingCompanyFrame = tOperatingCompany.getCorporationFrame ();
				tOperatingCompanyFrame.showFrame ();
			}
		}

		return tActionUndone;
	}
}
