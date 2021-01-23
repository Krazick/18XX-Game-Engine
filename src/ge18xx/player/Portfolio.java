package ge18xx.player;

import ge18xx.bank.Bank;

//
//  Portfolio.java
//  Game_18XX
//
//  Created by Mark Smith on 11/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateHolderI;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Portfolio implements CertificateHolderI {
	public static final int NO_COMPONENT = -1;
	public static final PortfolioHolderI NO_HOLDER = null;
	public static final String NO_COMPANY_YET = ">>NONE YET<<";
	public static final String NO_CERTIFICATES = ">> NO CERTIFICATES <<";
	public static final XMLElement NO_BIDDERS = null;
	public final static ElementName EN_PORTFOLIO = new ElementName ("Portfolio");
	public final static ElementName EN_BIDDERS = new ElementName ("Bidders");
	public static final String NO_NAME_STRING = "<NONE>";
	final static AttributeName AN_PRIVATE_INDEX = new AttributeName ("privateIndex");
	final static AttributeName AN_COAL_INDEX = new AttributeName ("coalIndex");
	final static AttributeName AN_MINOR_INDEX = new AttributeName ("minorIndex");
	final static AttributeName AN_SHARE_INDEX = new AttributeName ("shareIndex");

	/* These items are set once, no need to save/load */
	PortfolioHolderI holder;
	JPanel portfolioInfoJPanel;
	
	/* These items change during the Game, must be saved/loaded */
	List<Certificate> certificates;
	int privateIndex, coalIndex, minorIndex, shareIndex;
	
	public Portfolio () {
		this (NO_HOLDER);
	}
	
	public Portfolio (PortfolioHolderI aHolder) {
		certificates = new LinkedList<Certificate> ();
		holder = aHolder;
		privateIndex = NO_COMPONENT;
		coalIndex = NO_COMPONENT;
		minorIndex = NO_COMPONENT;
		shareIndex = NO_COMPONENT;
	}
	
	public void addCertificate (Certificate aCertificate) {
		aCertificate.setOwner (this);
		certificates.add (aCertificate);
		Collections.sort (certificates);
	}

	public void addJCAndVGlue (JComponent aParentComponent, JComponent aNewChildComponent) {
		if (aParentComponent != null) {
			if (aNewChildComponent != null) {
				aParentComponent.add (aNewChildComponent);
			}
			aParentComponent.add (Box.createVerticalGlue ());
		}
	}

	public void addJCAndHGlue (JComponent aParentComponent, JComponent aNewChildComponent) {
		if (aParentComponent != null) {
			if (aNewChildComponent != null) {
				aParentComponent.add (aNewChildComponent);
			}
			aParentComponent.add (Box.createHorizontalGlue ());
		}
	}

	public JPanel buildCertificateContainer (String aCorpType, String aSelectedButtonLabel, 
			ItemListener aItemListener, GameManager aGameManager) {
		JPanel tCertificatePanel;
		JPanel tCertificateInfoPanel;
		BoxLayout tLayout;
		int tCount;
		String tCertificateType;
		JLabel tLabel;
		boolean tIsBankPortfolioHolder;
		
		tCount = 0;
		tCertificatePanel = new JPanel ();
		tCertificatePanel.setBorder (BorderFactory.createTitledBorder (aCorpType + " Companies"));
		tLayout = new BoxLayout (tCertificatePanel, BoxLayout.X_AXIS);
		tCertificatePanel.setLayout (tLayout);
		tCertificatePanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		addJCAndHGlue (tCertificatePanel, null);
		
		tIsBankPortfolioHolder = holder.isBank ();
		
		for (Certificate tCertificate : certificates) {
			tCertificateType = tCertificate.getCorpType ();
			if (tCertificateType.equals (aCorpType)) {
				tCount++;
				tCertificateInfoPanel = tCertificate.buildCertificateInfoContainer (aSelectedButtonLabel, 
						aItemListener, tIsBankPortfolioHolder, Player.NO_PLAYER, aGameManager);
				addJCAndHGlue (tCertificatePanel, tCertificateInfoPanel);
			}
		}
		
		if (tCount == 0) {
			tLabel = new JLabel (NO_CERTIFICATES);
			addJCAndHGlue (tCertificatePanel, tLabel);
		}
		
		return tCertificatePanel;
	}
	
	public JPanel buildCompactCertInfoPanel (String aCompanyAbbrev, int aCertCount, int aCertTotalPercent) {
		JPanel tCertificateCompactPanel;
		BoxLayout tLayout;
		String tCertInfo;
		JLabel tLabel;
		
		tCertificateCompactPanel = new JPanel ();
		tLayout = new BoxLayout (tCertificateCompactPanel, BoxLayout.Y_AXIS);
		tCertificateCompactPanel.setLayout (tLayout);
		tCertificateCompactPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tCertInfo = compactCertInfo (aCompanyAbbrev, aCertCount, aCertTotalPercent); 
		tLabel = new JLabel (tCertInfo);
		tCertificateCompactPanel.add (tLabel);
		
		return tCertificateCompactPanel;
	}

	private String compactCertInfo (String aCompanyAbbrev, int aCertCount, int aCertTotalPercent) {
		String tCertInfo;
		
		tCertInfo = aCompanyAbbrev + " (" + aCertCount + "/" + aCertTotalPercent + "%)";
		
		return tCertInfo;
	}

	public JPanel buildShareCertificateJPanel (String aCorpType, String aSelectedButtonLabel, 
			ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		JPanel tAllCertificatesPanel;
		JPanel tCertificateInfoPanel;
		JPanel tOtherCertificatesInfoPanel;
		JPanel tCorporationPanel;
		BoxLayout tLayout, tSPLayout;
		Certificate tCertificateToShow;
		Corporation tCorporationToShow;
		int tCount, tCertCount,tCertTotalPercent;
		String tCertificateType, tPrevShareCorpAbbrev, tShareCorpAbbrev;
		JLabel tLabel;
		boolean tIsBankPortfolioHolder;
		
		tCount = 0;
		
		tCorporationPanel = new JPanel ();
		tCorporationPanel.setBorder (BorderFactory.createTitledBorder (aCorpType + " Companies"));
		tSPLayout = new BoxLayout (tCorporationPanel, BoxLayout.Y_AXIS);
		tCorporationPanel.setLayout (tSPLayout);
		tCorporationPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		
		addJCAndHGlue (tCorporationPanel, null);
		tPrevShareCorpAbbrev = NO_COMPANY_YET;
		tAllCertificatesPanel = null;
		
		tIsBankPortfolioHolder = holder.isBank ();
		tCertCount = 0;
		tCertTotalPercent = 0;
		for (Certificate tCertificate : certificates) {
			tCertificateType = tCertificate.getCorpType ();
			if (tCertificateType.equals (aCorpType)) {
				tCertCount = getCertificateCountFor (tCertificate.getCorporation ());
				tCertTotalPercent = getCertificatePercentageFor (tCertificate.getCorporation ());
				tCount++;
				tShareCorpAbbrev = tCertificate.getCompanyAbbrev ();
				if (tShareCorpAbbrev.equals (tPrevShareCorpAbbrev)) {
					if (!tIsBankPortfolioHolder) {
						// If this is NOT a Bank Portfolio -- Show ALL Certificates
						tCertificateInfoPanel = tCertificate.buildCertificateInfoContainer (aSelectedButtonLabel, 
								aItemListener, tIsBankPortfolioHolder, aPlayer, aGameManager);
						addJCAndHGlue (tAllCertificatesPanel, tCertificateInfoPanel);
					}
				} else {
					tCertificateToShow = tCertificate;
					// Want to be sure to show the President's Certificate FIRST to buy, if the Bank has it. 
					// The Sort Certificates has trouble placing the President Certificate in Proper order on the Undo.
					tCorporationToShow = tCertificateToShow.getCorporation ();
					if (containsPresidentShareOf (tCorporationToShow)) {
						tCertificateToShow = getPresidentCertificate (tCorporationToShow);
					}
					tAllCertificatesPanel = new JPanel ();
					tLayout = new BoxLayout (tAllCertificatesPanel, BoxLayout.X_AXIS);
					tAllCertificatesPanel.setLayout (tLayout);
					tAllCertificatesPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
					
					tCertificateInfoPanel = tCertificateToShow.buildCertificateInfoContainer (aSelectedButtonLabel, 
							aItemListener, tIsBankPortfolioHolder, aPlayer, aGameManager);
					addJCAndHGlue (tAllCertificatesPanel, null);
					addJCAndHGlue (tAllCertificatesPanel, tCertificateInfoPanel);
					if (tIsBankPortfolioHolder) {
						tOtherCertificatesInfoPanel = buildCompactCertInfoPanel (tShareCorpAbbrev, tCertCount, 
								tCertTotalPercent);
						addJCAndHGlue (tAllCertificatesPanel, tOtherCertificatesInfoPanel);
					}
					addJCAndVGlue (tCorporationPanel, tAllCertificatesPanel);
					tPrevShareCorpAbbrev = tShareCorpAbbrev;
				}
			}
		}

		if (tCount == 0) {
			tLabel = new JLabel (NO_CERTIFICATES);
			tAllCertificatesPanel = new JPanel ();
			tLayout = new BoxLayout (tAllCertificatesPanel, BoxLayout.X_AXIS);
			tAllCertificatesPanel.setLayout (tLayout);
			tAllCertificatesPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
			addJCAndHGlue (tAllCertificatesPanel, null);
			addJCAndHGlue (tAllCertificatesPanel, tLabel);
			addJCAndVGlue (tCorporationPanel, tAllCertificatesPanel);
		} else {
			addJCAndVGlue (tCorporationPanel, tAllCertificatesPanel);
		}
		
		return tCorporationPanel;
	}
	
	public JPanel buildPortfolioJPanel (boolean aPrivates, boolean aCoals, boolean aMinors, 
			boolean aShares, String aSelectedButtonLabel, ItemListener aItemListener, GameManager aGameManager) {
		BoxLayout tLayout;
		JPanel tPrivateCertPanel, tCoalCertPanel, tMinorCertPanel, tShareCertPanel;
		
		portfolioInfoJPanel = new JPanel ();
		portfolioInfoJPanel.setBorder (BorderFactory.createTitledBorder ("Portfolio Information"));
		tLayout = new BoxLayout (portfolioInfoJPanel, BoxLayout.Y_AXIS);
		portfolioInfoJPanel.setLayout (tLayout);
		portfolioInfoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		addJCAndVGlue (portfolioInfoJPanel, null);

		if (aPrivates) {
			tPrivateCertPanel = buildCertificateContainer (Corporation.PRIVATE_COMPANY, aSelectedButtonLabel, aItemListener, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tPrivateCertPanel);
			privateIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}
		if (aCoals) {
			tCoalCertPanel = buildCertificateContainer (Corporation.COAL_COMPANY, aSelectedButtonLabel, aItemListener, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tCoalCertPanel);
			coalIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}
		if (aMinors) {
			tMinorCertPanel = buildCertificateContainer (Corporation.MINOR_COMPANY, aSelectedButtonLabel, aItemListener, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tMinorCertPanel);
			minorIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}
		if (aShares) {
			tShareCertPanel = buildShareCertificateJPanel (Corporation.SHARE_COMPANY, aSelectedButtonLabel, aItemListener, null, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tShareCertPanel);
			shareIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}
		
		return portfolioInfoJPanel;
	}
	
	public void clearSelections () {
		for (Certificate tCertificate : certificates) {
			tCertificate.clearSelection ();
		}
	}
	
	public boolean containsPresidentShareOf (Corporation aCorporation) {
		boolean isPresidentOf;
		
		isPresidentOf = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isPresidentShare ()) {
					isPresidentOf = true;
				}
			}
		}
		
		return isPresidentOf;
	}
	
	public boolean noMustSellLeft () {
		boolean tNoMustSellLeft = true;
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.getMustSell ()) {
				tNoMustSellLeft = false;
			}
		}
		
		return tNoMustSellLeft;
	}
	
	public void applyDiscount () {
		boolean tDiscountApplied = false;
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.getMustSell () && !tDiscountApplied) {
				tCertificate.applyDiscount ();
				tDiscountApplied = true;
			}
		}
	}
	
	public boolean hasMustBuyCertificate () {
		boolean tMustBuy = false;
		Certificate tCertificate;
		
		// If the portfolio has no Certificates, there is no Must Buy
		if (!certificates.isEmpty ()) {
			tCertificate = certificates.get (0);
			// If the first certificate in the portfolio has no Par Value (Cost == Discount) it is a Must Buy
			if (tCertificate.getParValue () == 0) {
				tMustBuy = true;
			}
		}
		
		return tMustBuy;
	}

	public boolean hasMustSell () {
		boolean tHasMustSell = false;
		Certificate tCertificate;
		Corporation tCorporation;
		
		// If the portfolio has no Certificates, there is no Must Buy
		if (!certificates.isEmpty ()) {
			tCertificate = certificates.get (0);
			tCorporation = tCertificate.getCorporation ();
			if (tCorporation.getMustSell ()) {
			// If the first certificate in the portfolio has with the MustSell Flag this is what we return
				tHasMustSell = true;
			}
		}
		
		return tHasMustSell;
	}
	
	public Certificate getMustSellCertificate () {
		Certificate tCertificate;
		Certificate tThisCertificate = Certificate.NO_CERTIFICATE;
		Corporation tCorporation;
		
		// If the portfolio has no Certificates, there is no Must Buy
		if (!certificates.isEmpty ()) {
			tCertificate = certificates.get (0);
			tCorporation = tCertificate.getCorporation ();
			if (tCorporation.getMustSell ()) {
			// If the first certificate in the portfolio has with the MustSell Flag this is what we return
				tThisCertificate = tCertificate;
			}
		}
		
		return tThisCertificate;
	}
	
	public int getCertificateCountAgainstLimit () {
		int tCertificatesThatCount;
		
		tCertificatesThatCount = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.countsAgainstCertificateLimit ()) {
				tCertificatesThatCount++;
			}
		}
		
		return tCertificatesThatCount;
	}
	
	public int getCertificateTotalCount () {
		return certificates.size ();
	}

	public Certificate getCertificateFor (Corporation aCorporation) {
		Certificate tCertificate, tPortfolioCertificate;
		int tIndex, tCertificateCount;
		
		tCertificate = Certificate.NO_CERTIFICATE;
		tCertificateCount = certificates.size ();
		for (tIndex = 0; (tIndex < tCertificateCount) && (tCertificate == Certificate.NO_CERTIFICATE); tIndex++) {
			tPortfolioCertificate = certificates.get (tIndex);
			if (tPortfolioCertificate.isForThis (aCorporation)) {
				tCertificate = tPortfolioCertificate;
				certificates.remove (tIndex);
			}
		}
		
		return tCertificate;
	}

	public int getCertificateCountFor (Corporation aCorporation) {
		int tCount;
		
		tCount = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tCount++;
			}
		}
		
		return tCount;
	}
	
	public Certificate getCertificate (int aIndex) {
		Certificate tCertificate;
		int tCertificateCount;
		
		tCertificate = Certificate.NO_CERTIFICATE;
		tCertificateCount = certificates.size ();
		if (tCertificateCount > 0) {
			if ((aIndex >= 0) && (aIndex < tCertificateCount)) {
				tCertificate = certificates.get (aIndex);
			}
		}
		
		return tCertificate;
	}
	
	public Certificate getCertificate (Corporation aCorporation, int aPercentage) {
		Certificate tThisCertificate;
		
		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (aCorporation.equals (tCertificate.getCorporation ())) {
				if (tCertificate.getPercentage() == aPercentage) {
					tThisCertificate = tCertificate;
				}
			}
		}
		
		return tThisCertificate;
	}
	
	public Certificate getCertificate (String aAbbrev, int aPercentage, boolean isPresident) {
		Certificate tThisCertificate;
		
		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.getCompanyAbbrev ().equals (aAbbrev)) {
				if (tCertificate.getPercentage() == aPercentage) {
					if (tCertificate.isPresidentShare() == isPresident) {
						tThisCertificate = tCertificate;
					}
				}
			}
		}
		
		return tThisCertificate;
	}
	
	public Certificate getCertificate (int aPercentage, boolean aPresidentShare) {
		Certificate tThisCertificate;
	
		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (aPercentage == tCertificate.getPercentage()) {
				if (aPresidentShare && tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				} else if (! aPresidentShare && ! tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				}
			}
		}
		
		return tThisCertificate;
	}

	public int getCertificatePercentageFor (String aCorpAbbrev) {
		int tPercentage;
		
		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			/* Only worry if this is a Share Company */
			if (tCertificate.isShareCompany ()) {
				if (tCertificate.isForThis (aCorpAbbrev)) {
					tPercentage += tCertificate.getPercentage ();
				}
			}
		}
		
		return tPercentage;		
	}
	
	public int getCertificatePercentageFor (Corporation aCorporation) {
		int tPercentage;
		
		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tPercentage += tCertificate.getPercentage ();
			}
		}
		
		return tPercentage;
	}
	
	public int getPlayerOrCorpOwnedPercentageFor (Corporation aCorporation) {
		int tPercentage;
		
		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isOwnedbyPlayerOrCorp ()) {
					tPercentage += tCertificate.getPercentage ();
				}
			}
		}
		
		return tPercentage;		
	}

	public Certificate getCertificateToBidOn() {
		Certificate tCertificateToBidOn;
		
		tCertificateToBidOn = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBidOn ()) {
				tCertificateToBidOn = tCertificate;
			}
		}
		
		return tCertificateToBidOn;
	}

	public Certificate getCertificateToBuy () {
		Certificate tCertificateToBuy;
		
		tCertificateToBuy = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCertificateToBuy = tCertificate;
			}
		}
		
		return tCertificateToBuy;
	}
	
	public Certificate getCertificateToExchange () {
		Certificate tCertificateToExchange;
	
		tCertificateToExchange = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToExchange ()) {
				tCertificateToExchange = tCertificate;
			}
		}
		
		return tCertificateToExchange;
	}
	
	public List<Certificate> getCertificatesToSell () {
		List<Certificate> tCertificatesToSell;
		
		tCertificatesToSell = new LinkedList<Certificate> ();
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCertificatesToSell.add (tCertificate);
			}
		}
		
		return tCertificatesToSell;
	}
	
	public int getCountOfCertificatesForSale () {
		int tCountOfSelectedCertificates;
		
		tCountOfSelectedCertificates = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCountOfSelectedCertificates++;
			}
		}
		
		return tCountOfSelectedCertificates;
	}
	
	public int getCountOfCertificatesForBuy () {
		int tCountOfSelectedCertificates;
		
		tCountOfSelectedCertificates = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCountOfSelectedCertificates++;
			}
		}
		
		return tCountOfSelectedCertificates;
	}
	
	public XMLElement setRealAttributes (XMLElement aXMLElement, AttributeName aAttributeName, int aIndex) {
		if (aIndex > 0) {
			aXMLElement.setAttribute (aAttributeName,  aIndex);
		}
		
		return aXMLElement;
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLCertificateElements;
		
		tXMLElement = aXMLDocument.createElement (EN_PORTFOLIO);
		setRealAttributes (tXMLElement, AN_PRIVATE_INDEX, privateIndex);
		setRealAttributes (tXMLElement, AN_COAL_INDEX, coalIndex);
		setRealAttributes (tXMLElement, AN_MINOR_INDEX, minorIndex);
		setRealAttributes (tXMLElement, AN_SHARE_INDEX, shareIndex);
		for (Certificate tCertficate : certificates) {
			tXMLCertificateElements = tCertficate.getElement (aXMLDocument);
			tXMLElement.appendChild (tXMLCertificateElements);
		}
		
		return tXMLElement;
	}
	
	public int getBidderCount () {
		int tBidderCount = 0;
		
		for (Certificate tCertificate : certificates) {
			tBidderCount += tCertificate.getNumberOfBidders ();
		}
		
		return tBidderCount;
	}
	
	public int getHighestBid () {
		int tHighestBid = 0;
		
		for (Certificate tCertificate : certificates) {
			tHighestBid = tCertificate.getHighestBid ();
		}

		return tHighestBid;
	}
	
	public XMLElement getBidders (XMLDocument aXMLDocument) {
		XMLElement tXMLElement = NO_BIDDERS;
		XMLElement tXMLBidders;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.hasBidders ()) {
				if (tXMLElement == NO_BIDDERS) {
					tXMLElement = aXMLDocument.createElement (Certificate.EN_CERTIFICATE);
				}
				tXMLBidders = tCertificate.getElement (aXMLDocument);
				tXMLElement.appendChild (tXMLBidders);
			}
		}
		
		return tXMLElement;
	}
	
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = null;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.sameCertificate (aLoadedCertificate)) {
				tCurrentHolder = (PortfolioHolderLoaderI) holder;
			}
		}
		
		return tCurrentHolder;
	}

	public String getSelectedCompanyAbbrev () {
		String tSelectedCompanyAbbrev = "";
		

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelected ()) {
				tSelectedCompanyAbbrev = tCertificate.getCompanyAbbrev ();
			}
		}
		
		return tSelectedCompanyAbbrev;
	}

	public int getSelectedPercent () {
		int tSelectedPercent = 0;
		
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelected ()) {
				tSelectedPercent += tCertificate.getPercentage ();
			}
		}
		
		return tSelectedPercent;
	}

	public int getPresidentPercent (Corporation corporation) {
		String tCertificateOwnerName;
		String tPresidentName;
		PortfolioHolderI tPresident;
		int tPresidentPercent = 0;
		
		tPresident = getPresident ();
		tPresidentName = tPresident.getName ();

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tCertificateOwnerName = tCertificate.getOwnerName ();
				if (tCertificateOwnerName.equals (tPresidentName)) {
					tPresidentPercent += tCertificate.getPercentage ();
				}
			}
		}
		
		return tPresidentPercent;
	}

	public int getNextPresidentPercent (Corporation aCorporation) {
		String tCertificateOwnerName, tNextName, tPresidentName;
//		String tNextPresidentName;
		PortfolioHolderI tPresident;
		int tNextPresidentPercent;
		int tPercent;

//		tNextPresidentName = "";
		tNextName = "";
		tPresident = getPresident ();
		tPresidentName = tPresident.getName ();
		tNextPresidentPercent = 0;
		tPercent = 0;
		sortByOwners ();
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tCertificateOwnerName = tCertificate.getOwnerName ();
				if (! tCertificateOwnerName.equals (tPresidentName)) {
					if (tCertificateOwnerName.equals (tNextName)) {
						tPercent += tCertificate.getPercentage ();
					} else {
						if (tPercent > tNextPresidentPercent) {
							tNextPresidentPercent = tPercent;
//							tNextPresidentName = tNextName;
						}
						tPercent = tCertificate.getPercentage ();
					}
				}
				tNextName = tCertificateOwnerName;
			}
		}
		if (tPercent > tNextPresidentPercent) {
			tNextPresidentPercent = tPercent;
//			tNextPresidentName = tNextName;
		}
		
		return tNextPresidentPercent;
	}
	
	public PortfolioHolderI getPresident () {
		CertificateHolderI tCertificateHolder;
		PortfolioHolderI tPortfolioHolder;
		
		tPortfolioHolder = Portfolio.NO_HOLDER;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPresidentShare ()) {
				tCertificateHolder = tCertificate.getOwner ();
				tPortfolioHolder = tCertificateHolder.getPortfolioHolder ();
			}
		}
		
		return tPortfolioHolder;
	}
	
	public String getPresidentName () {
		String tName = NO_NAME_STRING;
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPresidentShare ()) {
				tName = tCertificate.getOwnerName ();
			}
		}
		
		return tName;
	}

	public Certificate getPresidentCertificate () {
		Certificate tPresidentCertificate;
		
		tPresidentCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPresidentShare ()) {
				tPresidentCertificate = tCertificate;
			}
		}

		return tPresidentCertificate;
	}
	
	public boolean isPresidentAPlayer () {
		boolean tPresidentIsAPlayer;
		PortfolioHolderI tPresident;
		
		tPresident = getPresident ();
		if (tPresident == null) {
			tPresidentIsAPlayer = false;
		} else {
			tPresidentIsAPlayer = tPresident.isPlayer ();
		}
		
		return tPresidentIsAPlayer;
	}
	
	public boolean isSelectedForBuy () {
		boolean tIsSelectedForBuy;
		
		tIsSelectedForBuy = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelected ()) {
				tIsSelectedForBuy = true;
			}
		}
		
		return tIsSelectedForBuy;
	}

	public void loadPortfolio (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;
		
		privateIndex = aXMLNode.getThisIntAttribute (AN_PRIVATE_INDEX, NO_COMPONENT);
		coalIndex = aXMLNode.getThisIntAttribute (AN_COAL_INDEX, NO_COMPONENT);
		minorIndex = aXMLNode.getThisIntAttribute (AN_MINOR_INDEX, NO_COMPONENT);
		shareIndex = aXMLNode.getThisIntAttribute (AN_SHARE_INDEX, NO_COMPONENT);
		tXMLNodeList = new XMLNodeList (certificateParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Certificate.EN_CERTIFICATE);
	}
	
	ParsingRoutineI certificateParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			LoadedCertificate tLoadedCertificate;
			Certificate tCertificate;
			PortfolioHolderI tCurrentHolder;
			Portfolio tPortfolio;
			PortfolioHolderLoaderI tHolder;
			Bank tBank;
			
			tLoadedCertificate = new LoadedCertificate (aChildNode);
			tHolder = (PortfolioHolderLoaderI) holder;
			if (tHolder.getName ().equals (BankPool.NAME)) {
				tCurrentHolder = ((BankPool) tHolder).getCurrentHolderGM (tLoadedCertificate);
			} else {
				tCurrentHolder = tHolder.getCurrentHolder (tLoadedCertificate);
			}
			if (tCurrentHolder == null) {
				tBank = holder.getBank ();
				tCurrentHolder = tBank.getStartPacketFrame ();
			}
			
			if (tCurrentHolder != null) {
				tPortfolio = tCurrentHolder.getPortfolio ();
				if (tPortfolio != null) {
					tCertificate = tPortfolio.getCertificate (tLoadedCertificate.getCompanyAbbrev (), 
							tLoadedCertificate.getPercentage (), 
							tLoadedCertificate.getIsPresidentShare ());
					if (tCertificate != null) {
						transferOneCertificateOwnership (tPortfolio, tCertificate);
					}
				}
			}
		}
	};
	
	public PortfolioHolderI getHolder () {
		return holder;
	}
	
	public String getHolderAbbrev () {
		String tHolderAbbrev;
		
		tHolderAbbrev = ">> NO HOLDER <<";
		if (holder != null) {
			tHolderAbbrev = holder.getAbbrev ();
		}
		
		return tHolderAbbrev;
	}
	
	public String getHolderName () {
		String tHolderName;
		
		tHolderName = ">> NO HOLDER <<";
		if (holder != null) {
			tHolderName = holder.getName ();
		}
		
		return tHolderName;
	}
	
	public String getName () {
		return "PORTFOLIO";
	}
	
	public int getPercentageFor (Corporation aCorporation) {
		int tPercentage;
		
		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tPercentage += tCertificate.getPercentage ();
			}
		}
		
		return tPercentage;
	}
	
	public int getPercentOwned () {
		int tPercentOwned;
		
		tPercentOwned = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tPercentOwned += tCertificate.getPercentage ();
			}
		}
		
		return tPercentOwned;
	}

	public PortfolioHolderI getPortfolioHolder () {
		return holder;
	}
	
	public int getPortfolioValue () {
		int iValue;
		
		iValue = 0;
		for (Certificate tCertificate : certificates) {
			iValue += tCertificate.getValue ();
		}
		
		return iValue;
	}
	
	// Find the Corporation, in the Portfolio that matches this CorpID
	// Used to search the Bank Portfolio to load the Start Packet
	public Corporation getCorporationForID (int CorpID) {
		Corporation tCorporation = CorporationList.NO_CORPORATION;
		Corporation tTestCorporation;
		int tTestCorporationID;
		
		for (Certificate tCertificate : certificates) {
			tTestCorporation = tCertificate.getCorporation ();
			tTestCorporationID = tTestCorporation.getID ();
			if (tTestCorporationID == CorpID) {
				tCorporation = tTestCorporation;
				return tCorporation;
			}
		}
		
		return tCorporation;
	}
	
	public Certificate getNonPresidentCertificate (Corporation aCorporation) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (! tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				}
			}
		}
	
		return tThisCertificate;
	}

	public Certificate getPresidentCertificate (Corporation aCorporation) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				}
			}
		}
	
		return tThisCertificate;
	}

	// Get the Specific Certificate that matches and REMOVE it from the Portfolio
	public Certificate getThisCertificate (Certificate aCertificate) {
		Certificate tCertificate, tPortfolioCertificate;
		int tIndex, tCertificateCount;
		
		tCertificate = Certificate.NO_CERTIFICATE;
		tCertificateCount = certificates.size ();
		for (tIndex = 0; (tIndex < tCertificateCount) && 
						(tCertificate == Certificate.NO_CERTIFICATE); tIndex++) {
			tPortfolioCertificate = certificates.get (tIndex);
			if (tPortfolioCertificate == aCertificate) {
				tCertificate = tPortfolioCertificate;
				certificates.remove (tIndex);
			}
		}
		
		return tCertificate;
	}
	
	public boolean hasCertificateFor (Corporation aCorporation) {
		boolean tCertificateFound;
		
		tCertificateFound = false;
		for (Certificate tCertificate : certificates)  {
			if (tCertificate.isForThis (aCorporation)) {
				tCertificateFound = true;
			}
		}
		
		return tCertificateFound;
	}
	
	public boolean hasSelectedPrivateOrMinorToExchange () {
		boolean tHasSelectedPrivateOrMinorToExchange;
		
		tHasSelectedPrivateOrMinorToExchange = false;
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPrivateCompany ()) {
				if (tCertificate.isSelected ()) {
					tHasSelectedPrivateOrMinorToExchange = true;
				}
			}
		}
		
		return tHasSelectedPrivateOrMinorToExchange;
	}

	public boolean hasSelectedPrezToExchange () {
		boolean tHasSelectedPrezToExchange;
		
		tHasSelectedPrezToExchange = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isShareCompany ()) {
				if (tCertificate.isPresidentShare ()) {
					if (tCertificate.isSelected ()) {
						tHasSelectedPrezToExchange = true;
					}
				}
			}
		}
		
		return tHasSelectedPrezToExchange;
	}

	public boolean hasSelectedPrivateToBidOn () {
		boolean tHasSelectedPrivateToBidOn;
		
		tHasSelectedPrivateToBidOn = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPrivateCompany ()) {
				if (tCertificate.isSelectedToBidOn ()) {
					tHasSelectedPrivateToBidOn = true;
				}
			}
		}
		
		return tHasSelectedPrivateToBidOn;
	}
	
	public boolean hasSelectedStockToBuy () {
		boolean tHasSelectedStockToBuy;
		
		tHasSelectedStockToBuy = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tHasSelectedStockToBuy = true;
			}
		}
	
		return tHasSelectedStockToBuy;
	}
	
	public Certificate getSelectedStockToSell () {
		Certificate tCertificateToSell = Certificate.NO_CERTIFICATE;
		
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCertificateToSell = tCertificate;
			}
		}

		return tCertificateToSell;
	}
	
	public int getSelectedStockCost () {
		int tSelectedStockCost;
		
		tSelectedStockCost = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tSelectedStockCost = tCertificate.getCost ();
			}
		}
		
		return tSelectedStockCost;
	}
	
	public boolean hasSelectedStocksToSell () {
		boolean tHasSelectedStocksToSell;
		
		tHasSelectedStocksToSell = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tHasSelectedStocksToSell = true;
			}
		}
		
		return tHasSelectedStocksToSell;
	}
	
	public boolean AreAllSelectedStocksSameCorporation () {
		boolean tAreAllSelectedStocksSameCorporation;
		Corporation tCorporation, tPreviousCorporation;
		
		tAreAllSelectedStocksSameCorporation = true;
		tPreviousCorporation = CorporationList.NO_CORPORATION;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCorporation = tCertificate.getCorporation ();
				if (tPreviousCorporation != CorporationList.NO_CORPORATION) {
					if (tCorporation != tPreviousCorporation) {
						tAreAllSelectedStocksSameCorporation = false;
					}
				}
				tPreviousCorporation = tCorporation;
			}
		}
		
		return tAreAllSelectedStocksSameCorporation;
	}
	
	public boolean hasShareCompanyStocks () {
		boolean tHasShareCompanyStocks;
		Corporation tCorporation;
		
		tHasShareCompanyStocks = false;
		for (Certificate tCertificate : certificates) {
			tCorporation = tCertificate.getCorporation ();
			if (tCorporation instanceof ShareCompany) {
				tHasShareCompanyStocks = true;
			}
		}
		
		return tHasShareCompanyStocks;
	}
	
	public boolean hasThisCertificate (Certificate aThisCertificate) {
		boolean tHasThisCertificate = false;
		
		for (Certificate tCertificate : certificates) {
			if (aThisCertificate == tCertificate) {
				tHasThisCertificate = true;
			}
		}
	
		return tHasThisCertificate;
	}

	public boolean isBank () {
		return holder.isBank ();
	}
	
	public boolean isBankPool () {
		return holder.isBankPool ();
	}

	public boolean isCompany () {
		return holder.isCompany ();
	}
	
	public boolean isEmpty () {
		return certificates.isEmpty ();
	}
	
	public boolean isPlayer () {
		return holder.isPlayer ();
	}

	public void printCompactPortfolioInfo () {
		String tCompanyName, tPreviousName;
		
		System.out.println ("Portfolio:");
		if (certificates.size () == 0) {
			System.out.print (">> NO CERTIFICATES IN PORTFOLIO <<");
		} else {
			tPreviousName = "";
			for (Certificate tCertificate : certificates) {
				tCompanyName = tCertificate.getCompanyName ();
				if (!(tCompanyName.equals (tPreviousName))) {
					System.out.println ("");
					System.out.print ("Certificates For " + tCompanyName + " ");
					tPreviousName = tCompanyName;
				}
				System.out.print (tCertificate.getPercentage () + " ");
			}
			System.out.println ("");
		}
	}

	public void printPortfolioInfo () {
		System.out.println ("Portfolio for " + getHolderName () + ":");
		if (certificates.size () == 0) {
			System.out.println (">> NO CERTIFICATES IN PORTFOLIO <<");
		} else {
			for (Certificate tCertificate : certificates) {
				tCertificate.printCertificateInfo ();
			}
		}
		System.out.println ("Total Portfolio Value: " + getPortfolioValue ());
	}
	
	static final Comparator<Certificate> orderByOwner = new Comparator<Certificate> () {
		public int compare (Certificate aCertificate1, Certificate aCertificate2) {
			String tOwner1;
			String tOwner2;
			int tSortOrder;
			int tCert1Percentage, tCert2Percentage;
			
			tOwner1 = aCertificate1.getOwnerName ();
			tOwner2 = aCertificate2.getOwnerName ();
			
			tSortOrder = tOwner1.compareTo (tOwner2);
			if (tSortOrder == 0) {
				tCert1Percentage = aCertificate1.getPercentage ();
				tCert2Percentage = aCertificate2.getPercentage ();
				if (tCert1Percentage > tCert2Percentage) {
					tSortOrder = -1;
				} else if (tCert1Percentage < tCert2Percentage) {
					tSortOrder = 1;
				}
			}
			
			return (tSortOrder);
		}
	};
	
	public void removeAllBids () {
		for (Certificate tCertificate : certificates) {
			tCertificate.removeAllBids ();
		}
	}

	public void sortByOwners () {
		Collections.sort (certificates, orderByOwner);
	}
	
	public boolean transferOneCertificateOwnership (Portfolio aFromPortfolio, Certificate aCertificate) {
		boolean tTransferGood;
		Certificate tThisCertificate, tCertificate;
		String tCompanyAbbrev;
		int tPercentage;
		boolean tIsPresident;
		
		tTransferGood = false;
		if (aFromPortfolio != null) {
			if (aCertificate != Certificate.NO_CERTIFICATE) {
				tCompanyAbbrev = aCertificate.getCompanyAbbrev ();
				tPercentage = aCertificate.getPercentage ();
				tIsPresident = aCertificate.isPresidentShare ();
				tThisCertificate = aFromPortfolio.getCertificate (tCompanyAbbrev, tPercentage, tIsPresident);
				tCertificate = aFromPortfolio.getThisCertificate (tThisCertificate);
				if (tCertificate != Certificate.NO_CERTIFICATE) {
					tCertificate.setOwner (this);
					tCertificate.sortCorporationCertificates ();
					this.addCertificate (tCertificate);
					tTransferGood = true;
				} else {
					System.err.println ("Transfer Certificate Failed since the Certificate could not be found");
					System.err.println ("Looking for " + tCompanyAbbrev + " " + tPercentage + "% as President " + tIsPresident);
					System.err.println ("From the Portfolio of " + aFromPortfolio.getName ());
				}
			} else {
				System.err.println ("Transfer Certificate Failed since Certificate is Null");
			}
		} else {
			System.err.println ("Transfer Certificate Failed since From Portfolio is Null");
		}
		
		return tTransferGood;
	}
	
	public boolean transferOwnership (Portfolio aFromPortfolio, Corporation aCorporation) {
		boolean tTransferGood;
		Certificate tCertificate;
		
		tTransferGood = false;
		if (aFromPortfolio != null) {
			if (aCorporation != null) {
				tCertificate = aFromPortfolio.getCertificateFor (aCorporation);
				if (tCertificate != Certificate.NO_CERTIFICATE) {
					tCertificate.setOwner (this);
					tCertificate.sortCorporationCertificates ();
					this.addCertificate (tCertificate);
					tTransferGood = true;
				}
			}
		}
		
		return tTransferGood;
	}
	
	public void updatePortfolioInfox (String aCorpType, String aSelectedButtonLabel, ItemListener aItemListener, GameManager aGameManager) {
		JPanel tCertificateContainer;
		int tAddLocation = -1;

		tCertificateContainer = buildCertificateContainer (aCorpType, aSelectedButtonLabel, aItemListener, aGameManager);
		if (aCorpType.equals (Corporation.PRIVATE_COMPANY)) {
			tAddLocation = privateIndex;
		} else if (aCorpType.equals (Corporation.COAL_COMPANY)) {
			tAddLocation = coalIndex;
		} else if (aCorpType.equals (Corporation.MINOR_COMPANY)) {
			tAddLocation = minorIndex;
		} else if (aCorpType.equals (Corporation.SHARE_COMPANY)) {
			tAddLocation = shareIndex;
		}
		portfolioInfoJPanel.add (tCertificateContainer, tAddLocation);
		// Note, even though adding Component to Container shows After the Container
		// When I need to remove the previous container, I have to use an index one lower.
		portfolioInfoJPanel.remove (tAddLocation - 1);
		portfolioInfoJPanel.validate ();
	}
	
	public void updateCertificateOwnersInfo () {
		CertificateHolderI tCertificateHolder;
		PortfolioHolderI tPortfolioHolder;
		Player tPlayerOwner;
		Player tPreviousPlayerOwner;
		
		tPreviousPlayerOwner = null;
		for (Certificate tCertificate : certificates) {
			tCertificateHolder = tCertificate.getOwner ();
			if (tCertificateHolder != null) {
				tPortfolioHolder = tCertificateHolder.getPortfolioHolder ();
				if (tPortfolioHolder instanceof Player) {
					tPlayerOwner = (Player) tPortfolioHolder;
					if (tPreviousPlayerOwner != tPlayerOwner) {
						tPlayerOwner.updatePlayerInfo ();
						tPreviousPlayerOwner = tPlayerOwner;
					}
				}
			}
		}
	}
	
	// This method may not be needed
	public void itemStateChanged (ItemEvent aItemEvent) {
		JCheckBox tCheckedButton;

		Object tSourceButton = aItemEvent.getItemSelectable ();
		
		for (Certificate tCertificate : certificates) {
			tCheckedButton = tCertificate.getCheckedButton ();
			if (tSourceButton == tCheckedButton) {
				if (tCheckedButton.isSelected ()) {
//					tAtLeastOneButtonSelected = true;
				}
			}
		}
	}

	private String buildAbbrevAndType (String aAbbrev, String aType) {
		String tAbbrevAndType;
		
		tAbbrevAndType = aAbbrev + aType;
		
		return tAbbrevAndType;
	}
	
	public Container buildOwnershipContainer () {
		Container tOwnershipContainer = null;
		JLabel tCertificateOwnershipLabel;
		List<PortfolioSummary> tPortfolioSummary;
		PortfolioSummary tASummary;
		String tAbbrev, tOwnershipLabel, tNote, tAbbrevAndType1, tAbbrevAndType2;
		int tCount, tPercentage;
		boolean tIsPresident, tHandledCertificate;
		Border tCorporateColorBorder;
		Corporation tCorporation;
		String tType;
		
		if (certificates.size () > 0) {
			tOwnershipContainer = Box.createVerticalBox ();
			tPortfolioSummary = new LinkedList<PortfolioSummary> ();
			for (Certificate tCertificate : certificates) {				
				tType = PortfolioSummary.SHARE_CORP_TYPE;
				tAbbrev = tCertificate.getCompanyAbbrev ();
				if (tAbbrev.length () < 4) {
					tAbbrev += "&nbsp;";
				}
				if (tCertificate.isPrivateCompany ()) {
					tType = PortfolioSummary.PRIVATE_CORP_TYPE;
				} else if (tCertificate.isMinorCompany ()) {
					tType = PortfolioSummary.MINOR_CORP_TYPE;
				} else if (tCertificate.isCoalCompany ()) {
					tType = PortfolioSummary.COAL_CORP_TYPE;
				}
				tAbbrevAndType1 = buildAbbrevAndType (tAbbrev, tType);
				tCount = 1;
				tPercentage = tCertificate.getPercentage ();
				tIsPresident = tCertificate.isPresidentShare ();
				tHandledCertificate = false;
				for (PortfolioSummary tASingleSummary : tPortfolioSummary) {
					// Test with both Abbrev and Type, to be sure to show B&O Private the B&O Share Company Certs when owned by
					// the same player
					tAbbrevAndType2 = buildAbbrevAndType (tASingleSummary.getAbbrev (), tASingleSummary.getType ());
					
					if (tAbbrevAndType1.equals (tAbbrevAndType2)) {
						tASingleSummary.addCount (tCount);
						tASingleSummary.addPercentage (tPercentage);
						tASingleSummary.setIsPresident (tIsPresident);
						tHandledCertificate = true;
					}
				}
				if (! tHandledCertificate) {
					tCorporation = tCertificate.getCorporation ();
					tCorporateColorBorder = tCorporation.setupBorder ();
					tNote = tCorporation.getNote ();
					tASummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident, tCorporateColorBorder, tNote);
					tPortfolioSummary.add (tASummary);
				}
			}
			for (PortfolioSummary tASingleSummary : tPortfolioSummary) {
				tOwnershipLabel = tASingleSummary.getSummary ();
				tCertificateOwnershipLabel = new JLabel (tOwnershipLabel);
				tCorporateColorBorder = tASingleSummary.getCorporateColorBorder ();
				if (tCorporateColorBorder != PortfolioSummary.NO_BORDER) {
					tCertificateOwnershipLabel.setBorder (tCorporateColorBorder);
				}
				tCertificateOwnershipLabel.setToolTipText (tASingleSummary.getNote ());
				tOwnershipContainer.add (tCertificateOwnershipLabel);
			}
		}
		
		return tOwnershipContainer;
	}
}
