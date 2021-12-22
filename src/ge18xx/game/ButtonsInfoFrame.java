package ge18xx.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import ge18xx.toplevel.TableFrame;

public class ButtonsInfoFrame extends TableFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<FrameButton> frameButtons;
	DefaultTableModel buttonModel = new DefaultTableModel (0, 0);
	JPanel allButtonInfoJPanel;
	JTable buttonsTable;
	int colWidths [] = {30, 100, 170, 100, 700};
	int rowHeight = 35;
	
	public ButtonsInfoFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getGameName ());
		frameButtons = new ArrayList<FrameButton> ();
		
		allButtonInfoJPanel = new JPanel ();
		allButtonInfoJPanel.setLayout (new BoxLayout (allButtonInfoJPanel, BoxLayout.Y_AXIS));

		buildButtonsTable ();
	}

	public void addButton (JButton aJButton) {
		FrameButton tFrameButton;
		
		tFrameButton = new FrameButton (aJButton);
		frameButtons.add (tFrameButton);
	}
	
	public void addButton (JCheckBox aJCheckBox, String aGroupName) {
		FrameButton tFrameButton;
		
		tFrameButton = new FrameButton (aJCheckBox, aGroupName);
		frameButtons.add (tFrameButton);
	}
	
	private void buildButtonsTable () {
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer ();
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer ();
		String [] tColumnNames = {"#", "Group", "Button", "Enabled", "Tool Tip Text"};
		TableColumnModel tColumnModel;
		JTableHeader tTableHeader;

		rightRenderer.setHorizontalAlignment (SwingConstants.RIGHT);
		centerRenderer.setHorizontalAlignment (SwingConstants.CENTER);
		buttonsTable = new JTable ();

		buttonModel.setColumnIdentifiers (tColumnNames);
		buttonsTable.setModel (buttonModel);
		buttonsTable.setGridColor (Color.BLACK);
		buttonsTable.setShowGrid (true);
		buttonsTable.setShowVerticalLines (true);
		buttonsTable.setShowHorizontalLines (true);
		buttonsTable.setFont (new Font ("Serif", Font.PLAIN, 20));
		buttonsTable.setRowHeight (rowHeight);
		tTableHeader = buttonsTable.getTableHeader();
		tTableHeader.setFont (new Font ("SansSerif", Font.ITALIC, 20));

		tColumnModel = buttonsTable.getColumnModel ();
		tColumnModel.getColumn (0).setCellRenderer (centerRenderer);
		tColumnModel.getColumn (3).setCellRenderer (centerRenderer);
		
		for (int tIndex = 0; tIndex < colWidths.length; tIndex++) {
			tColumnModel.getColumn (tIndex).setMaxWidth (colWidths [tIndex]);
		}
		buttonsTable.setAutoResizeMode (JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		setCalculatedSize ();
		setScrollPane (buttonsTable);
	}

	private void setCalculatedSize () {
		int tTotalWidth = 0;
		int tTotalHeight;
		
		for (int tIndex = 0; tIndex < colWidths.length; tIndex++) {
			tTotalWidth += colWidths [tIndex] + 1;
		}
		tTotalHeight = (rowHeight + 10) * (frameButtons.size () + 1);
		setSize (tTotalWidth, tTotalHeight);
	}
	
	public void handleExplainButtons (Point aNewPoint) {
		setLocation (aNewPoint);
		removeAllRows ();
		fillButtonsTable ();
		setCalculatedSize ();
		setVisible (true);
	}
	
	public void fillButtonsTable () {
		int tButtonIndex = 0;
		String tGroupName, tButtonText, tToolTipText;
		boolean tEnabled;
		
		for (FrameButton tFrameButton : frameButtons) {
			if (tFrameButton.isVisible ()) {
				tButtonIndex++;
				tGroupName = tFrameButton.getGroupName ();
				tButtonText = tFrameButton.getTitle ();
				tEnabled = tFrameButton.getEnabled ();
				tToolTipText = tFrameButton.getToolTipText ();
	
				addRow (tButtonIndex, tGroupName, tButtonText, tEnabled, tToolTipText);
			}
		}
	}
	
	public void addRow (int aButtonNumber, String aGroupName, String aButtonText, boolean aEnabled, String aToolTipText) {
		buttonModel.addRow (new Object [] {aButtonNumber, aGroupName, aButtonText, aEnabled, aToolTipText});
	}

	public void removeAllRows () {
		int tRowCount, tRowIndex;
		
		tRowCount = buttonModel.getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			buttonModel.removeRow (0);
		}
	}

}
