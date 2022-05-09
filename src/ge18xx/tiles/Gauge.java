package ge18xx.tiles;

//
//  Gauge.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import java.awt.Color;

public class Gauge implements Cloneable {
	public static final Gauge NO_GAUGE = null;
	static final int NO_TYPE = 0;
	public static final int NORMAL_GAUGE = 1;
	public static final int METER_GAUGE = 2;
	static final int DUAL_GAUGE = 3;
	static final int TUNNEL = 4;
	static final int FERRY = 5;
	static final int FERRY_BASE = 7;
	static final int METER_BASE = 8;
	static final int OVERPASS = 9;
	static final int MIN_GAUGE = NO_TYPE;
	static final int MAX_GAUGE = OVERPASS;

	// TODO Make this an Enum, with Names, and Useable Track Flag
	static final String NAMES[] = { "NO GAUGE", "NORMAL", "METER", "DUAL", "TUNNEL", "FERRY", "", "FERRY BASE",
			"METER BASE", "OVERPASS" };

	int gauge;

	public Gauge (int aGauge) {
		if ((aGauge >= MIN_GAUGE) && (aGauge <= MAX_GAUGE)) {
			gauge = aGauge;
		} else {
			gauge = NO_TYPE;
		}
	}

	public Gauge () {
		gauge = NO_TYPE;
	}

	@Override
	public Gauge clone () {
		try {
			Gauge tGauge = (Gauge) super.clone ();
			tGauge.gauge = gauge;

			return tGauge;
		} catch (CloneNotSupportedException e) {
			throw new Error ("Gauge.clone Not Supported Exception");
		}
	}

	public Gauge getBaseGauge () {
		Gauge tBaseGauge;

		if (hasBase ()) {
			if (gauge == FERRY) {
				tBaseGauge = new Gauge (FERRY_BASE);
			} else if (gauge == METER_GAUGE) {
				tBaseGauge = new Gauge (METER_BASE);
			} else if (gauge == TUNNEL) {
				tBaseGauge = new Gauge (METER_BASE);
			} else {
				tBaseGauge = NO_GAUGE;
			}
		} else {
			tBaseGauge = NO_GAUGE;
		}

		return tBaseGauge;
	}

	public Color getColor () {
		Color color;

		switch (gauge) {
		case NORMAL_GAUGE: /* Normal Gauge - Black */
			color = Color.black;
			break;

		case METER_GAUGE: /* Meter Gauge - Black [Dashed] */
			color = Color.black;
			break;

		case DUAL_GAUGE: /* Dual Gauge - White */
			color = Color.white;
			break;

		case TUNNEL: /* TUNNEL - Black [Dashed] */
			color = Color.black;
			break;

		case FERRY: /* FERRY - Red [Dashed] */
			color = Color.red;
			break;

		case FERRY_BASE:
			color = new Color (153, 204, 255);
			break;

		case METER_BASE:
			color = Color.white;
			break;

		case OVERPASS:
			color = null;
			break;

		default: /* use default, darkGray */
			color = Color.darkGray;
			break;
		}

		return color;
	}

	/**
	 * Test if a Gauge of a Track is useable or not.
	 * 
	 * @return True if Gauge is (NORMAL, METER, DUAL, TUNNEL, or FERRY)
	 */
	public boolean useableGauge () {
		boolean tUseableTrack;

		tUseableTrack = false;
		switch (gauge) {
		case NORMAL_GAUGE: /* Normal Gauge - Black */
		case METER_GAUGE: /* Meter Gauge - Black [Dashed] */
		case DUAL_GAUGE: /* Dual Gauge - White */
		case TUNNEL: /* TUNNEL - Black [Dashed] */
		case FERRY: /* FERRY - Red [Dashed] */
			tUseableTrack = true;
			break;
		}

		return tUseableTrack;
	}

	public static int getGaugeFromName (String aName) {
		int index;
		int thisGauge = NO_TYPE;

		for (index = MIN_GAUGE; index <= MAX_GAUGE; index++) {
			if (aName.equals (NAMES [index])) {
				thisGauge = index;
			}
		}

		return thisGauge;
	}

	public String getName () {
		return NAMES [gauge];
	}

	public String getTrainName () {
		String tTrainName;

		if (gauge == METER_GAUGE) {
			tTrainName = "M";
		} else {
			tTrainName = "";
		}

		return tTrainName;
	}

	public int getType () {
		return gauge;
	}

	public boolean hasBase () {
		if ((gauge == FERRY) || (gauge == METER_GAUGE)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDashed () {
		if ((gauge == FERRY) || (gauge == METER_GAUGE) || (gauge == TUNNEL)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFerryBase () {
		return (gauge == FERRY_BASE);
	}

	public boolean isOverpass () {
		return (gauge == OVERPASS);
	}

	public boolean isSelectable () {
		if ((gauge == OVERPASS) || (gauge == FERRY_BASE) || (gauge == METER_BASE)) {
			return false;
		} else {
			return true;
		}
	}

	public void printlog () {
		System.out.println ("Gauge Value " + gauge + " Name is " + NAMES [gauge]);
	}

	public boolean TrainCanUse () {
		return isSelectable ();
	}

	public boolean useNameInToolTip () {
		return isSelectable ();
	}
}
