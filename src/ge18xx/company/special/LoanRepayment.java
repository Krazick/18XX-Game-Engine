package ge18xx.company.special;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.RepaymentFinishedAction;
import ge18xx.round.action.RepaymentHandledAction;
import ge18xx.round.action.SpecialPanelAction;
import ge18xx.utilities.GUI;

public class LoanRepayment extends PlayerFormationPhase {
	public static final String PAY_FROM_TREASURY = "Pay From Treasury";
	public static final String PAY_FROM_PRESIDENT = "Pay From President";
	public static final String CONFIRM_REPAYMENT = "Confirm Repayment";
	public static final String PAY_TREASURY = "PayTreasury";
	public static final String PAY_PRESIDENT = "PayPresident";

	private static final long serialVersionUID = 1L;
	boolean sharesExchanged;
	boolean oneShareToBankPool;
	JButton exchange;
	int foldingCompanyCount;
	int totalExchangeCount;

	public LoanRepayment (GameManager aGameManager, FormationPhase aLoanRepayment, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aLoanRepayment, aPlayer, aActingPresident);
		
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
		tRepaymentHandledAction.addRebuildSpecialPanelEffect (aShareCompany);
	
		tCurrentPlayerIndex = getCurrentPlayerIndex ();
	
		formationPhase.rebuildSpecialPanel (tCurrentPlayerIndex);
	
		aShareCompany.addAction (tRepaymentHandledAction);
	}

	public void buildSpecialButtons (ShareCompany aShareCompany, JPanel aShareCompanyJPanel, boolean aActingPlayer) {
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

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		super.actionPerformed (aEvent);
		
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
	}
	
	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));

		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);
		
		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tShareCompanyJPanel;
	}
	
	public boolean repaymentFinished () {
		return player.getRepaymentFinished ();
	}

	@Override
	public void updateDoneButton () {
		String tToolTip;
		Portfolio tPortfolio;
		Certificate tCertificate;
		ShareCompany tShareCompany;
		int tCertificateCount;
		int tCertificateIndex;
		boolean tAllCompaniesHandled;

		if (repaymentFinished ()) {
			if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.LoanRepayment))) {
				done.setEnabled (false);
				tToolTip = "President already completed all loan paybacks";
				done.setToolTipText (tToolTip);
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
	}
	
	@Override
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
		
		player.setRepaymentFinished (true);
		tRepaymentFinishedAction = new RepaymentFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tRepaymentFinishedAction.addRepaymentFinishedEffect (player, true);
		tRepaymentFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
		gameManager.addAction (tRepaymentFinishedAction);
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
		tSpecialPanelAction.addRebuildSpecialPanelEffect (aShareCompany);
		tSpecialPanelAction.setChainToPrevious (true);
		aShareCompany.addAction (tSpecialPanelAction);
	}
}
