package ge18xx.player.ComboBoxInTable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ShareCompanyAbbrevCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public ShareCompanyAbbrevCellRenderer () {
		// TODO Auto-generated constructor stub
	}
    
   @Override
   public Component getTableCellRendererComponent (JTable aTable, Object aValue,
           boolean aIsSelected, boolean aHasFocus, int aRow, int aColumn) {
       if (aValue instanceof ShareCompanyAbbrev) {
    	   ShareCompanyAbbrev abbrev = (ShareCompanyAbbrev) aValue;
           setText (abbrev.getAbbrev ());
       }
        
       if (aIsSelected) {
//    	   setBackground (aTable.getSelectionBackground ());
    	   setBackground (Color.GREEN);
       } else {
//           setBackground (aTable.getSelectionForeground ());
    	   setBackground (Color.YELLOW);
       }
        
       return this;
   }
}
