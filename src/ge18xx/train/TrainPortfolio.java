package ge18xx.train;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.Coupon;
import ge18xx.company.TrainCompany;
import ge18xx.game.FrameButton;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.ParsingRoutineIO;
import geUtilities.ParsingRoutineIOO;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public class TrainPortfolio implements TrainHolderI {
	public static final ElementName EN_TRAIN_PORTFOLIO = new ElementName ("TrainPortfolio");
	public static final ElementName EN_RUSTED_TRAIN_PORTFOLIO = new ElementName ("RustedTrainPortfolio");
	public static final String CANNOT_BUY_IN_PHASE = "Cannot buy Other Corporation Trains in current Phase";
	public static final String ONLY_BUY_PERMANENT = "Can only buy Permanent Train";
	public static final String NOT_ENOUGH_CASH = " does not have enough Cash";
	public static final String NOT_ENOUGH_CASH_FULL_PRICE = " does not have enough Cash to pay full price";
	public static final String AT_TRAIN_LIMIT = " is at Train Limit";
	public static final String HAS_NO_CASH = " has not Cash";
	public static final String ALL_TRAINS = "ALL";
	public static final String NO_TRAINS_TEXT = ">> NO TRAINS <<";
	public static final String NO_NAME = "NONE";
	public static final String AVAILABLE_TRAINS = "AVAILABLE";
	public static final String FUTURE_TRAINS = "FUTURE";
	public static final String RUSTED_TRAINS = "RUSTED";
	public static final String ADDED_TRAIN = "ADDED TRAIN";
	public static final String REMOVED_TRAIN = "REMOVED TRAIN";
	public static final String UPGRADE = "Upgrade";
	public static final boolean FULL_TRAIN_PORTFOLIO = true;
	public static final boolean COMPACT_TRAIN_PORTFOLIO = false;
	public static final TrainPortfolio NO_TRAIN_PORTFOLIO = null;
	public static final ArrayList<Train> NO_TRAINS = null;
	TrainCheckboxInfo trainCheckboxInfo;
	ArrayList<Train> trains;
	CashHolderI portfolioHolder;

	public TrainPortfolio () {
		this (CashHolderI.NO_CASH_HOLDER);
	}

	public TrainPortfolio (CashHolderI aPortfolioHolder) {
		ArrayList<Train> tTrains;
		
		tTrains = new ArrayList<Train> ();
		setTrains (tTrains);
		setPortfolioHolder (aPortfolioHolder);
		trainCheckboxInfo = new TrainCheckboxInfo ();
	}

	public void setTrains (ArrayList<Train> aTrains) {
		trains = aTrains;
	}
	
	public void setPortfolioHolder (CashHolderI aPortfolioHolder) {
		portfolioHolder = aPortfolioHolder;
	}

	public String getPortfolioHolderAbbrev () {
		String tHolderName;

		tHolderName = NO_NAME;
		if (portfolioHolder != CashHolderI.NO_CASH_HOLDER) {
			tHolderName = portfolioHolder.getAbbrev ();
		}

		return tHolderName;
	}

	public String getPortfolioHolderName () {
		String tHolderName;

		tHolderName = NO_NAME;
		if (portfolioHolder != CashHolderI.NO_CASH_HOLDER) {
			tHolderName = portfolioHolder.getName ();
		}

		return tHolderName;
	}

	@Override
	public void addTrain (Train aTrain) {
		trains.add (aTrain);
		Collections.sort (trains);
		portfolioHolder.updateListeners (ADDED_TRAIN + " to " + portfolioHolder.getName ());
	}

	public FrameButton getFrameButtonAt (int aIndex) {
		FrameButton tFrameButton;
		Train tTrain;

		tFrameButton = FrameButton.NO_FRAME_BUTTON;
		tTrain = trains.get (aIndex);
		if (tTrain != Train.NO_TRAIN) {
			tFrameButton = tTrain.getFrameButton ();
		}

		return tFrameButton;
	}

	public void verifyCanBuyTrain (CashHolderI aTrainCompany, TrainCheckboxInfo aTrainActionCheckboxInfo) {
		boolean tCanBuyTrain;
		boolean tHasCash;
		String tCompanyAbbrev;

		tCompanyAbbrev = aTrainCompany.getAbbrev ();
		tHasCash = (aTrainCompany.getCash () > 0);
		if (!tHasCash) {
			tCanBuyTrain = false;
			aTrainActionCheckboxInfo.setToolTip (tCompanyAbbrev + " has no cash");
		} else {
			tCanBuyTrain = true;
		}
		aTrainActionCheckboxInfo.setEnabled (tCanBuyTrain);
	}
	
	public TrainCompany getOperatingCompany (Corporation aCompany) {
		Corporation tCorporation;
		TrainCompany tTrainCompany;
		GameManager tGameManager;
		
		tGameManager = aCompany.getGameManager ();
		tCorporation = tGameManager.getOperatingCompany ();
		tTrainCompany = (TrainCompany) tCorporation;
		
		return tTrainCompany;
	}
	
	public JPanel buildPortfolioJPanel (ItemListener aItemListener, Corporation aCorporation, 
					GameManager aGameManager, String aActionLabel, boolean aFullvsCompact) {
		JPanel tPortfolioJPanel;
		JPanel tTrainInfoJPanel;
		JLabel tLabel;
		int tTrainIndex;
		int tTrainCount;
		int tTrainQuantity;
		String tTrainName;
		String tOperatingCompanyAbbrev;
		boolean tCorporationIsPorfolioHolder;
		boolean tCanBeUpgraded;
		TrainCompany tTrainCompany;
		TrainCompany tOperatingCompany;
		Train [] tBankAvailableTrains;
		Train tTrain;

		tBankAvailableTrains = aGameManager.getBankAvailableTrains ();
		tPortfolioJPanel = new JPanel ();
		tPortfolioJPanel.setLayout (new BoxLayout (tPortfolioJPanel, BoxLayout.X_AXIS));
		tPortfolioJPanel.setAlignmentX (Component.LEFT_ALIGNMENT);
		tPortfolioJPanel.add (Box.createHorizontalStrut (10));
		if (isEmpty ()) {
			tLabel = new JLabel (NO_TRAINS_TEXT);
			tPortfolioJPanel.add (tLabel);
			tPortfolioJPanel.add (Box.createHorizontalStrut (10));
		} else {
			tPortfolioJPanel.add (Box.createHorizontalGlue ());
			tTrainCount = getTrainCount ();
			tOperatingCompany = getOperatingCompany (aCorporation);
			if (tOperatingCompany != Corporation.NO_CORPORATION) {
				if (aCorporation.isATrainCompany ()) {
					tOperatingCompanyAbbrev = tOperatingCompany.getAbbrev ();
					tTrainCompany = (TrainCompany) aCorporation;
					if (tTrainCompany.getName ().equals (portfolioHolder.getName ())) {
						tCorporationIsPorfolioHolder = true;
					} else {
						tCorporationIsPorfolioHolder = false;
					}
					for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
						trainCheckboxInfo = new TrainCheckboxInfo ();
						trainCheckboxInfo.setItemListener (aItemListener);
						tTrain = getTrainAt (tTrainIndex);
						tTrainName = tTrain.getName ();
						tTrainQuantity = getTrainCount (tTrainName);
						verifyCanBuyTrain (tOperatingCompany, trainCheckboxInfo);
						tCanBeUpgraded = tTrain.canUpgrade (tBankAvailableTrains);
						if (tTrainCompany.isOperating () && tCanBeUpgraded) {
							trainCheckboxInfo.setLabel (UPGRADE);
							if (tTrainCompany.canBuyTrain ()) {
								trainCheckboxInfo.setEnabled (true);
							} else {
								if (tOperatingCompany.atTrainLimit ()) {
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev +  AT_TRAIN_LIMIT);	
								} else if (tOperatingCompany.getCash () == TrainCompany.NO_CASH) {
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev +  HAS_NO_CASH);
								} else {
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev
												+ CorporationFrame.DIVIDENDS_NOT_HANDLED);
								}
							}
						}
						if (aActionLabel != GUI.NULL_STRING) {
							if (! tOperatingCompany.dividendsHandled ()) {
								if (tTrain.isAvailableForPurchase ()) {
									trainCheckboxInfo.setLabel (aActionLabel);
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (CorporationFrame.DIVIDENDS_NOT_HANDLED);
								} else {
									trainCheckboxInfo.setLabel (GUI.EMPTY_STRING);
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (GUI.EMPTY_STRING);
								}
							} else if (tOperatingCompany.atTrainLimit ()) {
								if (tTrain.isAvailableForPurchase ()) {
									trainCheckboxInfo.setLabel (aActionLabel);
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev +  AT_TRAIN_LIMIT);
								} else {
									trainCheckboxInfo.setLabel (GUI.EMPTY_STRING);
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (GUI.EMPTY_STRING);
								}
							} else if (portfolioHolder.isABank ()) {
								if (tTrain.isAvailableForPurchase ()) {
									if (tOperatingCompany.getTreasury () >= tTrain.getPrice ()) {
										trainCheckboxInfo.setLabel (aActionLabel);
										trainCheckboxInfo.setEnabled (true);
										trainCheckboxInfo.setToolTip (GUI.EMPTY_STRING);
									} else {
										trainCheckboxInfo.setLabel (aActionLabel);
										trainCheckboxInfo.setEnabled (false);
										trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev + NOT_ENOUGH_CASH);
									}
								}
							} else if (! tOperatingCompany.canBuyTrainInPhase ()) {
								trainCheckboxInfo.setLabel (aActionLabel);
								trainCheckboxInfo.setEnabled (false);
								trainCheckboxInfo.setToolTip (CANNOT_BUY_IN_PHASE);
							} else {
								if (tTrain.isAvailableForPurchase ()) {
									trainCheckboxInfo.setLabel (aActionLabel);
									trainCheckboxInfo.setEnabled (true);
									trainCheckboxInfo.setToolTip (GUI.EMPTY_STRING);
								}
							}
							if (! tCorporationIsPorfolioHolder) {
								if (tOperatingCompany.getTreasury () < tTrain.getPrice ()) {
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev + NOT_ENOUGH_CASH);
								}
							}
							if (tOperatingCompany.mustPayFullPrice ()) {
								if (tOperatingCompany.getTreasury () < tTrain.getPrice ()) {
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev 
											+ NOT_ENOUGH_CASH_FULL_PRICE);
								}
							}
							if (tOperatingCompany.onlyPermanentTrain ()) {
								if (! tTrain.isPermanent ()) {
									trainCheckboxInfo.setLabel (aActionLabel);
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev + " " + ONLY_BUY_PERMANENT);
								}
							}
							// If the company that owns the train must "sell" at the Full Price, test that here.
							if (tTrainCompany.mustPayFullPrice ()) { 
								if (tOperatingCompany.getTreasury () < tTrain.getPrice ()) {
									trainCheckboxInfo.setEnabled (false);
									trainCheckboxInfo.setToolTip (tOperatingCompanyAbbrev
											+ NOT_ENOUGH_CASH_FULL_PRICE);
								}
							}
						}
	
						tTrainInfoJPanel = tTrain.buildTrainInfoJPanel (trainCheckboxInfo);
					
						if (aFullvsCompact == COMPACT_TRAIN_PORTFOLIO) {
							updateForCompactPortfolio (tTrainInfoJPanel, tTrainQuantity, tTrainName);
							tTrainIndex = updateTrainIndex (tTrainIndex, tTrainQuantity);
						}
		
						tPortfolioJPanel.add (tTrainInfoJPanel);
						tPortfolioJPanel.add (Box.createHorizontalGlue ());
						tPortfolioJPanel.add (Box.createHorizontalStrut (10));
					}
				}
			}
		}

		return tPortfolioJPanel;
	}

	public void updateForCompactPortfolio (JPanel aTrainCertJPanel, int aTrainQuantity, String aTrainName) {
		JLabel tLabel;
		String tLabelText;
		
		if (aTrainQuantity > 1) {
			tLabelText = "Quantity: " + aTrainQuantity;
		} else if (aTrainQuantity == 1) {
			tLabelText = "LAST " + aTrainName + " Train";
		} else {
			tLabelText = GUI.EMPTY_STRING;
		}
		tLabel = new JLabel (tLabelText);

		if (aTrainQuantity > 0) {
			aTrainCertJPanel.add (tLabel);
		}
	}
	
	public int updateTrainIndex (int aTrainIndex, int aTrainQuantity) {
		aTrainIndex += aTrainQuantity - 1;
		
		return aTrainIndex;
	}

	public int getTrainCount () {
		int tTrainCount;
		
		if (trains == NO_TRAINS) {
			tTrainCount = 0;
		} else {
			tTrainCount = trains.size ();
		}
		
		return tTrainCount;
	}

	@Override
	public int getTrainCount (String aName) {
		int tTrainCount;
		String tTrainName;

		tTrainCount = 0;
		for (Train tTrain : trains) {
			tTrainName = tTrain.getName ();
			if (tTrainName.equals (aName)) {
				tTrainCount++;
			}
		}

		return tTrainCount;
	}

	public boolean isEmpty () {
		boolean tIsEmpty;
		
		if (trains == NO_TRAINS) {
			tIsEmpty = true;
		} else {
			tIsEmpty = (getTrainCount () == 0);
		}
		
		return tIsEmpty;
	}
	
	public boolean hasBorrowedTrain () {
		boolean tHasBorrowedTrain;
		
		tHasBorrowedTrain = false;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				if (tTrain.isBorrowed ()) {
					tHasBorrowedTrain = true;
				}
			}
		}
		
		return tHasBorrowedTrain;
	}
	
	public boolean hasTrains () {
		boolean tHasTrains;
		
		tHasTrains = ! isEmpty ();
		
		return tHasTrains;
	}
	
	public boolean hasPermanentTrain () {
		boolean tHasPermanentTrain;
		
		tHasPermanentTrain = false;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				if (tTrain.isPermanent ()) {
					tHasPermanentTrain = true;
				}
			}
		}
		
		return tHasPermanentTrain;
	}

	public void clearCurrentRoutes () {
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				tTrain.clearCurrentRoute ();
			}
		}
	}

	public void clearSelections () {
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				tTrain.clearSelection ();
			}
		}
	}

	public int countTrainsOfThisOrder (int aOrder) {
		int tCountOfTrains;

		tCountOfTrains = 0;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				if (tTrain.isTrainThisOrder (aOrder)) {
					tCountOfTrains++;
				}
			}
		}

		return tCountOfTrains;
	}

	public boolean anyTrainIsOperating () {
		boolean tAnyTrainIsOperating;

		tAnyTrainIsOperating = false;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				if (tTrain.isOperating ()) {
					tAnyTrainIsOperating = true;
				}
			}
		}

		return tAnyTrainIsOperating;
	}

	public String getTrainList () {
		String tTrainList;
		int tTrainIndex;
		int tTrainLimit;
		int tIndex;

		tTrainLimit = getTrainLimit ();
		tTrainList = "NO TRAINS";
		tTrainIndex = 0;
		if (hasTrains ()) {
			tTrainList = "Trains (";
			for (Train tTrain : trains) {
				tTrainList += tTrain.getName ();
				tTrainIndex++;
				if (tTrainIndex < trains.size ()) {
					tTrainList += GUI.COMMA_SPACE;
				}
			}
			if (tTrainIndex < tTrainLimit) {
				for (tIndex = tTrainIndex; tIndex < tTrainLimit; tIndex++) {
					tTrainList += GUI.COMMA_SPACE + "X";
				}
			}
			tTrainList += ")";
		}

		return tTrainList;
	}

	public Train getCheapestTrain () {
		Train tCheapestTrain;

		tCheapestTrain = Train.NO_TRAIN;
		for (Train tTrain : trains) {
			if (tCheapestTrain == Train.NO_TRAIN) {
				tCheapestTrain = tTrain;
			} else {
				if (tTrain.getPrice () < tCheapestTrain.getPrice ()) {
					tCheapestTrain = tTrain;
				}
			}
		}

		return tCheapestTrain;
	}
	
	public Train getCheapestPermanentTrain () {
		Train tCheapestPermanentTrain;

		tCheapestPermanentTrain = Train.NO_TRAIN;
		for (Train tTrain : trains) {
			if (tCheapestPermanentTrain == Train.NO_TRAIN) {
				if (tTrain.isPermanent ()) {
					tCheapestPermanentTrain = tTrain;
				}
			} else {
				if (tTrain.getPrice () < tCheapestPermanentTrain.getPrice ()) {
					if (tTrain.isPermanent ()) {
						tCheapestPermanentTrain = tTrain;
					}
				}
			}
		}

		return tCheapestPermanentTrain;
	}

	public XMLElement getElements (XMLDocument aXMLDocument) {
		return getElements (aXMLDocument, EN_TRAIN_PORTFOLIO);
	}

	public XMLElement getElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		XMLElement tXMLTrainElements;

		tXMLElement = aXMLDocument.createElement (aElementName);
		for (Train tTrain : trains) {
			tXMLTrainElements = tTrain.getElement (aXMLDocument);
			tXMLElement.appendChild (tXMLTrainElements);
		}

		return tXMLElement;
	}

	@Override
	public String getName () {
		return "Train Portfolio";
	}

	public XMLElement getRustedElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLRustedTrains;

		tXMLElement = aXMLDocument.createElement (EN_RUSTED_TRAIN_PORTFOLIO);
		tXMLRustedTrains = getElements (aXMLDocument, EN_TRAIN_PORTFOLIO);
		tXMLElement.appendChild (tXMLRustedTrains);

		return tXMLElement;
	}

	@Override
	public int getLocalSelectedTrainCount () {
		return getSelectedCount ();
	}

	public int getSelectedCount () {
		int tCount;

		tCount = 0;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				if (tTrain.isSelected ()) {
					tCount++;
				}
			}
		}

		return tCount;
	}

	@Override
	public Train getSelectedTrain () {
		Train tSelectedTrain;

		tSelectedTrain = Train.NO_TRAIN;
		if (trains != NO_TRAINS) {
			for (Train tTrain : trains) {
				if (tTrain.isSelected ()) {
					tSelectedTrain = tTrain;
				}
			}
		}

		return tSelectedTrain;
	}

	public Coupon getTrainOfOrder (int aOrder) {
		Coupon tTrainOfOrder;

		tTrainOfOrder = Train.NO_TRAIN;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				if (tTrain.getOrder () == aOrder) {
					tTrainOfOrder = tTrain;
				}
			}
		}

		return tTrainOfOrder;
	}

	@Override
	public Train getTrain (String aName) {
		Train tFoundTrain;
		String tTrainName;

		tFoundTrain = Train.NO_TRAIN;
		if (hasTrains ()) {
			for (Train tTrain : trains) {
				tTrainName = tTrain.getName ();
				if (tFoundTrain == Train.NO_TRAIN) {
					if (tTrainName.equals (aName)) {
						tFoundTrain = tTrain;
					}
				}
			}
		}

		return tFoundTrain;
	}
	
	/**
	 * Find the Train by the ID, and return it
	 *
	 * @param aTrainID The Index for the Train to find
	 * @return The Train at the specified index in the Train Portfolio
	 */
	public Train getTrainByID (int aTrainID) {
		Train tTrainFound;

		if (trains.isEmpty ()) {
			tTrainFound = Train.NO_TRAIN;
		} else {
			tTrainFound = Train.NO_TRAIN;
			for (Train tTrain : trains) {
				if (tTrain.getID () == aTrainID) {
					tTrainFound = tTrain;
				}
			}
		}

		return tTrainFound;
	}

	/**
	 * Find the Train at the specified index, and return it. If there are no trains in the portfolio
	 * return Train.NO_TRAIN. If the train index is after the last train in the
	 * portfolio, return Train.NO_TRAIN
	 *
	 * @param aIndex the Index for the Train to find
	 * @return The Train at the specified index
	 */
	public Train getTrainAt (int aIndex) {
		Train tTrain;

		if (trains.isEmpty ()) {
			tTrain = Train.NO_TRAIN;
		} else {
			if ((aIndex >= 0) && (aIndex < getTrainCount ())) {
				tTrain = trains.get (aIndex);
			} else {
				tTrain = Train.NO_TRAIN;
			}
		}

		return tTrain;
	}

	public Train getLastTrain () {
		int tTrainCount;
		Train tLastTrain;
		
		tTrainCount = getTrainCount ();
		tLastTrain = getTrainAt (tTrainCount - 1);
		trains.remove (tTrainCount - 1);
		
		return tLastTrain;
	}
	
	public Train getBorrowedTrain () {
		Train tBorrowedTrain;
		Train tTrain;
		int tIndex;
		int tCount;
		
		tCount = trains.size ();
		tBorrowedTrain = Train.NO_TRAIN;
		for (tIndex = 0; tIndex < tCount; tIndex++) {
			tTrain = trains.get (tIndex);
			if (tTrain.isBorrowed ()) {
				tBorrowedTrain = trains.remove (tIndex);
			}
		}

		return tBorrowedTrain;
	}
	
	public String getTrainAndCount (String aName, int aCount) {
		String tNameAndCount;

		tNameAndCount = aName;
		if (aCount == TrainInfo.UNLIMITED_TRAINS) {
			tNameAndCount += " (" + TrainInfo.UNLIMITED + ")";
		} else {
			tNameAndCount += " (" + aCount + ")";
		}

		return tNameAndCount;
	}

	public void printNameAndQty (String aPortfolioHolderName) {
		System.out.println ("Portfolio Holder " + aPortfolioHolderName);		// PRINTLOG
		System.out.println ("Owned Trains [" + getTrainNameAndQty (ALL_TRAINS) + "]");
		System.out.println ("Available Trains [" + getTrainNameAndQty (AVAILABLE_TRAINS) + "]");
		System.out.println ("Future Trains [" + getTrainNameAndQty (FUTURE_TRAINS) + "]");
	}

	@Override
	public String getTrainNameAndQty (String aStatus) {
		String tNameAndQuantity;
		String tName;
		String tNames [];
		int tQuantities [];
		int tIndex1;
		int tCount1;
		int tIndex2;
		int tCount2;
		Train tTrain;
		boolean tFoundTrain;
		boolean tAddTrain;

		tCount1 = getTrainCount ();
		tNameAndQuantity = "";
		if (tCount1 > 0) {
			tNames = new String [tCount1];
			tQuantities = new int [tCount1];
			for (tIndex1 = 0; tIndex1 < tCount1; tIndex1++) {
				tQuantities [tIndex1] = 0;
			}
			tCount2 = 0;
			for (tIndex1 = 0; tIndex1 < tCount1; tIndex1++) {
				tTrain = trains.get (tIndex1);
				tName = tTrain.getName ();
				tAddTrain = false;
				if (aStatus.equals (ALL_TRAINS)) {
					tAddTrain = true;
				} else if (aStatus.equals (AVAILABLE_TRAINS)) {
					if (tTrain.isAvailableForPurchase ()) {
						tAddTrain = true;
					}
				} else if (aStatus.equals (FUTURE_TRAINS)) {
					if (tTrain.isNotAvailable ()) {
						tAddTrain = true;
					}
				} else if (aStatus.equals (RUSTED_TRAINS)) {
					if (tTrain.isRusted ()) {
						tAddTrain = true;
					}
				}
				if (tAddTrain) {
					tFoundTrain = false;
					if (tCount2 > 0) {
						for (tIndex2 = 0; tIndex2 < tCount2; tIndex2++) {
							if (tNames [tIndex2].equals (tName)) {
								if (tTrain.isUnlimitedQuantity ()) {
									tQuantities [tIndex2] = TrainInfo.UNLIMITED_TRAINS;
								} else {
									tQuantities [tIndex2]++;
								}
								tFoundTrain = true;
							}
						}
					}
					if (!tFoundTrain) {
						tNames [tCount2] = tName;
						if (tTrain.isUnlimitedQuantity ()) {
							tQuantities [tCount2] = TrainInfo.UNLIMITED_TRAINS;
						} else {
							tQuantities [tCount2]++;
						}
						tCount2++;
					}
				}
			}
			for (tIndex2 = 0; tIndex2 < tCount2; tIndex2++) {
				tNameAndQuantity += getTrainAndCount (tNames [tIndex2], tQuantities [tIndex2]);
				if ((tIndex2 + 1) < tCount2) {
					tNameAndQuantity += GUI.COMMA_SPACE;
				}
			}
		}

		return tNameAndQuantity;
	}

	@Override
	public TrainPortfolio getTrainPortfolio () {
		return this;
	}

	public int getTrainStatusForOrder (int aOrder) {
		int tTrainStatusForOrder;

		tTrainStatusForOrder = Train.NO_ORDER;
		if (trains != NO_TRAINS) {
			for (Train tTrain : trains) {
				if (tTrain.getOrder () == aOrder) {
					tTrainStatusForOrder = tTrain.getStatus ();
				}
			}
		}

		return tTrainStatusForOrder;
	}

	@Override
	public boolean hasTrainNamed (String aTrainName) {
		boolean tHasTrain;

		tHasTrain = false;
		for (Coupon tTrain : trains) {
			if (tTrain.getName ().equals (aTrainName)) {
				tHasTrain = true;
			}
		}

		return tHasTrain;
	}

	public void loadTrainStatus (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (trainStatusParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Train.EN_TRAIN);
	}

	ParsingRoutineI trainStatusParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aTrainNode) {
			String tTrainName;
			int tTrainStatus;
			int tTrainOrder;
			Train tTrain;

			tTrainName = aTrainNode.getThisAttribute (Train.AN_NAME);
			tTrainStatus = aTrainNode.getThisIntAttribute (Train.AN_STATUS);
			tTrain = getTrain (tTrainName);
			tTrainOrder = tTrain.getOrder ();
			setTrainsStatus (tTrainOrder, tTrainStatus);
			tTrain.setStatus (tTrainStatus);
		}
	};

	public void loadTrainPortfolio (XMLNode aXMLNode, Bank aBank) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (trainPortfolioParsingRoutine, aBank);
		tXMLNodeList.parseXMLNodeList (aXMLNode, EN_TRAIN_PORTFOLIO);
	}

	ParsingRoutineI trainPortfolioParsingRoutine = new ParsingRoutineIO () {

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aBank) {
			Bank tBank;

			tBank = (Bank) aBank;
			loadTrainPortfolioFromBank (aChildNode, tBank);
		}
	};

	public void loadTrainPortfolioFromBank (XMLNode aTrainNode, Bank aBank) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (trainParsingRoutine, aBank);
		tXMLNodeList.parseXMLNodeList (aTrainNode, Train.EN_TRAIN);
	}

	ParsingRoutineIO trainParsingRoutine = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aBank) {
			Bank tBank;

			tBank = (Bank) aBank;
			loadTrainFromBank (aChildNode, tBank);
		}
	};

	public void loadTrainFromBank (XMLNode aTrainNode, Bank aBank) {
		String tTrainName;
		int tTrainStatus;
		Train tTrain;

		tTrainName = aTrainNode.getThisAttribute (Train.AN_NAME);
		tTrainStatus = aTrainNode.getThisIntAttribute (Train.AN_STATUS);
		tTrain = aBank.getTrain (tTrainName);
		if (tTrain != Train.NO_TRAIN) {
			aBank.removeTrain (tTrainName);
			restoreTrain (aTrainNode, tTrainStatus, tTrain);
		} else {
			tTrain = aBank.getRustedTrain (tTrainName);
			if (tTrain != Train.NO_TRAIN) {
				aBank.removeRustedTrain (tTrainName);
				restoreTrain (aTrainNode, tTrainStatus, tTrain);
			} else {
				System.err.println (
						"Trying to load a " + tTrainName + " Not found in the Bank, Status should be " 
							+ tTrainStatus);
			}
		}
	}

	public void restoreTrain (XMLNode aTrainNode, int aTrainStatus, Train aTrain) {
		aTrain.setStatus (aTrainStatus);
		trains.add (aTrain);
		loadRouteForTrain (aTrain, aTrainNode, Train.EN_CURRENT_ROUTE);
		loadRouteForTrain (aTrain, aTrainNode, Train.EN_PREVIOUS_ROUTE);
	}

	public void loadRouteForTrain (Coupon aTrain, XMLNode aTrainNode, ElementName aElementName) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (trainRouteParsingRoutine, aTrain, this);
		tXMLNodeList.parseXMLNodeList (aTrainNode, aElementName);
	}

	ParsingRoutineIOO trainRouteParsingRoutine = new ParsingRoutineIOO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aRouteNode, Object aTrain, Object aTrainPortfolio) {
			Train tTrain;
			TrainPortfolio tTrainPortfolio;

			tTrain = (Train) aTrain;
			tTrainPortfolio = (TrainPortfolio) aTrainPortfolio;
			tTrain.loadRouteInformation (aRouteNode, tTrain, tTrainPortfolio);
		}
	};

	@Override
	public boolean removeSelectedTrain () {
		boolean tTrainRemoved;
		int tIndex;
		int tCount;
		Train tTrain;

		tCount = getTrainCount ();
		tTrainRemoved = false;
		if (tCount > 0) {
			for (tIndex = 0; (tIndex < tCount) && !tTrainRemoved; tIndex++) {
				tTrain = trains.get (tIndex);
				if (tTrain.isSelected ()) {
					if (!(tTrain.isUnlimitedQuantity ())) {
						trains.remove (tIndex);
						tTrainRemoved = true;
						portfolioHolder.updateListeners (REMOVED_TRAIN + " from " + portfolioHolder.getName ());
					}
				}
			}
		}

		return tTrainRemoved;
	}

	@Override
	public boolean removeTrain (String aTrainName) {
		boolean tTrainRemoved;
		int tIndex;
		int tCount;
		Train tTrain;

		tCount = getTrainCount ();
		tTrainRemoved = false;
		if (tCount > 0) {
			for (tIndex = 0; (tIndex < tCount) && !tTrainRemoved; tIndex++) {
				tTrain = trains.get (tIndex);
				if (tTrain.getName ().equals (aTrainName)) {
					if (!(tTrain.isUnlimitedQuantity ())) {
						trains.remove (tIndex);
						tTrainRemoved = true;
					}
				}
			}
		}

		return tTrainRemoved;
	}

	public void rustAllTrainsNamed (String aTrainName, TrainPortfolio aRustedTrainPortfolio, ActorI aCurrentHolder,
			Bank aBank, BuyTrainAction aBuyTrainAction) {
		Train tTrain;
		int tCurrentTrainStatus;

		while (hasTrainNamed (aTrainName)) {
			tTrain = getTrain (aTrainName);
			tCurrentTrainStatus = tTrain.getStatus ();
			removeTrain (aTrainName);
			tTrain.rust ();
			aRustedTrainPortfolio.addTrain (tTrain);
			aBuyTrainAction.addRustTrainEffect (aCurrentHolder, tTrain, aBank, tCurrentTrainStatus);
		}
	}

	public void setTrainsAvailableStatus (int aOrder, int aTrainStatus) {
		if (trains != NO_TRAINS) {
			for (Train tTrain : trains) {
				if (tTrain.isTrainThisOrder (aOrder)) {
					tTrain.setStatus (aTrainStatus);
				}
			}
		}
	}

	public void setTrainsUnAvailable (int aOrder) {
		setTrainsAvailableStatus (aOrder, Train.NOT_AVAILABLE);
	}

	public void setTrainsAvailable (int aOrder) {
		setTrainsAvailableStatus (aOrder, Train.AVAILABLE_FOR_PURCHASE);
	}

	public void setTrainsStatus (int aOrder, int aStatus) {
		setTrainsAvailableStatus (aOrder, aStatus);
	}

	public int getAvailableCount () {
		int tAvailableCount;

		tAvailableCount = 0;
		for (Train tTrain : trains) {
			if (tTrain.isAvailableForPurchase ()) {
				tAvailableCount++;
			}
		}
		
		return tAvailableCount;
	}

	public Coupon getNextAvailableTrain () {
		Coupon tNextAvailableTrain;
		Coupon tAvailableTrains[];

		tAvailableTrains = getAvailableTrains ();
		if (tAvailableTrains.length > 0) {
			tNextAvailableTrain = tAvailableTrains [0];
		} else {
			tNextAvailableTrain = Train.NO_TRAIN;
		}

		return tNextAvailableTrain;
	}

	public Train [] getAvailableTrains () {
		Train [] tAvailableTrains;
		int tIndex;
		int tAvailableCount;
		

		tIndex = 0;
		tAvailableCount = getAvailableCount ();
		tAvailableTrains = new Train [tAvailableCount];
		for (Train tTrain : trains) {
			if (tTrain.isAvailableForPurchase ()) {
				tAvailableTrains [tIndex++] = tTrain;
			}
		}

		return tAvailableTrains;
	}

	/**
	 * Will clear all of the currently selected Trains so they are NOT selected
	 *
	 */
	public void clearAllTrainSelections () {
		for (Train tTrain : trains) {
			if (tTrain.isSelected ()) {
				tTrain.clearSelection ();
			}
		}
	}

	public int getMaxTrainSize () {
		int tMaxTrainSize;
		int tTrainSize;

		tMaxTrainSize = 2;
		for (Train tTrain : trains) {
			tTrainSize = tTrain.getCityCount ();
			if (tTrainSize > tMaxTrainSize) {
				tMaxTrainSize = tTrainSize;
			}
		}

		return tMaxTrainSize;
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		for (Train tTrain : trains) {
			tTrain.fixLoadedRoutes (aMapFrame);
		}
	}

	public boolean startRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation, String aRoundID, int aPhase, TrainCompany aTrainCompany,
			TrainRevenueFrame aTrainRevenueFrame) {
		Train tTrain;
		boolean tRouteStarted;

		tRouteStarted = false;
		tTrain = trains.get (aTrainIndex);
		if (tTrain != Train.NO_TRAIN) {
			tRouteStarted = tTrain.startRouteInformation (aTrainIndex, aMapCell, aStartLocation, 
					aEndLocation, aRoundID, aPhase, aTrainCompany, aTrainRevenueFrame);
		}

		return tRouteStarted;
	}

	public boolean extendRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation, String aRoundID, int aPhase, TrainCompany aTrainCompany,
			TrainRevenueFrame aTrainRevenueFrame) {
		Train tTrain;
		boolean tRouteExtended;

		tRouteExtended = false;
		tTrain = trains.get (aTrainIndex);
		if (tTrain != Train.NO_TRAIN) {
			tRouteExtended = tTrain.extendRouteInformation (aTrainIndex, aMapCell, aStartLocation, aEndLocation,
					aRoundID, aPhase, aTrainCompany, aTrainRevenueFrame);
		}

		return tRouteExtended;

	}

	public boolean setNewEndPoint (int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation,
			String aRoundID, int aPhase, TrainCompany aTrainCompany, TrainRevenueFrame aTrainRevenueFrame) {
		Train tTrain;
		boolean tRouteStarted;

		tRouteStarted = false;
		tTrain = trains.get (aTrainIndex);
		if (tTrain != Train.NO_TRAIN) {
			tRouteStarted = tTrain.setNewEndPoint (aTrainIndex, aMapCell, aStartLocation, aEndLocation, aRoundID,
					aPhase, aTrainCompany, aTrainRevenueFrame);
		}

		return tRouteStarted;
	}

	public String getTrainSummary () {
		String tTrainSummary;
		String tTrainInfo;
		String tPreviousName;
		String tCurrentName;
		String tCost;
		String tRustInfo;
		String tTileInfo;
		int tDiscountCost;
		int tCount;
		boolean tIsUnlimited;

		tTrainSummary = GUI.EMPTY_STRING;
		tPreviousName = GUI.EMPTY_STRING;
		tCurrentName = GUI.EMPTY_STRING;
		tCost = GUI.EMPTY_STRING;
		tRustInfo = TrainInfo.NO_RUST;
		tTileInfo = Train.NO_TILE_INFO;
		tCount = 0;
		
		tIsUnlimited = false;
		for (Train tTrain : trains) {
			tCurrentName = tTrain.getName ();
			if (!tCurrentName.equals (tPreviousName)) {
				if (tCount > 0) {
					tTrainInfo = buildTrainInfo (tPreviousName, tCost, tCount, tIsUnlimited, tRustInfo, tTileInfo);
					tTrainSummary += tTrainInfo;
				}
				tCount = 1;
				tPreviousName = tCurrentName;
				tRustInfo = tTrain.getRustInfo ();
				tTileInfo = tTrain.getTileInfo ();
				tCost = Bank.formatCash (tTrain.getPrice ());
				tDiscountCost = tTrain.getDiscountCost ();
				if (tDiscountCost > 0) {
					tCost = Bank.formatCash (tDiscountCost) + " / " + tCost;
				}
			} else {
				tCount++;
			}
			tIsUnlimited = tTrain.isUnlimitedQuantity ();
		}
		if (tCount > 0) {
			tTrainInfo = buildTrainInfo (tPreviousName, tCost, tCount, tIsUnlimited, tRustInfo, tTileInfo);
			tTrainSummary += tTrainInfo;
		} else {
			tTrainSummary = NO_TRAINS_TEXT;
		}
		tTrainSummary = tTrainSummary.replaceAll("^[\n\r]", "").replaceAll("[\n\r]$", "");
		
		return tTrainSummary;
	}

	public String buildTrainInfo (String aPreviousName, String aCost, int aCount, boolean aIsUnlimited, 
								String aRustInfo, String aTileInfo) {
		String tTrainInfo;
		String tRustTileInfo;
		String tTrainCount;

		tRustTileInfo = aRustInfo;
		if (!aTileInfo.equals (Train.NO_TILE_INFO)) {
			if (!tRustTileInfo.equals (TrainInfo.NO_RUST)) {
				tRustTileInfo += " and ";
			}
			tRustTileInfo += aTileInfo;
		}
		if (tRustTileInfo.length () > 0) {
			tRustTileInfo = " ( " + tRustTileInfo + " )";
		}
		if (aIsUnlimited) {
			tTrainCount = "Unlimited";
		} else {
			tTrainCount = aCount + "";
		}
		tTrainInfo = aPreviousName + " Train QTY: " + tTrainCount + " " + aCost + tRustTileInfo + GUI.NEWLINE;

		return tTrainInfo;
	}

	/**
	 * Update the Train Indexes for all of the company's Trains.
	 *
	 */
	public void updateTrainIndexes () {
		int tTrainIndex;
		int tTrainCount;
		Train tTrain;

		tTrainCount = trains.size ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trains.get (tTrainIndex);
			tTrain.updateTrainIndex (tTrainIndex);
		}
	}

	@Override
	public int getTrainLimit () {
		TrainCompany tTrainCompany;
		int tTrainLimit;

		tTrainLimit = 0;
		if (isATrainCompany ()) {
			tTrainCompany = (TrainCompany) portfolioHolder;
			tTrainLimit = tTrainCompany.getTrainLimit ();
		}

		return tTrainLimit;
	}

	@Override
	public String getAbbrev () {
		return null;
	}

	@Override
	public String getStateName () {
		return null;
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
	}

	@Override
	public boolean isABank () {
		return portfolioHolder.isABank ();
	}

	@Override
	public boolean isABankPool () {
		return portfolioHolder.isABankPool ();
	}

	@Override
	public boolean isACorporation () {
		return portfolioHolder.isACorporation ();
	}

	@Override
	public boolean isATrainCompany () {
		return portfolioHolder.isATrainCompany ();
	}

	@Override
	public boolean isAShareCompany () {
		return portfolioHolder.isAShareCompany ();
	}

	@Override
	public boolean isAPrivateCompany () {
		return false;
	}

	@Override
	public boolean isAPlayer () {
		return false;
	}

	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	@Override
	public void completeBenefitInUse () {
	}
}
