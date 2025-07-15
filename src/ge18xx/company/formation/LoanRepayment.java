package ge18xx.company.formation;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.RepaymentHandledAction;
import geUtilities.GUI;
import swingTweaks.KButton;

public class LoanRepayment extends PlayerFormationPanel {
	private static final long serialVersionUID = 1L;
	public static final String PAY_FROM_TREASURY = "Pay From Treasury";
	public static final String PAY_FROM_PRESIDENT = "Pay From President";
	public static final String CONFIRM_REPAYMENT = "Confirm Repayment";
	public static final String PAY_TREASURY = "PayTreasury";
	public static final String PAY_PRESIDENT = "PayPresident";
	public static final String PAYBACK_COMPLETED = "President already completed all loan paybacks";
	public static final String LOANS_REPAYMENTS_NEEDED = "Not all Share Companies have confirmed loan repayments";
	boolean oneShareToBankPool;
	int foldingCompanyCount;
	int totalExchangeCount;

	public LoanRepayment (GameManager aGameManager, FormCompany aFormCompany, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aFormCompany, aPlayer, aActingPresident);
	}

	public String confirmRepayment (ShareCompany aShareCompany) {
		String tToolTip;
		String tToolTipPrefix;
		int tLoanAmount;
		
		tToolTip = GUI.EMPTY_STRING;
		tToolTipPrefix = getToolTipPrefix (aShareCompany);
		if (aShareCompany.wasRepaymentHandled ()) {
			tToolTip = tToolTipPrefix + "has already confirmed repayments";		
		} else if (aShareCompany.hasOutstandingLoans ()) {
			tLoanAmount = aShareCompany.getLoanAmount ();
			if (aShareCompany.getCash () >= tLoanAmount) {
				tToolTip = tToolTipPrefix + "Treasury has enough to pay the Loan Amount " + 
							Bank.formatCash (tLoanAmount);
			}
		}
	
		return tToolTip;
	}

	public String getToolTipPrefix (ShareCompany aShareCompany) {
		String tToolTipPrefix;
		
		tToolTipPrefix = "Company [" + aShareCompany.getAbbrev () + "] ";
		
		return tToolTipPrefix;
	}
	
	public String canPayFromTreasury (ShareCompany aShareCompany) {
		String tToolTip;
		String tToolTipPrefix;
		int tLoanAmount;
		
		tToolTip = GUI.EMPTY_STRING;
		tToolTipPrefix = getToolTipPrefix (aShareCompany);
		if (aShareCompany.hasOutstandingLoans ()) {
			tLoanAmount = aShareCompany.getLoanAmount ();
			if (aShareCompany.getCash () < tLoanAmount) {
				tToolTip = tToolTipPrefix + "Treasury has less than the Loan Amount " + 
							Bank.formatCash (tLoanAmount);
			}
		} else {
			tToolTip = tToolTipPrefix + "has no Outstanding Loans";
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
		String tToolTipPrefix;
		
		tToolTip = GUI.EMPTY_STRING;
		tToolTipPrefix = getToolTipPrefix (aShareCompany);
		if (aShareCompany.hasOutstandingLoans ()) {
			if (! aShareCompany.wasRepaymentHandled ()) {
				tLoanAmount = aShareCompany.getLoanAmount ();
				if (aShareCompany.getCash () >= tLoanAmount) {
					tToolTip = tToolTipPrefix + "Treasury has enough to pay an Outstanding Loan";
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
				tToolTip = tToolTipPrefix + "Outstanding Loans have been confirmed";
			}
		} else {
			tToolTip = tToolTipPrefix + "has no Outstanding Loans";
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
		String tNotification;
		String tRoundID;
		ActorI.ActionStates tCurrentRoundState;
		RoundManager tRoundManager;
		Round tCurrentRound;
		boolean tRepaymentHandled;
		int tCurrentPlayerIndex;
		int tShareFoldCount;
		int tNewShareFoldCount;
		int tOldShareFoldCount;
		FormCGR tFormCGR;
		
		tRoundManager = gameManager.getRoundManager ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tCurrentRoundState = tCurrentRound.getRoundState ();
		tRoundID = tCurrentRound.getID ();
		tRepaymentHandledAction = new RepaymentHandledAction (tCurrentRoundState, tRoundID, aShareCompany);

		tRepaymentHandled = true;
		aShareCompany.setRepaymentHandled (tRepaymentHandled);
		tFormCGR = (FormCGR) formCompany;
		tOldShareFoldCount = tFormCGR.getShareFoldCount ();
		if (aShareCompany.willFold ()) {
			tShareFoldCount = aShareCompany.getShareFoldCount ();
			tFormCGR.addShareFoldCount (tShareFoldCount);
			
			tNewShareFoldCount = tFormCGR.getShareFoldCount ();
			
			tNotification = tFormCGR.buildFoldNotification (aShareCompany, tShareFoldCount);
			tFormCGR.setNotificationText (tNotification);
			tRepaymentHandledAction.addShareFoldCountEffect (aShareCompany, tOldShareFoldCount, tNewShareFoldCount);
			tRepaymentHandledAction.addSetNotificationEffect (aShareCompany, tNotification);
		}
	
		tRepaymentHandledAction.addSetRepaymentHandledEffect (aShareCompany, tRepaymentHandled);
		tRepaymentHandledAction.addRebuildFormationPanelEffect (aShareCompany);

		tCurrentPlayerIndex = getCurrentPlayerIndex ();
	
		tFormCGR.rebuildFormationPanel (tCurrentPlayerIndex);
		aShareCompany.addAction (tRepaymentHandledAction);
	}

	public void buildSpecialButtons (ShareCompany aShareCompany, JPanel aShareCompanyJPanel, boolean aActingPlayer) {
		KButton tPayFromTreasury;
		KButton tPayFromPresident;
		KButton tConfirm;
		String tToolTip;
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;
		if (aActingPlayer) {
			tToolTip = canPayFromTreasury (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tPayFromTreasury = tFormCGR.buildSpecialButton (PAY_FROM_TREASURY, PAY_TREASURY, tToolTip, this);
		aShareCompanyJPanel.add (tPayFromTreasury);
		aShareCompanyJPanel.add (Box.createHorizontalStrut (10));
		aShareCompany.addSpecialButton (tPayFromTreasury);
		
		if (aActingPlayer) {
			tToolTip = canPayFromPresident (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tPayFromPresident = tFormCGR.buildSpecialButton (PAY_FROM_PRESIDENT, PAY_PRESIDENT, tToolTip, this);
		aShareCompanyJPanel.add (tPayFromPresident);
		aShareCompany.addSpecialButton (tPayFromPresident);
		
		if (aActingPlayer) {
			tToolTip = confirmRepayment (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tConfirm = tFormCGR.buildSpecialButton (CONFIRM_REPAYMENT, CONFIRM_REPAYMENT, tToolTip, this);
		aShareCompanyJPanel.add (tConfirm);
		aShareCompany.addSpecialButton (tConfirm);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		super.actionPerformed (aEvent);
		
		String tActionCommand;
		ShareCompany tShareCompany;
		KButton tActivatedButton;
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;
		
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
			if (tActionCommand.equals (FormCGR.FOLD)) {
				tFormCGR.handleFoldIntoFormingCompany ();
			} else if (tActionCommand.equals (CONTINUE)) {
				tFormCGR.handleFormationComplete ();
			}
		}
		
		checkAndHandleRoundEnds ();
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

	public boolean allRepaymentsFinished () {
		boolean tAllRepaymentsFinished;
		PlayerManager tPlayerManager;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tAllRepaymentsFinished = tPlayerManager.allRepaymentsFinished ();

		return tAllRepaymentsFinished;
	}
	
	@Override
	public void updateContinueButton () {
		String tToolTip;
		String tFormingCompanyAbbrev;
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;

		if (repaymentFinished () && actingPlayer) {
			if (tFormCGR.getFormationState ().equals ((ActorI.ActionStates.LoanRepayment))) {
				tFormingCompanyAbbrev = tFormCGR.getFormingCompanyAbbrev ();
				continueButton.setEnabled (true);
				tToolTip = GUI.EMPTY_STRING;
				if (tFormCGR.haveSharesToFold ()) {
					tToolTip = "There are Outstanding Loans, " + tFormingCompanyAbbrev + " will Form.";
				} else {
					tToolTip = "No Outstanding Loans, " + tFormingCompanyAbbrev + " will not Form.";			
				}
				continueButton.setToolTipText (tToolTip);
				continueButton.setVisible (true);
			} else {
				continueButton.setEnabled (false);
				tToolTip = "Not Ready Yet";
				continueButton.setToolTipText (tToolTip);
				continueButton.setVisible (false);
			}	
			if (tFormCGR.haveSharesToFold ()) {
				continueButton.setActionCommand (FormCGR.FOLD);
			} else {
				continueButton.setActionCommand (CONTINUE);
			}
		} else {
			continueButton.setVisible (false);
		}
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
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;
		if (repaymentFinished ()) {
			if (tFormCGR.getFormationState ().equals ((ActorI.ActionStates.LoanRepayment))) {
				doneButton.setEnabled (false);
				tToolTip = PAYBACK_COMPLETED;
				doneButton.setToolTipText (tToolTip);
			} else {
				doneButton.setEnabled (false);
				tToolTip = "Not Ready Yet";
				doneButton.setToolTipText (tToolTip);
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
							tToolTip = LOANS_REPAYMENTS_NEEDED;
							doneButton.setToolTipText (tToolTip);
						}
					}
				}
			}
			doneButton.setEnabled (tAllCompaniesHandled);
		}
	}

	public void redeemLoanAndUpdate (ShareCompany aShareCompany, int tLoanCount, int tPresidentContribution) {
		int tCurrentPlayerIndex;
		boolean tHandledRepayment;
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;
		tCurrentPlayerIndex = getCurrentPlayerIndex ();
		tHandledRepayment = true;
		aShareCompany.redeemLoans (tLoanCount, tPresidentContribution, tHandledRepayment);
		tFormCGR.rebuildFormationPanel (tCurrentPlayerIndex);
	}
}
