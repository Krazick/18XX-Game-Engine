package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
	private static final int STARTING_COLUMN_COUNT = 4;
	private String REFRESH_LIST = "REFRESH LIST";
	public static final String BASE_TITLE = "Checksum Audit";
	DefaultTableModel checksumAuditModel;
	JTable checksumAuditTable;
	KButton refreshList;
	JLabel gameName;
	JLabel gameID;
	JLabel clientName;

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
		tColumnWidths [1] = 50;
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
		tColumnName = new String [tPlayerCount + STARTING_COLUMN_COUNT];
		
		tColumnName [0] = "#";
		tColumnName [1] = "Node";
		tColumnName [2] = "# of Effects";
		tColumnName [3] = "Action Report";
		
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
			tColumnName [tPlayerIndex + STARTING_COLUMN_COUNT] = tPlayer.getName ();
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
		GameManager tGameManager;
		
		tNorthComponents = new JPanel ();
		
		tGameManager = (GameManager) gameEngineManager;
		gameName = new JLabel ("Game Name: " + tGameManager.getGameName ());
		gameID = new JLabel ("ID: " + tGameManager.getGameID ());
		clientName = new JLabel ("Client: " + tGameManager.getClientUserName ());
		
		tNorthComponents.add (Box.createVerticalStrut (10));
		tNorthComponents.add (gameName);
//		tNorthComponents.add (Box.createVerticalStrut (10));
		tNorthComponents.add (Box.createHorizontalGlue ());
		tNorthComponents.add (gameID);
//		tNorthComponents.add (Box.createVerticalStrut (10));
		tNorthComponents.add (Box.createHorizontalGlue ());
		tNorthComponents.add (clientName);
//		tNorthComponents.add (Box.createVerticalStrut (10));
		tNorthComponents.add (Box.createHorizontalGlue ());
		
		refreshList = new KButton ("Refresh List");
		refreshList.setActionCommand (REFRESH_LIST);
		refreshList.addActionListener (this);
		
		tNorthComponents.add (refreshList);
		tNorthComponents.add (Box.createVerticalStrut (10));

		return tNorthComponents;
	}

	private void buildAuditTable (String [] aColumnNames, int [] aColumnWidths, int aTotalWidth) {
		CellColorRenderer tColorCellRenderer;
		TableColumnModel tColumnModel;
		Color tHeaderColor;
		
		tColorCellRenderer = new CellColorRenderer (STARTING_COLUMN_COUNT);
		
		checksumAuditModel = new DefaultTableModel (aColumnNames, 0);
		
		checksumAuditTable = new JTable (checksumAuditModel);
		checksumAuditTable.setGridColor (Color.BLACK);
		checksumAuditTable.setShowGrid (true);
		checksumAuditTable.setShowVerticalLines (true);
		checksumAuditTable.setShowHorizontalLines (true);
		checksumAuditTable.setDefaultRenderer (Object.class, tColorCellRenderer);
		tColumnModel = checksumAuditTable.getColumnModel ();

		tHeaderColor = new Color (230, 230, 230);
		setCellColumnAlign (0, SwingConstants.CENTER);
		setCellColumnAlign (2, SwingConstants.CENTER);
		for (int tIndex = 0; tIndex < aColumnWidths.length; tIndex++) {
			setHeaderColumnAlign (tIndex, tHeaderColor, SwingConstants.CENTER);
			tColumnModel.getColumn (tIndex).setMaxWidth (aColumnWidths [tIndex]);
			tColumnModel.getColumn (tIndex).setPreferredWidth (aColumnWidths [tIndex]);
		}
		
		setLocation (100, 100);
		setSize (aTotalWidth, 400);
		buildScrollPane (checksumAuditTable);
	}

	private void setCellColumnAlign (int aColumnIndex, int aAlignment) {
		DefaultTableCellRenderer tCellRenderer;

		tCellRenderer = new DefaultTableCellRenderer ();
		tCellRenderer.setHorizontalAlignment (aAlignment);

		checksumAuditTable.getColumnModel ().getColumn (aColumnIndex).setCellRenderer (tCellRenderer);
	}
	
	private void setHeaderColumnAlign (int aColumnIndex, Color aHeaderColor, int aAlignment) {
		JLabel tHeaderRenderer;
		Border tBorder;
		
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
		if (tAction != Action.NO_ACTION) {
			tActionNumber = tAction.getNumber ();
			tChecksums = aChecksum.getChecksums ();
			tEffectCount = tAction.getEffectCount ();
			tActionReport = tAction.getSimpleActionReport ();
			tItemCount = STARTING_COLUMN_COUNT + tChecksums.length;
			
			tDataRow = new Object [tItemCount];
			tDataRow [0] = tActionNumber;
			tDataRow [1] = aChecksum.getNodeName ();
			tDataRow [2] = tEffectCount;
			tDataRow [3] = tActionReport;
			for (tItemIndex = STARTING_COLUMN_COUNT; tItemIndex < tItemCount; tItemIndex++) {
				tDataRow [tItemIndex] = tChecksums [tItemIndex - STARTING_COLUMN_COUNT];
			}
	 		checksumAuditModel.insertRow (0, tDataRow);
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
	
	public void removeAllRows () {
		int tRowCount;
		int tRowIndex;

		tRowCount = checksumAuditModel.getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			checksumAuditModel.removeRow (0);
		}
	}
}
