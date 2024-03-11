package ge18xx.round.action;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.HideFormationPanelEffect;
import ge18xx.round.action.effects.RemoveTokenFromMarketCellEffect;
import ge18xx.round.action.effects.SetParValueEffect;
import geUtilities.XMLNode;

public class StockValueCalculationAction extends FormationPhaseAction {
	public final static String NAME = "Stock Value Calculation Finished";

	public StockValueCalculationAction () {
		this (NAME);
	}

	public StockValueCalculationAction (String aName) {
		super (aName);
	}

	public StockValueCalculationAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StockValueCalculationAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has finished Stock Value Calculation";

		return tSimpleActionReport;
	}
	
	public void addSetParValueEffect (ActorI aActor, ShareCompany aShareCompany, int aParPrice, String aCoordinates) {
		SetParValueEffect tSetParValueEffect;

		tSetParValueEffect = new SetParValueEffect (aActor, aShareCompany, aParPrice, aCoordinates);
		addEffect (tSetParValueEffect);
	}

	public void addRemoveTokenFromMarketCellEffect (ActorI aActor, MarketCell aMarketCell, int aStackLocation) {
		RemoveTokenFromMarketCellEffect tRemoveTokenFromMarketCellEffect;
		
		tRemoveTokenFromMarketCellEffect = new RemoveTokenFromMarketCellEffect (aActor, aMarketCell, aStackLocation);
		addEffect (tRemoveTokenFromMarketCellEffect);
	}
	
	public void addHideFormationPanelEffect (ActorI aActor) {
		HideFormationPanelEffect tHideFormationPanelEffect;
		
		tHideFormationPanelEffect = new HideFormationPanelEffect (aActor);
		addEffect (tHideFormationPanelEffect);
	}

	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		
		reverseEffects ();
		tActionUndone = super.undoAction (aRoundManager);
		reverseEffects ();
		
		return tActionUndone;
	}
}
