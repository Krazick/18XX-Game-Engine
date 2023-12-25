package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenStack;
import ge18xx.game.GameManager;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class RemoveTokenFromMarketCellEffect extends ChangeMarketCellEffect {
	public final static String NAME = "Remove Token from Market Cell";
	
	public RemoveTokenFromMarketCellEffect (ActorI aActor, MarketCell aMarketCell, int aStackLocation) {
		super (aActor, aMarketCell, aStackLocation, MarketCell.NO_MARKET_CELL, TokenStack.NO_STACK_LOCATION);
		setName (NAME);
	}

	public RemoveTokenFromMarketCellEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		int tStartPrice;
		MarketCell tStartCell;
		Market tMarket;

		tMarket = aRoundManager.getMarket ();
		tStartCell = tMarket.getMarketCellAtCoordinates (startCellCoordinates);
		tStartPrice = tStartCell.getValue ();

		return (REPORT_PREFIX + name + " for " + actor.getName () + " from " + startCellCoordinates + " ("
				+ Bank.formatCash (tStartPrice) + ") location (" + startLocation + ").");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Market tMarket;
		MarketCell tStartCell;
		ShareCompany tShareCompany;
		String tCompanyAbbrev;

		tEffectApplied = true;
		tMarket = aRoundManager.getMarket ();
		tStartCell = tMarket.getMarketCellAtCoordinates (startCellCoordinates);
		tShareCompany = (ShareCompany) actor;
		tCompanyAbbrev = tShareCompany.getAbbrev ();
		tStartCell.getToken (tCompanyAbbrev);
		aRoundManager.updatePlayerListeners (Market.MARKET_CELL_ADJUSTMENT);

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Market tMarket;
		MarketCell tStartCell;
		Token tToken;
		ShareCompany tShareCompany;

		tEffectUndone = true;
		tMarket = aRoundManager.getMarket ();
		tStartCell = tMarket.getMarketCellAtCoordinates (startCellCoordinates);
		tShareCompany = (ShareCompany) actor;
		tShareCompany.setSharePrice (tStartCell);
		tToken = tShareCompany.getMarketToken ();
		if (tToken != Token.NO_TOKEN) {
			tStartCell.addTokenToLocation (startLocation, tToken);
		}
		tStartCell.redrawMarket ();

		return tEffectUndone;
	}
}
