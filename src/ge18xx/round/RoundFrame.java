package ge18xx.round;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;

import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;
import geUtilities.GUI;

public class RoundFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	private static final String DO_STOCK_ACTION = " do Stock Action";
	private static final String DOING_STOCK_ACTION = " is doing Stock Action";
	private static final String PLAYER_DO_AUCTION = "Player do Auction Action";
	private static final String PLAYER_DO_FORMATION = "Player do Formation Action";
	private static final String COMPANY_DO_ACTION = "Company do Action";
	private static final String PLAYER_DO_STOCK = "Player" + DO_STOCK_ACTION;
	private static final String PASS_STOCK_TEXT = "Pass in Stock Round";
	private static final String YOU_NOT_PRESIDENT = "You are not the President of the Company";
	private static final String NOT_YOUR_TURN = "It is not your turn to Perform the Action";
	private static final String IS_WAITING = "You are in a Wait State";
	private static final String IS_OPERATING_ROUND = "It is an Operating Round, can't Pass";
	private static final String IS_AUCTION_ROUND = "It is an Auction Round, can't Pass";
	private static final String IS_FORMATION_ROUND = "It is an Formation Round, can't Pass";
	public static final XMLFrame NO_ROUND_FRAME = null;
	public static final String BASE_TITLE = "Round";
	public static final String TOTAL_CASH = "Total Cash: ";
	public static final String SHOW_GE_FRAME_ACTION = "showGEFrame";
	public static final String PASS_STOCK_ACTION = "passStockAction";
	public static final String BUY_STOCK_ACTION = "buyStockAction";
	public static final String PLAYER_ACTION = "DoPlayerAction";
	public static final String PLAYER_AUCTION_ACTION = "DoPlayerAuctionAction";
	public static final String PLAYER_FORMATION_ACTION = "DoPlayerFormationAction";
	public static final String CORPORATION_ACTION = "DoCorporationAction";
	JPanel roundJPanel;
	JPanel allCorporationsJPanel;
	JPanel buttonsJPanel;
	JPanel headerJPanel;
	JPanel roundInfoJPanel;
	JPanel playersJPanel;
	JPanel fastBuyJPanel;
	KButton passButton;
	KButton doButton;
	JLabel frameLabel;
	JLabel phaseLabel;
	JLabel totalCashLabel;
	JLabel parPriceLabel;
	JLabel gameStateLabel;
	PlayersInfoPanel playersInfoPanel;
	ParPricesPanel parPricesPanel;
	TrainSummaryPanel trainSummaryPanel;
	AllCorporationsPanel allCorporationsPanel;
	List<ListenerPanel> listenerPanels;
	Logger logger;
	int padding1;
	int padding2;
	boolean playerDoingAction;
	String currentRoundOf;
	RoundManager roundManager;

	public RoundFrame (String aFrameName, RoundManager aRoundManager, GameManager aGameManager) {
		super (aFrameName, aGameManager);

		JMenuBar tJMenuBar;
		String tGameName;
		String tRoundID;
		Round tCurrentRound;
		
		setRoundManager (aRoundManager);
		logger = roundManager.getLogger ();
		padding1 = 10;
		padding2 = 5;
		setPlayerDoingAction (false);
		listenerPanels = new LinkedList<ListenerPanel> ();
		buildRoundJPanel ();
		tJMenuBar = roundManager.getJMenuBar ();
		setJMenuBar (tJMenuBar);
		
		pack ();
		tGameName = aGameManager.getActiveGameName ();
		tCurrentRound = roundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();
		setStockRoundInfo (tGameName, tRoundID);
		setListenerPanels (false);
	}

	private void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
	}
	
	public void setPlayerDoingAction (boolean aPlayerDoingAction) {
		playerDoingAction = aPlayerDoingAction;
	}
	
	public void setListenerPanels (boolean aListen) {
		for (ListenerPanel tListenerPanel : listenerPanels) {
			tListenerPanel.setListen (aListen);
		}
	}
	
	private void updateFrameTitle () {
		updateFrameTitle (BASE_TITLE);
	}

	private void buildRoundJPanel () {
		Border tMargin;
		
		roundJPanel = new JPanel ();
		roundJPanel.setLayout (new BoxLayout (roundJPanel, BoxLayout.Y_AXIS));
		tMargin = new EmptyBorder (10,10,10,10);

		roundJPanel.setBorder (tMargin);

		buildHeaderJPanel ();
		roundJPanel.add (headerJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		playersInfoPanel = new PlayersInfoPanel (roundManager);
		playersInfoPanel.setRoundFrame (this);
		roundJPanel.add (playersInfoPanel);
		roundJPanel.add (Box.createVerticalGlue ());
		
		buildButtonsJPanel ();
		roundJPanel.add (buttonsJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		allCorporationsPanel = new AllCorporationsPanel (roundManager);
		roundJPanel.add (allCorporationsPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		listenerPanels.add (playersInfoPanel);
		listenerPanels.add (allCorporationsPanel);

		buildScrollPane (roundJPanel);
	}

	private void buildHeaderJPanel () {
		ParPricesPanel tParPricesPanel;
		
		buildRoundInfoJPanel ();
		tParPricesPanel = new ParPricesPanel (roundManager);
		setParPricesPanel (tParPricesPanel);
		trainSummaryPanel = new TrainSummaryPanel (roundManager);
		listenerPanels.add (parPricesPanel);
		listenerPanels.add (trainSummaryPanel);
		
		headerJPanel = new JPanel (true);
		headerJPanel.setMinimumSize (new Dimension (600, 100));
		headerJPanel.setMaximumSize (new Dimension (1100, 150));
		headerJPanel.setLayout (new BoxLayout (headerJPanel, BoxLayout.X_AXIS));
		headerJPanel.add (Box.createHorizontalStrut (5));
		headerJPanel.add (parPricesPanel);
		headerJPanel.add (Box.createHorizontalGlue ());
		headerJPanel.add (Box.createHorizontalStrut (5));
		headerJPanel.add (roundInfoJPanel);
		headerJPanel.add (Box.createHorizontalStrut (5));
		headerJPanel.add (Box.createHorizontalGlue ());
		headerJPanel.add (trainSummaryPanel);
		headerJPanel.add (Box.createHorizontalStrut (5));
	}

	public void setParPricesPanel (ParPricesPanel aParPricesPanel) {
		parPricesPanel = aParPricesPanel;
	}
	
	private void buildRoundInfoJPanel () {
		String tGameState;
		Bank tBank;
		JLabel tBankCashLabel;
		int tStrutSize;

		roundInfoJPanel = new JPanel ();
		roundInfoJPanel.setLayout (new BoxLayout (roundInfoJPanel, BoxLayout.Y_AXIS));
		
		tStrutSize = 10;
		frameLabel = new JLabel (BASE_TITLE);
		addRoundInfoLabel (frameLabel, tStrutSize);

		tBank = roundManager.getBank ();
		tBank.updateBankCashLabel ();
		tBankCashLabel = tBank.getBankCashLabel ();
		addRoundInfoLabel (tBankCashLabel, tStrutSize);

		updateTotalCashLabel ();
		addRoundInfoLabel (totalCashLabel, tStrutSize);

		tGameState = buildGameState ();
		gameStateLabel = new JLabel (tGameState);
		addRoundInfoLabel (gameStateLabel, tStrutSize);

		updatePhaseLabel ();
		addRoundInfoLabel (phaseLabel, tStrutSize);

		roundInfoJPanel.add (Box.createVerticalStrut (tStrutSize));
	}

	private void updatePhaseLabel () {
		PhaseManager tPhaseManager;
		PhaseInfo tCurrentPhaseInfo;
		String tPhaseInfoName;
		String tPhaseInfoText;

		tPhaseManager = roundManager.getPhaseManager ();
		tCurrentPhaseInfo = tPhaseManager.getCurrentPhaseInfo ();

		if (tCurrentPhaseInfo == PhaseInfo.NO_PHASE_INFO) {
			tPhaseInfoName = "NONE SPECIFIED";
		} else {
			tPhaseInfoName = tCurrentPhaseInfo.getFullName ();
		}
		tPhaseInfoText = "Current Game Phase is " + tPhaseInfoName;
		if (phaseLabel == GUI.NO_LABEL) {
			phaseLabel = new JLabel (tPhaseInfoText);
		} else {
			phaseLabel.setText (tPhaseInfoText);
		}
	}
	
	private void updateTotalCashLabel () {
		int tTotalCash;

		tTotalCash = roundManager.getTotalCash ();
		if (totalCashLabel == GUI.NO_LABEL) {
			totalCashLabel = new JLabel (TOTAL_CASH);
		} else {
			totalCashLabel.setText (TOTAL_CASH + Bank.formatCash (tTotalCash));
		}
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

	private void updateGameStateLabel () {
		String tGameState;

		tGameState = buildGameState ();
		gameStateLabel.setText (tGameState);
	}

	private void addRoundInfoLabel (JLabel aRoundInfoLabel, int aStrutSize) {
		aRoundInfoLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (aRoundInfoLabel);
		roundInfoJPanel.add (Box.createVerticalStrut (aStrutSize));
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
		if (tGameManager.isStartPacketPortfolioEmpty ()) {
			fastBuyJPanel.removeAll ();
			tCurrentPlayer = tGameManager.getCurrentPlayer ();
			tHasMoreFastBuys = true;
			tFastBuyIndex = 0;
			while (tHasMoreFastBuys) {
				tFastBuyCertificate = tCurrentPlayer.getNextFastBuyCertificate (tFastBuyIndex);
				if (tFastBuyCertificate != Certificate.NO_CERTIFICATE) {
					tPlayerName = tCurrentPlayer.getName ();
					if (tGameManager.notIsNetworkAndIsThisClient (tPlayerName)) {
						tButtonLabel = tPlayerName + " Fast Buy " + 
								tFastBuyCertificate.getPercentage () + "% of " +
								tFastBuyCertificate.getCompanyAbbrev () + " for " +
								Bank.formatCash (tFastBuyCertificate.getValue ());
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
	}

	private void addButtonAndSpace (JPanel aButtonPanel, KButton aButton) {
		aButtonPanel.add (aButton);
		aButtonPanel.add (Box.createHorizontalStrut (20));
	}

	private void updateDoButton (String aButtonLabel, String aActionCommand) {
		updateDoButtonText (aButtonLabel);
		doButton.setActionCommand (aActionCommand);
	}

	public void setCurrentPlayerText (String aPlayerName) {
		String tDoButtonAction;
		
		if (passButton != GUI.NO_BUTTON) {
			passButton.setText (aPlayerName + " " + PASS_STOCK_TEXT);
		}
		if (playerDoingAction) {
			tDoButtonAction = aPlayerName + DOING_STOCK_ACTION;
		} else {
			tDoButtonAction = aPlayerName + DO_STOCK_ACTION;
		}
		updateDoButtonText (tDoButtonAction);
		setActionForCurrentPlayer ();
		updatePassButton ();
	}

	public void setFrameLabel (String aGameName, int aRoundID) {
		setFrameLabel (aGameName, " " + aRoundID);
	}

	public void setFrameLabel (String aGameName, String aIDLabel) {
		String tRoundName;
		String tFrameTitle;

		tRoundName = roundManager.getRoundName ();
		tFrameTitle = aGameName + " " + tRoundName + aIDLabel;
		frameLabel.setText (tFrameTitle);
		revalidate ();
	}

	public void setAuctionRound (String aGameName, int aRoundID) {
		setFrameLabel (aGameName, aRoundID);
		updateDoButton (PLAYER_DO_AUCTION, PLAYER_AUCTION_ACTION);
		updatePassButton ();
	}

	public void setFormationRound (String aGameName, int aRoundID) {
		setFrameLabel (aGameName, aRoundID);
		updateDoButton (PLAYER_DO_FORMATION, PLAYER_FORMATION_ACTION);
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

	public void setStockRoundInfo (String aGameName, String aRoundID) {
		setFrameLabel (aGameName, " " + aRoundID);
		updateDoButton (PLAYER_DO_STOCK, PLAYER_ACTION);
		playersInfoPanel.setCurrentPlayerText ();
		updateTotalCashLabel ();
		updatePassButton ();
		fillFastBuyPanel ();
	}

	public void updatePassButton () {
		String tClientUserName;
		String tCurrentPlayerName;
		GameManager tGameManager;
		Player tCurrentPlayer;

		if (passButton != GUI.NO_BUTTON) {
			if (roundManager.isAOperatingRound ()) {
				disablePassButton (IS_OPERATING_ROUND);
			} else if (roundManager.isAAuctionRound ()) {
				disablePassButton (IS_AUCTION_ROUND);
			} else if (roundManager.isAFormationRound ()) {
				disablePassButton (IS_FORMATION_ROUND);
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
		String tClientUserName;
		String tCurrentPlayerName;
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
	
	public void updateAllCorporationsJPanel () {
		allCorporationsPanel.updateAllCorporationsJPanel ();
	}

	public void enableActionButton (boolean aEnableActionButton) {
		doButton.setEnabled (aEnableActionButton);
		if (aEnableActionButton) {
			doButton.setToolTipText (GUI.EMPTY_STRING);
		} else {
			doButton.setToolTipText (YOU_NOT_PRESIDENT);
		}
		revalidate ();
	}
	
	public void updateAll () {
		updateFrameTitle ();
		updateTotalCashLabel ();
		updateGameStateLabel ();
		updatePhaseLabel ();
		updatePassButton ();
		setFrameBackgrounds ();
		revalidate ();
		repaint ();
	}

	public void updateAllListenerPanels () {
		for (ListenerPanel tListenerPanel : listenerPanels) {
			tListenerPanel.updatePanel ();
		}
	}
	
	public void setFrameBackgrounds () {
		GameManager tGameManager;
		String tClientUserName;
		String tCurrentPlayerName;
		String tOperatingOwnerName;

		tGameManager = roundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tClientUserName = tGameManager.getClientUserName ();
			if (roundManager.isAStockRound ()) {
				tCurrentPlayerName = playersInfoPanel.getCurrentPlayerName ();
				if (tCurrentPlayerName.equals (tClientUserName)) {
					setAlertBackgrounds ();
				} else {
					resetBackgrounds ();
				}
			} else if (roundManager.isAOperatingRound ()) {
				tOperatingOwnerName = roundManager.getOwnerWhoWillOperate ();
				if (tOperatingOwnerName != Player.NO_NAME) {
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
		if (fastBuyJPanel != GUI.NO_PANEL) {
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
			passButton.setToolTipText (GUI.EMPTY_STRING);
		}
	}
}
