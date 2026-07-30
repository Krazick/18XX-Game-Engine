package ge18xx.player;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.center.City;
import ge18xx.company.ShareCompany;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ContractLine {
	public static final ContractLine NO_CONTRACT_LINE = null;
	public static final ElementName EN_CONTRACT_LINE = new ElementName ("ContractLine");
	public static final ElementName EN_CONTRACT_LINES = new ElementName ("ContractLines");
	public static final AttributeName AN_CITY_NAME = new AttributeName ("cityName");
	public static final AttributeName AN_SHARE_COMPANY_ID = new AttributeName ("shareCompanyID");
	public static final AttributeName AN_CONNECTED = new AttributeName ("connected");
	public static final AttributeName AN_BOND = new AttributeName ("bond");

	ShareCompany shareCompany;
	City city;
	boolean connected;
	int bond;
//	DefaultTableModel contractLinesModel = new DefaultTableModel (0, 5);
//	JTable contractLinesJTable;

	public ContractLine () {
		this (City.NO_CITY, ShareCompany.NO_SHARE_COMPANY, 0);
	}
	
	public ContractLine (City aCity, ShareCompany aShareCompany, int aBond) {
		setCity (aCity);
		setShareCompany (aShareCompany);
		setBond (aBond);
		setConnected (false);
	}

	public ContractLine (XMLNode aXMLNode, Player aPlayer) {
		City tCity;
		String tCityName;
		ShareCompany tShareCompany;
		int tShareCompanyID;
		int tBond;
		boolean tConnected;
		
		tCityName = aXMLNode.getThisAttribute (AN_CITY_NAME);
		tShareCompanyID = aXMLNode.getThisIntAttribute (AN_SHARE_COMPANY_ID);
		tBond = aXMLNode.getThisIntAttribute (AN_BOND);
		tConnected = aXMLNode.getThisBooleanAttribute (AN_CONNECTED);
		
		tCity = getCityWithName (tCityName, aPlayer);
		setCity (tCity);
		tShareCompany = getShareCompanyByID (tShareCompanyID, aPlayer);
		setShareCompany (tShareCompany);
		setBond (tBond);
		setConnected (tConnected);

	}
	
	public City getCityWithName (String aCityName, Player aPlayer) {
		City tCity;
		
		tCity = aPlayer.getCityWithName (aCityName);
		
		return tCity;
	}
	
	public ShareCompany getShareCompanyByID (int aShareCompanyID, Player aPlayer) {
		ShareCompany tShareCompany;
		
		tShareCompany = aPlayer.getShareCompanyByID (aShareCompanyID);
		
		return tShareCompany;
	}

	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLContractLineElement;
		
		tXMLContractLineElement = aXMLDocument.createElement (EN_CONTRACT_LINE);
		if (city == City.NO_CITY) {
			tXMLContractLineElement.setAttribute (AN_CITY_NAME, GUI.EMPTY_STRING);			
		} else {
			tXMLContractLineElement.setAttribute (AN_CITY_NAME, city.getCityName ());
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tXMLContractLineElement.setAttribute (AN_SHARE_COMPANY_ID, GUI.EMPTY_STRING);
		} else {
			tXMLContractLineElement.setAttribute (AN_SHARE_COMPANY_ID, shareCompany.getID ());
		}
		tXMLContractLineElement.setAttribute (AN_CONNECTED, connected);
		tXMLContractLineElement.setAttribute (AN_BOND, bond);

		return tXMLContractLineElement;
	}

	public boolean isValidContractLine () {
		boolean tIsValidContractLine;
		
		tIsValidContractLine = true;
		if (city == City.NO_CITY) {
			tIsValidContractLine = false;
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tIsValidContractLine = false;
		}
		if (bond <= 0) {
			tIsValidContractLine = false;
		}
		
		return tIsValidContractLine;
	}

	public String getAllReasonsContractLineInvalid () {
		String tAllReasonsContractLineInvalid;
		
		tAllReasonsContractLineInvalid = GUI.EMPTY_STRING;
		if (city == City.NO_CITY) {
			tAllReasonsContractLineInvalid += "No City is specified\n";
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tAllReasonsContractLineInvalid += "No Share Company is specified\n";
		}
		if (bond <= 0) {
			tAllReasonsContractLineInvalid += "Bond Value is <= zero (0)\n";
		}
		
		return tAllReasonsContractLineInvalid;
	}

	private void setConnected (boolean aConnected) {
		connected = aConnected;
	}
	
	private void setBond (int aBond) {
		bond = aBond;
	}

	private void setShareCompany (ShareCompany aShareCompany) {
		shareCompany = aShareCompany;
	}

	private void setCity (City aCity) {
		city = aCity;
	}

	public int getBond () {
		return bond;
	}

	public String getCityName () {
		String tCityName;
		
		tCityName = GUI.EMPTY_STRING;
		
		if (city != City.NO_CITY) {
			tCityName = city.getCityName ();
		}
		
		return tCityName;
	}
	
	public ShareCompany getShareCompany () {
		return shareCompany;
	}
	
	public City getCity () {
		return city;
	}
	
	public boolean isConnected () {
		return connected;
	}

	public boolean isDeltaTerrain () {
		boolean tIsDeltaTerrain;
		
		if (city == City.NO_CITY) {
			tIsDeltaTerrain = false;
		} else {
			tIsDeltaTerrain = city.isDeltaTerrain ();
		}
		
		return tIsDeltaTerrain;
	}
	
//	public JPanel buildContractLinesJPanel () {
//		String [] tColumnNames = { "City", "Company", "Bond", "Connected", "Delete" };
//		int tColWidths [] = { 150, 150, 100, 100, 100, };
//		int tTotalWidth;
//		JPanel tContractLinesJPanel;
//		
//		tTotalWidth = 0;
//		tContractLinesJPanel = configureContractLinesTable (tColumnNames, tColWidths, tTotalWidth);
//		
//		return tContractLinesJPanel;
//	}
//	
//	private JPanel configureContractLinesTable (String [] aColumnNames, int [] aColWidths, int aTotalWidth) {
//		DefaultTableCellRenderer tRightRenderer;
//		TableColumnModel tColumnModel;
//		JPanel tContractLinesJPanel;
//		
//		tRightRenderer = new DefaultTableCellRenderer ();
//		tRightRenderer.setHorizontalAlignment (SwingConstants.RIGHT);
//		contractLinesJTable = new JTable ();
//
//		contractLinesModel.setColumnIdentifiers (aColumnNames);
//		contractLinesJTable.setModel (contractLinesModel);
//		contractLinesJTable.setGridColor (Color.BLACK);
//		contractLinesJTable.setShowGrid (true);
//		contractLinesJTable.setShowVerticalLines (true);
//		contractLinesJTable.setShowHorizontalLines (true);
//
//		tColumnModel = contractLinesJTable.getColumnModel ();
//
//		for (int tIndex = 0; tIndex < aColWidths.length; tIndex++) {
//			tColumnModel.getColumn (tIndex).setMaxWidth (aColWidths [tIndex]);
//
//			aTotalWidth += aColWidths [tIndex] + 1;
//		}
//		setColumnAlign (2, SwingConstants.RIGHT);
////		setLocation (100, 100);
////		setSize (aTotalWidth, 400);
//		tContractLinesJPanel = buildScrollPane (contractLinesJTable);
//		tContractLinesJPanel.setBackground (Color.blue);
//		
//		return tContractLinesJPanel;
//	}
//	
//	public void addRow () {
//		String tShareCompanyName;
//		String tCityName;
//		KButton tDeleteButton;
//		
//		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
//			tShareCompanyName = "UNKNOWN";
//		} else {
//			tShareCompanyName = shareCompany.getName ();
//		}
//		tCityName = city.getCityName ();
//		tDeleteButton = new KButton ("X");
//		contractLinesModel.addRow (
//				new Object [] { tCityName, tShareCompanyName, bond, connected, tDeleteButton });
//	}
//	
//	public JPanel buildScrollPane (JComponent aImage) {
//		JPanel tJPanel;
//		
//		tJPanel = buildScrollPane (aImage, GUI.NULL_STRING);
//		
//		return tJPanel; 
//	}
//
//	public JPanel buildScrollPane (JComponent aImage, String aBorderLayout) {
//		JScrollPane tScrollPane;
//		JPanel tJPanel;
//		
//		tJPanel = new JPanel ();
//		tScrollPane = new JScrollPane ();
//		tScrollPane.setViewportView (aImage);
//		if (aBorderLayout != GUI.NULL_STRING) {
//			tJPanel.add (tScrollPane, aBorderLayout);
//		} else {
//			tJPanel.add (tScrollPane);
//		}
//		
//		return tJPanel;
//	}
//
//	private void setColumnAlign (int aColumnIndex, int tAlignment) {
//		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer ();
//
//		tCellRenderer.setHorizontalAlignment (tAlignment);
//		contractLinesJTable.getColumnModel ().getColumn (aColumnIndex).setHeaderRenderer (tCellRenderer);
//		contractLinesJTable.getColumnModel ().getColumn (aColumnIndex).setCellRenderer (tCellRenderer);
//	}

	public JPanel buildHeaderContractLineJPanel () {
		JPanel tHeaderContractLineJPanel;
		JLabel tCityLabel;
		JLabel tCompanyLabel;
		JLabel tBondLabel;
		JLabel tConnectedLabel;
		
		tHeaderContractLineJPanel = new JPanel ();
		tCityLabel = new JLabel ("City");
		tCompanyLabel = new JLabel ("Company");
		tBondLabel = new JLabel ("Bond");
		tConnectedLabel = new JLabel ("Connected");
		
		tHeaderContractLineJPanel.add (Box.createHorizontalStrut (10));
		tHeaderContractLineJPanel.add (Box.createHorizontalGlue ());
		tHeaderContractLineJPanel.add (tCityLabel);
		tHeaderContractLineJPanel.add (Box.createHorizontalGlue ());
		tHeaderContractLineJPanel.add (tCompanyLabel);
		tHeaderContractLineJPanel.add (Box.createHorizontalGlue ());
		tHeaderContractLineJPanel.add (tBondLabel);
		tHeaderContractLineJPanel.add (Box.createHorizontalGlue ());
		tHeaderContractLineJPanel.add (tConnectedLabel);
		tHeaderContractLineJPanel.add (Box.createHorizontalGlue ());
		tHeaderContractLineJPanel.add (Box.createHorizontalStrut (10));

		return tHeaderContractLineJPanel;
	}

	public JPanel buildContractLineJPanel () {
		JPanel tContractLineJPanel;
		
		tContractLineJPanel = new JPanel ();
		
		return tContractLineJPanel;
	}
	
	// New Methods to add
	// GenerateActionEffects -- Will generate the Action with Effects XML of the ContractBid
	// ParseActionEffects -- Will parse the Action with Effects XML of the ContractBid
}
