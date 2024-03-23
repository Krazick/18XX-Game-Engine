package ge18xx.round.action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import geUtilities.AttributeName;
import geUtilities.GUI;

public interface ActorI {
	public static final AttributeName AN_ACTOR_NAME = new AttributeName ("actor");
	public static final AttributeName AN_TO_ACTOR_NAME = new AttributeName ("toActor");
	public static final AttributeName AN_FROM_ACTOR_NAME = new AttributeName ("fromActor");
	public final String NO_NAME = GUI.NULL_STRING;
	public final ActorI NO_ACTOR = null;
	public final ActorI.ActionStates NO_STATE = null;
	
	public enum ActorTypes {
		NO_TYPE ("No Type"), 
		Corporation ("Corporation"), 
		ShareCompany ("Share Company"),
		MinorCompany ("Minor Company"), 
		PrivateCompany ("Private Company"), 
		Player ("Player"), 
		Bank ("Bank"),
		BankPool ("Bank Pool");

		private String enumString;

		ActorTypes (String aEnumString) {
			enumString = aEnumString;
		}

		@Override
		public String toString () {
			return enumString;
		}

		public static ActorTypes fromString (String aActorType) {
			ActorTypes tFoundActorType = ActorTypes.NO_TYPE;

			for (ActorTypes tActorType : ActorTypes.values ()) {
				if (tActorType.toString ().equalsIgnoreCase (aActorType)) {
					tFoundActorType = tActorType;
				}
			}
			return tFoundActorType;
		}
	}

	//  Remember that if ActionStates are updated, need to update corresponding methods in GenericActor
	//	to convert from the name to the specific state
	
	public enum ActionStates {
		// Player Primary States
		NoAction ("No Action", 
			Arrays.asList (new String [] { "Passed", "Acted", "Sold", "Bought",
											"Bought and Sold", "Bought, Sold and Done", 
											"Done", "Bid", "Bid and Done" })),
		Pass ("Passed", Arrays.asList (new String [] {"No Action"})), 
		Acted ("Acted"),
		Bought ("Bought", Arrays.asList (new String [] {"No Action"})),
		Sold ("Sold", Arrays.asList (new String [] {"No Action"})),
		BoughtDone ("Bought and Done", Arrays.asList (new String [] {"Bought"})),
		BoughtSold ("Bought and Sold", Arrays.asList (new String [] {"Bought", "Sold"})),
		SoldDone ("Sold and Done", Arrays.asList (new String [] {"Sold"})), 	
		BoughtSoldDone ("Bought, Sold and Done", Arrays.asList (new String [] {"Bought and Sold"})), 
		Bid ("Bid", Arrays.asList (new String [] {"No Action"})),
		BidDone ("Bid and Done", Arrays.asList (new String [] {"Bid"})), 
		WaitState ("Wait State", Arrays.asList (new String [] {"No Action"})),
		
		// Player Auction States
		Bidder ("Bidder"), 
		AuctionPass ("Auction Passed"),
		AuctionRaise ("Auction Raised"), 
		NotBidder ("Not a Bidder"),

		// Player Formation States
		CompanyFormation ("Company Formation", Arrays.asList (new String [] {"No State", "Pass", "Done"})),
		LoanRepayment ("Loan Repayment", Arrays.asList (new String [] {"Company Formation"})),
		ShareExchange ("Share Exchange", Arrays.asList (new String [] {"Loan Repayment"})),
		TokenExchange ("Token Exchange", Arrays.asList (new String [] {"Confirm Forming President"})),
		AssetCollection ("Asset Collection", Arrays.asList (new String [] {"Token Exchange"})),
		StockValueCalculation ("Stock Value Calculation", Arrays.asList (new String [] {"Asset Collection"})),
		FormationComplete ("Formation Complete", Arrays.asList (new String [] {"Loan Repayment", 
											"Stock Value Calculation"})),
		
		// Round States
		NoRound ("No Round", "NR"),
		StockRound ("Stock Round", "SR"),
		OperatingRound ("Operating Round", "OR"), 
		AuctionRound ("Auction Round", "AR"),
		FormationRound ("Formation Round", "FR"),

		// Corporation States
		Unowned ("Unowned"), 
		Owned ("Owned"),
		Closed ("Closed"),
		MayFloat ("May Float"), 
		WillFloat ("Will Float"), 
		NotOperated ("Not Operated"),
		StartedOperations ("Started Operating"), 
		TileLaid ("Tile Laid"),
		Tile2Laid ("Second Tile Laid"), 
		TileUpgraded ("Tile Upgraded"),
		StationLaid ("Station Laid"), 
		TileAndStationLaid ("Tile and Station Laid"),
		OperatedTrain ("Operated Train"), 
		HandledLoanInterest ("Handled Loan Interest"),
		HoldDividend ("No Dividend Paid"), 
		HalfDividend ("Half Dividend Paid"),
		FullDividend ("Full Dividend Paid"), 
		BoughtTrain ("Bought Train"),
		Operated ("Operated"), 
		Unformed ("Unformed"), 
		Inactive ("INACTIVE"), 
		WaitingResponse ("Waiting for Response"), 
		Fixed ("Fixed"), 
		NoState ("No State"), 
		Bankrupt ("Bankrupt"), 
		Recievership ("Recievership");

		private String enumString;
		private String enumAbbrev;
	    private List<String> validFromStates = new LinkedList<String> ();

		ActionStates (String aEnumString) {
			this (aEnumString, aEnumString);
		}
		
		ActionStates (String aEnumString, List<String> aValidFromStates) {
			this (aEnumString, aEnumString, aValidFromStates);
		}

		ActionStates (String aEnumString, String aEnumAbbrev) {
			this (aEnumString, aEnumAbbrev, Arrays.asList (new String [] {}));
		}
		
		ActionStates (String aEnumString, String aEnumAbbrev, List<String> aValidFromStates) {
			enumString = aEnumString;
			enumAbbrev = aEnumAbbrev;
			validFromStates = aValidFromStates;
		}
		
	    public boolean canChangeState (ActionStates aToState) {
    			boolean tValidChange;
    		
    			tValidChange = aToState.validFromStates.contains (this.toString ());
        
    			return tValidChange;
	    }
	    
	    public boolean canChangeState (ActionStates aFromState, ActionStates aToState) {
	    		boolean tValidChange;
	    		
	    		tValidChange = aToState.validFromStates.contains (aFromState.toString ());
	        
	        return tValidChange;
	    }

		@Override
		public String toString () {
			return enumString;
		}

		public String toNoSpaceString () {
			String tNoSpaceString;
			
			tNoSpaceString = enumString.replaceAll("\\s+","");
			
			return tNoSpaceString;
		}
		
		public String toAbbrev () {
			return enumAbbrev;
		}
	}

	public String getName ();

	public String getAbbrev ();

	public String getStateName ();

	public default void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// DO NOTHING by default
	}

	public default boolean isAPlayer () {
		return false;
	}

	public default boolean isAStockRound () {
		return false;
	}

	public default boolean isAAuctionRound () {
		return false;
	}

	public default boolean isAOperatingRound () {
		return false;
	}

	public default boolean isABank () {
		return false;
	}

	public default boolean isABankPool () {
		return false;
	}

	public default boolean isACorporation () {
		return false;
	}

	public default boolean isAPrivateCompany () {
		return false;
	}
	
	public default boolean isATokenCompany () {
		return false;
	}

	public default boolean isATrainCompany () {
		return false;
	}

	public default boolean isAShareCompany () {
		return false;
	}

	public default void completeBenefitInUse () {

	}

	public default void updateInfo () {

	}

	public default boolean isWaitingForResponse () {
		return false;
	}
}