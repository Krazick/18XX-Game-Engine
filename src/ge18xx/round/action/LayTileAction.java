package ge18xx.round.action;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.BenefitUsedEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.LayTileEffect;
import ge18xx.round.action.effects.SetHasLaidTileEffect;
import ge18xx.tiles.Tile;
import ge18xx.utilities.XMLNode;

public class LayTileAction extends ChangeMapAction {
	public final static String NAME = "Lay Tile";
	
	public LayTileAction () {
		this (NAME);
	}
	
	public LayTileAction (String aName) {
		super (aName);
	}
	
	public LayTileAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public LayTileAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addLayTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation, String aTokens, 
			String aBases) {
		LayTileEffect tTileLayEffect;

		tTileLayEffect = new LayTileEffect (aActor, aMapCell, aTile, aOrientation, aTokens, aBases);
		addEffect (tTileLayEffect);
	}
	
	public void addBenefitUsedEffect (ActorI aActor, Benefit aBenefit) {
		BenefitUsedEffect tBenefitUsedEffect;
		
		tBenefitUsedEffect = new BenefitUsedEffect (aActor, aBenefit);
		addEffect (tBenefitUsedEffect);
	}
	
	public void addSetHasLaidTileEffect (ActorI aActor, boolean aHasLaidTile) {
		SetHasLaidTileEffect tSetHasLaidTileEffect;
		
		tSetHasLaidTileEffect = new SetHasLaidTileEffect (aActor, aHasLaidTile);
		addEffect (tSetHasLaidTileEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " laid Tile " + getTileNumber () + 
				" with Orientation of " + getOrientation () + ".";
		
		return tSimpleActionReport;
	}
	
	public int getTileNumber () {
		int tTileNumber = -1;
		
		for (Effect tEffect : effects) {
			if (tTileNumber == -1) {
				if (tEffect instanceof LayTileEffect) {
					tTileNumber = ((LayTileEffect) tEffect).getTileNumber ();
				}
			}
		}
		
		return tTileNumber;
	}
	
	public int getOrientation () {
		int tOrientation = -1;
		
		for (Effect tEffect : effects) {
			if (tOrientation == -1) {
				if (tEffect instanceof LayTileEffect) {
					tOrientation = ((LayTileEffect) tEffect).getOrientation ();
				}
			}
		}
		
		return tOrientation;
	}
}
