package ge18xx.map;

import java.util.LinkedList;
import java.util.List;

public class MapGraphNode {
	public final static MapGraphNode NO_MAP_GRAPH_NODE = null;
	MapCell mapCell;
	Location location;
	List<MapEdge> mapEdges;
	String id;
	
	public MapGraphNode (MapCell aMapCell, Location aLocation) {
		setMapCell (aMapCell);
		setLocation (aLocation);
		setID ();
		mapEdges = new LinkedList<MapEdge> ();
	}

	public void printInfo () {
		System.out.println ("Node ID: " + id + " MapCell ID " + mapCell.getID () +
				" Location " + location.getLocation () + " Edge Count " + mapEdges.size ());
	}
	
	public String getID () {
		return id;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public Location getLocation () {
		return location;
	}
	
	public void setID () {
		id = mapCell.getCellID () + ":" + location.getLocation ();
	}
	
	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
}
