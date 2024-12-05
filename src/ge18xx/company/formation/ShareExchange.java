package ge18xx.company.formation;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ShareExchangeFinishedAction;
import ge18xx.round.action.TransferOwnershipAction;
import geUtilities.GUI;
import swingTweaks.KButton;

public class ShareExchange extends PlayerFormationPanel {
	private static final long serialVersionUID = 1L;
	public static final String EXCHANGE = "Exchange Shares";
	boolean oneShareToBankPool;
	KButton exchange;
	int foldingCompanyCount;
	int totalExchangeCount;

	public ShareExchange (GameManager aGameManager, FormCGR aShareExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aShareExchange, aPlayer, aActingPresident);
		if (isActingPlayer (aActingPresident)) {
			closeFormingCompanySecondIssue ();
		}
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
			tToolTip = "No Shares will be exchanged for " + formCGR.getFormingCompanyAbbrev ();;
		} else if (! aActingPlayer) {
			tToolTip = NOT_ACTING_PRESIDENT;
		} else {
			tToolTip = GUI.EMPTY_STRING;
		}
	
		exchange = formCGR.buildSpecialButton (EXCHANGE, EXCHANGE, tToolTip, this);
	
		tPlayerShareExchangePanel.add (exchange);
		tPlayerShareExchangePanel.add (Box.createVerticalStrut (5));
		
		return tPlayerShareExchangePanel;
	}

	public String buildShareExchangeText (Portfolio aPlayerPortfolio) {
		ShareCompany tShareCompany;
		Certificate tCertificate;
		List<String> tShareExchange;
		String tShareExchangeText;
		String tShareExchangePhrase;
		String tShareCompanyAbbrev;
		int tCertificateCount;
		int tCertificateIndex;
		int tShareExchangeCount;
		int tTotalShareCount;
		
		tCertificateCount = aPlayerPortfolio.getCertificateTotalCount ();
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
						tShareExchangePhrase = buildShareCount (tShareExchangeCount, " of " + tShareCompanyAbbrev);
						tShareExchange.add (tShareExchangePhrase);
						shareCompaniesHandled.add (tShareCompanyAbbrev);
					}
				}
			}
		}
		totalExchangeCount = formCGR.getSharesReceived (tTotalShareCount);
		if (tTotalShareCount % 2 != 0) {
			oneShareToBankPool = true;
		}
		foldingCompanyCount = tShareExchange.size ();
		tShareExchangeText = buildShareExchangeText (tTotalShareCount, tShareExchange);
		
		return tShareExchangeText;
	}

	private String buildShareExchangeText (int aTotalShareCount, List<String> aShareExchange) {
		int tFoldingCompanyIndex;
		String tShareExchangeText;
		String tExchangeText;
		String tFormingCompanyAbbrev;
		
		tFormingCompanyAbbrev = formCGR.getFormingCompanyAbbrev ();
		tShareExchangeText = GUI.EMPTY_STRING;
		if  (aTotalShareCount == 0) {
			tShareExchangeText = "No Shares will be exchanged for " + tFormingCompanyAbbrev;
		} else {
			if (foldingCompanyCount == 1) {
				tShareExchangeText = aShareExchange.get (0);
			} else {
				for (tFoldingCompanyIndex = 0; tFoldingCompanyIndex < foldingCompanyCount; tFoldingCompanyIndex++) {
					if (tFoldingCompanyIndex > 0) {
						if ((tFoldingCompanyIndex + 1) == foldingCompanyCount) {
							tShareExchangeText += " and ";
						} else {
							tShareExchangeText += GUI.COMMA_SPACE;
						}
					}
					tShareExchangeText += aShareExchange.get (tFoldingCompanyIndex);
				}
			}
			tExchangeText = buildShareCount (totalExchangeCount, " of " + tFormingCompanyAbbrev);
			tShareExchangeText += " will be exchanged for " + tExchangeText;
		}
		
		return tShareExchangeText;
	}

	private String buildShareCount (int aCount, String aPostfix) {
		String tShareCount;
		
		if (aCount == 1) {
			tShareCount = aCount + " Share";
		} else {
			tShareCount = aCount + " Shares";
		}
		tShareCount += aPostfix;

		return tShareCount;
	}
	
	public void handleShareExchange () {
		TransferOwnershipAction tTransferOwnershipAction1;
		TransferOwnershipAction tTransferOwnershipAction2;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		BankPool tBankPool;
		Bank tBank;
		Portfolio tBankPortfolio;
		Portfolio tPlayerPortfolio;
		ShareCompany tFormingCompany;
		Certificate tCertificate;
		Certificate tFormedCertificate;
		String tFormingAbbrev;
		String tNotification;
		String tPresidentName;
		String tTransferNotification;
		String tExchangeNotification;
		boolean tTransferred;
		boolean tNewPresident;
		int tCertificateIndex;
		int tCertificateCount;
		int tFoldingIndex;
		int tFormingCompanyID;
		int tPercentage;
		int tExchangeCount;
		
		tBankPool = gameManager.getBankPool ();
		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tPlayerPortfolio = player.getPortfolio ();
		tCertificateCount = tPlayerPortfolio.getCertificateTotalCount ();

		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tRoundID = tRoundManager.getCurrentRoundOf ();

		tTransferOwnershipAction1 = new TransferOwnershipAction (tRoundType, tRoundID, player);
		tTransferOwnershipAction2 = new TransferOwnershipAction (tRoundType, tRoundID, player);
		tExchangeNotification = player.getName () + " exchanged ";
		tTransferNotification = player.getName () + " transferred ";
		tNotification = GUI.EMPTY_STRING;
		tTransferred = false;
		tNewPresident = false;
		for (String tCompanyAbbrev : shareCompaniesHandled) {
			tCertificateCount = tPlayerPortfolio.getCertificateTotalCount ();
			tExchangeCount = 0;
			for (tCertificateIndex = tCertificateCount - 1; tCertificateIndex >= 0; tCertificateIndex--) {
				tCertificate = tPlayerPortfolio.getCertificate (tCertificateIndex);
				if (tCertificate.getCompanyAbbrev ().equals (tCompanyAbbrev)) {
					if ((tCertificate.getPercentage () == PhaseInfo.STANDARD_SHARE_SIZE) && oneShareToBankPool) {
						transferShare (player, tBankPool, tCertificate, tTransferOwnershipAction1);
						oneShareToBankPool = false;
						tTransferNotification += buildShareCount (1, " of " + tCompanyAbbrev + " to " + 
										tBankPool.getName () + ". ");
						tTransferred = true;
					} else {
						tExchangeCount += tCertificate.getShareCount ();
						transferShareToClosed (player, tCertificate, tTransferOwnershipAction1);
					}
				}
			}
			if (tExchangeCount > 0) {
				tExchangeNotification += buildShareCount (tExchangeCount, " of " + tCompanyAbbrev + " ");
			}
		}
		if (oneShareToBankPool) {
			tExchangeNotification += " and 1 Share to the " + tBankPool.getName ();
		}
		if (tTransferred) {
			tNotification = tTransferNotification;
		}
		tNotification += tExchangeNotification;
		
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tFormingCompany = (ShareCompany) gameManager.getCorporationByID (tFormingCompanyID);
		tFormingAbbrev = getFormingAbbrev ();
		tPercentage = formCGR.getPercentageForExchange ();

		// Transfer Shares from the Bank's IPO of the newly forming Company to the current Player
		tFormedCertificate = Certificate.NO_CERTIFICATE;
		if (totalExchangeCount > 0) {
			for (tFoldingIndex = 0; tFoldingIndex < totalExchangeCount; tFoldingIndex++) {
				tFormedCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
				if (tFormedCertificate != Certificate.NO_CERTIFICATE) {
					transferShare (tBank, Bank.IPO, player, tFormedCertificate, tTransferOwnershipAction1);
				} else {
					System.err.println ("No certificate available with All Players Total Exchange Count " + 
									formCGR.getShareFoldCount ());
				}
			}
		}
		// If at least one FormedCertificate has been transfered to a Player,
		// Need to update the Corporation Ownership. But only need to do this once after all done
		// The TransferOwnershipEffect applies this to remote clients
		// Don't need to create the Effect
		if (tFormedCertificate != Certificate.NO_CERTIFICATE) {
			tFormedCertificate.updateCorporationOwnership ();
		}
		
		// Note, at the end of this Exchange, really should just reset the Corporation Status to at least 'Will Float' 
		// No matter what, the newly formed company should always operated (1856 - CGR, 1835 - PR)
		
		tTransferOwnershipAction1.addSetNotificationEffect (player, tNotification);
		gameManager.addAction (tTransferOwnershipAction1);
		if (totalExchangeCount > 0) {
			tNewPresident = assignPresident (tBankPortfolio, tPercentage, tFormingCompany, tTransferOwnershipAction2);
			tNotification += "for " + totalExchangeCount + " Shares of " + tFormingAbbrev;
			tPresidentName = tFormingCompany.getPresidentName ();
			if (! GUI.EMPTY_STRING.equals (tPresidentName)) {
				tNotification += ", President is " + tPresidentName;
			}
		}

		formCGR.setNotificationText (tNotification);

		exchange.setEnabled (false);
		exchange.setToolTipText ("President has not completed all share exchanges");
		updateDoneButton (true);
		formCGR.rebuildFormationPanel (formCGR.getCurrentPlayerIndex ());
		tTransferOwnershipAction2.addRebuildFormationPanelEffect (player);
		if (tNewPresident) {
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
	
	public void handleOpenMarketShareExchange () {
		BankPool tBankPool;
		Portfolio tBankPoolPortfolio;
		int tPortfolioIndex;
		int tCertificateCount;
		int tExchangeCount;
		int tNewCount;
		int tNewIndex;
		int tPercentage;
		String tFormingAbbrev;
		String tNotification;
		Bank tBank;
		Portfolio tBankPortfolio;
		Corporation tCorporation;
		Certificate tCertificate;
		TransferOwnershipAction tTransferOwnershipAction;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;

		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tBankPool = gameManager.getBankPool ();
		tBankPoolPortfolio = tBankPool.getPortfolio ();
		tCertificateCount = tBankPoolPortfolio.getCertificateTotalCount ();
		if (tCertificateCount > 0) {
			tExchangeCount = 0;
			tPercentage = formCGR.getPercentageForExchange ();

			tRoundManager = gameManager.getRoundManager ();
			tRoundType = tRoundManager.getCurrentRoundState ();
			tRoundID = tRoundManager.getCurrentRoundOf ();
			tTransferOwnershipAction = new TransferOwnershipAction (tRoundType, tRoundID, player);
//			tTransferOwnershipAction = (TransferOwnershipAction) 
//					constructFormationAction (TransferOwnershipAction.class.getName (), player);

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
				tFormingAbbrev = GUI.EMPTY_STRING;
				for (tNewIndex = 0; tNewIndex < tNewCount; tNewIndex++) {
					tFormingAbbrev = getFormingAbbrev ();

					tCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false);
					if (tCertificate != Certificate.NO_CERTIFICATE) {
						transferShare (tBank, tBankPool, tCertificate, tTransferOwnershipAction);
					} else {
						System.err.println ("No certificate available with All Players Total Exchange Count " + 
										formCGR.getShareFoldCount ());
					}

				}
				tNotification = tBankPool.getName () + " exchanged " + tExchangeCount + " Shares into " + tNewCount + 
						" Shares of " + tFormingAbbrev;
			} else {
				
				tNotification = tBankPool.getName () + " moved 1 Share into the Closed Portfolio.";
			}
			formCGR.setNotificationText (tNotification);
			formCGR.rebuildFormationPanel ();
			tTransferOwnershipAction.setChainToPrevious (true);
			tTransferOwnershipAction.addRebuildFormationPanelEffect (player);
			tTransferOwnershipAction.addSetNotificationEffect (player, tNotification);
			gameManager.addAction (tTransferOwnershipAction);
		}
	}
	
	public void confirmFormingPresident () {
		PlayerManager tPlayerManager;
		Player tCurrentPresident;
		PortfolioHolderI tCurrentHolder;
		TransferOwnershipAction tTransferOwnershipAction;
		String tRoundID;
		String tNotification;
		Corporation tCorporation;
		RoundManager tRoundManager;
		Round tCurrentRound;
		ActorI.ActionStates tRoundType;
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
				tRoundManager = gameManager.getRoundManager ();
				tRoundType = tRoundManager.getCurrentRoundState ();
				tCurrentRound = tRoundManager.getCurrentRound ();
				tRoundID = tCurrentRound.getID ();
				tTransferOwnershipAction = new TransferOwnershipAction (tRoundType, tRoundID, player);
				tNotification = tFormingCompany.getPresidentName () + " is the President of the " + 
						tFormingCompany.getAbbrev ();
				tPlayerManager.handlePresidentialTransfer (tTransferOwnershipAction, tFormingCompany, tCurrentPresident);
				tTransferOwnershipAction.setChainToPrevious (true);
				gameManager.addAction (tTransferOwnershipAction);
				formCGR.rebuildFormationPanel ();
				formCGR.setNotificationText (tNotification);
			} else {
				System.err.println ("The Current President is not a Player");
			}
		} else {
			System.err.println ("The Forming Company ID found is NOT a Share Company");
		}
	}
	
	public boolean assignPresident (Portfolio aBankPortfolio, int aPercentage, ShareCompany aFormingCompany, 
					TransferOwnershipAction aTransferOwnershipAction) {
		Certificate tPresidentCertificate;
		Certificate tPresidentZeroCertificate;
		Portfolio tPlayerPortfolio;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		Bank tBank;
		int tOwnsPercentage;
		int tPresidentOwnedPercentage;
		int tPresidentCertificatePercentage;
		int tFindPercentage;
		boolean tNewPresident;
		
		tBank = gameManager.getBank ();
		tFindPercentage = formCGR.getPercentageForExchange () * 2;
		tPresidentOwnedPercentage = aFormingCompany.getPresidentOwnedPercent ();
		tPresidentCertificate = aFormingCompany.getPresidentCertificate (tFindPercentage);
		tPresidentCertificatePercentage = tPresidentCertificate.getPercentage ();
		tPlayerPortfolio = player.getPortfolio ();
		tOwnsPercentage = tPlayerPortfolio.getPercentageFor (aFormingCompany);
		tNewPresident = false;
		if (tOwnsPercentage >= tPresidentCertificatePercentage) {
			if (tOwnsPercentage > tPresidentOwnedPercentage) {
				tNewPresident = givePresidentCertificate (aBankPortfolio, aPercentage, aTransferOwnershipAction,
						aFormingCompany);
			}
		} else if (tPresidentOwnedPercentage == Certificate.NO_PERCENTAGE) {
			tPresidentZeroCertificate = new Certificate (aFormingCompany, true, Certificate.NO_PERCENTAGE,
							tPlayerPortfolio);
			tPlayerPortfolio.addCertificate (tPresidentZeroCertificate);
			aTransferOwnershipAction.addCreateNewCertificateEffet (tBank, tPresidentZeroCertificate, player);
			tNewPresident = true;
		}
		confirmFormingPresident ();
		if (! aFormingCompany.willFloat ()) {
			tOldState = aFormingCompany.getStatus ();
			aFormingCompany.resetStatus (ActorI.ActionStates.WillFloat);
			tNewState = aFormingCompany.getStatus ();
			aTransferOwnershipAction.addChangeCorporationStatusEffect (aFormingCompany, tOldState, tNewState);
		}
		
		return tNewPresident;
	}

	private boolean givePresidentCertificate (Portfolio aBankPortfolio, int aPercentage,
			TransferOwnershipAction aTransferOwnershipAction, Corporation aFormingCompany) {
		Certificate tPresidentCertificate;
		Certificate tExchangeCertificate;
		Certificate tPresidentZeroCertificate;
		Certificate tRemovedCertificate;
		Portfolio tOldPresidentPortfolio;
		Portfolio tPlayerPortfolio;
		PortfolioHolderI tCurrentPresident;
		Bank tBank;
		int tPercentageForExchange;
		String tFormingAbbrev;
		boolean tNewPresident;
		
		tBank = gameManager.getBank ();
		tPlayerPortfolio = player.getPortfolio ();
		tCurrentPresident = aFormingCompany.getPresident ();
		tFormingAbbrev = aFormingCompany.getAbbrev ();
		tPercentageForExchange = formCGR.getPercentageForExchange ();
		System.out.println ("Share Percentage for each Share Exchanged " + tPercentageForExchange);
		tPresidentCertificate = aBankPortfolio.getCertificate (tFormingAbbrev, tPercentageForExchange * 2, true);
		tNewPresident = true;
		if (tPresidentCertificate != Certificate.NO_CERTIFICATE) {
			transferShare (tBank, Bank.IPO, player, tPresidentCertificate, aTransferOwnershipAction);
			tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
			transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
			tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
			if (tExchangeCertificate != Certificate.NO_CERTIFICATE) {
				transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
			}
			if (tCurrentPresident != PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
				tOldPresidentPortfolio = tCurrentPresident.getPortfolio ();
				tPresidentZeroCertificate = tOldPresidentPortfolio.getCertificate (tFormingAbbrev,
						Certificate.NO_PERCENTAGE, tNewPresident);
				if (tPresidentZeroCertificate != Certificate.NO_CERTIFICATE) {
					System.out.println ("Found Cert found to remove from " + tCurrentPresident.getName ());
					tRemovedCertificate = tOldPresidentPortfolio.getThisCertificate (tPresidentZeroCertificate);
					System.out.println ("Found 0% Cert in old President Portfolio - Removed");
					aTransferOwnershipAction.addDeleteCertificateEffet (tCurrentPresident, tRemovedCertificate,
									tCurrentPresident);
				}
			}
		}
		formCGR.setFormingPresidentAssigned  (tNewPresident);
		
		return tNewPresident;
	}
	
	public void handleIPOShareClosing () {
		Portfolio tBankIPOPortfolio;
		ShareCompany tShareCompany;
		TransferOwnershipAction tTransferOwnershipAction;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		Round tCurrentRound;
		String tRoundID;
		Certificate tCertificate;
		String tFromName;
		Bank tBank;
		int tCertificatesTransferred;
		int tIPOIndex;
		int tIPOCount;
		
		tBank = gameManager.getBank ();
		tBankIPOPortfolio = tBank.getPortfolio ();
		tIPOCount = tBankIPOPortfolio.getCertificateTotalCount ();
		tFromName = Bank.IPO;
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();

		tTransferOwnershipAction = new TransferOwnershipAction (tRoundType, tRoundID, player);
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
			tTransferOwnershipAction.addRebuildFormationPanelEffect (player);
			tTransferOwnershipAction.setChainToPrevious (true);
			gameManager.addAction (tTransferOwnershipAction);
		}
	}
	
	public void closeFormingCompanySecondIssue () {
		int tPercentage;
		int tPrezPercentage;
		int tFormingCompanyID;
		boolean tMoreCerts;
		Portfolio tBankIPOPortfolio;
		ShareCompany tFormingCompany;
		Corporation tCorporation;
		TransferOwnershipAction tTransferOwnershipAction;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		Round tCurrentRound;
		String tRoundID;
		Certificate tCertificate;
		String tFormingAbbrev;
		String tFromName;
		Bank tBank;

		tPercentage = formCGR.getPercentageNotForExchange ();
		tPrezPercentage = tPercentage * 2;
		tBank = gameManager.getBank ();
		tBankIPOPortfolio = tBank.getPortfolio ();
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tCorporation = gameManager.getCorporationByID (tFormingCompanyID);
		if (tCorporation.isAShareCompany ()) {
			tFormingCompany = (ShareCompany) tCorporation;
			tFormingAbbrev = tFormingCompany.getAbbrev ();
			tRoundManager = gameManager.getRoundManager ();
			tRoundType = tRoundManager.getCurrentRoundState ();
			tCurrentRound = tRoundManager.getCurrentRound ();
			tRoundID = tCurrentRound.getID ();

			tTransferOwnershipAction = new TransferOwnershipAction (tRoundType, tRoundID, tBank);
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
			if (tCertificate != Certificate.NO_CERTIFICATE) {
				transferShareToClosed (tBank, tCertificate, tTransferOwnershipAction);
			}
			if (tTransferOwnershipAction.getEffectCount () > 0) {
				gameManager.addAction (tTransferOwnershipAction);
				System.out.println ("*** Added 2nd Issue move to CLOSED -- Action " + 
						tTransferOwnershipAction.getNumber () + " with " + 
						tTransferOwnershipAction.getEffectCount () + " Effects" +
						", Actor " + player.getName ());
			}
		}
	}
	
	public void transferShareToClosed (PortfolioHolderI aFromActor, Certificate aCertificate, 
			TransferOwnershipAction aTransferOwnershipAction) {
		transferShareToClosed (aFromActor, aFromActor.getName (), aCertificate, aTransferOwnershipAction);
	}
	
	public void transferShareToClosed (PortfolioHolderI aFromActor, String aFromName, Certificate aCertificate, 
						TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tPlayerPortfolio;
		Portfolio tClosedPortfolio;
		Bank tBank;
	
		tBank = gameManager.getBank ();
		tClosedPortfolio = tBank.getClosedPortfolio ();
		tPlayerPortfolio = aFromActor.getPortfolio ();
		tClosedPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, aFromName, aCertificate, 
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
	
	public void transferShare (PortfolioHolderI aFromActor, String aFromNickName, ActorI aToActor, String aToNickName, 
				Certificate aCertificate, TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tFromActorPortfolio;
		Portfolio tToActorPortfolio;
		PortfolioHolderI tToHolder;
	
		tFromActorPortfolio = aFromActor.getPortfolio ();
		tToHolder = (PortfolioHolderI) aToActor;
		aToNickName = aToActor.getName ();
		tToActorPortfolio = tToHolder.getPortfolio ();
		tToActorPortfolio.transferOneCertificateOwnership (tFromActorPortfolio,  aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, aFromNickName, aCertificate, aToActor, aToNickName);
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (EXCHANGE)) {
			handleShareExchange ();
		} else if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
		} else if (tActionCommand.equals (FormCGR.TOKEN_EXCHANGE)) {
			formCGR.handleTokenExchange ();
		} else {
			super.actionPerformed (aEvent);
		}
	}
	
	@Override
	public void updateDoneButton () {
		String tToolTip;

		tToolTip = GUI.NO_TOOL_TIP;
		if (getSharesExchanged ()) {
			doneButton.setEnabled (false);
			tToolTip = "President already completed all share exchanges";
		} else if (foldingCompanyCount == 0) {
			doneButton.setEnabled (true);
			tToolTip = "President has no shares to exchanges";
		} else {
			doneButton.setEnabled (false);
			tToolTip = "President has not completed all share exchanges";
		}
		doneButton.setToolTipText (tToolTip);
	}
	
	@Override
	public void updateContinueButton () {
		String tToolTip;
		
		if (getSharesExchanged () && actingPlayer) {
			if (formCGR.getFormationState ().equals (ActorI.ActionStates.ShareExchange)) {
				continueButton.setEnabled (true);
				tToolTip = "All Shares have been exchanged, proceed to Token Exchange";			
				continueButton.setToolTipText (tToolTip);
				continueButton.setVisible (true);
			} else {
				continueButton.setEnabled (false);
				tToolTip = "Not Ready Yet";
				continueButton.setToolTipText (tToolTip);
				continueButton.setVisible (false);
			}	
			continueButton.setActionCommand (FormCGR.TOKEN_EXCHANGE);
		} else {
			continueButton.setVisible (false);
		}
	}

	public boolean getSharesExchanged () {
		boolean tSharesExchanged;
		
		if (player.getSharesExchanged ()) {
			tSharesExchanged = true;
		} else {
			tSharesExchanged = false;
		}
		
		return tSharesExchanged;
	}

	@Override
	public void handlePlayerDone () {
		ShareExchangeFinishedAction tShareExchangeFinishedAction;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		Round tCurrentRound;
		String tRoundID;
		
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();

		tShareExchangeFinishedAction = new ShareExchangeFinishedAction (tRoundType, tRoundID, player);
		tShareExchangeFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tShareExchangeFinishedAction);
		super.handlePlayerDone ();
		if (formCGR.getAllPlayerSharesHandled ()) {
			handleOpenMarketShareExchange ();
			confirmFormingPresident ();
			handleIPOShareClosing ();
		}
	}
}
