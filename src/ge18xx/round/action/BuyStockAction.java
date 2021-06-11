package ge18xx.round.action;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.BoughtShareEffect;
import ge18xx.round.action.effects.SetParValueEffect;
import ge18xx.utilities.XMLNode;

public class BuyStockAction extends TransferOwnershipAction {
	public final static String NAME = "Buy Stock";
	
	public BuyStockAction () {
		super ();
		setName (NAME);
	}
	
	public BuyStockAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public BuyStockAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addBoughtShareEffect (ActorI aActor) {
		BoughtShareEffect tBoughtShareEffect;

		tBoughtShareEffect = new BoughtShareEffect (aActor);
		addEffect (tBoughtShareEffect);
	}
	
	public void addSetParValueEffect (ActorI aActor, ShareCompany aShareCompany, int aParPrice) {
		SetParValueEffect tSetParValueEffect;
		
		tSetParValueEffect = new SetParValueEffect (aActor, aShareCompany, aParPrice);
		addEffect (tSetParValueEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = getBuySaleSimpleReport ("bought", "purchase");
		
		return tSimpleActionReport;
	}
}
