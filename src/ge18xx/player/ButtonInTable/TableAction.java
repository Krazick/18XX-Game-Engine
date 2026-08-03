package ge18xx.player.ButtonInTable;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

//
// The "ButtonInTable" Package by Über mich was found in his "My Software-Developer Blog"
// Dated Februar 27, 2026 (February 27, 2026)
//
// Contains 5 new Classes that add fairly generic classes that make use of Swing features
// to add support for adding JButtons into JTables. The two primary examples are Editing a Row,
// and Deleting a Row.
//
// The Reformatting is based upon my personally preferred formatting to be consistent with
// the rest of my Game Engine code base. It will be moved into my Game Engine Utilities Repository 
// when it has been stabilized.
//
// The Classes include:
//
// 1) TableAction Extends Swing's AbstractAction
// 2) EditCellAction Extends TableAction
// 3) DeleteRowAction Extends TableAction
// 4) ButtonCellEditor Extends AbstractCellEditor and Implements TableCellEditor
// 5) ButtonCellRenderer Implements TableCellRenderer
//

public abstract class TableAction extends AbstractAction
{
    private static final long serialVersionUID = 1L;

	public static final String ROW_COLUMN_SEPARATOR = "/";
    
    protected JTable table;
    protected DefaultTableModel tableModel;
    protected int row;
    protected int column;
    protected Vector<Object> rowData;

    protected abstract void actionPerformed ();
    
	@SuppressWarnings ("unchecked")
	@Override
    public final void actionPerformed (ActionEvent event) {
		String tActionCommand;
		String [] tSplit;
		
        table = (JTable) event.getSource ();
        
        tActionCommand = event.getActionCommand ();
        tSplit = tActionCommand.split (TableAction.ROW_COLUMN_SEPARATOR);
        row = Integer.valueOf (tSplit [0].trim ());
        column = Integer.valueOf (tSplit [1].trim ());
        
        tableModel = (DefaultTableModel) table.getModel ();
        rowData = (Vector<Object>) tableModel.getDataVector ().get (row);
        
        actionPerformed ();
    }
}