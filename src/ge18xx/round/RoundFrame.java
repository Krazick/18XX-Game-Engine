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
import java.awt.Container;
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
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.apache.logging.log4j.Logger;

public class RoundFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = "\n";
	private static final String PASS_STOCK_TEXT = "Pass in Stock Round";
	private static final String SHOW_GE_FRAME_ACTION = "showGEFrame";
	private static final String PASS_STOCK_ACTION = "passStockAction";
	private static final String PLAYER_ACTION = "DoPlayerAction";
	private static final String PLAYER_AUCTION_ACTION = "DoPlayerAuctionAction";
	private static final String CORPORATION_ACTION = "DoCorporationAction";
	private static final String PLAYER_CONTAINER_LABEL = "Player Order and Last Action";
	private static final String YOU_NOT_PRESIDENT = "You are not the President of the Company";
	private static final String NOT_YOUR_TURN = "It is not your turn to Perform the Action";
	private static final String IS_OPERATING_ROUND = "It is an Operating Round, can't Pass";
	RoundManager roundManager;
	Container centerBox;
	Container roundBox;
	Container allCorporationsBox;
	Container buttonsBox;
	JPanel headerBox;
	JPanel parPricesBox;
	JPanel trainSummaryBox;
	JPanel roundInfoBox;
	JLabel frameLabel;
	JLabel phaseLabel;
	JPanel playersContainer;
	JLabel totalCashLabel;
	JButton passActionButton;
	JButton doActionButton;
	JButton showGameEngineFrameButton;
	Color defaultColor;
	JLabel parPriceLabel;
	List<JLabel> parPrices = new LinkedList<JLabel> ();
	List<JLabel> companiesAtPar = new LinkedList<JLabel> ();
	List<Container> parPriceLineBoxes = new LinkedList<Container> ();
	JTextArea trainSummary;
	Logger logger;
	long previousWhen;
	
	public RoundFrame (String aFrameName, RoundManager aRoundManager, String aGameName) {
		super (aFrameName, aGameName);
		
		int tTotalCash;
		
		roundManager = aRoundManager;
		logger = Game_18XX.getLogger ();
		
		roundBox = Box.createVerticalBox ();
		
		headerBox = new JPanel ();
		parPricesBox = new JPanel ();
		roundInfoBox = new JPanel ();
		trainSummaryBox = new JPanel ();
		
		fillParPrices ();
		updateParPrices ();
		trainSummary = new JTextArea ("");
		updateTrainSummary ();
		trainSummaryBox.add (trainSummary);
		
		headerBox.setLayout (new BoxLayout (headerBox, BoxLayout.X_AXIS));
		headerBox.setAlignmentY (Component.TOP_ALIGNMENT);
		headerBox.add (parPricesBox);
		headerBox.add (Box.createHorizontalGlue ());
		roundInfoBox.setLayout (new BoxLayout (roundInfoBox, BoxLayout.Y_AXIS));
		roundInfoBox.setAlignmentX (Component.CENTER_ALIGNMENT);
		headerBox.add (roundInfoBox);
		headerBox.add (Box.createHorizontalGlue ());
		headerBox.add (trainSummaryBox);
		
		allCorporationsBox = Box.createVerticalBox ();
		
		roundInfoBox.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Round");
		frameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoBox.add (frameLabel);
		roundInfoBox.add (Box.createVerticalStrut (10));
		
		Bank tBank = roundManager.getBank ();
		tBank.updateBankCashLabel ();
		JLabel tBankCashLabel = tBank.getBankCashLabel ();
		tBankCashLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoBox.add (tBankCashLabel);
		roundInfoBox.add (Box.createVerticalStrut (10));
		
		tTotalCash = roundManager.getTotalCash ();
		totalCashLabel = new JLabel ("Total Cash: " + Bank.formatCash (tTotalCash));
		totalCashLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoBox.add (totalCashLabel);
		
		phaseLabel = new JLabel ("Current Game Phase");
		phaseLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundInfoBox.add (phaseLabel);
		roundInfoBox.add (Box.createVerticalStrut (10));
		updatePhaseLabel ();
		
		roundBox.add (headerBox);
		
		buildPlayersContainer ();
		roundBox.add (playersContainer);
		
		roundBox.add (Box.createVerticalStrut (10));

		updateAllCorporationsBox ();

		roundBox.add (allCorporationsBox);
		roundBox.add (Box.createVerticalStrut (10));
		setStockRound (roundManager.getGameName (), roundManager.getStockRoundID ());
		
		buttonsBox = Box.createHorizontalBox ();
		
		passActionButton = new JButton (PASS_STOCK_TEXT);
		passActionButton.setActionCommand (PASS_STOCK_ACTION);
		passActionButton.addActionListener (this);
		passActionButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		
		buttonsBox.add (doActionButton);
		buttonsBox.add (Box.createHorizontalStrut(20));
		buttonsBox.add (passActionButton);
		buttonsBox.add (Box.createHorizontalStrut(20));
	
		showGameEngineFrameButton = new JButton ("Show Game Engine Frame");
		showGameEngineFrameButton.setActionCommand (SHOW_GE_FRAME_ACTION);
		showGameEngineFrameButton.addActionListener (this);
		showGameEngineFrameButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		buttonsBox.add (showGameEngineFrameButton);
		
		roundBox.add (buttonsBox);
		roundBox.add (Box.createVerticalStrut (10));
		
		centerBox = Box.createHorizontalBox();
		centerBox.add (Box.createHorizontalStrut(20));
		centerBox.add (roundBox);
		centerBox.add (Box.createHorizontalStrut(20));

		add (centerBox);
		pack ();
		defaultColor = UIManager.getColor ("Panel.background");
		setPreviousWhen (0);
	}

	private void setPreviousWhen (long aWhen) {
		previousWhen = aWhen;
	}
	
	private void updateTrainSummary () {
		String tFullTrainSummary;
		String tBankPoolTrainSummary;
		Bank tBank;
		BankPool tBankPool;
		
		trainSummary.setEditable (false);
		tBankPool = roundManager.getBankPool ();
		tBankPoolTrainSummary = getTrainSummary (tBankPool);
		tBank = roundManager.getBank ();
		tFullTrainSummary = tBankPoolTrainSummary +  NEWLINE + getTrainSummary (tBank);
		
		trainSummary.setText (tFullTrainSummary);
		trainSummary.setBackground (defaultColor);
	}

	public String getTrainSummary (GameBank aBankWithTrains) {
		String tBankPoolTrainSummary = "";
		
		if (aBankWithTrains.hasAnyTrains ()) {
			tBankPoolTrainSummary = aBankWithTrains.getName () + " Train Summary" + NEWLINE + 
									aBankWithTrains.getTrainSummary ();
		}
		
		return tBankPoolTrainSummary;
	}
	
	private void fillParPrices () {
		int tParPriceCount, tParPriceIndex, tIndex, tPrice;
		Container tParPriceLineBox;
		Integer [] tParPrices;
		GameManager tGameManager;
		
		tGameManager = roundManager.getGameManager ();
		tParPrices = tGameManager.getAllStartCells ();
		tParPriceCount = tParPrices.length;
		if (tParPriceCount > 0) {
			parPricesBox.setLayout (new BoxLayout (parPricesBox, BoxLayout.Y_AXIS));
			parPricesBox.setAlignmentX (Component.LEFT_ALIGNMENT);
			parPricesBox.setMinimumSize(new Dimension (150, 150));
			parPricesBox.setMaximumSize (new Dimension (200, 160));
			parPricesBox.setBorder (BorderFactory.createTitledBorder ("Par Prices"));
			String tPrices [] = new String [tParPriceCount];
			for (tIndex = 0; tIndex < tParPrices.length; tIndex++) {
				tPrice = tParPrices [tIndex].intValue ();
				tPrices [tIndex] = Bank.formatCash (tPrice);
			}
			
			for (tParPriceIndex = 0; tParPriceIndex < tParPriceCount; tParPriceIndex++) {
				parPrices.add (new JLabel (tPrices [tParPriceIndex]) );
				companiesAtPar.add (new JLabel ("B&O"));
				tParPriceLineBox = Box.createHorizontalBox ();
				tParPriceLineBox.add (parPrices.get (tParPriceIndex));
				tParPriceLineBox.add (Box.createHorizontalStrut (10));
				tParPriceLineBox.add (companiesAtPar.get (tParPriceIndex));
				parPriceLineBoxes.add (tParPriceLineBox);
				parPricesBox.add (parPriceLineBoxes.get (tParPriceIndex));
			}
			parPricesBox.setVisible (false);
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		long tWhen;
		
		if (CORPORATION_ACTION.equals (aEvent.getActionCommand ())) {
			if (! roundManager.companyStartedOperating ()) {
				logger.info ("Corporation Action for Operation Round selected");
				roundManager.prepareCorporation ();
			}
			roundManager.showCurrentCompanyFrame ();
		}
		if (PLAYER_ACTION.equals (aEvent.getActionCommand ())) {
			// TODO  --- KLUDGE, if current Event is 500ms (0.5 Sec) or more since last Player Action, handle it
			// Otherwise consider this a "double-click". To prevent Player Frame from taking longer and longer to process.
			tWhen = aEvent.getWhen ();
			if ((previousWhen + 500) < tWhen) {
				System.out.println ("Round Frame Player Action Selected - Show Current Player Frame When (" + tWhen + ")");
				roundManager.showCurrentPlayerFrame ();
				setPreviousWhen (tWhen);
			}
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
	
	private void buildPlayersContainer () {
		StockRound tStockRound;

		tStockRound = roundManager.getStockRound ();
		playersContainer = new JPanel ();
		playersContainer.setBorder (BorderFactory.createTitledBorder (PLAYER_CONTAINER_LABEL));
		BoxLayout tLayout = new BoxLayout (playersContainer, BoxLayout.X_AXIS);
		playersContainer.setLayout (tLayout);
		playersContainer.add (Box.createHorizontalStrut (10));
		fillPlayersContainer (tStockRound);
		playersContainer.add (Box.createVerticalStrut (20));
	}

	public void fillPlayersContainer (StockRound aStockRound) {
		int tPlayerIndex;
		Player tPlayer;
		Container tPlayerContainer;
		int tPlayerCount, tCurrentPlayer, tPriorityPlayer;
		
		tCurrentPlayer = aStockRound.getCurrentPlayerIndex ();
		tPlayerCount = aStockRound.getPlayerCount ();
		tPriorityPlayer = aStockRound.getPriorityIndex ();
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tPlayer = aStockRound.getPlayerAtIndex (tPlayerIndex);
			if (tPlayer != Player.NO_PLAYER) {
				tPlayerContainer = tPlayer.buildAPlayerContainer (tPriorityPlayer, tPlayerIndex);
				playersContainer.add (tPlayerContainer);
				playersContainer.add (Box.createHorizontalStrut (10));
			} else {
				logger.error ("No Player Found for " + tPlayerIndex);
			}
			if (tCurrentPlayer == tPlayerIndex) {
				setCurrentPlayerText (tPlayer.getName ());
			} 
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
	
	public void setActionButton (String aButtonLabel, String aActionCommand) {
		if (doActionButton == null) {
			doActionButton = new JButton ("");
		}
		updateActionButtonText (aButtonLabel);
		doActionButton.setAlignmentX (CENTER_ALIGNMENT);
		doActionButton.setActionCommand (aActionCommand);
		doActionButton.addActionListener (this);
	}
	
	public void setAuctionRound (String aGameName, int aRoundID) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundID);
		setActionButton ("Do Auction Action", PLAYER_AUCTION_ACTION);
		disablePassButton ("In Auction Round, Can't Pass");
	}

	public void setOperatingRound (String aGameName, int aRoundIDPart1, int aCurrentOR, int aMaxOR) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundIDPart1 + " [" + aCurrentOR + " of " + aMaxOR + "]");
		setActionButton ("Do Company Action", CORPORATION_ACTION);
		updateTotalCashLabel ();
		disablePassButton ("In Operating Round, Can't Pass");
		passActionButton.setText (PASS_STOCK_TEXT);
	}
	
	public void setStockRound (String aGameName, int aRoundID) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundID);
		setActionButton ("Player do Stock Action", PLAYER_ACTION);
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
		JPanel tCompanyContainer;
		OperatingRound tOperatingRound;
		int tCorporationCount;
		CorporationList tCorporationList;

		tOperatingRound = roundManager.getOperatingRound ();
		tCorporationCount = tOperatingRound.getPrivateCompanyCount ();
		allCorporationsBox.removeAll ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getPrivateCompanies ();
			tCompanyContainer = tCorporationList.buildCompanyContainer (true);
			allCorporationsBox.add (tCompanyContainer);
			allCorporationsBox.add (Box.createVerticalStrut (10));
		}

		tCorporationCount = tOperatingRound.getCoalCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getCoalCompanies ();
			tCompanyContainer = tCorporationList.buildCompanyContainer (true);
			allCorporationsBox.add (tCompanyContainer);
			allCorporationsBox.add (Box.createVerticalStrut (10));
		}
		
		tCorporationCount = tOperatingRound.getMinorCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getMinorCompanies ();
			tCompanyContainer = tCorporationList.buildCompanyContainer (true);
			allCorporationsBox.add (tCompanyContainer);
			allCorporationsBox.add (Box.createVerticalStrut (10));
		}
		
		tCorporationCount = tOperatingRound.getShareCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getShareCompanies ();
			tCompanyContainer = tCorporationList.buildCompanyContainer (false);
			allCorporationsBox.add (tCompanyContainer);
			allCorporationsBox.add (Box.createVerticalStrut (10));
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
		boolean tAtLeastOneParPrice = false;
		
		tOperatingRound = roundManager.getOperatingRound ();		
		tCorporationCount = tOperatingRound.getShareCompanyCount ();
		
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getShareCompanies ();
			tPriceCount = companiesAtPar.size ();
			String tCompaniesAtPrice [] = new String [tPriceCount];
			for (tCorporationIndex = 0; tCorporationIndex < tCorporationCount; tCorporationIndex++) {
				tShareCompany = (ShareCompany) tCorporationList.getCorporation (tCorporationIndex);
				if (tShareCompany.hasParPrice ()) {
					tParPrice = Bank.formatCash (tShareCompany.getParPrice ());
					for (tPriceIndex = 0; tPriceIndex < tPriceCount; tPriceIndex++) {
						tPriceLabel = parPrices.get(tPriceIndex).getText ();
						if (tPriceLabel.equals (tParPrice)) {
							if (tCompaniesAtPrice [tPriceIndex] == null) {
								tCompaniesAtPrice [tPriceIndex] = tShareCompany.getAbbrev ();
							} else {
								tCompaniesAtPrice [tPriceIndex] += ", " + tShareCompany.getAbbrev ();
							}
							tAtLeastOneParPrice = true;
						}
					}
				}
			}

			for (tPriceIndex = 0; tPriceIndex < tPriceCount; tPriceIndex++) {
				companiesAtPar.get (tPriceIndex).setText (tCompaniesAtPrice [tPriceIndex]);
			}
			if (tAtLeastOneParPrice) {
				parPricesBox.setVisible (true);
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
		updateTotalCashLabel ();
		updatePhaseLabel ();
		updateAllCorporationsBox ();
		updateParPrices ();
		updateTrainSummary ();
		updatePassButton ();
	}
	
	public void setBackGround () {
		GameManager tGameManager;
		Color tAlertColor = Color.ORANGE;
		
		tGameManager = roundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			getContentPane ().setBackground (tAlertColor);
			headerBox.setBackground (tAlertColor);
			parPricesBox.setBackground (tAlertColor);
			trainSummaryBox.setBackground (tAlertColor);
		}
	}

	public void resetBackGround () {
		getContentPane ().setBackground (defaultColor);	
		headerBox.setBackground (defaultColor);
		parPricesBox.setBackground (defaultColor);
		trainSummaryBox.setBackground (defaultColor);
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
