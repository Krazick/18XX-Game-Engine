package ge18xx.company;

import java.awt.Component;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.text.WordUtils;

import ge18xx.bank.Bank;
import ge18xx.center.Revenue;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.Benefits;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine2I;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

//
//  Private.java
//  Java_18XX
//
//  Created by Mark Smith on 8/7/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class PrivateCompany extends Corporation implements ParsingRoutine2I {
	public static final ElementName EN_PRIVATE = new ElementName ("Private");
	public static final AttributeName AN_COST = new AttributeName ("cost");
	public static final AttributeName AN_REVENUE = new AttributeName ("revenue");
	public static final AttributeName AN_SPECIAL = new AttributeName ("special");
	public static final AttributeName AN_NOTE = new AttributeName ("note");
	public static final AttributeName AN_EXCHANGE_ID = new AttributeName ("exchangeID");
	public static final AttributeName AN_EXCHANGE_PERCENTAGE = new AttributeName ("exchangePercentage");
	public static final AttributeName AN_MUST_SELL = new AttributeName ("mustSell");
	public static final AttributeName AN_DISCOUNT = new AttributeName ("discount");
	public static final PrivateCompany NO_PRIVATE_COMPANY = null;
	private static final int DISCOUNT = 5;
	private static final int INITIAL_DISCOUNT = 0;
	int cost;
	int revenue;
	int discount;
	boolean mustSell;
	String special;
	String note;
	int exchangeID; // Corporation ID to Exchange this Private For
	int exchangePercentage; // Exchange Percentage
	Benefits benefits;	// Move up to Corporation Level to allow all Companies to have Benefits
	
	public PrivateCompany () {
		this (Corporation.NO_ID, Corporation.NO_NAME, Corporation.NO_ABBREV, NO_COST, Revenue.NO_REVENUE_VALUE, 
				MapCell.NO_MAP_CELL, Location.NO_LOC, MapCell.NO_MAP_CELL, 
				Location.NO_LOC, Corporation.NO_ID, Certificate.NO_PERCENTAGE, 
				ActorI.ActionStates.Unowned, false);
	}

	public PrivateCompany (int aID, String aName, String aAbbrev, int aCost, int aRevenue, 
			MapCell aHomeCity1, Location aHomeLocation1, ActorI.ActionStates aState, 
			boolean aMustBeSoldBeforeOperatingRound) {
		this (aID, aName, aAbbrev, aCost, aRevenue, aHomeCity1, aHomeLocation1, 
				MapCell.NO_MAP_CELL, Location.NO_LOC, Corporation.NO_ID, Certificate.NO_PERCENTAGE, 
				aState, aMustBeSoldBeforeOperatingRound);
	}
	
	public PrivateCompany (int aID, String aName, String aAbbrev, int aCost, int aRevenue, 
			MapCell aHomeCity1, Location aHomeLocation1, MapCell aHomeCity2, Location aHomeLocation2,
			int aExchangeCorporationID, int aExchangeCorporationPercentage, ActorI.ActionStates aState, 
			boolean aMustBeSoldBeforeOperatingRound) {
		super (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, aState, false);
		cost = aCost;
		discount = INITIAL_DISCOUNT;
		revenue = aRevenue;
		exchangeID = aExchangeCorporationID;
		exchangePercentage = aExchangeCorporationPercentage;
		mustSell = aMustBeSoldBeforeOperatingRound;
	}
	
	public PrivateCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		String tNote;
		
		cost = aChildNode.getThisIntAttribute (AN_COST);
		discount = aChildNode.getThisIntAttribute (AN_DISCOUNT);
		mustSell = aChildNode.getThisBooleanAttribute (AN_MUST_SELL);
		revenue = aChildNode.getThisIntAttribute (AN_REVENUE);
		special = aChildNode.getThisAttribute (AN_SPECIAL);
		tNote = aChildNode.getThisAttribute (AN_NOTE, NO_NOTE);
		if (NO_NOTE.equals (tNote)) {
			note = tNote;
		} else {
			note = "<html>" + wordWrap (tNote) + "</html>";
		}
		exchangeID = aChildNode.getThisIntAttribute (AN_EXCHANGE_ID);
		exchangePercentage = aChildNode.getThisIntAttribute (AN_EXCHANGE_PERCENTAGE);
	}
	
	private String wordWrap (String aText) {
		String tWrappedWords = "";
		
		tWrappedWords = WordUtils.wrap (aText, 50, "<br/>", true);
		
		return tWrappedWords;
	}

	@Override
	public void foundItemMatchKey2 (XMLNode aChildNode) {
		benefits = new Benefits (aChildNode);
	}

	public boolean hasButtonFor (JPanel aButtonRow, String aButtonLabel) {
		boolean tHasButtonFor = false;
		JButton tThisButton;
		Component tComponent;
		String tButtonText;
		int tComponentCount, tComponentIndex;
		
		tComponentCount = aButtonRow.getComponentCount ();
		if (tComponentCount > 0) {
			for (tComponentIndex = 0; tComponentIndex < tComponentCount; tComponentIndex++) {
				tComponent = aButtonRow.getComponent (tComponentIndex);
				if (tComponent instanceof JButton) {
					tThisButton = (JButton) tComponent;
					tButtonText = tThisButton.getText ();
					if (aButtonLabel.equals (tButtonText)) {
						tHasButtonFor = true;
					}
				}
			}
		}
		
		return tHasButtonFor;
	}

	public void addBenefitButtons (JPanel aButtonRow) {
		benefits.configure (this, aButtonRow);
	}
	
	public void removeBenefitButtons (JPanel aButtonRow) {
		benefits.removeBenefitButtons(aButtonRow);
	}
	
	@Override
	public void removeBenefitButtons  () {
		if (hasActivePlayerBenefits ()) {
			benefits.removeBenefitButtons ();
		}
	}
	
	public boolean hasActiveCompanyBenefits () {
		boolean tHasActiveCompanyBenefits = false;
		
		if (benefits != Benefits.NO_BENEFITS) {
			tHasActiveCompanyBenefits = benefits.hasActiveCompanyBenefits ();
		}
		
		return tHasActiveCompanyBenefits;
	}
	
	public boolean hasActivePlayerBenefits () {
		boolean tHasActivePlayerBenefits = false;
		
		if (benefits != Benefits.NO_BENEFITS) {
			tHasActivePlayerBenefits = benefits.hasActivePlayerBenefits ();
		}
		
		return tHasActivePlayerBenefits;
	}
	
	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getCost (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getDiscount (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getMustSell (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getRevenue (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getSpecial (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getExchangeID (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getExchangePercentage (), aRowIndex, tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	@Override
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Cost", tCurrentColumn++);
		aCorporationList.addHeader ("Discount", tCurrentColumn++);
		aCorporationList.addHeader ("Must Sell", tCurrentColumn++);
		aCorporationList.addHeader ("Revenue", tCurrentColumn++);
		aCorporationList.addHeader ("Special", tCurrentColumn++);
		aCorporationList.addHeader ("Exchange ID", tCurrentColumn++);
		aCorporationList.addHeader ("Exchange Percentage", tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	@Override
	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivateCertJPanel;
		Certificate tPresidentCertificate;
		
		tPresidentCertificate = getPresidentCertificate ();
		tPrivateCertJPanel = tPresidentCertificate.buildPrivateCertJPanel (aItemListener, aAvailableCash);
		
		return tPrivateCertJPanel;
	}
	
	@Override
	public String buildCorpInfoLabel () {
		String tCorpLabel = "";
		int tBidderCount;
		String tBidderNames;
		
		tCorpLabel = getAbbrev () + "<br>";
		if (isActive ()) {
			tCorpLabel += "[" + getPlayerOrCorpOwnedPercentage () + "%&nbsp;" + getStatusName () + "]";
			tCorpLabel += "<br>Prez: " + getPresidentName ();
			tCorpLabel += "<br>Price: " + Bank.formatCash (getCost ());
			tCorpLabel += "<br>Revenue: " + Bank.formatCash (getRevenue ());
		} else {
			tCorpLabel += "[" + getStatusName () + "]";
			tBidderCount = getBidderCount ();
			if (tBidderCount > 0) {
				tBidderNames = getBidderNames ();
				if (tBidderCount == 1) {
					tCorpLabel += "<br>" + getBidderCount () + " Bidder: " + tBidderNames;
				} else {
					tCorpLabel += "<br>" + getBidderCount () + " Bidders (" + tBidderNames + ")";
				}
				tCorpLabel += "<br>Highest Bid " + Bank.formatCash (corporationCertificates.getHighestBid ());
			}
			tCorpLabel += "<br>Price: " + Bank.formatCash (getCost ());
			tCorpLabel += "<br>Revenue: " + Bank.formatCash (getRevenue ());
			if (getDiscount () > 0) {
				tCorpLabel += "<br>Discount: " + Bank.formatCash (getDiscount ());
			}
		}
		tCorpLabel = "<html>" + tCorpLabel + "</html>";
		
		return tCorpLabel;
	}

	@Override
	public String getNote () {
		return note;
	}
	
	@Override
	public boolean canBuyPrivate () {
		return corporationList.canBuyPrivate ();
	}

	// Number of Fields in Corporation Table to show
	@Override
	public int fieldCount () {
		return super.fieldCount () + 7;
	}
	
	public int getCost () {
		return cost;
	}
	
	@Override
	public int getDiscount () {
		return discount;
	}
	
	@Override
	public void setDiscount (int aDiscount) {
		discount = aDiscount;
	}
	
	@Override
	public void increaseDiscount () {
		discount += DISCOUNT;
	}
	
	@Override
	public ElementName getElementName () {
		return EN_PRIVATE;
	}
	
	public int getExchangeID () {
		return exchangeID;
	}
	
	public int getExchangePercentage () {
		return exchangePercentage;
	}

	@Override
	public boolean getMustSell () {
		return mustSell;
	}

	public String getOwnerName () {
		if (isOwned ()) {
			return getPresidentName ();
		} else {
			return NO_PRESIDENT;
		}
	}
	
	public PortfolioHolderI getOwner () {
		PortfolioHolderI tHolder;
		
		tHolder = Portfolio.NO_HOLDER;
		if (isOwned ()) {
			return getPresident ();
		}
		
		return tHolder;
	}
	
	@Override
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tBidders, tXMLBenefits;
		
		tXMLElement = aXMLDocument.createElement (EN_PRIVATE);
		super.getCorporationStateElement (tXMLElement);
		tXMLElement.setAttribute (AN_DISCOUNT, discount);
		tXMLElement.setAttribute (AN_MUST_SELL, mustSell);
		tBidders = corporationCertificates.getBidders (aXMLDocument);
		if (tBidders != Portfolio.NO_BIDDERS) {
			tXMLElement.appendChild (tBidders);
		}
		if (benefits != Benefits.NO_BENEFITS) {
			if (benefits.getCount () > 0) {
				tXMLBenefits = benefits.getBenefitsStateElement (aXMLDocument);
				tXMLElement.appendChild (tXMLBenefits);
			}
		}
		
		return tXMLElement;
	}

	@Override
	public int getRevenue () {
		return revenue;
	}
	
	public String getSpecial () {
		return special;
	}
	
	@Override
	public String getType () {
		return PRIVATE_COMPANY;
	}
	
	public int getValue () {
		return cost;
	}
	
	public boolean isOwned () {
		boolean tIsOwned;
		Certificate tCertificate;
		int tCertificateCount, tCertificateIndex;
		
		tIsOwned = false;
		tCertificateCount = corporationCertificates.getCertificateCountAgainstLimit ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = corporationCertificates.getCertificate (tCertificateIndex);
			if (tCertificate.isOwned ()) {
				tIsOwned = true;
			}
		}
		
		return tIsOwned;
	}
	
	@Override
	public boolean isAPrivateCompany () {
		return true;
	}

	@Override
	public void loadStates (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		discount = aXMLNode.getThisIntAttribute (AN_DISCOUNT);
		mustSell = aXMLNode.getThisBooleanAttribute (AN_MUST_SELL);
		tXMLNodeList = new XMLNodeList (benefitsParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Benefits.EN_BENEFITS);

	}
	
	ParsingRoutineI benefitsParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {			
			benefits.parseBenefitsStates (aChildNode);
		}
	};

	@Override
	public boolean atTrainLimit () {
		return false;
	}

	public int getTotalEscrows () {
		int tTotalEscrows = 0;
		Certificate tCertificate;
		int tCertificateCount, tCertificateIndex;
		
		tCertificateCount = corporationCertificates.getCertificateCountAgainstLimit ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = corporationCertificates.getCertificate (tCertificateIndex);
			tTotalEscrows = tCertificate.getTotalEscrows ();
		}

		return tTotalEscrows;
	}

	@Override
	public void completeBenefitInUse () {
		
	}

	@Override
	protected boolean choiceForBaseToken () {
		return false;
	}

	public Benefit findBenefit (String aBenefitName) {
		Benefit tFoundBenefit = Benefit.NO_BENEFIT;
		
		if (benefits != Benefits.NO_BENEFITS) {
			tFoundBenefit = benefits.findBenefit (aBenefitName);
		}
		
		return tFoundBenefit;
	}
}