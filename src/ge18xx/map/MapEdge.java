package ge18xx.map;

import ge18xx.tiles.Track;

public class MapEdge {
	Track track;
	MapGraphNode startNode;
	MapGraphNode endNode;
	
	public MapEdge (Track aTrack, MapGraphNode aStartNode, MapGraphNode aEndNode) {
		setTrack (aTrack);
		setStartNode (aStartNode);
		setEndNode (aEndNode);
	}

	public Track getTrack () {
		return track;
	}
	
	public MapGraphNode getStartNode () {
		return startNode;
	}
	
	public MapGraphNode getEndNode () {
		return endNode;
	}
	
	public void setTrack (Track aTrack) {
		track = aTrack;
	}
	
	public void setStartNode (MapGraphNode aStartNode) {
		startNode = aStartNode;
	}
	
	public void setEndNode (MapGraphNode aEndNode) {
		endNode = aEndNode;
	}

}
