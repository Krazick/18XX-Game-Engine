package ge18xx.company.special;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.toplevel.XMLFrame;

public class LoanRepayment extends TriggerClass {
	XMLFrame allPlayers;
	List<JPanel> playerPanels;
	GameManager gameManager;
 
	public LoanRepayment (GameManager aGameManager) {
		String tFrameName;
		
		gameManager = aGameManager;
		tFrameName = "Loan Repayment Frame";
		buildAllPlayers (tFrameName);
		allPlayers.showFrame ();
		System.out.println ("Initiate Loan Repayment Game: " + aGameManager.getActiveGameName ());
	}

	public void buildAllPlayers (String aFrameName) {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		JPanel tLoanRepaymentJPanel;
		JPanel tPlayerLoanRepaymentPanel;
		
		allPlayers = new XMLFrame (aFrameName, gameManager);
		allPlayers.setSize (800, 600);
		tLoanRepaymentJPanel = new JPanel ();
		tLoanRepaymentJPanel.setLayout (new BoxLayout (tLoanRepaymentJPanel, BoxLayout.Y_AXIS));
		
		playerPanels = new LinkedList<JPanel> ();

		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		for (Player tPlayer : tPlayers) {
			tPlayerLoanRepaymentPanel = buildPlayerLoanRepaymentJPanel (tPlayer);
			tLoanRepaymentJPanel.add (tPlayerLoanRepaymentPanel);
			tLoanRepaymentJPanel.add (Box.createVerticalStrut (10));
		}
		
		allPlayers.add (tLoanRepaymentJPanel);
	}

	public JPanel buildPlayerLoanRepaymentJPanel (Player aPlayer) {
		JPanel tLoanRepaymentJPanel;
		JLabel tPresidentName;
		JLabel tPresidentTreasury;
		JPanel tPortfolio;
		JPanel tCompanies;
		Border tBorder;
		Portfolio tPlayerPortfolio;
		
		tLoanRepaymentJPanel = new JPanel ();
		tLoanRepaymentJPanel.setLayout (new BoxLayout (tLoanRepaymentJPanel, BoxLayout.X_AXIS));
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
	
		tCompanies = buildPlayerCompaniesJPanel (tPlayerPortfolio);
		tLoanRepaymentJPanel.add (tCompanies);
		
		tBorder = BorderFactory.createLineBorder (Color.black, 1);
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
	
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio) {
		JPanel tPlayerCompaniesJPanel;
		JLabel tTitle;
		
		tPlayerCompaniesJPanel = new JPanel ();
		tPlayerCompaniesJPanel.setLayout (new BoxLayout (tPlayerCompaniesJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("Companies");
		tPlayerCompaniesJPanel.add (tTitle);

		return tPlayerCompaniesJPanel;
	}

}
