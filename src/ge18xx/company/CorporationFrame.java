package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.toplevel.XMLFrame;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CorporationFrame extends XMLFrame implements ActionListener, ItemListener {
	static final String SHOW_MAP = "Show Map";
	static final String PLACE_TILE = "Place Tile";
	public static final String PLACE_TILE_PRIVATE = "Place Tile on Private Home";
	public static final String PLACE_TOKEN_PRIVATE = "Place Token on Private Home";
	static final String PLACE_TOKEN = "Place Token";
	static final String PLACE_BASE_TOKEN = "Place Base Token";
	static final String IN_PLACE_TILE_MODE = "Already in Place Tile Mode";
	static final String IN_TOKEN_MODE = "Already in Place Token Mode";
	static final String SKIP_BASE_TOKEN = "Skip Base Token";
	static final String HOME_NO_TILE = "Home Map Cell %s does not have Tile";
	static final String OPERATE_TRAIN = "Operate Train";
	static final String OPERATE_TRAINS = "Operate Trains";
	static final String NO_TRAINS_TO_OPERATE = "No Trains to Operate";
	static final String PAY_FULL_DIVIDEND = "Pay Full Dividend";
	static final String PAY_HALF_DIVIDEND = "Pay Half Dividend";
	static final String PAY_NO_DIVIDEND = "Pay No Dividend";
	static final String BUY_TRAIN = "Buy Train";
	static final String OFFER_TO_BUY_TRAIN = "Offer to Buy Train";
	static final String FORCE_BUY_TRAIN = "Force Buy Train";
	static final String BUY_PRIVATE = "Buy Private";
	static final String COMPLETE_TT_PLACEMENT = "Need to complete Tile/Token Placement";
	static final String OFFER_TO_BUY_PRIVATE = "Offer to Buy Private";
	static final String GET_LOAN = "Get Loan";
	static final String PAYBACK_LOAN = "Payback Loan";
	static final String DONE = "Done";
	static final String UNDO = "Undo";
	static final String NO_TOOL_TIP = "";
	private static final long serialVersionUID = 1L;
	final JPanel NO_PANEL = null;
	JPanel bankJPanel;
	JPanel certJPanel;
	JPanel privatesBox;
	JPanel corporationJPanel;
	JPanel otherCorpsJPanel;
	JPanel corporationAllInfoJPanel;
	JPanel corporationInfoJPanel;
	JPanel privatesJPanel;
	JPanel certInfoJPanel;
	JPanel actionButtons;
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
	boolean isNetworkGame;
	
	public CorporationFrame (String aFrameName, Corporation aCorporation, boolean aIsNetworkGame) {
		super (((aCorporation != Corporation.NO_CORPORATION) ? aCorporation.getName () + " " : "") + aFrameName);
		Dimension tMinSize = new Dimension (20, 10);
		JPanel tTopBoxes, tPhaseInfoBox;
		
		corporationJPanel = new JPanel ();
		corporationJPanel.setLayout (new BoxLayout (corporationJPanel, BoxLayout.Y_AXIS));
		tTopBoxes = new JPanel ();
		tTopBoxes.setLayout (new BoxLayout (tTopBoxes, BoxLayout.X_AXIS));
		certJPanel = NO_PANEL;
		corporation = aCorporation;
		if (corporation != Corporation.NO_CORPORATION) {
			corporation = aCorporation;
			
			buildCorporationInfoJPanel (tMinSize);
			
			tTopBoxes.add (Box.createHorizontalStrut (20));
			tTopBoxes.add (corporationAllInfoJPanel);
			tTopBoxes.add (Box.createHorizontalStrut (10));
			tPhaseInfoBox = buildPhaseInfoBox ();
			tTopBoxes.add (tPhaseInfoBox);
			tTopBoxes.add (Box.createHorizontalStrut (20));
			corporationJPanel.add (Box.createVerticalStrut (20));
			corporationJPanel.add (tTopBoxes);
			corporationJPanel.add (Box.createVerticalStrut (10));
			createActionButtonRows ();
			corporationJPanel.add (actionButtons);
			corporationJPanel.add (Box.createVerticalStrut (10));

			if (corporation.gameHasPrivates ()) {
				if (corporation instanceof ShareCompany) {
					privatesBox = new JPanel ();
					privatesBox.setLayout (new BoxLayout (privatesBox, BoxLayout.Y_AXIS));
					corporationJPanel.add (privatesBox);
					corporationJPanel.add (Box.createVerticalStrut (10));
				}
			}
	
			// Set up Bank Pool and Bank Box for Train Certificates - But only for Train Companies

			if (corporation.isATrainCompany ()) {
				otherCorpsJPanel = new JPanel ();
				otherCorpsJPanel.setLayout (new BoxLayout (otherCorpsJPanel, BoxLayout.Y_AXIS));
				corporationJPanel.add (otherCorpsJPanel);
				corporationJPanel.add (Box.createVerticalStrut (10));
				bankJPanel = new JPanel ();
				bankJPanel.setLayout (new BoxLayout (bankJPanel, BoxLayout.X_AXIS));
				corporationJPanel.add (bankJPanel);
				corporationJPanel.add (Box.createVerticalStrut (10));
			}
			add (corporationJPanel);
			setSize (900, 800);
			setIsNetworkGame (aIsNetworkGame);
			updateUndoButton ();
		}
	}

	private void buildCorporationInfoJPanel (Dimension tMinSize) {
		corporationAllInfoJPanel = new JPanel ();
		corporationAllInfoJPanel.setLayout (new BoxLayout (corporationAllInfoJPanel, BoxLayout.Y_AXIS));
		corporationAllInfoJPanel.setAlignmentY (CENTER_ALIGNMENT);
		corporationAllInfoJPanel.setBorder (
				BorderFactory.createTitledBorder (
						BorderFactory.createLineBorder (((TrainCompany) corporation).getBgColor (), 2),
						" Information For " + corporation.getName ()));
		
		corporationInfoJPanel = new JPanel ();			
		corporationInfoJPanel.setLayout (new BoxLayout (corporationInfoJPanel, BoxLayout.X_AXIS));
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
			fillCertPortfolioJPanel ();
			if (corporation instanceof TrainCompany) {
				corporationAllInfoJPanel.add (certJPanel);
			}
		}
	}
	
	private JPanel buildPhaseInfoBox () {
		JPanel tPhaseInfoBox;
		
		tPhaseInfoBox = new JPanel ();
		tPhaseInfoBox.setLayout (new BoxLayout (tPhaseInfoBox, BoxLayout.Y_AXIS));
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
	
	public void setIsNetworkGame (boolean aIsNetworkGame) {
		isNetworkGame = aIsNetworkGame;
	}
	
	public void updateUndoButton () {
		if (isNetworkGame) {
			undoActionButton.setEnabled (false);
			undoActionButton.setToolTipText ("Network Game - Undo is not allowed");
		}
	}

	public void handlePlaceTile () {
		corporation.showTileTray ();
		corporation.enterPlaceTileMode ();
		corporation.showMap ();
		updateTTODButtons ();
	}
	
	public void handlePlaceToken () {
		corporation.showTileTray ();
		if (corporation.haveLaidAllBaseTokens () || corporation.choiceForBaseToken ()) {
			corporation.enterPlaceTokenMode ();
			updateTTODButtons ();
		} else {
			corporation.placeBaseTokens ();
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (SHOW_MAP.equals (tActionCommand)) {
			corporation.showMap ();
		}
		if (PLACE_TILE.equals (tActionCommand)) {
			handlePlaceTile ();
		}
		if (PLACE_TOKEN.equals (tActionCommand)) {
			corporation.showMap ();
			handlePlaceToken ();
		}
		if (SKIP_BASE_TOKEN.equals (tActionCommand)) {
			corporation.showMap ();
			corporation.skipBaseToken ();
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
			corporation.buyPrivate (true);
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
		}
		updateInfo ();
	}
	
	private JButton setupActionButton (String aButtonLabel, String aButtonAction) {
		JButton tActionButton;
		
		tActionButton = new JButton (aButtonLabel);
		tActionButton.setActionCommand (aButtonAction);
		tActionButton.addActionListener (this);
	
		return tActionButton;
	}
	
	private void createActionButtonRows () {
		actionButtons = new JPanel (new WrapLayout ());
		
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
		if (corporation.gameHasLoans ()) {
			getLoanActionButton = setupActionButton (GET_LOAN, GET_LOAN);
			paybackLoanActionButton = setupActionButton (PAYBACK_LOAN, PAYBACK_LOAN);
		}
		addActionButtons ();
	}
	
	private void addActionButtons () {
		actionButtons.removeAll ();
		
		actionButtons.add (showMapActionButton);
		actionButtons.add (placeTileActionButton);
		actionButtons.add (placeTokenActionButton);
		actionButtons.add (operateTrainActionButton);
		actionButtons.add (payNoDividendActionButton);
		if (corporation.canPayHalfDividend ()) {
			actionButtons.add (payHalfDividendActionButton);
		}
		actionButtons.add (payFullDividendActionButton);
		actionButtons.add (buyTrainActionButton);
		actionButtons.add (buyTrainForceActionButton);
		if (corporation.gameHasPrivates ()) {
			actionButtons.add (buyPrivateActionButton);
		}
		if (corporation.gameHasLoans ()) {
			actionButtons.add (getLoanActionButton);
			actionButtons.add (paybackLoanActionButton);
		}
		actionButtons.add (doneActionButton);
		actionButtons.add (undoActionButton);
	}
	
	public void fillOtherCorpsJPanel (boolean aCanBuyTrain, String aDisableToolTipReason) {
		GameManager tGameManager;
		CorporationList tShareCorporations;
		JPanel tCorporationsTrainsJPanel;

		if (isCorporationSet ()) {
			if (corporation.isOperating ()) {
				tGameManager = corporation.getGameManager ();
				if (tGameManager != GameManager.NO_GAME_MANAGER) {
					otherCorpsJPanel.removeAll ();
					tShareCorporations = tGameManager.getShareCompanies ();
					tCorporationsTrainsJPanel = tShareCorporations.buildFullCorpsJPanel (this, 
							corporation, tGameManager, TrainPortfolio.FULL_TRAIN_PORTFOLIO, 
							aCanBuyTrain, aDisableToolTipReason);
					otherCorpsJPanel.add (Box.createHorizontalGlue ());
					otherCorpsJPanel.add (tCorporationsTrainsJPanel);
					otherCorpsJPanel.add (Box.createHorizontalGlue ());
				}				
			}
		}
	}
	
	private boolean isCorporationSet () {
		return (corporation != Corporation.NO_CORPORATION);
	}
	
	public void fillBankBox (boolean aCanBuyTrain, String aDisableToolTipReason) {
		Bank tBank;
		BankPool tBankPool;
		JPanel tBPPortfolioJPanel;
		JPanel tBankPortfolioJPanel;
		GameManager tGameManager;
		
		if (isCorporationSet ()) {
			tGameManager = corporation.getGameManager ();
			tBank = corporation.getBank ();
			tBankPool = corporation.getBankPool ();
			
			if (tGameManager != GameManager.NO_GAME_MANAGER) {
				bankJPanel.removeAll ();
				if (tBankPool != BankPool.NO_BANK_POOL) {
					tBPPortfolioJPanel = tBankPool.buildTrainPortfolioInfoJPanel (this, corporation, 
							tGameManager, TrainPortfolio.FULL_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
					bankJPanel.add (Box.createHorizontalGlue ());
					bankJPanel.add (tBPPortfolioJPanel);
					bankJPanel.add (Box.createHorizontalGlue ());
				} else {
					System.err.println ("Bank Pool is Null");
				}
				if (tBank != Bank.NO_BANK) {
					tBankPortfolioJPanel = tBank.buildTrainPortfolioInfoJPanel (this, corporation, 
							tGameManager, TrainPortfolio.COMPACT_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
					bankJPanel.add (tBankPortfolioJPanel);
					bankJPanel.add (Box.createHorizontalGlue ());
				} else {
					System.err.println ("Bank is Null");
				}
				bankJPanel.repaint ();
				bankJPanel.revalidate ();
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
				} else {
					JLabel NoPrivatesLeft = new JLabel ("No Privates Left for purchase");
					privatesBox.add (NoPrivatesLeft);
				}
				privatesBox.repaint ();
				privatesBox.revalidate ();
			}
		}
	}
	
	public void fillCertPortfolioJPanel () {
		TrainCompany tTrainCompany;
		
		certInfoJPanel = NO_PANEL;
		if (corporation instanceof TrainCompany) {
			if (certJPanel == NO_PANEL) {
				certJPanel = new JPanel ();
				certJPanel.setLayout (new BoxLayout (certJPanel, BoxLayout.Y_AXIS));
			}
			tTrainCompany = (TrainCompany) corporation;
			certJPanel.removeAll ();
			certInfoJPanel = tTrainCompany.buildCertPortfolioInfoJPanel (this);
			certJPanel.add (certInfoJPanel);
			certJPanel.validate ();
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
		if (corporation != Corporation.NO_CORPORATION) {
			if (corporation instanceof TrainCompany) {
				tTreasuryValue = ((TrainCompany) corporation).getTreasury ();
			}
		}
		treasuryLabel.setText ("Treasury: " + Bank.formatCash (tTreasuryValue));
	}
	
	public void updateCFActionButtons () {
		if (corporation.mapVisible ()) {
			showMapActionButton.setEnabled (false);
			showMapActionButton.setToolTipText ("The Map is already visible.");
		} else {
			showMapActionButton.setEnabled (true);
			showMapActionButton.setToolTipText (NO_TOOL_TIP);
		}
		updateTTODButtons ();
		updateBuyTrainActionButton ();
		updateForceBuyTrainActionButton ();
		updateBuyPrivateActionButton ();
		updateDoneActionButton ();
		corporation.configurePrivateBenefitButtons (actionButtons);
		repaint ();
		revalidate ();
	}

	public void updateTTODButtons () {
		int tTrainCount;
		
		tTrainCount = corporation.getTrainCount ();
		updatePlaceTileActionButton ();
		updatePlaceTokenActionButton ();
		updateOperateTrainActionButton (tTrainCount);
		updatePayFullDividendActionButton ();
		updatePayHalfDividendActionButton (tTrainCount);
		updatePayNoDividendActionButton (tTrainCount);
	}
	
	private void updatePayHalfDividendActionButton (int aTrainCount) {
		String tDisableToolTipReason;
		
		if (corporation.isWaitingForResponse ()) {
			payHalfDividendActionButton.setEnabled (false);
			payHalfDividendActionButton.setToolTipText ("Waiting for Response");
		} else if (corporation.isPlaceTileMode ()) {
			payHalfDividendActionButton.setEnabled (false);
			tDisableToolTipReason = IN_PLACE_TILE_MODE;
			payHalfDividendActionButton.setToolTipText (tDisableToolTipReason);
		} else if (corporation.isPlaceTokenMode ()) {
			payHalfDividendActionButton.setEnabled (false);
			tDisableToolTipReason = IN_TOKEN_MODE;
			payHalfDividendActionButton.setToolTipText (tDisableToolTipReason);				
		} else if ((aTrainCount > 0) && (corporation.getThisRevenue () == TrainCompany.NO_REVENUE_GENERATED)) {
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
		
		if (! corporation.isWaitingForResponse ()) {
			payNoDividendActionButton.setText ("Pay No Dividend");
			if (! corporation.haveLaidAllBaseTokens ()) {
				if (corporation.isStationLaid ()) {
					payNoDividendActionButton.setEnabled (true);
					payNoDividendActionButton.setToolTipText ("Base Token was Skippped due to missing Tile.");
				} else {
					payNoDividendActionButton.setEnabled (false);
					payNoDividendActionButton.setToolTipText ("Base Token must be laid first.");
				}
			} else if (corporation.isPlaceTileMode ()) {
				payNoDividendActionButton.setEnabled (false);
				tToolTip = IN_PLACE_TILE_MODE;
				payNoDividendActionButton.setToolTipText (tToolTip);
			} else if (corporation.isPlaceTokenMode ()) {
				payNoDividendActionButton.setEnabled (false);
				tToolTip = IN_TOKEN_MODE;
				payNoDividendActionButton.setToolTipText (tToolTip);
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
		} else {
			payNoDividendActionButton.setEnabled (false);
			payNoDividendActionButton.setToolTipText ("Waiting for Response");
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
			if (corporation.isPlaceTileMode ()) {
				placeTileActionButton.setEnabled (false);
				tDisableToolTipReason = IN_PLACE_TILE_MODE;
				placeTileActionButton.setToolTipText (tDisableToolTipReason);				
			} else if (corporation.isPlaceTokenMode ()) {
				placeTileActionButton.setEnabled (false);
				tDisableToolTipReason = IN_TOKEN_MODE;
				placeTileActionButton.setToolTipText (tDisableToolTipReason);				
			} else {
				placeTileActionButton.setEnabled (true);
				placeTileActionButton.setToolTipText (NO_TOOL_TIP);
			}
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
				tCheapestTrain = corporation.getCheapestBankTrain ();
				
				if (corporation.mustBuyTrainNow ()) {
					if (corporation.getCash () < tCheapestTrain.getPrice ()) {
						buyTrainForceActionButton.setVisible (true);
						buyTrainForceActionButton.setEnabled (true);
						buyTrainForceActionButton.setToolTipText (NO_TOOL_TIP);
					} else {
						hideForceBuyTrainActionButton ();
					}
				} else {
					if (corporation.hasNoTrain ()) {
						if (corporation.getCash () < tCheapestTrain.getPrice ()) {
							buyTrainForceActionButton.setVisible (true);
							buyTrainForceActionButton.setEnabled (true);
							buyTrainForceActionButton.setToolTipText ("OPTIONAL to Force Buy a Train");
						} else {
							hideForceBuyTrainActionButton ();
						}
					} else {
						hideForceBuyTrainActionButton ();
					}
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
				updateOtherCorpsJPanel ();
			}
		}
		
		// if tThisSelectedCount is one, it has been selected to Upgrade to another train
		if (tThisSelectedTrainCount == 1) {
			// If the actual tSelectedCount is zero -- the Apply the Discount;
			if (tSelectedCount == 0) {
				tTrainCompany.applyDiscount ();
				fillOtherCorpsJPanel (false, "Select Train to Upgrade to from Bank");
			}
			if (canBuySelectedTrain (tSelectedCount)) {
				updateBuyTrainLabel ();
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
			updateBuyTrainLabel ();
			
			enableBuyTrainActionButton ();
		} else {
			updateBuyTrainLabel ();
			disableBuyTrainActionButton ();
		}
	}

	private void updateBuyTrainLabel () {
		TrainHolderI tOtherTrainHolder;
		Corporation tOtherCorporation;
		String tOtherPresidentName;
		String tCurrentPresidentName;
		
		buyTrainActionButton.setText (BUY_TRAIN);
		tOtherTrainHolder = corporation.getOtherSelectedTrainHolder ();
		if (tOtherTrainHolder != null) {
			if (tOtherTrainHolder instanceof Corporation) {
				tOtherCorporation = (Corporation) tOtherTrainHolder;
				tOtherPresidentName = tOtherCorporation.getPresidentName ();
				tCurrentPresidentName = corporation.getPresidentName ();
				if (! tCurrentPresidentName.equals (tOtherPresidentName)) {
					buyTrainActionButton.setText (OFFER_TO_BUY_TRAIN);
				}
			}
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
		String tMapCellID;
		MapCell tMapCell;
		int tCost;
		boolean tSetCostOnLabel = false;
		
		if (corporation.canLayToken ()) {
			placeTokenActionButton.setEnabled (true);
			if (corporation.haveLaidAllBaseTokens ()) {
				placeTokenActionButton.setText (PLACE_TOKEN);
				if (corporation.isPlaceTileMode ()){
					placeTokenActionButton.setEnabled (false);
					placeTokenActionButton.setToolTipText (IN_PLACE_TILE_MODE);
				} else if (corporation.isPlaceTokenMode ()){
					placeTokenActionButton.setEnabled (false);
					placeTokenActionButton.setToolTipText (IN_TOKEN_MODE);
				} else if (corporation.haveMoneyForToken ()) {
					placeTokenActionButton.setEnabled (true);
					tSetCostOnLabel = true;
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
					tMapCell = corporation.getHomeCity1 ();
					tMapCellID = tMapCell.getID ();
					if (corporation.isTileAvailableForMapCell (tMapCell)) {
						tDisableToolTipReason = String.format (HOME_NO_TILE, tMapCellID);
						placeTokenActionButton.setToolTipText (tDisableToolTipReason);
					} else {
						placeTokenActionButton.setText (SKIP_BASE_TOKEN);
						placeTokenActionButton.setActionCommand (SKIP_BASE_TOKEN);
						placeTokenActionButton.setEnabled (true);
						placeTokenActionButton.setToolTipText (NO_TOOL_TIP);
					}
				} else if (! corporation.homeMapCell2HasTile ()) {
					tMapCell = corporation.getHomeCity2 ();
					tMapCellID = tMapCell.getID ();
					tDisableToolTipReason = String.format (HOME_NO_TILE, tMapCellID);
					if (corporation.isTileAvailableForMapCell (tMapCell)) {
						placeTokenActionButton.setEnabled (false);
						placeTokenActionButton.setToolTipText (tDisableToolTipReason);
					} else {
						placeTokenActionButton.setText (SKIP_BASE_TOKEN);
						placeTokenActionButton.setActionCommand (SKIP_BASE_TOKEN);
						placeTokenActionButton.setEnabled (true);
						placeTokenActionButton.setToolTipText (NO_TOOL_TIP);
					}
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
		
		if (tSetCostOnLabel) {
			tMapCell = MapCell.NO_MAP_CELL;
			tCost = corporation.getCostToLayToken (tMapCell);
			if (tCost > 0) {
				placeTokenActionButton.setText (PLACE_TOKEN + " for " + Bank.formatCash (tCost));
			}
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
		if (corporation.isPlaceTileMode () || corporation.isPlaceTokenMode ()) {
			operateTrainActionButton.setEnabled(false);
			operateTrainActionButton.setToolTipText (COMPLETE_TT_PLACEMENT);
		} else if (corporation.canOperateTrains ()) {
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
		
		updateBuyPrivateLabel ();
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
	
	private void updateBuyPrivateLabel () {
		PrivateCompany tPrivateCompany;
		String tPrivatePrezName, tCorpPrezName;
		
		tPrivateCompany = corporation.getSelectedPrivateToBuy ();
		if (tPrivateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
			tPrivatePrezName = tPrivateCompany.getPresidentName ();
			tCorpPrezName = corporation.getPresidentName ();
			if (tCorpPrezName.equals (tPrivatePrezName)) {
				buyPrivateActionButton.setText (BUY_PRIVATE);
			} else {
				buyPrivateActionButton.setText (OFFER_TO_BUY_PRIVATE);
			}
		}
	}
	
	public void updateInfo () {
		setStatusLabel ();
		setPhaseInfo ();
		setPresidentLabel ();
		setTreasuryLabel ();
		setTokenLabel ();
		setLastRevenueLabel ();
		updateBuyableItems ();
		updateCFActionButtons ();
		revalidate ();
	}

	public void updateBuyableItems () {
		fillCertPortfolioJPanel ();
		fillPrivatesBox ();
		updateBankBox ();
		updateOtherCorpsJPanel ();
	}

	public void updateOtherCorpsJPanel () {
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
		fillOtherCorpsJPanel (tCanBuyTrain, tDisableToolTipReason);
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
		if (corporation.isWaitingForResponse ()) {
			disableBuyTrainActionButton ("Waiting for Response from Puchase Offer");
		} else  if (corporation.isOperating ()) {
			updateBuyTrainActionButton ();
			updateBuyPrivateActionButton ();
			updateDoneActionButton ();			
		}
	}

	public void waitForResponse() {
		System.out.println ("Time to Disable Everything while waiting for Response -  New State [" + corporation.getStateName () + "]");
		updateInfo ();
	}
}
