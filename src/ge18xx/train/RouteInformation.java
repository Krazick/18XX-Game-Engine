package ge18xx.train;

import java.awt.Color;
import java.util.ArrayList;

import ge18xx.center.RevenueCenter;
import ge18xx.company.TrainCompany;
import ge18xx.map.MapCell;

public class RouteInformation {
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
	TrainCompany trainCompany;
	TrainRevenueFrame trainRevenueFrame;
	
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
	}
	
	public void setTrainRevenueFrame (TrainRevenueFrame aTrainRevenueFrame) {
		trainRevenueFrame = aTrainRevenueFrame;
	}
	
	public TrainRevenueFrame getTrainRevenueFrame () {
		return trainRevenueFrame;
	}
	
	public void addRouteSegment (RouteSegment aRouteSegment) {
		RevenueCenter tRevenueCenter;
		
		if (revenueCenters != null) {
			if (aRouteSegment.hasRevenueCenter()) {
				tRevenueCenter = aRouteSegment.getRevenueCenter();
				if (! isSameRCasLast (tRevenueCenter)) {
					revenueCenters.add (tRevenueCenter);
				}
			}
			routeSegments.add (aRouteSegment);
			calculateTotalRevenue ();
			updateConfirmRoute ();
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
	
	public void setTrainCompany (TrainCompany aTrainCompany) {
		trainCompany = aTrainCompany;
	}
	
	public TrainCompany getTrainCompany () {
		return trainCompany;
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
			System.out.println (tCenterIndex + ". Center with Revenue " + tRevenueCenter.getRevenue (phase));
			tCenterIndex++;
		}
		System.out.println ("----------- End Route Information Detail ----------");
	}
	
	public void clearTrainOn () {
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.clearTrainOn ();
		}
	}

	public int getRevenueAt (int aRevenueCenterIndex) {
		int tRevenue = 0;
		RevenueCenter tRevenueCenter;
		
		if ((aRevenueCenterIndex > 0) && (aRevenueCenterIndex <= revenueCenters.size ())) {
			tRevenueCenter = revenueCenters.get (aRevenueCenterIndex);
			tRevenue = tRevenueCenter.getRevenue (phase);
		}
		
		return tRevenue;
	}
	
	public int getRevenueAt (int aRevenueCenterIndex, int aPhase) {
		int tRevenue;
		RevenueCenter tFoundRevenueCenter;
		
		tRevenue = 0;
		if ((aRevenueCenterIndex > 0) && (aRevenueCenterIndex <= revenueCenters.size ())) {
			tFoundRevenueCenter = revenueCenters.get (aRevenueCenterIndex - 1);
			tRevenue = tFoundRevenueCenter.getRevenue (phase);
		} else {
			System.err.println ("\nHave " + revenueCenters.size () + " Centers, asked for " + aRevenueCenterIndex);
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

	public void cycleToNextTrack () {
		RouteSegment tLastRouteSegment;
		
		tLastRouteSegment = getLastRouteSegment ();
		tLastRouteSegment.cycleToNextTrack ();
		printDetail ();
	}

	public void enableAllSelectRoutes() {
		if (trainRevenueFrame != null) {
			trainRevenueFrame.enableAllSelectRoutes ();
		}
	}

	public boolean isValidRoute() {
		boolean tIsValidRoute = false;
		
		if (getCenterCount () > 1) {
			tIsValidRoute = true;
		}
		
		return tIsValidRoute;
	}
	
	public void updateConfirmRoute () {
		String tToolTipText;
		
		if (isValidRoute ()) {
			trainRevenueFrame.enableConfirmRouteButton (trainIndex);
		} else {
			tToolTipText = "Route is not Valid";
			trainRevenueFrame.disableConfirmRouteButton (trainIndex, tToolTipText);
		}
	}
}
