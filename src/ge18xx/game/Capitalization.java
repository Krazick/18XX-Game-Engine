package ge18xx.game;

import java.util.HashMap;

import org.w3c.dom.NodeList;

import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLNode;

public class Capitalization {
	public static final String INCREMENTAL_5 = "Incremental_5";
	public static final String INCREMENTAL_10 = "Incremental_10";
	public static final String FULL_GAME_CAPITALIZATION = "FULL";
	public static final int FULL_CAPITALIZATION = 10;
	public static final int INCREMENTAL_5_MAX = 5;
	public static final int INCREMENTAL_10_MAX = 10;
	public static final ElementName EN_CAPITALIZATIONS = new ElementName ("Capitalizations");
	public static final ElementName EN_CAPITALIZATION = new ElementName ("Capitalization");
	public static final AttributeName AN_AVAILABLE_TRAIN = new AttributeName ("availableTrain");
	public static final AttributeName AN_LEVEL = new AttributeName ("level");
	HashMap<String, String> levels;
	
	public Capitalization (XMLNode aXMLNode) {
		String tAvailableTrain;
		String tLevel;
		NodeList tChildren;
		XMLNode tChildNode;
		int tChildrenCount;
		int tIndex;
		String tChildName;
		
		levels = new HashMap<String, String> ();
		tChildren = aXMLNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (EN_CAPITALIZATION.equals (tChildName)) {
				tAvailableTrain = tChildNode.getThisAttribute (AN_AVAILABLE_TRAIN);
				tLevel = tChildNode.getThisAttribute (AN_LEVEL);
				levels.put (tAvailableTrain, tLevel);
			}
		}
	}

	public void printInfo (String aGameName) {
		System.out.println ("Capitalization Info for " + aGameName + ":");		// PRINTLOG
		System.out.println (levels.toString ());
	}
	
	public int getCapitalizationLevel (int aSharesSold, String aNextTrainName) {
		int tCapitalizationLevel;
		String tLevel;
		
		tLevel = levels.get (aNextTrainName);

		if (tLevel.equals (FULL_GAME_CAPITALIZATION)) {
			tCapitalizationLevel = FULL_CAPITALIZATION;
		} else if (tLevel.equals (INCREMENTAL_5)) {
			tCapitalizationLevel = Math.min (INCREMENTAL_5_MAX, aSharesSold);
		} else if (tLevel.equals (INCREMENTAL_10)) {
			tCapitalizationLevel = Math.min (INCREMENTAL_10_MAX, aSharesSold);
		} else {
			tCapitalizationLevel = FULL_CAPITALIZATION;
		}

		return tCapitalizationLevel;
	}
}
