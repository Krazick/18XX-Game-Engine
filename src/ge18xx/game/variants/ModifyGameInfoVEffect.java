package ge18xx.game.variants;

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ModifyGameInfoVEffect extends VariantEffect {
	public static final String NAME = "Modify Game Info";
	String attributeName;

	public ModifyGameInfoVEffect () {
		super ();
		setName (NAME);
	}

	public ModifyGameInfoVEffect (XMLNode aVariantEffectNode) {
		super (aVariantEffectNode);
		String tAttributeName;

		tAttributeName = aVariantEffectNode.getThisAttribute (AN_ATTRIBUTE_NAME);
		setAttributeName (tAttributeName);
		switch (attributeName) {
			case "operateBeforeSale" :
				break;
			case "randomizeStartOrder" :
				break;
			default:
				System.err.println ("Don't know what to do with " + attributeName);
		}
	}

	public void setAttributeName (String aAttributeName) {
		attributeName = aAttributeName;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the CompanyID and the VariantEffect Class
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
		tXMLElement.setAttribute (AN_ATTRIBUTE_NAME, attributeName);
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
		GameInfo tGameInfo;

		tGameInfo = aGameManager.getActiveGame ();
		switch (attributeName) {
			case "operateBeforeSale":
				tGameInfo.setOperateBeforeSale (state);
				break;
			case "randomizeStartOrder":
				tGameInfo.setRandomizeStartOrder (state);
				break;
			default:
				System.err.println ("Don't know what to do with " + attributeName);
		}
	}

}
