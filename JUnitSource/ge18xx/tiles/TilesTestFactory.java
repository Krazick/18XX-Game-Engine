package ge18xx.tiles;

import org.mockito.Mockito;

import ge18xx.game.GameTestFactory;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class TilesTestFactory {
	GameTestFactory gameTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	MapTestFactory mapTestFactory;

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

	public Tile buildTile () {
		Tile tTile;
		String tTileXML = "	<Tile number=\"14\" type=\"Green\">\n"
				+ "		<Track enter=\"0\" exit=\"50\" gauge=\"NORMAL\" />\n"
				+ "		<Track enter=\"1\" exit=\"50\" gauge=\"NORMAL\" />\n"
				+ "		<Track enter=\"3\" exit=\"50\" gauge=\"NORMAL\" />\n"
				+ "		<Track enter=\"4\" exit=\"50\" gauge=\"NORMAL\" />\n"
				+ "		<RevenueCenter id=\"-1\" location=\"50\" name=\"\" number=\"2\"\n"
				+ "			type=\"Double City\">\n"
				+ "			<Revenue location=\"42\" phase=\"0\" value=\"30\" />\n" + "		</RevenueCenter>\n"
				+ "	</Tile>\n" + "";

		tTile = constructTile (tTileXML);

		return tTile;
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
	
	public void setMockCanAllTracksExit (Tile mTile, MapCell aMapCell, int aTileOrient, boolean aCanExit) {
		
//		mTile.canAllTracksExit (mapCell, aTileOrient)
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
}
