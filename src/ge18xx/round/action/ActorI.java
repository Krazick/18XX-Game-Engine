package ge18xx.round.action;

import ge18xx.utilities.AttributeName;

public interface ActorI {
	public final ActorI NO_ACTOR = null;
	public static final AttributeName AN_ACTOR_NAME = new AttributeName ("actor");
	public static final AttributeName AN_TO_ACTOR_NAME = new AttributeName ("toActor");
	public static final AttributeName AN_FROM_ACTOR_NAME = new AttributeName ("fromActor");
	public enum ActionStates { 
		NoAction ("No Action"), Pass ("Passed"), Acted ("Acted"), 			// Player Primary States
		Bought ("Bought"), Sold ("Sold"), 									
		BoughtDone ("Bought and Done"), BoughtSold ("Bought and Sold"), 	// Player Alternate States
		SoldDone ("Sold and Done"), BoughtSoldDone ("Bought, Sold and Done"), 
		Bid ("Bid"), BidDone ("Bid and Done"),								
		Bidder ("Bidder"), AuctionPass ("Auction Passed"), 					// Player Auction States
		AuctionRaise ("Auction Raised"), NotBidder ("Not a Bidder"),		
		NoRound ("No Round"), StockRound ("Stock Round"), 					// Round States
		OperatingRound ("Operating Round"), AuctionRound ("Auction Round"),	
		Unowned ("Unowned"), Owned ("Owned"), Closed ("Closed"), 			// Corporation States
		MayFloat ("May Float"), WillFloat ("Will Float"), NotOperated ("Not Operated"),			
		StartedOperations ("Started Operating"), TileLaid ("Tile Laid"), 
		Tile2Laid ("Second Tile Laid"), TileUpgraded ("Tile Upgraded"), 
		StationLaid ("Station Laid"), TileAndStationLaid ("Tile and Station Laid"), 
		OperatedTrain ("Operated Train"), HoldDividend ("No Dividend Paid"), 
		HalfDividend ("Half Dividend Paid"), FullDividend ("Full Dividend Paid"), 
		BoughtTrain ("Bought Train"), Operated ("Operated"), WaitingResponse ("Waiting for Response"),
		Fixed ("Fixed"), NoState ("No State")	;
		
		private String enumString;
		
		ActionStates (String aEnumString) { enumString = aEnumString; }
		
		@Override
		public String toString () { return enumString; }
		
		};
	
	public String getName ();
	public String getStateName ();
	public boolean isAPrivateCompany ();
	public void resetPrimaryActionState (ActionStates aPrimaryActionState);
	public boolean isAPlayer ();
	public boolean isAStockRound ();
	public boolean isAOperatingRound ();
	public boolean isABank ();
	public boolean isACorporation ();
}