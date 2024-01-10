package ge18xx.company;

import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.center.Centers;
import ge18xx.game.Capitalization;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.Vertex;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.OperatingRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.CloseCompanyAction;
import ge18xx.round.action.GetLoanAction;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayLoanInterestAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.round.action.ReachedDestinationAction;
import ge18xx.round.action.RedeemLoanAction;
import ge18xx.round.action.RemoveDestinationsAction;
import ge18xx.round.action.StockValueCalculationAction;
import ge18xx.tiles.Tile;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;
import swingDelays.KButton;

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
	static final AttributeName AN_START_PRICE = new AttributeName ("startPrice");
	static final AttributeName AN_DESTINATION = new AttributeName ("destination");
	static final AttributeName AN_DESTINATION_LOCATION = new AttributeName ("destinationLocation");
	static final AttributeName AN_CAPITALIZATION_LEVEL = new AttributeName ("capitalizationLevel");
	static final AttributeName AN_REPAYMENT_HANDLED = new AttributeName ("repaymentHandled");
	public static final String NO_START_CELL = null;
	public static final String SET_PAR_PRICE = "SET PAR PRICE";
	public static final int NO_PAR_PRICE = -1;
	public static ShareCompany NO_SHARE_COMPANY = null;
	public static final int NO_LOANS = 0;
	DestinationInfo destinationInfo;
	MarketCell sharePrice;
	String startCell;
	int parPrice;
	int loanCount;
	int parPriceColumn;
	int sharePriceColumn;
	boolean mustBuyCoupon;
	boolean loanTaken;	// Flag set to TRUE if a Loan was taken this OR (limit 1 loan per OR)
	boolean repaymentHandled;
	List<KButton> specialButtons;
	
	public ShareCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		int tParPrice;
		int tLoanCount;
		boolean tLoanTaken;
		boolean tRepaymentHandled;
		String tStartCell;

		destinationInfo = new DestinationInfo (aChildNode);
		specialButtons = new LinkedList<KButton> ();
		tStartCell = aChildNode.getThisAttribute (AN_START_PRICE, NO_START_CELL);
		tParPrice = aChildNode.getThisIntAttribute (AN_PAR_PRICE, NO_PAR_PRICE);
		tLoanCount = aChildNode.getThisIntAttribute (AN_LOAN_COUNT, NO_LOANS);
		tLoanTaken = aChildNode.getThisBooleanAttribute (AN_LOAN_TAKEN);
		tRepaymentHandled = aChildNode.getThisBooleanAttribute (AN_REPAYMENT_HANDLED);
		setNoPrice ();
		setValues (tParPrice, MarketCell.NO_SHARE_PRICE, tLoanCount, tLoanTaken, 
					tRepaymentHandled, tStartCell);
		
	}

	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getDestinationLabel (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getDestinationLocationInt (), aRowIndex, tCurrentColumn++);
		parPriceColumn = tCurrentColumn;
		aCorporationList.addDataElement (getSParPrice (), aRowIndex, tCurrentColumn++);
		sharePriceColumn = tCurrentColumn;
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
	
	@Override
	public boolean hasDestination () {
		boolean tHasDestination;
		
		tHasDestination = false;
		if (destinationInfo != DestinationInfo.NO_DESTINATION_INFO) {
			tHasDestination = destinationInfo.hasDestination ();
		}
		
		return tHasDestination;
	}

	@Override
	public String getDestinationCityName () {
		String tDestinationCityName;
		
		tDestinationCityName = destinationInfo.getCityName ();
		
		return tDestinationCityName;
	}
	
	@Override
	public void checkForDestinationReached (HexMap aHexMap) {
		String tDestinationMapCellID;
		String tHomeVertexID;
		Location tLocation;
		MapCell tHomeMapCell;
		MapCell tDestinationMapCell;
		boolean tContainsHomeMapCell;
		boolean tContainsDestinationMapCell;
		boolean tFoundInBFS;
		int tCorpID;
		
		tHomeMapCell = getHomeCity1 ();
		tCorpID = getID ();
		tLocation = tHomeMapCell.getLocationWithStation (tCorpID);
		tHomeVertexID = Vertex.buildID (tHomeMapCell, tLocation);
		
		if (tHomeVertexID != Vertex.NO_VERTEX_ID) {
			tDestinationMapCell = destinationInfo.getMapCell ();
			
			tDestinationMapCellID = destinationInfo.getMapCellID ();
			if (! destinationInfo.hasReached ()) {
				tContainsHomeMapCell = aHexMap.graphContainsMapCell (tHomeMapCell);
				tContainsDestinationMapCell = aHexMap.graphContainsMapCell (tDestinationMapCell);
				if (tContainsHomeMapCell && tContainsDestinationMapCell) {
					aHexMap.breadthFirstSearch (tHomeVertexID);
					tFoundInBFS = aHexMap.foundInBFS (tDestinationMapCellID);
					if (tFoundInBFS) {
						handleReachedDestination ();
					}
				}
			}
		}
	}

	public void handleReachedDestination () {
		ReachedDestinationAction tReachedDestinationAction;
		OperatingRound tOperatingRound;
		boolean tReachedDestination;
		int tEscrowToRelease;
		int tOldCapitalizationLevel;
		int tNewCapitalizationLevel;
		Bank tBank;

		tBank = corporationList.getBank ();	
		tReachedDestination = true;
		tOldCapitalizationLevel = destinationInfo.getCapitalizationLevel ();
		setReachedDestination (tReachedDestination);
		tNewCapitalizationLevel = Capitalization.INCREMENTAL_10_MAX;
		setDestinationCapitalizationLevel (tNewCapitalizationLevel);
		tOperatingRound = corporationList.getOperatingRound ();
		tReachedDestinationAction = new ReachedDestinationAction (tOperatingRound.getRoundType (), 
																tOperatingRound.getID (), this);
		tReachedDestinationAction.setChainToPrevious (true);
		tReachedDestinationAction.addReachedDestinationEffect (this, tReachedDestination, 
				tOldCapitalizationLevel, tNewCapitalizationLevel);
		
		tEscrowToRelease = calculateEscrowWithheld ();
		tBank.transferCashTo (this, tEscrowToRelease);

		tReachedDestinationAction.addCashTransferEffect (tBank, this, tEscrowToRelease);
		corporationList.addAction (tReachedDestinationAction);
	}

	@Override
	public int calculateEscrowWithheld () {
		int tEscrowCalculated;
		
		tEscrowCalculated = destinationInfo.calculateEscrowWithheld (this);
		
		return tEscrowCalculated;
	}
	
	public void setReachedDestination (boolean aReached) {
		destinationInfo.setReached (aReached);
	}
	
	@Override
	public boolean hasReachedDestination () {
		boolean tHasReachedDestination;
		
		if (hasDestination ()) {
			tHasReachedDestination = destinationInfo.hasReached ();
		} else {
			tHasReachedDestination = true;
		}
		
		return tHasReachedDestination;
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
	public void setMustBuyCoupon (boolean aMustBuyCoupon) {
		mustBuyCoupon = aMustBuyCoupon;
	}
	
	@Override
	public boolean mustBuyCoupon () {
		return mustBuyCoupon;
	}
	
	@Override
	public boolean canBuyPrivate () {
		return corporationList.canBuyPrivate ();
	}

	@Override
	public int getAllowedTileLays () {
		int tAllowedTileLays;
		
		tAllowedTileLays = corporationList.getMajorTileLays ();
		
		return tAllowedTileLays;
	}
	
	@Override
	public String reasonForNoBuyPrivate () {
		return "Cannot buy Private in current Phase";
	}

	public void handleCloseCorporation () {
		MarketCell tMarketCell;
		GameManager tGameManager;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		CloseCompanyAction tCloseCompanyAction;
		HexMap tHexMap;
		
		tMarketCell = getSharePriceMarketCell ();
		if (tMarketCell.isClosed ()) {
			System.out.println ("Share Company " + getAbbrev () + " moved into a Closed MarketCell -- CLOSING");
			// Remove all Tokens from for this company from the Map
			// Remove Home Base Corporation Info from the Map
			// Remove the Market Token for this company
			// Transfer all Cash to the Bank
			// Transfer all Trains to the BankPool
			// Close any Private Corporations owned by the Company (Licenses owned by other Corps still active)
			// Add Action that has all the Effects required
			
			tGameManager = getGameManager ();
			tRoundManager = tGameManager.getRoundManager ();
			tRoundType = tRoundManager.getCurrentRoundType ();
			tRoundID = tRoundManager.getCurrentRoundOf ();
			tCloseCompanyAction = new CloseCompanyAction (tRoundType, tRoundID, this);
			tHexMap = tGameManager.getGameMap ();
			tHexMap.removeAllMapTokens (this, tCloseCompanyAction);
		} else {
			System.out.println ("Tested Share Company " + getAbbrev () + " for Closing");
		}
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
		getCorporationStateElement (tXMLCorporationState, aXMLDocument);
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
	public void getCorporationStateElement (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		aXMLCorporationState.setAttribute (AN_PAR_PRICE, getParPrice ());
		if (gameHasLoans ()) {
			aXMLCorporationState.setAttribute (AN_LOAN_COUNT, loanCount);
			aXMLCorporationState.setAttribute (AN_LOAN_TAKEN, loanTaken);
			aXMLCorporationState.setAttribute (AN_REPAYMENT_HANDLED, repaymentHandled);
		}
		destinationInfo.getDestinationInfo (aXMLCorporationState);
		super.getCorporationStateElement (aXMLCorporationState, aXMLDocument);
	}

	public MapCell getDestinationMapCell () {
		return destinationInfo.getMapCell ();
	}
	
	@Override
	public String getDestinationLabel () {
		String tDestinationLabel;
		
		if (destinationInfo == DestinationInfo.NO_DESTINATION_INFO) {
			tDestinationLabel = "NONE";
		} else {
			tDestinationLabel = destinationInfo.getLabel ();
		}
		
		return tDestinationLabel;
	}

	public int getDestinationLocationInt () {
		int tDestinationInt;
		
		if (destinationInfo == DestinationInfo.NO_DESTINATION_INFO) {
			tDestinationInt = 0;
		} else {
			tDestinationInt = destinationInfo.getLocationInt ();
		}
		
		return tDestinationInt;
	}

	public Location getDestinationLocation () {
		Location tDestinationLocation;
		
		if (destinationInfo == DestinationInfo.NO_DESTINATION_INFO) {
			tDestinationLocation = Location.NO_LOC;
		} else {
			tDestinationLocation = destinationInfo.getLocation ();
		}
		
		return tDestinationLocation;
	}

	public void setRepaymentHandled (boolean aRepaymentHandled) {
		repaymentHandled = aRepaymentHandled;
	}
	
	public boolean wasRepaymentHandled () {
		return repaymentHandled;
	}
	
	public boolean sharesFolded () {
		boolean tSharesFolded;
		
		tSharesFolded = false;
		if (getShareFoldCount () == 0) {
			tSharesFolded = true;
		}
		
		return tSharesFolded;
	}
	
	public int getShareFoldCount () {
		int tShareFoldCount;
		
		tShareFoldCount = getSharesInBankPool () + getSharesOwnedByPlayerOrCorp ();
		
		return tShareFoldCount;
	}
	
	@Override
	public boolean willFold () {
		boolean tWillFold;
		
		if (wasRepaymentHandled () && hasOutstandingLoans ()) {
			tWillFold = true;
		} else {
			tWillFold = false;
		}
		
		return tWillFold;
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

	@Override
	public void payLoanInterest () {
		payLoanInterest (0);
	}
	/**
	 * Base method to handle Interest Payment for a Loan for the Company.
	 *
	 */

	public void payLoanInterest (int aPresidentContribution) {
		ActorI.ActionStates tOldState;
		int tLoanCount;
		int tInterestPayment;
		int tTreasury;
		int tRevenueContribution;
		Bank tBank;
		LoanInterestCoupon tInterestCoupon;

		tOldState = getStatus ();
		tLoanCount = getLoanCount ();
		tInterestPayment = tLoanCount * getLoanInterest ();
		tBank = corporationList.getBank ();
		if (tInterestPayment <= getCash ()) {
			completeLoanInterestPayment (tOldState, tInterestPayment, 0, aPresidentContribution, tBank);
		} else {

			tTreasury = getCash ();
			tRevenueContribution = calculateRevenueContribution (tInterestPayment);
			
			if (tInterestPayment > (tTreasury + tRevenueContribution + aPresidentContribution)) {
				tInterestCoupon = new LoanInterestCoupon (tInterestPayment, tRevenueContribution);
				forceBuyCoupon (tInterestCoupon);
			} else {
				completeLoanInterestPayment (tOldState, tInterestPayment, tRevenueContribution, aPresidentContribution, tBank);
			}
		}
	}

	private void completeLoanInterestPayment (ActorI.ActionStates aOldState, int aInterestPayment, int aRevenuePayment, 
											int aPresidentContribution, Bank aBank) {
		ActorI.ActionStates tNewState;
		PayLoanInterestAction tPayLoanInterestAction;
		OperatingRound tOperatingRound;
		int tCashPaid;
		
		if (updateStatus (ActorI.ActionStates.HandledLoanInterest)) {
			tNewState = getStatus ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayLoanInterestAction = new PayLoanInterestAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
			addNeededCashTransferEffect (tPayLoanInterestAction, aPresidentContribution);
			tCashPaid = aInterestPayment - aRevenuePayment;
			transferCashTo (aBank, tCashPaid);
			if (aRevenuePayment > 0) {
				tPayLoanInterestAction.addReduceRevenueEffect (this, aRevenuePayment);
			}
			tPayLoanInterestAction.addCashTransferEffect (this, aBank, tCashPaid);
			tPayLoanInterestAction.addChangeCorporationStatusEffect (this, aOldState, tNewState);
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
		
		tTreasuryContribution = calculateTreasuryContribution ();
		tRevenueContribution = trainRevenueFrame.getRevenueContribution ();
		
		tStillOwe = aInterestPayment - tTreasuryContribution - tRevenueContribution;
		
		System.out.println ("Treasury " + getCash () + " Contribution amount " + tTreasuryContribution);
		System.out.println ("Revenue Contribution " + tRevenueContribution);
		System.out.println ("Still Owed after Treasury and Revenue contributions " + tStillOwe);
		
		return tRevenueContribution;
	}
	
	private void forceBuyCoupon (Coupon aForceBuyCoupon) {
		ForceBuyCouponFrame tForceBuyCouponFrame;

		tForceBuyCouponFrame = new ForceBuyCouponFrame (this, aForceBuyCoupon);
		setForceBuyCouponFrame (tForceBuyCouponFrame);
		setMustBuyCoupon (true);
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

	@Override
	public int getSharesOwned () {
		int tSharesOwned;
		
		tSharesOwned = getPercentOwned ()/ PhaseInfo.STANDARD_SHARE_SIZE;

		return tSharesOwned;
	}
	
	@Override
	public int getSharesOwnedByPlayerOrCorp () {
		int tSharesOwned;
		
		tSharesOwned = getPlayerOrCorpOwnedPercentage ()/ PhaseInfo.STANDARD_SHARE_SIZE;

		return tSharesOwned;
	}

	public int getMustRedeemLoanCount () {
		int tLoanCount;
		int tSharesOwned;
		int tMustRedeemCount;
		
		tLoanCount = getLoanCount ();
		tSharesOwned = getSharesOwned ();
		if (tLoanCount > tSharesOwned) {
			tMustRedeemCount = tLoanCount - tSharesOwned;
		} else {
			tMustRedeemCount = 0;
		}

		return tMustRedeemCount;
	}

	public void redeemLoans (int aLoanRedemptionCount) {
		boolean tHandledRepayment;
		
		tHandledRepayment = false;
		redeemLoans (aLoanRedemptionCount, 0, tHandledRepayment);
	}

	public void redeemLoans (int aLoanRedemptionCount, int aPresidentContribution, boolean aHandledRepayment) {
		int tNewLoanCount;
		int tOldLoanCount;
		int tLoanRedemptionAmount;
		RedeemLoanAction tRedeemLoanAction;
		Bank tBank;
		OperatingRound tOperatingRound;
		LoanRedemptionCoupon tRedemptionCoupon;

		if (loanCount < aLoanRedemptionCount) {
			System.err.println ("Asked to repay " + aLoanRedemptionCount + " however the company only has " + 
								loanCount + " outstanding loans.");
		} else {
			tOldLoanCount = loanCount;
			tNewLoanCount = loanCount - aLoanRedemptionCount;
			tLoanRedemptionAmount = loanAmount * aLoanRedemptionCount;

			if (tLoanRedemptionAmount <= (getCash () + aPresidentContribution)) {
				tBank = corporationList.getBank ();
				tOperatingRound = corporationList.getOperatingRound ();
				setLoanCount (tNewLoanCount);
				tRedeemLoanAction = new RedeemLoanAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
				addNeededCashTransferEffect (tRedeemLoanAction, aPresidentContribution);
				tRedeemLoanAction.addUpdateLoanCountEffect (this, tOldLoanCount, tNewLoanCount);
				tRedeemLoanAction.addCashTransferEffect (this, tBank, tLoanRedemptionAmount);
				if (aHandledRepayment) {
					tRedeemLoanAction.addRebuildFormationPanelEffect (this);
				}
				transferCashTo (tBank, tLoanRedemptionAmount);
				setLoanCount (tNewLoanCount);
				corporationList.addAction (tRedeemLoanAction);

				corporationFrame.updateInfo ();
			} else {
				System.err.println ("Asked to replay " + Bank.formatCash (tLoanRedemptionAmount) +
						" however, the company only has " + Bank.formatCash (getCash ()) + " available.");
				tRedemptionCoupon = new LoanRedemptionCoupon (tLoanRedemptionAmount);
				forceBuyCoupon (tRedemptionCoupon);
			}
		}
	}

	public int getParPrice () {
		return parPrice;
	}

	public String getFormattedParPrice () {
		String tFormattedParPrice;
		
		if (parPrice == NO_PAR_PRICE) {
			tFormattedParPrice = "NO PAR PRICE";
		} else {
			tFormattedParPrice = Bank.formatCash (parPrice);
		}
		
		return tFormattedParPrice;
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
			(status == ActorI.ActionStates.Unformed) ||
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.Closed)) {
			tCanOperate = false;
		}

		return tCanOperate;
	}

	public boolean willFloat () {
		return (status == ActorI.ActionStates.WillFloat);
	}

	@Override
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
			setRepaymentHandled (aXMLNode.getThisBooleanAttribute (AN_REPAYMENT_HANDLED));
		}
		if (hasDestination ()) {
			destinationInfo.loadStatus (aXMLNode);
		}
	}

	public void payNoDividendAdjustment (PayNoDividendAction aPayNoDividendAction) {
		sharePrice.doPayNoDividendAdjustment (this, aPayNoDividendAction);
	}

	public void payFullDividendAdjustment (PayFullDividendAction aPayFullDividendAction) {
		sharePrice.doPayFullDividendAdjustment (this, aPayFullDividendAction);
	}

	public void setDestination (MapCell aDestinationCity, Location aDestinationLocation) {
		destinationInfo.setMapCell (aDestinationCity);
		destinationInfo.setLocation (aDestinationLocation);
	}

	public void setDestinationMapCell (MapCell aDestinationCity) {
		destinationInfo.setMapCell (aDestinationCity);
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
			corporationList.addDataElement (parPrice, tRowIndex, parPriceColumn);
		}
		updateListeners (SET_PAR_PRICE);
	}

	public void setStartCell (Market aMarket) {
		MarketCell tMarketCell;

		if (startCell != NO_START_CELL) {
			if (aMarket != Market.NO_MARKET) {
				tMarketCell = getMarketCellAt (aMarket);
				if (tMarketCell != MarketCell.NO_MARKET_CELL) {
					setParPrice (tMarketCell);
				}
			}
		}
	}

	public void setParPrice (MarketCell aMarketCell) {
		int tParPrice;
		
		tParPrice = aMarketCell.getValue ();
		setSharePrice (aMarketCell);
		setParPrice (tParPrice);
	}

	public void setSharePrice (MarketCell aSharePrice) {
		int tRowIndex;
		int tValue;

		sharePrice = aSharePrice;
		if (aSharePrice != MarketCell.NO_SHARE_PRICE) {
			tRowIndex = corporationList.getRowIndex (this);
			tValue = sharePrice.getValue ();
			corporationList.addDataElement (tValue, tRowIndex, sharePriceColumn);
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

	private void setValues (int aLoanCount, boolean aLoanTaken, String aStartCell) {
		setLoanCount (aLoanCount);
		setLoanTaken (aLoanTaken);
		startCell = aStartCell;
	}

	private void setValues (int aParPrice, MarketCell aSharePrice, int aLoanCount,
							boolean aLoanTaken, boolean aRepaymentHandled, String aStartCell) {
		setSharePrice (aSharePrice);
		setParPrice (aParPrice);
		setValues (aLoanCount, aLoanTaken, aStartCell);
		setRepaymentHandled (aRepaymentHandled);
	}

	public int getDestinationCapitalizationLevel () {
		return destinationInfo.getCapitalizationLevel ();
	}
	
	public void setDestinationCapitalizationLevel () {
		int tCapitalizationLevel;
		
		tCapitalizationLevel = getCapitalizationLevel ();
		setDestinationCapitalizationLevel (tCapitalizationLevel);
	}
	
	public void setDestinationCapitalizationLevel (int aCapitalizationLevel) {
		destinationInfo.setCapitalizationLevel (aCapitalizationLevel);
	}
	
	@Override
	public int calculateStartingTreasury () {
		int tStartingTreasury;

		tStartingTreasury = getDestinationCapitalizationLevel () * getParPrice ();

		return tStartingTreasury;
	}

	@Override
	public int getCapitalizationLevel () {
		int tCapitalizationAmount;
		int tSharesSold;

		tSharesSold = getSharesSold ();
		tCapitalizationAmount = super.getGameCapitalizationLevel (tSharesSold);

		return tCapitalizationAmount;
	}

	@Override
	public int getSharesSold () {
		int tSharesSold;

		tSharesSold = getPlayerOrCorpOwnedPercentage () / 10;

		return tSharesSold;
	}

	@Override
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

	public boolean hasBoughtTrain () {
		boolean tHasBoughtTrain;
		
		if (status == ActorI.ActionStates.BoughtTrain) {
			tHasBoughtTrain = true; 
		} else {
			tHasBoughtTrain = false;
		}
		
		return tHasBoughtTrain;
	}

	public boolean hasOperated () {
		boolean tHasOperated;
		
		if (status == ActorI.ActionStates.Operated) {
			tHasOperated = true; 
		} else {
			tHasOperated = false;
		}
		
		return tHasOperated;
	}
	
	public void handleRejectOfferPrivate () {
		corporationList.clearPrivateSelections ();
		updateInfo ();
	}

	public void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio, 
									Certificate aCertificate, BuyStockAction aBuyStockAction) {
		ActorI.ActionStates tCurrentCorporationStatus;
		ActorI.ActionStates tNewCorporationStatus;
		PortfolioHolderI tFromHolder;
		PortfolioHolderI tToHolder;

		tFromHolder = aFromPortfolio.getHolder ();
		tToHolder = aToPortfolio.getHolder ();
		aToPortfolio.transferOneCertificateOwnership (aFromPortfolio, aCertificate);
		aBuyStockAction.addTransferOwnershipEffect (tFromHolder, aCertificate, tToHolder);
		tCurrentCorporationStatus = aCertificate.getCorporationStatus ();
		handlePassiveBenefits (aCertificate, aBuyStockAction);
		aCertificate.updateCorporationOwnership ();
		tNewCorporationStatus = aCertificate.getCorporationStatus ();
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyStockAction.addChangeCorporationStatusEffect (aCertificate.getCorporation (),
							tCurrentCorporationStatus, tNewCorporationStatus);
		}
	}

	private void handlePassiveBenefits (Certificate aCertificate, BuyStockAction aBuyStockAction) {
		PrivateCompany tPrivateCompany;
		
		if (aCertificate.isAPrivateCompany ()) {
			tPrivateCompany = (PrivateCompany) aCertificate.getCorporation ();
			if (tPrivateCompany.hasAnyPassiveCompanyBenefits ()) {
				System.out.println ("The Private " + tPrivateCompany.getAbbrev () + 
						" has Unused Passive Benefits");
				tPrivateCompany.handlePassiveBenefits (this, aBuyStockAction);
			}
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
	public JPanel buildCorpInfoJPanel () {
		JPanel tCorpInfoJPanel;
		JLabel tLoanInfo;
		GameManager tGameManager;
		PhaseManager tPhaseManager;
		
		
		tGameManager = corporationList.getGameManager ();
		tCorpInfoJPanel = super.buildCorpInfoJPanel ();
		if (isActive ()) {
			if (gameHasLoans ()) {
				tPhaseManager = tGameManager.getPhaseManager ();
				if (tPhaseManager.loansAllowed ()) {
					tLoanInfo = new JLabel ("Loan Count: " + loanCount);
					tCorpInfoJPanel.add (tLoanInfo);
				}
			}
		}

		return tCorpInfoJPanel;
	}
	
	
	@Override
	public int getCurrentValue () {
		return getSharePrice ();
	}
	
	public void close (StockValueCalculationAction aStockValueCalculationAction) {
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		Market tMarket;
		MarketCell tMarketCell;
		GameManager tGameManager;
		int tStackLocation;

		tOldState = getActionStatus ();

		if (tOldState.equals (ActorI.ActionStates.Closed)) {
			appendErrorReport ("The Corporation " + name + " is already Closed... don't need to close again");
		} else {
			resetStatus (ActorI.ActionStates.Closed);
			tNewState = getActionStatus ();
			aStockValueCalculationAction.addChangeCorporationStatusEffect (this, tOldState, tNewState);
			tGameManager = corporationList.getGameManager ();
			tMarket = tGameManager.getMarket ();
			tMarketCell = tMarket.getMarketCellContainingToken (abbrev);
			tStackLocation = tMarketCell.getTokenLocation (abbrev);
			tMarketCell.getToken (abbrev);
			aStockValueCalculationAction.addRemoveTokenFromMarketCellEffect (this, tMarketCell, tStackLocation);
		}
	}

	/**
	 * Method to override the Basic Corporation Method so that if Share Company Closes, no error message is generated.
	 */
	@Override
	public void removeBenefitButtons () {
	}
	
	public void addSpecialButton (KButton aSpecialButton) {
		specialButtons.add (aSpecialButton);
	}
	
	public boolean hasSpecialButton (KButton aSpecialButton) {
		boolean tHasSpecialButton;
		
		tHasSpecialButton = false;
		for (KButton tSpecialButton : specialButtons) {
			if (tSpecialButton == aSpecialButton) {
				tHasSpecialButton = true;
			}
		}
		
		return tHasSpecialButton;
	}
	
	public void clearClosed () {		
		MapCell tDestinationMapCell;
		RemoveDestinationsAction tRemoveDestinationsAction;
		Location tDestinationLocation;
		
		clearHomeFromMapCell (homeCity1);
		clearHomeFromMapCell (homeCity2);
		
		if (hasDestination ()) {
			tDestinationMapCell = getDestinationMapCell ();
			tDestinationLocation = getDestinationLocation ();
			tRemoveDestinationsAction = new RemoveDestinationsAction ();
			tDestinationMapCell.removeDestination (tDestinationLocation, this, tRemoveDestinationsAction);
		}

	}
	
	public void clearHomeFromMapCell (MapCell aMapCell) {
		Tile tTile;
		Centers tCenters;
		
		if (aMapCell != MapCell.NO_MAP_CELL) {
			if (aMapCell.isTileOnCell ()) {
				tTile = aMapCell.getTile ();
				tCenters = tTile.getCenters ();
				tCenters.clearHomeCorporation (this);
			}
		}
	}

}
