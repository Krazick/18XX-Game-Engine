package ge18xx.round.action;

public class GenericActor implements ActorI {

	public GenericActor () {

	}

	@Override
	public String getName () {
		return "Generic";
	}

	@Override
	public String getStateName () {
		return ActionStates.Fixed.toString ();
	}

	public ActionStates getState (String aState) {
		ActionStates tState;

		tState = getPlayerState (aState);
		if (tState == ActionStates.NoState) {
			tState = getCorporationActionState (aState);
		}
		if (tState == ActionStates.NoState) {
			tState = getRoundType (aState);
		}
		if (tState == ActionStates.NoState) {
			tState = getPlayerFormationState (aState);
		}

		return tState;
	}

	// Player Primary Actions:
	// NoAction, Pass, Acted, Bought, Sold,
	// BoughtDone, BoughtSold, SoldDone, BoughtSoldDone,
	//
	// Player Auction, and Bidding States
	// Bid, BidDone, Bidder, AuctionPass, AuctionPass, AuctionRaise,
	// NotBidder, WaitState


	public ActionStates getPlayerState (String aState) {
		ActionStates tPlayerState;

		tPlayerState = ActionStates.NoState;
		if (aState.equals (ActionStates.NoAction.toString ())) {
			tPlayerState = ActionStates.NoAction;
		} else if (aState.equals (ActionStates.Acted.toString ())) {
			tPlayerState = ActionStates.Acted;
		} else if (aState.equals (ActionStates.Pass.toString ())) {
			tPlayerState = ActionStates.Pass;
		} else if (aState.equals (ActionStates.Bought.toString ())) {
			tPlayerState = ActionStates.Bought;
		} else if (aState.equals (ActionStates.Sold.toString ())) {
			tPlayerState = ActionStates.Sold;
		} else if (aState.equals (ActionStates.BoughtDone.toString ())) {
			tPlayerState = ActionStates.BoughtDone;
		} else if (aState.equals (ActionStates.SoldDone.toString ())) {
			tPlayerState = ActionStates.SoldDone;
		} else if (aState.equals (ActionStates.BoughtSoldDone.toString ())) {
			tPlayerState = ActionStates.BoughtSoldDone;
		} else if (aState.equals (ActionStates.Bid.toString ())) {
			tPlayerState = ActionStates.Bid;
		} else if (aState.equals (ActionStates.Bidder.toString ())) {
			tPlayerState = ActionStates.Bidder;
		} else if (aState.equals (ActionStates.AuctionPass.toString ())) {
			tPlayerState = ActionStates.AuctionPass;
		} else if (aState.equals (ActionStates.AuctionRaise.toString ())) {
			tPlayerState = ActionStates.AuctionRaise;
		} else if (aState.equals (ActionStates.NotBidder.toString ())) {
			tPlayerState = ActionStates.NotBidder;
		} else if (aState.equals (ActionStates.WaitState.toString ())) {
			tPlayerState = ActionStates.WaitState;
		}

		return tPlayerState;
	}
	public ActorI.ActionStates getPlayerFormationState (String aState) {
		ActorI.ActionStates tPlayerState;

		tPlayerState = ActionStates.NoState;

//		These are the States needed for New Company Formation ( 1856 - CGR, 1835 - Prussian )
//		LoanRepayment ("Loan Repayment", Arrays.asList (new String [] {"No State"})),
//		ShareExchange ("Share Exchange", Arrays.asList (new String [] {"Loan Repayment"})),
//		TokenExchange ("Token Exchange", Arrays.asList (new String [] {"Confirm Forming President"})),
//		AssetCollection ("Asset Collection", Arrays.asList (new String [] {"Token Exchange"})),
//		StockValueCalculation ("Stock Value Calculation", Arrays.asList (new String [] {"Asset Collection"})),

		if (aState.equals (ActionStates.LoanRepayment.toString ())) {
			tPlayerState = ActionStates.LoanRepayment;
		} else if (aState.equals (ActionStates.ShareExchange.toString ())) {
			tPlayerState = ActionStates.ShareExchange;
		} else if (aState.equals (ActionStates.TokenExchange.toString ())) {
			tPlayerState = ActionStates.TokenExchange;
		} else if (aState.equals (ActionStates.AssetCollection.toString ())) {
			tPlayerState = ActionStates.AssetCollection;
		} else if (aState.equals (ActionStates.StockValueCalculation.toString ())) {
			tPlayerState = ActionStates.StockValueCalculation;
		}
		
		return tPlayerState;
	}
		
	public ActorI.ActionStates getCorporationActionState (String aState) {
		ActorI.ActionStates tCorporationState;

		tCorporationState = ActionStates.NoState;
		
//		Unowned, Owned, Closed, MayFloat, WillFloat, NotOperated, StartedOperations, 
//		HandledLoanInterest,	 TileLaid, Tile2Laid, TileUpgraded, StationLaid, 
//		TileAndStationLaid, OperatedTrain,
//		HoldDividend, HalfDividend, FullDividend, BoughtTrain, Operated, 
//		Fixed, Inactive, WaitingResponse, Bankrupt, Recievership
		
		if (aState.equals (ActionStates.Unowned.toString ())) {						// 1
			tCorporationState = ActionStates.Unowned;
		} else if (aState.equals (ActionStates.Owned.toString ())) {					// 2
			tCorporationState = ActionStates.Owned;
		} else if (aState.equals (ActionStates.Closed.toString ())) {				// 3
			tCorporationState = ActionStates.Closed;
		} else if (aState.equals (ActionStates.MayFloat.toString ())) {				// 4
			tCorporationState = ActionStates.MayFloat;
		} else if (aState.equals (ActionStates.StartedOperations.toString ())) {		// 5
			tCorporationState = ActionStates.StartedOperations;
		} else if (aState.equals (ActionStates.Operated.toString ())) {				// 6
			tCorporationState = ActionStates.Operated;
		} else if (aState.equals (ActionStates.HandledLoanInterest.toString ())) {	// 7
			tCorporationState = ActionStates.HandledLoanInterest;
		} else if (aState.equals (ActionStates.NotOperated.toString ())) {			// 8
			tCorporationState = ActionStates.NotOperated;
		} else if (aState.equals (ActionStates.WillFloat.toString ())) {				// 9
			tCorporationState = ActionStates.WillFloat;
		} else if (aState.equals (ActionStates.TileLaid.toString ())) {				// 10
			tCorporationState = ActionStates.TileLaid;
		} else if (aState.equals (ActionStates.Tile2Laid.toString ())) {				// 11
			tCorporationState = ActionStates.Tile2Laid;
		} else if (aState.equals (ActionStates.TileUpgraded.toString ())) {			// 12
			tCorporationState = ActionStates.TileUpgraded;
		} else if (aState.equals (ActionStates.StationLaid.toString ())) {			// 13
			tCorporationState = ActionStates.StationLaid;
		} else if (aState.equals (ActionStates.TileAndStationLaid.toString ())) {		// 14
			tCorporationState = ActionStates.TileAndStationLaid;
		} else if (aState.equals (ActionStates.OperatedTrain.toString ())) {			// 15
			tCorporationState = ActionStates.OperatedTrain;
		} else if (aState.equals (ActionStates.HoldDividend.toString ())) {			// 16
			tCorporationState = ActionStates.HoldDividend;
		} else if (aState.equals (ActionStates.HalfDividend.toString ())) {			// 17
			tCorporationState = ActionStates.HalfDividend;
		} else if (aState.equals (ActionStates.FullDividend.toString ())) {			// 18
			tCorporationState = ActionStates.FullDividend;
		} else if (aState.equals (ActionStates.BoughtTrain.toString ())) {			// 19
			tCorporationState = ActionStates.BoughtTrain;
		} else if (aState.equals (ActionStates.WaitingResponse.toString ())) {		// 20
			tCorporationState = ActionStates.WaitingResponse;
		} else if (aState.equals (ActionStates.Unformed.toString ())) {				// 21
			tCorporationState = ActionStates.Unformed;
		} else if (aState.equals (ActionStates.Fixed.toString ())) {					// 22
			tCorporationState = ActionStates.Fixed;
		} else if (aState.equals (ActionStates.Inactive.toString ())) {				// 23
			tCorporationState = ActionStates.Inactive;
		} else if (aState.equals (ActionStates.WaitingResponse.toString ())) {		// 24
			tCorporationState = ActionStates.WaitingResponse;
		} else if (aState.equals (ActionStates.Bankrupt.toString ())) {				// 25
			tCorporationState = ActionStates.Bankrupt;
		} else if (aState.equals (ActionStates.Recievership.toString ())) {			// 26
			tCorporationState = ActionStates.Recievership;
		}

		return tCorporationState;
	}

//	NoRound, StockRound, OperatingRound, AuctionRound,				// Round States

	public ActionStates getRoundType (String aState) {
		ActionStates tRoundState;

		tRoundState = ActionStates.NoState;
		if (aState.equals (ActionStates.NoRound.toString ())) {
			tRoundState = ActionStates.NoRound;
		} else if (aState.equals (ActionStates.StockRound.toString ())) {
			tRoundState = ActionStates.StockRound;
		} else if (aState.equals (ActionStates.OperatingRound.toString ())) {
			tRoundState = ActionStates.OperatingRound;
		} else if (aState.equals (ActionStates.AuctionRound.toString ())) {
			tRoundState = ActionStates.AuctionRound;
		}

		return tRoundState;
	}

	public ActionStates getRT (String aState) {
		ActionStates tRoundState;
		ActionStates [] tRoundStates = { ActionStates.NoRound, ActionStates.StockRound, ActionStates.OperatingRound,
				ActionStates.AuctionRound };

		tRoundState = ActionStates.NoState;
		for (ActionStates tRoundState2 : tRoundStates) {
			if (tRoundState == ActionStates.NoState) {
				tRoundState = getMatchingActionState (aState, tRoundState2);
			}
		}

		return tRoundState;
	}

	public ActionStates getMatchingActionState (String aStateName, ActionStates aActionState) {
		ActionStates tActionState;

		tActionState = ActionStates.NoState;
		if (aStateName.equals (aActionState.toString ())) {
			tActionState = aActionState;
		}

		return tActionState;
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for the Generic Actor Class
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
}
