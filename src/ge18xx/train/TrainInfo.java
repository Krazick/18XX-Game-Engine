package ge18xx.train;

import ge18xx.tiles.Gauge;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

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
	public static final AttributeName AN_TILE_INFO = new AttributeName ("tileInfo");
	public static final AttributeName AN_IS_PERMANENT= new AttributeName ("isPermanent");
	public static final TrainInfo NO_TRAIN_INFO = null;
	public static final String NO_RUST = "";
	public static final String PERMANENT = "Permanent";
	public static final String NORMAL_GAUGE = "Normal";
	public static final String METER_GAUGE = "Meter";
	public static final String START_PHASE = "1.1";
	public static final String NO_CENTERS = "NO CENTERS";
	public static final String UNLIMITED = "unlimited";
	public static final String NO_TRADE_INS = "NO TRADE INS";
	public static final boolean NOT_PERMANENT = false;
	public static final int UNLIMITED_TRAINS = 99;
	public static final int NO_DISCOUNT = -1;
	public static final int NO_TRAINS = 0;
	public static final int NO_PHASE = 0;
	Train train;
	boolean unlimited_quantity;
	boolean isPermanent;
	int quantity;
	int triggerMainPhase;
	int triggerMinorPhase;
	int discountPrice;
	int onFirstOrderAvailable;
	int onLastOrderAvailable;
	String rust;
	String tileInfo;
	String tradeInTrains;

	public TrainInfo () {
		train = new Train ();
		setValues (NO_TRAINS, NO_PHASE, NO_PHASE, NO_RUST, NO_DISCOUNT, NO_TRADE_INS, Train.NO_ORDER, Train.NO_ORDER,
				NOT_PERMANENT, Train.NO_TILE_INFO);
	}

	public TrainInfo (TrainInfo aTrainInfo) {
		train = new Train (aTrainInfo.train);
		setValues (aTrainInfo.quantity, aTrainInfo.triggerMainPhase, aTrainInfo.triggerMinorPhase, aTrainInfo.rust,
				aTrainInfo.discountPrice, aTrainInfo.tradeInTrains, aTrainInfo.onFirstOrderAvailable,
				aTrainInfo.onLastOrderAvailable, aTrainInfo.isPermanent, aTrainInfo.tileInfo);
	}

	public TrainInfo (XMLNode aCellNode) {
		String tRust;
		String tName;
		String tTriggerPhase;
		String tRevenueCenters;
		String tTownRevenueCenters;
		String tGaugeValue;
		String tTileInfo;
		int tQuantity;
		int tTriggerMainPhase;
		int tTriggerMinorPhase;
		int tMajorCount;
		int tMinorCount;
		int tPrice;
		int tDiscountPrice;
		int tGauge;
		int tOrder;
		int tOnFirstOrderAvailable;
		int tOnLastOrderAvailable;
		boolean tIsPermanent;
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
		tSplit = tTriggerPhase.split ("\\."); // Split uses Reg-ex, so the . needs escaping -- twice
		tTriggerMainPhase = Integer.parseInt (tSplit [0]);
		tTriggerMinorPhase = Integer.parseInt (tSplit [1]);
		tDiscountPrice = aCellNode.getThisIntAttribute (AN_DISCOUNT_PRICE, NO_DISCOUNT);
		tTradeInTrains = aCellNode.getThisAttribute (AN_TRADE_IN_TRAINS, NO_TRADE_INS);
		tRust = aCellNode.getThisAttribute (AN_RUST, NO_RUST);
		tTileInfo = aCellNode.getThisAttribute (AN_TILE_INFO, Train.NO_TILE_INFO);
		tGaugeValue = aCellNode.getThisAttribute (AN_GAUGE, NORMAL_GAUGE);
		tGauge = Gauge.NORMAL_GAUGE;
		if (tGaugeValue.equals (METER_GAUGE)) {
			tGauge = Gauge.METER_GAUGE;
		}
		tOnFirstOrderAvailable = aCellNode.getThisIntAttribute (AN_ON_FIRST, Train.NO_ORDER);
		tOnLastOrderAvailable = aCellNode.getThisIntAttribute (AN_ON_LAST, Train.NO_ORDER);
		tIsPermanent = aCellNode.getThisBooleanAttribute (AN_IS_PERMANENT);
		train = new Train (tName, tOrder, tGauge, tMajorCount, tMinorCount, tPrice);
		setValues (tQuantity, tTriggerMainPhase, tTriggerMinorPhase, tRust, tDiscountPrice, tTradeInTrains,
				tOnFirstOrderAvailable, tOnLastOrderAvailable, tIsPermanent, tTileInfo);
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

	public String getTileInfo () {
		return tileInfo;
	}

	public Train getTrain () {
		return train;
	}

	public XMLElement getTrainInfoElement (XMLDocument aXMLDocument) {
		XMLElement tElement;
		int tTownCount;
		int tOnLast;
		int tOnFirst;

		tElement = aXMLDocument.createElement (EN_TRAIN_INFO);
		tElement.setAttribute (AN_NAME, train.getName ());
		tElement.setAttribute (AN_REVENUE_CENTERS, train.getCityCount ());
		tTownCount = train.getTownCount ();
		if (tTownCount != Train.NO_RC_COUNT) {
			tElement.setAttribute (AN_TOWN_REVENUE_CENTERS, tTownCount);
		}
		tElement.setAttribute (AN_QUANTITY, quantity);
		tElement.setAttribute (AN_PRICE, train.getPrice ());
		tElement.setAttribute (AN_ORDER, train.getOrder ());
		tOnLast = getOnLastOrderAvailable ();
		if (tOnLast != Train.NO_ORDER) {
			tElement.setAttribute (AN_ON_LAST, tOnLast);
		}
		tOnFirst = getOnFirstOrderAvailable ();
		if (tOnFirst != Train.NO_ORDER) {
			tElement.setAttribute (AN_ON_FIRST, tOnFirst);
		}
		tElement.setAttribute (AN_TRIGGER_PHASE, triggerMainPhase + "." + triggerMinorPhase);
		if (discountPrice != NO_DISCOUNT) {
			tElement.setAttribute (AN_DISCOUNT_PRICE, discountPrice);
			tElement.setAttribute (AN_TRADE_IN_TRAINS, tradeInTrains);
		}
		if (!rust.equals (NO_RUST)) {
			tElement.setAttribute (AN_RUST, rust);
		}
		if (!tileInfo.equals (Train.NO_TILE_INFO)) {
			tElement.setAttribute (AN_TILE_INFO, tileInfo);
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

	public boolean isPermanent () {
		return isPermanent;
	}
	
	public void setUnlimited () {
		unlimited_quantity = true;
	}

	public void setValues (int aQuantity, int aTriggerMajorPhase, int aTriggerMinorPhase, String aRust,
			int aDiscountPrice, String aTradeInTrains, int aOnFirstOrderAvailable, int aOnLastOrderAvailable,
			boolean aIsPermanent, String aTileInfo) {
		quantity = aQuantity;
		triggerMainPhase = aTriggerMajorPhase;
		triggerMinorPhase = aTriggerMinorPhase;
		rust = aRust;
		tileInfo = aTileInfo;
		discountPrice = aDiscountPrice;
		tradeInTrains = aTradeInTrains;
		train.setTrainInfo (this);
		unlimited_quantity = false;
		onFirstOrderAvailable = aOnFirstOrderAvailable;
		setOnLastOrderAvailable (aOnLastOrderAvailable);
		isPermanent = aIsPermanent;
	}

	public void setOnLastOrderAvailable (int aOnLastOrderAvailable) {
		onLastOrderAvailable = aOnLastOrderAvailable;
	}
}
