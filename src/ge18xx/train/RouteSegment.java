package ge18xx.train;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Gauge;
import ge18xx.tiles.Tile;
import ge18xx.tiles.Track;

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
		start = new SegmentInformation (aStartLocation, false, false, false, 0, 0, NORMAL_GAUGE, RevenueCenter.NO_CENTER);
	}
	
	public void setStartSegment (Location aStartLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
				int aBonus, Gauge aGauge, RevenueCenter aRevenueCenter) {
		start = new SegmentInformation (aStartLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aGauge, aRevenueCenter);
	}
	
	public void setEndSegment (Location aEndLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
			int aBonus, Gauge aGauge, RevenueCenter aRevenueCenter) {
		end = new SegmentInformation (aEndLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aGauge, aRevenueCenter);
	}
	
	public void setEndSegment (Location aEndLocation) {
		end = new SegmentInformation (aEndLocation, false, false, false, 0, 0, NORMAL_GAUGE, RevenueCenter.NO_CENTER);
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
				" Start " + start.getLocationInt () + " End " + end.getLocationInt ());
		
	}
	public int getStartLocationInt () {
		int tStartLocation;
		
		tStartLocation = start.getLocationInt ();
		
		return tStartLocation;
	}

	public int getEndLocationInt () {
		int tEndLocation;
		
		tEndLocation = end.getLocationInt ();
		
		return tEndLocation;
	}
	
	public Location getStartLocation () {
		return start.getLocation ();
	}
	
	public Location getEndLocation () {
		return end.getLocation ();
	}
	
	public void swapStartEndLocations () {
		SegmentInformation tTempSegmentInformation;
		
		tTempSegmentInformation = start;
		start = end;
		end = tTempSegmentInformation;
	}
	
	public Track getTrack () {
		Track tTrack = Track.NO_TRACK;
		Location tSideLocation;
		
		tSideLocation = getStartLocation ();
		if (! tSideLocation.isSide ()) {
			tSideLocation = getEndLocation ();
		}
		
		tTrack = mapCell.getTrackFromSide (tSideLocation.getLocation ());
		
		return tTrack;
	}

	public RevenueCenter getRevenueCenter() {
		RevenueCenter tRevenueCenter = RevenueCenter.NO_CENTER;
		
		if (start.hasRevenueCenter ()) {
			tRevenueCenter = start.getRevenueCenter();
		} else if (end.hasRevenueCenter ()) {
			tRevenueCenter = end.getRevenueCenter ();
		}
		
		return tRevenueCenter;
	}
	
	public void clearTrainOn () {
		Track tTrack;
		RevenueCenter tRevenueCenter;
		
		tTrack = getTrack ();
		System.out.println ("READY to Clear Train on Track from " + 
					tTrack.getEnterLocationInt () + " to " + tTrack.getExitLocationInt());
		tTrack.setTrainNumber (0);
		if (hasRevenueCenter ()) {
			tRevenueCenter = getRevenueCenter ();
			tRevenueCenter.clearAllSelected();
		}
	}
	
	public void setTrainOn (int aTrainIndex) {
		Track tTrack;
		RevenueCenter tRevenueCenter;
		
		tTrack = getTrack ();
		System.out.println ("READY to set Train " + aTrainIndex + " on Track from " + 
					tTrack.getEnterLocationInt () + " to " + tTrack.getExitLocationInt());
		tTrack.setTrainNumber (aTrainIndex);
		if (hasRevenueCenter ()) {
			tRevenueCenter = getRevenueCenter ();
			tRevenueCenter.setSelected (true, aTrainIndex);
		}
	}
}

