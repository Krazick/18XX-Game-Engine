package ge18xx.round.action;

public class ActionEffectsFactory {

	String testActions [] = {
			"<Action actor=\"TBNO\" chainPrevious=\"true\" class=\"ge18xx.round.action.LayTileAction\" dateTime=\"1685112002072\" name=\"Lay Tile Action\" number=\"118\" roundID=\"2.1\" roundType=\"Operating Round\" totalCash=\"12000\">\n "
			+ "     <Effects>\n "
			+ "             <Effect actor=\"TBNO\" bases=\"TPRR,1\" class=\"ge18xx.round.action.effects.LayTileEffect\" isAPrivate=\"false\" mapCellID=\"N11\" name=\"Lay Tile\" tileNumber=\"120\" tileOrientation=\"0\" tokens=\"\"/>\n "
			+ "             <Effect actor=\"TBNO\" class=\"ge18xx.round.action.effects.ChangeCorporationStatusEffect\" isAPrivate=\"false\" name=\"Change Corporation Status\" newState=\"Tile Laid\" previousState=\"Started Operating\"/>\n "
			+ "             <Effect actor=\"TBNO\" class=\"ge18xx.round.action.effects.SetHasLaidTileEffect\" hasLaidTile=\"true\" isAPrivate=\"false\" name=\"Set Has Laid Tile\"/>\n "
			+ "     </Effects>\n "
			+ "</Action>\n "
	};
	
	String testEffects [] = {
			"             <Effect actor=\"TBNO\" bases=\"TPRR,1\" class=\"ge18xx.round.action.effects.LayTileEffect\" isAPrivate=\"false\" mapCellID=\"N11\" name=\"Lay Tile\" tileNumber=\"120\" tileOrientation=\"0\" tokens=\"\"/>\n "	
	};
	
	public ActionEffectsFactory () {
		// TODO Auto-generated constructor stub
	}

}
