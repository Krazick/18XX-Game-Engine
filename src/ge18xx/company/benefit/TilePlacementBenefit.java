package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TilePlacementBenefit extends MapBenefit {
	boolean extraTilePlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");

	public TilePlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTilePlacement;
		
		tExtraTilePlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTilePlacement (tExtraTilePlacement);
	}
	
	private void setExtraTilePlacement (boolean aExtraTilePlacement) {
		extraTilePlacement = aExtraTilePlacement;
	}
	
	@Override
	public String getNewButtonLabel (PrivateCompany aPrivateCompany) {
		String tNewButtonText;
		
		tNewButtonText = "Place Tile on " + aPrivateCompany.getAbbrev () + " Home";
		
		return tNewButtonText;
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		String tOwningCorpAbbrev;
		JButton tPlaceTileButton;
		
		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure (aPrivateCompany)) {
			tOwningCorpAbbrev = aPrivateCompany.getOwnerName ();
			System.out.println ("Should Configure Buttons for " + tOwningCorpAbbrev + 
						" for Tile Placement with Private " + aPrivateCompany.getAbbrev ());
			tPlaceTileButton = new JButton (getNewButtonLabel (aPrivateCompany));
			setButton (tPlaceTileButton);
			setButtonPanel (aButtonRow);
			tPlaceTileButton.setActionCommand (CorporationFrame.PLACE_TILE_PRIVATE);
			tPlaceTileButton.addActionListener (this);
			aButtonRow.add (tPlaceTileButton);
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
		Corporation tOwningCompany;
		String tOwningCorpAbbrev;
		HexMap tMap;
		MapCell tMapCell;
		
		tOwningCompany = (Corporation) privateCompany.getOwner ();
		tOwningCorpAbbrev = privateCompany.getOwnerName ();
		System.out.println ("Place a Tile on " + getMapCellID () + 
				" for " + tOwningCorpAbbrev +
				" using Private " + privateCompany.getAbbrev () + " Benefit.");
		
		capturePreviousBenefitInUse (tOwningCompany, this);
		
		tOwningCompany.handlePlaceTile ();
		tMap = getMap ();
		tMap.clearAllSelected ();
		tMapCell = tMap.getMapCellForID (mapCellID);
		if (tMapCell == HexMap.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			System.out.println ("Did find Map Cell with ID " + mapCellID);
			tMap.toggleSelectedMapCell (tMapCell);
		}
	}
	
	public void resetBenefitInUse () {
		Corporation tOwningCompany;
		
		if (privateCompany != CorporationList.NO_PRIVATE_COMPANY) {
			tOwningCompany = (Corporation) privateCompany.getOwner ();
			tOwningCompany.setBenefitInUse (previousBenefitInUse);
		}
	}
	
	public void abortUse () {
		resetBenefitInUse ();
	}
	
	public void completeBenefitUse () {
		setUsed (true);
		removeButton ();
		resetBenefitInUse ();
		if (closeOnUse) {
			System.out.println ("Need to close the Private Company " + privateCompany.getAbbrev ());
		} else {
			System.out.println ("Private Benefit Used, but don't need to Close");
		}
	}
	
	// If this is an Extra Tile Placement Benefit, we DO NOT want to Change the State
	public boolean changeState () {
		return ! extraTilePlacement;
	}

}
