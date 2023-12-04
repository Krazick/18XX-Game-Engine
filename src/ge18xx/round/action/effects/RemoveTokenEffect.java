package ge18xx.round.action.effects;

import ge18xx.company.TokenInfo.TokenType;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import geUtilities.XMLNode;

public class RemoveTokenEffect extends LayTokenEffect {
	public final static String NAME = "Remove Token";
	int tileNumber;
	int revenueCenterIndex;
	int tokenIndex;
	TokenType tokenType;

	public RemoveTokenEffect () {
		super ();
		setName (NAME);
	}

	public RemoveTokenEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex,
			TokenType aTokenType, int aTokenIndex) {
		super (aActor, aMapCell, aTile, aRevenueCenterIndex, aTokenType, aTokenIndex, Benefit.NO_BENEFIT);
		setName (NAME);
	}

	public RemoveTokenEffect  (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		String tActionVerb;
		
		tEffectApplied = false;
		tActionVerb = "Apply";
		tEffectApplied = removeToken (aRoundManager, tActionVerb);
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		String tActionVerb;
		
		tActionVerb = "Undone";
		tEffectUndone = layToken (aRoundManager, tActionVerb);
		
		return tEffectUndone;
	}
}
