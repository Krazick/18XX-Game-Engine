package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import geUtilities.ElementName;
import geUtilities.ParsingRoutineI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;
import geUtilities.XMLNodeList;

public class AllPercentBought {
	public final static ElementName EN_ALL_PERCENT_BOUGHT = new ElementName ("AllPercentBought");
	List<PercentBought> allPercentBought;
	
	public AllPercentBought () {
		allPercentBought = new LinkedList<PercentBought> ();
	}

	public void addPercentBought (String aAbbrev, int aPercentBought) {
		if (allPercentBought.isEmpty ()) {
			addNewPercentBought (aAbbrev, aPercentBought);
		} else {
			if (containsAbbrev (aAbbrev)) {
				addPercent (aAbbrev, aPercentBought);
			} else {
				addNewPercentBought (aAbbrev, aPercentBought);
			}
		}
		removeIfEmpty (aAbbrev);
	}
	
	private void removeIfEmpty (String aAbbrev) {
		if (getPercentFor (aAbbrev) == 0) {
			removePercentFor (aAbbrev);
		}
	}
	
	private void removePercentFor (String aAbbrev) {
		String tAbbrev;
		
		tAbbrev = aAbbrev.trim ();
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (tAbbrev)) {
				allPercentBought.remove (tPercentBought);
			}
		}

	}
	
	public void addPercent (String aAbbrev, int aPercentBought) {
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (aAbbrev)) {
				tPercentBought.addPercent (aPercentBought);
			}
		}
	}
	
	public void addNewPercentBought (String aAbbrev, int aPercentBought) {
		PercentBought tPercentBought;

		tPercentBought = new PercentBought (aAbbrev, aPercentBought);
		allPercentBought.add (tPercentBought);
	}
	
	public boolean containsAbbrev (String aAbbrev) {
		boolean tContainsAbbrev;
		
		tContainsAbbrev = false;
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (aAbbrev)) {
				tContainsAbbrev = true;
			}
		}
		
		return tContainsAbbrev;
	}
	
	public int getPercentFor (String aAbbrev) {
		int tFoundPercent;
		String tAbbrev;
		
		tFoundPercent = 0;
		tAbbrev = aAbbrev.trim ();
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (tAbbrev)) {
				tFoundPercent = tPercentBought.getPercent ();
			}
		}
		
		return tFoundPercent;
	}
	
	public void clear () {
		allPercentBought.clear ();
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLPercentBoughtElement;

		tXMLElement = aXMLDocument.createElement (EN_ALL_PERCENT_BOUGHT);
		for (PercentBought tPercentBought : allPercentBought) {
			tXMLPercentBoughtElement = tPercentBought.getElement (aXMLDocument);
			tXMLElement.appendChild (tXMLPercentBoughtElement);
		}

		return tXMLElement;
	}
	
	public void loadAllPercentBought (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (percentBoughtParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, PercentBought.EN_PERCENT_BOUGHT);
	}

	ParsingRoutineI percentBoughtParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			String tAbbrev;
			int tPercent;
			
			tAbbrev = aChildNode.getThisAttribute (PercentBought.AN_ABBREV);
			tPercent = aChildNode.getThisIntAttribute (PercentBought.AN_PERCENT);
			addPercentBought (tAbbrev, tPercent);
		}
	};
}
