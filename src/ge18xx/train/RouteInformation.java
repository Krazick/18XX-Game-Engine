package ge18xx.train;

import java.awt.Color;
import java.util.ArrayList;

import ge18xx.company.TrainCompany;

public class RouteInformation {
	public static RouteInformation NO_ROUTE_INFORMATION = null;
	Train train;		// Reference to actual Train
	Color color;		// Route Color
	int totalRevenue;	// Total Revenue for Route
	String roundID;		// Operating Round ID when Route was Run
	int regionBonus;	// Bonus (Special Region-to-Region connection)
	int specialBonus;	// Bonus (Special Train/Car used)
	ArrayList<RouteSegment> routeSegments;
	TrainCompany trainCompany;
	
	// Collection of Route Segments
	public RouteInformation (Train aTrain, Color aColor, String aRoundID, int aRegionBonus, int aSpecialBonus, TrainCompany aTrainCompany) {
		setTrain (aTrain);
		setColor (aColor);
		setRoundID (aRoundID);
		setRegionBonus (aRegionBonus);
		setSpecialBonus (aSpecialBonus);
		setTotalRevenue (0);
		setTrainCompany (aTrainCompany);
		routeSegments = new ArrayList<RouteSegment> ();
	}
	
	public void addRouteSegment (RouteSegment aRouteSegment) {
		routeSegments.add(aRouteSegment);
	}
	
	public int getSegmentCount () {
		return routeSegments.size ();
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
	
	public void setTrainCompany (TrainCompany aTrainCompany) {
		trainCompany = aTrainCompany;
	}
	
	public TrainCompany getTrainCompany () {
		return trainCompany;
	}
}
