package ge18xx.train;

import java.awt.Color;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

//
//  Train.java
//  Java_18XX
//
//  Created by Mark Smith on 12/4/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.center.RevenueCenter;
import ge18xx.company.TrainCompany;
import ge18xx.company.Coupon;
import ge18xx.company.PurchaseTrainOffer;
import ge18xx.game.FrameButton;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.RouteAction;
import ge18xx.tiles.Gauge;
import ge18xx.tiles.Track;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class Train extends Coupon implements Comparable<Object> {
	public static final ElementName EN_TRAIN = new ElementName ("Train");
	public static final ElementName EN_CURRENT_ROUTE = new ElementName ("CurrentRoute");
	public static final ElementName EN_PREVIOUS_ROUTE = new ElementName ("PreviousRoute");
	public static final AttributeName AN_GAUGE = new AttributeName ("gauge");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ORDER = new AttributeName ("order");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_CITY_COUNT = new AttributeName ("cityCount");
	public static final AttributeName AN_TOWN_COUNT = new AttributeName ("townCount");
	public static final AttributeName AN_PRICE = new AttributeName ("price");
	public static final AttributeName AN_STATUS = new AttributeName ("status");
	public static final String NO_TRAIN_NAME = null;
	public static final String MISSING_NAME = "MISSING";
	public static final String NO_TILE_INFO = GUI.EMPTY_STRING;
	public static final String TYPE_NAME = "Train";
	public static final Train NO_TRAIN = null;
	public static final int MAX_STOPS = 30;
	public static final int HALF_MAX_STOPS = MAX_STOPS / 2;
	public static final int NO_ORDER = -1;
	public static final int NO_ID = 0;
	public static final int INFINITE_COUNT = 9999;
	public static final int NO_RC_COUNT = -1;
	public static final int NO_PRICE = -1;
	
	// Train Status constants. Maybe make this an ENUM ??
	public static final int NOT_AVAILABLE = 0;
	public static final int AVAILABLE_FOR_PURCHASE = 1;
	public static final int OWNED = 2;
	public static final int RUSTED = -2;
	public static final int RUST_AFTER_NEXT_OR = -3;
	public static final int NO_TRAIN_STATUS = -4;
	
	int order;
	int cityCount;
	int townCount;
	int status;
	int ID;
	boolean operating;
	boolean borrowed;
	Gauge gauge;
	TrainInfo trainInfo;
	FrameButton frameButton;
	RouteInformation currentRouteInformation;
	RouteInformation previousRouteInformation;
	JLabel costLabel;
	JCheckBox actionCheckbox;

	public Train () {
		this ("", NO_ORDER, new Gauge (), NO_RC_COUNT, NO_RC_COUNT, NO_PRICE, 0);
	}

	public Train (String aName, int aOrder, int aGaugeType, int aMajorCity, int aPrice, int aTrainID) {
		this (aName, aOrder, new Gauge (aGaugeType), aMajorCity, NO_RC_COUNT, aPrice, aTrainID);
	}

	public Train (String aName, int aOrder, int aGaugeType, int aMajorCity, int aMinorCity, int aPrice, int aTrainID) {
		this (aName, aOrder, new Gauge (aGaugeType), aMajorCity, aMinorCity, aPrice, aTrainID);
	}
	
	public Train (String aName, int aOrder, Gauge aGauge, int aMajorCity, int aMinorCity, int aPrice, int aTrainID) {
		super (aName, aPrice);
		setValues (aOrder, aGauge, aMajorCity, aMinorCity);
		setBorrowed (false);
		setTrainID (aTrainID);
	}

	public Train (Train aTrain) {
		this (aTrain.getName (), aTrain.order, aTrain.gauge.getType (), aTrain.cityCount, aTrain.townCount, 
			aTrain.getPrice (), aTrain.getID ());
		setStatus (aTrain.getStatus ());
		setTrainInfo (aTrain.getTrainInfo ());
	}

	private void setFrameButton (JCheckBox aJCheckBox, String aGroupName) {
		if (aJCheckBox != GUI.NO_CHECK_BOX) {
			frameButton = new FrameButton (aJCheckBox, aGroupName);
		}
	}

	public void setTrainID (int aTrainID) {
		ID = aTrainID;
	}
	
	public int getID () {
		return ID;
	}
	
	public FrameButton getFrameButton () {
		return frameButton;
	}

	public void clearFrameButton () {
		if (frameButton != FrameButton.NO_FRAME_BUTTON) {
			frameButton.setVisible (false);
		}
	}

	public void resetFrameButton () {
		if (frameButton != FrameButton.NO_FRAME_BUTTON) {
			frameButton.setVisible (true);
		}
	}

	public void setCurrentRouteInformation (RouteInformation aRouteInformation) {
		currentRouteInformation = aRouteInformation;
	}

	public void setPreviousRouteInformation (RouteInformation aRouteInformation) {
		previousRouteInformation = aRouteInformation;
	}

	public RouteInformation getCurrentRouteInformation () {
		return currentRouteInformation;
	}

	public RouteInformation getPreviousRouteInformation () {
		return previousRouteInformation;
	}

	@Override
	protected String getFullName () {
		return getName () + " " + TYPE_NAME;
	}

	public JPanel buildTrainInfoJPanel (TrainCheckboxInfo aTrainActionCheckboxInfo) {
		return buildTrainInfoJPanel (aTrainActionCheckboxInfo.getItemListener (),
									 aTrainActionCheckboxInfo.getLabel (),
									 aTrainActionCheckboxInfo.getEnabled (),
									 aTrainActionCheckboxInfo.getToolTip ());
	}
	
	public JPanel buildTrainInfoJPanel (ItemListener aItemListener, String aActionLabel, boolean aActionEnabled,
			String aActionToolTip) {
		JPanel tTrainInfoPanel;

		tTrainInfoPanel = buildCouponInfoPanel (isBorrowed ());
		if (aActionLabel != GUI.NULL_STRING) {
			if (aActionLabel != GUI.EMPTY_STRING) {
				if (actionCheckbox == GUI.NO_CHECK_BOX) {
					actionCheckbox = new JCheckBox (aActionLabel);
					setFrameButton (actionCheckbox, getFullName ());
				} else {
					actionCheckbox.setText (aActionLabel);
				}
				actionCheckbox.addItemListener (aItemListener);
				actionCheckbox.setEnabled (aActionEnabled);
				actionCheckbox.setToolTipText (aActionToolTip);
				tTrainInfoPanel.add (actionCheckbox);
			}
		} else {
			clearActionCheckbox (aActionLabel, aActionToolTip);
		}

		return tTrainInfoPanel;
	}

	public int getDiscountCost () {
		return trainInfo.getDiscount ();
	}

	public void applyDiscount (Coupon aTradeInTrain) {
		String tCostLabel;

		tCostLabel = "Discounted Cost " + Bank.formatCash (getDiscountCost ());
		setCostLabel (tCostLabel);
		setPrice (getDiscountCost ());
	}

	public boolean removeDiscount () {
		String tCostLabel;
		boolean tDiscountRemoved;
		int tTrainPrice;

		// during initial game setup, costLabel is not created yet, so don't have
		// cost labels (or trains) to discount yet
		if (validCostLabel ()) {
			tTrainPrice = trainInfo.getPrice ();
			tDiscountRemoved = (tTrainPrice != getPrice ());
			if (tDiscountRemoved) {
				setPrice (tTrainPrice);
				tCostLabel = "Cost " + Bank.formatCash (tTrainPrice);
				setCostLabel (tCostLabel);
				actionCheckbox.setSelected (false);
			}
		} else {
			tDiscountRemoved = false;
		}

		return tDiscountRemoved;
	}

	public boolean canBeUpgradedFrom (String aTradeInPossible) {
		boolean tCanBeUpgradedFrom = false;

		tCanBeUpgradedFrom = trainInfo.canBeUpgradedFrom (aTradeInPossible);

		return tCanBeUpgradedFrom;
	}

	public boolean canUpgrade (Train [] aAvailableTrains) {
		boolean tCanUpgrade = false;

		for (Train tAvailableTrain : aAvailableTrains) {
			if (tAvailableTrain.canBeUpgradedFrom (getName ())) {
				tCanUpgrade = true;
			}
		}
		
		return tCanUpgrade;
	}

	public void clearActionCheckbox (String aActionLabel, String aActionToolTip) {
		if (actionCheckbox == GUI.NO_CHECK_BOX) {
			actionCheckbox = new JCheckBox (aActionLabel);
			if (frameButton == FrameButton.NO_FRAME_BUTTON) {
				frameButton = new FrameButton (actionCheckbox, getFullName ());
			} else {
				frameButton.setCheckBox (actionCheckbox, getFullName ());
			}
		} else {
			actionCheckbox.setText (aActionLabel);
		}
		actionCheckbox.setEnabled (false);
		actionCheckbox.setToolTipText (aActionToolTip);
		clearSelection ();
	}

	@Override
	public void setSelection () {
		if (actionCheckbox != GUI.NO_CHECK_BOX) {
			actionCheckbox.setSelected (true);
		}
	}

	@Override
	public void clearSelection () {
		if (actionCheckbox != GUI.NO_CHECK_BOX) {
			actionCheckbox.setSelected (false);
		}
	}

	@Override
	public int compareTo (Object aTrain) throws ClassCastException {
		int tResult;

		if (!(aTrain instanceof Train))
			throw new ClassCastException ("A " + TYPE_NAME + " object expected.");
		tResult = order - ((Train) aTrain).order;

		return tResult;
	}

	public int getTrueCityCount () {
		return cityCount;
	}

	public int getCityCount () {
		// TODO: KLUDGE to allow Diesel to run with Infinite LENGTH (max 30).
		int tCityCountMax;

		tCityCountMax = cityCount;
		if (tCityCountMax > MAX_STOPS) {
			tCityCountMax = MAX_STOPS;
		}

		return tCityCountMax;
	}

	public Color getColor (int aTrainNumber) {
		Color color;

		switch (aTrainNumber) {
		case (1):
			color = Color.green;
			break;

		case (2):
			color = Color.orange;
			break;

		case (3):
			color = Color.red;
			break;

		case (4):
			color = Color.magenta;
			break;

		case (5):
			color = Color.cyan;
			break;

		case (6):
			color = Color.pink;
			break;

		default:
			color = Color.black;
			break;
		}

		return color;
	}

	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLCurrentRouteInfoElement;
		XMLElement tPreviousRouteInfoElement;

		tXMLElement = aXMLDocument.createElement (EN_TRAIN);
		tXMLElement.setAttribute (AN_NAME, getName ());
		tXMLElement.setAttribute (AN_STATUS, status);
		tXMLElement.setAttribute (AN_ORDER, order);
		tXMLElement.setAttribute (AN_ID, ID);
		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			if (currentRouteInformation.isValidRoute () > 0) {
				tXMLCurrentRouteInfoElement = currentRouteInformation.getElement (aXMLDocument, EN_CURRENT_ROUTE);
				tXMLElement.appendChild (tXMLCurrentRouteInfoElement);
			}
		}
		if (previousRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			if (previousRouteInformation.isValidRoute () > 0) {
				tPreviousRouteInfoElement = previousRouteInformation.getElement (aXMLDocument, EN_PREVIOUS_ROUTE);
				tXMLElement.appendChild (tPreviousRouteInfoElement);
			}
		}

		return tXMLElement;
	}

	public boolean isCurrentRouteValid () {
		boolean tIsCurrrentRouteValid = true;

		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			if (currentRouteInformation.isValidRoute () < 0) {
				tIsCurrrentRouteValid = false;
			}
		}

		return tIsCurrrentRouteValid;
	}

	public int getOrder () {
		return order;
	}

	public int getOnFirstOrderAvailable () {
		return trainInfo.getOnFirstOrderAvailable ();
	}

	public int getOnLastOrderAvailable () {
		return trainInfo.getOnLastOrderAvailable ();
	}

	public String getRust () {
		return trainInfo.getRust ();
	}

	public String getRustInfo () {
		String tRustInfo;

		tRustInfo = getRust ();
		if (!tRustInfo.equals (TrainInfo.NO_RUST)) {
			tRustInfo = "Rust " + tRustInfo + " " + TYPE_NAME + "s";
		}

		return tRustInfo;
	}

	public String getTileInfo () {
		String tTileInfo;

		tTileInfo = trainInfo.getTileInfo ();
		if (!tTileInfo.equals (Train.NO_TILE_INFO)) {
			tTileInfo = tTileInfo + " Tiles Available";
		}

		return tTileInfo;
	}

	public int getStatus () {
		return status;
	}

	static public String getNameOfStatus (int aStatusValue) {
		String tNameOfStatus;

		// TODO: update the TrainStatusValue to be an Enum, and get name from the Enum

		tNameOfStatus = "No " + TYPE_NAME + " Status";
		if (aStatusValue == NOT_AVAILABLE) {
			tNameOfStatus = "Not Available for Purchase";
		} else if (aStatusValue == AVAILABLE_FOR_PURCHASE) {
			tNameOfStatus = "Available for Purchase";
		} else if (aStatusValue == RUSTED) {
			tNameOfStatus = "Rusted";
		} else if (aStatusValue == RUST_AFTER_NEXT_OR) {
			tNameOfStatus = "Rust after Next Operating Round";
		}

		return tNameOfStatus;
	}

	public int getTownCount () {
		return townCount;
	}

	public TrainInfo getTrainInfo () {
		return trainInfo;
	}

	public boolean isAvailableForPurchase () {
		return (status == AVAILABLE_FOR_PURCHASE);
	}

	public boolean isNotAvailable () {
		return (status == NOT_AVAILABLE);
	}

	public boolean isPlusTrain () {
		return (townCount > 0);
	}

	public boolean isRusted () {
		return (status == RUSTED);
	}

	public boolean isSelected () {
		boolean tIsSelected;

		if (actionCheckbox == GUI.NO_CHECK_BOX) {
			tIsSelected = false;
		} else {
			tIsSelected = actionCheckbox.isSelected ();
		}

		return tIsSelected;
	}

	public boolean isThisCheckBox (Object aCheckBox) {
		return (actionCheckbox == aCheckBox);
	}

	public boolean isTrainThisOrder (int aOrder) {
		return (order == aOrder);
	}

	public boolean isUnlimitedQuantity () {
		return trainInfo.isUnlimitedQuantity ();
	}

	public boolean isPermanent () {
		return trainInfo.isPermanent ();
	}
	
	public void rust () {
		setStatus (RUSTED);
	}

	public void setStatus (int aStatus) {
		status = aStatus;
	}

	public void setTrainInfo (TrainInfo aTrainInfo) {
		trainInfo = aTrainInfo;
	}

	public void setUnlimitedQuantity () {
		trainInfo.setUnlimited ();
	}

	/** Set the Borrow Flag to the provided value 
	 * 
	 * @param aBorrowed
	 */
	public void setBorrowed (boolean aBorrowed) {
		borrowed = aBorrowed;
	}
	
	public void setValues (int aOrder, Gauge aGauge, int aMajorCity, int aMinorCity) {
		gauge = aGauge;
		order = aOrder;
		cityCount = aMajorCity;
		townCount = aMinorCity;

		setStatus (NOT_AVAILABLE);
		setTrainInfo (TrainInfo.NO_TRAIN_INFO);
		setCurrentRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
		setPreviousRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
		setOperating (false);
	}

	public void setOperating (boolean aOperating) {
		operating = aOperating;
	}

	public boolean isBorrowed () {
		return borrowed;
	}
	
	public boolean isOperating () {
		return operating;
	}

	public boolean hasRoute () {
		return (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION);
	}

	public boolean hasOperated () {
		return currentRouteInformation.hasOperated ();
	}

	public boolean willRustAfterNextOR () {
		return (status == RUST_AFTER_NEXT_OR);
	}

	public String getType () {
		return PurchaseTrainOffer.TRAIN_TYPE;
	}

	/**
	 * Update the Train Index on the Current Route, and Previous Route for this
	 * Train. Required so that when a Company loses a Train (it was bought, rusted,
	 * or discarded) the Reuse Route will have updated information for the remaining
	 * trains.
	 *
	 * @param aTrainIndex The train index value to set to.
	 */
	public void updateTrainIndex (int aTrainIndex) {
		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			currentRouteInformation.setTrainIndex (aTrainIndex);
		}
		if (previousRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			previousRouteInformation.setTrainIndex (aTrainIndex);
		}
	}

	/**
	 * Clear the Current Route Information by setting it to NO_ROUTE_INFORMATION
	 *
	 */
	public void clearCurrentRoute () {
		setCurrentRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
	}

	/**
	 * Clear the Previous Route Information by setting it to NO_ROUTE_INFORMATION
	 *
	 */
	public void clearPreviousRoute () {
		setPreviousRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
	}

	/**
	 * Clear the Current RouteInformation by calling currentRouteInformation.clear, if it is set
	 *
	 */
	public void clearRouteInformation () {
		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			currentRouteInformation.clear ();
		}
	}

	public void loadRouteInformation (XMLNode aRouteNode, Coupon aTrain, TrainPortfolio aTrainPortfolio) {
		RouteInformation tRouteInformation;
		String tNodeName;

		tNodeName = aRouteNode.getNodeName ();
		tRouteInformation = new RouteInformation (this, aRouteNode, aTrainPortfolio);
		if (tNodeName.equals (EN_CURRENT_ROUTE.getString ())) {
			currentRouteInformation = tRouteInformation;
		}
		if (tNodeName.equals (EN_PREVIOUS_ROUTE.getString ())) {
			previousRouteInformation = tRouteInformation;
		}
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			currentRouteInformation.fixLoadedRoutes (aMapFrame);
		}
		if (previousRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			previousRouteInformation.fixLoadedRoutes (aMapFrame);
		}
	}

	public boolean startRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation, String aRoundID, int aPhase, TrainCompany aTrainCompany,
			TrainRevenueFrame aTrainRevenueFrame) {
		boolean tRouteStarted = false;
		Color tColor = Color.BLUE;
		int tRegionBonus = 0;
		int tSpecialBonus = 0;
		int tCorpID;
		RevenueCenter tRevenueCenter;
		RouteSegment tRouteSegment;
		RouteAction tRouteAction;
		NodeInformation tEndNode;

		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
//			currentRouteInformation.clearTrainOn ();
		}
		currentRouteInformation = new RouteInformation (this, aTrainIndex, tColor, aRoundID, tRegionBonus,
				tSpecialBonus, aPhase, aTrainCompany, aTrainRevenueFrame);
		tRouteSegment = new RouteSegment (aMapCell);
		tCorpID = aTrainCompany.getID ();
		tRouteAction = RouteAction.NO_ROUTE_ACTION;
		tRevenueCenter = aMapCell.getCenterAtLocation (aStartLocation);
		currentRouteInformation.setStartSegment (tRouteSegment, tRevenueCenter, aPhase, tCorpID);
		if (aEndLocation != Location.NO_LOC) {
			tRevenueCenter = aMapCell.getRevenueCenterAt (aEndLocation);
			tEndNode = currentRouteInformation.buildNodeInformation (tRevenueCenter, aEndLocation, aPhase, tCorpID);
			tRouteSegment.setEndNode (tEndNode);
		}
		tRevenueCenter = aMapCell.getRevenueCenterAt (aEndLocation);
		currentRouteInformation.addTheRouteSegment (tRouteSegment, tRouteAction);
		tRouteStarted = true;

		return tRouteStarted;
	}

	public boolean extendRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation, String aRoundID, int aPhase, TrainCompany aTrainCompany,
			TrainRevenueFrame aTrainRevenueFrame) {
		boolean tRouteExtended = false;
		RouteSegment tRouteSegment;
		int tCorpID;
		RouteAction tRouteAction;
		NodeInformation tStartNode;
		NodeInformation tEndNode;
		RevenueCenter tRevenueCenter;

		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			tCorpID = aTrainCompany.getID ();
			tRouteSegment = new RouteSegment (aMapCell);
			tRevenueCenter = aMapCell.getRevenueCenterAt (aStartLocation);
			tStartNode = currentRouteInformation.buildNodeInformation (tRevenueCenter, aStartLocation, aPhase, tCorpID);
			tRouteSegment.setStartNode (tStartNode);

			tRevenueCenter = aMapCell.getRevenueCenterAt (aEndLocation);
			tEndNode = currentRouteInformation.buildNodeInformation (tRevenueCenter, aEndLocation, aPhase, tCorpID);
			tRouteSegment.setEndNode (tEndNode);

			tCorpID = aTrainCompany.getID ();
			tRouteAction = RouteAction.NO_ROUTE_ACTION;
			currentRouteInformation.addTheRouteSegment (tRouteSegment, tRouteAction);
			tRouteExtended = true;
		}

		return tRouteExtended;

	}

	public boolean setNewEndPoint (int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation,
			String aRoundID, int aPhase, TrainCompany aTrainCompany, TrainRevenueFrame aTrainRevenueFrame) {
		RouteSegment tPreviousRouteSegment;
		boolean tSetNewEndPoint;
		int tPreviousEndLocation;
		int tPreviousStartLocation;
		Location tPreviousEnd;
		Track tOldTrack;
		Track tNewTrack;

		if (currentRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
			tSetNewEndPoint = false;
		} else {
			tPreviousRouteSegment = currentRouteInformation.getLastRouteSegment ();
			tPreviousStartLocation = tPreviousRouteSegment.getStartLocationInt ();
			tPreviousEnd = tPreviousRouteSegment.getEndLocation ();
			tPreviousEndLocation = tPreviousEnd.getLocation ();

			if (tPreviousEndLocation == Location.NO_LOCATION) {
				tPreviousEndLocation = aEndLocation.getLocation ();
			}
			tOldTrack = aMapCell.getTrackFromStartToEnd (tPreviousStartLocation, tPreviousEndLocation);
			tNewTrack = aMapCell.getTrackFromStartToEnd (aStartLocation.getLocation (), aEndLocation.getLocation ());
			tPreviousRouteSegment.setEndNode (aEndLocation, aPhase);

			currentRouteInformation.swapTrackHighlights (aTrainIndex, tOldTrack, tNewTrack);

			currentRouteInformation.updateRevenueCenterInfo (aTrainCompany.getID (), tPreviousRouteSegment,
					aEndLocation, tPreviousEnd);
			currentRouteInformation.updateRevenueFrame ();

			tSetNewEndPoint = true;
		}

		return tSetNewEndPoint;
	}
}
