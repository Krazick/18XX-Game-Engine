package ge18xx.player.ButtonInTable;

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

public class DeleteRowAction extends TableAction
{
    private static final long serialVersionUID = 1L;

	@Override
    protected void actionPerformed () {
        tableModel.getDataVector ().remove (row);
        table.revalidate ();
        table.repaint ();
    }
}
