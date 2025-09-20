package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class ActionEffectsFactory {
	UtilitiesTestFactory utilitiesTestFactory;
	GameManager gameManager;
	
	String testActions [] = {
			"<Action actor=\"TBNO\" chainPrevious=\"true\" class=\"ge18xx.round.action.LayTileAction\" dateTime=\"1685112002072\" name=\"Lay Tile Action\" number=\"118\" roundID=\"2.1\" roundType=\"Operating Round\" totalCash=\"12000\">\n "
			+ "     <Effects>\n "
			+ "             <Effect actor=\"TBNO\" bases=\"TPRR,1\" class=\"ge18xx.round.action.effects.LayTileEffect\" isAPrivate=\"false\" mapCellID=\"N11\" name=\"Lay Tile\" tileNumber=\"120\" tileOrientation=\"0\" tokens=\"\"/>\n "
			+ "             <Effect actor=\"TBNO\" class=\"ge18xx.round.action.effects.ChangeCorporationStatusEffect\" isAPrivate=\"false\" name=\"Change Corporation Status\" newState=\"Tile Laid\" previousState=\"Started Operating\"/>\n "
			+ "             <Effect actor=\"TBNO\" class=\"ge18xx.round.action.effects.SetHasLaidTileEffect\" hasLaidTile=\"true\" isAPrivate=\"false\" name=\"Set Has Laid Tile\"/>\n "
			+ "     </Effects>\n "
			+ "</Action>\n ",
			"<Action actor=\"Formation Round\" chainPrevious=\"true\" class=\"ge18xx.round.action.ChangeRoundAction\" dateTime=\"1757373917050\" name=\"Change Round\" number=\"1704\" roundID=\"1\" roundType=\"Formation Round\">\n"
				+ " <Effects>\n"
				+ " 		<Effect actor=\"Formation Round\" class=\"ge18xx.round.action.effects.SetInterruptionStartedEffect\" fromName=\"Formation Round\" isAPrivate=\"false\" name=\"Set Interruption Started\" order=\"0\" setInterruptionStarted=\"false\"/>\n"
				+ "		<Effect class=\"ge18xx.round.action.effects.HideFrameEffect\" fromActor=\"Formation Round\" fromName=\"Formation Round\" isAPrivate=\"false\" name=\"Hide Frame\" order=\"1\" xmlFrameTitle=\"1856 Stock Value Calculation Frame\"/>\n"
				+ "		<Effect actor=\"Formation Round\" class=\"ge18xx.round.action.effects.StateChangeEffect\" fromName=\"Formation Round\" isAPrivate=\"false\" name=\"State Change\" newState=\"Operating Round\" order=\"2\" previousState=\"Formation Round\"/>\n"
				+ "		<Effect actor=\"Operating Round\" class=\"ge18xx.round.action.effects.ChangeRoundIDEffect\" fromName=\"Operating Round\" isAPrivate=\"false\" name=\"Change Round ID\" newRoundID=\"10.1\" oldRoundID=\"1\" order=\"3\"/>\n"
				+ "	</Effects>\n"
				+ "</Action>\n"
	};
	
	String testEffects [] = {
			"<Effect actor=\"TBNO\" bases=\"TPRR,1\" class=\"ge18xx.round.action.effects.LayTileEffect\" isAPrivate=\"false\" mapCellID=\"N11\" name=\"Lay Tile\" tileNumber=\"120\" tileOrientation=\"0\" tokens=\"\"/>\n "	
	};
	
	public ActionEffectsFactory (GameManager aGameManager, UtilitiesTestFactory aUtilitiesTestFactory) {
		utilitiesTestFactory = aUtilitiesTestFactory;
		gameManager = aGameManager;
	}
	
	public Action getTestActionAt (int aActionIndex) {
		Action tAction;
		String tActionText;
		XMLNode tActionNode;

		
		if ((aActionIndex >= 0) && (aActionIndex < testActions.length)) {
			tActionText = testActions [aActionIndex];
			tActionNode = utilitiesTestFactory.buildXMLNode (tActionText);

			tAction = new Action (tActionNode, gameManager);
		} else {
			tAction = Action.NO_ACTION;
		}
		
		return tAction;
	}
}
