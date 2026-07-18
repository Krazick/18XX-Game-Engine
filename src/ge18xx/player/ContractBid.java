package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.center.City;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public class ContractBid {
	public static final ElementName EN_CONTRACT_BID = new ElementName ("ContractBid");
	public static final AttributeName AN_EXTRA_FOR_BOND = new AttributeName ("extraForBond");
	public static final AttributeName AN_SIGNED = new AttributeName ("signed");
	public static final AttributeName AN_FULLFILLED = new AttributeName ("fullfilled");
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final int NO_EXTRA_BOND = 0;
	public static final int DELTA_CITY_MAX_COUNT = 2;
	Player player;
	JPanel contractBidJPanel;
	List<ContractLine> contractLines;
	boolean signed;
	boolean fullfilled;
	int extraForBond;
	
//	public ContractBid () {
//		this (Player.NO_PLAYER);
//	}
	
	public ContractBid (Player aPlayer) {
		JPanel tContractBidJPanel;
		
		setPlayer (aPlayer);
		setSigned (false);
		setFullfilled (false);
		setExtraForBond (NO_EXTRA_BOND);
		contractLines = new LinkedList<> ();
		tContractBidJPanel = buildJPanel ();
		setContractBidJPanel (tContractBidJPanel);
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLContractBidElement;
		XMLElement tXMLContractLineElement;
		XMLElement tXMLContractLinesElement;
		
		tXMLContractBidElement = aXMLDocument.createElement (EN_CONTRACT_BID);
		if (contractLines.size () > 0) {
			tXMLContractLinesElement = aXMLDocument.createElement (ContractLine.EN_CONTRACT_LINES);
			for (ContractLine tContractLine : contractLines) {
				tXMLContractLineElement = tContractLine.getElements (aXMLDocument);
				tXMLContractLinesElement.appendChild (tXMLContractLineElement);
			}
			tXMLContractBidElement.appendChild (tXMLContractLinesElement);
		}
		tXMLContractBidElement.setAttribute (AN_EXTRA_FOR_BOND, extraForBond);
		tXMLContractBidElement.setAttribute (AN_SIGNED, signed);
		tXMLContractBidElement.setAttribute (AN_FULLFILLED, fullfilled);

		return tXMLContractBidElement;
	}

	public void loadXMLNode (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;
		int tExtraForBond;
		boolean tSigned;
		boolean tFullfilled;
		JPanel tContractBidJPanel;
		
		tXMLNodeList = new XMLNodeList (contractBidParsingRoutine);
		tExtraForBond = aXMLNode.getThisIntAttribute (AN_EXTRA_FOR_BOND);
		tSigned = aXMLNode.getThisBooleanAttribute (AN_SIGNED);
		tFullfilled = aXMLNode.getThisBooleanAttribute (AN_FULLFILLED);
		setExtraForBond (tExtraForBond);
		setSigned (tSigned);
		setFullfilled (tFullfilled);
		
		tXMLNodeList.parseXMLNodeList (aXMLNode, ContractLine.EN_CONTRACT_LINES);
		
		tContractBidJPanel = buildJPanel ();
		setContractBidJPanel (tContractBidJPanel);
	}
	
	ParsingRoutineI contractBidParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			XMLNodeList tXMLNodeList;

			tXMLNodeList = new XMLNodeList (contractLineParsingRoutine);
			tXMLNodeList.parseXMLNodeList (aChildNode, ContractLine.EN_CONTRACT_LINE);
		}
		
		ParsingRoutineI contractLineParsingRoutine = new ParsingRoutineI () {
			@Override
			public void foundItemMatchKey1 (XMLNode aChildNode) {
				ContractLine tContractLine;

				tContractLine = new ContractLine (aChildNode, player);
				addContractLine (tContractLine);
			}
		};
	};
	
	public JPanel buildJPanel () {
		JPanel tJPanel;
		JLabel tTitleLine;
		
		tJPanel = new JPanel ();
		tJPanel.setLayout (new BoxLayout (tJPanel, BoxLayout.Y_AXIS));
		tTitleLine = new JLabel ("Contract Bid for " + player.getName ());
		tJPanel.add (tTitleLine);
		
		return tJPanel;
	}
	
	public void setContractBidJPanel (JPanel aContractBidJPanel) {
		contractBidJPanel = aContractBidJPanel;
	}
	
	public void setSigned (boolean aSigned) {
		signed = aSigned;
	}

	public void setPlayer (Player aPlayer) {
		player = aPlayer;
	}
	
	public boolean isSigned () {
		return signed;
	}
	
	public void setFullfilled (boolean aFullfilled) {
		fullfilled = aFullfilled;
	}
	
	public boolean isFullfilled () {
		return fullfilled;
	}
	
	public void setExtraForBond (int aExtraForBond) {
		extraForBond = aExtraForBond;
	}
	
//	public JPanel getContractBidJPanel () {
//		return contractBidJPanel;
//	}
	
	public int getExtraForBond () {
		return extraForBond;
	}
	
	public int getCityCount () {
		return contractLines.size ();
	}
	
	public int getDeltaCityCount () {
		int tDeltaCityCount;
		
		tDeltaCityCount = 0;
		for (ContractLine tContractLine : contractLines) {
			if (tContractLine.isDeltaTerrain ()) {
				tDeltaCityCount++;
			}
		}

		return tDeltaCityCount;
	}
	
	public int getTotalValue () {
		int tTotalValue;
		
		tTotalValue = extraForBond;
		for (ContractLine tContractLine : contractLines) {
			tTotalValue += tContractLine.getBond ();
		}
		
		return tTotalValue;
	}
	
	public JLabel buildLabel () {
		JLabel tJLabel;
		String tText;
		
		tText = "Contract Bid: ";
		if (isFullfilled ()) {
			tText += "Fulfilled";
		} else if (isSigned ()) {
			tText += "Signed " + getCityCount () + "/" + Bank.formatCash (getTotalValue ());
		} else {
			tText += "Unsigned";
		}
		
		tJLabel = new JLabel (tText);

		return tJLabel;
	}
	
	public boolean cityAlreadyInContractLines (String aCityName) {
		boolean tCityAlreadyInContractLines;
		String tContractCityName;
		
		tCityAlreadyInContractLines = false;
		if (getCityCount () > 0) {
			for (ContractLine tContractLine : contractLines) {
				tContractCityName = tContractLine.getCityName ();
				if (tContractCityName.equals (aCityName)) {
					tCityAlreadyInContractLines = true;
				}
			}
		}
		
		return tCityAlreadyInContractLines;
	}
	
	public void addContractLine (ContractLine aContractLine) {
		String tNewCityName;
		
		tNewCityName = aContractLine.getCityName ();
		if (! cityAlreadyInContractLines (tNewCityName)) {
			contractLines.add (aContractLine);
		}
	} 
	
	public void deleteContractLine (City aCity) {
		String tCityNameToDelete;
		String tContractCityName;
		ContractLine tContractLineToDelete;
		
		if (aCity == City.NO_CITY) {
			tCityNameToDelete = GUI.EMPTY_STRING;
		} else {
			tCityNameToDelete = aCity.getCityName ();
		}
	
		if (cityAlreadyInContractLines (tCityNameToDelete)) {
			tContractLineToDelete = ContractLine.NO_CONTRACT_LINE;
			for (ContractLine tContractLine : contractLines) {
				tContractCityName = tContractLine.getCityName ();
				if (tContractCityName.equals (tCityNameToDelete)) {
					tContractLineToDelete = tContractLine;
				}
			}
			contractLines.remove (tContractLineToDelete);
		}
	}

	public String getAllReasonsInvalid () {
		String tAllReasonsInvalid;
		
		tAllReasonsInvalid = GUI.EMPTY_STRING;
		for (ContractLine tContractLine : contractLines) {
			if (! tContractLine.isValidContractLine ()) {
				tAllReasonsInvalid += tContractLine.getAllReasonsContractLineInvalid ();
			}
		}
		if (getCityCount () < player.getMinBidCities ()) {
			tAllReasonsInvalid += "Not enough Cities (minimum is " + 
							player.getMinBidCities () + ") are in the Contract Bid\n";
		}
		if (getCityCount () > player.getMaxBidCities ()) {
			tAllReasonsInvalid += "Too many Cities (maximum is " +
					player.getMaxBidCities () + ") are in the Contract Bid\n";			
		}
		if (getDeltaCityCount () > DELTA_CITY_MAX_COUNT) {
			tAllReasonsInvalid += "Too many Cities in the Delta (maximum of " +
							DELTA_CITY_MAX_COUNT + ") are in the Contract Bid\n";	
		}
		if (player.getCash () < getTotalValue ()) {
			tAllReasonsInvalid += "Player does not have enough cash to post bond.";
		}
		
		return tAllReasonsInvalid;
	}
	
	private boolean allContractLinesAreValid () {
		boolean tAllContractLinesAreValid;
		
		tAllContractLinesAreValid = true;
		for (ContractLine tContractLine : contractLines) {
			if (! tContractLine.isValidContractLine ()) {
				tAllContractLinesAreValid = false;
			}
		}

		return tAllContractLinesAreValid;
	}

	public boolean isValid () {
		boolean tIsValid;
		
		tIsValid = true;
		
		if (getCityCount () < player.getMinBidCities ()) {
			tIsValid = false;
		}
		if (getCityCount () > player.getMaxBidCities ()) {
			tIsValid = false;
		}
		if (getDeltaCityCount () > DELTA_CITY_MAX_COUNT) {
			tIsValid = false;
		}
		if (player.getCash () < getTotalValue ()) {
			tIsValid = false;
		}
		if (! allContractLinesAreValid ()) {
			tIsValid = false;
		}
		
		return tIsValid;
	}
	
	public ContractLine getContractLineAt (int aIndex) {
		ContractLine tContractLine;
		
		tContractLine = contractLines.get (aIndex);
		
		return tContractLine;
	}
	
	// New Methods to add:
	// GenerateActionEffects -- Will generate the Action with Effects XML of the ContractBid
	// ParseActionEffects -- Will parse the Action with Effects XML of the ContractBid
}
