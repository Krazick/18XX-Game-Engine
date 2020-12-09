package ge18xx.train;

import java.awt.Color;
import java.util.ArrayList;

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
	TrainCompany trainCompany;
	
	// Collection of Route Segments
	public RouteInformation (Train aTrain, int aTrainIndex, Color aColor, String aRoundID, int aRegionBonus, 
				int aSpecialBonus, int aPhase, TrainCompany aTrainCompany) {
		setTrain (aTrain);
		setTrainIndex (aTrainIndex);
		setColor (aColor);
		setRoundID (aRoundID);
		setRegionBonus (aRegionBonus);
		setSpecialBonus (aSpecialBonus);
		setTotalRevenue (0);
		phase = aPhase;
		setTrainCompany (aTrainCompany);
		routeSegments = new ArrayList<RouteSegment> ();
	}
	
	public void addRouteSegment (RouteSegment aRouteSegment) {
		routeSegments.add (aRouteSegment);
		calculateTotalRevenue ();
	}
		
	public int getSegmentCount () {
		return routeSegments.size ();
	}
	
	public int getCenterCount () {
		int tCenterCount;
		
		tCenterCount = 0;
		for (RouteSegment tRouteSegment : routeSegments) {
			if (tRouteSegment.hasRevenueCenter ()) {
				tCenterCount++;
			}
		}
		
		return tCenterCount;
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
		int tSegmentRevenue;
		MapCell tMapCell, tPreviousMapCell;
		
		setTotalRevenue (0);
		tPreviousMapCell = MapCell.NO_MAP_CELL;
		for (RouteSegment tRouteSegment : routeSegments) {
			tMapCell = tRouteSegment.getMapCell ();
			// Don't add the Revenue a second time if the Previous MapCell is the same as the current MapCell
			// If a Tile has two (or more) Revenue Centers connected via tracks, this needs to account for the 
			// different RevenueCenters
			if (tMapCell != tPreviousMapCell) {
				tSegmentRevenue = tRouteSegment.getRevenue (phase);
				addToTotalRevenue (tSegmentRevenue);
			}
			tPreviousMapCell = tMapCell;
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
		System.out.println ((trainIndex + 1) + ". " +train.getName () + " Train, Total Revenue: " + totalRevenue);
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.printDetail ();
		}
	}
	
	public void clearTrainOn () {
		for (RouteSegment tRouteSegment : routeSegments) {
			tRouteSegment.clearTrainOn ();
		}
	}

	public void clear() {
		clearTrainOn ();
		setTotalRevenue (0);
	}
	
	public void setTrainCurrentRouteInformation () {
		train.setCurrentRouteInformation (this);
	}
}
