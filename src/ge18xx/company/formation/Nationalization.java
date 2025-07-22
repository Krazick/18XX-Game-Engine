package ge18xx.company.formation;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TransferOwnershipAction;
import geUtilities.GUI;
import swingTweaks.KButton;

public class Nationalization extends PlayerFormationPanel {
	private static final long serialVersionUID = 1L;
	public static final String UPGRADE_TO_PRUSSIAN = "Upgrade to Prussian";

	public Nationalization (GameManager aGameManager, FormCompany aFormCompany, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aFormCompany, aPlayer, aActingPresident);
	}
	
	@Override
	public JPanel buildCompanyJPanel (PrivateCompany aPrivateCompany, boolean aActingPlayer) {
		JPanel tPrivateCompanyJPanel;
		
		tPrivateCompanyJPanel = new JPanel ();
		tPrivateCompanyJPanel.setLayout (new BoxLayout (tPrivateCompanyJPanel, BoxLayout.X_AXIS));

		tPrivateCompanyJPanel = super.buildCompanyJPanel (aPrivateCompany, aActingPlayer, tPrivateCompanyJPanel);
		
		buildSpecialButtons (aPrivateCompany, tPrivateCompanyJPanel, aActingPlayer);

		return tPrivateCompanyJPanel;
	}

	@Override
	public JPanel buildCompanyJPanel (MinorCompany aMinorCompany, boolean aActingPlayer) {
		JPanel tMinorCompanyJPanel;
		
		tMinorCompanyJPanel = new JPanel ();
		tMinorCompanyJPanel.setLayout (new BoxLayout (tMinorCompanyJPanel, BoxLayout.X_AXIS));

		tMinorCompanyJPanel = super.buildCompanyJPanel (aMinorCompany, aActingPlayer, tMinorCompanyJPanel);
		
		buildSpecialButtons (aMinorCompany, tMinorCompanyJPanel, aActingPlayer);

		return tMinorCompanyJPanel;
	}

	/* For the 1835 Nationalization, Prussian Formation, the Share Companies never have any impact, 
	 * Don't provide a panel for them.
	 */
	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		
		tShareCompanyJPanel = null;

		return tShareCompanyJPanel;
	}
	
	public String canUpgradeToPrussian (Corporation aCorporation) {
		String tToolTip;
		FormPrussian tFormPrussian;
		
		tFormPrussian = (FormPrussian) formCompany;
		
		tToolTip = GUI.EMPTY_STRING;
		
		if (prussianIsForming (tFormPrussian, aCorporation)) {
			tToolTip = tFormPrussian.formingShareCompany.getName () + " is Forming";
		} else {
			tToolTip = tFormPrussian.formingShareCompany.getName () + " has NOT formed yet";
		}
		
		return tToolTip;
	}

	private boolean prussianIsForming (FormPrussian aFormPrussian, Corporation aCorporation) {
		boolean tPrussianIsForming;
		
		tPrussianIsForming = aFormPrussian.formingShareCompany.isFormed ();
		if (aCorporation.canFormUpgrade ()) {
			tPrussianIsForming = true;
		}
		
		return tPrussianIsForming;
	}

	public void buildSpecialButtons (PrivateCompany aPrivateCompany, JPanel aCompanyJPanel, boolean aActingPlayer) {
		KButton tUpgradeToPrussian;
		String tToolTip;
		FormPrussian tFormPrussian;
		Corporation tCorporation;
		
		tFormPrussian = (FormPrussian) formCompany;
		if (aActingPlayer) {
			tToolTip = canUpgradeToPrussian (aPrivateCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}

		tUpgradeToPrussian = tFormPrussian.buildSpecialButton (UPGRADE_TO_PRUSSIAN, UPGRADE_TO_PRUSSIAN, 
				tToolTip, this);
		if (prussianIsForming (tFormPrussian, aPrivateCompany)) {
			tUpgradeToPrussian.setEnabled (false);
		}
		tCorporation = getExchangeCorporation (aPrivateCompany);
		if (tCorporation != Corporation.NO_CORPORATION) {
			aCompanyJPanel.add (tUpgradeToPrussian);
			aCompanyJPanel.add (Box.createHorizontalStrut (10));
			aPrivateCompany.addSpecialButton (tUpgradeToPrussian);
		}
	}
	
	public void buildSpecialButtons (MinorCompany aMinorCompany, JPanel aCompanyJPanel, boolean aActingPlayer) {
		KButton tUpgradeToPrussian;
		String tToolTip;
		String tCorpAbbrev;
		String tFullCommand;
		FormPrussian tFormPrussian;
		Corporation tCorporation;
		
		tFormPrussian = (FormPrussian) formCompany;
		if (aActingPlayer) {
			tToolTip = canUpgradeToPrussian (aMinorCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tCorporation = getExchangeCorporation (aMinorCompany);
		tCorpAbbrev = tCorporation.getAbbrev ();
		tFullCommand = UPGRADE_TO_PRUSSIAN + " " + tCorpAbbrev;
		tUpgradeToPrussian = tFormPrussian.buildSpecialButton (UPGRADE_TO_PRUSSIAN, tFullCommand, 
				tToolTip, this);
		if (prussianIsForming (tFormPrussian, aMinorCompany)) {
			if (aActingPlayer) {
				tUpgradeToPrussian.setEnabled (true);
			} else {
				tUpgradeToPrussian.setEnabled (false);
			}
		} else {
			tUpgradeToPrussian.setEnabled (false);
		}
		if (tCorporation != Corporation.NO_CORPORATION) {
			aCompanyJPanel.add (tUpgradeToPrussian);
			aCompanyJPanel.add (Box.createHorizontalStrut (10));
			aMinorCompany.addSpecialButton (tUpgradeToPrussian);
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		super.actionPerformed (aEvent);
		
		String tActionCommand;
		MinorCompany tMinorCompany;
		KButton tActivatedButton;
//		FormPrussian tFormPrussian;
		
//		tFormPrussian = (FormPrussian) formCompany;
		
		tActionCommand = aEvent.getActionCommand ();
		tActivatedButton = getActivatedButton (aEvent);
		if (tActivatedButton != GUI.NO_BUTTON) {
			tMinorCompany = findMinorCompany (tActivatedButton);
			if (tMinorCompany != MinorCompany.NO_MINOR_COMPANY) {
				if (tActionCommand.startsWith (UPGRADE_TO_PRUSSIAN)) {
					handleUpgradeToPrussian (tMinorCompany);
				}
			}
//			if (tActionCommand.equals (FormCGR.FOLD)) {
//				tFormPrussian.handleFoldIntoFormingCompany ();
//			} else if (tActionCommand.equals (CONTINUE)) {
//				tFormPrussian.handleFormationComplete ();
//			}
		}
		
		checkAndHandleRoundEnds ();
	}

	private void handleUpgradeToPrussian (MinorCompany tMinorCompany) {
		TransferOwnershipAction tTransferOwnershipAction1;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		String tMinorCompanyAbbrev;
		String tFormingAbbrev;
		Portfolio tPlayerPortfolio;
		Portfolio tBankPortfolio;
		int tCertificateCount;
		int tCertificateIndex;
		int tPercentage;
		Certificate tCertificate;
		Certificate tFormedCertificate;
		ShareCompany tFormingShareCompany;
		FormPrussian tFormPrussian;
		Bank tBank;
		
		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tFormPrussian = (FormPrussian) formCompany;
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tRoundID = tRoundManager.getCurrentRoundOf ();
		tTransferOwnershipAction1 = new TransferOwnershipAction (tRoundType, tRoundID, player);
		tPlayerPortfolio = player.getPortfolio ();
		tMinorCompanyAbbrev = tMinorCompany.getAbbrev ();
		tCertificateCount = tPlayerPortfolio.getCertificateTotalCount ();
		for (tCertificateIndex = tCertificateCount - 1; tCertificateIndex >= 0; tCertificateIndex--) {
			tCertificate = tPlayerPortfolio.getCertificate (tCertificateIndex);
			if (tCertificate.getCompanyAbbrev ().equals (tMinorCompanyAbbrev)) {
				transferShareToClosed (player, tCertificate, tTransferOwnershipAction1);
				System.out.println ("Transfer the Certificate for " + tMinorCompanyAbbrev + " with " + 
							tCertificate.getPercentage () + " % to the Closed Portfolio");
			}
		}
		tFormedCertificate = Certificate.NO_CERTIFICATE;
		tFormingAbbrev = getFormingAbbrev ();
		tPercentage = tFormPrussian.getPercentageForExchange ();

		tFormedCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
		if (tFormedCertificate != Certificate.NO_CERTIFICATE) {
			transferShare (tBank, Bank.IPO, player, tFormedCertificate, tTransferOwnershipAction1);
		} else {
			System.err.println ("No certificate available with All Players Total Exchange Count 1");
		}
		updateCorporationOwnership (tFormedCertificate);
		tFormingShareCompany = tFormPrussian.getFormingCompany ();
		transferAllCash (tMinorCompany, tFormingShareCompany, tTransferOwnershipAction1);
		transferAllTrains (tMinorCompany, tFormingShareCompany, tTransferOwnershipAction1);
		tFormPrussian.rebuildFormationPanel (tFormPrussian.getCurrentPlayerIndex ());
		gameManager.addAction (tTransferOwnershipAction1);
	}
}
