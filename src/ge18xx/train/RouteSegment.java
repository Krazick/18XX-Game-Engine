package ge18xx.train;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Gauge;
import ge18xx.tiles.Tile;

public class RouteSegment {
	static Gauge NORMAL_GAUGE = new Gauge (Gauge.NORMAL_GAUGE);
	public static RouteSegment NO_ROUTE_SEGMENT = null;
	MapCell mapCell; 			// Hex ID
	Tile tile;
	SegmentInformation start;
	SegmentInformation end;
	int cost;					// For Ferry/Tunnel/Bridge Fee
	
	public RouteSegment (MapCell aMapCell) {
		setMapCell (aMapCell);
		setTile (aMapCell.getTile ());
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

	public void setTile (Tile aTile) {
		tile = aTile;
	}
	
	public Tile getTile () {
		return tile;
	}
	
	public boolean hasTownOnTile () {
		boolean tHasTownOnTile = false;
		
		if (start.hasRevenueCenter()) {
			if (tile.hasTown ()) {
				tHasTownOnTile = true;
			}
		}
		if (end.hasRevenueCenter()) {
			if (tile.hasTown ()) {
				tHasTownOnTile = true;
			}
		}
		return tHasTownOnTile;
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
//	MapCell mapCell; 			// Hex ID
//	Tile tile;
//	SegmentInformation start;
//	SegmentInformation end;
//	int cost;					// For Ferry/Tunnel/Bridge Fee

	public void printDetail() {
		System.out.println ("MapCell " + mapCell.getCellID () + " Tile # " + tile.getNumber() + 
				" Start " + start.getLocation () + " End " + end.getLocation ());
		
	}
	public int getStartLocation() {
		return start.getLocation ();
	}

	public int getEndLocation() {
		return end.getLocation ();
	}
	
	public void swapStartEndLocations () {
		SegmentInformation tTempSegmentInformation;
		
		tTempSegmentInformation = start;
		start = end;
		end = tTempSegmentInformation;
	}
}

