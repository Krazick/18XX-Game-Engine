package ge18xx.company.special;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.utilities.GUI;

public class ShareExchange extends PlayerFormationPhase {
	public static final String EXCHANGE = "Exchange Shares";

	private static final long serialVersionUID = 1L;
	boolean sharesExchanged;
	boolean oneShareToBankPool;
	JButton exchange;
	int foldingCompanyCount;
	int totalExchangeCount;

	public ShareExchange (GameManager aGameManager, FormationPhase aLoanRepayment, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aLoanRepayment, aPlayer, aActingPresident);
		setSharesExchanged (false);
	}

	public void setSharesExchanged (boolean aSharesExchanged) {
		sharesExchanged = aSharesExchanged;
	}
	
	@Override
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
		JPanel tPlayerShareExchangePanel;
		JLabel tShareExchangeLabel;
		JLabel tShareExchangeInfo;
		String tShareExchangeText;
		String tToolTip;
		
		tPlayerShareExchangePanel = new JPanel ();
		tPlayerShareExchangePanel.setLayout (new BoxLayout (tPlayerShareExchangePanel, BoxLayout.Y_AXIS));
		tPlayerShareExchangePanel.add (Box.createVerticalStrut (5));
		
		tShareExchangeLabel = new JLabel ("Player Share Exchange");
		tPlayerShareExchangePanel.add (tShareExchangeLabel);
		tPlayerShareExchangePanel.add (Box.createVerticalStrut (5));
	
		tShareExchangeInfo = new JLabel ();
		tShareExchangeText = buildShareExchangeText (aPlayerPortfolio);
		tShareExchangeInfo.setText (tShareExchangeText);
	
		tPlayerShareExchangePanel.add (tShareExchangeInfo);
		tPlayerShareExchangePanel.add (Box.createVerticalStrut (5));
	
		if (	foldingCompanyCount == 0) {
			tToolTip = "No Shares will be exchanged for " + formationPhase.getFormingCompanyAbbrev ();;
		} else if (! aActingPlayer) {
			tToolTip = NOT_ACTING_PRESIDENT;
		} else {
			tToolTip = GUI.EMPTY_STRING;
		}
	
		exchange = formationPhase.buildSpecialButton (EXCHANGE, EXCHANGE, tToolTip, this);
	
		tPlayerShareExchangePanel.add (exchange);
		tPlayerShareExchangePanel.add (Box.createVerticalStrut (5));
		
		return tPlayerShareExchangePanel;
	}

	public String buildShareExchangeText (Portfolio aPlayerPortfolio) {
		String tShareExchangeText;
		String tShareExchangePhrase;
		int tCertificateCount;
		int tCertificateIndex;
		int tShareExchangeCount;
		int tTotalShareCount;
	
		int tFoldingCompanyIndex;
		String tShareCompanyAbbrev;
		String tFormingCompanyAbbrev;
		List<String> tShareExchange;
		ShareCompany tShareCompany;
		Certificate tCertificate;
		
		tCertificateCount = aPlayerPortfolio.getCertificateTotalCount ();
		tShareExchangeText = GUI.EMPTY_STRING;
		tTotalShareCount = 0;
		shareCompaniesHandled = new LinkedList<String> ();
		tShareExchange = new LinkedList<String> ();
	
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = aPlayerPortfolio.getCertificate (tCertificateIndex);
			if (tCertificate.isAShareCompany ()) {
				tShareCompany = tCertificate.getShareCompany ();
				tShareCompanyAbbrev = tShareCompany.getAbbrev ();
				if ( !(shareCompaniesHandled.contains (tShareCompanyAbbrev))) {
					if (tShareCompany.willFold ()) {
						tShareExchangeCount = aPlayerPortfolio.getShareCountFor (tShareCompany);
						tTotalShareCount += tShareExchangeCount;
						if (tShareExchangeCount == 1) {
							tShareExchangePhrase = tShareExchangeCount + " Share ";
						} else {
							tShareExchangePhrase = tShareExchangeCount + " Shares ";
						}
						tShareExchangePhrase += " of " + tShareCompanyAbbrev;
						tShareExchange.add (tShareExchangePhrase);
						shareCompaniesHandled.add (tShareCompanyAbbrev);
					}
				}
			}
		}
		totalExchangeCount = tTotalShareCount/2;
		if (tTotalShareCount % 2 != 0) {
			oneShareToBankPool = true;
		}
		foldingCompanyCount = tShareExchange.size ();
		tFormingCompanyAbbrev = formationPhase.getFormingCompanyAbbrev ();
		tShareExchangeText = GUI.EMPTY_STRING;
		if  (tTotalShareCount == 0) {
			tShareExchangeText = "No Shares will be exchanged for " + tFormingCompanyAbbrev;
		} else {
			if (foldingCompanyCount == 1) {
				tShareExchangeText = tShareExchange.get (0);
			} else {
				for (tFoldingCompanyIndex = 0; tFoldingCompanyIndex < foldingCompanyCount; tFoldingCompanyIndex++) {
					if (tFoldingCompanyIndex == foldingCompanyCount) {
						tShareExchangeText += " and ";
					} else {
						tShareExchangeText += ", ";
					}
					tShareExchangeText += tShareExchange.get (tFoldingCompanyIndex);
				}
			}
			tShareExchangeText += " will be exchanged for " + totalExchangeCount + " Shares of " + tFormingCompanyAbbrev;
		}
		
		return tShareExchangeText;
	}

	public void handleShareExchange () {
		TransferOwnershipAction tTransferOwnershipAction;
		BankPool tBankPool;
		Bank tBank;
		Portfolio tBankPortfolio;
		Portfolio tPlayerPortfolio;
		ShareCompany tShareCompany;
		Corporation tFormingCompany;
		Certificate tCertificate;
		Certificate tFoldedCertificate;
		int tCertificateIndex;
		int tCertificateCount;
		int tFoldingIndex;
		int tFormingCompanyID;
		int tPercentage;
		String tOperatingRoundID;
		String tFormingAbbrev;
		
		tBankPool = gameManager.getBankPool ();
		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tPlayerPortfolio = player.getPortfolio ();
		tCertificateCount = tPlayerPortfolio.getCertificateTotalCount ();
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, player);
		for (String tCompanyAbbrev : shareCompaniesHandled) {
			tShareCompany = gameManager.getShareCompany (tCompanyAbbrev);
			tCertificateCount = tPlayerPortfolio.getCertificateTotalCount ();
			for (tCertificateIndex = tCertificateCount - 1; tCertificateIndex >= 0; tCertificateIndex--) {
				tCertificate = tPlayerPortfolio.getCertificate (tCertificateIndex);
				if (tCertificate.getCompanyAbbrev ().equals (tCompanyAbbrev)) {
					if ((tCertificate.getPercentage () == PhaseInfo.STANDARD_SHARE_SIZE)  && oneShareToBankPool) {
						transferShare (player, tBankPool, tCertificate, tTransferOwnershipAction);
						oneShareToBankPool = false;
					} else {
						transferShare (player, tShareCompany, tCertificate, tTransferOwnershipAction);
					}
				}
			}
		}
		if (totalExchangeCount > 0) {
			tFormingCompanyID = gameManager.getFormingCompanyId ();
			tFormingCompany = gameManager.getCorporationByID (tFormingCompanyID);
			tFormingAbbrev = tFormingCompany.getAbbrev ();
			if (formationPhase.getShareFoldCount () > 20) {
				tPercentage = PhaseInfo.STANDARD_SHARE_SIZE/2;
			} else {
				tPercentage = PhaseInfo.STANDARD_SHARE_SIZE;
			}
			System.out.println ("All Players Total Exchange Count " +  formationPhase.getShareFoldCount ());
			for (tFoldingIndex = 0; tFoldingIndex < totalExchangeCount; tFoldingIndex++) {
				tFoldedCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
				if (tFoldedCertificate != Certificate.NO_CERTIFICATE) {
					transferShare (tBank, player, tFoldedCertificate, tTransferOwnershipAction);
				} else {
					System.err.println ("No certificate available with All Players Total Exchange Count " + 
									formationPhase.getShareFoldCount ());
				}
			}
			if (! formationPhase.getFormingPresidentAssigned ()) {
				assignPresident (tBankPortfolio, tPercentage, tFormingCompany, tTransferOwnershipAction);
			}
		}
		if (tTransferOwnershipAction.getEffectCount () > 0) {
			tTransferOwnershipAction.printActionReport (gameManager.getRoundManager ());
		} else {
			System.err.println ("No Effects in the Action");
		}
		setSharesExchanged (true);
		exchange.setEnabled (false);
		exchange.setToolTipText ("President has not completed all share exchanges");
		updateDoneButton (true);
		formationPhase.rebuildSpecialPanel (formationPhase.getCurrentPlayerIndex ());
	}

	public void handleOpenMarketShareExchange () {
		
	}
	
	public void assignPresident (Portfolio aBankPortfolio, int aPercentage, Corporation aFormingCompany, 
					TransferOwnershipAction aTransferOwnershipAction) {
		Certificate tPresidentCertificate;
		Certificate tHalfPresidentCertificate;
		Certificate tExchangeCertificate;
		Portfolio tPlayerPortfolio;
		Bank tBank;
		int tOwnedPercentage;
		String tFormingAbbrev;
		
		tBank = gameManager.getBank ();
		tPlayerPortfolio = player.getPortfolio ();
		tOwnedPercentage = tPlayerPortfolio.getPercentageFor (aFormingCompany);
		tFormingAbbrev = aFormingCompany.getAbbrev ();
		if (tOwnedPercentage >= aPercentage) {
			tPresidentCertificate = aBankPortfolio.getCertificate (tFormingAbbrev, aPercentage * 2, true);
			if (tPresidentCertificate != Certificate.NO_CERTIFICATE) {
				tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
				transferShare (player, tBank, tExchangeCertificate, aTransferOwnershipAction);
				tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
				transferShare (player, tBank, tExchangeCertificate, aTransferOwnershipAction);
				transferShare (tBank, player, tPresidentCertificate, aTransferOwnershipAction);
				formationPhase.setFormingPresidentAssigned  (true);
			} else {
				tHalfPresidentCertificate = aBankPortfolio.getCertificate (tFormingAbbrev, aPercentage, true);
				tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
				transferShare (player, tBank, tExchangeCertificate, aTransferOwnershipAction);
				transferShare (tBank, player, tHalfPresidentCertificate, aTransferOwnershipAction);		
				formationPhase.setFormingPresidentAssigned  (true);
			}
		}
	}
	
	public void transferShare (PortfolioHolderI aFromActor, ActorI aToActor, Certificate aCertificate, 
					TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tPlayerPortfolio;
		Portfolio tToActorPortfolio;
		PortfolioHolderI tToHolder;
	
		tPlayerPortfolio = aFromActor.getPortfolio ();
		tToHolder = (PortfolioHolderI) aToActor;
		tToActorPortfolio = tToHolder.getPortfolio ();
		tToActorPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, aCertificate, aToActor);
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (EXCHANGE)) {
			handleShareExchange ();
		} else if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
		} else {
			super.actionPerformed (aEvent);
		}
	}
	
	public boolean sharesExchanged () {
		return sharesExchanged;
	}
	
	@Override
	public void updateDoneButton () {
		String tToolTip;

		if (sharesExchanged ()) {
			done.setEnabled (true);
			tToolTip = "President already completed all share exchanges";
			done.setToolTipText (tToolTip);
		} else if (foldingCompanyCount == 0) {
			done.setEnabled (true);
			tToolTip = "President has no shares to exchanges";
			done.setToolTipText (tToolTip);
		} else {
			done.setEnabled (false);
			tToolTip = "President has not completed all share exchanges";
			done.setToolTipText (tToolTip);
		}
	}
	
	@Override
	public void handlePlayerDone () {
		super.handlePlayerDone ();
		if (formationPhase.getAllPlayerSharesHandled ()) {
			System.out.println ("All Players Done, now handle Open Market");
		}
	}
}
