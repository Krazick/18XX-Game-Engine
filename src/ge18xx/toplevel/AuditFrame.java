package ge18xx.toplevel;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ge18xx.game.GameManager;

public class AuditFrame extends TableFrame {
	DefaultTableModel auditModel = new DefaultTableModel (0, 0);
	JTable auditTable;
	int actorBalance;
	String actorName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuditFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getGameName ());
		
		String [] tColumnNames = {"#", "Actor", "Action / Event", "Debit", "Credit", "Balance"};
		int tColWidths [] = {50, 110, 600, 50, 50, 70};
		int tTotalWidth = 0;
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer ();
		rightRenderer.setHorizontalAlignment (SwingConstants.RIGHT);
		
		auditTable = new JTable ();

		auditModel.setColumnIdentifiers (tColumnNames);
		auditTable.setModel (auditModel);
		auditTable.setGridColor (Color.BLACK);
		auditTable.setShowGrid (true);
		auditTable.setShowVerticalLines (true);
		auditTable.setShowHorizontalLines (true);
		
		TableColumnModel tColumnModel = auditTable.getColumnModel ();
		
		for (int tIndex = 0; tIndex < tColWidths.length; tIndex++) {
			tColumnModel.getColumn (tIndex).setMaxWidth (tColWidths [tIndex]);
			
			tTotalWidth += tColWidths [tIndex] + 1;
		}
		auditTable.getColumnModel ().getColumn (0).setCellRenderer (rightRenderer);
		auditTable.getColumnModel ().getColumn (3).setCellRenderer (rightRenderer);
		auditTable.getColumnModel ().getColumn (4).setCellRenderer (rightRenderer);
		auditTable.getColumnModel ().getColumn (5).setCellRenderer (rightRenderer);
		setLocation (100, 100);
		setSize (tTotalWidth, 400);
		setScrollPane (auditTable);
		
		setActorName ("Canadian Pacific");
		setActorBalance (0);
//		addRow (0, "Float Company/Cash Transfer from Bank", 0, 1000);
//		addRow (1, "PRR Buy Train/2 Train Purchase, Cash Transfer to Bank", 80, 0);
	}

	public void setActorBalance (int aActorBalance) {
		actorBalance = aActorBalance;
	}
	
	public void setActorName (String aActorName) {
		actorName = aActorName;
	}
	
	public String getActorName () {
		return actorName;
	}
	
	public int getActorBalance () {
		return actorBalance;
	}
	
	public void addToActorBalance (int aCredit) {
		actorBalance += aCredit;
	}
	
	public void subtractFromActorBalance (int aDebit) {
		actorBalance -= aDebit;
	}
	
	public void addRow (int aActionNumber, String aActionEvent, int aDebit, int aCredit) {
		addToActorBalance (aCredit);
		subtractFromActorBalance (aDebit);
		auditModel.addRow (new Object [] {aActionNumber, actorName, aActionEvent, aDebit, aCredit, actorBalance});
	}
}
