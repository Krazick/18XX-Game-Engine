package ge18xx.map;

import org.mockito.Mockito;

import ge18xx.toplevel.MapFrame;

public class MapTestFactory {

	public MapTestFactory () {
		
	}
	
	public MapFrame buildMapFrame () {
		MapFrame tMapFrame = (MapFrame) MapFrame.NO_XML_FRAME;
		
		return tMapFrame;
	}
	
	public HexMap buildHexMap () {
		HexMap tHexMap;
		MapFrame tMapFrame;
		
		tMapFrame = buildMapFrame ();
		tHexMap = new HexMap (tMapFrame);
		
		return tHexMap;
	}
	
	public HexMap buildMockHexMap () {
		MapCell tMapCell;
		HexMap mHexMap;
		
		tMapCell = buildMapCell ();
		mHexMap = Mockito.mock (HexMap.class);
		Mockito.when (mHexMap.getMapCellForID ("T1")).thenReturn (tMapCell);
		
		return mHexMap;
	}
	
	public MapCell buildMapCell () {
		MapCell tMapCell;
		
		tMapCell = buildMapCell ("T1");
		
		return tMapCell;
	}
	
	public MapCell buildMapCell (String aID) {
		MapCell tMapCell;
		
		tMapCell = buildMapCell (aID, 100, 100);
		
		return tMapCell;
	}
	
	public MapCell buildMapCell (String aID, int aXc, int aYc) {
		HexMap tHexMap;
		MapCell tMapCell;
		
		tHexMap = buildHexMap ();
		tMapCell = new MapCell (aXc, aYc, tHexMap);
		tMapCell.setID (aID);
		
		return tMapCell;
	}

}
