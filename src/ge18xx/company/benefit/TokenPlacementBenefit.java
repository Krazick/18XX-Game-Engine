package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.center.City;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.TokenCompany;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TokenPlacementBenefit extends MapBenefit {
	boolean extraTokenPlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");
	public final static String NAME = "Token Placement";

	public TokenPlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTokenPlacement;
		
		tExtraTokenPlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTokenPlacement (tExtraTokenPlacement);
		setName (NAME);
	}
	
	private void setExtraTokenPlacement (boolean aExtraTokenPlacement) {
		extraTokenPlacement = aExtraTokenPlacement;
	}
	
	@Override
	public String getName () {
		String tName;
		
		tName = super.getName ();
		if (extraTokenPlacement) {
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
		
		tNewButtonText = "Place Token on " + privateCompany.getAbbrev () + " Home";
		
		return tNewButtonText;
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlaceTokenButton;
		
		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (! hasButton ()) {
				tPlaceTokenButton = new JButton (getNewButtonLabel ());
				setButton (tPlaceTokenButton);
				setButtonPanel (aButtonRow);
				tPlaceTokenButton.setActionCommand (CorporationFrame.PLACE_TOKEN_PRIVATE);
				tPlaceTokenButton.addActionListener (this);
				aButtonRow.add (tPlaceTokenButton);
			}
			updateButton ();
		}
	}
	
	private boolean hasTokens () {
		TokenCompany tOwningCompany;
		boolean tHasTokens = true;
		int tTokenCount;
		
		tOwningCompany = (TokenCompany) privateCompany.getOwner ();
		tTokenCount = tOwningCompany.getTokenCount ();
		if (tTokenCount == 0) {
			tHasTokens = false;
		}
		
		return tHasTokens;
	}
	
	@Override
	public void updateButton () {
		Corporation tOwningCompany;
		Benefit tBenefitInUse;
		String tBenefitInUseName;
		
		tOwningCompany = (Corporation) privateCompany.getOwner ();
		tBenefitInUse = tOwningCompany.getBenefitInUse ();
		tBenefitInUseName = tBenefitInUse.getName ();
		if ((tBenefitInUse.realBenefit ()) && (! NAME.equals(tBenefitInUseName))) {
			disableButton ();
			setToolTip ("Another Benefit is currently in Use");
		} else if (hasTile ()) {
			enableButton ();
			setToolTip ("");
		} else if (! hasTokens ()) {
			disableButton ();
			setToolTip ("Company has no Tokens, can't place Button");
		} else {
			disableButton ();
			setToolTip ("No Tile on the MapCell, can't place Button");
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
		HexMap tMap;
		MapCell tMapCell;
		Tile tTile;
		Corporation tOwningCompany;
		City tCity;
		
		tOwningCompany = (Corporation) privateCompany.getOwner ();
		capturePreviousBenefitInUse (tOwningCompany, this);

		tMap = getMap ();
		tMap.clearAllSelected ();
		tMapCell = tMap.getMapCellForID (mapCellID);
		if (tMapCell == HexMap.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			if (tMapCell.isTileOnCell ()) {
				tTile = tMapCell.getTile ();
				System.out.println ("Found Tile # " + tTile.getNumber () + " on the MapCell");
				if (tTile.cityOnTile ()) {
					System.out.println ("City is on the Tile");
					tCity = (City) tTile.getRevenueCenter (0);
					tMap.putMapTokenDown (tOwningCompany, tCity, tMapCell);
				}
				tMap.toggleSelectedMapCell (tMapCell);
			} else {
				System.err.println ("No Tile found on MapCell");
			}
		}
	}
	
	public boolean realBenefit () {
		return true;
	}
	
	public boolean changeState () {
		return true;
	}
}
