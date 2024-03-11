package ge18xx.company.formation;

import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.StockValueCalculationAction;
import ge18xx.toplevel.MarketFrame;
import geUtilities.GUI;

public class StockValueCalculation extends PlayerFormationPhase {
	private static final long serialVersionUID = 1L;
	private int newParPrice;
	private MarketCell closestMarketCell;

	public StockValueCalculation (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aTokenExchange, aPlayer, aActingPresident);
	}
	
	@Override
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
		JPanel tCompanyInfoPanel;
		JPanel tShareCompanyJPanel;
		JLabel tNoteLabel;
		Corporation tNewCompany;
		ShareCompany tFormingShareCompany;
		boolean tIsPresident;
		int tFormingCompanyID;

		tCompanyInfoPanel = new JPanel ();		
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tNewCompany = gameManager.getCorporationByID (tFormingCompanyID);
		tIsPresident = false;
		if (tNewCompany.isAShareCompany ()) {
			tFormingShareCompany = (ShareCompany) tNewCompany;
			if (player.isPresidentOf (tNewCompany)) {
				tIsPresident = true;
				tShareCompanyJPanel = buildCompanyJPanel (tFormingShareCompany, aActingPlayer);
				tCompanyInfoPanel.add (tShareCompanyJPanel);
				tCompanyInfoPanel.add (Box.createVerticalStrut (5));
			}
		}
		if (!tIsPresident) {
			tNoteLabel = new JLabel (player.getName () + " is not the President of the " + 
								tNewCompany.getAbbrev () +  ". Nothing to do.");
			tCompanyInfoPanel.add (tNoteLabel);
		}

		return tCompanyInfoPanel;
	}

	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;

		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));
		
		buildParPriceCalcPanel (aShareCompany, tShareCompanyJPanel);
		tShareCompanyJPanel.add (Box.createHorizontalStrut (30));
		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);

		return tShareCompanyJPanel;
	}

	public void buildParPriceCalcPanel (ShareCompany aShareCompany, JPanel tShareCompanyJPanel) {
		JPanel tParPriceCalcPanel;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;		
		List<String> tFoldingAbbrevs;
		List<Integer> tFoldingPrices;
		String tShareInfoText;
		int tShareIndex;
		int tShareCount;
		int tSharePrice;
		int tTotalPrice;
		int tPriceCount;
		int tMinPriceIndex;
		int tIngoredIndex;
		int tPriceIndex;
		int tClosestMarketValue;
		JLabel tShareInfo;
		JLabel tNewStockPrice;
		JLabel tNewParPrice;
		
		tParPriceCalcPanel = new JPanel ();
		tParPriceCalcPanel.setLayout (new BoxLayout (tParPriceCalcPanel, BoxLayout.Y_AXIS));

		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tFoldingAbbrevs = new LinkedList<String> ();
		tFoldingPrices = new LinkedList<Integer> ();
	
		tTotalPrice = 0;
		tPriceCount = 0;
		tMinPriceIndex = 0;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tSharePrice = tShareCompany.getSharePrice ();
					tFoldingAbbrevs.add (tShareCompany.getAbbrev ());
					tFoldingPrices.add (tSharePrice);
					if (tSharePrice < tFoldingPrices.get (tMinPriceIndex)) {
						tMinPriceIndex = tFoldingPrices.size () - 1;
					}
					tTotalPrice += tSharePrice;
					tPriceCount++;
				}
			}
		}
		tIngoredIndex = calculateNewParPrice (tFoldingPrices, tTotalPrice, tPriceCount, tMinPriceIndex);
		tClosestMarketValue = closestMarketCell.getValue ();
		for (tPriceIndex = 0; tPriceIndex < tPriceCount; tPriceIndex++) {
			tShareInfoText = tFoldingAbbrevs.get (tPriceIndex) + " " + 
								Bank.formatCash (tFoldingPrices.get (tPriceIndex));
			if (tPriceIndex == tIngoredIndex) {
				tShareInfoText = "[" + tShareInfoText + " IGNORED]";
			}
			tShareInfo = new JLabel (tShareInfoText);
			tParPriceCalcPanel.add (tShareInfo);
		}
		tNewStockPrice = new JLabel (aShareCompany.getAbbrev () + " Par Price " + Bank.formatCash (newParPrice));
		tParPriceCalcPanel.add (Box.createVerticalStrut (20));
		tParPriceCalcPanel.add (tNewStockPrice);
		if (tClosestMarketValue != newParPrice) {
			tNewParPrice = new JLabel ("Closest Par Price " + Bank.formatCash (tClosestMarketValue));
			tParPriceCalcPanel.add (tNewParPrice);
		}
		
		tShareCompanyJPanel.add (tParPriceCalcPanel);
	}

	public int calculateNewParPrice (List<Integer> aFoldingPrices, int aTotalPrice, int aPriceCount,
			int aMinPriceIndex) {
		int tIgnoredIndex;
		
		if (aPriceCount > 2) {
			aTotalPrice -= aFoldingPrices.get (aMinPriceIndex);
			tIgnoredIndex = aMinPriceIndex;
			newParPrice = aTotalPrice/(aPriceCount - 1);
		} else {
			newParPrice = aTotalPrice/aPriceCount;
			tIgnoredIndex = -1;
		}
		newParPrice = 5 * (Math.round (newParPrice/5));
		newParPrice = Math.max (100, newParPrice);
		
		closestMarketCell = getClosestMarketCell (newParPrice);
		
		// Returned the ignored price index (lowest if at least 3 prices).
		
		return tIgnoredIndex;
	}
	
	@Override
	public void updateContinueButton (boolean aActingPlayer) {
		continueButton.setVisible (false);
	}

	public MarketCell getClosestMarketCell (int aNewParPrice) {
		Market tMarket;
		MarketCell tParPriceMarketCell;
		
		tMarket = gameManager.getMarket ();
		tParPriceMarketCell = tMarket.getClosestMarketCell (aNewParPrice, 0);
		
		return tParPriceMarketCell;
	}
	
	public void completeFormingCompany (StockValueCalculationAction aStockValueCalculationAction) {
		ShareCompany tFormingShareCompany;
		ActorI.ActionStates tOldFormingCoStatus;
		ActorI.ActionStates tNewFormingCoStatus;
		MarketFrame tMarketFrame;
		String tCoordinates;
		int tParPrice;
		int tShareIndex;
		int tShareCount;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;	
		String tFoldingCorps;

		tFormingShareCompany = formationPhase.getFormingCompany ();
		tMarketFrame = gameManager.getMarketFrame ();
		tParPrice = closestMarketCell.getValue ();
		tMarketFrame.setParPriceToMarketCell (tFormingShareCompany, tParPrice, closestMarketCell);
		
		tNewFormingCoStatus = ActorI.ActionStates.NotOperated;
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tFoldingCorps = GUI.EMPTY_STRING;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tFoldingCorps += tShareCompany.getAbbrev () + ",";
					if (tShareCompany.hasOperated () || tShareCompany.hasBoughtTrain ()) {
						tNewFormingCoStatus = ActorI.ActionStates.Operated;
					}
				}
			}
		}
		
		tCoordinates = closestMarketCell.getCoordinates ();
		aStockValueCalculationAction.addSetParValueEffect (tFormingShareCompany, tFormingShareCompany, tParPrice, 
										tCoordinates);
		tOldFormingCoStatus = tFormingShareCompany.getActionStatus ();
		tFormingShareCompany.resetStatus (tNewFormingCoStatus);
		aStockValueCalculationAction.addChangeCorporationStatusEffect (tFormingShareCompany, 
						tOldFormingCoStatus, tNewFormingCoStatus);
		closeAllFoldingCompanies (aStockValueCalculationAction, tFoldingCorps);
	}
	
	public void closeAllFoldingCompanies (StockValueCalculationAction aStockValueCalculationAction, 
										String aFoldingCorps) {
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;	
		String tClosingCorps [];
		String tCoordinates;
		int tNewParPrice;
		
		tClosingCorps = aFoldingCorps.split (",");
		tShareCompanies = gameManager.getShareCompanies ();
		for (String tClosingCorp : tClosingCorps) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tClosingCorp);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tShareCompany.close (aStockValueCalculationAction);
					tCoordinates = GUI.EMPTY_STRING;
					tShareCompany.setNoPrice ();
					tNewParPrice = tShareCompany.getParPrice ();
					aStockValueCalculationAction.addSetParValueEffect (tShareCompany, tShareCompany, tNewParPrice,
										tCoordinates);
				}
			}
		}
	}
	
	@Override
	public void handlePlayerDone () {
		StockValueCalculationAction tStockValueCalculationAction;
		String tOperatingRoundID;
		PlayerManager tPlayerManager;
		Corporation tFormingCompany;
		
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tStockValueCalculationAction = new StockValueCalculationAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);

		completeFormingCompany (tStockValueCalculationAction);
		
		// Possible special test if none of the folding companies have operated yet, may start CGR if next in order
		// Check for Next company to Operate
		
		tStockValueCalculationAction.setChainToPrevious (true);
		formationPhase.setFormationState (ActorI.ActionStates.NoState);
		tFormingCompany = formationPhase.getFormingCompany ();
		tFormingCompany.setCorporationFrame ();
		formationPhase.hideFormationPanel ();
		tStockValueCalculationAction.addHideFormationPanelEffect (player);
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayerManager.setPlayersToNoAction ();
		gameManager.addAction (tStockValueCalculationAction);
		gameManager.updateAllFrames ();
	}
}
