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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.train.Train;
import geUtilities.GUI;
import swingTweaks.KButton;

public class ForceBuyCouponFrame extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;
	public static final ForceBuyCouponFrame NO_FRAME = null;
	public static final String BUY_ACTION = "BuyAction";
	public static final String CANCEL_ACTION = "Cancel";
	public static final String SELL_ACTION = "SellStock";
	public static final String EXCHANGE_ACTION = "Exchange";
	public static final String UNDO_ACTION = "Undo";
	public static final String DECLARE_BANKRUPTCY_ACTION = "DeclareBankruptcy";
	KButton doSellButton;
	KButton exchangeButton;
	KButton doBuyButton;
	KButton undoButton;
	KButton declareBankruptcyButton;
	KButton cancelButton;
	JPanel buttonJPanel;
	JPanel infoJPanel;
	JPanel mainJPanel;
	JPanel stockCertificatesJPanel;
	JLabel corporationTreasuryLabel;
	JLabel presidentTreasuryLabel;
	JLabel totalTreasuryLabel;
	JLabel presidentalCashNeededLabel;
	JLabel totalLiquidAssetLabel;
	JLabel totalSelectedAssetLabel;
	JLabel saleLimitReasons;
	JLabel frameLabel;
	GameManager gameManager;
	ShareCompany shareCompany;
	ShareCompany exchangedCompany;
	Coupon mustBuyCoupon;
	Player president;
	String operatingRoundID;
	String forceAction;
	int buyingTrainCompanyTreasury;
	int presidentTreasury;
	int actionCount;
	int presidentContribution;
	int liquidAssetTotal;
	int selectedAssetTotal;
	int revenueContribution;
	
	public ForceBuyCouponFrame (ShareCompany aBuyingCompany, Coupon aMustBuyCoupon) {
		super ("Force Payment Frame");
		
		forceAction = "";
		if (aMustBuyCoupon instanceof Train) {
			forceAction += "Buy " + aMustBuyCoupon.getName () + " Train Now";
		} else {
			forceAction += "Pay " + aMustBuyCoupon.getName () + " Now";
		}
		setTitle ("Force " + forceAction);
		shareCompany = aBuyingCompany;
		mustBuyCoupon = aMustBuyCoupon;
		actionCount = 0;
		revenueContribution = 0;
		setExchangedCompany (ShareCompany.NO_SHARE_COMPANY);

		buildMainJPanel ();

		add (mainJPanel);

		updateButtons ();
		setSize (400, 350);
		pack ();
	}

	public void showFrame () {
		setVisible (true);
	}

	private void setRevenueContribution (int aRevenueContribution) {
		revenueContribution = aRevenueContribution;
	}

	private int getRevenueContribution () {
		return revenueContribution;
	}

	private void buildMainJPanel () {
		mainJPanel = new JPanel ();
		mainJPanel.setLayout (new BoxLayout (mainJPanel, BoxLayout.Y_AXIS));
		updateMainJPanel ();
	}

	public void updateMainJPanel () {
		mainJPanel.removeAll ();
		buildButtonJPanel ();
		buildInfoJPanel ();
		buildStockJPanel ();
		buildButtonJPanel ();

		mainJPanel.add (infoJPanel);
		mainJPanel.add (buttonJPanel);
		mainJPanel.add (stockCertificatesJPanel);
		updateButtons ();
	}
	
	private void buildInfoJPanel () {
		JLabel tPresidentLabel;
		JPanel tCouponPanel;
		JPanel tCouponAndButtonPanel;
		PortfolioHolderI tPortfolioHolder;
		int tCompanyTreasury;

		tPortfolioHolder = shareCompany.getPresident ();
		if (tPortfolioHolder.isAPlayer ()) {
			president = (Player) tPortfolioHolder;
		}
		presidentTreasury = president.getCash ();
		gameManager = shareCompany.getGameManager ();

		tCompanyTreasury = shareCompany.getCash ();
		infoJPanel = new JPanel ();
		infoJPanel.setLayout (new BoxLayout (infoJPanel, BoxLayout.Y_AXIS));
		infoJPanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Force " + forceAction + " for " + shareCompany.getAbbrev ());
		addLabelAndSpace (frameLabel);
		corporationTreasuryLabel = new JLabel ("Treasury: " + Bank.formatCash (tCompanyTreasury));
		addLabelAndSpace (corporationTreasuryLabel);
		
		addRevenueContributionToPanel ();
		
		tPresidentLabel = new JLabel ("President: " + president.getName ());
		addLabelAndSpace (tPresidentLabel);
		presidentTreasuryLabel = new JLabel ("President Treasury: " + Bank.formatCash (presidentTreasury));
		addLabelAndSpace (presidentTreasuryLabel);
		totalTreasuryLabel = new JLabel ("Total Treasury: " + Bank.formatCash (presidentTreasury +
							tCompanyTreasury));
		addLabelAndSpace (totalTreasuryLabel);
		presidentalCashNeededLabel = new JLabel ("XXX");
		updatePresidentalCashNeeded ();
		addLabelAndSpace (presidentalCashNeededLabel);
		totalLiquidAssetLabel = new JLabel (GUI.EMPTY_STRING);
		addLabelAndSpace (totalLiquidAssetLabel);
		totalSelectedAssetLabel = new JLabel (GUI.EMPTY_STRING);
		addLabelAndSpace (totalSelectedAssetLabel);
		saleLimitReasons = new JLabel (GUI.EMPTY_STRING);
		addLabelAndSpace (saleLimitReasons);
		updateLiquidAssetLabel ();
		updateSelectedAssetLabel ();

		tCouponPanel = mustBuyCoupon.buildCouponInfoPanel (false);
		tCouponAndButtonPanel = new JPanel ();
		tCouponAndButtonPanel.setLayout (new BoxLayout (tCouponAndButtonPanel, BoxLayout.X_AXIS));
		tCouponAndButtonPanel.add (Box.createHorizontalGlue ());
		tCouponAndButtonPanel.add (tCouponPanel);
		tCouponAndButtonPanel.add (Box.createHorizontalStrut (20));
		tCouponAndButtonPanel.add (doBuyButton);
		tCouponAndButtonPanel.add (Box.createHorizontalGlue ());
		updateBuyCouponButton ();
		
		infoJPanel.add (tCouponAndButtonPanel);
		infoJPanel.add (Box.createVerticalStrut (10));
	}

	private void addLabelAndSpace (JLabel aLabelToAdd) {
		infoJPanel.add (aLabelToAdd);
		infoJPanel.add (Box.createVerticalStrut (10));
	}

	private void addRevenueContributionToPanel () {
		JLabel tRevenueContributionLabel;
		LoanInterestCoupon tLoanInterestCoupon;
		int tRevenueContribution;
		
		if (mustBuyCoupon instanceof LoanInterestCoupon) {
			tLoanInterestCoupon = (LoanInterestCoupon) mustBuyCoupon;
			tRevenueContribution = tLoanInterestCoupon.getRevenueContribution ();
			tRevenueContributionLabel = new JLabel ("Revenue Contribution: " + 
					Bank.formatCash (tRevenueContribution));
			addLabelAndSpace (tRevenueContributionLabel);
		} else {
			tRevenueContribution = 0;
		}
		setRevenueContribution (tRevenueContribution);
	}

	private void calculatePresidentalCashNeeded (int aCompanyTreasury, int aCouponCost) {
		presidentContribution = aCouponCost - (getRevenueContribution () + aCompanyTreasury);
	}

	private void updatePresidentalCashNeeded () {
		String tCashLabel;
		int tCompanyTreasury;
		int tCouponCost;
		int tCashToRaise;

		tCouponCost = mustBuyCoupon.getPrice ();
		tCompanyTreasury = shareCompany.getTreasury ();
		calculatePresidentalCashNeeded (tCompanyTreasury, tCouponCost);
		tCashToRaise = tCouponCost - (getRevenueContribution () + tCompanyTreasury + presidentTreasury);
		if (presidentContribution > 0) {
			if (tCashToRaise > 0) {
				tCashLabel = "Cash amount that must be raised: " + Bank.formatCash (tCashToRaise);
			} else {
				tCashLabel = String.format ("Excess Cash of %s will remain in the President's Treasury.",
					Bank.formatCash (-tCashToRaise));
			}
			presidentalCashNeededLabel.setText (tCashLabel);
		} else {
			presidentalCashNeededLabel.setText (
					"Have enough Cash, will have " + 
					Bank.formatCash (presidentTreasury + presidentContribution) + " in President Treasury");
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

	private void updateSelectedAssetLabel () {
		int tSelectedAssetTotal;

		tSelectedAssetTotal = calculateSelectedAssetValue ();
		setSelectedAssetTotal (tSelectedAssetTotal);
		if (tSelectedAssetTotal > 0) {
			totalSelectedAssetLabel.setText ("Total Selected Certificates Value " + Bank.formatCash (selectedAssetTotal));
		} else {
			totalSelectedAssetLabel.setText ("No Selected Certificates");
		}
	}

	private void setExchangedCompany (ShareCompany aShareCompany) {
		exchangedCompany = aShareCompany;
	}

	private void buildButtonJPanel () {
		buttonJPanel = new JPanel ();
		buttonJPanel.setLayout (new BoxLayout (buttonJPanel, BoxLayout.X_AXIS));
		buttonJPanel.add (Box.createHorizontalGlue ());
		doBuyButton = setupButton (forceAction, BUY_ACTION, false);
		buttonJPanel.add (Box.createHorizontalGlue ());
		doSellButton = setupButton (Player.SELL_LABEL, SELL_ACTION);
		buttonJPanel.add (Box.createHorizontalGlue ());
		exchangeButton = setupButton (Player.EXCHANGE_LABEL, EXCHANGE_ACTION);
		buttonJPanel.add (Box.createHorizontalGlue ());
		undoButton = setupButton (UNDO_ACTION, UNDO_ACTION);
		buttonJPanel.add (Box.createHorizontalGlue ());
		declareBankruptcyButton = setupButton ("Declare Bankruptcy", DECLARE_BANKRUPTCY_ACTION);
		buttonJPanel.add (Box.createHorizontalGlue ());
		cancelButton = setupButton (CANCEL_ACTION, CANCEL_ACTION);
		buttonJPanel.add (Box.createHorizontalGlue ());
	}

	private KButton setupButton (String aLabel, String aActionCommand) {
		return setupButton (aLabel, aActionCommand, true);
	}
	
	private KButton setupButton (String aLabel, String aActionCommand, boolean aAddButton) {
		KButton tKButton;

		tKButton = new KButton (aLabel);
		tKButton.setActionCommand (aActionCommand);
		tKButton.addActionListener (this);
		if (aAddButton) {
			buttonJPanel.add (tKButton);
		}

		return tKButton;
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
		updateBuyCouponButton ();
		updateLiquidAssetLabel ();
		updateSelectedAssetLabel ();
		updateDeclareBankruptcyButton ();
		updateCancelButton ();
	}

	private void updateTreasuryLabels () {
		presidentTreasury = president.getCash ();
		presidentTreasuryLabel.setText ("President Treasury: " + Bank.formatCash (presidentTreasury));
		totalTreasuryLabel.setText ("Total Treasury: " + Bank.formatCash (presidentTreasury + shareCompany.getCash ()));
	}

	private void updateCancelButton () {
		if (actionCount == 0) {
			cancelButton.setEnabled (true);
			cancelButton.setToolTipText ("Cancel " + forceAction + ", and Close window");
		} else {
			cancelButton.setEnabled (false);
			cancelButton.setToolTipText ("Stock has been sold, must undo all Sales or complete " + forceAction);
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

	private void setSelectedAssetTotal (int aSelectedAssetTotal) {
		selectedAssetTotal = aSelectedAssetTotal;
	}

	private int calculateSelectedAssetValue () {
		int tSelectedAssetValue;
		Portfolio tPresidentPortfolio;
		List<Certificate> tCertificatesToSell;

		tSelectedAssetValue = 0;

		tPresidentPortfolio = getPresidentPortfolio ();
		tCertificatesToSell = tPresidentPortfolio.getCertificatesToSell ();
		for (Certificate tCertificate : tCertificatesToSell) {
			if (tCertificate != Certificate.NO_CERTIFICATE) {
				tSelectedAssetValue += tCertificate.getValue ();
			}
		}

		return tSelectedAssetValue;
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
		tReasons = new HashSet<> ();

		tLiquidCertificateValue += calculateCurrentLiquidStock (tCertificatesCanBeSold, tReasons);

		tLiquidCertificateValue += calculateOtherLiquidStock (tCertificatesCanBeSold, tReasons);

		tAllReasons = GUI.EMPTY_STRING;
		for (String tAReason : tReasons) {
			tAllReasons += "<li>" + tAReason + "</li>";
		}
		if (tAllReasons != GUI.EMPTY_STRING) {
			tAllReasons = "<html><ul>" + tAllReasons + "</ul></html>";
		}
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
		BankPool tBankPool;
		Corporation tCorporation;
		// Determine Sale Limits for Certificates from other Companies, limited by Bank
		// Pool Limits

		tTrainCoAbbrev = shareCompany.getAbbrev ();
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
					tCorporation = tCertificate.getCorporation ();
					tBankPool = tCorporation.getBankPool ();
					if (tSellLimit > 0) {
						tReason = tBankPool.getName () + " can only hold " + tSellLimit + " more " + 
									tCurrentAbbrev + " Certificate";
						if (tSellLimit > 1) {
							tReason += "s";
						}
					} else {
						tBankPoolShareLimit = tCertificate.getBankPoolShareLimit (gameManager);
						tReason = tBankPool.getName () + " has reached the limit of " + tBankPoolShareLimit + 
								" Certificates of " + tCertificate.getCompanyAbbrev ();
					}
					aResons.add (tReason);
				}
			}
		}

		return tLiquidCertificateValue;
	}

	private int calculateCurrentLiquidStock (List<Certificate> aCertificatesCanBeSold, Set<String> aReasons) {
		String tShareCompanyAbbrev;
		String tReason;
		int tCanSellPercent;
		int tCanSellCount;
		int tCurrentCorpCounted;
		int tLiquidCertificateValue;
		int tNextPresidentPercent;
		int tSharePercent = 10; // Should determine minumum Share Percent for Company.
		int tPresidentPercent;

		tShareCompanyAbbrev = shareCompany.getAbbrev ();
		tLiquidCertificateValue = 0;
		tPresidentPercent = shareCompany.getPresidentOwnedPercent ();
		tNextPresidentPercent = shareCompany.getNextPresidentPercent ();
		tCanSellPercent = (tPresidentPercent - tNextPresidentPercent);
		tCanSellCount = tCanSellPercent / tSharePercent;
		tCurrentCorpCounted = 0;
		tCanSellCount = tCanSellPercent / tSharePercent;
		tCanSellPercent = 0;
		tNextPresidentPercent = 0;

		// Determine Sale Limits for Certificates from shareCompany, limited by Next
		// President Holdings
		for (Certificate tCertificate : aCertificatesCanBeSold) {
			if (tCertificate.getCompanyAbbrev ().equals (tShareCompanyAbbrev)) {
				if (tCurrentCorpCounted < tCanSellCount) {
					tLiquidCertificateValue += tCertificate.getCost ();
					tCurrentCorpCounted++;
				} else {
					if (tNextPresidentPercent > 0) {
						tReason = shareCompany.getNextPresidentName () + " has " + tNextPresidentPercent + "% of "
								+ tShareCompanyAbbrev + ", limits Sale to " + tCanSellPercent + "%";
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
		if (tTotalSaleCost > presidentContribution) {
			tExcessCash = tTotalSaleCost - presidentContribution;
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

		if (tSelectedCompanyAbbrev.equals (shareCompany.getAbbrev ())) {
			tSelectedPercent = president.getSelectedPercent ();
			tPresidentPercent = shareCompany.getPresidentOwnedPercent ();
			tNextPresidentPercent = shareCompany.getNextPresidentPercent ();
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
			tToolTip = Certificate.HAVE_ENOUGH_CASH;
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
				tToolTip = "Cannot sell all selected Shares - Operating " + shareCompany.getAbbrev ()
						+ " must " + forceAction + ", cannot sell out Presidency";
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
		tNewPresidentPercent = exchangedCompany.getPresidentOwnedPercent ();
		tMustSellPercent = (tCurrentPresidentPercent - tNewPresidentPercent) + 1;
		tToolTip = "Must sell at least " + tMustSellPercent + "% of " + exchangedCompany.getAbbrev ();

		return tToolTip;
	}

	private void updateBuyCouponButton () {
		String tToolTip;

		if (mustSellStock ()) {
			doBuyButton.setEnabled (false);
			tToolTip = getMustSellToolTip ();
		} else if (haveEnoughCash ()) {
			doBuyButton.setEnabled (true);
			tToolTip = "Can Force " + forceAction;
		} else {
			doBuyButton.setEnabled (false);
			tToolTip = "Not Enough cash to " + forceAction;
		}

		doBuyButton.setToolTipText (tToolTip);
	}

	private void updateDeclareBankruptcyButton () {
		if (liquidAssetTotal >= presidentContribution) {
			declareBankruptcyButton.setEnabled (false);
			declareBankruptcyButton.setToolTipText ("Have enough to " + forceAction);
		} else {
			declareBankruptcyButton.setEnabled (true);
			declareBankruptcyButton.setToolTipText ("Does NOT have enough to  " + forceAction);
		}
	}

	public boolean haveEnoughCash () {
		boolean tHaveEnoughCash = false;

		tHaveEnoughCash = ((presidentTreasury + shareCompany.getCash () + revenueContribution) >= mustBuyCoupon.getPrice ());

		return tHaveEnoughCash;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		if (BUY_ACTION.equals (aEvent.getActionCommand ())) {
			buyCoupon ();
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
			cancelForceBuyCoupon ();
		}
		updateButtons ();
	}

	private void cancelForceBuyCoupon () {
		setVisible (false);
	}

	private void declareBankruptcy () {
		cancelForceBuyCoupon ();
		System.out.println (president.getName () + " is Declaring Bankruptcy for " + shareCompany.getName ());
		shareCompany.declareBankruptcy ();
	}

	private void undoAction () {
		shareCompany.undoAction ();
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
		updateTreasuryLabels ();
		updateMainJPanel ();
		repaint ();
	}

	private void buyCoupon () {
		int tLoanCountToRepay;
		boolean tHandlingRepayment;
		
		if (mustBuyCoupon instanceof Train) {
			mustBuyCoupon.setSelection ();
			shareCompany.buyTrain (presidentContribution);
		} else if (mustBuyCoupon instanceof LoanInterestCoupon) {
			shareCompany.payLoanInterest (presidentContribution);
			shareCompany.updateFrameInfo ();
			shareCompany.setMustBuyCoupon (false);
		} else if (mustBuyCoupon instanceof LoanRedemptionCoupon) {
			tHandlingRepayment = false;
			tLoanCountToRepay = mustBuyCoupon.getPrice ()/shareCompany.getLoanAmount ();
			shareCompany.redeemLoans (tLoanCountToRepay, presidentContribution, tHandlingRepayment);
			shareCompany.setMustBuyCoupon (false);
		} else {
			System.err.println ("The Must Buy Coupon " + mustBuyCoupon.getName () + " is not a recognized type.");
		}
		setVisible (false);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		updateButtons ();
	}
}
