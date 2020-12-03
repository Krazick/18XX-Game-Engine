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
	
	RouteSegment (MapCell aMapCell) {
		setMapCell (aMapCell);
		setCost (0);
	}
	
	private void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public void setStartSegment (Location aStartLocation) {
		start = new SegmentInformation (aStartLocation, false, false, 0, 0, NORMAL_GAUGE);
	}
	
	public void setEndSegment (Location aEndLocation) {
		end = new SegmentInformation (aEndLocation, false, false, 0, 0, NORMAL_GAUGE);
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}
}

