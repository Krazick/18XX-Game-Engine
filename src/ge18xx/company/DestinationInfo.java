package ge18xx.company;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class DestinationInfo {
	static final AttributeName AN_DESTINATION = new AttributeName ("destination");
	static final AttributeName AN_DESTINATION_LOCATION = new AttributeName ("destinationLocation");
	static final AttributeName AN_CAPITALIZATION_LEVEL = new AttributeName ("capitalizationLevel");
	Location location;
	MapCell mapCell;
	String label;
	int capitalizationLevel;
	int escrowForPayment;

	public DestinationInfo (XMLNode aChildNode) {
		Location tLocation;
		int tCapitalizationLevel;
		int tDestinationLocation;
		String tDestinationLabel;
		
		tDestinationLabel = aChildNode.getThisAttribute (AN_DESTINATION);
		setLabel (tDestinationLabel);
		tDestinationLocation = aChildNode.getThisIntAttribute (AN_DESTINATION_LOCATION, Location.NO_LOCATION);
		tLocation = new Location (tDestinationLocation);
		setLocation (tLocation);
		tCapitalizationLevel = aChildNode.getThisIntAttribute (AN_CAPITALIZATION_LEVEL);
		setCapitalizationLevel (tCapitalizationLevel);
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
	
	public Location getLocation () {
		return location;
	}
	
	public MapCell getMapCell () {
		return mapCell;
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

}
