package ge18xx.train;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import ge18xx.center.RevenueCenter;
import ge18xx.game.Game_18XX;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Gauge;
import ge18xx.tiles.Tile;
import ge18xx.tiles.Track;
import ge18xx.toplevel.MapFrame;

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
	MapCell mapCell; // Hex ID
	Tile tile;
	String mapCellID;
	int tileNumber;
	int cost; // For Ferry/Tunnel/Bridge Fee
	NodeInformation start;
	NodeInformation end;
	Gauge gauge; // Track Gauge
	Logger logger;

	public RouteSegment (MapCell aMapCell) {
		NodeInformation tNodeInformation1;
		NodeInformation tNodeInformation2;
		Tile tTile;
		Gauge tGauge;

		tTile = aMapCell.getTile ();
		tGauge = new Gauge ();
		tNodeInformation1 = new NodeInformation (new Location (), false, false, false, 0, 0, RevenueCenter.NO_CENTER);
		tNodeInformation2 = new NodeInformation (new Location (), false, false, false, 0, 0, RevenueCenter.NO_CENTER);
		setValues (aMapCell, tTile, 0, tNodeInformation1, tNodeInformation2, tGauge);
		setLogger ();
	}

	public void setValues (MapCell aMapCell, Tile aTile, int aCost, NodeInformation aNode1, NodeInformation aNode2,
			Gauge aGauge) {
		setMapCell (aMapCell);
		setTile (aTile);
		setCost (0);

		setStartNode (aNode1);
		setEndNode (aNode2);
		setGauge (aGauge);
	}

	public RouteSegment (RouteSegment aSegmentToCopyFrom) {
		Tile tTile;
		MapCell tMapCell;

		tMapCell = aSegmentToCopyFrom.getMapCell ();
		tTile = aSegmentToCopyFrom.getTile ();
		if (tTile == Tile.NO_TILE) {
			tTile = tMapCell.getTile ();
		}
		setValues (tMapCell, tTile, aSegmentToCopyFrom.getCost (), aSegmentToCopyFrom.getStartNode (),
				aSegmentToCopyFrom.getEndNode (), aSegmentToCopyFrom.getGauge ());
		fixTile (tMapCell);
	}

	public RouteSegment (XMLNode aRouteSegmentNode) {
		String tMapCellID;
		String tSegmentNodeName;
		int tTileNumber;
		int tCost;
		int tGaugeInt;
		int tSegmentIndex;
		int tSegmentNodeCount;
		XMLNode tSegmentNode;
		NodeList tSegmentChildren;
		NodeInformation tStartNode;
		NodeInformation tEndNode;
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
		setLogger ();
	}

	private void setLogger () {
		if (logger == null) {
			logger = Game_18XX.getLoggerX ();
		}
	}

	private Gauge getGauge () {
		return gauge;
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

	public boolean countableRevenueCenter (int aSegmentIndex) {
		boolean tCountableRevenueCenter = false;

		if (aSegmentIndex == 0) {
			tCountableRevenueCenter = true;
		} else {
			if (start.isSide ()) {
				if (!end.isSide ()) {
					tCountableRevenueCenter = true;
				}
			}
		}

		return tCountableRevenueCenter;
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

	public boolean hasRevenueCenter () {
		boolean tHasRevenueCenter = false;

		if (start.hasRevenueCenter ()) {
			tHasRevenueCenter = true;
		} else if (end.hasRevenueCenter ()) {
			tHasRevenueCenter = true;
		}

		return tHasRevenueCenter;
	}

	public boolean hasCityOnTile () {
		boolean tHasCity = false;

		if (hasRevenueCenter ()) {
			if (!hasTownOnTile ()) {
				tHasCity = true;
			}
		}

		return tHasCity;
	}

	public boolean hasRevenueCenterAtStart () {
		return start.hasRevenueCenter ();
	}

	public boolean hasRevenueCenterAtEnd () {
		return end.hasRevenueCenter ();
	}

	public void applyRCInfo (int aPhase, int aCorpID) {
		Location tStartLocation;
		Location tEndLocation;

		tStartLocation = start.getLocation ();
		if (tile.getRunThroughCenter () != RevenueCenter.NO_CENTER) {
			start.applyRCinfo (tile, tStartLocation, aCorpID);
		} else {
			if (!tStartLocation.isSide ()) {
				start.applyRCinfo (tile, tStartLocation, aCorpID);
			}
			tEndLocation = end.getLocation ();
			if (!tEndLocation.isSide ()) {
				end.applyRCinfo (tile, tEndLocation, aCorpID);
			}
		}
	}

	public void printDetail () {
		String tMapCellDetail;

		if (mapCell != MapCell.NO_MAP_CELL) {
			tMapCellDetail = mapCell.getDetail ();
		} else {
			tMapCellDetail = "ID " + mapCellID + " Tile Number " + tileNumber;
		}
		System.out.println (			// PRINTLOG
				"MapCell " + tMapCellDetail + " Track Starts " + start.getDetail () + " Ends " + end.getDetail ());
	}

	public int getStartLocationInt () {
		return start.getLocationInt ();
	}

	public int getEndLocationInt () {
		return end.getLocationInt ();
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
		if (!tSideLocation.isSide ()) {
			tSideLocation = getEndLocation ();
		}

		return tSideLocation;
	}

	/**
	 * Get the specific Track on the Map Cell with the two specific end points
	 *
	 * @return The Track found from the Start to the End on the Map Cell.
	 */
	public Track getTrack () {
		Track tTrack;
		int tStartLoc;
		int tEndLoc;

		tStartLoc = start.getLocationInt ();
		tEndLoc = end.getLocationInt ();
		tTrack = mapCell.getTrackFromStartToEnd (tStartLoc, tEndLoc);

		return tTrack;
	}

	/**
	 * Test if the Track for a specific Route Segment is currently in use
	 *
	 * @return True If the Track connecting the two end points on the Map Cell is in
	 *         use
	 */
	public boolean trackIsInUse () {
		boolean tTrackIsInUse;
		Track tTrack;

		tTrack = getTrack ();
		if (tTrack == Track.NO_TRACK) {
			tTrackIsInUse = false;
		} else {
			tTrackIsInUse = tTrack.isTrackUsed ();
		}

		return tTrackIsInUse;
	}

	public NodeInformation getStartNode () {
		return start;
	}

	public NodeInformation getEndNode () {
		return end;
	}

	/**
	 * Update both the Start Node, and then End Node for the Route Segment to be
	 * sure the route Open Flow flags are set correctly.
	 *
	 * @param aRouteInformation The 'determineOpenFlow' method is in the
	 *                          RouteInformation Object
	 * @param aCorpID           The current Corporation ID needed to test for proper
	 *                          Token on a station
	 */
	public void updateOpenFlow (RouteInformation aRouteInformation, int aCorpID) {
		start.updateOpenFlow (aRouteInformation, aCorpID);
		end.updateOpenFlow (aRouteInformation, aCorpID);
	}

	public void updateRevenues (int aPhase) {
		start.updateRevenue (aPhase);
		end.updateRevenue (aPhase);
	}

	public void updateTile () {
		Tile tCurrentTile;
		int tOrientation;

		tCurrentTile = mapCell.getTile ();
		tOrientation = mapCell.getTileOrient ();
		setTile (tCurrentTile);
		start.updateNode (tCurrentTile, end, tOrientation);
		end.updateNode (tCurrentTile, start, tOrientation);
	}

	public boolean isTileUpdated () {
		boolean tIsTileUpdated;
		Tile tCurrentTile;

		tCurrentTile = mapCell.getTile ();
		tIsTileUpdated = false;
		if (tile.getNumber () != tCurrentTile.getNumber ()) {
			tIsTileUpdated = true;
		}

		return tIsTileUpdated;
	}

	public boolean isSame (RouteSegment aRouteSegment) {
		boolean tIsSame = true;

		if (!mapCellID.equals (aRouteSegment.getMapCellID ())) {
			tIsSame = false;
		}
		if (tileNumber != aRouteSegment.getTileNumber ()) {
			tIsSame = false;
		}
		if (!start.isSame (aRouteSegment.getStartNode ())) {
			tIsSame = false;
		}
		if (!end.isSame (aRouteSegment.getEndNode ())) {
			tIsSame = false;
		}
		return tIsSame;
	}

	public RevenueCenter getRevenueCenter () {
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
		Location tEnd;

		if (aTrack != Track.NO_TRACK) {
			aTrack.setTrainNumber (0);
			if (hasRevenueCenter ()) {
				tRevenueCenter = getRevenueCenter ();
				tRevenueCenter.clearAllSelected ();
			}
		} else {
			logger.error ("Track Provided is NULL");
		}
		if (isStartASide ()) {
			tSide = getStartLocationIsSide ();
			mapCell.clearTrainUsingASide (tSide.getLocation ());
		}
		if (isEndASide ()) {
			tSide = getEndLocationIsSide ();
			mapCell.clearTrainUsingASide (tSide.getLocation ());
		} else {
			tEnd = end.getLocation ();
			mapCell.removeEndRoute (tEnd);
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
		boolean tIsEnterSideUsed = false;
		boolean tIsExitSideUsed = false;
		boolean tIsSideUsed;
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

	// if the Track is used, or even the side that the Track is used then return
	// true.
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
		tStartLocation = tStartLocation.unrotateLocation (mapCell.getTileOrient ());
		tStartLoc = tStartLocation.getLocation ();
		tTrack = tile.getTrackFromSide (tStartLoc);
		if (tTrack.getEnterLocationInt () == tStartLoc) {
			tPossibleEnd = tTrack.getExitLocation ();
		} else {
			tPossibleEnd = tTrack.getEnterLocation ();
		}
		if (tPossibleEnd.isSide () || tPossibleEnd.isDeadEndSide ()) {
			tPossibleEnd = tPossibleEnd.rotateLocation (mapCell.getTileOrient ());
		}

		return tPossibleEnd;
	}

	/**
	 * This will cycle through the list of Tracks on the Tile, ignoring the
	 * unuseable Tracks to find the next one that begins at the Start and does NOT
	 * End at the End. (ie we get back to the original track. as long as it is not
	 * the same Track segment on the Current Index
	 *
	 * @param aCurrentIndex  The Index starting from
	 * @param aStartLocation The starting Location for the Current Track Segment
	 * @param aEndLocation   The ending Location for the Current Track Segment
	 * @return the Next useable Track Segment.
	 */
	public Track getNextTrack (int aCurrentIndex, Location aStartLocation, Location aEndLocation) {
		Track tNextTrack;
		Track tFoundTrack = Track.NO_TRACK;
		int tNextIndex;
		int tTrackCount;
		boolean tTestedAll = false;

		// Want to move to Next Track Index, cycling back to first (which is zero)
		// Verify that after the Retrieval, that it begins at the Start Location
		// Or that it ends at the Start Location (need to swap positions

		tNextIndex = aCurrentIndex;
		tTrackCount = tile.getTrackCount ();
		while (!tTestedAll) {
			tNextIndex = ((tNextIndex + 1) % tTrackCount);
			tNextTrack = tile.getTrackByIndex (tNextIndex);
			if (tNextTrack != Track.NO_TRACK) {
				if (tNextTrack.useableTrack ()) {
					if (tNextTrack.startsAt (aStartLocation)) {
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
		}

		return tFoundTrack;
	}

	public boolean cycleToNextTrack () {
		boolean tCycledToNextTrack = false;
		Track tNextTrack;
		Track tTrack;
		int tTrackCount;
		int tTrainNumber;
		int tTileOrient;
		int tCurrentTrackIndex;
		Location tNewStartLocation;
		Location tEndLocation;
		Location tStartLocation;
		Location tNewEndLocation;
		Location tOriginalStart;
		Location tOriginalEnd;
		Location tNewRotStart;
		Location tNewRotEnd;

		tTileOrient = mapCell.getTileOrient ();
		tOriginalStart = new Location (start.getLocationInt ());
		tOriginalEnd = new Location (end.getLocationInt ());
		tStartLocation = tOriginalStart.unrotateLocation (tTileOrient);
		tEndLocation = tOriginalEnd.unrotateLocation (tTileOrient);
		tTrackCount = tile.getTrackCountFromSide (tStartLocation);
		if (tTrackCount > 1) {
			tCurrentTrackIndex = tile.getTrackIndexBetween (tStartLocation, tEndLocation);
			tTrack = tile.getTrackFromStartByIndex (tStartLocation, tCurrentTrackIndex);

			tNextTrack = getNextTrack (tCurrentTrackIndex, tStartLocation, tEndLocation);
			if (tNextTrack != Track.NO_TRACK) {
				tNewStartLocation = tNextTrack.getEnterLocation ();
				tNewEndLocation = tNextTrack.getExitLocation ();
				tNewRotStart = tNewStartLocation.rotateLocation (tTileOrient);
				tNewRotEnd = tNewEndLocation.rotateLocation (tTileOrient);

				tTrainNumber = tTrack.getTrainNumber ();
				clearTrainOnTrack (tTrack);

				if (tOriginalStart.getLocation () == tNewStartLocation.getLocation ()) {
					if (tOriginalStart.getLocation () == tNewRotEnd.getLocation ()) {
						end.setLocation (tNewRotStart);
					} else {
						end.setLocation (tNewRotEnd);
					}
				} else {
					if (tOriginalStart.getLocation () == tNewRotStart.getLocation ()) {
						end.setLocation (tNewRotEnd);
					} else {
						end.setLocation (tNewRotStart);
					}
				}

				tCycledToNextTrack = true;
				setTrainOnTrack (tNextTrack, tTrainNumber);
			} else {
				logger.info ("Failed to Find Next Track connected to " + tStartLocation.getLocation ());
			}
		} else {
			logger.info ("Tile has only found " + tTrackCount + " Track from side " + tStartLocation.getLocation ());
		}

		return tCycledToNextTrack;
	}

	public boolean hasACorpStation () {
		boolean tHasACorpStation;

		tHasACorpStation = start.getCorpStation () || end.getCorpStation ();

		return tHasACorpStation;
	}

	public boolean foundTrack () {
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
		XMLElement tXMLStartElement;
		XMLElement tXMLEndElement;

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

	public void fixLoadedRouteSegment (MapFrame aMapFrame) {
		MapCell tMapCell;

		tMapCell = aMapFrame.getMapCellForID (mapCellID);
		if (tMapCell != MapCell.NO_MAP_CELL) {
			setMapCell (tMapCell);
			fixTile (tMapCell);
		} else {
			logger.error ("Looking for MapCell " + mapCellID + " Did not find it in the Map");
		}
	}

	private void fixTile (MapCell aMapCell) {
		Tile tTile;
		int tTileNumber;
		int tOrientation;

		tTile = aMapCell.getTile ();
		tTileNumber = tTile.getNumber ();
		if (tTileNumber == tileNumber) {
			setTile (tTile);
			tOrientation = mapCell.getTileOrient ();
			if (start.hasRevenueCenter ()) {
				start.fixRevenueCenter (tTile, end, tOrientation);
			}
			if (end.hasRevenueCenter ()) {
				end.fixRevenueCenter (tTile, start, tOrientation);
			}
		}
	}
}
