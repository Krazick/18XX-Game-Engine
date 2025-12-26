package ge18xx.round.action;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.SetParValueEffect;
import ge18xx.round.action.effects.SetPercentBoughtEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class BuyStockAction extends TransferOwnershipAction {
	public final static String NAME = "Buy Stock";
	public final static BuyStockAction NO_BUY_STOCK_ACTION = null;

	public BuyStockAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public BuyStockAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	public void addSetPercentBoughtEffect (ActorI aActor, String aAbbrev, int aPreviousPercent, int aNewPercent) {
		SetPercentBoughtEffect tSetPercentBoughtEffect;

		tSetPercentBoughtEffect = new SetPercentBoughtEffect (aActor, aAbbrev, aPreviousPercent, aNewPercent);
		addEffect (tSetPercentBoughtEffect);
	}

	public void addSetParValueEffect (ActorI aActor, ShareCompany aShareCompany, 
						int aParPrice, boolean aFixedParValue) {
		SetParValueEffect tSetParValueEffect;

		tSetParValueEffect = new SetParValueEffect (aActor, aShareCompany, aParPrice,
							GUI.EMPTY_STRING, aFixedParValue);
		addEffect (tSetParValueEffect);
	}

	public void addSetParValueEffect (ActorI aActor, ShareCompany aShareCompany, int aParPrice, 
										String aCoordinates, boolean aFixedParValue) {
		SetParValueEffect tSetParValueEffect;

		tSetParValueEffect = new SetParValueEffect (aActor, aShareCompany, aParPrice, 
				aCoordinates, aFixedParValue);
		addEffect (tSetParValueEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = getBuySaleSimpleReport ("bought", "purchase");

		return tSimpleActionReport;
	}
}
