package ge18xx.company.special;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.RepaymentFinishedAction;
import ge18xx.round.action.RepaymentHandledAction;
import ge18xx.round.action.SpecialPanelAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.GUI;

public class PlayerFormationPhase extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String PAY_FROM_TREASURY = "Pay From Treasury";
	public static final String PAY_FROM_PRESIDENT = "Pay From President";
	public static final String CONFIRM_REPAYMENT = "Confirm Repayment";
	public static final String PAY_TREASURY = "PayTreasury";
	public static final String PAY_PRESIDENT = "PayPresident";
	public static final String EXCHANGE = "Exchange Shares";
	public static final String DONE = "Done";
	public static final String UNDO = "Undo";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";

	Player player;
	boolean sharesExchanged;
	boolean oneShareToBankPool;
	GameManager gameManager;
	FormationPhase formationPhase;
	JButton done;
	JButton undo;
	JButton exchange;
	int foldingCompanyCount;
	int totalExchangeCount;
	List<String> shareCompaniesHandled;

	public PlayerFormationPhase (GameManager aGameManager, FormationPhase aLoanRepayment, Player aPlayer, 
							Player aActingPresident) {
		String tActingPresidentName;
		Color tBackgroundColor;
		Color tBorderColor;
		Border tActingBorder;
		boolean tActingPlayer;
		
		gameManager = aGameManager;
		formationPhase = aLoanRepayment;
		player = aPlayer;
		setSharesExchanged (false);
		
		if (aActingPresident == aPlayer) {
			tActingPresidentName = aActingPresident.getName ();
			if (gameManager.isNetworkAndIsThisClient (tActingPresidentName)) {
				tBackgroundColor = gameManager.getAlertColor ();
				tBorderColor = gameManager.getAlertColor ();
				tActingPlayer = true;
			} else {
				tBackgroundColor = GUI.defaultColor;
				tBorderColor = gameManager.getAlertColor ();
				tActingPlayer = false;
			}
		} else {
			tBackgroundColor = GUI.defaultColor;
			tBorderColor = Color.BLACK;
			tActingPlayer = false;
		}
		tActingBorder = BorderFactory.createLineBorder (tBorderColor, 3);
		buildPlayerLoanRepaymentJPanel (tActingPlayer, tActingBorder);
		setBackground (tBackgroundColor);
	}
	
	public void setSharesExchanged (boolean aSharesExchanged) {
		sharesExchanged = aSharesExchanged;
	}
	
	public void buildPlayerLoanRepaymentJPanel (boolean aActingPlayer, Border aActingBorder) {
		JLabel tPresidentName;
		JLabel tPresidentTreasury;
		JPanel tPlayerInfo;
		JPanel tPortfolio;
		JPanel tCompanies;
		JPanel tDoneUndo;
		Portfolio tPlayerPortfolio;
		Border tMargin;
		Border tCombinedBorder;
		String tToolTip;
		
		setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
		tMargin = new EmptyBorder (10,10,10,10);

		tPlayerInfo = new JPanel ();
		tPlayerInfo.setLayout (new BoxLayout (tPlayerInfo, BoxLayout.Y_AXIS));
		tPresidentName = new JLabel ("Name: " + player.getName ());
		tPlayerInfo.add (tPresidentName);
		tPlayerInfo.add (Box.createVerticalStrut (10));
	
		tPresidentTreasury = new JLabel ("Cash: " + Bank.formatCash (player.getCash ()));
		tPlayerInfo.add (tPresidentTreasury);
		tPlayerInfo.add (Box.createVerticalStrut (10));
		
		add (tPlayerInfo);
		add (Box.createHorizontalStrut (10));
		
		tPlayerPortfolio = player.getPortfolio ();
		tPortfolio = buildPlayerPortfolioJPanel (tPlayerPortfolio);
		add (tPortfolio);
		add (Box.createHorizontalStrut (10));
	
		if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.LoanRepayment))) {
			tCompanies = buildPlayerCompaniesJPanel (tPlayerPortfolio, aActingPlayer);
		} else if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.ShareExchange))) {
			tCompanies = buildPlayerShareExchangePanel (tPlayerPortfolio, aActingPlayer);
		} else {
			tCompanies = new JPanel ();
		}
		add (tCompanies);
		
		tToolTip = GUI.EMPTY_STRING;
		done = formationPhase.buildSpecialButton (DONE, DONE, tToolTip, this);
		undo = formationPhase.buildSpecialButton (UNDO, UNDO, tToolTip, this);
		updateDoneButton (aActingPlayer);
		updateUndoButton (aActingPlayer);
		tDoneUndo = new JPanel ();
		tDoneUndo.setLayout (new BoxLayout (tDoneUndo, BoxLayout.Y_AXIS));

		tDoneUndo.add (Box.createVerticalGlue ());
		tDoneUndo.add (Box.createVerticalStrut (10));
		tDoneUndo.add (done);
		tDoneUndo.add (Box.createVerticalGlue ());
		tDoneUndo.add (undo);
		tDoneUndo.add (Box.createVerticalStrut (10));
		tDoneUndo.add (Box.createVerticalGlue ());
		add (tDoneUndo);
		
		tMargin = new EmptyBorder (10,10,10,10);
		tCombinedBorder = BorderFactory.createCompoundBorder (aActingBorder, tMargin);
		setBorder (tCombinedBorder);
	}
	
	public void updateDoneButton (boolean aActingPlayer) {
		String tToolTip;
		Portfolio tPortfolio;
		Certificate tCertificate;
		ShareCompany tShareCompany;
		int tCertificateCount;
		int tCertificateIndex;
		boolean tAllCompaniesHandled;
		
		if (aActingPlayer) {
			if (repaymentFinished ()) {
				if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.LoanRepayment))) {
					done.setEnabled (false);
					tToolTip = "President already completed all loan paybacks";
					done.setToolTipText (tToolTip);
				} else if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.ShareExchange))) {
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
				} else {
					done.setEnabled (false);
					tToolTip = "Not Ready Yet";
					done.setToolTipText (tToolTip);
				}
			
			} else {
				tPortfolio = player.getPortfolio ();
				tCertificateCount = tPortfolio.getCertificateTotalCount ();
				tAllCompaniesHandled = true;
				for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
					tCertificate = tPortfolio.getCertificate (tCertificateIndex);
					if (tCertificate.isAShareCompany ()) {
						if (tCertificate.isPresidentShare ()) {
							tShareCompany = tCertificate.getShareCompany ();
							if (! tShareCompany.wasRepaymentHandled ()) {
								tAllCompaniesHandled = false;
								tToolTip = "Not all Share Companies have confirmed loan repayments";
								done.setToolTipText (tToolTip);
							}
						}
					}
				}
			
				done.setEnabled (tAllCompaniesHandled);
			}
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
			done.setToolTipText (tToolTip);
			done.setEnabled (false);
		}
	}
	
	public void updateUndoButton (boolean aActingPlayer) {
		if (aActingPlayer) {
			undo.setEnabled (true);
		} else {
			undo.setToolTipText (NOT_ACTING_PRESIDENT);
			undo.setEnabled (false);
		}
	}
	
	public JPanel buildPlayerPortfolioJPanel (Portfolio aPlayerPortfolio) {
		JPanel tPlayerPortfolioJPanel;
		JPanel tOwnershipPanel;
		JLabel tTitle;
		
		tPlayerPortfolioJPanel = new JPanel ();
		tPlayerPortfolioJPanel.setLayout (new BoxLayout (tPlayerPortfolioJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("Portfolio");
		tPlayerPortfolioJPanel.add (tTitle);
		tOwnershipPanel = aPlayerPortfolio.buildOwnershipPanel (gameManager);
		tPlayerPortfolioJPanel.add (tOwnershipPanel);
		
		return tPlayerPortfolioJPanel;
	}
	
	// new method needed -- buildPlayerShareExchangePanel
	public JPanel buildPlayerShareExchangePanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
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
			tToolTip = "No Shares till be exchanged for " + formationPhase.getFormingCompanyAbbrev ();;
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
			tShareExchangeText = "No Shares till be exchanged for " + tFormingCompanyAbbrev;
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
	
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
		JPanel tPlayerCompaniesJPanel;
		JPanel tShareCompanyJPanel;
		JPanel tTitlePanel;
		JLabel tTitleLabel;
		ShareCompany tShareCompany;
		Certificate tCertificate;
		String tTitle;
		int tCertificateCount;
		int tCertificateIndex;
		
		tPlayerCompaniesJPanel = new JPanel ();
		tPlayerCompaniesJPanel.setLayout (new BoxLayout (tPlayerCompaniesJPanel, BoxLayout.Y_AXIS));
		
		tTitlePanel = new JPanel ();
		tTitlePanel.setLayout (new BoxLayout (tTitlePanel, BoxLayout.X_AXIS));
		tTitle = "Companies where " + player.getName () + " is President";
		tTitleLabel = new JLabel (tTitle);
		tTitlePanel.add (tTitleLabel);
		tPlayerCompaniesJPanel.add (tTitlePanel);
		
		tCertificateCount = aPlayerPortfolio.getCertificateTotalCount ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = aPlayerPortfolio.getCertificate (tCertificateIndex);
			if (tCertificate.isAShareCompany () && tCertificate.isPresidentShare ()) {
				tShareCompany = tCertificate.getShareCompany ();
				tShareCompanyJPanel = buildCompanyJPanel (tShareCompany, aActingPlayer);
				tPlayerCompaniesJPanel.add (tShareCompanyJPanel);
				tPlayerCompaniesJPanel.add (Box.createVerticalStrut (5));
			}
		}

		return tPlayerCompaniesJPanel;
	}

	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		JPanel tCompanyInfoJPanel;
		JLabel tCompanyAbbrev;
		JLabel tLoans;
		JLabel tTreasury;
		JLabel tTrains;
		Border tCorporateColorBorder;
		TrainPortfolio tTrainPortfolio;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));
		tCorporateColorBorder = aShareCompany.setupBorder ();
		tShareCompanyJPanel.setBorder (tCorporateColorBorder);
		
		tCompanyInfoJPanel = new JPanel ();
		tCompanyInfoJPanel.setLayout (new BoxLayout (tCompanyInfoJPanel, BoxLayout.Y_AXIS));
		tCompanyAbbrev = new JLabel (aShareCompany.getAbbrev ());
		tCompanyInfoJPanel.add (tCompanyAbbrev);
		
		tLoans = new JLabel ("Loans: " + aShareCompany.getLoanCount ());
		tCompanyInfoJPanel.add (tLoans);

		tTreasury = new JLabel ("Treasury: " + Bank.formatCash (aShareCompany.getTreasury ()));
		tCompanyInfoJPanel.add (tTreasury);
		
		tTrainPortfolio = aShareCompany.getTrainPortfolio ();
		tTrains = new JLabel (tTrainPortfolio.getTrainList ());
		tCompanyInfoJPanel.add (tTrains);

		tShareCompanyJPanel.add (tCompanyInfoJPanel);
		tShareCompanyJPanel.add (Box.createHorizontalStrut (10));

		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tShareCompanyJPanel;
	}

	public void buildSpecialButtons (ShareCompany aShareCompany, JPanel aShareCompanyJPanel, 
					boolean aActingPlayer) {
		JButton tPayFromTreasury;
		JButton tPayFromPresident;
		JButton tConfirm;
		String tToolTip;
		
		if (aActingPlayer) {
			tToolTip = canPayFromTreasury (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tPayFromTreasury = formationPhase.buildSpecialButton (PAY_FROM_TREASURY, PAY_TREASURY, tToolTip, this);
		aShareCompanyJPanel.add (tPayFromTreasury);
		aShareCompanyJPanel.add (Box.createHorizontalStrut (10));
		aShareCompany.addSpecialButton (tPayFromTreasury);
		
		if (aActingPlayer) {
			tToolTip = canPayFromPresident (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tPayFromPresident = formationPhase.buildSpecialButton (PAY_FROM_PRESIDENT, PAY_PRESIDENT, tToolTip, this);
		aShareCompanyJPanel.add (tPayFromPresident);
		aShareCompany.addSpecialButton (tPayFromPresident);
		
		if (aActingPlayer) {
			tToolTip = confirmRepayment (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tConfirm = formationPhase.buildSpecialButton (CONFIRM_REPAYMENT, CONFIRM_REPAYMENT, tToolTip, this);
		aShareCompanyJPanel.add (tConfirm);
		aShareCompany.addSpecialButton (tConfirm);
	}

	public String confirmRepayment (ShareCompany aShareCompany) {
		String tToolTip;
		int tLoanAmount;
		
		tToolTip = GUI.EMPTY_STRING;
		if (aShareCompany.wasRepaymentHandled ()) {
			tToolTip = "Company [" + aShareCompany.getAbbrev () + "] has already confirmed repayments";		
		} else if (aShareCompany.hasOutstandingLoans ()) {
			tLoanAmount = aShareCompany.getLoanAmount ();
			if (aShareCompany.getCash () >= tLoanAmount) {
				tToolTip = "Company [" + aShareCompany.getAbbrev () + "] Treasury has enough to pay the Loan Amount " + 
							Bank.formatCash (tLoanAmount);
			}
		}

		return tToolTip;
	}
	
	public String canPayFromTreasury (ShareCompany aShareCompany) {
		String tToolTip;
		int tLoanAmount;
		
		tToolTip = GUI.EMPTY_STRING;
		if (aShareCompany.hasOutstandingLoans ()) {
			tLoanAmount = aShareCompany.getLoanAmount ();
			if (aShareCompany.getCash () < tLoanAmount) {
				tToolTip = "Company [" + aShareCompany.getAbbrev () + "] Treasury has less than the Loan Amount " + 
							Bank.formatCash (tLoanAmount);
			}
		} else {
			tToolTip = "Company [" + aShareCompany.getAbbrev () + "] has no Outstanding Loans";
		}
		
		return tToolTip;
	}
	
	public String canPayFromPresident (ShareCompany aShareCompany) {
		Player tPlayer;
		int tPlayerCash;
		int tTotalLoanAmount;
		int tLoanAmount;
		int tLoanCount;
		String tToolTip;
		
		tToolTip = GUI.EMPTY_STRING;
		if (aShareCompany.hasOutstandingLoans ()) {
			if (! aShareCompany.wasRepaymentHandled ()) {
				tLoanAmount = aShareCompany.getLoanAmount ();
				if (aShareCompany.getCash () >= tLoanAmount) {
					tToolTip = "Company [" + aShareCompany.getAbbrev () + "] Treasury has enough to pay an Outstanding Loan";
				} else {
					tPlayer = (Player) aShareCompany.getPresident ();
					tPlayerCash = tPlayer.getCash ();
					tLoanCount = aShareCompany.getLoanCount ();
					tTotalLoanAmount = tLoanAmount * tLoanCount;
					if (tPlayerCash < tTotalLoanAmount)  {
						tToolTip = "President " + aShareCompany.getPresidentName () + 
									" Treasury has less than the Loan Amount " + Bank.formatCash (tLoanAmount);
					}
				}
			} else {
				tToolTip = "Company [" + aShareCompany.getAbbrev () + "] Outstanding Loans have been confirmed";
			}
		} else {
			tToolTip = "Company [" + aShareCompany.getAbbrev () + "] has no Outstanding Loans";
		}
		
		return tToolTip;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		ShareCompany tShareCompany;
		JButton tActivatedButton;
		
		tActionCommand = aEvent.getActionCommand ();
		tActivatedButton = getActivatedButton (aEvent);
		if (tActivatedButton != GUI.NO_BUTTON) {
			tShareCompany = findShareCompany (tActivatedButton);
			if (tShareCompany != ShareCompany.NO_SHARE_COMPANY) {
				if (tShareCompany != ShareCompany.NO_SHARE_COMPANY) {
					if (tActionCommand.equals (PAY_TREASURY)) {
						handleRepayFromTreasury (tShareCompany);
					} else if (tActionCommand.equals (PAY_PRESIDENT)) {
						handleRepayFromPresident (tShareCompany);
					} else if (tActionCommand.equals (CONFIRM_REPAYMENT)) {
						handleConfirmRepayment (tShareCompany);
					}
				}
			}
		}
		if (tActionCommand.equals (EXCHANGE)) {
			handleShareExchange ();
		}
		if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
		}
		if (tActionCommand.equals (UNDO)) {
			handlePlayerUndo ();
		}
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
			if (formationPhase.getShareFoldCount () > 10) {
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
	
	public void handleRepayFromTreasury (ShareCompany aShareCompany) {
		int tLoanCount;
		int tLoanAmount;
		int tTotalLoanCount;
		int tTreasury;
		int tPresidentContribution;
		
		tTreasury = aShareCompany.getTreasury ();
		tTotalLoanCount = aShareCompany.getLoanCount ();
		tLoanAmount = aShareCompany.getLoanAmount ();
		if (tTotalLoanCount > 0) {
			if (tTreasury >= tLoanAmount) {
				tLoanCount = Math.min (tTotalLoanCount, tTreasury/tLoanAmount);
				tPresidentContribution = 0;
				redeemLoanAndUpdate (aShareCompany, tLoanCount, tPresidentContribution);
			}
		}
	}
	
	public int getCurrentPlayerIndex () {
		int tCurrentPlayerIndex;

		tCurrentPlayerIndex = formationPhase.getCurrentPlayerIndex ();
		
		return tCurrentPlayerIndex;
	}
	
	public void handleRepayFromPresident (ShareCompany aShareCompany) {
		int tLoanAmount;
		int tTreasury;
		int tLoanCount;
		int tTotalLoanCount;
		int tPresidentContribution;
		
		tTotalLoanCount = aShareCompany.getLoanCount ();
		tLoanAmount = aShareCompany.getLoanAmount ();
		tTreasury = player.getCash ();
		tLoanCount = 1;
		if (tTotalLoanCount > 0) {
			if (tTreasury >= tLoanAmount) {
				tPresidentContribution = tLoanAmount;
				redeemLoanAndUpdate (aShareCompany, tLoanCount, tPresidentContribution);
			}
		}
	}

	public void redeemLoanAndUpdate (ShareCompany aShareCompany, int tLoanCount, int tPresidentContribution) {
		int tCurrentPlayerIndex;
		SpecialPanelAction tSpecialPanelAction;
		String tOperatingRoundID;
		
		tCurrentPlayerIndex = getCurrentPlayerIndex ();
		aShareCompany.redeemLoans (tLoanCount, tPresidentContribution);
		formationPhase.rebuildSpecialPanel (tCurrentPlayerIndex);
		tOperatingRoundID = aShareCompany.getOperatingRoundID ();
		tSpecialPanelAction = new SpecialPanelAction (ActorI.ActionStates.OperatingRound, 
								tOperatingRoundID, aShareCompany);
		tSpecialPanelAction.addShowSpecialPanelEffect (aShareCompany, aShareCompany);
		aShareCompany.addAction (tSpecialPanelAction);
	}
	
	public void handleConfirmRepayment (ShareCompany aShareCompany) {
		RepaymentHandledAction tRepaymentHandledAction;
		String tOperatingRoundID;
		String tNotification;
		String tFoldingCompanyAbbrev;
		boolean tRepaymentHandled;
		int tCurrentPlayerIndex;
		int tShareFoldCount;
		int tNewShareFoldCount;
		int tOldShareFoldCount;
		
		tOperatingRoundID = aShareCompany.getOperatingRoundID ();
		tRepaymentHandledAction = new RepaymentHandledAction (ActorI.ActionStates.OperatingRound, 
								tOperatingRoundID, aShareCompany);
		tRepaymentHandled = true;
		aShareCompany.setRepaymentHandled (tRepaymentHandled);
		tOldShareFoldCount = formationPhase.getShareFoldCount ();
		if (aShareCompany.willFold ()) {
			tShareFoldCount = aShareCompany.getShareFoldCount ();
			formationPhase.addShareFoldCount (tShareFoldCount);
			
			tNewShareFoldCount = formationPhase.getShareFoldCount ();
			
			tFoldingCompanyAbbrev = aShareCompany.getAbbrev ();
			tNotification = formationPhase.buildFoldNotification (tFoldingCompanyAbbrev, tShareFoldCount);
			formationPhase.setNotificationText (tNotification);
					
			tRepaymentHandledAction.addShareFoldCountEffect (aShareCompany, tOldShareFoldCount, tNewShareFoldCount);
		}

		tRepaymentHandledAction.addSetRepaymentHandledEffect (aShareCompany, tRepaymentHandled);

		tCurrentPlayerIndex = getCurrentPlayerIndex ();

		formationPhase.rebuildSpecialPanel (tCurrentPlayerIndex);

		aShareCompany.addAction (tRepaymentHandledAction);
	}
	
	public void handlePlayerDone () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		RepaymentFinishedAction tRepaymentFinishedAction;
		Player tNewPlayer;
		String tOperatingRoundID;
		
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		formationPhase.updateToNextPlayer (tPlayers);
		tNewPlayer = formationPhase.getCurrentPlayer ();
		
		if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.LoanRepayment))) {
			player.setRepaymentFinished (true);
			tRepaymentFinishedAction = new RepaymentFinishedAction (ActorI.ActionStates.OperatingRound, 
					tOperatingRoundID, player);
			tRepaymentFinishedAction.addRepaymentFinishedEffect (player, true);
			tRepaymentFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
			gameManager.addAction (tRepaymentFinishedAction);
		} else if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.ShareExchange))) {
			// Action to move to Next player
		}
	}
	
	public void handlePlayerUndo () {
		int tCurrentPlayerIndex;
		
		player.undoAction ();
		if (formationPhase.getFormationState () != ActorI.ActionStates.NoState) {
			tCurrentPlayerIndex = getCurrentPlayerIndex ();
			formationPhase.rebuildSpecialPanel (tCurrentPlayerIndex);
		}
	}
	
	public boolean sharesExchanged () {
		return sharesExchanged;
	}
	
	public boolean repaymentFinished () {
		return player.getRepaymentFinished ();
	}
	
	public JButton getActivatedButton (ActionEvent aEvent) {
		JButton tActivatedButton;
		Object tSource;
		
		tSource = aEvent.getSource ();
		if (tSource instanceof JButton) {
			tActivatedButton = (JButton) tSource;
		} else {
			tActivatedButton = GUI.NO_BUTTON;
		}
		
		return tActivatedButton;
	}
	
	public ShareCompany findShareCompany (JButton aActivatedButton) {
		ShareCompany tFoundShareCompany;
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		tFoundShareCompany = tShareCompanies.findCompanyWithSpecial (aActivatedButton);
		
		return tFoundShareCompany;
	}
}
