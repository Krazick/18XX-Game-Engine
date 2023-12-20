package ge18xx.map;

import org.mockito.Mockito;

import ge18xx.toplevel.MapFrame;
import geUtilities.xml.XMLFrame;

public class MapTestFactory {
	public static final MapTestFactory NO_MAP_TEST_FACTORY = null;

	public MapTestFactory () {

	}

	public MapFrame buildMapFrame () {
		MapFrame tMapFrame = (MapFrame) XMLFrame.NO_XML_FRAME;

		return tMapFrame;
	}

	public HexMap buildHexMap () {
		HexMap tHexMap;
		MapFrame tMapFrame;

		tMapFrame = buildMapFrame ();
		tHexMap = new HexMap (tMapFrame);

		return tHexMap;
	}

	public HexMap buildHexMapMock () {
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
	
	public MapCell buildMapCellMock (String aID) {
		MapCell mMapCell;
		
		mMapCell = Mockito.mock (MapCell.class);
		Mockito.when (mMapCell.getID ()).thenReturn (aID);
		
		return mMapCell;
	}
	
	public MapCell buildMapCellMock (String aID, int aXCenter, int aYCenter) {
		MapCell mMapCell;
		
		mMapCell = Mockito.mock (MapCell.class);
		Mockito.when (mMapCell.getID ()).thenReturn (aID);
		Mockito.when (mMapCell.getXCenter ()).thenReturn (aXCenter);
		Mockito.when (mMapCell.getYCenter ()).thenReturn (aYCenter);
		
		return mMapCell;
	}

	public void setMockAllowedRotation (MapCell mMapCell, int aIndex, boolean aAllowed) {
		if (mMapCell != MapCell.NO_MAP_CELL) {
			Mockito.when (mMapCell.getAllowedRotation (aIndex)).thenReturn (aAllowed);
		}
	}
	
	public Hex buildHex () {
		Hex tHex;
		
		tHex = new Hex ();
		
		return tHex;
	}
	
	public Hex buildHex (boolean aHexDirection) {
		Hex tHex;
		
		tHex = new Hex (aHexDirection);
		
		return tHex;
	}
	
	public Hex buildHex (int aOffsetX, int aOffsetY, boolean aHexDirection) {
		Hex tHex;
		
		tHex = new Hex (aOffsetX, aOffsetY, aHexDirection);
		
		return tHex;
	}
	
	public Hex buildHex (int aOffsetX, int aOffsetY, boolean aHexDirection, int aScale) {
		Hex tHex;
		
		tHex = new Hex (aOffsetX, aOffsetY, aHexDirection, aScale);
		
		return tHex;
	}
	
	public Location buildLocation () {
		Location tLocation;
		int tCenterLocation;

		tCenterLocation = Location.CENTER_CITY_LOC;
		tLocation = buildLocation (tCenterLocation);

		return tLocation;
	}

	public Location buildLocation (int aIntLocation) {
		Location tLocation;

		tLocation = new Location (aIntLocation);

		return tLocation;
	}
	
	public Location buildLocationMock () {
		Location mLocation = Mockito.mock (Location.class);

		Mockito.when (mLocation.getLocation ()).thenReturn (Location.NO_LOCATION);

		return mLocation;
	}
	
	public Location buildLocationMock (int aLocation) {
		Location mLocation = Mockito.mock (Location.class);
		
		Mockito.when (mLocation.getLocation ()).thenReturn (aLocation);

		return mLocation;
	}
}
