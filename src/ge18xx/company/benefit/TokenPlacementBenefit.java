package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.center.City;
import ge18xx.company.CorporationFrame;
import ge18xx.company.MapToken;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TokenPlacementBenefit extends MapBenefit {
	final static AttributeName AN_EXTRA = new AttributeName ("extra");
	public final static String NAME = "Token Placement";
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

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TOKEN_PRIVATE.equals (tActionCommand)) {
			handlePlaceToken ();
		}
	}

	@Override
	public boolean changeState () {
		return true;
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlaceTokenButton;

		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (!hasButton ()) {
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

	private void handlePlaceToken () {
		HexMap tMap;
		MapCell tMapCell;
		MapToken tMapToken;
		TokenType tTokenType;
		Tile tTile;
		ShareCompany tOwningCompany;
		City tCity;

		tOwningCompany = getOwningCompany ();
		capturePreviousBenefitInUse (tOwningCompany, this);

		tMap = getMap ();
		tMap.clearAllSelected ();
		tMapCell = tMap.getMapCellForID (mapCellID);
		if (tMapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("Did not find Map Cell with ID " + mapCellID);
		} else {
			if (tMapCell.isTileOnCell ()) {
				tTile = tMapCell.getTile ();
				if (tTile.cityOnTile ()) {
					tCity = (City) tTile.getRevenueCenter (0);
					// Local Client, need to add the Lay Token Action
					tMapToken = tOwningCompany.getLastMapToken ();
					tTokenType = tOwningCompany.getTokenType (tMapToken);
					tMap.putMapTokenDown (tOwningCompany, tMapToken, tTokenType, tCity, tMapCell, true);
				}
				tMap.toggleSelectedMapCell (tMapCell);
			} else {
				System.err.println ("No Tile found on MapCell");
			}
		}
	}

	private boolean hasTokenOnTile () {
		ShareCompany tOwningCompany;
		boolean tHasTokenOnTile = true;
		MapCell tMapCell;

		tOwningCompany = getOwningCompany ();
		tMapCell = getMapCell ();
		tHasTokenOnTile = tMapCell.hasStation (tOwningCompany.getID ());

		return tHasTokenOnTile;
	}

	private boolean hasTokens () {
		ShareCompany tOwningCompany;
		boolean tHasTokens = true;
		int tTokenCount;

		tOwningCompany = getOwningCompany ();
		tTokenCount = tOwningCompany.getTokenCount ();
		if (tTokenCount == 0) {
			tHasTokens = false;
		}

		return tHasTokens;
	}

	@Override
	public boolean realBenefit () {
		return true;
	}

	private void setExtraTokenPlacement (boolean aExtraTokenPlacement) {
		extraTokenPlacement = aExtraTokenPlacement;
	}

	@Override
	public void updateButton () {
		ShareCompany tOwningCompany;
		Benefit tBenefitInUse;
		String tBenefitInUseName;

		tOwningCompany = getOwningCompany ();
		tBenefitInUse = tOwningCompany.getBenefitInUse ();
		tBenefitInUseName = tBenefitInUse.getName ();
		if ((tBenefitInUse.realBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
			disableButton ();
			setToolTip ("Another Benefit is currently in Use");
		} else if (!hasTile ()) {
			disableButton ();
			setToolTip ("No Tile on the MapCell, can't place Station");
		} else if (hasTokenOnTile ()) {
			hideButton ();
			setToolTip ("Company has Station on Tile already");
		} else if (!hasTokens ()) {
			disableButton ();
			setToolTip ("Company has no Tokens, can't place Station");
		} else {
			enableButton ();
			setToolTip ("All Good");
		}
	}
}
