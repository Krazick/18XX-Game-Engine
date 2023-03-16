package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.License;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.utilities.XMLNode;

public class BuyLicenseAction extends TransferOwnershipAction {
	public final static String NAME = "Buy License";

	public BuyLicenseAction () {
		this (NAME);
	}

	public BuyLicenseAction (String aName) {
		super (aName);
	}

	public BuyLicenseAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public BuyLicenseAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	protected String getFromActorName () {
		String tFromActorName;

		tFromActorName = ActorI.NO_NAME;
		for (Effect tEffect : effects) {
			if (tEffect instanceof AddLicenseEffect) {
				tFromActorName = ((AddLicenseEffect) tEffect).getActorName ();
			}
		}

		if (tFromActorName == ActorI.NO_NAME) {
			tFromActorName = super.getFromActorName ();
		}
		
		return tFromActorName;
	}

	public String getLicenseName () {
		String tLicenseName = "";
		AddLicenseEffect tAddLicenseEffect;
		License tLicense;

		for (Effect tEffect : effects) {
			if (tLicenseName.equals ("")) {
				if (tEffect instanceof AddLicenseEffect) {
					tAddLicenseEffect = (AddLicenseEffect) tEffect;
					tLicense = tAddLicenseEffect.getLicense ();
					tLicenseName = tLicense.getLicenseName ();
				}
			}
		}

		return tLicenseName;
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		String tFromActorName;
		String tToActorName;
		String tLicenseName;
		int tTotalPrice;
		
		tFromActorName = getFromActorName ();
		tToActorName = getToActorName ();
		tTotalPrice = getCashAmount ();
		tLicenseName = getLicenseName ();
		tSimpleActionReport = tToActorName + " bought a " + tLicenseName + " from " + tFromActorName + " for " +
								Bank.formatCash (tTotalPrice);
		
		return tSimpleActionReport;
	}

}
