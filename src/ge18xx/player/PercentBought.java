package ge18xx.player;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class PercentBought {
	public final static ElementName EN_PERCENT_BOUGHT = new ElementName ("PercentBought");
	public final static AttributeName AN_ABBREV = new AttributeName ("abbrev");
	public final static AttributeName AN_PERCENT = new AttributeName ("percent");
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
	
	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_PERCENT_BOUGHT);
		tXMLElement.setAttribute (AN_ABBREV, abbrev);
		tXMLElement.setAttribute (AN_PERCENT, percent);

		return tXMLElement;
	}
}
