package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.ChangeTrainStatusEffect;
import ge18xx.round.action.effects.DiscardExcessTrainEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.PhaseChangeEffect;
import ge18xx.round.action.effects.RustTrainEffect;
import ge18xx.round.action.effects.SetFormationStateEffect;
import ge18xx.round.action.effects.ShowFormationPanelEffect;
import ge18xx.round.action.effects.StartFormationEffect;
import ge18xx.round.action.effects.TransferTrainEffect;
import ge18xx.round.action.effects.UpgradeTrainEffect;
import ge18xx.train.Train;
import geUtilities.GUI;
import geUtilities.XMLNode;

public class BuyTrainAction extends TransferTrainAction {
	public final static String NAME = "Buy Train";

	public BuyTrainAction () {
		this (NAME);
	}

	public BuyTrainAction (String aName) {
		super (aName);
	}

	public BuyTrainAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public BuyTrainAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}

	public void addDiscardExcessTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor) {
		DiscardExcessTrainEffect tDiscardExcessTrainEffect;

		tDiscardExcessTrainEffect = new DiscardExcessTrainEffect (aFromActor, aTrain, aToActor);
		addEffect (tDiscardExcessTrainEffect);
	}

	public void addUpgradeTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor) {
		UpgradeTrainEffect tUpgradeTrainEffect;

		tUpgradeTrainEffect = new UpgradeTrainEffect (aFromActor, aTrain, aToActor);
		addEffect (tUpgradeTrainEffect);
	}

	public void addPhaseChangeEffect (ActorI aActor, int aPreviousPhaseIndex, int aNewPhaseIndex) {
		PhaseChangeEffect tPhaseChangeEffect;

		tPhaseChangeEffect = new PhaseChangeEffect (aActor, aPreviousPhaseIndex, aNewPhaseIndex);
		addEffect (tPhaseChangeEffect);
	}

	public void addRustTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor, int aOldTrainStatus) {
		RustTrainEffect tRustTrainEffect;

		tRustTrainEffect = new RustTrainEffect (aFromActor, aTrain, aToActor, aOldTrainStatus);
		addEffect (tRustTrainEffect);
	}

	public void addTrainAvailableStatusEffect (ActorI aActor, String aTrainName, int aTrainOrder, int aOldTrainStatus,
			int aNewTrainStatus) {
		ChangeTrainStatusEffect tChangeTrainStatusEffect;

		tChangeTrainStatusEffect = new ChangeTrainStatusEffect (aActor, aTrainName, aTrainOrder, aOldTrainStatus,
				aNewTrainStatus);
		addEffect (tChangeTrainStatusEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " bought a " + getTrainBought () + " Train for "
				+ Bank.formatCash (getCashAmount ()) + " from " + getFromActorName () + ".";

		return tSimpleActionReport;
	}

	@Override
	protected String getFromActorName () {
		String tFromActorName;

		tFromActorName = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tFromActorName.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof TransferTrainEffect) {
					tFromActorName = ((TransferTrainEffect) tEffect).getActorName ();
				}
			}
		}

		return tFromActorName;
	}

	private String getTrainBought () {
		String tTrainName;

		tTrainName = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tTrainName.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof TransferTrainEffect) {
					tTrainName = ((TransferTrainEffect) tEffect).getTrainName ();
				}
			}
		}

		return tTrainName;
	}
	
	public void addShowFormationPanelEffect (ActorI aFromActor) {
		ShowFormationPanelEffect tShowFormationPanelEffect;
		
		tShowFormationPanelEffect = new ShowFormationPanelEffect (aFromActor);
		addEffect (tShowFormationPanelEffect);
	}

	public void addSetFormationStateEffect (ActorI aFromActor, ActorI.ActionStates aOldFormationState,
							ActorI.ActionStates aNewFormationState) {
		SetFormationStateEffect tSetFormationStateEffect;
		
		tSetFormationStateEffect = new SetFormationStateEffect (aFromActor, aOldFormationState, aNewFormationState);
		addEffect (tSetFormationStateEffect);
	}
	
	public void addStartFormationEffect (ActorI aActor, Corporation aFormingCorporation, 
						ShareCompany aTriggeringShareCompany) {
		StartFormationEffect tStartFormationEffect;

			tStartFormationEffect = new StartFormationEffect (aActor, aFormingCorporation, aTriggeringShareCompany);
			addEffect (tStartFormationEffect);
	}
}
