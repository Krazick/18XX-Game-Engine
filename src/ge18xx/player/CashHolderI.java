package ge18xx.player;

import ge18xx.round.action.ActorI;

//
//  CashHolder.java
//  Game_18XX
//
//  Created by Mark Smith on 1/8/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

public interface CashHolderI extends ActorI {
	public static final CashHolderI NO_CASH_HOLDER = null;
	public static final String PLAYER_CASH_CHANGED = "Player Cash Changed";
	public static final String PLAYER_BID_CHANGED = "Player Bid Changed";
	public static final String PLAYER_STATUS_CHANGED = "Player Status Changed";
	public static final String BANK_CASH_CHANGED = "Bank Cash Changed";
	public static final String CORPORATION_CASH_CHANGED = "Corporation Cash Changed";

	public abstract void addCash (int aAmount);

	public abstract int getCash ();

	public abstract void transferCashTo (CashHolderI aToCashHolder, int aAmount);
	
	public default void addCashToDividends (int aAmount, int aOperatingRoundID) {
		// Only a Player needs to track this for reporting
	}
	
	public default void clearRoundDividends (int aOperatingRoundID) {
		// Only a Player needs to track this for reporting		
	}
	
	public abstract void updateListeners (String aMessage);
}