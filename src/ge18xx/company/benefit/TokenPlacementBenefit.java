package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TokenPlacementBenefit extends MapBenefit {
	boolean extraTokenPlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");

	public TokenPlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTokenPlacement;
		
		tExtraTokenPlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTokenPlacement (tExtraTokenPlacement);
	}
	
	private void setExtraTokenPlacement (boolean aExtraTokenPlacement) {
		extraTokenPlacement = aExtraTokenPlacement;
	}
	
	@Override
	public String getNewButtonLabel (PrivateCompany aPrivateCompany) {
		String tNewButtonText;
		
		tNewButtonText = "Place Token on " + aPrivateCompany.getAbbrev () + " Home";
		
		return tNewButtonText;
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlaceTokenButton;
		
		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure (aPrivateCompany)) {
			System.out.println ("Should Configure for Token Placement");
			tPlaceTokenButton = new JButton (getNewButtonLabel (aPrivateCompany));
			setButton (tPlaceTokenButton);
			tPlaceTokenButton.setActionCommand (CorporationFrame.PLACE_TOKEN_PRIVATE);
			tPlaceTokenButton.addActionListener (this);
			aButtonRow.add (tPlaceTokenButton);
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TOKEN_PRIVATE.equals (tActionCommand)) {
			handlePlaceToken  ();
		}
	}

	private void handlePlaceToken () {
		Corporation tOwningCompany;
		HexMap tMap;
		MapCell tMapCell;
		Tile tTile;
		
		tOwningCompany = (Corporation) privateCompany.getOwner ();
		System.out.println ("Place a Token on " + getMapCellID () + 
				" for " + tOwningCompany.getAbbrev () +				
				" using Private " + privateCompany.getAbbrev () + " Benefit");
		tMap = getMap ();
		tMap.clearAllSelected ();
		tMapCell = tMap.getMapCellForID (mapCellID);
		if (tMapCell == HexMap.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			System.out.println ("Did find Map Cell with ID " + mapCellID);
			if (tMapCell.isTileOnCell ()) {
				tTile = tMapCell.getTile ();
				System.out.println ("Found Tile # " + tTile.getNumber () + " on the MapCell");
				if (tTile.cityOnTile ()) {
					System.out.println ("City is on the Tile");
					
				}
				tMap.toggleSelectedMapCell (tMapCell);
			} else {
				System.err.println ("No Tile found on MapCell");
			}
		}
		
	}
}
