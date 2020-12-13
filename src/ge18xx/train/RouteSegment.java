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
		NodeInformation tNodeInformation1;
		NodeInformation tNodeInformation2;

		setMapCell (aMapCell);
		setTile (aMapCell.getTile ());
		setCost (0);
		
		tNodeInformation1 = new NodeInformation (new Location (), false, false, false, 0, 0, RevenueCenter.NO_CENTER);
		setStartNode (tNodeInformation1);
		
		tNodeInformation2 = new NodeInformation (new Location (), false, false, false, 0, 0, RevenueCenter.NO_CENTER);
		setEndNode (tNodeInformation2);
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
		boolean tValidSegment;
		
		tValidSegment = (start.isValid () && end.isValid ());
		if (tValidSegment) {
			tValidSegment = (start.getLocationInt () != end.getLocationInt ());
		}
		
		return tValidSegment;
	}
	
	public void setStartNode (NodeInformation aNodeInformation) {
		start = aNodeInformation;
	}
	
	public void setEndNode (NodeInformation aNodeInformation) {
		end = aNodeInformation;
	}
	
	public void setStartNode (Location aStartLocation) {
		NodeInformation tNodeInformation;
		
		tNodeInformation = new NodeInformation (aStartLocation, false, false, false, 0, 0, RevenueCenter.NO_CENTER);
		setStartNode (tNodeInformation);
	}
	
	public void setStartNode (Location aStartLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
				int aBonus, RevenueCenter aRevenueCenter) {
		NodeInformation tNodeInformation;
		
		tNodeInformation = new NodeInformation (aStartLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aRevenueCenter);
		setStartNode (tNodeInformation);
	}
	
	public void setEndNode (Location aEndLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter, int aRevenue, 
			int aBonus, RevenueCenter aRevenueCenter) {
		NodeInformation tNodeInformation;
		
		tNodeInformation = new NodeInformation (aEndLocation, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, aRevenueCenter);
		setEndNode (tNodeInformation);
	}
	
	public void setEndNode (Location aEndLocation) {
		NodeInformation tNodeInformation;
		
		tNodeInformation = new NodeInformation (aEndLocation, false, false, false, 0, 0, RevenueCenter.NO_CENTER);
		setEndNode (tNodeInformation);
	}
	
	public void setStartNodeLocation (Location aStartLocation) {
		start.setLocation (aStartLocation);
	}
	
	public void setEndNodeLocation (Location aEndLocation) {
		end.setLocation (aEndLocation);
	}
	
	public void setEndNodeLocationInt (int aEndLocation) {
		Location tLocation;
		
		tLocation = new Location (aEndLocation);
		setEndNode (tLocation);
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}

	public int getCost () {
		return cost;
	}
	
	public void setTile (Tile aTile) {
		tile = aTile;
	}
	
	public Tile getTile () {
		return tile;
	}
	
	public boolean hasTownOnTile () {
		boolean tHasTownOnTile = false;
		
		if (start.hasRevenueCenter ()) {
			if (tile.hasTown ()) {
				tHasTownOnTile = true;
			}
		}
		if (end.hasRevenueCenter ()) {
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
	
	public boolean isStartASide () {
		Location tStartSide;
		boolean tIsStartASide = false;
		
		tStartSide = getStartLocation ();
		tIsStartASide = tStartSide.isSide ();
		
		return tIsStartASide;
	}
	
	public boolean isEndASide () {
		Location tEndSide;
		boolean tIsEndASide = false;
		
		tEndSide = getEndLocation ();
		tIsEndASide = tEndSide.isSide ();
		
		return tIsEndASide;
	}
	
	public Location getStartLocationIsSide () {
		Location tStartSide = Location.NO_LOC;
		
		if (isStartASide ()) {
			tStartSide = getStartLocation ();
		}
		
		return tStartSide;
	}
	
	public Location getEndLocationIsSide () {
		Location tEndSide = Location.NO_LOC;
		
		if (isEndASide ()) {
			tEndSide = getEndLocation ();
		}
		
		return tEndSide;
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
			tRevenueCenter = start.getRevenueCenter ();
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
		
		tTileOrient = mapCell.getTileOrient ();
		if (isStartASide ()) {
			tSide = getStartLocationIsSide ();
			tSide = tSide.rotateLocation (tTileOrient);
			tIsEnterSideUsed = tile.isSideUsed (tSide);
		}
		if (isEndASide ()) {
			tSide = getEndLocationIsSide ();
			tSide = tSide.rotateLocation (tTileOrient);
			tIsExitSideUsed = tile.isSideUsed (tSide);
		}
//		tSide = getEnterSide ();
//		tSide = tSide.rotateLocation (tTileOrient);
//		tIsEnterSideUsed = tile.isSideUsed (tSide);
//		tSide = getExitSide ();
//		tSide = tSide.rotateLocation (tTileOrient);
//		tIsExitSideUsed = tile.isSideUsed (tSide);
		
		tIsSideUsed = tIsEnterSideUsed || tIsExitSideUsed;
		
//		tIsSideUsed = false;
		
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
			tRevenue = tRevenueCenter.getRevenue (aPhase);
		}
		
		return tRevenue;
	}
}

