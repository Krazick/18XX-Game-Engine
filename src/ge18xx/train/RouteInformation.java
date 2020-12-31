package ge18xx.train;

import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.NodeList;

import ge18xx.center.RevenueCenter;
import ge18xx.company.TrainCompany;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.RouteAction;
import ge18xx.tiles.Track;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineIO;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class RouteInformation {
	final static ElementName EN_ROUTE_SEGMENTS = new ElementName ("RouteSegments");
	final static ElementName EN_REVENUE_CENTERS = new ElementName ("RevenueCenters");
	final static AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	final static AttributeName AN_TRAIN_INDEX = new AttributeName ("trainIndex");
	final static AttributeName AN_TOTAL_REVENUE = new AttributeName ("totalRevenue");
	final static AttributeName AN_ROUND_ID = new AttributeName ("roundID");
	final static AttributeName AN_REGION_BONUS = new AttributeName ("regionBonus");
	final static AttributeName AN_SPECIAL_BONUS = new AttributeName ("specialBonus");
	final static AttributeName AN_PHASE = new AttributeName ("phase");
	public static RouteInformation NO_ROUTE_INFORMATION = null;
	Train train;		// Reference to actual Train
	int trainIndex;		// Index for Train within TrainPortfolio
	Color color;		// Route Color
	int totalRevenue;	// Total Revenue for Route
	String roundID;		// Operating Round ID when Route was Run
	int regionBonus;	// Bonus (Special Region-to-Region connection)
	int specialBonus;	// Bonus (Special Train/Car used)
	int phase;
	ArrayList<RouteSegment> routeSegments;
	ArrayList<RevenueCenter> revenueCenters;
	TrainRevenueFrame trainRevenueFrame;
	TrainCompany trainCompany;
	
	// Collection of Route Segments
	public RouteInformation (Train aTrain, int aTrainIndex, Color aColor, String aRoundID, int aRegionBonus, 
				int aSpecialBonus, int aPhase, TrainCompany aTrainCompany, TrainRevenueFrame aTrainRevenueFrame) {
		setTrain (aTrain);
		setTrainIndex (aTrainIndex);
		setColor (aColor);
		setRoundID (aRoundID);
		setRegionBonus (aRegionBonus);
		setSpecialBonus (aSpecialBonus);
		setTotalRevenue (0);
		phase = aPhase;
		trainCompany = aTrainCompany;
		setTrainRevenueFrame (aTrainRevenueFrame);
		routeSegments = new ArrayList<RouteSegment> ();
		revenueCenters = new ArrayList<RevenueCenter> ();
	}

	public RouteInformation (Train aTrain, XMLNode aRouteNode) {
		XMLNodeList tXMLNodeList;
		String tTrainName, tProvidedTrainName;
		int tPhase, tRegionBonus, tSpecialBonus, tTotalRevenue, tTrainIndex;
		String tRoundID;

		tTrainName = aRouteNode.getThisAttribute (Train.AN_NAME, Train.MISSING_NAME);
		tRoundID = aRouteNode.getThisAttribute (AN_ROUND_ID);
		tPhase = aRouteNode.getThisIntAttribute (AN_PHASE);
		tRegionBonus = aRouteNode.getThisIntAttribute (AN_REGION_BONUS);
		tSpecialBonus = aRouteNode.getThisIntAttribute (AN_SPECIAL_BONUS);
		tTotalRevenue = aRouteNode.getThisIntAttribute (AN_TOTAL_REVENUE);
		tTrainIndex = aRouteNode.getThisIntAttribute (AN_TRAIN_INDEX);
		tProvidedTrainName = aTrain.getName ();
		if (tTrainName.equals (tProvidedTrainName)) {
			setTrainIndex (tTrainIndex);
			setTrain (aTrain);
			setRoundID (tRoundID);
			setRegionBonus (tRegionBonus);
			setSpecialBonus (tSpecialBonus);
			setTotalRevenue (tTotalRevenue);
			phase = tPhase;
			
			routeSegments = new ArrayList<RouteSegment> ();
			revenueCenters = new ArrayList<RevenueCenter> ();
			
			tXMLNodeList = new XMLNodeList (routeSegmentsParsingRoutine, this);
			tXMLNodeList.parseXMLNodeList (aRouteNode, EN_ROUTE_SEGMENTS, RouteSegment.EN_ROUTE_SEGMENT);
		} else {
			System.err.println ("Looking for Train Named " + tProvidedTrainName + " Found " + tTrainName);
		}
		
		// TODO: Parse Revenue Centers List
			
	}
	
	public void addJustRouteSegment (RouteSegment aRouteSegment) {
		routeSegments.add(aRouteSegment);
	}
	
	public void loadRouteSegments (RouteInformation aRouteInformation, XMLNode aRouteSegmentsNode) {
		XMLNode tRouteSegmentNode;
		NodeList tRoutesChildren;
		int tRoutesNodeCount, tRouteIndex;
		String tRouteNodeName;
		RouteSegment tRouteSegment;
		
		tRoutesChildren = aRouteSegmentsNode.getChildNodes ();
		tRoutesNodeCount = tRoutesChildren.getLength ();
		try {
			for (tRouteIndex = 0; tRouteIndex < tRoutesNodeCount; tRouteIndex++) {
				tRouteSegmentNode = new XMLNode (tRoutesChildren.item (tRouteIndex));
				tRouteNodeName = tRouteSegmentNode.getNodeName ();
				if (RouteSegment.EN_ROUTE_SEGMENT.equals (tRouteNodeName)) {
					tRouteSegment = new RouteSegment (tRouteSegmentNode);
					aRouteInformation.addJustRouteSegment (tRouteSegment);
				}
			}			
		} catch (Exception tException) {
			System.out.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
		printDetail ();
	}
	
	public void loadRouteForTrain (XMLNode aRouteSegmentNode, ElementName aElementName, Train aTrain) {
		XMLNodeList tXMLNodeList;
		
		tXMLNodeList = new XMLNodeList (routeSegmentsParsingRoutine, this);
		tXMLNodeList.parseXMLNodeList (aRouteSegmentNode, aElementName);
	}

	ParsingRoutineIO routeSegmentsParsingRoutine  = new ParsingRoutineIO ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aRouteSegmentNode, Object aRouteInformation) {
			RouteInformation tRouteInformation;
			
			tRouteInformation = (RouteInformation) aRouteInformation;
			loadRouteSegments (tRouteInformation, aRouteSegmentNode);
		}

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			
		}
	};
	
	public void setTrainRevenueFrame (TrainRevenueFrame aTrainRevenueFrame) {
		trainRevenueFrame = aTrainRevenueFrame;
	}
	
	public TrainRevenueFrame getTrainRevenueFrame () {
		return trainRevenueFrame;
	}
	
	public void addRouteSegment (RouteSegment aRouteSegment, RouteAction aRouteAction) {
		RevenueCenter tRevenueCenter;
		MapCell tMapCell;
		Location tStartLocation, tEndLocation;
		
		if (revenueCenters != null) {
			if (aRouteSegment.hasRevenueCenter()) {
				tRevenueCenter = aRouteSegment.getRevenueCenter();
				aRouteSegment.setRevenue (tRevenueCenter, phase);
				if (! isSameRCasLast (tRevenueCenter)) {
					revenueCenters.add (tRevenueCenter);
				}
			}
			routeSegments.add (aRouteSegment);
			calculateTotalRevenue ();
			trainRevenueFrame.updateRevenues (this);
			updateConfirmRouteButton ();
			
			// Add the New Route Segment Effect
			tMapCell = aRouteSegment.getMapCell ();
			tStartLocation = aRouteSegment.getStartLocation ();
			tEndLocation = aRouteSegment.getEndLocation ();
			aRouteAction.addNewRouteSegmentEffect (trainCompany, trainIndex, tMapCell, tStartLocation, tEndLocation);
		} else {
			System.err.println ("Revenue Centers Array not Initialized");
		}
	}
	
	public boolean isSameRCasLast (RevenueCenter aRevenueCenter) {
		boolean tIsSameRCasLast = false;
		RevenueCenter tLastRevenueCenter;
		int tRevenueCenterCount;
		
		tRevenueCenterCount = revenueCenters.size ();
		if (tRevenueCenterCount > 0) {
			tLastRevenueCenter = revenueCenters.get (tRevenueCenterCount - 1);
			if (tLastRevenueCenter.equals (aRevenueCenter)) {
				tIsSameRCasLast = true;
			}
		}
		
		return tIsSameRCasLast;
	}
	
	public int getSegmentCount () {
		return routeSegments.size ();
	}
	
	public int getCenterCount () {
		return revenueCenters.size ();
	}
	
	public RouteSegment getRouteSegment (int aRouteIndex) {
		RouteSegment tRouteSegment = RouteSegment.NO_ROUTE_SEGMENT;
		
		if ((aRouteIndex >= 0) && (aRouteIndex < getSegmentCount ())) {
			tRouteSegment = routeSegments.get (aRouteIndex);
		}
		
		return tRouteSegment;
	}
	
	public void setTrain (Train aTrain) {
		train = aTrain;
	}
	
	public Train getTrain () {
		return train;
	}
	
	public void setRoundID (String aRoundID) {
		roundID = aRoundID;
	}
	
	public String getRoundID () {
		return roundID;
	}
	
	public void setColor (Color aColor) {
		color = aColor;
	}
	
	public Color getColor () {
		return color;
	}
	
	public void setRegionBonus (int aRegionBonus) {
		regionBonus = aRegionBonus;
	}
	
	public int getRegionBonus () {
		return regionBonus;
	}
	
	public void setSpecialBonus (int aSpecialBonus) {
		specialBonus = aSpecialBonus;
	}
	
	public int getSpecialBonus () {
		return specialBonus;
	}
	
	public void setTotalRevenue (int aTotalRevenue) {
		totalRevenue = aTotalRevenue;
	}
	
	public int getTotalRevenue () {
		return totalRevenue;
	}
	
	public void addToTotalRevenue (int aRevenue) {
		totalRevenue += aRevenue;
	}
	
	public void calculateTotalRevenue () {
		int tRevenue;
		
		setTotalRevenue (0);
		for (RevenueCenter tRevenueCenter : revenueCenters) {
			tRevenue = tRevenueCenter.getRevenue (phase);
			addToTotalRevenue (tRevenue);
		}
	}
	
	public void setTrainIndex (int aTrainIndex) {
		trainIndex = aTrainIndex;
	}
	
	public int getTrainIndex () {
		return trainIndex;
	}

	public void printDetail () {
		int tCenterIndex;
		
		System.out.println ("----------- Start Route Information Detail ----------");
		System.out.println ((trainIndex + 1) + ". " + train.getName () + " Train, Total Revenue: " + totalRevenue +
				" Center Count " + getCenterCount ());
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.printDetail ();
		}
		
		tCenterIndex = 1;
		for (RevenueCenter tRevenueCenter : revenueCenters) {
			System.out.println (tCenterIndex + ". Center with Revenue " + tRevenueCenter.getRevenue (phase) + 
					" Train " + (trainIndex + 1) +  " is Using " + tRevenueCenter.getSelected (trainIndex + 1));
			tCenterIndex++;
		}
		System.out.println ("----------- End Route Information Detail ----------");
	}
	
	public void clearTrainFromMap () {
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.clearTrainOn ();
		}
	}
	
	public void clearTrainOn () {
		clearTrainFromMap ();
		routeSegments.removeAll (routeSegments);
		revenueCenters.removeAll (revenueCenters);
	}

	public int getRevenueAt (int aRevenueCenterIndex) {
		int tRevenue = 0;
		RevenueCenter tRevenueCenter;
		
		if ((aRevenueCenterIndex >= 0) && (aRevenueCenterIndex < revenueCenters.size ())) {
			tRevenueCenter = revenueCenters.get (aRevenueCenterIndex);
			tRevenue = tRevenueCenter.getRevenue (phase);
		}
		
		return tRevenue;
	}

	public void clear() {
		clearTrainOn ();
		setTotalRevenue (0);
	}
	
	public void setTrainCurrentRouteInformation () {
		train.setCurrentRouteInformation (this);
	}

	public boolean lastMapCellIs (MapCell aSelectedMapCell) {
		boolean tLastMapCellMatches = false;
		MapCell tLastMapCell;
		
		if (getSegmentCount () > 0) {
			tLastMapCell = getLastMapCell  ();
			if (tLastMapCell == aSelectedMapCell) {
				tLastMapCellMatches = true;
			}
		}
		
		return tLastMapCellMatches;
	}

	private RouteSegment getLastRouteSegment () {
		RouteSegment tLastRouteSegment;
		
		tLastRouteSegment = routeSegments.get (getSegmentCount () - 1);
		
		return tLastRouteSegment;
	}
	
	private MapCell getLastMapCell () {
		MapCell tLastMapCell;
		RouteSegment tLastRouteSegment;
		
		tLastRouteSegment = getLastRouteSegment ();
		tLastMapCell = tLastRouteSegment.getMapCell ();
		
		return tLastMapCell;
	}

	public void enableAllSelectRoutes () {
		if (trainRevenueFrame != null) {
			trainRevenueFrame.enableAllSelectRoutes ();
		}
	}

	public boolean isValidRoute() {
		boolean tIsValidRoute = false;
		
		if (train != Train.NO_TRAIN) {
			if (getCenterCount () > 1) {
				if (hasACorpStation ()) {
					tIsValidRoute = isRouteOpen ();
					if (tIsValidRoute) {
						tIsValidRoute = true;
					}
				}
			}
		}
		
		return tIsValidRoute;
	}
	
	public boolean isRouteOpen () {
		boolean tIsRouteOpen = true, tIsSegmentRouteOpen;
		int tSegmentCount, tSegmentIndex;
		RouteSegment tRouteSegment;
		
		tSegmentCount = getSegmentCount ();

		if (tSegmentCount > 2) {
			for (tSegmentIndex = 0; tSegmentIndex < tSegmentCount; tSegmentIndex++) {
				tRouteSegment = routeSegments.get (tSegmentIndex);
				if (tSegmentIndex == 0) {
					tIsSegmentRouteOpen = tRouteSegment.isEndOpen ();
				} else if ((tSegmentIndex + 1) == tSegmentCount) {
					tIsSegmentRouteOpen = tRouteSegment.isStartOpen ();
				} else {
					tIsSegmentRouteOpen = tRouteSegment.isFullyOpen ();
				}
				tIsRouteOpen = tIsRouteOpen && tIsSegmentRouteOpen;
			}
		}
			
		return tIsRouteOpen;
	}
	
	public boolean hasACorpStation () {
		boolean tHasACorpStation = false;
		int tSegmentIndex, tSegmentCount;
		RouteSegment tRouteSegment;
		
		tSegmentCount = getSegmentCount ();
		for (tSegmentIndex = 0; tSegmentIndex < tSegmentCount; tSegmentIndex++) {
			tRouteSegment = routeSegments.get (tSegmentIndex);
			tHasACorpStation = tHasACorpStation || tRouteSegment.hasACorpStation ();
		}
		
		return tHasACorpStation;
	}
	
	public void updateConfirmRouteButton () {
		String tToolTipText;
		
		if (isValidRoute ()) {
			trainRevenueFrame.enableConfirmRouteButton (trainIndex);
			train.setCurrentRouteInformation (this);
		} else {
			tToolTipText = "Route is not Valid";
			trainRevenueFrame.disableConfirmRouteButton (trainIndex, tToolTipText);
		}
	}

	public void extendRouteInformation (RouteSegment aRouteSegment, int aPhase, int aCorpID, RouteAction aRouteAction) {
		// Break this into three separate subroutines, based upon Criteria
		// Generic Testing Criteria to be met before the Route Segment can be successfully added/modified:
			// Test 1: Route Segment must exist on the Tile that is on the mapCell, based upon both Start & End Locations, in the given orientation
			// When Adding a new Route Segment:
				// Test 2: the MapCells must be Adjacent
				// Test 3: the Tiles on the MapCells must both have a Track to the adjoining sides
				// Test 4: the Track on the Tile from the Previous Route Segment MapCell must NOT be in use by any train
				// Test 5: the Track on the Tile from the new Route Segment's MapCell must NOT be in use by any train
				// Test 6: the Adjoining Sides of the two MapCells must NOT be in use by any train
			// When in Scenario 2, a Track is being cycled in,
				// Test 1: for the track being swapped in, connecting to the "new" end point, if a side, 
						// Test 1A: must not be used by any train
						// Test 1B: the neighboring mapCell's must have a Tile
						// Test 1C: the neighboring mapCell's must have a Track on the Tile that connects to the adjoining side
						// Test 1D: the neighboring mapCell's Track that is adjoining must NOT be in use by any train.
		// Trying to mesh these together into a single routine (first attempt) leads to 
		// DONE: 1. Handle Scenario 1 - Add First Segment to Route
		
		// DONE: 2. Handle Scenario 2 - Adding Segment # 2 to Route (First Route Segment has RC but no valid end point)
		
		// DONE: 3. Handle Scenario 3 - Cycling to Next Track Segment on Last Route Segment due to re-click of MapCell
		
		// DONE: 4. Handle Scenario 4 - Adding Segment # 3+ to Route, when Previous Route Segment has Valid End Point
		boolean tContinueWork = true;
		int tInitialSegmentCount;
		MapCell tNewMapCell;
		
		System.out.println ("--------- Start Extending Route, Success: " + tContinueWork);
	
		tInitialSegmentCount = getSegmentCount ();
		if (tInitialSegmentCount == 0) {
			System.out.println ("\nScenario 1 - Add Segment # 1");
			addRouteSegment (aRouteSegment, aRouteAction);
		} else if (tInitialSegmentCount == 1) {
			System.out.println ("\nScenario 2 - Add Segment # 2");
			tContinueWork = fillEndPoint (aRouteSegment, aRouteAction);
			if (tContinueWork) {
				tContinueWork = addNextRouteSegment (aRouteSegment, aCorpID, aRouteAction);
			}
		} else if (tInitialSegmentCount > 1) {
			tNewMapCell = aRouteSegment.getMapCell ();
			if (lastMapCellIs (tNewMapCell)) {
				System.out.println ("\nScenario 3 - Cycle Track");
				cycleToNextTrack (aRouteAction);
			} else {
				System.out.println ("\nScenario 4 - Add Route Segment # 3+");
				tContinueWork = addNewPreviousSegment (aRouteSegment, aPhase, aCorpID, aRouteAction);
				if (tContinueWork) {
					tContinueWork = addNextRouteSegment (aRouteSegment, aCorpID, aRouteAction);
				}
			}
		}
		System.out.println ("--------- Done Extending Route, Success: " + tContinueWork + " \n");
		printDetail ();
	}
	
	public void cycleToNextTrack (RouteAction aRouteAction) {
		RouteSegment tLastRouteSegment;
		Track tLastTrack, tNextTrack;
		int tTrainNumber;
		boolean tCycledToNextTrack;
		MapCell tMapCell;
		Location tStartLocation, tEndLocation;
		
		tTrainNumber = getTrainIndex () + 1;
		tLastRouteSegment = getLastRouteSegment ();
		tLastTrack = tLastRouteSegment.getTrack ();
		tCycledToNextTrack = tLastRouteSegment.cycleToNextTrack ();
		if (tCycledToNextTrack) {
			tLastTrack.clearTrainNumber ();
			tNextTrack = tLastRouteSegment.getTrack ();
			tNextTrack.setTrainNumber (tTrainNumber);
			
			// Add the New Route Segment Effect
			tMapCell = tLastRouteSegment.getMapCell ();
			tStartLocation = tLastRouteSegment.getStartLocation ();
			tEndLocation = tLastRouteSegment.getEndLocation ();
			aRouteAction.addSetNewEndPointEffect (trainCompany, trainIndex, tMapCell, tStartLocation, tEndLocation);
		}
	}

	private boolean addNewPreviousSegment (RouteSegment aRouteSegment, int aPhase, int aCorpID, RouteAction aRouteAction) {
		boolean tAddNewPreviousSegment = false;
		RouteSegment tPreviousSegment, tNewPreviousSegment;
		MapCell tCurrentMapCell, tPreviousMapCell;
		int tSegmentCount, tTrainNumber;
		int tPreviousEnd, tPreviousSide;
		Track tTrack;
		Location tPreviousEndLocation;
		int tCurrentCellNeighborSide;
		RevenueCenter tPreviousRevenueCenter;
		
		System.out.println ("Time to Add New Previous Route Segment");
		
		tSegmentCount = getSegmentCount ();
		tPreviousSegment = getRouteSegment (tSegmentCount - 1);
		tCurrentMapCell = aRouteSegment.getMapCell ();
		tPreviousMapCell = tPreviousSegment.getMapCell ();
		if (tCurrentMapCell.isNeighbor (tPreviousMapCell)) {
			if (tCurrentMapCell.hasConnectingTrackTo (tPreviousMapCell)) {
				tPreviousEndLocation = tPreviousSegment.getEndLocation ();
				tPreviousSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
				tPreviousEnd = tPreviousEndLocation.getLocation ();
				if (! tPreviousEndLocation.isSide ()) {
					tTrack = tPreviousMapCell.getTrackFromStartToEnd (tPreviousEnd, tPreviousSide);
					if (! tTrack.isTrackUsed ()) {
						tNewPreviousSegment = new RouteSegment (tPreviousMapCell);
						tPreviousRevenueCenter = tPreviousSegment.getRevenueCenter ();
						setStartSegment (tNewPreviousSegment, tPreviousRevenueCenter, aPhase, aCorpID);
						
						tNewPreviousSegment.setEndNodeLocationInt (tPreviousSide, phase);

						addRouteSegment (tNewPreviousSegment, aRouteAction);
						tTrainNumber = getTrainIndex () + 1;
						tNewPreviousSegment.setTrainOnTrack (tTrack, tTrainNumber);
						tAddNewPreviousSegment = true;
					} else {
						System.err.println ("Previous Map Cell's Track is in Use");
					}
				} else {
					tCurrentCellNeighborSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
					if (tCurrentCellNeighborSide != tPreviousEnd) {
						System.err.println ("Previous Map Cell Track ended on " + tPreviousEnd + 
								" Current Map Cell track connects to "+ tCurrentCellNeighborSide);
					} else {
						tAddNewPreviousSegment = true;
					}
				}
			} else {
				System.err.println ("No connecting Track between MapCell " + tPreviousMapCell.getCellID () + " and " + tCurrentMapCell.getCellID ());
			}
		} else {
			System.err.println ("MapCell " + tPreviousMapCell.getCellID () + " and " + tCurrentMapCell.getCellID () + " are not Neighbors");
		}
		
		return tAddNewPreviousSegment;
	}

	private boolean addNextRouteSegment (RouteSegment aRouteSegment, int aCorpID, RouteAction aRouteAction) {
		boolean tAddNextRouteSegment = false;
		int tCurrentSide, tTrainNumber;
		MapCell tCurrentMapCell, tPreviousMapCell;
		RouteSegment tPreviousSegment;
		Location tPossibleEnd;
		Track tTrack;
		
		tPreviousSegment = getRouteSegment (getSegmentCount () - 1);
		tCurrentMapCell = aRouteSegment.getMapCell ();
		tPreviousMapCell = tPreviousSegment.getMapCell ();
		tCurrentSide = tCurrentMapCell.getSideToNeighbor (tPreviousMapCell);
		aRouteSegment.setStartNodeLocationInt (tCurrentSide);
		tPossibleEnd = aRouteSegment.getPossibleEnd ();
		aRouteSegment.setEndNodeLocation (tPossibleEnd);
		tTrack = aRouteSegment.getTrack ();
		
		if (! tTrack.isTrackUsed ()) {
			aRouteSegment.applyRCInfo (phase, aCorpID);
			tTrainNumber = getTrainIndex () + 1;
			aRouteSegment.setTrainOnTrack (tTrack, tTrainNumber);
			addRouteSegment (aRouteSegment, aRouteAction);
			tAddNextRouteSegment = true;
		}
		
		return tAddNextRouteSegment;
	}

	private boolean fillEndPoint (RouteSegment aRouteSegment, RouteAction aRouteAction) {
		boolean tFillEndPoint = false;
		RouteSegment tPreviousSegment;
		MapCell tCurrentMapCell, tPreviousMapCell;
		int tSegmentCount, tTrainNumber;
		int tPreviousSide, tPreviousEnd, tPreviousStart;
		Track tTrack;
		Location tPreviousStartLoc, tPreviousEndLoc;
		
		System.out.println ("Time to Fill End Point for Previous Route Segment");
		tSegmentCount = getSegmentCount ();
		tPreviousSegment = getRouteSegment (tSegmentCount - 1);
		tCurrentMapCell = aRouteSegment.getMapCell ();
		tPreviousMapCell = tPreviousSegment.getMapCell ();
		if (tCurrentMapCell.isNeighbor (tPreviousMapCell)) {
			
			if (tCurrentMapCell.hasConnectingTrackTo (tPreviousMapCell)) {
				tPreviousSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
				tPreviousEnd = tPreviousSegment.getEndLocationInt ();
				tPreviousStart = tPreviousSegment.getStartLocationInt ();
				if (tPreviousEnd == Location.NO_LOCATION) {
					if (tPreviousMapCell.hasConnectingTrackBetween (tPreviousStart, tPreviousSide)) {
						tTrack = tPreviousMapCell.getTrackFromStartToEnd (tPreviousStart, tPreviousSide);
						if (! tTrack.isTrackUsed ()) {
							tTrainNumber = getTrainIndex () + 1;
							tPreviousSegment.setEndNodeLocationInt (tPreviousSide, phase);
							// TODO: Add Effect to Change End Point
							tPreviousStartLoc = new Location (tPreviousStart);
							tPreviousEndLoc = new Location (tPreviousSide);
							aRouteAction.addSetNewEndPointEffect (trainCompany, trainIndex, tPreviousMapCell, tPreviousStartLoc, tPreviousEndLoc);
							tPreviousSegment.setTrainOnTrack (tTrack, tTrainNumber);
							tFillEndPoint = true;
						} else {
							System.err.println ("Previous Map Cell's Track is in Use");
						}
					} else {
						System.err.println ("Previous Map Cell's Tile does not have Track Segment between " + tPreviousSide + " and " + tPreviousStart);
					}
				} else {
					System.err.println ("The Previous End is not NO_LOCATION - it is " + tPreviousEnd);
				}
			} else {
				System.err.println ("No connecting Track between MapCell " + tPreviousMapCell.getCellID () + " and " + tCurrentMapCell.getCellID ());
			}
		} else {
			System.err.println ("MapCell " + tPreviousMapCell.getCellID () + " and " + tCurrentMapCell.getCellID () + " are not Neighbors");
		}
		System.out.println ("--------- Done Filling End Point, Success: " + tFillEndPoint);
		
		return tFillEndPoint;
	}
	
	public void setStartSegment (RouteSegment aRouteSegment, RevenueCenter aSelectedRevenueCenter, int aPhase, int aCorpID) {
		boolean tCorpStation, tOpenFlow, tIsCity, tIsDeadEnd, tHasRevenueCenter;
		int tRevenue, tBonus;
		Location tStartLocation;
		NodeInformation tStartNode;
		
		if (aSelectedRevenueCenter == RevenueCenter.NO_CENTER) {
			tCorpStation = false;
			tOpenFlow = true;
			tHasRevenueCenter = false;
			tRevenue = 0;
			tStartLocation = new Location ();
			tIsCity = false;
		} else {
			tCorpStation = aSelectedRevenueCenter.cityHasStation (aCorpID);
			tIsCity = aSelectedRevenueCenter.isCity ();
			tIsDeadEnd = aSelectedRevenueCenter.isDeadEnd ();
			tHasRevenueCenter = true;
			if (tIsDeadEnd) {			// if a Dead-End City, no Flow beyond this.
				tOpenFlow = false;
			} else if (tIsCity) {	
				if (tCorpStation) {		// If this is a City, and it has the Current Operating Company matches the Token
										// Then can flow beyond
					tOpenFlow = true;
				} else { 				// If this is a City, then if there is an Open Station, Flow can continue
					tOpenFlow = aSelectedRevenueCenter.isOpen ();
				}
			} else {					// If this is not a City, it is a Town, and Flow is allowed further
				tOpenFlow = true;
			}
			tRevenue = aSelectedRevenueCenter.getRevenue (aPhase);
			tStartLocation = aSelectedRevenueCenter.getLocation ();
		}
		
		tBonus = 0;		// TODO: If Selected City has Cattle, Port, etc that will add a Bonus, put that here
		
		tStartNode = new NodeInformation (tStartLocation, tCorpStation, tOpenFlow, tHasRevenueCenter, 
				tRevenue, tBonus, aSelectedRevenueCenter);
		aRouteSegment.setStartNode (tStartNode);
	}

	public int getPhase() {
		return phase;
	}

	public XMLElement getElement (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		XMLElement tXMLRouteSegmentsElement, tXMLRevenueCentersElement;
		XMLElement tXMLRouteSegmentElement, tXMLRevenueCenterElement;
		
		tXMLElement = aXMLDocument.createElement (aElementName);
		tXMLElement.setAttribute (Train.AN_NAME, train.getName ());
		tXMLElement.setAttribute (AN_TRAIN_INDEX, trainIndex);
		tXMLElement.setAttribute (AN_TOTAL_REVENUE, totalRevenue);
		tXMLElement.setAttribute (AN_ROUND_ID, roundID);
		tXMLElement.setAttribute (AN_REGION_BONUS, regionBonus);
		tXMLElement.setAttribute (AN_SPECIAL_BONUS, specialBonus);
		tXMLElement.setAttribute (AN_PHASE, phase);
		tXMLRouteSegmentsElement = aXMLDocument.createElement (EN_ROUTE_SEGMENTS);
		for (RouteSegment tRouteSegment : routeSegments) {
			tXMLRouteSegmentElement = tRouteSegment.getElement (aXMLDocument);
			tXMLRouteSegmentsElement.appendChild (tXMLRouteSegmentElement);
		}
		tXMLRevenueCentersElement = aXMLDocument.createElement (EN_REVENUE_CENTERS);
		for (RevenueCenter tRevenueCenter : revenueCenters) {
			tXMLRevenueCenterElement = tRevenueCenter.getElement (aXMLDocument, RevenueCenter.EN_REVENUE_CENTER);
			tXMLRevenueCentersElement.appendChild (tXMLRevenueCenterElement);
		}
		
		tXMLElement.appendChild (tXMLRouteSegmentsElement);
		tXMLElement.appendChild (tXMLRevenueCentersElement);

		return tXMLElement;
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.fixLoadedRouteSegment (aMapFrame);
		}
	}
}
