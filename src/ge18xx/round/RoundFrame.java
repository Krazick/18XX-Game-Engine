package ge18xx.round;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.Game_18XX;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.toplevel.XMLFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.Logger;

public class RoundFrame extends XMLFrame implements ActionListener {
	public static final RoundFrame NO_ROUND_FRAME = null;
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = "\n";
	private static final String PASS_STOCK_TEXT = "Pass in Stock Round";
	private static final String SHOW_GE_FRAME_ACTION = "showGEFrame";
	private static final String PASS_STOCK_ACTION = "passStockAction";
	private static final String PLAYER_ACTION = "DoPlayerAction";
	private static final String PLAYER_AUCTION_ACTION = "DoPlayerAuctionAction";
	private static final String CORPORATION_ACTION = "DoCorporationAction";
	private static final String PLAYER_JPANEL_LABEL = "Player Information";
	private static final String YOU_NOT_PRESIDENT = "You are not the President of the Company";
	private static final String NOT_YOUR_TURN = "It is not your turn to Perform the Action";
	private static final String IS_OPERATING_ROUND = "It is an Operating Round, can't Pass";
	RoundManager roundManager;
	JPanel roundJPanel;
	JPanel allCorporationsJPanel;
	JPanel buttonsJPanel;
	JPanel headerJPanel;
	JPanel parPricesJPanel;
	JPanel trainSummaryJPanel;
	JPanel roundInfoJPanel;
	JPanel playersJPanel;
	JScrollPane roundScrollPane;
	JButton passActionButton;
	JButton doActionButton;
	JButton showGameEngineFrameButton;
	JTextArea trainSummary;
	JLabel frameLabel;
	JLabel phaseLabel;
	JLabel totalCashLabel;
	JLabel parPriceLabel;
	List<JLabel> parPrices = new LinkedList<JLabel> ();
	List<JLabel> companiesAtPar = new LinkedList<JLabel> ();
	List<JPanel> parPriceLineJPanels = new LinkedList<JPanel> ();
	Color defaultColor;
	Logger logger;
	int padding1;
	int padding2;
	
	public RoundFrame (String aFrameName, RoundManager aRoundManager, String aGameName) {
		super (aFrameName, aGameName);
		
		roundManager = aRoundManager;
		logger = Game_18XX.getLogger ();
		
		buildRoundJPanel ();
		
		roundScrollPane = new JScrollPane (roundJPanel);
		add (roundScrollPane);
		pack ();
		
		setStockRound (aGameName, roundManager.getStockRoundID ());
		defaultColor = UIManager.getColor ("Panel.background");
	}

	private void buildRoundJPanel () {
		padding1 = 10;
		padding2 = 5;
		
		roundJPanel = new JPanel ();
		roundJPanel.setLayout (new BoxLayout (roundJPanel, BoxLayout.Y_AXIS));
		roundJPanel.setBorder (BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1));
		
		buildHeaderJPanel();
		roundJPanel.add (headerJPanel);
		roundJPanel.add (Box.createVerticalGlue ());
		
		buildPlayersJPanel ();
		roundJPanel.add (playersJPanel);
		roundJPanel.add (Box.createVerticalGlue ());

		buildAllCorporationsJPanel ();
		roundJPanel.add (allCorporationsJPanel);
		roundJPanel.add (Box.createVerticalGlue ());
		
		buildButtonsJPanel();
		roundJPanel.add (buttonsJPanel);
		roundJPanel.add (Box.createVerticalGlue ());
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
		int tParPriceCount, tParPriceIndex, tPrice;
		JPanel tParPriceLineBox;
		Integer [] tParPrices;
		GameManager tGameManager;
		String tPrices [];
		Border tBorder1, tBorder2;
		
		tGameManager = roundManager.getGameManager ();
		tParPrices = tGameManager.getAllStartCells ();
		tParPriceCount = tParPrices.length;
		
		parPricesJPanel = new JPanel ();
		parPricesJPanel.setLayout (new BoxLayout (parPricesJPanel, BoxLayout.Y_AXIS));
		tBorder1 = BorderFactory.createLineBorder (Color.BLACK);
		tBorder2 = BorderFactory.createTitledBorder (tBorder1, "Par Prices", TitledBorder.CENTER, TitledBorder.TOP);
		parPricesJPanel.setBorder (tBorder2);
		
		tPrices = new String [tParPriceCount];
		for (tParPriceIndex = 0; tParPriceIndex < tParPriceCount; tParPriceIndex++) {
			tPrice = tParPrices [tParPriceIndex].intValue ();
			tPrices [tParPriceIndex] = Bank.formatCash (tPrice);
			parPrices.add (new JLabel (tPrices [tParPriceIndex]));
			companiesAtPar.add (new JLabel (""));
			
			tParPriceLineBox = new JPanel ();
			tParPriceLineBox.setLayout (new BoxLayout (tParPriceLineBox, BoxLayout.X_AXIS));
			tParPriceLineBox.add (Box.createHorizontalStrut (20));
			tParPriceLineBox.add (parPrices.get (tParPriceIndex));
			tParPriceLineBox.add (Box.createHorizontalStrut (10));
			tParPriceLineBox.add (companiesAtPar.get (tParPriceIndex));
			tParPriceLineBox.add (Box.createHorizontalStrut (20));
			parPriceLineJPanels.add (tParPriceLineBox);
			parPricesJPanel.add (parPriceLineJPanels.get (tParPriceIndex));
		}
		updateParPrices ();
	}

	private void buildRoundInfoJPanel () {
		int tTotalCash;
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
		
		phaseLabel = new JLabel ("Current Game Phase");
		phaseLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoJPanel.add (Box.createVerticalStrut (10));
		roundInfoJPanel.add (phaseLabel);
		roundInfoJPanel.add (Box.createVerticalStrut (10));
		updatePhaseLabel ();
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
		tBankTrainSummary  = getTrainSummary (tBank);
		tFullTrainSummary = tBankPoolTrainSummary +  NEWLINE + tBankTrainSummary;
		
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

	public void updateAllPlayerJPanels () {
		int tPlayerIndex;
		Player tPlayer;
		JPanel tPlayerJPanel;
		int tPlayerCount, tPriorityPlayer;
		StockRound tStockRound;

		tStockRound = roundManager.getStockRound ();
		tPlayerCount = tStockRound.getPlayerCount ();
		tPriorityPlayer = tStockRound.getPriorityIndex ();
		playersJPanel.removeAll ();
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
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
	
	public void updateCurrentPlayerText () {
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

		setupActionButton ("Player do Stock Action", PLAYER_ACTION);
		passActionButton = new JButton (PASS_STOCK_TEXT);
		passActionButton.setActionCommand (PASS_STOCK_ACTION);
		passActionButton.addActionListener (this);
		passActionButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		
		buttonsJPanel.add (doActionButton);
		buttonsJPanel.add (Box.createHorizontalStrut(20));
		buttonsJPanel.add (passActionButton);
		buttonsJPanel.add (Box.createHorizontalStrut(20));
		
		showGameEngineFrameButton = new JButton ("Show Game Engine Frame");
		showGameEngineFrameButton.setActionCommand (SHOW_GE_FRAME_ACTION);
		showGameEngineFrameButton.addActionListener (this);
		showGameEngineFrameButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		buttonsJPanel.add (showGameEngineFrameButton);
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		if (CORPORATION_ACTION.equals (aEvent.getActionCommand ())) {
			if (! roundManager.companyStartedOperating ()) {
				logger.info ("Corporation Action for Operation Round selected");
				roundManager.prepareCorporation ();
			}
			roundManager.showCurrentCompanyFrame ();
		}
		if (PLAYER_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.showCurrentPlayerFrame ();
		}
		if (PLAYER_AUCTION_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.showCurrentPlayerFrame ();
			roundManager.showAuctionFrame ();
		}
		if (SHOW_GE_FRAME_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.showGEFrame ();
		}
		if (PASS_STOCK_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.passStockAction ();
			updateAllCorporationsBox ();
		}
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
		if (passActionButton != null) {
			passActionButton.setText (aPlayerName + " " + PASS_STOCK_TEXT);
		}
		updateActionButtonText (aPlayerName + " do Stock Action");
		setActionForCurrentPlayer ();
		updatePassButton ();
	}

	public void setFrameLabel (String aGameName, String aIDLabel) {
		String tRoundType;
		
		tRoundType = roundManager.getRoundType ();
		frameLabel.setText (aGameName + " " + tRoundType + aIDLabel);
		revalidate ();
	}
	
	public void setupActionButton (String aButtonLabel, String aActionCommand) {
		doActionButton = new JButton (aButtonLabel);
		doActionButton.setAlignmentX (CENTER_ALIGNMENT);
		doActionButton.addActionListener (this);			
		updateActionButton (aButtonLabel, aActionCommand);
	}
	
	public void updateActionButton (String aButtonLabel, String aActionCommand) {
		updateActionButtonText (aButtonLabel);
		doActionButton.setActionCommand (aActionCommand);
	}
	
	public void setAuctionRound (String aGameName, int aRoundID) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundID);
		updateActionButton ("Do Auction Action", PLAYER_AUCTION_ACTION);
		disablePassButton ("In Auction Round, Can't Pass");
	}

	public void setOperatingRound (String aGameName, int aRoundIDPart1, int aCurrentOR, int aMaxOR) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundIDPart1 + " [" + aCurrentOR + " of " + aMaxOR + "]");
		updateActionButton ("Do Company Action", CORPORATION_ACTION);
		updateTotalCashLabel ();
		disablePassButton ("In Operating Round, Can't Pass");
		passActionButton.setText (PASS_STOCK_TEXT);
	}
	
	public void setStockRound (String aGameName, int aRoundID) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundID);
		updateActionButton ("Player do Stock Action", PLAYER_ACTION);
		setCurrentPlayerText ();
		updateTotalCashLabel ();
		updatePassButton ();
	}
	
	public void updatePassButton () {
		String tClientUserName, tCurrentPlayerName;
		GameManager tGameManager;
		
		if (passActionButton != null) {
			if (roundManager.isOperatingRound ()) {
				disablePassButton (IS_OPERATING_ROUND);
			} else {
				tGameManager = roundManager.getGameManager ();
				if (tGameManager.isNetworkGame ()) {
					tCurrentPlayerName = getCurrentPlayerName ();
					tClientUserName = tGameManager.getClientUserName ();
					if (tCurrentPlayerName.equals (tClientUserName)) {
						verifyMustActions (tGameManager);
						setBackGround ();
					} else {
						disablePassButton (NOT_YOUR_TURN);
						resetBackGround ();
					}
				} else {
					verifyMustActions (tGameManager);
				}
			}
		}
	}

	public void verifyMustActions (GameManager aGameManager) {
		PlayerFrame tPlayerFrame;
		Player tCurrentPlayer;
		String tToolTip;
		
		tPlayerFrame = aGameManager.getCurrentPlayerFrame ();
		if (tPlayerFrame.hasMustBuyCertificate ()) {
			disablePassButton (PlayerFrame.MUST_BUY_PRIVATE);
		} else if (tPlayerFrame.mustSellStock ()){
			tCurrentPlayer = aGameManager.getPlayerManager().getCurrentPlayer ();
			tToolTip = tPlayerFrame.getMustSellToolTip (tCurrentPlayer);
			disablePassButton (tToolTip);
		} else {
			enablePassButton ();
		}
	}
	
	public void setActionForCurrentPlayer () {
		String tClientUserName, tCurrentPlayerName;
		GameManager tGameManager;
		
		tGameManager = roundManager.getGameManager ();
		if (doActionButton != null) {
			if (tGameManager.isNetworkGame ()) {
				tCurrentPlayerName = getCurrentPlayerName ();
				tClientUserName = tGameManager.getClientUserName ();
				if (tCurrentPlayerName.equals (tClientUserName)) {
					doActionButton.setEnabled (true);
					doActionButton.setToolTipText ("");
					setBackGround ();
				} else {
					doActionButton.setEnabled (false);
					doActionButton.setToolTipText (NOT_YOUR_TURN);
					resetBackGround ();
				}
			}			
		}
	}
	
	public void updateActionButtonText (String aNewLabel) {
		if (doActionButton != null) {
			doActionButton.setText (aNewLabel);
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
	
	public void updateParPrices () {
		OperatingRound tOperatingRound;
		int tCorporationCount, tCorporationIndex;
		int tPriceCount, tPriceIndex;
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
					for (tPriceIndex = 0; tPriceIndex < tPriceCount; tPriceIndex++) {
						tPriceLabel = parPrices.get (tPriceIndex).getText ();
						if (tPriceLabel.equals (tParPrice)) {
							if (tCompaniesAtPrice [tPriceIndex] == null) {
								tCompaniesAtPrice [tPriceIndex] = tShareCompany.getAbbrev ();
							} else {
								tCompaniesAtPrice [tPriceIndex] += ", " + tShareCompany.getAbbrev ();
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
		doActionButton.setEnabled (aEnableActionButton);
		if (aEnableActionButton) {
			doActionButton.setToolTipText ("");
			setBackGround ();
		} else {
			doActionButton.setToolTipText (YOU_NOT_PRESIDENT);
			resetBackGround ();
		}
		revalidate ();
	}
	
	private void updateTotalCashLabel () {
		int tTotalCash;
		
		tTotalCash = roundManager.getTotalCash ();
		totalCashLabel.setText ("Total Cash: " + Bank.formatCash (tTotalCash));
	}
	
	public void updateAll () {
		updateParPrices ();
		updateTotalCashLabel ();
		updatePhaseLabel ();
		updateTrainSummary ();
		updateAllPlayerJPanels ();
		updateAllCorporationsBox ();
		updatePassButton ();
	}
	
	public void setBackGround () {
		GameManager tGameManager;
		Color tAlertColor = Color.ORANGE;
		
		tGameManager = roundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			getContentPane ().setBackground (tAlertColor);
			headerJPanel.setBackground (tAlertColor);
			parPricesJPanel.setBackground (tAlertColor);
			trainSummaryJPanel.setBackground (tAlertColor);
			buttonsJPanel.setBackground (tAlertColor);
			roundJPanel.setBackground (tAlertColor);
		}
	}

	public void resetBackGround () {
		getContentPane ().setBackground (defaultColor);	
		headerJPanel.setBackground (defaultColor);
		parPricesJPanel.setBackground (defaultColor);
		trainSummaryJPanel.setBackground (defaultColor);
		buttonsJPanel.setBackground (defaultColor);
		roundJPanel.setBackground (defaultColor);
	}
	
	public void disablePassButton (String aToolTip) {
		if (passActionButton != null) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (aToolTip);
		}
	}
	
	public void enablePassButton () {
		if (passActionButton != null) {
			passActionButton.setEnabled (true);
			passActionButton.setToolTipText ("");
		}
	}
}
