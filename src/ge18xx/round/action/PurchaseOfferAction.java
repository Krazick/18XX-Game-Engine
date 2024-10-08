package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.PurchaseOfferEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class PurchaseOfferAction extends QueryActorAction {
	public final static String NAME = "Purchase Offer";

	public PurchaseOfferAction () {
		this (NAME);
	}

	public PurchaseOfferAction (String aName) {
		super (aName);
	}

	public PurchaseOfferAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PurchaseOfferAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addPurchaseOfferEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount, String aItemType,
			String aItemName) {
		PurchaseOfferEffect tPurchaseOfferEffect;

		tPurchaseOfferEffect = new PurchaseOfferEffect (aFromActor, aToActor, aCashAmount, aItemType, aItemName);
		addEffect (tPurchaseOfferEffect);
	}

	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aOldStatus,
			ActorI.ActionStates aNewStatus) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aOldStatus, aNewStatus);
		addEffect (tChangeCorporationStatusEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		String tFromActorName;

		tFromActorName = getToActorName ();
		tSimpleActionReport = actor.getName () + getPresidentName () + " offered to buy " + getItemName () + " "
				+ getItemType () + " for " + Bank.formatCash (getCashAmount ()) + " from " + tFromActorName + ".";

		return tSimpleActionReport;
	}

	private String getPresidentName () {
		String tPresidentName;
		Corporation tCorporation;

		tPresidentName = GUI.EMPTY_STRING;
		if (actor.isACorporation ()) {
			tCorporation = (Corporation) actor;
			tPresidentName = " (" + tCorporation.getPresidentName () + ")";
		}

		return tPresidentName;
	}

	public int getCashAmount () {
		int tCashAmount;

		tCashAmount = -1;
		for (Effect tEffect : effects) {
			if (tCashAmount == -1) {
				if (tEffect instanceof PurchaseOfferEffect) {
					tCashAmount = ((PurchaseOfferEffect) tEffect).getCash ();
				}
			}
		}

		return tCashAmount;
	}

	private String getItemName () {
		String tItemName;

		tItemName = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tItemName.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof PurchaseOfferEffect) {
					tItemName = ((PurchaseOfferEffect) tEffect).getItemName ();
				}
			}
		}

		return tItemName;
	}

	private String getItemType () {
		String tItemType;

		tItemType = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tItemType.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof PurchaseOfferEffect) {
					tItemType = ((PurchaseOfferEffect) tEffect).getItemType ();
				}
			}
		}

		return tItemType;
	}

	private String getToActorName () {
		String tToActorName;

		tToActorName = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tToActorName.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof PurchaseOfferEffect) {
					tToActorName = ((PurchaseOfferEffect) tEffect).getToActorName ();
				}
			}
		}

		return tToActorName;
	}
}
