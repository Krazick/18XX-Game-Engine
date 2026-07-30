package ge18xx.player;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ge18xx.bank.Bank;
import ge18xx.center.City;
import ge18xx.center.CityInfo;
import ge18xx.company.ShareCompany;
import ge18xx.map.Location;
import ge18xx.tiles.TileType;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import swingTweaks.KButton;

public class ContractBid implements ActionListener, FocusListener {
	public static final ElementName EN_CONTRACT_BID = new ElementName ("ContractBid");
	public static final AttributeName AN_EXTRA_FOR_BOND = new AttributeName ("extraForBond");
	public static final AttributeName AN_SIGNED = new AttributeName ("signed");
	public static final AttributeName AN_FULLFILLED = new AttributeName ("fullfilled");
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final JPanel NO_CONTRACT_BID_PANEL = null;
	public static final int NO_EXTRA_BOND = 0;
	public static final int DELTA_CITY_MAX_COUNT = 2;
	Player player;
	JPanel contractBidJPanel;
	List<ContractLine> contractLines;
	boolean signed;
	boolean fullfilled;
	int extraForBond;
	
	JTextField extraBidJTextField;
	JTextField totalBondJTextField;
	DefaultTableModel contractLinesModel; // = new DefaultTableModel (0, 5);
	JTable contractLinesJTable;

	public ContractBid (Player aPlayer) {
		setPlayer (aPlayer);
		setSigned (false);
		setFullfilled (false);
		setExtraForBond (NO_EXTRA_BOND);
		contractLines = new LinkedList<> ();
		buildJPanel ();
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
		
		tXMLNodeList = new XMLNodeList (contractBidParsingRoutine);
		tExtraForBond = aXMLNode.getThisIntAttribute (AN_EXTRA_FOR_BOND);
		tSigned = aXMLNode.getThisBooleanAttribute (AN_SIGNED);
		tFullfilled = aXMLNode.getThisBooleanAttribute (AN_FULLFILLED);
		setExtraForBond (tExtraForBond);
		setSigned (tSigned);
		setFullfilled (tFullfilled);
		
		tXMLNodeList.parseXMLNodeList (aXMLNode, ContractLine.EN_CONTRACT_LINES);

		buildJPanel ();
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
	
	public void buildJPanel () {
		JPanel tJPanel;
		
		if (contractBidJPanel == NO_CONTRACT_BID_PANEL) {
			tJPanel = new JPanel ();
			tJPanel.setLayout (new BoxLayout (tJPanel, BoxLayout.Y_AXIS));
			setContractBidJPanel (tJPanel);
			
			fillContractBidJPanel ();
			tJPanel.setVisible (false);
		}
	}

	public void fillContractBidJPanel () {
		JLabel tTitleLine;
		JPanel tExtraBidJPanel;
		JPanel tTotalBondJPanel;
		JPanel tContractLinesJPanel;
		JLabel tCityCount;
		int tMinCityCount;
		int tMaxCityCount;
		
		contractBidJPanel.removeAll ();
		contractBidJPanel.add (Box.createVerticalStrut (10));
		tTitleLine = new JLabel ("Contract Bid for " + player.getName ());
		tTitleLine.setAlignmentX (Component.LEFT_ALIGNMENT);
		contractBidJPanel.add (tTitleLine);
		contractBidJPanel.add (Box.createVerticalStrut (10));
		
		tMinCityCount = player.getMinBidCities ();
		tMaxCityCount = player.getMaxBidCities ();
		tCityCount = new JLabel ("City Count must be between " + tMinCityCount + " and " + tMaxCityCount);
		tTitleLine.setAlignmentX (Component.LEFT_ALIGNMENT);
		contractBidJPanel.add (tCityCount);
		contractBidJPanel.add (Box.createVerticalStrut (10));
		
		tContractLinesJPanel = fillContractLinesJPanel ();
		contractBidJPanel.add (tContractLinesJPanel);
		
		extraBidJTextField = new JTextField (5);
		extraBidJTextField.addActionListener (this);
		extraBidJTextField.addFocusListener (this);
		extraBidJTextField.setMaximumSize (extraBidJTextField.getMinimumSize ());
		extraBidJTextField.setHorizontalAlignment (SwingConstants.RIGHT);
		tExtraBidJPanel = buildOneFieldPanel (extraBidJTextField, "Extra Bid: ");
		contractBidJPanel.add (tExtraBidJPanel);
		contractBidJPanel.add (Box.createVerticalStrut (10));

		totalBondJTextField = new JTextField (5);
		totalBondJTextField.addActionListener (this);
		totalBondJTextField.setMaximumSize (totalBondJTextField.getMinimumSize ());
		totalBondJTextField.setHorizontalAlignment (SwingConstants.RIGHT);
		totalBondJTextField.setEnabled (false);
		tTotalBondJPanel = buildOneFieldPanel (totalBondJTextField, "Total Bond Value: ");

		contractBidJPanel.add (tTotalBondJPanel);
		contractBidJPanel.add (Box.createVerticalStrut (10));
	}
	
	private JPanel fillContractLinesJPanel () {
		JPanel tContractLinesJPanel;
		JPanel tContractLineJPanel;
		
		tContractLinesJPanel = new JPanel ();
		tContractLinesJPanel.setBackground (Color.CYAN);
		
//		System.out.println ("Contract Line Count " + contractLines.size ());
		tContractLineJPanel = buildContractLinesJPanel ();
		
		tContractLinesJPanel.add (tContractLineJPanel);
		
		return tContractLinesJPanel;
	}

	private void addTestContractLine () {
		ContractLine tTestContractLine;
		City tTestCity;
		CityInfo tCityInfo;
		Location tLocation;
		ShareCompany tTestShareCompany;
		int tTestBond;
		
		tLocation = new Location (50);
		tCityInfo = new CityInfo (22, "Calcutta", tLocation, 3);
		
		tTestCity = new City (3, 6, 22, 7, "", 30, TileType.NO_TILE_TYPE);
		tTestCity.setCityInfo (tCityInfo);
		tTestShareCompany = ShareCompany.NO_SHARE_COMPANY;
		tTestBond = 50;
		tTestContractLine = new ContractLine (tTestCity, tTestShareCompany, tTestBond);
		addContractLine (tTestContractLine);
	}
	
	private JPanel buildOneFieldPanel (JTextField aJTextField, String aLabelText) {
		JPanel tFieldJPanel;
		JLabel tLabel;

		tLabel = new JLabel (aLabelText);

		tFieldJPanel = new JPanel ();
		tFieldJPanel.setLayout (new BoxLayout (tFieldJPanel, BoxLayout.X_AXIS));
		tFieldJPanel.add (Box.createHorizontalStrut (10));
		tFieldJPanel.add (Box.createHorizontalGlue ());
		tFieldJPanel.add (tLabel);
		tFieldJPanel.add (Box.createHorizontalGlue ());
		tFieldJPanel.add (aJTextField);
		tFieldJPanel.add (Box.createHorizontalGlue ());
		tFieldJPanel.add (Box.createHorizontalStrut (10));
		
		return tFieldJPanel;
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
	
	public Player getPlayer () {
		return player;
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
	
	public JPanel getContractBidJPanel () {
		return contractBidJPanel;
	}
	
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
			addRow (aContractLine);
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
	
	public boolean hasActionsToUndo () {
		return player.hasActionsToUndo ();
	}
	
	public void showContractBidJPanel () {
		contractBidJPanel.setVisible (true);
	}
	
	public void hideContractBidJPanel () {
		contractBidJPanel.setVisible (false);
	}
	
	public ContractLine getContractLineAt (int aIndex) {
		ContractLine tContractLine;
		
		tContractLine = contractLines.get (aIndex);
		
		return tContractLine;
	}

	public void setExtraBidJTextField (int aExtraForBid) {
		String tExtraForBidText;
		
		setExtraForBond (aExtraForBid);
		tExtraForBidText = GUI.EMPTY_STRING + aExtraForBid;
		extraBidJTextField.setText (tExtraForBidText);
	}
	
	public void setTotalBidJTextField (int aTotalForBid) {
		String tBidJTextField;
		
		tBidJTextField = GUI.EMPTY_STRING + aTotalForBid;
		totalBondJTextField.setText (tBidJTextField);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		System.out.println ("Contract Bid triggered Action Performed");
	}

	@Override
	public void focusGained (FocusEvent aEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void focusLost (FocusEvent aEvent) {
		Object tEventObject;
		int tExtraBidValue;
		int tTotalBidValue;
		String tExtraBidText;
		
		tEventObject = aEvent.getSource ();
		if (tEventObject instanceof JTextField) {
			tExtraBidValue = NO_EXTRA_BOND;
			if (tEventObject == this.extraBidJTextField) {
				tExtraBidText = extraBidJTextField.getText ();
				try {
					tExtraBidValue = Integer.parseInt (tExtraBidText);
					System.out.println ("Extra Bid Value entered: " + tExtraBidValue);
				} catch (Exception eExtraBid) {
				}
			}
			
			setExtraBidJTextField (tExtraBidValue);
			tTotalBidValue = getTotalValue ();
			setTotalBidJTextField (tTotalBidValue);
		}
	}
	
	public JPanel buildContractLinesJPanel () {
		String [] tColumnNames = { "City", "Company", "Bond", "Connected", "Delete" };
		int tColWidths [] = { 150, 150, 100, 100, 100 };
		int tTotalWidth;
		JPanel tContractLinesJPanel;
		
		tTotalWidth = 0;
		tContractLinesJPanel = configureContractLinesTable (tColumnNames, tColWidths, tTotalWidth);
		
		return tContractLinesJPanel;
	}
	
	private <T> JPanel configureContractLinesTable (String [] aColumnNames, int [] aColWidths, int aTotalWidth) {
		TableColumnModel tColumnModel;
		JPanel tContractLinesJPanel;
		
		contractLinesModel = new DefaultTableModel (new Object [] 
				{aColumnNames [0], aColumnNames [1], aColumnNames [2], aColumnNames [3], 
				aColumnNames [4]}, 6) {
//			@SuppressWarnings ({ "unchecked" })
//			@Override
//			public Class<T> getColumnClass (int aColumnIndex) {
//				@SuppressWarnings ("rawtypes")
//				Class<T> tColumnClass;
//				
//				if (aColumnIndex == 3) {
//					tColumnClass = (Class<T>) Boolean.class;
//				} else {
//					tColumnClass = (Class<T>) super.getColumnClass (aColumnIndex);
//				}
//				
//				return tColumnClass;
//			}
		};
		
		contractLinesJTable = new JTable (contractLinesModel);
//		addTestContractLine ();

//		contractLinesModel.setColumnIdentifiers (aColumnNames);
//		contractLinesJTable.setModel (contractLinesModel);
		contractLinesJTable.setGridColor (Color.BLACK);
		contractLinesJTable.setShowGrid (true);
		contractLinesJTable.setShowVerticalLines (true);
		contractLinesJTable.setShowHorizontalLines (true);
		contractLinesJTable.setFillsViewportHeight(true);
//		contractLinesJTable.setFillsViewportWidth(true);

		tColumnModel = contractLinesJTable.getColumnModel ();

		for (int tIndex = 0; tIndex < aColWidths.length; tIndex++) {
			tColumnModel.getColumn (tIndex).setMaxWidth (aColWidths [tIndex]);

			aTotalWidth += aColWidths [tIndex] + 1;
		}
//		setColumnAlign (2, SwingConstants.RIGHT);
//		setColumnAlign (3, SwingConstants.CENTER);
		tContractLinesJPanel = buildScrollPane (contractLinesJTable);
		tContractLinesJPanel.setBackground (Color.blue);
		
		return tContractLinesJPanel;
	}
	
	public void addRow (ContractLine aContractLine) {
		String tShareCompanyName;
		String tCityName;
		ShareCompany tShareCompany;
		City tCity;
		int tBond;
		boolean tConnected;
		KButton tDeleteButton;
		
		tShareCompany = aContractLine.getShareCompany ();
		tCity = aContractLine.getCity ();
		tBond = aContractLine.getBond ();
		tConnected = aContractLine.isConnected ();
		if (tShareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tShareCompanyName = "UNKNOWN";
		} else {
			tShareCompanyName = tShareCompany.getName ();
		}
		if (tCity == City.NO_CITY) {
			tCityName = "UNKOWN";
		} else {
			tCityName = tCity.getCityName ();
		}
		tDeleteButton = new KButton ("X");
		contractLinesModel.addRow (
				new Object [] { tCityName, tShareCompanyName, tBond, tConnected, tDeleteButton }
			);
	}
	
	public JPanel buildScrollPane (JComponent aImage) {
		JPanel tJPanel;
		
		tJPanel = buildScrollPane (aImage, GUI.NULL_STRING);
		
		return tJPanel; 
	}

	public JPanel buildScrollPane (JComponent aImage, String aBorderLayout) {
		JScrollPane tScrollPane;
		JPanel tJPanel;
		
		tJPanel = new JPanel ();
		tScrollPane = new JScrollPane (aImage);
		if (aBorderLayout != GUI.NULL_STRING) {
			tJPanel.add (tScrollPane, aBorderLayout);
		} else {
			tJPanel.add (tScrollPane);
		}
		
		return tJPanel;
	}

//	private void setColumnAlign (int aColumnIndex, int tAlignment) {
//		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer ();
//
//		tCellRenderer.setHorizontalAlignment (tAlignment);
//		contractLinesJTable.getColumnModel ().getColumn (aColumnIndex).setHeaderRenderer (tCellRenderer);
//		contractLinesJTable.getColumnModel ().getColumn (aColumnIndex).setCellRenderer (tCellRenderer);
//	}

	// New Methods to add:
	// GenerateActionEffects -- Will generate the Action with Effects XML of the ContractBid
	// ParseActionEffects -- Will parse the Action with Effects XML of the ContractBid
}
