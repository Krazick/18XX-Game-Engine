package ge18xx.map;

import java.util.LinkedList;
import java.util.List;

import ge18xx.company.TokenCompany;

public class MapGraph {
	public static final MapGraph NO_MAP_GRAPH = null;
	List<Vertex> vertexes;

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
}
