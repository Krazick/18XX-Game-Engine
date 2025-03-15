package ge18xx.map.hexGrids;

public class OffsetCoord {
	public final int col;
	public final int row;
	static public int EVEN = 1;
	static public int ODD = -1;
	
	public OffsetCoord (int col, int row) {
        this.col = col;
        this.row = row;
    }
 
    static public OffsetCoord qoffsetFromCube (int offset, Hex h) {
        int col = h.q;
        int row = h.r + (int) ((h.q + offset * (h.q & 1)) / 2);
        
        if ((offset != OffsetCoord.EVEN) && (offset != OffsetCoord.ODD)) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new OffsetCoord(col, row);
    }

    static public Hex qoffsetToCube (int offset, OffsetCoord h) {
        int q = h.col;
        int r = h.row - (int) ((h.col + offset * (h.col & 1)) / 2);
        int s = -q - r;
        
        if ((offset != OffsetCoord.EVEN) && (offset != OffsetCoord.ODD)) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new Hex(q, r, s);
    }

    static public OffsetCoord roffsetFromCube (int offset, Hex h) {
        int col = h.q + (int)((h.r + offset * (h.r & 1)) / 2);
        int row = h.r;
 
       	if ((offset != OffsetCoord.EVEN) && (offset != OffsetCoord.ODD)) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new OffsetCoord(col, row);
    }

    static public Hex roffsetToCube (int offset, OffsetCoord h) {
        int q = h.col - (int) ((h.row + offset * (h.row & 1)) / 2);
        int r = h.row;
        int s = -q - r;
        
        if ((offset != OffsetCoord.EVEN) && (offset != OffsetCoord.ODD)) {
            throw new IllegalArgumentException ("offset must be EVEN (+1) or ODD (-1)");
        }
        return new Hex (q, r, s);
    }
    
	public String getCoordinates () {
		String tString;
		
		tString = "(" + col + "," + row + ")";
		
		return tString;
	}
	
	public int getDistanceTo (int aEvenOdd, OffsetCoord aOffsetCoord) throws IllegalArgumentException {
		Hex tHex1;
		Hex tHex2;
		int tDistance;
		
		tHex1 = OffsetCoord.roffsetToCube (aEvenOdd, this);
		tHex2 = OffsetCoord.roffsetToCube (aEvenOdd, aOffsetCoord);

		tDistance = tHex1.distance (tHex2);
		
		return tDistance;
	}
}
