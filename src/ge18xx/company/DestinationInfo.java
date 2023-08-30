package ge18xx.company;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class DestinationInfo {
	public static final DestinationInfo NO_DESTINATION_INFO = null;
	static final AttributeName AN_DESTINATION = new AttributeName ("destination");
	static final AttributeName AN_REACHED = new AttributeName ("reached");
	static final AttributeName AN_DESTINATION_LOCATION = new AttributeName ("destinationLocation");
	static final AttributeName AN_CAPITALIZATION_LEVEL = new AttributeName ("capitalizationLevel");
	static final AttributeName AN_ESCROW = new AttributeName ("escrow");
	// TODO:
	// Should take the location, and MapCell and make a sub-item "Destination" that is an array.
	// This will allow for 1853 to support multiple Destinations (setup in the initial contract
	// that can be used to determine if -ALL- Destinations have been reached, the escrow is then released.
	// 1856 will use same destination, and the variable escrow.
	Location location;
	MapCell mapCell;
	String label;
	int capitalizationLevel;
	int escrowForPayment;
	boolean reached;

	public DestinationInfo (XMLNode aChildNode) {
		loadStatus (aChildNode);
	}

	public void loadStatus (XMLNode aXMLNode) {
		Location tLocation;
		int tCapitalizationLevel;
		int tDestinationLocation;
		String tDestinationLabel;
		boolean tReached;

		tCapitalizationLevel = aXMLNode.getThisIntAttribute (AN_CAPITALIZATION_LEVEL);
		setCapitalizationLevel (tCapitalizationLevel);
		tDestinationLocation = aXMLNode.getThisIntAttribute (AN_DESTINATION_LOCATION, Location.NO_LOCATION);
		if (tDestinationLocation == Location.NO_LOCATION) {
			setLocation (Location.NO_LOC);
			setReached (true);
			setLabel ("NONE");
		} else {
			tLocation = new Location (tDestinationLocation);
			setLocation (tLocation);
			tReached = aXMLNode.getThisBooleanAttribute (AN_REACHED);
			setReached (tReached);
			tDestinationLabel = aXMLNode.getThisAttribute (AN_DESTINATION);
			setLabel (tDestinationLabel);
		}

	}
	
	public boolean hasDestination () {
		return (location != Location.NO_LOC);
	}
	
	public void setReached (boolean aReached) {
		reached = aReached;
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
	
	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public void setLabel (String aLabel) {
		label = aLabel;
	}
	
	public void setCapitalizationLevel (int aCapitalizationLevel) {
		capitalizationLevel = aCapitalizationLevel;
	}
	
	public void setEscrowForPayment (int aEscrowForPayment) {
		escrowForPayment = aEscrowForPayment;
	}
	
	public boolean hasReached () {
		return reached;
	}
	
	public Location getLocation () {
		return location;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public String getMapCellID () {
		return mapCell.getCellID ();
	}
	
	public String getLabel () {
		return label;
	}
	
	public int getCapitalizationLevel () {
		return capitalizationLevel;
	}
	
	public int getEscrowForPayment () {
		return escrowForPayment;
	}
	
	public int getLocationInt () {
		if (location == Location.NO_DESTINATION_LOCATION) {
			return Location.NO_LOCATION;
		} else {
			return location.getLocation ();
		}
	}

	public int calculateEscrowToRelease (ShareCompany aShareCompany) {
		int tEscrowReleased;
		int tSharesSold;
		int tParPrice;
		
		tEscrowReleased = 0;
		if (!reached) {
			tSharesSold = aShareCompany.getSharesOwned ();
			tParPrice = aShareCompany.getParPrice ();
			if (tSharesSold > 5) {
				tEscrowReleased = (tSharesSold - 5) * tParPrice;
			}
		}
		
		return tEscrowReleased;
	}

	public void getDestinationInfo (XMLElement aXMLCorporationState) {
		aXMLCorporationState.setAttribute (AN_CAPITALIZATION_LEVEL, getCapitalizationLevel ());
		if (location != Location.NO_LOC) {
			aXMLCorporationState.setAttribute (AN_DESTINATION, getLabel ());
			aXMLCorporationState.setAttribute (AN_DESTINATION_LOCATION, getLocationInt ());
			aXMLCorporationState.setAttribute (AN_REACHED, hasReached ());
			aXMLCorporationState.setAttribute (AN_ESCROW, getEscrowForPayment ());
		}
	}
}
