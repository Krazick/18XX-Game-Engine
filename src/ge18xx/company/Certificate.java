package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.center.Revenue;
import ge18xx.game.FrameButton;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.player.Bidder;
import ge18xx.player.Bidders;
import ge18xx.player.CashHolderI;
import ge18xx.player.ParPriceFrame;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.WinAuctionAction;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

//
//  Certificate.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class Certificate implements Comparable<Certificate> {
	public static AttributeName AN_DIRECTOR = new AttributeName ("director");
	public static AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	public static AttributeName AN_IS_PRESIDENT = new AttributeName ("isPresident");
	public static AttributeName AN_ALLOWED_OWNERS = new AttributeName ("allowedOwners");
	public static ElementName EN_CERTIFICATE = new ElementName ("Certificate");
	public static ElementName EN_BIDDERS = new ElementName ("Bidders");
	private static String NO_OWNER_NAME = "";
	private static String SHARE_OWNER = "Share";
	private static String IPO_OWNER = "IPO";
	private static String PLAYER_OWNER = "Player";
	private static String BANK_POOL_OWNER = "BankPool";
	public static final String NO_CERTIFICATE_NAME = "";
	public static final Certificate NO_CERTIFICATE = null;
	public static final String NO_REASON = ">> NO REASON <<";
	public static final String NOT_ENOUGH_CASH = "You do not have enough cash to Buy";
	public static final String NOT_ENOUGH_CASH_TO_BID = "You do not have enough cash to Raise Bid";
	public static final String ALREADY_SOLD = "You already Sold this Stock in this Round";
	public static final String ALREADY_HAVE_MAX = "You are already own the Maximum Share Percentage";
	public static final String AT_CERT_LIMIT = "You are already at Certificate Limit";
	public static final String ALREADY_BOUGHT = "You already bought a share this Round";
	public static final String ALREADY_BID_ON_CERT = "You already Bid on this certificate";
	public static final String NO_SHARE_PRICE = "No Share Price set";
	public static final String CANNOT_SELL_PRIVATE = "Cannot sell a Private Company";
	public static final String CANNOT_SELL_MINOR = "Cannot sell a Minor Company";
	public static final String CANNOT_SELL_COAL = "Cannot sell a Coal Company";
	public static final String BANK_POOL_AT_LIMIT = "Bank Pool at Share Limit";
	public static final String CANNOT_SELL_PRESIDENT = "Cannot directly sell President Share";
	public static final String CANNOT_EXCHANGE_PRESIDENT = "Cannot exchange President Share";
	public static final String COMPANY_NOT_OPERATED = "This Share Company has NOT operated yet";
	public static final String HAVE_MUST_BUY = "You must buy the Private where COST == DISCOUNT";
	static final int NO_PERCENTAGE = 0;
	static final float X_LEFT_ALIGNMENT = 0.0f;
	static final float X_CENTER_ALIGNMENT = 0.5f;
	static final float X_RIGHT_ALIGNMENT = 1.0f;
	static final CertificateHolderI NO_OWNER = null;
	public static final String NO_PAR_PRICE = "???";
	
//	Border REDLINE_BORDER = BorderFactory.createLineBorder (Color.red);
	Corporation corporation;
	boolean isPresidentShare;
	int percentage;
	CertificateHolderI owner;
	String [] allowedOwners = null;
	JCheckBox checkBox;
	FrameButton frameButton;
	JComboBox<String> parValuesCombo;
	Bidders bidders;
	
	public Certificate () {
		this (Corporation.NO_CORPORATION, false, NO_PERCENTAGE, NO_OWNER);
	}
	
	public Certificate (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage, CertificateHolderI aOwner) {
		setValues (aCorporation, aIsPresidentShare, aPercentage, aOwner);
		parValuesCombo = null;
		checkBox = GUI.NO_CHECK_BOX;
		setFrameButton (checkBox, "");
	}
	
	public Certificate (Certificate aCertificate) {
		if (aCertificate != NO_CERTIFICATE) {
			isPresidentShare = aCertificate.isPresidentShare ();
			percentage = aCertificate.getPercentage ();
			allowedOwners = aCertificate.allowedOwners.clone ();
			setCorporation (aCertificate.getCorporation ());
			setOwner (aCertificate.getOwner ());	
			checkBox = new JCheckBox ("EMPTY");
			checkBox = FrameButton.NO_JCHECKBOX;
			setFrameButton (checkBox, "");
			parValuesCombo = null;
			bidders = new Bidders (this);
		}
	}
	
	private void setFrameButton (JCheckBox aJCheckBox, String aGroupName) {
		if (aJCheckBox != null) {
			frameButton = new FrameButton (aJCheckBox, aGroupName);
		}
	}
	
	public FrameButton getFrameButton () {
		return frameButton;
	}
	
	public void clearFrameButton () {
		if (frameButton != FrameButton.NO_FRAME_BUTTON) {
			frameButton.setVisible (false);
		}
	}
	
	public void resetFrameButton () {
		if (frameButton != FrameButton.NO_FRAME_BUTTON) {
			frameButton.setVisible (true);
		}
	}
	
	public Certificate (XMLNode aNode) {
		String tAllowedOwners = null;
		
		if (AN_DIRECTOR.hasValue ()) {
			isPresidentShare = aNode.getThisBooleanAttribute (AN_DIRECTOR);
		} else {
			System.err.println ("Bad AN_DIRECTOR Object");
		}
		
		if (AN_PERCENTAGE.hasValue ()) {
			percentage = aNode.getThisIntAttribute (AN_PERCENTAGE);
		} else {
			System.err.println ("Bad AN_PERCENTAGE Object");
		}
		if (AN_ALLOWED_OWNERS.hasValue ()) {
			tAllowedOwners = aNode.getThisAttribute (AN_ALLOWED_OWNERS);
		} else {
			System.err.println ("Bad AN_ALLOWED_OWNERS Object");
		}
		
		if (tAllowedOwners != null) {
			allowedOwners = tAllowedOwners.split (",");
		}
		setCorporation (Corporation.NO_CORPORATION);
		setOwner (NO_OWNER);
		checkBox = FrameButton.NO_JCHECKBOX;
		setFrameButton (checkBox, "");
		parValuesCombo = null;
		bidders = new Bidders (this);
	}
	
	public boolean bankPoolAtLimit (GameManager aGameManager) {
		boolean tBankPoolAtLimit;
		BankPool tBankPool;
		GameInfo tGameInfo;
		int tBankPoolShareLimit, tBankPoolShareCount;
		
		tBankPoolAtLimit = false;
		tBankPool = aGameManager.getBankPool ();
		tGameInfo = aGameManager.getActiveGame ();
		tBankPoolShareLimit = tGameInfo.getBankPoolShareLimit ();
		tBankPoolShareCount = tBankPool.getCertificateCountFor (corporation);
		if (tBankPoolShareCount >= tBankPoolShareLimit) {
			tBankPoolAtLimit = true;
		}

		return tBankPoolAtLimit;
	}
	
	public JPanel buildCertificateInfoJPanel (String aCheckBoxLabel, ItemListener aItemListener, 
			boolean aIsBankHolder, Player aPlayer, GameManager aGameManager) {
		JPanel tCertificateInfoJPanel;
		JLabel tLabel, tLastRevenueLabel;
		JLabel tDiscountLabel;
		String tRevenueInfo;
		String tToolTip = "";
		int tRevenue, tPrice, tPlayerCash, tDiscount;
		boolean tEnabled = false;
		boolean tPlayerHasEnoughCash, tPlayerHasBidOnThisCert, tPlayerHasEnoughCashToBid;
		boolean tPlayerHasSoldThisCompany, tPlayerHasMaxShares, tPlayerHasBoughtShare;
		boolean tHasMustBuyCertificate, tPlayerAtCertLimit;
		String tCompanyAbbrev;
		Integer [] tParValues;
		
		if (aPlayer != Player.NO_PLAYER) {
			tCompanyAbbrev = getCompanyAbbrev ();
			tPlayerHasSoldThisCompany = aPlayer.hasSoldCompany (tCompanyAbbrev);
			tPlayerHasMaxShares = aPlayer.hasMaxShares (tCompanyAbbrev);
			tPlayerAtCertLimit = aPlayer.atCertLimit ();
			tPlayerHasBoughtShare = aPlayer.hasBoughtShare ();
			tPlayerHasBidOnThisCert = hasBidOnThisCert (aPlayer);
			tPlayerCash = aPlayer.getCash ();
			tHasMustBuyCertificate = aPlayer.hasMustBuyCertificate ();
		} else {
			tCompanyAbbrev = "NONE";
			tPlayerHasSoldThisCompany = false;
			tPlayerHasMaxShares = false;
			tPlayerAtCertLimit = false;
			tPlayerHasBoughtShare = false;
			tPlayerHasBidOnThisCert = false;
			tPlayerCash = 0;
			tHasMustBuyCertificate = false;
		}

		tCertificateInfoJPanel = buildBasicCertInfoJPanel ();

		tPlayerHasEnoughCash = true;
		tPlayerHasEnoughCashToBid = true;
		if (aIsBankHolder && aCheckBoxLabel.equals (Player.BUY_AT_PAR_LABEL)) {
			if (hasParPrice ()) {
				tPrice = getParPrice ();
				tPlayerHasEnoughCash = (tPrice > tPlayerCash);
			} else {
				tParValues = buildParValuesCombo (aItemListener, tCertificateInfoJPanel);
				// Update the Par Value Combo Box, and confirm or deny the Player has enough Cash to buy Cheapest.
				tPlayerHasEnoughCash = updateParValuesComboBox (parValuesCombo, tParValues, tPlayerCash);
				tPrice = 0;
			}
		} else {
			tPrice = getValue ();
			tPlayerHasEnoughCash = (tPrice > tPlayerCash);
		}
		if (tPrice != 0) {
			tLabel = new JLabel ("Price: " + Bank.formatCash (tPrice));
			tCertificateInfoJPanel.add (tLabel);
		}
		if (isPrivateCompany ()) {
			tRevenue = getRevenue ();
			if (tRevenue != Revenue.NO_REVENUE_VALUE) {
				tRevenueInfo = "Revenue: " + Bank.formatCash (tRevenue);
				tLabel = new JLabel (tRevenueInfo);
				tCertificateInfoJPanel.add (tLabel);
			}			
		} else {
			if (corporation.canOperate ()) {
				tLastRevenueLabel = new JLabel ("Revenue: " + corporation.getFormattedThisRevenue ());
				tCertificateInfoJPanel.add (tLastRevenueLabel);
			}
		}

		tPlayerHasEnoughCashToBid = addBidderLabels (tCertificateInfoJPanel, tPlayerCash);
		
		if (aCheckBoxLabel.equals (Player.SELL_LABEL)) {
			if (! isPrivateCompany ()) {
				// Only if it is a Share Company, can it be Sold 
				// TODO: non-1830 For 1835 with Minors, 1837 with Coal we cannot Sell them either, test for CanBeSold
				if (isPresidentShare ()) {
					if (canBeExchanged (aGameManager)) {
						checkBox = setupCheckedButton (Player.EXCHANGE_LABEL, true, GUI.NO_TOOL_TIP, aItemListener);
						setFrameButton (checkBox, getCompanyAbbrev () + " President Share");
						tCertificateInfoJPanel.add (checkBox);
					} else {
						checkBox = setupCheckedButton (Player.EXCHANGE_LABEL, false, CANNOT_EXCHANGE_PRESIDENT, aItemListener);
						setFrameButton (checkBox, getCompanyAbbrev () + " President Share");
						tCertificateInfoJPanel.add (checkBox);						
					}
				} else if (canBeSold (aGameManager)) {
					checkBox = setupCheckedButton (aCheckBoxLabel, true, GUI.NO_TOOL_TIP, aItemListener);
					setFrameButton (checkBox, getCompanyAbbrev () + " Share");
					tCertificateInfoJPanel.add (checkBox);
				} else {
					checkBox = setupCheckedButton (aCheckBoxLabel, false, getReasonForNoSale (aGameManager), aItemListener);
					setFrameButton (checkBox, getCompanyAbbrev () + " Share");
					tCertificateInfoJPanel.add (checkBox);
				}
			}			
		} else if (aCheckBoxLabel.equals (Player.BUY_LABEL) || aCheckBoxLabel.equals (Player.BUY_AT_PAR_LABEL)) {
			if (canBeBought ()) {
				tToolTip = "";
				tEnabled = false;
				
				if (tPlayerHasEnoughCash) {
					tToolTip = NOT_ENOUGH_CASH;
				} else if (tPlayerHasBoughtShare) {
					tToolTip = ALREADY_BOUGHT;
				} else if (tPlayerHasBidOnThisCert) {
					tToolTip = ALREADY_BID_ON_CERT;
				} else if (tPlayerHasSoldThisCompany) { 
					tToolTip = ALREADY_SOLD;
				} else if (tPlayerHasMaxShares) {
					tToolTip = ALREADY_HAVE_MAX;
				} else if (tPlayerAtCertLimit) {
					tToolTip = AT_CERT_LIMIT;
				} else {
					tEnabled = true;
				}
				if (checkBox == FrameButton.NO_JCHECKBOX) {
					checkBox = setupCheckedButton (aCheckBoxLabel, tEnabled, tToolTip, aItemListener);	
					setFrameButton (checkBox, getCompanyAbbrev () + " Share");
				} else {
					updateCheckedButton (aCheckBoxLabel, tEnabled, tToolTip, aItemListener);
				}
				tCertificateInfoJPanel.add (checkBox);
			} else {
				System.err.println ("Flagged Certificate cannot be Bought");
			}
			tDiscount = getDiscount ();
			if (tDiscount > 0) {
				tDiscountLabel = new JLabel ("Discount: " + Bank.formatCash (tDiscount));
				tCertificateInfoJPanel.add (tDiscountLabel);
			}
		} else if (aCheckBoxLabel.equals (Player.BID_LABEL)) {
			if (canBeBidUpon ()) {
				if (tPlayerHasBoughtShare) {
					tToolTip = ALREADY_BOUGHT;
				} else if (tPlayerHasBidOnThisCert) {
					tToolTip = ALREADY_BID_ON_CERT;
				} else if (tPlayerHasEnoughCashToBid) {
					tToolTip = NOT_ENOUGH_CASH_TO_BID;
				} else if (tPlayerHasEnoughCash) {
					tToolTip = NOT_ENOUGH_CASH;
				} else if (tHasMustBuyCertificate) {
					tToolTip = HAVE_MUST_BUY;
				} else {
					tEnabled = true;
				}
				checkBox = setupCheckedButton (aCheckBoxLabel, tEnabled, tToolTip, aItemListener);					
				setFrameButton (checkBox, getCompanyAbbrev () + " Share");
				tCertificateInfoJPanel.add (checkBox);
			}
		} else if (aCheckBoxLabel.equals ("")) {
//			System.err.println ("CHECKBOX Label equal EMPTY String");
		} else {
			System.err.println ("No label that matches [" + aCheckBoxLabel + "]");	
		}
		
		return tCertificateInfoJPanel;
	}

	public void enableParValuesCombo (boolean aEnable) {
		if (parValuesCombo != null) {
			parValuesCombo.setEnabled (aEnable);
			if (! aEnable) {
				if (parValuesCombo.getItemCount () > 0) {
					parValuesCombo.setSelectedIndex (0);
				}
			}
		}
	}
	
	public Integer[] buildParValuesCombo (ItemListener aItemListener, JPanel aCertificateInfoJPanel) {
		Integer [] tParValues;
		Dimension tParValueSize;
		GameManager tGameManager;
		
		tGameManager = corporation.getGameManager ();
		tParValues = tGameManager.getAllStartCells ();
		parValuesCombo = new JComboBox <String> ();
		tParValueSize = new Dimension (75, 20);
		parValuesCombo.setPreferredSize (tParValueSize);
		parValuesCombo.setMaximumSize (tParValueSize);
		parValuesCombo.addItemListener (aItemListener);
		parValuesCombo.setAlignmentX (Component.LEFT_ALIGNMENT);

		enableParValuesCombo (false);
		aCertificateInfoJPanel.add (parValuesCombo);

		return tParValues;
	}

	public JPanel buildBasicCertInfoJPanel () {
		JPanel tCertificateInfoJPanel;
		JLabel tLabel;
		String tCertInfo;
		String tNote;
		CompoundBorder tCertInfoBorder2;
		
		int tPadding;
		tCertificateInfoJPanel = new JPanel ();
		tCertificateInfoJPanel.setLayout (new BoxLayout (tCertificateInfoJPanel, BoxLayout.Y_AXIS));
		tPadding = 3;
		tCertificateInfoJPanel.setBorder (BorderFactory.createEmptyBorder (tPadding, tPadding, tPadding, tPadding));
		tCertificateInfoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tCertInfoBorder2 = setupCIPBorder ();
		tCertificateInfoJPanel.setBorder (tCertInfoBorder2);
		
		tCertInfo = getCompanyAbbrev () + " (" +  getPercentage () + "%)"; 
		tLabel = new JLabel (tCertInfo);
		tNote = corporation.getNote ();
		tLabel.setToolTipText (tNote);
		tCertificateInfoJPanel.add (tLabel);
		
		if (isPresidentShare) {
			tLabel = new JLabel ("PREZ SHARE");
			tCertificateInfoJPanel.add (tLabel);
		}
		
		return tCertificateInfoJPanel;
	}

	public void updateCheckedButton (String aLabel, boolean aEnabledState, String aToolTip, ItemListener aItemListener) {
		checkBox.setText (aLabel);
		checkBox.setEnabled (aEnabledState);
		checkBox.setToolTipText (aToolTip);
		checkBox.setSelected (false);
		checkBox.addItemListener (aItemListener);
	}
	
	public JCheckBox setupCheckedButton (String aLabel, boolean aEnabledState, String aToolTip, ItemListener aItemListener) {
		JCheckBox tCheckBox;
		
		tCheckBox = new JCheckBox (aLabel);
		tCheckBox.setEnabled (aEnabledState);
		tCheckBox.setToolTipText (aToolTip);
		tCheckBox.setVisible (true);
		tCheckBox.setSelected (false);
		tCheckBox.addItemListener (aItemListener);
		
		return tCheckBox;
	}

	public CompoundBorder setupCIPBorder () {
		CompoundBorder tCertInfoBorder;
		CompoundBorder tCertInfoBorder2;
		Border tRegionBorder;
		Border tInnerBorder;
		Border tCorporateColorBorder;
		Color tRegionColor;
		Color tInnerColor;
		
		tInnerColor = new Color (237, 237, 237);
		if (corporation.isShareCompany ()) {
			tRegionColor = getRegionColor ();
			tRegionBorder = BorderFactory.createLineBorder (tRegionColor, 3);
			tCorporateColorBorder = getCorporateBorder ();
			tInnerBorder = BorderFactory.createLineBorder (tInnerColor, 5);
			tCertInfoBorder = BorderFactory.createCompoundBorder (tRegionBorder, tCorporateColorBorder);
			tCertInfoBorder2 = BorderFactory.createCompoundBorder (tCertInfoBorder, tInnerBorder);
		} else {
			tRegionBorder = BorderFactory.createLineBorder (Color.BLACK);
			tInnerBorder = BorderFactory.createLineBorder (tInnerColor, 5);
			tCertInfoBorder2 = BorderFactory.createCompoundBorder (tRegionBorder, tInnerBorder);
		}
		
		return tCertInfoBorder2;
	}

	public Border getCorporateBorder () {
		Border tCorporateColorBorder;
		Color tCorporateColor;
		
		tCorporateColor = getCorporateColor ();
		tCorporateColorBorder = BorderFactory.createLineBorder (tCorporateColor, 5);
		
		return tCorporateColorBorder;
	}
	
	private Color getCorporateColor () {
		return corporation.getBgColor ();
	}
	
	private boolean addBidderLabels (JPanel aCertificateInfoPanel, int aPlayerCash) {
		JLabel tLabel;
		boolean tPlayerHasEnoughCashToBid = false;
		String tBidderInfo;
		Player tBidder;
		int tAmount;
		int tHighestBid = 0;
		int tBidderCount = getNumberOfBidders ();
		
		if (tBidderCount > 0) {
			for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
				tBidder = (Player) getCashHolderAt (tBidderIndex);
				tAmount = getBidAt (tBidderIndex);
				tBidderInfo = "Bidder: " + tBidder.getName () + " " + Bank.formatCash (tAmount);
				if (tAmount > tHighestBid) {
					tHighestBid = tAmount;
				}
				tLabel = new JLabel (tBidderInfo);
				aCertificateInfoPanel.add (tLabel);
			}
			tPlayerHasEnoughCashToBid = ((tHighestBid + PlayerManager.BID_INCREMENT) > aPlayerCash);
		}

		return tPlayerHasEnoughCashToBid;
	}

	public Border setupPCPBorder () {
		String tPrivatePresident, tSharePresident;
		Border tPanelBorder, tInnerBorder, tOuterBorder, tRaisedBevel, tLoweredBevel, tBevelBorder;
		Color tInnerColor;

		tPrivatePresident = getOwnerName ();
		tSharePresident = corporation.getOperatingOwnerName ();
		tInnerColor = new Color (237, 237, 237);
		tInnerBorder = BorderFactory.createLineBorder (tInnerColor, 5);
		if (tPrivatePresident.equals (tSharePresident)) {
			tRaisedBevel = BorderFactory.createBevelBorder (BevelBorder.RAISED, Color.CYAN, Color.DARK_GRAY);
			tLoweredBevel = BorderFactory.createBevelBorder (BevelBorder.LOWERED, Color.CYAN, Color.DARK_GRAY);
			tBevelBorder = BorderFactory.createCompoundBorder (tRaisedBevel, tLoweredBevel);
			tPanelBorder = BorderFactory.createCompoundBorder (tBevelBorder, tInnerBorder);
		} else {
			tOuterBorder = BorderFactory.createLineBorder (Color.black, 1);
			tPanelBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);
		}
		
		return tPanelBorder;
	}
	
	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivateCertJPanel;
		JLabel tPrivateAbbrevLabel, tPresidentLabel, tPriceLabel, tSaleLabel, tRevenueLabel;
		BoxLayout tLayout;
		Border tPanelBorder;
		String tCheckboxLabel;
		int tPrice, tHalfValue, tDoubleValue, tRevenueValue;
		String tPrivatePresident;
		
		tPrivatePresident = getOwnerName ();
		tPanelBorder = setupPCPBorder ();
		tPrivateCertJPanel = new JPanel ();
		tLayout = new BoxLayout (tPrivateCertJPanel, BoxLayout.Y_AXIS);
		tPrivateCertJPanel.setLayout (tLayout);
		tPrivateCertJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tPrivateCertJPanel.setBorder (tPanelBorder);
		tPrivateAbbrevLabel = new JLabel (getCompanyAbbrev ());
		tPrivateAbbrevLabel.setToolTipText (getCompanyName ());
		tPrivateCertJPanel.add (tPrivateAbbrevLabel);
		tPresidentLabel = new JLabel ("Prez: " + tPrivatePresident);
		tPrice = getValue ();
		tPriceLabel = new JLabel ("Price: " + Bank.formatCash (tPrice));
		tPrivateCertJPanel.add (tPriceLabel);
		tRevenueValue = getRevenue ();
		tRevenueLabel = new JLabel ("Revenue: " + Bank.formatCash (tRevenueValue));
		tPrivateCertJPanel.add (tRevenueLabel);
		
		if (canBeOwnedByShare ()) {
			tHalfValue = tPrice/2;
			tDoubleValue = tPrice * 2;
			tSaleLabel = new JLabel ("[ " + Bank.formatCash (tHalfValue) + " - " + 
					Bank.formatCash (tDoubleValue) + " ]");
			tPrivateCertJPanel.add (tSaleLabel);	
			tPrivateCertJPanel.add (tPresidentLabel);
			tCheckboxLabel = "Buy";
			if (aAvailableCash < tHalfValue) {
				checkBox = setupCheckedButton (tCheckboxLabel, false, "Not enough cash to buy at half price",  aItemListener);
				setFrameButton (checkBox, getCompanyAbbrev () + " Private");
				tPrivateCertJPanel.add (checkBox);
			} else if (corporation.canBuyPrivate ()) {
				checkBox = setupCheckedButton (tCheckboxLabel, true, GUI.NO_TOOL_TIP, aItemListener);
				setFrameButton (checkBox, getCompanyAbbrev () + " Private");
				tPrivateCertJPanel.add (checkBox);
			}
		} else {
			tPrivateCertJPanel.add (tPresidentLabel);		
		}
		
		return tPrivateCertJPanel;
	}
	
	public void updateDiscountLabel () {
		
	}
	
	public boolean canBeOwnedByShare () {
		return canBeOwnedBy (SHARE_OWNER);
	}
	
	public boolean canBeOwnedByPlayer () {
		return canBeOwnedBy (PLAYER_OWNER);
	}
	
	public boolean canBeOwnedByIPO () {
		return canBeOwnedBy (IPO_OWNER);
	}
	
	public boolean canBeOwnedByBankPool () {
		return canBeOwnedBy (BANK_POOL_OWNER);
	}

	public boolean canBeOwnedBy (String aOwner) {
		boolean tCanBeOwnedBy = false;
		
		for (String tAllowedOwner : allowedOwners) {
			if (aOwner.equals (tAllowedOwner)) {
				tCanBeOwnedBy = true;
			}
		}
		
		return tCanBeOwnedBy;
	}
	
	public boolean canBeBidUpon () {
		boolean tCanBeBidUpon;
		
		tCanBeBidUpon = true;
		
		return tCanBeBidUpon;
	}

	public boolean hasBidOnThisCert (Player aPlayer) {
		boolean tPlayerAlreadyBid = bidders.hasBidOnThisCert (aPlayer);

		return tPlayerAlreadyBid;
	}
	
	public boolean canBeBought () {
		boolean tCanBeBought;
		
		tCanBeBought = false;
		
		if (hasParPrice ()) {
			tCanBeBought = true;
		} else {
		
		// If there is no Par Price, and this Certificate is in the Bank, 
		// Then it can be bought -- Will have to Choose Par Price when buying.
			
			if (owner.isBank ()) {
				tCanBeBought = true;
			}
		}
		
		return tCanBeBought;
	}
	
	public boolean canBeExchanged (GameManager aGameManager) {
		boolean tCanBeExchanged;
		PrivateCompany tPrivateCompany;
		int tExchangeID;
		
		tCanBeExchanged = false;
		// Only a Share Company Stock Share can be Exchanged
		if (isShareCompany ()) {
			
			// Company must have Operated before Exchange can ha
			if (didOperate ()) {
				// Can Exchange only if there is a Par Price for the Company
				if (hasParPrice ()) {
					// Only the President Share can be Exchanged.
					if (isPresidentShare) {
						tCanBeExchanged = aGameManager.canBeExchanged (corporation);
						// TODO: Test if the Bank Pool can hold the shares needed to be sold to allow Exchange to happen
					}
				}
			}
		}
		if (isPrivateCompany ()) {
			tPrivateCompany = (PrivateCompany) corporation;
			tExchangeID = tPrivateCompany.getExchangeID ();
			if (tExchangeID != Corporation.NO_ID){
				tCanBeExchanged = true;
			}
		}
	
		return tCanBeExchanged;
	}
	
	public boolean didOperate () {
		ShareCompany tShareCompany;
		boolean tDidOperate = false;
		
		if (isShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tDidOperate = tShareCompany.didOperate ();
		}
		
		return tDidOperate;
	}
	
	public boolean canBeSold (GameManager aGameManager) {
		boolean tCanBeSold;
		ShareCompany tShareCompany;
		
		tCanBeSold = false;
		// Only a Share Company Stock Share can be sold
		if (isShareCompany ()) {
			// Can sell only if there is a Par Price for the Company
			if (hasParPrice ()) {
				// Some games allow a President Share to be sold.
				if (isPresidentShare) {
					tCanBeSold = aGameManager.canSellPresidentShare ();
				} else {
					if (!bankPoolAtLimit (aGameManager)) {
						tShareCompany = (ShareCompany) corporation;
						if (tShareCompany.didOperate ()) {
							tCanBeSold = true;
						} else {
							tCanBeSold = operatingCompanyMustBuyTrain (aGameManager);
						}
					}
				}
			}
		}
		
		return tCanBeSold;
	}
	
	// In the case of a ForceTrainBuy state, the Certificate flag is set to true
	// If the company is Operating, the company has no train, and the company must buy a Train
	// Otherwise return false;
	private boolean operatingCompanyMustBuyTrain (GameManager aGameManager) {
		boolean tOCMustBuyTrain = false;
		Corporation tOperatingCompany;
		
		if (aGameManager.isOperatingRound ()) {
			tOperatingCompany = aGameManager.getOperatingCompany ();
			// During Loading a game, this is not set yet, so the result is false
			if (tOperatingCompany != Corporation.NO_CORPORATION) {
				if (tOperatingCompany.getTrainCount () == 0) {
					if (tOperatingCompany.mustBuyTrain ()) {
						tOCMustBuyTrain = true;
					}
				}
				
			}
		}
		
		return tOCMustBuyTrain;
	}

	public void clearSelection () {
		if (checkBox != FrameButton.NO_JCHECKBOX) {
			checkBox.setSelected (false);
		}
	}

	// TODO: Build JUNIT Test Cases for getCost, getValue, getDiscount
	public int getCost () {
		int tCertificateCost = 0;
		int tParPrice;
		
		if (hasParPrice ()) {
			tCertificateCost = getValue () - getDiscount ();
		} else if (isShareCompany ()) {
			tParPrice = getComboParValue ();
			if (tParPrice != ParPriceFrame.NO_PAR_PRICE_VALUE) {
				tCertificateCost = calcCertificateValue (tParPrice);
			}
		// If it does not have a Share Price, and is not a Share Company
		// Originally this was just a Private, but this is now part of getValue Method
		} else {
			tCertificateCost = getValue () - getDiscount ();
		}
		
		return tCertificateCost;
	}

	public int getValue () {
		int iValue;
		int iSharePrice;
		
		iValue = 0;
		if (corporation.isShareCompany ()) {
			iSharePrice = getSharePrice ();
			iValue = calcCertificateValue (iSharePrice);
		} else if (corporation.isAPrivateCompany ()) {
			PrivateCompany tPrivate = (PrivateCompany) corporation;
			iValue = tPrivate.getValue ();
		}
		
		return iValue;
	}
	
	public int getDiscount () {
		return corporation.getDiscount ();
	}

	public boolean hasBidders () {
		return bidders.hasBidders ();
	}
	
	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tXMLBidders;
		
		tXMLElement = aXMLDocument.createElement (EN_CERTIFICATE);
		tXMLElement.setAttribute (Corporation.AN_ABBREV, corporation.getAbbrev ());
		tXMLElement.setAttribute (AN_IS_PRESIDENT, isPresidentShare);
		tXMLElement.setAttribute (AN_PERCENTAGE, percentage);
		tXMLBidders = bidders.getOnlyBiddersElement (aXMLDocument);
		if (tXMLBidders != Bidders.NO_BIDDERS) {
			tXMLElement.appendChild (tXMLBidders);
		}

		return tXMLElement;
	}
	
	public boolean getMustSell () {
		return corporation.getMustSell ();
	}
	
	public void applyDiscount () {
		corporation.increaseDiscount ();
	}
	
	public String getReasonForNoSale (GameManager aGameManager) {
		String tReason;
		ShareCompany tShareCompany;
		
		tReason = NO_REASON;
		if (isPrivateCompany ()) {
			tReason = CANNOT_SELL_PRIVATE;
		} else if (isMinorCompany ()) {
			tReason = CANNOT_SELL_MINOR;
		} else if (isCoalCompany ()) {
			tReason = CANNOT_SELL_COAL;
		} else if (bankPoolAtLimit (aGameManager)) {
			tReason = BANK_POOL_AT_LIMIT;
		} else if (! hasParPrice ()) {
			tReason = NO_SHARE_PRICE;
		} else if (isPresidentShare && (! aGameManager.canSellPresidentShare ()) ) {
			tReason = CANNOT_SELL_PRESIDENT;
		} else {
			tShareCompany = (ShareCompany) corporation;
			if (! tShareCompany.didOperate ()) {
				tReason = COMPANY_NOT_OPERATED;
			}
		}
		
		return tReason;
	}
	
	@Override
	public int compareTo (Certificate aCertificate) {
		int tCompareValue;
		int tThisID;
		int tOtherID;
		Corporation tOtherCorporporation;
		
		tThisID = corporation.getID ();
		tOtherCorporporation = aCertificate.getCorporation ();
		tOtherID = tOtherCorporporation.getID ();
		tCompareValue = tThisID - tOtherID;
		if (tCompareValue == 0) {
			if (isPresidentShare ()) {
				tCompareValue = -1;
			} else if (aCertificate.isPresidentShare ()) {
				tCompareValue = 1;
			} else {
				tCompareValue = percentage - aCertificate.getPercentage ();
			}
		}
		
		return tCompareValue;
	}
	
	public JCheckBox getCheckedButton () {
		return checkBox;
	}
	
	public JComboBox<String> getComboBox () {
		return parValuesCombo;
	}
	
	public int getComboParValue () {
		int tIParPrice = ParPriceFrame.NO_PAR_PRICE_VALUE;
		String tSelectedValue;
		
		tSelectedValue = (String) parValuesCombo.getSelectedItem ();
		if (! NO_PAR_PRICE.equals (tSelectedValue)) {
			tIParPrice = Integer.parseInt ((String) parValuesCombo.getSelectedItem ());
		}

		return tIParPrice;
	}
	
	public String getCompanyAbbrev () {
		return corporation.getAbbrev ();
	}
	
	public String getCompanyName () {
		return corporation.getName ();
	}
	
	public Corporation getCorporation () {
		return corporation;
	}
	
	public ActorI.ActionStates getCorporationStatus () {
		return corporation.getActionStatus ();
	}
	
	public String getCorpType () {
		return corporation.getType ();
	}
	
	public ShareCompany getShareCompany () {
		ShareCompany tShareCompany;
		
		tShareCompany = null;
		if (getCorpType ().equals (Corporation.SHARE_COMPANY)) {
			tShareCompany = (ShareCompany) corporation;
		}
		
		return tShareCompany;
	}
	
	public boolean countsAgainstCertificateLimit () {
		ShareCompany tShareCompany;
		boolean tCounts;
		
		tCounts = true;
		if (corporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tCounts = tShareCompany.countsAgainstCertificateLimit ();
		}
		
		return tCounts;
	}
	
	public CertificateHolderI getOwner () {
		return owner;
	}
	
	public String getOwnerName () {
		String tOwnerName = NO_OWNER_NAME;
		
		if (owner != NO_OWNER) {
			if (owner.isPlayer ()) {
				tOwnerName = owner.getHolderName ();
			} else if (owner.isCompany ()) {
				tOwnerName = owner.getHolderAbbrev ();
			}
		}
		
		return tOwnerName;
	}
	
	public int getParPrice () {
		int tParPrice;
		
		tParPrice = 0;
		if (corporation.isAShareCompany ()) {
			ShareCompany tShare = (ShareCompany) corporation;
			
			tParPrice = tShare.getParPrice();
		}
		if (corporation.isAMinorCompany ()) {
			MinorCompany tMinor = (MinorCompany) corporation;
			
			tParPrice = tMinor.getValue ();
		}
		if (corporation.isACoalCompany ()) {
			CoalCompany tCoal = (CoalCompany) corporation;
			
			tParPrice = tCoal.getValue ();
		}
		if (corporation.isAPrivateCompany ()) {
			PrivateCompany tPrivate = (PrivateCompany) corporation;
			
			tParPrice = tPrivate.getValue ();
		}
		
		return tParPrice;
	}
	
	public int getPercentage () {
		return percentage;
	}
	
	// TODO: 1835 When updating for minor Companies, need to update this appropriately.
	public int getParValue () {
		int iValue;
		int iParPrice;
		float fSinglePercentPrice;
		
		iValue = 0;
		if (corporation.isShareCompany ()) {
			iParPrice = getParPrice ();
			fSinglePercentPrice = (float) iParPrice/10;
			iValue = (int) (fSinglePercentPrice * percentage);
		} else if (corporation.isAPrivateCompany ()) {
			if (hasBidders ()) {
				iValue = bidders.getHighestBid ();
			} else {
				PrivateCompany tPrivate = (PrivateCompany) corporation;
				iValue = tPrivate.getValue () - getDiscount ();				
			}
		}
		
		return iValue;
	}
	
	public Color getRegionColor () {
		Color tColor;
		
		tColor = Color.white;
		if (corporation.isShareCompany ()) {
			ShareCompany tShare = (ShareCompany) corporation;
			MarketCell tMarketCell = tShare.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				tColor = tMarketCell.getRegionColor ();
			}
		}
		
		return tColor;
	}
	
	public int getRevenue () {
		int tRevenue;
		PrivateCompany iPrivate;
		
		tRevenue = Revenue.NO_REVENUE_VALUE;
		if (corporation.isAPrivateCompany ()) {
			iPrivate = (PrivateCompany) corporation;
			tRevenue = iPrivate.getRevenue () * percentage/100;
		}
		
		return tRevenue;
	}
	
	public int getSharePrice () {
		int tSharePrice;
		
		tSharePrice = 0;
		if (corporation.isShareCompany ()) {
			ShareCompany tShare = (ShareCompany) corporation;
			
			tSharePrice = tShare.getSharePrice();
		}
		if (corporation.isMinorCompany ()) {
			MinorCompany tMinor = (MinorCompany) corporation;
			
			tSharePrice = tMinor.getValue ();
		}
		if (corporation.isACoalCompany ()) {
			CoalCompany tCoal = (CoalCompany) corporation;
			
			tSharePrice = tCoal.getValue ();
		}
		
		return tSharePrice;
	}
	
	public int calcCertificateValue (int aSharePrice) {
		float fSinglePercentPrice;
		int iValue;
		
		fSinglePercentPrice = (float) aSharePrice/10;
		iValue = (int) (fSinglePercentPrice * percentage);
		
		return iValue;
	}
	
	public boolean hasParPrice () {
		boolean tHasParPrice;
		ShareCompany tShareCompany;
		
		if (corporation.isShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tHasParPrice = tShareCompany.hasParPrice ();
		} else {
			tHasParPrice = true;
		}
		
		return tHasParPrice;
	}
	
	public boolean isCoalCompany () {
		return corporation.isACoalCompany ();
	}
	
	public boolean isForThis (String aCorpAbbrev) {
		return (aCorpAbbrev.equals (corporation.getAbbrev ()));
	}
	
	public boolean isForThis (Corporation aCorporation) {
		return (aCorporation == corporation);
	}
	
	public boolean isMinorCompany () {
		return corporation.isMinorCompany ();
	}

	public boolean isOwned () {
		PortfolioHolderI tHolder;
		boolean tOwned;
		
		tOwned = false;
		if (owner != null) {
			tHolder = owner.getPortfolioHolder ();
			if (tHolder != Portfolio.NO_HOLDER) {
				if ((tHolder .isAPlayer ()) || 
					(tHolder.isACorporation ()) || 
					(tHolder.isABankPool ())) {
					tOwned = true;
				}
			}
		}
		
		return tOwned;
	}

	public boolean isOwnedbyBankPool () {
		PortfolioHolderI tHolder;
		boolean tOwned;
		
		tOwned = false;
		if (owner != null) {
			tHolder = owner.getPortfolioHolder ();
			if (tHolder != Portfolio.NO_HOLDER) {
				if (tHolder.isABankPool ()) {
					tOwned = true;
				}
			}
		}
		
		return tOwned;
	}

	public boolean isOwnedByBank () {
		boolean tIsOwnedByBank;
		
		tIsOwnedByBank = ! isOwned ();
		
		return (tIsOwnedByBank);
	}
	
	public boolean isOwnedbyPlayerOrCorp () {
		PortfolioHolderI tHolder;
		boolean tOwned;
		
		tOwned = false;
		if (owner != null) {
			tHolder = owner.getPortfolioHolder ();
			if (tHolder != Portfolio.NO_HOLDER) {
				if ((tHolder.isAPlayer ()) || 
					(tHolder.isACorporation ())) {
					tOwned = true;
				}
			}
		}
		
		return tOwned;
	}
	
	public boolean isPrivateCompany () {
		return corporation.isAPrivateCompany ();
	}
	
	public boolean isPresidentShare () {
		return isPresidentShare;
	}
	
	public boolean isSelected () {
		boolean tIsSelected;
		
		tIsSelected = false;
		if (checkBox != FrameButton.NO_JCHECKBOX) {
			tIsSelected = checkBox.isSelected ();
		}
		
		return tIsSelected;
	}
	
	public boolean isSelectedToBidOn () {
		boolean tIsSelectedToBidOn;
		
		tIsSelectedToBidOn = false;
		if (isSelected ()) {
			if (checkBox.getText().equals (Player.BID_LABEL)) {
				tIsSelectedToBidOn = true;
			}
		}
		return tIsSelectedToBidOn;
	}
	
	public boolean isSelectedToBuy () {
		boolean tIsSelectedToBuy;
		
		tIsSelectedToBuy = false;
		if (isSelected ()) {
			if (checkBox.getText().equals (Player.BUY_LABEL) || 
				checkBox.getText ().equals (Player.BUY_AT_PAR_LABEL)) {
				tIsSelectedToBuy = true;
			}
		}
		
		return tIsSelectedToBuy;
	}
	
	public boolean isSelectedToBid () {
		boolean tIsSelectedToBid;
		
		tIsSelectedToBid = false;
		if (isSelected ()) {
			if (checkBox.getText().equals (Player.BID_LABEL)) {
				tIsSelectedToBid = true;
			}
		}
		
		return tIsSelectedToBid;
	}
	
	public boolean isSelectedToExchange () {
		boolean tIsSelectedToExchange;
		
		tIsSelectedToExchange = false;
		if (isSelected ()) {
			if (checkBox.getText().equals (Player.EXCHANGE_LABEL)) {
				tIsSelectedToExchange = true;
			}
		}
		
		return tIsSelectedToExchange;
	}
	
	/**
	 * Determine if the Certificate has a "SELL" Label on the CheckBox, with no regards to limits
	 * @return TRUE if the Certificate has a "SELL" Label on the Checkbox
	 */
	public boolean canBeSold () {
		boolean tHasSaleLabel;
		
		tHasSaleLabel = false;
		if (checkBox != GUI.NO_CHECK_BOX) {
			if (checkBox.getText().equals (Player.SELL_LABEL)) {
				tHasSaleLabel = true;
			}
		}
		
		return tHasSaleLabel;
	}
	
	public boolean isSelectedToSell () {
		boolean tIsSelectedToSell;
		
		tIsSelectedToSell = false;
		if (isSelected ()) {
			if (checkBox.getText().equals (Player.SELL_LABEL)) {
				tIsSelectedToSell = true;
			}
		}
		
		return tIsSelectedToSell;
	}
	
	public boolean canBuyMultiple () {
		boolean tCanBuyMultiple = false;
		ShareCompany tShareCompany;
		
		if (corporation.isAShareCompany()) {
			tShareCompany = (ShareCompany) corporation;
			if (tShareCompany.canBuyMultiple ()) {
				tCanBuyMultiple = true;
			}
		}
	
		return tCanBuyMultiple;
	}
	
	public boolean isShareCompany () {
		return corporation.isShareCompany ();
	}

	public void printCertificateInfo () {
		String tOwnerName;
		String tCorpType;
		
		tOwnerName = " >> NOT OWNED <<";
		if (owner != null) {
			tOwnerName = " Owner: " + owner.getHolderName ();
		}
		if (isPresidentShare) {
			System.out.print ("President's ");
		}
		tCorpType = corporation.getType ();
		System.out.print ("Certificate for " + corporation.getName () + " " + tCorpType);
		System.out.println (" is " + percentage + 
				"% with Current Value " + Bank.formatCash (getValue ()) + tOwnerName);
	}
	
	public boolean sameCertificate (LoadedCertificate aLoadedCertificate) {
		boolean tCompareValue;
		String tAbbrev;
		String tLookingFor;
		boolean tSameName, tBothPrez, tBothNotPrez, tSamePercent;
		
		tAbbrev = corporation.getAbbrev ();
		tLookingFor = aLoadedCertificate.getCompanyAbbrev ();
		tSameName = (tAbbrev.equals (tLookingFor));
		tSamePercent = (percentage == aLoadedCertificate.getPercentage ());
		tBothPrez = isPresidentShare () && aLoadedCertificate.getIsPresidentShare ();
		tBothNotPrez = (!isPresidentShare ()) && (!aLoadedCertificate.getIsPresidentShare ());
		tCompareValue = false;
		if (tSameName && tSamePercent) {
			if (tBothPrez || tBothNotPrez) {
				tCompareValue = true;
			}
		}
		
		return tCompareValue;	
	}
	
	public void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}
	
	public boolean haveOnlyOneBidderLeft () {
		return bidders.haveOnlyOneBidderLeft ();
	}
	
	public int getNumberOfBidders () {
		return bidders.getNumberOfBidders ();
	}
	
	public String getBidderNames () {
		return bidders.getBidderNames ();
	}
	
	public CashHolderI getCashHolderAt (int aIndex) {
		return bidders.getCashHolderAt (aIndex);
	}
	
	public Bidder getBidderAt (int aIndex) {
		return bidders.getBidderAt (aIndex);
	}
	
	public int getBidAt (int aIndex) {
		return bidders.getBidAt (aIndex);
	}
	
	public void setBidAt (int aIndex, int aAmount) {
		bidders.setBidAt (aIndex, aAmount);
	}
	
	public int getHighestBid () {
		return bidders.getHighestBid ();
	}
	
	public int getLowestBidderIndex () {
		return bidders.getLowestBidderIndex ();
	}

	public int getHighestBidderIndex () {
		return bidders.getHighestBidderIndex ();
	}
	
	public void addBidderInfo (CashHolderI aCashHolder, int aAmount) {
		bidders.addBidderInfo (aCashHolder, aAmount);
	}
	
	public int getRaiseAmount (int aBidderIndex) {
		return bidders.getRaiseAmount (aBidderIndex);
	}
	
	public void passBidFor (int aBidderIndex) {
		bidders.passBidFor (aBidderIndex);
	}
	
	public void raiseBidFor (int aBidderIndex) {
		bidders.raiseBidFor (aBidderIndex);
	}
	
	public void removeBidder (CashHolderI aCashHolder) {
		bidders.removeBidder (aCashHolder);
	}
	
	public void refundBids (WinAuctionAction aWinAuctionAction) {
		bidders.refundBids (aWinAuctionAction);
	}
	
	public void removeAllBids () {
		bidders.removeAllBids ();
	}

	public void setBiddersAsRaiseBid () {
		bidders.setBiddersAsRaiseBid ();
	}
	
	// Needed to set the Discount on XML Load from Save Game
	public void setDiscount (int aDiscount) {
		corporation.setDiscount (aDiscount);
	}
	
	public boolean isATestGame () {
		return corporation.isATestGame ();
	}
	
	public void setOwner (CertificateHolderI aOwner) {
		owner = aOwner;
		if (checkBox != FrameButton.NO_JCHECKBOX) {
			checkBox.setSelected (false);
		}
	}

	public void setStateCheckedButton (boolean aEnabledState, String aToolTip) {
		if (checkBox != FrameButton.NO_JCHECKBOX) {
			checkBox.setEnabled (aEnabledState);
			checkBox.setToolTipText (aToolTip);
		}
	}
	
	public void setValues (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage, CertificateHolderI aOwner) {
		setCorporation (aCorporation);
		isPresidentShare = aIsPresidentShare;
		percentage = aPercentage;
		setOwner (aOwner);
		bidders = new Bidders (this);
	}
	
	public void sortCorporationCertificates () {
		corporation.sortCorporationCertificates ();
	}
	
	public void updateCorporationOwnership () {
		ActorI.ActionStates tState;
		ActorI.ActionStates tNewState;
		ShareCompany tShareCompany;
		
		tState = corporation.getActionStatus ();
		tNewState = tState;
		if (tState == ActorI.ActionStates.Unowned) {
			tNewState = ActorI.ActionStates.Owned;
		} else if ((tState == ActorI.ActionStates.Owned) || (tState == ActorI.ActionStates.MayFloat)) {
			if (corporation.isAShareCompany ()) {
				tShareCompany = (ShareCompany) corporation;
				if (tShareCompany.getPlayerOrCorpOwnedPercentage () >= 60) {
					tNewState = ActorI.ActionStates.WillFloat;
				}
			}
		}
		if (tNewState != tState) {
			corporation.setStatus (tNewState);
		}
	}
	
	public boolean updateParValuesComboBox (JComboBox<String> aParValuesCombo, Integer [] aParValues, int aPlayerCash) {
		int tIndex, tSize, tParValue, tMinSharePrice, tMinPrice;
		boolean tNotEnoughForCheapest;
		
		tMinPrice = 1000;
		tNotEnoughForCheapest = false;
		fillParValueComboBox (aParValuesCombo, aParValues);
		if (aParValues != null) {
			tSize = aParValues.length;
			for (tIndex = 0; tIndex < tSize; tIndex++) {
				tParValue = aParValues [tIndex];
				if (tParValue < tMinPrice) {
					tMinPrice = tParValue;
				}
			}
			tMinSharePrice = calcCertificateValue (tMinPrice);
			if (tMinSharePrice > aPlayerCash) {
				tNotEnoughForCheapest = true;
			}
		}
		
		return tNotEnoughForCheapest;
	}
	
	public void fillParValueComboBox (JComboBox<String> aParValuesCombo, Integer [] aParValues) {
		int tIndex;
		
		if (aParValuesCombo != null) {
			if (aParValues != null) {
				aParValuesCombo.addItem (NO_PAR_PRICE);
				for (tIndex = 0; tIndex < aParValues.length; tIndex++) {
					aParValuesCombo.addItem (aParValues [tIndex] + "");
				}
			} else {
				System.err.println ("***No Par Values to fill into Combo Box ***");
			}			
		} else {
			System.err.println ("***Par Values Combo Box to fill is NULL ***");
		}
	}

	public void setAsPassForBidder (Player aPlayer) {
		bidders.setAsPassForBidder (aPlayer);
	}

	public boolean amIABidder (String aClientName) {
		boolean tAmIABidder = false;
		
		tAmIABidder = bidders.hasBidOnThisCert (aClientName);
		
		return tAmIABidder;
	}

	public int getTotalEscrows () {
		int tTotalEscrows = 0;
		
		if (bidders != null) {
			if (bidders.getCount () > 0) {
				tTotalEscrows += bidders.getTotalEscrows ();
			}
		}
		
		return tTotalEscrows;
	}

	public boolean isMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		boolean tIsMatchingCertificate = false;
		
		if ((aAbbrev.equals(corporation.getAbbrev ())) &&
			(aPercentage == percentage) &&
			(aIsPresident == isPresidentShare)) {
			tIsMatchingCertificate = true;
		}
		
		return tIsMatchingCertificate;
	}

	public void addBiddersInfo (XMLNode aCertificateNode) {

		XMLNodeList tXMLBiddersNodeList;
		
		tXMLBiddersNodeList = new XMLNodeList (biddersParsingRoutine);
		tXMLBiddersNodeList.parseXMLNodeList (aCertificateNode, Bidders.EN_BIDDERS);
	}
			
	ParsingRoutineI biddersParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aBiddersNode) {
			bidders.addBidderInfo (aBiddersNode);
		}
	};

	public CashHolderI getCashHolderByName (String aBidderName) {
		CashHolderI tCashHolder;
		
		tCashHolder = corporation.getCashHolderByName (aBidderName);
		
		return tCashHolder;
	}

	public void printAllBiddersEscrows () {
		System.out.println ("Printing all Escrows for all Bidders on the Cert for " + 
				getCompanyAbbrev ());
		bidders.printAllBidderEscrows ();
	}

	public boolean isLoading () {
		return corporation.isLoading ();
	}
}
