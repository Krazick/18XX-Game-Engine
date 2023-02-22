package ge18xx.round;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
//import java.util.LinkedList;
//import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
//import javax.swing.border.Border;
//import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
//import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class RoundFrame extends XMLFrame {
	public static final XMLFrame NO_ROUND_FRAME = null;
	public final static String BASE_TITLE = "Round";
	private static final long serialVersionUID = 1L;
	private static final String DO_STOCK_ACTION = " do Stock Action";
	private static final String PLAYER_DO_AUCTION = "Player do Auction Action";
	private static final String COMPANY_DO_ACTION = "Company do Action";
	private static final String PLAYER_DO_STOCK = "Player" + DO_STOCK_ACTION;
	private static final String PASS_STOCK_TEXT = "Pass in Stock Round";
	private static final String PLAYER_JPANEL_LABEL = "Player Information";
	private static final String YOU_NOT_PRESIDENT = "You are not the President of the Company";
	private static final String NOT_YOUR_TURN = "It is not your turn to Perform the Action";
	private static final String IS_WAITING = "You are in a Wait State";
	private static final String IS_OPERATING_ROUND = "It is an Operating Round, can't Pass";
	private static final String IS_AUCTION_ROUND = "It is an Auction Round, can't Pass";
	static final String SHOW_GE_FRAME_ACTION = "showGEFrame";
	static final String PASS_STOCK_ACTION = "passStockAction";
	static final String BUY_STOCK_ACTION = "buyStockAction";
	static final String PLAYER_ACTION = "DoPlayerAction";
	static final String PLAYER_AUCTION_ACTION = "DoPlayerAuctionAction";
	static final String CORPORATION_ACTION = "DoCorporationAction";
	JPanel roundJPanel;
	JPanel allCorporationsJPanel;
	JPanel buttonsJPanel;
	JPanel headerJPanel;
	JPanel parPricesJPanel;
	JPanel roundInfoJPanel;
	JPanel playersJPanel;
	JPanel fastBuyJPanel;
	JButton passButton;
	JButton doButton;
	JLabel frameLabel;
	JLabel phaseLabel;
	JLabel totalCashLabel;
	JLabel parPriceLabel;
	JLabel gameStateLabel;
	ParPricesPanel parPricesPanel;
	TrainSummaryPanel trainSummaryPanel;
//	List<JLabel> parPrices = new LinkedList<> ();
//	List<JLabel> companiesAtPar = new LinkedList<> ();
//	List<JPanel> parPriceLineJPanels = new LinkedList<> ();
	Logger logger;
	int padding1;
	int padding2;
	String currentRoundOf;
	RoundManager roundManager;

	public RoundFrame (String aFrameName, RoundManager aRoundManager, GameManager aGameManager) {
		super (aFrameName, aGameManager);

		JMenuBar tJMenuBar;
		String tGameName;
		
		roundManager = aRoundManager;
		logger = roundManager.getLogger ();
		padding1 = 10;
		padding2 = 5;
		
		buildRoundJPanel ();
		tJMenuBar = roundManager.getJMenuBar ();
		setJMenuBar (tJMenuBar);
		
		pack ();
		tGameName = aGameManager.getActiveGameName ();
		setStockRoundInfo (tGameName, roundManager.getStockRoundID ());
	}

	private void updateFrameTitle () {
		updateFrameTitle (BASE_TITLE);
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
//		buildParPrices ();
		parPricesPanel = new ParPricesPanel (roundManager);
		buildRoundInfoJPanel ();
		trainSummaryPanel = new TrainSummaryPanel (roundManager);
		
		headerJPanel = new JPanel (true);
		headerJPanel.setMinimumSize (new Dimension (600, 100));
		headerJPanel.setMaximumSize (new Dimension (1100, 150));
		headerJPanel.setBorder (BorderFactory.createEmptyBorder (padding2, padding2, padding2, padding2));
		headerJPanel.setLayout (new BoxLayout (headerJPanel, BoxLayout.X_AXIS));
		headerJPanel.add (Box.createHorizontalStrut (20));
		headerJPanel.add (parPricesPanel);
		headerJPanel.add (Box.createHorizontalGlue ());
		headerJPanel.add (Box.createHorizontalStrut (20));
		headerJPanel.add (roundInfoJPanel);
		headerJPanel.add (Box.createHorizontalStrut (20));
		headerJPanel.add (Box.createHorizontalGlue ());
		headerJPanel.add (trainSummaryPanel);
		headerJPanel.add (Box.createHorizontalStrut (20));
	}

//	public void updateParPrices () {
//		parPricesPanel.updateParPrices ();
//	}

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

	private void buildPlayersJPanel () {
		BoxLayout tLayout;
		
		playersJPanel = new JPanel ();
		playersJPanel.setBorder (BorderFactory.createTitledBorder (PLAYER_JPANEL_LABEL));
		tLayout = new BoxLayout (playersJPanel, BoxLayout.X_AXIS);
		playersJPanel.setLayout (tLayout);
		playersJPanel.add (Box.createHorizontalStrut (10));
		updateAllPlayerJPanels ();
		updateCurrentPlayerText ();
	}

	private void updateAllPlayerJPanels () {
		Player tPlayer;
		StockRound tStockRound;
		JPanel tPlayerJPanel;
		int tPlayerCount;
		int tPriorityPlayer;
		int tIndex;
		int tPlayerOffset;
		int tPlayerIndex;

		tStockRound = roundManager.getStockRound ();
		tPlayerCount = tStockRound.getPlayerCount ();
		tPriorityPlayer = tStockRound.getPriorityIndex ();
		playersJPanel.removeAll ();
		tPlayerOffset = getPlayerOffset (tPlayerCount, tStockRound);
		playersJPanel.add (Box.createHorizontalGlue ());
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayerIndex = getAdjustedPlayerIndex (tPlayerCount, tIndex, tPlayerOffset);
			tPlayer = tStockRound.getPlayerAtIndex (tPlayerIndex);
			if (tPlayer != Player.NO_PLAYER) {
				tPlayerJPanel = tPlayer.buildAPlayerJPanel (tPriorityPlayer, tPlayerIndex);
				playersJPanel.add (tPlayerJPanel);
				playersJPanel.add (Box.createHorizontalStrut (10));
				playersJPanel.add (Box.createHorizontalGlue ());
			} else {
				logger.error ("No Player Found for " + tPlayerIndex);
			}
		}
	}

	private int getAdjustedPlayerIndex (int aPlayerCount, int aIndex, int aIndexOffset) {
		int tPlayerIndex;

		tPlayerIndex = (aIndex + aIndexOffset) % aPlayerCount;

		return tPlayerIndex;
	}

	private int getPlayerOffset (int aPlayerCount, StockRound aStockRound) {
		Player tPlayer;
		String tPlayerName;
		String tFirstPlayerName;
		int tPlayerOffset;
		int tPlayerIndex;

		tFirstPlayerName = roundManager.getFirstPlayerName ();
		
		tPlayerOffset = 0;
		for (tPlayerIndex = 0; tPlayerIndex < aPlayerCount; tPlayerIndex++) {
			tPlayer = aStockRound.getPlayerAtIndex (tPlayerIndex);
			tPlayerName = tPlayer.getName ();
			if (tPlayerName.equals (tFirstPlayerName)) {
				tPlayerOffset = tPlayerIndex;
			}
		}

		return tPlayerOffset;
	}

	private void updateCurrentPlayerText () {
		Player tPlayer;
		StockRound tStockRound;
		int tCurrentPlayer;
		int tPlayerCount;
		int tPlayerIndex;

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

		fastBuyJPanel = new JPanel ();
		fastBuyJPanel.setLayout (new BoxLayout (fastBuyJPanel, BoxLayout.X_AXIS));

		doButton = setupButton (PLAYER_DO_STOCK, PLAYER_ACTION, roundManager, Component.CENTER_ALIGNMENT);
		passButton = setupButton (PASS_STOCK_TEXT, PASS_STOCK_ACTION, roundManager, Component.CENTER_ALIGNMENT);

		addButtonAndSpace (buttonsJPanel, doButton);
		addButtonAndSpace (buttonsJPanel, passButton);
		buttonsJPanel.add (fastBuyJPanel);
		updateDoButton (PLAYER_DO_STOCK, PLAYER_ACTION);

	}

	private void fillFastBuyPanel () {
		GameManager tGameManager;
		Player tCurrentPlayer;
		Certificate tFastBuyCertificate;
		String tPlayerName;
		int tFastBuyIndex;
		boolean tHasMoreFastBuys;
		String tButtonLabel;
		FastBuyButton tFastBuyButton;

		tGameManager = roundManager.getGameManager ();
		fastBuyJPanel.removeAll ();
		tCurrentPlayer = tGameManager.getCurrentPlayer ();
		tHasMoreFastBuys = true;
		tFastBuyIndex = 0;
		while (tHasMoreFastBuys) {
			tFastBuyCertificate = tCurrentPlayer.getNextFastBuyCertificate (tFastBuyIndex);
			if (tFastBuyCertificate != Certificate.NO_CERTIFICATE) {
				tPlayerName = tCurrentPlayer.getName ();
				if (tGameManager.isNetworkAndIsThisClient (tPlayerName)) {
					tButtonLabel = tPlayerName + " Fast Buy of " + tFastBuyCertificate.getCompanyAbbrev () + " for " +
								Bank.formatCash (tFastBuyCertificate.getParPrice ());
					tFastBuyButton = new FastBuyButton (tButtonLabel, tFastBuyCertificate);
					setupButton (BUY_STOCK_ACTION, roundManager, Component.CENTER_ALIGNMENT, tFastBuyButton);
					addButtonAndSpace (fastBuyJPanel, tFastBuyButton);
					if (tCurrentPlayer.hasBoughtShare ()) {
						tFastBuyButton.setEnabled (false);
						tFastBuyButton.setToolTipText ("Already Bought a Share of Stock");
					} else {
						tFastBuyButton.setEnabled (true);
						tFastBuyButton.setToolTipText ("Buy another Share of your Company Stock");
					}
				}
				tFastBuyIndex++;
			} else {
				tHasMoreFastBuys = false;
			}
		}
	}

	private void addButtonAndSpace (JPanel aButtonPanel, JButton aButton) {
		aButtonPanel.add (aButton);
		aButtonPanel.add (Box.createHorizontalStrut (20));
	}

	private void updateDoButton (String aButtonLabel, String aActionCommand) {
		updateDoButtonText (aButtonLabel);
		doButton.setActionCommand (aActionCommand);
	}
	
	public void setCurrentPlayerText () {
		String tPlayerName;

		tPlayerName = getCurrentPlayerName ();
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
		updateDoButtonText (aPlayerName + DO_STOCK_ACTION);
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
		fastBuyJPanel.removeAll ();
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
		fillFastBuyPanel ();
	}

	public void updatePassButton () {
		String tClientUserName, tCurrentPlayerName;
		GameManager tGameManager;
		Player tCurrentPlayer;

		if (passButton != GUI.NO_BUTTON) {
			if (roundManager.isOperatingRound ()) {
				disablePassButton (IS_OPERATING_ROUND);
			} else if (roundManager.isAAuctionRound ()) {
				disablePassButton (IS_AUCTION_ROUND);
			} else {
				tGameManager = roundManager.getGameManager ();
				if (tGameManager.isNetworkGame ()) {
					tCurrentPlayer = tGameManager.getCurrentPlayer ();
					tCurrentPlayerName = tCurrentPlayer.getName ();
					tClientUserName = tGameManager.getClientUserName ();
					if (tCurrentPlayerName.equals (tClientUserName)) {
						if (tCurrentPlayer.isWaiting ()) {
							disablePassButton (IS_WAITING);
						} else {
							verifyMustActions (tGameManager);
						}
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
		Player tCurrentPlayer;

		tGameManager = roundManager.getGameManager ();
		if (doButton != GUI.NO_BUTTON) {
			if (tGameManager.isNetworkGame ()) {
				tCurrentPlayer = tGameManager.getCurrentPlayer ();
				tCurrentPlayerName = tCurrentPlayer.getName ();
				tClientUserName = tGameManager.getClientUserName ();
				if (tCurrentPlayerName.equals (tClientUserName)) {
					if (tCurrentPlayer.isWaiting ()) {
						doButton.setEnabled (false);
						doButton.setToolTipText (IS_WAITING);
					} else {
						doButton.setEnabled (true);
						doButton.setToolTipText ("");
					}
				} else {
					doButton.setEnabled (false);
					doButton.setToolTipText (NOT_YOUR_TURN);
				}
			}
		}
	}

	public void updateDoButtonText (String aNewLabel) {
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
		updateFrameTitle ();
		updateTotalCashLabel ();
		updateGameStateLabel ();
		updatePhaseLabel ();
		updateAllPlayerJPanels ();
		updateAllCorporationsBox ();
		updatePassButton ();
		setFrameBackgrounds ();
		revalidate ();
		repaint ();
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
					setAlertBackgrounds ();
				} else {
					resetBackgrounds ();
				}
			} else if (roundManager.isOperatingRound ()) {
				tOperatingOwnerName = roundManager.getOwnerWhoWillOperate ();
				if (tOperatingOwnerName != Corporation.NO_NAME) {
					if (tOperatingOwnerName.equals (tClientUserName)) {
						setAlertBackgrounds ();
					} else {
						resetBackgrounds ();
					}
				}
			}
		}
	}

	public void setAlertBackgrounds () {
		Color tAlertColor;

		tAlertColor = Color.ORANGE;
		setAllBackgrounds (tAlertColor);
	}

	public void resetBackgrounds () {
		setAllBackgrounds (GUI.defaultColor);
	}

	private void setAllBackgrounds (Color aBackgroundColor) {
		getContentPane ().setBackground (aBackgroundColor);
		setPanelBackground (headerJPanel, aBackgroundColor);
		setPanelBackground (buttonsJPanel, aBackgroundColor);
		setPanelBackground (roundJPanel, aBackgroundColor);
		if (fastBuyJPanel != null) {
			if (fastBuyJPanel.getComponentCount () > 0) {
				setPanelBackground (fastBuyJPanel, aBackgroundColor);
			}
		}
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
