package ge18xx.center;

//
//  RevenueCenterType.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

public class RevenueCenterType implements Cloneable {
	public static final RevenueCenterType NO_REVENUE_CENTER_TYPE = null;
	public static final String NO_REVENUE_CENTER_TYPE_NAME = null;
	public static final int NO_REVENUE_CENTER = 0;
	public static final int SMALL_TOWN = 1;
	public static final int TWO_SMALL_TOWNS = 2;
	public static final int SINGLE_CITY = 3;
	public static final int TWO_CITIES = 4;
	public static final int THREE_CITIES = 5;
	public static final int FOUR_CITIES = 6;
	public static final int FIVE_CITIES = 7;
	public static final int SIX_CITIES = 8;
	public static final int DOUBLE_CITY = 9;
	public static final int TRIPLE_CITY = 10;
	public static final int QUAD_CITY = 11;
	public static final int TWO_DOUBLE_CITIES = 12;
	public static final int DEAD_END_CITY = 13;
	public static final int DEAD_END_ONLY_CITY = 14;
	public static final int BYPASS_CITY = 15;
	public static final int DESTINATION_CITY = 16;
	public static final int DOT_TOWN = 17;
	public static final int PRIVATE_RAILWAY_POINT = 18;
	public static final int RUN_THROUGH_CITY = 19;
	public static final int MIN_REVENUE_CENTER_TYPE = NO_REVENUE_CENTER;
	public static final int MAX_REVENUE_CENTER_TYPE = RUN_THROUGH_CITY;
	static final String NAMES[] = { "No Revenue Center", "Small Town", "Two Small Towns", "Single City", "Two Cities",
			"Three Cities", "Four Cities", "Five Cities", "Six Cities", "Double City", "Triple City", "Quad City",
			"Two Double Cities", "Dead-End City", "Dead-End Only City", "Bypass City", "Destination City", "Dot Town",
			"Private Railway", "Run Through City" };

	int type;

	public RevenueCenterType () {
		setType (NO_REVENUE_CENTER);
	}

	public RevenueCenterType (int aType) {
		setType (aType);
	}

	public RevenueCenterType (String aTypeName) {
		setType (getTypeFromName (aTypeName));
	}
	
	public boolean canPlaceStation () {
		boolean canPlaceStation = false;

		switch (type) {
//			case NO_REVENUE_CENTER:
		case SMALL_TOWN:
		case TWO_SMALL_TOWNS:
		case DOT_TOWN:
		case DESTINATION_CITY:
		case PRIVATE_RAILWAY_POINT:
		case RUN_THROUGH_CITY:
			canPlaceStation = false;
			break;

		case SINGLE_CITY:
		case TWO_CITIES:
		case THREE_CITIES:
		case FOUR_CITIES:
		case FIVE_CITIES:
		case SIX_CITIES:
		case DOUBLE_CITY:
		case TRIPLE_CITY:
		case QUAD_CITY:
		case TWO_DOUBLE_CITIES:
		case DEAD_END_CITY:
		case DEAD_END_ONLY_CITY:
		case BYPASS_CITY:
			canPlaceStation = true;
			break;
		default:
			canPlaceStation = false;
			break;

		}

		return (canPlaceStation);
	}

	public boolean cityOrTown () {
		if (type == NO_REVENUE_CENTER) {
			return (false);
		} else {
			return (true);
		}
	}

	@Override
	public RevenueCenterType clone () {
		try {
			RevenueCenterType tRCT = (RevenueCenterType) super.clone ();
			tRCT.type = type;

			return tRCT;
		} catch (CloneNotSupportedException e) {
			throw new Error ("RevenueCenterType.clone Not Supported Exception");
		}
	}

	public int getCenterCount () {
		int count = 0;

		switch (type) {
		case SMALL_TOWN:
		case DOT_TOWN:
		case DEAD_END_CITY:
		case BYPASS_CITY:
		case DEAD_END_ONLY_CITY:
			count = 1;
			break;

		case TWO_SMALL_TOWNS:
			count = 2;
			break;

		default:
			count = getMaxStations ();
			break;
		}

		return count;
	}

	public int getMaxStations () {
		int aMaxStationCount;

		switch (type) {
		case SMALL_TOWN:
		case TWO_SMALL_TOWNS:
		case DOT_TOWN:
		case RUN_THROUGH_CITY:
		case PRIVATE_RAILWAY_POINT:
			aMaxStationCount = 0;
			break;

		case DESTINATION_CITY:
		case SINGLE_CITY:
			aMaxStationCount = 1;
			break;

		case DEAD_END_CITY:
		case BYPASS_CITY:
		case DEAD_END_ONLY_CITY:
			aMaxStationCount = 0;
			break;

		case TWO_CITIES:
		case DOUBLE_CITY:
			aMaxStationCount = 2;
			break;

		case THREE_CITIES:
		case TRIPLE_CITY:
			aMaxStationCount = 3;
			break;

		case FOUR_CITIES:
		case QUAD_CITY:
		case TWO_DOUBLE_CITIES:
			aMaxStationCount = 4;
			break;

		case FIVE_CITIES:
			aMaxStationCount = 5;
			break;

		case SIX_CITIES:
			aMaxStationCount = 6;
			break;

		default:
			aMaxStationCount = 0;
		}

		return aMaxStationCount;
	}

	public String getName () {
		return NAMES [type];
	}

	public int getStationCount () {
		int tStationCount;

		switch (type) {
		case SMALL_TOWN:
		case DOT_TOWN:
		case DEAD_END_CITY:
		case BYPASS_CITY:
		case DEAD_END_ONLY_CITY:
		case TWO_SMALL_TOWNS:
		case RUN_THROUGH_CITY:
		case PRIVATE_RAILWAY_POINT:
			tStationCount = 0;
			break;

		case SINGLE_CITY:
		case TWO_CITIES:
		case THREE_CITIES:
		case FOUR_CITIES:
		case FIVE_CITIES:
		case SIX_CITIES:
			tStationCount = 1;
			break;

		case DOUBLE_CITY:
		case TWO_DOUBLE_CITIES:
			tStationCount = 2;
			break;

		case TRIPLE_CITY:
			tStationCount = 3;
			break;

		case QUAD_CITY:
			tStationCount = 4;
			break;

		default:
			tStationCount = 0;
			break;
		}

		return tStationCount;
	}

	public int getType () {
		return type;
	}

	public int getTypeFromName (String aName) {
		int index;
		int thisType = NO_REVENUE_CENTER;

		for (index = MIN_REVENUE_CENTER_TYPE; index <= MAX_REVENUE_CENTER_TYPE; index++) {
			if (thisType == NO_REVENUE_CENTER) {
				if (aName.equals (NAMES [index])) {
					thisType = index;
				}
			}
		}

		return thisType;
	}

	public boolean isCity () {
		if (((type >= SINGLE_CITY) && (type <= DESTINATION_CITY)) || (type == RUN_THROUGH_CITY)) {
			return (true);
		} else {
			return (false);
		}
	}

	static public boolean isCity (String aType) {
		if (aType.equals ("Single City") || aType.equals ("Double City") || 
			aType.equals ("Two Cities") || aType.equals ("Three Cities") || 
			aType.equals ("Four Cities") || aType.equals ("Five Cities") || 
			aType.equals ("Six Cities") || aType.equals ("Triple City") || 
			aType.equals ("Quad City") || aType.equals ("Two Double Cities") || 
			aType.equals ("Dead-End City") || aType.equals ("Dead-End Only City") || 
			aType.equals ("Bypass City") || aType.equals ("Destination City") || 
			aType.equals ("Run Through City")) {
			return (true);
		} else {
			return (false);
		}
	}

	public boolean isARunThroughCity () {
		return (type == RUN_THROUGH_CITY);
	}

	public boolean isDestination () {
		return (type == DESTINATION_CITY);
	}

	public boolean isDotTown () {
		if (type == DOT_TOWN) {
			return (true);
		} else {
			return (false);
		}
	}

	static public boolean isDotTown (String aType) {
		if (aType.equals ("Dot Town")) {
			return (true);
		} else {
			return (false);
		}
	}

	public boolean isPrivateRailway () {
		return (type == PRIVATE_RAILWAY_POINT);
	}

	public boolean isTown () {
		if ((type == SMALL_TOWN) || (type == TWO_SMALL_TOWNS) || (type == DOT_TOWN)) {
			return (true);
		} else {
			return (false);
		}
	}

	static public boolean isTown (String aType) {
		if (aType.equals ("Small Town") || aType.equals ("Two Small Towns") || aType.equals ("Dot Town")) {
			return (true);
		} else {
			return (false);
		}
	}

	public boolean isTwoTowns () {
		if (type == TWO_SMALL_TOWNS) {
			return (true);
		} else {
			return (false);
		}
	}

	public void setType (int aType) {
		if ((aType >= MIN_REVENUE_CENTER_TYPE) && (aType <= MAX_REVENUE_CENTER_TYPE)) {
			type = aType;
		} else {
			type = NO_REVENUE_CENTER;
		}
	}
}
