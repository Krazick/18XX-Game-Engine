package ge18xx.round.action;

import ge18xx.utilities.AttributeName;

public interface ActorI {
	public final ActorI NO_ACTOR = null;
	public final String NO_NAME = null;
	public final ActorI.ActionStates NO_STATE = null;
	public static final AttributeName AN_ACTOR_NAME = new AttributeName ("actor");
	public static final AttributeName AN_TO_ACTOR_NAME = new AttributeName ("toActor");
	public static final AttributeName AN_FROM_ACTOR_NAME = new AttributeName ("fromActor");
	public enum ActorTypes { 
		NO_TYPE ("No Type"), Corporation ("Corporation"), 
		ShareCompany ("Share Company"), MinorCompany ("Minor Company"),  CoalCompany ("Coal Company"),
		Player ("Player"), Bank ("Bank"), BankPool ("Bank Pool");
		private String enumString;
		
		ActorTypes (String aEnumString) { enumString = aEnumString; }
		
		@Override
		public String toString () { return enumString; }
		
		public static ActorTypes fromString (String aActorType) {
			ActorTypes tFoundActorType = ActorTypes.NO_TYPE;
			
			for (ActorTypes tActorType : ActorTypes.values ()) {
				if (tActorType.toString ().equalsIgnoreCase (aActorType)) {
					tFoundActorType = tActorType;
				}
			}
			return tFoundActorType;
		}
		
	};
	
	public enum ActionStates { 
		NoAction ("No Action"), Pass ("Passed"), Acted ("Acted"), 			// Player Primary States
		Bought ("Bought"), Sold ("Sold"), 									
		BoughtDone ("Bought and Done"), BoughtSold ("Bought and Sold"), 	// Player Alternate States
		SoldDone ("Sold and Done"), BoughtSoldDone ("Bought, Sold and Done"), 
		Bid ("Bid"), BidDone ("Bid and Done"),								
		Bidder ("Bidder"), AuctionPass ("Auction Passed"), 					// Player Auction States
		AuctionRaise ("Auction Raised"), NotBidder ("Not a Bidder"),
		
		NoRound ("No Round", "NR"), StockRound ("Stock Round", "SR"), 					// Round States
		OperatingRound ("Operating Round", "OR"), AuctionRound ("Auction Round", "AR"),	
		
		Unowned ("Unowned"), Owned ("Owned"), Closed ("Closed"), 			// Corporation States
		MayFloat ("May Float"), WillFloat ("Will Float"), NotOperated ("Not Operated"),			
		StartedOperations ("Started Operating"), TileLaid ("Tile Laid"), 
		Tile2Laid ("Second Tile Laid"), TileUpgraded ("Tile Upgraded"), 
		StationLaid ("Station Laid"), TileAndStationLaid ("Tile and Station Laid"), 
		OperatedTrain ("Operated Train"), HoldDividend ("No Dividend Paid"), 
		HalfDividend ("Half Dividend Paid"), FullDividend ("Full Dividend Paid"), 
		BoughtTrain ("Bought Train"), Operated ("Operated"), 
		Unformed ("Unformed"), WaitingResponse ("Waiting for Response"),
		Fixed ("Fixed"), NoState ("No State"), Bankrupt ("Bankrupt");
		
		private String enumString;
		private String enumAbbrev;
		ActionStates (String aEnumString) { enumString = aEnumString; enumAbbrev = aEnumString;}
		ActionStates (String aEnumString, String aEnumAbbrev) { enumString = aEnumString; enumAbbrev = aEnumAbbrev;}
		
		@Override
		public String toString () { return enumString; }
		public String toAbbrev () { return enumAbbrev; }
				
	};

	public String getName ();
	public String getAbbrev();
	public String getStateName ();
	public void resetPrimaryActionState (ActionStates aPrimaryActionState);
	public boolean isAPrivateCompany ();
	public boolean isAPlayer ();
	public boolean isAStockRound ();
	public boolean isAOperatingRound ();
	public boolean isABank ();
	public boolean isABankPool ();
	public boolean isACorporation ();
	public boolean isATrainCompany ();
	public void completeBenefitInUse ();
}