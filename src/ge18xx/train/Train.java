package ge18xx.train;

//
//  Train.java
//  Java_18XX
//
//  Created by Mark Smith on 12/4/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.company.PurchaseOffer;
import ge18xx.tiles.Gauge;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

import java.awt.Color;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Train implements Comparable<Object> {
	final static ElementName EN_TRAIN = new ElementName ("Train");
	final static AttributeName AN_GAUGE = new AttributeName ("gauge");
	final static AttributeName AN_NAME = new AttributeName ("name");
	final static AttributeName AN_ORDER = new AttributeName ("order");
	final static AttributeName AN_CITY_COUNT = new AttributeName ("cityCount");
	final static AttributeName AN_TOWN_COUNT = new AttributeName ("townCount");
	final static AttributeName AN_PRICE = new AttributeName ("price");
	final static AttributeName AN_STATUS = new AttributeName ("status");
	public static final JCheckBox NO_ACTION_CHECKBOX = null;
	static final TrainInfo NO_TRAIN_INFO = null;
	static final int NO_TRAIN = 0;
	public static final int NO_ORDER = -1;
	public static final int INFINITE_COUNT = 9999;
	public static final int NO_RC_COUNT = -1;
	static final int NO_PRICE = -1;
	public static final int NOT_AVAILABLE = 0;
	public static final int AVAILABLE_FOR_PURCHASE = 1;
	public static final int RUSTED = -2;
	public static final int RUST_AFTER_NEXT_OR = -1;
	public static final int NO_TRAIN_STATUS = -3;
	public static final String NO_TRAIN_NAME = null;
	Gauge gauge;
	String name;
	int order;
	int cityCount;
	int townCount;
	int price;
	int status;
	TrainInfo trainInfo;
	JCheckBox actionCheckbox;
	JLabel costLabel;
	RouteInformation currentRouteInformation;
	RouteInformation previousRouteInformation;
	
	public Train () {
		Gauge no_gauge = new Gauge ();
		setValues ("", NO_ORDER, no_gauge, NO_RC_COUNT, NO_RC_COUNT, NO_PRICE);
	}

	public Train (String aName, int aOrder, int aGaugeType, int aMajorCity, int aPrice) {
		Gauge tGauge = new Gauge (aGaugeType);
		setValues (aName, aOrder, tGauge, aMajorCity, NO_RC_COUNT, aPrice);
	} 
	
	public Train (String aName, int aOrder, int aGaugeType, int aMajorCity, int aMinorCity, int aPrice) {
		Gauge tGauge = new Gauge (aGaugeType);
		setValues (aName, aOrder, tGauge, aMajorCity, aMinorCity, aPrice);
	}
	
	public Train (Train aTrain) {
		this (aTrain.name, aTrain.order, aTrain.gauge.getType (), aTrain.cityCount, aTrain.townCount, aTrain.price);
		setStatus (aTrain.getStatus ());
		setTrainInfo (aTrain.getTrainInfo ());
	}

	public void addRouteInformation (RouteInformation aRouteInformation) {
		setPreviousRouteInformation (currentRouteInformation);
		setCurrentRouteInformation  (aRouteInformation);
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
	
	public JPanel buildCertificateInfoPanel () {
		JPanel tCertificateInfoPanel;
		BoxLayout tCertInfoLayout;
		String tNameLabel;
		JLabel tLabel;
		Border tCertInfoBorder;
		
		tCertificateInfoPanel = new JPanel ();
		tCertInfoLayout = new BoxLayout (tCertificateInfoPanel, BoxLayout.Y_AXIS);
		tCertificateInfoPanel.setLayout (tCertInfoLayout);
		tCertInfoBorder = setupBorder ();
		tCertificateInfoPanel.setBorder (tCertInfoBorder);
		tNameLabel = name + " Train";
		tLabel = new JLabel (tNameLabel);
		tCertificateInfoPanel.add (tLabel);
		setCostLabel (tCertificateInfoPanel, price);

		return tCertificateInfoPanel;
	}
	
	public JPanel buildCertificateInfoContainer (ItemListener aItemListener, String aActionLabel,
					boolean aActionEnabled, String aActionToolTip) {
		JPanel tCertificateInfoPanel;
		
		tCertificateInfoPanel = buildCertificateInfoPanel ();
		if (aActionLabel != null) {
			if (actionCheckbox == NO_ACTION_CHECKBOX) {
				actionCheckbox = new JCheckBox (aActionLabel);			
			} else {
				actionCheckbox.setText (aActionLabel);
			}
			actionCheckbox.addItemListener (aItemListener);
			actionCheckbox.setEnabled (aActionEnabled);
			actionCheckbox.setToolTipText (aActionToolTip);
			tCertificateInfoPanel.add (actionCheckbox);
		} else {
			clearActionCheckbox (aActionLabel, aActionToolTip);
		}
		
		return tCertificateInfoPanel;
	}

	private Border setupBorder () {
		Border tCertInfoBorder;
		Border tInnerBorder;
		Border tOuterBorder;
		Color tInnerColor;
		
		tInnerColor = new Color (237, 237, 237);
		tInnerBorder = BorderFactory.createLineBorder (tInnerColor, 5);
		tOuterBorder = BorderFactory.createLineBorder (Color.black, 1);
		tCertInfoBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);
		
		return tCertInfoBorder;
	}

	public int getDiscountCost () {
		return trainInfo.getDiscount ();
	}
	
	public void applyDiscount (Train aTradeInTrain) {
		String tCostLabel;
		
		tCostLabel = "Discounted Cost " + Bank.formatCash (getDiscountCost ());
		costLabel.setText (tCostLabel);
		setPrice (getDiscountCost ());
	}
	
	public boolean removeDiscount () {
		String tCostLabel;
		boolean tDiscountRemoved = false;
		
		// during initial game setup, costLabel is not created yet, so don't have 
		// cost labels (or trains) to discount yet
		if (costLabel != null) {
			tDiscountRemoved = (trainInfo.getPrice () != price);
			if (tDiscountRemoved) {
				tCostLabel = "Cost " + Bank.formatCash (trainInfo.getPrice ());
				costLabel.setText (tCostLabel);
				setPrice (trainInfo.getPrice ());
				actionCheckbox.setSelected (false);
			}
		}
		
		return tDiscountRemoved;
	}
	
	private void setCostLabel (JPanel aCertificateInfoPanel, int aPrice) {
		String tCostLabel;
		
		tCostLabel = "Cost " + Bank.formatCash (aPrice);
		costLabel = new JLabel (tCostLabel);
		aCertificateInfoPanel.add (costLabel);
	}
	
	public boolean canBeUpgradedFrom (String aTradeInPossible) {
		boolean tCanBeUpgradedFrom = false;
		
		tCanBeUpgradedFrom = trainInfo.canBeUpgradedFrom (aTradeInPossible);
		
		return tCanBeUpgradedFrom;
	}
	
	public boolean canUpgrade (Train [] aAvailableTrains) {
		boolean tCanUpgrade = false;
		
		for (Train tAvailableTrain : aAvailableTrains) {
			if (tAvailableTrain.canBeUpgradedFrom (name)) {
				tCanUpgrade = true;
			}
		}
		return tCanUpgrade;
	}

	public void clearActionCheckbox (String aActionLabel, String aActionToolTip) {
		if (actionCheckbox == NO_ACTION_CHECKBOX) {
			actionCheckbox = new JCheckBox (aActionLabel);	
		} else {
			actionCheckbox.setText (aActionLabel);
		}
		actionCheckbox.setEnabled (false);
		actionCheckbox.setToolTipText (aActionToolTip);
		actionCheckbox.setSelected (false);
	}
	
	public void setSelection () {
		if (actionCheckbox != NO_ACTION_CHECKBOX) {
			actionCheckbox.setSelected (true);
		}
	}
	
	public void clearSelection () {
		if (actionCheckbox != NO_ACTION_CHECKBOX) {
			actionCheckbox.setSelected (false);
		}
	}
	
	public int compareTo (Object aTrain) throws ClassCastException {
		int tResult;
		
		if (!(aTrain instanceof Train))
			throw new ClassCastException ("A Train object expected.");
		tResult =  order - ((Train) aTrain).order;
		
		return tResult;
	}

	public int getCityCount () {
		return cityCount;
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
				color = Color.pink;
				break;
						
			case (4):		
				color = Color.cyan;
				break;
				
			case (5):		
				color = Color.magenta;
				break;
				
			case (6):		
				color = Color.red;
				break;
				
			default:		
				color = Color.black;
				break;
        }
		
		return color;
	}
	
	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (EN_TRAIN);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_STATUS, status);
		
		return tXMLElement;
	}
	
	public String getName () {
		return name;
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
	
	public int getPrice () {
		return price;
	}
	
	public String getRust () {
		return trainInfo.getRust ();
	}
	
	public int getStatus () {
		return status;
	}
	
	static public String getNameOfStatus (int aStatusValue) {
		String tNameOfStatus;
		
		tNameOfStatus = "No Train Status";
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
	
	public boolean isRusted () {
		return (status == RUSTED);
	}
	
	public boolean isSelected () {
		boolean tIsSelected;
		
		if (actionCheckbox == NO_ACTION_CHECKBOX) {
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
	
	public void rust () {
		setStatus (RUSTED);
	}
	
	public void setPrice (int aPrice) {
		price = aPrice;
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
	
	public void setValues (String aName, int aOrder, Gauge aGauge, int aMajorCity, int aMinorCity, int aPrice) {
		gauge = aGauge;
		order = aOrder;
		name = aName;
		cityCount = aMajorCity;
		townCount = aMinorCity;

		setPrice (aPrice);
		setStatus (NOT_AVAILABLE);
		setTrainInfo (NO_TRAIN_INFO);
		currentRouteInformation = RouteInformation.NO_ROUTE_INFORMATION;
		previousRouteInformation = RouteInformation.NO_ROUTE_INFORMATION;
	}
	
	public boolean willRustAfterNextOR () {
		return (status == RUST_AFTER_NEXT_OR);
	}

	public String getType () {
		return PurchaseOffer.TRAIN_TYPE;
	}

	public void clearRouteInformation() {
		if (currentRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			currentRouteInformation.clear ();
		}
		
	}
}
