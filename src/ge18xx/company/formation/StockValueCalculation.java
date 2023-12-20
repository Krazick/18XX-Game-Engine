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
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.StockValueCalculationAction;

public class StockValueCalculation extends PlayerFormationPhase {
	private static final long serialVersionUID = 1L;
	private int newParPrice;

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
		JLabel tShareInfo;
		JLabel tNewStockPrice;
		
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
		
		// Returned the ignored price index (lowest if at least 3 prices).
		
		return tIgnoredIndex;
	}
	
	@Override
	public void updateContinueButton (boolean aActingPlayer) {
		continueButton.setVisible (false);
	}

	@Override
	public void handlePlayerDone () {
		StockValueCalculationAction tStockValueCalculationAction;
		String tOperatingRoundID;
		
		super.handlePlayerDone ();

		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tStockValueCalculationAction = new StockValueCalculationAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);

		// Set CGR Par Price to what is shown.
		// if > $100, find it on the Top Row of the Market
		// Place CGR Market Token on the proper spot on the Market
		// Remove Market Tokens from all other folding companies
		// Change Folding Companies' Status to Closed
		// Hide Formation Phase Frame -- this should continue with Operating Round (if not finished)
		// Possible special test if none of the folding companies have operated yet, may start CGR if next in order
		
		tStockValueCalculationAction.setChainToPrevious (true);
		gameManager.addAction (tStockValueCalculationAction);
	}
}
