package ge18xx.map;

import java.util.LinkedList;
import java.util.List;

public class MapGraph {
	public static final MapGraph NO_MAP_GRAPH = null;
	List<MapGraphNode> mapGraphNodes;
	
	public MapGraph () {
		mapGraphNodes = new LinkedList<MapGraphNode> ();
	}

	public void clear () {
		mapGraphNodes.removeAll (mapGraphNodes);
	}
	
	public boolean isEmpty () {
		return mapGraphNodes.isEmpty ();
	}
	
	public void addMapGraphNode (MapGraphNode aMapGraphNode) {
		if (aMapGraphNode != MapGraphNode.NO_MAP_GRAPH_NODE) {
			if (! containsMapGraphNode (aMapGraphNode)) {
				mapGraphNodes.add (aMapGraphNode);
			}
		}
	}
	
	public List<MapGraphNode> getMapGraphNodes () {
		return mapGraphNodes;
	}
	
	public boolean containsMapGraphNode (MapGraphNode aMapGraphNode) {
		boolean tContainsMapGraphNode;
		String tMapGraphNodeID;
		String tFoundMapGraphNodeID;
		
		tContainsMapGraphNode = false;
		if (! mapGraphNodes.isEmpty ()) {
			if (aMapGraphNode != MapGraphNode.NO_MAP_GRAPH_NODE) {
				tMapGraphNodeID = aMapGraphNode.getID ();
				for (MapGraphNode tMapGraphNode: mapGraphNodes) {
					tFoundMapGraphNodeID = tMapGraphNode.getID ();
					if (tFoundMapGraphNodeID.equals (tMapGraphNodeID)) {
						tContainsMapGraphNode = true;
					}
				}
			}
		}
		
		return tContainsMapGraphNode;
	}
}
