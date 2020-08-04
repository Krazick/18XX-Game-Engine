package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.effects.SoldCompanyEffect;
import ge18xx.round.action.effects.ClearExchangePrezShareEffect;
import ge18xx.utilities.XMLNode;

public class SellStockAction extends ChangeMarketCellAction {
	public final static String NAME = "Sell Stock";
	
	public SellStockAction () {
		super ();
		setName (NAME);
	}
	
	public SellStockAction (ActorI.ActionStates aRoundType, String aRoundID,ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public SellStockAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	public void addSoldCompanyEffect (Player aPlayer, String aCompanyAbbrev) {
		SoldCompanyEffect tSoldCompanyEffect;
		
		tSoldCompanyEffect = new SoldCompanyEffect (aPlayer, aCompanyAbbrev);
		addEffect (tSoldCompanyEffect);
	}
	
	public void addClearExchangePrezShareEffect (Player aPlayer, String aCompanyAbbrev) {
		ClearExchangePrezShareEffect tClearExchangePrezShareEffect;
		
		tClearExchangePrezShareEffect = new ClearExchangePrezShareEffect (aPlayer, aCompanyAbbrev);
		addEffect (tClearExchangePrezShareEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = getBuySaleSimpleReport ("sold", "sale");
		
		return tSimpleActionReport;
	}
}
