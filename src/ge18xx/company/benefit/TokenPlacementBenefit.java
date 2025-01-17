package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.center.City;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.MapToken;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.player.CashHolderI;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;
import swingTweaks.KButton;

public class TokenPlacementBenefit extends MapBenefit {
	public static final AttributeName AN_EXTRA = new AttributeName ("extra");
	public static final String NAME = "Token Placement";
	boolean extraTokenPlacement;

	public TokenPlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		boolean tExtraTokenPlacement;

		tExtraTokenPlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTokenPlacement (tExtraTokenPlacement);
		setName (NAME);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		CashHolderI tTrainCompany;
		ShareCompany tShareCompany;

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TOKEN_PRIVATE.equals (tActionCommand)) {
			tTrainCompany = getOperatingCompany ();
			if (tTrainCompany.isAShareCompany ()) {
				tShareCompany = (ShareCompany) tTrainCompany;
				handlePlaceToken (tShareCompany);
			}
		}
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		Corporation tOperatingCompany;

		tOperatingCompany = getOperatingCompany ();
		configure (aPrivateCompany, aButtonRow, tOperatingCompany);
	}

	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow, Corporation aOperatingCompany) {
		KButton tPlaceTokenButton;
		
		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (!hasButton ()) {
				tPlaceTokenButton = new KButton (getNewButtonLabel ());
				setButton (tPlaceTokenButton);
				setButtonPanel (aButtonRow);
				tPlaceTokenButton.setActionCommand (CorporationFrame.PLACE_TOKEN_PRIVATE);
				tPlaceTokenButton.addActionListener (this);
				aButtonRow.add (tPlaceTokenButton);
			}

			if (aOperatingCompany != Corporation.NO_CORPORATION) {
				updateButton (aOperatingCompany);
			}
		}

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

		tNewButtonText = "Place Token on " + privateCompany.getAbbrev () + " Home (" + mapCellID + ")";

		return tNewButtonText;
	}
	
	private void handlePlaceToken (ShareCompany aShareCompany) {
		HexMap tMap;
		MapCell tMapCell;
		MapToken tMapToken;
		MapFrame tMapFrame;
		TokenType tTokenType;
		Tile tTile;
		City tCity;
		int tCityCount;

		capturePreviousBenefitInUse (aShareCompany, this);

		tMap = getMap ();
		tMap.clearAllSelected ();
		tMapCell = tMap.getMapCellForID (mapCellID);
		if (tMapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			if (tMapCell.isTileOnCell ()) {
				tTile = tMapCell.getTile ();
				tCityCount = tTile.getCityCenterCount ();
				if (tCityCount == 1) {
					tCity = (City) tTile.getRevenueCenter (0);
					// Local Client, need to add the Lay Token Action
					tMapToken = aShareCompany.getLastMapToken ();
					tTokenType = aShareCompany.getTokenType (tMapToken);
					tMap.putMapTokenDown (aShareCompany, tMapToken, tTokenType, tCity, tMapCell, true);
				} else if (tCityCount > 1) {
					aShareCompany.enterPlaceTokenMode ();
					tMap.addMapCellSMC (tMapCell);
					tMapFrame = getMapFrame ();
					tMapFrame.updatePutTokenButton (City.NO_CITY, tMapCell);
				}
				tMap.toggleSelectedMapCell (tMapCell);
			} else {
				System.err.println ("No Tile found on MapCell");
			}
		}
	}

	private boolean hasTokenOnTile () {
		Corporation tOperatingCompany;
		boolean tHasTokenOnTile;
		MapCell tMapCell;

		tOperatingCompany = getOperatingCompany ();
		tMapCell = getMapCell ();
		tHasTokenOnTile = tMapCell.hasStation (tOperatingCompany.getID ());

		return tHasTokenOnTile;
	}

	private boolean hasMapTokens () {
		Corporation tOperatingCompany;
		boolean tHasMapTokens;
		int tTokenCount;

		tOperatingCompany = getOperatingCompany ();
		tTokenCount = tOperatingCompany.getTokenCount ();
		if (tTokenCount == 0) {
			tHasMapTokens = false;
		} else {
			tHasMapTokens = true;
		}

		return tHasMapTokens;
	}

	@Override
	public boolean isRealBenefit () {
		return true;
	}

	private void setExtraTokenPlacement (boolean aExtraTokenPlacement) {
		extraTokenPlacement = aExtraTokenPlacement;
	}

	@Override
	public void updateButton () {
		Corporation tOwningCompany;

		tOwningCompany = getOperatingCompany ();
		updateButton (tOwningCompany);
	}
	
	public void updateButton (Corporation aOperatingCompany) {
		Benefit tBenefitInUse;
		String tBenefitInUseName;

		tBenefitInUse = aOperatingCompany.getBenefitInUse ();
		tBenefitInUseName = tBenefitInUse.getName ();
		if ((tBenefitInUse.isRealBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
			disableButton ();
			setToolTip ("Another Benefit is currently in Use");
		} else if (! hasTileWithTrack ()) {
			disableButton ();
			setToolTip ("No Tile with Track on the MapCell, can't place Station");
		} else if (hasTokenOnTile ()) {
			hideButton ();
			setToolTip ("Company has Station on Tile already");
		} else if (!hasMapTokens ()) {
			disableButton ();
			setToolTip ("Company has no Tokens, can't place Station");
		} else {
			enableButton ();
			setToolTip ("Token can be placed");
		}
	}
	
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		String tBenefitText;
		
		tBenefitText = "Token Placement on " + getMapCellID ();
		if (extraTokenPlacement) {
			tBenefitText = "Extra " + tBenefitText;
		}
		
		tBenefitLabel = new JLabel (tBenefitText);
		
		return tBenefitLabel;
	}
}
