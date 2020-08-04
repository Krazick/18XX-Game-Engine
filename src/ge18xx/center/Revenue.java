package ge18xx.center;

//
//  Revenue.java
//  Game_18XX
//
//  Created by Mark Smith on 12/9/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class Revenue implements Cloneable {
	static final int NO_REVENUE = 0;
	static final int ALL_PHASES = 0;
	int value;
	int phase;
	
	public Revenue () {
		this (NO_REVENUE, ALL_PHASES);
	}
	
	public Revenue (int aValue, int aPhase) {
		setValues (aValue, aPhase);
	}
	
	public Revenue (Revenue aRevenue) {
		if (aRevenue != null) {
			value = aRevenue.value;
			phase = aRevenue.phase;
		} else {
			setValues (NO_REVENUE, ALL_PHASES);
		}
	}
	
	public Revenue clone () {
		try {
			Revenue copy = (Revenue) super.clone ();
			copy.value = value;
			copy.phase = phase;
			
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new Error ("This should never happen! (Revenue Clone)");
		}
	}
	
	public int getPhase () {
		return phase;
	}
	
	public String getPhaseToString () {
		return (new Integer (phase).toString ());
	}
	
	public int getValue () {
		return value;
	}
	
	public String getValueToString () {
		return (new Integer (value).toString ());
	}
	
	public void setValues (int aValue, int aPhase){
		value = aValue;
		phase = aPhase;
	}
}
