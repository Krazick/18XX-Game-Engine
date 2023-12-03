package ge18xx.train;

import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.NodeList;

import ge18xx.center.RevenueCenter;
import ge18xx.company.Coupon;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.RemoveRouteSegmentsAction;
import ge18xx.round.action.RouteAction;
import ge18xx.round.action.StartRouteAction;
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
	private String NO_MESSAGE = "";
	
	Train train; // Reference to actual Train
	Color color; // Route Color
	int trainIndex; // Index for Train within TrainPortfolio
	int totalRevenue; // Total Revenue for Route
	int regionBonus; // Bonus (Special Region-to-Region connection)
	int specialBonus; // Bonus (Special Train/Car used)
	int phase;
	ArrayList<RouteSegment> routeSegments;
	ArrayList<RevenueCenter> revenueCenters;
	TrainRevenueFrame trainRevenueFrame;
	TrainCompany trainCompany;
	String roundID; // Operating Round ID when Route was Run
	String warningMessage;

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
		setTrainCompany (aTrainCompany);
		setTrainRevenueFrame (aTrainRevenueFrame);
		routeSegments = new ArrayList<RouteSegment> ();
		revenueCenters = new ArrayList<RevenueCenter> ();
		clearWarningMessage ();
	}

	public RouteInformation (RouteInformation aRouteInformation, String aRoundID, int aPhase) {
		this (aRouteInformation.getTrain (), aRouteInformation.getTrainIndex (), aRouteInformation.getColor (),
				aRoundID, aRouteInformation.getRegionBonus (), aRouteInformation.getSpecialBonus (), aPhase,
				aRouteInformation.getTrainCompany (), aRouteInformation.getTrainRevenueFrame ());
	}

	public RouteInformation (Train aTrain, XMLNode aRouteNode, TrainPortfolio aTrainPortfolio) {
		XMLNodeList tXMLNodeList;
		String tTrainName;
		String tProvidedTrainName;
		String tRoundID;
		int tPhase;
		int tRegionBonus;
		int tSpecialBonus;
		int tTotalRevenue;
		int tTrainIndex;
		TrainRevenueFrame tTrainRevenueFrame;
		TrainCompany tTrainCompany;
		CashHolderI tTrainHolder;

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
			tTrainHolder = aTrainPortfolio.portfolioHolder;
			if (tTrainHolder.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tTrainHolder;
				tTrainRevenueFrame = tTrainCompany.getTrainRevenueFrame ();
				setTrainCompany (tTrainCompany);
				setTrainRevenueFrame (tTrainRevenueFrame);
			}
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

	public boolean hasOperated () {
		boolean tHasOperated = false;
		int tValidRouteCode;

		tValidRouteCode = isValidRoute ();
		if (tValidRouteCode < 1) {
			if (routeSegments.size () > 1) {
				if (! isRouteTooLong ()) {
					tHasOperated = true;
				}
			}
		}

		return tHasOperated;
	}

	public void addJustRouteSegment (RouteSegment aRouteSegment) {
		routeSegments.add (aRouteSegment);
	}

	public void loadRouteSegments (RouteInformation aRouteInformation, XMLNode aRouteSegmentsNode) {
		XMLNode tRouteSegmentNode;
		NodeList tRoutesChildren;
		int tRoutesNodeCount;
		int tRouteIndex;
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
			System.err.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
	}

	public void copyRouteSegments (RouteInformation aRouteToCopyFrom) {
		int tSegmentCount;
		int tSegmentIndex;
		RouteSegment tNewRouteSegment;
		RouteSegment tOldSegment;

		tSegmentCount = aRouteToCopyFrom.getSegmentCount ();
		for (tSegmentIndex = 0; tSegmentIndex < tSegmentCount; tSegmentIndex++) {
			tOldSegment = aRouteToCopyFrom.getRouteSegment (tSegmentIndex);
			tNewRouteSegment = new RouteSegment (tOldSegment);
			addRouteSegment (tNewRouteSegment, RouteAction.NO_ROUTE_ACTION);
		}
	}

	public void loadRouteForTrain (XMLNode aRouteSegmentNode, ElementName aElementName, Coupon aTrain) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (routeSegmentsParsingRoutine, this);
		tXMLNodeList.parseXMLNodeList (aRouteSegmentNode, aElementName);
	}

	ParsingRoutineIO routeSegmentsParsingRoutine = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aRouteSegmentNode, Object aRouteInformation) {
			RouteInformation tRouteInformation;

			tRouteInformation = (RouteInformation) aRouteInformation;
			loadRouteSegments (tRouteInformation, aRouteSegmentNode);
		}
	};

	public void highlightRouteSegments (HexMap aMap) {
		int tSegmentCount;
		int tSegmentIndex;
		RouteSegment tRouteSegment;

		tSegmentCount = getSegmentCount ();
		for (tSegmentIndex = 0; tSegmentIndex < tSegmentCount; tSegmentIndex++) {
			tRouteSegment = getRouteSegment (tSegmentIndex);
			tRouteSegment.setTrainOn (trainIndex + 1);
		}
	}

	private void setTrainCompany (TrainCompany aTrainCompany) {
		trainCompany = aTrainCompany;
	}

	private void setTrainRevenueFrame (TrainRevenueFrame aTrainRevenueFrame) {
		trainRevenueFrame = aTrainRevenueFrame;
	}

	public TrainCompany getTrainCompany () {
		return trainCompany;
	}

	public TrainRevenueFrame getTrainRevenueFrame () {
		return trainRevenueFrame;
	}

	public void removeLastRevenueCenter () {
		revenueCenters.remove (revenueCenters.size () - 1);
	}

	public void addRevenueCenter (RouteSegment aRouteSegment) {
		RevenueCenter tRevenueCenter;

		if (aRouteSegment.hasRevenueCenter ()) {
			tRevenueCenter = aRouteSegment.getRevenueCenter ();
			aRouteSegment.setRevenue (tRevenueCenter, phase);
			if (!isSameRCasLast (tRevenueCenter)) {
				revenueCenters.add (tRevenueCenter);
			}
		}
	}

	public boolean isLastSegmentSame (RouteSegment aRouteSegment) {
		boolean tIsLastSegmentSame = false;
		RouteSegment tPreviousSegment;
		int tLastSegmentIndex;

		tLastSegmentIndex = routeSegments.size ();
		if (tLastSegmentIndex > 0) {
			tPreviousSegment = routeSegments.get (tLastSegmentIndex - 1);
			tIsLastSegmentSame = aRouteSegment.isSame (tPreviousSegment);
		}

		return tIsLastSegmentSame;
	}

	public void addRouteSegment (RouteSegment aRouteSegment, RouteAction aRouteAction) {
		MapCell tMapCell;
		Location tStartLocation;
		Location tEndLocation;

		if (revenueCenters != null) {
			addRevenueCenter (aRouteSegment);
			if (!isLastSegmentSame (aRouteSegment)) {
				routeSegments.add (aRouteSegment);
				calculateTotalRevenue ();
				updateRevenueFrame ();

				// Add the New Route Segment Effect
				tMapCell = aRouteSegment.getMapCell ();
				tStartLocation = aRouteSegment.getStartLocation ();
				tEndLocation = aRouteSegment.getEndLocation ();
				if (aRouteAction != RouteAction.NO_ROUTE_ACTION) {
					aRouteAction.addNewRouteSegmentEffect (trainCompany, trainIndex, tMapCell, tStartLocation,
							tEndLocation);
				}
			}
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

	public boolean isEmpty () {
		return routeSegments.isEmpty ();
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

		System.out.println ("----------- Start Route Information Detail ----------");		// PRINTLOG
		System.out.println ((trainIndex + 1) + ". " + train.getName () + " Train, Total Revenue: " + totalRevenue
				+ " Center Count " + getCenterCount ());
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.printDetail ();
		}

		tCenterIndex = 1;
		for (RevenueCenter tRevenueCenter : revenueCenters) {
			System.out.println (tCenterIndex + ". Center with Revenue " + tRevenueCenter.getRevenue (phase) + " Train "
					+ (trainIndex + 1) + " is Using " + tRevenueCenter.getSelected (trainIndex + 1));
			tCenterIndex++;
		}
		System.out.println ("----------- End Route Information Detail ----------");
	}

	private void clearTrainFromMap () {
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.clearTrainOn ();
		}
	}

	public void clearTrainOn () {
		clearTrainFromMap ();
		routeSegments.removeAll (routeSegments);
		revenueCenters.removeAll (revenueCenters);
	}

	public boolean canBeReused () {
		boolean tCanBeReused;

		tCanBeReused = true;
		for (RouteSegment tRouteSegment : routeSegments) {
			if (tRouteSegment.trackIsInUse ()) {
				tCanBeReused = false;
			}
		}

		return tCanBeReused;
	}

	public RevenueCenter getRevenueCenterAt (int aRevenueCenterIndex) {
		RevenueCenter tRevenueCenter = RevenueCenter.NO_CENTER;
		
		if ((aRevenueCenterIndex >= 0) && (aRevenueCenterIndex < revenueCenters.size ())) {
			tRevenueCenter = revenueCenters.get (aRevenueCenterIndex);
		}
		
		return tRevenueCenter;
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

	public void clear () {
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
			tLastMapCell = getLastMapCell ();
			if (tLastMapCell == aSelectedMapCell) {
				tLastMapCellMatches = true;
			}
		}

		return tLastMapCellMatches;
	}

	public RouteSegment getLastRouteSegment () {
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

	public int getRouteCityCount () {
		int tRouteCityCount = 0;
		int tSegmentIndex = 0;

		for (RouteSegment tRouteSegment : routeSegments) {
			if (tRouteSegment.countableRevenueCenter (tSegmentIndex)) {
				if (tRouteSegment.hasCityOnTile ()) {
					tRouteCityCount++;
				}
			}
			tSegmentIndex++;
		}

		return tRouteCityCount;
	}

	public int getRouteTownCount () {
		int tRouteTownCount = 0;
		int tSegmentIndex = 0;

		for (RouteSegment tRouteSegment : routeSegments) {
			if (tRouteSegment.countableRevenueCenter (tSegmentIndex)) {
				if (tRouteSegment.hasTownOnTile ()) {
					tRouteTownCount++;
				}
			}
			tSegmentIndex++;
		}

		return tRouteTownCount;
	}

	public boolean isRouteTooLong () {
		boolean tRouteIsTooLong = false;
		int tCityCount;
		int tTownCount;
		int tRouteCityCount;
		int tRouteTownCount;
		int tRouteRCCount;
		int tTrueCityCount;

		tTrueCityCount = train.getTrueCityCount ();

		// If the True City Count is Infinite, it is never too long
		if (tTrueCityCount == Train.INFINITE_COUNT) {
			tRouteIsTooLong = false;
		} else {
			tCityCount = train.getCityCount ();
			tTownCount = train.getTownCount ();
			if (routeSegments.size () == 0) {
				tRouteIsTooLong = false;
			} else {
				tRouteCityCount = getRouteCityCount ();
				tRouteTownCount = getRouteTownCount ();
				tRouteRCCount = tRouteCityCount + tRouteTownCount;
				if (tTownCount == -1) {
					if (tRouteRCCount > tCityCount) {
						tRouteIsTooLong = true;
					}
				} else {
					if (tRouteCityCount > tCityCount) {
						tRouteIsTooLong = true;
					}
					if (tRouteTownCount > tTownCount) {
						tRouteIsTooLong = true;
					}
					if (tRouteRCCount > (tCityCount + tTownCount)) {
						tRouteIsTooLong = true;
					}
				}
			}
		}

		return tRouteIsTooLong;
	}

	public int isValidRoute () {
		int tValidRoute = 0;

		// Return Code --- Description
		// 1 Valid Route, with Token, not blocked
		// 0 No Train
		// -1 Route Size 0
		// -2 No Revenue Centers Found on Route
		// -3 Route has no Corporation Station
		// -4 Route is Looped somewhere (reusing the same Revenue Center)
		// -5 Route is Blocked by other Corporate Stations

		if (train != Train.NO_TRAIN) {
			if (routeSegments.size () == 0) {
				tValidRoute = -1;
			} else if (getCenterCount () > 1) {
				if (hasACorpStation ()) {
					if (isRouteLooped ()) {
						tValidRoute = -4;
					} else {
						if (isRouteOpen ()) {
							tValidRoute = 1;
						} else {
							tValidRoute = -5;
						}
					}
				} else {
					tValidRoute = -3;
				}
			} else {
				tValidRoute = -2;
			}
		}

		return tValidRoute;
	}

	private boolean isRouteLooped () {
		boolean tIsRouteLooped = false;
		int tSegmentCount;
		int tSegmentIndex;
		RouteSegment tRouteSegment;
		NodeInformation tARouteNode;
		String tMapCellID;

		tSegmentCount = getSegmentCount ();
		if (tSegmentCount > 2) {
			for (tSegmentIndex = 0; (tSegmentIndex < tSegmentCount) && (!tIsRouteLooped); tSegmentIndex++) {
				tRouteSegment = routeSegments.get (tSegmentIndex);
				tMapCellID = tRouteSegment.getMapCellID ();
				tARouteNode = tRouteSegment.getStartNode ();
				tIsRouteLooped = isRevenueCenterReused (tMapCellID, tARouteNode, tSegmentIndex);
				if (!tIsRouteLooped) {
					tARouteNode = tRouteSegment.getEndNode ();
					tIsRouteLooped = isRevenueCenterReused (tMapCellID, tARouteNode, tSegmentIndex);
				}
			}
		}

		return tIsRouteLooped;
	}

	private boolean isRevenueCenterReused (String aMapCellId, NodeInformation aRouteNode, int aSourceIndex) {
		boolean tIsRouteLooped = false;
		Location tLocation;
		Location tNodeLocation;
		int tSegmentIndex;
		int tSegmentCount;
		RouteSegment tRouteSegment;
		String tMapCellId;
		String tPreviousMapCellId;
		NodeInformation tRouteNode;

		tLocation = aRouteNode.getLocation ();
		tSegmentCount = getSegmentCount ();
		tPreviousMapCellId = "";
		for (tSegmentIndex = aSourceIndex + 1; (tSegmentIndex < tSegmentCount) && (!tIsRouteLooped); tSegmentIndex++) {
			tRouteSegment = routeSegments.get (tSegmentIndex);
			tMapCellId = tRouteSegment.getMapCellID ();
			if (!tPreviousMapCellId.equals ("")) {
				if (aMapCellId.equals (tMapCellId)) {
					tRouteNode = tRouteSegment.getStartNode ();
					tNodeLocation = tRouteNode.getLocation ();
					tIsRouteLooped = tLocation.isSameLocationValue (tNodeLocation);
					if (!tIsRouteLooped) {
						tRouteNode = tRouteSegment.getEndNode ();
						tNodeLocation = tRouteNode.getLocation ();
						tIsRouteLooped = tLocation.isSameLocationValue (tNodeLocation);
					}
				}
			}
			tPreviousMapCellId = tMapCellId;
		}

		return tIsRouteLooped;
	}

	public void updateReusedRoute (int aPhase, int aCorpID) {
		int tSegmentCount;
		int tSegmentIndex;
		RouteSegment tRouteSegment;

		tSegmentCount = getSegmentCount ();
		revenueCenters.clear ();
		for (tSegmentIndex = 0; tSegmentIndex < tSegmentCount; tSegmentIndex++) {
			tRouteSegment = routeSegments.get (tSegmentIndex);
			tRouteSegment.updateTile ();
			tRouteSegment.updateRevenues (aPhase);
			tRouteSegment.updateOpenFlow (this, aCorpID);
			addRevenueCenter (tRouteSegment);
		}
		calculateTotalRevenue ();
		updateRevenueFrame ();
	}

	private boolean isRouteOpen () {
		boolean tIsRouteOpen = true;
		boolean tIsSegmentRouteOpen;
		int tSegmentCount;
		int tSegmentIndex;
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

	private boolean hasACorpStation () {
		boolean tHasACorpStation = false;
		int tSegmentIndex;
		int tSegmentCount;
		RouteSegment tRouteSegment;

		tSegmentCount = getSegmentCount ();
		for (tSegmentIndex = 0; tSegmentIndex < tSegmentCount; tSegmentIndex++) {
			tRouteSegment = routeSegments.get (tSegmentIndex);
			tHasACorpStation = tHasACorpStation || tRouteSegment.hasACorpStation ();
		}

		return tHasACorpStation;
	}

	public void updateRouteButtons () {
		train.setCurrentRouteInformation (this);
		trainRevenueFrame.updateAllFrameButtons ();
	}

	public void extendRouteInformation (RouteSegment aRouteSegment, int aPhase, int aCorpID, RouteAction aRouteAction) {
		// Break this into three separate subroutines, based upon Criteria
		// Generic Testing Criteria to be met before the Route Segment can be
		// successfully added/modified:
		// Test 1: Route Segment must exist on the Tile that is on the mapCell, based
		// upon both Start & End Locations, in the given orientation
		// When Adding a new Route Segment:
		// Test 2: the MapCells must be Adjacent
		// Test 3: the Tiles on the MapCells must both have a Track to the adjoining
		// sides
		// Test 4: the Track on the Tile from the Previous Route Segment MapCell must
		// NOT be in use by any train
		// Test 5: the Track on the Tile from the new Route Segment's MapCell must NOT
		// be in use by any train
		// Test 6: the Adjoining Sides of the two MapCells must NOT be in use by any
		// train
		// When in Scenario 2, a Track is being cycled in,
		// Test 1: for the track being swapped in, connecting to the "new" end point, if
		// a side,
		// Test 1A: must not be used by any train
		// Test 1B: the neighboring mapCell's must have a Tile
		// Test 1C: the neighboring mapCell's must have a Track on the Tile that
		// connects to the adjoining side
		// Test 1D: the neighboring mapCell's Track that is adjoining must NOT be in use
		// by any train.
		// Trying to mesh these together into a single routine (first attempt) leads to
		// DONE: 1. Handle Scenario 1 - Add First Segment to Route

		// DONE: 2. Handle Scenario 2 - Adding Segment # 2 to Route (First Route Segment
		// has RC but no valid end point)

		// DONE: 3. Handle Scenario 3 - Cycling to Next Track Segment on Last Route
		// Segment due to re-click of MapCell

		// DONE: 4. Handle Scenario 4 - Adding Segment # 3+ to Route, when Previous
		// Route Segment has Valid End Point
		boolean tContinueWork = true;
		int tInitialSegmentCount;
		MapCell tNewMapCell;

		tInitialSegmentCount = getSegmentCount ();
		if (tInitialSegmentCount == 0) {
			System.out.println ("\nScenario 1 - Add Segment # 1");		// PRINTLOG
			addRouteSegment (aRouteSegment, aRouteAction);
		} else if (tInitialSegmentCount == 1) {
			System.out.println ("\nScenario 2 - Add Segment # 2");		// PRINTLOG
			if (previousSegmentNeedsFill ()) {
				tContinueWork = fillEndPoint (aRouteSegment, aRouteAction);
			} else {
				tContinueWork = true;
			}
			if (tContinueWork) {
				tContinueWork = addNextRouteSegment (aRouteSegment, aCorpID, aRouteAction);
			}
		} else if (tInitialSegmentCount > 1) {
			tNewMapCell = aRouteSegment.getMapCell ();
			if (lastMapCellIs (tNewMapCell)) {
				System.out.println ("\nScenario 3 - Cycle Track");		// PRINTLOG
				cycleToNextTrack (aRouteAction, aCorpID);
			} else {
				System.out.println ("\nScenario 4 - Add Route Segment # 3+");	// PRINTLOG
				tContinueWork = addNewPreviousSegment (aRouteSegment, aPhase, aCorpID, aRouteAction);
				if (tContinueWork) {
					tContinueWork = addNextRouteSegment (aRouteSegment, aCorpID, aRouteAction);
				}
			}
		}
		updateRouteButtons ();
	}
	

	public void cycleToNextTrack (RouteAction aRouteAction, int aCorpID) {
		RouteSegment tLastRouteSegment;
		Track tLastTrack;
		Track tNextTrack;
		int tTrainNumber;
		boolean tCycledToNextTrack;
		MapCell tMapCell;
		Location tStartLocation;
		Location tEndLocation;
		Location tOldEndLocation;

		tTrainNumber = getTrainIndex () + 1;
		tLastRouteSegment = getLastRouteSegment ();
		tLastTrack = tLastRouteSegment.getTrack ();
		tOldEndLocation = tLastRouteSegment.getEndLocation ();
		tCycledToNextTrack = tLastRouteSegment.cycleToNextTrack ();
		if (tCycledToNextTrack) {
			tNextTrack = tLastRouteSegment.getTrack ();
			swapTrackHighlights (tTrainNumber, tLastTrack, tNextTrack);

			tMapCell = tLastRouteSegment.getMapCell ();
			tStartLocation = tLastRouteSegment.getStartLocation ();
			tEndLocation = tLastRouteSegment.getEndLocation ();
			moveEndRoute (tLastRouteSegment, tLastRouteSegment, tMapCell, tMapCell);
			updateRevenueCenterInfo (aCorpID, tLastRouteSegment, tEndLocation, tOldEndLocation);

			// Add the New Route Segment Effect
			if (aRouteAction != RouteAction.NO_ROUTE_ACTION) {
				aRouteAction.addSetNewEndPointEffect (trainCompany, trainIndex, tMapCell, tStartLocation, tEndLocation);
			}
			updateRevenueFrame ();
		}
	}

	public void updateRevenueFrame () {
		trainRevenueFrame.updateRevenues (this);
		trainRevenueFrame.updateAllFrameButtons ();
	}
	

	public void updateRevenueCenterInfo (int aCorpID, RouteSegment aLastRouteSegment, Location aNewEndLocation,
			Location aOldEndLocation) {
		if ((!aOldEndLocation.isSide ()) && (!aOldEndLocation.isNoLocation ())) {
			removeLastRevenueCenter ();
		}
		if (!aNewEndLocation.isSide ()) {
			aLastRouteSegment.applyRCInfo (phase, aCorpID);
			addRevenueCenter (aLastRouteSegment);
		}
	}
	

	public void swapTrackHighlights (int aTrainNumber, Track aOldTrack, Track aNewTrack) {
		int tTrainIndex = getTrainIndex () + 1;

		aOldTrack.clearTrainNumber ();
		aNewTrack.setTrainNumber (tTrainIndex);
	}
	

	private boolean addNewPreviousSegment (RouteSegment aRouteSegment, int aPhase, int aCorpID,
			RouteAction aRouteAction) {
		boolean tAddNewPreviousSegment = false;
		RouteSegment tPreviousSegment;
		RouteSegment tNewPreviousSegment;
		MapCell tCurrentMapCell;
		MapCell tPreviousMapCell;
		Track tTrack;
		Location tPreviousEndLocation;
		int tSegmentCount;
		int tTrainNumber;
		int tPreviousEnd;
		int tPreviousSide;
		int tCurrentCellNeighborSide;
		RevenueCenter tPreviousRevenueCenter;

		tSegmentCount = getSegmentCount ();
		tPreviousSegment = getRouteSegment (tSegmentCount - 1);
		tCurrentMapCell = aRouteSegment.getMapCell ();
		tPreviousMapCell = tPreviousSegment.getMapCell ();
		clearWarningMessage ();
		if (tCurrentMapCell.isNeighbor (tPreviousMapCell)) {
			if (tCurrentMapCell.hasConnectingTrackTo (tPreviousMapCell)) {
				tPreviousEndLocation = tPreviousSegment.getEndLocation ();
				tPreviousSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
				tPreviousEnd = tPreviousEndLocation.getLocation ();
				if (!tPreviousEndLocation.isSide ()) {
					tTrack = tPreviousMapCell.getTrackFromStartToEnd (tPreviousEnd, tPreviousSide);
					if (!tTrack.isTrackUsed ()) {
						tNewPreviousSegment = new RouteSegment (tPreviousMapCell);
						tPreviousRevenueCenter = tPreviousSegment.getRevenueCenter ();
						setStartSegment (tNewPreviousSegment, tPreviousRevenueCenter, aPhase, aCorpID);

						tNewPreviousSegment.setEndNodeLocationInt (tPreviousSide, phase);

						addRouteSegment (tNewPreviousSegment, aRouteAction);
						tTrainNumber = getTrainIndex () + 1;
						tNewPreviousSegment.setTrainOnTrack (tTrack, tTrainNumber);
						tAddNewPreviousSegment = true;
					} else {
						warningMessage = "Previous Map Cell's Track is in Use";
						System.err.println (warningMessage);
					}
				} else {
					tCurrentCellNeighborSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
					if (tCurrentCellNeighborSide != tPreviousEnd) {
						warningMessage = "Previous Map Cell Track ended on " + tPreviousEnd
								+ " Current Map Cell track connects to " + tCurrentCellNeighborSide;
						System.err.println (warningMessage);
					} else {
						tAddNewPreviousSegment = true;
					}
				}
			} else {
				warningMessage = "No connecting Track between MapCell " + tPreviousMapCell.getCellID () + " and "
						+ tCurrentMapCell.getCellID ();
				System.err.println (warningMessage);
			}
		} else {
			warningMessage = "MapCell " + tPreviousMapCell.getCellID () + " and " + tCurrentMapCell.getCellID ()
					+ " are not Neighbors";
			System.err.println (warningMessage);
		}

		return tAddNewPreviousSegment;
	}

	public String getWarningMessage () {
		return warningMessage;
	}

	public void clearWarningMessage () {
		warningMessage = NO_MESSAGE;
	}
	

	private boolean addNextRouteSegment (RouteSegment aRouteSegment, int aCorpID, RouteAction aRouteAction) {
		boolean tAddNextRouteSegment = false;
		int tCurrentSide;
		MapCell tCurrentMapCell;
		MapCell tPreviousMapCell;
		RouteSegment tPreviousSegment;
		Location tPossibleEnd;
		Track tTrack;

		tPreviousSegment = getRouteSegment (getSegmentCount () - 1);
		tCurrentMapCell = aRouteSegment.getMapCell ();
		tPreviousMapCell = tPreviousSegment.getMapCell ();
		tCurrentSide = tCurrentMapCell.getSideToNeighbor (tPreviousMapCell);
		aRouteSegment.setStartNodeLocationInt (tCurrentSide);

		tPossibleEnd = moveEndRoute (tPreviousSegment, aRouteSegment, tPreviousMapCell, tCurrentMapCell);

		aRouteSegment.setEndNodeLocation (tPossibleEnd);
		tTrack = aRouteSegment.getTrack ();

		if (!tTrack.isTrackUsed ()) {
			aRouteSegment.applyRCInfo (phase, aCorpID);
			tAddNextRouteSegment = addTheRouteSegment (aRouteSegment, aRouteAction);
		}

		return tAddNextRouteSegment;
	}
	

	public Location moveEndRoute (RouteSegment aPreviousSegment, RouteSegment aRouteSegment, MapCell aPreviousMapCell,
			MapCell aCurrentMapCell) {
		Location tPossibleEnd;
		Location tPreviousEnd;

		tPossibleEnd = aRouteSegment.getPossibleEnd ();
		tPreviousEnd = aPreviousSegment.getEndLocation ();
		aPreviousMapCell.removeEndRoute (tPreviousEnd);
		aCurrentMapCell.addEndRoute (tPossibleEnd);

		return tPossibleEnd;
	}
	

	public boolean addTheRouteSegment (RouteSegment aRouteSegment, RouteAction aRouteAction) {
		int tTrainNumber;
		Track tTrack;

		tTrack = aRouteSegment.getTrack ();
		if (tTrack != Track.NO_TRACK) {
			tTrainNumber = getTrainIndex () + 1;
			aRouteSegment.setTrainOnTrack (tTrack, tTrainNumber);
		}
		addRouteSegment (aRouteSegment, aRouteAction);

		return true;
	}

	private RouteSegment getPreviousRouteSegment () {
		RouteSegment tPreviousSegment;
		int tSegmentCount;
		
		tSegmentCount = getSegmentCount ();
		tPreviousSegment = getRouteSegment (tSegmentCount - 1);

		return tPreviousSegment;
	}
	
	private boolean previousSegmentNeedsFill () {
		RouteSegment tPreviousSegment;
		boolean tPreviousSegmentNeedsFill;
		int tPreviousEnd;
		
		tPreviousSegment = getPreviousRouteSegment ();
		tPreviousEnd = tPreviousSegment.getEndLocationInt ();

		if (tPreviousEnd == Location.NO_LOCATION) {
			tPreviousSegmentNeedsFill = true;
		} else {
			tPreviousSegmentNeedsFill = false;
		}
		
		return tPreviousSegmentNeedsFill;
	}
	
	private boolean fillEndPoint (RouteSegment aRouteSegment, RouteAction aRouteAction) {
		boolean tFillEndPoint;
		RouteSegment tPreviousSegment;
		Track tPreviousTrack;
		MapCell tCurrentMapCell;
		MapCell tPreviousMapCell;
		int tPreviousSide;
		int tPreviousEnd;
		int tPreviousStart;
		int tNewPreviousStart;

		tFillEndPoint = false;
		tPreviousSegment = getPreviousRouteSegment ();
		tCurrentMapCell = aRouteSegment.getMapCell ();
		tPreviousMapCell = tPreviousSegment.getMapCell ();
		clearWarningMessage ();
		if (tCurrentMapCell.isNeighbor (tPreviousMapCell)) {

			if (tCurrentMapCell.hasConnectingTrackTo (tPreviousMapCell)) {
				tPreviousSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
				tPreviousEnd = tPreviousSegment.getEndLocationInt ();
				tPreviousStart = tPreviousSegment.getStartLocationInt ();
				if (tPreviousEnd == Location.NO_LOCATION) {
					if (tPreviousMapCell.hasConnectingTrackBetween (tPreviousStart, tPreviousSide)) {
						tFillEndPoint = setTrainOn (aRouteAction, tPreviousSegment, tPreviousMapCell, tPreviousSide,
								tPreviousStart);
					} else if (tPreviousStart == Location.DEAD_END_LOC) {
						tPreviousTrack = tPreviousMapCell.getTrackFromSide (tPreviousSide);
						tNewPreviousStart = tPreviousTrack.getEnterLocationInt ();
						if (tPreviousSide == tNewPreviousStart) {
							tNewPreviousStart = tPreviousTrack.getExitLocationInt ();
						}
						tFillEndPoint = setTrainOn (aRouteAction, tPreviousSegment, tPreviousMapCell, tPreviousSide,
								tNewPreviousStart);
					} else {
						warningMessage = "Previous Map Cell's Tile does not have Track Segment between " + tPreviousSide
								+ " and " + tPreviousStart;
						System.err.println (warningMessage);
					}
				} else {
					warningMessage = "The Previous End is not NO_LOCATION - it is " + tPreviousEnd;
					System.err.println (warningMessage);
				}
			} else {
				warningMessage = "No connecting Track between MapCell " + tPreviousMapCell.getCellID () + " and "
						+ tCurrentMapCell.getCellID ();
				System.err.println (warningMessage);
			}
		} else {
			warningMessage = "MapCell " + tPreviousMapCell.getCellID () + " and " + tCurrentMapCell.getCellID ()
					+ " are not Neighbors";
			System.err.println (warningMessage);
		}

		return tFillEndPoint;
	}

	public boolean setTrainOn (RouteAction aRouteAction, RouteSegment aPreviousSegment, MapCell aPreviousMapCell,
			int aPreviousSide, int aPreviousStart) {
		int tTrainNumber;
		Track tTrack;
		Location tPreviousStartLoc;
		Location tPreviousEndLoc;
		boolean tFillEndPoint = false;

		tTrack = aPreviousMapCell.getTrackFromStartToEnd (aPreviousStart, aPreviousSide);
		clearWarningMessage ();
		if (tTrack != Track.NO_TRACK) {
			if (!tTrack.isTrackUsed ()) {
				tTrainNumber = getTrainIndex () + 1;
				aPreviousSegment.setEndNodeLocationInt (aPreviousSide, phase);
				if (aRouteAction != RouteAction.NO_ROUTE_ACTION) {

					tPreviousStartLoc = new Location (aPreviousStart);
					tPreviousEndLoc = new Location (aPreviousSide);
					aRouteAction.addSetNewEndPointEffect (trainCompany, trainIndex, aPreviousMapCell, tPreviousStartLoc,
							tPreviousEndLoc);
				}
				aPreviousSegment.setTrainOnTrack (tTrack, tTrainNumber);
				tFillEndPoint = true;
			} else {
				warningMessage = "Previous Map Cell's Track is in Use";
				System.err.println (warningMessage);
			}
		} else {
			warningMessage = "No Track Found from " + aPreviousStart + " to " + aPreviousSide + " on MapCell "
					+ aPreviousMapCell.getID () + " with Tile " + aPreviousMapCell.getTileNumber ();
			System.err.println (warningMessage);
		}

		return tFillEndPoint;
	}

	public void removeEndIfMapCell (MapCell aMapCell) {
		RouteSegment tLastRouteSegment;
		int tLastRouteSegmentIndex;
		boolean tRouteSegmentsRemoved;
		String tOperatingRoundID;
		MapCell tLastMapCell;
		RemoveRouteSegmentsAction tRemoveRouteSegmentsAction;
		GameManager tGameManager;
		
		tLastRouteSegmentIndex = routeSegments.size () - 1;
		tLastRouteSegment = routeSegments.get (tLastRouteSegmentIndex);
		tLastMapCell = tLastRouteSegment.getMapCell ();
		if (tLastMapCell.getID ().equals (aMapCell.getCellID ())) {
			tOperatingRoundID = trainCompany.getOperatingRoundID ();
			tRemoveRouteSegmentsAction = new RemoveRouteSegmentsAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					trainCompany);
			tRemoveRouteSegmentsAction.addRemoveRouteSegmentEffect (trainCompany, tLastRouteSegmentIndex, trainIndex, aMapCell);
			tRouteSegmentsRemoved = removeSegment (tLastRouteSegmentIndex);
			if (tRouteSegmentsRemoved) {
				if (tLastRouteSegment.isStartASide ()) {
					tRouteSegmentsRemoved = removeSegmentsBackToJunction (tRemoveRouteSegmentsAction);
				}
				if (tRouteSegmentsRemoved) {
						tGameManager = trainCompany.getGameManager ();
						tGameManager.addAction (tRemoveRouteSegmentsAction);
				} else {
					System.err.println ("Failed to Removed Route Segments to Junction ");
				}
			} else {
				System.err.println ("Failed to Removed Route Segment " + tLastRouteSegmentIndex);
			}
		} else {
			System.err.println ("Last MapCell on Route DOES NOT match the one to remove");
		}
		updateRevenueFrame ();
	}

	/**
	 * Remove the Route Segment specified by the provided index. If The end Location is not a Side 
	 * then remove the Last Revenue Center as well.
	 * 
	 * @param aRouteSegmentIndex
	 */
	
	public boolean removeSegment (int aRouteSegmentIndex) {
		Location tEndLocation;
		RouteSegment tLastRouteSegment;
		boolean tRouteSegmentRemoved;
		
		tLastRouteSegment = routeSegments.get (aRouteSegmentIndex);
		if (tLastRouteSegment != RouteSegment.NO_ROUTE_SEGMENT) {
			tLastRouteSegment.clearTrainOn ();
			routeSegments.remove (aRouteSegmentIndex);
			tEndLocation = tLastRouteSegment.getEndLocation ();
			if (! tEndLocation.isSide ()) {
				removeLastRevenueCenter ();
			}
			tRouteSegmentRemoved = true;
		} else {
			tRouteSegmentRemoved = false;
		}
		
		return tRouteSegmentRemoved;
	}
	
	public boolean removeSegmentsBackToJunction (RemoveRouteSegmentsAction aRemoveRouteSegmentsAction) {
		RouteSegment tLastRouteSegment;
		int tLastRouteSegmentIndex;
		int tTrackCount;
		boolean tFoundJunction;
		boolean tRouteSegmentRemoved;
		MapCell tLastMapCell;
		Location tStartLocation;
		
		tLastRouteSegmentIndex = routeSegments.size () - 1;
		tFoundJunction = false;

		if (tLastRouteSegmentIndex == 0) {
			tRouteSegmentRemoved = true;
		} else {
			tRouteSegmentRemoved = false;
			while ((tLastRouteSegmentIndex > 0) && (tFoundJunction == false)) {
				tLastRouteSegment = routeSegments.get (tLastRouteSegmentIndex);
				tStartLocation = tLastRouteSegment.getStartLocation ();
				tLastMapCell = tLastRouteSegment.getMapCell ();
				if (tStartLocation.isSide ()) {
					tTrackCount = tLastMapCell.getTrackCountFromSide (tStartLocation);
					if (tTrackCount > 1) {
						tFoundJunction = true;
					}
				} else {
					tFoundJunction = true;
				}
				
				tRouteSegmentRemoved = removeSegment (tLastRouteSegmentIndex);
				if (tRouteSegmentRemoved) {
					aRemoveRouteSegmentsAction.addRemoveRouteSegmentEffect (trainCompany, tLastRouteSegmentIndex, trainIndex, tLastMapCell);
					tLastRouteSegmentIndex--;
				} else {
					tLastRouteSegmentIndex = 0;
				}
			}
		}
		
		return tRouteSegmentRemoved;
	}
	
	public void setStartSegment (RouteSegment aRouteSegment, RevenueCenter aSelectedRevenueCenter, int aPhase,
			int aCorpID) {
		NodeInformation tNode;
		Location tLocation;

		tLocation = new Location ();
		tNode = buildNodeInformation (aSelectedRevenueCenter, tLocation, aPhase, aCorpID);
		aRouteSegment.setStartNode (tNode);
	}

	public NodeInformation buildNodeInformation (RevenueCenter aRevenueCenter, Location aLocation, int aPhase,
			int aCorpID) {
		boolean tCorpStation;
		boolean tOpenFlow;
		boolean tHasRevenueCenter;
		int tRevenue;
		int tBonus;
		Location tLocation;
		NodeInformation tNode;

		if (aRevenueCenter == RevenueCenter.NO_CENTER) {
			tCorpStation = false;
			tOpenFlow = true;
			tHasRevenueCenter = false;
			tRevenue = 0;
			tLocation = aLocation;
		} else {
			tHasRevenueCenter = true;
			tCorpStation = aRevenueCenter.cityHasStation (aCorpID);
			tOpenFlow = determineOpenFlow (aRevenueCenter, tCorpStation);
			tRevenue = aRevenueCenter.getRevenue (aPhase);
			tLocation = aRevenueCenter.getLocation ();
		}

		tBonus = 0; // TODO: If Selected City has Cattle, Port, etc that will add a Bonus, put that
					// here

		tNode = new NodeInformation (tLocation, tCorpStation, tOpenFlow, tHasRevenueCenter, tRevenue, tBonus,
				aRevenueCenter);

		return tNode;
	}

	/**
	 * Determine if the Location is open to this Company to flow through. If the
	 * RevenueCenter provides is NO_CENTER -- YES A Dead End -- NO If not a City (no
	 * RevenueCenter, or a Town, or Double Town - YES If a City, and it has the
	 * Corporation Station (passed in) -- YES If a City, and it has an Open Station
	 * -- YES Otherwise, it is blocked --- NO
	 *
	 * @param aRevenueCenter The Revenue Center under consideration,
	 * @param aCorpStation   - True is this corporation has a Station present
	 * @return True if the the answer above is YES, otherwise FALSE
	 */
	public boolean determineOpenFlow (RevenueCenter aRevenueCenter, boolean aCorpStation) {
		boolean tOpenFlow;
		boolean tIsCity;
		boolean tIsDeadEnd;

		if (aRevenueCenter == RevenueCenter.NO_CENTER) {
			tOpenFlow = true;
		} else {
			tIsCity = aRevenueCenter.isCity ();
			tIsDeadEnd = aRevenueCenter.isDeadEnd ();
			if (tIsDeadEnd) { // if a Dead-End City, no Flow beyond this location
				tOpenFlow = false;
			} else if (tIsCity) {
				if (aCorpStation) { // If this is a City, and it has the Current Operating Company matches the Token
									// Then can flow beyond
					tOpenFlow = true;
				} else { // If this is a City, then if there is an Open Station, Flow can continue
					tOpenFlow = aRevenueCenter.isOpen ();
				}
			} else { // If this is not a City, it is a Town, and Flow is allowed further
				tOpenFlow = true;
			}
		}

		return tOpenFlow;
	}

	public int getPhase () {
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

	public void addReuseRouteAction (Train aTrain) {
		StartRouteAction tStartRouteAction;
		RouteInformation tRouteInformation;
		boolean tStartedRoute = false;

		tRouteInformation = aTrain.getCurrentRouteInformation ();
		tStartRouteAction = new StartRouteAction (ActorI.ActionStates.OperatingRound, tRouteInformation.getRoundID (),
				trainCompany);
		for (RouteSegment tRouteSegment : routeSegments) {
			if (tStartedRoute) {
				// AddNewRouteSegmentEffect
				tStartRouteAction.addNewRouteSegmentEffect (trainCompany, trainIndex, tRouteSegment.getMapCell (),
						tRouteSegment.getStartLocation (), tRouteSegment.getEndLocation ());
			} else {
				// StartRouteEffect
				tStartRouteAction.addStartRouteEffect (trainCompany, trainIndex, tRouteSegment.getMapCell (),
						tRouteSegment.getStartLocation (), tRouteSegment.getEndLocation ());
				tStartedRoute = true;
			}
		}
		trainCompany.addAction (tStartRouteAction);
	}
}
