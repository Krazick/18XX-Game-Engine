package ge18xx.tiles;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import ge18xx.toplevel.TileTrayFrame;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class TilesTestFactory {
	GameTestFactory gameTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	MapTestFactory mapTestFactory;
	String testTiles [] = {
			// Tile 0 - Yellow # 9995
		"<Tile number=\"9995\" type=\"Yellow\" fixed=\"true\">\n"
		+ "	<TileName location=\"43\" name=\"Toronto\" />\n"
		+ "	<Track enter=\"1\" exit=\"7\" gauge=\"NORMAL\" />\n"
		+ "	<Track enter=\"4\" exit=\"10\" gauge=\"NORMAL\" />\n"
		+ "	<RevenueCenter id=\"-1\" location=\"8\" name=\"\" number=\"0\" type=\"Destination City\" />\n"
		+ "	<RevenueCenter id=\"-1\" location=\"7\" name=\"\" number=\"1\" type=\"Single City\">\n"
		+ "		<Revenue location=\"12\" phase=\"1\" value=\"30\" />\n"
		+ "	</RevenueCenter>\n"
		+ "	<RevenueCenter id=\"-1\" location=\"10\" name=\"\" number=\"1\" type=\"Single City\">\n"
		+ "		<Revenue location=\"-1\" phase=\"1\" value=\"30\" />\n"
		+ "	</RevenueCenter>\n"
		+ "</Tile>" ,
		
			// Tile 1 - Green # 14
		"<Tile number=\"14\" type=\"Green\">\n"
		+ "		<Track enter=\"0\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "		<Track enter=\"1\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "		<Track enter=\"3\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "		<Track enter=\"4\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "		<RevenueCenter id=\"-1\" location=\"50\" name=\"\" number=\"2\" type=\"Double City\">\n"
		+ "			<Revenue location=\"42\" phase=\"0\" value=\"30\" />\n" + "		</RevenueCenter>\n"
		+ "	</Tile>" ,
		
			// Tile 2 - Green # 120
		"<Tile number=\"120\" type=\"Green\">\n "
		 + "    <TileName location=\"43\" name=\"Toronto\" />\n "
		 + "    <Track enter=\"0\" exit=\"13\" gauge=\"NORMAL\" />\n "
		 + "    <Track enter=\"1\" exit=\"13\" gauge=\"NORMAL\" />\n "
		 + "    <Track enter=\"4\" exit=\"17\" gauge=\"NORMAL\" />\n "
		 + "    <Track enter=\"5\" exit=\"17\" gauge=\"NORMAL\" />\n "
		 + "    <RevenueCenter id=\"-1\" location=\"13\" name=\"\" number=\"1\" type=\"Single City\">\n "
		 + "            <Revenue location=\"12\" phase=\"0\" value=\"60\" />\n "
		 + "    </RevenueCenter>\n "
		 + "    <RevenueCenter id=\"-1\" location=\"17\" name=\"\" number=\"1\" type=\"Single City\">\n "
		 + "            <Revenue location=\"-1\" phase=\"0\" value=\"60\" />\n "
		 + "    </RevenueCenter>\n "
		 + "</Tile>",
		 
			// Tile 3 - Green # 54
		 "	<Tile number=\"54\" type=\"Green\">\n"
		 + "		<TileName location=\"16\" name=\"NY\" />\n"
		 + "		<Track enter=\"2\" exit=\"14\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"1\" exit=\"14\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"5\" exit=\"12\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"0\" exit=\"12\" gauge=\"NORMAL\" />\n"
		 + "		<RevenueCenter id=\"-1\" location=\"14\" name=\"\" number=\"1\" type=\"Single City\">\n"
		 + "			<Revenue location=\"17\" phase=\"0\" value=\"60\" />\n"
		 + "		</RevenueCenter>\n"
		 + "		<RevenueCenter id=\"-1\" location=\"12\" name=\"\" number=\"1\" type=\"Single City\">\n"
		 + "			<Revenue location=\"17\" phase=\"0\" value=\"60\" />\n"
		 + "		</RevenueCenter>\n"
		 + "	</Tile>",
		 
			// Tile 4 - Green # 59
		 "	<Tile number=\"59\" type=\"Green\">\n"
		 + "		<TileName location=\"17\" name=\"OO\" />\n"
		 + "		<Track enter=\"1\" exit=\"13\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"3\" exit=\"16\" gauge=\"NORMAL\" />\n"
		 + "		<RevenueCenter id=\"-1\" location=\"13\" name=\"\" number=\"1\" type=\"Single City\">\n"
		 + "			<Revenue location=\"50\" phase=\"0\" value=\"40\" />\n"
		 + "		</RevenueCenter>\n"
		 + "		<RevenueCenter id=\"-1\" location=\"16\" name=\"\" number=\"1\" type=\"Single City\">\n"
		 + "			<Revenue location=\"50\" phase=\"0\" value=\"40\" />\n"
		 + "		</RevenueCenter>\n"
		 + "	</Tile>",
		 
			// Tile 5 - Yellow # 55
		 "	<Tile number=\"55\" type=\"Yellow\">\n"
		 + "		<Track enter=\"2\" exit=\"8\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"5\" exit=\"8\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"3\" exit=\"6\" gauge=\"OVERPASS\" />\n"
		 + "		<Track enter=\"3\" exit=\"6\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"0\" exit=\"6\" gauge=\"NORMAL\" />\n"
		 + "		<RevenueCenter id=\"-1\" location=\"6\" name=\"\" type=\"Small Town\">\n"
		 + "			<Revenue location=\"41\" phase=\"0\" value=\"10\" />\n"
		 + "		</RevenueCenter>\n"
		 + "		<RevenueCenter id=\"-1\" location=\"8\" name=\"\" type=\"Small Town\">\n"
		 + "			<Revenue location=\"-1\" phase=\"0\" value=\"10\" />\n"
		 + "		</RevenueCenter>\n"
		 + "</Tile>",
		 
			// Tile 6 - Green # 210
		"	<Tile number=\"210\" type=\"Green\">\n"
		+ "		<TileName location=\"10\" name=\"XX\" />\n"
		+ "		<Track enter=\"0\" exit=\"6\" gauge=\"NORMAL\" />\n"
		+ "		<Track enter=\"3\" exit=\"6\" gauge=\"NORMAL\" />\n"
		+ "		<Track enter=\"1\" exit=\"14\" gauge=\"NORMAL\" />\n"
		+ "		<Track enter=\"2\" exit=\"14\" gauge=\"NORMAL\" />\n"
		+ "		<RevenueCenter id=\"-1\" location=\"6\" name=\"\" number=\"1\" type=\"Single City\">\n"
		+ "			<Revenue location=\"15\" phase=\"0\" value=\"30\" />\n"
		+ "		</RevenueCenter>\n"
		+ "		<RevenueCenter id=\"-1\" location=\"14\" name=\"\" number=\"1\" type=\"Single City\">\n"
		+ "			<Revenue location=\"15\" phase=\"0\" value=\"30\" />\n"
		+ "		</RevenueCenter>\n"
		+ "	</Tile>",
		
			// Tile 7 - Green # 208
		"<Tile number=\"208\" type=\"Green\">\n"
		+ "   <TileName location=\"12\" name=\"Y\" />\n"
		+ "   <Track enter=\"0\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "   <Track enter=\"1\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "   <Track enter=\"3\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"4\" exit=\"50\" gauge=\"NORMAL\" />\n"
		+ "   <RevenueCenter id=\"-1\" location=\"50\" name=\"\" number=\"2\" type=\"Double City\">\n"
		+ "	     <Revenue location=\"42\" phase=\"0\" value=\"40\" />\n"
		+ "   </RevenueCenter>\n"
		+ "</Tile>",
		
			// Tile 8 - Brown # 221
		"<Tile number=\"221\" type=\"Brown\">\n"
		+ "	  <TileName location=\"17\" name=\"HH\" />\n"
		+ "	  <Track enter=\"0\" exit=\"6\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"1\" exit=\"6\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"5\" exit=\"6\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"2\" exit=\"9\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"3\" exit=\"9\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"4\" exit=\"9\" gauge=\"NORMAL\" />\n"
		+ "	  <Track enter=\"6\" exit=\"9\" gauge=\"TUNNEL\" />\n"
		+ "	  <RevenueCenter id=\"-1\" location=\"9\" name=\"Hamburg\" number=\"2\" type=\"Double City\">\n"
		+ "	     <Revenue location=\"14\" phase=\"0\" value=\"60\" />\n"
		+ "	  </RevenueCenter>\n"
		+ "	  <RevenueCenter id=\"-1\" location=\"6\" name=\"Hamburg\" number=\"1\" type=\"Single City\">\n"
		+ "	     <Revenue location=\"14\" phase=\"0\" value=\"60\" />\n"
		+ "   </RevenueCenter>\n"
		+ "</Tile>",
		
			// Tile 9 - Yellow # 57
		 "		<Tile number=\"57\" type=\"Yellow\">\n"
		 + "		<Track enter=\"2\" exit=\"50\" gauge=\"NORMAL\" />\n"
		 + "		<Track enter=\"5\" exit=\"50\" gauge=\"NORMAL\" />\n"
		 + "		<RevenueCenter id=\"-1\" location=\"50\" name=\"\" number=\"1\"\n"
		 + "			type=\"Single City\">\n"
		 + "			<Revenue location=\"13\" phase=\"0\" value=\"20\" />\n"
		 + "		</RevenueCenter>\n"
		 + "	</Tile>\n"

	};
	String testUpgrades [] = {
		  " <Upgrade toNumber=\"120\" rotations=\"0\">\n "
		+ "		<RevenueCenter from=\"7\" to=\"13\" />\n "
		+ "		<RevenueCenter from=\"10\" to=\"17\" />\n "
		+ "	</Upgrade>",
		  "	<Upgrade toNumber=\"63\" rotations=\"0\">\n "
		+ "		<RevenueCenter from=\"50\" to=\"50\" />\n "
		+ "	</Upgrade>"
		+ "	<Upgrade toNumber=\"125\" rotations=\"0,1,2,3,4,5\">\n "
		+ "		<BaseTileName value=\"L\" />\n "
		+ "		<RevenueCenter from=\"50\" to=\"50\" />\n "
		+ "</Upgrade>",
		 " <Upgrade toNumber=\"122\" rotations=\"0\">\n "
		+ "		<RevenueCenter from=\"13\" to=\"13\" />\n "
		+ "		<RevenueCenter from=\"17\" to=\"17\" />\n "
		+ " </Upgrade>"
	};

	public TilesTestFactory () {
		this (MapTestFactory.NO_MAP_TEST_FACTORY);
	}
	
	public TilesTestFactory (MapTestFactory aMapTestFactory) {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
		if (aMapTestFactory == MapTestFactory.NO_MAP_TEST_FACTORY) {
			mapTestFactory = new MapTestFactory ();
		} else {
			mapTestFactory = aMapTestFactory;
		}
	}

	public Upgrade buildUpgrade (int aTileIndex) {
		Upgrade tUpgrade;
		String tUpgradeXML;
		
		tUpgradeXML = testUpgrades [aTileIndex];
		tUpgrade = constructUpgrade (tUpgradeXML);
		
		return tUpgrade;
	}
	
	public Tile buildTile (int aTileIndex) {
		Tile tTile;
		String tTileXML;
		
		if ((aTileIndex >= 0) && (aTileIndex < testTiles.length)) {
			tTileXML = testTiles [aTileIndex];
			tTile = constructTile (tTileXML);
		} else {
			tTile = Tile.NO_TILE;
		}
		
		return tTile;
	}

	private Upgrade constructUpgrade (String aUpgradeXML) {
		XMLNode tUpgradeXMLNode;
		Upgrade tUpgrade;
		
		tUpgrade = Upgrade.NO_UPGRADE;
		tUpgradeXMLNode = utilitiesTestFactory.buildXMLNode (aUpgradeXML);
		if (tUpgradeXMLNode != XMLNode.NO_NODE) {
			tUpgrade = new Upgrade (tUpgradeXMLNode);
		}
		
		return tUpgrade;
	}
	
	private Tile constructTile (String aTileTextXML) {
		XMLNode tTileXMLNode;
		Tile tTile;

		tTile = Tile.NO_TILE;
		tTileXMLNode = utilitiesTestFactory.buildXMLNode (aTileTextXML);
		if (tTileXMLNode != XMLNode.NO_NODE) {
			tTile = new Tile (tTileXMLNode);
		}

		return tTile;
	}
	
	public Tile buildTileMock (int aNumber) {
		Tile mTile;
		
		mTile = Mockito.mock (Tile.class);
		Mockito.when (mTile.getNumber ()).thenReturn (aNumber);
		
		return mTile;
	}
	
	public TileType buildTileTypeMock (int tTileTypeInt) {
		TileType mTileType;
		
		mTileType = Mockito.mock (TileType.class);
		Mockito.when (mTileType.getType ()).thenReturn (tTileTypeInt);
		
		return mTileType;
	}
	
	public TileName buildTileNameMock (boolean aIsOOTile, boolean aIsNYTile, boolean aIsXXTile, boolean aIsHHTile) {
		TileName mTileName;
		
		mTileName = Mockito.mock (TileName.class);
		Mockito.when (mTileName.isOOTile ()).thenReturn (aIsOOTile);
		Mockito.when (mTileName.isNYTile ()).thenReturn (aIsNYTile);
		Mockito.when (mTileName.isXXTile ()).thenReturn (aIsXXTile);
		Mockito.when (mTileName.isHHTile ()).thenReturn (aIsHHTile);
		
		return mTileName;
	}

	public void setMockCanAllTracksExit (Tile mTile, MapCell aMapCell, int aTileOrient, boolean aCanExit) {
		
		if (mTile != Tile.NO_TILE) {
			Mockito.when (mTile.canAllTracksExit (aMapCell, aTileOrient)).thenReturn (aCanExit);
		}
	}

	public Feature buildFeature () {
		Feature tFeature;
		
		tFeature = new Feature ();
		
		return tFeature;
	}
	
	public Feature buildFeature (int aLocation ) {
		Feature tFeature;
		
		tFeature = new Feature (aLocation);
		
		return tFeature;
	}
	
	public Feature buildFeature (Location aLocation) {
		Feature tFeature;
		
		tFeature = new Feature (aLocation);
		
		return tFeature;
	}
	
	public Feature buildFeatureMock () {
		Feature mFeature = Mockito.mock (Feature.class);

		Mockito.when (mFeature.getLocation ()).thenReturn (Location.NO_LOC);

		return mFeature;
	}
	
	public Feature buildFeatureMock (int aLocation) {
		Feature mFeature = Mockito.mock (Feature.class);
		Location tLocation;
		
		tLocation = mapTestFactory.buildLocation (aLocation);
		Mockito.when (mFeature.getLocation ()).thenReturn (tLocation);

		return mFeature;
	}
	
	public Feature buildFeatureMock (Location aLocation) {
		Feature mFeature = Mockito.mock (Feature.class);

		Mockito.when (mFeature.getLocation ()).thenReturn (aLocation);

		return mFeature;
	}
	

	public Feature2 buildFeature2 () {
		Feature2 tFeature2;
		
		tFeature2 = new Feature2 ();
		
		return tFeature2;
	}
	
	public Feature2 buildFeature2 (int aLocation1, int aLocation2 ) {
		Feature2 tFeature2;
		
		tFeature2 = new Feature2 (aLocation1, aLocation2);
		
		return tFeature2;
	}
	
	public Feature2 buildFeature2 (Location aLocation1, Location aLocation2) {
		Feature2 tFeature2;
		
		tFeature2 = new Feature2 (aLocation1, aLocation2);
		
		return tFeature2;
	}
	
	public Feature2 buildFeature2Mock () {
		Feature2 mFeature2 = Mockito.mock (Feature2.class);

		Mockito.when (mFeature2.getLocation ()).thenReturn (Location.NO_LOC);

		return mFeature2;
	}
	
	public Feature2 buildFeature2Mock (int aLocation1, int aLocation2) {
		Feature2 mFeature2 = Mockito.mock (Feature2.class);
		Location tLocation1;
		Location tLocation2;
		
		tLocation1 = mapTestFactory.buildLocation (aLocation1);
		tLocation2 = mapTestFactory.buildLocation (aLocation2);
		Mockito.when (mFeature2.getLocation ()).thenReturn (tLocation1);
		Mockito.when (mFeature2.getLocation2 ()).thenReturn (tLocation2);

		return mFeature2;
	}
	
	public Feature2 buildFeature2Mock (Location aLocation1, Location aLocation2) {
		Feature2 mFeature2 = Mockito.mock (Feature2.class);

		Mockito.when (mFeature2.getLocation ()).thenReturn (aLocation1);
		Mockito.when (mFeature2.getLocation2 ()).thenReturn (aLocation2);

		return mFeature2;
	}
	
	public TileSet buildTileSet (GameManager aGameManager) {
		TileSet tTileSet;
		TileTrayFrame tTileTrayFrame;
		
		tTileTrayFrame = gameTestFactory.buildTileTrayFrame (aGameManager);
		tTileSet = new TileSet (tTileTrayFrame);
		
		return tTileSet;
	}
}
