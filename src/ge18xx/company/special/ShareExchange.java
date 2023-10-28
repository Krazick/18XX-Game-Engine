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
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ShareExchangeFinishedAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.utilities.GUI;

public class ShareExchange extends PlayerFormationPhase {
	public static final String EXCHANGE = "Exchange Shares";
	public static final int SHARES_NEEDED_FOR_2ND_ISSUE = 21;
	private static final long serialVersionUID = 1L;
	boolean sharesExchanged;
	boolean oneShareToBankPool;
	JButton exchange;
	int foldingCompanyCount;
	int totalExchangeCount;

	public ShareExchange (GameManager aGameManager, FormationPhase aShareExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aShareExchange, aPlayer, aActingPresident);
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
		TransferOwnershipAction tTransferOwnershipAction1;
		TransferOwnershipAction tTransferOwnershipAction2;
		BankPool tBankPool;
		Bank tBank;
		Portfolio tBankPortfolio;
		Portfolio tPlayerPortfolio;

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
		tTransferOwnershipAction1 = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
							tOperatingRoundID, player);
		tTransferOwnershipAction2 = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		for (String tCompanyAbbrev : shareCompaniesHandled) {
			tCertificateCount = tPlayerPortfolio.getCertificateTotalCount ();
			for (tCertificateIndex = tCertificateCount - 1; tCertificateIndex >= 0; tCertificateIndex--) {
				tCertificate = tPlayerPortfolio.getCertificate (tCertificateIndex);
				if (tCertificate.getCompanyAbbrev ().equals (tCompanyAbbrev)) {
					if ((tCertificate.getPercentage () == PhaseInfo.STANDARD_SHARE_SIZE)  && oneShareToBankPool) {
						transferShare (player, tBankPool, tCertificate, tTransferOwnershipAction1);
						oneShareToBankPool = false;
					} else {
						transferShareToClosed (player, tCertificate, tTransferOwnershipAction1);
					}
				}
			}
		}
		if (totalExchangeCount > 0) {
			tFormingCompanyID = gameManager.getFormingCompanyId ();
			tFormingCompany = gameManager.getCorporationByID (tFormingCompanyID);
			tFormingAbbrev = getFormingAbbrev ();
			tPercentage = getPercentageForExchange ();
			System.out.println ("All Players Total Exchange Count " +  formationPhase.getShareFoldCount ());
			for (tFoldingIndex = 0; tFoldingIndex < totalExchangeCount; tFoldingIndex++) {
				tFoldedCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
				if (tFoldedCertificate != Certificate.NO_CERTIFICATE) {
					transferShare (tBank, Bank.IPO, player, tFoldedCertificate, tTransferOwnershipAction1);
				} else {
					System.err.println ("No certificate available with All Players Total Exchange Count " + 
									formationPhase.getShareFoldCount ());
				}
			}
			if (! formationPhase.getFormingPresidentAssigned ()) {
				assignPresident (tBankPortfolio, tPercentage, tFormingCompany, tTransferOwnershipAction2);
			}
		}
		if (tTransferOwnershipAction1.getEffectCount () > 0) {
			tTransferOwnershipAction1.printActionReport (gameManager.getRoundManager ());
		} else {
			System.err.println ("No Effects in the Action");
		}
		setSharesExchanged (true);
		exchange.setEnabled (false);
		exchange.setToolTipText ("President has not completed all share exchanges");
		updateDoneButton (true);
		formationPhase.rebuildSpecialPanel (formationPhase.getCurrentPlayerIndex ());
		gameManager.addAction (tTransferOwnershipAction1);
		if (tTransferOwnershipAction2.getEffectCount () > 0) {
			tTransferOwnershipAction2.setChainToPrevious (true);
			gameManager.addAction (tTransferOwnershipAction2);
		}
	}

	public String getFormingAbbrev () {
		Corporation tFormingCompany;
		int tFormingCompanyID;
		String tFormingAbbrev;
	
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tFormingCompany = gameManager.getCorporationByID (tFormingCompanyID);
		tFormingAbbrev = tFormingCompany.getAbbrev ();

		return tFormingAbbrev;
	}
	
	public int getPercentageForExchange () {
		int tPercentage;
		
		if (formationPhase.getShareFoldCount () > SHARES_NEEDED_FOR_2ND_ISSUE) {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE/2;
		} else {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE;
		}
		return tPercentage;
	}

	public void handleOpenMarketShareExchange () {
		BankPool tBankPool;
		Portfolio tBankPoolPortfolio;
		int tPortfolioIndex;
		int tCertificateCount;
		int tExchangeCount;
		int tNewCount;
		int tNewIndex;
		int tPercentage;
		String tOperatingRoundID;
		String tFormingAbbrev;
		Bank tBank;
		Portfolio tBankPortfolio;
		Corporation tCorporation;
		Certificate tCertificate;
		TransferOwnershipAction tTransferOwnershipAction;
		
		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tBankPool = gameManager.getBankPool ();
		tBankPoolPortfolio = tBankPool.getPortfolio ();
		tCertificateCount = tBankPoolPortfolio.getCertificateTotalCount ();
		if (tCertificateCount > 0) {
			tExchangeCount = 0;
			tPercentage = getPercentageForExchange ();
			tOperatingRoundID = gameManager.getOperatingRoundID ();

			tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
								tOperatingRoundID, player);

			for (tPortfolioIndex = (tCertificateCount - 1); tPortfolioIndex >= 0; tPortfolioIndex--) {
				tCertificate = tBankPoolPortfolio.getCertificate (tPortfolioIndex);
				tCorporation = tCertificate.getCorporation ();
				if (tCorporation.willFold ()) {
					transferShareToClosed (tBankPool, tCertificate, tTransferOwnershipAction);
					tExchangeCount++;
				}
			}
			tNewCount = tExchangeCount / 2;
			if (tNewCount > 0) {
				for (tNewIndex = 0; tNewIndex < tNewCount; tNewIndex++) {
					tFormingAbbrev = getFormingAbbrev ();

					tCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
					if (tCertificate != Certificate.NO_CERTIFICATE) {
						transferShare (tBank, tBankPool, tCertificate, tTransferOwnershipAction);
					} else {
						System.err.println ("No certificate available with All Players Total Exchange Count " + 
										formationPhase.getShareFoldCount ());
					}

				}
			}
			formationPhase.rebuildSpecialPanel ();
			tTransferOwnershipAction.setChainToPrevious (true);
			gameManager.addAction (tTransferOwnershipAction);
		}
	}
	
	public void confirmFormingPresident () {
		PlayerManager tPlayerManager;
		Player tCurrentPresident;
		PortfolioHolderI tCurrentHolder;
		TransferOwnershipAction tTransferOwnershipAction;
		String tOperatingRoundID;
		Corporation tCorporation;
		ShareCompany tFormingCompany;
		int tFormingCompanyID;
	
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tCorporation = gameManager.getCorporationByID (tFormingCompanyID);
		if (tCorporation.isAShareCompany ()) {
			tFormingCompany = (ShareCompany) tCorporation;
			tPlayerManager = gameManager.getPlayerManager ();
			tCurrentHolder = tFormingCompany.getPresident ();
			if (tCurrentHolder.isAPlayer ()) {
				tCurrentPresident = (Player) tCurrentHolder;
				tOperatingRoundID = gameManager.getOperatingRoundID ();
				tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
						tOperatingRoundID, player);
				tPlayerManager.handlePresidentialTransfer (tTransferOwnershipAction, tFormingCompany, tCurrentPresident);
				tTransferOwnershipAction.setChainToPrevious (true);
				gameManager.addAction (tTransferOwnershipAction);
				formationPhase.rebuildSpecialPanel ();
			} else {
				System.err.println ("The Current President is not a Player");
				// No Player holds enough to be President
			}
		} else {
			System.err.println ("The Forming Company ID found is NOT a Share Company");
		}
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
				transferShare (tBank, Bank.IPO, player, tPresidentCertificate, aTransferOwnershipAction);
				tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
				transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
				tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
				transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
				formationPhase.setFormingPresidentAssigned  (true);
			} else {
				tHalfPresidentCertificate = aBankPortfolio.getCertificate (tFormingAbbrev, aPercentage, true);
				tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
				transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
				transferShare (tBank, Bank.IPO, player, tHalfPresidentCertificate, aTransferOwnershipAction);		
				formationPhase.setFormingPresidentAssigned  (true);
			}
		}
	}
	
	public void handleIPOShareClosing () {
		Portfolio tBankIPOPortfolio;
		ShareCompany tShareCompany;
		TransferOwnershipAction tTransferOwnershipAction;
		Certificate tCertificate;
		String tOperatingRoundID;
		String tFromName;
		Bank tBank;
		int tCertificatesTransferred;
		int tIPOIndex;
		int tIPOCount;
		
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tBank = gameManager.getBank ();
		tBankIPOPortfolio = tBank.getPortfolio ();
		tIPOCount = tBankIPOPortfolio.getCertificateTotalCount ();
		tFromName = Bank.IPO;
		tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tCertificatesTransferred = 0;
		for (tIPOIndex = tIPOCount - 1; tIPOIndex >= 0; tIPOIndex--) {
			tCertificate = tBankIPOPortfolio.getCertificate (tIPOIndex);
			if (tCertificate.isAShareCompany ()) {
				tShareCompany = tCertificate.getShareCompany ();
				if (tShareCompany.willFold ()) {
					transferShareToClosed (tBank, tFromName, tCertificate, tTransferOwnershipAction);
					tCertificatesTransferred++;
				}
			}
		}
		if (tCertificatesTransferred > 0) {
			gameManager.addAction (tTransferOwnershipAction);
		}
		System.out.println ("\n======Bank Portfolio after IPO Closing");
		tBank.printInfo ();
	}
	
	public void closeFormingCompanyAlternateIssue () {
		int tPercentage;
		int tPrezPercentage;
		int tFormingCompanyID;
		boolean tMoreCerts;
		Portfolio tBankIPOPortfolio;
		ShareCompany tFormingCompany;
		Corporation tCorporation;
		TransferOwnershipAction tTransferOwnershipAction;
		Certificate tCertificate;
		String tOperatingRoundID;
		String tFormingAbbrev;
		String tFromName;
		Bank tBank;

		tPercentage = getPercentageForExchange ()/2;
		tPrezPercentage = tPercentage * 2;
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tBank = gameManager.getBank ();
		tBankIPOPortfolio = tBank.getPortfolio ();
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tCorporation = (ShareCompany)gameManager.getCorporationByID (tFormingCompanyID);
		if (tCorporation.isAShareCompany ()) {
			tFormingCompany = (ShareCompany) tCorporation;
			tFormingAbbrev = tFormingCompany.getAbbrev ();
			tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
					tOperatingRoundID, player);
			tCertificate = Certificate.NO_CERTIFICATE;
			tMoreCerts = true;
			tFromName = Bank.IPO;
			while (tMoreCerts) {
				tCertificate = tBankIPOPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
				if (tCertificate != Certificate.NO_CERTIFICATE) {
					transferShareToClosed (tBank, tFromName, tCertificate, tTransferOwnershipAction);
				} else {
					tMoreCerts = false;
				}
			}
			tCertificate = tBankIPOPortfolio.getCertificate (tFormingAbbrev, tPrezPercentage, true);
			transferShareToClosed (tBank, tCertificate, tTransferOwnershipAction);
		}
		System.out.println ("\n****** Bank Portfolio after Alternate Issue Shares were Closed");
		tBank.printInfo ();
	}
	
	public void transferShareToClosed (PortfolioHolderI aFromActor, Certificate aCertificate, 
			TransferOwnershipAction aTransferOwnershipAction) {
		transferShareToClosed (aFromActor, aFromActor.getName (), aCertificate, aTransferOwnershipAction);
	}
	
	public void transferShareToClosed (PortfolioHolderI aFromActor, String tFromName, Certificate aCertificate, 
						TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tPlayerPortfolio;
		Portfolio tClosedPortfolio;
		Bank tBank;
	
		tBank = gameManager.getBank ();
		tClosedPortfolio = tBank.getClosedPortfolio ();
		tPlayerPortfolio = aFromActor.getPortfolio ();
		tClosedPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, tFromName, aCertificate, 
						tBank, Bank.CLOSED);
	}

	public void transferShare (PortfolioHolderI aFromActor, ActorI aToActor, Certificate aCertificate, 
					TransferOwnershipAction aTransferOwnershipAction) {
		String tFromName;
		String tToName;
		
		tFromName = ActorI.NO_NAME;
		tToName = ActorI.NO_NAME;
		transferShare (aFromActor, tFromName, aToActor, tToName, aCertificate, aTransferOwnershipAction);
	}

	public void transferShare (PortfolioHolderI aFromActor, String aFromName, ActorI aToActor, Certificate aCertificate, 
					TransferOwnershipAction aTransferOwnershipAction) {
		String tToName;
		
		tToName = ActorI.NO_NAME;
		transferShare (aFromActor, aFromName, aToActor, tToName, aCertificate, aTransferOwnershipAction);
	}
	
	public void transferShare (PortfolioHolderI aFromActor, String aFromName, ActorI aToActor, String aToName, 
				Certificate aCertificate, TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tFromActorPortfolio;
		Portfolio tToActorPortfolio;
		PortfolioHolderI tToHolder;
	
		tFromActorPortfolio = aFromActor.getPortfolio ();
		tToHolder = (PortfolioHolderI) aToActor;
		aToName = aToActor.getName ();
		tToActorPortfolio = tToHolder.getPortfolio ();
		tToActorPortfolio.transferOneCertificateOwnership (tFromActorPortfolio,  aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, aFromName, aCertificate, aToActor, aToName);
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
		ShareExchangeFinishedAction tShareExchangeFinishedAction;
		String tOperatingRoundID;
		Player tNewPlayer;
		
		super.handlePlayerDone ();
		if (formationPhase.getAllPlayerSharesHandled ()) {
			handleOpenMarketShareExchange ();
			confirmFormingPresident ();
			handleIPOShareClosing ();
			closeFormingCompanyAlternateIssue ();
			// -- Test to update to Next Phase (Token Exchange)
			formationPhase.allPlayerSharesExchanged ();
		}
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tShareExchangeFinishedAction = new ShareExchangeFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tNewPlayer = formationPhase.getCurrentPlayer ();

		tShareExchangeFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
		tShareExchangeFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tShareExchangeFinishedAction);

	}
}
