package ge18xx.round;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.Logger;

public class RoundFrame extends XMLFrame {
	public static final RoundFrame NO_ROUND_FRAME = null;
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = "\n";
	private static final String DO_STOCK_ACTION = " do Stock Action";
	private static final String PLAYER_DO_AUCTION = "Player do Auction Action";
	private static final String COMPANY_DO_ACTION = "Company do Action";
	private static final String PLAYER_DO_STOCK = "Player" + DO_STOCK_ACTION;
	private static final String PASS_STOCK_TEXT = "Pass in Stock Round";
	private static final String PLAYER_JPANEL_LABEL = "Player Information";
	private static final String YOU_NOT_PRESIDENT = "You are not the President of the Company";
	private static final String NOT_YOUR_TURN = "It is not your turn to Perform the Action";
	private static final String IS_OPERATING_ROUND = "It is an Operating Round, can't Pass";
	private static final String IS_AUCTION_ROUND = "It is an Auction Round, can't Pass";
	static final String SHOW_GE_FRAME_ACTION = "showGEFrame";
	static final String PASS_STOCK_ACTION = "passStockAction";
	static final String PLAYER_ACTION = "DoPlayerAction";
	static final String PLAYER_AUCTION_ACTION = "DoPlayerAuctionAction";
	static final String CORPORATION_ACTION = "DoCorporationAction";
	RoundManager roundManager;
	JPanel roundJPanel;
	JPanel allCorporationsJPanel;
	JPanel buttonsJPanel;
	JPanel headerJPanel;
	JPanel parPricesJPanel;
	JPanel trainSummaryJPanel;
	JPanel roundInfoJPanel;
	JPanel playersJPanel;
	JButton passButton;
	JButton doButton;
	JButton showGameEngineFrameButton;
	JTextArea trainSummary;
	JLabel frameLabel;
	JLabel phaseLabel;
	JLabel totalCashLabel;
	JLabel parPriceLabel;
	JLabel gameStateLabel;
	List<JLabel> parPrices = new LinkedList<JLabel> ();
	List<JLabel> companiesAtPar = new LinkedList<JLabel> ();
	List<JPanel> parPriceLineJPanels = new LinkedList<JPanel> ();
	Color defaultColor;
	Logger logger;
	int padding1;
	int padding2;
	String currentRoundOf;

	public RoundFrame (String aFrameName, RoundManager aRoundManager, String aGameName) {
		super (aFrameName, aGameName);

		defaultColor = UIManager.getColor ("Panel.background");
		roundManager = aRoundManager;
		logger = roundManager.getLogger ();
		padding1 = 10;
		padding2 = 5;

		buildRoundJPanel ();

		pack ();
		setStockRoundInfo (aGameName, roundManager.getStockRoundID ());
	}

	private void buildRoundJPanel () {
		roundJPanel = new JPanel ();
		roundJPanel.setLayout (new BoxLayout (roundJPanel, BoxLayout.Y_AXIS));
		roundJPanel.setBorder (BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1));

		buildHeaderJPanel ();
		roundJPanel.add (headerJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		buildPlayersJPanel ();
		roundJPanel.add (playersJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		buildAllCorporationsJPanel ();
		roundJPanel.add (allCorporationsJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		buildButtonsJPanel ();
		roundJPanel.add (buttonsJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		buildScrollPane (roundJPanel);
	}

	private void buildHeaderJPanel () {
		buildParPrices ();
		buildRoundInfoJPanel ();
		buildTrainSummary ();

		headerJPanel = new JPanel ();
		headerJPanel.setMinimumSize (new Dimension (600, 100));
		headerJPanel.setMaximumSize (new Dimension (1000, 150));
		headerJPanel.setBorder (BorderFactory.createEmptyBorder (padding2, padding2, padding2, padding2));
		headerJPanel.setLayout (new BoxLayout (headerJPanel, BoxLayout.X_AXIS));
		headerJPanel.add (Box.createHorizontalStrut (20));
		headerJPanel.add (parPricesJPanel);
		headerJPanel.add (Box.createHorizontalGlue ());
		headerJPanel.add (Box.createHorizontalStrut (20));
		headerJPanel.add (roundInfoJPanel);
		headerJPanel.add (Box.createHorizontalStrut (20));
		headerJPanel.add (Box.createHorizontalGlue ());
		headerJPanel.add (trainSummaryJPanel);
		headerJPanel.add (Box.createHorizontalStrut (20));
	}

	private void buildParPrices () {
		Border tBorder1, tBorder2;

		parPricesJPanel = new JPanel ();
		parPricesJPanel.setLayout (new BoxLayout (parPricesJPanel, BoxLayout.Y_AXIS));
		tBorder1 = BorderFactory.createLineBorder (Color.BLACK);
		tBorder2 = BorderFactory.createTitledBorder (tBorder1, "Par Prices", TitledBorder.CENTER, TitledBorder.TOP);
		parPricesJPanel.setBorder (tBorder2);

		updateParPrices ();
	}

	public void updateParPrices () {
		GameManager tGameManager;
		int tParPriceCount;
		Integer tParPrices [];
		int tMinToFloat;
		int tParPriceIndex;
		int tPrice;
		String [] tPrices;
		JPanel tParPriceLinePanel;
		JLabel tPriceLabel;
		JLabel tCompaniesAtParLabel;

		parPriceLineJPanels.clear ();
		parPrices.clear ();
		parPricesJPanel.removeAll ();
		tGameManager = roundManager.getGameManager ();
		tParPrices = tGameManager.getAllStartCells ();
		tParPriceCount = tParPrices.length;
		tMinToFloat = tGameManager.getMinSharesToFloat ();

		tPrices = new String [tParPriceCount];

		for (tParPriceIndex = 0; tParPriceIndex < tParPriceCount; tParPriceIndex++) {
			tPrice = tParPrices [tParPriceIndex].intValue ();
			tPrices [tParPriceIndex] = Bank.formatCash (tPrice);
			tPriceLabel = new JLabel (tPrices [tParPriceIndex]);
			parPrices.add (tPriceLabel);
			tCompaniesAtParLabel = new JLabel ("");
			companiesAtPar.add (tCompaniesAtParLabel);

			tParPriceLinePanel = buildParPriceLinePanel (tParPriceIndex, tMinToFloat, tPrice);
			parPriceLineJPanels.add (tParPriceLinePanel);
			parPricesJPanel.add (parPriceLineJPanels.get (tParPriceIndex));
		}
		updateJustParPrices (tParPriceCount);
	}

	private JPanel buildParPriceLinePanel (int aParPriceIndex, int aMinToFloat, int aPrice) {
		JPanel tParPriceLinePanel;
		JLabel tMinStartupLabel;
		int tMinStartupCash;
		String tMinStartup;
	
		tMinStartupCash = aMinToFloat * aPrice;
		tMinStartup  = "[" + aMinToFloat + " / " + Bank.formatCash (tMinStartupCash) + "]";
		tMinStartupLabel = new JLabel (tMinStartup);
		tParPriceLinePanel = new JPanel ();
		tParPriceLinePanel.setLayout (new BoxLayout (tParPriceLinePanel, BoxLayout.X_AXIS));
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.add (tMinStartupLabel);
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.add (parPrices.get (aParPriceIndex));
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.add (companiesAtPar.get (aParPriceIndex));
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.setAlignmentX (Component.LEFT_ALIGNMENT);
		
		return tParPriceLinePanel;
	}

	private void buildRoundInfoJPanel () {
		int tTotalCash;
		String tGameState;
		Bank tBank;
		JLabel tBankCashLabel;

		frameLabel = new JLabel ("Round");
		frameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel = new JPanel ();
		roundInfoJPanel.setLayout (new BoxLayout (roundInfoJPanel, BoxLayout.Y_AXIS));
		roundInfoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (Box.createVerticalStrut (10));
		roundInfoJPanel.add (frameLabel);
		roundInfoJPanel.add (Box.createVerticalStrut (10));

		tBank = roundManager.getBank ();
		tBank.updateBankCashLabel ();
		tBankCashLabel = tBank.getBankCashLabel ();
		tBankCashLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (tBankCashLabel);
		roundInfoJPanel.add (Box.createVerticalStrut (10));

		tTotalCash = roundManager.getTotalCash ();
		totalCashLabel = new JLabel ("Total Cash: " + Bank.formatCash (tTotalCash));
		totalCashLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (totalCashLabel);

		tGameState = buildGameState ();
		gameStateLabel = new JLabel (tGameState);
		gameStateLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (Box.createVerticalStrut (10));
		roundInfoJPanel.add (gameStateLabel);

		phaseLabel = new JLabel ("Current Game Phase");
		phaseLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (Box.createVerticalStrut (10));
		roundInfoJPanel.add (phaseLabel);
		roundInfoJPanel.add (Box.createVerticalStrut (10));
		updatePhaseLabel ();
	}

	private String buildGameState () {
		String tGameState;

		tGameState = "Current Game State: ";
		if (roundManager.bankIsBroken ()) {
			tGameState += "BANK BROKEN";
		} else if (roundManager.isBankrupt ()) {
			tGameState += "BANKRUPT";
		} else {
			tGameState += "PLAYING";
		}

		return tGameState;
	}

	private void buildTrainSummary () {
		Border tBorder1, tBorder2;

		trainSummaryJPanel = new JPanel ();
		trainSummary = new JTextArea ("");
		trainSummary.setEditable (false);
		updateTrainSummary ();
		tBorder1 = BorderFactory.createLineBorder (Color.BLACK);
		tBorder2 = BorderFactory.createTitledBorder (tBorder1, "Train Summary", TitledBorder.CENTER, TitledBorder.TOP);
		trainSummaryJPanel.setBorder (tBorder2);
		trainSummaryJPanel.add (Box.createHorizontalStrut (10));
		trainSummaryJPanel.add (trainSummary);
		trainSummaryJPanel.add (Box.createHorizontalStrut (10));
	}

	private void updateTrainSummary () {
		String tFullTrainSummary;
		String tBankPoolTrainSummary;
		String tBankTrainSummary;
		Bank tBank;
		BankPool tBankPool;

		tBankPool = roundManager.getBankPool ();
		tBankPoolTrainSummary = getTrainSummary (tBankPool);

		tBank = roundManager.getBank ();
		tBankTrainSummary = getTrainSummary (tBank);
		tFullTrainSummary = tBankPoolTrainSummary + NEWLINE + tBankTrainSummary;

		trainSummary.setText (tFullTrainSummary);
		trainSummary.setBackground (defaultColor);
	}

	public String getTrainSummary (GameBank aBankWithTrains) {
		String tBankTrainSummary = "";

		if (aBankWithTrains.hasAnyTrains ()) {
			tBankTrainSummary = aBankWithTrains.getName () + NEWLINE + NEWLINE + aBankWithTrains.getTrainSummary ();
		}

		return tBankTrainSummary;
	}

	private void buildPlayersJPanel () {
		playersJPanel = new JPanel ();
		playersJPanel.setBorder (BorderFactory.createTitledBorder (PLAYER_JPANEL_LABEL));
		BoxLayout tLayout = new BoxLayout (playersJPanel, BoxLayout.X_AXIS);
		playersJPanel.setLayout (tLayout);
		playersJPanel.add (Box.createHorizontalStrut (10));
		updateAllPlayerJPanels ();
		updateCurrentPlayerText ();
	}

	private void updateAllPlayerJPanels () {
		int tPlayerIndex;
		Player tPlayer;
		JPanel tPlayerJPanel;
		int tPlayerCount, tPriorityPlayer;
		int tIndex, tClientIndex;
		StockRound tStockRound;

		tStockRound = roundManager.getStockRound ();
		tPlayerCount = tStockRound.getPlayerCount ();
		tPriorityPlayer = tStockRound.getPriorityIndex ();
		playersJPanel.removeAll ();
		tClientIndex = 0;
		if (roundManager.isNetworkGame ()) {
			tClientIndex = getClientIndex (tPlayerCount, tStockRound);
		}
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayerIndex = (tIndex + tClientIndex) % tPlayerCount;
			tPlayer = tStockRound.getPlayerAtIndex (tPlayerIndex);
			if (tPlayer != Player.NO_PLAYER) {
				tPlayerJPanel = tPlayer.buildAPlayerJPanel (tPriorityPlayer, tPlayerIndex);
				playersJPanel.add (tPlayerJPanel);
				playersJPanel.add (Box.createHorizontalStrut (10));
			} else {
				logger.error ("No Player Found for " + tPlayerIndex);
			}
		}
	}

	private int getClientIndex (int aPlayerCount, StockRound aStockRound) {
		int tClientIndex = 0;
		int tPlayerIndex;
		String tClientName, tPlayerName;
		Player tPlayer;

		tClientName = roundManager.getClientUserName ();

		for (tPlayerIndex = 0; tPlayerIndex < aPlayerCount; tPlayerIndex++) {
			tPlayer = aStockRound.getPlayerAtIndex (tPlayerIndex);
			tPlayerName = tPlayer.getName ();
			if (tPlayerName.equals (tClientName)) {
				tClientIndex = tPlayerIndex;
			}
		}

		return tClientIndex;
	}

	private void updateCurrentPlayerText () {
		int tPlayerIndex;
		Player tPlayer;
		int tPlayerCount, tCurrentPlayer;
		StockRound tStockRound;

		tStockRound = roundManager.getStockRound ();
		tCurrentPlayer = tStockRound.getCurrentPlayerIndex ();
		tPlayerCount = tStockRound.getPlayerCount ();
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tPlayer = tStockRound.getPlayerAtIndex (tPlayerIndex);
			if (tCurrentPlayer == tPlayerIndex) {
				setCurrentPlayerText (tPlayer.getName ());
			}
		}
	}

	private void buildAllCorporationsJPanel () {
		allCorporationsJPanel = new JPanel ();
		allCorporationsJPanel.setLayout (new BoxLayout (allCorporationsJPanel, BoxLayout.Y_AXIS));
		updateAllCorporationsBox ();
	}

	private void buildButtonsJPanel () {
		buttonsJPanel = new JPanel ();
		buttonsJPanel.setLayout (new BoxLayout (buttonsJPanel, BoxLayout.X_AXIS));

		doButton = setupButton (PLAYER_DO_STOCK, PLAYER_ACTION, roundManager, Component.CENTER_ALIGNMENT);
		passButton = setupButton (PASS_STOCK_TEXT, PASS_STOCK_ACTION, roundManager, Component.CENTER_ALIGNMENT);
		showGameEngineFrameButton = setupButton ("Show Game Engine Frame", SHOW_GE_FRAME_ACTION, roundManager,
				Component.CENTER_ALIGNMENT);

		buttonsJPanel.add (doButton);
		buttonsJPanel.add (Box.createHorizontalStrut (20));
		buttonsJPanel.add (passButton);
		buttonsJPanel.add (Box.createHorizontalStrut (20));
		buttonsJPanel.add (showGameEngineFrameButton);

		updateDoButton (PLAYER_DO_STOCK, PLAYER_ACTION);
	}

	private void updateDoButton (String aButtonLabel, String aActionCommand) {
		updateButtonText (aButtonLabel);
		doButton.setActionCommand (aActionCommand);
	}

	public JButton setupButton (String aLabel, String aAction, ActionListener aListener, float aAlignment) {
		JButton tButton;

		tButton = new JButton (aLabel);
		tButton.setActionCommand (aAction);
		tButton.addActionListener (aListener);
		tButton.setAlignmentX (aAlignment);

		return tButton;
	}

	public void setCurrentPlayerText () {
		String tPlayerName = getCurrentPlayerName ();

		setCurrentPlayerText (tPlayerName);
	}

	public String getCurrentPlayerName () {
		StockRound tStockRound;
		String tPlayerName;

		tStockRound = roundManager.getStockRound ();
		tPlayerName = tStockRound.getCurrentPlayerName ();

		return tPlayerName;
	}

	public void setCurrentPlayerText (String aPlayerName) {
		if (passButton != GUI.NO_BUTTON) {
			passButton.setText (aPlayerName + " " + PASS_STOCK_TEXT);
		}
		updateButtonText (aPlayerName + DO_STOCK_ACTION);
		setActionForCurrentPlayer ();
		updatePassButton ();
	}

	public void setFrameLabel (String aGameName, String aIDLabel) {
		String tRoundType;

		tRoundType = roundManager.getRoundType ();
		frameLabel.setText (aGameName + " " + tRoundType + aIDLabel);
		revalidate ();
	}

	public void setAuctionRound (String aGameName, int aRoundID) {
		setFrameLabel (aGameName, " " + aRoundID);
		updateDoButton (PLAYER_DO_AUCTION, PLAYER_AUCTION_ACTION);
		updatePassButton ();
	}

	public void setOperatingRound (String aGameName, int aRoundIDPart1, int aCurrentOR, int aMaxOR) {
		setCurrentRoundOf (aCurrentOR, aMaxOR);
		setFrameLabel (aGameName, " " + aRoundIDPart1 + " [" + currentRoundOf + "]");
		updateDoButton (COMPANY_DO_ACTION, CORPORATION_ACTION);
		updateTotalCashLabel ();
		updatePassButton ();
	}

	private void setCurrentRoundOf (int aCurrentOR, int aMaxOR) {
		currentRoundOf = aCurrentOR + " of " + aMaxOR;
	}

	public String getCurrentRoundOf () {
		return currentRoundOf;
	}

	public void setStockRoundInfo (String aGameName, int aRoundID) {
		setFrameLabel (aGameName, " " + aRoundID);
		updateDoButton (PLAYER_DO_STOCK, PLAYER_ACTION);
		setCurrentPlayerText ();
		updateTotalCashLabel ();
		updatePassButton ();
	}

	public void updatePassButton () {
		String tClientUserName, tCurrentPlayerName;
		GameManager tGameManager;

		if (passButton != GUI.NO_BUTTON) {
			if (roundManager.isOperatingRound ()) {
				disablePassButton (IS_OPERATING_ROUND);
			} else if (roundManager.isAAuctionRound ()) {
				disablePassButton (IS_AUCTION_ROUND);
			} else {
				tGameManager = roundManager.getGameManager ();
				if (tGameManager.isNetworkGame ()) {
					tCurrentPlayerName = getCurrentPlayerName ();
					tClientUserName = tGameManager.getClientUserName ();
					if (tCurrentPlayerName.equals (tClientUserName)) {
						verifyMustActions (tGameManager);
					} else {
						disablePassButton (NOT_YOUR_TURN);
					}
				} else {
					verifyMustActions (tGameManager);
				}
			}
		}
	}

	private void verifyMustActions (GameManager aGameManager) {
		PlayerFrame tPlayerFrame;
		Player tCurrentPlayer;
		String tToolTip;

		tPlayerFrame = aGameManager.getCurrentPlayerFrame ();
		tCurrentPlayer = aGameManager.getCurrentPlayer ();
		if (tPlayerFrame.hasMustBuyCertificate ()) {
			disablePassButton (PlayerFrame.MUST_BUY_PRIVATE);
		} else if (tPlayerFrame.mustSellStock ()) {
			tToolTip = tPlayerFrame.getMustSellToolTip (tCurrentPlayer);
			disablePassButton (tToolTip);
		} else if (tCurrentPlayer.hasActed ()) {
			disablePassButton (tCurrentPlayer.getName () + " has already acted, cannot Pass");
		} else if (tPlayerFrame.isVisible ()) {
			disablePassButton (PlayerFrame.ALREADY_VISIBLE);
		} else {
			enablePassButton ();
		}
	}

	public void setActionForCurrentPlayer () {
		String tClientUserName, tCurrentPlayerName;
		GameManager tGameManager;

		tGameManager = roundManager.getGameManager ();
		if (doButton != GUI.NO_BUTTON) {
			if (tGameManager.isNetworkGame ()) {
				tCurrentPlayerName = getCurrentPlayerName ();
				tClientUserName = tGameManager.getClientUserName ();
				if (tCurrentPlayerName.equals (tClientUserName)) {
					doButton.setEnabled (true);
					doButton.setToolTipText ("");
				} else {
					doButton.setEnabled (false);
					doButton.setToolTipText (NOT_YOUR_TURN);
				}
			}
		}
	}

	public void updateButtonText (String aNewLabel) {
		if (doButton != GUI.NO_BUTTON) {
			doButton.setText (aNewLabel);
		}
	}

	public void updateAllCorporationsBox () {
		JPanel tCompanyJPanel;
		OperatingRound tOperatingRound;
		int tCorporationCount;
		CorporationList tCorporationList;

		tOperatingRound = roundManager.getOperatingRound ();
		tCorporationCount = tOperatingRound.getPrivateCompanyCount ();
		allCorporationsJPanel.removeAll ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getPrivateCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (true);
			allCorporationsJPanel.add (tCompanyJPanel);
			allCorporationsJPanel.add (Box.createVerticalStrut (10));
		}

		tCorporationCount = tOperatingRound.getCoalCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getCoalCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (true);
			allCorporationsJPanel.add (tCompanyJPanel);
			allCorporationsJPanel.add (Box.createVerticalStrut (10));
		}

		tCorporationCount = tOperatingRound.getMinorCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getMinorCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (true);
			allCorporationsJPanel.add (tCompanyJPanel);
			allCorporationsJPanel.add (Box.createVerticalStrut (10));
		}

		tCorporationCount = tOperatingRound.getShareCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getShareCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (false);
			allCorporationsJPanel.add (tCompanyJPanel);
			allCorporationsJPanel.add (Box.createVerticalStrut (10));
		}
		revalidate ();
	}

	public void updateJustParPrices (int aParPriceCount) {
		OperatingRound tOperatingRound;
		int tCorporationCount, tCorporationIndex;
		int tPriceCount, tPriceIndex;
		int tParPriceIndex;
		CorporationList tCorporationList;
		String tPriceLabel;
		String tParPrice;
		ShareCompany tShareCompany;
		String tCompaniesAtPrice [];

		tOperatingRound = roundManager.getOperatingRound ();
		tCorporationCount = tOperatingRound.getShareCompanyCount ();

		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getShareCompanies ();
			tPriceCount = companiesAtPar.size ();
			tCompaniesAtPrice = new String [tPriceCount];
			for (tCorporationIndex = 0; tCorporationIndex < tCorporationCount; tCorporationIndex++) {
				tShareCompany = (ShareCompany) tCorporationList.getCorporation (tCorporationIndex);
				if (tShareCompany.hasParPrice ()) {
					tParPrice = Bank.formatCash (tShareCompany.getParPrice ());
					for (tParPriceIndex = 0; tParPriceIndex < aParPriceCount; tParPriceIndex++) {
						tPriceLabel = parPrices.get (tParPriceIndex).getText ();
						if (tPriceLabel.equals (tParPrice)) {
							if (tCompaniesAtPrice [tParPriceIndex] == null) {
								tCompaniesAtPrice [tParPriceIndex] = tShareCompany.getAbbrev ();
							} else {
								tCompaniesAtPrice [tParPriceIndex] += ", " + tShareCompany.getAbbrev ();
							}
						}
					}
				}
			}

			for (tPriceIndex = 0; tPriceIndex < tPriceCount; tPriceIndex++) {
				companiesAtPar.get (tPriceIndex).setText (tCompaniesAtPrice [tPriceIndex]);
			}
		}
		revalidate ();
	}

	public void updatePhaseLabel () {
		PhaseManager tPhaseManager;
		PhaseInfo tCurrentPhaseInfo;

		tPhaseManager = roundManager.getPhaseManager ();
		tCurrentPhaseInfo = tPhaseManager.getCurrentPhaseInfo ();

		phaseLabel.setText ("Current Game Phase is " + tCurrentPhaseInfo.getFullName ());
	}

	public void enableActionButton (boolean aEnableActionButton) {
		doButton.setEnabled (aEnableActionButton);
		if (aEnableActionButton) {
			doButton.setToolTipText ("");
		} else {
			doButton.setToolTipText (YOU_NOT_PRESIDENT);
		}
		revalidate ();
	}

	private void updateTotalCashLabel () {
		int tTotalCash;

		tTotalCash = roundManager.getTotalCash ();
		totalCashLabel.setText ("Total Cash: " + Bank.formatCash (tTotalCash));
	}

	private void updateGameStateLabel () {
		String tGameState;

		tGameState = buildGameState ();
		gameStateLabel.setText (tGameState);
	}

	public void updateAll () {
		updateParPrices ();
		updateTotalCashLabel ();
		updateGameStateLabel ();
		updatePhaseLabel ();
		updateTrainSummary ();
		updateAllPlayerJPanels ();
		updateAllCorporationsBox ();
		updatePassButton ();
//		System.out.println ("========= Round Frane, UpdateAll Setting Round Frame Set Backgrounds");
		setFrameBackgrounds ();
		revalidate ();
	}

	public void setFrameBackgrounds () {
		GameManager tGameManager;
		String tClientUserName, tCurrentPlayerName;
		String tOperatingOwnerName;

		tGameManager = roundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tClientUserName = tGameManager.getClientUserName ();
			if (roundManager.isStockRound ()) {
				tCurrentPlayerName = getCurrentPlayerName ();
				if (tCurrentPlayerName.equals (tClientUserName)) {
//					System.out.println ("*********** Stock Round, Round Frame Set Alert Backgrounds");
					setAlertBackgrounds ();
				} else {
					resetBackgrounds ();
				}
			} else if (roundManager.isOperatingRound ()) {
				tOperatingOwnerName = roundManager.getOwnerWhoWillOperate ();
				if (tOperatingOwnerName != Corporation.NO_NAME) {
					if (tOperatingOwnerName.equals (tClientUserName)) {
//						System.out.println ("*********** Operating Round, Round Frame Set Alert Backgrounds");
						setAlertBackgrounds ();
					} else {
						resetBackgrounds ();
					}
				}
			}
		}
	}

	public void setAlertBackgrounds () {
		Color tAlertColor = Color.ORANGE;

//		System.out.println ("*********** Round Frame Set Alert Backgrounds");
		setAllBackgrounds (tAlertColor);
	}

	public void resetBackgrounds () {
//		System.out.println ("*********** Round Frame Resetting Backgrounds");
		setAllBackgrounds (defaultColor);
	}

	private void setAllBackgrounds (Color aBackgroundColor) {
		getContentPane ().setBackground (aBackgroundColor);
		setPanelBackground (headerJPanel, aBackgroundColor);
		setPanelBackground (parPricesJPanel, aBackgroundColor);
		setPanelBackground (trainSummaryJPanel, aBackgroundColor);
		setPanelBackground (buttonsJPanel, aBackgroundColor);
		setPanelBackground (roundJPanel, aBackgroundColor);
	}

	private void setPanelBackground (JPanel aJPanel, Color aBackgroundColor) {
		if (aJPanel != GUI.NO_PANEL) {
			aJPanel.setBackground (aBackgroundColor);
		}
	}

	private void disablePassButton (String aToolTip) {
		if (passButton != GUI.NO_BUTTON) {
			passButton.setEnabled (false);
			passButton.setToolTipText (aToolTip);
		}
	}

	private void enablePassButton () {
		if (passButton != GUI.NO_BUTTON) {
			passButton.setEnabled (true);
			passButton.setToolTipText ("");
		}
	}
}
