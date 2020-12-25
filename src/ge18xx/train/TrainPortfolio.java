package ge18xx.train;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.ParsingRoutineIO;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.Container;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TrainPortfolio implements TrainHolderI {
	public final static ElementName EN_TRAIN_PORTFOLIO = new ElementName ("TrainPortfolio");
	public final static ElementName EN_RUSTED_TRAIN_PORTFOLIO = new ElementName ("RustedTrainPortfolio");
	public static final Train NO_TRAIN = null;
	public static final TrainHolderI NO_TRAIN_HOLDER = null;
	public static final String ALL_TRAINS = "ALL";
	public static final String AVAILABLE_TRAINS = "AVAILABLE";
	public static final String FUTURE_TRAINS = "FUTURE";
	public static final String RUSTED_TRAINS = "RUSTED";
	public static final boolean FULL_TRAIN_PORTFOLIO = true;
	public static final boolean COMPACT_TRAIN_PORTFOLIO = false;
	ArrayList<Train> trains;
	CashHolderI portfolioHolder;
	
	public TrainPortfolio () {
		trains = new ArrayList<Train> ();
	}
	
	public TrainPortfolio (CashHolderI aPortfolioHolder) {
		trains = new ArrayList<Train> ();
		setPortfolioHolder (aPortfolioHolder);
	}
	
	public void setPortfolioHolder (CashHolderI aPortfolioHolder) {
		portfolioHolder = aPortfolioHolder;
	}
	
	public void addTrain (Train aTrain) {
		trains.add (aTrain);
		Collections.sort (trains);
	}

	public JPanel buildPortfolioJPanel (ItemListener aItemListener, Corporation aCorporation, 
			GameManager aGameManager, String aActionLabel, boolean aFullvsCompact,
			boolean aEnableAction, String aDisableReason) {
		JPanel tPortfolioContainer;
		JLabel tLabel;
		Container tTrainCertContainer;
		int tTrainIndex, tTrainCount, tTrainQuantity;
		Train tTrain;
		String tTrainName, tLabelText;
		String tActionLabel;
		String tActionToolTip;
		boolean tActionEnabled;
		boolean tCanBeUpgraded;
		TrainCompany tTrainCompany;
		Train [] tBankAvailableTrains = aGameManager.getBankAvailableTrains ();
		
		tPortfolioContainer = new JPanel ();
		if (trains.isEmpty ()) {
			tLabel = new JLabel (">> NO TRAINS <<");
			tPortfolioContainer.add (tLabel);
		} else {
			tTrainIndex = 0;
			tTrainCount = getTrainCount ();
			while (tTrainIndex < tTrainCount) {
				tTrain = getTrainAt (tTrainIndex);
				tTrainName = tTrain.getName ();
				tTrainQuantity = getTrainQuantity (tTrainName);
				tCanBeUpgraded = tTrain.canUpgrade (tBankAvailableTrains);
				tActionToolTip = "";
				if (aCorporation.isOperating () && tCanBeUpgraded) {
					tActionLabel = "Upgrade";
					if (aCorporation.canBuyTrain ()) {
						tActionEnabled = true;
						tActionToolTip = "";
					} else {
						tActionEnabled = false;
						if (aCorporation.atTrainLimit ()) {
							tActionToolTip = "Corporation at Train Limit";
						} else if (aCorporation.getCash () == 0) {
							tActionToolTip = "Corporation has no Cash";

						} else {
							tActionToolTip = "Train has not handled dividends yet";
						}
					}
				} else {
					tActionLabel = null;
					tActionEnabled = false;
				}
				if (aActionLabel != null) {
					if (tTrain.isAvailableForPurchase ()) {
						tActionLabel = aActionLabel;
						tActionEnabled = aEnableAction;
						if (!aEnableAction && (tActionToolTip == "")) {
							tActionToolTip = aDisableReason;
						}
					}
					if (aCorporation.getName ().equals (portfolioHolder.getName ()))  {
					} else {
						if (aCorporation instanceof TrainCompany) {
							tTrainCompany = (TrainCompany) aCorporation;
							if (tTrainCompany.atTrainLimit ()) {
								tActionToolTip = "Company has reached Train Limit";
								tActionEnabled = false;
							}
							if (tTrainCompany.getTreasury () < tTrain.getPrice ()) {
								tActionToolTip = "Company does not have sufficient funds";
								tActionEnabled = false;
							}
						}
					}
				}
				tTrainCertContainer = tTrain.buildCertificateInfoContainer (aItemListener, tActionLabel, 
						tActionEnabled, tActionToolTip);
				if (aFullvsCompact == COMPACT_TRAIN_PORTFOLIO) {
					if (tTrainQuantity > 1) {
						tLabelText = "<br>" + (tTrainQuantity - 1) + " More<br>" + tTrainName + " Train";
						if (tTrainQuantity > 2) {
							tLabelText += "s";
						}
					} else if (tTrainQuantity == 1) {
						tLabelText = "<br>LAST " + tTrainName + " Train";
					} else {
						tLabelText = "";
					}
					tLabel = new JLabel ("<html>" + tLabelText + "</html>");
				
					if (tTrainQuantity > 0) {
						tTrainCertContainer.add (tLabel);
					}
					tTrainIndex += tTrainQuantity - 1;	
				}
				tPortfolioContainer.add (tTrainCertContainer);
				tTrainIndex++;
			}			
		}
		
		return tPortfolioContainer;
	}
	
	public void clearSelections () {
		if (trains != null) {
			for (Train tTrain : trains) {
				tTrain.clearSelection ();
			}
		}
	}
	
	public int countTrainsOfThisOrder (int aOrder) {
		int tCountOfTrains;
		
		tCountOfTrains = 0;
		if (trains != null) {
			for (Train tTrain : trains) {
				if (tTrain.isTrainThisOrder (aOrder)) {
					tCountOfTrains++;
				}
			}
		}
		
		return tCountOfTrains;
	}
	
	@Override
	public CashHolderI getCashHolder() {
		return null;
	}
	
	public String getTrainList () {
		String tTrainList = "NO TRAINS";
		int tTrainIndex = 0;
		
		if (trains.size () > 0) {
			tTrainList = "Trains (";
			for (Train tTrain : trains) {
				tTrainList += tTrain.getName ();
				tTrainIndex++;
				if (tTrainIndex < trains.size ()) {
					tTrainList += ", ";
				}
			}
			tTrainList += ")";
		}
		
		return tTrainList;
	}
	
	public Train getCheapestTrain () {
		Train tCheapestTrain;
		
		tCheapestTrain = NO_TRAIN;
		for (Train tTrain : trains) {
			if (tCheapestTrain == NO_TRAIN) {
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
	
	public Train getSelectedTrain () {
		Train tSelectedTrain;
	
		tSelectedTrain = NO_TRAIN;
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

	public Train getTrainOfOrder (int aOrder) {
		Train tTrainOfOrder;
		
		tTrainOfOrder = NO_TRAIN;			
		
		if (! trains.isEmpty ()) {
			for (Train tTrain : trains) {
				if (tTrain.getOrder () == aOrder) {
					tTrainOfOrder = tTrain;
				}
			}
		}
		
		return tTrainOfOrder;
	}
	
	public Train getTrain (String aName) {
		Train tTrain;
		int tIndex, tCount;
		String tTrainName;
		
		tTrain = NO_TRAIN;
		tCount = getTrainCount ();
		if (tCount > 0) {
			for (tIndex = 0; (tIndex < tCount) && (tTrain == NO_TRAIN); tIndex++) {
				tTrainName = trains.get (tIndex).getName ();
				if (tTrainName.equals (aName)) {
					tTrain = trains.get (tIndex);
				}
			}
		}
		
		return tTrain;
	}

	public Train getTrainAt (int aIndex) {
		Train tTrain;
		
		if (trains.isEmpty ()) {
			tTrain = NO_TRAIN;
		} else {
			if ((aIndex >= 0) && (aIndex < getTrainCount ())) {
				tTrain = trains.get (aIndex);
			} else {
				tTrain = NO_TRAIN;
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

	public String getTrainNameAndQty (String aStatus) {
		String tNameAndQuantity, tName;
		String tNames [];
		int tQuantities [];
		int tIndex1, tCount1, tIndex2, tCount2;;
		Train tTrain;
		boolean tFoundTrain, tAddTrain;
		
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
					if (tFoundTrain == false) {
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
	public TrainPortfolio getTrainPortfolio() {
		return this;
	}
	
	public int getTrainQuantity (String aName) {
		int tTrainQuantity, tCount, tIndex;
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

	public boolean hasTrainNamed (String aTrainName) {
		boolean tHasTrain;
		
		tHasTrain = false;
		for (Train tTrain : trains) {
			if (tTrain.getName ().equals (aTrainName)) {
				tHasTrain = true;
			}
		}
		
		return tHasTrain;
	}
	
	public boolean isSelectedItem (Object aItem) {
		boolean tIsSelectedItem;
		
		tIsSelectedItem = false;
		for (Train tTrain : trains) {
			if (tTrain.isThisCheckBox (aItem)) {
				tIsSelectedItem = true;
			}
		}
		
		return tIsSelectedItem;
	}

	public void loadTrainStatus (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;
		
		tXMLNodeList = new XMLNodeList (trainStatusParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Train.EN_TRAIN);
	}
	
	ParsingRoutineI trainStatusParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aTrainNode) {
			String tTrainName;
			int tTrainStatus, tTrainOrder;
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
	
	ParsingRoutineI trainPortfolioParsingRoutine  = new ParsingRoutineIO ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aBank) {
			Bank tBank;
			
			tBank = (Bank) aBank;
			loadTrainPortfolioFromBank (aChildNode, tBank);
		}

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			
		}
	};

	public void loadTrainPortfolioFromBank (XMLNode aTrainNode, Bank aBank) {	
		XMLNodeList tXMLNodeList;
		
		tXMLNodeList = new XMLNodeList (trainParsingRoutine, aBank);
		tXMLNodeList.parseXMLNodeList (aTrainNode, Train.EN_TRAIN);
	}
	
	public void loadTrainFromBank (XMLNode aTrainNode, Bank aBank) {
		String tTrainName;
		int tTrainStatus;
		Train tTrain;
		
		tTrainName = aTrainNode.getThisAttribute (Train.AN_NAME);
		tTrainStatus = aTrainNode.getThisIntAttribute (Train.AN_STATUS);
		tTrain = aBank.getTrain (tTrainName);
		aBank.removeTrain (tTrainName);
		tTrain.setStatus (tTrainStatus);
		trains.add (tTrain);
		loadRouteForTrain (aTrainNode, Train.EN_CURRENT_ROUTE, tTrain);
		loadRouteForTrain (aTrainNode, Train.EN_CURRENT_ROUTE, tTrain);
	}
	
	public void loadRouteForTrain (XMLNode aTrainNode, ElementName aElementName, Train aTrain) {
		XMLNodeList tXMLNodeList;
		
		tXMLNodeList = new XMLNodeList (trainRouteParsingRoutine, aTrain);
		tXMLNodeList.parseXMLNodeList (aTrainNode, aElementName);
	}
	
	ParsingRoutineIO trainRouteParsingRoutine  = new ParsingRoutineIO ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aRouteNode, Object aTrain) {
			Train tTrain;
			
			tTrain = (Train) aTrain;
			tTrain.loadRouteInformation (aRouteNode);
		}

		@Override
		public void foundItemMatchKey1(XMLNode aChildNode) {
			
		}
	};
	
	ParsingRoutineI trainParsingRoutine  = new ParsingRoutineIO ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aBank) {
			Bank tBank;
			
			tBank = (Bank) aBank;
			loadTrainFromBank (aChildNode, tBank);
		}

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			
		}
	};
	
	public boolean removeSelectedTrain () {
		boolean tTrainRemoved;
		int tIndex, tCount;
		Train tTrain;
		
		tCount = getTrainCount ();
		tTrainRemoved = false;
		if (tCount > 0) {
			for (tIndex = 0; (tIndex < tCount) && (tTrainRemoved == false); tIndex++) {
				tTrain = trains.get (tIndex);
				if (tTrain.isSelected ()) {
					if (!(tTrain.isUnlimitedQuantity ())) {
						trains.remove (tIndex);
						tTrainRemoved = true;
					} 
				}
			}
		}
		
		return tTrainRemoved;
	}
	
	public boolean removeTrain (String aTrainName) {
		boolean tTrainRemoved;
		int tIndex, tCount;
		Train tTrain;
		
		tCount = getTrainCount ();
		tTrainRemoved = false;
		if (tCount > 0) {
			for (tIndex = 0; (tIndex < tCount) && (tTrainRemoved == false); tIndex++) {
				tTrain = trains.get (tIndex);
				if (tTrain.getName ().equals(aTrainName)) {
					if (!(tTrain.isUnlimitedQuantity ())) {
						trains.remove (tIndex);
						tTrainRemoved = true;
					} 
				}
			}
		}
		
		return tTrainRemoved;
	}

	public void rustAllTrainsNamed (String aTrainName, TrainPortfolio aRustedTrainPortfolio, 
			ActorI aCurrentHolder, Bank aBank, BuyTrainAction aBuyTrainAction) {
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
}
