package ge18xx.game.variants;

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.train.TrainInfo;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ModifyTrainVEffect extends VariantEffect {
	public static final String NAME = "Modify Train";
	String attributeName;
	String value;
	String trainName;

	public ModifyTrainVEffect () {
		super ();
		setName (NAME);
	}

	public ModifyTrainVEffect (XMLNode aVariantEffectNode) {
		super (aVariantEffectNode);
		String tAttributeName;
		String tValue;
		String tTrainName;

		tTrainName = aVariantEffectNode.getThisAttribute (TrainInfo.AN_NAME);
		tAttributeName = aVariantEffectNode.getThisAttribute (AN_ATTRIBUTE_NAME);
		tValue = aVariantEffectNode.getThisAttribute (AN_VALUE);
		setAttributeName (tAttributeName);
		setValue (tValue);
		setTrainName (tTrainName);
		switch (attributeName) {
			case "onLast" :
				break;
			default:
				System.err.println ("Don't know what to do with " + attributeName);
		}
	}

	public void setAttributeName (String aAttributeName) {
		attributeName = aAttributeName;
	}

	public void setTrainName (String aTrainName) {
		trainName = aTrainName;
	}

	public void setValue (String aValue) {
		value = aValue;
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
		tXMLElement.setAttribute (AN_VALUE, value);
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
		TrainInfo tTrainInfo;
		int tTrainInfoCount;
		int tTrainInfoIndex;
		String tTrainName;
		int tValue;

		tGameInfo = aGameManager.getActiveGame ();
		tTrainInfoCount = tGameInfo.getTrainCount ();
		for (tTrainInfoIndex = 0; tTrainInfoIndex < tTrainInfoCount; tTrainInfoIndex++) {
			tTrainInfo = tGameInfo.getTrainInfo (tTrainInfoIndex);
			tTrainName = tTrainInfo.getName ();
			if (tTrainName.equals (trainName)) {
				switch (attributeName) {
					case "onLast":
						tValue = Integer.valueOf (value);
						tTrainInfo.setOnLastOrderAvailable (tValue);
						break;
					default:
						System.err.println ("Don't know what to do with " + attributeName);
				}
			}
		}
	}
}
