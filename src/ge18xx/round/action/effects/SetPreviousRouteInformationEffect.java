package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;

public class SetPreviousRouteInformationEffect extends Effect {
	public final static String NAME = "Set Previous Route Information";
	public final static AttributeName AN_ROUTE_INFORMATION_ID = new AttributeName ("routeInformationID");
	public final static AttributeName AN_TRAIN_ID = new AttributeName ("trainID");
	String routeInformationID;
	int trainID;
	
	public SetPreviousRouteInformationEffect (ActorI aActor, int aTrainID, String aRouteInformationID) {
		super (NAME, aActor);
		setTrainID (aTrainID);
		setRouteInformationID (aRouteInformationID);
	}
	
	public SetPreviousRouteInformationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tRouteInformationID;
		int tTrainID;
		
		tTrainID = aEffectNode.getThisIntAttribute (AN_TRAIN_ID);
		tRouteInformationID = aEffectNode.getThisAttribute (AN_ROUTE_INFORMATION_ID);

		setTrainID (tTrainID);
		setRouteInformationID (tRouteInformationID);
	}
	
	public void setTrainID (int aTrainID) {
		trainID = aTrainID;
	}
	
	public void setRouteInformationID (String aRouteInformationID) {
		routeInformationID = aRouteInformationID;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Train tTrain;
		RouteInformation tRouteInformation;
		
		tEffectApplied = true;
		tTrain = aRoundManager.getTrain (trainID);
		tRouteInformation = tTrain.getCurrentRouteInformation ();
		tTrain.setPreviousRouteInformation (tRouteInformation);
		
		return tEffectApplied;
	}
}
