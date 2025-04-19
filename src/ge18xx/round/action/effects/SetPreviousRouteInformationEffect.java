package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
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
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_TRAIN_ID, trainID);
		tEffectElement.setAttribute (AN_ROUTE_INFORMATION_ID, routeInformationID);
		
		return tEffectElement;
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
		
		tEffectApplied = false;
		tTrain = aRoundManager.getTrain (trainID);
		if (tTrain != Train.NO_TRAIN) {
			tRouteInformation = tTrain.getCurrentRouteInformation ();
			tTrain.setPreviousRouteInformation (tRouteInformation);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("No Train with ID " + trainID + " Found to retrieve Current Route from" );
		}
		
		return tEffectApplied;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for Train ID " + trainID + " for  " + actor.getName ()
				+ " to Route ID " + routeInformationID);
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Train tTrain;
		RouteInformation tRouteInformation;
		
		tEffectUndone = false;
		tTrain = aRoundManager.getTrain (trainID);
		if (tTrain != Train.NO_TRAIN) {
			tRouteInformation = tTrain.getPreviousRouteInformation ();
			tTrain.setCurrentRouteInformation (tRouteInformation);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("No Train with ID " + trainID + " Found to retrieve Previous Route from" );
		}
		
		return tEffectUndone;
	}
}
