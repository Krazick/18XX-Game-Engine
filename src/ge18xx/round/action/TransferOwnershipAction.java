package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.bank.StartPacketItem;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.CloseCorporationEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import ge18xx.round.action.effects.StartPacketItemSetAvailableEffect;
import ge18xx.round.action.effects.TransferOwnershipEffect;
import ge18xx.utilities.XMLNode;

public class TransferOwnershipAction extends CashTransferAction {
	public final static String NAME = "Transfer Ownership";

	public TransferOwnershipAction () {
		this (NAME);
	}

	public TransferOwnershipAction (String aName) {
		super (aName);
	}

	public TransferOwnershipAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public TransferOwnershipAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addCloseCorporationEffect (Corporation aCorporation, ActorI.ActionStates aOldState,
			ActorI.ActionStates aNewState) {
		CloseCorporationEffect tCloseCorporationEffect;

		tCloseCorporationEffect = new CloseCorporationEffect (aCorporation, aOldState, aNewState);
		addEffect (tCloseCorporationEffect);
	}

	public void addTransferOwnershipEffect (ActorI afromActor, Certificate aCertificate, ActorI aToActor) {
		TransferOwnershipEffect tTransferOwnershipEffect;

		tTransferOwnershipEffect = new TransferOwnershipEffect (afromActor, aCertificate, aToActor);
		addEffect (tTransferOwnershipEffect);
	}

	public void addTransferOwnershipEffect (ActorI afromActor, String aFromName, Certificate aCertificate, ActorI aToActor,
			String aToName) {
		TransferOwnershipEffect tTransferOwnershipEffect;

		tTransferOwnershipEffect = new TransferOwnershipEffect (afromActor, aFromName, aCertificate, aToActor, aToName);
		addEffect (tTransferOwnershipEffect);
	}

	public void addRebuildFormationPanelEffect (ActorI aFromActor) {
		RebuildFormationPanelEffect tRebuildFormationPanelEffect;
		
		tRebuildFormationPanelEffect = new RebuildFormationPanelEffect (aFromActor);
		addEffect (tRebuildFormationPanelEffect);
	}

	public void startPacketItemSetAvailableEffect (ActorI aActor, StartPacketItem aStartPacketItem, boolean aAvailable) {
		StartPacketItemSetAvailableEffect tRemoveStartPacketItemEffect;
		
		tRemoveStartPacketItemEffect = new StartPacketItemSetAvailableEffect (aActor, aStartPacketItem, aAvailable);
		addEffect (tRemoveStartPacketItemEffect);
	}
	
	protected String getFromActorName () {
		String tFromActorName;

		tFromActorName = ActorI.NO_NAME;
		for (Effect tEffect : effects) {
			if (tEffect instanceof TransferOwnershipEffect) {
				tFromActorName = ((TransferOwnershipEffect) tEffect).getActorName ();
			}
		}

		return tFromActorName;
	}

	public String getCompanyAbbrev () {
		String tCompanyAbbrev = "";

		for (Effect tEffect : effects) {
			if (tCompanyAbbrev.equals ("")) {
				if (tEffect instanceof TransferOwnershipEffect) {
					tCompanyAbbrev = ((TransferOwnershipEffect) tEffect).getCompanyAbbrev ();
				}
			}
		}

		return tCompanyAbbrev;
	}

	protected int getShareCountTransferred () {
		int tShareCountTransferred = 0;

		for (Effect tEffect : effects) {
			if (tEffect instanceof TransferOwnershipEffect) {
				tShareCountTransferred++;
			}
		}

		return tShareCountTransferred;
	}

	protected int getSharePercentageTransferred () {
		int tSharePercentage = 0;
		Certificate tCertificate;

		for (Effect tEffect : effects) {
			if (tEffect instanceof TransferOwnershipEffect) {
				tCertificate = ((TransferOwnershipEffect) tEffect).getCertificate ();
				tSharePercentage += tCertificate.getPercentage ();
			}
		}

		return tSharePercentage;
	}

	protected boolean isPresidentTransferred () {
		boolean tIsPresident = false;
		Certificate tCertificate;

		for (Effect tEffect : effects) {
			if ((tEffect instanceof TransferOwnershipEffect) && (!tIsPresident)) {
				tCertificate = ((TransferOwnershipEffect) tEffect).getCertificate ();
				tIsPresident = tCertificate.isPresidentShare ();
			}
		}

		return tIsPresident;
	}

	protected boolean isPrivateTransferred () {
		boolean tIsPrivateTransferred = false;
		Certificate tCertificate;

		for (Effect tEffect : effects) {
			if ((tEffect instanceof TransferOwnershipEffect) && (!tIsPrivateTransferred)) {
				tCertificate = ((TransferOwnershipEffect) tEffect).getCertificate ();
				tIsPrivateTransferred = tCertificate.isAPrivateCompany ();
			}
		}

		return tIsPrivateTransferred;
	}

	protected String getBuySaleSimpleReport (String aVerb1, String aVerb2) {
		String tShareCountTransferred, tSimpleActionReport;
		int tCount, tTotalPrice, tSharePrice, tSharePercentage;
		boolean tIsPresident, tIsPrivate;
		String tFullShareDescription;
		String tPrice;
		String tFromActorName;
		String tCashSentTo;
		String tSentTo;

		tCount = getShareCountTransferred ();
		tSharePercentage = getSharePercentageTransferred ();
		tShareCountTransferred = tCount + " Share";
		if (tCount > 1) {
			tShareCountTransferred += "s";
		}
		tTotalPrice = getCashAmount ();
		tSharePrice = (10 * tTotalPrice) / tSharePercentage;
		tIsPresident = isPresidentTransferred ();
		tIsPrivate = isPrivateTransferred ();
		tFromActorName = getFromActorName ();
		tCashSentTo = getToActorName ();
		tSentTo = " sent to " + tCashSentTo;
		if (tIsPrivate) {
			tFullShareDescription = "the Private Company " + getCompanyAbbrev () + " from " + tFromActorName + ".";
			tPrice = " Total " + aVerb2 + " price of " + Bank.formatCash (tTotalPrice) + tSentTo + ".";
		} else {
			tFullShareDescription = tShareCountTransferred + " (" + tSharePercentage + "%) of " + getCompanyAbbrev ();
			if (tIsPresident) {
				tFullShareDescription += " (President Share)";
			}
			
			tPrice = " for " + Bank.formatCash (tSharePrice) + " per share from " + tFromActorName + "." + " Total "
					+ aVerb2 + " price of " + Bank.formatCash (tTotalPrice) + tSentTo +".";
		}

		tSimpleActionReport = actor.getName () + " " + aVerb1 + " " + tFullShareDescription + tPrice;

		return tSimpleActionReport;
	}
}
