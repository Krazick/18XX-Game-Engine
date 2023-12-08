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

import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.ParsingRoutineIO;
import geUtilities.ParsingRoutineIOO;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;
import geUtilities.XMLNodeList;

public class TrainPortfolio implements TrainHolderI {
	public static final String NO_TRAINS_TEXT = ">> NO TRAINS <<";
	private final static String NEWLINE = "\n";
	public final static ElementName EN_TRAIN_PORTFOLIO = new ElementName ("TrainPortfolio");
	public final static ElementName EN_RUSTED_TRAIN_PORTFOLIO = new ElementName ("RustedTrainPortfolio");
	public static final String ALL_TRAINS = "ALL";
	public static final String AVAILABLE_TRAINS = "AVAILABLE";
	public static final String FUTURE_TRAINS = "FUTURE";
	public static final String RUSTED_TRAINS = "RUSTED";
	public static final String ADDED_TRAIN = "ADDED TRAIN";
	public static final String REMOVED_TRAIN = "REMOVED TRAIN";
	public static final boolean FULL_TRAIN_PORTFOLIO = true;
	public static final boolean COMPACT_TRAIN_PORTFOLIO = false;
	public static final TrainPortfolio NO_TRAIN_PORTFOLIO = null;
	private final static ArrayList<Train> NO_TRAINS = null;
	ArrayList<Train> trains;
	CashHolderI portfolioHolder;

	public TrainPortfolio () {
		this (CashHolderI.NO_CASH_HOLDER);
	}

	public TrainPortfolio (CashHolderI aPortfolioHolder) {
		trains = new ArrayList<Train> ();
		setPortfolioHolder (aPortfolioHolder);
	}

	public void setPortfolioHolder (CashHolderI aPortfolioHolder) {
		portfolioHolder = aPortfolioHolder;
	}

	public String getPortfolioHolderAbbrev () {
		String tHolderName;

		tHolderName = "NONE";
		if (portfolioHolder != CashHolderI.NO_CASH_HOLDER) {
			tHolderName = portfolioHolder.getAbbrev ();
		}

		return tHolderName;
	}

	public String getPortfolioHolderName () {
		String tHolderName;

		tHolderName = "NONE";
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

	public JPanel buildPortfolioJPanel (ItemListener aItemListener, Corporation aCorporation, GameManager aGameManager,
			String aActionLabel, boolean aFullvsCompact, boolean aEnableAction, String aDisableReason) {
		JPanel tPortfolioJPanel;
		JPanel tTrainInfoJPanel;
		JLabel tLabel;
		int tTrainIndex;
		int tTrainCount;
		int tTrainQuantity;
		Train tTrain;
		String tTrainName;
		String tActionLabel;
		String tActionToolTip;
		String tCompanyAbbrev;
		boolean tActionEnabled;
		boolean tCanBeUpgraded;
		TrainCompany tTrainCompany;
		Train [] tBankAvailableTrains = aGameManager.getBankAvailableTrains ();

		tPortfolioJPanel = new JPanel ();
		tPortfolioJPanel.setLayout (new BoxLayout (tPortfolioJPanel, BoxLayout.X_AXIS));
		tPortfolioJPanel.setAlignmentX (Component.LEFT_ALIGNMENT);
		tPortfolioJPanel.add (Box.createHorizontalStrut (10));
		if (trains.isEmpty ()) {
			tLabel = new JLabel (NO_TRAINS_TEXT);
			tPortfolioJPanel.add (tLabel);
			tPortfolioJPanel.add (Box.createHorizontalStrut (10));
		} else {
			tTrainCount = getTrainCount ();
			tPortfolioJPanel.add (Box.createHorizontalGlue ());
			if (aCorporation.isATrainCompany ()) {
				tCompanyAbbrev = aCorporation.getAbbrev ();
				tTrainCompany = (TrainCompany) aCorporation;
				for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
					tTrain = getTrainAt (tTrainIndex);
					tTrainName = tTrain.getName ();
					tTrainQuantity = getTrainQuantity (tTrainName);
					tCanBeUpgraded = tTrain.canUpgrade (tBankAvailableTrains);
					tActionToolTip = GUI.NO_TOOL_TIP;
					if (tTrainCompany.isOperating () && tCanBeUpgraded) {
						tActionLabel = "Upgrade";
						if (aCorporation.canBuyTrain ()) {
							tActionEnabled = true;
							tActionToolTip = GUI.NO_TOOL_TIP;
						} else {
							tActionEnabled = false;
							if (tTrainCompany.atTrainLimit ()) {
								tActionToolTip = tCompanyAbbrev + " is at the Train Limit";
							} else if (tTrainCompany.getCash () == TrainCompany.NO_CASH) {
								tActionToolTip = tCompanyAbbrev + " has no cash";
							} else {
								tActionToolTip = tCompanyAbbrev + " has not handled dividends yet";
							}
						}
					} else {
						tActionLabel = GUI.EMPTY_STRING;
						tActionEnabled = false;
					}
					if (aActionLabel != GUI.NULL_STRING) {
						if (tTrain.isAvailableForPurchase ()) {
							tActionLabel = aActionLabel;
							tActionEnabled = aEnableAction;
							if (!aEnableAction && (tActionToolTip == GUI.EMPTY_STRING)) {
								tActionToolTip = aDisableReason;
							}
						}
						if (aCorporation.getName ().equals (portfolioHolder.getName ())) {
						} else {
							if (tTrainCompany.atTrainLimit ()) {
								tActionToolTip = tCompanyAbbrev + " is at Train Limit";
								tActionEnabled = false;
							}
							if (tTrainCompany.getTreasury () < tTrain.getPrice ()) {
								tActionToolTip = tCompanyAbbrev + " does not have enough cash";
								tActionEnabled = false;
							}
						}
					}
					tTrainInfoJPanel = tTrain.buildTrainInfoJPanel (aItemListener, tActionLabel, tActionEnabled, tActionToolTip);

					if (aFullvsCompact == COMPACT_TRAIN_PORTFOLIO) {
						tTrainIndex = updateForCompactPortfolio (tTrainInfoJPanel, tTrainQuantity, tTrainName, tTrainIndex);
					}
	
					tPortfolioJPanel.add (tTrainInfoJPanel);
					tPortfolioJPanel.add (Box.createHorizontalGlue ());
					tPortfolioJPanel.add (Box.createHorizontalStrut (10));
				}
			}
		}

		return tPortfolioJPanel;
	}

	private int updateForCompactPortfolio (JPanel aTrainCertJPanel, int aTrainQuantity, String aTrainName, int aTrainIndex) {
		JLabel tLabel;
		String tLabelText;
		
		if (aTrainQuantity > 1) {
			tLabelText = "Quantity: " + aTrainQuantity;
		} else if (aTrainQuantity == 1) {
			tLabelText = "LAST " + aTrainName + " Train";
		} else {
			tLabelText = "";
		}
		tLabel = new JLabel (tLabelText);

		if (aTrainQuantity > 0) {
			aTrainCertJPanel.add (tLabel);
		}
		aTrainIndex += aTrainQuantity - 1;
		
		return aTrainIndex;
	}

	public void clearCurrentRoutes () {
		if (trains != NO_TRAINS) {
			for (Train tTrain : trains) {
				tTrain.clearCurrentRoute ();
			}
		}

	}

	public void clearSelections () {
		if (trains != NO_TRAINS) {
			for (Coupon tTrain : trains) {
				tTrain.clearSelection ();
			}
		}
	}

	public int countTrainsOfThisOrder (int aOrder) {
		int tCountOfTrains;

		tCountOfTrains = 0;
		if (trains != NO_TRAINS) {
			for (Train tTrain : trains) {
				if (tTrain.isTrainThisOrder (aOrder)) {
					tCountOfTrains++;
				}
			}
		}

		return tCountOfTrains;
	}

	@Override
	public CashHolderI getCashHolder () {
		return CashHolderI.NO_CASH_HOLDER;
	}

	public boolean anyTrainIsOperating () {
		boolean tAnyTrainIsOperating = false;

		if (trains != NO_TRAINS) {
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
		if (trains.size () > 0) {
			tTrainList = "Trains (";
			for (Coupon tTrain : trains) {
				tTrainList += tTrain.getName ();
				tTrainIndex++;
				if (tTrainIndex < trains.size ()) {
					tTrainList += ", ";
				}
			}
			if (tTrainIndex < tTrainLimit) {
				for (tIndex = tTrainIndex; tIndex < tTrainLimit; tIndex++) {
					tTrainList += ", X";
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
		if (trains != null) {
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
		if (trains != null) {
			for (Train tTrain : trains) {
				if (tTrain.isSelected ()) {
					tSelectedTrain = tTrain;
				}
			}
		}

		return tSelectedTrain;
	}

	public int getTrainCount () {
		return trains.size ();
	}

	public boolean hasNoTrain () {
		return (getTrainCount () == 0);
	}

	public Coupon getTrainOfOrder (int aOrder) {
		Coupon tTrainOfOrder;

		tTrainOfOrder = Train.NO_TRAIN;

		if (!trains.isEmpty ()) {
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
		Train tTrain;
		int tIndex;
		int tCount;
		String tTrainName;

		tTrain = Train.NO_TRAIN;
		tCount = getTrainCount ();
		if (tCount > 0) {
			for (tIndex = 0; (tIndex < tCount) && (tTrain == Train.NO_TRAIN); tIndex++) {
				tTrainName = trains.get (tIndex).getName ();
				if (tTrainName.equals (aName)) {
					tTrain = trains.get (tIndex);
				}
			}
		}

		return tTrain;
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

	public String getTrainAndQty (String aName, int aQty) {
		String tNameAndQty;

		tNameAndQty = aName;
		if (aQty == TrainInfo.UNLIMITED_TRAINS) {
			tNameAndQty += " (" + TrainInfo.UNLIMITED + ")";
		} else {
			tNameAndQty += " (" + aQty + ")";
		}

		return tNameAndQty;
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
		String tNames[];
		int tQuantities[];
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
				tNameAndQuantity += getTrainAndQty (tNames [tIndex2], tQuantities [tIndex2]);
				if ((tIndex2 + 1) < tCount2) {
					tNameAndQuantity += ", ";
				}
			}
		}

		return tNameAndQuantity;
	}

	@Override
	public TrainPortfolio getTrainPortfolio () {
		return this;
	}

	@Override
	public int getTrainQuantity (String aName) {
		int tTrainQuantity;
		int tCount;
		int tIndex;
		String tTrainName;

		tTrainQuantity = 0;
		tCount = getTrainCount ();
		if (tCount > 0) {
			for (tIndex = 0; tIndex < tCount; tIndex++) {
				tTrainName = trains.get (tIndex).getName ();
				if (tTrainName.equals (aName)) {
					tTrainQuantity++;
				}
			}
		}

		return tTrainQuantity;
	}

	public int getTrainStatusForOrder (int aOrder) {
		int tTrainStatusForOrder;

		tTrainStatusForOrder = Train.NO_ORDER;
		if (trains != null) {
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
						"Trying to load a " + tTrainName + " Not found in the Bank, Status should be " + tTrainStatus);
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
		if (trains != null) {
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
		int tAvailableCount = 0;

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
		Train [] tAvailableTrains = new Train [getAvailableCount ()];
		int tIndex = 0;

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
		int tMaxTrainSize = 2;
		int tTrainSize;

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
		boolean tRouteStarted = false;

		tTrain = trains.get (aTrainIndex);
		if (tTrain != Train.NO_TRAIN) {
			tRouteStarted = tTrain.startRouteInformation (aTrainIndex, aMapCell, aStartLocation, aEndLocation, aRoundID,
					aPhase, aTrainCompany, aTrainRevenueFrame);
		}

		return tRouteStarted;
	}

	public boolean extendRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation, String aRoundID, int aPhase, TrainCompany aTrainCompany,
			TrainRevenueFrame aTrainRevenueFrame) {
		Train tTrain;
		boolean tRouteExtended = false;

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
		boolean tRouteStarted = false;

		tTrain = trains.get (aTrainIndex);
		if (tTrain != Train.NO_TRAIN) {
			tRouteStarted = tTrain.setNewEndPoint (aTrainIndex, aMapCell, aStartLocation, aEndLocation, aRoundID,
					aPhase, aTrainCompany, aTrainRevenueFrame);
		}

		return tRouteStarted;
	}

	public String getTrainSummary () {
		String tTrainSummary = "";
		String tTrainInfo;
		String tPreviousName = "";
		String tCurrentName = "";
		String tCost = "";
		int tDiscountCost;
		int tCount = 0;
		String tRustInfo = TrainInfo.NO_RUST;
		String tTileInfo = Train.NO_TILE_INFO;
		boolean tIsUnlimited;

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

		return tTrainSummary;
	}

	public String buildTrainInfo (String aPreviousName, String aCost, int aCount, boolean aIsUnlimited, String aRustInfo, String aTileInfo) {
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
		tTrainInfo = aPreviousName + " Train QTY: " + tTrainCount + " " + aCost + tRustTileInfo + NEWLINE;

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
		int tTrainLimit = 0;

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
