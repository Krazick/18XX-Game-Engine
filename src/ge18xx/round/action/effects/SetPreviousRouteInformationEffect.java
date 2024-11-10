package ge18xx.round.action.effects;

//import ge18xx.game.GameManager;
//import ge18xx.round.action.ActorI;
import ge18xx.train.RouteInformation;
import geUtilities.xml.AttributeName;
//import geUtilities.xml.XMLNode;

public class SetPreviousRouteInformationEffect extends Effect {
	public final static String NAME = "Set Previous Route Information";
	final static AttributeName AN_OLD_PREVIOUS_ROUTE_INFORMATION = new AttributeName ("oldPreviousRouteInformation");
	final static AttributeName AN_NEW_PREVIOUS_ROUTE_INFORMATION = new AttributeName ("newPreviousRouteInformation");
	RouteInformation oldPreviousRouteInformation;
	RouteInformation newPreviousRouteInformation;

//	public SetPreviousRouteInformationEffect (ActorI aActor, RouteInformation aOldPreviousRouteInformation, RouteInformation aNewPreviousRouteInformation) {
//		super (NAME, aActor);
//		setOldPreviousRouteInformation (aOldPreviousRouteInformation);
//		setNewPreviousRouteInformation (aNewPreviousRouteInformation);
//	}
//
//	public SetPreviousRouteInformationEffect (XMLNode aEffectNode, GameManager aGameManager) {
//		super (aEffectNode, aGameManager);
//
//		RouteInformation tOldPreviousRouteInformation;
//		RouteInformation tNewPreviousRouteInformation;
//
//		tOldPreviousRouteInformation = aEffectNode.getThisAttribute (AN_OLD_PREVIOUS_ROUTE_INFORMATION);
//		tOldPreviousRouteInformation = aEffectNode.getThisAttribute (AN_NEW_PREVIOUS_ROUTE_INFORMATION);
//		setOldPreviousRouteInformation (tOldPreviousRouteInformation);
//		setNewPreviousRouteInformation (tNewPreviousRouteInformation);
//	}

}
