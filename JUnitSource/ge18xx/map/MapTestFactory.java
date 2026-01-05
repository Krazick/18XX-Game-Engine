package ge18xx.map;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.toplevel.MapFrame;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class MapTestFactory {
	public static final MapTestFactory NO_MAP_TEST_FACTORY = null;
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;

	public MapTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}

	public GameTestFactory getGameTestFactory () {
		return gameTestFactory;
	}
	
	public UtilitiesTestFactory getUtilitiesTestFactory () {
		return utilitiesTestFactory;
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

	public GameMap buildHexMap (String aMapFrameName, GameManager aGameManager) {
		GameMap tHexMap;
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

	public MapCell buildMapCell (XMLNode aChildNode, int [] aTerrainCost, int [] aTerrainType, String aChildName) {
		
		MapCell tMapCell;
		GameMap mHexMap;
		String tMapDirection;
		
		mHexMap = buildHexMapMock ();
		tMapDirection = "EW";
		tMapCell = new MapCell (mHexMap, tMapDirection);
		
		return tMapCell;
	}
	
	public MapCell buildMapCell (String aID, int aXc, int aYc) {
		GameMap tHexMap;
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
	
	public MapCell buildAMapCellFromXML (int tMapCellXMLIndex, String aMapCellID) {
		String tMapCell1TestXML = "<MapCell>\n"
				+ "		<Terrain category=\"base\" type=\"Clear\" />\n"
				+ "		<Terrain category=\"optional\" location=\"16\" type=\"River\" />\n"
				+ "		<TileName name=\"NY\" location=\"13\" />\n"
				+ "		<Tile number=\"9940\" orientation=\"0\" starting=\"TRUE\" />\n"
				+ "		<RevenueCenter id=\"8\" location=\"8\" name=\"\" number=\"1\"\n"
				+ "			type=\"Single City\" />\n"
				+ "		<RevenueCenter id=\"8\" location=\"11\" name=\"\" number=\"1\"\n"
				+ "			type=\"Single City\" />\n"
				+ " </MapCell>\n";
		String tMapCell2TestXML = "<MapCell>\n"
				+ " 	<Terrain category=\"base\" type=\"Clear\" />\n"
				+ "	</MapCell>\n";
		String tMapCell3TestXML = "<MapCell>\n"
				+ "		<Terrain category=\"base\" type=\"Clear\" />\n"
				+ "		<Terrain category=\"optional\" location=\"50\" type=\"Hill\" />\n"
				+ "	</MapCell>\n"
				+ "";
		String tMapCell4TestXML = "<MapCell>\n"
				+ "		<Terrain category=\"base\" type=\"Clear\" />\n"
				+ "		<Terrain category=\"optional\" location=\"50\" type=\"Clear\" />\n"
				+ "		<Terrain category=\"optional\" location=\"50\" type=\"River\" />\n"
				+ "	</MapCell>\n"
				+ "";
		String tMapCell5TestXML = "<MapCell>\n"
				+ "		<Terrain category=\"base\" type=\"Clear\" />\n"
				+ "		<Terrain category=\"optional\" location=\"11\" type=\"Hill\" />\n"
				+ "		<Terrain category=\"optional\" location=\"8\" type=\"River\" />\n"
				+ "	</MapCell>\n"
				+ "";
		MapCell tMapCell;
		
		tMapCell = MapCell.NO_MAP_CELL;

		if (tMapCellXMLIndex == 1) {
			tMapCell = buildMapCellFromXML (tMapCell1TestXML, aMapCellID);
		} else if (tMapCellXMLIndex == 2) {
			tMapCell = buildMapCellFromXML (tMapCell2TestXML, aMapCellID);
		} else if (tMapCellXMLIndex == 3) {
			tMapCell = buildMapCellFromXML (tMapCell3TestXML, aMapCellID);
		} else if (tMapCellXMLIndex == 4) {
			tMapCell = buildMapCellFromXML (tMapCell4TestXML, aMapCellID);
		} else if (tMapCellXMLIndex == 5) {
			tMapCell = buildMapCellFromXML (tMapCell5TestXML, aMapCellID);
		}
		
		return tMapCell;
	}

	private MapCell buildMapCellFromXML (String aMapCellTextXML, String aMapCellID) {
		XMLNode tMapCellNode;
		int tTerrainCost [];
		int tTerrainType [];
		MapCell tMapCell;
		
		tMapCellNode = utilitiesTestFactory.buildXMLNode (aMapCellTextXML);
		tMapCell = MapCell.NO_MAP_CELL;
		if (tMapCellNode != XMLNode.NO_NODE) {
			tMapCell = buildMapCell ();
			tTerrainType = new int [Terrain.MAX_TERRAIN_TYPES];
			tTerrainCost = new int [Terrain.MAX_TERRAIN_TYPES];

			tMapCell.loadXMLCell (tMapCellNode, tTerrainCost, tTerrainType, aMapCellID);
		}
		
		return tMapCell;
	}
}
