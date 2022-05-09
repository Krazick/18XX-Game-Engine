package ge18xx.company;

import java.awt.Component;
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
	private static final String EXCHANGE_ACTION = "Exchange";
	private static final String UNDO_ACTION = "Undo";
	private static final String DECLARE_BANKRUPTCY_ACTION = "DeclareBankruptcy";
	private static final long serialVersionUID = 1L;
	JButton doSellButton;
	JButton exchangeButton;
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
	int actionCount;
	int cashNeeded;
	int liquidAssetTotal;
	ShareCompany exchangedCompany;

	public ForceBuyTrainFrame (TrainCompany aBuyingCompany, Train aCheapestTrain) {
		super ("Force Buy Train");

		trainCompany = aBuyingCompany;
		train = aCheapestTrain;
		actionCount = 0;
		setExchangedCompany (ShareCompany.NO_SHARE_COMPANY);

		buildMainJPanel ();

		add (mainJPanel);

		updateButtons ();
		setSize (400, 350);
		pack ();
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		setVisible (true);
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
		infoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);

		infoJPanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Force Buy Train for " + trainCompany.getAbbrev ());
		addLabelAndSpace (frameLabel);
		corporationTreasuryLabel = new JLabel ("Treasury: " + Bank.formatCash (tCompanyTreasury));
		addLabelAndSpace (corporationTreasuryLabel);
		tPresidentLabel = new JLabel ("President: " + president.getName ());
		addLabelAndSpace (tPresidentLabel);
		presidentTreasuryLabel = new JLabel ("President Treasury: " + Bank.formatCash (presidentTreasury));
		addLabelAndSpace (presidentTreasuryLabel);
		totalTreasuryLabel = new JLabel ("Total Treasury: " + Bank.formatCash (presidentTreasury + tCompanyTreasury));
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
		infoJPanel.add (Box.createHorizontalStrut (50));
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
			cashNeededLabel.setText (
					"Have enough Cash, will have " + Bank.formatCash (-cashNeeded) + " in President Treasury");
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

	private void setExchangedCompany (ShareCompany aShareCompany) {
		exchangedCompany = aShareCompany;
	}

	private void buildButtonJPanel () {
		buttonJPanel = new JPanel ();
		buttonJPanel.setLayout (new BoxLayout (buttonJPanel, BoxLayout.X_AXIS));
		doBuyButton = setupButton (CorporationFrame.BUY_TRAIN, BUY_ACTION);
		doSellButton = setupButton (Player.SELL_LABEL, SELL_ACTION);
		exchangeButton = setupButton (Player.EXCHANGE_LABEL, EXCHANGE_ACTION);
		undoButton = setupButton ("Undo", UNDO_ACTION);
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

	private void updateButtons () {
		updateUndoButtion ();
		updateSellButton ();
		updateExchangeButton ();
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
		if (actionCount == 0) {
			cancelButton.setEnabled (true);
			cancelButton.setToolTipText ("Cancel Force Train Buy, and Close window");
		} else {
			cancelButton.setEnabled (false);
			cancelButton.setToolTipText ("Stock has been sold, must undo all Sales or complete Train purchase");
		}
	}

	private void updateUndoButtion () {

		if (actionCount == 0) {
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
		Set<String> tReasons;
		String tAllReasons;

		tLiquidCertificateValue = 0;
		tPresidentPortfolio = getPresidentPortfolio ();
		tCertificatesCanBeSold = tPresidentPortfolio.getCertificatesCanBeSold ();
		tReasons = new HashSet<String> ();

		tLiquidCertificateValue += calculateCurrentLiquidStock (tCertificatesCanBeSold, tReasons);

		tLiquidCertificateValue += calculateOtherLiquidStock (tCertificatesCanBeSold, tReasons);

		tAllReasons = "";
		for (String tAReason : tReasons) {
			tAllReasons += "<li>" + tAReason + "</li>";
		}
		tAllReasons = "<html><ul>" + tAllReasons + "</ul></html>";
		setSaleLimitReasons (tAllReasons);

		return tLiquidCertificateValue;
	}

	private int calculateOtherLiquidStock (List<Certificate> aCertificatesCanBeSold, Set<String> aResons) {
		String tReason;
		String tPreviousAbbrev;
		String tCurrentAbbrev;
		String tTrainCoAbbrev;
		int tSellLimit;
		int tCertCount;
		int tLiquidCertificateValue;
		int tBankPoolShareLimit;
		// Determine Sale Limits for Certificates from other Companies, limited by Bank
		// Pool Limits

		tTrainCoAbbrev = trainCompany.getAbbrev ();
		tPreviousAbbrev = "";
		tCertCount = 0;
		tSellLimit = 9;
		tLiquidCertificateValue = 0;
		for (Certificate tCertificate : aCertificatesCanBeSold) {
			tCurrentAbbrev = tCertificate.getCompanyAbbrev ();
			if (!tCurrentAbbrev.equals (tTrainCoAbbrev)) {
				if (!tCurrentAbbrev.equals (tPreviousAbbrev)) {
					tSellLimit = tCertificate.sellLimit ();
					tCertCount = 0;
					tPreviousAbbrev = tCurrentAbbrev;
				}
				if (tCertCount < tSellLimit) {
					tLiquidCertificateValue += tCertificate.getCost ();
					tCertCount++;
				} else {
					if (tSellLimit > 0) {
						tReason = "Bank Pool can only hold " + tSellLimit + " more " + tCurrentAbbrev + " Certificate";
						if (tSellLimit > 1) {
							tReason += "s";
						}
					} else {
						tBankPoolShareLimit = tCertificate.getBankPoolShareLimit (gameManager);
						tReason = "Bank Pool has reached the limit of " + tBankPoolShareLimit + " Certificates of "
								+ tCertificate.getCompanyAbbrev ();
					}
					aResons.add (tReason);
				}
			}
		}

		return tLiquidCertificateValue;
	}

	private int calculateCurrentLiquidStock (List<Certificate> aCertificatesCanBeSold, Set<String> aReasons) {
		String tTrainCoAbbrev;
		String tReason;
		int tCanSellPercent;
		int tCanSellCount;
		int tCurrentCorpCounted;
		int tLiquidCertificateValue;
		int tNextPresidentPercent;
		int tSharePercent = 10; // Should determine minumum Share Percent for Company.
		int tPresidentPercent;

		tTrainCoAbbrev = trainCompany.getAbbrev ();
		tLiquidCertificateValue = 0;
		tPresidentPercent = trainCompany.getPresidentPercent ();
		tNextPresidentPercent = trainCompany.getNextPresidentPercent ();
		tCanSellPercent = (tPresidentPercent - tNextPresidentPercent);
		tCanSellCount = tCanSellPercent / tSharePercent;
		tCurrentCorpCounted = 0;
		tCanSellCount = tCanSellPercent / tSharePercent;
		tCanSellPercent = 0;
		tNextPresidentPercent = 0;

		// Determine Sale Limits for Certificates from trainCompany, limited by Next
		// President Holdings
		for (Certificate tCertificate : aCertificatesCanBeSold) {
			if (tCertificate.getCompanyAbbrev ().equals (tTrainCoAbbrev)) {
				if (tCurrentCorpCounted < tCanSellCount) {
					tLiquidCertificateValue += tCertificate.getCost ();
					tCurrentCorpCounted++;
				} else {
					if (tNextPresidentPercent > 0) {
						tReason = trainCompany.getNextPresidentName () + " has " + tNextPresidentPercent + "% of "
								+ tTrainCoAbbrev + ", limits Sale to " + tCanSellPercent + "%";
						aReasons.add (tReason);
					}
				}
			}
		}

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

	private boolean hasSelectedPrezToExchange () {
		boolean tHasSelectedPrezToExchange = false;
		Portfolio tPresidentPortfolio;

		tPresidentPortfolio = getPresidentPortfolio ();
		if (tPresidentPortfolio.hasSelectedPrezToExchange ()) {
			tHasSelectedPrezToExchange = true;
		}

		return tHasSelectedPrezToExchange;
	}

	private Certificate getCertificateToExchange () {
		Certificate tCertificateToSell;
		Portfolio tPresidentPortfolio;

		tPresidentPortfolio = getPresidentPortfolio ();
		tCertificateToSell = tPresidentPortfolio.getSelectedStockToExchange ();

		return tCertificateToSell;
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
			if (!president.hasSelectedSameStocksToSell ()) {
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
				tToolTip = "Cannot sell all selected Shares - Operating " + trainCompany.getAbbrev ()
						+ " must buy Train, cannot sell out Presidency";
			} else {
				Certificate tCertificateToSell;
				String tAbbrevSelected;
				boolean tOKtoSell;

				tCertificateToSell = getACertificateToSell ();
				tAbbrevSelected = tCertificateToSell.getCompanyAbbrev ();

				if (exchangedCompany == ShareCompany.NO_SHARE_COMPANY) {
					tOKtoSell = true;
				} else if (tAbbrevSelected.equals (exchangedCompany.getAbbrev ())) {
					tOKtoSell = true;
				} else {
					tOKtoSell = false;
				}
				if (tOKtoSell) {
					doSellButton.setEnabled (true);
					tToolTip = "";
					tSharesInfo = buildSharesInfo ();
					tButtonLabel = tButtonLabel + " " + tSharesInfo;
				} else {
					doSellButton.setEnabled (false);
					tToolTip = getMustSellToolTip ();
				}
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
		tShareInfo += " of " + tCertificateToSell.getCompanyAbbrev () + " for " + Bank.formatCash (tSharesTotalValue);

		return tShareInfo;
	}

	private boolean mustSellStock () {
		boolean tMustSellStock;
		String tExchangedShare;

		tMustSellStock = false;
		tExchangedShare = president.hasExchangedShare ();
		if (!president.hasLessThanPresident (tExchangedShare)) {
			tMustSellStock = true;
		}

		return tMustSellStock;
	}

	private void updateExchangeButton () {
		String tToolTip;

		if (mustSellStock ()) {
			exchangeButton.setEnabled (false);
			tToolTip = getMustSellToolTip ();
		} else if (hasSelectedPrezToExchange ()) {
			exchangeButton.setEnabled (true);
			tToolTip = "";
		} else {
			exchangeButton.setEnabled (false);
			tToolTip = "No President Share selected to Exchange";
		}
		exchangeButton.setToolTipText (tToolTip);
	}

	private String getMustSellToolTip () {
		String tToolTip;
		int tCurrentPresidentPercent;
		int tNewPresidentPercent;
		int tMustSellPercent;
		Portfolio tPresidentPortfolio;

		tPresidentPortfolio = getPresidentPortfolio ();
		tCurrentPresidentPercent = tPresidentPortfolio.getPercentageFor (exchangedCompany);
		tNewPresidentPercent = exchangedCompany.getPresidentPercent ();
		tMustSellPercent = (tCurrentPresidentPercent - tNewPresidentPercent) + 1;
		tToolTip = "Must sell at least " + tMustSellPercent + "% of " + exchangedCompany.getAbbrev ();

		return tToolTip;
	}

	private void updateBuyTrainButton () {
		String tToolTip;

		if (mustSellStock ()) {
			doBuyButton.setEnabled (false);
			tToolTip = getMustSellToolTip ();
		} else if (haveEnoughCash ()) {
			doBuyButton.setEnabled (true);
			tToolTip = "Can Force Buy Train";
		} else {
			doBuyButton.setEnabled (false);
			tToolTip = "Not Enough cash to buy Train";
		}

		doBuyButton.setToolTipText (tToolTip);
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
		if (EXCHANGE_ACTION.equals (aEvent.getActionCommand ())) {
			exchangeStock ();
		}
		if (UNDO_ACTION.equals (aEvent.getActionCommand ())) {
			undoAction ();
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
		cancelForceTrainBuy ();
		System.out.println (president.getName () + " is Declaring Bankruptcy for " + trainCompany.getName ());
		trainCompany.declareBankruptcy ();
	}

	private void undoAction () {
		trainCompany.undoAction ();
		actionCount--;
		setVisible (false);
	}

	private void sellStock () {
		++actionCount;
		president.sellAction ();
		if (!mustSellStock ()) {
			setExchangedCompany (ShareCompany.NO_SHARE_COMPANY);
		}
		refreshFrame ();
	}

	private void exchangeStock () {
		ShareCompany tShareCompany;
		Certificate tCertificateToExchange;

		tCertificateToExchange = getCertificateToExchange ();
		tShareCompany = tCertificateToExchange.getShareCompany ();
		++actionCount;
		setExchangedCompany (tShareCompany);
		president.exchangeAction ();
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
