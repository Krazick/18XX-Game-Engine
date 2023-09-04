package ge18xx.company.special;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class LoanRepayment extends TriggerClass implements ActionListener {
	public static final String PAY_FROM_TREASURY = "Pay From Treasury";
	public static final String PAY_FROM_PRESIDENT = "Pay From President";
	public static final String CONFIRM_REPAYMENT = "Confirm Repayment";
	public static final String PAY_TREASURY = "PayTreasury";
	public static final String PAY_PRESIDENT = "PayPresident";
	public static final String DONE = "Done";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";

	XMLFrame allLoanRepaymentFrame;
	GameManager gameManager;
	int currentPlayerIndex;
	boolean currentPlayerDone;
	JPanel allLoanRepaymentJPanel;
	
	public LoanRepayment (GameManager aGameManager) {
		String tFrameName;
		
		gameManager = aGameManager;
		tFrameName = "Loan Repayment Frame";
		buildAllPlayers (tFrameName);
	}

	public void buildAllPlayers (String aFrameName) {
		Border tMargin;
		Point tRoundFrameOffset;

		allLoanRepaymentFrame = new XMLFrame (aFrameName, gameManager);
		allLoanRepaymentFrame.setSize (800, 600);
		
		allLoanRepaymentJPanel = new JPanel ();
		tMargin = new EmptyBorder (10,10,10,10);
		allLoanRepaymentJPanel.setBorder (tMargin);
		
		allLoanRepaymentJPanel.setLayout (new BoxLayout (allLoanRepaymentJPanel, BoxLayout.Y_AXIS));

		setupPlayers ();
		
		allLoanRepaymentFrame.add (allLoanRepaymentJPanel);
		
		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		allLoanRepaymentFrame.setLocation (tRoundFrameOffset);
		allLoanRepaymentFrame.showFrame ();
	}

	public void setupPlayers () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPresident;
		
		tActingPresident = findActingPresident ();
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		currentPlayerIndex = tPlayerManager.getPlayerIndex (tActingPresident);
		updatePlayers (tPlayers, tActingPresident);
	}

	public boolean updateToNextPlayer (List<Player> aPlayers) {
		Player tActingPresident;
		Player tFirstPresident;
		PlayerManager tPlayerManager;
		int tNextPlayerIndex;
		boolean tFoundNextPlayer;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tNextPlayerIndex = tPlayerManager.getNextPlayerIndex (currentPlayerIndex);
		tActingPresident = tPlayerManager.getPlayer (tNextPlayerIndex);
		tFirstPresident = findActingPresident ();
		if (tActingPresident == tFirstPresident) {
			tFoundNextPlayer = false;
			System.out.println ("All Players processed");
		} else {
			tFoundNextPlayer = true;
			updatePlayers (aPlayers, tActingPresident);
			System.out.println ("Updated to Next Player");
		}
		
		return tFoundNextPlayer;
	}
	
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		JPanel tPlayerLoanRepaymentPanel;
		Color tBackgroundColor;
		boolean tActingPlayer;
		
		currentPlayerDone = false;

		for (Player tPlayer : aPlayers) {
			if (aActingPresident == tPlayer) {
				tBackgroundColor = Color.ORANGE;
				tActingPlayer = true;
			} else {
				tBackgroundColor = GUI.defaultColor;
				tActingPlayer = false;
			}

			tPlayerLoanRepaymentPanel = buildPlayerLoanRepaymentJPanel (tPlayer, tActingPlayer);
			tPlayerLoanRepaymentPanel.setBackground (tBackgroundColor);
			allLoanRepaymentJPanel.add (tPlayerLoanRepaymentPanel);
			allLoanRepaymentJPanel.add (Box.createVerticalStrut (10));
		}
	}

	public Player findActingPresident () {
		Corporation tActingCorporation;
		Player tActingPlayer;
		
		tActingCorporation = gameManager.getOperatingCompany ();
		tActingPlayer = (Player) tActingCorporation.getPresident ();
		
		return tActingPlayer;
	}
	
	public JPanel buildPlayerLoanRepaymentJPanel (Player aPlayer, boolean aActingPlayer) {
		JPanel tLoanRepaymentJPanel;
		JLabel tPresidentName;
		JLabel tPresidentTreasury;
		JPanel tPortfolio;
		JPanel tCompanies;
		JButton tDone;
		Portfolio tPlayerPortfolio;
		Border tBasicBorder;
		Border tMargin;
		Border tBorder;
		String tToolTip;
		
		tLoanRepaymentJPanel = new JPanel ();
		tLoanRepaymentJPanel.setLayout (new BoxLayout (tLoanRepaymentJPanel, BoxLayout.X_AXIS));
		tMargin = new EmptyBorder (10,10,10,10);

		tPresidentName = new JLabel ("Name: " + aPlayer.getName ());
		tLoanRepaymentJPanel.add (tPresidentName);
		tLoanRepaymentJPanel.add (Box.createHorizontalStrut (10));
	
		tPresidentTreasury = new JLabel ("Cash: " + Bank.formatCash (aPlayer.getCash ()));
		tLoanRepaymentJPanel.add (tPresidentTreasury);
		tLoanRepaymentJPanel.add (Box.createHorizontalStrut (10));
		
		tPlayerPortfolio = aPlayer.getPortfolio ();
		tPortfolio = buildPlayerPortfolioJPanel (tPlayerPortfolio);
		tLoanRepaymentJPanel.add (tPortfolio);
		tLoanRepaymentJPanel.add (Box.createHorizontalStrut (10));
	
		tCompanies = buildPlayerCompaniesJPanel (tPlayerPortfolio, aActingPlayer);
		tLoanRepaymentJPanel.add (tCompanies);
		
		if (currentPlayerDone) {
			tToolTip = GUI.EMPTY_STRING;
		} else {
			tToolTip= "One or more Companies have loans to be repaid";
		}
		tDone = buildSpecialButton (DONE, DONE, tToolTip);
		tLoanRepaymentJPanel.add (tDone);
		
		tBasicBorder = BorderFactory.createLineBorder (Color.black, 1);
		tMargin = new EmptyBorder (10,10,10,10);
		tBorder = BorderFactory.createCompoundBorder (tBasicBorder, tMargin);
		tLoanRepaymentJPanel.setBorder (tBorder);
		
		return tLoanRepaymentJPanel;
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
	
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
		JPanel tPlayerCompaniesJPanel;
		JPanel tShareCompanyJPanel;
		JLabel tTitle;
		ShareCompany tShareCompany;
		Certificate tCertificate;
		int tCertificateCount;
		int tCertificateIndex;
		
		tPlayerCompaniesJPanel = new JPanel ();
		tPlayerCompaniesJPanel.setLayout (new BoxLayout (tPlayerCompaniesJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("Companies");
		tPlayerCompaniesJPanel.add (tTitle);
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
		JLabel tCompanyAbbrev;
		JLabel tLoans;
		JLabel tTreasury;
		Border tCorporateColorBorder;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));
		tCorporateColorBorder = aShareCompany.setupBorder ();
		tShareCompanyJPanel.setBorder (tCorporateColorBorder);
		
		tCompanyAbbrev = new JLabel (aShareCompany.getAbbrev ());
		tShareCompanyJPanel.add (tCompanyAbbrev);
		tShareCompanyJPanel.add (Box.createHorizontalStrut (10));
		
		tLoans = new JLabel ("Loans: " + aShareCompany.getLoanCount ());
		tShareCompanyJPanel.add (tLoans);
		tShareCompanyJPanel.add (Box.createHorizontalStrut (10));

		tTreasury = new JLabel ("Treasury: " + Bank.formatCash (aShareCompany.getTreasury ()));
		tShareCompanyJPanel.add (tTreasury);
		tShareCompanyJPanel.add (Box.createHorizontalStrut (10));

		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tShareCompanyJPanel;
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
		tPayFromTreasury = buildSpecialButton (PAY_FROM_TREASURY, PAY_TREASURY, tToolTip);
		aShareCompanyJPanel.add (tPayFromTreasury);
		aShareCompanyJPanel.add (Box.createHorizontalStrut (10));
		aShareCompany.addSpecialButton (tPayFromTreasury);
		
		if (aActingPlayer) {
			tToolTip = canPayFromPresident (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tPayFromPresident = buildSpecialButton (PAY_FROM_PRESIDENT, PAY_PRESIDENT, tToolTip);
		aShareCompanyJPanel.add (tPayFromPresident);
		aShareCompany.addSpecialButton (tPayFromPresident);
		
		if (aActingPlayer) {
			tToolTip = canPayFromPresident (aShareCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tConfirm = buildSpecialButton (CONFIRM_REPAYMENT, CONFIRM_REPAYMENT, tToolTip);
		aShareCompanyJPanel.add (tConfirm);
		aShareCompany.addSpecialButton (tConfirm);
	}

	public String confirmRepayment (ShareCompany aShareCompany) {
		String tToolTip;
		int tLoanAmount;
		
		tToolTip = GUI.EMPTY_STRING;
		if (aShareCompany.hasOutstandingLoans ()) {
			tLoanAmount = aShareCompany.getLoanAmount ();
			if (aShareCompany.getCash () >= tLoanAmount) {
				tToolTip = "Company [" + aShareCompany.getAbbrev () + "] Treasury has enough to pay the Loan Amount " + 
							Bank.formatCash (tLoanAmount);
			}
		} else {
			tToolTip = "Company [" + aShareCompany.getAbbrev () + "] has no Outstanding Loans";
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
			tToolTip = "Company [" + aShareCompany.getAbbrev () + "] has no Outstanding Loans";
		}
		
		return tToolTip;
	}

	public JButton buildSpecialButton (String aTitle, String aActionCommand, String aToolTip) {
		JButton tSpecialButton;
		boolean tEnabled;
		
		tEnabled = getEnabled (aToolTip);
		tSpecialButton = new JButton (aTitle);
		tSpecialButton.setActionCommand (aActionCommand);
		tSpecialButton.setEnabled (tEnabled);
		tSpecialButton.setToolTipText (aToolTip);
		tSpecialButton.addActionListener (this);
		
		return tSpecialButton;
	}

	public boolean getEnabled (String aToolTip) {
		boolean tEnabled;
		
		if (GUI.EMPTY_STRING.equals (aToolTip)) {
			tEnabled = true;
		} else {
			tEnabled = false;
		}
		
		return tEnabled;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		ShareCompany tShareCompany;
		Player tPlayer;
		JButton tActivatedButton;
		
		tActionCommand = aEvent.getActionCommand ();
		System.out.println ("Action Command selected: " + tActionCommand);
		tActivatedButton = getActivatedButton (aEvent);
		if (tActivatedButton != GUI.NO_BUTTON) {
			tShareCompany = findShareCompany (tActivatedButton);
			System.out.println ("Button for Share Company " + tShareCompany.getAbbrev ());
			if (tShareCompany != ShareCompany.NO_SHARE_COMPANY) {
				tPlayer = (Player) tShareCompany.getPresident ();
				System.out.println ("President of Company is " + tPlayer.getName ());
			}
		}
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
