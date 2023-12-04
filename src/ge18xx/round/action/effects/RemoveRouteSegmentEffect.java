package ge18xx.round.action.effects;

import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainRevenueFrame;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class RemoveRouteSegmentEffect extends ChangeRouteEffect {
	final static AttributeName AN_SEGMENT_INDEX = new AttributeName ("segmentIndex");

	public static final String NAME = "Remove Route Segment";
	int segmentIndex;
	
	public RemoveRouteSegmentEffect () {
		super (NAME);
	}

	public RemoveRouteSegmentEffect (String aName) {
		super (aName);
	}

	public RemoveRouteSegmentEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public RemoveRouteSegmentEffect (ActorI aActor, int aSegmentIndex, int aTrainIndex, MapCell aMapCell) {
		super (aActor, aTrainIndex, aMapCell);
		setName (NAME);
		setSegmentIndex (aSegmentIndex);
	}

	public RemoveRouteSegmentEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		int tSegmentIndex;
		tSegmentIndex = aEffectNode.getThisIntAttribute (AN_SEGMENT_INDEX);
		setSegmentIndex (tSegmentIndex);
	}

	public void setSegmentIndex (int aSegmentIndex) {
		segmentIndex = aSegmentIndex;
	}
	
	public int getSegmentIndex () {
		return segmentIndex;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_SEGMENT_INDEX, segmentIndex);
		
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;

		tReport = REPORT_PREFIX + name + " for " + getActorName () + " for Train " + (getTrainIndex () + 1) +
				" on MapCell " + mapCell.getCellID () + " Segment Index " + segmentIndex + ".";

		return tReport;
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Train tTrain;
		TrainCompany tTrainCompany;
		TrainRevenueFrame tTrainRevenueFrame;
		RouteInformation tRouteInformation;
		
		tEffectApplied = false;
		if (actor.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) actor;
			tEffectApplied = tTrainCompany.removeRouteSegment (trainIndex, mapCell, segmentIndex);
			tTrainRevenueFrame = tTrainCompany.getTrainRevenueFrame ();
			tTrain = tTrainCompany.getTrain (getTrainIndex ());
			tRouteInformation = tTrain.getCurrentRouteInformation ();
			tTrainRevenueFrame.updateRevenues (tRouteInformation);
		}
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		// Don't really need to undo this effect
		return true;
	}
}
