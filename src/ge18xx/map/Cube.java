package ge18xx.map;

public class Cube {
	int colIndex;
	int rowIndex;
	int sIndex;
	
	public Cube (int aColIndex, int aRowIndex, int aSIndex) {
		setCoordinates (aColIndex, aRowIndex, aSIndex);
	}
	
	public Cube (Axial aAxial) {
		int tColIndex;
		int tRowIndex;
		int tSIndex;
		
		tColIndex = aAxial.getColIndex ();
		tRowIndex = aAxial.getRowIndex ();
		tSIndex = - tColIndex - tRowIndex;
		setCoordinates (tColIndex, tRowIndex, tSIndex);
	}
	
	public Axial toAxial () {
		return new Axial (colIndex, rowIndex);
	}
	
	public void setCoordinates (int aColIndex, int aRowIndex, int aSIndex) {
		colIndex = aColIndex;
		rowIndex = aRowIndex;
		sIndex = aSIndex;
	}
	
	public int getColIndex () {
		return colIndex;
	}
	
	public int getRowIndex () {
		return rowIndex;
	}
	
	public int getSIndex () {
		return sIndex;
	}
	
	public Cube subtract (Cube aAlpha, Cube aBeta) {
		Cube tCube;
		int tCol;
		int tRow;
		int tS;
		
		tCol = aAlpha.getColIndex () - aBeta.getColIndex ();
		tRow = aAlpha.getRowIndex () - aBeta.getRowIndex ();
		tS = aAlpha.getSIndex () - aBeta.getSIndex ();
		tCube = new Cube (tCol, tRow, tS);
		
		return tCube;
	}
	
	public int cubeDistance (Cube aBeta) {
		int tCubeDistance;
		Cube tDistanceCube;
		
		tDistanceCube = subtract (this, aBeta);
		tCubeDistance = Math.max (
				Math.abs (tDistanceCube.getColIndex ()), 
					Math.max (Math.abs (tDistanceCube.getRowIndex ()),
								Math.abs (tDistanceCube.getSIndex ())));
	
		return tCubeDistance;
	}
	
	public String getCoordinates () {
		String tString;
		
		tString = "(" + colIndex + "," + rowIndex + "," + sIndex + ")";
		
		return tString;
	}
}
