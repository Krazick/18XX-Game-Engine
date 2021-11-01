package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TilePlacementBenefit extends MapBenefit {
	boolean extraTilePlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");
	public final static String NAME = "Tile Placement";

	public TilePlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTilePlacement;
		
		tExtraTilePlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTilePlacement (tExtraTilePlacement);
		setName (NAME);
	}
	
	private void setExtraTilePlacement (boolean aExtraTilePlacement) {
		extraTilePlacement = aExtraTilePlacement;
	}
	
	@Override
	public String getName () {
		String tName;
		
		tName = super.getName ();
		if (extraTilePlacement) {
			tName = "Extra " + tName;
		}
		if (getCost () == 0) {
			tName = "Free " + tName;
		}
		
		return tName;
	}

	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;
		
		tNewButtonText = "Place Tile on " + privateCompany.getAbbrev () + " Home";
		
		return tNewButtonText;
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlaceTileButton;
		
		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (! hasButton ()) {
				tPlaceTileButton = new JButton (getNewButtonLabel ());
				setButton (tPlaceTileButton);
				setButtonPanel (aButtonRow);
				tPlaceTileButton.setActionCommand (CorporationFrame.PLACE_TILE_PRIVATE);
				tPlaceTileButton.addActionListener (this);
				aButtonRow.add (tPlaceTileButton);
			}
			updateButton ();
		}
	}
	
	@Override
	public void updateButton () {
		if (hasTile ()) {
			disableButton ();
			setToolTip ("MapCell already has Tile");
		} else if (isTileAvailable ()) {
			enableButton ();
			setToolTip ("");
		} else {
			disableButton ();
			setToolTip ("No Tile available to place on MapCell");
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TILE_PRIVATE.equals (tActionCommand)) {
			handlePlaceTile  ();
		}
	}
	
	private void handlePlaceTile () {
		HexMap tMap;
		MapCell tMapCell;
		Corporation tOwningCompany;
		
		tOwningCompany = (Corporation) privateCompany.getOwner ();		
		capturePreviousBenefitInUse (tOwningCompany, this);
		
		tOwningCompany.handlePlaceTile ();
		tMap = getMap ();
		tMap.clearAllSelected ();
		tMapCell = tMap.getMapCellForID (mapCellID);
		if (tMapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			System.out.println ("Did find Map Cell with ID " + mapCellID);
			tMap.toggleSelectedMapCell (tMapCell);
		}
	}
	
	@Override
	public boolean realBenefit () {
		return true;
	}
	
	@Override
	public void abortUse () {
		resetBenefitInUse ();
	}
	
	// If this is an Extra Tile Placement Benefit, we DO NOT want to Change the State
	@Override
	public boolean changeState () {
		return ! extraTilePlacement;
	}
}
