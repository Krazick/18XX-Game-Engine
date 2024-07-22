package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.TrainCompany;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;

import geUtilities.AttributeName;
import geUtilities.XMLNode;
import swingTweaks.KButton;

public class TilePlacementBenefit extends MapBenefit {
	public static final AttributeName AN_EXTRA = new AttributeName ("extra");
	public static final String NAME = "Tile Placement";
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

		tNewButtonText = "Place Tile on " + privateCompany.getAbbrev () + " Home";

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
	public void updateButton () {
		if (hasTile ()) {
			hideButton ();
			setToolTip ("MapCell already has Tile");
		} else if (!isTileAvailable ()) {
			disableButton ();
			setToolTip ("No Tile available to place on MapCell");
		} else if (!ownerHasEnoughCash ()) {
			disableButton ();
			setToolTip ("Owner does not have enough cash to pay for Tile");
		} else if (!ownerLaidTile ()) {
			enableButton ();
			setToolTip ("");
		} else {
			if (extraTilePlacement) {
				enableButton ();
				setToolTip ("");
			} else {
				disableButton ();
				setToolTip ("Owner has already laid or upgraded a Tile");
			}
		}
	}

	private boolean ownerHasEnoughCash () {
		boolean tOwnerHasEnoughCash = false;
		TrainCompany tTrainCompany;
		HexMap tMap;
		MapCell tMapCell;
		int tCost;

		tTrainCompany = getOwningCompany ();
		if (tTrainCompany != Corporation.NO_CORPORATION) {
			tMap = getMap ();
			tMapCell = tMap.getMapCellForID (mapCellID);
			tCost = tMapCell.getCostToLayTile ();
			if (tTrainCompany.getTreasury () >= tCost) {
				tOwnerHasEnoughCash = true;
			}
		}

		return tOwnerHasEnoughCash;
	}

	private boolean ownerLaidTile () {
		boolean tOwnerLaidTile = false;
		TrainCompany tTrainCompany;

		tTrainCompany = getOwningCompany ();
		if (tTrainCompany != Corporation.NO_CORPORATION) {
			if (tTrainCompany.hasLaidTile ()) {
				tOwnerLaidTile = true;
			}
		}

		return tOwnerLaidTile;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TILE_PRIVATE.equals (tActionCommand)) {
			handlePlaceTile ();
		}
	}

	@Override
	public boolean isAExtraTilePlacement () {
		return extraTilePlacement;
	}

	private void handlePlaceTile () {
		HexMap tMap;
		MapCell tMapCell;
		TrainCompany tOwningCompany;

		tOwningCompany = getOwningCompany ();
		capturePreviousBenefitInUse (tOwningCompany, this);

		tOwningCompany.handlePlaceTile ();
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
		TrainCompany tOwningCompany;

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
