package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

//
//  SharesFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 1/2/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.game.GameManager;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.ChangeMarketCellAction;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import swingTweaks.KButton;

public class ShareCompaniesFrame extends CorporationTableFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TYPE = CorporationList.TYPE_NAMES [2].toString ();
	public static final String BASE_TITLE = BASE_TYPE +  " " + Corporation.COMPANIES;
	public static final ElementName EN_SHARES = new ElementName (BASE_TYPE + "s");
	public static final ShareCompaniesFrame NO_SHARES_FRAME = null;
	KButton sellShareButton;
	KButton buyShareButton;
	KButton payDividendButton;
	KButton holdDividendButton;
	KButton allSoldOutButton;
	JComboBox<String> companyCombo;
	JComboBox<Integer> parValuesCombo;
	Market market;

	public ShareCompaniesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [2], aRoundManager);
		JLabel tLabel;

		// Test Components
		JPanel tNorthComponents = new JPanel ();

		companyCombo = new JComboBox<> ();
		tNorthComponents.add (companyCombo);

		tLabel = new JLabel ("Par Value");
		tNorthComponents.add (tLabel);

		parValuesCombo = new JComboBox<> ();
		tNorthComponents.add (parValuesCombo);

		sellShareButton = new KButton ("Sell Share of Stock");
		sellShareButton.addActionListener (this);
		sellShareButton.setActionCommand ("SellShare");
		tNorthComponents.add (sellShareButton);

		buyShareButton = new KButton ("Buy Share of Stock");
		buyShareButton.addActionListener (this);
		buyShareButton.setActionCommand ("BuyShare");
		tNorthComponents.add (buyShareButton);

		payDividendButton = new KButton ("Pay Dividend");
		payDividendButton.addActionListener (this);
		payDividendButton.setActionCommand ("PayDividend");
		tNorthComponents.add (payDividendButton);

		holdDividendButton = new KButton ("Hold Dividend");
		holdDividendButton.addActionListener (this);
		holdDividendButton.setActionCommand ("HoldDividend");
		tNorthComponents.add (holdDividendButton);

		allSoldOutButton = new KButton ("All Sold Out");
		allSoldOutButton.addActionListener (this);
		allSoldOutButton.setActionCommand ("AllSoldOut");
		tNorthComponents.add (allSoldOutButton);

		add (tNorthComponents, BorderLayout.NORTH);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tTheAction;

		tTheAction = aEvent.getActionCommand ();
		if ("SellShare".equals (tTheAction)) {
			sellStockShare ();
		}
		if ("BuyShare".equals (tTheAction)) {
			buyShare ();
		}
		if ("PayDividend".equals (tTheAction)) {
			payDividend ();
		}
		if ("HoldDividend".equals (tTheAction)) {
			holdDividend ();
		}
		if ("AllSoldOut".equals (tTheAction)) {
			allSoldOut ();
		}
		market.updateAllFrames ();
	}

	// Test Method
	public void allSoldOut () {
		String tCompanyAbbrev;
		ShareCompany tShareCompany;
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		Token tToken;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tMarketCell = tShareCompany.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				tNewMarketCell = tMarketCell.getSoldOutMarketCell ();
				if (tMarketCell != tNewMarketCell) {
					tShareCompany.setSharePrice (tNewMarketCell);
					tToken = tMarketCell.getToken (tCompanyAbbrev);
					if (tToken != Token.NO_TOKEN) {
						tNewMarketCell.addTokenToBottom (tToken);
						moveMarketCellAction (tShareCompany, tCompanyAbbrev, tMarketCell, tNewMarketCell);
					}
					tNewMarketCell.redrawMarket ();
				}
			}
		}
	}

	// Test Method
	public void buyShare () {
		String tCompanyAbbrev;
		int tCurrentParPrice;
		ShareCompany tShareCompany;
		MarketCell tMarketCell;
		Token tToken;
		String tParPrice;
		int tIParPrice;
		int tRow, tCol;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tCurrentParPrice = tShareCompany.getParPrice ();
			if (tCurrentParPrice == ShareCompany.NO_PAR_PRICE) {
				tParPrice = (String) parValuesCombo.getSelectedItem ();
				tIParPrice = Integer.parseInt (tParPrice);
				setShareParPrice (tShareCompany, tIParPrice);
			} else {
				if (tShareCompany.hasStartCell ()) {
					if (tShareCompany.getSharePrice () == 0) {
						tRow = tShareCompany.getStartRow ();
						tCol = tShareCompany.getStartCol ();
						if ((tRow >= 0) && (tCol >= 0)) {
							tMarketCell = market.getMarketCellAtRowCol (tRow, tCol);
							if (tMarketCell != MarketCell.NO_MARKET_CELL) {
								tShareCompany.setSharePrice (tMarketCell);
								tToken = tShareCompany.getMarketToken ();
								if (tToken != Token.NO_TOKEN) {
									tMarketCell.addTokenToBottom (tToken);
									tMarketCell.redrawMarket ();
								} else {
									System.err.println ("No Token Available");
								}
							} else {
								System.err.println ("No Market Cell Found at Row [" + tRow + "] Col [" + tCol + "]");
							}
						} else {
							System.err.println ("Start Row [" + tRow + "] or Col [" + tCol + "] is < Zero");
						}
					} else {
						System.err.println ("Share Price Set Already");
					}
				} else {
					System.err.println ("Par Price already set");
				}
			}
		}
	}

	private void setShareParPrice (ShareCompany aShareCompany, int aParPrice) {
		MarketCell tMarketCell;
		Token tToken;

		tMarketCell = market.findStartCell (aParPrice);
		if (tMarketCell != MarketCell.NO_MARKET_CELL) {
			tToken = aShareCompany.getMarketToken ();
			if (tToken != Token.NO_TOKEN) {
				aShareCompany.setParPrice (aParPrice);
				aShareCompany.setSharePrice (tMarketCell);
				tMarketCell.addTokenToBottom (tToken);
			}
			tMarketCell.redrawMarket ();
		}
	}

	public XMLElement createShareCompaniesListDefinitions (XMLDocument aXMLDocument) {
		return (super.createCompaniesListDefinitions (aXMLDocument));
	}

	public ActorI.ActionStates getCorporationState (String aCorpStateName) {
		return companies.getCorporationState (aCorpStateName);
	}

	public ShareCompany getShareCompanyByID (int aCompanyID) {
		ShareCompany tShareCompany;

		tShareCompany = (ShareCompany) companies.getCorporationByID (aCompanyID);

		return tShareCompany;
	}

	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		ShareCompany tShareCompany;

		tShareCompany = (ShareCompany) companies.getCorporation (aCompanyAbbrev);

		return tShareCompany;
	}

	public int getStockPrice () {
		int tPrice;
		ShareCompany tShareCompany;

		tPrice = ShareCompany.NO_PAR_PRICE;
		tShareCompany = getSelectedShareCompany ();
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tPrice = tShareCompany.getSharePrice ();
		}

		return tPrice;
	}

	public ShareCompany getSelectedShareCompany () {
		ShareCompany tShareCompany;
		String tCompanyAbbrev;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);

		return tShareCompany;
	}

	public Token getToken (String aCompanyAbbrev) {
		Token tToken;
		ShareCompany tShareCompany;

		tShareCompany = (ShareCompany) companies.getCorporation (aCompanyAbbrev);
		tToken = tShareCompany.getMarketToken ();

		return tToken;
	}

	// Test Method
	public void holdDividend () {
		String tCompanyAbbrev;
		ShareCompany tShareCompany;
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		Token tToken;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tMarketCell = tShareCompany.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				tNewMarketCell = tMarketCell.getDividendHoldMarketCell ();
				if (tMarketCell != tNewMarketCell) {
					tShareCompany.setSharePrice (tNewMarketCell);
					tToken = tMarketCell.getToken (tCompanyAbbrev);
					if (tToken != Token.NO_TOKEN) {
						tNewMarketCell.addTokenToBottom (tToken);
						moveMarketCellAction (tShareCompany, tCompanyAbbrev, tMarketCell, tNewMarketCell);
					}
					tNewMarketCell.redrawMarket ();
				}
			}
		}
	}

	public void moveMarketCellAction (ShareCompany aShareCompany, String aCompanyAbbrev, MarketCell aOldMarketCell, 
								MarketCell aNewMarketCell) {
		int tOldStackLocation;
		int tNewStackLocation;
		String tOperatingRoundID;
		ChangeMarketCellAction tChangeMarketCellAction;
		ActionStates tRoundType;
		RoundManager tRoundManager;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tRoundManager = tGameManager.getRoundManager ();
		tOperatingRoundID = tGameManager.getOperatingRoundID ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tChangeMarketCellAction = new ChangeMarketCellAction (tRoundType, tOperatingRoundID, aShareCompany);
		
		tOldStackLocation = aOldMarketCell.getTokenLocation (aCompanyAbbrev);
		tNewStackLocation = aNewMarketCell.getTokenLocation (aCompanyAbbrev);
		tChangeMarketCellAction.addChangeMarketCellEffect (aShareCompany, aOldMarketCell, tOldStackLocation, 
										aNewMarketCell, tNewStackLocation);
		tGameManager.addAction (tChangeMarketCellAction);
	}
	
	// Test Method
	public void payDividend () {
		String tCompanyAbbrev;
		ShareCompany tShareCompany;
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		Token tToken;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tMarketCell = tShareCompany.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				tNewMarketCell = tMarketCell.getDividendPayMarketCell ();
				if (tMarketCell != tNewMarketCell) {
					tShareCompany.setSharePrice (tNewMarketCell);
					tToken = tMarketCell.getToken (tCompanyAbbrev);
					if (tToken != Token.NO_TOKEN) {
						tNewMarketCell.addTokenToBottom (tToken);
						moveMarketCellAction (tShareCompany, tCompanyAbbrev, tMarketCell, tNewMarketCell);
					}
					tNewMarketCell.redrawMarket ();
				}
			}
		}
	}

	public void sellStockShare () {
		String tCompanyAbbrev;
		ShareCompany tShareCompany;
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		Token tToken;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tMarketCell = tShareCompany.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				tNewMarketCell = tMarketCell.getSellShareMarketCell (1);
				if (tMarketCell != tNewMarketCell) {
					tShareCompany.setSharePrice (tNewMarketCell);
					tToken = tMarketCell.getToken (tCompanyAbbrev);
					if (tToken != Token.NO_TOKEN) {
						tNewMarketCell.addTokenToBottom (tToken);
						moveMarketCellAction (tShareCompany, tCompanyAbbrev, tMarketCell, tNewMarketCell);
					}
					tNewMarketCell.redrawMarket ();
				}
			}
		}
	}

	public void setMarket (Market aMarket) {
		market = aMarket;
	}
	
	public void setLoanInfo (int aLoanAmount, int aLoanInterest) {
		ShareCompany tShareCompany;
		int tIndex;
		int tCorpCount;

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
					tShareCompany = (ShareCompany) companies.getCorporation (tIndex);
					tShareCompany.setLoanInfo (aLoanAmount, aLoanInterest);
				}
			}
		}
	}

	public void setStartCells () {
		ShareCompany tShareCompany;
		int tIndex;
		int tCorpCount;

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
					tShareCompany = (ShareCompany) companies.getCorporation (tIndex);
					tShareCompany.setStartCell (market);
				}
			}
		}
	}
	
	public void fillCertificateInfo (GameManager aGameManager) {
		ShareCompany tShareCompany;
		int tIndex;
		int tCorpCount;

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
					tShareCompany = (ShareCompany) companies.getCorporation (tIndex);
					tShareCompany.fillCertificateInfo (aGameManager);
				}
			}
		}
	}

	public void updateCorpComboBox () {
		int tIndex;
		int tCorpCount;
		Corporation tCorporation;
		String tAbbrev;

		companyCombo.removeAllItems ();
		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
					tCorporation = companies.getCorporation (tIndex);
					if (tCorporation != Corporation.NO_CORPORATION) {
						tAbbrev = tCorporation.getAbbrev ();
						companyCombo.addItem (tAbbrev);
					}
				}
			}
		}
	}

	public void updateParValuesComboBox (Integer [] aParValues) {
		int tIndex;
		int tSize;

		if (aParValues != null) {
			tSize = aParValues.length;
			for (tIndex = 0; tIndex < tSize; tIndex++) {
				parValuesCombo.addItem (aParValues [tIndex]);
			}
		}
	}

	public boolean hasDestinations () {
		return companies.hasDestinations ();
	}
	
	public void checkForDestinationsReached () {
		companies.checkForDestinationsReached ();
	}
	
	public void fixLoadedRoutes (MapFrame aMapFrame) {
		super.fixLoadedRoutes (aMapFrame, BASE_TYPE);
	}
}
