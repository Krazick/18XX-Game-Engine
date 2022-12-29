package ge18xx.company;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.OperatingRound;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.GetLoanAction;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayLoanInterestAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.round.action.RedeemLoanAction;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

//
//  ShareCompany.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class ShareCompany extends TokenCompany {
	public static final ElementName EN_SHARE_COMPANY = new ElementName ("ShareCompany");
	static final AttributeName AN_PAR_PRICE = new AttributeName ("parPrice");
	static final AttributeName AN_LOAN_COUNT = new AttributeName ("loanCount");
	static final AttributeName AN_LOAN_TAKEN = new AttributeName ("loanTaken");
	static final AttributeName AN_DESTINATION = new AttributeName ("destination");
	static final AttributeName AN_DESTINATION_LOCATION = new AttributeName ("destinationLocation");
	static final AttributeName AN_START_PRICE = new AttributeName ("startPrice");
	static final AttributeName AN_CAPITALIZATION_LEVEL = new AttributeName ("capitalizationLevel");
	static final String NO_START_CELL = null;
	public static final int NO_PAR_PRICE = -1;
	public static ShareCompany NO_SHARE_COMPANY = null;
	static final int NO_LOANS = 0;
	MarketCell sharePrice;
	MapCell destination;
	Location destinationLocation;
	String destinationLabel;
	String startCell;
	int escrowForPayment;
	int parPrice;
	int loanCount;
	int capitalizationLevel;
	boolean loanTaken;	// Flag set to TRUE if a Loan was taken this OR (limit 1 loan per OR)

	public ShareCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		int tDestLocation;
		int tParPrice;
		int tLoanCount;
		int tCapitalizationLevel;
		boolean tLoanTaken;
		String tStartCell;

		destinationLabel = aChildNode.getThisAttribute (AN_DESTINATION);
		tDestLocation = aChildNode.getThisIntAttribute (AN_DESTINATION_LOCATION, Location.NO_LOCATION);
		destinationLocation = new Location (tDestLocation);
		tStartCell = aChildNode.getThisAttribute (AN_START_PRICE, NO_START_CELL);
		tParPrice = aChildNode.getThisIntAttribute (AN_PAR_PRICE, NO_PAR_PRICE);
		tLoanCount = aChildNode.getThisIntAttribute (AN_LOAN_COUNT, NO_LOANS);
		tLoanTaken = aChildNode.getThisBooleanAttribute (AN_LOAN_TAKEN);
		tCapitalizationLevel = aChildNode.getThisIntAttribute (AN_CAPITALIZATION_LEVEL);
		setNoPrice ();
		setValues (tParPrice, MarketCell.NO_SHARE_PRICE, MapCell.NO_DESTINATION, tLoanCount, tLoanTaken, tStartCell, tCapitalizationLevel);
	}

	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getDestinationLabel (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getDestinationLocationInt (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getSParPrice (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getSharePrice (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getLoanCount (), aRowIndex, tCurrentColumn++);

		return tCurrentColumn;
	}

	@Override
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Destination", tCurrentColumn++);
		aCorporationList.addHeader ("Dest. Loc", tCurrentColumn++);
		aCorporationList.addHeader ("Par Price", tCurrentColumn++);
		aCorporationList.addHeader ("Share Price", tCurrentColumn++);
		aCorporationList.addHeader ("Loan Count", tCurrentColumn++);

		return tCurrentColumn;
	}

	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener) {
		JPanel tPrivatesJPanel;

		tPrivatesJPanel = corporationList.buildPrivatesForPurchaseJPanel (aItemListener, getCash ());

		return tPrivatesJPanel;
	}

	// Buy the Private Corporation into the Share Company
	@Override
	public void buyPrivate (boolean tVisible) {
		PrivateCompany tPrivateToBuy;
		CertificateHolderI tCertificateHolder;
		PortfolioHolderI tPrivateOwner;
		Certificate tPresidentCertificate;
		BuyPrivateFrame tBuyPrivateFrame;

		tPrivateToBuy = getSelectedPrivateToBuy ();
		tPresidentCertificate = getPresidentCertificate (tPrivateToBuy);
		tCertificateHolder = tPresidentCertificate.getOwner ();
		tPrivateOwner = tCertificateHolder.getPortfolioHolder ();
		tBuyPrivateFrame = new BuyPrivateFrame (this, tPrivateOwner, tPresidentCertificate);
		tBuyPrivateFrame.requestFocus ();
	}

	@Override
	public PrivateCompany getSelectedPrivateToBuy () {
		return (corporationList.getSelectedPrivateCompanyToBuy ());
	}

	public Certificate getPresidentCertificate (Corporation aCorporation) {
		return aCorporation.getPresidentCertificate ();
	}

	@Override
	public boolean canBuyPrivate () {
		return corporationList.canBuyPrivate ();
	}

	@Override
	public String reasonForNoBuyPrivate () {
		return "Cannot buy Private in current Phase";
	}

	public boolean countsAgainstCertificateLimit () {
		boolean tCounts;

		tCounts = true;
		if (sharePrice != MarketCell.NO_MARKET_CELL) {
			tCounts = sharePrice.countsAgainstCertificateLimit ();
		}

		return tCounts;
	}

	@Override
	public int fieldCount () {
		return super.fieldCount () + 5;
	}

	/* Build XML Element of Current Share Company State -- For Saving */
	@Override
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;

		tXMLCorporationState = aXMLDocument.createElement (EN_SHARE_COMPANY);
		getCorporationStateElement (tXMLCorporationState);
		super.appendOtherElements (tXMLCorporationState, aXMLDocument);

		return tXMLCorporationState;
	}

	@Override
	public int getCountOfSelectedPrivates () {
		return corporationList.getCountOfSelectedPrivates ();
	}

	@Override
	public ElementName getElementName () {
		return EN_SHARE_COMPANY;
	}

	/*
	 * Fill In the XML Element with Par Price, and Loan Count, Loan Taken, and call super's
	 * routine
	 */
	@Override
	public void getCorporationStateElement (XMLElement aXMLCorporationState) {
		aXMLCorporationState.setAttribute (AN_PAR_PRICE, getParPrice ());
		if (gameHasLoans ()) {
			aXMLCorporationState.setAttribute (AN_LOAN_COUNT, loanCount);
			aXMLCorporationState.setAttribute (AN_LOAN_TAKEN, loanTaken);
		}
		aXMLCorporationState.setAttribute (AN_CAPITALIZATION_LEVEL, getParPrice ());
		super.getCorporationStateElement (aXMLCorporationState);
	}

	public MapCell getDestination () {
		return destination;
	}
	
	public String getDestinationLabel () {
		return destinationLabel;
	}

	public int getDestinationLocationInt () {
		if (destinationLocation == Location.NO_DESTINATION_LOCATION) {
			return NO_NAME_INT;
		} else {
			return destinationLocation.getLocation ();
		}
	}

	public Location getDestinationLocation () {
		return destinationLocation;
	}

	/**
	 * Flag if the Corporation has taken a loan in this Operating Round.
	 *
	 */
	@Override
	public boolean wasLoanTaken () {
		return loanTaken;
	}

	/**
	 * Based upon the corporation state, determine if the Loan Interest has been handled. Needed to determine loan amount for new Loans
	 *
	 * @return TRUE if we have moved beyond the Loan Interest Handling State
	 */
	@Override
	public boolean loanInterestHandled () {
		boolean tLoanInterestHandled;

		tLoanInterestHandled = false;
		if (dividendsHandled () ||
			(status == ActorI.ActionStates.HandledLoanInterest) ||
			(status == ActorI.ActionStates.BoughtTrain) ||
			(status == ActorI.ActionStates.Operated)) {
			tLoanInterestHandled = true;
		}

		return tLoanInterestHandled;
	}

	/**
	 * Determines if there are any outstanding Loans
	 *
	 * @return True if there are any outstanding loans (Loan Count > 0)
	 *
	 */
	@Override
	public boolean hasOutstandingLoans () {
		return (loanCount > 0);
	}

	/**
	 * Return the Count of the Loans the company has outstanding
	 *
	 * @return Total number of Loans that are outstanding
	 */
	@Override
	public int getLoanCount () {
		return loanCount;
	}

	/**
	 * Handle the Process of getting a Loan for this Company, add one to the LoanCount,
	 * transfer the cash from the bank to the company treasury, create the action to document this,
	 * and add the action.
	 *
	 */
	@Override
	public void getLoan () {
		boolean tNewLoanTaken;
		int tNewLoanCount;
		int tLoanAmount;
		Bank tBank;
		GetLoanAction tGetLoanAction;
		OperatingRound tOperatingRound;

		tNewLoanCount = loanCount + 1;
		tBank = corporationList.getBank ();
		if (loanInterestHandled ()) {
			tLoanAmount = loanAmount - loanInterest;
		} else {
			tLoanAmount = loanAmount;
		}

		tNewLoanTaken = true;
		tOperatingRound = corporationList.getOperatingRound ();
		tGetLoanAction = new GetLoanAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
		tGetLoanAction.addGetLoanEffect (this, loanTaken, tNewLoanTaken);
		tGetLoanAction.addUpdateLoanCountEffect (this, loanCount, tNewLoanCount);
		tGetLoanAction.addCashTransferEffect (tBank, this, tLoanAmount);

		tBank.transferCashTo (this, tLoanAmount);
		setLoanCount (tNewLoanCount);
		setLoanTaken (tNewLoanTaken);
		corporationList.addAction (tGetLoanAction);
	}

	/**
	 * Base method to handle Interest Payment for a Loan for the Company.
	 *
	 */
	@Override
	public void payLoanInterest () {
		ActorI.ActionStates tOldState;
		int tLoanCount;
		int tInterestPayment;
		int tTreasuryContribution;
		int tRevenueContribution;
		int tRevenueReducedTo;
		Bank tBank;
		LoanInterestCoupon tInterestCoupon;

		tOldState = getStatus ();
		tLoanCount = getLoanCount ();
		tInterestPayment = tLoanCount * getLoanInterest ();
		tBank = corporationList.getBank ();
		if (tInterestPayment <= getCash ()) {
			completeLoanInterestPayment (tOldState, tInterestPayment, 0, tBank);
		} else {
			// TODO -- 
			// If not enough Cash in Treasury to pay Interest on Loans -- we get here
			tTreasuryContribution = calculateTreasuryContribution ();
			tRevenueContribution = calculateRevenueContribution (tInterestPayment);
			tRevenueReducedTo = getThisRevenue () - tRevenueContribution;
			setThisRevenue (tRevenueReducedTo);
			
			if (tInterestPayment <= (tTreasuryContribution + tRevenueContribution)) {
				System.err.println ("Need " + Bank.formatCash (tInterestPayment) + " needed to pay Loan Interest Payment on " +
									tLoanCount + " Loans.");
				tInterestCoupon = new LoanInterestCoupon (tInterestPayment);
				forceBuyCoupon (tInterestCoupon);
			} else {
				completeLoanInterestPayment (tOldState, tInterestPayment, tRevenueContribution, tBank);
			}
		}
	}

	private void completeLoanInterestPayment (ActorI.ActionStates aOldState, int aCompanyPayment, int aRevenuePayment, Bank aBank) {
		ActorI.ActionStates tNewState;
		PayLoanInterestAction tPayLoanInterestAction;
		OperatingRound tOperatingRound;
		
		if (updateStatus (ActorI.ActionStates.HandledLoanInterest)) {
			tNewState = getStatus ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayLoanInterestAction = new PayLoanInterestAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
			tPayLoanInterestAction.addCashTransferEffect (this, aBank, aCompanyPayment);
			tPayLoanInterestAction.addChangeCorporationStatusEffect (this, aOldState, tNewState);
			if (aRevenuePayment > 0) {
				tPayLoanInterestAction.addReduceRevenueEffect (this, aRevenuePayment);
			}
			transferCashTo (aBank, aCompanyPayment);
			addAction (tPayLoanInterestAction);
		}
	}

	private int calculateTreasuryContribution () {
		int tTreasuryContribution;
		
		tTreasuryContribution = ((int) (getCash ()/10) * 10);
		
		return tTreasuryContribution;
	}
	
	// Before we Force By the Coupon (using President Cash and Sales)
	// Examine the Revenue Earned, and deduct $10 increments into "Escrow for Interest Payment" until Revenue is Zero or 
	// (Treasury + Escrow) is equal to Interest Owed. Complete Payment.
	// If (Treasury + Escrow) is less than Interest Owed, then Force Buy Coupon for Interest.
	private int calculateRevenueContribution (int aInterestPayment) {
		int tTreasuryContribution;
		int tRevenueContribution;
		int tStillOwe;
		int tRevenueEarned;
		
		escrowForPayment = 0;
		tTreasuryContribution = calculateTreasuryContribution ();
		tStillOwe = aInterestPayment - tTreasuryContribution;
		tRevenueEarned = getThisRevenue ();
		if (tRevenueEarned >= tStillOwe) {
			tRevenueContribution = tStillOwe;
		} else {
			tRevenueContribution = tRevenueEarned;
		}
		
		System.out.println ("Treasury " + getCash () + " Contribution amount " + tTreasuryContribution);
		System.out.println ("Still Owed after Treasury " + tStillOwe);
		System.out.println ("Revenue Earned " + tRevenueEarned + " Revenue Contribution " + tRevenueContribution);
		
		return tRevenueContribution;
	}
	
	private void forceBuyCoupon (Coupon aForceBuyCoupon) {
		ForceBuyCouponFrame tForceBuyCouponFrame;

		tForceBuyCouponFrame = new ForceBuyCouponFrame (this, aForceBuyCoupon);
		setForceBuyCouponFrame (tForceBuyCouponFrame);
		forceBuyCouponFrame.updateMainJPanel ();
		forceBuyCouponFrame.showFrame ();
	}
	
	/**
	 * Handle the Process of Redeeming (paying back) a Loan for this Company, deduct one to the LoanCount,
	 * transfer the cash from the company treasury to the bank, create the action to document this,
	 * and add the action. If a Company does not have enough cash, need to expand this get this from
	 * the company President, and possible Force Stock Sale to raise the Cash.
	 *
	 */
	@Override
	public void redeemLoan () {

		if (loanCount < 1) {
			System.err.println ("There are no Loans to Redeem");
		} else {
			redeemLoans (1);
		}
	}

	@Override
	public int getLoanAmount () {
		return loanAmount;
	}
	
	public void redeemLoans (int aLoanRedemptionCount) {
		int tNewLoanCount;
		int tLoanRedemptionAmount;
		RedeemLoanAction tRedeemLoanAction;
		Bank tBank;
		OperatingRound tOperatingRound;
		LoanRedemptionCoupon tRedemptionCoupon;

		if (loanCount < aLoanRedemptionCount) {
			System.err.println ("Asked to repay " + aLoanRedemptionCount + " however the company only has " + 
								loanCount + " outstanding loans.");
		} else {
			tNewLoanCount = loanCount - aLoanRedemptionCount;
			tLoanRedemptionAmount = loanAmount * aLoanRedemptionCount;

			if (getCash () >= tLoanRedemptionAmount) {
				tBank = corporationList.getBank ();
				tOperatingRound = corporationList.getOperatingRound ();
				tRedeemLoanAction = new RedeemLoanAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
				tRedeemLoanAction.addRedeemLoanEffect (this);
				tRedeemLoanAction.addUpdateLoanCountEffect (this, loanCount, tNewLoanCount);
				tRedeemLoanAction.addCashTransferEffect (this, tBank, tLoanRedemptionAmount);

				transferCashTo (tBank, tLoanRedemptionAmount);
				setLoanCount (tNewLoanCount);
				corporationList.addAction (tRedeemLoanAction);

				setLoanCount (tNewLoanCount);
			} else {
				System.err.println ("Asked to replay " + Bank.formatCash (tLoanRedemptionAmount) +
						" however, the company only has " + Bank.formatCash (getCash ()) + " available.");
				// TODO: Add in Emergency Fund Raising, from President, and possible Forced Stock Sale
				tRedemptionCoupon = new LoanRedemptionCoupon (tLoanRedemptionAmount);
				forceBuyCoupon (tRedemptionCoupon);
			}
		}
	}

	public int getParPrice () {
		return parPrice;
	}

	public String getSParPrice () {
		if (parPrice == NO_PAR_PRICE) {
			return "NO PAR";
		} else {
			return String.valueOf (parPrice);
		}
	}

	@Override
	public int getSharePrice () {
		if (sharePrice == MarketCell.NO_MARKET_CELL) {
			return MarketCell.NO_STOCK_PRICE;
		} else {
			return sharePrice.getValue ();
		}
	}

	public MarketCell getSharePriceMarketCell () {
		return sharePrice;
	}

	public boolean canBuyMultiple () {
		boolean tCanBuyMultiple;

		if (sharePrice == MarketCell.NO_MARKET_CELL) {
			tCanBuyMultiple = false;
		} else {
			tCanBuyMultiple = sharePrice.canBuyMultiple ();
		}

		return tCanBuyMultiple;
	}

	public int getStartCol () {
		String [] tSplit = null;
		int tCol;

		tCol = 0;
		if (startCell != NO_START_CELL) {
			tSplit = startCell.split (",");
			tCol = Integer.parseInt (tSplit [1]);
		}

		return tCol;
	}

	public int getStartRow () {
		String [] tSplit = null;
		int tRow;

		tRow = 0;
		if (startCell != NO_START_CELL) {
			tSplit = startCell.split (",");
			tRow = Integer.parseInt (tSplit [0]);
		}

		return tRow;
	}

	@Override
	public String getStatusName () {
		String tStatus;

		tStatus = super.getStatusName ();
		if (tStatus.equals (ActorI.ActionStates.Owned.toString ())) {
			if (hasFloated ()) {
				tStatus = ActorI.ActionStates.NotOperated.toString ();
			}
		}

		return tStatus;
	}

	@Override
	public String getType () {
		return SHARE_COMPANY;
	}

	@Override
	public boolean canOperate () {
		boolean tCanOperate = true;

		if ((status == ActorI.ActionStates.Unowned) ||
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.Closed)) {
			tCanOperate = false;
		}

		return tCanOperate;
	}

	public boolean willFloat () {
		return (status == ActorI.ActionStates.WillFloat);
	}

	public boolean hasFloated () {
		boolean tHasFloated;

		if ((status == ActorI.ActionStates.Unowned) ||
			(status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.MayFloat) ||
			(status == ActorI.ActionStates.WillFloat)) {
			tHasFloated = false;
		} else {
			tHasFloated = true;
		}

		return (tHasFloated);
	}

	public boolean hasParPrice () {
		return (parPrice != NO_PAR_PRICE);
	}

	public boolean hasStartCell () {
		return (startCell != NO_START_CELL);
	}

	public boolean isOperational () {
		boolean tOperational;

		tOperational = hasFloated ();
		if (isClosed ()) {
			tOperational = false;
		}

		return tOperational;
	}

	@Override
	public void loadStatus (XMLNode aXMLNode) {
		super.loadStatus (aXMLNode);
		setParPrice (aXMLNode.getThisIntAttribute (AN_PAR_PRICE));
		if (gameHasLoans ()) {
			setLoanCount (aXMLNode.getThisIntAttribute (AN_LOAN_COUNT));
			setLoanTaken (aXMLNode.getThisBooleanAttribute (AN_LOAN_TAKEN));
		}
	}

	public void payNoDividendAdjustment (PayNoDividendAction aPayNoDividendAction) {
		sharePrice.doPayNoDividendAdjustment (this, aPayNoDividendAction);
	}

	public void payFullDividendAdjustment (PayFullDividendAction aPayFullDividendAction) {
		sharePrice.doPayFullDividendAdjustment (this, aPayFullDividendAction);
	}

	public void setDestination (MapCell aDestinationCity, Location aDestinationLocation) {
		setDestinationMapCell (aDestinationCity);
		destinationLocation = aDestinationLocation;
	}

	public void setDestinationMapCell (MapCell aDestinationCity) {
		destination = aDestinationCity;
	}
	
	public void setLoanCount (int aLoanCount) {
		loanCount = aLoanCount;
	}

	public void setLoanTaken (boolean aLoanTaken) {
		loanTaken = aLoanTaken;
	}

	public boolean loanTaken () {
		return loanTaken;
	}

	public void setNoPrice () {
		setParPrice (NO_PAR_PRICE);
		setSharePrice (MarketCell.NO_SHARE_PRICE);
	}

	public void setParPrice (int aParPrice) {
		int tRowIndex;

		parPrice = aParPrice;
		if (parPrice != NO_PAR_PRICE) {
			tRowIndex = corporationList.getRowIndex (this);
			corporationList.addDataElement (parPrice, tRowIndex, 17);
		}
	}

	public void setSharePrice (MarketCell aSharePrice) {
		int tRowIndex;

		sharePrice = aSharePrice;
		if (aSharePrice != MarketCell.NO_SHARE_PRICE) {
			tRowIndex = corporationList.getRowIndex (this);
			corporationList.addDataElement (sharePrice.getValue (), tRowIndex, 18);
		}
	}

	public void setStartCell (Market aMarket) {
		MarketCell tMarketCell;
		int tParPrice;

		if (startCell != NO_START_CELL) {
			if (aMarket != Market.NO_MARKET) {
				tMarketCell = getMarketCellAt (aMarket);
				if (tMarketCell != MarketCell.NO_MARKET_CELL) {
					tParPrice = tMarketCell.getValue ();
					setSharePrice (tMarketCell);
					setParPrice (tParPrice);
				}
			}
		}
	}

	public MarketCell getMarketCellAt (Market aMarket) {
		int tRow;
		int tCol;
		MarketCell tMarketCell;

		tRow = getStartRow ();
		tCol = getStartCol ();
		tMarketCell = aMarket.getMarketCellAtRowCol (tRow, tCol);

		return tMarketCell;
	}

	private void setValues (MapCell aDestination, int aLoanCount, boolean aLoanTaken, String aStartCell) {
		setDestination (aDestination, Location.NO_DESTINATION_LOCATION);
		setLoanCount (aLoanCount);
		setLoanTaken (aLoanTaken);
		startCell = aStartCell;
	}

	private void setValues (int aParPrice, MarketCell aSharePrice, MapCell aDestination, int aLoanCount,
			boolean aLoanTaken, String aStartCell, int aCapitalizationLevel) {
		setSharePrice (aSharePrice);
		setParPrice (aParPrice);
		setCapitalizationLevel (aCapitalizationLevel);
		setValues (aDestination, aLoanCount, aLoanTaken, aStartCell);
	}

	public int getCapitalizationLevel () {
		return capitalizationLevel;
	}
	
	public void setCapitalizationLevel (int aCapitalizationLevel) {
		capitalizationLevel = aCapitalizationLevel;
	}
	
	public void setCapitalizationLevel () {
		int tCapitalizationLevel;
		
		tCapitalizationLevel = getGameCapitalizationLevel ();
		setCapitalizationLevel (tCapitalizationLevel);
	}
	
	@Override
	public int calculateStartingTreasury () {
		int tStartingTreasury;

		tStartingTreasury = capitalizationLevel * getParPrice ();

		return tStartingTreasury;
	}

	@Override
	public int getGameCapitalizationLevel (int aSharesSold) {
		return getGameCapitalizationLevel ();
	}

	private int getGameCapitalizationLevel () {
		int tCapitalizationAmount;
		int tSharesSold;

		tSharesSold = getSharesSold ();
		tCapitalizationAmount = super.getGameCapitalizationLevel (tSharesSold);
//		if (corporationList.doIncrementalCapitalization ()) {
//			System.out.println ("Should do Incremental Capitalization for " + abbrev);
//			// NOTE -- 1856 in early Phases, will do Incremental Capitalization based on Shares Sold
//		}

		return tCapitalizationAmount;
	}
	
	private int getSharesSold () {
		int tSharesSold;

		tSharesSold = getPlayerOrCorpOwnedPercentage () / 10;

		return tSharesSold;
	}

	public boolean shouldFloat () {
		boolean tShouldFloat;
		int tMinSharesToFloat;
		int tSharesSold;

		if (status == ActorI.ActionStates.WillFloat) {
			tShouldFloat = true;
		} else if (status == ActorI.ActionStates.MayFloat) {
			tMinSharesToFloat = getMinSharesToFloat ();
			tSharesSold = getSharesSold ();
			if (tSharesSold >= tMinSharesToFloat) {
				tShouldFloat = true;
			} else {
				setStatus (ActorI.ActionStates.Owned);
				corporationList.updateRoundFrame ();
				tShouldFloat = false;
			}
		} else {
			tShouldFloat = false;
		}

		return tShouldFloat;
	}

	public void handleRejectOfferPrivate () {
		corporationList.clearPrivateSelections ();
		updateInfo ();
	}

	public void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio, Certificate aCertificate,
			BuyStockAction aBuyStockAction) {
		ActorI.ActionStates tCurrentCorporationStatus, tNewCorporationStatus;
		PortfolioHolderI tFromHolder, tToHolder;

		tFromHolder = aFromPortfolio.getHolder ();
		tToHolder = aToPortfolio.getHolder ();
		aToPortfolio.transferOneCertificateOwnership (aFromPortfolio, aCertificate);
		aBuyStockAction.addTransferOwnershipEffect (tFromHolder, aCertificate, tToHolder);
		tCurrentCorporationStatus = aCertificate.getCorporationStatus ();

		// TODO: If buying Private into a Share Company, we don't need to change the Corporation State

		aCertificate.updateCorporationOwnership ();
		tNewCorporationStatus = aCertificate.getCorporationStatus ();
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyStockAction.addChangeCorporationStatusEffect (aCertificate.getCorporation (), tCurrentCorporationStatus,
					tNewCorporationStatus);
		}
	}

	@Override
	public boolean isAShareCompany () {
		return true;
	}

	@Override
	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		return null;
	}

	@Override
	public String buildCorpInfoLabel () {
		String tCorpLabel;
		String tLoanInfo;
		
		if (gameHasLoans ()) {
			tLoanInfo = "Loan Count: " + loanCount;
		} else {
			tLoanInfo = GUI.NULL_STRING;
		}
		tCorpLabel = super.buildCorpInfoLabel (tLoanInfo);
		
		return tCorpLabel;
	}
	
	@Override
	public int getCurrentValue () {
		return getSharePrice ();
	}
}
