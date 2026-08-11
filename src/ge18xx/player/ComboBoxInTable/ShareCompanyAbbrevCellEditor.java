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
	private String shareCompanyAbbrev;
	private List<String> listShareCompanyAbbrevs;

	public ShareCompanyAbbrevCellEditor (JComboBox<String> comboBox) {
		super (comboBox);
	}

	public ShareCompanyAbbrevCellEditor (JComboBox<String> comboBox, List<String> aListShareCompanyAbbrevs) {
		super (comboBox);
		setListShareCompanyAbbrevs (aListShareCompanyAbbrevs);
	}
 
	public void setListShareCompanyAbbrevs (List<String> aListShareCompanyAbbrevs) {
		listShareCompanyAbbrevs = aListShareCompanyAbbrevs;
	}
	
	@Override
	public Object getCellEditorValue () {
		return shareCompanyAbbrev;
	}

	@Override
	public Component getTableCellEditorComponent (JTable aTable, Object aValue, boolean aIsSelected, int aRow, int aColumn) {
		return updateComboBox (aValue);
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	public Component updateComboBox (Object aValue) {
		JComboBox<String> tComboShareCompanyAbbrev;
		
		if (aValue instanceof String) {
			shareCompanyAbbrev = (String) aValue;
		}
     
		tComboShareCompanyAbbrev = (JComboBox) editorComponent;
     
		tComboShareCompanyAbbrev.removeAllItems ();
		for (String tShareCompanyAbbrev : listShareCompanyAbbrevs) {
			tComboShareCompanyAbbrev.addItem (tShareCompanyAbbrev);
		}
     
		tComboShareCompanyAbbrev.setSelectedItem (shareCompanyAbbrev);
		tComboShareCompanyAbbrev.addActionListener (this);
          
		return tComboShareCompanyAbbrev;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		JComboBox<String> tComboShareCompanyAbbrev;
		Object tSource;
		
		tSource = aEvent.getSource ();
		if (tSource instanceof JComboBox<?>) {
			tComboShareCompanyAbbrev = (JComboBox<String>) tSource;
			shareCompanyAbbrev = (String) tComboShareCompanyAbbrev.getSelectedItem ();
		}
	}

}
