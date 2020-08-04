package ge18xx.game;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

public class File18XX {
	public static final ElementName EN_FILES = new ElementName ("Files");
	public static final ElementName EN_FILE = new ElementName ("File");
	public static final AttributeName AN_CITIES_FILE = new AttributeName ("cities");
	public static final AttributeName AN_COMPANIES_FILE = new AttributeName ("companies");
	public static final AttributeName AN_MAP_FILE = new AttributeName ("map");
	public static final AttributeName AN_MARKET_FILE = new AttributeName ("market");
	public static final AttributeName AN_TILESET_FILE = new AttributeName ("tileSet");
	static final AttributeName AN_TYPE = new AttributeName ("type");
	static final AttributeName AN_NAME = new AttributeName ("name");
	static final String NO_NAME = "<NO NAME>";
	static final String NO_TYPE = "<NO TYPE>";
	String name;
	String type;
	
	public File18XX () {
		setValue (NO_NAME, NO_TYPE);
	}
	
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
