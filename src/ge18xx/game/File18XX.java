package ge18xx.game;

import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLNode;

public class File18XX {
	public static final String CITIES_TYPE = "cities";
	public static final String COMPANIES_TYPE = "companies";
	public static final String MAP_TYPE = "map";
	public static final String MARKET_TYPE = "market";
	public static final String TILE_SET_TYPE = "tileSet";
	public static final ElementName EN_FILES = new ElementName ("Files");
	public static final ElementName EN_FILE = new ElementName ("File");
	public static final AttributeName AN_CITIES_FILE = new AttributeName (CITIES_TYPE);
	public static final AttributeName AN_COMPANIES_FILE = new AttributeName (COMPANIES_TYPE);
	public static final AttributeName AN_MAP_FILE = new AttributeName (MAP_TYPE);
	public static final AttributeName AN_MARKET_FILE = new AttributeName (MARKET_TYPE);
	public static final AttributeName AN_TILESET_FILE = new AttributeName (TILE_SET_TYPE);
	static final AttributeName AN_TYPE = new AttributeName ("type");
	static final AttributeName AN_NAME = new AttributeName ("name");
	static final String NO_NAME = "<NO NAME>";
	static final String NO_TYPE = "<NO TYPE>";
	String name;
	String type;

	public File18XX (XMLNode aCellNode) {
		String tName;
		String tType;

		tType = aCellNode.getThisAttribute (AN_TYPE);
		tName = aCellNode.getThisAttribute (AN_NAME);
		setValue (tName, tType);
	}

	public String getName () {
		return name;
	}

	public String getType () {
		return type;
	}

	private void setValue (String aName, String aType) {
		name = aName;
		type = aType;
	}
}
