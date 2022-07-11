package ge18xx.map;

import java.util.LinkedList;
import java.util.List;

import ge18xx.tiles.Tile;
import ge18xx.tiles.Track;

public class Vertex {
	public final static Vertex NO_VERTEX = null;
	Vertex neighborVertex;
	MapCell mapCell;
	Location location;
	List<Edge> edges;
	String id;
	
	public Vertex (MapCell aMapCell, Location aLocation) {
		
		setMapCell (aMapCell);
		setLocation (aLocation);
		edges = new LinkedList<Edge> ();
		setNeighborVertex (NO_VERTEX);
		setID ();
	}

	public void attachNeighborVertex (MapCell aMapCell, Location aLocation, Vertex tNeighborVertex) {
		MapCell tNeighborMapCell;
		Location tNeighborLocation;
		int tFoundLocation;
		
		tNeighborVertex = NO_VERTEX;
		if (aLocation.isSide ()) {
			tNeighborMapCell = aMapCell.getNeighbor (location);
			if (tNeighborMapCell != MapCell.NO_MAP_CELL) {
				tFoundLocation = aMapCell.getSideFromNeighbor (tNeighborMapCell);
				tNeighborLocation = new Location (tFoundLocation);
				tNeighborVertex = new Vertex (tNeighborMapCell, tNeighborLocation);
			}
		}
		setNeighborVertex (tNeighborVertex);

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
	
	public Vertex getNeighborVertex () {
		return neighborVertex;
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
		String tNeighborID;
		String tFullID;
		
		tFullID = buildID ();

		if (neighborVertex != NO_VERTEX) {
			tNeighborID = neighborVertex.buildID ();
			tFullID += "|" + tNeighborID;
		}
		
		id = tFullID;
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
	
	public void setNeighborVertex (Vertex aNeighborVertex) {
		neighborVertex = NO_VERTEX;
		if (aNeighborVertex != NO_VERTEX) {
			if (aNeighborVertex.isOnSide ()) {
				neighborVertex = aNeighborVertex;
			} else {
				neighborVertex = NO_VERTEX;
			}
		}
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
//		Vertex tVertex2;
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
//				tVertex2 = Vertex.NO_VERTEX;
				if (tStartLocation.getLocation () == location.getLocation ()) {
					tVertex = new Vertex (mapCell, tEndLocation);
				} else if (tEndLocation.getLocation () == location.getLocation ()) {
					tVertex = new Vertex (mapCell, tStartLocation);
//				} else {
//					tVertex = new MapGraphNode (mapCell, tStartLocation);
//					tVertex2 = new MapGraphNode (mapCell, tEndLocation);
//					tMapEdge1 = new MapEdge (tTrack, tVertex, tVertex2);
//					tVertex.addMapEdge (tMapEdge1);
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
		edges.add (aEdge);
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
}
