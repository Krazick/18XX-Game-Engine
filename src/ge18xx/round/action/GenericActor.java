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

		return tState;
	}

	// NoAction, Pass, Acted, Bought, Sold, // Player Primary States
	// BoughtDone, BoughtSold, SoldDone, BoughtSoldDone, Bid, BidDone // Player
	// Alternate States

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
		}

		return tPlayerState;
	}

//	Unowned, Owned, Closed, MayFloat, WillFloat, NotOperated, StartedOperations		// Corporation States
//	TileLaid, Tile2Laid, TileUpgraded, StationLaid, TileAndStationLaid, OperatedTrain, 
//	HoldDividend, HalfDividend, FullDividend, BoughtTrain, Operated,

	public ActorI.ActionStates getCorporationActionState (String aState) {
		ActorI.ActionStates tCorporationState;

		tCorporationState = ActionStates.NoState;
		if (aState.equals (ActionStates.Unowned.toString ())) {
			tCorporationState = ActionStates.Unowned;
		} else if (aState.equals (ActionStates.Owned.toString ())) {
			tCorporationState = ActionStates.Owned;
		} else if (aState.equals (ActionStates.Closed.toString ())) {
			tCorporationState = ActionStates.Closed;
		} else if (aState.equals (ActionStates.MayFloat.toString ())) {
			tCorporationState = ActionStates.MayFloat;
		} else if (aState.equals (ActionStates.StartedOperations.toString ())) {
			tCorporationState = ActionStates.StartedOperations;
		} else if (aState.equals (ActionStates.Operated.toString ())) {
			tCorporationState = ActionStates.Operated;
		} else if (aState.equals (ActionStates.NotOperated.toString ())) {
			tCorporationState = ActionStates.NotOperated;
		} else if (aState.equals (ActionStates.WillFloat.toString ())) {
			tCorporationState = ActionStates.WillFloat;
		} else if (aState.equals (ActionStates.TileLaid.toString ())) {
			tCorporationState = ActionStates.TileLaid;
		} else if (aState.equals (ActionStates.Tile2Laid.toString ())) {
			tCorporationState = ActionStates.Tile2Laid;
		} else if (aState.equals (ActionStates.TileUpgraded.toString ())) {
			tCorporationState = ActionStates.TileUpgraded;
		} else if (aState.equals (ActionStates.StationLaid.toString ())) {
			tCorporationState = ActionStates.StationLaid;
		} else if (aState.equals (ActionStates.TileAndStationLaid.toString ())) {
			tCorporationState = ActionStates.TileAndStationLaid;
		} else if (aState.equals (ActionStates.OperatedTrain.toString ())) {
			tCorporationState = ActionStates.OperatedTrain;
		} else if (aState.equals (ActionStates.HoldDividend.toString ())) {
			tCorporationState = ActionStates.HoldDividend;
		} else if (aState.equals (ActionStates.HalfDividend.toString ())) {
			tCorporationState = ActionStates.HalfDividend;
		} else if (aState.equals (ActionStates.FullDividend.toString ())) {
			tCorporationState = ActionStates.FullDividend;
		} else if (aState.equals (ActionStates.BoughtTrain.toString ())) {
			tCorporationState = ActionStates.BoughtTrain;
		} else if (aState.equals (ActionStates.WaitingResponse.toString ())) {
			tCorporationState = ActionStates.WaitingResponse;
		} else if (aState.equals (ActionStates.Unformed.toString ())) {
			tCorporationState = ActionStates.Unformed;
		} else if (aState.equals (ActionStates.Inactive.toString ())) {
			tCorporationState = ActionStates.Inactive;
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
		for (int tIndex = 0; tIndex < tRoundStates.length; tIndex++) {
			if (tRoundState == ActionStates.NoState) {
				tRoundState = getMatchingActionState (aState, tRoundStates [tIndex]);
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
	public boolean isAPrivateCompany () {
		return false;
	}

	@Override
	public boolean isAPlayer () {
		return false;
	}

	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	@Override
	public boolean isABank () {
		return false;
	}

	@Override
	public boolean isABankPool () {
		return false;
	}

	@Override
	public boolean isACorporation () {
		return false;
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for the Generic Actor Class
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}

	@Override
	public boolean isATrainCompany () {
		return false;
	}

	@Override
	public boolean isAShareCompany () {
		return false;
	}

	@Override
	public void completeBenefitInUse () {
	}
}
