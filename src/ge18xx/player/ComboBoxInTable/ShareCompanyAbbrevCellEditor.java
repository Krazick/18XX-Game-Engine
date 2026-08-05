package ge18xx.player.ComboBoxInTable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ShareCompanyAbbrevCellEditor extends DefaultCellEditor implements TableCellEditor, ActionListener {

	private static final long serialVersionUID = 1L;	
	private ShareCompanyAbbrev shareCompanyAbbrev;
	private List<ShareCompanyAbbrev> listShareCompanyAbbrev;

	public ShareCompanyAbbrevCellEditor (JComboBox<ShareCompanyAbbrev> comboBox, List<ShareCompanyAbbrev> aListShareCompanyAbbrev) {
		super (comboBox);
		listShareCompanyAbbrev = aListShareCompanyAbbrev;
	}
 
	@Override
	public Object getCellEditorValue () {
		return shareCompanyAbbrev;
	}

	@Override
	public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof ShareCompanyAbbrev) {
			shareCompanyAbbrev = (ShareCompanyAbbrev) value;
		}
     
		JComboBox<ShareCompanyAbbrev> comboShareCompanyAbbrev = new JComboBox<ShareCompanyAbbrev> ();
     
		for (ShareCompanyAbbrev tShareCompanyAbbrev : listShareCompanyAbbrev) {
			comboShareCompanyAbbrev.addItem (tShareCompanyAbbrev);
		}
     
		comboShareCompanyAbbrev.setSelectedItem (shareCompanyAbbrev);
		comboShareCompanyAbbrev.addActionListener (this);
     
		if (isSelected) {
			comboShareCompanyAbbrev.setBackground( table.getSelectionBackground ());
//			comboShareCompanyAbbrev.setBackground (Color.BLUE);
		} else {
			comboShareCompanyAbbrev.setBackground (table.getSelectionForeground ());
//			comboShareCompanyAbbrev.setBackground (Color.RED);
		}
     
		return comboShareCompanyAbbrev;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		JComboBox<ShareCompanyAbbrev> tComboShareCompanyAbbrev;
		Object tSource;
		
		tSource = aEvent.getSource ();
		if (tSource instanceof JComboBox<?>) {
			tComboShareCompanyAbbrev = (JComboBox<ShareCompanyAbbrev>) tSource;
			shareCompanyAbbrev = (ShareCompanyAbbrev) tComboShareCompanyAbbrev.getSelectedItem ();
		}
	}

}
