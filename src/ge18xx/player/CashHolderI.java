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
	public final CashHolderI NO_CASH_HOLDER = null;
	
	public abstract void addCash (int aAmount);

	public abstract int getCash ();

	public abstract void transferCashTo (CashHolderI aToCashHolder, int aAmount);
	
	public abstract void updateObservers (String aMessage);
}