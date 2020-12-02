package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.train.Train;
import ge18xx.train.TrainPortfolio;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CorporationFrame extends JFrame implements ActionListener, ItemListener {
	static final String SHOW_MAP = "Show Map";
	static final String PLACE_TILE = "Place Tile";
	static final String PLACE_TOKEN = "Place Token";
	static final String PLACE_BASE_TOKEN = "Place Base Token";
	static final String HOME1_NO_TILE = "Home Map Cell 1 does not have Tile";
	static final String HOME2_NO_TILE = "Home Map Cell 2 does not have Tile";
	static final String OPERATE_TRAIN = "Operate Train";
	static final String OPERATE_TRAINS = "Operate Trains";
	static final String NO_TRAINS_TO_OPERATE = "No Trains to Operate";
	static final String PAY_FULL_DIVIDEND = "Pay Full Dividend";
	static final String PAY_HALF_DIVIDEND = "Pay Half Dividend";
	static final String PAY_NO_DIVIDEND = "Pay No Dividend";
	static final String BUY_TRAIN = "Buy Train";
	static final String FORCE_BUY_TRAIN = "Force Buy Train";
	static final String BUY_PRIVATE = "Buy Private";
	static final String GET_LOAN = "Get Loan";
	static final String PAYBACK_LOAN = "Payback Loan";
	static final String DONE = "Done";
	static final String UNDO = "Undo";
	static final String NO_TOOL_TIP = "";
	private static final long serialVersionUID = 1L;
	Container bankBox;
	Container certBox;
	Container privatesBox;
	Container corporationContainer;
	Container otherCorpsContainer;
	JPanel corporationAllInfoJPanel;
	JPanel corporationInfoJPanel;
	JPanel privatesJPanel;
	Container certContainer;
	JPanel buttonRow1, buttonRow2;
	JLabel treasuryLabel;
	JLabel presidentLabel;
	JLabel statusLabel;
	JLabel tokenLabel;
	JLabel phaseNameLabel;
	JLabel trainLimitLabel;
	JLabel allowedTilesLabel;
	JLabel roundInfoLabel;
	JLabel lastRevenueLabel;
	JButton showMapActionButton;
	JButton placeTileActionButton;
	JButton placeTokenActionButton;
	JButton operateTrainActionButton;
	JButton payFullDividendActionButton;
	JButton payHalfDividendActionButton;
	JButton payNoDividendActionButton;
	JButton buyTrainActionButton;
	JButton buyTrainForceActionButton;
	JButton buyPrivateActionButton;
	JButton getLoanActionButton;
	JButton paybackLoanActionButton;
	JButton doneActionButton;
	JButton undoActionButton;
	Corporation corporation;
	
	public CorporationFrame (String aFrameName, Corporation aCorporation) {
		super (((aCorporation != null) ? aCorporation.getName () + " " : "") + aFrameName);
		Dimension tMinSize = new Dimension (20, 10);
		Container tActionButtons, tTopBoxes;
		
		corporationContainer = Box.createVerticalBox ();
		tTopBoxes = Box.createHorizontalBox ();
		certBox = null;
		corporation = aCorporation;
		if (corporation != CorporationList.NO_CORPORATION) {
			corporation = aCorporation;
			
			buildCorporationInfoJPanel (tMinSize);
			
			tTopBoxes.add (Box.createHorizontalStrut (20));
			tTopBoxes.add (corporationAllInfoJPanel);
			tTopBoxes.add (Box.createHorizontalStrut (10));
			Container tPhaseInfoBox = buildPhaseInfoBox ();
			tTopBoxes.add (tPhaseInfoBox);
			tTopBoxes.add (Box.createHorizontalStrut (20));
			corporationContainer.add (Box.createVerticalStrut (20));
			corporationContainer.add (tTopBoxes);
			corporationContainer.add (Box.createVerticalStrut (10));
			tActionButtons = createActionButtonRows ();
			corporationContainer.add (tActionButtons);
			corporationContainer.add (Box.createVerticalStrut (10));

			if (corporation.gameHasPrivates ()) {
				if (corporation instanceof ShareCompany) {
					privatesBox = Box.createVerticalBox ();
					corporationContainer.add (privatesBox);
					corporationContainer.add (Box.createVerticalStrut (10));
				}
			}
	
			// Set up Bank Pool and Bank Box for Train Certificates - But only for Train Companies

			if (corporation.isATrainCompany ()) {
				otherCorpsContainer = Box.createHorizontalBox ();
				corporationContainer.add (otherCorpsContainer);
				corporationContainer.add (Box.createVerticalStrut (10));
				bankBox = Box.createHorizontalBox ();
				corporationContainer.add (bankBox);
				corporationContainer.add (Box.createVerticalStrut (10));
			}
			add (corporationContainer);
			setSize (900, 700);
		}
	}

	private void buildCorporationInfoJPanel (Dimension tMinSize) {
		BoxLayout tLayoutX;
		BoxLayout tLayoutY;
		
		corporationAllInfoJPanel = new JPanel ();
		corporationAllInfoJPanel.setBorder (
				BorderFactory.createTitledBorder (
						BorderFactory.createLineBorder (((TrainCompany) corporation).getBgColor (), 2),
						" Information For " + corporation.getName ()));
		tLayoutY = new BoxLayout (corporationAllInfoJPanel, BoxLayout.Y_AXIS);
		corporationAllInfoJPanel.setLayout (tLayoutY);
		corporationAllInfoJPanel.setAlignmentY (CENTER_ALIGNMENT);
		
		corporationInfoJPanel = new JPanel ();			
		tLayoutX = new BoxLayout (corporationInfoJPanel, BoxLayout.X_AXIS);
		corporationInfoJPanel.setLayout (tLayoutX);
		corporationInfoJPanel.setAlignmentX (CENTER_ALIGNMENT);
		statusLabel = new JLabel ("");
		corporationInfoJPanel.add (Box.createRigidArea (tMinSize));
		corporationInfoJPanel.add (statusLabel);
		corporationInfoJPanel.add (Box.createRigidArea (tMinSize));
		setStatusLabel ();
		if (corporation.isActive ()) {
			presidentLabel = new JLabel ("");
			corporationInfoJPanel.add (presidentLabel);
			setPresidentLabel ();
			corporationInfoJPanel.add (Box.createRigidArea (tMinSize));
			treasuryLabel = new JLabel ("");
			corporationInfoJPanel.add (treasuryLabel);
			setTreasuryLabel ();
			tokenLabel = corporation.buildTokenLabel ();
			if (tokenLabel != Corporation.NO_LABEL) {
				corporationInfoJPanel.add (Box.createRigidArea (tMinSize));
				corporationInfoJPanel.add (tokenLabel);
				setTokenLabel ();
			}
			corporationInfoJPanel.add (Box.createRigidArea (tMinSize));
			lastRevenueLabel = new JLabel ("");
			corporationInfoJPanel.add (lastRevenueLabel);
			setLastRevenueLabel ();
			corporationInfoJPanel.add (Box.createRigidArea (tMinSize));
			
			corporationAllInfoJPanel.add (Box.createRigidArea (tMinSize));
			corporationAllInfoJPanel.add (corporationInfoJPanel);
			corporationAllInfoJPanel.add (Box.createRigidArea (tMinSize));
			fillCertPortfolioContainer ();
			if (corporation instanceof TrainCompany) {
				corporationAllInfoJPanel.add (certBox);
			}
		}
	}
	
	private Container buildPhaseInfoBox () {
		Container tPhaseInfoBox = Box.createVerticalBox ();
		tPhaseInfoBox.add (Box.createVerticalStrut (5));
		phaseNameLabel = new JLabel ("Current Phase Name ");
		tPhaseInfoBox.add (phaseNameLabel);
		tPhaseInfoBox.add (Box.createVerticalStrut (10));
		roundInfoLabel = new JLabel ("Round # of #");
		tPhaseInfoBox.add (roundInfoLabel);
		tPhaseInfoBox.add (Box.createVerticalStrut (10));
		trainLimitLabel = new JLabel ("Train Limit");
		tPhaseInfoBox.add (trainLimitLabel);
		tPhaseInfoBox.add (Box.createVerticalStrut (10));
		allowedTilesLabel = new JLabel ("Tile Colors");
		tPhaseInfoBox.add (allowedTilesLabel);
		tPhaseInfoBox.add (Box.createVerticalStrut (10));

		return tPhaseInfoBox;
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (SHOW_MAP.equals (tActionCommand)) {
			corporation.showMap ();
		}
		if (PLACE_TILE.equals (tActionCommand)) {
			corporation.showMap ();
			corporation.showTileTray ();
			corporation.enterPlaceTileMode ();	
		}
		if (PLACE_TOKEN.equals (tActionCommand)) {
			corporation.showMap ();
			corporation.enterPlaceTokenMode ();
		}
		if (OPERATE_TRAIN.equals (tActionCommand)) {
			corporation.showMap ();
			corporation.operateTrains ();
		}
		if (PAY_NO_DIVIDEND.equals (tActionCommand)) {
			corporation.payNoDividend ();
		}
		if (PAY_HALF_DIVIDEND.equals (tActionCommand)) {
			System.out.println ("Pay Half Dividend Action");
//			corporation.payHalfDividend ();
		}
		if (PAY_FULL_DIVIDEND.equals (tActionCommand)) {
			corporation.payFullDividend ();
		}
		if (BUY_TRAIN.equals (tActionCommand)) {
			corporation.buyTrain ();
		}
		if (FORCE_BUY_TRAIN.equals (tActionCommand)) {
			corporation.forceBuyTrain ();
		}
		if (BUY_PRIVATE.equals (tActionCommand)) {
			corporation.buyPrivate ();
		}
		if (GET_LOAN.equals (tActionCommand)) {
			corporation.getLoan ();
		}
		if (PAYBACK_LOAN.equals (tActionCommand)) {
			corporation.paybackLoan ();
		}
		if (DONE.equals (tActionCommand)) {
			corporation.doneAction ();	
		}
		if (UNDO.equals (tActionCommand)) {
			System.out.println ("Undo Last Action");
			corporation.clearBankSelections ();
			corporation.undoAction ();
			updateInfo ();
		}
		updateActionButtons ();
	}
	
	private JButton setupActionButton (String aButtonLabel, String aButtonAction) {
		JButton tActionButton;
		
		tActionButton = new JButton (aButtonLabel);
		tActionButton.setActionCommand (aButtonAction);
		tActionButton.addActionListener (this);
	
		return tActionButton;
	}
	
	private Container createActionButtonRows () {
		FlowLayout tFlowLayout = new FlowLayout();
		Container tActionButtons = Box.createVerticalBox ();
		
		buttonRow1 = new JPanel (tFlowLayout);
		buttonRow2 = new JPanel (tFlowLayout);
		
		doneActionButton = setupActionButton (DONE, DONE);
		undoActionButton = setupActionButton (UNDO, UNDO);
		placeTileActionButton = setupActionButton (PLACE_TILE, PLACE_TILE);
		placeTokenActionButton = setupActionButton (PLACE_TOKEN, PLACE_TOKEN);
		showMapActionButton = setupActionButton (SHOW_MAP, SHOW_MAP);
		operateTrainActionButton = setupActionButton (OPERATE_TRAIN, OPERATE_TRAIN);
		payNoDividendActionButton = setupActionButton (PAY_NO_DIVIDEND, PAY_NO_DIVIDEND);
		payHalfDividendActionButton = setupActionButton (PAY_HALF_DIVIDEND, PAY_HALF_DIVIDEND);
		payFullDividendActionButton = setupActionButton (PAY_FULL_DIVIDEND, PAY_FULL_DIVIDEND);
		buyTrainForceActionButton = setupActionButton (FORCE_BUY_TRAIN, FORCE_BUY_TRAIN);
		buyTrainActionButton = setupActionButton (BUY_TRAIN, BUY_TRAIN);
		buyPrivateActionButton = setupActionButton (BUY_PRIVATE, BUY_PRIVATE);
		getLoanActionButton = setupActionButton (GET_LOAN, GET_LOAN);
		paybackLoanActionButton = setupActionButton (PAYBACK_LOAN, PAYBACK_LOAN);
		addActionButtons ();
		tActionButtons.add (buttonRow1);
		tActionButtons.add (Box.createVerticalStrut (10));
		tActionButtons.add (buttonRow2);
		
		return tActionButtons;
	}
	
	private void addActionButtons () {
		buttonRow1.removeAll ();
		buttonRow2.removeAll ();
		
		buttonRow1.add (showMapActionButton);
		buttonRow1.add (placeTileActionButton);
		buttonRow1.add (placeTokenActionButton);
		buttonRow1.add (operateTrainActionButton);
		buttonRow1.add (payNoDividendActionButton);
		if (corporation.canPayHalfDividend ()) {
			buttonRow1.add (payHalfDividendActionButton);
		}
		buttonRow1.add (payFullDividendActionButton);
		buttonRow2.add (buyTrainActionButton);
		buttonRow2.add (buyTrainForceActionButton);
		if (corporation.gameHasPrivates ()) {
			buttonRow2.add (buyPrivateActionButton);
		}
		if (corporation.gameHasLoans ()) {
			buttonRow2.add (getLoanActionButton);
			buttonRow2.add (paybackLoanActionButton);
		}
		buttonRow2.add (doneActionButton);
		buttonRow2.add (undoActionButton);
	}
	
	public void fillOtherCorpsContainer (boolean aCanBuyTrain, String aDisableToolTipReason) {
		GameManager tGameManager;
		CorporationList tShareCorporations;
		Container tCorporationsTrainsContainer;

		if (corporation != null) {
			if (corporation.isOperating ()) {
				tGameManager = corporation.getGameManager ();
				if (tGameManager != null) {
					otherCorpsContainer.removeAll ();
					tShareCorporations = tGameManager.getShareCompanies ();
					tCorporationsTrainsContainer = tShareCorporations.buildOtherTrainsContainer (this, 
							corporation, tGameManager, TrainPortfolio.FULL_TRAIN_PORTFOLIO, 
							aCanBuyTrain, aDisableToolTipReason);
					otherCorpsContainer.add (Box.createHorizontalGlue ());
					otherCorpsContainer.add (tCorporationsTrainsContainer);
					otherCorpsContainer.add (Box.createHorizontalGlue ());
				}				
			}
		}
	}
	
	public void fillBankBox (boolean aCanBuyTrain, String aDisableToolTipReason) {
		Bank tBank;
		BankPool tBankPool;
		Container tBPPortfolioJPanel;
		Container tBankPortfolioJPanel;
		GameManager tGameManager;
		
		if (corporation != null) {
			tGameManager = corporation.getGameManager ();
			tBank = corporation.getBank ();
			tBankPool = corporation.getBankPool ();
			
			if (tGameManager != null) {
				bankBox.removeAll ();
				if (tBankPool != null) {
					tBPPortfolioJPanel = tBankPool.buildTrainPortfolioInfoJPanel (this, corporation, 
							tGameManager, TrainPortfolio.FULL_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
					bankBox.add (Box.createHorizontalGlue ());
					bankBox.add (tBPPortfolioJPanel);
					bankBox.add (Box.createHorizontalGlue ());
				} else {
					System.out.println ("Bank Pool is Null");
				}
				if (tBank != null) {
					tBankPortfolioJPanel = tBank.buildTrainPortfolioInfoJPanel (this, corporation, 
							tGameManager, TrainPortfolio.COMPACT_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
					bankBox.add (tBankPortfolioJPanel);
					bankBox.add (Box.createHorizontalGlue ());
					bankBox.validate ();
				} else {
					System.out.println ("Bank is Null");
				}
			}
		}
	}

	public void fillPrivatesBox () {
		GameManager tGameManager;
		int tCountOpenPrivates, tCountPlayerOwnedPrivates;
		ShareCompany tShareCompany;
		
		if (corporation instanceof ShareCompany) {
			tGameManager = corporation.getGameManager ();
			tCountOpenPrivates = tGameManager.getCountOfOpenPrivates ();
			if (tCountOpenPrivates > 0) {
				tCountPlayerOwnedPrivates = tGameManager.getCountOfPlayerOwnedPrivates ();
				privatesBox.removeAll ();
				if (tCountPlayerOwnedPrivates > 0) {
					tShareCompany = (ShareCompany) corporation;
					privatesJPanel = tShareCompany.buildPrivatesForPurchaseJPanel (this);
					privatesBox.add (Box.createVerticalGlue ());
					privatesBox.add (privatesJPanel);
					privatesBox.add (Box.createVerticalGlue ());
					privatesBox.validate ();
				} else {
					JLabel NoPrivatesLeft = new JLabel ("No Privates Left for purchase");
					privatesBox.add (NoPrivatesLeft);
				}
			}
		}
	}
	
	public void fillCertPortfolioContainer () {
		TrainCompany tTrainCompany;
		
		certContainer = null;
		if (corporation instanceof TrainCompany) {
			if (certBox == null) {
				certBox = Box.createVerticalBox (); 
			}
			tTrainCompany = (TrainCompany) corporation;
			certBox.removeAll ();
			certContainer = tTrainCompany.buildCertPortfolioInfoContainer (this);
			certBox.add (certContainer);
			certBox.validate ();
		}
	}
	
	public void setLastRevenueLabel () {
		lastRevenueLabel.setText ("Last Revenue: " + corporation.getFormattedLastRevenue ());
	}
	
	public void setPhaseInfo () {
		PhaseInfo tPhaseInfo;
		int tMaxOR, tCurrentOR;
		
		tPhaseInfo = corporation.getCurrentPhaseInfo ();
		phaseNameLabel.setText ("Current Phase Name " + tPhaseInfo.getFullName ());
		trainLimitLabel.setText ("Train Limit: " + tPhaseInfo.getTrainLimit ());
		allowedTilesLabel.setText ("Tile Colors: " + tPhaseInfo.getTiles ());
		tMaxOR = tPhaseInfo.getOperatingRoundsCount ();
		tCurrentOR = corporation.getCurrentOR ();
		roundInfoLabel.setText ("Round " + tCurrentOR + " of " + tMaxOR);
	}

	public void setPresidentLabel () {
		presidentLabel.setText ("President: " + corporation.getPresidentName ());
	}
	
	public void setStatusLabel () {
		statusLabel.setText ("Status: " + corporation.getStatusName ());
	}
	
	public void setTokenLabel () {
		String tTokenLabel;
		
		tTokenLabel = corporation.getTokenLabel ();
		if (tTokenLabel != null) {
			tokenLabel.setText (tTokenLabel);
		}
	}
	
	public void setTreasuryLabel () {
		int tTreasuryValue;
		
		tTreasuryValue = 0;
		if (corporation != CorporationList.NO_CORPORATION) {
			if (corporation instanceof TrainCompany) {
				tTreasuryValue = ((TrainCompany) corporation).getTreasury ();
			}
		}
		treasuryLabel.setText ("Treasury: " + Bank.formatCash (tTreasuryValue));
	}
	
	public void updateActionButtons () {
		int tTrainCount;
		
		if (corporation.mapVisible ()) {
			showMapActionButton.setEnabled (false);
			showMapActionButton.setToolTipText ("The Map is already visible.");
		} else {
			showMapActionButton.setEnabled (true);
			showMapActionButton.setToolTipText (NO_TOOL_TIP);
		}
		updatePlaceTileActionButton ();
		updatePlaceTokenActionButton ();
		tTrainCount = corporation.getTrainCount ();
		updateOperateTrainActionButton (tTrainCount);
		updatePayFullDividendActionButton ();
		updatePayHalfDividendActionButton (tTrainCount);
		updatePayNoDividendActionButton (tTrainCount);
		updateBuyTrainActionButton ();
		updateForceBuyTrainActionButton ();
		updateBuyPrivateActionButton ();
		updateDoneActionButton ();
	}

	private void updatePayHalfDividendActionButton (int aTrainCount) {
		String tDisableToolTipReason;
		
		if ((aTrainCount > 0) && (corporation.getThisRevenue () == TrainCompany.NO_REVENUE)) {
			payHalfDividendActionButton.setEnabled (false);
			tDisableToolTipReason = "No Dividends calculated yet";
			payHalfDividendActionButton.setToolTipText (tDisableToolTipReason);
		} else if (corporation.canPayDividend ()) {
			payHalfDividendActionButton.setEnabled (true);
			payHalfDividendActionButton.setText ("Pay " + Bank.formatCash (corporation.getHalfShareDividend ()) + " per Share");
			payHalfDividendActionButton.setToolTipText (NO_TOOL_TIP);
		} else if (corporation.dividendsHandled ()) {
			payHalfDividendActionButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoDividendOptions ();
			payHalfDividendActionButton.setToolTipText (tDisableToolTipReason);
		}
	}
	
	private void updatePayNoDividendActionButton (int aTrainCount) {
		String tToolTip;
		
		payNoDividendActionButton.setText ("Pay No Dividend");
		if (! corporation.haveLaidAllBaseTokens ()) {
			payNoDividendActionButton.setEnabled (false);
			payNoDividendActionButton.setToolTipText ("Base Token must be laid first.");							
		} else if (corporation.dividendsHandled ()) {
			payNoDividendActionButton.setEnabled (false);
			tToolTip = corporation.reasonForNoDividendPayment ();
			payNoDividendActionButton.setToolTipText (tToolTip);
		} else if (aTrainCount == 0) {
			payNoDividendActionButton.setEnabled (true);
			payNoDividendActionButton.setToolTipText (NO_TOOL_TIP);
		} else if (corporation.canPayDividend ()) {
			payNoDividendActionButton.setEnabled (true);
			payNoDividendActionButton.setText ("Hold " + Bank.formatCash (corporation.getThisRevenue ()) + " in Treasury");
			payNoDividendActionButton.setToolTipText (NO_TOOL_TIP);
		} else if ((aTrainCount > 0) && (corporation.didOperateTrain ())) {
			if (corporation.getThisRevenue () == 0) {
				payNoDividendActionButton.setEnabled (true);
				payNoDividendActionButton.setToolTipText (NO_TOOL_TIP);
			} else {
				payNoDividendActionButton.setEnabled (false);
				if (aTrainCount == 1) {
					tToolTip = "Must Operate the Train first.";
				} else {
					tToolTip = "Must Operate the Trains (QTY: " + aTrainCount + ") first.";
				}
				payNoDividendActionButton.setToolTipText (tToolTip);
			}
		} else {
			payNoDividendActionButton.setEnabled (false);
			tToolTip = corporation.reasonForNoDividendOptions ();
			payNoDividendActionButton.setToolTipText (tToolTip);
		}
	}
	
	private void updatePayFullDividendActionButton () {
		String tDisableToolTipReason;
		
		if (corporation.canPayDividend ()) {
			payFullDividendActionButton.setEnabled (true);
			payFullDividendActionButton.setText ("Pay " + Bank.formatCash (corporation.getFullShareDividend ()) + " per Share");
			payFullDividendActionButton.setToolTipText (NO_TOOL_TIP);
		} else {
			payFullDividendActionButton.setText (PAY_FULL_DIVIDEND);
			payFullDividendActionButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoDividendPayment ();
			payFullDividendActionButton.setToolTipText (tDisableToolTipReason);
		}
	}
	
	private void updatePlaceTileActionButton () {
		String tDisableToolTipReason;
		if (corporation.canLayTile ()) {
			placeTileActionButton.setEnabled (true);
			placeTileActionButton.setToolTipText (NO_TOOL_TIP);
		} else {
			placeTileActionButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoTileLay ();
			placeTileActionButton.setToolTipText (tDisableToolTipReason);
		}
	}

	private void updateDoneActionButton () {
		String tDisableToolTipReason;
		
		if (! corporation.dividendsHandled ()) {
			doneActionButton.setEnabled (false);
			tDisableToolTipReason = "Dividends have not been handled yet";
			doneActionButton.setToolTipText (tDisableToolTipReason);
		} else if (corporation.getSelectedTrainCount () > 0) {
			doneActionButton.setEnabled (false);
			tDisableToolTipReason = "Train has been Selected for Purchase";
			doneActionButton.setToolTipText (tDisableToolTipReason);
		} else if (corporation.getCountOfSelectedPrivates () > 0) {
			doneActionButton.setEnabled (false);
			tDisableToolTipReason = "Private has been Selected for Purchase";
			doneActionButton.setToolTipText (tDisableToolTipReason);
		} else if (corporation.mustBuyTrainNow ()) {
			doneActionButton.setEnabled (false);
			tDisableToolTipReason = "Corporation must buy a Train";
			doneActionButton.setToolTipText (tDisableToolTipReason);
		} else {
			doneActionButton.setEnabled (true);
			doneActionButton.setToolTipText (NO_TOOL_TIP);
		}
	}
	
	private void updateForceBuyTrainActionButton () {
		Train tCheapestTrain;
		
		if (corporation.isOperating ()) {
			if (corporation.dividendsHandled ()) {
				if (corporation.mustBuyTrainNow ()) {
					tCheapestTrain = corporation.getCheapestBankTrain ();
					if (corporation.getCash () < tCheapestTrain.getPrice ()) {
						buyTrainForceActionButton.setVisible (true);
						buyTrainForceActionButton.setEnabled (true);
						buyTrainForceActionButton.setToolTipText (NO_TOOL_TIP);
					} else {
						hideForceBuyTrainActionButton ();
					}
				} else {
					hideForceBuyTrainActionButton ();
				}
			} else {
				hideForceBuyTrainActionButton ();
			}
		} else {
			hideForceBuyTrainActionButton ();
		}
	}
	
	private void hideForceBuyTrainActionButton () {
		buyTrainForceActionButton.setVisible (false);
		buyTrainForceActionButton.setEnabled (false);
		buyTrainForceActionButton.setToolTipText (NO_TOOL_TIP);
	}
	
	private boolean canBuySelectedTrain (int aSelectedCount) {
		boolean tCanBuySelectedTrain;

		tCanBuySelectedTrain = (corporation.canBuyTrain () && (aSelectedCount == 1));
		
		return tCanBuySelectedTrain;
	}
	
	private void updateBuyTrainActionButton () {
		int tSelectedCount;
		int tThisSelectedTrainCount;
		TrainCompany tTrainCompany = (TrainCompany) corporation;
		Train tSelectedTrainToBuy;
		Train tSelectedTrainToUpgrade;
		boolean tRemovedADiscount;
		
		tThisSelectedTrainCount = tTrainCompany.getLocalSelectedTrainCount ();
		tSelectedCount = corporation.getSelectedTrainCount ();

		if (tThisSelectedTrainCount == 0) {
			tRemovedADiscount = tTrainCompany.removeAllDiscounts ();
			if ((tSelectedCount == 0) && tRemovedADiscount) {
	// TODO Something here when an Upgrade is Selected, a Diesel is selected, then the Diesel is unselected, and Upgrade is unselected
	// then tRemovedADiscount is true -- Fine, but this update then puts the state of the Other Corp Action Buttons to allow a Select,
	// But then disallows an unselect --- This should rebuild the entire set of trains from scratch... but it gets into a weird state
				updateOtherCorpsContainer ();
			}
		}
		
		// if tThisSelectedCount is one, it has been selected to Upgrade to another train
		if (tThisSelectedTrainCount == 1) {
			// If the actual tSelectedCount is zero -- the Apply the Discount;
			if (tSelectedCount == 0) {
				tTrainCompany.applyDiscount ();
				fillOtherCorpsContainer (false, "Select Train for Upgrading from Bank");
			}
			if (canBuySelectedTrain (tSelectedCount)) {
				if (tTrainCompany.isSelectedTrainHolderTheBank ()) {
					tSelectedTrainToBuy = tTrainCompany.getSelectedBankTrain ();
					tSelectedTrainToUpgrade = tTrainCompany.getSelectedTrain ();
					if (tSelectedTrainToBuy.canBeUpgradedFrom (tSelectedTrainToUpgrade.getName ())) {
						enableBuyTrainActionButton ();
					} else {
						disableBuyTrainActionButton ("Must select Train from the Bank that Can be Upgraded to " + tSelectedTrainToBuy.getName ());
					}
				} else {
					disableBuyTrainActionButton ("Must select Train from the Bank to Upgrade");
				}
			} else {
				disableBuyTrainActionButton ();
			}
		} else if (canBuySelectedTrain (tSelectedCount)) {
			enableBuyTrainActionButton ();
		} else {
			disableBuyTrainActionButton ();
		}
	}

	private void enableBuyTrainActionButton () {
		buyTrainActionButton.setEnabled (true);
		buyTrainActionButton.setToolTipText (NO_TOOL_TIP);		
	}
	
	private void disableBuyTrainActionButton (String aDisableToolTipReason) {
		buyTrainActionButton.setEnabled (false);
		buyTrainActionButton.setToolTipText (aDisableToolTipReason);	
	}
	
	private void disableBuyTrainActionButton () {
		String tDisableToolTipReason;
		
		tDisableToolTipReason = corporation.reasonForNoBuyTrain ();
		disableBuyTrainActionButton (tDisableToolTipReason);
	}
	
	private void updatePlaceTokenActionButton () {
		String tDisableToolTipReason;
		
		if (corporation.canLayToken ()) {
			placeTokenActionButton.setEnabled (true);
			if (corporation.haveLaidAllBaseTokens ()) {
				placeTokenActionButton.setText (PLACE_TOKEN);
				if (corporation.haveMoneyForToken ()) {
					placeTokenActionButton.setEnabled (true);
					placeTokenActionButton.setToolTipText (NO_TOOL_TIP);
				} else {
					placeTokenActionButton.setEnabled (false);
					tDisableToolTipReason = corporation.reasonForNoTokenLay ();
					placeTokenActionButton.setToolTipText (tDisableToolTipReason);				
				}
			} else {
				placeTokenActionButton.setText (PLACE_BASE_TOKEN);
				if (! corporation.homeMapCell1HasTile ()) {
					placeTokenActionButton.setEnabled (false);
					placeTokenActionButton.setToolTipText (HOME1_NO_TILE);
				} else if (corporation.homeMapCell2HasTile ()) {
					placeTokenActionButton.setEnabled (false);
					placeTokenActionButton.setToolTipText (HOME2_NO_TILE);
				} else {
					placeTokenActionButton.setEnabled (true);
					placeTokenActionButton.setToolTipText (NO_TOOL_TIP);
				}
			}
		} else {
			placeTokenActionButton.setText (PLACE_TOKEN);
			placeTokenActionButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoTokenLay ();
			placeTokenActionButton.setToolTipText (tDisableToolTipReason);
		}
	}

	private void updateOperateTrainActionButton (int aTrainCount) {
		String tDisableToolTipReason;
		
		if (aTrainCount > 1) {
			operateTrainActionButton.setText (OPERATE_TRAINS);
		} else if (aTrainCount == 1) {
			operateTrainActionButton.setText (OPERATE_TRAIN);
		} else {
			operateTrainActionButton.setText (NO_TRAINS_TO_OPERATE);
		}
		if (corporation.canOperateTrains ()) {
			operateTrainActionButton.setEnabled (true);
			operateTrainActionButton.setToolTipText (NO_TOOL_TIP);
		} else {
			operateTrainActionButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoTrainOperation ();
			operateTrainActionButton.setToolTipText (tDisableToolTipReason);
		}
	}
	
	private void updateBuyPrivateActionButton () {
		String tDisableToolTipReason;
		
		// TODO: Build routines to 'Enable' and 'Disable' Private Box Checkboxes, 
		// with a reason for Enabling/Disabling for the ToolTip
		if (corporation.canBuyPrivate ()) {
			if (corporation.getCountOfSelectedPrivates () == 1) {
				buyPrivateActionButton.setEnabled (true);
			} else {
				buyPrivateActionButton.setEnabled (false);
				tDisableToolTipReason = "Must Select a Single Private to buy";
				buyPrivateActionButton.setToolTipText (tDisableToolTipReason);
			}
		} else {
			buyPrivateActionButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoBuyPrivate ();
			buyPrivateActionButton.setToolTipText (tDisableToolTipReason);
		}
	}
	
	public void updateInfo () {
		setStatusLabel ();
		setPhaseInfo ();
		setPresidentLabel ();
		setTreasuryLabel ();
		setTokenLabel ();
		setLastRevenueLabel ();
		updateActionButtons ();
		updateBuyableItems ();
	}

	public void updateBuyableItems () {
		fillCertPortfolioContainer ();
		fillPrivatesBox ();
		updateBankBox ();
		updateOtherCorpsContainer ();
	}

	public void updateOtherCorpsContainer () {
		boolean tCanBuyTrain;
		boolean tHasCash;
		boolean tIsAtTrainLimit;
		boolean tCanBuyTrainInPhase;
		String tDisableToolTipReason;
		
		tCanBuyTrainInPhase = corporation.canBuyTrainInPhase ();
		
		if (tCanBuyTrainInPhase) {
			tCanBuyTrain = corporation.dividendsHandled ();
			tDisableToolTipReason = "";
			if (tCanBuyTrain) {
				tHasCash = (corporation.getCash () > 0);
				if (!tHasCash) {
					tCanBuyTrain = false;
					tDisableToolTipReason = corporation.getAbbrev () + " has no Cash available";
				} else {
					tIsAtTrainLimit = ((TrainCompany) corporation).atTrainLimit ();
					if (tIsAtTrainLimit) {
						tCanBuyTrain = false;
						tDisableToolTipReason = corporation.getAbbrev () + " at Train Limit";					
					}
				}
			} else {
				tDisableToolTipReason = "Dividends have not been handled yet";
			}
		} else {
			tCanBuyTrain = false;
			tDisableToolTipReason = "Cannot buy Other Corporation Trains in current Phase";
		}
//		System.out.println ("updateOtherCorps Method " + corporation.getAbbrev () + " - Can Buy " + tCanBuyTrain + " disable reason " + tDisableToolTipReason);
		fillOtherCorpsContainer (tCanBuyTrain, tDisableToolTipReason);
	}

	public void updateBankBox () {
		boolean tCanBuyTrain;
		boolean tHasCash;
		boolean tIsAtTrainLimit;
		String tDisableToolTipReason;

		if (corporation.isATrainCompany ()) {
			tCanBuyTrain = corporation.dividendsHandled ();
			tDisableToolTipReason = "";
			if (tCanBuyTrain) {
				tHasCash = (corporation.getCash () > 0);
				if (!tHasCash) {
					tCanBuyTrain = false;
					tDisableToolTipReason = corporation.getAbbrev () + " has no Cash available";
				} else {
					tIsAtTrainLimit = ((TrainCompany) corporation).atTrainLimit ();
					if (tIsAtTrainLimit) {
						tCanBuyTrain = false;
						tDisableToolTipReason = corporation.getAbbrev () + " at Train Limit";					
					}
				}
			} else {
				tDisableToolTipReason = "Dividends have not been handled yet";
			}
			fillBankBox (tCanBuyTrain, tDisableToolTipReason);
		}
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		if (corporation.isOperating ()) {
			updateBuyTrainActionButton ();
			updateBuyPrivateActionButton ();
			updateDoneActionButton ();			
		}
	}
}
