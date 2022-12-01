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

import ge18xx.bank.StartPacketFrame;
import ge18xx.bank.StartPacketRow;
import ge18xx.company.Certificate;
import ge18xx.player.Portfolio;
//import ge18xx.toplevel.TableFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.train.TrainPortfolio;

public class ButtonsInfoFrame extends XMLFrame {

	/**
	 *
	 */
	public static final String EXPLAIN = "Explain";
	private static final long serialVersionUID = 1L;
	private ArrayList<FrameButton> frameButtons;
	DefaultTableModel buttonModel;
	JPanel allButtonInfoJPanel;
	JTable buttonsTable;
	int colWidths[] = { 30, 320, 100, 700 };
	int rowHeight = 35;
	int buttonIndex;

	public ButtonsInfoFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		frameButtons = new ArrayList<> ();

		buttonModel = new DefaultTableModel (0, 4) {
		    private static final long serialVersionUID = 1L;

		    /**
		     * Set so that no Cell in the Table can be edited.
		     *
		     */
			@Override
		    public boolean isCellEditable (int row, int column) {
		       //all cells false
		       return false;
		    }
		};

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
		String [] tColumnNames = { "#", "Button Description", "Enabled", "Tool Tip Text" };
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
		tTableHeader = buttonsTable.getTableHeader ();
		tTableHeader.setFont (new Font ("SansSerif", Font.ITALIC, 20));

		tColumnModel = buttonsTable.getColumnModel ();
		tColumnModel.getColumn (0).setCellRenderer (centerRenderer);
		tColumnModel.getColumn (2).setCellRenderer (centerRenderer);

		for (int tIndex = 0; tIndex < colWidths.length; tIndex++) {
			tColumnModel.getColumn (tIndex).setMaxWidth (colWidths [tIndex]);

		}
		buttonsTable.setAutoResizeMode (JTable.AUTO_RESIZE_ALL_COLUMNS);

		setCalculatedSize ();
		buildScrollPane (buttonsTable);
	}

	private void setCalculatedSize () {
		int tTotalWidth = 0;
		int tTotalHeight;

		for (int colWidth : colWidths) {
			tTotalWidth += colWidth + 1;
		}
		tTotalHeight = (rowHeight + 10) * (frameButtons.size () + 1);
		setSize (tTotalWidth, tTotalHeight);
	}

	public void prepareExplainButtons (Portfolio aPortfolio) {
		removeAllRows ();
		fillWithButtonsTable ();
		fillWithCheckBoxes (aPortfolio, "");
	}

	public void handleExplainButtons (Point aNewPoint) {
		setLocation (aNewPoint);
		setCalculatedSize ();
		setVisible (true);
	}

	private void fillWithButtonsTable () {
		for (FrameButton tFrameButton : frameButtons) {
			if (tFrameButton.isVisible ()) {
				addRow (tFrameButton);
			}
		}
	}

	public void fillWithCheckBoxes (Portfolio aPortfolio, String aHolderName) {
		int tCount, tIndex;
		FrameButton tFrameButton;

		if (aPortfolio != Portfolio.NO_PORTFOLIO) {
			tCount = aPortfolio.getCertificateTotalCount ();
			if (tCount > 0) {
				for (tIndex = 0; tIndex < tCount; tIndex++) {
					tFrameButton = aPortfolio.getFrameButtonAt (tIndex);
					if (buttonValidAndVisible (tFrameButton)) {
						addCheckboxFrameButton (tFrameButton, aHolderName);
					}
				}
			}
		}
	}

	private boolean buttonValidAndVisible (FrameButton aFrameButton) {
		boolean tButtonValidAndVisible;

		tButtonValidAndVisible = false;
		if (aFrameButton != FrameButton.NO_FRAME_BUTTON) {
			if (aFrameButton.isVisible ()) {
				tButtonValidAndVisible = true;
			}
		}

		return tButtonValidAndVisible;
	}

	public void fillWithPrivateCheckBoxes (Portfolio aPortfolio, String aHolderName) {
		int tCount, tIndex;
		FrameButton tFrameButton;
		Certificate tCertificate;

		if (aPortfolio != Portfolio.NO_PORTFOLIO) {
			tCount = aPortfolio.getCertificateTotalCount ();
			if (tCount > 0) {
				for (tIndex = 0; tIndex < tCount; tIndex++) {
					tCertificate = aPortfolio.getCertificate (tIndex);
					if (tCertificate.isAPrivateCompany ()) {
						tFrameButton = tCertificate.getFrameButton ();
						if (buttonValidAndVisible (tFrameButton)) {
							addCheckboxFrameButton (tFrameButton, aHolderName);
						}
					}
				}
			}
		}
	}

	public void fillWithCheckBoxes (StartPacketFrame aStartPacketFrame) {
		int tRowCount, tRowIndex;
		StartPacketRow tStartPacketRow;

		tRowCount = aStartPacketFrame.getStartPacketRowCount ();
		if (tRowCount > 0) {
			for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
				tStartPacketRow = aStartPacketFrame.getStartPacketRowAt (tRowIndex);
				fillWithCheckBoxes (tStartPacketRow);
			}
		}
	}

	public void fillWithCheckBoxes (StartPacketRow aStartPacketRow) {
		int tCount, tIndex;
		FrameButton tFrameButton;

		if (aStartPacketRow != StartPacketRow.NO_START_PACKET_ROW) {
			tCount = aStartPacketRow.getItemCount ();
			if (tCount > 0) {
				for (tIndex = 0; tIndex < tCount; tIndex++) {
					tFrameButton = aStartPacketRow.getFrameButtonInRow (tIndex);
					if (buttonValidAndVisible (tFrameButton)) {
						addRow (tFrameButton);
					}
				}
			}
		}
	}

	public void fillWithCheckBoxes (TrainPortfolio aTrainPortfolio) {
		int tCount, tIndex;
		FrameButton tFrameButton;
		String tPortfolioOwner;

		if (aTrainPortfolio != TrainPortfolio.NO_TRAIN_PORTFOLIO) {
			tCount = aTrainPortfolio.getAvailableCount ();
			tPortfolioOwner = aTrainPortfolio.getPortfolioHolderAbbrev ();
			if (tCount > 0) {
				for (tIndex = 0; tIndex < tCount; tIndex++) {
					tFrameButton = aTrainPortfolio.getFrameButtonAt (tIndex);
					if (buttonValidAndVisible (tFrameButton)) {
						addCheckboxFrameButton (tFrameButton, tPortfolioOwner);
					}
				}
			}
		}
	}

	private void addCheckboxFrameButton (FrameButton aFrameButton, String aPortfolioOwner) {
		String tNewGroupName;
		String tCurrentName;

		if (aFrameButton != FrameButton.NO_FRAME_BUTTON) {
			tCurrentName = aFrameButton.getGroupName ();
			if (!tCurrentName.startsWith (aPortfolioOwner)) {
				tNewGroupName = aPortfolioOwner + " " + tCurrentName;
				aFrameButton.setGroupName (tNewGroupName);
			}
			addRow (aFrameButton);
		}
	}

	private void addRow (FrameButton aFrameButton) {
		String tButtonText, tToolTipText;
		String tButtonDescription = "";
		boolean tEnabled;

		if (aFrameButton != FrameButton.NO_FRAME_BUTTON) {
			if (aFrameButton.isVisible ()) {
				tButtonText = aFrameButton.getTitle ();
				if (!(EXPLAIN.equals (tButtonText))) {
					tButtonDescription = aFrameButton.getDescription ();
					tEnabled = aFrameButton.getEnabled ();
					tToolTipText = aFrameButton.getToolTipText ();
					buttonIndex++;
					addRow (buttonIndex, tButtonDescription, tEnabled, tToolTipText);
				}
			}
		}
	}

	private void addRow (int aButtonNumber, String aButtonDescription, boolean aEnabled, String aToolTipText) {
		buttonModel.addRow (new Object [] { aButtonNumber, aButtonDescription, aEnabled, aToolTipText });
	}

	private void removeAllRows () {
		int tRowCount, tRowIndex;

		tRowCount = buttonModel.getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			buttonModel.removeRow (0);
		}
		buttonIndex = 0;
	}
}
