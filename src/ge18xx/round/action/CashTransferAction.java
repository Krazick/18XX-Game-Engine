package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.effects.CashTransferEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class CashTransferAction extends ChangeStateAction {
	public final static String NAME = "Cash Transfer";

	public CashTransferAction () {
		this (NAME);
	}

	public CashTransferAction (String aName) {
		super (aName);
	}

	public CashTransferAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public CashTransferAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addCashTransferEffect (CashHolderI aFromCashHolder, CashHolderI aToCashHolder, int aCashAmount) {
		CashTransferEffect tCashTransferEffect;

		tCashTransferEffect = new CashTransferEffect (aFromCashHolder, aToCashHolder, aCashAmount);
		addEffect (tCashTransferEffect);
	}
	
	public void addRebuildFormationPanelEffect (ActorI aFromActor) {
		RebuildFormationPanelEffect tRebuildFormationPanelEffect;
		
		tRebuildFormationPanelEffect = new RebuildFormationPanelEffect (aFromActor);
		addEffect (tRebuildFormationPanelEffect);
	}

	public String getToActorName () {
		String tToActorName = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tToActorName.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof CashTransferEffect) {
					tToActorName = ((CashTransferEffect) tEffect).getToActor ().getName ();
				}
			}
		}

		return tToActorName;
	}

	public int getCashAmount () {
		int tCashAmount = -1;

		for (Effect tEffect : effects) {
			if (tCashAmount == -1) {
				if (tEffect instanceof CashTransferEffect) {
					tCashAmount = ((CashTransferEffect) tEffect).getCash ();
				}
			}
		}

		return tCashAmount;
	}
}
