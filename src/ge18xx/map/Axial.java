package ge18xx.map;

public class Axial {
	int colIndex;
	int rowIndex;
	
	public Axial (int aColIndex, int aRowIndex) {
		setCoordinates (aColIndex, aRowIndex);
	}
	
	public Axial (Cube aCube) {
		setCoordinates (aCube.getColIndex (), aCube.getRowIndex ());
	}
	
	public void setCoordinates (int aColIndex, int aRowIndex) {
		colIndex = aColIndex;
		rowIndex = aRowIndex;
	}
	
	public int getColIndex () {
		return colIndex;
	}
	
	public int getRowIndex () {
		return rowIndex;
	}
	
	public String getCoordinates () {
		String tString;
		
		tString = "(" + colIndex + "," + rowIndex + ")";
		
		return tString;
	}
	
	public Cube toCube () {
		int sIndex;
		
		sIndex = - colIndex - rowIndex;
		
		return new Cube (colIndex, rowIndex, sIndex);
	}
}
