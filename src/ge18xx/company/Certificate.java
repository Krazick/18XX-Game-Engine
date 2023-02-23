package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.Comparator;

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
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Bidder;
import ge18xx.player.Bidders;
import ge18xx.player.CashHolderI;
import ge18xx.player.ParPriceFrame;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
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
	public static String NO_OWNER_NAME = "";
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
	public static final String ALREADY_HAVE_MAX = "You already own the Maximum Share Percentage";
	public static final String AT_CERT_LIMIT = "You are already at Certificate Limit";
	public static final String ALREADY_BOUGHT = "You already bought a share this Round";
	public static final String ALREADY_BID_ON_CERT = "You already Bid on this certificate";
	public static final String NO_SHARE_PRICE = "No Share Price set";
	public static final String CANNOT_SELL_PRIVATE = "Cannot sell a Private Company";
	public static final String CANNOT_SELL_MINOR = "Cannot sell a Minor Company";
	public static final String BANK_POOL_AT_LIMIT = "Bank Pool at Share Limit";
	public static final String CANNOT_SELL_PRESIDENT = "Cannot directly sell President Share";
	public static final String CANNOT_EXCHANGE_PRESIDENT = "Cannot exchange President Share";
	public static final String COMPANY_NOT_OPERATED = "This Share Company has NOT operated yet";
	public static final String HAVE_MUST_BUY = "You must buy the Private where COST == DISCOUNT";
	public static final String HAVE_ENOUGH_CASH = "Enough cash to buy the Train, can't sell stock";
	public static final String NO_SALE_FIRST_STOCK_ROUND = "Can't sell Stock in First Stock Round";
	static final int SORT_CERT1_BEFORE_CERT2 = -100;
	static final int SORT_CERT2_BEFORE_CERT1 = 100;
	static final int NO_PERCENTAGE = 0;
	static final float X_LEFT_ALIGNMENT = 0.0f;
	static final float X_CENTER_ALIGNMENT = 0.5f;
	static final float X_RIGHT_ALIGNMENT = 1.0f;
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
	boolean justBought;

	public Certificate (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage,
			CertificateHolderI aOwner) {
		setValues (aCorporation, aIsPresidentShare, aPercentage, aOwner);
		setParValuesCombo (GUI.NO_COMBO_BOX);
		setCheckBox (GUI.NO_CHECK_BOX);
		setFrameButton (checkBox, "");
		setJustBought (false);
	}

	public Certificate (Certificate aCertificate) {
		if (aCertificate != NO_CERTIFICATE) {
			isPresidentShare = aCertificate.isPresidentShare ();
			percentage = aCertificate.getPercentage ();
			allowedOwners = aCertificate.allowedOwners.clone ();
			setCorporation (aCertificate.getCorporation ());
			setOwner (aCertificate.getOwner ());
			setCheckBox (GUI.NO_CHECK_BOX);
			setFrameButton (checkBox, "");
			setParValuesCombo (GUI.NO_COMBO_BOX);
			bidders = new Bidders (this);
			setJustBought (false);
		}
	}

	public void setCheckBox (JCheckBox aCheckBox) {
		checkBox = aCheckBox;
	}

	public void setParValuesCombo (JComboBox<String> aParValuesCombo) {
		parValuesCombo = aParValuesCombo;
	}
	
	public void setJustBought (boolean aJustBought) {
		justBought = aJustBought;
	}
	
	public boolean justBought () {
		return justBought;
	}
	
	private void setFrameButton (JCheckBox aJCheckBox, String aGroupName) {
		if (aJCheckBox != GUI.NO_CHECK_BOX) {
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
		setOwner (CertificateHolderI.NO_OWNER);
		setParValuesCombo (GUI.NO_COMBO_BOX);
		setCheckBox (GUI.NO_CHECK_BOX);
		setFrameButton (checkBox, "");
		bidders = new Bidders (this);
	}

	/**
	 *
	 * Retrieve current Game's Bank Pool Sell Limit as number of Certificates
	 *
	 * @param aGameManager Current Game's GameManager
	 * @return the Bank Pool Share Limit
	 *
	 */
	// TODO Update to use a % Limit instead of Certificate Limit
	public int sellLimit () {
		int tSellLimit;
		int tBankPoolShareLimit, tBankPoolShareCount;
		GameManager tGameManager;
		BankPool tBankPool;

		tGameManager = corporation.getGameManager ();
		tBankPool = tGameManager.getBankPool ();
		tBankPoolShareLimit = getBankPoolShareLimit (tGameManager);
		tBankPoolShareCount = tBankPool.getCertificateCountFor (corporation);
		tSellLimit = tBankPoolShareLimit - tBankPoolShareCount;

		return tSellLimit;
	}

	/**
	 *
	 * Retrieve current Game's Bank Pool Share Limit
	 *
	 * @param aGameManager Current Game's GameManager
	 * @return the Bank Pool Share Limit
	 *
	 */
	// TODO Update to use a % Limit instead of Certificate Limit
	public int getBankPoolShareLimit (GameManager aGameManager) {
		int tBankPoolShareLimit;
		GameInfo tGameInfo;

		tGameInfo = aGameManager.getActiveGame ();
		tBankPoolShareLimit = tGameInfo.getBankPoolShareLimit ();

		return tBankPoolShareLimit;
	}

	public boolean bankPoolAtLimit (GameManager aGameManager) {
		boolean tBankPoolAtLimit;
		BankPool tBankPool;
		int tBankPoolShareLimit, tBankPoolShareCount;

		tBankPoolAtLimit = false;
		tBankPool = aGameManager.getBankPool ();
		tBankPoolShareLimit = getBankPoolShareLimit (aGameManager);
		tBankPoolShareCount = tBankPool.getCertificateCountFor (corporation);
		if (tBankPoolShareCount >= tBankPoolShareLimit) {
			tBankPoolAtLimit = true;
		}

		return tBankPoolAtLimit;
	}

	public JPanel buildCertificateInfoJPanel (String aCheckBoxLabel, ItemListener aItemListener, boolean aIsBankHolder,
											Player aPlayer, GameManager aGameManager) {
		JPanel tCertificateInfoJPanel;
		JLabel tLabel;
		JLabel tLastRevenueLabel;
		JLabel tLoanCountLabel;
		JLabel tDiscountLabel;
		String tRevenueInfo;
		String tToolTip = "";
		int tRevenue, tPrice, tPlayerCash, tDiscount;
		boolean tEnabled = false;
		boolean tPlayerHasEnoughCash, tPlayerHasBidOnThisCert, tPlayerHasEnoughCashToBid;
		boolean tPlayerHasSoldThisCompany, tPlayerHasMaxShares, tPlayerHasBoughtShare;
		boolean tHasMustBuyCertificate, tPlayerAtCertLimit;
		String tCompanyAbbrev;
		String tBoughtShare;
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
				// Update the Par Value Combo Box, and confirm or deny the Player has enough
				// Cash to buy Cheapest.
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
		if (isAPrivateCompany ()) {
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
			if (corporation.gameHasLoans ()) {
				tLoanCountLabel = new JLabel ("Loans: " + corporation.getLoanCount ());
				tCertificateInfoJPanel.add (tLoanCountLabel);
			}
		}

		tPlayerHasEnoughCashToBid = addBidderLabels (tCertificateInfoJPanel, tPlayerCash);

		if (aCheckBoxLabel.equals (Player.SELL_LABEL)) {
			updateSellCheckBox (aCheckBoxLabel, aItemListener, aGameManager, tCertificateInfoJPanel);
		} else if (aCheckBoxLabel.equals (Player.BUY_LABEL) || aCheckBoxLabel.equals (Player.BUY_AT_PAR_LABEL)) {
			if (canBeBought ()) {
				tToolTip = "";
				tEnabled = false;

				if (tPlayerHasEnoughCash) {
					tToolTip = NOT_ENOUGH_CASH;
				} else if (tPlayerHasBoughtShare) {
					tBoughtShare = aPlayer.boughtShare ();
					if (canBuyMultiple ()) {
						if (tBoughtShare.equals (tCompanyAbbrev)) {
							tPlayerHasBoughtShare = false;
							tEnabled = true;
						} else {
							tToolTip = ALREADY_BOUGHT;
						}
					} else {
						tToolTip = ALREADY_BOUGHT;
					}
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
				if (checkBox == GUI.NO_CHECK_BOX) {
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

	private void updateSellCheckBox (String aCheckBoxLabel, ItemListener aItemListener, GameManager aGameManager,
			JPanel aCertificateInfoJPanel) {
		String tGroupName;
		String tNoSaleToolTip;
		boolean tCanBeSold;

		if (! isAPrivateCompany ()) {
			// Only if it is a Share Company, can it be Sold
			// TODO: non-1830 For 1835 with Minors we cannot Sell them
			// either, test for CanBeSold
			tGroupName = getCompanyAbbrev () + " Share";
			if (isPresidentShare ()) {
				tGroupName = updateExchangeCheckBox (aItemListener, aGameManager);
			} else {
				tCanBeSold = canBeSold (aGameManager);
				if (! tCanBeSold) {
					tNoSaleToolTip = getReasonForNoSale (aGameManager);
				} else {
					tNoSaleToolTip = "Can Sell Stock";
				}
				checkBox = setupCheckedButton (aCheckBoxLabel, tCanBeSold, tNoSaleToolTip, aItemListener);

//				if (canBeSold (aGameManager)) {
//					checkBox = setupCheckedButton (aCheckBoxLabel, true, GUI.NO_TOOL_TIP, aItemListener);
//				} else {
//					checkBox = setupCheckedButton (aCheckBoxLabel, false, getReasonForNoSale (aGameManager),
//							aItemListener);
//				}
			}
			setFrameButton (checkBox, tGroupName);
			aCertificateInfoJPanel.add (checkBox);
		} else {
			// If the CheckBox is created, set it to invisible (don't want it on Explain Table)
			if (checkBox != GUI.NO_CHECK_BOX) {
				checkBox.setVisible (false);
			}
		}
	}

	private String updateExchangeCheckBox (ItemListener aItemListener, GameManager aGameManager) {
		String tGroupName;
		tGroupName = getCompanyAbbrev () + " President Share";
		if (canBeExchanged (aGameManager)) {
			checkBox = setupCheckedButton (Player.EXCHANGE_LABEL, true, GUI.NO_TOOL_TIP, aItemListener);
		} else {
			// TODO -- Find the Reason cannot Exchange:
			// No other player owns at least 20%, Company hasn't operated yet, Bank Pool cannot hold enough
			//
			checkBox = setupCheckedButton (Player.EXCHANGE_LABEL, false, CANNOT_EXCHANGE_PRESIDENT,
					aItemListener);
		}
		return tGroupName;
	}

	public void enableParValuesCombo (boolean aEnable) {
		if (parValuesCombo != GUI.NO_COMBO_BOX) {
			parValuesCombo.setEnabled (aEnable);
			if (!aEnable) {
				if (parValuesCombo.getItemCount () > 0) {
					parValuesCombo.setSelectedIndex (0);
				}
			}
		}
	}

	public Integer [] buildParValuesCombo (ItemListener aItemListener, JPanel aCertificateInfoJPanel) {
		Integer [] tParValues;
		Dimension tParValueSize;
		GameManager tGameManager;

		tGameManager = corporation.getGameManager ();
		tParValues = tGameManager.getAllStartCells ();
		setParValuesCombo (new JComboBox<String> ());
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

		tCertInfo = getCompanyAbbrev () + " (" + getPercentage () + "%)";
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

	public void updateCheckedButton (String aLabel, boolean aEnabledState, String aToolTip,
			ItemListener aItemListener) {
		checkBox.setText (aLabel);
		checkBox.setEnabled (aEnabledState);
		checkBox.setToolTipText (aToolTip);
		checkBox.setSelected (false);
		checkBox.addItemListener (aItemListener);
	}

	public JCheckBox setupCheckedButton (String aLabel, boolean aEnabledState, String aToolTip,
			ItemListener aItemListener) {
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
		if (corporation.isAShareCompany ()) {
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
			tHalfValue = tPrice / 2;
			tDoubleValue = tPrice * 2;
			tSaleLabel = new JLabel (
					"[ " + Bank.formatCash (tHalfValue) + " - " + Bank.formatCash (tDoubleValue) + " ]");
			tPrivateCertJPanel.add (tSaleLabel);
			tPrivateCertJPanel.add (tPresidentLabel);
			tCheckboxLabel = "Buy";
			if (aAvailableCash < tHalfValue) {
				checkBox = setupCheckedButton (tCheckboxLabel, false, "Not enough cash to buy at half price",
						aItemListener);
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

			if (owner.isABank ()) {
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
		if (isAShareCompany ()) {

			// Company must have Operated before Exchange can happen
			if (didOperate ()) {
				// Can Exchange only if there is a Par Price for the Company
				if (hasParPrice ()) {
					// Only the President Share can be Exchanged.
					if (isPresidentShare) {
						tCanBeExchanged = aGameManager.canBeExchanged (corporation);
						// TODO: Test if the Bank Pool can hold the shares needed to be sold to allow
						// Exchange to happen
					}
				}
			}
		}
		if (isAPrivateCompany ()) {
			tPrivateCompany = (PrivateCompany) corporation;
			tExchangeID = tPrivateCompany.getExchangeID ();
			if (tExchangeID != Corporation.NO_ID) {
				tCanBeExchanged = true;
			}
		}

		return tCanBeExchanged;
	}

	public boolean didOperate () {
		ShareCompany tShareCompany;
		boolean tDidOperate = false;

		if (isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tDidOperate = tShareCompany.didOperate ();
		}

		return tDidOperate;
	}

	public boolean canBeSold (GameManager aGameManager) {
		boolean tCanBeSold;
		ShareCompany tShareCompany;
		ShareCompany tOperatingCompany;

		tCanBeSold = false;
		// Only a Share Company Stock Share can be sold
		if (isAShareCompany ()) {
			// Can sell only if there is a Par Price for the Company
			if (hasParPrice ()) {
				// Some games allow a President Share to be sold.
				if (isPresidentShare) {
					tCanBeSold = aGameManager.canSellPresidentShare ();
				} else if (aGameManager.isFirstStockRound ()) {
					tCanBeSold = false;
				} else {
					if (! bankPoolAtLimit (aGameManager)) {
						if (aGameManager.isOperatingRound ()) {
							tOperatingCompany = (ShareCompany) aGameManager.getOperatingCompany ();
							// During Loading a game, this is not set yet, so the result is false
							if (tOperatingCompany != Corporation.NO_CORPORATION) {
								tShareCompany = (ShareCompany) corporation;
								if (tOperatingCompany.forceBuyEnoughCash ()) {
									tCanBeSold = false;
								} else if (tShareCompany.didOperate ()) {
									tCanBeSold = true;
								} else if (tOperatingCompany.mustBuyCoupon ()) {
									tCanBeSold = operatingCompanyMustBuyCoupon (aGameManager);
								} else {
									tCanBeSold = true;
								}
							}
						} else {
							if (aGameManager.operateBeforeSale ()) {
								if (corporation.didOperate ()) {
									tCanBeSold = true;
								} else {
									tCanBeSold = false;
								}
							} else {
								tCanBeSold = true;
							}
						}
					}
				}
			}
		}

		return tCanBeSold;
	}

	// In the case of a ForceBuyCoupon state, the Certificate flag is set to true
	// If the Company is Operating, 
	// The Company has Loan Count > 0, and 
	// The Company must Pay Loan Interest or Redeem Loan
	// Otherwise return false;
	private boolean operatingCompanyMustBuyCoupon (GameManager aGameManager) {
		boolean tOCMustBuyCoupon;
		Corporation tOperatingCompany;

		tOCMustBuyCoupon = false;

		if (aGameManager.isOperatingRound ()) {
			tOperatingCompany = aGameManager.getOperatingCompany ();
			// During Loading a game, this is not set yet, so the result is false
			if (tOperatingCompany != Corporation.NO_CORPORATION) {
				if (tOperatingCompany.getLoanCount () > 0) {
					if (tOperatingCompany.mustBuyCoupon ()) {
						tOCMustBuyCoupon = true;
					}
				}

			}
		}

		return tOCMustBuyCoupon;
	}
	
	public void clearSelection () {
		if (checkBox != GUI.NO_CHECK_BOX) {
			checkBox.setSelected (false);
		}
	}

	// TODO: Build JUNIT Test Cases for getCost, getValue, getDiscount
	public int getCost () {
		int tCertificateCost;
		int tParPrice;
		
		if (isAShareCompany ()) {
			if (owner.isABankPool ()) {
				tCertificateCost = getValue ();
			} else if (owner.isABank ()) {
				tCertificateCost = getParPrice ();
				if (tCertificateCost == ShareCompany.NO_PAR_PRICE) {
					tParPrice = getComboParValue ();
					tCertificateCost = calcCertificateValue (tParPrice);
				}
			} else {
				tCertificateCost = getValue ();
			}
		} else {
			tCertificateCost = getValue () - getDiscount ();
		}

		return tCertificateCost;
	}

	public int getValue () {
		int iValue;
		int iSharePrice;

		iValue = 0;
		if (corporation.isAShareCompany ()) {
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

	public boolean valueEqualsDiscount () {
		return (getValue () == getDiscount ());
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
		if (tXMLBidders != Bidders.NO_XML_BIDDERS) {
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
		if (isAPrivateCompany ()) {
			tReason = CANNOT_SELL_PRIVATE;
		} else if (isAMinorCompany ()) {
			tReason = CANNOT_SELL_MINOR;
		} else if (aGameManager.isFirstStockRound ()) {
			tReason = NO_SALE_FIRST_STOCK_ROUND;
		} else if (bankPoolAtLimit (aGameManager)) {
			tReason = BANK_POOL_AT_LIMIT;
		} else if (!hasParPrice ()) {
			tReason = NO_SHARE_PRICE;
		} else if (isPresidentShare && (!aGameManager.canSellPresidentShare ())) {
			tReason = CANNOT_SELL_PRESIDENT;
		} else {
			tShareCompany = (ShareCompany) corporation;
			if (aGameManager.isStockRound ()) {
				if (! tShareCompany.didOperate ()) {
					tReason = COMPANY_NOT_OPERATED;
				} else {
					tReason = "Stock Round, Don't know why can't sell -- FIX THIS";
				}
			} else {
				if (tShareCompany.forceBuyEnoughCash ()) {
					tReason = HAVE_ENOUGH_CASH;
				} else {
					tReason = "Operating Round (Force Train Buy) - Don't know why can't sell -- FIX THIS";
				}
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
		if (!NO_PAR_PRICE.equals (tSelectedValue)) {
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

		if (owner != CertificateHolderI.NO_OWNER) {
			if (owner.isAPlayer ()) {
				tOwnerName = owner.getHolderName ();
			} else if (owner.isACorporation ()) {
				tOwnerName = owner.getHolderAbbrev ();
			}
		}

		return tOwnerName;
	}

	public int getParPrice () {
		int tParPrice;

		tParPrice = ShareCompany.NO_PAR_PRICE;
		if (corporation.isAShareCompany ()) {
			ShareCompany tShare = (ShareCompany) corporation;

			tParPrice = tShare.getParPrice ();
		} else if (corporation.isAMinorCompany ()) {
			MinorCompany tMinor = (MinorCompany) corporation;

			tParPrice = tMinor.getValue ();
		} else if (corporation.isAPrivateCompany ()) {
			PrivateCompany tPrivate = (PrivateCompany) corporation;

			tParPrice = tPrivate.getValue ();
		}

		return tParPrice;
	}

	public int getPercentage () {
		return percentage;
	}

	// TODO: 1835 When updating for minor Companies, need to update this
	// appropriately.
	public int getParValue () {
		int iValue;
		int iParPrice;
		float fSinglePercentPrice;

		iValue = 0;
		if (corporation.isAShareCompany ()) {
			iParPrice = getParPrice ();
			fSinglePercentPrice = (float) iParPrice / PhaseInfo.STANDARD_SHARE_SIZE;
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
		if (corporation.isAShareCompany ()) {
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
			tRevenue = iPrivate.getRevenue () * percentage / 100;
		}

		return tRevenue;
	}

	public int getSharePrice () {
		int tSharePrice;

		tSharePrice = 0;
		if (corporation.isAShareCompany ()) {
			ShareCompany tShare = (ShareCompany) corporation;

			tSharePrice = tShare.getSharePrice ();
		} else if (corporation.isAMinorCompany ()) {
			MinorCompany tMinor = (MinorCompany) corporation;

			tSharePrice = tMinor.getValue ();
		}

		return tSharePrice;
	}

	public int calcCertificateValue (int aSharePrice) {
		float fSinglePercentPrice;
		int iValue;

		fSinglePercentPrice = (float) aSharePrice / PhaseInfo.STANDARD_SHARE_SIZE;
		iValue = (int) (fSinglePercentPrice * percentage);

		return iValue;
	}

	public boolean hasParPrice () {
		boolean tHasParPrice;
		ShareCompany tShareCompany;

		if (corporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tHasParPrice = tShareCompany.hasParPrice ();
		} else {
			tHasParPrice = true;
		}

		return tHasParPrice;
	}

	/**
	 * Test if this Certificate is a President Certificate of a Share Company that
	 * has no Par Price Set.
	 *
	 * @return True if this is a President Certificate of a Share Company with no Par Price
	 */
	public boolean noParPriceSet () {
		boolean tNoParPriceSet;

		tNoParPriceSet = false;

		if (isPresidentShare ()) {
			if (isAShareCompany ()) {
				if (! hasParPrice ()) {
					tNoParPriceSet = true;
				}
			}
		}

		return tNoParPriceSet;
	}

	public boolean isForThis (String aCorpAbbrev) {
		return (aCorpAbbrev.equals (corporation.getAbbrev ()));
	}

	public boolean isForThis (Corporation aCorporation) {
		return (aCorporation == corporation);
	}

	public boolean isAMinorCompany () {
		return corporation.isAMinorCompany ();
	}

	public boolean isOwned () {
		PortfolioHolderI tHolder;
		boolean tOwned;

		tOwned = false;
		if (owner != CertificateHolderI.NO_OWNER) {
			tHolder = owner.getPortfolioHolder ();
			if (tHolder != PortfolioHolderI.NO_HOLDER) {
				if ((tHolder.isAPlayer ()) || (tHolder.isACorporation ()) || (tHolder.isABankPool ())) {
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
		if (owner != CertificateHolderI.NO_OWNER) {
			tHolder = owner.getPortfolioHolder ();
			if (tHolder != PortfolioHolderI.NO_HOLDER) {
				if (tHolder.isABankPool ()) {
					tOwned = true;
				}
			}
		}

		return tOwned;
	}

	public boolean isOwnedByBank () {
		boolean tIsOwnedByBank;

		tIsOwnedByBank = !isOwned ();

		return (tIsOwnedByBank);
	}

	public boolean isOwnedbyPlayerOrCorp () {
		PortfolioHolderI tHolder;
		boolean tOwned;

		tOwned = false;
		if (owner != CertificateHolderI.NO_OWNER) {
			tHolder = owner.getPortfolioHolder ();
			if (tHolder != PortfolioHolderI.NO_HOLDER) {
				if ((tHolder.isAPlayer ()) || (tHolder.isACorporation ())) {
					tOwned = true;
				}
			}
		}

		return tOwned;
	}

	public boolean isAPrivateCompany () {
		return corporation.isAPrivateCompany ();
	}

	public boolean isPresidentShare () {
		return isPresidentShare;
	}

	public boolean isSelected () {
		boolean tIsSelected;

		tIsSelected = false;
		if (checkBox != GUI.NO_CHECK_BOX) {
			tIsSelected = checkBox.isSelected ();
		}

		return tIsSelected;
	}

	public boolean isSelectedToBidOn () {
		boolean tIsSelectedToBidOn;

		tIsSelectedToBidOn = false;
		if (isSelected ()) {
			if (checkBox.getText ().equals (Player.BID_LABEL)) {
				tIsSelectedToBidOn = true;
			}
		}
		return tIsSelectedToBidOn;
	}

	public boolean isSelectedToBuy () {
		boolean tIsSelectedToBuy;

		tIsSelectedToBuy = false;
		if (isSelected ()) {
			if (checkBox.getText ().equals (Player.BUY_LABEL) || checkBox.getText ().equals (Player.BUY_AT_PAR_LABEL)) {
				tIsSelectedToBuy = true;
			}
		}

		return tIsSelectedToBuy;
	}

	public boolean isSelectedToBid () {
		boolean tIsSelectedToBid;

		tIsSelectedToBid = false;
		if (isSelected ()) {
			if (checkBox.getText ().equals (Player.BID_LABEL)) {
				tIsSelectedToBid = true;
			}
		}

		return tIsSelectedToBid;
	}

	public boolean isSelectedToExchange () {
		boolean tIsSelectedToExchange;

		tIsSelectedToExchange = false;
		if (isSelected ()) {
			if (checkBox.getText ().equals (Player.EXCHANGE_LABEL)) {
				tIsSelectedToExchange = true;
			}
		}

		return tIsSelectedToExchange;
	}

	/**
	 * Determine if the Certificate has a "SELL" Label on the CheckBox, with no
	 * regards to limits
	 *
	 * @return TRUE if the Certificate has a "SELL" Label on the Checkbox
	 */
	public boolean canBeSold () {
		boolean tHasSaleLabel;

		tHasSaleLabel = false;
		if (checkBox != GUI.NO_CHECK_BOX) {
			if (checkBox.getText ().equals (Player.SELL_LABEL)) {
				tHasSaleLabel = true;
			}
		}

		return tHasSaleLabel;
	}

	public boolean isSelectedToSell () {
		boolean tIsSelectedToSell;

		tIsSelectedToSell = false;
		if (isSelected ()) {
			if (checkBox.getText ().equals (Player.SELL_LABEL)) {
				tIsSelectedToSell = true;
			}
		}

		return tIsSelectedToSell;
	}

	public boolean canBuyMultiple () {
		boolean tCanBuyMultiple = false;
		ShareCompany tShareCompany;

		if (corporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			if (tShareCompany.canBuyMultiple ()) {
				tCanBuyMultiple = true;
			}
		}

		return tCanBuyMultiple;
	}

	public boolean isAShareCompany () {
		return corporation.isAShareCompany ();
	}

	public void printCertificateInfo () {
		String tOwnerName;
		String tCorpType;

		tOwnerName = " >> NOT OWNED <<";
		if (owner != CertificateHolderI.NO_OWNER) {
			tOwnerName = " Owner: " + owner.getHolderName ();
		}
		if (isPresidentShare) {
			System.out.print ("President's ");
		}
		tCorpType = corporation.getType ();
		System.out.print ("Certificate for " + corporation.getName () + " " + tCorpType);
		System.out.println (" is " + percentage + "% with Current Value " + Bank.formatCash (getValue ()) + tOwnerName);
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

	public boolean auctionIsOver () {
		return bidders.auctionIsOver ();
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
		if (checkBox != GUI.NO_CHECK_BOX) {
			checkBox.setSelected (false);
		}
	}

	public void setStateCheckedButton (boolean aEnabledState, String aToolTip) {
		if (checkBox != GUI.NO_CHECK_BOX) {
			checkBox.setEnabled (aEnabledState);
			checkBox.setToolTipText (aToolTip);
		}
	}

	public void setValues (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage,
			CertificateHolderI aOwner) {
		setCorporation (aCorporation);
		setIsPresidentShare (aIsPresidentShare);
		setPercentage (aPercentage);
		setOwner (aOwner);
		bidders = new Bidders (this);
	}

	private void setPercentage (int aPercentage) {
		percentage = aPercentage;
	}

	private void setIsPresidentShare (boolean aIsPresidentShare) {
		isPresidentShare = aIsPresidentShare;
	}

	public void sortCorporationCertificates () {
		corporation.sortCorporationCertificates ();
	}

	/**
	 * Update the Status of the Corporation Ownership to Owned, May Float or Will
	 * Float based on game rules
	 *
	 */
	public void updateCorporationOwnership () {
		ActorI.ActionStates tState;
		ActorI.ActionStates tNewState;
		ShareCompany tShareCompany;
		int tWillFloatPercent;

		tState = corporation.getActionStatus ();
		tNewState = tState;
		tWillFloatPercent = corporation.getWillFloatPercent ();
		if (tState == ActorI.ActionStates.Unowned) {
			tNewState = ActorI.ActionStates.Owned;
			tNewState = updateToMayFloat (tNewState);
		} else if ((tState == ActorI.ActionStates.Owned) || (tState == ActorI.ActionStates.MayFloat)) {
			if (corporation.isAShareCompany ()) {
				tShareCompany = (ShareCompany) corporation;
				if (tShareCompany.getPlayerOrCorpOwnedPercentage () >= tWillFloatPercent) {
					tNewState = ActorI.ActionStates.WillFloat;
				} else {
					tNewState = updateToMayFloat (tNewState);
				}
			}
		}
		if (tNewState != tState) {
			corporation.setStatus (tNewState);
		}
	}

	private ActorI.ActionStates updateToMayFloat (ActorI.ActionStates aCurrentState) {
		ActorI.ActionStates tNewState;
		int tSharesSold;
		int tMinSharesToFloat;
		int tPercentOwned;

		tNewState = aCurrentState;
		if (corporation.isAShareCompany ()) {
			tMinSharesToFloat = corporation.getMinSharesToFloat ();
			tPercentOwned = corporation.getPercentOwned ();
			tSharesSold = tPercentOwned / PhaseInfo.STANDARD_SHARE_SIZE;
			if (tSharesSold >= tMinSharesToFloat) {
				tNewState = ActorI.ActionStates.MayFloat;
			}
		}

		return tNewState;
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

		if (bidders != Bidders.NO_BIDDERS) {
			if (bidders.getCount () > 0) {
				tTotalEscrows += bidders.getTotalEscrows ();
			}
		}

		return tTotalEscrows;
	}

	public boolean isMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		boolean tIsMatchingCertificate = false;

		if ((aAbbrev.equals (corporation.getAbbrev ())) && (aPercentage == percentage)
				&& (aIsPresident == isPresidentShare)) {
			tIsMatchingCertificate = true;
		}

		return tIsMatchingCertificate;
	}

	public void addBiddersInfo (XMLNode aCertificateNode) {

		XMLNodeList tXMLBiddersNodeList;

		tXMLBiddersNodeList = new XMLNodeList (biddersParsingRoutine);
		tXMLBiddersNodeList.parseXMLNodeList (aCertificateNode, Bidders.EN_BIDDERS);
	}

	ParsingRoutineI biddersParsingRoutine = new ParsingRoutineI () {
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
		System.out.println ("Printing all Escrows for all Bidders on the Cert for " + getCompanyAbbrev ());
		bidders.printAllBidderEscrows ();
	}

	public boolean isLoading () {
		return corporation.isLoading ();
	}

	public void updateObservers (String aMessage) {
		corporation.updateObservers (aMessage);
	}
	
	/**
	 * Compare the Percentage of this Certificate with the Certificate Passed In.
	 * If Same percentage, return Zero.
	 * If Certificate passed in is Larger, Sort Certificate1 (this certificate) before the one passed in.
	 * If Certificate passed in is smaller, Sort Certificate1 (this certificate) after the one passed in.
	 *
	 * @param aCertificate The Certificate to compare too.
	 * @return the integer Zero (same), SORT_CERT1_BEFORE_CERT2, SORT_CERT2_BEFORE CERT1
	 *
	 */
	public int comparePercentage (Certificate aCertificate) {
		int tCertificatePercentage;
		int tCompareResult;

		tCertificatePercentage = aCertificate.getPercentage ();
		if (tCertificatePercentage == percentage) {
			tCompareResult = 0;
		} else if (tCertificatePercentage > percentage) {
			tCompareResult = SORT_CERT1_BEFORE_CERT2;
		} else {
			tCompareResult = SORT_CERT2_BEFORE_CERT1;
		}

		return tCompareResult;
	}

	public static Comparator<Certificate> CertificateActiveOrderComparator = new Comparator<Certificate> () {

		@Override
		public int compare (Certificate aCertificate1, Certificate aCertificate2) {
			int tActiveOrderValue;
			Corporation tCorporation1;
			Corporation tCorporation2;

			tCorporation1 = aCertificate1.getCorporation ();
			tCorporation2 = aCertificate2.getCorporation ();
			if (tCorporation1 == tCorporation2) {
				tActiveOrderValue = aCertificate1.comparePercentage (aCertificate2);
			} else {
				tActiveOrderValue = tCorporation1.compareFormed (tCorporation2);
				if (tActiveOrderValue == 0) { // Both Companies are Formed
					tActiveOrderValue = tCorporation1.compareActive (tCorporation2);
				}

				if (tActiveOrderValue == 0) {
					tActiveOrderValue = tCorporation1.compareID (tCorporation2);
				}
			}

			// ascending order
			return tActiveOrderValue;
		}
	};
}
