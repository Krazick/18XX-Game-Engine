package ge18xx.round.action;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class ActionEffectsFactory {
	UtilitiesTestFactory utilitiesTestFactory;
	GameManager gameManager;
	
	String testActions [] = {
			"<Action actor=\"TBNO\" chainPrevious=\"true\" class=\"ge18xx.round.action.LayTileAction\" dateTime=\"1685112002072\" name=\"Lay Tile\" number=\"118\" roundID=\"2.1\" roundType=\"Operating Round\" totalCash=\"12000\">\n "
			+ "     <Effects>\n "
			+ "             <Effect actor=\"TBNO\" bases=\"TPRR,1\" class=\"ge18xx.round.action.effects.LayTileEffect\" isAPrivate=\"false\" mapCellID=\"N11\" name=\"Lay Tile\" tileNumber=\"120\" tileOrientation=\"0\" tokens=\"\"/>\n "
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
			+ "</Action>\n", 
			
			"<Action actor=\"David\" chainPrevious=\"true\" class=\"ge18xx.round.action.StartStockAction\" dateTime=\"1775510774763\" name=\"Start Stock Action\" number=\"1\" roundID=\"1\" roundType=\"Stock Round\">\n"
			+ "  <Effects>\n"
			+ "  	<Effect class=\"ge18xx.round.action.effects.ShowFrameEffect\" fromActor=\"Stock Round\" fromName=\"Stock Round\" isAPrivate=\"false\" name=\"Show Frame\" order=\"0\" xmlFrameTitle=\"1830 Player Frame\"/>\n"
			+ "  	<Effect actor=\"David\" class=\"ge18xx.round.action.effects.StartStockEffect\" fromName=\"David\" isAPrivate=\"false\" name=\"Start Stock\" order=\"1\"/>\n"
			+ "  </Effects>\n"
			+ "</Action>\n",
			
			"<Action actor=\"David\" chainPrevious=\"false\" class=\"ge18xx.round.action.BuyStockAction\" dateTime=\"1775510778676\" name=\"Buy Stock Action\" number=\"2\" roundID=\"1\" roundType=\"Stock Round\">\n"
			+ "  <Effects>\n"
			+ "      <Effect cash=\"20\" class=\"ge18xx.round.action.effects.CashTransferEffect\" fromActor=\"David\" fromName=\"David\" isAPrivate=\"false\" name=\"Cash Transfer\" order=\"0\" toActor=\"Bank\" toNickName=\"\"/>\n"
			+ "      <Effect class=\"ge18xx.round.action.effects.TransferOwnershipEffect\" companyAbbrev=\"SVN&amp;RR\" fromActor=\"Start Packet\" fromName=\"Start Packet\" isAPrivate=\"false\" name=\"Transfer Ownership\" order=\"1\" percentage=\"100\" president=\"true\" toActor=\"David\" toNickName=\"\"/>\n"
//			+ "      <Effect available=\"false\" canBeBidOn=\"false\" class=\"ge18xx.round.action.effects.StartPacketItemSetAvailableEffect\" corporationId=\"1001\" discountAmount=\"5\" fromActor=\"Start Packet\" fromName=\"Start Packet\" isAPrivate=\"false\" name=\"Start Packet Item Set Available\" order=\"2\"/>\n"
//			+ "      <Effect actor=\"SVN&amp;RR\" class=\"ge18xx.round.action.effects.ChangeCorporationStatusEffect\" fromName=\"Schuylkill Valley\" isAPrivate=\"true\" name=\"Change Corporation Status\" newState=\"Owned\" order=\"3\" previousState=\"Unowned\"/>\n"
			+ "      <Effect actor=\"David\" boughtShare=\"SVN&amp;RR\" class=\"ge18xx.round.action.effects.BoughtShareEffect\" fromName=\"David\" isAPrivate=\"false\" name=\"Bought Share\" order=\"4\" priorBoughtShare=\"\"/>\n"
			+ "      <Effect actor=\"David\" class=\"ge18xx.round.action.effects.StateChangeEffect\" fromName=\"David\" isAPrivate=\"false\" name=\"State Change\" newState=\"Bought\" order=\"5\" previousState=\"No Action\"/>\n"
			+ "  </Effects>\n"
			+ "</Action>\n",
			
			"<Action actor=\"David\" chainPrevious=\"false\" class=\"ge18xx.round.action.DonePlayerAction\" dateTime=\"1775514410805\" name=\"Done Player Action\" number=\"3\" roundID=\"1\" roundType=\"Stock Round\">\n"
			+ "  <Effects>\n"
			+ "       <Effect actor=\"David\" class=\"ge18xx.round.action.effects.NewPriorityPlayerEffect\" fromName=\"David\" isAPrivate=\"false\" name=\"Change Priority Player\" newPlayer=\"1\" order=\"0\" previousPlayer=\"0\"/>\n"
			+ "       <Effect actor=\"David\" class=\"ge18xx.round.action.effects.NewCurrentPlayerEffect\" fromName=\"David\" isAPrivate=\"false\" name=\"Change Current Player\" newPlayer=\"1\" order=\"1\" previousPlayer=\"0\"/>\n"
			+ "       <Effect actor=\"Mark\" boughtShare=\"\" class=\"ge18xx.round.action.effects.BoughtShareEffect\" fromName=\"Mark\" isAPrivate=\"false\" name=\"Bought Share\" order=\"2\" priorBoughtShare=\"\"/>\n"
			+ "       <Effect actor=\"Mark\" class=\"ge18xx.round.action.effects.BidShareEffect\" fromName=\"Mark\" hasBidShare=\"false\" isAPrivate=\"false\" name=\"Bid On a Share\" order=\"3\"/>\n"
			+ "       <Effect actor=\"Mark\" class=\"ge18xx.round.action.effects.StateChangeEffect\" fromName=\"Mark\" isAPrivate=\"false\" name=\"State Change\" newState=\"No Action\" order=\"4\" previousState=\"No Action\"/>\n"
			+ "    </Effects>\n"
			+ "</Action>\n"
	};
	
	String testEffects [] = {
			"<Effect actor=\"TBNO\" bases=\"TPRR,1\" class=\"ge18xx.round.action.effects.LayTileEffect\" isAPrivate=\"false\" mapCellID=\"N11\" name=\"Lay Tile\" tileNumber=\"120\" tileOrientation=\"0\" tokens=\"\"/>\n "	
	};
	
	public ActionManager buildActionManager (RoundManager aRoundManager) {
		ActionManager tActionManager;
		
		tActionManager = new ActionManager (aRoundManager);
		
		return tActionManager;
	}
	
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
	
	public Action buildActionMock (String aActionName) {
		Action mAction;
		
		mAction = Mockito.mock (Action.class);
		Mockito.when (mAction.getName ()).thenReturn (aActionName);
		
		return mAction;
	}
}
