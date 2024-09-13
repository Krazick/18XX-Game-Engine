package ge18xx.player;

import ge18xx.round.action.SetPercentBoughtAction;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public class PercentBought {
	public static final ElementName EN_PERCENT_BOUGHT = new ElementName ("PercentBought");
	public static final AttributeName AN_ABBREV = new AttributeName ("abbrev");
	public static final AttributeName AN_PERCENT = new AttributeName ("percent");
	String abbrev;
	int percent;
	
	public PercentBought (String aAbbrev, int aPercent) {
		setAbbrev (aAbbrev);
		setPercent (aPercent);
	}
	
	void setAbbrev (String aAbbrev) {
		abbrev = aAbbrev;
	}
	
	void setPercent (int aPercent) {
		percent = aPercent;
	}
	
	String getAbbrev () {
		return abbrev;
	}
	
	int getPercent () {
		return percent;
	}
	
	void addPercent (int aPercent) {
		percent += aPercent;
	}
	
	boolean isAbbrev (String aAbbrev) {
		boolean tIsAbbrev;
		
		if (abbrev.equals (aAbbrev)) {
			tIsAbbrev = true;
		} else {
			tIsAbbrev = false;
		}
		
		return tIsAbbrev;
	}
	
	public void addSetPercentBoughtEffect (SetPercentBoughtAction aSetPercentBoughtAction, Player aPlayer, int aNewPercent) {
		aSetPercentBoughtAction.addSetPercentBoughtEffect (aPlayer, abbrev, percent, aNewPercent);
	}
	
	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_PERCENT_BOUGHT);
		tXMLElement.setAttribute (AN_ABBREV, abbrev);
		tXMLElement.setAttribute (AN_PERCENT, percent);

		return tXMLElement;
	}
}
