package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ResponseOfferAction;
import ge18xx.round.action.effects.PurchaseOfferEffect;

public class PurchaseOfferFrame extends QueryFrame {
	private static final long serialVersionUID = 1L;
	String itemType;
	String itemName;

	public PurchaseOfferFrame (PurchaseOfferEffect aPurchaseOfferEffect, RoundManager aRoundManager, String aItemType,
			String aItemName) {
		super (aRoundManager, aPurchaseOfferEffect);

		String tPlayerName;

		setItemType (aItemType);
		setItemName (aItemName);
		tPlayerName = aRoundManager.getClientUserName ();
		setTitle ("Purchase Offer for " + tPlayerName);
	}

	public String getItemType () {
		return itemType;
	}

	public String getItemName () {
		return itemName;
	}

	public void setItemType (String aItemType) {
		itemType = aItemType;
	}

	public void setItemName (String aItemName) {
		itemName = aItemName;
	}

	@Override
	protected void setOfferTopPanel () {
		String tOffer1;
		String tOffer2;
		String tPresidentName;
		Corporation tOperatingCompany;
		PurchaseOfferEffect tPurchaseOfferEffect;

		if (toEffect instanceof PurchaseOfferEffect) {
			tPurchaseOfferEffect = (PurchaseOfferEffect) toEffect;
			tOperatingCompany = roundManager.getOperatingCompany ();
			tPresidentName = tOperatingCompany.getPresidentName ();
			tOffer1 = "The President of " + tPurchaseOfferEffect.getActorName () + " (" + tPresidentName
					+ ") offers to buy a ";
			tOffer2 = tPurchaseOfferEffect.getItemName () + " " + tPurchaseOfferEffect.getItemType () + " for "
					+ Bank.formatCash (tPurchaseOfferEffect.getCash ()) + " from "
					+ tPurchaseOfferEffect.getToActor ().getName () + ".";
			buildOfferTopPanel (tOffer1, tOffer2);
		}
	}

	@Override
	protected void addResponseOfferEffect (ResponseOfferAction aResponseOfferAction, ActorI aFromActor,
			ActorI aToActor, boolean aResponse) {
		aResponseOfferAction.addResponseOfferEffect (aFromActor, aToActor, aResponse, itemType, itemName);
	}
}
