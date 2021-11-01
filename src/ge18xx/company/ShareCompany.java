package ge18xx.company;

import java.awt.Point;
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
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.FloatCompanyAction;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayNoDividendAction;
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
	BuyPrivateFrame buyPrivateFrame;
	BuyTrainFrame buyTrainFrame;
	
	public ShareCompany () {
		super ();
		setNoPrice ();
		setValues (NO_PAR_PRICE, MarketCell.NO_SHARE_PRICE, MapCell.NO_DESTINATION, NO_LOANS, NO_START_CELL);
	}
	
	public ShareCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		int tDestLocation;
		String tStartCell;
		int tParPrice;
		
		destinationLabel = aChildNode.getThisAttribute (AN_DESTINATION);
		tDestLocation = aChildNode.getThisIntAttribute (AN_DESTINATION_LOCATION, Location.NO_LOCATION);
		destinationLocation = new Location (tDestLocation);
		tStartCell = aChildNode.getThisAttribute (AN_START_PRICE, NO_START_CELL);
		tParPrice = aChildNode.getThisIntAttribute (AN_PAR_PRICE, NO_PAR_PRICE);
		setNoPrice ();
		setValues (tParPrice, MarketCell.NO_SHARE_PRICE, MapCell.NO_DESTINATION, NO_LOANS, tStartCell);
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
		Certificate tPresidentCertificate;
		BuyPrivateFrame tBuyPrivateFrame;
		GameManager tGameManager;
		Point tCorpFrameOffset;
		
		tGameManager = corporationList.getGameManager ();
		tCorpFrameOffset = tGameManager.getOffsetCorporationFrame ();
		
		tPrivateToBuy = getSelectedPrivateToBuy ();
		tPresidentCertificate = getPresidentCertificate (tPrivateToBuy);
		tBuyPrivateFrame = new BuyPrivateFrame (this);
		tBuyPrivateFrame.updateInfo (tPresidentCertificate);
		tBuyPrivateFrame.setLocation (tCorpFrameOffset);
		tBuyPrivateFrame.setVisible (tVisible);
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
	
	public void floatCompany (int aInitialTreasury) {
		int tRowIndex;
		FloatCompanyAction tFloatCompanyAction;
		ActorI.ActionStates tOldState, tNewState;
		OperatingRound tOperatingRound;
		Bank tBank;
		
		tBank = corporationList.getBank ();
		tOperatingRound = corporationList.getOperatingRound ();
		tRowIndex = corporationList.getRowIndex (this);
		tOldState = getStatus ();
		setStatus (ActorI.ActionStates.NotOperated);
		
		tNewState = getStatus ();
		tFloatCompanyAction = new FloatCompanyAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
		tFloatCompanyAction.addChangeCorporationStatusEffect (this, tOldState, tNewState);
		tFloatCompanyAction.addCashTransferEffect (tBank, this, aInitialTreasury);
		
		tBank.transferCashTo (this, aInitialTreasury);
		corporationList.addDataElement (treasury, tRowIndex, 9);
		corporationList.addDataElement (getStatusName (), tRowIndex, 3);
		corporationList.addAction (tFloatCompanyAction);
	}
	
	@Override
	public int getCapitalizationAmount () {
		int tCapitalizationAmount;
		
		tCapitalizationAmount = super.getCapitalizationAmount ();
		if (corporationList.doPartialCapitalization ()) {
			System.out.println ("Should do Partial Capitalization for " + abbrev);
			// NOTE -- 1856 in early Phases, will do Partial Capitalization based on Shares Sold
		}
		
		return tCapitalizationAmount;
	}
	
	/* Build XML Element of Current Share Company State  -- For Saving */
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

	/* Fill In the XML Element with Par Price, and Loan Count, and call super's routine */
	@Override
	public void getCorporationStateElement (XMLElement aXMLCorporationState) {
		int tLoanCount;
		
		aXMLCorporationState.setAttribute (AN_PAR_PRICE, getParPrice ());
		tLoanCount = getLoanCount ();
		if (tLoanCount > 0) {
			aXMLCorporationState.setAttribute (AN_LOAN_COUNT, tLoanCount);
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
	
	public int getLoanCount () {
		return loanCount;
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
	
	public int getStartCol () {
		String [] tSplit = null;
		int tCol;
		
		tCol = 0;
		if (startCell != NO_START_CELL) {
			tSplit = startCell.split(",");
			tCol = Integer.parseInt(tSplit [1]);
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
	
	public boolean hasFloated () {
		boolean tHasFloated;
		
		if ((status == ActorI.ActionStates.Unowned) ||
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
	public boolean isShareCompany () {
		return true;
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
	
	public void setStartCell (Market aMarket){
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
	
	private void setValues (MapCell aDestination, int aLoanCount, String aStartCell) {
		setDestination (aDestination, Location.NO_DESTINATION_LOCATION);
		setLoanCount (aLoanCount);
		startCell = aStartCell;
	}
	
	private void setValues (int aParPrice, MarketCell aSharePrice, MapCell aDestination, int aLoanCount, String aStartCell) {
		setSharePrice (aSharePrice);
		setParPrice (aParPrice);
		setValues (aDestination, aLoanCount, aStartCell);
	}
	
	public boolean shouldFloat () {
		boolean tShouldFloat;
		
		if (status == ActorI.ActionStates.WillFloat) {
			tShouldFloat = true;
		} else {		
			// TODO: 1856 - Test for a state of "MayFloat" against the number sold compared to first available train
			tShouldFloat = false;
		}
		
		return tShouldFloat;
	}
	
	public void handleRejectOfferPrivate (RoundManager aRoundManager) {
		CorporationFrame tCorporationFrame;
		
		corporationList.clearPrivateSelections ();
		tCorporationFrame = getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}
	
	public void handleAcceptOfferPrivate (RoundManager aRoundManager) {
		Player tOwningPlayer;
		ActorI tActorOfferSentTo;
		int tCashValue;
		PrivateCompany tPrivateCompany;
		CorporationFrame tCorporationFrame;
		String tActorToName;
		String tItemName, tItemType;
		GameManager tGameManager;
		
		tGameManager = aRoundManager.getGameManager ();
		tCashValue = purchaseOffer.getAmount ();
		tActorToName = purchaseOffer.getToName ();
		tActorOfferSentTo = tGameManager.getActor (tActorToName);
		if (tActorOfferSentTo.isAPlayer ()) {
			tOwningPlayer = (Player) tActorOfferSentTo;
			if (tOwningPlayer.isAPlayer ()) {
				tCorporationFrame = getCorporationFrame ();
				tItemType = purchaseOffer.getItemType ();
				tItemName = purchaseOffer.getItemName ();
				System.out.println ("Received approval for buying the " + tItemName + " " + tItemType);
				tPrivateCompany = purchaseOffer.getPrivateCompany ();
				if (tPrivateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
					if (tPrivateCompany.getType ().equals (tItemType)) {
						if (tPrivateCompany.getName ().equals (tItemName)) {
							System.out.println ("Almost Ready to buy " + tItemName + " " + tItemType);
							buyPrivateCompany (tOwningPlayer, tPrivateCompany, tCashValue);
						} else {
							System.err.println ("Purchase Offer's Item Name " + tItemName +
									" does not match Selected Item Name " + tPrivateCompany.getName ());
						}
					} else {
						System.err.println ("Purchase Offer's Item Type " + tItemType +
								" does not match Selected Item Type " + tPrivateCompany.getType ());
					}
					tCorporationFrame.updateInfo ();
				} else {
					System.err.println ("Private Company Selected to buy not found (NULL)");
				}
			} else {
				System.err.println ("Company " + tActorToName + " is not a Share Company");
			}
		} else {
			System.out.println ("Actor " + tActorToName + " is not a Corporation - Likely Player");
		}
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
		tBuyStockAction = new BuyStockAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, this);
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
	
	public void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio, 
			Certificate aCertificate, BuyStockAction aBuyStockAction) {
		ActorI.ActionStates tCurrentCorporationStatus, tNewCorporationStatus;
		PortfolioHolderI tFromHolder, tToHolder;
		
		tFromHolder = aFromPortfolio.getHolder ();
		tToHolder = aToPortfolio.getHolder ();
		aToPortfolio.transferOneCertificateOwnership (aFromPortfolio, aCertificate);
		aBuyStockAction.addTransferOwnershipEffect (tFromHolder, aCertificate,  tToHolder);
		tCurrentCorporationStatus = aCertificate.getCorporationStatus ();
		aCertificate.updateCorporationOwnership ();
		tNewCorporationStatus = aCertificate.getCorporationStatus ();
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyStockAction.addStateChangeEffect (aCertificate.getCorporation (), 
					tCurrentCorporationStatus, tNewCorporationStatus);
		}
	}

	@Override
	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		return null;
	}
}
