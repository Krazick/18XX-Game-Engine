package ge18xx.map;

import ge18xx.tiles.Track;

public class Edge {
	public static final Edge NO_EDGE = null;
	public static final String NO_TRACK_LABEL = "EDGE";
	Track track;
	Vertex startVertex;
	Vertex endVertex;

	public Edge (Track aTrack, Vertex aStartVertex, Vertex aEndVertex) {
		setTrack (aTrack);
		setStartVertex (aStartVertex);
		setEndVertex (aEndVertex);
	}

	public Track getTrack () {
		return track;
	}

	public Vertex getStartVertex () {
		return startVertex;
	}

	public Vertex getEndVertex () {
		return endVertex;
	}

	public boolean bothVertexesOnSide () {
		boolean tBothVertexesOnSide;

		tBothVertexesOnSide = false;
		if (startVertex.isOnSide () && endVertex.isOnSide ()) {
			tBothVertexesOnSide = true;
		}

		return tBothVertexesOnSide;
	}

	public void reverseVertexes () {
		Vertex tVertex;

		tVertex = startVertex;
		startVertex = endVertex;
		endVertex = tVertex;
	}

	public void setTrack (Track aTrack) {
		track = aTrack;
	}

	public void setStartVertex (Vertex aStartVertex) {
		startVertex = aStartVertex;
	}

	public void setEndVertex (Vertex aEndVertex) {
		endVertex = aEndVertex;
	}

	public boolean sameEdge (Edge aOtherEdge) {
		boolean tSameEdge;
		String tOtherForwardInfo;
		String tOtherBackwardInfo;
		String tThisInfo;

		tOtherForwardInfo = aOtherEdge.getForwardInfo ();
		tOtherBackwardInfo = aOtherEdge.getBackwardInfo ();
		tThisInfo = getForwardInfo ();
		tSameEdge = false;
		if (tThisInfo.equals (tOtherForwardInfo)) {
			tSameEdge = true;
		} else if (tThisInfo.equals (tOtherBackwardInfo)) {
			tSameEdge = true;
		}

		return tSameEdge;
	}

	public String getInfo () {
		String tInfo;

		tInfo = getForwardInfo ();

		return tInfo;
	}

	public boolean isNoTrackEdge () {
		boolean tIsNoTrackEdge;
		
		if (track == Track.NO_TRACK) {
			tIsNoTrackEdge = true;
		} else {
			tIsNoTrackEdge = false;
		}
		
		return tIsNoTrackEdge;
	}
	
	public String getForwardInfo () {
		String tInfo;
		String tGaugeName;

		if (isNoTrackEdge ()) {
			tGaugeName = NO_TRACK_LABEL;
		} else {
			tGaugeName = track.getGaugeName ();
		}

		tInfo = startVertex.getID () + "|" + tGaugeName + "|" + endVertex.getID ();

		return tInfo;
	}

	public String getBackwardInfo () {
		String tInfo;
		String tGaugeName;

		if (isNoTrackEdge ()) {
			tGaugeName = NO_TRACK_LABEL;
		} else {
			tGaugeName = track.getGaugeName ();
		}

		tInfo = endVertex.getID () + "|" + tGaugeName + "|" + startVertex.getID ();

		return tInfo;
	}

	public Vertex getOtherVertex (Vertex aVertex) {
		Vertex tFoundVertex;

		if (startVertex == aVertex) {
			tFoundVertex = endVertex;
		} else {
			tFoundVertex = startVertex;
		}

		return tFoundVertex;
	}

	public void replaceVertex (Vertex aNewVertex) {
		if (startVertex.getID ().equals (aNewVertex.getID ())) {
			setStartVertex (aNewVertex);
		}
		if (endVertex.getID ().equals (aNewVertex.getID ())) {
			setEndVertex (aNewVertex);
		}
	}
}
