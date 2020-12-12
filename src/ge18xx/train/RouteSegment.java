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
	NodeInformation start;
	NodeInformation end;
	int cost;				// For Ferry/Tunnel/Bridge Fee
	Gauge gauge;			//	Track Gauge

	public RouteSegment (MapCell aMapCell) {
		setMapCell (aMapCell);
		setTile (aMapCell.getTile ());
		setCost (0);
		setStartSegment (new Location ());
		setEndSegment (new Location ());
		setGauge (new Gauge ());
	}
	
	private void setGauge (Gauge aGauge) {
		gauge = aGauge;
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
		start = new NodeInformation (aStartLocation, false, false, false, 0, 0, RevenueCenter.NO_CENTER);
	}
	
	public void setStartSegment (Location aStartLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
				int aBonus, RevenueCenter aRevenueCenter) {
		start = new NodeInformation (aStartLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aRevenueCenter);
	}
	
	public void setEndSegment (Location aEndLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
			int aBonus, RevenueCenter aRevenueCenter) {
		end = new NodeInformation (aEndLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aRevenueCenter);
	}
	
	public void setEndSegment (Location aEndLocation) {
		end = new NodeInformation (aEndLocation, false, false, false, 0, 0, RevenueCenter.NO_CENTER);
	}
	
	public void setStartLocation (Location aStartLocation) {
		start.setLocation (aStartLocation);
	}
	
	public void setEndLocation (Location aEndLocation) {
		end.setLocation (aEndLocation);
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
	
	public boolean hasRevenueCenterAtStart () {
		return start.hasRevenueCenter ();
	}
	
	public boolean hasRevenueCenterAtEnd () {
		return end.hasRevenueCenter ();
	}

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
		NodeInformation tTempSegmentInformation;
		
		tTempSegmentInformation = start;
		start = end;
		end = tTempSegmentInformation;
	}
	
	public Location getEnterSide () {
		Location tEnterSide;
		
		tEnterSide = getStartLocation ();
		if (! tEnterSide.isSide ()) {
			tEnterSide = new Location ();
		}
		
		return tEnterSide;
	}
	
	public Location getExitSide () {
		Location tExitSide;
		
		tExitSide = getEndLocation ();
		if (! tExitSide.isSide ()) {
			tExitSide = new Location ();
		}
		
		return tExitSide;
	}
	
	public Location getSide () {
		Location tSideLocation;
		
		tSideLocation = getStartLocation ();
		if (! tSideLocation.isSide ()) {
			tSideLocation = getEndLocation ();
		}

		return tSideLocation;
	}
	
	public Track getTrack () {
		Track tTrack = Track.NO_TRACK;
		Location tSideLocation;
		
		tSideLocation = getSide ();	
		tSideLocation.rotateLocation (mapCell.getTileOrient ());
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
		if (tTrack != Track.NO_TRACK) {
			System.out.println ("READY to Clear Train on Track from " + 
						tTrack.getEnterLocationInt () + " to " + tTrack.getExitLocationInt());
			tTrack.setTrainNumber (0);
			if (hasRevenueCenter ()) {
				tRevenueCenter = getRevenueCenter ();
				tRevenueCenter.clearAllSelected ();
			}
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

	public boolean isSideUsed () {
		boolean tIsEnterSideUsed = false, tIsExitSideUsed = false, tIsSideUsed;
		Location tSide;
		int tTileOrient;
		
		tTileOrient = mapCell.getTileOrient();
		tSide = getEnterSide ();
		tSide = tSide.rotateLocation (tTileOrient);
		tIsEnterSideUsed = tile.isSideUsed (tSide);
		tSide = getExitSide ();
		tSide = tSide.rotateLocation (tTileOrient);
		tIsExitSideUsed = tile.isSideUsed (tSide);
		
		tIsSideUsed = tIsEnterSideUsed || tIsExitSideUsed;
		
		return tIsSideUsed;
	}
	
	// if the Track is used, or even the side that the Track is used then return true.
	public boolean isTrackUsed () {
		Track tTrack;
		boolean tIsTrackUsed;
		
		if (isSideUsed ()) {
			tIsTrackUsed = true;
		} else {
			tTrack = getTrack ();
			if (tTrack == Track.NO_TRACK) {
				tIsTrackUsed = false;
			} else {
				tIsTrackUsed = tTrack.isTrackUsed ();
			}
		}
		
		return tIsTrackUsed;
	}

	public int getRevenue (int aPhase) {
		RevenueCenter tRevenueCenter;
		
		int tRevenue = 0;
		tRevenueCenter = getRevenueCenter ();
		if (tRevenueCenter != RevenueCenter.NO_CENTER) {
			tRevenue = tRevenueCenter.getRevenue(aPhase);
		}
		
		return tRevenue;
	}
}

