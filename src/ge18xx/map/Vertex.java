package ge18xx.map;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ge18xx.company.TokenCompany;
import ge18xx.tiles.Tile;
import ge18xx.tiles.Track;

public class Vertex {
	public final static Vertex NO_VERTEX = null;
	MapCell mapCell;
	Location location;
	List<Edge> edges;
	String id;
	
	public Vertex (MapCell aMapCell, Location aLocation) {
		
		setMapCell (aMapCell);
		setLocation (aLocation);
		edges = new LinkedList<Edge> ();
		setID ();
	}

	public void printInfo () {
		String tEdgeInfo;
		String tTileInfo;
		
		tEdgeInfo = " Edge Count: " + edges.size ();
		if (edges.size () > 0) {
			for (Edge tMapEdge : edges) {
				tEdgeInfo += " [" + tMapEdge.getInfo () + "]";
			}
		}
		tTileInfo = mapCell.getTileInfo ();
		
		System.out.println ("Vertex"
				+ " ID: " + id + " MapCell ID " + mapCell.getID () +
				" Location " + location.getLocation () + tEdgeInfo + tTileInfo);
	}
	
	public String getID () {
		return id;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public String getMapCellID () {
		return mapCell.getCellID ();
	}
	
	public List<Edge> getEdges () {
		return edges;
	}
	
	public int getEdgeCount () {
		return edges.size ();
	}
	
	public Edge getEdge (int aIndex) {
		return edges.get (aIndex);
	}
	
	public boolean containsEdge (Edge aMapEdge) {
		boolean tContainsEdge;
		
		tContainsEdge = false;
		for (Edge tEdge : edges) {
			if (tEdge.sameEdge (aMapEdge)) {
				tContainsEdge = true;
			}
		}
		
		return tContainsEdge;
	}
	
	public boolean sameVertex (Vertex aVertex) {
		boolean tSameVertex;
		String tVertexID;
		
		tSameVertex = false;
		if (aVertex != Vertex.NO_VERTEX) {
			tVertexID = aVertex.getID ();
			if (id.equals (tVertexID)) {
				tSameVertex = true;
			}
		}
		
		return tSameVertex;
	}
	
	public Location getLocation () {
		return location;
	}
	
	public void setID () {
		id = buildID ();
	}
	
	public String buildID () {
		String tID;
		
		tID = mapCell.getCellID () + ":" + location.getLocation ();

		return tID;
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
	
	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public boolean isOnSide () {
		boolean tIsOnSide;
		
		tIsOnSide = location.isSide ();
		
		return tIsOnSide;
	}
	
	public void fillVertexEdges () {
		Tile tTile;
		int tTrackCount;
		int tTrackIndex;
		Track tTrack;
		Edge tEdge1;
		Edge tEdge2;
		Vertex tVertex;
		Location tStartLocation;
		Location tEndLocation;
		
		tTile = mapCell.getTile ();
		tTrackCount = tTile.getTrackCount ();
		for (tTrackIndex = 0; tTrackIndex < tTrackCount; tTrackIndex++ ) {
			tTrack = tTile.getTrackByIndex (tTrackIndex);
			if (tTrack != Track.NO_TRACK) {
				tStartLocation = tTrack.getEnterLocation ();
				tEndLocation = tTrack.getExitLocation ();
				tVertex = Vertex.NO_VERTEX;
				if (tStartLocation.getLocation () == location.getLocation ()) {
					tVertex = new Vertex (mapCell, tEndLocation);
				} else if (tEndLocation.getLocation () == location.getLocation ()) {
					tVertex = new Vertex (mapCell, tStartLocation);
				}
				if (tVertex != Vertex.NO_VERTEX) {
					tEdge1 = new Edge (tTrack, this, tVertex);
					tEdge2 = new Edge (tTrack, tVertex, this);
					tVertex.addEdge (tEdge2);
					addEdge (tEdge1);
				}
			}
		}
	}
	
	public void addEdge (Edge aEdge) {
		boolean tAddNewEdge;
		
		tAddNewEdge = true;
		for (Edge tExistingEdge : edges) {
			if (tExistingEdge.sameEdge (aEdge)) {
				tAddNewEdge = false;
			}
		}
		if (tAddNewEdge) {
			edges.add (aEdge);
		}
	}
	
	public void addNeighborVertexes (MapGraph aMapGraph) {
		MapCell tNeighborMapCell;
		Location tNeighborLocation;
		Location tEndLocation;
		int tNeighborLoc;
		Vertex tEndVertex;
		Vertex tNeighborVertex;
		
		for (Edge tEdge : edges) {
			tEndVertex = tEdge.getEndVertex ();
			tEndLocation = tEndVertex.getLocation ();
			tNeighborMapCell = mapCell.getNeighbor (tEndLocation.getLocation ());
			tNeighborLoc = Location.NO_LOCATION;
			if (tNeighborMapCell != MapCell.NO_MAP_CELL) {
				tNeighborLoc = mapCell.getSideFromNeighbor (tNeighborMapCell);
				tNeighborLocation = new Location (tNeighborLoc);
				if (! tNeighborMapCell.isTileOnCell ()) {
					tNeighborVertex = new Vertex (tNeighborMapCell, tNeighborLocation);
					aMapGraph.addVertex (tNeighborVertex);
				} else {
					if (! aMapGraph.containsMapCell (tNeighborMapCell)) {
						tNeighborVertex = new Vertex (tNeighborMapCell, tNeighborLocation);
						aMapGraph.addVertex (tNeighborVertex);
						tNeighborVertex.fillVertexEdges ();
					}
				}
			} else {
				System.out.println ("No Map Cell found as neighbor on side " + tNeighborLoc);
			}
		}
	}

	public boolean hasTokenFor (TokenCompany aTokenCompany) {
		boolean tHasTokenFor;
		Location tLocation;
		int tCompanyID;
		
		tHasTokenFor = false;
		if (mapCell.isTileOnCell ()) {
			tCompanyID = aTokenCompany.getID ();
			if (mapCell.hasStation (tCompanyID)) {
				tLocation = mapCell.getLocationWithStation (tCompanyID);
				tHasTokenFor = location.isSameLocationValue (tLocation);
			}
		}
		
		return tHasTokenFor;
	}

	public List<MapCell> getEmptyMapCells () {
		List<MapCell> tEmptyMapCells;
		MapCell tEmptyMapCell;
		HashSet<String> tVisited;
		List<Vertex> tNextToVisit;
		List<Edge> tEdges;
		Vertex tVertexToVisit;
		Vertex tRemoteVertex;
		String tVertexID;
		String tRemoteID;
		
		tEmptyMapCells = new LinkedList<MapCell> ();
		tVisited = new HashSet<String> ();
		tNextToVisit = new LinkedList<Vertex> ();
		tNextToVisit.add (this);
		
		while (! tNextToVisit.isEmpty ()) {
			tVertexToVisit = tNextToVisit.remove (0);
			tVertexID = tVertexToVisit.getID ();
//			System.out.println ("Visiting: " + tVertexID);
//			tVertexToVisit.printInfo ();
			if (tVertexToVisit.isEmptyMapCell ()) {
				tEmptyMapCell = tVertexToVisit.getMapCell ();
				if (! tEmptyMapCells.contains (tEmptyMapCell)) {
					tEmptyMapCells.add (tEmptyMapCell);
				}
			}
			
			if (! tVisited.contains (tVertexID)) {
				tVisited.add (tVertexID);
				tEdges = tVertexToVisit.getEdges ();
				for (Edge tEdge : tEdges) {
					tRemoteVertex = tEdge.getOtherVertex (tVertexToVisit);
					tRemoteID = tRemoteVertex.getID ();
					if (tVisited.contains (tRemoteID)) {
//						System.out.println ("Remote Vertex " + tRemoteID + " already Visited, not Adding");
					} else {
						if (tNextToVisit.contains (tRemoteVertex)) {
//							System.out.println ("Remote Vertex " + tRemoteID + " already in Visit List, not Adding");
						} else {
//							System.out.println ("Remote Vertex " + tRemoteID + " added for Visiting");
							tNextToVisit.add (tRemoteVertex);
						}
					}
				}
			}
		}
		
		return tEmptyMapCells;
	}
	
	public boolean isEmptyMapCell () {
		return ! mapCell.isTileOnCell ();
	}
}
