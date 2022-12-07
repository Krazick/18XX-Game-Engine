package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class RemoveHomeEffect extends Effect {
	public final static String NAME = "Remove Home";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_HOME_CITY1 = new AttributeName ("homeCity1ID");
	final static AttributeName AN_HOME_CITY2 = new AttributeName ("homeCity2ID");
	final static AttributeName AN_HOME_LOCATION1 = new AttributeName ("homeLocation1");
	final static AttributeName AN_HOME_LOCATION2 = new AttributeName ("homeLocation2");
	String companyAbbrev;
	String homeCity1ID;
	String homeCity2ID;
	int homeLocation1;
	int homeLocation2;

	public RemoveHomeEffect () {
		this (NAME);
	}

	public RemoveHomeEffect (String aName) {
		super (aName);
	}

	public RemoveHomeEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public RemoveHomeEffect (ActorI aActor, String aCorporationAbbrev, MapCell aHomeCity1, MapCell aHomeCity2, 
			Location aHomeLocation1, Location aHomeLocation2) {
		this (NAME, aActor, aCorporationAbbrev, aHomeCity1, aHomeCity2, aHomeLocation1, aHomeLocation2);
	}
	
	public RemoveHomeEffect (String aName, ActorI aActor, String aCorporationAbbrev, MapCell aHomeCity1, MapCell aHomeCity2, 
							Location aHomeLocation1, Location aHomeLocation2) {
		super (aName, aActor);
		setCompanyAbbrev (aCorporationAbbrev);
		if (aHomeCity1 != MapCell.NO_MAP_CELL) {
			setHomeCity1ID (aHomeCity1.getCellID ());
			setHomeLocation1 (aHomeLocation1.getLocation ());
			setHomeCity2ID (MapCell.NO_ID);
			setHomeLocation2 (Location.NO_LOCATION);
		} else if (aHomeCity2 != MapCell.NO_MAP_CELL) {
			setHomeCity1ID (MapCell.NO_ID);
			setHomeLocation1 (Location.NO_LOCATION);
			setHomeCity2ID (aHomeCity2.getCellID ());
			setHomeLocation2 (aHomeLocation2.getLocation ());
		}
	}

	public RemoveHomeEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		String tCompanyAbbrev;
		String tHomeCity1ID;
		String tHomeCity2ID;
		int tHomeLocation1;
		int tHomeLocation2;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tHomeCity1ID = aEffectNode.getThisAttribute (AN_HOME_CITY1);
		tHomeLocation1 = aEffectNode.getThisIntAttribute (AN_HOME_LOCATION1);
		tHomeCity2ID = aEffectNode.getThisAttribute (AN_HOME_CITY2);
		tHomeLocation2 = aEffectNode.getThisIntAttribute (AN_HOME_LOCATION2);

		setCompanyAbbrev (tCompanyAbbrev);
		setHomeCity1ID (tHomeCity1ID);
		setHomeCity2ID (tHomeCity2ID);
		setHomeLocation1 (tHomeLocation1);
		setHomeLocation2 (tHomeLocation2);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tHomeCityID1;
		String tHomeCityID2;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, getCompanyAbbrev ());
		tHomeCityID1 = getHomeCity1ID ();
		tHomeCityID2 = getHomeCity1ID ();
		if (tHomeCityID2.equals (MapCell.NO_ID)) {
			tEffectElement.setAttribute (AN_HOME_CITY1, getHomeCity1ID ());
			tEffectElement.setAttribute (AN_HOME_LOCATION1, getHomeLocation1 ());
		} else if (tHomeCityID1.equals (MapCell.NO_ID)) {
			tEffectElement.setAttribute (AN_HOME_CITY2, getHomeCity2ID ());
			tEffectElement.setAttribute (AN_HOME_LOCATION2, getHomeLocation2 ());
		}
		
		return tEffectElement;
	}

	private String getCompanyAbbrev () {
		return companyAbbrev;
	}
	
	private String getHomeCity1ID () {
		return homeCity1ID;
	}

	private String getHomeCity2ID () {
		return homeCity2ID;
	}

	private int getHomeLocation1 () {
		return homeLocation1;
	}

	private int getHomeLocation2 () {
		return homeLocation2;
	}

	private void setCompanyAbbrev (String aCorporationAbbrev) {
		companyAbbrev = aCorporationAbbrev;
	}

	private void setHomeCity1ID (String aHomeCity1ID) {
		homeCity1ID = aHomeCity1ID;
	}
	
	private void setHomeCity2ID (String aHomeCity2ID) {
		homeCity2ID = aHomeCity2ID;
	}
	
	private void setHomeLocation1 (int aHomeLocation1) {
		homeLocation1 = aHomeLocation1;
	}
	
	private void setHomeLocation2 (int aHomeLocation2) {
		homeLocation2 = aHomeLocation2;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		String tHomeCityID1;
		String tHomeCityID2;
		
		tReport = REPORT_PREFIX + getName ();
		tHomeCityID1 = getHomeCity1ID ();
		tHomeCityID2 = getHomeCity1ID ();
		
		if (tHomeCityID1.equals (MapCell.NO_ID)) {
			tReport += " at " + homeCity2ID + " Location " + homeLocation2;
		} else if (tHomeCityID2.equals (MapCell.NO_ID)) {
			tReport += " at " + homeCity1ID + " Location " + homeLocation1;
		}
		tReport += " for " + getActorName () + ".";
		
		return tReport;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied = false;

		System.out.println ("Need to Apply " + NAME);
//		tCertificate = aRoundManager.getCertificate (getCompanyAbbrev (), getPercentage (), isPresident ());
//		if (tCertificate != Certificate.NO_CERTIFICATE) {
//			tCertificate.removeAllBids ();
//			tEffectApplied = true;
//		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}

}
