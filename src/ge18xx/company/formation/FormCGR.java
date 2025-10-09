package ge18xx.company.formation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import ge18xx.round.action.ChangeStateAction;
import geUtilities.GUI;

public class FormCGR extends FormCompany implements ActionListener {
	public static final AttributeName AN_SHARE_FOLD_COUNT = new AttributeName ("shareFoldCount");
	public static final AttributeName AN_ALL_PLAYER_SHARES_HANDLED = new AttributeName ("allPlayerSharesHandled");
	public static final AttributeName AN_NOTITIFCATION_TEXT = new AttributeName ("notificationText");
	public static final AttributeName AN_HOME_TOKENS_EXCHANGED = new AttributeName ("homeTokensExchanged");
	public static final AttributeName AN_NON_HOME_TOKENS_EXCHANGED = new AttributeName ("nonHomeTokensExchanged");
	public static final String TIME_TO_REPAY = "Time to repay company outstanding Loans or Fold";
	public static final String NOT_CURRENT_PLAYER = "You are not the current President";
	public static final String NO_OPEN_MARKET_CERTS = "NO CERTIFICATES IN OPEN MARKET";
	public static final String CONTINUE = "Continue";
	public static final String FOLD = "Fold";
	public static final String TOKEN_EXCHANGE = "TokenExchange";
	public static final String ASSET_COLLECTION = "AssetCollection";
	public static final String STOCK_VALUE_CALCULATION = "StockValueCalculation";
	public static final int SHARES_NEEDED_FOR_2ND_ISSUE = 21;
	
	int shareFoldCount;

	boolean currentPlayerDone;
	boolean formingPresidentAssigned;
	boolean homeTokensExchanged;
	boolean nonHomeTokensExchanged;

	JPanel bottomJPanel;
	JPanel openMarketJPanel;
	JPanel ipoJPanel;
	
	public FormCGR (GameManager aGameManager) {
		super (aGameManager);
		String tFullFrameTitle;
		
		tFullFrameTitle = setFormationState (ActorI.ActionStates.LoanRepayment);
		
		setNotificationText (TIME_TO_REPAY);
		setAllPlayerSharesHandled (false);
		setHomeTokensExchanged (false);
		setNonHomeTokensExchanged (false);

		buildAllPlayers (tFullFrameTitle);
		setShareFoldCount (0);

		gameManager.setTriggerClass (this);
		gameManager.setTriggerFormation (this);
	}
	
	public FormCGR (XMLNode aXMLNode, GameManager aGameManager) {
		this (aGameManager);

		int tShareFoldCount;
		boolean tAllPlayerSharesHandled;
		boolean tHomeTokensExchanged;
		boolean tNonHomeTokensExchanged;
		String tNotificationText;
		
		parseXML (aXMLNode);
		tShareFoldCount = aXMLNode.getThisIntAttribute (AN_SHARE_FOLD_COUNT);
		tHomeTokensExchanged = aXMLNode.getThisBooleanAttribute (AN_HOME_TOKENS_EXCHANGED);
		tNonHomeTokensExchanged = aXMLNode.getThisBooleanAttribute (AN_NON_HOME_TOKENS_EXCHANGED);
		tAllPlayerSharesHandled = aXMLNode.getThisBooleanAttribute (AN_ALL_PLAYER_SHARES_HANDLED);
		tNotificationText = aXMLNode.getThisAttribute (AN_NOTITIFCATION_TEXT);
		
		setShareFoldCount (tShareFoldCount);
		setAllPlayerSharesHandled (tAllPlayerSharesHandled);
		setHomeTokensExchanged (tHomeTokensExchanged);
		setNonHomeTokensExchanged (tNonHomeTokensExchanged);
		setNotificationText (tNotificationText);
	}

	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		
		tXMLElement = super.addElements (aXMLDocument, aElementName);
		
		tXMLElement.setAttribute (AN_SHARE_FOLD_COUNT, shareFoldCount);
		tXMLElement.setAttribute (AN_ALL_PLAYER_SHARES_HANDLED, allPlayerSharesHandled);
		tXMLElement.setAttribute (AN_HOME_TOKENS_EXCHANGED, homeTokensExchanged);
		tXMLElement.setAttribute (AN_NON_HOME_TOKENS_EXCHANGED, nonHomeTokensExchanged);
		tXMLElement.setAttribute (AN_NOTITIFCATION_TEXT, notificationText);
		
		if (initialPlayer != ActorI.NO_ACTOR) {
			tXMLElement.setAttribute (AN_ACTING_PRESIDENT, initialPlayer.getName ());
		}

		return tXMLElement;
	}
	
	public void setHomeTokensExchanged (boolean aHomeTokenExchanged) {
		homeTokensExchanged = aHomeTokenExchanged;
	}

	public void setNonHomeTokensExchanged (boolean aNonHomeTokenExchanged) {
		nonHomeTokensExchanged = aNonHomeTokenExchanged;
	}
	
	public boolean getHomeTokensExchanged () {
		return homeTokensExchanged;
	}
	
	public boolean getNonHomeTokensExchanged () {
		return nonHomeTokensExchanged;
	}
	
	public void updateDoneButton () {
		
	}

	public void setShareFoldCount (int aCountToFold) {
		shareFoldCount = aCountToFold;
	}
	
	public void addShareFoldCount (int aShareCountToFold) {
		shareFoldCount += aShareCountToFold;
	}
	
	@Override
	public boolean haveSharesToFold () {
		return shareFoldCount > 0;
	}
	
	public boolean hasAssetsToCollect () {
		boolean tHasAssetsToCollect;
		
		tHasAssetsToCollect = true;
		
		return tHasAssetsToCollect;
	}
	
	public boolean hasStockValueToCalculate () {
		boolean tHasStockValueToCalculate;
		
		tHasStockValueToCalculate = true;
		
		return tHasStockValueToCalculate;
	}
	
	public boolean hasTokensToExchange () {
		boolean tHasTokensToExchange;
		int tCompanyIndex;
		int tCompanyCount;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		
		tHasTokensToExchange = false;
		if (formationState == ActorI.ActionStates.ShareExchange) {
			tShareCompanies = gameManager.getShareCompanies ();
			tCompanyCount = tShareCompanies.getRowCount ();
			for (tCompanyIndex = 0; tCompanyIndex < tCompanyCount; tCompanyIndex++) {
				tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tCompanyIndex);
				if (tShareCompany.willFold ()) {
					tHasTokensToExchange = true;
				}
			}
		}
		
		return tHasTokensToExchange;
	}

	@Override
	public void allPlayersHandled (ChangeStateAction aChangeStateAction) {
		String tNotification;
		String tFormingAbbrev;
		Player tCurrentPlayer;
		
		tNotification = GUI.EMPTY_STRING;
		
		tFormingAbbrev = formingShareCompany.getAbbrev ();
		if (formationState == ActorI.ActionStates.LoanRepayment) {
			if (! haveSharesToFold ()) {
				tNotification = String.format (NO_OUTSTANDING_LOANS, tFormingAbbrev);
				setNotificationText (tNotification);
			}
		} else if (formationState == ActorI.ActionStates.ShareExchange) {
			setAllPlayerSharesHandled (true);
			tCurrentPlayer = getCurrentPlayer ();
			aChangeStateAction.addSetAllPlayerSharesHandledEffect (tCurrentPlayer, allPlayerSharesHandled);
		}
		setNotificationText (tNotification);
		rebuildFormationPanel (currentPlayerIndex);
	}

	@Override
	public boolean ends () {
		boolean tEnds;
		boolean tAllRepaymentsFinished;
		PlayerFormationPanel tPlayerFormationPanel;
		LoanRepayment tLoanRepayment;
		
		tEnds = false;
		tPlayerFormationPanel = getPlayerFormationPanel ();
		if (tPlayerFormationPanel != PlayerFormationPanel.NO_PLAYER_FORMATION_PANEL) {
			if (tPlayerFormationPanel instanceof LoanRepayment) {
				tLoanRepayment = (LoanRepayment) tPlayerFormationPanel;
				tAllRepaymentsFinished = tLoanRepayment.allRepaymentsFinished ();
				if (tAllRepaymentsFinished) {
					if (! haveSharesToFold ()) {
						tEnds = true;
					}
				}
			} else if (tPlayerFormationPanel instanceof StockValueCalculation) {
				tEnds = true;
			}
		}
		
		return tEnds;
	}
	
	@Override
	public void rebuildFormationPanel () {
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = currentPlayerIndex;
		if (tCurrentPlayerIndex >= 0) {
			rebuildFormationPanel (tCurrentPlayerIndex);
		}
	}
	
	@Override
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		super.updatePlayers (aPlayers, aActingPresident);
		buildNotificationJPanel ();
		buildBottomJPanel ();
		formationJPanel.add (bottomJPanel);
		formationJPanel.repaint ();
	}

	public PlayerFormationPanel getPlayerFormationPanel () {
		PlayerFormationPanel tPlayerFormationPanel;
		Component tFirstComponent;
		
		tPlayerFormationPanel = PlayerFormationPanel.NO_PLAYER_FORMATION_PANEL;
		if (formationJPanel.getComponentCount () > 0) {
			tFirstComponent = formationJPanel.getComponent (0);
			if (tFirstComponent instanceof PlayerFormationPanel) {
				tPlayerFormationPanel = (PlayerFormationPanel) tFirstComponent;
			}
		}
		
		return tPlayerFormationPanel;
	}
	
	public void buildBottomJPanel () {		
		notificationArea.setText (notificationText);
		
		if (bottomJPanel == GUI.NO_PANEL) {
			bottomJPanel = new JPanel ();
			bottomJPanel.setLayout (new BoxLayout (bottomJPanel, BoxLayout.X_AXIS));
			
			bottomJPanel.add (Box.createHorizontalGlue ());
			bottomJPanel.add (notificationJPanel);
			bottomJPanel.add (Box.createHorizontalStrut (20));
		}
		
		buildOpenMarketPortfolio ();
	}
	
	public void buildOpenMarketPortfolio () {
		BankPool tOpenMarket;
		Bank tBank;
		Portfolio tOpenMarketPortfolio;
		Portfolio tIPOPortfolio;
		
		if (openMarketJPanel == GUI.NO_PANEL) {
			openMarketJPanel = new JPanel ();
			bottomJPanel.add (openMarketJPanel);
		}
		tOpenMarket = gameManager.getBankPool ();
		
		tOpenMarketPortfolio = tOpenMarket.getPortfolio ();
		
		if (ipoJPanel == GUI.NO_PANEL) {
			ipoJPanel = new JPanel ();
			bottomJPanel.add (ipoJPanel);
		}
		tBank = gameManager.getBank ();
		tIPOPortfolio = tBank.getPortfolio ();
		buildPortfolioJPanel (tOpenMarketPortfolio, tIPOPortfolio);
		bottomJPanel.add (Box.createHorizontalGlue ());
	}

	public void buildPortfolioJPanel (Portfolio aOpenMarketPortfolio, Portfolio aIPOPortfolio) {
		JPanel tOwnershipPanel;
		JPanel tIPOPanel;
		JLabel tTitle;
		JLabel tEmptyOpenMarket;
		BankPool tBankPool;
		
		tBankPool = gameManager.getBankPool ();
		openMarketJPanel.removeAll ();
		openMarketJPanel.setLayout (new BoxLayout (openMarketJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel (tBankPool.getName ());
		openMarketJPanel.add (tTitle);
		tOwnershipPanel = aOpenMarketPortfolio.buildOwnershipPanel (gameManager);
		if (tOwnershipPanel == GUI.NO_PANEL) {
			tEmptyOpenMarket = new JLabel (NO_OPEN_MARKET_CERTS);
			openMarketJPanel.add (tEmptyOpenMarket);
		} else {
			openMarketJPanel.add (tOwnershipPanel);
		}
		
		ipoJPanel.removeAll ();
		ipoJPanel.setLayout (new BoxLayout (ipoJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("IPO Holdings");
		tIPOPanel = aIPOPortfolio.buildOwnershipPanel (gameManager);
		ipoJPanel.add (tTitle);
		ipoJPanel.add (tIPOPanel);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
	
		tActionCommand = aEvent.getActionCommand ();
		applyCommand (tActionCommand);
	}
	
	public void applyCommand (String aActionCommand) {
		if (aActionCommand.equals (CONTINUE)) {
			hideFormationPanel ();
		} else if (aActionCommand.equals (FOLD)) {
			handleFoldIntoFormingCompany ();
		} else if (aActionCommand.equals (TOKEN_EXCHANGE)) {
			handleTokenExchange ();
		} else if (aActionCommand.equals (ASSET_COLLECTION)) {
			handleAssetCollection ();
		} else if (aActionCommand.equals (STOCK_VALUE_CALCULATION)) {
			handleStockValueCalculation ();
		}
	}

	public void handleFoldIntoFormingCompany () {
		handleFormationStateChange (ActorI.ActionStates.ShareExchange);
	}

	public void handleTokenExchange () {
		handleFormationStateChange (ActorI.ActionStates.TokenExchange);
	}

	public void handleAssetCollection () {
		handleFormationStateChange (ActorI.ActionStates.AssetCollection);
	}
	
	public void handleStockValueCalculation () {
		handleFormationStateChange (ActorI.ActionStates.StockValueCalculation);
	}
	
	public void handleFormationComplete () {
		handleFormationStateChange (ActorI.ActionStates.FormationComplete);
	}

	public int getSharesReceived (int aSharesExchanged) {
		int tSharesReceived;
		
		tSharesReceived = aSharesExchanged/2;
		
		return tSharesReceived;
	}
	
	public int getPercentageForExchange () {
		int tPercentage;
		
		if (shareFoldCount > SHARES_NEEDED_FOR_2ND_ISSUE) {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE/2;
		} else {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE;
		}
		
		return tPercentage;
	}
	
	public int getPercentageNotForExchange () {
		int tPercentage;
		
		if (shareFoldCount > SHARES_NEEDED_FOR_2ND_ISSUE) {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE;
		} else {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE/2;
		}
		
		return tPercentage;
	}

	public int getShareFoldCount () {
		return shareFoldCount;
	}

	public String buildFoldNotification (ShareCompany aFoldingCompany, int aShareFoldCount) {
		String tNotification;
		String tFormingCompanyAbbrev;
		String tFoldingCompanyAbbrev;
		String tTotalSharesFolded;
		String tPresidentName;
		int tNewShareCount;
		int tSharePercentage;
		
		tFormingCompanyAbbrev = getFormingCompanyAbbrev ();
		tFoldingCompanyAbbrev = aFoldingCompany.getAbbrev ();
		tPresidentName = aFoldingCompany.getPresidentName ();
		tNotification = tFoldingCompanyAbbrev + " will fold " + aShareFoldCount + 
				" Shares into the " + tFormingCompanyAbbrev + ".";
		
		tNewShareCount = getSharesReceived (aShareFoldCount);
		
		tTotalSharesFolded = " A total of " + shareFoldCount + " Shares will be folded into " +
				tFormingCompanyAbbrev + ".";
		
		tSharePercentage = getPercentageForExchange ();
		tNotification += " " + tFormingCompanyAbbrev + " will issue " + tNewShareCount + " shares at " + 
				tSharePercentage + "% per Share to " + tPresidentName + " of the First ";
		if (tSharePercentage != PhaseInfo.STANDARD_SHARE_SIZE) {
			tNotification += "and Second Issues.";
		} else {
			tNotification += "Issue.";
		}
		tNotification += tTotalSharesFolded;
		
		return tNotification;
	}
	
	@Override
	public boolean isInterrupting () {
		return hasOutstandingLoans ();
	}
	
	public boolean hasOutstandingLoans () {
		CorporationList tShareCompanies;
		boolean tCanStart;
		
		tCanStart = false;
		if (gameManager.gameHasLoans ()) {
			tShareCompanies = gameManager.getShareCompanies ();
			tCanStart = tShareCompanies.anyHaveLoans ();
		}
		
		return tCanStart;
	}
}
