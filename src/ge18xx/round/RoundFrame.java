package ge18xx.round;

import ge18xx.bank.Bank;
import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.toplevel.XMLFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class RoundFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String PLAYER_ACTION = "DoPlayerAction";
	private static final String PLAYER_AUCTION_ACTION = "DoPlayerAuctionAction";
	private static final String CORPORATION_ACTION = "DoCorporationAction";
	private static final String PLAYER_CONTAINER_LABEL = "Player Order and Last Action";
	private static final String YOU_NOT_PRESIDENT = "You are not the President of the Company";
	RoundManager roundManager;
	Container centerBox;
	Container roundBox;
	Container allCorporationsBox;
	JLabel frameLabel;
	JLabel phaseLabel;
	JPanel playersContainer;
	JLabel totalCashLabel;
	JButton doActionButton;
	Color defaultColor;
	
	public RoundFrame (String aFrameName, RoundManager aRoundManager, String aGameName) {
		super (aFrameName, aGameName);
		
		int tTotalCash;
		
		roundManager = aRoundManager;
		
		roundBox = Box.createVerticalBox ();
		allCorporationsBox = Box.createVerticalBox ();
		
		roundBox.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Round");
		frameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundBox.add (frameLabel);
		roundBox.add (Box.createVerticalStrut (10));
		
		Bank tBank = roundManager.getBank ();
		tBank.updateBankCashLabel ();
		JLabel tBankCashLabel = tBank.getBankCashLabel ();
		tBankCashLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundBox.add (tBankCashLabel);
		roundBox.add (Box.createVerticalStrut (10));
		
		tTotalCash = roundManager.getTotalCash ();
		totalCashLabel = new JLabel ("Total Cash: " + Bank.formatCash (tTotalCash));
		totalCashLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundBox.add (totalCashLabel);
		
		phaseLabel = new JLabel ("Current Game Phase");
		phaseLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		roundBox.add (phaseLabel);
		roundBox.add (Box.createVerticalStrut (10));
		updatePhaseLabel ();
		
		buildPlayersContainer ();
		roundBox.add (playersContainer);
		
		roundBox.add (Box.createVerticalStrut (10));

		updateAllCorporationsBox ();

		roundBox.add (allCorporationsBox);
		roundBox.add (Box.createVerticalStrut (10));
		setStockRound (roundManager.getGameName (), roundManager.getStockRoundID ());
		roundBox.add (doActionButton);
		roundBox.add (Box.createVerticalStrut (10));
	
		centerBox = Box.createHorizontalBox();
		centerBox.add (Box.createHorizontalStrut(20));
		centerBox.add (roundBox);
		centerBox.add (Box.createHorizontalStrut(20));

		add (centerBox);
		pack ();
		defaultColor = UIManager.getColor ( "Panel.background" );
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		if (CORPORATION_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.showCurrentCompanyFrame ();
		}
		if (PLAYER_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.showCurrentPlayerFrame ();
		}
		if (PLAYER_AUCTION_ACTION.equals (aEvent.getActionCommand ())) {
			roundManager.showCurrentPlayerFrame ();
			roundManager.showAuctionFrame ();
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
				System.err.println ("No Player Found for " + tPlayerIndex);
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
		updateActionButtonText (aPlayerName + " do Stock Action");
		setActionForCurrentPlayer ();
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
	}

	public void setOperatingRound (String aGameName, int aRoundIDPart1, int aCurrentOR, int aMaxOR) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundIDPart1 + " [" + aCurrentOR + " of " + aMaxOR + "]");
		setActionButton ("Do Company Action", CORPORATION_ACTION);
		updateTotalCashLabel ();
	}
	
	public void setStockRound (String aGameName, int aRoundID) {
		resetBackGround ();
		setFrameLabel (aGameName, " " + aRoundID);
		setActionButton ("Player do Stock Action", PLAYER_ACTION);
		setCurrentPlayerText ();
		updateTotalCashLabel ();
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
					doActionButton.setToolTipText ("It is not your turn to Perform the Action");
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
	}
	
	public void setBackGround () {
		GameManager tGameManager;

		tGameManager = roundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			getContentPane ().setBackground (Color.CYAN);
		}
	}
	
	public void resetBackGround () {
		getContentPane ().setBackground (defaultColor);		
	}
}
