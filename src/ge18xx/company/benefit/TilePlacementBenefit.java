package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;
import swingTweaks.KButton;

public class TilePlacementBenefit extends MapBenefit {
	public static final AttributeName AN_EXTRA = new AttributeName ("extra");
	public static final String NAME = "Tile Placement";
	public static final TilePlacementBenefit NO_TILE_PLACEMENT_BENEFIT = (TilePlacementBenefit) NO_BENEFIT;
	boolean extraTilePlacement;

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

		tNewButtonText = "Put Tile on " + privateCompany.getAbbrev () + " Home (" + mapCellID + ")";

		return tNewButtonText;
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		KButton tPlaceTileButton;

		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (!hasButton ()) {
				tPlaceTileButton = new KButton (getNewButtonLabel ());
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
	protected void updateButton () {
		if (hasNonStartingTile ()) {
			hideButton ();
			setToolTip ("MapCell already has Tile");
		} else if (! isTileAvailable ()) {
			disableButton ();
			setToolTip ("No Tile available to place on MapCell");
		} else if (! operatingCompanyHasEnoughCash ()) {
			disableButton ();
			setToolTip ("Operating Train Company does not have enough cash to pay for Tile");
		} else {
			if (extraTilePlacement) {
				enableButton ();
				setToolTip ("Benefit allows an Extra Tile Placement");
			} if (! operatingCompanyCanLayTile ()) {
				disableButton ();
				setToolTip ("Operating Train Company has already laid or upgraded a Tile");
			} else {
				enableButton ();
				setToolTip ("Operating Train Company has enough cash to pay for Tile Placement");	
			}
		}
	}
	
	private boolean operatingCompanyHasEnoughCash () {
		boolean tOperatingCompanyHasEnoughCash;
		ShareCompany tShareCompany;
		
		tShareCompany = getOperatingCompany ();
		tOperatingCompanyHasEnoughCash = companyHasEnoughCash (tShareCompany);
		
		return tOperatingCompanyHasEnoughCash;
	}
	
	private boolean companyHasEnoughCash (ShareCompany aShareCompany) {
		boolean tOwnerHasEnoughCash;
		
		tOwnerHasEnoughCash = false;
		if (aShareCompany != ShareCompany.NO_SHARE_COMPANY) {
			if (aShareCompany.getTreasury () >= cost) {
				tOwnerHasEnoughCash = true;
			}
		}
		
		return tOwnerHasEnoughCash;
	}

	protected boolean operatingCompanyCanLayTile () {
		boolean tOperatingCompanyCanLayTile;
		ShareCompany tShareCompany;
		int tTileLaysAllowed;
		
		tOperatingCompanyCanLayTile = true;
		tShareCompany = getOperatingCompany ();
		tTileLaysAllowed = tShareCompany.getTileLaysAllowed ();
		if (tShareCompany.canLayTile (tTileLaysAllowed)) {
			
		} else if (tShareCompany.hasLaidTile ()) {
			tOperatingCompanyCanLayTile = false;
		}

		return tOperatingCompanyCanLayTile;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		MapCell tSelectedMapCell;
		
		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TILE_PRIVATE.equals (tActionCommand)) {
			tSelectedMapCell = MapCell.NO_MAP_CELL;
			handlePlaceTile (tSelectedMapCell);
		}
	}

	@Override
	public boolean isAExtraTilePlacement () {
		return extraTilePlacement;
	}

	private void handlePlaceTile (MapCell aMapCell) {
		HexMap tMap;
		MapCell tMapCell;
		Corporation tOwningCompany;

		tOwningCompany = getOperatingCompany ();
		capturePreviousBenefitInUse (tOwningCompany, this);

		tOwningCompany.handlePlaceTile (aMapCell);
		tMap = getMap ();
		tMap.clearAllSelected ();
		tMap.removeAllSMC ();
		tMapCell = getMapCell ();
		if (tMapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			tMap.addMapCellSMC (tMapCell);
			tMap.toggleSelectedMapCell (tMapCell);
		}
	}

	@Override
	public boolean isRealBenefit () {
		return true;
	}

	@Override
	public void abortUse () {
		Corporation tOwningCompany;

		tOwningCompany = getOwningCompany ();
		resetBenefitInUse (tOwningCompany);
	}

	// If this is an Extra Tile Placement Benefit, we DO NOT want to Change the
	// State
	@Override
	public boolean changeState () {
		return !extraTilePlacement;
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		String tBenefitText;
		
		tBenefitText = "Tile Placement on " + getMapCellID ();
		if (extraTilePlacement) {
			tBenefitText = "Extra " + tBenefitText;
		}
		
		tBenefitLabel = new JLabel (tBenefitText);
		
		return tBenefitLabel;
	}
}
