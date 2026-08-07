package ge18xx.player.ComboBoxInTable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ShareCompanyAbbrevCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public ShareCompanyAbbrevCellRenderer () {
	}
    
   @Override
   public Component getTableCellRendererComponent (JTable aTable, Object aValue,
           boolean aIsSelected, boolean aHasFocus, int aRow, int aColumn) {
	   
	   String tAbbrev;
	   
       if (aValue instanceof String) {
    	   tAbbrev = (String) aValue;
           setText (tAbbrev);
       }
        
       return this;
   }
}
