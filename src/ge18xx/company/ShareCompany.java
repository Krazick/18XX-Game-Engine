package ge18xx.company;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.player.Player;
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
	static final String NO_START_CELL = null;
	public static final int NO_PAR_PRICE = -1;
	public static ShareCompany NO_SHARE_COMPANY = null;
	static final int NO_LOANS = 0;
	MarketCell sharePrice;
	MapCell destination;
	Location destinationLocation;
	String destinationLabel;
	String startCell;
	int parPrice;
	int loanCount;
	boolean loanTaken;	// Flag set to TRUE if a Loan was taken this OR (limit 1 loan per OR)

	public ShareCompany () {
		super ();
		setNoPrice ();
		setValues (NO_PAR_PRICE, MarketCell.NO_SHARE_PRICE, MapCell.NO_DESTINATION, NO_LOANS, false, NO_START_CELL);
	}

	public ShareCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		int tDestLocation;
		String tStartCell;
		int tParPrice;
		int tLoanCount;
		boolean tLoanTaken;
		
		destinationLabel = aChildNode.getThisAttribute (AN_DESTINATION);
		tDestLocation = aChildNode.getThisIntAttribute (AN_DESTINATION_LOCATION, Location.NO_LOCATION);
		destinationLocation = new Location (tDestLocation);
		tStartCell = aChildNode.getThisAttribute (AN_START_PRICE, NO_START_CELL);
		tParPrice = aChildNode.getThisIntAttribute (AN_PAR_PRICE, NO_PAR_PRICE);
		tLoanCount = aChildNode.getThisIntAttribute (AN_LOAN_COUNT, NO_LOANS);
		tLoanTaken = aChildNode.getThisBooleanAttribute (AN_LOAN_TAKEN);
		setNoPrice ();
		setValues (tParPrice, MarketCell.NO_SHARE_PRICE, MapCell.NO_DESTINATION, tLoanCount, tLoanTaken, tStartCell);
	}

	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getDestination (), aRowIndex, tCurrentColumn++);
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

	@Override
	public int calculateStartingTreasury () {
		int tStartingTreasury;
		int tCapitalizationAmount;

		tCapitalizationAmount = getCapitalizationLevel ();
		tStartingTreasury = tCapitalizationAmount * getParPrice ();

		return tStartingTreasury;
	}

	@Override
	public int getCapitalizationLevel (int aSharesSold) {
		return getCapitalizationLevel ();
	}

	private int getCapitalizationLevel () {
		int tCapitalizationAmount;
		int tSharesSold;

		tSharesSold = getSharesSold ();
		tCapitalizationAmount = super.getCapitalizationLevel (tSharesSold);
//		if (corporationList.doIncrementalCapitalization ()) {
//			System.out.println ("Should do Incremental Capitalization for " + abbrev);
//			// NOTE -- 1856 in early Phases, will do Incremental Capitalization based on Shares Sold
//		}

		return tCapitalizationAmount;
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
		super.getCorporationStateElement (aXMLCorporationState);
	}

	public String getDestination () {
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
		if ((status == ActorI.ActionStates.HandledLoanInterest) || 
			(status == ActorI.ActionStates.HoldDividend) || 
			(status == ActorI.ActionStates.HalfDividend) || 
			(status == ActorI.ActionStates.FullDividend) || 
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
		System.out.println ("Getting a Loan (" + loanCount + " up to " + tNewLoanCount + ")");
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
		ActorI.ActionStates tNewState;
		int tLoanCount;
		int tInterestPayment;
		PayLoanInterestAction tPayLoanInterestAction;
		OperatingRound tOperatingRound;
		Bank tBank;
		
		System.out.println ("Pay Loan interest on " + loanCount + " Loans.");
		tOldState = getStatus ();
		tLoanCount = getLoanCount ();
		tInterestPayment = tLoanCount * getLoanInterest ();
		tBank = corporationList.getBank ();
		if (tInterestPayment <= getCash ()) {
			if (updateStatus (ActorI.ActionStates.HandledLoanInterest)) {
				tNewState = getStatus ();
				tOperatingRound = corporationList.getOperatingRound ();
				tPayLoanInterestAction = new PayLoanInterestAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
				tPayLoanInterestAction.addCashTransferEffect (this, tBank, tInterestPayment);
				tPayLoanInterestAction.addChangeCorporationStatusEffect (this, tOldState, tNewState);
				transferCashTo (tBank, tInterestPayment);
			}
		} else {
			System.err.println ("Need " + Bank.formatCash (tInterestPayment) + " needed to may Loan Payment on " + tLoanCount + " Loans.");
		}
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

	private void redeemLoans (int aLoanCount) {
		int tNewLoanCount;
		int tLoanAmount;
		RedeemLoanAction tRedeemLoanAction;
		Bank tBank;
		OperatingRound tOperatingRound;
		
		if (loanCount < aLoanCount) {
			System.err.println ("Asked to repay " + aLoanCount + " however the company only has " + loanCount + " outstanding loans.");
		} else {
			tNewLoanCount = loanCount - aLoanCount;
			tLoanAmount = loanAmount * aLoanCount;
			
			if (getCash () >= tLoanAmount) {
				tBank = corporationList.getBank ();
				tOperatingRound = corporationList.getOperatingRound ();
				tRedeemLoanAction = new RedeemLoanAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
				tRedeemLoanAction.addRedeemLoanEffect (this);
				tRedeemLoanAction.addUpdateLoanCountEffect (this, loanCount, tNewLoanCount);
				tRedeemLoanAction.addCashTransferEffect (this, tBank, tLoanAmount);
		
				transferCashTo (tBank, tLoanAmount);
				setLoanCount (tNewLoanCount);
				corporationList.addAction (tRedeemLoanAction);
		
				System.out.println ("Redeem a Loan (" + loanCount + " up to " + tNewLoanCount + ")");
				setLoanCount (tNewLoanCount);
			} else {
				System.err.println ("Asked to replay " + Bank.formatCash (tLoanAmount) +
						" however, the company only has " + Bank.formatCash (getCash ()) + " available.");
				// TODO: Add in Emergency Fund Raising, from President, and possible Forced Stock Sale
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
		return sharePrice.canBuyMultiple ();
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

		if ((status == ActorI.ActionStates.Unowned) || (status == ActorI.ActionStates.Owned)
				|| (status == ActorI.ActionStates.Closed)) {
			tCanOperate = false;
		}

		return tCanOperate;
	}

	public boolean hasFloated () {
		boolean tHasFloated;

		if ((status == ActorI.ActionStates.Unowned) || (status == ActorI.ActionStates.Closed)
				|| (status == ActorI.ActionStates.Owned) || (status == ActorI.ActionStates.MayFloat)
				|| (status == ActorI.ActionStates.WillFloat)) {
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
		setLoanCount (aXMLNode.getThisIntAttribute (AN_LOAN_COUNT));
	}

	public void payNoDividendAdjustment (PayNoDividendAction aPayNoDividendAction) {
		sharePrice.doPayNoDividendAdjustment (this, aPayNoDividendAction);
	}

	public void payFullDividendAdjustment (PayFullDividendAction aPayFullDividendAction) {
		sharePrice.doPayFullDividendAdjustment (this, aPayFullDividendAction);
	}

	public void setDestination (MapCell aDestinationCity, Location aDestinationLocation) {
		destination = aDestinationCity;
		destinationLocation = aDestinationLocation;
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
		int tRow, tCol;
		MarketCell tMarketCell;
		int tParPrice;

		if (startCell != NO_START_CELL) {
			if (aMarket != null) {
				tRow = getStartRow ();
				tCol = getStartCol ();
				tMarketCell = aMarket.getMarketCellAtRowCol (tRow, tCol);
				if (tMarketCell != MarketCell.NO_MARKET_CELL) {
					tParPrice = tMarketCell.getValue ();
					setParPrice (tParPrice);
				}
			}
		}
	}

	private void setValues (MapCell aDestination, int aLoanCount, boolean aLoanTaken, String aStartCell) {
		setDestination (aDestination, Location.NO_DESTINATION_LOCATION);
		setLoanCount (aLoanCount);
		setLoanTaken (aLoanTaken);
		startCell = aStartCell;
	}

	private void setValues (int aParPrice, MarketCell aSharePrice, MapCell aDestination, int aLoanCount,
			boolean aLoanTaken, String aStartCell) {
		setSharePrice (aSharePrice);
		setParPrice (aParPrice);
		setValues (aDestination, aLoanCount, aLoanTaken, aStartCell);
	}

	private int getSharesSold () {
		int tSharesSold;

		tSharesSold = getOwnedPercentage () / 10;

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
		CorporationFrame tCorporationFrame;

		corporationList.clearPrivateSelections ();
		tCorporationFrame = getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}

	public void buyPrivateCompany (Player aOwningPlayer, PrivateCompany aPrivateCompany, int aCashValue) {
		Portfolio tCompanyPortfolio;
		Portfolio tPlayerPortfolio;
		BuyStockAction tBuyStockAction;
		CorporationFrame tCorporationFrame;
		Certificate tCertificate;
		String tOperatingRoundID;
		GameManager tGameManager;
		boolean tCurrentNotify;

		tGameManager = corporationList.getGameManager ();
		tOperatingRoundID = "X.X";
		tCertificate = aOwningPlayer.getPresidentCertificateFor (aPrivateCompany);
		tBuyStockAction = new BuyStockAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
		transferCashTo (aOwningPlayer, aCashValue);
		tBuyStockAction.addCashTransferEffect (this, aOwningPlayer, aCashValue);
		tCompanyPortfolio = getPortfolio ();
		tPlayerPortfolio = aOwningPlayer.getPortfolio ();
		doFinalShareBuySteps (tCompanyPortfolio, tPlayerPortfolio, tCertificate, tBuyStockAction);
		tBuyStockAction.addBoughtShareEffect (this);
		tCurrentNotify = tGameManager.getNotifyNetwork ();
		tGameManager.setNotifyNetwork (true);
		addAction (tBuyStockAction);
		tGameManager.setNotifyNetwork (tCurrentNotify);
		tCorporationFrame = getCorporationFrame ();
		tCorporationFrame.updateInfo ();
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
			aBuyStockAction.addStateChangeEffect (aCertificate.getCorporation (), tCurrentCorporationStatus,
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
	public int getCurrentValue () {
		return getSharePrice ();
	}
}
