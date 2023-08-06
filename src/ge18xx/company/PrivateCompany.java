package ge18xx.company;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.center.Revenue;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.Benefits;
import ge18xx.company.benefit.PassiveEffectBenefit;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine2I;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.WordWrapping;
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
	public static final String INCREASE_DISCOUNT = "- INCREASE DISCOUNT";
	private static final int DISCOUNT = 5;
	private static final int INITIAL_DISCOUNT = 0;
	int cost;
	int revenue;
	int discount;
	int exchangeID; // Corporation ID to Exchange this Private For
	int exchangePercentage; // Exchange Percentage
	boolean mustSell;
	String special;
	String note;
	Benefits benefits; // Move up to Corporation Level to allow all Companies to have Benefits

	public PrivateCompany () {
		this (Corporation.NO_ID, Corporation.NO_NAME, Corporation.NO_ABBREV, NO_COST, Revenue.NO_REVENUE_VALUE,
				MapCell.NO_MAP_CELL, Location.NO_LOC, MapCell.NO_MAP_CELL, Location.NO_LOC, Corporation.NO_ID,
				Certificate.NO_PERCENTAGE, ActorI.ActionStates.Unowned, false);
	}

	public PrivateCompany (int aID, String aName, String aAbbrev, int aCost, int aRevenue, MapCell aHomeCity1,
			Location aHomeLocation1, ActorI.ActionStates aState, boolean aMustBeSoldBeforeOperatingRound) {
		this (aID, aName, aAbbrev, aCost, aRevenue, aHomeCity1, aHomeLocation1, MapCell.NO_MAP_CELL, Location.NO_LOC,
				Corporation.NO_ID, Certificate.NO_PERCENTAGE, aState, aMustBeSoldBeforeOperatingRound);
	}

	public PrivateCompany (int aID, String aName, String aAbbrev, int aCost, int aRevenue, MapCell aHomeCity1,
			Location aHomeLocation1, MapCell aHomeCity2, Location aHomeLocation2, int aExchangeCorporationID,
			int aExchangeCorporationPercentage, ActorI.ActionStates aState, boolean aMustBeSoldBeforeOperatingRound) {
		super (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, aState, false);
		setCost (aCost);
		setDiscount (INITIAL_DISCOUNT);
		revenue = aRevenue;
		exchangeID = aExchangeCorporationID;
		exchangePercentage = aExchangeCorporationPercentage;
		mustSell = aMustBeSoldBeforeOperatingRound;
	}

	public PrivateCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		String tNote;
		int tDiscount;
		int tCost;

		tCost = aChildNode.getThisIntAttribute (AN_COST);
		tDiscount = aChildNode.getThisIntAttribute (AN_DISCOUNT);
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
		setCost (tCost);
		setDiscount (tDiscount);
	}

	private String wordWrap (String aText) {
		WordWrapping tWordWrapping;
		String tWrappedWords = "";

		tWordWrapping = new WordWrapping ("<br/>");
		tWrappedWords = tWordWrapping.wrap (aText, 50);

		return tWrappedWords;
	}

	@Override
	public void foundItemMatchKey2 (XMLNode aChildNode) {
		benefits = new Benefits (aChildNode, this);
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
		benefits.removeBenefitButtons (aButtonRow);
	}

	public void addAllActorsBenefitButtons (JPanel aButtonRow) {
		benefits.addAllActorsBenefitButtons (this, aButtonRow);
	}
	
	/**
	 * Remove the Benefit Buttons (if any) that have Active Player Benefits or Active Company Benefits
	 * from the Player Frame, or Corporate Benefit Frames Respectively.
	 *
	 */
	@Override
	public void removeBenefitButtons () {
		if (hasActivePlayerBenefits ()|| hasActiveCompanyBenefits ()) {
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

	public void enableBenefit (String aBenefitName) {
		if (benefits != Benefits.NO_BENEFITS) {
			benefits.enableBenefit (aBenefitName);
		}
	}

	public void disableBenefit (String aBenefitName) {
		if (benefits != Benefits.NO_BENEFITS) {
			benefits.disableBenefit (aBenefitName);
		}
	}

	public void handleQueryBenefits (JFrame aRoundFrame) {
		if (benefits != Benefits.NO_BENEFITS) {
			benefits.handleQueryBenefits (aRoundFrame);
		}
	}

	public Benefit getBenefitNamed (String aBenefitName) {
		Benefit tFoundBenefit;

		tFoundBenefit = Benefit.NO_BENEFIT;
		if (benefits != Benefits.NO_BENEFITS) {
			tFoundBenefit = benefits.getBenefitNamed (aBenefitName);
		}

		return tFoundBenefit;
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
		int tBidderCount;
		String tCorpInfoLabel;
		String tBidderNames;
		String tPriceLabel;
		String tRevenueLabel;

		tCorpInfoLabel = getAbbrev () + "<br>";
		tPriceLabel = "<br>Price: " + Bank.formatCash (getCost ());
		tRevenueLabel = "<br>Revenue: " + Bank.formatCash (getRevenue ());
		if (isActive ()) {
			tCorpInfoLabel += buildPercentOwnedLabel ();
			tCorpInfoLabel += "<br>Prez: " + getPresidentName ();
			tCorpInfoLabel += tPriceLabel;
			tCorpInfoLabel += tRevenueLabel;
		} else {
			tCorpInfoLabel += "[" + getStatusName () + "]";
			tBidderCount = getBidderCount ();
			if (tBidderCount > 0) {
				tBidderNames = getBidderNames ();
				if (tBidderCount == 1) {
					tCorpInfoLabel += "<br>" + getBidderCount () + " Bidder: " + tBidderNames;
				} else {
					tCorpInfoLabel += "<br>" + getBidderCount () + " Bidders (" + tBidderNames + ")";
				}
				tCorpInfoLabel += "<br>Highest Bid " + Bank.formatCash (corporationCertificates.getHighestBid ());
			}
			tCorpInfoLabel += tPriceLabel;
			tCorpInfoLabel += tRevenueLabel;
			if (getDiscount () > 0) {
				tCorpInfoLabel += "<br>Discount: " + Bank.formatCash (getDiscount ());
			}
		}

		return tCorpInfoLabel;
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

	public void setCost (int aCost) {
		cost = aCost;
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
		notifyObservers (CORPORATION_STATUS_CHANGE + INCREASE_DISCOUNT);
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

		tHolder = PortfolioHolderI.NO_HOLDER;
		if (isOwned ()) {
			return getPresident ();
		}

		return tHolder;
	}
	
	public Corporation getOwningCompany () {
		Corporation tOwningCompany;
		PortfolioHolderI tHolder;

		tOwningCompany = Corporation.NO_CORPORATION;
		if (isOwned ()) {
			tHolder =  getPresident ();
			if (tHolder.isACorporation ()) {
				tOwningCompany = (Corporation) tHolder;
			}
		}

		return tOwningCompany;

	}

	@Override
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tBidders, tXMLBenefits;

		tXMLElement = aXMLDocument.createElement (EN_PRIVATE);
		super.getCorporationStateElement (tXMLElement, aXMLDocument);
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
		return getCost ();
	}

	public boolean isOwned () {
		boolean tIsOwned;
		Certificate tCertificate;
		int tCertificateCount;
		int tCertificateIndex;

		tIsOwned = false;
		tCertificateCount = corporationCertificates.getCertificateTotalCount ();
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

	ParsingRoutineI benefitsParsingRoutine = new ParsingRoutineI () {
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
	
	public void getOwnerTypeBenefits (List<Benefit> aOwnerTypeBenefits) {
		if (benefits != Benefits.NO_BENEFITS) {
			benefits.getOwnerTypeBenefits (aOwnerTypeBenefits);
		}
	}

	public Benefit findBenefit (String aBenefitName) {
		Benefit tFoundBenefit;

		if (benefits != Benefits.NO_BENEFITS) {
			tFoundBenefit = benefits.findBenefit (aBenefitName);
		} else {
			tFoundBenefit = Benefit.NO_BENEFIT;
		}

		return tFoundBenefit;
	}

	public boolean hasAnyPassiveCompanyBenefits () {
		boolean tHasAnyPassiveCompanyBenefits;
		
		if (benefits == Benefits.NO_BENEFITS) {
			tHasAnyPassiveCompanyBenefits = false;
		} else {
			tHasAnyPassiveCompanyBenefits = benefits.hasAnyPassiveCompanyBenefits ();
		}
		
		return tHasAnyPassiveCompanyBenefits;
	}
	
	public PassiveEffectBenefit getUnusedPassiveCompanyBenefit () {
		PassiveEffectBenefit tPassiveEffectBenefit;
		
		if (benefits == Benefits.NO_BENEFITS) {
			tPassiveEffectBenefit = (PassiveEffectBenefit) Benefit.NO_BENEFIT;
		} else {
			tPassiveEffectBenefit = benefits.getUnusedPassiveCompanyBenefit ();
		}
		
		return tPassiveEffectBenefit;

	}
	
	public boolean hasAnyPlayerBenefits () {
		boolean tHasAnyPassivePlayerBenefits;
		
		if (benefits == Benefits.NO_BENEFITS) {
			tHasAnyPassivePlayerBenefits = false;
		} else {
			tHasAnyPassivePlayerBenefits = true;
		}
		
		return tHasAnyPassivePlayerBenefits;
	}

	public boolean hasAnyPassivePlayerBenefits () {
		boolean tHasAnyPassivePlayerBenefits;
		
		if (benefits == Benefits.NO_BENEFITS) {
			tHasAnyPassivePlayerBenefits = false;
		} else {
			tHasAnyPassivePlayerBenefits = benefits.hasAnyPassivePlayerBenefits ();
		}
		
		return tHasAnyPassivePlayerBenefits;
	}

	public void handlePassiveBenefits (ShareCompany aShareCompany, Action aAction) {
		PassiveEffectBenefit tPassiveBenefit;
		boolean tWhileMore;
		
		tWhileMore = true;
		while (tWhileMore) {
			tPassiveBenefit = getUnusedPassiveCompanyBenefit ();
			if (tPassiveBenefit != Benefit.NO_BENEFIT) {
				tPassiveBenefit.handlePassive (aShareCompany, aAction);
			} else {
				tWhileMore = false;
			}
		}
	}
	
	@Override
	public int getCurrentValue () {
		return getCost ();
	}

	@Override
	public int calculateStartingTreasury () {
		return 0;
	}

	@Override
	public void completeBenefitInUse (Corporation aCorporation) {

	}
	
	@Override
	public Benefits getBenefits () {
		return benefits;
	}

}