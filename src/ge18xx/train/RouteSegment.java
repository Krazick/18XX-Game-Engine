package ge18xx.train;

import org.w3c.dom.NodeList;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Gauge;
import ge18xx.tiles.Tile;
import ge18xx.tiles.Track;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class RouteSegment {
	final static ElementName EN_ROUTE_SEGMENT = new ElementName ("RouteSegment");
	final static ElementName EN_START_NODE = new ElementName ("StartNode");
	final static ElementName EN_END_NODE = new ElementName ("EndNode");
	final static AttributeName AN_MAP_CELL_ID = new AttributeName ("mapCellID");
	final static AttributeName AN_TILE_NUMBER = new AttributeName ("tileNumber");
	final static AttributeName AN_COST = new AttributeName ("cost");
	final static AttributeName AN_GAUGE = new AttributeName ("gauge");

	static Gauge NORMAL_GAUGE = new Gauge (Gauge.NORMAL_GAUGE);
	public static RouteSegment NO_ROUTE_SEGMENT = null;
	MapCell mapCell; 			// Hex ID
	Tile tile;
	String mapCellID;
	int tileNumber;
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
//	<RouteSegment cost="0" gauge="0" mapCellID="H18" tileNumber="59">
//	<StartNode bonus="0" corpStation="false" hasRevenueCenter="true" location="16" openFlow="false" revenue="40"/>
//	<EndNode bonus="0" corpStation="false" hasRevenueCenter="false" location="0" openFlow="true" revenue="0"/>
//	</RouteSegment>

	public RouteSegment (XMLNode aRouteSegmentNode) {
		String tMapCellID;
		int tTileNumber, tCost, tGaugeInt;
		XMLNode tSegmentNode;
		NodeList tSegmentChildren;
		int tSegmentIndex, tSegmentNodeCount;
		String tSegmentNodeName;
		NodeInformation tStartNode, tEndNode;
		Gauge tGauge;
		
		tMapCellID = aRouteSegmentNode.getThisAttribute (AN_MAP_CELL_ID);
		tCost = aRouteSegmentNode.getThisIntAttribute (AN_COST);
		tGaugeInt = aRouteSegmentNode.getThisIntAttribute (AN_GAUGE);
		tTileNumber = aRouteSegmentNode.getThisIntAttribute (AN_TILE_NUMBER);
		
		setMapCellID (tMapCellID);
		setTileNumber (tTileNumber);
		setCost (tCost);
		tGauge = new Gauge (tGaugeInt);
		setGauge (tGauge);
		
		tSegmentChildren = aRouteSegmentNode.getChildNodes ();
		tSegmentNodeCount = tSegmentChildren.getLength ();
		for (tSegmentIndex = 0; tSegmentIndex < tSegmentNodeCount; tSegmentIndex++) {
			tSegmentNode = new XMLNode (tSegmentChildren.item (tSegmentIndex));
			tSegmentNodeName = tSegmentNode.getNodeName ();
			if (EN_START_NODE.equals (tSegmentNodeName)) {
				tStartNode = new NodeInformation (tSegmentNode);
				setStartNode (tStartNode);
			}
			if (EN_END_NODE.equals (tSegmentNodeName)) {
				tEndNode = new NodeInformation (tSegmentNode);
				setEndNode (tEndNode);
			}
		}
	}


	private void setGauge (Gauge aGauge) {
		gauge = aGauge;
	}
	
	private void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
		setMapCellID (mapCell.getID ());
	}
	
	private void setMapCellID (String aMapCellID) {
		mapCellID = aMapCellID;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public String getMapCellID () {
		return mapCellID;
	}
	
	public boolean validSegment () {
		boolean tValidSegment;
		
		tValidSegment = (validStart () && validEnd ());
		
		if (tValidSegment) {
			tValidSegment = (start.getLocationInt () != end.getLocationInt ());
		}
		
		return tValidSegment;
	}
	
	public boolean isStartOpen () {
		return start.getOpenFlow ();
	}
	
	public boolean isEndOpen () {
		return end.getOpenFlow ();
	}
	
	public boolean isFullyOpen () {
		return isStartOpen () && isEndOpen ();
	}
	
	public boolean validStart () {
		return start.isValid ();
	}
	
	public boolean validEnd () {
		return end.isValid ();
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
	
	public void setEndNode (Location aEndLocation, int aPhase) {
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
	
	public void setStartNodeLocationInt (int aStartLocation) {
		Location tLocation;
		
		tLocation = new Location (aStartLocation);
		setStartNode (tLocation);
	}
	
	public void setEndNodeLocationInt (int aEndLocation, int aPhase) {
		Location tLocation;
		
		tLocation = new Location (aEndLocation);
		setEndNode (tLocation, aPhase);
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}

	public int getCost () {
		return cost;
	}
	
	private void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}
	
	public void setTile (Tile aTile) {
		tile = aTile;
		setTileNumber (tile.getNumber ());
	}
	
	public int getTileNumber () {
		return tileNumber;
	}
	
	public Tile getTile () {
		return tile;
	}
	
	public void setRevenue (RevenueCenter aRevenueCenter, int aPhase) {
		int tRevenue;
		
		tRevenue = aRevenueCenter.getRevenue (aPhase);
		if (start.hasRevenueCenter ()) {
			if (aRevenueCenter.equals (start.getRevenueCenter ())) {
				start.setRevenue (tRevenue);
			}
		}
		if (end.hasRevenueCenter ()) {
			if (aRevenueCenter.equals (end.getRevenueCenter ())) {
				end.setRevenue (tRevenue);
			}
		}
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

	public void applyRCInfo (int aPhase, int aCorpID) {
		Location tStartLocation, tEndLocation;
		
		tStartLocation = start.getLocation ();
		if (! tStartLocation.isSide ()) {
			start.applyRCinfo (tile, tStartLocation, aCorpID);
		}
		tEndLocation = end.getLocation ();
		if (! tEndLocation.isSide ()) {
			end.applyRCinfo (tile, tEndLocation, aCorpID);
		}
	}
	
	public void printDetail() {
		String tMapCellDetail;
		
		if (mapCell != MapCell.NO_MAP_CELL) {
			tMapCellDetail = mapCell.getDetail ();
		} else {
			tMapCellDetail = "ID " + mapCellID + " Tile Number " + tileNumber;
		}
		System.out.println ("MapCell " + tMapCellDetail + 
				" Track Starts " + start.getDetail () + " Ends " + end.getDetail ());
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
		Location tStartLocation, tEndLocation;
		
		tStartLocation = start.getLocation ();
		tEndLocation = end.getLocation ();
		tTrack = mapCell.getTrackFromStartToEnd (tStartLocation.getLocation (), tEndLocation.getLocation ());
		
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
	
	public void clearTrainOnTrack (Track aTrack) {
		RevenueCenter tRevenueCenter;
		Location tSide;
		
		if (aTrack != Track.NO_TRACK) {
			System.out.println ("READY to Clear Train on Track from " + 
						aTrack.getEnterLocationInt () + " to " + aTrack.getExitLocationInt ());
			aTrack.setTrainNumber (0);
			if (hasRevenueCenter ()) {
				tRevenueCenter = getRevenueCenter ();
				tRevenueCenter.clearAllSelected ();
			}
		} else {
			System.err.println ("Track Provided is NULL");
		}
		if (isStartASide ()) {
			tSide = getStartLocationIsSide ();
			mapCell.clearTrainUsingASide (tSide.getLocation ());
		}
		if (isEndASide ()) {
			tSide = getEndLocationIsSide ();
			mapCell.clearTrainUsingASide (tSide.getLocation ());
		}
	}
	
	public void clearTrainOn () {
		Track tTrack;
		
		tTrack = getTrack ();
		clearTrainOnTrack (tTrack);
	}
	
	public void setTrainOnTrack (Track aTrack, int aTrainIndex) {
		RevenueCenter tRevenueCenter;
		Location tSide;
		
		aTrack.setTrainNumber (aTrainIndex);
		if (hasRevenueCenter ()) {
			tRevenueCenter = getRevenueCenter ();
			tRevenueCenter.setSelected (true, aTrainIndex);
		}
		if (isStartASide ()) {
			tSide = getStartLocationIsSide ();
			mapCell.setTrainUsingSide (tSide.getLocation (), aTrainIndex);
		}
		if (isEndASide ()) {
			tSide = getEndLocationIsSide ();
			mapCell.setTrainUsingSide (tSide.getLocation (), aTrainIndex);
		}
	}
	
	public void setTrainOn (int aTrainIndex) {
		Track tTrack;
		
		tTrack = getTrack ();
		setTrainOnTrack (tTrack, aTrainIndex);
	}

	public boolean isSideUsed () {
		boolean tIsEnterSideUsed = false, tIsExitSideUsed = false, tIsSideUsed;
		Location tSide;
		if (isStartASide ()) {
			tSide = getStartLocationIsSide ();
			tIsEnterSideUsed = mapCell.isTrainUsingSide (tSide.getLocation ());
		}
		if (isEndASide ()) {
			tSide = getEndLocationIsSide ();
			tIsExitSideUsed = mapCell.isTrainUsingSide (tSide.getLocation ());
		}
		
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
			tRevenue = tRevenueCenter.getRevenue (aPhase);
		}
		
		return tRevenue;
	}

	public Location getPossibleEnd () {
		Location tPossibleEnd;
		Location tStartLocation;
		Track tTrack;
		int tStartLoc;
		
		tStartLocation = start.getLocation ();
		tStartLocation = tStartLocation.unrotateLocation (mapCell.getTileOrient());
		tStartLoc = tStartLocation.getLocation ();
		tTrack = tile.getTrackFromSide (tStartLoc);
		if (tTrack.getEnterLocationInt () == tStartLoc) {
			tPossibleEnd = tTrack.getExitLocation ();
		} else {
			tPossibleEnd = tTrack.getEnterLocation ();
		}
		if (tPossibleEnd.isSide ()) {
			tPossibleEnd = tPossibleEnd.rotateLocation (mapCell.getTileOrient ());
		}
		
		return tPossibleEnd;
	}

	public Track getNextTrack (int aCurrentIndex, Location aStartLocation, Location aEndLocation) {
		Track tNextTrack, tFoundTrack = Track.NO_TRACK;
		int tNextIndex, tTrackCount;
		boolean tTestedAll = false;
		
		// Want to move to Next Track Index, cycling back to first (which is zero)
		// Verify that after the Retrieval, that it begins at the Start Location
		// Or that it ends at the Start Location (need to swap positions
		
		tNextIndex = aCurrentIndex;
		tTrackCount = tile.getTrackCount ();
		while (tTestedAll == false) {
			tNextIndex = ((tNextIndex + 1) % tTrackCount);
			tNextTrack = tile.getTrackByIndex (tNextIndex);
			if (tNextTrack != Track.NO_TRACK) {
				if (tNextTrack.startsAt (aStartLocation) ) {
					if (tNextTrack.endsAt (aEndLocation)) {
						tTestedAll = true;
					} else {
						tFoundTrack = tNextTrack;
					}
				} else if (tNextTrack.endsAt (aStartLocation)) {
					if (tNextTrack.startsAt (aEndLocation)) {
						tTestedAll = true;
					} else {
						tFoundTrack = tNextTrack;
					}
				}
			}
		}

		return tFoundTrack;
	}
	
	public boolean cycleToNextTrack () {
		boolean tCycledToNextTrack = false;
		Track tNextTrack, tTrack;
		int tTrackCount, tTrainNumber;
		Location tNewStartLocation, tEndLocation, tStartLocation, tNewEndLocation, tOriginalStart, tOriginalEnd;
		Location tNewRotStart, tNewRotEnd;
		int tTileOrient, tCurrentTrackIndex, tNextTrackIndex;
		
		tTileOrient = mapCell.getTileOrient ();
		tOriginalStart = new Location (start.getLocationInt ());
		tOriginalEnd = new Location (end.getLocationInt ());
		tStartLocation = tOriginalStart.unrotateLocation (tTileOrient);
		tEndLocation = tOriginalEnd.unrotateLocation (tTileOrient);
		tTrackCount = tile.getTrackCountFromSide (tStartLocation);
		if (tTrackCount > 1) {
			tCurrentTrackIndex = tile.getTrackIndexBetween (tStartLocation, tEndLocation);
			tTrack = tile.getTrackFromStartByIndex (tStartLocation, tCurrentTrackIndex);
			System.out.println ("Counted Tracks for MapCell " + mapCell.getCellID () + " Found " + tTrackCount + 
					" on Side " + tStartLocation.getLocation () + " Current Track Index " + tCurrentTrackIndex);
			
			tNextTrack = getNextTrack (tCurrentTrackIndex, tStartLocation, tEndLocation);
			if (tNextTrack != Track.NO_TRACK) {
				tNewStartLocation = tNextTrack.getEnterLocation ();
				tNewEndLocation = tNextTrack.getExitLocation ();
				tNewRotStart = tNewStartLocation.rotateLocation(tTileOrient);
				tNewRotEnd = tNewEndLocation.rotateLocation(tTileOrient);
				
				tNextTrackIndex = tile.getTrackIndexBetween (tStartLocation, tNewEndLocation);;
				System.out.println ("Next Track Index is " + tNextTrackIndex + " found Track from " + 
					tNextTrack.getEnterLocationInt () + " to " + tNextTrack.getExitLocationInt ());
				tTrainNumber = tTrack.getTrainNumber ();
				clearTrainOnTrack (tTrack);
				
				System.out.println ("Original [" + tOriginalStart.getLocation () + " to " +  tOriginalEnd.getLocation () +
						"] UnRotated [" + tStartLocation.getLocation () + " to " +  tEndLocation.getLocation () + 
						"] NextTrack [" + tNewStartLocation.getLocation () + " to " +  tNewEndLocation.getLocation () + 
						"] NextTrack Rotated [" + tNewRotStart.getLocation () + " to " +  tNewRotEnd.getLocation () + "]");
				if (tOriginalStart.getLocation () == tNewStartLocation.getLocation ()) {
					if (tOriginalStart.getLocation () == tNewRotEnd.getLocation ()) {
						end.setLocation (tNewRotStart);
						System.out.println ("A. ReSetting Route Segment new EndLocation to Start Rotated by " + tTileOrient + " to " + tNewRotStart.getLocation ());
					} else {
						end.setLocation (tNewRotEnd);
						System.out.println ("B. ReSetting Route Segment new EndLocation to End Rotated by " + tTileOrient + " to " + tNewRotEnd.getLocation ());
					}
				} else {
					if (tOriginalStart.getLocation () == tNewRotStart.getLocation ()) {
						end.setLocation (tNewRotEnd);
						System.out.println ("C. ReSetting Route Segment new EndLocation to End Rotated by " + tTileOrient + " to " + tNewRotEnd.getLocation ());
					} else {
						end.setLocation (tNewRotStart);
						System.out.println ("D. ReSetting Route Segment new EndLocation to Start Rotated by " + tTileOrient + " to " + tNewRotStart.getLocation ());
					}
				}
				tCycledToNextTrack = true;
				setTrainOnTrack (tNextTrack, tTrainNumber);
			} else {
				System.err.println ("Failed to Find Next Track connected to " + tStartLocation.getLocation ());
			}
		} else {
			System.out.println ("Tile has only found " + tTrackCount + " Track from side " + tStartLocation.getLocation ());
		}
		
		return tCycledToNextTrack;
	}

	public boolean hasACorpStation() {
		boolean tHasACorpStation;
		
		tHasACorpStation = start.getCorpStation () || end.getCorpStation ();
		
		return tHasACorpStation;
	}

	public boolean foundTrack() {
		boolean tFoundTrack = false;
		Track tTrack;
		
		tTrack = getTrack ();
		if (tTrack != Track.NO_TRACK) {
			tFoundTrack = true;
		}
		
		return tFoundTrack;
	}

	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLStartElement, tXMLEndElement;
		
		tXMLElement = aXMLDocument.createElement (EN_ROUTE_SEGMENT);
		tXMLElement.setAttribute (AN_MAP_CELL_ID, mapCell.getCellID ());
		tXMLElement.setAttribute (AN_TILE_NUMBER, tile.getNumber ());
		tXMLElement.setAttribute (AN_COST, cost);
		tXMLElement.setAttribute (AN_GAUGE, gauge.getType ());
		tXMLStartElement = start.getElement (aXMLDocument, EN_START_NODE);
		tXMLEndElement = end.getElement (aXMLDocument, EN_END_NODE);
		tXMLElement.appendChild (tXMLStartElement);
		tXMLElement.appendChild (tXMLEndElement);
		
		return tXMLElement;
	}
}

