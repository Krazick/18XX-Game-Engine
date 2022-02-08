package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.train.Train;

public class ForceBuyTrainFrame extends JFrame implements ActionListener, ItemListener {
	private static final String BUY_ACTION = "BuyTrain";
	private static final String SELL_ACTION = "SellStock";
	private static final String UNDO_SELL_ACTION = "UndoSellStock";
	private static final long serialVersionUID = 1L;
	JButton doSellButton;
	JButton doBuyButton;
	JButton undoButton;
	JPanel buttonJPanel;
	JPanel infoJPanel;
	JPanel mainJPanel;
	JPanel stockCertificatesJPanel;
	JLabel corporationTreasuryLabel;
	JLabel presidentTreasuryLabel;
	JLabel totalTreasuryLabel;
	JLabel frameLabel;
	Train train;
	TrainCompany trainCompany;
	Player president;
	String operatingRoundID;
	GameManager gameManager;
	int buyingTrainCompanyTreasury;
	int presidentTreasury;
	int sellActionCount;
	
	public ForceBuyTrainFrame (TrainCompany aBuyingCompany, Train aCheapestTrain) {
		super ("Force Buy Train");
		PortfolioHolderI tPortfolioHolder;
		JLabel tPresidentLabel;
		JPanel tTrainPanel;
		
		trainCompany = aBuyingCompany;
		tPortfolioHolder = trainCompany.getPresident ();
		if (tPortfolioHolder.isPlayer ()) {
			president = (Player) tPortfolioHolder;
		}
		train = aCheapestTrain;
		presidentTreasury = president.getCash ();
		gameManager = trainCompany.getGameManager ();
		sellActionCount = 0;
		
		mainJPanel = new JPanel ();
		mainJPanel.setLayout (new BoxLayout (mainJPanel, BoxLayout.Y_AXIS));
		infoJPanel = new JPanel ();
		infoJPanel.setLayout (new BoxLayout (infoJPanel, BoxLayout.Y_AXIS));
		buttonJPanel = new JPanel ();
		buttonJPanel.setLayout (new BoxLayout (buttonJPanel, BoxLayout.X_AXIS));
		infoJPanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Force Buy Train for " + trainCompany.getAbbrev ());
		infoJPanel.add (frameLabel);
		infoJPanel.add (Box.createVerticalStrut (10));
		corporationTreasuryLabel = new JLabel ("Treasury: " + Bank.formatCash (trainCompany.getCash ()));
		infoJPanel.add (corporationTreasuryLabel);
		infoJPanel.add (Box.createVerticalStrut (10));
		tPresidentLabel = new JLabel ("President: " + president.getName ());
		infoJPanel.add (tPresidentLabel);
		infoJPanel.add (Box.createVerticalStrut (10));
		presidentTreasuryLabel = new JLabel ("President Treasury: " + Bank.formatCash (presidentTreasury));
		infoJPanel.add (presidentTreasuryLabel);
		infoJPanel.add (Box.createVerticalStrut (10));
		totalTreasuryLabel = new JLabel ("Total Treasury: " + Bank.formatCash (presidentTreasury + trainCompany.getCash ()));
		infoJPanel.add (totalTreasuryLabel);
		infoJPanel.add (Box.createVerticalStrut (10));
		tTrainPanel = train.buildCertificateInfoPanel ();
		infoJPanel.add (tTrainPanel);
		infoJPanel.add (Box.createVerticalStrut (10));
		
		setupStockJPanel ();
		
		doBuyButton = new JButton ("Buy Train");
		doBuyButton.setActionCommand (BUY_ACTION);
		doBuyButton.addActionListener (this);
		buttonJPanel.add (doBuyButton);
		doSellButton = new JButton ("Sell");
		doSellButton.setActionCommand (SELL_ACTION);
		doSellButton.setEnabled (false);
		doSellButton.addActionListener (this);
		buttonJPanel.add (doSellButton);
		undoButton = new JButton ("Undo Sell");
		undoButton.setActionCommand (UNDO_SELL_ACTION);
		undoButton.addActionListener (this);
		buttonJPanel.add (undoButton);
		
		setupMainJPanel ();

		add (mainJPanel);
		
		updateButtons ();
		setSize (400, 350);
		pack ();
		setVisible (true);
	}

	private void setupStockJPanel () {
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = president.getPortfolio ();
		stockCertificatesJPanel = tPresidentPortfolio.buildShareCertificateJPanel (Corporation.SHARE_COMPANY, "Sell", 
						this, Player.NO_PLAYER, gameManager);
	}
	
	private void setupMainJPanel () {
		mainJPanel.removeAll ();
		
		mainJPanel.add (infoJPanel);
		mainJPanel.add (stockCertificatesJPanel);
		mainJPanel.add (buttonJPanel);
	}
	
	private void updateButtons () {
		updateUndoButtion ();
		updateSellButton ();
		updateBuyTrainButton ();
	}
	
	private void updateTreasuryLabels () {
		presidentTreasury = president.getCash ();
		presidentTreasuryLabel.setText ("President Treasury: " + Bank.formatCash (presidentTreasury));
		totalTreasuryLabel.setText ("Total Treasury: " + Bank.formatCash (presidentTreasury + trainCompany.getCash ()));
	}
	
	private void updateUndoButtion () {
		
		if (sellActionCount == 0) {
			undoButton.setEnabled (false);
			undoButton.setToolTipText ("No Stock Sell Actions to undo. Close Window to undo previous OR Action");
		} else {
			undoButton.setEnabled (true);
			undoButton.setToolTipText ("Undo Previous Stock Sale");
		}
	}
	
	private boolean hasSelectedStocksToSell () {
		boolean tHasSelectedStocksToSell = false;
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = president.getPortfolio ();
		if (tPresidentPortfolio.hasSelectedStocksToSell ()) {
			tHasSelectedStocksToSell = true;
		}

		return tHasSelectedStocksToSell;
	}
	
	private boolean willChangePresidency () {
		boolean tWillChangePresidency = false;
		int tNextPresidentPercent, tPresidentPercent, tSelectedPercent;
		String tSelectedCompanyAbbrev;
		
		tSelectedCompanyAbbrev = president.getSelectedCompanyAbbrev ();
		
		if (tSelectedCompanyAbbrev.equals (trainCompany.getAbbrev ())) {
			tSelectedPercent = president.getSelectedPercent ();
			tPresidentPercent = trainCompany.getPresidentPercent ();
			tNextPresidentPercent = trainCompany.getNextPresidentPercent ();
			if (tNextPresidentPercent > (tPresidentPercent - tSelectedPercent)) {
				tWillChangePresidency = true;
			}			
		}
		
		return tWillChangePresidency;
	}
	
	private void updateSellButton () {
		if (haveEnoughCash ()) {
			doSellButton.setEnabled (false);
			doSellButton.setToolTipText ("Enough Cash to buy the Train, can't sell stock");
		} else if (hasSelectedStocksToSell ()) {
			if (! president.hasSelectedSameStocksToSell ()) {
				doSellButton.setEnabled (false);
				doSellButton.setToolTipText ("All Stocks selected to be sold must be the same Company");
			} else if (president.willSaleOverfillBankPool ()) {
				doSellButton.setEnabled (false);
				doSellButton.setToolTipText ("Cannot sell all selected Shares - Bank pool will be over 50%");
			} else if (willChangePresidency ()) {
				doSellButton.setEnabled (false);
				doSellButton.setToolTipText ("Cannot sell all selected Shares - Operating " + 
						trainCompany.getAbbrev () + " must buy Train, cannot sell out Presidency");
			} else {
				// Test if stock to sale would change current Corp President... if not disallow
				doSellButton.setEnabled (true);
				doSellButton.setToolTipText ("");
			}
			
		} else {
			doSellButton.setEnabled (false);
			doSellButton.setToolTipText ("No Stocks Selected to be Sold");
		}
	}
	
	private void updateBuyTrainButton () {
		if (haveEnoughCash ()) {
			doBuyButton.setEnabled (true);
			doBuyButton.setToolTipText ("Can Force Buy Train");
		} else {
			doBuyButton.setEnabled (false);
			doBuyButton.setToolTipText ("Not Enough Cash to buy Train");
		}
	}
	
	private boolean haveEnoughCash () {
		boolean tHaveEnoughCash = false;
		
		tHaveEnoughCash = ((presidentTreasury + trainCompany.getCash ()) > train.getPrice ());
		
		return tHaveEnoughCash;
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		if (BUY_ACTION.equals (aEvent.getActionCommand ())) {
			buyTrain ();
		}
		if (SELL_ACTION.equals (aEvent.getActionCommand ())) {
			sellStock ();
		}
		if (UNDO_SELL_ACTION.equals (aEvent.getActionCommand ())) {
			undoSellStock ();
		}
		updateButtons ();
	}

	private void undoSellStock () {
		trainCompany.undoAction ();
		sellActionCount--;
		refreshFrame ();
	}

	private void sellStock () {
		++sellActionCount;
		president.sellAction ();
		
		refreshFrame ();
	}

	private void refreshFrame () {
		setupStockJPanel ();
		updateButtons ();
		updateTreasuryLabels ();
		setupMainJPanel ();
	}

	private void buyTrain () {
		int tNeededCash;
		
		tNeededCash = train.getPrice () - trainCompany.getCash ();
		president.transferCashTo (trainCompany, tNeededCash);
		train.setSelection ();
		trainCompany.buyTrain (tNeededCash);
		setVisible (false);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		updateButtons ();
	}
}
