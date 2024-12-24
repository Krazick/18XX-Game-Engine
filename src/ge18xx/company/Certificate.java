package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.Benefits;
import ge18xx.game.ColorPalette;
import ge18xx.game.FrameButton;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Bidder;
import ge18xx.player.Bidders;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.WinAuctionAction;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import swingTweaks.KButton;

//
//  Certificate.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class Certificate implements Comparable<Certificate> {
	public static final ElementName EN_CERTIFICATE = new ElementName ("Certificate");
	public static final ElementName EN_BIDDERS = new ElementName ("Bidders");
	public static final AttributeName AN_DIRECTOR = new AttributeName ("director");
	public static final AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	public static final AttributeName AN_IS_PRESIDENT = new AttributeName ("isPresident");
	public static final AttributeName AN_ALLOWED_OWNERS = new AttributeName ("allowedOwners");
	public static final AttributeName AN_SECOND_ISSUE = new AttributeName ("secondIssue");
	public static final Certificate NO_CERTIFICATE = null;
	public static final String NO_OWNER_NAME = GUI.EMPTY_STRING;
	public static final String SHARE_OWNER = "Share";
	public static final String IPO_OWNER = "IPO";
	public static final String PLAYER_OWNER = "Player";
	public static final String NO_CERTIFICATE_NAME = GUI.EMPTY_STRING;
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
	public static final String CANNOT_SELL_PRESIDENT = "Cannot directly sell President Share";
	public static final String CANNOT_EXCHANGE_PRESIDENT = "Cannot exchange President Share";
	public static final String COMPANY_NOT_OPERATED = "This Share Company has NOT operated yet";
	public static final String HAVE_MUST_BUY = "You must buy the Private where COST == DISCOUNT";
	public static final String HAVE_ENOUGH_CASH = "Enough cash to buy the Train, can't sell stock";
	public static final String NO_SALE_FIRST_STOCK_ROUND = "Can't sell Stock in First Stock Round";
	public static final String SELECT_PAR_PRICE = "Select Par Price for Purchase";
	public static final String CERTIFICATE_NOT_SELECTED = "Certificate not Selected for Purchase";
	public static final String NO_PAR_PRICE = "???";
	public static final String BANK_POOL_AT_LIMIT = " at Share Limit";
	public static final String BANK_POOL_OWNER = "Bank";
	public static final int SORT_CERT1_BEFORE_CERT2 = -100;
	public static final int SORT_CERT2_BEFORE_CERT1 = 100;
	public static final int NO_PERCENTAGE = 0;
	public static final float X_LEFT_ALIGNMENT = 0.0f;
	public static final float X_CENTER_ALIGNMENT = 0.5f;
	public static final float X_RIGHT_ALIGNMENT = 1.0f;
	
	JCheckBox checkBox;
	JComboBox<String> parValuesCombo;
	KButton infoButton;
	boolean isPresidentShare;
	boolean secondIssue;
	int percentage;
	String [] allowedOwners = null;
	Bidders bidders;
	FrameButton frameButton;
	Corporation corporation;
	CertificateHolderI owner;
	CertificateInfoFrame infoFrame;

	public Certificate (XMLNode aNode, Corporation aCorporation) {
		String tAllowedOwners;
		boolean tIsPresidentShare;
		boolean tSecondIssue;
		int tPercentage;
		
		tIsPresidentShare = aNode.getThisBooleanAttribute (AN_DIRECTOR);
		tPercentage = aNode.getThisIntAttribute (AN_PERCENTAGE);
		tSecondIssue = aNode.getThisBooleanAttribute (AN_SECOND_ISSUE);
		
		tAllowedOwners = aNode.getThisAttribute (AN_ALLOWED_OWNERS);
		if (tAllowedOwners != GUI.NULL_STRING) {
			allowedOwners = tAllowedOwners.split (GUI.COMMA);
		}
		
		setValues (aCorporation, tIsPresidentShare, tPercentage);
		setSecondIssue (tSecondIssue);

		initCommon (CertificateHolderI.NO_OWNER);
	}
	
	public Certificate (Certificate aCertificate) {
		if (aCertificate != NO_CERTIFICATE) {
			setValues (aCertificate.getCorporation (), aCertificate.isPresidentShare (),
						aCertificate.getPercentage ());
			allowedOwners = aCertificate.allowedOwners.clone ();
			setSecondIssue (aCertificate.isSecondIssue ());
			initCommon (aCertificate.getOwner ());
		}
	}

	public Certificate (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage,
			CertificateHolderI aOwner) {
		setValues (aCorporation, aIsPresidentShare, aPercentage);
		initCommon (aOwner);
	}
	
	public void setValues (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage) {
		setCorporation (aCorporation);
		setIsPresidentShare (aIsPresidentShare);
		setPercentage (aPercentage);
	}

	private void initCommon (CertificateHolderI aOwner) {
		setOwner (aOwner);
		setCheckBox (GUI.NO_CHECK_BOX);
		setFrameButton (checkBox, GUI.EMPTY_STRING);
		setParValuesCombo (GUI.NO_COMBO_BOX);
		setBidders (new Bidders (this));
	}

	public void addBiddersInfo (XMLNode aCertificateNode) {
		XMLNodeList tXMLBiddersNodeList;
		
		tXMLBiddersNodeList = new XMLNodeList (biddersParsingRoutine);
		tXMLBiddersNodeList.parseXMLNodeList (aCertificateNode, Bidders.EN_BIDDERS);
	}

	public ParsingRoutineI biddersParsingRoutine = new ParsingRoutineI () {
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

	public void setupInfoBuffon () {
		infoButton = new KButton ("Info");
		if (infoFrame == CertificateInfoFrame.NO_CERTIFICATE_INFO_FRAME) {
			setupInfoFrame (corporation);
		}
		infoButton.setActionCommand (CertificateInfoFrame.GET_INFO);
		infoButton.addActionListener (infoFrame);
	}

	public void setupInfoFrame (Corporation aCorporation) {
		GameManager tGameManager;
		
		tGameManager = aCorporation.getGameManager ();
		infoFrame = new CertificateInfoFrame (this, tGameManager);
	}

	public CertificateInfoFrame getCertificateInfoFrame () {
		return infoFrame;
	}
	
	public KButton getInfoButton () {
		return infoButton;
	}
	
	public void fillCertificateInfo (GameManager aGameManager) {
		if (infoFrame != CertificateInfoFrame.NO_CERTIFICATE_INFO_FRAME) {
			infoFrame.setGameManager ("Certificate Info Frame", aGameManager);
			setCertificateInfoDialog (this);
		}
	}
	
	public void setCertificateInfoDialog (Certificate aCertificate) {
		infoFrame.setCertificate (aCertificate);
	}

	public void setCheckBox (JCheckBox aCheckBox) {
		checkBox = aCheckBox;
	}

	public void setParValuesCombo (JComboBox<String> aParValuesCombo) {
		parValuesCombo = aParValuesCombo;
	}
	
	private void setFrameButton (JCheckBox aJCheckBox, String aGroupName) {
		if (aJCheckBox != GUI.NO_CHECK_BOX) {
			frameButton = new FrameButton (aJCheckBox, aGroupName);
		}
	}

	public FrameButton getFrameButton () {
		return frameButton;
	}

	public void hideFrameButton () {
		if (frameButton != FrameButton.NO_FRAME_BUTTON) {
			frameButton.setVisible (false);
		}
	}

	public void showFrameButton () {
		if (frameButton != FrameButton.NO_FRAME_BUTTON) {
			frameButton.setVisible (true);
		}
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
		GameManager tGameManager;
		BankPool tBankPool;
		int tSellLimit;
		int tBankPoolShareLimit;
		int tBankPoolShareCount;

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
		GameInfo tGameInfo;
		int tBankPoolShareLimit;

		tGameInfo = aGameManager.getActiveGame ();
		tBankPoolShareLimit = tGameInfo.getBankPoolShareLimit ();

		return tBankPoolShareLimit;
	}

	public boolean bankPoolAtLimit (GameManager aGameManager) {
		BankPool tBankPool;
		boolean tBankPoolAtLimit;
		int tBankPoolShareLimit;
		int tBankPoolShareCount;

		tBankPoolAtLimit = false;
		tBankPool = aGameManager.getBankPool ();
		tBankPoolShareLimit = getBankPoolShareLimit (aGameManager);
		tBankPoolShareCount = tBankPool.getCertificateCountFor (corporation);
		if (tBankPoolShareCount >= tBankPoolShareLimit) {
			tBankPoolAtLimit = true;
		}

		return tBankPoolAtLimit;
	}

	public void handlePrice (String aCheckBoxLabel, ItemListener aItemListener, boolean aIsBankHolder, 
							JPanel aCertificateInfoJPanel, CertificateFlags aCertificateFlags) {
		JLabel tLabel;
		Integer [] tParValues;
		boolean tPlayerHasEnoughCash;
		int tPrice;

		if (aCheckBoxLabel.equals (GUI.EMPTY_STRING)) {
			tPrice = getValue ();
		} else {
			if (aIsBankHolder && aCheckBoxLabel.equals (Player.BUY_AT_PAR_LABEL)) {
				if (hasParPrice ()) {
					tPrice = getParPrice ();
					aCertificateFlags.enoughPlayerCash (tPrice);
				} else {
					tParValues = buildParValuesCombo (aItemListener, aCertificateInfoJPanel);
					// Update the Par Value Combo Box, and confirm or deny the Player has enough
					// Cash to buy Cheapest.
					tPlayerHasEnoughCash = updateParValuesComboBox (parValuesCombo, tParValues,
									aCertificateFlags.getPlayerCash ());
					aCertificateFlags.setPlayerHasEnoughCash (tPlayerHasEnoughCash);
					tPrice = 0;
				}
			} else {
				tPrice = getValue ();
				aCertificateFlags.enoughPlayerCash (tPrice);
			}
		}
		if (tPrice != 0) {
			tLabel = new JLabel ("Price: " + Bank.formatCash (tPrice));
			aCertificateInfoJPanel.add (tLabel);
		}
	}
	
	public JPanel buildCertificateInfoJPanel (String aCheckBoxLabel, ItemListener aItemListener, 
						boolean aIsBankHolder, Player aPlayer, GameManager aGameManager) {
		JPanel tCertificateInfoJPanel;
		JLabel tDiscountLabel;
		CertificateFlags tCertificateFlags;
		boolean tPlayerHasSoldThisCompany;
		boolean tPlayerHasMaxShares;
		boolean tPlayerAtCertLimit;
		String tCompanyAbbrev;
		String tBoughtShare;
		String tToolTip;
		String tGroupName;
		int tDiscount;

		if (aPlayer != Player.NO_PLAYER) {
			tCompanyAbbrev = getCompanyAbbrev ();
			tPlayerHasSoldThisCompany = aPlayer.hasSoldCompany (tCompanyAbbrev);
			tPlayerHasMaxShares = aPlayer.hasMaxShares (tCompanyAbbrev);
			tPlayerAtCertLimit = aPlayer.atCertLimit ();
		} else {
			tCompanyAbbrev = "NONE";
			tPlayerHasSoldThisCompany = false;
			tPlayerHasMaxShares = false;
			tPlayerAtCertLimit = false;
		}
		
		tCertificateFlags = new CertificateFlags (this, aPlayer);
		tCertificateInfoJPanel = buildBasicCertInfoJPanel ();
		
		handlePrice (aCheckBoxLabel, aItemListener, aIsBankHolder, tCertificateInfoJPanel, tCertificateFlags);
		addRevenueLabel (tCertificateInfoJPanel);
		addLoanLabel (tCertificateInfoJPanel);

		tCertificateFlags.setPlayerHasEnoughToCashToBid (addBidderLabels (tCertificateInfoJPanel, 
								tCertificateFlags.getPlayerCash ()));

		tToolTip = GUI.EMPTY_STRING;
		if (aCheckBoxLabel.equals (Player.SELL_LABEL)) {
			updateSellCheckBox (aCheckBoxLabel, aItemListener, aGameManager, tCertificateInfoJPanel);
		} else if (aCheckBoxLabel.equals (Player.BUY_LABEL) || 
					aCheckBoxLabel.equals (Player.BUY_AT_PAR_LABEL)) {
			if (canBeBought ()) {
				tCertificateFlags.setEnabled (false);			
				if (! tCertificateFlags.playerHasEnoughCash ()) {
					tToolTip = NOT_ENOUGH_CASH;
				} else if (tCertificateFlags.playerHasBoughtShare ()) {
					tBoughtShare = aPlayer.boughtShare ();
					if (canBuyMultiple ()) {
						if (tBoughtShare.equals (tCompanyAbbrev)) {
							tCertificateFlags.setPlayerHasBoughtShare (false);
							tCertificateFlags.setEnabled (true);
						} else {
							tToolTip = ALREADY_BOUGHT + " " + tCompanyAbbrev;
						}
					} else {
						tToolTip = ALREADY_BOUGHT;
					}
				} else if (tCertificateFlags.playerHasBidOnThisCert ()) {
					tToolTip = ALREADY_BID_ON_CERT;
				} else if (tPlayerHasSoldThisCompany) {
					tToolTip = ALREADY_SOLD;
				} else if (tPlayerHasMaxShares) {
					tToolTip = ALREADY_HAVE_MAX;
				} else if (tPlayerAtCertLimit) {
					tToolTip = AT_CERT_LIMIT;
				} else {
					tCertificateFlags.setEnabled (true);
				}
				if (checkBox == GUI.NO_CHECK_BOX) {
					checkBox = setupCheckedButton (aCheckBoxLabel, tCertificateFlags.enabled (), tToolTip,
								aItemListener);
					tGroupName = getCompanyAbbrev () + " Share";
					if (isAMinorCompany ()) {
						tGroupName = getCorpType () + " " + tGroupName;
					}
					setFrameButton (checkBox, tGroupName);
				} else {
					updateCheckedButton (aCheckBoxLabel, tCertificateFlags.enabled (), tToolTip, aItemListener);
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
			updateBidLabel (aCheckBoxLabel, aItemListener, tCertificateInfoJPanel, tCertificateFlags);
		} else if (aCheckBoxLabel.equals (GUI.EMPTY_STRING)) {

		} else {
			System.err.println ("No label that matches [" + aCheckBoxLabel + "]");
		}
		setupInfoBuffon ();
		tCertificateInfoJPanel.add (infoButton);

		return tCertificateInfoJPanel;
	}

	public void addRevenueLabel (JPanel tCertificateInfoJPanel) {
		JLabel tRevenueLabel;
		String tRevenueInfo;
		int tRevenue;
		
		if (isAPrivateCompany ()) {
			tRevenue = getRevenue ();
			if (tRevenue != Revenue.NO_REVENUE_VALUE) {
				tRevenueInfo = Revenue.LABEL + Bank.formatCash (tRevenue);
				tRevenueLabel = new JLabel (tRevenueInfo);
				tCertificateInfoJPanel.add (tRevenueLabel);
			}
		} else {
			if (corporation.canOperate ()) {
				tRevenueLabel = new JLabel (Revenue.LABEL + corporation.getFormattedThisRevenue ());
				tCertificateInfoJPanel.add (tRevenueLabel);
			}
		}
	}

	public void addLoanLabel (JPanel tCertificateInfoJPanel) {
		JLabel tLoanCountLabel;
		
		if (! isAPrivateCompany ()) {
			if (corporation.gameHasLoans ()) {
				tLoanCountLabel = new JLabel ("Loans: " + corporation.getLoanCount ());
				tCertificateInfoJPanel.add (tLoanCountLabel);
			}
		}
	}

	public void addBenefitLabels (JPanel aCertificateInfoPanel, boolean aAddStrut) {
		JLabel tBenefitLabel;
		PrivateCompany tPrivateCompany;
		MinorCompany tMinorCompany;
		Benefits tAllBenefits;
		List<Benefit> tBenefits;

		if (isAPrivateCompany ()) {
			tPrivateCompany = (PrivateCompany) getCorporation ();
			tAllBenefits = tPrivateCompany.getBenefits ();
			if (tAllBenefits != Benefits.NO_BENEFITS) {
				tBenefits = tAllBenefits.getBenefits ();
				if (tBenefits != Benefits.NO_BENEFITS) {
					for (Benefit tBenefit : tBenefits) {
						tBenefitLabel = tBenefit.getBenefitLabel ();
						if (tBenefitLabel != GUI.NO_LABEL) {
							aCertificateInfoPanel.add (tBenefitLabel);
							if (aAddStrut) {
								aCertificateInfoPanel.add (Box.createVerticalStrut (10));
							}
						}
					}
				}
			}
		} else if (isAMinorCompany ()) {
			tMinorCompany = (MinorCompany) getCorporation ();
			tBenefitLabel = tMinorCompany.getBenefitLabel ();
			if (tBenefitLabel != GUI.NO_LABEL) {
				aCertificateInfoPanel.add (tBenefitLabel);
			}
		}
	}
	
	public String getPercentPrezInfo () {
		String tPercentPrez;
		
		tPercentPrez = getPercentage () + "% ";
		if (isPresidentShare ()) {
			tPercentPrez += " President";
		}
		
		return tPercentPrez;
	}

	public void updateBidLabel (String aCheckBoxLabel, ItemListener aItemListener, JPanel aCertificateInfoJPanel,
								CertificateFlags aCertificateFlags) {
		String tToolTip;
		
		if (canBeBidUpon ()) {
			tToolTip = GUI.EMPTY_STRING;
			aCertificateFlags.setEnabled (false);			
			if (aCertificateFlags.playerHasBoughtShare ()) {
				tToolTip = ALREADY_BOUGHT;
			} else if (aCertificateFlags.playerHasBidOnThisCert ()) {
				tToolTip = ALREADY_BID_ON_CERT;
			} else if (! aCertificateFlags.playerHasEnoughCashToBid ()) {
				tToolTip = NOT_ENOUGH_CASH_TO_BID;
			} else if (aCertificateFlags.playerHasMustBuyCertificate ()) {
				tToolTip = HAVE_MUST_BUY;
			} else {
				aCertificateFlags.setEnabled (true);
			}
			checkBox = setupCheckedButton (aCheckBoxLabel, aCertificateFlags.enabled (), tToolTip, aItemListener);
			setFrameButton (checkBox, getCompanyAbbrev () + " Share");
			aCertificateInfoJPanel.add (checkBox);
		}
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
		boolean tEnableCheckbox;
		String tGroupName;
		String tToolTip;
		String tButtonLabel;
		
		tGroupName = getCompanyAbbrev () + " President Share";
		tEnableCheckbox = false;
		tToolTip = GUI.NO_TOOL_TIP;	
		tButtonLabel = Player.EXCHANGE_LABEL;
		if (canBeExchanged (aGameManager)) {
			tEnableCheckbox = calcCheckboxState ();
			// TODO -- Test if the Checkbox should be "Exchange for YYY" or "Form XXX"
			checkBox = setupCheckedButton (tButtonLabel, tEnableCheckbox, tToolTip, aItemListener);
		} else {
			// TODO -- Find the Reason cannot Exchange:
			// No other player owns at least 20%, Company hasn't operated yet, Bank Pool cannot hold enough
			//
			tToolTip = CANNOT_EXCHANGE_PRESIDENT;
			checkBox = setupCheckedButton (tButtonLabel, tEnableCheckbox, tToolTip, aItemListener);
		}
		
		return tGroupName;
	}

	public boolean calcCheckboxState () {
		MinorCompany tMinorCompany;
		boolean tCalcCheckBoxState;
		
		tCalcCheckBoxState = false;
		if (isAMinorCompany ()) {
			tMinorCompany = (MinorCompany) getCorporation ();
			if (tMinorCompany.canFormUpgrade ()) {
				tCalcCheckBoxState = true;
			} else {
				// TODO Test if the company being exchanged into has been formed (ask Game Manager)
				// if has formed state is TRUE
			}
		}
		
		return tCalcCheckBoxState;
	}
	
	public void enableParValuesCombo (boolean aEnable) {
		String tToolTip;

		if (aEnable) {
			tToolTip = Certificate.SELECT_PAR_PRICE;
		} else {
			tToolTip = Certificate.CERTIFICATE_NOT_SELECTED;
		}

		if (parValuesCombo != GUI.NO_COMBO_BOX) {
			parValuesCombo.setEnabled (aEnable);
			parValuesCombo.setToolTipText (tToolTip);
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
		BoxLayout tInfoBoxLayout;
		CompoundBorder tCertInfoBorder2;
		JPanel tCertificateInfoJPanel;
		JLabel tPrimaryLabel;
		String tCertInfo;
		String tNote;
		
		tCertificateInfoJPanel = new JPanel ();
		tInfoBoxLayout = new BoxLayout (tCertificateInfoJPanel, BoxLayout.Y_AXIS);
		tCertificateInfoJPanel.setLayout (tInfoBoxLayout);
		tCertInfoBorder2 = setupCIPBorder ();
		tCertificateInfoJPanel.setBorder (tCertInfoBorder2);

		tCertInfo = getCompanyAbbrev () + " (" + getPercentage () + "%)";
		if (corporation.isAMinorCompany ()) {
			tCertInfo = "Minor " + tCertInfo;
		}
		if (isPresidentShare) {
			tCertInfo += " PREZ SHARE";
		}
		
		tPrimaryLabel = new JLabel (tCertInfo);
		
		tNote = corporation.getNote ();
		tPrimaryLabel.setToolTipText (tNote);
		tCertificateInfoJPanel.add (tPrimaryLabel);
		
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
		Color tBorderColor;
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
			tBorderColor = getBidderColor ();
			tRegionBorder = BorderFactory.createLineBorder (tBorderColor);
			tInnerBorder = BorderFactory.createLineBorder (tInnerColor, 5);
			tCertInfoBorder2 = BorderFactory.createCompoundBorder (tRegionBorder, tInnerBorder);
		}

		return tCertInfoBorder2;
	}

	public Color getBidderColor () {
		Color tBorderColor;
		ColorPalette tBiddersPalette;
		GameManager tGameManager;
		Corporation tCorporation;
		int tBidderCount;
		
		tCorporation = getCorporation ();
		tGameManager = tCorporation.getGameManager ();
		tBiddersPalette = tGameManager.getBiddersPalette ();
		tBidderCount = bidders.getCount ();
		tBorderColor = (Color) tBiddersPalette.getPaint (tBidderCount);
		
		return tBorderColor;
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
		Player tBidder;
		boolean tPlayerHasEnoughCashToBid;
		String tBidderInfo;
		int tAmount;
		int tHighestBid;
		int tBidderCount;
		int tValue;

		tHighestBid = 0;
		tBidderCount = getNumberOfBidders ();
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
			tPlayerHasEnoughCashToBid = (aPlayerCash >= (tHighestBid + PlayerManager.BID_INCREMENT));
		} else {
			tValue = getValue ();
			tPlayerHasEnoughCashToBid = (aPlayerCash >= (tValue + PlayerManager.BID_INCREMENT));
		}

		return tPlayerHasEnoughCashToBid;
	}

	public Border setupPCPBorder () {
		Border tPanelBorder;
		Border tInnerBorder;
		Border tOuterBorder;
		Border tRaisedBevel;
		Border tLoweredBevel;
		Border tBevelBorder;
		Color tInnerColor;
		Color tBorderColor;
		String tPrivatePresident;
		String tSharePresident;

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
			tBorderColor = getBidderColor ();
			tOuterBorder = BorderFactory.createLineBorder (tBorderColor, 1);
			tPanelBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);
		}

		return tPanelBorder;
	}

	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivateCertJPanel;
		JLabel tPrivateAbbrevLabel;
		JLabel tPresidentLabel;
		JLabel tPriceLabel;
		JLabel tSaleLabel;
		JLabel tRevenueLabel;
		BoxLayout tLayout;
		Border tPanelBorder;
		String tCheckboxLabel;
		int tPrice;
		int tHalfValue;
		int tDoubleValue;
		int tRevenueValue;

		tPanelBorder = setupPCPBorder ();
		tPrivateCertJPanel = new JPanel ();
		tLayout = new BoxLayout (tPrivateCertJPanel, BoxLayout.Y_AXIS);
		tPrivateCertJPanel.setLayout (tLayout);
		tPrivateCertJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tPrivateCertJPanel.setBorder (tPanelBorder);
		tPrivateAbbrevLabel = new JLabel (getCompanyAbbrev ());
		tPrivateAbbrevLabel.setToolTipText (getCompanyName ());
		tPrivateCertJPanel.add (tPrivateAbbrevLabel);
		tPresidentLabel = new JLabel ("Prez: " + getOwnerName ());
		tPrice = getValue ();
		tPriceLabel = new JLabel ("Price: " + Bank.formatCash (tPrice));
		tPrivateCertJPanel.add (tPriceLabel);
		tRevenueValue = getRevenue ();
		tRevenueLabel = new JLabel (Revenue.LABEL + Bank.formatCash (tRevenueValue));
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
		boolean tCanBeOwnedBy;

		tCanBeOwnedBy = false;
		for (String tAllowedOwner : allowedOwners) {
			if (aOwner.equals (tAllowedOwner)) {
				tCanBeOwnedBy = true;
			}
		}

		return tCanBeOwnedBy;
	}

	public boolean onlyOwnedBy (String aOwner) {
		boolean tOnlyOwnedBy;
		
		tOnlyOwnedBy = false;
		if (allowedOwners.length == 1) {
			for (String tAllowedOwner : allowedOwners) {
				if (aOwner.equals (tAllowedOwner)) {
					tOnlyOwnedBy = true;
				}
			}
		}
		
		return tOnlyOwnedBy;
	}
	
	public boolean canBeBidUpon () {
		return true;
	}

	public boolean hasBidOnThisCert (Player aPlayer) {
		boolean tPlayerAlreadyBid;

		tPlayerAlreadyBid = bidders.hasBidOnThisCert (aPlayer);
		
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
		PrivateCompany tPrivateCompany;
		boolean tCanBeExchanged;
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
		boolean tDidOperate;

		tDidOperate = false;
		if (isAShareCompany ()) {
			tShareCompany = (ShareCompany) corporation;
			tDidOperate = tShareCompany.didOperate ();
		}

		return tDidOperate;
	}

	public boolean canBeSold (GameManager aGameManager) {
		ShareCompany tOperatingCompany;
		ShareCompany tShareCompany;
		boolean tCanBeSold;

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
		Corporation tOperatingCompany;
		boolean tOCMustBuyCoupon;

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
		PrivateCompany tPrivate;
		MinorCompany tMinor;
		int iSharePrice;
		int iValue;
		
		iValue = 0;
		if (corporation.isAShareCompany ()) {
			iSharePrice = getSharePrice ();
			iValue = calcCertificateValue (iSharePrice);
		} else if (corporation.isAMinorCompany ()) {
			tMinor = (MinorCompany) corporation;
			iValue = tMinor.getValue ();
		}else if (corporation.isAPrivateCompany ()) {
			tPrivate = (PrivateCompany) corporation;
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

	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLBidders;

		tXMLElement = aXMLDocument.createElement (EN_CERTIFICATE);
		tXMLElement.setAttribute (Corporation.AN_ABBREV, corporation.getAbbrev ());
		tXMLElement.setAttribute (AN_IS_PRESIDENT, isPresidentShare);
		if (secondIssue) {
			tXMLElement.setAttribute (AN_SECOND_ISSUE, secondIssue);
		}
		tXMLElement.setAttribute (AN_PERCENTAGE, percentage);
		if (bidders != Bidders.NO_BIDDERS) {
			tXMLBidders = bidders.getOnlyBiddersElement (aXMLDocument);
			if (tXMLBidders != Bidders.NO_XML_BIDDERS) {
				tXMLElement.appendChild (tXMLBidders);
			}
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
		ShareCompany tShareCompany;
		BankPool tBankPool;
		String tReason;
		
		tReason = NO_REASON;
		if (isAPrivateCompany ()) {
			tReason = CANNOT_SELL_PRIVATE;
		} else if (isAMinorCompany ()) {
			tReason = CANNOT_SELL_MINOR;
		} else if (aGameManager.isFirstStockRound ()) {
			tReason = NO_SALE_FIRST_STOCK_ROUND;
		} else if (bankPoolAtLimit (aGameManager)) {
			tBankPool = corporation.getBankPool ();
			tReason = tBankPool.getName () + BANK_POOL_AT_LIMIT;
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
		Corporation tOtherCorporporation;
		int tCompareValue;
		int tOtherID;
		int tThisID;

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
		String tSelectedValue;
		int tParPrice;

		tParPrice = getParPrice ();
		if (tParPrice == ShareCompany.NO_PAR_PRICE) {
			tSelectedValue = (String) parValuesCombo.getSelectedItem ();
			if (!NO_PAR_PRICE.equals (tSelectedValue)) {
				tParPrice = Integer.parseInt ((String) parValuesCombo.getSelectedItem ());
			}
		}

		return tParPrice;
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

		tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		if (getCorpType ().equals (Corporation.SHARE_COMPANY)) {
			tShareCompany = (ShareCompany) corporation;
		}

		return tShareCompany;
	}

	public boolean countsAgainstCertificateLimit () {
		ShareCompany tShareCompany;
		boolean tCounts;

		tCounts = true;
		if (percentage > NO_PERCENTAGE) {
			if (corporation.isAShareCompany ()) {
				tShareCompany = (ShareCompany) corporation;
				tCounts = tShareCompany.countsAgainstCertificateLimit ();
			}
		}

		return tCounts;
	}

	public CertificateHolderI getOwner () {
		return owner;
	}

	public String getOwnerName () {
		String tOwnerName;
		
		tOwnerName = NO_OWNER_NAME;
		if (owner != CertificateHolderI.NO_OWNER) {
			if (owner.isAPlayer ()) {
				tOwnerName = owner.getHolderName ();
			} else if (owner.isACorporation ()) {
				tOwnerName = owner.getHolderAbbrev ();
			} else if (owner.isABank ()) {
				tOwnerName = BANK_POOL_OWNER;
			}
		}

		return tOwnerName;
	}

	public int getParPrice () {
		PrivateCompany tPrivate;
		ShareCompany tShare;
		MinorCompany tMinor;
		int tParPrice;
		
		tParPrice = ShareCompany.NO_PAR_PRICE;
		if (corporation.isAShareCompany ()) {
			tShare = (ShareCompany) corporation;

			tParPrice = tShare.getParPrice ();
		} else if (corporation.isAMinorCompany ()) {
			tMinor = (MinorCompany) corporation;

			tParPrice = tMinor.getValue ();
		} else if (corporation.isAPrivateCompany ()) {
			tPrivate = (PrivateCompany) corporation;

			tParPrice = tPrivate.getValue ();
		}

		return tParPrice;
	}

	public int getPercentage () {
		return percentage;
	}

	public int getShareCount () {
		int tShareCount;
		
		tShareCount = percentage / PhaseInfo.STANDARD_SHARE_SIZE;
		
		return tShareCount;
	}
	
	public int getParValue () {
		PrivateCompany tPrivate;
		MinorCompany tMinor;
		float tSinglePercentPrice;
		int tParPrice;
		int tValue;

		tValue = 0;
		if (corporation.isAShareCompany ()) {
			tParPrice = getParPrice ();
			tSinglePercentPrice = (float) tParPrice / PhaseInfo.STANDARD_SHARE_SIZE;
			tValue = (int) (tSinglePercentPrice * percentage);
		} else if (corporation.isAMinorCompany ()) {
			tMinor = (MinorCompany) corporation;
			tValue = tMinor.getValue ();
		} else if (corporation.isAPrivateCompany ()) {
			if (hasBidders ()) {
				tValue = bidders.getHighestBid ();
			} else {
				tPrivate = (PrivateCompany) corporation;
				tValue = tPrivate.getValue () - getDiscount ();
			}
		}

		return tValue;
	}

	public Color getRegionColor () {
		Color tColor;

		tColor = Color.white;
		if (corporation.isAShareCompany ()) {
			ShareCompany tShare = (ShareCompany) corporation;
			MarketCell tMarketCell = tShare.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				tColor = tMarketCell.getCertificateBorderColor ();
			}
		}

		return tColor;
	}

	public int getRevenue () {
		int tRevenue;

		tRevenue = Revenue.NO_REVENUE_VALUE;
		if (corporation.isAPrivateCompany ()) {
			tRevenue = corporation.getThisRevenue () * percentage / 100;
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
		int tValue;

		fSinglePercentPrice = (float) aSharePrice / PhaseInfo.STANDARD_SHARE_SIZE;
		tValue = (int) (fSinglePercentPrice * percentage);
		if (secondIssue) {
			tValue = tValue + tValue;
		}
		return tValue;
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
			if (tHolder != PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
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
			if (tHolder != PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
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
			if (tHolder != PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
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

	public boolean isSecondIssue () {
		return secondIssue;
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
			if (checkBox.getText ().equals (Player.BUY_LABEL) || 
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
		ShareCompany tShareCompany;
		boolean tCanBuyMultiple;

		tCanBuyMultiple = false;
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
		String tCertificateInfo;

		tCertificateInfo = getCertificateInfo ();
		System.out.println (tCertificateInfo);
	}

	public String getCertificateInfo () {
		String tCertificateInfo;
		String tCorpType;
		String tOwnerName;
		
		tOwnerName = " >> NOT OWNED <<";
		if (owner != CertificateHolderI.NO_OWNER) {
			tOwnerName = " Owner: " + owner.getHolderName ();
		}
		if (isPresidentShare) {
			tCertificateInfo = "President's ";
		}
		tCorpType = corporation.getType ();
		tCertificateInfo = "Certificate for " + corporation.getName () + " " + tCorpType + " is " + 
					percentage + "% with Current Value " + Bank.formatCash (getValue ()) + tOwnerName;

		return tCertificateInfo;
	}
	
	public boolean sameCertificate (LoadedCertificate aLoadedCertificate) {
		String tAbbrev;
		String tLookingFor;
		boolean tCompareValue;
		boolean tBothNotPrez;
		boolean tSamePercent;
		boolean tSameName;
		boolean tBothPrez;

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
	
	public void setBidders (Bidders aBidders) {
		bidders = aBidders;
	}
	
	public Bidders getBidders () {
		return bidders;
	}
	
	public boolean hasBidders () {
		return bidders.hasBidders ();
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
	
	public void removeAllBidders () {
		setBidders (new Bidders (this));
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

	private void setPercentage (int aPercentage) {
		percentage = aPercentage;
	}

	private void setIsPresidentShare (boolean aIsPresidentShare) {
		isPresidentShare = aIsPresidentShare;
	}

	private void setSecondIssue (boolean aSecondIssue) {
		secondIssue = aSecondIssue;
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
		if ((tState == ActorI.ActionStates.Unowned) || 
			(tState == ActorI.ActionStates.Unformed)) {
			tNewState = ActorI.ActionStates.Owned;
			tNewState = updateToMayFloat (tNewState);
		} else if ((tState == ActorI.ActionStates.Owned) || 
					(tState == ActorI.ActionStates.MayFloat)) {
			if (corporation.isAShareCompany ()) {
				tShareCompany = (ShareCompany) corporation;
				
				if (tShareCompany.isOperational () || tShareCompany.willFloat ()) {
					// If Operational already (operational OR will float) DO NOT Change)
				} else if (tShareCompany.getPlayerOrCorpOwnedPercentage () >= tWillFloatPercent) {
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
		int tMinSharesToFloat;
		int tPercentOwned;
		int tSharesSold;

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
		boolean tEnoughForCheapest;
		int tIndex;
		int tSize;
		int tParValue;
		int tMinSharePrice;
		int tMinPrice;

		tMinPrice = 1000;
		tEnoughForCheapest = false;
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
			if (tMinSharePrice <= aPlayerCash) {
				tEnoughForCheapest = true;
			}
		}

		return tEnoughForCheapest;
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
		boolean tAmIABidder;

		tAmIABidder = bidders.hasBidOnThisCert (aClientName);

		return tAmIABidder;
	}

	public int getTotalEscrows () {
		int tTotalEscrows;

		tTotalEscrows = 0;
		if (bidders != Bidders.NO_BIDDERS) {
			if (bidders.getCount () > 0) {
				tTotalEscrows += bidders.getTotalEscrows ();
			}
		}

		return tTotalEscrows;
	}

	public boolean isMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		boolean tIsMatchingCertificate;

		tIsMatchingCertificate = false;
		if ((aAbbrev.equals (corporation.getAbbrev ())) && (aPercentage == percentage)
				&& (aIsPresident == isPresidentShare)) {
			tIsMatchingCertificate = true;
		}

		return tIsMatchingCertificate;
	}

	public void printAllBiddersEscrows () {
		System.out.println ("Printing all Escrows for all Bidders on the Cert for " + getCompanyAbbrev ());
		bidders.printAllBidderEscrows ();
	}

	public boolean isLoading () {
		return corporation.isLoading ();
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
			Corporation tCorporation1;
			Corporation tCorporation2;
			int tActiveOrderValue;

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
