package ge18xx.game.variants;

import ge18xx.game.GameManager;
import ge18xx.tiles.TileType;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class AddTilesVEffect extends VariantEffect {
	public static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	public static final AttributeName AN_TILE_NUMBER = new AttributeName ("tileNumber");
	public static final AttributeName AN_TILE_TYPE = new AttributeName ("tileType");
	public static final String NAME = "Add Tile";
	int quantity;
	int tileNumber;
	String tileType;

	public AddTilesVEffect () {
		setName (NAME);
	}

	public AddTilesVEffect (XMLNode aVariantEffectNode) {
		super (aVariantEffectNode);

		int tQuantity;
		int tTileNumber;
		String tTileType;

		tQuantity = aVariantEffectNode.getThisIntAttribute (AN_QUANTITY);
		tTileNumber = aVariantEffectNode.getThisIntAttribute (AN_TILE_NUMBER);
		tTileType = aVariantEffectNode.getThisAttribute (AN_TILE_TYPE);
		setQuantity (tQuantity);
		setTileNumber (tTileNumber);
		if (TileType.validName (tTileType)) {
			setTileType (tTileType);
		} else {
			setTileType ("INVALID TYPE");
		}
	}

	public int getQuantity () {
		return quantity;
	}

	public int getTileNumber () {
		return tileNumber;
	}

	public String getTileType () {
		return tileType;
	}

	public void setQuantity (int aQuantity) {
		quantity = aQuantity;
	}

	public void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}

	public void setTileType (String aTileType) {
		tileType = aTileType;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the PhaseName
	 *
	 * @param aXMLDocument The XMLDocumdnt to use to create the XMLElement
	 *
	 * @return the filled out XMLElement
	 *
	 */
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = super.getEffectElement (aXMLDocument);
		tXMLElement.setAttribute (AN_TILE_NUMBER, tileNumber);
		tXMLElement.setAttribute (AN_TILE_TYPE, tileType);

		if (quantity != NO_QUANTITY) {
			tXMLElement.setAttribute (AN_QUANTITY, quantity);
		}
		tXMLElement.setAttribute (AN_CLASS, getClass ().getName ());

		return tXMLElement;
	}

	/**
	 * Apply the Variant Effect using the Game Manager as needed.
	 *
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 *
	 */
	@Override
	public void applyVariantEffect (GameManager aGameManager) {
		aGameManager.loadATileFromASet (tileType, tileNumber, quantity);
	}
}
