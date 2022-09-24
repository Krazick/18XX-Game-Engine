package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.MapFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.GUI;
import ge18xx.utilities.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CorporationFrame extends XMLFrame implements ActionListener, ItemListener {
	static final String SHOW_MAP = "Show Map";
	static final String PLACE_TILE = "Place Tile";
	static final String PLACE_BASE_TILE = "Place Base Tile";
	public static final String PLACE_TILE_PRIVATE = "Place Tile on Private Home";
	public static final String PLACE_TOKEN_PRIVATE = "Place Token on Private Home";
	public static final String PLACE_PORT_TOKEN = "Place Port Token";
	public static final String PLACE_CATTLE_TOKEN = "Place Cattle Token";
	public static final String BUY_LICENSE = "Buy License to use Private Home";
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
	public static final String BUY_TRAIN = "Buy Train";
	static final String OFFER_TO_BUY_TRAIN = "Offer to Buy Train";
	static final String FORCE_BUY_TRAIN = "Force Buy Train";
	static final String BUY_PRIVATE = "Buy Private";
	static final String COMPLETE_TT_PLACEMENT = "Need to complete Tile/Token Placement";
	static final String OFFER_TO_BUY_PRIVATE = "Offer to Buy Private";
	static final String MUST_LAY_BASE_TOKEN = "Must lay Base Token(s) before Tile Lay";
	static final String DIVIDENDS_NOT_HANDLED = "Dividends have not been handled yet";
	static final String TRAIN_SELECTED = "Train has been Selected for Purchase";
	static final String PRIVATE_SELECTED = "Private has been Selected for Purchase";
	static final String MUST_BUY_TRAIN = "Corporation must buy a Train";
	static final String MUST_PAY_INTEREST = "Must Pay Interest on outstanding loans before handling dividends.";
	static final String NO_CORPORATION_LOANS = "Corporation has no Loans";
	static final String GET_LOAN = "Get Loan";
	static final String REDEEM_LOAN = "Redeem Loan";
	static final String PAY_LOAN_INTEREST = "Pay Loan Interest";
	static final String DONE = "Done";
	static final String UNDO = "Undo";
	private static final long serialVersionUID = 1L;
	JPanel bankJPanel;
	JPanel certJPanel;
	JPanel privatesBox;
	JPanel corporationJPanel;
	JPanel otherCorpsJPanel;
	JPanel corporationAllInfoJPanel;
	JPanel corporationInfoJPanel;
	JPanel privatesJPanel;
	JPanel certInfoJPanel;
	JPanel buttonsJPanel;
	JLabel treasuryLabel;
	JLabel presidentLabel;
	JLabel statusLabel;
	JLabel tokenLabel;
	JLabel phaseNameLabel;
	JLabel trainLimitLabel;
	JLabel allowedTilesLabel;
	JLabel roundInfoLabel;
	JLabel lastRevenueLabel;
	JLabel loanCountLabel;
	JButton showMapButton;
	JButton placeBaseTileButton;
	JButton placeTileButton;
	JButton placeTokenButton;
	JButton operateTrainButton;
	JButton payFullDividendButton;
	JButton payHalfDividendButton;
	JButton payNoDividendButton;
	JButton buyTrainButton;
	JButton buyTrainForceButton;
	JButton buyPrivateButton;
	JButton getLoanButton;
	JButton redeemLoanButton;
	JButton payLoanInterestButton;
	JButton doneButton;
	JButton undoButton;
	JButton explainButton;
	ButtonsInfoFrame buttonsInfoFrame;
	Corporation corporation;
	boolean isNetworkGame;

	public CorporationFrame (String aFrameName, Corporation aCorporation, boolean aIsNetworkGame) {
		super (((aCorporation != Corporation.NO_CORPORATION) ? aCorporation.getName () + " " : "") + aFrameName);
		GameManager tGameManager;

		certJPanel = GUI.NO_PANEL;
		corporation = aCorporation;
		if (isCorporationSet ()) {
			corporation = aCorporation;
			tGameManager = corporation.getGameManager ();
			buttonsInfoFrame = new ButtonsInfoFrame (corporation.getName () + " Corporation Frame Info for Buttons",
					tGameManager);

			buildCorporationAllInfoJPanel ();

			buildCorporationJPanel ();
			
			setSize (900, 800);
			setIsNetworkGame (aIsNetworkGame);
			updateUndoButton ();
		}
	}

	private void buildCorporationJPanel () {
		JPanel tTopBoxes;
		
		tTopBoxes = buildTopBoxes ();

		corporationJPanel = new JPanel ();
		corporationJPanel.setLayout (new BoxLayout (corporationJPanel, BoxLayout.Y_AXIS));
		corporationJPanel.add (Box.createVerticalStrut (20));
		corporationJPanel.add (tTopBoxes);
		corporationJPanel.add (Box.createVerticalStrut (10));
		createActionButtonJPanel ();
		corporationJPanel.add (buttonsJPanel);
		corporationJPanel.add (Box.createVerticalStrut (10));

		if (corporation.gameHasPrivates ()) {
			if (corporation.isAShareCompany ()) {
				privatesBox = new JPanel ();
				privatesBox.setLayout (new BoxLayout (privatesBox, BoxLayout.Y_AXIS));
				corporationJPanel.add (privatesBox);
				corporationJPanel.add (Box.createVerticalStrut (10));
			}
		}

		// Set up Bank Pool and Bank Box for Train Certificates - But only for Train
		// Companies

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
	}

	private JPanel buildTopBoxes () {
		JPanel tTopBoxes;
		JPanel tPhaseInfoBox;

		tTopBoxes = new JPanel ();
		tTopBoxes.setLayout (new BoxLayout (tTopBoxes, BoxLayout.X_AXIS));
		tTopBoxes.add (Box.createHorizontalStrut (20));
		tTopBoxes.add (corporationAllInfoJPanel);
		tTopBoxes.add (Box.createHorizontalStrut (10));
		tPhaseInfoBox = buildPhaseInfoPanel ();
		tTopBoxes.add (tPhaseInfoBox);
		tTopBoxes.add (Box.createHorizontalStrut (20));

		return tTopBoxes;
	}

	private void buildCorporationAllInfoJPanel () {
		Dimension tMinSize = new Dimension (10, 10);

		corporationAllInfoJPanel = new JPanel ();
		corporationAllInfoJPanel.setLayout (new BoxLayout (corporationAllInfoJPanel, BoxLayout.Y_AXIS));
		corporationAllInfoJPanel.setAlignmentY (CENTER_ALIGNMENT);
		corporationAllInfoJPanel.setBorder (BorderFactory.createTitledBorder (
				BorderFactory.createLineBorder (((TrainCompany) corporation).getBgColor (), 2),
				" Information For " + corporation.getName ()));

		buildCorporationInfoJPanel (tMinSize);

		corporationAllInfoJPanel.add (Box.createRigidArea (tMinSize));
		corporationAllInfoJPanel.add (corporationInfoJPanel);
		corporationAllInfoJPanel.add (Box.createRigidArea (tMinSize));
		if (corporation.isATrainCompany ()) {
			// If the Corporation is not Formed, there is no CertJPanel, so don't add it
			// TODO 1856 and 1835, May need to adjust code to add the newly formed CGR,
			// or Prussian in later stages of the game.
			if (certJPanel != GUI.NO_PANEL) {
				corporationAllInfoJPanel.add (certJPanel);
			}
		}
	}

	private void buildCorporationInfoJPanel (Dimension aMinSize) {
		corporationInfoJPanel = new JPanel ();
		corporationInfoJPanel.setLayout (new BoxLayout (corporationInfoJPanel, BoxLayout.X_AXIS));
		corporationInfoJPanel.setAlignmentX (CENTER_ALIGNMENT);
		statusLabel = new JLabel ("");
		corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
		corporationInfoJPanel.add (statusLabel);
		corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
		setStatusLabel ();
		if (corporation.isActive ()) {
			presidentLabel = new JLabel ("");
			corporationInfoJPanel.add (presidentLabel);
			setPresidentLabel ();
			corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
			treasuryLabel = new JLabel ("");
			corporationInfoJPanel.add (treasuryLabel);
			setTreasuryLabel ();
			tokenLabel = corporation.buildTokenLabel ();
			if (tokenLabel != GUI.NO_LABEL) {
				corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
				corporationInfoJPanel.add (tokenLabel);
				setTokenLabel ();
			}
			corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
			lastRevenueLabel = new JLabel ("");
			corporationInfoJPanel.add (lastRevenueLabel);
			setLastRevenueLabel ();
			if (corporation.gameHasLoans ()) {
				loanCountLabel = new JLabel ("");
				corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
				corporationInfoJPanel.add (loanCountLabel);
				setLoanCountLabel ();
			}
			corporationInfoJPanel.add (Box.createRigidArea (aMinSize));
			fillCertPortfolioJPanel ();
		}
	}

	private JPanel buildPhaseInfoPanel () {
		JPanel tPhaseInfoPanel;

		tPhaseInfoPanel = new JPanel ();
		tPhaseInfoPanel.setLayout (new BoxLayout (tPhaseInfoPanel, BoxLayout.Y_AXIS));
		tPhaseInfoPanel.add (Box.createVerticalStrut (5));
		phaseNameLabel = new JLabel ("Current Phase Name ");
		tPhaseInfoPanel.add (phaseNameLabel);
		tPhaseInfoPanel.add (Box.createVerticalStrut (10));
		roundInfoLabel = new JLabel ("Round # of #");
		tPhaseInfoPanel.add (roundInfoLabel);
		tPhaseInfoPanel.add (Box.createVerticalStrut (10));
		trainLimitLabel = new JLabel ("Train Limit");
		tPhaseInfoPanel.add (trainLimitLabel);
		tPhaseInfoPanel.add (Box.createVerticalStrut (10));
		allowedTilesLabel = new JLabel ("Tile Colors");
		tPhaseInfoPanel.add (allowedTilesLabel);
		tPhaseInfoPanel.add (Box.createVerticalStrut (10));

		return tPhaseInfoPanel;
	}

	public void setIsNetworkGame (boolean aIsNetworkGame) {
		isNetworkGame = aIsNetworkGame;
	}

	public void updateUndoButton () {
	}

	private HexMap getMap () {
		MapFrame tMapFrame;
		GameManager tGameManager;
		HexMap tMap;

		tGameManager = corporation.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();
		tMap = tMapFrame.getMap ();

		return tMap;
	}

	public void handlePlaceBaseToken () {
		corporation.enterPlaceTokenMode ();
		updateBaseSelectableMapCells ();
	}
	
	public void handlePlaceBaseTile () {
		handlePlaceTile ();
		updateBaseSelectableMapCells ();
	}

	public void updateBaseSelectableMapCells () {
		MapCell tMapCell;
		HexMap tMap;
		
		tMap = getMap ();
		tMap.removeAllSMC ();
		tMap.clearAllSelected ();
		tMapCell = corporation.getHomeCity1 ();
		addBaseMapCellToSMC (tMapCell, tMap);
		tMapCell = corporation.getHomeCity2 ();
		addBaseMapCellToSMC (tMapCell, tMap);
	}

	public void addBaseMapCellToSMC (MapCell aMapCell, HexMap aMap) {
		if (aMapCell != MapCell.NO_MAP_CELL) {
			aMap.addMapCellSMC (aMapCell);
			aMap.toggleSelectedMapCell (aMapCell);
		}
	}

	public void handlePlaceTile () {
		corporation.showTileTray ();
		corporation.enterPlaceTileMode ();
		corporation.showMap ();
		updateTTODButtons ();
	}

	public void handlePlaceToken () {
		HexMap tMap;
		
		corporation.showTileTray ();
		if (corporation.haveLaidAllBaseTokens ()) {
			corporation.enterPlaceTokenMode ();
			tMap = getMap ();
			tMap.removeAllSMC ();
			tMap.clearAllSelected ();
			tMap.addReachableMapCells ();	// Currently does nothing.
			updateTTODButtons ();
		} else if (corporation.choiceForBaseToken ()) {
			handlePlaceBaseToken ();
		} else {
			corporation.placeBaseTokens ();
		}
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tCommand;

		tCommand = aEvent.getActionCommand ();
		if (SHOW_MAP.equals (tCommand)) {
			corporation.showMap ();
		}
		if (PLACE_BASE_TILE.equals (tCommand)) {
			handlePlaceBaseTile ();
		}
		if (PLACE_TILE.equals (tCommand)) {
			handlePlaceTile ();
		}
		if (PLACE_TOKEN.equals (tCommand)) {
			corporation.showMap ();
			handlePlaceToken ();
		}
		if (SKIP_BASE_TOKEN.equals (tCommand)) {
			corporation.showMap ();
			corporation.skipBaseToken ();
		}
		if (OPERATE_TRAIN.equals (tCommand)) {
			corporation.showMap ();
			corporation.operateTrains ();
		}
		if (PAY_NO_DIVIDEND.equals (tCommand)) {
			corporation.payNoDividend ();
		}
		if (PAY_HALF_DIVIDEND.equals (tCommand)) {
			System.out.println ("Pay Half Dividend Action");
//			corporation.payHalfDividend ();
		}
		if (PAY_FULL_DIVIDEND.equals (tCommand)) {
			corporation.payFullDividend ();
		}
		if (BUY_TRAIN.equals (tCommand)) {
			corporation.buyTrain ();
		}
		if (FORCE_BUY_TRAIN.equals (tCommand)) {
			corporation.forceBuyTrain ();
		}
		if (BUY_PRIVATE.equals (tCommand)) {
			corporation.buyPrivate (true);
		}
		if (GET_LOAN.equals (tCommand)) {
			corporation.getLoan ();
		}
		if (PAY_LOAN_INTEREST.equals (tCommand)) {
			corporation.payLoanInterest ();
		}
		if (REDEEM_LOAN.equals (tCommand)) {
			corporation.redeemLoan ();
		}
		if (DONE.equals (tCommand)) {
			corporation.doneAction ();
		}
		if (UNDO.equals (tCommand)) {
			System.out.println ("Undo Last Action");
			corporation.clearBankSelections ();
			corporation.undoAction ();
		}
		if (ButtonsInfoFrame.EXPLAIN.equals (tCommand)) {
			handleExplainButtons ();
		}
		updateInfo ();
	}

	private void handleExplainButtons () {
		Point tNewPoint;
		GameManager tGameManager;
		PlayerManager tPlayerManager;
		Bank tBank;
		BankPool tBankPool;
		TrainPortfolio tTrainPortfolio;

		tGameManager = corporation.getGameManager ();
		tNewPoint = tGameManager.getOffsetCorporationFrame ();
		buttonsInfoFrame.prepareExplainButtons (Portfolio.NO_PORTFOLIO);

		tBank = tGameManager.getBank ();
		tTrainPortfolio = tBank.getTrainPortfolio ();
		buttonsInfoFrame.fillWithCheckBoxes (tTrainPortfolio);
		tBankPool = tGameManager.getBankPool ();
		tTrainPortfolio = tBankPool.getTrainPortfolio ();
		buttonsInfoFrame.fillWithCheckBoxes (tTrainPortfolio);

		corporation.fillCorporationTrains (buttonsInfoFrame);

		tPlayerManager = tGameManager.getPlayerManager ();
		tPlayerManager.fillPrivateCompanies (buttonsInfoFrame);

		buttonsInfoFrame.handleExplainButtons (tNewPoint);
	}

	private JButton setupButton (String aButtonLabel, String aButtonAction) {
		JButton tButton;

		tButton = new JButton (aButtonLabel);
		tButton.setActionCommand (aButtonAction);
		tButton.addActionListener (this);
		tButton.setVisible (false);

		return tButton;
	}

	private void createActionButtonJPanel () {
		buttonsJPanel = new JPanel (new WrapLayout ());

		doneButton = setupButton (DONE, DONE);
		undoButton = setupButton (UNDO, UNDO);
		placeBaseTileButton = setupButton (PLACE_BASE_TILE, PLACE_BASE_TILE);
		placeTileButton = setupButton (PLACE_TILE, PLACE_TILE);
		placeTokenButton = setupButton (PLACE_TOKEN, PLACE_TOKEN);
		showMapButton = setupButton (SHOW_MAP, SHOW_MAP);
		operateTrainButton = setupButton (OPERATE_TRAIN, OPERATE_TRAIN);
		payNoDividendButton = setupButton (PAY_NO_DIVIDEND, PAY_NO_DIVIDEND);
		payHalfDividendButton = setupButton (PAY_HALF_DIVIDEND, PAY_HALF_DIVIDEND);
		payFullDividendButton = setupButton (PAY_FULL_DIVIDEND, PAY_FULL_DIVIDEND);
		buyTrainForceButton = setupButton (FORCE_BUY_TRAIN, FORCE_BUY_TRAIN);
		buyTrainButton = setupButton (BUY_TRAIN, BUY_TRAIN);
		buyPrivateButton = setupButton (BUY_PRIVATE, BUY_PRIVATE);
		if (corporation.gameHasLoans ()) {
			getLoanButton = setupButton (GET_LOAN, GET_LOAN);
			payLoanInterestButton = setupButton (PAY_LOAN_INTEREST, PAY_LOAN_INTEREST);
			redeemLoanButton = setupButton (REDEEM_LOAN, REDEEM_LOAN);
		}
		explainButton = setupButton (ButtonsInfoFrame.EXPLAIN, ButtonsInfoFrame.EXPLAIN);
		addButtons ();
	}

	private void addButtons () {
		buttonsJPanel.removeAll ();

		addButton (showMapButton);
		addButton (placeBaseTileButton);
		addButton (placeTileButton);
		addButton (placeTokenButton);
		addButton (operateTrainButton);
		if (corporation.gameHasLoans ()) {
			addButton (payLoanInterestButton);
		}
		addButton (payNoDividendButton);
		if (corporation.canPayHalfDividend ()) {
			addButton (payHalfDividendButton);
		}
		addButton (payFullDividendButton);
		addButton (buyTrainButton);
		addButton (buyTrainForceButton);
		if (corporation.gameHasPrivates ()) {
			addButton (buyPrivateButton);
		}
		if (corporation.gameHasLoans ()) {
			addButton (getLoanButton);
			addButton (redeemLoanButton);
		}
		addButton (doneButton);
		addButton (undoButton);
		addButton (explainButton);
	}

	private void addButton (JButton aButton) {
		aButton.setVisible (true);
		buttonsJPanel.add (aButton);
		buttonsInfoFrame.addButton (aButton);
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
					tCorporationsTrainsJPanel = tShareCorporations.buildFullCorpsJPanel (this, corporation,
							tGameManager, TrainPortfolio.FULL_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
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

	public void updateBankJPanel (boolean aCanBuyTrain, String aDisableToolTipReason) {
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
							TrainPortfolio.FULL_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
					bankJPanel.add (Box.createHorizontalGlue ());
					bankJPanel.add (tBPPortfolioJPanel);
					bankJPanel.add (Box.createHorizontalGlue ());
				} else {
					System.err.println ("Bank Pool is Null");
				}
				if (tBank != Bank.NO_BANK) {
					tBankPortfolioJPanel = tBank.buildTrainPortfolioInfoJPanel (this, corporation,
							TrainPortfolio.COMPACT_TRAIN_PORTFOLIO, aCanBuyTrain, aDisableToolTipReason);
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

	public void fillPrivatesBox () { // To show Privates that are owned by the Players
		GameManager tGameManager;
		int tCountOpenPrivates, tCountPlayerOwnedPrivates;
		ShareCompany tShareCompany;

		if (corporation.isAShareCompany ()) {
			tGameManager = corporation.getGameManager ();
			tCountOpenPrivates = tGameManager.getCountOfOpenPrivates ();
			privatesBox.removeAll ();
			if (tCountOpenPrivates > 0) {
				tCountPlayerOwnedPrivates = tGameManager.getCountOfPlayerOwnedPrivates ();
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

		certInfoJPanel = GUI.NO_PANEL;
		if (corporation.isATrainCompany ()) {
			if (certJPanel == GUI.NO_PANEL) {
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

	public void setLoanCountLabel () {
		lastRevenueLabel.setText ("Loan Count: " + corporation.getLoanCount ());
	}
	
	public void setPhaseInfo () {
		PhaseInfo tPhaseInfo;
		String tCurrentRoundOf;

		tPhaseInfo = corporation.getCurrentPhaseInfo ();
		phaseNameLabel.setText ("Current Phase Name " + tPhaseInfo.getFullName ());
		trainLimitLabel.setText ("Train Limit: " + tPhaseInfo.getTrainLimit ());
		allowedTilesLabel.setText ("Tile Colors: " + tPhaseInfo.getTiles ());
		tCurrentRoundOf = corporation.getCurrentRoundOf ();
		roundInfoLabel.setText ("Round " + tCurrentRoundOf);
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
		if (isCorporationSet ()) {
			if (corporation.isATrainCompany ()) {
				tTreasuryValue = ((TrainCompany) corporation).getTreasury ();
			}
		}
		treasuryLabel.setText ("Treasury: " + Bank.formatCash (tTreasuryValue));
	}

	public void updateCFButtons () {
		if (corporation.mapVisible ()) {
			showMapButton.setEnabled (false);
			showMapButton.setToolTipText ("The Map is already visible.");
		} else {
			showMapButton.setEnabled (true);
			showMapButton.setToolTipText (GUI.NO_TOOL_TIP);
		}
		updateTTODButtons ();
		updateBuyTrainButton ();
		updateForceBuyTrainButton ();
		updateBuyPrivateButton ();
		updateDoneButton ();
		corporation.configurePrivateBenefitButtons (buttonsJPanel);
		repaint ();
		revalidate ();
	}

	public void updateTTODButtons () {
		int tTrainCount;

		tTrainCount = corporation.getTrainCount ();
		updatePlaceBaseTileButton ();
		updatePlaceTileButton ();
		updatePlaceTokenButton ();
		updateOperateTrainButton (tTrainCount);
		if (corporation.gameHasLoans ()) {
			updateGetLoanButton ();
			updatePayLoanInterestButton ();
			updateRedeemLoanButton ();
		}
		updatePayFullDividendButton ();
		updatePayHalfDividendButton (tTrainCount);
		updatePayNoDividendButton (tTrainCount);
	}

	private void updateGetLoanButton () {
		String tToolTip;
		int tSharesOwned;
		int tLoanCount;
		int tLoanAmount;
		int tLoanInterest;
		
		tSharesOwned = corporation.getPercentOwned ()/ 10;
		tLoanCount = corporation.getLoanCount ();
		tLoanAmount = corporation.getLoanAmount ();
		tLoanInterest = corporation.getLoanInterest ();
		if (corporation.wasLoanTaken ()) {
			getLoanButton.setEnabled (false);
			tToolTip = "Only one Loan can be taken per Operating Round";
		} else if (tSharesOwned < tLoanCount) {
			getLoanButton.setEnabled (false);
			tToolTip = "Company has " + tSharesOwned + " Shares owned by Players, and has " + tLoanCount + 
					" Loans outstanding. Can't take out any more.";
		} else {
			getLoanButton.setEnabled (true);
			tToolTip = "Can take out Loan of " + Bank.formatCash (tLoanAmount) + ". Interest is " + 
						Bank.formatCash (tLoanInterest) + " per Operating Round.";
		}
		getLoanButton.setToolTipText (tToolTip);
	}
	
	private void updatePayLoanInterestButton () {
		String tToolTip;
		int tLoanCount;
		int tLoanPaymentDue;
		int tLoanInterest;
		
		tLoanCount = corporation.getLoanCount ();
		tLoanInterest = corporation.getLoanInterest ();
		tLoanPaymentDue = tLoanCount * tLoanInterest;
		tToolTip = GUI.NO_TOOL_TIP;
		if (tLoanCount == 0) {
			payLoanInterestButton.setEnabled (false);
			tToolTip = "Company has no Loans, so there is no Interest Due.";
		} else if (corporation.didOperateTrains ()){
			payLoanInterestButton.setEnabled (true);
			tToolTip = "Company has " + tLoanCount + " outstanding Loans, and owes " + Bank.formatCash (tLoanPaymentDue);
		} else if (corporation.hasNoTrain ()) {
			payLoanInterestButton.setEnabled (true);
			tToolTip = "Company has " + tLoanCount + " outstanding Loans, and owes " + Bank.formatCash (tLoanPaymentDue);			
		} else {
			payLoanInterestButton.setEnabled (false);
			tToolTip = "Not Time to pay Loan Interest";
		}
		payLoanInterestButton.setToolTipText (tToolTip);
	}
	
	private void updateRedeemLoanButton () {
		String tToolTip;
		ShareCompany tShareCompany;
		
		if (corporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			if (! tShareCompany.hasOutstandingLoans ()) {
				redeemLoanButton.setEnabled (false);
				tToolTip = NO_CORPORATION_LOANS;
			} else if (!corporation.dividendsHandled ()) {
				redeemLoanButton.setEnabled (false);
				tToolTip = DIVIDENDS_NOT_HANDLED;
			} else if (corporation.getSelectedTrainCount () > 0) {
				redeemLoanButton.setEnabled (false);
				tToolTip = TRAIN_SELECTED;
			} else if (corporation.getCountOfSelectedPrivates () > 0) {
				redeemLoanButton.setEnabled (false);
				tToolTip = PRIVATE_SELECTED;
			} else if (corporation.mustBuyTrainNow ()) {
				redeemLoanButton.setEnabled (false);
				tToolTip = MUST_BUY_TRAIN;
			} else {
				redeemLoanButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			}
		} else {
			redeemLoanButton.setEnabled (false);
			tToolTip = NO_CORPORATION_LOANS;
		}
		redeemLoanButton.setToolTipText (tToolTip);
	}
	
	private void updatePayHalfDividendButton (int aTrainCount) {
		String tToolTip;

		tToolTip = "Status is " + corporation.getStatusName ();
		if (corporation.isWaitingForResponse ()) {
			payHalfDividendButton.setEnabled (false);
			tToolTip = "Waiting for Response";
		} else if (corporation.isPlaceTileMode ()) {
			payHalfDividendButton.setEnabled (false);
			tToolTip = IN_PLACE_TILE_MODE;
		} else if (corporation.isPlaceTokenMode ()) {
			payHalfDividendButton.setEnabled (false);
			tToolTip = IN_TOKEN_MODE;
		} else if ((aTrainCount > 0) && (corporation.getThisRevenue () == TrainCompany.NO_REVENUE_GENERATED)) {
			payHalfDividendButton.setEnabled (false);
			tToolTip = "No Dividends calculated yet";
		} else if (mustPayInterest ()) {
			payHalfDividendButton.setEnabled (false);
			tToolTip = MUST_PAY_INTEREST;
		} else if (corporation.canPayDividend ()) {
			payHalfDividendButton.setEnabled (true);
			payHalfDividendButton
					.setText ("Pay " + Bank.formatCash (corporation.getHalfShareDividend ()) + " per Share");
			tToolTip = GUI.NO_TOOL_TIP;
		} else if (corporation.dividendsHandled ()) {
			payHalfDividendButton.setEnabled (false);
		} else {
			tToolTip = "After Checking, Status is " + corporation.getStatusName ();
		}
		payHalfDividendButton.setToolTipText (tToolTip);
	}

	private boolean mustPayInterest () {
		boolean tMustPayInterest;
		
		if (corporation.gameHasLoans ()) {
			if (corporation.loanInterestHandled ()) {
				tMustPayInterest = false;
			} else if (corporation.getLoanCount () == 0) {
				tMustPayInterest = false;
			} else {
				tMustPayInterest = true;
			}
		} else {
			tMustPayInterest = false;
		}
		
		return tMustPayInterest;
	}
	
	private void updatePayNoDividendButton (int aTrainCount) {
		String tToolTip;

		if (!corporation.isWaitingForResponse ()) {
			payNoDividendButton.setText ("Pay No Dividend");
			if (mustPayInterest ()) {
				payNoDividendButton.setEnabled (false);
				tToolTip = MUST_PAY_INTEREST;
			} else if (!corporation.haveLaidAllBaseTokens ()) {
				if (corporation.isStationLaid ()) {
					payNoDividendButton.setEnabled (true);
					tToolTip = "Base Token was Skippped due to missing Tile.";
				} else if (corporation.canLayBaseToken ()) {
					payNoDividendButton.setEnabled (false);
					tToolTip = "Base Token must be laid first.";
				} else if (corporation.dividendsHandled ()) {
					payNoDividendButton.setEnabled (false);
					tToolTip = corporation.reasonForNoDividendPayment ();
				} else {
					payNoDividendButton.setEnabled (true);
					tToolTip = "Base Token was Skippped due to missing Tile.";
				}
			} else if (corporation.isPlaceTileMode ()) {
				payNoDividendButton.setEnabled (false);
				tToolTip = IN_PLACE_TILE_MODE;
			} else if (corporation.isPlaceTokenMode ()) {
				payNoDividendButton.setEnabled (false);
				tToolTip = IN_TOKEN_MODE;
			} else if (corporation.dividendsHandled ()) {
				payNoDividendButton.setEnabled (false);
				tToolTip = corporation.reasonForNoDividendPayment ();
			} else if (aTrainCount == 0) {
				payNoDividendButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			} else if (corporation.canPayDividend ()) {
				payNoDividendButton.setEnabled (true);
				payNoDividendButton
						.setText ("Hold " + Bank.formatCash (corporation.getThisRevenue ()) + " in Treasury");
				tToolTip = GUI.NO_TOOL_TIP;
			} else if ((aTrainCount > 0) && (corporation.didOperateTrain ())) {
				if (corporation.getThisRevenue () == 0) {
					payNoDividendButton.setEnabled (true);
					tToolTip = GUI.NO_TOOL_TIP;
				} else {
					payNoDividendButton.setEnabled (false);
					if (aTrainCount == 1) {
						tToolTip = "Must Operate the Train first.";
					} else {
						tToolTip = "Must Operate the Trains (QTY: " + aTrainCount + ") first.";
					}
				}
			} else {
				payNoDividendButton.setEnabled (false);
				tToolTip = corporation.reasonForNoDividendOptions ();
			}
		} else {
			payNoDividendButton.setEnabled (false);
			tToolTip = "Waiting for Response";
		}
		payNoDividendButton.setToolTipText (tToolTip);
	}

	private void updatePayFullDividendButton () {
		String tToolTip;

		if (corporation.isPlaceTileMode ()) {
			payFullDividendButton.setEnabled (false);
			tToolTip = IN_PLACE_TILE_MODE;
		} else if (mustPayInterest ()) {
			payFullDividendButton.setEnabled (false);
			tToolTip = MUST_PAY_INTEREST;
		} else if (corporation.canPayDividend ()) {
			payFullDividendButton.setEnabled (true);
			payFullDividendButton
					.setText ("Pay " + Bank.formatCash (corporation.getFullShareDividend ()) + " per Share");
			tToolTip = GUI.NO_TOOL_TIP;
		} else {
			payFullDividendButton.setText (PAY_FULL_DIVIDEND);
			payFullDividendButton.setEnabled (false);
			tToolTip = corporation.reasonForNoDividendPayment ();
		}
		payFullDividendButton.setToolTipText (tToolTip);
	}

	public void updatePlaceBaseTileButton () {
		if (corporation.homeMapCell1HasTile ()) {
			placeBaseTileButton.setVisible (false);
		} else {
			placeBaseTileButton.setVisible (true);
			updateTileButton (placeBaseTileButton);
		}
	}

	private void updatePlaceTileButton () {
		if (corporation.hasPlacedAnyStation ()) {
			placeTileButton.setVisible (true);
			updateTileButton (placeTileButton);
		} else {
			placeTileButton.setVisible (false);
		}
	}

	private void updateTileButton (JButton aTileButton) {
		String tToolTip;

		if (corporation.canLayTile ()) {
			if (corporation.isPlaceTileMode ()) {
				aTileButton.setEnabled (false);
				tToolTip = IN_PLACE_TILE_MODE;
			} else if (corporation.isPlaceTokenMode ()) {
				aTileButton.setEnabled (false);
				tToolTip = IN_TOKEN_MODE;
			} else if (corporation.canLayBaseToken ()) {
				aTileButton.setEnabled (false);
				tToolTip = MUST_LAY_BASE_TOKEN;
			} else {
				aTileButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			}
		} else {
			aTileButton.setEnabled (false);
			tToolTip = corporation.reasonForNoTileLay ();
		}
		aTileButton.setToolTipText (tToolTip);
	}

	private void updateDoneButton () {
		String tToolTip;

		if (!corporation.dividendsHandled ()) {
			doneButton.setEnabled (false);
			tToolTip = DIVIDENDS_NOT_HANDLED;
		} else if (corporation.getSelectedTrainCount () > 0) {
			doneButton.setEnabled (false);
			tToolTip = TRAIN_SELECTED;
		} else if (corporation.getCountOfSelectedPrivates () > 0) {
			doneButton.setEnabled (false);
			tToolTip = PRIVATE_SELECTED;
		} else if (corporation.mustBuyTrainNow ()) {
			doneButton.setEnabled (false);
			tToolTip = MUST_BUY_TRAIN;
		} else {
			doneButton.setEnabled (true);
			tToolTip = GUI.NO_TOOL_TIP;
		}
		doneButton.setToolTipText (tToolTip);
	}

	private void updateForceBuyTrainButton () {
		Train tCheapestTrain;

		if (corporation.isOperating ()) {
			if (corporation.dividendsHandled ()) {
				tCheapestTrain = corporation.getCheapestBankTrain ();

				if (corporation.mustBuyTrainNow ()) {
					if (corporation.getCash () < tCheapestTrain.getPrice ()) {
						buyTrainForceButton.setVisible (true);
						buyTrainForceButton.setEnabled (true);
						buyTrainForceButton.setToolTipText (GUI.NO_TOOL_TIP);
					} else {
						hideForceBuyTrainButton ();
					}
				} else {
					if (corporation.hasNoTrain ()) {
						if (corporation.getCash () < tCheapestTrain.getPrice ()) {
							buyTrainForceButton.setVisible (true);
							buyTrainForceButton.setEnabled (true);
							buyTrainForceButton.setToolTipText ("OPTIONAL to Force Buy a Train");
						} else {
							hideForceBuyTrainButton ();
						}
					} else {
						hideForceBuyTrainButton ();
					}
				}
			} else {
				hideForceBuyTrainButton ();
			}
		} else {
			hideForceBuyTrainButton ();
		}
	}

	private void hideForceBuyTrainButton () {
		buyTrainForceButton.setVisible (false);
		buyTrainForceButton.setEnabled (false);
		buyTrainForceButton.setToolTipText (GUI.NO_TOOL_TIP);
	}

	private boolean canBuySelectedTrain (int aSelectedCount) {
		boolean tCanBuySelectedTrain;

		tCanBuySelectedTrain = (corporation.canBuyTrain () && (aSelectedCount == 1));

		return tCanBuySelectedTrain;
	}

	private void updateBuyTrainButton () {
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
				// TODO Something here when an Upgrade is Selected, a Diesel is selected, then
				// the Diesel is unselected,
				// and Upgrade is unselected then tRemovedADiscount is true -- Fine, but this
				// update then puts the state
				// of the Other Corp Action Buttons to allow a Select,
				// But then disallows an unselect --- This should rebuild the entire set of
				// trains from scratch...
				// but it gets into a weird state
				updateOtherCorpsJPanel ();
			}
		}

		// if tThisSelectedCount is one, it has been selected to Upgrade to another
		// train
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
						updateBuyTrainButton (true);
					} else {
						updateButton (buyTrainButton, false, "Must select Train from the Bank that Can be Upgraded to "
								+ tSelectedTrainToBuy.getName ());
//						updateBuyTrainButton (false, "Must select Train from the Bank that Can be Upgraded to "
//								+ tSelectedTrainToBuy.getName ());
					}
				} else {
					updateButton (buyTrainButton, false, "Must select Train from the Bank to Upgrade");
//					updateBuyTrainButton (false, "Must select Train from the Bank to Upgrade");
				}
			} else {
				updateBuyTrainButton (false);
			}
		} else if (canBuySelectedTrain (tSelectedCount)) {
			updateBuyTrainLabel ();
			updateBuyTrainButton (true);
		} else {
			updateBuyTrainLabel ();
			updateBuyTrainButton (false);
		}
	}

	private void updateBuyTrainLabel () {
		TrainHolderI tOtherTrainHolder;
		Corporation tOtherCorporation;
		String tOtherPresidentName;
		String tCurrentPresidentName;

		buyTrainButton.setText (BUY_TRAIN);
		tOtherTrainHolder = corporation.getOtherSelectedTrainHolder ();
		if (tOtherTrainHolder != TrainHolderI.NO_TRAIN_HOLDER) {
			if (tOtherTrainHolder.isACorporation ()) {
				tOtherCorporation = (Corporation) tOtherTrainHolder;
				tOtherPresidentName = tOtherCorporation.getPresidentName ();
				tCurrentPresidentName = corporation.getPresidentName ();
				if (!tCurrentPresidentName.equals (tOtherPresidentName)) {
					buyTrainButton.setText (OFFER_TO_BUY_TRAIN);
				}
			}
		}
	}

	private void updateBuyTrainButton (boolean aEnable) {
		String tToolTip;

		if (aEnable) {
			tToolTip = GUI.NO_TOOL_TIP;
		} else {
			tToolTip = corporation.reasonForNoBuyTrain ();
		}
//		updateBuyTrainButton (aEnable, tToolTip);
		updateButton (buyTrainButton, aEnable, tToolTip);
	}

//	private void updateBuyTrainButton (boolean aEnable, String aToolTip) {
//		buyTrainButton.setEnabled (aEnable);
//		buyTrainButton.setToolTipText (aToolTip);
//	}

	private void updatePlaceTokenButton () {
		String tDisableToolTipReason;
		String tMapCellID;
		String tPlaceTokenText;
		MapCell tMapCell;
		int tCost;
		boolean tSetCostOnLabel = false;

		if (corporation.canLayToken ()) {
			placeTokenButton.setEnabled (true);
			if (corporation.haveLaidAllBaseTokens ()) {
				tPlaceTokenText = PLACE_TOKEN;
				placeTokenButton.setText (tPlaceTokenText);
				if (corporation.isPlaceTileMode ()) {
					placeTokenButton.setEnabled (false);
					placeTokenButton.setToolTipText (IN_PLACE_TILE_MODE);
				} else if (corporation.isPlaceTokenMode ()) {
					placeTokenButton.setEnabled (false);
					placeTokenButton.setToolTipText (IN_TOKEN_MODE);
				} else if (corporation.haveMoneyForToken ()) {
					placeTokenButton.setEnabled (true);
					tSetCostOnLabel = true;
					placeTokenButton.setToolTipText (GUI.NO_TOOL_TIP);
				} else {
					placeTokenButton.setEnabled (false);
					tDisableToolTipReason = corporation.reasonForNoTokenLay ();
					placeTokenButton.setToolTipText (tDisableToolTipReason);
				}
			} else {
				tPlaceTokenText = PLACE_BASE_TOKEN;
				placeTokenButton.setText (tPlaceTokenText);
				if (!corporation.homeMapCell1HasTile ()) {
					placeTokenButton.setEnabled (false);
					tMapCell = corporation.getHomeCity1 ();
					tMapCellID = tMapCell.getID ();
					if (corporation.isTileAvailableForMapCell (tMapCell)) {
						tDisableToolTipReason = String.format (HOME_NO_TILE, tMapCellID);
						placeTokenButton.setActionCommand (PLACE_TOKEN);
						placeTokenButton.setToolTipText (tDisableToolTipReason);
					} else {
						placeTokenButton.setText (SKIP_BASE_TOKEN);
						placeTokenButton.setActionCommand (SKIP_BASE_TOKEN);
						placeTokenButton.setEnabled (true);
						placeTokenButton.setToolTipText (GUI.NO_TOOL_TIP);
					}
				} else if (!corporation.homeMapCell2HasTile ()) {
					tMapCell = corporation.getHomeCity2 ();
					tMapCellID = tMapCell.getID ();
					tDisableToolTipReason = String.format (HOME_NO_TILE, tMapCellID);
					if (corporation.isTileAvailableForMapCell (tMapCell)) {
						placeTokenButton.setEnabled (false);
						placeTokenButton.setActionCommand (PLACE_TOKEN);
						placeTokenButton.setToolTipText (tDisableToolTipReason);
					} else {
						placeTokenButton.setText (SKIP_BASE_TOKEN);
						placeTokenButton.setActionCommand (SKIP_BASE_TOKEN);
						placeTokenButton.setEnabled (true);
						placeTokenButton.setToolTipText (GUI.NO_TOOL_TIP);
					}
				} else {
					placeTokenButton.setActionCommand (PLACE_TOKEN);
					placeTokenButton.setEnabled (true);
					placeTokenButton.setToolTipText (GUI.NO_TOOL_TIP);
				}
			}
		} else {
			placeTokenButton.setText (PLACE_TOKEN);
			placeTokenButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoTokenLay ();
			placeTokenButton.setToolTipText (tDisableToolTipReason);
		}

		if (tSetCostOnLabel) {
			tMapCell = MapCell.NO_MAP_CELL;
			tCost = corporation.getCostToLayToken (tMapCell);
			if (tCost > 0) {
				placeTokenButton.setText (PLACE_TOKEN + " for " + Bank.formatCash (tCost));
			}
		}
	}

	private void updateOperateTrainButton (int aTrainCount) {
		String tToolTip;
		String tButtonLabel;
		boolean tEnable;

		if (aTrainCount > 1) {
			tButtonLabel = OPERATE_TRAINS;
		} else if (aTrainCount == 1) {
			tButtonLabel = OPERATE_TRAIN;
		} else {
			tButtonLabel = NO_TRAINS_TO_OPERATE;
		}
		if (corporation.isPlaceTileMode () || corporation.isPlaceTokenMode ()) {
			tEnable = false;
			tToolTip = COMPLETE_TT_PLACEMENT;
		} else if (corporation.canOperateTrains ()) {
			tEnable = true;
			tToolTip = GUI.NO_TOOL_TIP;
		} else {
			tEnable = false;
			tToolTip = corporation.reasonForNoTrainOperation ();
		}
		updateButton (operateTrainButton, tEnable, tToolTip, tButtonLabel);
	}

	private void updateBuyPrivateButton () {
		String tToolTipReason;
		String tButtonLabel;
		boolean tEnable;
		
		tButtonLabel = createBuyPrivateLabel ();
		if (corporation.canBuyPrivate ()) {
			if (corporation.getCountOfSelectedPrivates () == 1) {
				tToolTipReason = GUI.NO_TOOL_TIP;
				tEnable = true;
			} else {
				tEnable = false;
				tToolTipReason = "Must Select a Single Private to buy";
			}
		} else {
			tEnable = false;
			tToolTipReason = corporation.reasonForNoBuyPrivate ();
		}
		updateButton (buyPrivateButton, tEnable, tToolTipReason, tButtonLabel);
	}

	private void updateButton (JButton aButton, boolean aEnable, String aToolTip) {
		String tButtonLabel;
		
		tButtonLabel = aButton.getText ();
		updateButton (aButton, aEnable, aToolTip, tButtonLabel);
	}
	
	private void updateButton (JButton aButton, boolean aEnable, String aToolTip, String aButtonLabel) {
		aButton.setEnabled (aEnable);
		aButton.setToolTipText (aToolTip);
		aButton.setText (aButtonLabel);
	}
	
	private String createBuyPrivateLabel () {
		PrivateCompany tPrivateCompany;
		String tPrivatePrezName;
		String tCorpPrezName;
		String tButtonLabel;
		
		tPrivateCompany = corporation.getSelectedPrivateToBuy ();
		tButtonLabel = BUY_PRIVATE;
		if (tPrivateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
			tPrivatePrezName = tPrivateCompany.getPresidentName ();
			tCorpPrezName = corporation.getPresidentName ();
			if (! tCorpPrezName.equals (tPrivatePrezName)) {
				tButtonLabel = OFFER_TO_BUY_PRIVATE;
			}
		}
		
		return tButtonLabel;
	}

	public void updateInfo () {
		setStatusLabel ();
		setPhaseInfo ();
		setPresidentLabel ();
		setTreasuryLabel ();
		setTokenLabel ();
		setLastRevenueLabel ();
		if (corporation.gameHasLoans ()) {
			setLoanCountLabel ();
		}
		updateBuyableItems ();
		updateCFButtons ();
		revalidate ();
		if (corporation.getStatus () == ActorI.ActionStates.WillFloat) {
			setVisible (false);
		}
	}

	public void updateBuyableItems () {
		fillCertPortfolioJPanel ();
		fillPrivatesBox ();
		updateBankJPanel ();
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
					tDisableToolTipReason = corporation.getAbbrev () + " has no cash";
				} else {
					tIsAtTrainLimit = ((TrainCompany) corporation).atTrainLimit ();
					if (tIsAtTrainLimit) {
						tCanBuyTrain = false;
						tDisableToolTipReason = corporation.getAbbrev () + " is at Train Limit";
					}
				}
			} else {
				tDisableToolTipReason = DIVIDENDS_NOT_HANDLED;
			}
		} else {
			tCanBuyTrain = false;
			tDisableToolTipReason = "Cannot buy Other Corporation Trains in current Phase";
		}
		fillOtherCorpsJPanel (tCanBuyTrain, tDisableToolTipReason);
	}

	public void updateBankJPanel () {
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
					tDisableToolTipReason = corporation.getAbbrev () + " has no cash";
				} else {
					tIsAtTrainLimit = ((TrainCompany) corporation).atTrainLimit ();
					if (tIsAtTrainLimit) {
						tCanBuyTrain = false;
						tDisableToolTipReason = corporation.getAbbrev () + " is at Train Limit";
					}
				}
			} else {
				tDisableToolTipReason = DIVIDENDS_NOT_HANDLED;
			}
			updateBankJPanel (tCanBuyTrain, tDisableToolTipReason);
		}
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		if (corporation.isWaitingForResponse ()) {
			updateButton (buyTrainButton, false, "Waiting for Response from Purchase Offer");
		} else if (corporation.isOperating ()) {
			updateBuyTrainButton ();
			updateBuyPrivateButton ();
			updateDoneButton ();
		}
	}
}
