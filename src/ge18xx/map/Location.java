package ge18xx.map;

import java.awt.Point;

//
//  Location.java
//  Java_18XX
//
//  Created by Mark Smith on 11/18/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

//  0 -  5 Actual Side of Hexagon
//  6 - 11 Near Side [NS] (Inside Hexagon)
// 12 - 17 Near Corner [NC] (Inside Hexagon)
// 18 - 23 Far (from) Side [FS] (Inside Hexagon)
// 24 - 29 Far (from) Corner [FCL] (Inside Hexagon)
// 30 - 35 Far (from) Corner [FCR] (Inside Hexagon)
// 36 - 39 Near Center [NCNTR] (only 4 locations here)
// 40 - 45 Adjacent to Side [ADJSIDE] (Inside Hexagon)
// 50      Center City Clocation
// 99      Dead End Location

import ge18xx.utilities.AttributeName;

public class Location implements Cloneable {
	public static final AttributeName AN_LOCATION = new AttributeName ("location");
	public static final AttributeName AN_HOME_LOCATION1 = new AttributeName ("homeLocation1");
	public static final AttributeName AN_HOME_LOCATION2 = new AttributeName ("homeLocation2");
	public static final Location NO_DESTINATION_LOCATION = null;
	public static final Location NO_LOC = null;
	public static final int NO_LOCATION = -1;
	public static final int MIN_SIDE = 0;
	public static final int MAX_SIDE = 5;
	static final int MIN_CITY_LOC_NS = 6;
	static final int MAX_CITY_LOC_NS = 11;
	static final int MIN_CITY_LOC_NC = 12;
	static final int MAX_CITY_LOC_NC = 17;
	static final int MIN_CITY_LOC_FS = 18;
	static final int MAX_CITY_LOC_FS = 23;
	static final int MIN_CITY_LOC_FCL = 24;
	static final int MAX_CITY_LOC_FCL = 29;
	static final int MIN_CITY_LOC_FCR = 30;
	static final int MAX_CITY_LOC_FCR = 35;
	static final int MIN_CITY_LOC_NCNTR = 36;
	static final int MAX_CITY_LOC_NCNTR = 39;
	static final int MIN_CITY_LOC_ADJSIDE = 40;
	static final int MAX_CITY_LOC_ADJSIDE = 45;
	public static final int CENTER_CITY_LOC = 50;
	public static final int DEAD_END0_LOC = 90;
	public static final int DEAD_END1_LOC = 91;
	public static final int DEAD_END2_LOC = 92;
	public static final int DEAD_END3_LOC = 93;
	public static final int DEAD_END4_LOC = 94;
	public static final int DEAD_END5_LOC = 95;
	public static final int DEAD_END_LOC = 99;
	static final int MIN_LOCATION = NO_LOCATION;
	static final int MAX_LOCATION = MAX_CITY_LOC_ADJSIDE;
	int location;

	public Location () {
		this (NO_LOCATION);
	}

	public Location (int aLocation) {
		setValue (aLocation);
	}

	public Point calcCenter (Hex aHex) {
		int Xdisp, Ydisp;
		int Xsign, Ysign;
		int Xbase, Ybase;
		double Xfactor, Yfactor;
		int tDisp;

		Xdisp = 0;
		Ydisp = 0;
		Xfactor = 1.0;
		Yfactor = 1.0;
		Xsign = getXSign ();
		Ysign = getYSign ();
		Ybase = aHex.getDisplaceLeftRight ();
		Xbase = aHex.getDisplaceUpDown ();

		if (this.isCityHexSide ()) {
			Xfactor = 2.0;
			if ((location == 6) || (location == 9)) {
				Yfactor = 1.0;
			} else {
				Yfactor = 2.0;
			}
			Xbase = aHex.getXt ();
			Ybase = aHex.getYt ();
		} else if (this.isCityAdjacentSide ()) {
			Xfactor = 1.25;
			if ((location == 40) || (location == 43)) {
				Yfactor = 0.65;
			} else {
				Yfactor = 1.4;
			}
			Xbase = aHex.getXt ();
			Ybase = aHex.getYt ();
		} else if (this.isCityHexCorner ()) {
			if ((location == 14) || (location == 17)) {
				Xfactor = 0.9;
				Yfactor = 1.00;
			} else {
				Xfactor = 1.75;
				Yfactor = 1.75;
			}
		} else if (this.isCityNearCenter ()) {
			Xfactor = 1.5;
			Yfactor = 1.5;
			Xbase = aHex.getXt ();
			Ybase = aHex.getYt ();
		} else if (this.isCityFarHexSide ()) {
			Xfactor = 4.975;
			Yfactor = 4.310;
			Xbase = aHex.getIntDWidth ();
			Ybase = aHex.getIntDWidth ();
		} else if (this.isCityFarHexCornerRight ()) {
			Xfactor = 2.585;
			if ((location == 30) || (location == 33)) {
				Xfactor = 11.0;
				Yfactor = 2.3;
			} else if ((location == 26) || (location == 29)) {
				Yfactor = 4.975;
			} else {
				Yfactor = 3.2;
			}
			Xbase = aHex.getIntDWidth ();
			Ybase = aHex.getIntDWidth ();
		} else if (this.isCityFarHexCornerLeft ()) {
			Xfactor = 2.585;
			if ((location == 25) || (location == 28)) {
				Xfactor = 11.0;
				Yfactor = 2.3;
			} else if ((location == 26) || (location == 29)) {
				Yfactor = 4.975;
			} else {
				Yfactor = 3.2;
			}
			Xbase = aHex.getIntDWidth ();
			Ybase = aHex.getIntDWidth ();
		}
		Xdisp = new Double (Xsign * (Xbase / Xfactor)).intValue ();
		Ydisp = new Double (Ysign * (Ybase / Yfactor)).intValue ();
		if (Hex.getDirection ()) {
			tDisp = Xdisp;
			Xdisp = Ydisp;
			Ydisp = -tDisp;
		}
		return (new Point (Xdisp, Ydisp));
	}

	@Override
	public Location clone () {
		try {
			Location tLocation = (Location) super.clone ();
			tLocation.location = location;

			return tLocation;
		} catch (CloneNotSupportedException e) {
			throw new Error ("Location.clone Not Supported Exception");
		}
	}

	public int getXSign () {
		int tXsign;

		switch (location) {
		case (6):
		case (9):
		case (18):
		case (21):
		case (36):
		case (38):
		case (40):
		case (43):
		case (CENTER_CITY_LOC):
		case (DEAD_END0_LOC):
		case (DEAD_END1_LOC):
		case (DEAD_END2_LOC):
		case (DEAD_END3_LOC):
		case (DEAD_END4_LOC):
		case (DEAD_END5_LOC):
		case (DEAD_END_LOC):
			tXsign = 0;
			break;

		case (10):
		case (11):
		case (12):
		case (16):
		case (17):
		case (22):
		case (23):
		case (24):
		case (28):
		case (29):
		case (30):
		case (34):
		case (35):
		case (39):
		case (44):
		case (45):
			tXsign = -1;
			break;

		default:
			tXsign = 1;
		}

		return tXsign;
	}

	public int getYSign () {
		int tYsign;

		switch (location) {
		case (6):
		case (7):
		case (11):
		case (12):
		case (13):
		case (18):
		case (19):
		case (23):
		case (24):
		case (25):
		case (26):
		case (30):
		case (31):
		case (35):
		case (36):
		case (40):
		case (41):
		case (45):
			tYsign = -1;
			break;

		case (14):
		case (17):
		case (37):
		case (39):
		case (CENTER_CITY_LOC):
		case (DEAD_END0_LOC):
		case (DEAD_END1_LOC):
		case (DEAD_END2_LOC):
		case (DEAD_END3_LOC):
		case (DEAD_END4_LOC):
		case (DEAD_END5_LOC):
		case (DEAD_END_LOC):
			tYsign = 0;
			break;

		default:
			tYsign = 1;
		}

		return tYsign;
	}

	public int getLocation () {
		return location;
	}

	public boolean GreaterThan (Location aOther) {
		int tOther = aOther.getLocation ();

		return (location > tOther);
	}

	public boolean isAdjacent (Location aOther) {
		int tOther;
		boolean retValue = false;

		if (this.isCityAdjacentSide ()) {
			tOther = aOther.getLocation ();
			if ((tOther + 40) == location) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return retValue;
	}

	public boolean isAdjacentBackward (Location aOther) {
		int tOther;
		boolean retValue = false;

		if ((location >= MIN_CITY_LOC_NC) && (location <= CENTER_CITY_LOC)) {
			if (this.isCityHexCorner ()) {
				tOther = aOther.getLocation ();
				if (((tOther + 12) == location)) {
					retValue = true;
				} else {
					retValue = false;
				}
			}
		}

		return retValue;
	}

	public boolean isAdjacentFarBackward (Location aOther) {
		int tOther;
		boolean retValue = false;

		if (this.isCityFarHexCornerRight ()) {
			tOther = aOther.getLocation ();
			if ((tOther + 30) == location) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return retValue;
	}

	public boolean isAdjacentFarForward (Location aOther) {
		int tOther;
		boolean retValue = false;

		if (this.isCityFarHexCornerLeft ()) {
			tOther = aOther.getLocation ();
			if (tOther == 5) {
				if ((tOther + 19) == location) {
					retValue = true;
				} else {
					retValue = false;
				}
			} else if (((tOther + 25) == location)) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return retValue;
	}

	public boolean isAdjacentForward (Location aOther) {
		int tOther;
		boolean retValue = false;

		if ((location >= MIN_CITY_LOC_NC) && (location <= CENTER_CITY_LOC)) {
			if (this.isCityHexCorner ()) {
				tOther = aOther.getLocation ();
				if (tOther == 5) {
					if ((tOther + 7) == location) {
						retValue = true;
					} else {
						retValue = false;
					}
				} else if (((tOther + 13) == location)) {
					retValue = true;
				} else {
					retValue = false;
				}
			}
		}

		return retValue;
	}

	public boolean isBackward (Location aOther) {
		int tOther;
		boolean retValue = false;

		if ((location >= MIN_CITY_LOC_FS) && (location <= CENTER_CITY_LOC)) {
			if (this.isCityFarHexSide ()) {
				tOther = aOther.getLocation ();
				if (tOther == 0) {
					if ((tOther + 23) == location) {
						retValue = true;
					} else {
						retValue = false;
					}
				} else if (((tOther + 17) == location)) {
					retValue = true;
				} else {
					retValue = false;
				}
			}
		}

		return retValue;
	}

	public boolean isCenterLocation () {
		return (location == CENTER_CITY_LOC);
	}

	public boolean isCity () {
		if (((location >= MIN_CITY_LOC_NS) && (location <= MAX_CITY_LOC_ADJSIDE)) || (location == CENTER_CITY_LOC)) {
			return (true);
		} else {
			return (false);
		}
	}

	public boolean isCityHexCorner () {
		return ((location >= MIN_CITY_LOC_NC) && (location <= MAX_CITY_LOC_NC));
	}

	public boolean isCityHexSide () {
		return ((location >= MIN_CITY_LOC_NS) && (location <= MAX_CITY_LOC_NS));
	}

	public boolean isCityFarHexCorner () {
		return ((location >= MIN_CITY_LOC_FCL) && (location <= MAX_CITY_LOC_FCR));
	}

	public boolean isCityFarHexCornerRight () {
		return ((location >= MIN_CITY_LOC_FCR) && (location <= MAX_CITY_LOC_FCR));
	}

	public boolean isCityFarHexCornerLeft () {
		return ((location >= MIN_CITY_LOC_FCL) && (location <= MAX_CITY_LOC_FCL));
	}

	public boolean isCityFarHexSide () {
		return ((location >= MIN_CITY_LOC_FS) && (location <= MAX_CITY_LOC_FS));
	}

	public boolean isCityNearCenter () {
		return ((location >= MIN_CITY_LOC_NCNTR) && (location <= MAX_CITY_LOC_NCNTR));
	}

	public boolean isCityAdjacentSide () {
		return ((location >= MIN_CITY_LOC_ADJSIDE) && (location <= MAX_CITY_LOC_ADJSIDE));
	}

	public boolean isClose (Location aOther) {
		int tOther;
		boolean retValue = false;

		if (this.isCityHexSide ()) {
			tOther = aOther.getLocation ();
			if ((tOther + 6) == location) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return retValue;
	}

	public boolean isDeadEnd () {
		return isDeadEnd (location);
	}
	
	public boolean isDeadEnd (int aLocation) {
		boolean tIsDeadEnd;
		
		switch (aLocation) {
			case (DEAD_END0_LOC):
			case (DEAD_END1_LOC):
			case (DEAD_END2_LOC):
			case (DEAD_END3_LOC):
			case (DEAD_END4_LOC):
			case (DEAD_END5_LOC):
			case (DEAD_END_LOC):
				tIsDeadEnd = true;
				break;
			default:
				tIsDeadEnd = false;
		}
		return tIsDeadEnd;
	}

	public boolean isFarOpposite (Location aOther) {
		Location tOther = new Location (aOther.getLocation ());
		boolean retValue = false;

		if (this.isCityHexSide ()) {
			tOther = new Location (aOther.getLocation ());
			tOther.rotateLocation180 ();
			retValue = isClose (tOther);
		}
		return retValue;
	}

	public boolean isForward (Location aOther) {
		int tOther;
		boolean retValue = false;

		if ((location >= MIN_CITY_LOC_FS) && (location <= CENTER_CITY_LOC)) {
			if (this.isCityFarHexSide ()) {
				tOther = aOther.getLocation ();
				if (tOther == 5) {
					if ((tOther + 13) == location) {
						retValue = true;
					} else {
						retValue = false;
					}
				} else if (((tOther + 19) == location)) {
					retValue = true;
				} else {
					retValue = false;
				}
			}
		}

		return retValue;
	}

	public boolean isNoLocation () {
		return (location == NO_LOCATION);
	}

	public boolean isOppositeSide (Location aOther) {
		int tOther;
		boolean retValue = false;

		if ((isSide ()) && (aOther.isSide ())) {
			tOther = aOther.getLocation ();
			if (location == (tOther + 3) % 6) {
				retValue = true;
			}
		}

		return (retValue);
	}

	public boolean isFarAdjacentForward (Location aOther) {
		int tOther = aOther.getLocation ();
		boolean retValue = false;

		if (this.isCityFarHexCornerRight ()) {
			if ((tOther == 4) || (tOther == 5)) {
				if ((tOther + 26) == location) {
					retValue = true;
				} else {
					retValue = false;
				}
			} else if ((tOther + 32) == location) {
				retValue = true;
			} else {
				retValue = false;
			}
		} else if (this.isCityHexSide ()) {
			if ((tOther == 4) || (tOther == 5)) {
				if ((tOther + 2) == location) {
					retValue = true;
				} else {
					retValue = false;
				}
			} else if ((tOther + 8) == location) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return retValue;
	}

	public boolean isFarAdjacentBackward (Location aOther) {
		int tOther = aOther.getLocation ();
		boolean retValue = false;

		if (this.isCityFarHexCornerLeft ()) {
			if (tOther == 0) {
				if ((tOther + 29) == location) {
					retValue = true;
				} else {
					retValue = false;
				}
			} else {
				if ((tOther + 23) == location) {
					retValue = true;
				} else {
					retValue = false;
				}
			}
		} else if (this.isCityHexSide ()) {
			if ((tOther == 0) || (tOther == 1)) {
				if ((tOther + 10) == location) {
					retValue = true;
				} else {
					retValue = false;
				}
			} else if ((tOther + 4) == location) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return retValue;
	}

	public static boolean isValidSide (int aLocation) {
		if ((aLocation >= MIN_SIDE) && (aLocation <= MAX_SIDE)) {
			return (true);
		} else {
			return (false);
		}
	}

	public static boolean isValidLocation (int aLocation) {
		if ((aLocation >= MIN_SIDE) && (aLocation <= MAX_LOCATION)) {
			return (true);
		} else if (aLocation == CENTER_CITY_LOC) {
			return (true);
		} else if (aLocation == DEAD_END_LOC) {
			return (true);
		} else if ((aLocation >= DEAD_END0_LOC) && (aLocation <= DEAD_END5_LOC)) {
			return (true);
		} else {
			return (false);
		}
	}

	public boolean isSide () {
		return (isValidSide (location));
	}

	public boolean isSide (int aSide) {
		if (isSide ()) {
			return (location == aSide);
		} else {
			return false;
		}
	}

	public void printlog () {
		System.out.println ("Location is " + location);
	}

	protected int rotateLocation (int aMinValue, int aOrientation, int aMod) {
		return aMinValue + (location - aMinValue + aOrientation) % aMod;
	}

	/**
	 * Given the current Location, rotate it by the provided Orientation, creating a new Location
	 * that is returned
	 *
	 * @param aOrientation The amount of Rotation that should be applied.
	 *
	 * @return a New Location Value that is rotated
	 *
	 */
	public Location rotateLocation (int aOrientation) {
		int newLocation = location;

		if (this.isSide ()) {
			newLocation = rotateLocation (0, aOrientation, 6);
		} else if (this.isCityHexSide ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_NS, aOrientation, 6);
		} else if (this.isCityHexCorner ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_NC, aOrientation, 6);
		} else if (this.isCityFarHexSide ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_FS, aOrientation, 6);
		} else if (this.isCityFarHexCornerRight ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_FCR, aOrientation, 6);
		} else if (this.isCityFarHexCornerLeft ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_FCL, aOrientation, 6);
		} else if (this.isCityAdjacentSide ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_ADJSIDE, aOrientation, 6);
		} else if (this.isCityNearCenter ()) {
			if (aOrientation < 4) {
				newLocation = rotateLocation (MIN_CITY_LOC_NCNTR, aOrientation, 4);
			}
		}

		return (new Location (newLocation));
	}

	public void rotateLocation180 () {
		Location newLocation = new Location (location);

		if ((location >= MIN_LOCATION) && (location <= MAX_CITY_LOC_FCR)) {
			newLocation = rotateLocation (3);
		} else if (this.isCityAdjacentSide ()) {
			newLocation = rotateLocation (3);
		} else if (this.isCityNearCenter ()) {
			newLocation = rotateLocation (2);
		}
		location = newLocation.getLocation ();
	}

	public void rotateLocation1Tick () {
		Location newLocation = new Location (location);

		newLocation = rotateLocation (1);
		location = newLocation.getLocation ();
	}

	public void rotateLocation2Tick () {
		Location newLocation = new Location (location);

		newLocation = rotateLocation (1);
		newLocation = rotateLocation (2);
		location = newLocation.getLocation ();
	}

	public void setValue (int aLocation) {
		if (((aLocation >= MIN_LOCATION) && (aLocation <= MAX_LOCATION))
				|| ((aLocation == CENTER_CITY_LOC) || (isDeadEnd (aLocation)))) {
			location = aLocation;
		}
	}

	@Override
	public String toString () {
		return (new Integer (location).toString ());
	}

	public Location unrotateLocation (int aOrientation) {
		int newLocation = location;

		if (this.isSide ()) {
			newLocation = rotateLocation (0, 6 - aOrientation, 6);
		} else if (this.isCityHexSide ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_NS, 6 - aOrientation, 6);
		} else if (this.isCityHexCorner ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_NC, 6 - aOrientation, 6);
		} else if (this.isCityFarHexSide ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_FS, 6 - aOrientation, 6);
		} else if (this.isCityFarHexCornerRight ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_FCR, 6 - aOrientation, 6);
		} else if (this.isCityFarHexCornerLeft ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_FCL, 6 - aOrientation, 6);
		} else if (this.isCityAdjacentSide ()) {
			newLocation = rotateLocation (MIN_CITY_LOC_ADJSIDE, 6 - aOrientation, 6);
		} else if (this.isCityNearCenter ()) {
			if (aOrientation < 4) {
				newLocation = rotateLocation (MIN_CITY_LOC_NCNTR, 4 - aOrientation, 4);
			}
		}

		return (new Location (newLocation));
	}

	public boolean isSameLocationValue (Location aOtherLocation) {
		int tOtherLocation;
		boolean tSameLocationValue;

		tOtherLocation = aOtherLocation.getLocation ();
		tSameLocationValue = (location == tOtherLocation);

		return tSameLocationValue;
	}
}
