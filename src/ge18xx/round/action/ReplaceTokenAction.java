package ge18xx.round.action;

import ge18xx.company.TokenInfo.TokenType;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.ClearCorporationEffect;
import ge18xx.round.action.effects.RemoveTokenEffect;
import ge18xx.round.action.effects.SetHomeTokensExchangedEffect;
import ge18xx.tiles.Tile;
import geUtilities.XMLNode;

public class ReplaceTokenAction extends LayTokenAction {
	public final static String NAME = "Replace Token";

	public ReplaceTokenAction () {
		super ();
		setName (NAME);
	}

	public ReplaceTokenAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ReplaceTokenAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " replaced Token on Map Cell " + getMapCellID ();

		return tSimpleActionReport;
	}
	
	public void addRemoveTokenEffect (ActorI aFromActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex,
			TokenType aTokenType, int aTokenIndex) {
		RemoveTokenEffect tRemoveTokenEffect;
		
		tRemoveTokenEffect = new RemoveTokenEffect (aFromActor, aMapCell, aTile, aRevenueCenterIndex,
				aTokenType, aTokenIndex);
		addEffect (tRemoveTokenEffect);
	}
	
	public void addClearCorporationEffect (ActorI aFromActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex) {
		ClearCorporationEffect tClearCorporationEffect;
		
		tClearCorporationEffect = new ClearCorporationEffect (aFromActor, aMapCell, aTile, aRevenueCenterIndex);
		addEffect (tClearCorporationEffect);
	}
	
	public void addSetHomeTokensExchangedEffect (ActorI aFromActor, boolean aHomeTokensExchanged) {
		SetHomeTokensExchangedEffect tSetHomeTokensExchangedEffect;
		
		tSetHomeTokensExchangedEffect = new SetHomeTokensExchangedEffect (aFromActor, aHomeTokensExchanged);
		addEffect (tSetHomeTokensExchangedEffect);
	}
	
	public void addSetNonHomeTokensExchangedEffect (ActorI aFromActor, boolean aHomeTokensExchanged) {
		SetHomeTokensExchangedEffect tSetHomeTokensExchangedEffect;
		
		tSetHomeTokensExchangedEffect = new SetHomeTokensExchangedEffect (aFromActor, aHomeTokensExchanged);
		addEffect (tSetHomeTokensExchangedEffect);
	}
	
	// TODO: Consider options:
	// 1) Reverse order ALWAYS before undoing.
	// 2) Use flag on the Action that if set will reverse before undoing. This is applied when Action is created
	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		
		reverseEffects ();
		tActionUndone = super.undoAction (aRoundManager);
		reverseEffects ();
		
		return tActionUndone;
	}
}
