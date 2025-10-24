package ge18xx.map;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.toplevel.MapFrame;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class MapTestFactory {
	public static final MapTestFactory NO_MAP_TEST_FACTORY = null;

	public MapTestFactory () {

	}

	public MapFrame buildMapFrame () {
		MapFrame tMapFrame;

		tMapFrame = (MapFrame) XMLFrame.NO_XML_FRAME;
		
		return tMapFrame;
	}

	public MapFrame buildMapFrame (String aMapFrameName, GameManager aGameManager) {
		MapFrame tMapFrame;
		
		tMapFrame = new MapFrame (aMapFrameName, aGameManager);
		
		return tMapFrame;
	}
	
	public MapFrame buildMapFrameMock () {
		MapFrame mMapFrame;
		
		mMapFrame = Mockito.mock (MapFrame.class);
		
		return mMapFrame;
	}

	public HexMap buildHexMap () {
		HexMap tHexMap;
		MapFrame tMapFrame;

		tMapFrame = buildMapFrame ();
		tHexMap = new HexMap (tMapFrame);

		return tHexMap;
	}

	public HexMap buildHexMap (String aMapFrameName, GameManager aGameManager) {
		HexMap tHexMap;
		MapFrame tMapFrame;
		
		tMapFrame = buildMapFrame (aMapFrameName, aGameManager);
		tHexMap = new HexMap (tMapFrame);
		
		return tHexMap;
	}
	
	public HexMap buildHexMap (MapFrame aMapFrame) {
		HexMap tHexMap;
		
		tHexMap = new HexMap (aMapFrame);
		
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

//	protected void loadXMLMapCell (XMLNode aChildNode, int [] aTerrainCost, int [] aTerrainType, String aChildName) {
	public MapCell buildMapCell (XMLNode aChildNode, int [] aTerrainCost, int [] aTerrainType, String aChildName) {
		
		MapCell tMapCell;
		HexMap mHexMap;
		String tMapDirection;
		
		mHexMap = buildHexMapMock ();
		tMapDirection = "EW";
		tMapCell = new MapCell (mHexMap, tMapDirection);
		
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
