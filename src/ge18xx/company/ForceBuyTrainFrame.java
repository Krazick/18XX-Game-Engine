package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private static final String CANCEL_ACTION = "Cancel";
	private static final String SELL_ACTION = "SellStock";
	private static final String UNDO_SELL_ACTION = "UndoSellStock";
	private static final String DECLARE_BANKRUPTCY_ACTION = "DeclareBankruptcy";
	private static final long serialVersionUID = 1L;
	JButton doSellButton;
	JButton doBuyButton;
	JButton undoButton;
	JButton declareBankruptcyButton;
	JButton cancelButton;
	JPanel buttonJPanel;
	JPanel infoJPanel;
	JPanel mainJPanel;
	JPanel stockCertificatesJPanel;
	JLabel corporationTreasuryLabel;
	JLabel presidentTreasuryLabel;
	JLabel totalTreasuryLabel;
	JLabel cashNeededLabel;
	JLabel totalLiquidAssetLabel;
	JLabel saleLimitReasons;
	JLabel frameLabel;
	Train train;
	TrainCompany trainCompany;
	Player president;
	String operatingRoundID;
	GameManager gameManager;
	int buyingTrainCompanyTreasury;
	int presidentTreasury;
	int sellActionCount;
	int cashNeeded;
	int liquidAssetTotal;
	
	public ForceBuyTrainFrame (TrainCompany aBuyingCompany, Train aCheapestTrain) {
		super ("Force Buy Train");
		
		trainCompany = aBuyingCompany;
		train = aCheapestTrain;
		sellActionCount = 0;
		
		buildMainJPanel ();

		add (mainJPanel);
		
		updateButtons ();
		setSize (400, 350);
		pack ();
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		setVisible (true);
	}

	private void buildInfoJPanel () {
		JLabel tPresidentLabel;
		JPanel tTrainPanel;
		PortfolioHolderI tPortfolioHolder;
		int tCompanyTreasury;
		
		tPortfolioHolder = trainCompany.getPresident ();
		if (tPortfolioHolder.isPlayer ()) {
			president = (Player) tPortfolioHolder;
		}
		presidentTreasury = president.getCash ();
		gameManager = trainCompany.getGameManager ();

		tCompanyTreasury = trainCompany.getCash ();
		infoJPanel = new JPanel ();
		infoJPanel.setLayout (new BoxLayout (infoJPanel, BoxLayout.Y_AXIS));
		infoJPanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Force Buy Train for " + trainCompany.getAbbrev ());
//		infoJPanel.add (frameLabel);
//		infoJPanel.add (Box.createVerticalStrut (10));
		addLabelAndSpace (frameLabel);
		corporationTreasuryLabel = new JLabel ("Treasury: " + Bank.formatCash (tCompanyTreasury));
//		infoJPanel.add (corporationTreasuryLabel);
//		infoJPanel.add (Box.createVerticalStrut (10));
		addLabelAndSpace (corporationTreasuryLabel);
		tPresidentLabel = new JLabel ("President: " + president.getName ());
//		infoJPanel.add (tPresidentLabel);
//		infoJPanel.add (Box.createVerticalStrut (10));
		addLabelAndSpace (tPresidentLabel);
		presidentTreasuryLabel = new JLabel ("President Treasury: " + Bank.formatCash (presidentTreasury));
//		infoJPanel.add (presidentTreasuryLabel);
//		infoJPanel.add (Box.createVerticalStrut (10));
		addLabelAndSpace (presidentTreasuryLabel);
		totalTreasuryLabel = new JLabel ("Total Treasury: " + Bank.formatCash (presidentTreasury + tCompanyTreasury));
//		infoJPanel.add (totalTreasuryLabel);
//		infoJPanel.add (Box.createVerticalStrut (10));
		addLabelAndSpace (totalTreasuryLabel);
		cashNeededLabel = new JLabel ("XXX");
		updateCashNeeded ();
		addLabelAndSpace (cashNeededLabel);
		
		totalLiquidAssetLabel = new JLabel ("YYY");
		addLabelAndSpace (totalLiquidAssetLabel);
		saleLimitReasons = new JLabel ("");
		addLabelAndSpace (saleLimitReasons);
		
		tTrainPanel = train.buildCertificateInfoPanel ();
		infoJPanel.add (tTrainPanel);
		infoJPanel.add (Box.createVerticalStrut (10));
	}

	private void addLabelAndSpace (JLabel aLabelToAdd) {
		infoJPanel.add (aLabelToAdd);
		infoJPanel.add (Box.createVerticalStrut (10));
	}

	private int calculateCashNeeded (int aCompanyTreasury, int aTrainCost) {
		int tCashNeeded;
		
		tCashNeeded = aTrainCost - (presidentTreasury + aCompanyTreasury);
		
		return tCashNeeded;
	}
	
	private void updateCashNeeded () {
		int tCompanyTreasury;
		int tTrainCost;
		
		tTrainCost = train.getPrice ();
		tCompanyTreasury = trainCompany.getTreasury ();
		cashNeeded = calculateCashNeeded (tCompanyTreasury, tTrainCost);
		if (cashNeeded > 0) {
			cashNeededLabel.setText ("Cash Needed: " + Bank.formatCash (cashNeeded));
		} else {
			cashNeededLabel.setText ("Have enough Cash, will have " + 
					Bank.formatCash (-cashNeeded) + " in President Treasury");
		}
	}
	
	private void updateLiquidAssetLabel () {
		int tLiquidAssetTotal;
		
		tLiquidAssetTotal = calculateTotalLiquidCertificateValue ();
		setLiquidAssetTotal (tLiquidAssetTotal);
		if (liquidAssetTotal > 0) {
			totalLiquidAssetLabel.setText ("Total Saleable Certificates Value " + Bank.formatCash (liquidAssetTotal));
		} else {
			totalLiquidAssetLabel.setText ("No Certificates can be sold to generate cash");
		}
	}
	
	private void buildButtonJPanel () {
		buttonJPanel = new JPanel ();
		buttonJPanel.setLayout (new BoxLayout (buttonJPanel, BoxLayout.X_AXIS));
		doBuyButton = setupButton (CorporationFrame.BUY_TRAIN, BUY_ACTION);
		doSellButton = setupButton (Player.SELL_LABEL, SELL_ACTION);
		undoButton = setupButton ("Undo " + Player.SELL_LABEL, UNDO_SELL_ACTION);
		declareBankruptcyButton = setupButton ("Declare Bankruptcy", DECLARE_BANKRUPTCY_ACTION);
		cancelButton = setupButton ("Cancel", CANCEL_ACTION);
	}

	private JButton setupButton (String aLabel, String aActionCommand) {
		JButton tJButton;
		
		tJButton = new JButton (aLabel);
		tJButton.setActionCommand (aActionCommand);
		tJButton.addActionListener (this);
		buttonJPanel.add (tJButton);
		
		return tJButton;
	}
	
	private void buildStockJPanel () {
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = getPresidentPortfolio ();
		stockCertificatesJPanel = tPresidentPortfolio.buildShareCertificateJPanel (Corporation.SHARE_COMPANY, 
						Player.SELL_LABEL, this, Player.NO_PLAYER, gameManager);
	}
	
	private void buildMainJPanel () {
		mainJPanel = new JPanel ();
		mainJPanel.setLayout (new BoxLayout (mainJPanel, BoxLayout.Y_AXIS));
		updateMainJPanel ();
	}
	
	private void updateMainJPanel () {
		mainJPanel.removeAll ();
		buildInfoJPanel ();
		buildStockJPanel ();
		buildButtonJPanel ();

		mainJPanel.add (infoJPanel);
		mainJPanel.add (stockCertificatesJPanel);
		mainJPanel.add (buttonJPanel);		
	}
	
	private void updateButtons () {
		updateUndoButtion ();
		updateSellButton ();
		updateBuyTrainButton ();
		updateLiquidAssetLabel ();
		updateDeclareBankruptcyButton ();
		updateCancelButton ();
	}
	
	private void updateTreasuryLabels () {
		presidentTreasury = president.getCash ();
		presidentTreasuryLabel.setText ("President Treasury: " + Bank.formatCash (presidentTreasury));
		totalTreasuryLabel.setText ("Total Treasury: " + Bank.formatCash (presidentTreasury + trainCompany.getCash ()));
	}
	
	private void updateCancelButton () {
		if (sellActionCount == 0) {
			cancelButton.setEnabled (true);
			cancelButton.setToolTipText ("Cancel Force Train Buy, and Close window");
		} else {
			cancelButton.setEnabled (false);
			cancelButton.setToolTipText ("Stock has been sold, must undo all Sales or complete Train purchase");	
		}
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
	
	private void setLiquidAssetTotal (int aLiquidAssetTotal) {
		liquidAssetTotal = aLiquidAssetTotal;
	}
	
	private int calculateTotalLiquidCertificateValue () {
		int tLiquidCertificateValue;
		Portfolio tPresidentPortfolio;
		List<Certificate> tCertificatesCanBeSold;
		int tPresidentPercent;
		int tNextPresidentPercent;
		int tCanSellPercent;
		int tCanSellCount;
		int tSharePercent = 10;	// Should determine minumum Share Percent for Company.
		int tCurrentCorpCounted;
		Set<String> tReasons;
		String tReason, tAllReasons;
		String tPreviousAbbrev, tCurrentAbbrev, tTrainCoAbbrev;
		int tSellLimit, tCertCount;
		
		tLiquidCertificateValue = 0;
		tPresidentPortfolio = getPresidentPortfolio ();
		tCertificatesCanBeSold = tPresidentPortfolio.getCertificatesCanBeSold ();
		
		tPresidentPercent = trainCompany.getPresidentPercent ();
		tNextPresidentPercent = trainCompany.getNextPresidentPercent ();
		tCanSellPercent = (tPresidentPercent - tNextPresidentPercent);
		tCanSellCount = tCanSellPercent/tSharePercent;
		tCurrentCorpCounted = 0;
		tReasons = new HashSet<String> ();
		tTrainCoAbbrev = trainCompany.getAbbrev ();
		
		// Determine Sale Limits for Certificates from trainCompany, limited by Next President Holdings
		for (Certificate tCertificate : tCertificatesCanBeSold) {
			if (tCertificate.getCompanyAbbrev ().equals (tTrainCoAbbrev)) {
				if (tCurrentCorpCounted < tCanSellCount) {
					tLiquidCertificateValue += tCertificate.getCost ();
					tCurrentCorpCounted++;
				} else {
					tReason = trainCompany.getNextPresidentName () + " has " + tNextPresidentPercent + 
							"% of " + tTrainCoAbbrev + ", limits Sale to " + tCanSellPercent + "%";
					tReasons.add (tReason);
				}
			}
		}
		
		// Determine Sale Limits for Certificates from other Companies, limited by Bank Pool Limits
		
		tPreviousAbbrev = "";
		tCertCount = 0;
		tSellLimit = 9;
		for (Certificate tCertificate : tCertificatesCanBeSold) {
			tCurrentAbbrev = tCertificate.getCompanyAbbrev ();
			if (! tCurrentAbbrev.equals (tTrainCoAbbrev)) {
				if (! tCurrentAbbrev.equals (tPreviousAbbrev)) {
					tSellLimit = tCertificate.sellLimit ();
					tCertCount = 0;
					tPreviousAbbrev = tCurrentAbbrev;
				}
				if (tCertCount < tSellLimit) {
					tLiquidCertificateValue += tCertificate.getCost ();
					tCertCount++;
				} else {
					tReason = "Bank Pool can only hold " + tSellLimit + " more " + tCurrentAbbrev + 
							" Certificates";
					tReasons.add (tReason);
				}
			}
		}
		
		tAllReasons = "";
		for (String tAReason : tReasons) {
			if (tAllReasons.length () > 0) {
				tAllReasons += "<br>";
			}
			tAllReasons += tAReason;
		}
		tAllReasons = "<html>" + tAllReasons + "</html>";
		setSaleLimitReasons (tAllReasons);
		
		return tLiquidCertificateValue;
	}
	
	private void setSaleLimitReasons (String aSaleLimitReasons) {
		saleLimitReasons.setText (aSaleLimitReasons);
	}
		
	private int getCountOfCertificatesForSale () {
		Portfolio tPresidentPortfolio;
		int tShareCount;
		
		tPresidentPortfolio = getPresidentPortfolio ();
		tShareCount = tPresidentPortfolio.getCountOfCertificatesForSale ();
		
		return tShareCount;
	}

	private Portfolio getPresidentPortfolio () {
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = president.getPortfolio ();
		
		return tPresidentPortfolio;
	}
	
	private int getSelectedStocksSaleCost () {
		Portfolio tPresidentPortfolio;
		int tShareCost;
		
		tPresidentPortfolio = getPresidentPortfolio ();
		tShareCost = tPresidentPortfolio.getSelectedStocksSaleCost ();
		
		return tShareCost;
	}
	
	private boolean hasSelectedStocksToSell () {
		boolean tHasSelectedStocksToSell = false;
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = getPresidentPortfolio ();
		if (tPresidentPortfolio.hasSelectedStocksToSell ()) {
			tHasSelectedStocksToSell = true;
		}

		return tHasSelectedStocksToSell;
	}
	
	private Certificate getACertificateToSell () {
		Certificate tCertificateToSell;
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = getPresidentPortfolio ();
		tCertificateToSell = tPresidentPortfolio.getSelectedStockToSell ();
		
		return tCertificateToSell;
	}
	
	private boolean allSelectedSharesSameSize () {
		boolean allSelectedSharesSameSize;
		Portfolio tPresidentPortfolio;
		
		tPresidentPortfolio = getPresidentPortfolio ();
		allSelectedSharesSameSize = tPresidentPortfolio.allSelectedSharesSameSize ();
		
		return allSelectedSharesSameSize;
	}
	
	private boolean tooManySharesSelectedToSell () {
		boolean tTooManySharesSelectedToSell;
		Certificate tCertificateToSell;
		int tTotalSaleCost;
		int tCertificateCost;
		int tExcessCash;
		
		tTooManySharesSelectedToSell = false;
		tCertificateToSell = getACertificateToSell ();
		tTotalSaleCost = getSelectedStocksSaleCost ();
		// TODO -- Find the smallest percentage Share to be sold
		tCertificateCost = tCertificateToSell.getCost ();
		if (tTotalSaleCost > cashNeeded) {
			tExcessCash = tTotalSaleCost - cashNeeded;
			if (tExcessCash > tCertificateCost) {
				if (allSelectedSharesSameSize ()) {
					tTooManySharesSelectedToSell = true;
				} else {
					tTooManySharesSelectedToSell = true;
					// TODO for 18XX games with non-President Share different sizes (20%, 5%, etc)
					// Determine way to flag when a mix of sizes selected to be sold at once.
					// For initial version, just allow this anyway.
				}
			}
		}
		
		return tTooManySharesSelectedToSell;
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
		String tToolTip;
		String tButtonLabel;
		String tSharesInfo;
		
		tButtonLabel = Player.SELL_LABEL;
		if (haveEnoughCash ()) {
			doSellButton.setEnabled (false);
			tToolTip = "Enough cash to buy the Train, can't sell stock";
		} else if (hasSelectedStocksToSell ()) {
			if (! president.hasSelectedSameStocksToSell ()) {
				doSellButton.setEnabled (false);
				tToolTip = "All Stocks selected to be sold must be the same Company";
			} else if (president.willSaleOverfillBankPool ()) {
				doSellButton.setEnabled (false);
				tToolTip = "Cannot sell all selected Shares - Bank pool will be over 50%";
			} else if (tooManySharesSelectedToSell ()) {
				doSellButton.setEnabled (false);
				tToolTip = "Selected more shares to sell than required to reach cash needed";
				// Test if stock to sale would change current Corp President... if not disallow
			} else if (willChangePresidency ()) {
				doSellButton.setEnabled (false);
				tToolTip = "Cannot sell all selected Shares - Operating " + 
						trainCompany.getAbbrev () + " must buy Train, cannot sell out Presidency";
			} else {
				doSellButton.setEnabled (true);
				tToolTip = "";
				tSharesInfo = buildSharesInfo ();
				tButtonLabel = tButtonLabel + " " + tSharesInfo;
			}
			
		} else {
			doSellButton.setEnabled (false);
			tToolTip = "No Stocks Selected to be Sold";
		}
		doSellButton.setText (tButtonLabel);
		doSellButton.setToolTipText (tToolTip);
	}

	private String buildSharesInfo () {
		int tShareCountToSell;
		int tSharesTotalValue;
		String tShareInfo;
		Certificate tCertificateToSell;
		
		tCertificateToSell = getACertificateToSell ();
		tShareCountToSell = getCountOfCertificatesForSale ();
		tSharesTotalValue = getSelectedStocksSaleCost ();
		tShareInfo = tShareCountToSell + " Share";
		if (tShareCountToSell > 1) {
			tShareInfo += "s";
		}
		tShareInfo += " of " + tCertificateToSell.getCompanyAbbrev () +" for " +
				Bank.formatCash (tSharesTotalValue);
		
		return tShareInfo;
	}
	
	private void updateBuyTrainButton () {
		if (haveEnoughCash ()) {
			doBuyButton.setEnabled (true);
			doBuyButton.setToolTipText ("Can Force Buy Train");
		} else {
			doBuyButton.setEnabled (false);
			doBuyButton.setToolTipText ("Not Enough cash to buy Train");
		}
	}
	
	private void updateDeclareBankruptcyButton () {
		if (liquidAssetTotal >= cashNeeded) {
			declareBankruptcyButton.setEnabled (false);
			declareBankruptcyButton.setToolTipText ("Have enough to buy Train");
		} else {
			declareBankruptcyButton.setEnabled (true);
			declareBankruptcyButton.setToolTipText ("Does NOT have enough to buy Train");
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
		if (DECLARE_BANKRUPTCY_ACTION.equals (aEvent.getActionCommand ())) {
			declareBankruptcy ();
		}
		if (CANCEL_ACTION.equals (aEvent.getActionCommand ())) {
			cancelForceTrainBuy ();
		}
		updateButtons ();
	}

	private void cancelForceTrainBuy () {
		setVisible (false);
	}
	
	private void declareBankruptcy () {
		System.out.println (president.getName () + " is Declaring Bankruptcy for " + trainCompany.getName ());
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
		buildStockJPanel ();
		updateButtons ();
		updateTreasuryLabels ();
		updateMainJPanel ();
		repaint ();
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
