package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class SetCorporationBaseEffect extends ClearCorporationEffect {
	public final static String NAME = "Set Corporation Base";

	public SetCorporationBaseEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex) {
		super (aActor, aMapCell, aTile, aRevenueCenterIndex);
		setName (NAME);
	}

	public SetCorporationBaseEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		String tApplyFailureReason;
		

		tEffectApplied = super.undoEffect (aRoundManager);
		tApplyFailureReason = getUndoFailureReason ();
		setUndoFailureReason (GUI.EMPTY_STRING);
		setApplyFailureReason (tApplyFailureReason);
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		String tUndoFailureReason;
		
		tEffectUndone = super.applyEffect (aRoundManager);
		tUndoFailureReason = getApplyFailureReason ();
		setUndoFailureReason (GUI.EMPTY_STRING);
		setApplyFailureReason (tUndoFailureReason);

		return tEffectUndone;

	}
}
