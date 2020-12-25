package ge18xx.train;

import ge18xx.tiles.Gauge;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

//
//  TrainInfo.java
//  Game_18XX
//
//  Created by Mark Smith on 12/21/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class TrainInfo {
	public static final ElementName EN_TRAINS_INFO = new ElementName ("Trains");
	public static final ElementName EN_TRAIN_INFO = new ElementName ("Train");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ORDER = new AttributeName ("order");
	public static final AttributeName AN_REVENUE_CENTERS = new AttributeName ("revenueCenters");
	public static final AttributeName AN_TOWN_REVENUE_CENTERS = new AttributeName ("townRevenueCenters");
	public static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	public static final AttributeName AN_PRICE = new AttributeName ("price");
	public static final AttributeName AN_TRIGGER_PHASE = new AttributeName ("triggerPhase");
	public static final AttributeName AN_DISCOUNT_PRICE = new AttributeName ("discountPrice");
	public static final AttributeName AN_TRADE_IN_TRAINS = new AttributeName ("tradeInTrain");
	public static final AttributeName AN_RUST = new AttributeName ("rust");
	public static final AttributeName AN_GAUGE = new AttributeName ("gauge");
	public static final AttributeName AN_ON_FIRST = new AttributeName ("onFirst");
	public static final AttributeName AN_ON_LAST = new AttributeName ("onLast");
	public static final String NO_RUST = "";
	static final String NORMAL_GAUGE = "Normal";
	static final String METER_GAUGE = "Meter";
	static final String START_PHASE = "1.1";
	static final int NO_TRAINS = 0;
	static final int NO_PHASE = 0;
	static final String NO_CENTERS = "NO CENTERS";
	static final String UNLIMITED = "unlimited";
	public static final int UNLIMITED_TRAINS = 99;
	static final int NO_DISCOUNT = -1;
	static final String NO_TRADE_INS = "NO TRADE INS";
	Train train;
	boolean unlimited_quantity;
	int quantity;
	int triggerMainPhase;
	int triggerMinorPhase;
	String rust;
	int discountPrice;
	String tradeInTrains;
	int onFirstOrderAvailable;
	int onLastOrderAvailable;
	
	public TrainInfo () {
		train = new Train ();
		setValues (NO_TRAINS, NO_PHASE, NO_PHASE, NO_RUST, NO_DISCOUNT, NO_TRADE_INS, Train.NO_ORDER, Train.NO_ORDER);
	}
	
	public TrainInfo (TrainInfo aTrainInfo) {
		train = new Train (aTrainInfo.train);
		setValues (aTrainInfo.quantity, aTrainInfo.triggerMainPhase, aTrainInfo.triggerMinorPhase,
				aTrainInfo.rust, aTrainInfo.discountPrice, aTrainInfo.tradeInTrains,
				aTrainInfo.onFirstOrderAvailable, aTrainInfo.onLastOrderAvailable);
	}
	
	public TrainInfo (XMLNode aCellNode) {
		String tRust;
		int tQuantity, tTriggerMainPhase, tTriggerMinorPhase;
		String tName, tTriggerPhase;
		String tRevenueCenters, tTownRevenueCenters;
		String tGaugeValue;
		int tMajorCount, tMinorCount;
		int tPrice, tDiscountPrice, tGauge, tOrder, tOnFirstOrderAvailable, tOnLastOrderAvailable;
		String tTradeInTrains;
		String [] tSplit = null;
		
		tName = aCellNode.getThisAttribute (AN_NAME);
		tOrder = aCellNode.getThisIntAttribute (AN_ORDER, Train.NO_ORDER);
		tRevenueCenters = aCellNode.getThisAttribute (AN_REVENUE_CENTERS);
		tTownRevenueCenters = aCellNode.getThisAttribute (AN_TOWN_REVENUE_CENTERS, NO_CENTERS);
		if (tRevenueCenters.equals (UNLIMITED)) {
			tMajorCount = Train.INFINITE_COUNT;
		} else {
			tMajorCount = Integer.parseInt (tRevenueCenters);
		}
		if (tTownRevenueCenters.equals (UNLIMITED)) {
			tMinorCount = Train.INFINITE_COUNT;
		} else if (tTownRevenueCenters.equals (NO_CENTERS)) {
			tMinorCount = Train.NO_RC_COUNT;
		} else {
			tMinorCount = Integer.parseInt (tTownRevenueCenters);
		}
		tQuantity = aCellNode.getThisIntAttribute (AN_QUANTITY);
		tPrice = aCellNode.getThisIntAttribute (AN_PRICE);
		tTriggerPhase = aCellNode.getThisAttribute (AN_TRIGGER_PHASE, START_PHASE);
		tSplit = tTriggerPhase.split ("\\.");	// Split uses Reg-ex, so the . needs escaping -- twice
		tTriggerMainPhase = Integer.parseInt (tSplit [0]);
		tTriggerMinorPhase = Integer.parseInt (tSplit [1]);
		tDiscountPrice = aCellNode.getThisIntAttribute (AN_DISCOUNT_PRICE, NO_DISCOUNT);
		tTradeInTrains = aCellNode.getThisAttribute (AN_TRADE_IN_TRAINS, NO_TRADE_INS);
		tRust = aCellNode.getThisAttribute (AN_RUST, NO_RUST);
		tGaugeValue = aCellNode.getThisAttribute (AN_GAUGE, NORMAL_GAUGE);
		tGauge = Gauge.NORMAL_GAUGE;
		if (tGaugeValue.equals (METER_GAUGE)) {
			tGauge = Gauge.METER_GAUGE;
		}
		tOnFirstOrderAvailable = aCellNode.getThisIntAttribute (AN_ON_FIRST, Train.NO_ORDER);
		tOnLastOrderAvailable = aCellNode.getThisIntAttribute (AN_ON_LAST, Train.NO_ORDER);
		train = new Train (tName, tOrder, tGauge, tMajorCount, tMinorCount, tPrice);
		setValues (tQuantity, tTriggerMainPhase, tTriggerMinorPhase, tRust, tDiscountPrice, tTradeInTrains, 
				tOnFirstOrderAvailable, tOnLastOrderAvailable);
	}
	
	public String getName () {
		return train.getName ();
	}
	
	public int getDiscount () {
		return discountPrice;
	}
	
	public int getPrice () {
		return train.getPrice ();
	}

	public int getQuantity () {
		return quantity;
	}
	
	public String getRust () {
		return rust;
	}
	
	public Train getTrain () {
		return train;
	}
	
	public XMLElement getTrainInfoElement (XMLDocument aXMLDocument) {
		XMLElement tElement;
		int tTownCount;
		
		tElement = aXMLDocument.createElement (EN_TRAIN_INFO);
		tElement.setAttribute (AN_NAME, train.getName ());
		tElement.setAttribute (AN_REVENUE_CENTERS, train.getCityCount ());
		tTownCount = train.getTownCount ();
		if (tTownCount != Train.NO_RC_COUNT){
			tElement.setAttribute (AN_TOWN_REVENUE_CENTERS, tTownCount);
		}
		tElement.setAttribute (AN_QUANTITY, quantity);
		tElement.setAttribute (AN_PRICE, train.getPrice ());
		tElement.setAttribute (AN_TRIGGER_PHASE, triggerMainPhase + "." + triggerMinorPhase);
		if (discountPrice != NO_DISCOUNT) {
			tElement.setAttribute (AN_DISCOUNT_PRICE, discountPrice);
			tElement.setAttribute (AN_TRADE_IN_TRAINS, tradeInTrains);
		}
		if (! rust.equals (NO_RUST)) {
			tElement.setAttribute (AN_RUST, rust);
		}
		
		return tElement;
	}
	
	public int getTriggerMainPhase () {
		return triggerMainPhase;
	}
	
	public int getTriggerMinorPhase () {
		return triggerMinorPhase;
	}
	
	public String getTriggerPhase () {
		return triggerMainPhase + "." + triggerMinorPhase;
	}
	
	public int getOnFirstOrderAvailable () {
		return onFirstOrderAvailable;
	}
	
	public int getOnLastOrderAvailable () {
		return onLastOrderAvailable;
	}
	
	public boolean isStartPhase () {
		boolean tStartPhase;
		
		if ((triggerMainPhase == 1) && (triggerMinorPhase == 1)) {
			tStartPhase = true;
		} else {
			tStartPhase = false;
		}
		
		return tStartPhase;
	}
	
	public boolean canBeUpgradedFrom (String aTradeInPossible) {
		boolean tCanBeUpgradedFrom = false;
		
		if (tradeInTrains.contains (aTradeInPossible)) {
			tCanBeUpgradedFrom = true;
		}
		
		return tCanBeUpgradedFrom;
	}
	
	public boolean isUnlimitedQuantity () {
		return unlimited_quantity;
	}
	
	public void setUnlimited () {
		unlimited_quantity = true;
	}
	
	public void setValues (int aQuantity, int aTriggerMajorPhase, int aTriggerMinorPhase, String aRust, 
			int aDiscountPrice, String aTradeInTrains, int aOnFirstOrderAvailable, int aOnLastOrderAvailable) {
		quantity = aQuantity;
		triggerMainPhase = aTriggerMajorPhase;
		triggerMinorPhase = aTriggerMinorPhase;
		rust = aRust;
		discountPrice = aDiscountPrice;
		tradeInTrains = aTradeInTrains;
		train.setTrainInfo (this);
		unlimited_quantity = false;
		onFirstOrderAvailable = aOnFirstOrderAvailable;
		onLastOrderAvailable = aOnLastOrderAvailable;
	}
}
