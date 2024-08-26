package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.bank.StartPacketItem;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.License;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.round.action.effects.CloseCorporationEffect;
import ge18xx.round.action.effects.CreateNewCertificateEffect;
import ge18xx.round.action.effects.DeleteCertificateEffect;
import ge18xx.round.action.effects.Effect;
//import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import ge18xx.round.action.effects.RemoveLicenseEffect;
import ge18xx.round.action.effects.SetNotificationEffect;
import ge18xx.round.action.effects.StartPacketItemSetAvailableEffect;
import ge18xx.round.action.effects.TransferOwnershipEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

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

	public void addTransferOwnershipEffect (ActorI afromActor, String aFromNickName, Certificate aCertificate, 
									ActorI aToActor, String aToNickName) {
		TransferOwnershipEffect tTransferOwnershipEffect;

		tTransferOwnershipEffect = new TransferOwnershipEffect (afromActor, aFromNickName, aCertificate, 
									aToActor, aToNickName);
		addEffect (tTransferOwnershipEffect);
	}
	
	public void addSetNotificationEffect (ActorI aActor, String aNotificationText) {
		SetNotificationEffect tSetNotificationEffect;
		
		tSetNotificationEffect = new SetNotificationEffect (aActor, aNotificationText);
		addEffect (tSetNotificationEffect);
	}

	public void addAddLicenseEffect (ActorI aFromActor, ActorI aToActor, License aLicense) {
		AddLicenseEffect tAddLicenseEffect;
		
		tAddLicenseEffect = new AddLicenseEffect (aFromActor, aToActor, aLicense);
		addEffect (tAddLicenseEffect);
	}

	public void addRemoveLicenseEffect (ActorI aFromActor, ActorI aToActor, License aLicense) {
		RemoveLicenseEffect tRemoveLicenseEffect;
		
		tRemoveLicenseEffect = new RemoveLicenseEffect (aFromActor, aToActor, aLicense);
		addEffect (tRemoveLicenseEffect);
	}
	
	public void addCreateNewCertificateEffet (ActorI aFromActor, Certificate aCertificate, ActorI aToActor) {
		CreateNewCertificateEffect tCreateNewCertificateEffet;
		
		tCreateNewCertificateEffet = new CreateNewCertificateEffect (aFromActor, aCertificate, aToActor);
		addEffect (tCreateNewCertificateEffet);
	}
	
	public void addDeleteCertificateEffet (ActorI aFromActor, Certificate aCertificate, ActorI aToActor) {
		DeleteCertificateEffect tDeleteCertificateEffect;
		
		tDeleteCertificateEffect = new DeleteCertificateEffect (aFromActor, aCertificate, aToActor);
		addEffect (tDeleteCertificateEffect);
	}

	public void addStartPacketItemSetAvailableEffect (ActorI aActor, StartPacketItem aStartPacketItem, 
										boolean aAvailable) {
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
		String tCompanyAbbrev;

		tCompanyAbbrev = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tCompanyAbbrev.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof TransferOwnershipEffect) {
					tCompanyAbbrev = ((TransferOwnershipEffect) tEffect).getCompanyAbbrev ();
				}
			}
		}

		return tCompanyAbbrev;
	}

	protected int getShareCountTransferred () {
		int tShareCountTransferred;

		tShareCountTransferred = 0;
		for (Effect tEffect : effects) {
			if (tEffect instanceof TransferOwnershipEffect) {
				tShareCountTransferred++;
			}
		}

		return tShareCountTransferred;
	}

	protected int getSharePercentageTransferred () {
		int tSharePercentage;
		Certificate tCertificate;

		tSharePercentage = 0;
		for (Effect tEffect : effects) {
			if (tEffect instanceof TransferOwnershipEffect) {
				tCertificate = ((TransferOwnershipEffect) tEffect).getCertificate ();
				tSharePercentage += tCertificate.getPercentage ();
			}
		}

		return tSharePercentage;
	}

	protected boolean isPresidentTransferred () {
		boolean tIsPresident;
		Certificate tCertificate;

		tIsPresident = false;
		for (Effect tEffect : effects) {
			if ((tEffect instanceof TransferOwnershipEffect) && (!tIsPresident)) {
				tCertificate = ((TransferOwnershipEffect) tEffect).getCertificate ();
				tIsPresident = tCertificate.isPresidentShare ();
			}
		}

		return tIsPresident;
	}

	protected boolean isPrivateTransferred () {
		boolean tIsPrivateTransferred;
		Certificate tCertificate;

		tIsPrivateTransferred = false;
		for (Effect tEffect : effects) {
			if ((tEffect instanceof TransferOwnershipEffect) && (!tIsPrivateTransferred)) {
				tCertificate = ((TransferOwnershipEffect) tEffect).getCertificate ();
				tIsPrivateTransferred = tCertificate.isAPrivateCompany ();
			}
		}

		return tIsPrivateTransferred;
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = GUI.EMPTY_STRING;
		tSimpleActionReport = actor.getName () + " transfers items between actors";

		return tSimpleActionReport;
	}

	protected String getBuySaleSimpleReport (String aVerb1, String aVerb2) {
		int tCount;
		int tTotalPrice;
		int tSharePrice;
		int tSharePercentage;
		boolean tIsPresident;
		boolean tIsPrivate;
		String tShareCountTransferred;
		String tSimpleActionReport;
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
