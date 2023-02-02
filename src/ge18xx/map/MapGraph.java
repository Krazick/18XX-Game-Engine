package ge18xx.map;

import java.util.LinkedList;
import java.util.List;

import ge18xx.company.TokenCompany;

public class MapGraph {
	public static final MapGraph NO_MAP_GRAPH = null;
	List<Vertex> vertexes;
	List<Vertex> visitedVertexes;

	public MapGraph () {
		vertexes = new LinkedList<> ();
	}

	public void clear () {
		vertexes.removeAll (vertexes);
	}

	public boolean isEmpty () {
		return vertexes.isEmpty ();
	}

	public void addVertex (Vertex aVertex) {
		if (aVertex != Vertex.NO_VERTEX) {
			if (! containsVertex (aVertex)) {
				vertexes.add (aVertex);
			}
		}
	}

	public List<Vertex> getVertexes () {
		return vertexes;
	}

	public boolean removeVertex (Vertex aVertex) {
		return vertexes.remove (aVertex);
	}

	public boolean containsVertex (Vertex aVertex) {
		boolean containsVertex;
		String tVertexID;
		String tFoundVertexID;

		containsVertex = false;
		if (! vertexes.isEmpty ()) {
			if (aVertex != Vertex.NO_VERTEX) {
				tVertexID = aVertex.getID ();
				for (Vertex tVertex: vertexes) {
					tFoundVertexID = tVertex.getID ();
					if (tFoundVertexID.equals (tVertexID)) {
						containsVertex = true;
					}
				}
			}
		}

		return containsVertex;
	}

	public boolean containsMapCell (MapCell aMapCell) {
		boolean tContainsMapCell;
		MapCell tFoundMapCell;

		tContainsMapCell = false;
		if (! vertexes.isEmpty ()) {
			if (aMapCell != MapCell.NO_MAP_CELL) {
				for (Vertex tVertex: vertexes) {
					tFoundMapCell = tVertex.getMapCell ();
					if (tFoundMapCell.sameID (aMapCell)) {
						tContainsMapCell = true;
					}
				}

			}
		}

		return tContainsMapCell;
	}

	public Vertex getVertexWith (MapCell aMapCell) {
		MapCell tFoundMapCell;
		Vertex tFoundVertex;

		tFoundVertex = Vertex.NO_VERTEX;
		if (! vertexes.isEmpty ()) {
			if (aMapCell != MapCell.NO_MAP_CELL) {
				for (Vertex tVertex: vertexes) {
					tFoundMapCell = tVertex.getMapCell ();
					if (tFoundMapCell.sameID (aMapCell)) {
						tFoundVertex = tVertex;
					}
				}
			}
		}

		return tFoundVertex;
	}

	public Vertex getVertexWithID (String aID) {
		String tFoundID;
		Vertex tFoundVertex;

		tFoundVertex = Vertex.NO_VERTEX;
		if (! vertexes.isEmpty ()) {
			if (aID != null) {
				for (Vertex tVertex: vertexes) {
					tFoundID = tVertex.getID ();
					if (tFoundID.equals (aID)) {
						tFoundVertex = tVertex;
					}
				}
			}
		}

		return tFoundVertex;
	}

	public void addNeighborVertexes (Vertex aVertex) {
		aVertex.addNeighborVertexes (this);
	}

	public List<Vertex> getVertexesWithToken (TokenCompany aTokenCompany) {
		List<Vertex> tVertexes;

		tVertexes = new LinkedList<> ();

		for (Vertex tVertex : vertexes) {
			if (tVertex.hasTokenFor (aTokenCompany)) {
				tVertexes.add (tVertex);
			}
		}

		return tVertexes;
	}

	public List<MapCell> getEmptyMapCellsWithCompany (List<Vertex> aBaseVertexes) {
		List<MapCell> tEmptyMapCells;
		List<MapCell> tAllEmptyMapCells;

		tAllEmptyMapCells = new LinkedList<> ();
		for (Vertex tBaseVertex : aBaseVertexes) {
			tEmptyMapCells = tBaseVertex.getEmptyMapCells ();
			for (MapCell tEmptyMapCell : tEmptyMapCells) {
				if (! tAllEmptyMapCells.contains (tEmptyMapCell)) {
					tAllEmptyMapCells.add (tEmptyMapCell);
				}
			}
		}

		return tAllEmptyMapCells;
	}
	
	public void breadthFirstSearch (String aHomeVertexID) {
		Vertex tVertex;
		String tVertexID;
		List<String> tQueueVertexIDs;
		
		tQueueVertexIDs = new LinkedList<String> ();
		visitedVertexes = new LinkedList<Vertex> ();
		
		tVertex = getVertexWithID (aHomeVertexID);
		tQueueVertexIDs.add (aHomeVertexID);
		visitedVertexes.add (tVertex);
		while (! tQueueVertexIDs.isEmpty ()) {
			tVertexID = tQueueVertexIDs.remove (0);
			tVertex = getVertexWithID (tVertexID);
			tVertex.visitNeighbors (tQueueVertexIDs, visitedVertexes);
		}
		System.out.println ("Visited Vertexes: " + visitedVertexes.toString ());
	}
	
	public boolean foundInBFS (String aMapCellID) {
		boolean tFoundInBFS;
		String tFoundMapCellID;
		
//		Visited Vertexes: [M4:50, M4:1, M4:3, M4:5, N3:4, M6:0, L3:2, N3:50, M6:23, N3:1, N3:5, N3:0, M6:4, O2:4, M2:2, N1:3, L7:1, O2:94, N1:93, L7:4, K8:1, K8:50, K8:0, K8:3, K8:4, K6:3, K10:0, J9:1, K6:0, K10:2, J9:50, K4:3, K10:5, L11:5, J9:4, K4:0, J9:2, L11:3, I10:1, K2:3, L13:0, I10:4, K2:93, L13:50, H11:1, L13:1, L13:3, L13:4, H11:20, M12:4, L15:0, K14:1, H11:3, M12:1, M12:0, L15:13, H13:0, N11:4, M10:3, H13:5, N11:17, M10:33, G12:2, N11:5, M10:1, M10:2, N9:4, M10:25, N9:16, M10:0, N9:3, M8:3, N11:0, M8:2, N11:13, N9:5, N11:1, O10:4, O10:1, P9:4, P9:50, P9:1, Q8:4, Q8:94]
		
		tFoundInBFS = false;
		for (Vertex tVertex : visitedVertexes) {
			if (! tVertex.isOnSide ()) {
				tFoundMapCellID = tVertex.getMapCellID ();
				if (tFoundMapCellID.equals (aMapCellID)) {
					tFoundInBFS = true;
				}
			}
		}
		
		return tFoundInBFS;
	}
}
