package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
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

public class CorporationFrame extends XMLFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;
	public static final String PLACE_TILE_PRIVATE = "Place Tile on Private Home";
	public static final String PLACE_TOKEN_PRIVATE = "Place Token on Private Home";
	public static final String PLACE_PORT_TOKEN = "Place Port Token";
	public static final String PLACE_CATTLE_TOKEN = "Place Cattle Token";
	public static final String BUY_LICENSE = "Buy License to use Private Home";
	public static final String BUY_TRAIN = "Buy Train";
	static final String SHOW_MAP = "Show Map";
	static final String PLACE_TILE = "Place Tile";
	static final String PLACE_BASE_TILE = "Place Base Tile";
	static final String PLACE_TOKEN = "Place Token";
	static final String PLACE_BASE_TOKEN = "Place Base Token";
	static final String NO_TOKENS = "No Tokens";
	static final String NO_TOKENS_AVAILABLE = "The Company has no Tokens available to Place";
	static final String IN_PLACE_TILE_MODE = "Already in Place Tile Mode";
	static final String IN_TOKEN_MODE = "Already in Place Token Mode";
	static final String SKIP_BASE_TOKEN = "Skip Base Token";
	static final String HOME_NO_TILE = "Home Map Cell %s does not have Tile";
	static final String HOME_NO_TILE_AVAILABLE = "Home Map Cell %s does not have Tile Available";
	static final String OPERATE_TRAIN = "Operate Train";
	static final String OPERATE_TRAINS = "Operate Trains";
	static final String NO_TRAINS_TO_OPERATE = "No Trains to Operate";
	static final String PAY_FULL_DIVIDEND = "Pay Full Dividend";
	static final String PAY_HALF_DIVIDEND = "Pay Half Dividend";
	static final String PAY_NO_DIVIDEND = "Pay No Dividend";
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
	static final String MUST_REDEEM_LOANS = "Corporation has more loans out than Shares Owned by Players. Must Redeem Loans";
	static final String MUST_PAY_INTEREST = "Must Pay Interest on outstanding loans before handling dividends.";
	static final String NO_CORPORATION_LOANS = "Corporation has no Loans";
	static final String ONE_LOAN_PER_OR = "Only one Loan can be taken per Operating Round";
	static final String LOANS_CANNOT_BE_TAKEN_IN_PHASE = "Loans cannot be taken in current Phase";
	static final String GET_LOAN = "Get Loan";
	static final String REDEEM_LOAN = "Redeem Loan";
	static final String MUST_REDEEM_LOAN = "Must Redeem at least %d Loan";
	static final String PAY_LOAN_INTEREST = "Pay Loan Interest";
	static final String DONE = "Done";
	static final String UNDO = "Undo";
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
	JPanel buyTrainButtonsJPanel;
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
	JButton placeBaseTileButton1;
	JButton placeBaseTileButton2;
	JButton placeTileButton;
	JButton placeTokenButton;
	JButton placeBaseTokenButton1;
	JButton placeBaseTokenButton2;
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

	public CorporationFrame (String aFrameName, Corporation aCorporation, boolean aIsNetworkGame, GameManager aGameManager) {
		super (((aCorporation != Corporation.NO_CORPORATION) ? aCorporation.getName () + " " : "") + aFrameName, aGameManager);

		certJPanel = GUI.NO_PANEL;
		corporation = aCorporation;
		if (isCorporationSet ()) {
			corporation = aCorporation;
			buttonsInfoFrame = new ButtonsInfoFrame (corporation.getName () + " Corporation Frame Info for Buttons",
					gameManager);

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
		buildButtonJPanel ();
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
		updateCorpInfoBorder ();

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

	private void updateCorpInfoBorder () {
		String tOwnedPercent;
		
		tOwnedPercent = corporation.buildPercentOwnedLabel ();
		corporationAllInfoJPanel.setBorder (BorderFactory.createTitledBorder (
				BorderFactory.createLineBorder (((TrainCompany) corporation).getBgColor (), 2),
				" Information For " + corporation.getName () + " " + tOwnedPercent + " "));
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
		HexMap tMap;

		tMapFrame = getMapFrame ();
		tMap = tMapFrame.getMap ();

		return tMap;
	}

	private MapFrame getMapFrame () {
		MapFrame tMapFrame;
		GameManager tGameManager;
		tGameManager = corporation.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();
		return tMapFrame;
	}

	// For a company like Eire where it must choose between two Revenue Centers
	// To place the Base Token, come here and enter Place Token Mode
	
	public void handlePlaceHomeToken () {
		corporation.enterPlaceTokenMode ();
	}
	
	// For when the Place Base Token Button that specifies a Map Cell ID
	// Place the Token On, extract it's MapCellID, and Location 
	public void handlePlaceBaseToken (String aSourceTitle) {
		MapCell tMapCell;
		Location tLocation;
		
		if (corporation.choiceForBaseToken ()) {
			updateSelectableMapCell (aSourceTitle);
			handlePlaceHomeToken ();
		} else {
			tMapCell = extractMapCell (aSourceTitle);
			if (corporation.getHomeCity1 () == tMapCell) {
				tLocation = corporation.getHomeLocation1 ();
			} else if (corporation.getHomeCity2 () == tMapCell) {
				tLocation = corporation.getHomeLocation2 ();
			} else {
				tLocation = Location.NO_LOC;
			}
			corporation.placeHomeToken (tMapCell, tLocation);
		}
	}

	public void handlePlaceBaseTile (String aSourceTitle) {
		handlePlaceTile ();
		updateSelectableMapCell (aSourceTitle);
	}

	private void updateSelectableMapCell (String aSourceTitle) {
		MapCell tMapCell;
		HexMap tMap;
		
		tMap = getMap ();
		tMap.removeAllSMC ();
		tMap.clearAllSelected ();
		tMapCell = extractMapCell (aSourceTitle);
		addBaseMapCellToSMC (tMapCell, tMap);
	}

	public void updateBaseSelectableMapCells () {
		MapCell tMapCell1;
		MapCell tMapCell2;
		HexMap tMap;

		tMap = getMap ();
		tMap.removeAllSMC ();
		tMap.clearAllSelected ();
		tMapCell1 = corporation.getHomeCity1 ();
		addBaseMapCellToSMC (tMapCell1, tMap);
		tMapCell2 = corporation.getHomeCity2 ();
		addBaseMapCellToSMC (tMapCell2, tMap);
		if ((tMapCell1 != MapCell.NO_DESTINATION) &&
			(tMapCell2 == tMapCell1)) {
			tMap.toggleSelectedMapCell (tMapCell1);
		}
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

	public void handlePlaceToken (String aSourceTitle) {
		HexMap tMap;

		corporation.showTileTray ();
		tMap = getMap ();
		if (corporation.haveLaidAllBaseTokens ()) {
			corporation.enterPlaceTokenMode ();
			tMap.removeAllSMC ();
			tMap.clearAllSelected ();
			tMap.addReachableMapCells ();	// TODO Currently does nothing.
			updateTTODButtons ();
		}
	}

	private MapCell extractMapCell (String aSourceTitle) {
		MapCell tMapCell;
		String tMapCellID;
		HexMap tMap;
		
		tMap = getMap ();
		tMapCellID = extractMapCellID (aSourceTitle);
		tMapCell = tMap.getMapCellForID (tMapCellID);
		
		return tMapCell;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tCommand;
		String tSourceTitle;
		JButton tSourceButton;
		boolean tConfirmedDoneAction;

		tCommand = aEvent.getActionCommand ();
		corporation.showMap ();
		tSourceButton = (JButton) aEvent.getSource ();
		tSourceTitle = tSourceButton.getText ();
		if (SHOW_MAP.equals (tCommand)) {
			corporation.showMap ();
		}
		if (PLACE_BASE_TILE.equals (tCommand)) {
			handlePlaceBaseTile (tSourceTitle);
		}
		if (PLACE_TILE.equals (tCommand)) {
			handlePlaceTile ();
		}
		if (PLACE_BASE_TOKEN.equals (tCommand)) {
			handlePlaceBaseToken (tSourceTitle);
		}
		if (PLACE_TOKEN.equals (tCommand)) {
			handlePlaceToken (tSourceTitle);
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
			corporation.payHalfDividend ();
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
			tConfirmedDoneAction = confirmDoneAction ();
			if (tConfirmedDoneAction) {	
				corporation.doneAction ();
			}
		}
		if (UNDO.equals (tCommand)) {
			corporation.clearBankSelections ();
			corporation.undoAction ();
		}
		if (ButtonsInfoFrame.EXPLAIN.equals (tCommand)) {
			handleExplainButtons ();
		}
		updateInfo ();
	}

	private boolean confirmDoneAction () {
		boolean tConfirmedDoneAction;
		int tResponse;
				
		if ((gameManager.confirmDontBuyTrain ()) && (corporation.hasNoTrain ())) {
			tResponse = JOptionPane.showConfirmDialog (this,
					"Your Company " + corporation.getAbbrev () + " does not own a Train.\nAre you sure you want to be DONE?", 
					"Confirm DONE", JOptionPane.YES_NO_OPTION);
			if (tResponse == JOptionPane.YES_OPTION) {
				tConfirmedDoneAction = true;
			} else {
				tConfirmedDoneAction = false;
			}
		} else {
			tConfirmedDoneAction = true;
		}

		return tConfirmedDoneAction;
	}
	
	private void handleExplainButtons () {
		Point tNewPoint;
		PlayerManager tPlayerManager;
		Bank tBank;
		BankPool tBankPool;
		TrainPortfolio tTrainPortfolio;

		tNewPoint = gameManager.getOffsetCorporationFrame ();
		buttonsInfoFrame.prepareExplainButtons (Portfolio.NO_PORTFOLIO);

		tBank = gameManager.getBank ();
		tTrainPortfolio = tBank.getTrainPortfolio ();
		buttonsInfoFrame.fillWithCheckBoxes (tTrainPortfolio);
		tBankPool = gameManager.getBankPool ();
		tTrainPortfolio = tBankPool.getTrainPortfolio ();
		buttonsInfoFrame.fillWithCheckBoxes (tTrainPortfolio);

		corporation.fillCorporationTrains (buttonsInfoFrame);

		tPlayerManager = gameManager.getPlayerManager ();
		tPlayerManager.fillPrivateCompanies (buttonsInfoFrame);

		buttonsInfoFrame.handleExplainButtons (tNewPoint);
	}

	private JButton setupButton (String aButtonLabel, String aButtonAction) {
		JButton tButton;

		tButton = setupButton (aButtonLabel, aButtonAction, this, Component.CENTER_ALIGNMENT);
		tButton.setVisible (false);

		return tButton;
	}

	private void buildButtonJPanel () {
		String tPlaceBaseTileLabel;
		String tPlaceBaseTokenLabel;

		buttonsJPanel = new JPanel (new WrapLayout ());

		doneButton = setupButton (DONE, DONE);
		undoButton = setupButton (UNDO, UNDO);
		tPlaceBaseTileLabel = createBaseTileLabel (1);
		placeBaseTileButton1 = setupButton (tPlaceBaseTileLabel, PLACE_BASE_TILE);
		tPlaceBaseTileLabel = createBaseTileLabel (2);
		placeBaseTileButton2 = setupButton (tPlaceBaseTileLabel, PLACE_BASE_TILE);
		placeTileButton = setupButton (PLACE_TILE, PLACE_TILE);
		
		tPlaceBaseTokenLabel = createBaseTokenLabel (1);
		placeBaseTokenButton1 = setupButton (tPlaceBaseTokenLabel, PLACE_BASE_TOKEN);
		tPlaceBaseTokenLabel = createBaseTokenLabel (2);
		placeBaseTokenButton2 = setupButton (tPlaceBaseTokenLabel, PLACE_BASE_TOKEN);
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
		addButton (placeBaseTileButton1);
		addButton (placeBaseTileButton2);
		addButton (placeTileButton);
		
		addButton (placeBaseTokenButton1);
		addButton (placeBaseTokenButton2);
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
		buyTrainButtonsJPanel = new JPanel ();
		addButtonsSubPanel (buyTrainButtonsJPanel, buyTrainButton);
		addButtonsSubPanel (buyTrainButtonsJPanel, buyTrainForceButton);
		addButtonsSubPanel (buyTrainButtonsJPanel);
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

	private void addButtonsSubPanel (JPanel aButtonsSubPanel, JButton aButton) {
		aButton.setVisible (true);
		aButtonsSubPanel.add (aButton);
		buttonsInfoFrame.addButton (aButton);
	}
	
	private void addButtonsSubPanel (JPanel aButtonsSubPanel) {
		buttonsJPanel.add (aButtonsSubPanel);
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
		String tLoanCount;
		
		tLoanCount = "Loan Count: " + corporation.getLoanCount ();
		if (corporation.wasLoanTaken ()) {
			tLoanCount += "*";
		}
		loanCountLabel.setText (tLoanCount);
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

	public void updatePrivateBenefitButtons () {
		corporation.configurePrivateBenefitButtons (buttonsJPanel);
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
		updatePrivateBenefitButtons ();
		repaint ();
		revalidate ();
	}

	public void updateTTODButtons () {
		int tTrainCount;

		tTrainCount = corporation.getTrainCount ();
		updatePlaceBaseTileButtons ();
		updatePlaceTileButton ();
		updatePlaceBaseTokenButtons ();
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
		ShareCompany tShareCompany;
		
		if (corporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tSharesOwned = tShareCompany.getSharesOwnedByPlayerOrCorp ();
			tLoanCount = tShareCompany.getLoanCount ();
			tLoanAmount = tShareCompany.getLoanAmount ();
			tLoanInterest = tShareCompany.getLoanInterest ();
			if (! gameManager.loansAllowed ()) {
				getLoanButton.setEnabled (false);
				tToolTip = LOANS_CANNOT_BE_TAKEN_IN_PHASE;				
			} else if (tShareCompany.wasLoanTaken ()) {
				getLoanButton.setEnabled (false);
				tToolTip = ONE_LOAN_PER_OR;
			} else if (tSharesOwned <= tLoanCount) {
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
		} else if (corporation.didPayLoanInterest ()){
			payLoanInterestButton.setEnabled (false);
			tToolTip = "Company has paid Loan Interest, so there is no additional Interest Due.";			
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
		int tMustRedeemCount;
		String tRedeemLabel;
		String tToolTip;
		ShareCompany tShareCompany;
		
		tRedeemLabel = REDEEM_LOAN;
		if (corporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tMustRedeemCount = tShareCompany.getMustRedeemLoanCount ();
			if (! tShareCompany.hasOutstandingLoans ()) {
				redeemLoanButton.setEnabled (false);
				tToolTip = NO_CORPORATION_LOANS;
			} else if (! corporation.dividendsHandled ()) {
				redeemLoanButton.setEnabled (false);
				tToolTip = DIVIDENDS_NOT_HANDLED;
			} else if (corporation.getCountOfSelectedPrivates () > 0) {
				redeemLoanButton.setEnabled (false);
				tToolTip = PRIVATE_SELECTED;
			} else if (corporation.getSelectedTrainCount () > 0) {
				redeemLoanButton.setEnabled (false);
				tToolTip = TRAIN_SELECTED;
			} else if (corporation.mustBuyTrainNow ()) {
				redeemLoanButton.setEnabled (false);
				tToolTip = MUST_BUY_TRAIN;
			} else if (tMustRedeemCount > 0) {
				tRedeemLabel = String.format (MUST_REDEEM_LOAN, tMustRedeemCount);
				if (tMustRedeemCount > 1) {
					tRedeemLabel += "s";
				}
				redeemLoanButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			} else if (corporation.getCash () < corporation.loanAmount) {
				redeemLoanButton.setEnabled (false);
				tToolTip = "Not enough cash to pay a Loan back";
			} else {
				redeemLoanButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			}
		} else {
			redeemLoanButton.setEnabled (false);
			tToolTip = NO_CORPORATION_LOANS;
		}
		redeemLoanButton.setText (tRedeemLabel);
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

	public void updatePlaceBaseTileButtons () {
		String tPlaceBaseTileLabel1;
		String tPlaceBaseTileLabel2;

		tPlaceBaseTileLabel1 = createBaseTileLabel (1);
		if (corporation.homeMapCell1HasTile ()) {
			placeBaseTileButton1.setVisible (false);
		} else {
			placeBaseTileButton1.setText (tPlaceBaseTileLabel1);
			placeBaseTileButton1.setVisible (true);
			updateTileButton (placeBaseTileButton1);
		}
		tPlaceBaseTileLabel2 = createBaseTileLabel (2);
		if (corporation.homeMapCell2HasTile () || tPlaceBaseTileLabel1.equals (tPlaceBaseTileLabel2)) {
			placeBaseTileButton2.setVisible (false);
		} else {
			placeBaseTileButton2.setText (tPlaceBaseTileLabel2);
			placeBaseTileButton2.setVisible (true);
			updateTileButton (placeBaseTileButton2);
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
		boolean tEnableTile;
		MapFrame tMapFrame;
		
		if (corporation.canLayTile ()) {
			if (corporation.isPlaceTileMode ()) {
				tEnableTile = false;
				tToolTip = IN_PLACE_TILE_MODE;
			} else if (corporation.isPlaceTokenMode ()) {
				tEnableTile = false;
				tToolTip = IN_TOKEN_MODE;
			} else if (! corporation.allBasesHaveTiles ()) {
				tEnableTile = true;
				tToolTip = "Can Lay Base Tile";
			} else if (corporation.canLayBaseToken ()) {
				tEnableTile = false;
				tToolTip = MUST_LAY_BASE_TOKEN;
			} else {
				tEnableTile = true;
				tToolTip = GUI.NO_TOOL_TIP;
			}
		} else {
			tEnableTile = false;
			tToolTip = corporation.reasonForNoTileLay ();
		}
		aTileButton.setEnabled (tEnableTile);
		aTileButton.setToolTipText (tToolTip);
		tMapFrame = getMapFrame ();
		tMapFrame.setEnabledBuildGraphsButton (tEnableTile, "Not ready to place a tile");
	}
	
	public void updatePlaceBaseTokenButtons () {
		String tPlaceBaseTokenLabel1;
		String tPlaceBaseTokenLabel2;
		MapCell tMapCell;
		
		tPlaceBaseTokenLabel1 = createBaseTokenLabel (1);
		tMapCell = corporation.getHomeCity1 ();
		if (corporation.haveLaidThisBaseToken (tMapCell)) {
			placeBaseTokenButton1.setVisible (false);
		} else {
			updatePlaceBaseTokenButton (placeBaseTokenButton1, tMapCell, tPlaceBaseTokenLabel1);
		}
		tPlaceBaseTokenLabel2 = createBaseTokenLabel (2);
		tMapCell = corporation.getHomeCity2 ();
		if (corporation.haveLaidThisBaseToken (tMapCell) || tPlaceBaseTokenLabel1.equals (tPlaceBaseTokenLabel2)) {
			placeBaseTokenButton2.setVisible (false);
		} else {
			updatePlaceBaseTokenButton (placeBaseTokenButton2, tMapCell, tPlaceBaseTokenLabel2);
		}
	}
	
	private void updatePlaceBaseTokenButton (JButton aPlaceBaseTokenButton, MapCell aMapCell, String aPlaceBaseTokenLabel1) {
		String tToolTip;
		String tMapCellID;
		
		aPlaceBaseTokenButton.setText (aPlaceBaseTokenLabel1);
		aPlaceBaseTokenButton.setVisible (true);
		aPlaceBaseTokenButton.setEnabled (false);
		tMapCellID = aMapCell.getID ();
		tToolTip = String.format (HOME_NO_TILE, tMapCellID);
		if (aMapCell.isTileOnCell () ) {
			if (corporation.isPlaceTileMode ()) {
				aPlaceBaseTokenButton.setEnabled (false);
				tToolTip = IN_PLACE_TILE_MODE;
			} else {
				aPlaceBaseTokenButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			}
		} else {
			if (corporation.isTileAvailableForMapCell (aMapCell)) {
				tToolTip = String.format (HOME_NO_TILE_AVAILABLE, tMapCellID);
			} else {
				aPlaceBaseTokenButton.setText (SKIP_BASE_TOKEN);
				aPlaceBaseTokenButton.setActionCommand (SKIP_BASE_TOKEN);
				aPlaceBaseTokenButton.setEnabled (true);
				tToolTip = GUI.NO_TOOL_TIP;
			}
		}
		aPlaceBaseTokenButton.setToolTipText (tToolTip);
	}

	private String createBaseTokenLabel (int aHomeID) {
		String tMapCellID;
		String tBaseTokenLabel;

		tMapCellID = corporation.getHomeMapCellID (aHomeID);
		tBaseTokenLabel = createBaseLabel (PLACE_BASE_TOKEN, tMapCellID);

		return tBaseTokenLabel;
	}

	private String createBaseTileLabel (int aHomeID) {
		String tMapCellID;
		String tBaseTileLabel;

		tMapCellID = corporation.getHomeMapCellID (aHomeID);
		tBaseTileLabel = createBaseLabel (PLACE_BASE_TILE, tMapCellID);

		return tBaseTileLabel;
	}

	private String createBaseLabel (String aPrefixString, String aHomeMapCellID) {
		String tPlaseBaseLabel;

		if (aHomeMapCellID != null) {
			tPlaseBaseLabel = aPrefixString + " (" + aHomeMapCellID + ")";
		} else {
			tPlaseBaseLabel = aPrefixString;
		}

		return tPlaseBaseLabel;
	}

	private String extractMapCellID (String aLabelText) {
		String tLabelTile = PLACE_BASE_TILE + " \\((.*)\\)";
		String tLabelToken = PLACE_BASE_TOKEN + " \\((.*)\\)";
		Pattern tLabelTokenPattern = Pattern.compile (tLabelToken);
		Pattern tLabelTilePattern = Pattern.compile (tLabelTile);
		Matcher tMatcherToken = tLabelTokenPattern.matcher (aLabelText);
		Matcher tMatcherTile = tLabelTilePattern.matcher (aLabelText);
		String tFoundMapCellID;

		tFoundMapCellID = "";

		if (tMatcherTile.find ()) {
			tFoundMapCellID = tMatcherTile.group (1);
		} else if (tMatcherToken.find () ) {
			tFoundMapCellID = tMatcherToken.group (1);
		}

		return tFoundMapCellID;
	}

	private void updatePlaceTokenButton () {
		TokenCompany tTokenCompany;
		String tDisableToolTipReason;
		String tPlaceTokenText;
		int tTokenCost;
		int tTokenCount;
		MapToken tMapToken;

		tPlaceTokenText = PLACE_TOKEN;
		tTokenCount = corporation.getTokenCount ();
		if (tTokenCount > 0) {
			if (corporation.isATokenCompany ()) {
				tTokenCompany = (TokenCompany) corporation;
				tMapToken = tTokenCompany.getMapTokenOnly (); 
				tTokenCost = tMapToken.getCost ();
				if (tTokenCost > 0) {
					tPlaceTokenText +=  " for " + Bank.formatCash (tTokenCost);
				}
			}
		} else {
			tPlaceTokenText = NO_TOKENS;
		}
		placeTokenButton.setText (tPlaceTokenText);
		if (corporation.canLayToken ()) {
			placeTokenButton.setEnabled (true);
			if (corporation.haveLaidAllBaseTokens ()) {
				if (tTokenCount == 0) {
					updateButton (placeTokenButton, false, NO_TOKENS_AVAILABLE);
				} else if (corporation.isPlaceTileMode ()) {
					updateButton (placeTokenButton, false, IN_PLACE_TILE_MODE);
				} else if (corporation.isPlaceTokenMode ()) {
					updateButton (placeTokenButton, false, IN_TOKEN_MODE);
				} else if (corporation.haveMoneyForToken ()) {
					updateButton (placeTokenButton, true, GUI.NO_TOOL_TIP);
				} else {
					tDisableToolTipReason = corporation.reasonForNoTokenLay ();
					updateButton (placeTokenButton, false, tDisableToolTipReason);
				}
			} else {
				updateButton (placeTokenButton, false, "Have not placed Base Token yet");
			}
		} else {
			placeTokenButton.setEnabled (false);
			tDisableToolTipReason = corporation.reasonForNoTokenLay ();
			placeTokenButton.setToolTipText (tDisableToolTipReason);
		}
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
		} else if (corporation.getLoanCount () > corporation.getSharesOwned ()) {
			doneButton.setEnabled (false);
			tToolTip = MUST_REDEEM_LOANS;
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
		Coupon tCheapestTrain;

		if (corporation.isOperating ()) {
			if (corporation.dividendsHandled ()) {
				tCheapestTrain = corporation.getCheapestBankTrain ();

				if (corporation.mustBuyTrainNow ()) {
					if (corporation.getCash () < tCheapestTrain.getPrice ()) {
						// Kludge here. Should test if any other Corporation has a Train Selected to be 
						// purchased. If the BuyTrainButton is enabled, then a train has been selected.
						if (buyTrainButton.isEnabled ()) {
							buyTrainForceButton.setVisible (true);
							buyTrainForceButton.setEnabled (false);
							buyTrainForceButton.setToolTipText ("A Train from another company has been Selected for Purchase");	
						} else {
							buyTrainForceButton.setVisible (true);
							buyTrainForceButton.setEnabled (true);
							buyTrainForceButton.setToolTipText (GUI.NO_TOOL_TIP);
						}
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
		Train tSelectedTrainToBuy;
		Coupon tSelectedTrainToUpgrade;
		boolean tRemovedADiscount;
		boolean tCanBuyTrain;
		TrainCompany tTrainCompany;

		tTrainCompany = (TrainCompany) corporation;
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

		tCanBuyTrain = false;
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
						tCanBuyTrain = true;
						updateBuyTrainButton (tCanBuyTrain);
					} else {
						updateButton (buyTrainButton, tCanBuyTrain, "Must select Train from the Bank that Can be Upgraded to "
								+ tSelectedTrainToBuy.getName ());
					}
				} else {
					updateButton (buyTrainButton, tCanBuyTrain, "Must select Train from the Bank to Upgrade");
				}
			} else {
				updateBuyTrainButton (tCanBuyTrain);
			}
		} else if (canBuySelectedTrain (tSelectedCount)) {
			updateBuyTrainLabel ();
			tCanBuyTrain = true;
			updateBuyTrainButton (tCanBuyTrain);
		} else {
			updateBuyTrainLabel ();
			updateBuyTrainButton (tCanBuyTrain);
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
		Color tAlertColor;
		Color tBackgroundColor;
		
		if (aEnable) {
			tToolTip = GUI.NO_TOOL_TIP;
		} else {
			tToolTip = corporation.reasonForNoBuyTrain ();
		}
		if (corporation.dividendsHandled ()) {
			tAlertColor = gameManager.getAlertColor ();
			buyTrainButtonsJPanel.setBackground (tAlertColor);
		} else {
			tBackgroundColor = gameManager.getDefaultColor ();
			buyTrainButtonsJPanel.setBackground (tBackgroundColor);
		}
		updateButton (buyTrainButton, aEnable, tToolTip);
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
		updateCorpInfoBorder ();
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
		JCheckBox tCheckbox;
		Object tSource;
		
		if (corporation.isWaitingForResponse ()) {
			updateButton (buyTrainButton, false, "Waiting for Response from Purchase Offer");
		} else if (corporation.isOperating ()) {
			tSource = aItemEvent.getSource ();
			if (tSource instanceof JCheckBox) {
				tCheckbox = (JCheckBox) tSource;
				if (tCheckbox.isSelected ()) {
					tCheckbox.setToolTipText ("Train Selected for Purchase");
				} else {
					tCheckbox.setToolTipText (GUI.NO_TOOL_TIP);
				}
			}
			updateBuyTrainButton ();
			updateForceBuyTrainButton ();
			updateBuyPrivateButton ();
			updateDoneButton ();
		}
	}
}
