package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;

import checksum.CellColorRenderer;
import checksum.Checksum;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;

public class ChecksumAuditFrame extends XMLFrame implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	public static final String BASE_TITLE = "Checksum Audit";
	private String REFRESH_LIST = "REFRESH LIST";
	DefaultTableModel checksumAuditModel = new DefaultTableModel (0, 7);
	JTable checksumAuditTable;
	KButton refreshList;

	public ChecksumAuditFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		
		JPanel tNorthComponents;
		int tTotalWidth;
		int tColumnCount;		
		int [] tColumnWidths;
		String [] tColumnNames;
		
		tColumnNames = buildColumnNames (aGameManager);
		tColumnCount = tColumnNames.length;
		tColumnWidths = buildColumnWidths (tColumnCount);
		tTotalWidth = getTotalWidth (tColumnWidths);
		
		buildAuditTable (tColumnNames, tColumnWidths, tTotalWidth);

		tNorthComponents = buildNorthComponents ();

		add (tNorthComponents, BorderLayout.NORTH);
	}

	public int getTotalWidth (int [] aColumnWidths) {
		int tTotalWidth;
		int tColumnCount;
		int tColumnIndex;
		
		tTotalWidth = 0;
		tColumnCount = aColumnWidths.length;
		for (tColumnIndex = 0; tColumnIndex < tColumnCount; tColumnIndex++) {
			tTotalWidth += aColumnWidths [tColumnIndex];
		}
		
		return tTotalWidth;
	}
	
	public int [] buildColumnWidths (int aColumnCount) {
		int [] tColumnWidths;
		int tColumnIndex;

		tColumnWidths = new int [aColumnCount];
		tColumnWidths [0] = 50;
		tColumnWidths [1] = 100;
		tColumnWidths [2] = 80;
		tColumnWidths [3] = 6000;
		for (tColumnIndex = 3; tColumnIndex < aColumnCount; tColumnIndex++) {
			tColumnWidths [tColumnIndex] = 300;
		}
		
		return tColumnWidths;
	}
	
	public String [] buildColumnNames (GameManager aGameManager) {
		int tPlayerCount;
		int tPlayerIndex;
		PlayerManager tPlayerManager;
		Player tPlayer;
		String [] tColumnName;

		tPlayerManager = aGameManager.getPlayerManager ();
		tPlayerCount = tPlayerManager.getPlayerCount ();
		tColumnName = new String [tPlayerCount + 4];
		
		tColumnName [0] = "#";
		tColumnName [1] = "Node Name";
		tColumnName [2] = "# of Effects";
		tColumnName [3] = "Action / Event";
		
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
			tColumnName [4 + tPlayerIndex] = tPlayer.getName ();
		}

		return tColumnName;
	}
	
	/**
	 * Update the Frame, and specifically updateFrameTitle (from super class XMLFrame) 
	 * with the static BASE_TITLE provided
	 */
	public void updateFrame () {
		updateFrameTitle (BASE_TITLE);
	}

	private JPanel buildNorthComponents () {
		JPanel tNorthComponents;
		
		tNorthComponents = new JPanel ();
		
		// TODO: Add components 'Game Name' 'Game ID', 'Client Name'
		refreshList = new KButton ("Refresh List");
		refreshList.setActionCommand (REFRESH_LIST);
		refreshList.addActionListener (this);
		tNorthComponents.add (refreshList);

		return tNorthComponents;
	}

	private void buildAuditTable (String [] aColumnNames, int [] aColWidths, int aTotalWidth) {
		TableColumnModel tColumnModel;
		Color tCellColor;
		Color tHeaderColor;
		CellColorRenderer tColorCellRenderer;
		
		tColorCellRenderer = new CellColorRenderer (4);

		checksumAuditTable = new JTable ();

		checksumAuditModel.setColumnIdentifiers (aColumnNames);
		checksumAuditTable.setModel (checksumAuditModel);
		checksumAuditTable.setGridColor (Color.BLACK);
		checksumAuditTable.setShowGrid (true);
		checksumAuditTable.setShowVerticalLines (true);
		checksumAuditTable.setShowHorizontalLines (true);
		checksumAuditTable.setDefaultRenderer (Object.class, tColorCellRenderer);
		tColumnModel = checksumAuditTable.getColumnModel ();

		tCellColor = Color.WHITE;
		tHeaderColor = new Color (230, 230, 230);
		setCellColumnAlign (0, tCellColor, SwingConstants.CENTER);
		for (int tIndex = 0; tIndex < aColWidths.length; tIndex++) {
			setHeaderColumnAlign (tIndex, tHeaderColor, SwingConstants.CENTER);
			tColumnModel.getColumn (tIndex).setMaxWidth (aColWidths [tIndex]);
//			if (tIndex != 3) {
//				setCellColumnAlign (tIndex, tCellColor, SwingConstants.CENTER);
//			}
		}
		
		setLocation (100, 100);
		setSize (aTotalWidth, 400);
		buildScrollPane (checksumAuditTable);
	}

	private void setCellColumnAlign (int aColumnIndex, Color aCellColor, int aAlignment) {
		DefaultTableCellRenderer tCellRenderer;

		tCellRenderer = new DefaultTableCellRenderer ();
		tCellRenderer.setHorizontalAlignment (aAlignment);

		checksumAuditTable.getColumnModel ().getColumn (aColumnIndex).setCellRenderer (tCellRenderer);
	}
	
	private void setHeaderColumnAlign (int aColumnIndex, Color aHeaderColor, int aAlignment) {
		JLabel tHeaderRenderer;
		Border tBorder;
		
		tHeaderRenderer = (JLabel) checksumAuditTable.getColumnModel ().getColumn (aColumnIndex).getHeaderRenderer ();
		tHeaderRenderer = new DefaultTableCellRenderer ();
		tHeaderRenderer.setHorizontalAlignment (aAlignment);
		tHeaderRenderer.setBackground (aHeaderColor);
		tBorder = BorderFactory.createLineBorder (Color.black, 3);
		tHeaderRenderer.setBorder (tBorder);
		checksumAuditTable.getColumnModel ().getColumn (aColumnIndex).
			setHeaderRenderer ((TableCellRenderer) tHeaderRenderer);
	}

	public void addRow (Checksum aChecksum) {
		GameManager tGameManager;
		ActionManager tActionManager;
		RoundManager tRoundManager;
		Action tAction;
		Object [] tDataRow;
		String [] tChecksums;
		String tActionReport;
		int tItemCount;
		int tItemIndex;
		int tActionNumber;
		int tActionIndex;
		int tEffectCount;
		
		tGameManager = (GameManager) gameEngineManager;
		tRoundManager = tGameManager.getRoundManager ();
		tActionManager = tRoundManager.getActionManager ();
		tActionIndex = aChecksum.getActionIndex ();
		tAction = tActionManager.getActionAt (tActionIndex);
		tActionNumber = tAction.getNumber ();
		tChecksums = aChecksum.getChecksums ();
		tEffectCount = tAction.getEffectCount ();
		tActionReport = tAction.getSimpleActionReport ();
		tItemCount = 4 + tChecksums.length;
		
		tDataRow = new Object [tItemCount];
		tDataRow [0] = tActionNumber;
		tDataRow [1] = aChecksum.getNodeName ();
		tDataRow [2] = tEffectCount;
		tDataRow [3] = tActionReport;
		for (tItemIndex = 4; tItemIndex < tItemCount; tItemIndex++) {
			tDataRow [tItemIndex] = tChecksums [tItemIndex - 4];
		}
//		checksumAuditModel.addRow (tDataRow);
		checksumAuditModel.insertRow (0, tDataRow);
	}

	public void removeAllRows () {
		int tRowCount;
		int tRowIndex;

		tRowCount = checksumAuditModel.getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			checksumAuditModel.removeRow (0);
		}
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {

	}

	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;

		tTheAction = aActionEvent.getActionCommand ();
		if (REFRESH_LIST.equals (tTheAction)) {
			updateAuditTable ();
		}
	}

	private void updateAuditTable () {
		refreshAuditTable ();
	}

	public void refreshAuditTable () {
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		removeAllRows ();
		tGameManager.fillChecksumAuditFrame ();
	}
}
