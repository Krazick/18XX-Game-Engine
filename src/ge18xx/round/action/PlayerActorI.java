package ge18xx.round.action;

import ge18xx.utilities.AttributeName;

public interface PlayerActorI extends ActorI {
	public static final AttributeName AN_TO_PLAYER_NAME = new AttributeName ("toPlayer");
	public static final AttributeName AN_FROM_PLAYER_NAME = new AttributeName ("fromPlayer");
	public enum PlayerActionStates { 
		NoAction, Pass, Acted, Bought, Sold, 							// Player Primary States
		BoughtDone, BoughtSold, SoldDone, BoughtSoldDone, Bid, BidDone }	// Player Alternate States
}
