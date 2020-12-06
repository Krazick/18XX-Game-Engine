package ge18xx.train;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Gauge;

public class RouteSegment {
	static Gauge NORMAL_GAUGE = new Gauge (Gauge.NORMAL_GAUGE);
	public static RouteSegment NO_ROUTE_SEGMENT = null;
	MapCell mapCell; 			// Hex ID
	SegmentInformation start;
	SegmentInformation end;
	int cost;					// For Ferry/Tunnel/Bridge Fee
	
	public RouteSegment (MapCell aMapCell) {
		setMapCell (aMapCell);
		setCost (0);
		setStartSegment (new Location ());
		setEndSegment (new Location ());
	}
	
	private void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public boolean validSegment () {
		return (start.isValid () && end.isValid ());
	}
	
	public void setStartSegment (Location aStartLocation) {
		start = new SegmentInformation (aStartLocation, false, false, false, 0, 0, NORMAL_GAUGE);
	}
	
	public void setStartSegment (Location aStartLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
				int aBonus, Gauge aGauge) {
		start = new SegmentInformation (aStartLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aGauge);
	}
	
	public void setEndSegment (Location aEndLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
			int aBonus, Gauge aGauge) {
		end = new SegmentInformation (aEndLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aGauge);
	}
	
	public void setEndSegment (Location aEndLocation) {
		end = new SegmentInformation (aEndLocation, false, false, false, 0, 0, NORMAL_GAUGE);
	}
	
	public void setEndSegment (int aEndLocation) {
		Location tLocation;
		
		tLocation = new Location (aEndLocation);
		setEndSegment (tLocation);
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}

	public boolean hasRevenueCenter() {
		boolean tHasRevenueCenter = false;
		
		if (start.hasRevenueCenter ()) {
			tHasRevenueCenter = true;
		} else if (end.hasRevenueCenter ()) {
			tHasRevenueCenter = true;
		}
		
		return tHasRevenueCenter;
	}
}

