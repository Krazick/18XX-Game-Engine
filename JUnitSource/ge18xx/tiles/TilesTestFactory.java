package ge18xx.tiles;

import ge18xx.game.GameTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class TilesTestFactory {
	GameTestFactory gameTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;

	public TilesTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();	
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
				+ "			<Revenue location=\"42\" phase=\"0\" value=\"30\" />\n"
				+ "		</RevenueCenter>\n"
				+ "	</Tile>\n"
				+ "";
		
		tTile = constructTile (tTileXML);
		
		return tTile;
	}
	
	private Tile constructTile (String aTileTextXML) {
		XMLNode tTileXMLNode;
		Tile tTile;
		
		tTile = Tile.NO_TILE;
		tTileXMLNode = utilitiesTestFactory.constructXMLNode (aTileTextXML);
		if (tTileXMLNode != XMLNode.NO_NODE) {
			tTile = new Tile (tTileXMLNode);
		}
		
		return tTile;
	}

}
